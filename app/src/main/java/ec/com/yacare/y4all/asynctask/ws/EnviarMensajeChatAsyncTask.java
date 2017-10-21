package ec.com.yacare.y4all.asynctask.ws;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.webservice.EnviarMensajeChat;

public class EnviarMensajeChatAsyncTask extends AsyncTask<String, Float, String> {

	
	private Activity activity;
	private String mensaje;
	private String idDestino;
	private String idOrigen;

	public EnviarMensajeChatAsyncTask(Activity activity, String idOrigen,  String idDestino, String mensaje) {
		super();
		this.activity = activity;
		this.mensaje = mensaje;
		this.idDestino = idDestino;
		this.idOrigen = idOrigen;
	}

	@Override
	protected String doInBackground(String... arg0) {
		DatosAplicacion datosAplicacion  = ((DatosAplicacion)activity.getApplicationContext());
		String respStr = EnviarMensajeChat.enviarMensajeChat(datosAplicacion.getToken(),  idOrigen, idDestino, mensaje);
		Log.d("ws: enviarMensaje",respStr);
		return respStr;
	}
	
	
}
