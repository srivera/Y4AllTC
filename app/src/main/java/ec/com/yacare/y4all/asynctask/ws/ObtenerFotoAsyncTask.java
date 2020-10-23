package ec.com.yacare.y4all.asynctask.ws;

import android.app.Activity;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.webservice.ObtenerFoto;

import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;

public class ObtenerFotoAsyncTask extends Thread {


	private Activity context;
	private String idEvento;
	private String ip;


	public ObtenerFotoAsyncTask(Activity context, String idEvento, String ip) {
		super();
		this.context = context;
		this.idEvento = idEvento;
		this.ip = ip;
	}

	public void run() {
		AudioQueu.buscandoFoto = true;
		DatosAplicacion datosAplicacion = ((DatosAplicacion) context.getApplicationContext());
		String respuesta = ObtenerFoto.obtenerFoto(datosAplicacion.getToken(), datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), idEvento, ip);
		Log.d("ws: obtenerFoto", respuesta);

		if (!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))) {
			String foto = null;
			JSONObject respuestaJSON = null;
			try {
				respuestaJSON = new JSONObject(respuesta);
				foto = respuestaJSON.getString("resultado");
			} catch (JSONException e) {
				e.printStackTrace();
				AudioQueu.buscandoFoto = false;
			}

			if (foto != null) {

				byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);

				if (decodedString != null) {
					Log.d("FOTO OBTENIDA", "WS");

					AudioQueu.fotoTimbre = decodedString;
					AudioQueu.imagenRecibida = true;

					FileOutputStream fileOuputStream;
					try {
						fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + idEvento + ".jpg");
						fileOuputStream.write(decodedString);
						fileOuputStream.close();
						AudioQueu.imagenRecibida = true;
						EventoDataSource datasource = new EventoDataSource(context);
						datasource.open();
						//Verificar si el id existe
						Evento eventoBusqueda = new Evento();
						eventoBusqueda.setId(idEvento);
						Evento evento = datasource.getEventoId(eventoBusqueda);
						if (evento != null && evento.getId() != null) {
							evento.setMensaje("S");
							datasource.updateEvento(evento);
						}
						datasource.close();
						Log.d("TimbrarAsyncTask", "8");
					} catch (FileNotFoundException e) {
						AudioQueu.buscandoFoto = false;
						e.printStackTrace();
					} catch (IOException e) {
						AudioQueu.buscandoFoto = false;
						e.printStackTrace();
					}

				}

			} else {
				AudioQueu.buscandoFoto = false;
			}
		}else{
			//Error pedir por

			String datosConfT = YACSmartProperties.COM_SOLICITAR_FOTO_INICIAL + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + idEvento + ";";

			AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
			AudioQueu.contadorComandoEnviado++;

			EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null,
					null, null, null, YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
			enviarComandoThread.start();
		}


	}
}
