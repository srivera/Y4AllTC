package ec.com.yacare.y4all.lib.asynctask.hotspot;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.com.yacare.y4all.activities.instalacion.AgregarEquipoFragment;

public class ConfigurarWifiFocosAsyncTask extends AsyncTask<String, Float, String> {


	private AgregarEquipoFragment activity;
	private String nombreWifi;
	private String claveWifi;
	private String routerEncontrado;

//    public static String IP_ROUTER_FOCOS = "10.10.100.254";

	public static String IP_ROUTER_FOCOS = "255.255.255.255";

	public ConfigurarWifiFocosAsyncTask(AgregarEquipoFragment activity, String nombreWifi, String claveWifi, String routerEncontrado) {
		super();
		this.activity = activity;
		this.nombreWifi = nombreWifi;
		this.claveWifi = claveWifi;
		this.routerEncontrado = routerEncontrado;
	}


	@Override
	protected String doInBackground(String... arg0) {
		String routerEncontradoWifi = "";
		String[] datosRouter = routerEncontrado.split(",");
		byte[] datosRecibir2 = new byte[1024];

		DatagramSocket datagramSocket = null;

		DatagramPacket packet2;

		WifiManager wifiManager = (WifiManager) activity.getActivity().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		Log.d("wifiInfo.getSSID()", wifiInfo.getSSID() + "/" + nombreWifi.trim());
		/*if (wifiInfo.getSSID().contains(nombreWifi.trim())) {

		} else {
			wifiManager.disconnect();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			wifiManager.setWifiEnabled(true);


			Log.d("routerEncontrado3", routerEncontradoWifi);
			WifiConfiguration wifiConfig = new WifiConfiguration();
			wifiConfig.SSID = String.format("\"%s\"", nombreWifi);
			wifiConfig.preSharedKey = String.format("\"%s\"", claveWifi);
			Log.d("WIFI ", "Reconectado");
			int netId = wifiManager.addNetwork(wifiConfig);
			wifiManager.disconnect();
			wifiManager.enableNetwork(netId, true);
			wifiManager.reconnect();

			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		if(wifiManager.isWifiEnabled() && wifiInfo.getSSID().contains(nombreWifi.trim())) {
			try {
				datagramSocket = new DatagramSocket();
				datagramSocket.setBroadcast(true);
				packet2 = new DatagramPacket(("HF-A11ASSISTHREAD").getBytes(), ("HF-A11ASSISTHREAD").getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
				for (int i = 0; i < 20; i++) {
					try {

						datagramSocket.send(packet2);
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (SocketException e) {
						e.printStackTrace();
						continue;
					}
				}
				routerEncontrado = "";
				Date hora = new Date();
				Log.d("routerEncontradoWifi", routerEncontradoWifi);
				while (((new Date()).getTime() - hora.getTime()) < 1000) {
					try {
						datosRecibir2 = new byte[1024];
						DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
								datosRecibir2.length);
						datagramSocket.setSoTimeout(200);
						datagramSocket.receive(receivePacket2);
						routerEncontradoWifi = new String(receivePacket2.getData()).trim();
						if (!routerEncontradoWifi.contains(datosRouter[1])) {
							routerEncontradoWifi = "";
						}
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
						continue;
					}
				}
				Log.d("routerEncontradoWifi 2", routerEncontradoWifi);
				if (!routerEncontradoWifi.equals("")) {
					configurarIpStatica(routerEncontradoWifi, datagramSocket);
				}

			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (routerEncontradoWifi.equals("")) {
				return "CON,Debe conectarse a la wifi " + nombreWifi  + " Verifique que los datos de su wifi sean correctos. Intente apagar y encender su wifiBox.";
			} else {
				return "OK," + routerEncontradoWifi;
			}
		}else{
			return "CON,Debe conectarse a la wifi 1 " + nombreWifi + " Verifique que los datos de su wifi sean correctos. Intente apagar y encender su wifiBox.";
		}
	}

	private void configurarIpStatica(String routerEncontrado, DatagramSocket datagramSocket) throws IOException {
		try {

			if(!routerEncontrado.equals("")){
				String datosred[] = routerEncontrado.split(",");

				DatagramPacket packet4 = new DatagramPacket("+ok".getBytes(), "+ok".getBytes().length, InetAddress.getByName(datosred[0]), 48899);
				datagramSocket.send(packet4);

				String tipoConexion = "AT+WANN\r";
				DatagramPacket packet10 = new DatagramPacket(tipoConexion.getBytes(), tipoConexion.getBytes().length, InetAddress.getByName(datosred[0]), 48899);
				byte[] datosRecibir6 = new byte[1024];
				DatagramPacket receivePacket6 = new DatagramPacket(datosRecibir6,
						datosRecibir6.length, InetAddress.getByName(obtenerIP()), datagramSocket.getLocalPort());
				datagramSocket.send(packet10);
				datagramSocket.setSoTimeout(1000);
				datagramSocket.receive(receivePacket6);
				String respuesta =  new String(receivePacket6.getData()).replace("+ok=", "");
				String datos[] = respuesta.split(",");
//				System.out.println("resp1 "+ datos[0] +  " " + datos[1]);
				respuesta = respuesta.toUpperCase().replace("DHCP", "STATIC");

				//cambiar a static
				String tipoConexionEstatica = "AT+WANN=" + respuesta + "\r";
				DatagramPacket packet11 = new DatagramPacket(tipoConexionEstatica.getBytes(), tipoConexionEstatica.getBytes().length, InetAddress.getByName(datosred[0]), 48899);
				byte[] datosRecibir8 = new byte[1024];
				DatagramPacket receivePacket8 = new DatagramPacket(datosRecibir8,
						datosRecibir8.length, InetAddress.getByName(obtenerIP()), datagramSocket.getLocalPort());
				datagramSocket.send(packet11);
				datagramSocket.setSoTimeout(1000);
				datagramSocket.receive(receivePacket8);
				String resultado = new String(receivePacket8.getData());
				if(!resultado.endsWith("+ok")){
					//Actualizar estado a ip estatica
				}
				System.out.println("resp2 " + new String(receivePacket8.getData()));

				//Cambiar modo a STA
				DatagramPacket packet7 = new DatagramPacket("AT+WMODE=STA\r".getBytes(), "AT+WMODE=STA\r".getBytes().length, InetAddress.getByName(datosred[0]), 48899);
				datagramSocket.send(packet7);

				//Reiniciar
				DatagramPacket packet8 = new DatagramPacket("AT+Z\r".getBytes(), "AT+Z\r".getBytes().length, InetAddress.getByName(datosred[0]), 48899);
				datagramSocket.send(packet8);

				//Cerrar session
				DatagramPacket packet6 = new DatagramPacket("AT+Q\r".getBytes(), "AT+Q\r".getBytes().length, InetAddress.getByName(datosred[0]), 48899);
				datagramSocket.send(packet6);

				datagramSocket.close();

			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void onPostExecute(String resultado) {
		Log.d("verificarResultadoWifiFocos", resultado);
		activity.verificarResultadoWifiFocos(resultado);
	}
	private static final String IPADDRESS_PATTERN =
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public String obtenerIP() {
		String ip = "";
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();

			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					Matcher matcher = pattern.matcher(i.toString().replace("/", ""));
					if ( pattern.matcher(i.getHostAddress()).matches() && !i.getHostAddress().substring(0, 3).equals("127") && !i.getHostAddress().equals("192.168.43.1") && !i.getHostAddress().equals("::1%1")) {
						ip = i.getHostAddress();
					}
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		return ip;
	}

}