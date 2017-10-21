package ec.com.yacare.y4all.lib.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
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

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class RecibirVideoScheduledTask extends Thread {

	private DatagramSocket clientSocketVideo = null;
	private Context context;
	private MediaPlayer mediaPlayer;
	private ImageView imageView;
	
	private Integer paqRecibido = 0;
	private Equipo equipo;
	
	private Bitmap bmp;
	
	private  ProgressBar videoRecibido;

	private Integer puertoVideo;
	private String ipEquipoInternet;




	public RecibirVideoScheduledTask(Context context, MediaPlayer mediaPlayer, ImageView imageView, ProgressBar videoRecibido, Integer puertoVideo, String ipEquipoInternet) {
		super();
		this.context = context;
		this.mediaPlayer = mediaPlayer;
		this.imageView = imageView;
		this.videoRecibido = videoRecibido;
		this.puertoVideo = puertoVideo;
		this.ipEquipoInternet = ipEquipoInternet;

	}


	@Override
	public void run() {



		String datosConfS = "8000;1;16";
		byte[] datosConfB = datosConfS.getBytes();
		int BUF_SIZE = 1024 * 60;

		DatosAplicacion datosAplicacion = (DatosAplicacion) context;
		equipo = datosAplicacion.getEquipoSeleccionado();

		InetAddress ipEquipo = null;
		try {
			ipEquipo = InetAddress.getByName(ipEquipoInternet);
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {

			clientSocketVideo = new DatagramSocket();
			DatagramPacket sendPacketConf = new DatagramPacket(datosConfB,
					datosConfB.length, ipEquipo,
					puertoVideo);
			Boolean recibido = false;
			int intentos = 0;
			while (!recibido || intentos < 20) {
				intentos++;
				clientSocketVideo.send(sendPacketConf);
//				Log.d("Paquete video", "enviado a " + ipEquipo.toString() + " al puerto " + equipo.getPuertoVideo());
				byte[] receiveData = new byte[BUF_SIZE];
				try {
					DatagramPacket receivePacket = new DatagramPacket(receiveData,
							receiveData.length);
					clientSocketVideo.setSoTimeout(1000);
					clientSocketVideo.receive(receivePacket);
					recibido = true;
//					Log.d("Paquete video", "recibido de " + ipEquipo.toString() + " al puerto " + equipo.getPuertoVideo());
				} catch (SocketTimeoutException e1) {
					e1.printStackTrace();
					continue;
				}

			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}



		while (AudioQueu.getComunicacionAbierta()) {
			byte[] receiveData = new byte[BUF_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				clientSocketVideo.setSoTimeout(1000);
				clientSocketVideo.receive(receivePacket);
				Log.d("RECIBIENDO VIDEO", "RECIBIENDO VIDEO" + receivePacket.getLength());
				byte[] data  = descomprimirGZIP(receivePacket.getData(), receivePacket.getLength());
				bmp = BitmapFactory.decodeByteArray(data,0,data.length);
//				Log.d("Paquete video", "enviado a " + ipEquipo.toString() + " al puerto " + equipo.getPuertoVideo());
				paqRecibido++;
				imageView.post(new Runnable() {
					@Override
					public void run() {
						imageView.setVisibility(View.VISIBLE);
						imageView.setImageBitmap(bmp);
						//imageView.setRotation(180);
						videoRecibido.setVisibility(View.VISIBLE);
						videoRecibido.setProgress(paqRecibido);
						if(paqRecibido == 100){
							paqRecibido = 0;
						}

					}
				});
			} catch (SecurityException e) {
				e.printStackTrace();
				continue;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				continue;
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				continue;
			} catch (SocketException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

		}
		Log.d("video cerrado", "Cerrando video");
		clientSocketVideo.close();
		mediaPlayer.release();
		mediaPlayer = null;
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
