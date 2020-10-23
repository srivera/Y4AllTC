package ec.com.yacare.y4all.asynctask.ws;

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
import ec.com.yacare.y4all.activities.luces.DetalleLucesFragment;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class LinkFocosAsyncTask extends AsyncTask<String, Float, String> {

	private DetalleLucesFragment detalleLucesFragment;

	private String seriesFocos;

	private String trama;

	public LinkFocosAsyncTask(DetalleLucesFragment detalleLucesFragment, String seriesFocos, String trama) {
		super();
		this.detalleLucesFragment = detalleLucesFragment;
		this.seriesFocos = seriesFocos;
		this.trama = trama;
	}


	@Override
	protected String doInBackground(String... arg0) {
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);

		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_LINK_FOCOS);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		DatosAplicacion datosAplicacion = (DatosAplicacion) detalleLucesFragment.getApplicationContext();

		String respStr = "";
		try {
			String json = "{\"linkFocos\":{\"numeroSerie\":\"" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
					+ "\",\"seriesFocos\":\""+ seriesFocos
					+ "\",\"idDispositivo\":\""+ Settings.Secure.getString(detalleLucesFragment.getContentResolver(),Settings.Secure.ANDROID_ID)
					+ "\",\"mail\":\""+ datosAplicacion.getCuenta().getEmail()
					+ "\",\"token\":\""+ datosAplicacion.getToken()
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

//	protected void onPostExecute(String resultado) {
//		detalleLucesFragment.verificarLinkFocos(resultado, trama);
//	}
}
