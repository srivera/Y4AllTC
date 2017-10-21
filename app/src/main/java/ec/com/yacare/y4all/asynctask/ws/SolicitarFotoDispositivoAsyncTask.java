package ec.com.yacare.y4all.asynctask.ws;

import android.content.Context;
import android.os.Environment;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

/**
 * Created by yacare on 25/01/2017.
 */

public class SolicitarFotoDispositivoAsyncTask extends Thread {


    private Context context;
    private String idDispositivo;

    public SolicitarFotoDispositivoAsyncTask(Context context, String idDispositivo) {
        super();
        this.context = context;
        this.idDispositivo = idDispositivo;
    }

    @Override
    public void run() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(YACSmartProperties.URL_OBTENER_FOTO_DISPOSITIVO);
        httppost.setHeader("content-type", "application/x-www-form-urlencoded");

        String respStr = "";
        try {
            DatosAplicacion datosAplicacion = (DatosAplicacion) context;
            StringEntity se = new StringEntity(
                    "{\"obtenerFotoDispositivo\":{\"idDispositivo\":\"" + idDispositivo
                    + "\",\"token\":\"" + datosAplicacion.getToken() + "\"}}");
            httppost.setEntity(se);
            HttpResponse resp = httpclient.execute(httppost);
            respStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
            Log.d("obtenerFotoDispositivo",respStr);
            String foto = null;
            JSONObject respuestaJSON = null;
            try {
                respuestaJSON = new JSONObject(respStr);
                foto = respuestaJSON.getString("resultado");
            } catch (JSONException e) {
                e.printStackTrace();
                AudioQueu.buscandoFoto = false;
            }

            if (foto != null) {
                FileOutputStream fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + idDispositivo + ".jpg");
                byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
                fileOuputStream.write(decodedString);
                fileOuputStream.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
