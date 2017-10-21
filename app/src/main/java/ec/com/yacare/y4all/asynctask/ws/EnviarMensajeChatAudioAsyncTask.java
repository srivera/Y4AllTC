package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.util.Log;

import ec.com.yacare.y4all.activities.principal.PrincipalMenuActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.webservice.EnviarMensajeChatAudio;

public class EnviarMensajeChatAudioAsyncTask extends AsyncTask<String, Float, String> {

	private PrincipalMenuActivity activity;
	private String idDestino;
	private String idOrigen;

	public EnviarMensajeChatAudioAsyncTask(PrincipalMenuActivity activity, String idOrigen,  String idDestino) {
		super();
		this.activity = activity;
		this.idDestino = idDestino;
		this.idOrigen = idOrigen;
	}

	@Override
	protected String doInBackground(String... arg0) {
		DatosAplicacion datosAplicacion  = ((DatosAplicacion)activity.getApplicationContext());
		String respStr = EnviarMensajeChatAudio.enviarMensajeChatAudio(datosAplicacion.getToken(),  idOrigen, idDestino);
		Log.d("ws: enviarMensaje",respStr);
		return respStr;
	}
	
	protected void onPostExecute(String respuesta) {
//		if(activity != null){
//			activity.verificarEnvioMensajeAudio(respuesta);
//		}
	}
	
}
