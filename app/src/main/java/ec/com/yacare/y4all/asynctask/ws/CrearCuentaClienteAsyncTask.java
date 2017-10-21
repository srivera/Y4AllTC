package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.util.Log;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.instalacion.CrearCuentaFragment;
import ec.com.yacare.y4all.activities.instalacion.ValidarCuentaFragment;
import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.webservice.CrearCuentaCliente;

public class CrearCuentaClienteAsyncTask extends AsyncTask<String, Float, String> {
	
	private CrearCuentaFragment crearCuentaFragment;
	private String correoAnterior;
	private ValidarCuentaFragment validarCuentaFragment;
	
	public CrearCuentaClienteAsyncTask(CrearCuentaFragment crearCuentaFragment, ValidarCuentaFragment validarCuentaFragment, String correoAnterior) {
		super();
		this.crearCuentaFragment = crearCuentaFragment;
		this.validarCuentaFragment = validarCuentaFragment;
		this.correoAnterior = correoAnterior;
	}

		@Override
	protected String doInBackground(String... arg0) {
			Cuenta cuenta;
			DatosAplicacion datosAplicacion;
			if(crearCuentaFragment != null){
				cuenta = crearCuentaFragment.getCuenta();
				datosAplicacion = (DatosAplicacion) crearCuentaFragment.getActivity().getApplicationContext();
			}else{
				cuenta = validarCuentaFragment.getCuenta();
				datosAplicacion = (DatosAplicacion) validarCuentaFragment.getActivity().getApplicationContext();
			}

			Equipo equipo = datosAplicacion.getEquipoSeleccionado();
			String respStr = CrearCuentaCliente.crearCuentaCliente(cuenta, equipo.getNumeroSerie(), datosAplicacion.getToken(), correoAnterior);
			Log.d("ws: crearCuentaCliente",respStr);
			return respStr;
	}
	
	protected void onPostExecute(String respuesta) {
		if(crearCuentaFragment != null) {
			crearCuentaFragment.verificarEstadoCrearCuenta(respuesta);
		}
	}

}