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

import java.io.IOException;

import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class CrearCuentaCliente {

	public static String crearCuentaCliente(Cuenta cuenta, String numeroSerie, String token, String correoAnterior){
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
		
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_CREAR_CUENTA);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		String respStr = "";
		try {
			String json = "{\"crearCuentaCliente\":{\"numeroSerie\":\"" + numeroSerie
					+ "\",\"token\":\""+ token
					+ "\",\"email\":\"" + cuenta.getEmail()
					+ "\",\"emailAnterior\":\"" + correoAnterior
					+ "\",\"clave\":\"" + cuenta.getClave()
					+ "\",\"idMensajePush\":\"" + ((cuenta.getIdMensajePush() == null)?"":cuenta.getIdMensajePush())
					+ "\",\"estadoCuenta\":\"" + cuenta.getEstadoCuenta()
					+ "\",\"fechaCuenta\":\""+ cuenta.getFechaCuenta()
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