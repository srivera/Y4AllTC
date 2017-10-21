package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;

import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.webservice.ActualizarEquipo;

public class ActualizarEquipoAsyncTask extends AsyncTask<String, Float, String> {


	private Equipo equipo;


	public ActualizarEquipoAsyncTask(Equipo equipo) {
		super();
		this.equipo = equipo;
	}

		@Override
	protected String doInBackground(String... arg0) {
		String idDevice = "";
			if (equipo.getDevice() != null && equipo.getDevice().getXDevice() != null && equipo.getDevice().getXDevice().getDeviceId() != 0){
				idDevice =  String.valueOf(equipo.getDevice().getXDevice().getDeviceId());
			}

		String respStr = ActualizarEquipo.actualizarEquipo(equipo.getNumeroSerie(), equipo.getNombreEquipo(), equipo.getIpLocal(), idDevice);

		return respStr;
	}
	
//	protected void onPostExecute(String respuesta) {
//		configActivity.verificarResultadoInstalacion(respuesta);
//	}
}
