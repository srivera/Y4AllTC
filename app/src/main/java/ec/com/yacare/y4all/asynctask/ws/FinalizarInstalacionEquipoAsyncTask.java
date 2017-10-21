package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.util.Log;

import ec.com.yacare.y4all.activities.instalacion.AgregarEquipoFragment;
import ec.com.yacare.y4all.activities.instalacion.ValidarCuentaFragment;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.webservice.FinalizarInstalacionEquipo;

public class FinalizarInstalacionEquipoAsyncTask extends AsyncTask<String, Float, String> {

	
	private ValidarCuentaFragment validarCuentaFragment;
	private Equipo equipo;
	private AgregarEquipoFragment agregarEquipoFragment;
	
	
	public FinalizarInstalacionEquipoAsyncTask(ValidarCuentaFragment validarCuentaFragment, Equipo equipo, AgregarEquipoFragment agregarEquipoFragment) {
		super();
		this.validarCuentaFragment = validarCuentaFragment;
		this.agregarEquipoFragment = agregarEquipoFragment;
		this.equipo = equipo;
	}

		@Override
	protected String doInBackground(String... arg0) {
			DatosAplicacion datosAplicacion;
			if(validarCuentaFragment != null) {
				datosAplicacion = (DatosAplicacion) validarCuentaFragment.getActivity().getApplicationContext();
			}else{
				datosAplicacion = (DatosAplicacion) agregarEquipoFragment.getActivity().getApplicationContext();
			}

			String respStr = FinalizarInstalacionEquipo.finalizarInstalacionEquipo(equipo.getNumeroSerie(), datosAplicacion.getToken(), equipo.getNombreEquipo(), datosAplicacion.getCuenta().getEmail());
			Log.d("finalizarInstalacion",respStr);
			return respStr;
	}
	
	protected void onPostExecute(String respuesta) {
		if(validarCuentaFragment != null) {
			validarCuentaFragment.verificarResultadoInstalacion(respuesta);
		}else{
			agregarEquipoFragment.verificarResultadoInstalacion(respuesta);
		}
	}
}
