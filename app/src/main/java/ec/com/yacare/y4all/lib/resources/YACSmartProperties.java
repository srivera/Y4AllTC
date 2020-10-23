package ec.com.yacare.y4all.lib.resources;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import ec.com.yacare.y4all.lib.util.Cipher;


public class YACSmartProperties {

	public static String COM_INICIAR_COMUNICACION 		= "C01";
	public static String COM_ABRIR_PUERTA 				= "C02";
	public static String COM_REPRODUCIR_RESPUESTA 		= "C03";
	public static String COM_ENCENDER_LUZ		 		= "C04";
	public static String COM_APAGAR_LUZ 				= "C05";
	public static String COM_CERRAR_COMUNICACION 		= "C06";
	public static String COM_ALERTA_POLICIA		 		= "C07";
	public static String COM_CREAR_CUENTA				= "C08";
	public static String COM_SOLICITAR_FOTO_INICIAL	 	= "C09";
	public static String COM_SOLICITAR_VIDEO_INICIAL 	= "C10";
	public static String COM_DISPOSITIVO_CONTESTO 		= "C11";
	public static String COM_BUZON_MENSAJES 			= "C12";
	public static String COM_ELIMINAR_DISPOSITIVO		= "C13";
	public static String COM_REPRODUCIR_TEXTO			= "C14";
	public static String COM_FINALIZAR_CONFIGURACION 	= "C15";
	public static String COM_ACTUALIZAR_EQUIPO 			= "C16";
	public static String COM_ACTIVACION_SENSOR			= "C17";
	public static String COM_ACTIVACION_SENSOR_ULTIMO_AVISO	= "C18";
	public static String COM_DESACTIVACION_SENSOR		= "C19";
	public static String COM_TIMBRE_COMUNICACION_ABIERTA= "C20";
	public static String COM_CREAR_DISPOSITIVO			= "C21";

	public static String COM_ENCENDER_LUZ_WIFI 			= "C22";
	public static String COM_APAGAR_LUZ_WIFI			= "C23";
	public static String COM_ENCENDER_LUZ_BOX_WIFI		= "C24";
	public static String COM_APAGAR_LUZ_BOX_WIFI 		= "C25";
	public static String COM_LUZ_WHITE_WIFI 			= "C26";
	public static String COM_LUZ_WHITE_BOX_WIFI 		= "C27";
	public static String COM_LUZ_COLOR_WIFI 			= "C28";

	public static String COM_ELIMINAR_RESPUESTA 		= "C29";
	public static String COM_NOTIFICAR_IP_ACTUALIZADO 	= "C30";
	public static String COM_PING 						= "C31";
	public static String COM_INICIAR_HABLAR				= "C32";
	public static String COM_TERMINAR_HABLAR 			= "C33";
	public static String COM_LUZ_CALIDA_WIFI			 	= "C34";

	public static String COM_LUZ_COLOR_BOX_WIFI 		= "C35";
	public static String COM_LUZ_BRIGHTNESS_WIFI 		= "C36";
	public static String COM_LUZ_BRIGHTNESS_BOX_WIFI 	= "C37";
	public static String COM_LUZ_MODE_WIFI				= "C38";
	public static String COM_LUZ_MODE_BOX_WIFI 			= "C39";
	public static String COM_LUZ_DISCO_FASTER_WIFI 		= "C40";
	public static String COM_LUZ_DISCO_FASTER_BOX_WIFI 	= "C41";
	public static String COM_LUZ_DISCO_SLOWLER_WIFI 	= "C42";
	public static String COM_LUZ_DISCO_SLOWLER_BOX_WIFI	= "C43";
	public static String COM_LUZ_NIGHT_WIFI 			= "C44";
	public static String COM_SINCRONIZAR_TIMBRE 		= "C45";
	public static String COM_SOLICITAR_VIDEO_SENSOR 	= "C46";
	public static String COM_SOLICITAR_FOTO_TIMBRE 		= "C47";
	public static String COM_LUZ_SATURATION_WIFI 		= "C48";
	public static String COM_CONFIGURAR_PARAMETROS		= "C49";

	public static String COM_ACTIVAR_ZONA_TIMBRE		= "C50";
	public static String COM_GUARDAR_FOTO_PERFIL		= "C51";
	public static String COM_SOLICITAR_FOTO_PERFIL		= "C52";
	public static String COM_ABRIR_SESION_LUCES			= "C53";

