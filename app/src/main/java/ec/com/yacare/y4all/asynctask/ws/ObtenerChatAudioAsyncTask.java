package ec.com.yacare.y4all.asynctask.ws;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import ec.com.yacare.y4all.activities.principal.PrincipalMenuActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.asynctask.dispositivo.RecibirMensajeVozScheduledTask;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.dto.Mensaje;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.DispositivoDataSource;

public class ObtenerChatAudioAsyncTask extends AsyncTask<String, Float, String> {

	
	private String idOrigen;
	private Integer numeroPaquetes;
	private Context context;
	private Dispositivo dispositivo; 
	private PrincipalMenuActivity porteroPrincipalActivity;
	private Mensaje mensaje;

	public ObtenerChatAudioAsyncTask(String idOrigen, Integer numeroPaquetes, Context context,  Dispositivo dispositivo, PrincipalMenuActivity porteroPrincipalActivity, Mensaje mensaje) {
		super();
		this.idOrigen = idOrigen;
		this.numeroPaquetes = numeroPaquetes;
		this.context = context;
		this.dispositivo = dispositivo;
		this.porteroPrincipalActivity = porteroPrincipalActivity;
		this.mensaje = mensaje;
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
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_OBTENER_CHAT_AUDIO);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		String respStr = "";
		try {
			String json = "{\"obtenerChatAudio\":{\"token\":\"" + "token"
					+ "\",\"idOrigen\":\""+ idOrigen
					+ "\",\"idDestino\":\""+ dispositivo.getId()
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
		
		String puerto = null;
		JSONObject respuestaJSON = null;
		try {
			respuestaJSON = new JSONObject(respuesta);
			puerto = respuestaJSON.getString("resultado");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if(puerto != null ){
			DatosAplicacion datosAplicacion = (DatosAplicacion) context.getApplicationContext();
			RecibirMensajeVozScheduledTask recibirMensajeVozScheduledTask = new RecibirMensajeVozScheduledTask(Integer.valueOf(puerto.replace(";", "")),
					numeroPaquetes, datosAplicacion, dispositivo, porteroPrincipalActivity, mensaje);
			recibirMensajeVozScheduledTask.start();

		}
	}
	
}
