package ec.com.yacare.y4all.lib.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class RecibirVideoInternetScheduledTask  extends Thread {
	private Bitmap bmp;
	private ImageView iv;
	private ProgressBar videoRecibido;
	private Integer paqRecibido = 0;
	private Integer puerto;
	
	public RecibirVideoInternetScheduledTask(ImageView iv, ProgressBar videoRecibido, Integer puerto) {
		super();
		this.iv = iv;
		this.videoRecibido = videoRecibido;
		this.puerto = puerto;
	}
	
	@Override
	public void run() {
		DatagramSocket clientSocket = null;
		InetAddress ipEquipo;

		Boolean contesto = false;
		Integer numeroIntentos = 0;

		InetAddress ipComunicar = null;
		Integer puertoComunicar = null;
		Integer puertoLocal = null;

		try {
			ipEquipo = InetAddress.getByName(YACSmartProperties.IP_CORP_P);

			//Envia el paquete para establecer la comunicacion
			byte[] datosConfB = "PING;".getBytes();
			byte[] datosRecibir = new byte[512];
			clientSocket = new DatagramSocket();
			DatagramPacket sendPacketP = new DatagramPacket(datosConfB,
					datosConfB.length, ipEquipo, puerto);
			clientSocket.send(sendPacketP);


			while(!contesto && numeroIntentos < 10){
				try{
					numeroIntentos++;
					DatagramPacket receivePacket = new DatagramPacket(datosRecibir,
							datosRecibir.length);
					clientSocket.setSoTimeout(8000);
					clientSocket.receive(receivePacket);
					String response = new String(receivePacket.getData());
					Log.d("VIDEO", response);
					String[] splitResponse = response.split(";");
					ipComunicar = InetAddress.getByName(splitResponse[0].substring(1));
					puertoComunicar = Integer.parseInt(splitResponse[1]);
					puertoLocal = clientSocket.getLocalPort();
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
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		if(contesto) {

			try {

				String datosConfS = "VIDEOCELU";
				byte[] datosConfB = datosConfS.getBytes();

				DatagramPacket sendPacketConf = new DatagramPacket(datosConfB,
						datosConfB.length, ipComunicar, puertoComunicar);
				//Sleep para que llegue el mensaje de video
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				clientSocket.send(sendPacketConf);
				Log.d("PAQUETE ENVIADO VIDEO", "VIDEO" + paqRecibido + "/" + YACSmartProperties.IP_CORP_P + " / " + puerto + " PUERTO RECIBIR " + clientSocket.getLocalPort());
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
				Log.d("PAQUETE ENVIADO VIDEO", "IOException");
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("PAQUETE ENVIADO VIDEO", "IOException");
			}


			while (AudioQueu.getComunicacionAbierta()) {
				Log.d("PAQUETE RECIBIDO VIDEO", "AudioQueu.getComunicacionAbierta()");
				if (clientSocket != null) {
					byte[] receiveData = new byte[1024 * 64];
					DatagramPacket receivePacket = new DatagramPacket(receiveData,
							receiveData.length);
					try {
						clientSocket.setSoTimeout(5000);
						clientSocket.receive(receivePacket);
						Log.d("PAQUETE RECIBIDO VIDEO", "VIDEO11" + paqRecibido + "/" + YACSmartProperties.IP_CORP_P + " / " + puerto + " PUERTO RECIBIR " + clientSocket.getLocalPort());
						byte[] datos = descomprimirGZIP(receivePacket.getData(), receivePacket.getLength());

						bmp = BitmapFactory.decodeByteArray(datos, 0, datos.length);
						paqRecibido++;
						if (bmp != null) {
							iv.post(new Runnable() {

								@Override
								public void run() {
									iv.setVisibility(View.VISIBLE);
									iv.setImageBitmap(bmp);
//								iv.setRotation(180);
									videoRecibido.setVisibility(View.VISIBLE);
									videoRecibido.setProgress(paqRecibido);
									if (paqRecibido == 100) {
										paqRecibido = 0;
									}
								}
							});
						}
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
						Log.d("PAQUETE ENVIADO VIDEO", "SocketTimeoutException");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Log.d("PAQUETE ENVIADO VIDEO", "else AudioQueu.getDataSocketIntercomVideo() != null");
				}
			}
			if (clientSocket != null) {
				clientSocket.close();
			}
		}else{
			clientSocket.close();
		}
	}
	

	
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