	public static String COM_UNLINK_FOCOS				= "C54";
	public static String COM_LINK_FOCOS					= "C55";


	//TRAMAS EDIFICIO
	public static String COM_INICIAR_COMUNICACION_DEPTO	= "F01";
	public static String COM_ACTUALIZAR_DEPARTAMENTOS	= "F02";
	public static String COM_ACTIVACION_SENSOR_EDIFICIO = "F03";
	public static String COM_CONFIGURAR_PARAMETROS_EDIFICIO		= "F04";
	public static String COM_ABRIR_PUERTA_EDIFICIO		= "F05";
	public static String COM_CREAR_CLAVE_APERTURA		= "F06";
	public static String COM_BORRAR_CLAVE_APERTURA		= "F07";
	public static String COM_LISTAR_CLAVE_APERTURA		= "F08";

	public static String PORTERO_CASA		= "C";
	public static String PORTERO_EDIFICIO		= "E";


	public static String COM_CREAR_ZONA_LUCES = "C56";
	public static String COM_ELIMINAR_ZONA_LUCES = "C57";
	public static String COM_SINCRONIZAR_ZONA_LUCES = "C58";
	public static String COM_CREAR_ALARMA_FOCOS = "C59";

	public static String COM_BUSCAR_ROUTER = "C60";
	public static String COM_LINK_ROUTER = "C61";
	public static String COM_UNLINK_ROUTER = "C62";

	public static String COM_CREAR_PROGRAMACION = "C63";
	public static String COM_ELIMINAR_PROGRAMACION = "C64";
	public static String COM_ABRIR_PUERTA_UDP = "C65";

	public static String COM_HABILITAR_WIFI 	= "C66";
	public static String COM_DESHABILITAR_WIFI 	= "C67";

	public static String COM_SPEAKER_EXTERNO 	= "C68";
	public static String COM_SPEAKER_INTERNO 	= "C69";

	public static String COM_PROBAR_TIMBRE 		= "C70";
	public static String COM_SINCRONIZAR_LOG 		= "C71";
	public static String COM_BORRAR_LOG 		= "C72";
	public static String COM_TOMAR_FOTO 		= "C73";
	public static String COM_GRABAR_LOG 		= "C74";
	public static String COM_NOTIFICAR_BUZON 		= "C75";

	public static String COM_ABRIR_PUERTA_OPCIONAL = "C79";
	public static String COM_ABRIR_PUERTA_OPCIONAL_UDP = "C80";
	public static String COM_LUZ_MODE_BOX_WIFI_CONEXION 			= "";

	//Comandos hotsopot
	public static String HOTSPOT_WIFI 			= "H01";
	public static String HOTSPOT_CONEXION_OK 	= "H02";
	public static String HOTSPOT_VERIFICAR_SERIE= "H03";
	public static String HOTSPOT_MODO_HOTSPOT 	= "H04";
	public static String HOTSPOT_MODO_WIFI 		= "H05";

	//comandos dispositivos
	public static String COM_MENSAJE_CHAT 		= "D01";
	public static String COM_BUSCAR_DISPOSITIVO = "D02"; 
	public static String COM_MENSAJE_VOZ 		= "D03";

	// configuracion de audio
	public static String ADM_EQUIPO_INICIADO = "A00";// Equipo iniciado
	public static String ADM_RECIBIR_AUDIO_PREDEFINIDO = "A01";// audio predefinido
	public static String ADM_RECIBIR_AUDIO_TOTAL_PAQUETES = "A02";// audio predefinido total de paquetes
	public static String ADM_RECIBIR_ID_PUSH = "A03";

	public static String LISTAR_RESPUESTAS = "L01";// lista las respuestas predefinidas

	public static String COM_ACTUALIZAR_IP_CORP = "V04";
	public static String COM_CONFIGURAR_BUZON = "V05";


	//Comandos intercomunidor
	public static String INT_INICIAR_LLAMADA = "I01"; // iniciar llamada
	public static String INT_CONTESTO_LLAMADA = "I02"; // contesto la llamada	

	public static String CONSTANTE_RESPUESTA_COMANDO = "R";

	public static String MODO_AP 	= "AP";
	public static String MODO_WIFI 	= "WIFI";
	public static String MODO_CABLE	= "CABLE";

