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

public class CrearEquiposAsyncTask extends AsyncTask<String, Float, String> {

	String ambiente;

	public CrearEquiposAsyncTask(String ambiente) {
		this.ambiente = ambiente;
	}

	@Override
	protected String doInBackground(String... arg0) {
			final HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);

			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

			HttpClient httpclient = new DefaultHttpClient(httpParams);
//			HttpPost httppost = new HttpPost("http://" + "ec2-54-187-12-91.us-west-2.compute.amazonaws.com" + ":"+ "8080" +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?crearEquipos");
			HttpPost httppost = new HttpPost("http://" + "201.217.103.92" + ":"+ "10083" +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?crearEquipos");
				httppost.setHeader("content-type", "application/x-www-form-urlencoded");

			String respStr = "";
			try {
				String json = "{\"crearEquipos\":{\"cantidad\":\"" + "10"
//						+ "\",\"tipoEquipo\":\""+ "8aef4b3d-46fc-45f9-8136-6a13d899665f                        "
						+ "\",\"tipoEquipo\":\""+ "93613ebf-6423-4923-8d02-9cc69a85bed3                        "
						+ "\",\"ambiente\":\""+ ambiente
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
