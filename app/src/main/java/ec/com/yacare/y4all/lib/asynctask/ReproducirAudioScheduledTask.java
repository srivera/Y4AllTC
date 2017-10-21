package ec.com.yacare.y4all.lib.asynctask;



import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.ProgressBar;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class ReproducirAudioScheduledTask extends Thread {


	private Integer contador;

	private Integer paquetesEspera;

	private AudioTrack track;

	private ProgressBar audioRecibido;

	private int paqRecibido = 0;


	public ReproducirAudioScheduledTask(Integer paquetesEspera, ProgressBar audioRecibido) {
		super();
		this.paquetesEspera = paquetesEspera;
		this.audioRecibido = audioRecibido;
	}

	public void run() {


		int TAMANO_PAKETE = 1024;

		Integer	encoding = AudioFormat.ENCODING_PCM_16BIT;
		int mSampleRate = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("sample.rate"));
		//Aqui se instancia el track con la eliminacion de eco
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,mSampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				encoding, TAMANO_PAKETE,
				AudioTrack.MODE_STREAM);

		track.play();


		AudioQueu.setContadorReproducir(0);
		contador = 0;


		while (AudioQueu.getComunicacionAbierta()) {

			if(AudioQueu.getAudioRecibido() .size() > 0 && AudioQueu.getAudioRecibido().containsKey(contador)  ){

				if(!AudioQueu.hablar || !AudioQueu.speakerExterno) {
					//Log.d("contadorReproducir", String.valueOf(contador));
					track.write(AudioQueu.getAudioRecibido().get(contador), 0, AudioQueu.getAudioRecibido().get(contador).length);
					AudioQueu.getAudioRecibido().remove(contador);
					contador++;
					AudioQueu.setContadorReproducir(contador);

					paqRecibido++;

//					audioRecibido.post(new Runnable() {
//						@Override
//						public void run() {
//							audioRecibido.setVisibility(View.VISIBLE);
//							audioRecibido.setProgress(paqRecibido);
//
//							if (paqRecibido > 100) {
//								paqRecibido = 0;
//							}
//						}
//					});
				}
			}else {
				if(AudioQueu.getAudioRecibido() .size() > 0 ) {

				//	Log.d("tamano del hash", String.valueOf(AudioQueu.getAudioRecibido() .size()));
				}
			}


		}

		track.stop();
		track.release();
	}

}