	public static String VOZ_MUJER1 	= "M1";
	public static String VOZ_MUJER2 	= "M2";
	public static String VOZ_HOMBRE1 	= "H1";
	
	//Constantes
	public static Integer PUERTO_COMANDO_DEFECTO = 9994;
	public static Integer PUERTO_VIDEO_DEFECTO = 9996;
	public static Integer PUERTO_AUDIO_DEFECTO = 9997;

	
	//Comando remoto
	public static Integer PUERTO_COMANDO_REMOTO_MULTICAST = 9990;
	public static Integer PUERTO_COMANDO_REMOTO_MULTICAST_INSTALACION = 9991;
	public static String GRUPO_COMANDO_REMOTO_MULTICAST = "224.0.0.251";
	public static String GRUPO_COMANDO_REMOTO_MULTICAST_INSTALACION = "224.0.0.252";
	public static String GRUPO_COMANDO_REMOTO_MULTICAST_CABLE_RED = "224.0.0.251";

	public static String IP_CORP_P = "186.71.55.178";
	public static String IP_CORP_S = "wiibell.cloudapp.net";
	//public static String ipComunicacion = IP_CORP_P;

	public static String PUERTO_JBOSS_CORP = "10083";
	public final static Integer PUERTO_UDP_CORP = 10084;
	public static String IP_HOT_SPOT = "192.168.10.1";
//	public static String IP_HOT_SPOT = "10.98.117.122";
	public static String RED_HOT_SPOT = "YacareTech";
//	public static String IP_HOT_SPOT = "10.98.117.126";

	public static String IP_ROUTER_FOCOS = "10.10.100.254";


	//WS INSTALACION
	public static String URL_OBTENER_EQUIPO_NUMSERIE = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?obtenerEquipoPorNumeroSerie";
	public static String URL_CREAR_CUENTA = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?crearCuentaCliente";
	public static String URL_CONFIRMAR_CUENTA = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?activarCuentaCliente";
	public static String URL_NOTIFICAR_INSTALACION = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?finalizarInstalacionEquipo";
	public static String URL_LOGIN_CUENTA = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?loginCuenta";
	public static String URL_OBTENER_EQUIPOS_CUENTA = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?obtenerCuenta";
	public static String URL_GUARDAR_DISPOSITIVO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?guardarDispositivo";


	//	public final static String URL_GUARDAR_CONFIGURACION_XML= "http://" + IP_CORP + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?guardarConfiguracionXML";
	public static String URL_ACTUALIZAR_EQUIPO= "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?actualizarEquipo";
	public static String URL_ENVIAR_COMANDO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioVersionJS?enviarComando";
	public static String URL_CREAR_EQUIPOS = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?crearEquipos";
	public static String URL_ACTIVAR_EQUIPO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?activarEquipo";


	//WS COMUNICACION
	public static String URL_MONITOREAR_HOLE      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?monitorearHolePunshing";
	public static String URL_CONTESTAR_PORTERO      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?contestar";
	public static String URL_MONITOREAR_PORTERO      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?monitorear";
	public static String URL_MONITOREAR_PORTERO_S      = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?monitorear";

	public static String URL_MONITOREAR_PORTERO_PUERTOS      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?monitorearPuertos";
	public static String URL_CERRAR_MONITOREAR_PORTERO      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?cerrarMonitoreo";


	//WS NOTIFICACIONES


	//WS VERSIONES
	//WS Localizacion
//	public final static String URL_LOCALIZADOR_UBICACION = "http://" + IP_CORP + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLocalizacionJS?obtenerLocalizacionActual";
//	public final static String URL_LOCALIZADOR_OBTENER_RUTA_HOY = "http://" + IP_CORP + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLocalizacionJS?obtenerRutaHoy";
	public static String URL_GUARDAR_ALERTA_UBICACION = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLocalizacionJS?guardarAlertaUbicacion";

	//WS Intercomunicadpr
	public static String URL_GUARDAR_CONTACTO       = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioIntercomunicadorJS?guardarContacto";
//	public final static String URL_OBTENER_LISTA_CONTACTO = "http://" + IP_CORP + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioIntercomunicadorJS?obtenerListaContactos";
	public static String URL_INICIAR_LLAMADA        = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioIntercomunicadorJS?iniciarLLamada";
	public static String URL_CONTESTAR_LLAMADA      = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioIntercomunicadorJS?contestarLLamada";
	
