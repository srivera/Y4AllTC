package ec.com.yacare.y4all.lib.tareas;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;


public class EnviarAudiowfThread extends Thread {

	private	AudioRecord audio_recorder;
	private MonitorIOActivity monitorActivity;
	private TextView textoEstado;
	private AudioManager audioManager;
	private int modoAudioAnterior;

	private String ipEquipoInternet;
	private Integer puertoAudio;

	public EnviarAudiowfThread(MonitorIOActivity monitorActivity, TextView textoEstado,
							   AudioManager audioManager,
							   String ipEquipoInternet, Integer puertoAudio) {
		super();
		this.textoEstado = textoEstado;
		this.monitorActivity = monitorActivity;
		this.audioManager = audioManager;
		this.ipEquipoInternet = ipEquipoInternet;
		this.puertoAudio = puertoAudio;
	}

	@Override
	public void run() {
		int mSampleRate = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("sample.rate"));
		InetAddress ipEquipo = null;
		try {
			ipEquipo = InetAddress.getByName(ipEquipoInternet);
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
		int TAMANO_PAKETE = 256;

		modoAudioAnterior = audioManager.getMode();

		audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		final int minBufferSize = AudioRecord.getMinBufferSize(
				mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		audio_recorder = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER| MediaRecorder.AudioSource.MIC, mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
		audio_recorder.startRecording();

		int bytes_read = 0;
		byte[] bufferB;
		byte[] datosEnviar;

//		AudioQueu.setAudioEnviado(new HashMap<Integer, short[]>());
//		AudioQueu.setContadorEnviar(0);

		while (AudioQueu.getComunicacionAbierta()) {
			bufferB = new byte[TAMANO_PAKETE];
			bytes_read =audio_recorder.read(bufferB, 0, TAMANO_PAKETE);

			try {
				if(AudioQueu.hablar || !AudioQueu.speakerExterno) {
					datosEnviar = comprimirGZIP(bufferB, TAMANO_PAKETE);
					Log.d("AUDIO ENVIADO ", "TAMANO " + datosEnviar.length + " / " +  bufferB.length);
				}else{
					datosEnviar = new byte[TAMANO_PAKETE];
				}
				DatagramPacket sendPacket = new DatagramPacket(datosEnviar,
						datosEnviar.length, ipEquipo,
						puertoAudio);
				AudioQueu.clientSocket.send(sendPacket);


			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}


		}

		Log.d("Fecha Final ", ((Long) new Date().getTime()).toString());

		AudioQueu.clientSocket.close();
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
