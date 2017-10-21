package ec.com.yacare.y4all.lib.asynctask.io;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ComandoIOFocoScheduledTask extends Thread {

	private String comando;
	public Socket mSocketFoco;

	public ComandoIOFocoScheduledTask(String comando) {
		super();
		this.comando = comando;
	}
	
	
	public void run() {

		try {
			mSocketFoco = IO.socket(YACSmartProperties.URL_SOCKET_FOCOS);
			mSocketFoco.emit("room", "WiiLight");
			mSocketFoco.on(Socket.EVENT_CONNECT, onConnectC);
			mSocketFoco.on(Socket.EVENT_DISCONNECT, onDisconnectC);
			mSocketFoco.on(Socket.EVENT_CONNECT_ERROR, onConnectErrorC);
			mSocketFoco.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorC);
			mSocketFoco.on("chat message1", onComando);
			mSocketFoco.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		JSONObject obj = new JSONObject();
		try {
			obj.put("room", "WiiLight");
			obj.put("mensaje", comando);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		mSocketFoco.emit("chat message1", obj);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mSocketFoco.disconnect();
		mSocketFoco.off(Socket.EVENT_CONNECT, onConnectC);
		mSocketFoco.off(Socket.EVENT_DISCONNECT, onDisconnectC);
		mSocketFoco.off(Socket.EVENT_CONNECT_ERROR, onConnectErrorC);
		mSocketFoco.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorC);
		mSocketFoco.off("chat message", onComando);
		mSocketFoco.close();
		mSocketFoco = null;
	}

	private Emitter.Listener onConnectC = new Emitter.Listener() {
		@Override
		public void call(Object... args) {

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

		}
	};
}