	//WS PORTERO
	public static String URL_REPRODUCIR_RESPUESTA     = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?reproducirRespuesta";
	public static String URL_OBTENER_FOTO     		= "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerFoto";
	public static String URL_ENVIAR_MENSAJE    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?enviarMensaje";
	public static String URL_OBTENER_LISTA_DISPOSITIVOS = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerListaDispositivos";
	public static String URL_ENVIAR_MENSAJE_CHAT    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?enviarMensajeChat";
	public static String URL_ENCONTRAR_DISPOSITIVO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?encontrar";
	public static String URL_ENVIAR_MENSAJE_CHAT_AUDIO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?enviarMensajeChatAudio";
	public static String URL_OBTENER_CHAT_AUDIO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerChatAudio";
	public static String URL_SOLICITAR_BUZON    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?solicitarBuzon";
	public static String URL_ETIQUETAR_FOTO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?etiquetarFoto";

	public static String URL_ENVIAR_COMANDO_LUCES   = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?ejecutarComandoLuces";
	public static String URL_OBTENER_ESTADO_LUCES   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?obtenerEstadoLuces";
	public static String URL_GUARDAR_ZONA   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?guardarZona";
	public static String URL_GUARDAR_PROGRAMACION   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?guardarProgramacion";
	public static String URL_LINK_FOCOS   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?linkFocos";
	public static String URL_ELIMINAR_PROGRAMACION   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?eliminarProgramacion";
	public static String URL_INVITAR_CUENTA   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?invitarCuenta";
	public static String URL_DESACTIVAR_DISPOSITIVO   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?desactivarDispositivo";
	public static String URL_CAMBIAR_CLAVE   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?cambiarClave";
	public static String URL_RECOMENDAR_EMAIL   = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?recomendarEmail";

	public static String URL_ACTIVAR_FINALIZAR_EQUIPO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?activarFinalizarEquipo";
	public static String URL_GUARDAR_FOTO_DISPOSITIVO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?guardarFotoDispositivo";
	public static String URL_OBTENER_FOTO_DISPOSITIVO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerFotoDispositivo";
	public static String URL_GUARDAR_FOTO_EQUIPO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?guardarFotoEquipo";
	public static String URL_OBTENER_FOTO_EQUIPO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerFotoEquipo";
	public static String URL_SOCKET    = "http://" + IP_CORP_S + ":"+ "3000";
	public static String URL_SOCKET_INSTALLER    = "http://" + IP_CORP_S + ":"+ "3002";
	public static String URL_SOCKET_FOCOS    = "http://" + IP_CORP_S + ":"+ "3003";
	//public static String URL_SOCKET_PRO    = "http://" + "wiibell.cloudapp.net" + ":"+ "3000";

