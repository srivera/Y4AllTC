package ec.com.yacare.y4all.lib.asynctask.hotspot;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;

import ec.com.yacare.y4all.activities.instalacion.AgregarEquipoFragment;

public class ConfigurarFocosAsyncTask extends AsyncTask<String, Float, String> {


	private AgregarEquipoFragment activity;
	private String nombreWifi;
	private String claveWifi;
	String routerEncontrado;

	public static String IP_ROUTER_FOCOS = "255.255.255.255";

	public ConfigurarFocosAsyncTask(AgregarEquipoFragment activity, String nombreWifi, String claveWifi) {
		super();
		this.activity = activity;
		this.nombreWifi = nombreWifi;
		this.claveWifi = claveWifi;
	}


	@Override
	protected String doInBackground(String... arg0) {
		routerEncontrado = "";

		byte[] datosRecibir2 = new byte[1024];
		DatagramPacket receivePacket2 = new DatagramPacket(datosRecibir2,
				datosRecibir2.length);

		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
			datagramSocket.setBroadcast(true);
			DatagramPacket packet2 = new DatagramPacket(("HF-A11ASSISTHREAD").getBytes(), ("HF-A11ASSISTHREAD").getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
			for (int i = 0; i < 20; i++) {
				datagramSocket.send(packet2);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Date hora = new Date();
			while (((new Date()).getTime() - hora.getTime()) < 1000) {
				try {
					datagramSocket.setSoTimeout(10);
					datagramSocket.receive(receivePacket2);
					routerEncontrado = new String(receivePacket2.getData());
					System.out.println(routerEncontrado);
				} catch (SocketTimeoutException e) {
					continue;
				}
			}

			Log.d("routerEncontrado1", routerEncontrado);
			if (!routerEncontrado.equals("")) {
				//Start admin
				DatagramPacket packet4 = new DatagramPacket("+ok".getBytes(), "+ok".getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
				datagramSocket.send(packet4);

				//Set wifi
				String comandoWifi = "AT+WSSSID=" + nombreWifi + "\r";
				DatagramPacket packet3 = new DatagramPacket(comandoWifi.getBytes(), comandoWifi.getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);

				byte[] datosRecibir3 = new byte[1024];

				DatagramPacket receivePacket3 = new DatagramPacket(datosRecibir3,
						datosRecibir3.length);

				datagramSocket.send(packet3);
				datagramSocket.setSoTimeout(30000);
				datagramSocket.receive(receivePacket3);

				System.out.println(new String(receivePacket3.getData()));

				//Set clave wifi
				comandoWifi = "AT+WSKEY=WPA2PSK,AES," + claveWifi + "\r";
				DatagramPacket packet5 = new DatagramPacket(comandoWifi.getBytes(), comandoWifi.getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
				byte[] datosRecibir5 = new byte[1024];
				DatagramPacket receivePacket5 = new DatagramPacket(datosRecibir5,
						datosRecibir5.length);
				datagramSocket.send(packet5);
				datagramSocket.setSoTimeout(30000);
				datagramSocket.receive(receivePacket5);
				System.out.println(new String(receivePacket5.getData()));

				//Cambiar modo a APSTA
				DatagramPacket packet7 = new DatagramPacket("AT+WMODE=APSTA\r".getBytes(), "AT+WMODE=APSTA\r".getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
				datagramSocket.send(packet7);

				//Reiniciar
				DatagramPacket packet8 = new DatagramPacket("AT+Z\r".getBytes(), "AT+Z\r".getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
				datagramSocket.send(packet8);

				//Cerrar session
				DatagramPacket packet6 = new DatagramPacket("AT+Q\r".getBytes(), "AT+Q\r".getBytes().length, InetAddress.getByName(IP_ROUTER_FOCOS), 48899);
				datagramSocket.send(packet6);

				datagramSocket.close();

				Log.d("routerEncontrado2", routerEncontrado);


				if (routerEncontrado.equals("")) {
					return "ERR,Verifique que los datos de la red wifi son correctos. Vuelva a conectarse a la red de su equipo. Puede apagar y encender su WifiBox.";
				} else {
					return "OK," + routerEncontrado;
				}
			} else {
				return "ERR,Verifique que su celular este conectado a la red de su equipo. Apague y encienda nuevamente su wifiBox";
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return "ERR,1. Verifique que su celular este conectado a la red de su equipo o que su equipo este encendido";
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "ERR,2. Verifique que su celular este conectado a la red de su equipo o que su equipo este encendido";
		} catch (IOException e) {
			e.printStackTrace();
			return "ERR,3. Verifique que su celular este conectado a la red de su equipo o que su equipo este encendido";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERR,4. Verifique que su celular este conectado a la red de su equipo o que su equipo este encendido";
		}

	}


	protected void onPostExecute(String resultado) {
		activity.verificarResultadoConfigurarFocos(resultado, routerEncontrado);
	}
}