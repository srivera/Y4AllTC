package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Base64;
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

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.cuenta.PerfilActivity;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

/**
 * Created by yacare on 25/01/2017.
 */

public class GuardarFotoDispositivoAsyncTask extends AsyncTask<String, Float, String> {


    private PerfilActivity perfilActivity;
    private byte[] foto;

    public GuardarFotoDispositivoAsyncTask(PerfilActivity perfilActivity, byte[] foto) {
        super();
        this.perfilActivity = perfilActivity;
        this.foto = foto;
    }

    @Override
    protected String doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(YACSmartProperties.URL_GUARDAR_FOTO_DISPOSITIVO);
        httppost.setHeader("content-type", "application/x-www-form-urlencoded");

        String imageAsString =  Base64.encodeToString(foto, Base64.DEFAULT);

          String respStr = "";
        try {
            DatosAplicacion datosAplicacion = (DatosAplicacion) perfilActivity.getActivity().getApplicationContext();
            StringEntity se = new StringEntity(
                    "{\"guardarFotoDispositivo\":{\"idDispositivo\":\"" + Settings.Secure.getString(perfilActivity.getActivity().getContentResolver(),Settings.Secure.ANDROID_ID)
                            + "\",\"token\":\""+ datosAplicacion.getToken()
                            + "\",\"foto\":\"" + imageAsString + "\"}}");
            httppost.setEntity(se);
            HttpResponse resp = httpclient.execute(httppost);
            respStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
            Log.d("guardarFotoDispositivo",respStr);
            return respStr;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respStr;
    }

}
