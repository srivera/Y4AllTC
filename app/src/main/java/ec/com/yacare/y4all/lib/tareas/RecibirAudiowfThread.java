package ec.com.yacare.y4all.lib.tareas;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
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
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.asynctask.ws.ContestarPorteroAsyncTask;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.lib.util.AudioQueu.paqRecibido;

public class RecibirAudiowfThread extends Thread {

	private MonitorIOActivity monitorActivity;
	//private DatagramSocket clientSocket;

	private AudioTrack track;

	private ProgressBar audioRecibido;

	private String ipEquipoInternet;
	private Integer puertoAudio;

	private AudioManager audioManager;
	private TextView textoEstado;

	public RecibirAudiowfThread(MonitorIOActivity monitorActivity, TextView textoEstado,
								AudioManager audioManager, ProgressBar audioRecibido,
								String ipEquipoInternet, Integer puertoAudio) {
		super();
		this.textoEstado = textoEstado;
		this.monitorActivity = monitorActivity;
		this.audioManager = audioManager;
		this.audioRecibido = audioRecibido;
		this.ipEquipoInternet = ipEquipoInternet;
		this.puertoAudio = puertoAudio;
	}


	
	@Override
	public void run() {

		//Se coloca el tamano del paquete dependiendo del ambiente
		int TAMANO_PAKETE =256 * 10;

		//Aqui se instancia el track con la eliminacion de eco
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, TAMANO_PAKETE,
				AudioTrack.MODE_STREAM);

		track.play();

		paqRecibido = 0;
		AudioQueu.hablar = false;
		Boolean recibido = false;
		Integer cantidadIntentos =0;
		Boolean activarEnvio = false;
		int timeout = 200;
		try {
			AudioQueu.clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Log.i("Paquetes Recibidos", "rec0 " );
		while (!recibido && cantidadIntentos < 100) {
			try {
				if (!recibido) {
					byte[] sendData = new byte[372];
					DatagramPacket sendPacketConf = new DatagramPacket(sendData,
							sendData.length, InetAddress.getByName(ipEquipoInternet),
							puertoAudio);
					AudioQueu.clientSocket.send(sendPacketConf);
					Log.i("Paquetes Recibidos", "rec1 " );
				}

				byte[] receiveData = new byte[TAMANO_PAKETE];

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				AudioQueu.clientSocket.setSoTimeout(timeout);
				AudioQueu.clientSocket.receive(receivePacket);
				recibido = true;
				Log.i("Paquetes Recibidos", "rec2 " );
				if(!activarEnvio){
					timeout = 10000;
					activarEnvio = true;
					EnviarAudiowfThread enviarAudioThread = new EnviarAudiowfThread(monitorActivity, textoEstado,
							audioManager, ipEquipoInternet, YACSmartProperties.PUERTO_AUDIO_DEFECTO);
					enviarAudioThread.start();

					ContestarPorteroAsyncTask contestarPorteroAsyncTask = new ContestarPorteroAsyncTask(monitorActivity);
					contestarPorteroAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					Log.i("Paquetes Recibidos", "rec3 " );
				}
			} catch (SocketTimeoutException e){
				if(cantidadIntentos > 100) {
					AudioQueu.setComunicacionAbierta(false);
					AudioQueu.llamadaEntrante = false;
					monitorActivity.cerrarComunicacion(true);
				}
				cantidadIntentos++;

			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

		}

		while (AudioQueu.getComunicacionAbierta() && recibido) {
			try{
				AudioQueu.paqRecibido++;

				byte[] receiveData = new byte[TAMANO_PAKETE];

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				AudioQueu.clientSocket.setSoTimeout(timeout);
				AudioQueu.clientSocket.receive(receivePacket);
				Log.i("Paquetes Recibidos", "rec4 " );
				if(!AudioQueu.hablar || !AudioQueu.speakerExterno) {
						byte[] data = descomprimirGZIP(receiveData, TAMANO_PAKETE);
						track.write(data, 0, data.length);
					   Log.i("Paquetes Recibidos", "Cantidad " + paqRecibido);
				}

			} catch (SocketTimeoutException e){
				AudioQueu.setComunicacionAbierta(false);
				AudioQueu.llamadaEntrante = false;
				monitorActivity.cerrarComunicacion(true);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

		}
		Log.i("audio cerrado", "Cerrando audio");
		if(AudioQueu.clientSocket.isConnected()){
			AudioQueu.clientSocket.close();
		} 
		track.stop();
		track.release();
		AudioQueu.llamadaEntrante = false;
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