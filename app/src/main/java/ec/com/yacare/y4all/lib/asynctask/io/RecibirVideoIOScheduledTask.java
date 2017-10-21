package ec.com.yacare.y4all.lib.asynctask.io;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.enumer.EstadoEventoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEventoEnum;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RecibirVideoIOScheduledTask extends Thread {

	private Boolean isConnectedV = true;

	private MonitorIOActivity monitorIOActivity;

	private Context context;
	private ImageView imageView;

	private Integer paqRecibido = 0;
	private Bitmap bmp;

	private  ProgressBar videoRecibido;
	private Integer contador;

	private Integer contadorVideo;

	private String ruta;

	public RecibirVideoIOScheduledTask(Context context, ImageView imageView, ProgressBar videoRecibido, String ruta, MonitorIOActivity monitorIOActivity) {
		super();
		this.context = context;
		this.imageView = imageView;
		this.videoRecibido = videoRecibido;
		this.monitorIOActivity = monitorIOActivity;
		this.ruta = ruta;

	}

	@Override
	public void run() {

		synchronized(ruta) {
			AudioQueu.setVideoRecibido(new ConcurrentHashMap<Integer, byte[]>());
			contadorVideo = 0;

			int BUF_SIZE = 1024 * 90;

			try {
				AudioQueu.mSocketVideo = IO.socket(ruta);
				AudioQueu.mSocketVideo.on(Socket.EVENT_CONNECT, onConnectV);
				AudioQueu.mSocketVideo.on(Socket.EVENT_DISCONNECT, onDisconnectV);
				AudioQueu.mSocketVideo.on(Socket.EVENT_CONNECT_ERROR, onConnectErrorV);
				AudioQueu.mSocketVideo.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorV);
				AudioQueu.mSocketVideo.on("chat message", onVideo);
				AudioQueu.mSocketVideo.connect();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			Log.d("video ingreso", "video " );

			contador = 0;
			while (AudioQueu.getComunicacionAbierta()) {
				try {
					if (AudioQueu.getVideoRecibido().size() > 0 && AudioQueu.getVideoRecibido().containsKey(contador)) {
						byte[] data = descomprimirGZIP(AudioQueu.getVideoRecibido().get(contador), BUF_SIZE);
						bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

						paqRecibido++;
						imageView.post(new Runnable() {
							@Override
							public void run() {
								monitorIOActivity.loadingPanel.setVisibility(View.GONE);
								monitorIOActivity.videoPanel.setVisibility(View.VISIBLE);
								imageView.setVisibility(View.VISIBLE);
								imageView.setVisibility(View.VISIBLE);
								imageView.setImageBitmap(bmp);
								//imageView.setRotation(180);
//							videoRecibido.setVisibility(View.VISIBLE);
//							videoRecibido.setProgress(paqRecibido);
//							if (paqRecibido == 100) {
//								paqRecibido = 0;
//							}

							}
						});

						AudioQueu.getVideoRecibido().remove(contador);
						contador++;

						if(AudioQueu.guardarFoto){
							DatosAplicacion datosAplicacion = (DatosAplicacion) context;
							EventoDataSource datasource = new EventoDataSource(context);
							datasource.open();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							Date date = new Date();
							Evento evento = new Evento();
							evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + datosAplicacion.getEquipoSeleccionado().getNombreEquipo());
							evento.setId(UUID.randomUUID().toString());
							evento.setFecha(dateFormat.format(date));
							evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
							evento.setComando("FOTO");
							evento.setTipoEvento(TipoEventoEnum.FOTO.getCodigo());
							evento.setIdEquipo(datosAplicacion.getEquipoSeleccionado().getId());

							FileOutputStream fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/"+  evento.getId() +".jpg");
							fileOuputStream.write(data);
							fileOuputStream.close();
							evento.setMensaje("S");
							datasource.createEvento(evento);
							datasource.close();
							AudioQueu.guardarFoto = false;
						}
					}
				}catch(SecurityException e){
					e.printStackTrace();
					continue;
				}catch(IllegalStateException e){
					e.printStackTrace();
					continue;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			AudioQueu.mSocketVideo.disconnect();
			AudioQueu.mSocketVideo.off(Socket.EVENT_CONNECT, onConnectV);
			AudioQueu.mSocketVideo.off(Socket.EVENT_DISCONNECT, onDisconnectV);
			AudioQueu.mSocketVideo.off(Socket.EVENT_CONNECT_ERROR, onConnectErrorV);
			AudioQueu.mSocketVideo.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorV);
			AudioQueu.mSocketVideo.off("chat message", onVideo);
			AudioQueu.mSocketVideo.close();
			AudioQueu.mSocketVideo = null;
		}

	}


	private Emitter.Listener onConnectV = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			if (!isConnectedV) {
				isConnectedV = true;
			}

		}
	};

	private Emitter.Listener onDisconnectV = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			isConnectedV = false;

		}
	};

	private Emitter.Listener onConnectErrorV = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
//				Toast.makeText(context,
//							"Eror", Toast.LENGTH_LONG).show();
		}
	};


	private Emitter.Listener onVideo = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
//			Log.d("video recibido", "video " + contadorVideo + " / " +  contador);
			AudioQueu.getVideoRecibido().put(contadorVideo, (byte[]) args[0]);
			contadorVideo++;
		}
	};

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
