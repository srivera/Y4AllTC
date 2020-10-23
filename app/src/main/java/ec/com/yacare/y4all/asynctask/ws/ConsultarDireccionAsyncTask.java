package ec.com.yacare.y4all.asynctask.ws;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;


public class ConsultarDireccionAsyncTask extends AsyncTask<String, Float, Boolean> {


	private Context datosAplicacion;

	public ConsultarDireccionAsyncTask(Context datosAplicacion) {
		super();
		this.datosAplicacion = datosAplicacion;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		try{
			StringBuilder result = new StringBuilder();
			URL url = new URL("http://www.yacaretech.com/datos/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			String resultado = result.toString();


			if(resultado != null && resultado.length() > 0) {
				int posicionInicialPri = resultado.indexOf("inipri");
				int posicionFinalPri = resultado.indexOf("finpri") + 6;
				if (posicionInicialPri != -1) {
					String servidorPrimario = resultado.substring(posicionInicialPri, posicionFinalPri).replace("inipri", "").replace("finpri", "");
					Log.d("direccion primario", servidorPrimario);

					int posicionInicialSec = resultado.indexOf("inisec");
					int posicionFinalSec = resultado.indexOf("finsec") + 6;
					String servidorSecundario = resultado.substring(posicionInicialSec, posicionFinalSec).replace("inisec", "").replace("finsec", "");
					Log.d("direccion secundario", servidorSecundario);

					//Actualizar

					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(datosAplicacion);
					String servidorPrimarioAnterior = sharedPrefs.getString("prefservidorPrimario", "").toString();
					String servidorSecundarioAnterior = sharedPrefs.getString("prefservidorSecundario", "").toString();

					if (servidorPrimario != null && servidorPrimario.length() > 7 && !servidorPrimarioAnterior.equals(servidorPrimario)) {
						YACSmartProperties.IP_CORP_P = servidorPrimario;
						YACSmartProperties.actualizarURL();
						SharedPreferences.Editor editor = sharedPrefs.edit();
						editor.putString("prefservidorPrimario", servidorPrimario);
						editor.apply();
						editor.commit();
					}
					if (servidorSecundario != null && servidorSecundario.length() > 7 && !servidorSecundarioAnterior.equals(servidorSecundario)) {
						YACSmartProperties.IP_CORP_S = servidorSecundario;
						YACSmartProperties.actualizarURL();
						SharedPreferences.Editor editor = sharedPrefs.edit();
						editor.putString("prefservidorSecundario", servidorSecundario);
						editor.apply();
						editor.commit();
					}
				}
				return true;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