	public static void actualizarURL(){
		//WS INSTALACION
		URL_OBTENER_EQUIPO_NUMSERIE = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?obtenerEquipoPorNumeroSerie";
		URL_CREAR_CUENTA = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?crearCuentaCliente";
		URL_CONFIRMAR_CUENTA = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?activarCuentaCliente";
		URL_NOTIFICAR_INSTALACION = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?finalizarInstalacionEquipo";
		URL_LOGIN_CUENTA = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?loginCuenta";
		URL_OBTENER_EQUIPOS_CUENTA = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?obtenerCuenta";
		URL_GUARDAR_DISPOSITIVO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?guardarDispositivo";


		//	public final static String URL_GUARDAR_CONFIGURACION_XML= "http://" + IP_CORP + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?guardarConfiguracionXML";
		URL_ACTUALIZAR_EQUIPO= "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?actualizarEquipo";
		URL_ENVIAR_COMANDO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioVersionJS?enviarComando";
		URL_CREAR_EQUIPOS = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?crearEquipos";
		URL_ACTIVAR_EQUIPO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?activarEquipo";


		//WS COMUNICACION
		URL_MONITOREAR_HOLE      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?monitorearHolePunshing";
		URL_CONTESTAR_PORTERO      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?contestar";
		URL_MONITOREAR_PORTERO      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?monitorear";
		URL_MONITOREAR_PORTERO_S      = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?monitorear";

		URL_MONITOREAR_PORTERO_PUERTOS      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?monitorearPuertos";
		URL_CERRAR_MONITOREAR_PORTERO      = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?cerrarMonitoreo";

		URL_GUARDAR_ALERTA_UBICACION = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLocalizacionJS?guardarAlertaUbicacion";

		//WS Intercomunicadpr
		URL_GUARDAR_CONTACTO       = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioIntercomunicadorJS?guardarContacto";
		URL_INICIAR_LLAMADA        = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioIntercomunicadorJS?iniciarLLamada";
		URL_CONTESTAR_LLAMADA      = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioIntercomunicadorJS?contestarLLamada";

		//WS PORTERO
		URL_REPRODUCIR_RESPUESTA     = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?reproducirRespuesta";
		URL_OBTENER_FOTO     		= "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerFoto";
		URL_ENVIAR_MENSAJE    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?enviarMensaje";
		URL_OBTENER_LISTA_DISPOSITIVOS = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerListaDispositivos";
		URL_ENVIAR_MENSAJE_CHAT    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?enviarMensajeChat";
		URL_ENCONTRAR_DISPOSITIVO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?encontrar";
		URL_ENVIAR_MENSAJE_CHAT_AUDIO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?enviarMensajeChatAudio";
		URL_OBTENER_CHAT_AUDIO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerChatAudio";
		URL_SOLICITAR_BUZON    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?solicitarBuzon";
		URL_ETIQUETAR_FOTO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?etiquetarFoto";

		URL_ENVIAR_COMANDO_LUCES   = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?ejecutarComandoLuces";
		URL_OBTENER_ESTADO_LUCES   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?obtenerEstadoLuces";
		URL_GUARDAR_ZONA   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?guardarZona";
		URL_GUARDAR_PROGRAMACION   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?guardarProgramacion";
		URL_LINK_FOCOS   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?linkFocos";
		URL_ELIMINAR_PROGRAMACION   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioLucesJS?eliminarProgramacion";
		URL_INVITAR_CUENTA   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?invitarCuenta";
		URL_DESACTIVAR_DISPOSITIVO   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?desactivarDispositivo";
		URL_CAMBIAR_CLAVE   = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?cambiarClave";
		URL_RECOMENDAR_EMAIL   = "http://" + IP_CORP_P + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?recomendarEmail";

		URL_ACTIVAR_FINALIZAR_EQUIPO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?activarFinalizarEquipo";
		URL_GUARDAR_FOTO_DISPOSITIVO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?guardarFotoDispositivo";
		URL_OBTENER_FOTO_DISPOSITIVO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerFotoDispositivo";
		URL_GUARDAR_FOTO_EQUIPO = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioInstalacionPorteroJS?guardarFotoEquipo";
		URL_OBTENER_FOTO_EQUIPO    = "http://" + IP_CORP_S + ":"+ PUERTO_JBOSS_CORP +"/yconference-web-1.0.0/json/servicioPorteroJS?obtenerFotoEquipo";
		URL_SOCKET    = "http://" + IP_CORP_S + ":"+ "3000";
		URL_SOCKET_FOCOS    = "http://" + IP_CORP_S + ":"+ "3003";

	}


	private Hashtable properties;
	
	public static final String PROPERTIES_BUNDLE="yac_smart.properties";
	
	public static final YACSmartProperties intance=new YACSmartProperties();
	
