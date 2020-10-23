package ec.com.yacare.y4all.lib.util;

import android.media.AudioRecord;

import org.json.JSONObject;

import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import io.socket.client.Socket;

public class AudioQueu {

	public  static AudioRecord audio_recorder = null;

	public static Socket mSocket;
	public static Socket mSocketVideo;

	public static Integer contadorComandoEnviado = 0;

	public static Socket mSocketComando;

	public static DatagramSocket clientSocket;

	public static DatagramSocket socketFocos;
	public static byte[] sesionFocos;

	public static Integer paqRecibido = 0;
	public static Integer paqRecibidoVideo = 0;

	public static HashMap<String, String> imacRouter = new HashMap<String, String>();

	public static String version = "1";
	private static String tipoConexion = "INTERNET";

	public static Boolean socketComando = false;

	public static Boolean llamadaEntrante = false;

	public static Boolean speakerExterno = true;

	public static Boolean encenderLuz = true;

	public static Boolean buscandoFoto = false;

	public static byte[] fotoTimbre = null;
	
	public static Boolean esComunicacionDirecta = false;
	
	public static Boolean imagenRecibida = false;

	public static Boolean guardarFoto = false;

	public static Boolean isInBackground = false;

	public static Boolean hablar = false;

	public static Boolean buscarCelular = false;


	public static Boolean monitorearPortero = false;

	public static Boolean modoCamaraLuces = false;

	public static String ipComunicacionHole;

	public static int modoAudioAnterior;

	public static String tipoComunicacion = "HOLE";

	//INTERCOM
	private static Integer puertoComunicacionIntercomIndirecto;
	private static Integer puertoComunicacionIntercomIndirectoVideo;
	private static Integer puertoComunicacionIntercomIndirectoComando;

	public static String rutaAudio;
	public static String rutaVideo;
	
	private static DatagramSocket dataSocketIntercomVideo = null;
	private static HashMap<Integer, byte[]> videoIntercom = new HashMap<Integer, byte[]>();

	private static int requestedOrientation;
	
	//Eliminacion de paquetes de mas

	//Paquetes de audio totales que se han recibidp
	public static Integer paqueteAudioRecibido = 0;

	//Paquetes de audio totales que se han enviado
	public static Integer paqueteAudioEnviado = 0;
	

	//Indica si esta abierta la comunicacion
	public static Boolean comunicacionAbierta = false;
	
	//Indica si la aplicacion esta en segundo plano
	private static Boolean segundoPlano = false;
	
	private static ConcurrentHashMap<Integer, byte[]> audioRecibido = new ConcurrentHashMap<Integer, byte[]>();
	private static ConcurrentHashMap<Integer,JSONObject> comandoRecibido = new ConcurrentHashMap<Integer, JSONObject>();
	private static ConcurrentHashMap<Integer, String> comandoEnviado = new ConcurrentHashMap<Integer,String>();
	private static ConcurrentHashMap<Integer, byte[]> videoRecibido = new ConcurrentHashMap<Integer, byte[]>();

	private static Integer contadorReproducir;
	public static Boolean falloHotSpot = true;

	public static Boolean verificarHotSpot = false;


	public static void setSegundoPlano(Boolean segundoPlano) {
		AudioQueu.segundoPlano = segundoPlano;
	}

	public static Boolean getComunicacionAbierta() {
		return comunicacionAbierta;
	}

	public static void setComunicacionAbierta(Boolean comunicacionAbierta) {
		AudioQueu.comunicacionAbierta = comunicacionAbierta;
	}

	public static DatagramSocket getClientSocket() {
		return clientSocket;
	}

	public static void setClientSocket(DatagramSocket clientSocket) {
		AudioQueu.clientSocket = clientSocket;
	}


	public static short bytesToShort(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
	}

	public static Integer getPaqueteAudioRecibido() {
		return paqueteAudioRecibido;
	}

