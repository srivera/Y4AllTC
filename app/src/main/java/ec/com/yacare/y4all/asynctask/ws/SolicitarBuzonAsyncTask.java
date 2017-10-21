package ec.com.yacare.y4all.asynctask.ws;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.DispositivoDataSource;

public class SolicitarBuzonAsyncTask extends AsyncTask<String, Float, String> {


	private String idEvento;
	private String numeroSerie;
	private Context context;

	public SolicitarBuzonAsyncTask(String numeroSerie, Context context, String idEvento) {
		super();
		this.numeroSerie = numeroSerie;
		this.idEvento = idEvento;
		this.context = context;
	}

	@Override
	protected String doInBackground(String... arg0) {
			
		DispositivoDataSource datasource = new DispositivoDataSource(context);
		datasource.open();
		
		String idDispositivo = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
		
		Dispositivo dispositivo = datasource.getDispositivoImei(idDispositivo);
		
		datasource.close();
		
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_SOLICITAR_BUZON);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		String respStr = "";
		try {
			String json = "{\"solicitarBuzon\":{\"numeroSerie\":\"" + numeroSerie
					+ "\",\"idEvento\":\""+ idEvento
					+ "\",\"imei\":\""+ idDispositivo
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

		if(!respuesta.equals("")){
			String archivo = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				archivo = respuestaJSON.getString("resultado");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(archivo != null){


				byte[] imageAsBytes = Base64.decode(archivo.getBytes(), Base64.DEFAULT);

				FileOutputStream fos;
				try {
					fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" +  idEvento +".mp4");

					BufferedOutputStream outputStream = new BufferedOutputStream(fos);

					outputStream.write(imageAsBytes);

					outputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}



			}
		}else{
			//Error general
		}
	}
	
}
