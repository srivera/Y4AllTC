package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;

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

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class CrearArticulosAsyncTask extends AsyncTask<String, Float, String> {


	@Override
	protected String doInBackground(String... arg0) {
			final HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);

			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

			HttpClient httpclient = new DefaultHttpClient(httpParams);
//			HttpPost httppost = new HttpPost("http://" + "ec2-54-187-12-91.us-west-2.compute.amazonaws.com" + ":"+ "8080" +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?crearEquipos");
			HttpPost httppost = new HttpPost("http://" + "wiibell.cloudapp.net" + ":"+ "10083" +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?crearArticulos");
				httppost.setHeader("content-type", "application/x-www-form-urlencoded");

			String respStr = "";
			try {
				String json = "{\"crearArticulos\":{\"cantidad\":\"" + "15"
						+ "\",\"tienda\":\""+ "YACARE"
						+ "\",\"modelo\":\""+ "D4W"
						+ "\",\"lote\":\""+ "AAC"
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
