package ec.com.yacare.y4all.lib.asynctask.hole;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.ws.MonitoreoPorteroIOAsyncTask;

public class RecibirAudioInternetScheduledTask extends Thread {

	private DatagramSocket clientSocket;
	private MonitorIOActivity activity;

	private Integer paqRecibido = 0;
	private ProgressBar audioRecibido;
	
	private String datos;
	
	private AudioTrack track;
	
	public AudioManager audioManager;

	public RecibirAudioInternetScheduledTask(DatagramSocket clientSocket, ProgressBar audioRecibido, MonitorIOActivity activity, AudioManager audioManager) {
		super();
		this.clientSocket = clientSocket;
		this.audioRecibido = audioRecibido;
		this.activity = activity;
		this.audioManager = audioManager;
	}

	@Override
	public void run() {
		int TAMANO_PAKETE = 512;

//		AudioQueu.setContadorRecibir(0);
		Integer contador = 0;

		AudioQueu.setAudioRecibido( new ConcurrentHashMap<Integer, byte[]>());

		Boolean primerPaquete = true;
		int totalPaquetes = 0;
//		datos = "AUDIO RECIB: " + String.valueOf(totalPaquetes);
//		((MonitorActivity)activity.fragmentTab1).origen2.post(new Runnable() {
//
//			@Override
//			public void run() {
//				((MonitorActivity)activity.fragmentTab1).origen2.setText(datos);
//
//			}
//		});
		
		
		Integer	encoding = AudioFormat.ENCODING_PCM_16BIT;
		int mSampleRate = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("sample.rate"));
		//Aqui se instancia el track con la eliminacion de eco
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,mSampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				encoding, TAMANO_PAKETE,
				AudioTrack.MODE_STREAM);

		track.play();
		audioManager.setStreamVolume(
			    AudioManager.STREAM_MUSIC,
			    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
			    0);
		

		while (AudioQueu.getComunicacionAbierta()) {
			try {
				byte[] receiveData = new byte[TAMANO_PAKETE];

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				clientSocket.setSoTimeout(10000);
				clientSocket.receive(receivePacket);
				
				totalPaquetes++;
				
//				datos = "AUDIO RECIB: "  + clientSocket.getLocalPort() + "/" + String.valueOf(totalPaquetes);
//				((MonitorActivity)activity.fragmentTab1).origen2.post(new Runnable() {
//
//					@Override
//					public void run() {
//						((MonitorActivity)activity.fragmentTab1).origen2.setText(datos);
//
//					}
//				});
				
				primerPaquete = false;
				byte[] data = descomprimirGZIP(receiveData, 512);	
				
				track.write(data, 0,data.length);	
				
				
//				AudioQueu.getAudioRecibido().put(contador, data);
//				contador++;
//				AudioQueu.setContadorRecibir(contador);

				paqRecibido++;
//				audioRecibido.post(new Runnable() {
//					@Override
//					public void run() {
//						audioRecibido.setVisibility(View.VISIBLE);
//						audioRecibido.setProgress(paqRecibido);
//						if(paqRecibido > 100){
//							paqRecibido = 0;
//						}
//					}
//				});


			} catch (SocketTimeoutException e){
				e.printStackTrace();
				AudioQueu.setComunicacionAbierta(false);
				MonitoreoPorteroIOAsyncTask monitorearPorteroAsyncTask = new MonitoreoPorteroIOAsyncTask(activity);
				monitorearPorteroAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				clientSocket.close();
//				if(primerPaquete){
//					audioRecibido.post(new Runnable() {
//						@Override
//						public void run() {
//							
//							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//									activity);
//							alertDialogBuilder.setTitle(activity.getResources().getString(R.string.titulo_error))
//							.setMessage(activity.getResources().getString(R.string.error_internet))
//							.setCancelable(false)
//							.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,int id) {
//									activity.cerrarConexion();
//								}
//							});
//
//							AlertDialog alertDialog = alertDialogBuilder.create();
//							alertDialog.show();
//						}
//					});
//				}
					
			} catch (IOException e) {
				e.printStackTrace();
				AudioQueu.setComunicacionAbierta(false);
			}
		}
		track.stop();
		track.release();
		Log.d("audio cerrado", "Cerrando audio");
		clientSocket.close();
//		AudioQueu.socketComandoInternet.close();
//		AudioQueu.socketComandoInternet = null;
		AudioQueu.ipComunicacionHole = null;
	}



	/**
	 * Obtiene el array de bytes descomprimido a partir de otro array de bytes
	 * comprimido
	 * 
	 * @param file
	 *            los datos comprimidos
	 * @return los datos descomprimidos.
	 * @throws IOException
	 *             de vez en cuando
	 */
	public byte[] descomprimirGZIP(byte[] file, Integer paquete)  {
		ByteArrayInputStream gzdata = new ByteArrayInputStream(file);
		GZIPInputStream gunzipper;
		try {
			gunzipper = new GZIPInputStream(gzdata, file.length);
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			byte[] readed = new byte[paquete];
			int actual = 1;
			while ((actual = gunzipper.read(readed)) > 0) {
				data.write(readed, 0, actual);
			}
			gzdata.close();
			gunzipper.close();
			byte[] returndata = data.toByteArray();
			return returndata;
		} catch (IOException e) {
		}
		return new byte[paquete];
	}
}