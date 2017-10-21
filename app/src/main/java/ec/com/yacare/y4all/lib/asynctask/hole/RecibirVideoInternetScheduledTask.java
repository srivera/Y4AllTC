package ec.com.yacare.y4all.lib.asynctask.hole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.util.AudioQueu;


public class RecibirVideoInternetScheduledTask  extends Thread {
	private Bitmap bmp;
	private ImageView iv;
	private ProgressBar videoRecibido;
	private Integer paqRecibido = 0;
	private MonitorIOActivity activity;
	private String datos;

	public RecibirVideoInternetScheduledTask(ImageView iv, ProgressBar videoRecibido, MonitorIOActivity activity) {
		super();
		this.iv = iv;
		this.videoRecibido = videoRecibido;
		this.activity = activity;
	}

	@Override
	public void run() {

		int totalPaquetes= 0;
		
//		datos = "VIDEO RECIB: " + String.valueOf(totalPaquetes);
//		((MonitorActivity)activity.fragmentTab1).origen3.post(new Runnable() {
//
//			@Override
//			public void run() {
//				((MonitorActivity)activity.fragmentTab1).origen3.setText(datos);
//
//			}
//		});
		
		
		DatagramSocket clientSocket = AudioQueu.getDataSocketIntercomVideo();
		
		while (AudioQueu.getComunicacionAbierta()) {
			if(clientSocket != null){
				byte[] receiveData = new byte[1024 * 20];
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				try {
					
					clientSocket.setSoTimeout(1000);
					clientSocket.receive(receivePacket);
					totalPaquetes++;
//					datos = "VIDEO RECIB: " +  clientSocket.getLocalPort()+ "/" +  String.valueOf(totalPaquetes);
//					((MonitorActivity)activity.fragmentTab1).origen3.post(new Runnable() {
//
//						@Override
//						public void run() {
//							((MonitorActivity)activity.fragmentTab1).origen3.setText(datos);
//
//						}
//					});
					
					byte[] datos = descomprimirGZIP(receivePacket.getData(), receivePacket.getLength());

					bmp = BitmapFactory.decodeByteArray(datos,0,datos.length);

//					if(AudioQueu.tomarFoto){
//						AudioQueu.tomarFoto = false;
//						FileOutputStream out = null;
//						try {
//							out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + "Y4Baby"+ (new Date()).getTime() + ".jpg");
//							bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
//						} catch (Exception e) {
//							e.printStackTrace();
//						} finally {
//							try {
//								if (out != null) {
//									out.close();
//								}
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//					}
					paqRecibido++;
					if(bmp != null)
					{
						iv.post(new Runnable() {

							@Override
							public void run() {
								if(bmp != null){
									iv.setVisibility(View.VISIBLE);
									iv.setImageBitmap(bmp);
//									iv.setRotation(180);
									videoRecibido.setVisibility(View.VISIBLE);
									videoRecibido.setProgress(paqRecibido);
									if(paqRecibido == 100){
										paqRecibido = 0;
									}
								}
							}
						});
					}
				} catch (SocketTimeoutException e){
					e.printStackTrace();
					Log.d("PAQUETE ENVIADO VIDEO", "SocketTimeoutException") ;
					continue;
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}else{
				Log.d("PAQUETE ENVIADO VIDEO", "else AudioQueu.getDataSocketIntercomVideo() != null") ;
			}
		}

		if(clientSocket  != null){
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
