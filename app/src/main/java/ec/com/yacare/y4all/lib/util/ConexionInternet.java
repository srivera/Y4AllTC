package ec.com.yacare.y4all.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class ConexionInternet {
	private Context _context;

	public ConexionInternet(Context context){
		this._context = context;
	}

	public boolean isConnectingToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
				for (int i = 0; i < info.length; i++) 
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						if(info[i].getType() == ConnectivityManager.TYPE_WIFI){
							try {             
								InetAddress adr = InetAddress.getByName(YACSmartProperties.IP_HOT_SPOT);

								String ipAddressPublic = null;
								try {
									for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
										NetworkInterface intf = en.nextElement();
										for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
											InetAddress inetAddress = enumIpAddr.nextElement();
											if (!inetAddress.isLoopbackAddress()) {
												ipAddressPublic = inetAddress.getHostAddress().toString();
											}
										}
									}
								} catch (SocketException ex) {
									ex.printStackTrace();
								}

								Boolean redLocal = true;
								String[] ipHotSpot = YACSmartProperties.IP_HOT_SPOT.split("\\.");
								String[] miIp = ipAddressPublic.split("\\.");

								for (int j = 0; j < 3; j++){
									if(!ipHotSpot[j].equalsIgnoreCase(miIp[j])){
										redLocal = false;
										break;
									}
								}
								if(redLocal){
									return false;
								}else{
									return true;
								}

							}
							catch (IOException e) {             
								e.printStackTrace();   
								return true;
							}
						}
						return true;
					}

		}
		return false;
	}

	public static boolean isInternetOn(Context context) {

		if (isMobileOrWifiConnectivityAvailable(context)) {
			try {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
				HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(500);
				urlc.setReadTimeout(500);
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.d("Internet not available!", "");
		}
		return false;
	}



	public static boolean isMobileOrWifiConnectivityAvailable(Context ctx) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;


		try {
			ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			for (NetworkInfo ni : netInfo) {
				if (ni.getTypeName().equalsIgnoreCase("WIFI"))
					if (ni.isConnected()) {
						haveConnectedWifi = true;
					}
				if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
					if (ni.isConnected()) {
						haveConnectedMobile = true;
					}
			}
		} catch (Exception e) {
			Log.d("[ConnectionVerifier] inside isInternetOn() Exception is : " , e.toString());
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	public void connectHotSpot(String nombreRed, String claveRed){
		boolean existsRed = false;
		WifiManager wifi = (WifiManager) _context.getSystemService(Context.WIFI_SERVICE);
		boolean res1 = wifi.setWifiEnabled(true);
		
		List<WifiConfiguration> list = wifi.getConfiguredNetworks();
		for( WifiConfiguration i : list ) {
			if(i.SSID != null && i.SSID.equals("\"" + nombreRed + "\"") ) {
				wifi.removeNetwork(i.networkId);
				
//				wifi.disconnect();
//				i.preSharedKey = "\"" + claveRed + "\"";
//				i.status = WifiConfiguration.Status.ENABLED;
//				i.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//				i.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//				i.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//				i.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//				i.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//				i.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//				i.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//				if(wifi.updateNetwork(i) == -1){
//					int netId = wifi.addNetwork(i);
//					wifi.setWifiEnabled(true);
//					wifi.disconnect();
//					wifi.enableNetwork(netId, true);
//					wifi.reconnect();
//				}else{
//					wifi.enableNetwork(i.networkId, true);
//					Boolean res = wifi.reconnect();   
//					Log.d("wifi.reconnect()", res.toString());
//				}
//				existsRed = true;
				break;
			}           	
		}

		if(!existsRed){
			WifiConfiguration wc = new WifiConfiguration(); 
			wc.SSID = "\"" + nombreRed + "\"";
			wc.preSharedKey = "\"" + claveRed + "\"";
			wc.status = WifiConfiguration.Status.ENABLED;
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

			// connect to and enable the connection
			int netId = wifi.addNetwork(wc);
			wifi.setWifiEnabled(true);
			wifi.disconnect();
			wifi.enableNetwork(netId, true);
			
			wifi.reconnect();
		}
	}
	
	public void connectIdRed(int idRed){
		WifiManager wifi = (WifiManager) _context.getSystemService(Context.WIFI_SERVICE);
		wifi.disconnect();
		wifi.enableNetwork(idRed, true);
		wifi.reconnect();               
	}
}
