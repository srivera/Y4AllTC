package ec.com.yacare.y4all.asynctask.ws;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import ec.com.yacare.y4all.activities.LoginActivity;
import ec.com.yacare.y4all.activities.cuenta.PerfilActivity;
import ec.com.yacare.y4all.activities.instalacion.ValidarCuentaFragment;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.webservice.GuardarDispositivo;

public class GuardarDispositivoAsyncTask extends AsyncTask<String, Float, String> {

	
	private Context context;
	private ValidarCuentaFragment validarCuentaFragment;
	private LoginActivity loginActivity;
	private PerfilActivity perfilActivity;

	public GuardarDispositivoAsyncTask(Context context, ValidarCuentaFragment validarCuentaFragment, LoginActivity loginActivity, PerfilActivity perfilActivity) {
		super();
		this.context = context;
		this.validarCuentaFragment = validarCuentaFragment;
		this.loginActivity = loginActivity;
		this.perfilActivity = perfilActivity;
	}

		@Override
	protected String doInBackground(String... arg0) {
		DatosAplicacion datosAplicacion = ((DatosAplicacion) context);
		String email = datosAplicacion.getCuenta().getEmail();
		String clave = datosAplicacion.getCuenta().getClave();
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		String respStr = GuardarDispositivo.guardarDispositivo(email, clave,  Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID), "ANDROID", sharedPrefs.getString("prefNombreDispositivo", "").toString(), sharedPrefs.getString("registration_id", "").toString());
		Log.d("ws: guardarDispositivo",respStr);
		return respStr;
	}

	protected void onPostExecute(String respuesta) {
		if(validarCuentaFragment != null){
			validarCuentaFragment.verificarGuardarDispositivo(respuesta);
		}else if(loginActivity != null){
			loginActivity.verificarGuardarDispositivo(respuesta);
		}else if(perfilActivity != null){
			perfilActivity.verificarGuardarDispositivo(respuesta);
		}
	}
}
