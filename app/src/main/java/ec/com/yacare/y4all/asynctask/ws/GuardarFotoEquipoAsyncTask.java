package ec.com.yacare.y4all.asynctask.ws;

import android.app.Activity;
import android.os.AsyncTask;
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
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

/**
 * Created by yacare on 25/01/2017.
 */

public class GuardarFotoEquipoAsyncTask extends AsyncTask<String, Float, String> {


    private Activity preferenciasActivity;
    private byte[] foto;
    private String numeroSerie;

    public GuardarFotoEquipoAsyncTask(Activity preferenciasActivity, byte[] foto, String numeroSerie) {
        super();
        this.preferenciasActivity = preferenciasActivity;
        this.foto = foto;
        this.numeroSerie = numeroSerie;
    }

    @Override
    protected String doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(YACSmartProperties.URL_GUARDAR_FOTO_EQUIPO);
        httppost.setHeader("content-type", "application/x-www-form-urlencoded");

        String imageAsString =  Base64.encodeToString(foto, Base64.DEFAULT);

          String respStr = "";
        try {
            DatosAplicacion datosAplicacion = (DatosAplicacion) preferenciasActivity.getApplicationContext();
            StringEntity se = new StringEntity(
                    "{\"guardarFotoEquipo\":{\"numeroSerie\":\"" + numeroSerie
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
