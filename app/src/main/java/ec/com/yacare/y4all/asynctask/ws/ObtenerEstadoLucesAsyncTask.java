package ec.com.yacare.y4all.asynctask.ws;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

/**
 * Created by yacare on 25/01/2017.
 */

public class ObtenerEstadoLucesAsyncTask extends AsyncTask<String, Float, String> {


    private String numeroSerie;
    private String idDispositivo;
    private String token;
    private String ipServidor;

    public ObtenerEstadoLucesAsyncTask(String numeroSerie, String idDispositivo, String token, String ipServidor) {
        super();
        this.numeroSerie = numeroSerie;
        this.idDispositivo = idDispositivo;
        this.token = token;
        this.ipServidor = ipServidor;
    }

    @Override
    protected String doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(YACSmartProperties.URL_OBTENER_ESTADO_LUCES);
        httppost.setHeader("content-type", "application/x-www-form-urlencoded");
        String respStr = "";
        try {
            StringEntity se = new StringEntity(
                    "{\"obtenerEstadoLuces\":{\"numeroSerie\":\"" + numeroSerie
                            + "\",\"idDispositivo\":\"" + idDispositivo
                            + "\",\"comando\":\"" + ""
                            + "\",\"ipServidor\":\"" + ipServidor
                            + "\",\"token\":\"" + token + "\"}}");
            httppost.setEntity(se);
            HttpResponse resp = httpclient.execute(httppost);
            respStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
            Log.d("activarEquipo",respStr);
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

    public static String obtenerIPPublico() {
        BufferedReader in = null;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");

            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
