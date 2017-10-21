package ec.com.yacare.y4all.lib.webservice;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class ActualizarEquipo {

	public static String actualizarEquipo(String numeroSerie, String nombreEquipo, String ipLocal, String idDevice){
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_ACTUALIZAR_EQUIPO);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		String respStr = "";
		
		try {
			StringEntity se = new StringEntity("{\"actualizarEquipo\":{\"numeroSerie\":\""+ numeroSerie
					+ "\",\"nombreEquipo\":\""+ nombreEquipo
					+ "\",\"ipLocal\":\""+ ipLocal
					+ "\",\"ipPublico\":\""+ obtenerIPPublico()
					+ "\",\"version\":\""+ "1"
					+ "\",\"idPushEquipo\":\""+ idDevice
					+"\"}}"); 
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

	public static String obtenerIPPublico() {
		BufferedReader in = null;
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");

			in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
}