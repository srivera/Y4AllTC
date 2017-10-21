package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.util.Log;

import ec.com.yacare.y4all.activities.instalacion.ValidarCuentaFragment;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.webservice.ActivarCuentaCliente;

public class ActivarCuentaClienteAsyncTask extends AsyncTask<String, Float, String> {
	
	private ValidarCuentaFragment validarCuentaFragment;
	
	public ActivarCuentaClienteAsyncTask(
			ValidarCuentaFragment validarCuentaFragment) {
		super();
		this.validarCuentaFragment = validarCuentaFragment;
	}

		@Override
	protected String doInBackground(String... arg0) {
		Cuenta cuenta = validarCuentaFragment.getCuenta();
		DatosAplicacion datosAplicacion = ((DatosAplicacion) validarCuentaFragment.getActivity().getApplicationContext());
		String codigoVerificacion = validarCuentaFragment.getNumeroConfirmacionText();
		String respStr = ActivarCuentaCliente.activarCuentaCliente(cuenta, codigoVerificacion, datosAplicacion.getToken());
		Log.d("ws: activarCuentaCliente",respStr);
		return respStr;
	}
	
	protected void onPostExecute(String respuesta) {
		validarCuentaFragment.verificarConfirmacionCuenta(respuesta);
	}
}
