package ec.com.yacare.y4all.lib.webservice;

import java.io.IOException;

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

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class ObtenerListaDispositivos {

	public static String obtenerListaDispositivo(String email, String token, String idDispositivo){
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_OBTENER_LISTA_DISPOSITIVOS);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		String respStr = "";
		try {
			String json = "{\"obtenerListaDispositivos\":{\"email\":\"" + email
					+ "\",\"token\":\""+ token
					+ "\",\"idDispositivo\":\""+ idDispositivo
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
}