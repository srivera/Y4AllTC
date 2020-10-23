package ec.com.yacare.y4all.lib.asynctask.hotspot;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;


public class ComandoCambiarWIFIScheduledTask extends AsyncTask<String, Float, String> {
	private Y4HomeActivity activity;
	private String comando;

	public ComandoCambiarWIFIScheduledTask(Y4HomeActivity activity, String comando) {
		super();
		this.activity = activity;
		this.comando = comando;
	}
	@Override
	protected String doInBackground(String... arg0) {
		String comandoRespuesta = null;
		try {
			DatagramSocket clientSocket = new DatagramSocket();

			String datosConfS = comando + ";" ;

			byte[] datosConfB = datosConfS.getBytes();
			byte[] datosComando = new byte[512];

			System.arraycopy(datosConfB, 0, datosComando, 0, datosConfB.length);

			DatagramPacket sendPacketConf = new DatagramPacket(datosComando,
					datosComando.length,  InetAddress.getByName("192.168.43.1"),
					9989);
			clientSocket.send(sendPacketConf);

			byte[] recibido = new byte[256];
			try{
				DatagramPacket receivePacket = new DatagramPacket(recibido,
						recibido.length);
				clientSocket.setSoTimeout(5000);
				clientSocket.receive(receivePacket);

				clientSocket.close();
				comandoRespuesta = new String(recibido);
				Log.d("COMANDO HOT SPOT", comandoRespuesta);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				clientSocket.close();
				comandoRespuesta =  "ERR";
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return comandoRespuesta;
	}

//	protected void onPostExecute(String comandoRespuesta) {
//		activity.verificarResultadoHotSpot(comandoRespuesta);
//	}
}
