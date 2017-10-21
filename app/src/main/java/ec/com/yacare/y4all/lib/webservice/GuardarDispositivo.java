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

public class GuardarDispositivo {

	public static String guardarDispositivo(String email, String clave, String idDispositivo, String tipoDispositivo, String nombreDispositivo,  String idPushMessage){
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_GUARDAR_DISPOSITIVO);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		String respStr = "";
		try {
			String json = "{\"guardarDispositivo\":{\"email\":\"" + email
					+ "\",\"clave\":\""+ clave
					+ "\",\"idDispositivo\":\""+ idDispositivo
					+ "\",\"tipoDispositivo\":\""+ tipoDispositivo
					+ "\",\"nombre\":\""+ nombreDispositivo
					+ "\",\"idPushMessage\":\""+ idPushMessage
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