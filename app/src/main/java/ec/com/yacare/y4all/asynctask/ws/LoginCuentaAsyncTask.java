package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import ec.com.yacare.y4all.activities.LoginActivity;
import ec.com.yacare.y4all.lib.webservice.LoginCuenta;
import io.xlink.wifi.pipe.util.XlinkUtils;

public class LoginCuentaAsyncTask extends AsyncTask<String, Float, String> {


	private LoginActivity loginActivity;


	public LoginCuentaAsyncTask(LoginActivity loginActivity) {
		super();
		this.loginActivity = loginActivity;
	}

	@Override
	protected String doInBackground(String... arg0) {
		String email = loginActivity.getEmailTexto();
		String clave = loginActivity.getClaveTexto();
		String respStr = LoginCuenta.loginCuenta(email, clave, Settings.Secure.getString(loginActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID), loginActivity);
		Log.d("ws: loginCuenta",respStr);
		return respStr;
	}

	protected void onPostExecute(String respuesta) {
		XlinkUtils.shortTips("Respondio ws " + respuesta);
		loginActivity.verificarLoginCuenta(respuesta);
	}
}
