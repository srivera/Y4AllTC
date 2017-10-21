package ec.com.yacare.y4all.asynctask.ws;

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

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.cuenta.PerfilActivity;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

/**
 * Created by yacare on 25/01/2017.
 */

public class CambiarClaveAsyncTask extends AsyncTask<String, Float, String> {

    private PerfilActivity perfilActivity;
    private String claveNueva;

    public CambiarClaveAsyncTask(PerfilActivity perfilActivity, String claveNueva) {
        super();
        this.perfilActivity = perfilActivity;
        this.claveNueva = claveNueva;
    }

    @Override
    protected String doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(YACSmartProperties.URL_CAMBIAR_CLAVE);
        httppost.setHeader("content-type", "application/x-www-form-urlencoded");
        String respStr = "";
        DatosAplicacion datosAplicacion = (DatosAplicacion) perfilActivity.getActivity().getApplicationContext();
        try {
            StringEntity se = new StringEntity(
                    "{\"cambiarClave\":{\"claveNueva\":\"" + claveNueva
                            + "\",\"idDispositivo\":\"" + Settings.Secure.getString(perfilActivity.getActivity().getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID)
                            + "\",\"claveAnterior\":\"" + datosAplicacion.getCuenta().getClave()
                            + "\",\"email\":\"" + datosAplicacion.getCuenta().getEmail()
                            + "\",\"token\":\"" + datosAplicacion.getToken() + "\"}}");
            httppost.setEntity(se);
            HttpResponse resp = httpclient.execute(httppost);
            respStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
            Log.d("cambiarClave",respStr);
            return respStr;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respStr;
    }


    protected void onPostExecute(String resultado) {
        perfilActivity.verificarEstadoCrearCuenta(resultado);
    }
}
