package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import ec.com.yacare.y4all.activities.LoginActivity;
import ec.com.yacare.y4all.activities.SplashActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.webservice.ObtenerDatosCuenta;
import io.xlink.wifi.pipe.util.XlinkUtils;

public class ObtenerDatosCuentaAsyncTask extends AsyncTask<String, Float, String> {

	
	private LoginActivity loginActivity;
	
	private SplashActivity splashActivity;
	

	public ObtenerDatosCuentaAsyncTask(LoginActivity loginActivity, SplashActivity splashActivity) {
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
			datosAplicacion = ((DatosAplicacion) loginActivity.getApplicationContext());
			email = loginActivity.getEmailTexto();
			idDispositivo= Settings.Secure.getString(loginActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
		}else{
			datosAplicacion = ((DatosAplicacion) splashActivity.getApplicationContext());
			idDispositivo= Settings.Secure.getString(splashActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
			email = datosAplicacion.getCuenta().getEmail();
		}
		
		String respStr = ObtenerDatosCuenta.obtenerDatosCuenta(email, datosAplicacion.getToken(), idDispositivo);
		Log.d("ws: obtenerDatosCuenta",respStr);
		return respStr;
	}
	
	protected void onPostExecute(String respuesta) {
		if(loginActivity != null){
			XlinkUtils.shortTips("obtenerdatoscuenta " + respuesta);
			loginActivity.verificarDatosCuenta(respuesta);
		}else{
			XlinkUtils.shortTips("validarIngreso 7");
			splashActivity.verificarDatosCuenta(respuesta);
		}
	}
}
