package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.provider.Settings;

import ec.com.yacare.y4all.activities.LoginActivity;
import ec.com.yacare.y4all.activities.SplashActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.webservice.ObtenerListaDispositivos;

public class ObtenerListaDispositivoAsyncTask extends AsyncTask<String, Float, String> {

	
	private LoginActivity loginActivity;
	
	private SplashActivity splashActivity;
	

	public ObtenerListaDispositivoAsyncTask(LoginActivity loginActivity, SplashActivity splashActivity) {
		super();
		this.loginActivity = loginActivity;
		this.splashActivity = splashActivity;
	}

		@Override
	protected String doInBackground(String... arg0) {
		String email = null;
		DatosAplicacion datosAplicacion = null;
		String idDispositivo = null;
		if(loginActivity != null){
			idDispositivo= Settings.Secure.getString(loginActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
			datosAplicacion = ((DatosAplicacion) loginActivity.getApplicationContext());
			email = loginActivity.getEmailTexto();
		}else{
			idDispositivo= Settings.Secure.getString(splashActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
			datosAplicacion = ((DatosAplicacion) splashActivity.getApplicationContext());
			email = datosAplicacion.getCuenta().getEmail();
		}
		
		String respStr = ObtenerListaDispositivos.obtenerListaDispositivo(	email, datosAplicacion.getToken(),idDispositivo);
		//Log.d("ws: obtenerListaDispositivo",respStr);
		return respStr;
	}
	
	protected void onPostExecute(String respuesta) {
//		if(loginActivity != null){
//			loginActivity.verificarListaDispositivos(respuesta);
//		}else{
//			splashActivity.verificarListaDispositivos(respuesta);
//		}
	}
}
