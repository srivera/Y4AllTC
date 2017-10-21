package ec.com.yacare.y4all.asynctask.ws;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.webservice.EncontrarDispositivo;

public class EncontrarDispositivoAsyncTask extends AsyncTask<String, Float, String> {

	
	private Activity activity;
	private String idDestino;
	private String idOrigen;

	public EncontrarDispositivoAsyncTask(Activity activity, String idOrigen,  String idDestino) {
		super();
		this.activity = activity;
		this.idDestino = idDestino;
		this.idOrigen = idOrigen;
	}

	@Override
	protected String doInBackground(String... arg0) {
		DatosAplicacion datosAplicacion  = ((DatosAplicacion)activity.getApplicationContext());
		String respStr = EncontrarDispositivo.encontrarDispositivo(datosAplicacion.getToken(),  idOrigen, idDestino);
		Log.d("ws: enviarMensaje",respStr);
		return respStr;
	}
	
	
}
