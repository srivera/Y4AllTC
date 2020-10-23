package ec.com.yacare.y4all.activities.socket;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.ProgressBar;

import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

import ec.com.yacare.y4all.lib.asynctask.ReproducirAudioScheduledTask;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class EnviarAudioIOScheduledTask extends Thread {


	private MonitorIOActivity context;
	private AudioManager audioManager;
	private Boolean isConnected = true;

	private Integer contador;
	private ProgressBar audioRecibido;

	private String ruta;

	private int modoAudioAnterior;

	public EnviarAudioIOScheduledTask(MonitorIOActivity context,ProgressBar audioRecibido, String ruta) {
		super();
		this.context = context;
		this.audioRecibido = audioRecibido;
		this.ruta = ruta;
	}

	@Override
	public void run() {
		AudioQueu.paqRecibido = 0;
		contador = 0;
		AudioQueu.setAudioRecibido(new ConcurrentHashMap<Integer, byte[]>());

		try {
			AudioQueu.mSocket = IO.socket(ruta);
			AudioQueu.mSocket.on(Socket.EVENT_CONNECT, onConnect);
			AudioQueu.mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
			AudioQueu.mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
			AudioQueu.mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
			AudioQueu.mSocket.on("chat message", onAudio);
			AudioQueu.mSocket.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ReproducirAudioScheduledTask reproducirAudioScheduledTask = new ReproducirAudioScheduledTask(0, audioRecibido);
		reproducirAudioScheduledTask.start();


		int mSampleRate = 8000;

		int TAMANO_PAKETE = 372;

		audioManager  =(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

		modoAudioAnterior = audioManager.getMode();
		audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		final int minBufferSize = AudioRecord.getMinBufferSize(
				mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		if(AudioQueu.audio_recorder == null) {
			AudioQueu.audio_recorder = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER| MediaRecorder.AudioSource.MIC, mSampleRate,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

			AudioQueu.audio_recorder.startRecording();
		}
		int bytes_read = 0;
		byte[] bufferB;
		Log.d("audio ingreso", "audio " );
		while (AudioQueu.getComunicacionAbierta()) {
			bufferB = new byte[TAMANO_PAKETE];
			bytes_read = AudioQueu.audio_recorder.read(bufferB, 0, TAMANO_PAKETE);
			AudioQueu.mSocket.emit("chat message", bufferB);
		}

		AudioQueu.mSocket.disconnect();
		AudioQueu.mSocket.off(Socket.EVENT_CONNECT, onConnect);
		AudioQueu.mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
		AudioQueu.mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
		AudioQueu.mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
		AudioQueu.mSocket.off("chat message", onAudio);
		AudioQueu.mSocket.close();
		AudioQueu.mSocket = null;
		audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
	}


	private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			if (!isConnected) {
				isConnected = true;
			}
		}
	};

	private Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			isConnected = false;
			Log.d("onConnectError AUDIO", "onDisconnect AUDIO");
		}
	};

	private Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			Log.d("onConnectError AUDIO", "onConnectError AUDIO");
		}
	};

	Boolean recibido = false;
	private Emitter.Listener onAudio = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			//Log.d("ENVIANDO AUDIO", "RECIBIENDO AUDIO");
			if(!AudioQueu.hablar) {
				AudioQueu.getAudioRecibido().put(contador, (byte[]) args[0]);
				contador++;

			}
			AudioQueu.paqRecibido++;
			if(!recibido){
				recibido  = true;
				context.mostrarMensaje("PRESIONE el botón para hablar y suba el volumen");
			}

		}

	};

}
