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
import ec.com.yacare.y4all.activities.focos.ProgramacionActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

/**
 * Created by yacare on 25/01/2017.
 */

public class GuardarProgramacionAsyncTask extends AsyncTask<String, Float, String> {


    private ProgramacionActivity programacionActivity;

    public GuardarProgramacionAsyncTask(ProgramacionActivity programacionActivity) {
        super();
        this.programacionActivity = programacionActivity;
    }

    @Override
    protected String doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(YACSmartProperties.URL_GUARDAR_PROGRAMACION);
        httppost.setHeader("content-type", "application/x-www-form-urlencoded");

        String respStr = "";
        DatosAplicacion datosAplicacion = (DatosAplicacion) programacionActivity.getApplicationContext();
        try {
            StringEntity se = new StringEntity(
                    "{\"guardarProgramacion\":{\"nombre\":\"" + programacionActivity.getProgramacionGuardar().getNombre()
                            + "\",\"idZona\":\"" + programacionActivity.getProgramacionGuardar().getIdZona()
                            + "\",\"hora\":\"" + programacionActivity.getProgramacionGuardar().getHoraInicio()
                            + "\",\"numeroSerie\":\"" + programacionActivity.getProgramacionGuardar().getIdRouter()
                            + "\",\"estado\":\"" + "1"
                            + "\",\"duracion\":\"" + programacionActivity.getProgramacionGuardar().getDuracion()
                            + "\",\"dias\":\"" + programacionActivity.getProgramacionGuardar().getDias()
                            + "\",\"token\":\""+ datosAplicacion.getToken()
                            + "\",\"idDispositivo\":\""+ Settings.Secure.getString(programacionActivity.getContentResolver(),Settings.Secure.ANDROID_ID)
                            + "\",\"accion\":\"" + programacionActivity.getProgramacionGuardar().getAccion() + "\"}}");
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
        programacionActivity.verificarProgramacion(resultado);
    }
}
