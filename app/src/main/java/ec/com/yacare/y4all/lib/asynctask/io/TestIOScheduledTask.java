package ec.com.yacare.y4all.lib.asynctask.io;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TestIOScheduledTask extends Thread {

	private Boolean isConnectedC = true;



	public Socket mSocketComando;

	private String nombreSala;

	public TestIOScheduledTask( String nombreSala) {
		super();
		this.nombreSala = nombreSala;
	}
	
	
	public void run() {

		try {
			mSocketComando = IO.socket("http://201.217.103.92:3000/");
			mSocketComando.emit("room", nombreSala);
			mSocketComando.on(Socket.EVENT_CONNECT, onConnectC);
			mSocketComando.on(Socket.EVENT_DISCONNECT, onDisconnectC);
			mSocketComando.on(Socket.EVENT_CONNECT_ERROR, onConnectErrorC);
			mSocketComando.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorC);
			mSocketComando.on("chat message1", onComando);
			mSocketComando.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}


		while (true) {

			for(int i = 0 ; i < 100; i++) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("room", nombreSala);
					obj.put("mensaje", nombreSala + ";");
					mSocketComando.emit("chat message1", obj);
					Thread.sleep(30000);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private Emitter.Listener onConnectC = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
					if (!isConnectedC) {

						isConnectedC = true;
					}

		}
	};

	private Emitter.Listener onDisconnectC = new Emitter.Listener() {
		@Override
		public void call(Object... args) {


		}
	};

	private Emitter.Listener onConnectErrorC = new Emitter.Listener() {
		@Override
		public void call(Object... args) {


		}
	};


	private Emitter.Listener onComando = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
		Log.d("onComando",((Object) args[0]).toString());

		}
	};
}