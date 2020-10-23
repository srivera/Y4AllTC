package ec.com.yacare.y4all.lib.asynctask.hole;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.util.Log;
import android.widget.ImageView;
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
import java.net.UnknownHostException;
import java.util.zip.GZIPOutputStream;

import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;


public class EnviarVideoInternetScheduledTask  extends Thread {

	private	AudioRecord audio_recorder;
	private DatagramSocket clientSocket;
	private Integer numeroPaquetesEnviados;
	private AudioManager audioManager;
	private MonitorIOActivity activity;
	private int modoAudioAnterior;
	private Integer puerto;
	private ProgressBar audioRecibido;
	
	private TextView origen;
	private String datos;
	
	private ImageView iv;
	private ProgressBar videoRecibido;

	public EnviarVideoInternetScheduledTask(AudioManager audioManager,ProgressBar audioRecibido, Integer puerto, MonitorIOActivity activity, TextView origen, ImageView iv, ProgressBar videoRecibido) {
		super();
		this.activity = activity;
		this.audioManager = audioManager;
		this.audioRecibido = audioRecibido;
		this.puerto = puerto;
		this.origen = origen;
		this.iv = iv;
		this.videoRecibido = videoRecibido;
	}


	@Override
	public void run() {
		InetAddress ipEquipo;

		Boolean contesto = false;
		Integer numeroIntentos = 0;

		InetAddress ipComunicar = null;
		Integer puertoComunicar = null;
		Integer puertoLocal = null;
		
		byte[] datosRecibir = new byte[512];

		try {
			ipEquipo = InetAddress.getByName(YACSmartProperties.IP_CORP_P);

			//Envia el paquete para establecer la comunicacion
			byte[] datosConfB = "PING;".getBytes();
			
			clientSocket = new DatagramSocket();	
			DatagramPacket sendPacketP = new DatagramPacket(datosConfB,
					datosConfB.length, ipEquipo, puerto);
			clientSocket.send(sendPacketP);


			while(!contesto && numeroIntentos < 5){
				try{
					numeroIntentos++;
					DatagramPacket receivePacket = new DatagramPacket(datosRecibir,
							datosRecibir.length);
					clientSocket.setSoTimeout(1000);
					clientSocket.receive(receivePacket);
					String response = new String(receivePacket.getData());
					Log.d("VIDEO", response);
					String[] splitResponse = response.split(";");
					ipComunicar = InetAddress.getByName(splitResponse[0].substring(1));
					puertoComunicar = Integer.parseInt(splitResponse[1]);
					puertoLocal = clientSocket.getLocalPort();
					
					
//					datos = " P1: " + String.valueOf(puertoComunicar) + " P2: " + String.valueOf(puertoLocal) ;
//					((MonitorActivity)activity.fragmentTab1).origen5.post(new Runnable() {
//
//						@Override
//						public void run() {
//							((MonitorActivity)activity.fragmentTab1).origen5.setText("/" +  ((MonitorActivity)activity.fragmentTab1).origen5.getText() + " V: " + datos);
//
//						}
//					});
					
					clientSocket.close();
					clientSocket = new DatagramSocket(puertoLocal);
					contesto = true;
				} catch (SocketTimeoutException e){
					//No respondio el otro dispositivo
					continue;
				}
			}

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
//			datos = e1.getMessage();
//			((MonitorActivity)activity.fragmentTab1).origen5.post(new Runnable() {
//
//				@Override
//				public void run() {
//					((MonitorActivity)activity.fragmentTab1).origen5.setText("/" +((MonitorActivity)activity.fragmentTab1).origen5.getText() + "VE: " + datos);
//
//				}
//			});
		} catch (SocketException e1) {
			e1.printStackTrace();
//			datos = e1.getMessage();
//			((MonitorActivity)activity.fragmentTab1).origen5.post(new Runnable() {
//
//				@Override
//				public void run() {
//					((MonitorActivity)activity.fragmentTab1).origen5.setText("/" +((MonitorActivity)activity.fragmentTab1).origen5.getText() + "VE: " + datos);
//
//				}
//			});
		} catch (IOException e1) {
			e1.printStackTrace();
//			datos = e1.getMessage();
//			((MonitorActivity)activity.fragmentTab1).origen5.post(new Runnable() {
//
//				@Override
//				public void run() {
//					((MonitorActivity)activity.fragmentTab1).origen5.setText("/" +((MonitorActivity)activity.fragmentTab1).origen5.getText() + "VE: " + datos);
//
//				}
//			});
		}

			
		if(contesto){
			byte[] datosEnviar;
			
			try {
				datosRecibir = new byte[512];
				DatagramPacket sendPacket = new DatagramPacket(datosRecibir,
						datosRecibir.length, ipComunicar,
						puertoComunicar);
				
				clientSocket.send(sendPacket);
				
				AudioQueu.setDataSocketIntercomVideo(clientSocket);

				RecibirVideoInternetScheduledTask recibirVideoInternetAsyncTask = new RecibirVideoInternetScheduledTask(iv,videoRecibido, activity);
				recibirVideoInternetAsyncTask.start();
			
				Integer contador  = 0;

				while (AudioQueu.comunicacionAbierta) {
					if(AudioQueu.getVideoIntercom().containsKey(contador)){
						datosEnviar =  comprimirGZIP(AudioQueu.getVideoIntercom().get(contador), AudioQueu.getVideoIntercom().get(contador).length);
						sendPacket = new DatagramPacket(datosEnviar,
								datosEnviar.length,ipComunicar,
								puertoComunicar);
						clientSocket.send(sendPacket);
						AudioQueu.getVideoIntercom().remove(contador);
						contador++;

					}
				}
				clientSocket.close();
			} catch (SocketTimeoutException e){
				e.printStackTrace();
				clientSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				clientSocket.close();
			}
		}else{
			clientSocket.close();
		}
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
