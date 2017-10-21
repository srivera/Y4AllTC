package ec.com.yacare.y4all.asynctask.ws;

import android.content.Context;
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

public class EtiquetarFotoAsyncTask extends AsyncTask<String, Float, String> {


	private String etiqueta;
	private String numeroSerie;
	private String foto;
	private Context context;

	public EtiquetarFotoAsyncTask(String numeroSerie, Context context, String etiqueta, String foto) {
		super();
		this.numeroSerie = numeroSerie;
		this.etiqueta = etiqueta;
		this.context = context;
		this.foto = foto;
	}

	@Override
	protected String doInBackground(String... arg0) {

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_ETIQUETAR_FOTO);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		String respStr = "";
		try {
			String json = "{\"etiquetarFoto\":{\"numeroSerie\":\"" + numeroSerie
					+ "\",\"etiqueta\":\""+ etiqueta
					+ "\",\"foto\":\""+ foto
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


	}
	
}