	private YACSmartProperties(){
		 InputStream is = this.getClass().getResourceAsStream(PROPERTIES_BUNDLE);
		 properties=new Hashtable();
		 
		 InputStreamReader isr=null;
		try {
			isr = new InputStreamReader(is,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		 LineReader lineReader=new LineReader(isr);
		 try {
			while (lineReader.hasLine()) {
				String line=lineReader.readLine();
				if(line.length()>1 && line.substring(0,1).equals("#")) continue;
				if(line.indexOf("=")!=-1){
					String key=line.substring(0,line.indexOf("="));
					String value=line.substring(line.indexOf("=")+1,line.length());
					properties.put(key, value);
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
			 
	}
	
	public static YACSmartProperties getInstance(){
		return intance;
	}

	public Hashtable getProperties() {
		return properties;
	}

	public void setProperties(Hashtable properties) {
		this.properties = properties;
	}
	
	/**
	 * @param key
	 * @param args Values to replace placeholders inside the message, it must be an String array since
	 * 			   Java ME doesn't support var args
	 * @return
	 */
	public String getMessageForKey(String key, String[] args)
	{
		if(args==null) return (String) getProperties().get(key);
		else return format(getProperties().get(key).toString(),args);
	}
	
	public String getMessageForKey(String key)
	{
		return (String) getProperties().get(key);
	}
	
	
	private String format(String format, String[] args) {
	    int argIndex = 0;
	    int startOffset = 0;
	    int placeholderValue=0;
	    int placeholderOffset = format.indexOf("{"+placeholderValue+"}");

	    if (placeholderOffset == -1) {
	        return format;
	    }
	    int capacity = format.length();

	    if (args != null) {
	        for (int i=0;i<args.length;i++) {
	            capacity+=args[i].length();
	        }
	    }

	    StringBuffer sb = new StringBuffer(capacity);

	    while (placeholderOffset != -1) {
	    	placeholderValue++;
	        sb.append(format.substring(startOffset,placeholderOffset));

	        if (args!=null && argIndex<args.length) {
	            sb.append(args[argIndex]);
	        }

	        argIndex++;
	        startOffset=placeholderOffset+3;
	        placeholderOffset = format.indexOf("{"+placeholderValue+"}");
	    }

	    if (startOffset<format.length()) {
	        sb.append(format.substring(startOffset));
	    }

	    return sb.toString();
	  }

	
	
	public String replacePlaceHolderForResource(String resource, String args[]) {
		if (args == null)
			return resource;
		return format(resource, args);
	}

	public static String Desencriptar(byte [] texto, String numeroSerie) {
		Cipher encriptar = new Cipher(numeroSerie);
		try {
			byte[] result1 = encriptar.decrypt(texto);
			return new String(result1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	public static String Encriptar(String texto, String numeroSerie) {
		Cipher encriptar = new Cipher(numeroSerie);
		try {
			byte[] result1 = encriptar.encrypt(texto.getBytes("utf-8"));
			return new String(result1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";


//		String secretKey = "Y4hOmEyAcArETeChNoLoGy"; //llave para encriptar datos
//		String base64EncryptedString = "";
//
//		try {
//
//			MessageDigest md = MessageDigest.getInstance("MD5");
//			byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
//			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
//
//			SecretKey key = new SecretKeySpec(keyBytes, "DESede");
//			Cipher cipher = Cipher.getInstance("DESede");
//			cipher.init(Cipher.ENCRYPT_MODE, key);
//
//			byte[] plainTextBytes = texto.getBytes("utf-8");
//			byte[] buf = cipher.doFinal(plainTextBytes);
//			byte[] base64Bytes = Base64.encode(buf, 0);
//			base64EncryptedString = new String(base64Bytes);
//
//		} catch (Exception ex) {
//		}
//		return base64EncryptedString;
	}


	public static Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

		int width = Math.round(sentBitmap.getWidth() * scale);
		int height = Math.round(sentBitmap.getHeight() * scale);
		sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) { return (null); } int w = bitmap.getWidth(); int h = bitmap.getHeight(); int[] pix = new int[w * h]; Log.e("pix", w + " " + h + " " + pix.length); bitmap.getPixels(pix, 0, w, 0, 0, w, h); int wm = w - 1; int hm = h - 1; int wh = w * h; int div = radius + radius + 1; int r[] = new int[wh]; int g[] = new int[wh]; int b[] = new int[wh]; int rsum, gsum, bsum, x, y, i, p, yp, yi, yw; int vmin[] = new int[Math.max(w, h)]; int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) { p = pix[yi + Math.min(wm, Math.max(i, 0))]; sir = stack[i + radius]; sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) { r[yi] = dv[rsum]; g[yi] = dv[gsum]; b[yi] = dv[bsum]; rsum -= routsum; gsum -= goutsum; bsum -= boutsum; stackstart = stackpointer - radius + div; sir = stack[stackstart % div]; routsum -= sir[0]; goutsum -= sir[1]; boutsum -= sir[2]; if (y == 0) { vmin[x] = Math.min(x + radius + 1, wm); } p = pix[yw + vmin[x]]; sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) { yi = Math.max(0, yp) + x; sir = stack[i + radius]; sir[0] = r[yi]; sir[1] = g[yi]; sir[2] = b[yi]; rbs = r1 - Math.abs(i); rsum += r[yi] * rbs; gsum += g[yi] * rbs; bsum += b[yi] * rbs; if (i > 0) {
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
			} else {
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
			}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		Log.e("pix", w + " " + h + " " + pix.length);
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}
}
