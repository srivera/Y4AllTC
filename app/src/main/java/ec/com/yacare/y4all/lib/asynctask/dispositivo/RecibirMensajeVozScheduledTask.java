package ec.com.yacare.y4all.lib.asynctask.dispositivo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.activities.principal.PrincipalMenuActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.dto.Mensaje;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class RecibirMensajeVozScheduledTask extends Thread  {

	private Integer numeroPaquetes;
	private Integer puerto;
	private DatosAplicacion datosAplicacion;
	private Dispositivo dispositivo;
	private PrincipalMenuActivity porteroPrincipalActivity;
	private  Mensaje mensaje;

	public RecibirMensajeVozScheduledTask(Integer puerto, Integer numeroPaquetes,  DatosAplicacion datosAplicacion, Dispositivo dispositivo, PrincipalMenuActivity porteroPrincipalActivity,  Mensaje mensaje) {
		super();
		this.puerto = puerto;
		this.numeroPaquetes = numeroPaquetes;
		this.datosAplicacion = datosAplicacion;
		this.dispositivo = dispositivo;
		this.porteroPrincipalActivity = porteroPrincipalActivity;
		this.mensaje = mensaje;
	}

	@Override
	public void run() {
		InetAddress ipEquipo;
		DatagramSocket socket = null;
		try {
			ipEquipo = InetAddress.getByName(YACSmartProperties.ipComunicacion);
			socket = new DatagramSocket();


			//"codigo;id;tipo;nombre;totalpaquetes;"
			String datosConfS = YACSmartProperties.COM_MENSAJE_VOZ + ";" ;

			byte[] datosConfB = datosConfS.getBytes();
			byte[] datosComando = new byte[372];

			System.arraycopy(datosConfB, 0, datosComando, 0, datosConfB.length);

			DatagramPacket sendPacketConf = new DatagramPacket(datosComando,
					datosComando.length, ipEquipo,
					puerto);
			Log.d("ANTES SEND", "ANTES SEND");
			socket.send(sendPacketConf);
			Log.d("DESPUES SEND", "DESPUES SEND");
			Integer contador = 0;

			porteroPrincipalActivity.audioMensajeRecibido = new HashMap<Integer, byte[]>();

			Log.d("TOTAL PAQUETES RECIBIR", "TOTAL PAQUETES " + numeroPaquetes);

			ByteArrayOutputStream audio = new ByteArrayOutputStream();
			while(contador < numeroPaquetes){
				try{
					byte[] archivo = new byte[512];
					DatagramPacket receivePacket = new DatagramPacket(archivo,
							archivo.length);
					socket.receive(receivePacket);
					byte[] datos = descomprimirGZIP(archivo, 512);
					porteroPrincipalActivity.audioMensajeRecibido.put(contador, datos);
					audio.write(datos, 0, datos.length);
					contador++;
					
					Log.d("MENSAJE VOZ","recibido " +contador + datos.length);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					break;
				}
			}
			Log.d("TOTAL PAQUETES RECIBIDOS", "TOTAL PAQUETES " + porteroPrincipalActivity.audioMensajeRecibido.size());
			socket.close();

			OutputStream os = new FileOutputStream(new File("/sdcard/" + mensaje.getId() + ".txt"));
			BufferedOutputStream bos = new BufferedOutputStream(os);
			DataOutputStream outFile = new DataOutputStream(bos);
			outFile.write(audio.toByteArray());
			outFile.flush();
			outFile.close();


			if(porteroPrincipalActivity.audioMensajeRecibido.size() > 0 && porteroPrincipalActivity.contestarAutomatico){
				if(porteroPrincipalActivity.escucharAuto){
					porteroPrincipalActivity.audioManager.startBluetoothSco();
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.d("MENSAJE VOZ","reproducir");
				AudioTrack track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT,372,
						AudioTrack.MODE_STREAM);
				track.play();
				contador = 0;
				while(contador < porteroPrincipalActivity.audioMensajeRecibido.size()){
					track.write(porteroPrincipalActivity.audioMensajeRecibido.get(contador), 0, porteroPrincipalActivity.audioMensajeRecibido.get(contador).length);
					contador++;
				}
				track.stop();
				track.release();
				if(porteroPrincipalActivity.escucharAuto){
					porteroPrincipalActivity.audioManager.stopBluetoothSco();
				}
				//datosAplicacion.getPorteroPrincipalActivity().convertTextToSpeechPregunta(YACSmartProperties.intance.getMessageForKey("y4chat.pregunta") + " " + dispositivo.getNombreDispositivo() + "?");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
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
