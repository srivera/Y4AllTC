package ec.com.yacare.y4all.asynctask.ws;


import android.os.AsyncTask;
import android.util.Log;

import ec.com.yacare.y4all.activities.SplashActivity;
import ec.com.yacare.y4all.activities.instalacion.AgregarEquipoFragment;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.webservice.ObtenerEquipoPorNumeroSerie;

public class ObtenerEquipoPorNumeroSerieAsyncTask extends AsyncTask<String, Float, String> {

	private AgregarEquipoFragment agregarEquipoActivity;

	private SplashActivity splashActivity;
	
	public ObtenerEquipoPorNumeroSerieAsyncTask(AgregarEquipoFragment agregarEquipoActivity, SplashActivity splashActivity) {
		super();
		this.agregarEquipoActivity = agregarEquipoActivity;
		this.splashActivity = splashActivity;
	}

		@Override
	protected String doInBackground(String... arg0) {
		String valorNumSerie = null;
		DatosAplicacion datosAplicacion = null;
		if(agregarEquipoActivity!= null){
			valorNumSerie = agregarEquipoActivity.getNumeroSerieText();
			datosAplicacion = ((DatosAplicacion) agregarEquipoActivity.getActivity().getApplicationContext());
		}else if(splashActivity != null){
			valorNumSerie = splashActivity.numeroSerie;
			datosAplicacion = ((DatosAplicacion) splashActivity.getApplicationContext());
		}
		
		String respStr = ObtenerEquipoPorNumeroSerie.obtenerEquipoPorNumeroSerie(valorNumSerie, datosAplicacion.getToken());
		Log.d("ws: obtenerEquipoNumeroSerie",respStr);
		return respStr;
	}
	
	protected void onPostExecute(String respuesta) {
		if(agregarEquipoActivity!= null){
			agregarEquipoActivity.mostrarRespuestaIngresarEquipo(respuesta);
		}else if(splashActivity!= null){
			splashActivity.mostrarRespuestaIngresarEquipo(respuesta);
		}
	}
}
