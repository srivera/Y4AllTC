package ec.com.yacare.y4all.lib.focos;

import android.os.AsyncTask;

import java.io.IOException;

import ec.com.yacare.y4all.activities.luces.LucesFragment;

public class BuscarRutersAsyncTask extends AsyncTask<String, Float, String> {

	private LucesFragment activity;

	public BuscarRutersAsyncTask( LucesFragment activity) {
		super();
		this.activity = activity;

	}


	@Override
	protected String doInBackground(String... arg0) {

		String listaRouters = "";
		try {
			WifiBox wifiBox = new WifiBox();
			listaRouters = wifiBox.buscarRouter(activity.getApplicationContext());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return listaRouters;
	}

	@Override
	protected void onPostExecute(String listaRouters) {
		activity.actualizarRouters(listaRouters);
	}
}
