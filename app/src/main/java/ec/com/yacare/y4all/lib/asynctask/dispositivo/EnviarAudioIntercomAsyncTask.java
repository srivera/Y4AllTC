package ec.com.yacare.y4all.lib.asynctask.dispositivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;


public class EnviarAudioIntercomAsyncTask extends AsyncTask<String, Float, String>{

	private	AudioRecord audio_recorder;
	private DatagramSocket clientSocket;
	private Integer numeroPaquetesEnviados;
	private Context context;
	private AudioManager audioManager;
	private TextView textoPaqEnviado;
	private TextView textoPaqRecibido;
	private Activity activity;
	private int modoAudioAnterior;
	private Integer puerto;

	public EnviarAudioIntercomAsyncTask(Context context, TextView textoPaqEnviado,
			AudioManager audioManager, TextView textoPaqRecibido, Integer puerto, Activity activity) {
		super();
		this.activity = activity;
		this.context = context;
		this.audioManager = audioManager;
		this.textoPaqEnviado = textoPaqEnviado;
		this.textoPaqRecibido = textoPaqRecibido;
		this.puerto = puerto;
	}


	@Override
	protected String doInBackground(String... arg0) {

		try {
			int mSampleRate = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("sample.rate"));
			InetAddress ipEquipo = InetAddress.getByName(YACSmartProperties.ipComunicacion);
			int TAMANO_PAKETE = 372;
			//Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("tamano.paquete.audio"));

			modoAudioAnterior = audioManager.getMode();

			audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

			final int minBufferSize = AudioRecord.getMinBufferSize(
					mSampleRate, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 20, 0);
			audio_recorder = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER| MediaRecorder.AudioSource.MIC, mSampleRate,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

			int bytes_read = 0;

			clientSocket = new DatagramSocket();
			audio_recorder.startRecording();

			byte[] bufferB = new byte[TAMANO_PAKETE];
			bytes_read = audio_recorder.read(bufferB, 0, TAMANO_PAKETE);
			byte[] datosEnviar =  comprimirGZIP(bufferB, TAMANO_PAKETE);
			DatagramPacket sendPacketP = new DatagramPacket(datosEnviar,
					datosEnviar.length, ipEquipo,
					puerto);
			clientSocket.send(sendPacketP);
//			textoPaqEnviado.post(new Runnable() {
//
//				@Override
//				public void run() {
//					textoPaqEnviado.setText(String.valueOf(puerto));
//				}
//			});
			AudioQueu.setClientSocket(clientSocket);
			RecibirAudioIntercomAsyncTask recibirAudioAsyncTask = new RecibirAudioIntercomAsyncTask(clientSocket, context, textoPaqRecibido, activity);
			recibirAudioAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			Integer contadorSalto = 0;
			numeroPaquetesEnviados = 0;

			while (AudioQueu.getComunicacionAbierta()) {
				bufferB = new byte[TAMANO_PAKETE];
				bytes_read =audio_recorder.read(bufferB, 0, TAMANO_PAKETE);

				contadorSalto++;
				if(10 <= contadorSalto){
					contadorSalto = 0;
				}else{
					numeroPaquetesEnviados++;
//					textoPaqRecibido.post(new Runnable() {
//
//						@Override
//						public void run() {
//							textoPaqRecibido.setText(String.valueOf(puerto) +  " / " + clientSocket.getLocalPort());
//						}
//					});
					datosEnviar =  comprimirGZIP(bufferB, TAMANO_PAKETE);
					DatagramPacket sendPacket = new DatagramPacket(datosEnviar,
							datosEnviar.length, ipEquipo,
							puerto);
					clientSocket.send(sendPacket);
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}


	protected void onPostExecute(String respuesta) {
		Log.d("Fecha Final ", ((Long) new Date().getTime()).toString());
		audio_recorder.stop();
		audio_recorder.release();
		audioManager.setMode(modoAudioAnterior);
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
