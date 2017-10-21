package ec.com.yacare.y4all.asynctask.ws;

import android.app.Activity;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.webservice.EnviarMensaje;

public class EnviarMensajeAsyncTask extends AsyncTask<String, Float, String> {

	
	private Activity activity;
	private String mensaje;
	

	public EnviarMensajeAsyncTask(Activity activity, String mensaje) {
		super();
		this.activity = activity;
		this.mensaje = mensaje;
	}

	@Override
	protected String doInBackground(String... arg0) {
		DatosAplicacion datosAplicacion  = ((DatosAplicacion)activity.getApplicationContext());
		String respStr = EnviarMensaje.enviarMensaje(datosAplicacion.getToken(),  datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), mensaje,
				Settings.Secure.getString(activity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID));
		Log.d("ws: enviarMensaje",respStr);
		return respStr;
	}
	
	
}