	public static void setPaqueteAudioRecibido(Integer paqueteAudioRecibido) {
		AudioQueu.paqueteAudioRecibido = paqueteAudioRecibido;
	}

	public static Integer getPaqueteAudioEnviado() {
		return paqueteAudioEnviado;
	}

	public static void setPaqueteAudioEnviado(Integer paqueteAudioEnviado) {
		AudioQueu.paqueteAudioEnviado = paqueteAudioEnviado;
	}


	public static String getTipoConexion() {
		return tipoConexion;
	}

	public static void setTipoConexion(String tipoConexion) {
		AudioQueu.tipoConexion = tipoConexion;
	}

	public static Integer getPuertoComunicacionIntercomIndirecto() {
		return puertoComunicacionIntercomIndirecto;
	}

	public static void setPuertoComunicacionIntercomIndirecto(
			Integer puertoComunicacionIntercomIndirecto) {
		AudioQueu.puertoComunicacionIntercomIndirecto = puertoComunicacionIntercomIndirecto;
	}

	public static Integer getPuertoComunicacionIntercomIndirectoVideo() {
		return puertoComunicacionIntercomIndirectoVideo;
	}

	public static void setPuertoComunicacionIntercomIndirectoVideo(
			Integer puertoComunicacionIntercomIndirectoVideo) {
		AudioQueu.puertoComunicacionIntercomIndirectoVideo = puertoComunicacionIntercomIndirectoVideo;
	}

	public static DatagramSocket getDataSocketIntercomVideo() {
		return dataSocketIntercomVideo;
	}

	public static void setDataSocketIntercomVideo(
			DatagramSocket dataSocketIntercomVideo) {
		AudioQueu.dataSocketIntercomVideo = dataSocketIntercomVideo;
	}

	public static HashMap<Integer, byte[]> getVideoIntercom() {
		return videoIntercom;
	}

	public static void setVideoIntercom(HashMap<Integer, byte[]> videoIntercom) {
		AudioQueu.videoIntercom = videoIntercom;
	}

	public static int getRequestedOrientation() {
		return requestedOrientation;
	}

	public static void setRequestedOrientation(int requestedOrientation) {
		AudioQueu.requestedOrientation = requestedOrientation;
	}

	public static Integer getPuertoComunicacionIntercomIndirectoComando() {
		return puertoComunicacionIntercomIndirectoComando;
	}

	public static void setPuertoComunicacionIntercomIndirectoComando(
			Integer puertoComunicacionIntercomIndirectoComando) {
		AudioQueu.puertoComunicacionIntercomIndirectoComando = puertoComunicacionIntercomIndirectoComando;
	}

	public static ConcurrentHashMap<Integer, byte[]> getAudioRecibido() {
		return audioRecibido;
	}

	public static void setAudioRecibido(ConcurrentHashMap<Integer, byte[]> audioRecibido) {
		AudioQueu.audioRecibido = audioRecibido;
	}

	public static Integer getContadorReproducir() {
		return contadorReproducir;
	}

	public static void setContadorReproducir(Integer contadorReproducir) {
		AudioQueu.contadorReproducir = contadorReproducir;
	}


	public static ConcurrentHashMap<Integer,JSONObject> getComandoRecibido() {
		return comandoRecibido;
	}

	public static void setComandoRecibido(ConcurrentHashMap<Integer, JSONObject> comandoRecibido) {
		AudioQueu.comandoRecibido = comandoRecibido;
	}

	public static ConcurrentHashMap<Integer, String> getComandoEnviado() {
		return comandoEnviado;
	}

	public static void setComandoEnviado(ConcurrentHashMap<Integer, String> comandoEnviado) {
		AudioQueu.comandoEnviado = comandoEnviado;
	}

	public static ConcurrentHashMap<Integer, byte[]> getVideoRecibido() {
		return videoRecibido;
	}

	public static void setVideoRecibido(ConcurrentHashMap<Integer, byte[]> videoRecibido) {
		AudioQueu.videoRecibido = videoRecibido;
	}
}
