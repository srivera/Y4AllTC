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

import ec.com.yacare.y4all.activities.LoginActivity;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import io.xlink.wifi.pipe.util.XlinkUtils;

public class LoginCuenta {

	public static String loginCuenta(String email, String clave, String idDispositivo, LoginActivity loginActivity){
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		
		
		int timeoutSocket = 1000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(YACSmartProperties.URL_LOGIN_CUENTA);
		httppost.setHeader("content-type", "application/x-www-form-urlencoded");

		if(loginActivity != null) {
			loginActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					XlinkUtils.shortTips("antes de llamar al ws ");
				}
			});
		}

		String respStr = "";
		try {
			String json = "{\"loginCuenta\":{\"email\":\"" + email
					+ "\",\"clave\":\""+ clave
					+ "\",\"idDispositivo\":\""+ idDispositivo
					+ "\"}}";
			StringEntity se = new StringEntity(json);
			httppost.setEntity(se);
			
			HttpResponse resp = httpclient.execute(httppost); 
			respStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
			if(loginActivity != null) {
				loginActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						XlinkUtils.shortTips("despues de llamar al ws ");
					}
				});
			}

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