package ec.com.yacare.y4all.lib.asynctask.dispositivo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class EnviarMensajeVozScheduledTask extends Thread  {

	private Integer numeroPaquetes;
	private Integer puerto;

	public EnviarMensajeVozScheduledTask(Integer puerto, Integer numeroPaquetes) {
		super();
		this.puerto = puerto;
		this.numeroPaquetes = numeroPaquetes;
	}

	@Override
	public void run() {
		InetAddress ipEquipo;
		DatagramSocket socket = null;
		try {
			ipEquipo = InetAddress.getByName(YACSmartProperties.IP_CORP_P);
			socket = new DatagramSocket();


			//"codigo;id;tipo;nombre;totalpaquetes;"
			String datosConfS = YACSmartProperties.COM_MENSAJE_VOZ + ";" ;

			byte[] datosConfB = datosConfS.getBytes();
			byte[] datosComando = new byte[372];

			System.arraycopy(datosConfB, 0, datosComando, 0, datosConfB.length);

			DatagramPacket sendPacketConf = new DatagramPacket(datosComando,
					datosComando.length, ipEquipo,
					puerto);
			socket.send(sendPacketConf);

			Integer contador = 0;

//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			HashMap<Integer, byte[]> audioMensaje = new HashMap<Integer, byte[]>();

			while(contador < numeroPaquetes){
				try{
					byte[] archivo = new byte[372];
					DatagramPacket receivePacket = new DatagramPacket(archivo,
							archivo.length);
					socket.setSoTimeout(2000);
					socket.receive(receivePacket);
					byte[] datos = descomprimirGZIP(archivo, 372);
					audioMensaje.put(contador, datos);
					contador++;
					
					Log.d("MENSAJE VOZ","recibido " +contador + datos.length);
//					outputStream.write(datos, 0, datos.length);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					break;
				}
			}
//			outputStream.close();
			if(audioMensaje.size() > 0){
				Log.d("MENSAJE VOZ","reproducir");
				AudioTrack track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT,372,
						AudioTrack.MODE_STREAM);
//				track.setNotificationMarkerPosition(outputStream.size() / 2);
//				track.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
//					@Override
//					public void onPeriodicNotification(AudioTrack track) {
//						// nothing to do
//					}
//					@Override
//					public void onMarkerReached(AudioTrack track1) {
//						if(track1.getState() == AudioTrack.STATE_INITIALIZED){
//							track1.stop();
//							track1.release();
//						}
//					}
//				});
				track.play();
				contador = 0;
				while(contador < audioMensaje.size()){
					track.write(audioMensaje.get(contador), 0, audioMensaje.get(contador).length);
					contador++;
				}
				track.stop();
				track.release();
				
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket.close();
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
