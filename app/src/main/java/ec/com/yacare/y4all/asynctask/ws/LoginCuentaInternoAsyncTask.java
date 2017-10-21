package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import ec.com.yacare.y4all.activities.SplashActivity;
import ec.com.yacare.y4all.activities.instalacion.ValidarCuentaFragment;
import ec.com.yacare.y4all.lib.webservice.LoginCuenta;
import io.xlink.wifi.pipe.util.XlinkUtils;

public class LoginCuentaInternoAsyncTask extends AsyncTask<String, Float, String> {

	
	private SplashActivity splashActivity;
	private ValidarCuentaFragment validarCuentaFragment;
	private String email;
	private String clave;
	

	public LoginCuentaInternoAsyncTask(SplashActivity splashActivity, ValidarCuentaFragment validarCuentaFragment, String email, String clave) {
		super();
		this.splashActivity = splashActivity;
		this.validarCuentaFragment = validarCuentaFragment;
		this.email = email;
		this.clave = clave;
	}

		@Override
	protected String doInBackground(String... arg0) {
			String idDispositivo;
			if(validarCuentaFragment != null){
				idDispositivo = Settings.Secure.getString(validarCuentaFragment.getActivity().getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
			}else{
				idDispositivo = Settings.Secure.getString(splashActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
			}
			String respStr = LoginCuenta.loginCuenta(email, clave, idDispositivo, null);
			Log.d("ws: loginCuentaInterno",respStr);
			return respStr;
	}
	
		
	protected void onPostExecute(String respuesta) {
		XlinkUtils.shortTips("validarIngreso 5");
		if(validarCuentaFragment != null){
			validarCuentaFragment.verificarLoginInterno(respuesta);
		}else{
			splashActivity.verificarLoginInterno(respuesta);
		}
	}
}
