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
import ec.com.yacare.y4all.activities.luces.DetalleLucesFragment;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

/**
 * Created by yacare on 25/01/2017.
 */

public class GuardarZonaAsyncTask extends AsyncTask<String, Float, String> {


    private DetalleLucesFragment detalleLucesFragment;

    private ZonaLuces zonaLuces;

    private  String nombre;

    public GuardarZonaAsyncTask(DetalleLucesFragment detalleLucesFragment, ZonaLuces zonaLuces, String nombre) {
        super();
        this.detalleLucesFragment = detalleLucesFragment;
        this.zonaLuces = zonaLuces;
        this.nombre = nombre;
    }

    @Override
    protected String doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(YACSmartProperties.URL_GUARDAR_ZONA);
        httppost.setHeader("content-type", "application/x-www-form-urlencoded");

        String respStr = "";
        DatosAplicacion datosAplicacion = (DatosAplicacion) detalleLucesFragment.getApplicationContext();
        try {
            StringEntity se = new StringEntity(
                    "{\"guardarZona\":{\"nombre\":\"" + nombre
                            + "\",\"idZona\":\"" + zonaLuces.getId()
                            + "\",\"numeroZona\":\"" + zonaLuces.getNumeroZona()
                            + "\",\"numeroSerie\":\"" + zonaLuces.getIdRouter()
                            + "\",\"estado\":\"" + "1"
                            + "\",\"token\":\""+ datosAplicacion.getToken()
                            + "\",\"idDispositivo\":\""+ Settings.Secure.getString(detalleLucesFragment.getContentResolver(),Settings.Secure.ANDROID_ID)
                            + "\",\"encenderTimbre\":\"" + zonaLuces.getEncenderTimbre() + "\"}}");
            httppost.setEntity(se);
            HttpResponse resp = httpclient.execute(httppost);
            respStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
            Log.d("guardarZona",respStr);
            return respStr;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respStr;
    }


    protected void onPostExecute(String resultado) {
       detalleLucesFragment.verificarZonas(resultado);
    }
}
