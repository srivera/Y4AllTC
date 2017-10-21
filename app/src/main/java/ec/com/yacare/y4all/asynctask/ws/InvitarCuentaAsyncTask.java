package ec.com.yacare.y4all.asynctask.ws;

import android.app.Activity;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

/**
 * Created by yacare on 25/01/2017.
 */

public class InvitarCuentaAsyncTask extends AsyncTask<String, Float, String> {


    private String numeroSerie;
    private String idDispositivo;
    private String emailCuenta;
    private String emailInvitado;
    private String token;
    private Activity y4HomeActivity;

    public InvitarCuentaAsyncTask(Activity y4HomeActivity, String numeroSerie, String idDispositivo, String emailCuenta, String emailInvitado, String token) {
        super();
        this.numeroSerie = numeroSerie;
        this.idDispositivo = idDispositivo;
        this.emailInvitado = emailInvitado;
        this.emailCuenta = emailCuenta;
        this.token = token;
        this.y4HomeActivity = y4HomeActivity;
    }

    @Override
    protected String doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(YACSmartProperties.URL_INVITAR_CUENTA);
        httppost.setHeader("content-type", "application/x-www-form-urlencoded");

        String respStr = "";
        try {
            StringEntity se = new StringEntity(
                    "{\"invitarCuenta\":{\"numeroSerie\":\"" + numeroSerie
                            + "\",\"idDispositivo\":\"" + idDispositivo
                            + "\",\"emailCuenta\":\"" + emailCuenta
                            + "\",\"emailInvitado\":\"" + emailInvitado
                            + "\",\"idDispositivo\":\"" + Settings.Secure.getString(y4HomeActivity.getContentResolver(),Settings.Secure.ANDROID_ID)
                            + "\",\"tipo\":\"" + "I"
                            + "\",\"token\":\"" + token + "\"}}");
            httppost.setEntity(se);
            HttpResponse resp = httpclient.execute(httppost);
            respStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
            Log.d("invitarCuenta",respStr);
            return respStr;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respStr;
    }


    protected void onPostExecute(String resultado) {

    }

}
