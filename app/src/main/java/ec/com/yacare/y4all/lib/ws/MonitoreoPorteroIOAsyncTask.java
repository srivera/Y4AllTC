package ec.com.yacare.y4all.lib.ws;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class MonitoreoPorteroIOAsyncTask extends AsyncTask<String, Float, String> {


	private MonitorIOActivity monitorActivity;


	public MonitoreoPorteroIOAsyncTask(MonitorIOActivity monitorActivity) {
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
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_MONITOREAR_PORTERO);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");



		ConnectivityManager manager = (ConnectivityManager) monitorActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo WiFiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		String tipoComunicacion = "3G";
		if((mobileInfo != null &&  mobileInfo.isConnectedOrConnecting()) || (WiFiInfo != null  && WiFiInfo.isConnectedOrConnecting()))
		{
			if(mobileInfo != null &&  mobileInfo.isConnectedOrConnecting() ){
				tipoComunicacion = "3G";
			}
			if(WiFiInfo != null &&  WiFiInfo.isConnectedOrConnecting() ){
				tipoComunicacion = "WIFI";
			}

		}

		String respStr = "";
		try {
			String json = "{\"monitorear\":{\"numeroSerie\":\"" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
					+ "\",\"idDispositivo\":\""+ Settings.Secure.getString(monitorActivity.getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID)
					+ "\",\"idEvento\":\""+ monitorActivity.idMonitoreo
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
		monitorActivity.verificarResultadoMonitoreoSocket(respuesta);
	}
	
}
