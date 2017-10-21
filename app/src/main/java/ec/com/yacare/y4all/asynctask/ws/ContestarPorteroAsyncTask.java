package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.webservice.ContestarPortero;

public class ContestarPorteroAsyncTask extends AsyncTask<String, Float, String> {

	
	private MonitorIOActivity monitorIOActivity;
	


	public ContestarPorteroAsyncTask(MonitorIOActivity monitorIOActivity) {
		super();
		this.monitorIOActivity = monitorIOActivity;
	}

	@Override
	protected String doInBackground(String... arg0) {
		DatosAplicacion datosAplicacion;
		String idDispositivo;
		datosAplicacion = ((DatosAplicacion) monitorIOActivity.getApplicationContext());

		idDispositivo = Settings.Secure.getString(monitorIOActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);

		
		String respStr = ContestarPortero.contestarPortero(datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), idDispositivo);
		Log.d("ws: contestarPortero",respStr);
		return respStr;
	}

}
