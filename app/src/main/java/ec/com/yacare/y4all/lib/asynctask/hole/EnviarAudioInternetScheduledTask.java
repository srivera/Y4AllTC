package ec.com.yacare.y4all.lib.asynctask.hole;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.ws.MonitoreoPorteroIOAsyncTask;


public class EnviarAudioInternetScheduledTask  extends Thread {


	private DatagramSocket clientSocket;
	private Integer numeroPaquetesEnviados;
	private AudioManager audioManager;
	private MonitorIOActivity activity;
	private Integer puerto;
	private ProgressBar audioRecibido;
	
	private TextView origen;
	private String datos;

	public EnviarAudioInternetScheduledTask(AudioManager audioManager,ProgressBar audioRecibido, Integer puerto, MonitorIOActivity activity, TextView origen) {
		super();
		this.activity = activity;
		this.audioManager = audioManager;
		this.audioRecibido = audioRecibido;
		this.puerto = puerto;
		this.origen = origen;
	}


	@Override
	public void run() {
		InetAddress ipEquipo;

		Boolean contesto = false;
		Integer numeroIntentos = 0;

		InetAddress ipComunicar = null;
		Integer puertoComunicar = null;
		Integer puertoLocal = null;
		Integer puertoPaquete = null;

		try {
			ipEquipo = InetAddress.getByName(YACSmartProperties.IP_CORP_P);

			//Envia el paquete para establecer la comunicacion
			byte[] datosConfB = "PING;".getBytes();
			byte[] datosRecibir = new byte[512];
			clientSocket = new DatagramSocket();	
			DatagramPacket sendPacketP = new DatagramPacket(datosConfB,
					datosConfB.length, ipEquipo, puerto);
			clientSocket.send(sendPacketP);


			while(!contesto && numeroIntentos < 5){
				try{
					numeroIntentos++;
					DatagramPacket receivePacket = new DatagramPacket(datosRecibir,
							datosRecibir.length);
					clientSocket.setSoTimeout(1000);
					clientSocket.receive(receivePacket);
					String response = new String(receivePacket.getData());
					Log.d("AUDIO", response);
					String[] splitResponse = response.split(";");
					ipComunicar = InetAddress.getByName(splitResponse[0].substring(1));
					puertoComunicar = Integer.parseInt(splitResponse[1]);
					puertoLocal = clientSocket.getLocalPort();
					puertoPaquete=receivePacket.getPort();

					clientSocket.close();
					clientSocket = new DatagramSocket(puertoLocal);
					
					contesto = true;
				} catch (SocketTimeoutException e){
					//No respondio el otro dispositivo
					continue;
				}
			}

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
//			datos = e1.getMessage();
//			((MonitorActivity)activity.fragmentTab1).origen1.post(new Runnable() {
//
//				@Override
//				public void run() {
//					((MonitorActivity)activity.fragmentTab1).origen1.setText("/" + ((MonitorActivity)activity.fragmentTab1).origen1.getText() + "AE: " + datos);
//
//				}
//			});
		} catch (SocketException e1) {
			e1.printStackTrace();
//			datos = e1.getMessage();
//			((MonitorActivity)activity.fragmentTab1).origen1.post(new Runnable() {
//
//				@Override
//				public void run() {
//					((MonitorActivity)activity.fragmentTab1).origen1.setText("/" + ((MonitorActivity)activity.fragmentTab1).origen1.getText() + "AE: " + datos);
//
//				}
//			});
		} catch (IOException e1) {
			e1.printStackTrace();
			datos = e1.getMessage();
//			((MonitorActivity)activity.fragmentTab1).origen1.post(new Runnable() {
//
//				@Override
//				public void run() {
//					((MonitorActivity)activity.fragmentTab1).origen1.setText("/" + ((MonitorActivity)activity.fragmentTab1).origen1.getText() + "AE: " + datos);
//
//				}
//			});
		}

			
		if(contesto){
			try {	
				int TAMANO_PAKETE = 372;

				byte[] bufferB = new byte[TAMANO_PAKETE];
				byte[] datosEnviar =  comprimirGZIP(bufferB, TAMANO_PAKETE);

				DatagramPacket sendPacket = new DatagramPacket(datosEnviar,
						datosEnviar.length, ipComunicar,
						puertoComunicar);
				
				clientSocket.send(sendPacket);
//				
//				
//				byte[] receiveData = new byte[512];
//
//				DatagramPacket receivePacket = new DatagramPacket(receiveData,
//						receiveData.length);
//				clientSocket.setSoTimeout(5000);
//				clientSocket.receive(receivePacket);
//				
//				
				int mSampleRate = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("sample.rate"));
				

				AudioQueu.modoAudioAnterior = audioManager.getMode();
				audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
				final int minBufferSize = AudioRecord.getMinBufferSize(
						mSampleRate, AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT);
				AudioQueu.audio_recorder = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER| MediaRecorder.AudioSource.MIC, mSampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
				int bytes_read = 0;
				AudioQueu.audio_recorder.startRecording();
				
				AudioQueu.setClientSocket(clientSocket);
				RecibirAudioInternetScheduledTask recibirAudioAsyncTask = new RecibirAudioInternetScheduledTask(clientSocket, audioRecibido, activity, audioManager);
				recibirAudioAsyncTask.start();

				numeroPaquetesEnviados = 0;

				while (AudioQueu.getComunicacionAbierta() ) {
					bufferB = new byte[TAMANO_PAKETE];
					bytes_read = AudioQueu.audio_recorder.read(bufferB, 0, TAMANO_PAKETE);

					numeroPaquetesEnviados++;
					datosEnviar =  comprimirGZIP(bufferB, TAMANO_PAKETE);
					sendPacket = new DatagramPacket(datosEnviar,
							datosEnviar.length, ipComunicar,
							puertoComunicar);
					clientSocket.send(sendPacket);
				}
				Log.d("Fecha Final ", ((Long) new Date().getTime()).toString());
//				AudioQueu.audio_recorder.stop();
//				AudioQueu.audio_recorder.release();
				audioManager.setMode(AudioQueu.modoAudioAnterior);
//			} catch (SocketTimeoutException e){
//				e.printStackTrace();
//				clientSocket.close();

			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else{
			MonitoreoPorteroIOAsyncTask monitorearPorteroAsyncTask = new MonitoreoPorteroIOAsyncTask(activity);
			monitorearPorteroAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}


	public byte[] comprimirGZIP(byte[] file, Integer paquete) throws IOException {
		ByteArrayOutputStream gzdata = new ByteArrayOutputStream();
		GZIPOutputStream gzipper = new GZIPOutputStream(gzdata);
		ByteArrayInputStream data = new ByteArrayInputStream(file);
		byte[] readed = new byte[paquete];
		int actual = 1;
		while ((actual = data.read(readed)) > 0) {
			gzipper.write(readed, 0, actual);
		}
		gzipper.finish();
		data.close();
		byte[] compressed = gzdata.toByteArray();
		gzdata.close();
		return compressed;
	}
}
