package ec.com.yacare.y4all.lib.asynctask.dispositivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class RecibirAudioIntercomAsyncTask  extends  AsyncTask<String, Float, String> {

	private Context context;
	private DatagramSocket clientSocket;
	private TextView textoPaqRecibido;
	private Activity activity;
	private AudioTrack track;

	private Integer paqRecibido = 0;

	public RecibirAudioIntercomAsyncTask(DatagramSocket clientSocket, Context context, TextView textoPaqRecibido, Activity activity) {
		super();
		this.clientSocket = clientSocket;
		this.context = context;
		this.activity = activity;
		this.textoPaqRecibido = textoPaqRecibido;
	}



	@Override
	protected String doInBackground(String... arg0) {

//		int TAMANO_PAKETE = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("tamano.paquete.audio")) * 10;

		int TAMANO_PAKETE = 512;
		
		Integer	encoding = AudioFormat.ENCODING_PCM_16BIT;
		int mSampleRate = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("sample.rate"));
		//Aqui se instancia el track con la eliminacion de eco
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,mSampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				encoding, TAMANO_PAKETE,
				AudioTrack.MODE_STREAM);

		track.play();


		while (AudioQueu.getComunicacionAbierta()) {
			try {
				byte[] receiveData = new byte[TAMANO_PAKETE];

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				clientSocket.setSoTimeout(10000);
				clientSocket.receive(receivePacket);
				
				paqRecibido++;
				
				byte[] data = descomprimirGZIP(receiveData, TAMANO_PAKETE);	
				Log.d("RECIBIDO", "REC " + paqRecibido + " / " + data.length);
				track.write(data, 0,data.length);

			} catch (SocketTimeoutException e){
				AudioQueu.setComunicacionAbierta(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@Override
	protected void onPostExecute(String respuesta) {
		Log.d("audio cerrado", "Cerrando audio");
		clientSocket.close();
		activity.finish();
		track.stop();
		track.release();
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