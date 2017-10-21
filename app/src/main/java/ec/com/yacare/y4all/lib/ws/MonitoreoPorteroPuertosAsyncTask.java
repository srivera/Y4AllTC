package ec.com.yacare.y4all.lib.ws;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.provider.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class MonitoreoPorteroPuertosAsyncTask extends AsyncTask<String, Float, String> {


	private MonitorIOActivity monitorActivity;


	public MonitoreoPorteroPuertosAsyncTask(MonitorIOActivity monitorActivity) {
		super();
		this.monitorActivity = monitorActivity;
	}

	@Override
	protected String doInBackground(String... arg0) {
		DatosAplicacion datosAplicacion  = ((DatosAplicacion) monitorActivity.getApplicationContext());

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);

		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_MONITOREAR_PORTERO_PUERTOS);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");



		ConnectivityManager manager = (ConnectivityManager) monitorActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

		//For 3G check
//		boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//				.isConnectedOrConnecting();
//		//For WiFi Check
//		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//				.isConnectedOrConnecting();

		String tipoComunicacion = "3G";
//		if(isWifi){
//			tipoComunicacion = "WIFI";
//		}
//		System.out.println(is3g + " net " + isWifi);

		String respStr = "";
		try {
			String json = "{\"monitorearPuertos\":{\"numeroSerie\":\"" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
					+ "\",\"idDispositivo\":\""+ Settings.Secure.getString(monitorActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID)
					+ "\",\"idEvento\":\""+ "123"
					+ "\",\"tipoComunicacion\":\""+ tipoComunicacion
					+ "\"}}";

			StringEntity se = new StringEntity(json);
			httppost.setEntity(se);
			HttpResponse resp = httpclient.execute(httppost);
			respStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return YACSmartProperties.getInstance().getMessageForKey("error.general");
		} catch (IOException e) {
			e.printStackTrace();
			return YACSmartProperties.getInstance().getMessageForKey("error.general");
		}
		return respStr;

	}
	
	protected void onPostExecute(String respuesta) {
		monitorActivity.verificarResultadoDirecto(respuesta);
	}
	
}
