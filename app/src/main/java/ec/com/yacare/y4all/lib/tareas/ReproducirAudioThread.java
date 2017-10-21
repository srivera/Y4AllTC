package ec.com.yacare.y4all.lib.tareas;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class ReproducirAudioThread extends Thread {


	private Integer contador;

	private Integer paquetesEspera;

	private AudioTrack track;

	public ReproducirAudioThread(Integer paquetesEspera) {
		super();
		this.paquetesEspera = paquetesEspera;
	}

	public void run() {


		int TAMANO_PAKETE = 512;

		Integer encoding = AudioFormat.ENCODING_PCM_16BIT;
		int mSampleRate = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("sample.rate"));
		//Aqui se instancia el track con la eliminacion de eco
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, mSampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				encoding, TAMANO_PAKETE,
				AudioTrack.MODE_STREAM);

		track.play();


		AudioQueu.setContadorReproducir(0);
		contador = 0;


		while (AudioQueu.getComunicacionAbierta()) {

			if (AudioQueu.getAudioRecibido().containsKey(contador) && AudioQueu.getAudioRecibido().size() > paquetesEspera) {
				if (!AudioQueu.hablar) {
					track.write(AudioQueu.getAudioRecibido().get(contador), 0, AudioQueu.getAudioRecibido().get(contador).length);
					AudioQueu.getAudioRecibido().remove(contador);
					contador++;
					AudioQueu.setContadorReproducir(contador);
				}
			}
		}


		track.stop();
		track.release();
	}

	public byte[] descomprimirGZIP(byte[] file, Integer paquete) {
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
