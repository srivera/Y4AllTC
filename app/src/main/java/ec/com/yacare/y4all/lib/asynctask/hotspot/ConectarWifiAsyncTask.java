package ec.com.yacare.y4all.lib.asynctask.hotspot;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ec.com.yacare.y4all.activities.instalacion.AgregarEquipoFragment;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.webservice.ObtenerEquipoPorNumeroSerie;

public class ConectarWifiAsyncTask extends Thread {

	Boolean estadoActualizado = false;
	private AgregarEquipoFragment activity;
	private String nombreWifi;
	private String clave;
	String respStr;

	public ConectarWifiAsyncTask(AgregarEquipoFragment activity, String nombreWifi, String clave) {
		super();
		this.activity = activity;
		this.nombreWifi = nombreWifi;
		this.clave = clave;
	}


	@Override
	public void run() {
		//Conectarse a wifi
		WifiManager wifiManager = (WifiManager) activity.getActivity().getSystemService(Context.WIFI_SERVICE);
		ConnectivityManager connManager = (ConnectivityManager) activity.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		wifiManager.setWifiEnabled(true);

		Boolean conectado = false;

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(wifiInfo.getSSID().equals(nombreWifi)){
			conectado = true;
		}else{

			WifiConfiguration wifiConfig = new WifiConfiguration();
			wifiConfig.SSID = String.format("\"%s\"", nombreWifi);
			wifiConfig.preSharedKey = String.format("\"%s\"", clave);
			Log.d("WIFI ", "Reconectado");
			int netId = wifiManager.addNetwork(wifiConfig);
			wifiManager.disconnect();
			wifiManager.enableNetwork(netId, true);
			wifiManager.reconnect();

			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		for(int i = 0; i < 30; i++){
			DatosAplicacion datosAplicacion = (DatosAplicacion) activity.getActivity().getApplicationContext();
			respStr = ObtenerEquipoPorNumeroSerie.obtenerEquipoPorNumeroSerie(activity.getNumeroSerieText(), datosAplicacion.getToken());
			String estadoEquipo = null;
			JSONObject equipoJSON = null;

			try {
				JSONObject respuestaJSON = new JSONObject(respStr);
				if(respuestaJSON.has("equipo")){
					equipoJSON = new JSONObject(respuestaJSON.get("equipo").toString());
					estadoEquipo = equipoJSON.getString("estado");
					if(estadoEquipo.equals(EstadoDispositivoEnum.CONFIGURACION.getCodigo())){
						estadoActualizado = true;
						activity.getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								activity.mostrarRespuestaIngresarEquipo(respStr);
							}
						});

						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		if(!estadoActualizado){
			activity.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					activity.mostrarRespuestaIngresarEquipo("ERR");
				}
			});
		}

	}
}