package ec.com.yacare.y4all.lib.asynctask.io;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ComandoIOEncenderLucesScheduledTask extends Thread {

	private String comando;
	private ArrayList<Equipo> equipos;

	public ComandoIOEncenderLucesScheduledTask(String comando, ArrayList<Equipo> equipos) {
		super();
		this.comando = comando;
		this.equipos = equipos;
	}
	
	
	public void run() {
		for(Equipo equipo : equipos) {
			try {
				AudioQueu.mSocketComando = IO.socket(YACSmartProperties.URL_SOCKET);
//			AudioQueu.mSocketComando = IO.socket(datosAplicacion.getEquipoSeleccionado().getSocketComando());
				AudioQueu.mSocketComando.emit("room", equipo.getNumeroSerie());
				AudioQueu.mSocketComando.on(Socket.EVENT_CONNECT, onConnectC);
				AudioQueu.mSocketComando.on(Socket.EVENT_DISCONNECT, onDisconnectC);
				AudioQueu.mSocketComando.on(Socket.EVENT_CONNECT_ERROR, onConnectErrorC);
				AudioQueu.mSocketComando.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorC);
				AudioQueu.mSocketComando.on("chat message1", onComando);
				AudioQueu.mSocketComando.connect();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			JSONObject obj = new JSONObject();
			try {
				obj.put("room", equipo.getNumeroSerie());
				obj.put("mensaje", comando);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			AudioQueu.mSocketComando.emit("chat message1", obj);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			AudioQueu.mSocketComando.disconnect();
			AudioQueu.mSocketComando.off(Socket.EVENT_CONNECT, onConnectC);
			AudioQueu.mSocketComando.off(Socket.EVENT_DISCONNECT, onDisconnectC);
			AudioQueu.mSocketComando.off(Socket.EVENT_CONNECT_ERROR, onConnectErrorC);
			AudioQueu.mSocketComando.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorC);
			AudioQueu.mSocketComando.off("chat message", onComando);
			AudioQueu.mSocketComando.close();
			AudioQueu.mSocketComando = null;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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