package ec.com.yacare.y4all.lib.asynctask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.sqllite.DispositivoDataSource;

public class ComandoCrearDispositivoLoginAsyncTask extends AsyncTask<String, Float, String> {

	private Activity activity;
	private JSONArray dispositivos;

	public ComandoCrearDispositivoLoginAsyncTask(Activity activity, JSONArray dispositivos) {
		super();
		this.activity = activity;
		this.dispositivos = dispositivos;
	}

	boolean vaciar = false;
	@Override
	protected String doInBackground(String... arg0) {

		try {
			
			DispositivoDataSource dispositivoDataSource = new DispositivoDataSource(activity.getApplicationContext());
			dispositivoDataSource.open();
			if(dispositivos.length() > 0){
				dispositivoDataSource.deleteAll();
			}

			for (int i = 0, size = dispositivos.length(); i < size; i++) {
				JSONObject dispositivoInArray = dispositivos.getJSONObject(i);
				Dispositivo dispositivo = new Dispositivo();
				dispositivo.setId(dispositivoInArray.getString("id").trim());
				
				dispositivo.setNombreDispositivo(dispositivoInArray.getString("nombre"));
				if(dispositivoInArray.has("tipodispositivo")){
					dispositivo.setTipo(dispositivoInArray.getString("tipodispositivo"));
				}
				
				if(dispositivoInArray.has("iddispositivo")){
					dispositivo.setImei(dispositivoInArray.getString("iddispositivo"));
				}
				dispositivoDataSource.createDispositivo(dispositivo);
				
			}
			dispositivoDataSource.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
}
