package ec.com.yacare.y4all.lib.tareas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.enumer.EstadoEventoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEventoEnum;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class RecibirVideoThread extends Thread {

	private DatagramSocket clientSocketVideo = null;
	private Context context;
	private ImageView imageView;

	private Integer paqRecibido = 0;
	private Equipo equipo;

	private Bitmap bmp;

	private  ProgressBar videoRecibido;

	private Integer puertoVideo;
	private String ipEquipoInternet;

	private MonitorIOActivity monitorActivity;
	int contador = 1;

	public RecibirVideoThread(Context context, ImageView imageView, ProgressBar videoRecibido, Integer puertoVideo, String ipEquipoInternet, MonitorIOActivity monitorActivity) {
		super();
		this.context = context;
		this.imageView = imageView;
		this.videoRecibido = videoRecibido;
		this.puertoVideo = puertoVideo;
		this.ipEquipoInternet = ipEquipoInternet;
		this.monitorActivity = monitorActivity;
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

		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		Boolean recibido = false;
		contador = 0;
		while (AudioQueu.getComunicacionAbierta()) {
			byte[] receiveData = new byte[BUF_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				if (!recibido) {
					byte[] sendData = new byte[372];
					DatagramPacket sendPacketConf = new DatagramPacket(sendData,
							sendData.length, ipEquipo,
							puertoVideo);
					clientSocketVideo.send(sendPacketConf);

				}
				clientSocketVideo.setSoTimeout(200);
				clientSocketVideo.receive(receivePacket);
				recibido = true;
				Log.d("RECIBIENDO VIDEO", "RECIBIENDO VIDEO" + receivePacket.getLength());
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inSampleSize = 2;
				byte[] data  = descomprimirGZIP(receivePacket.getData(), receivePacket.getLength());
				bmp = BitmapFactory.decodeByteArray(data,0,data.length, options);
//				Log.d("Paquete video", "enviado a " + ipEquipo.toString() + " al puerto " + equipo.getPuertoVideo());
				paqRecibido++;
				Log.d("Paquetes", "Cantidad " + paqRecibido + "/" + receivePacket.getLength());
				imageView.post(new Runnable() {
					@Override
					public void run() {
						if(monitorActivity.loadingPanel.getVisibility() == View.VISIBLE) {
							monitorActivity.loadingPanel.setVisibility(View.GONE);
							monitorActivity.videoPanel.setVisibility(View.VISIBLE);
							monitorActivity.constraintLayout.bringToFront();
							monitorActivity.mostrarMensaje("Comunicación iniciada, presione el micrófono para hablar");
//							ViewGroup.LayoutParams params = monitorActivity.constraintLayout.getLayoutParams();
//							params.height = 200;
//							params.width = 500;
//							monitorActivity.constraintLayout.setLayoutParams(params);
//						ResizeWidthAnimation anim = new ResizeWidthAnimation(monitorActivity.constraintLayout, 300);
//						anim.setDuration(500);
//						monitorActivity.constraintLayout.startAnimation(anim);

//							Animation scaleAnimation = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
//							scaleAnimation.setDuration(750);
//							scaleAnimation.setFillEnabled(true);
//							scaleAnimation.setFillAfter(true);
//							monitorActivity.constraintLayout.startAnimation(scaleAnimation);


							imageView.setVisibility(View.VISIBLE);
						}
						imageView.setImageBitmap(bmp);
						//imageView.setRotation(180);
//						videoRecibido.setVisibility(View.VISIBLE);
//						videoRecibido.setProgress(paqRecibido);

//						if(paqRecibido == 100){
//							paqRecibido = 0;
//							contador = 0;
//							monitorActivity.imageItems = new ArrayList<ImageItem>();
//							monitorActivity.gridAdapter = new GridViewAdapter(monitorActivity, R.layout.grid_foto_item_layout,  monitorActivity.imageItems);
//							monitorActivity.gridView.setAdapter(monitorActivity.gridAdapter);
//						}
						contador++;

//						ImageItem imageItem = new ImageItem(bmp, String.valueOf(contador));
//						monitorActivity.imageItems.add(imageItem);
//						monitorActivity.gridAdapter.notifyDataSetChanged();
//						monitorActivity.gridAdapter = new GridViewAdapter(monitorActivity, R.layout.grid_foto_item_layout,  monitorActivity.imageItems);
//						monitorActivity.gridView.setAdapter(monitorActivity.gridAdapter);


					}
				});
				if(AudioQueu.guardarFoto){
					EventoDataSource datasource = new EventoDataSource(monitorActivity.getApplicationContext());
					datasource.open();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					Evento evento = new Evento();
					evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + equipo.getNombreEquipo());
					evento.setId(UUID.randomUUID().toString());
					evento.setFecha(dateFormat.format(date));
					evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
					evento.setComando("FOTO");
					evento.setTipoEvento(TipoEventoEnum.FOTO.getCodigo());
					evento.setIdEquipo(equipo.getId());

					FileOutputStream fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/"+  evento.getId() +".jpg");
					fileOuputStream.write(data);
					fileOuputStream.close();
					evento.setMensaje("S");
					datasource.createEvento(evento);
					datasource.close();
					AudioQueu.guardarFoto = false;
				}
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
