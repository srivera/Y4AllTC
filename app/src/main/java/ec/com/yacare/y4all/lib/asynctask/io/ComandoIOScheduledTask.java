package ec.com.yacare.y4all.lib.asynctask.io;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.portero.LlamadaEntrantePorteroActivity;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.enumer.EstadoEventoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEventoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.sqllite.ZonaDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static ec.com.yacare.y4all.lib.util.AudioQueu.esComunicacionDirecta;


public class ComandoIOScheduledTask extends Thread {

	private Integer contador;
	private Integer contadorEnviados;

	private Y4HomeActivity y4HomeActivity;
	private Boolean isConnectedC = true;

	private String recibido[];
	private Integer contadorRecibido;

	//private long numeroVisitas;

	private String numeroSerie;
	private DatosAplicacion datosAplicacion;

	public ComandoIOScheduledTask(Y4HomeActivity y4HomeActivity, DatosAplicacion datosAplicacion, String numeroSerie) {
		super();
		this.y4HomeActivity = y4HomeActivity;
		this.datosAplicacion = datosAplicacion;
		this.numeroSerie = numeroSerie;
	}
	
	
	public void run() {
		contador = 0;
		contadorEnviados =0;
		contadorRecibido = 0;

		try {
			AudioQueu.mSocketComando = IO.socket(YACSmartProperties.URL_SOCKET);
//			AudioQueu.mSocketComando = IO.socket(datosAplicacion.getEquipoSeleccionado().getSocketComando());
			AudioQueu.mSocketComando.emit("room", datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
			AudioQueu.mSocketComando.on(Socket.EVENT_CONNECT, onConnectC);
			AudioQueu.mSocketComando.on(Socket.EVENT_DISCONNECT, onDisconnectC);
			AudioQueu.mSocketComando.on(Socket.EVENT_CONNECT_ERROR, onConnectErrorC);
			AudioQueu.mSocketComando.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorC);
			AudioQueu.mSocketComando.on("chat message1", onComando);
			AudioQueu.mSocketComando.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(datosAplicacion);
		String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
		FileOutputStream fos;
		BufferedOutputStream outputStream;
		Evento eventoBusqueda;
		EventoDataSource eventoDataSource = new EventoDataSource(datosAplicacion);
		Equipo equipoBusqueda;
//		AlertDialog.Builder alertDialogBuilder;
		FileOutputStream fileOuputStream;
		EquipoDataSource equipoDataSource = new EquipoDataSource(datosAplicacion);
		JSONObject rec;
		JSONObject eventoJson;
		File file;
		Intent intent;

		while (AudioQueu.socketComando
				&& datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(numeroSerie)
				&& !AudioQueu.isInBackground) {

			if(AudioQueu.getComandoRecibido().containsKey(contador)  ){
				try {
					recibido  = ((AudioQueu.getComandoRecibido().get(contador)).getString("mensaje")).split(";");

//					Log.d("ComandoRecibido", Arrays.toString(recibido));
					Log.d("ComandoRecibido", recibido[0]);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if( recibido[0].startsWith(YACSmartProperties.COM_ENCENDER_LUZ + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)) {
					Log.d("Luz encendida", "Luz");
					if (y4HomeActivity != null) {
						y4HomeActivity.btnHoy.post(new Runnable() {
							@Override
							public void run() {
								y4HomeActivity.verificarResultadoComando(recibido[0], true);
							}
						});
					}
				}else if( recibido[0].startsWith(YACSmartProperties.COM_APAGAR_LUZ + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					Log.d("Luz encendida", "Luz");
					if(y4HomeActivity != null){
						y4HomeActivity.btnHoy.post(new Runnable() {
							@Override
							public void run() {
								y4HomeActivity.verificarResultadoComando(recibido[0], true);
							}
						});
					}

				}else if( recibido[0].startsWith(YACSmartProperties.COM_BUZON_MENSAJES + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) ||
						recibido[0].startsWith(YACSmartProperties.COM_SOLICITAR_VIDEO_INICIAL + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) ||
						recibido[0].startsWith(YACSmartProperties.COM_SOLICITAR_VIDEO_SENSOR + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					Log.d("Buzon", "Buzon Mensajes");

					if(!recibido[5].equals("ERR")) {
						rec = AudioQueu.getComandoRecibido().get(contador);
						if(rec.has("valor")){
							try {

								try {
									fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + recibido[5]);
									outputStream = new BufferedOutputStream(fos);
									outputStream.write((byte[])rec.get("valor"));
									outputStream.close();
									if(datosAplicacion.getEventosActivity() != null){
										datosAplicacion.getEventosActivity().actualizarLista();
									}else if(datosAplicacion.getY4HomeActivity() != null){
										datosAplicacion.getY4HomeActivity().actualizarLista();
									}
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}else{
						//No hay archivo
						eventoBusqueda = new Evento();
						eventoBusqueda.setId(recibido[4]);

						eventoDataSource.open();
						eventoBusqueda = eventoDataSource.getEventoId(eventoBusqueda);
						if(eventoBusqueda != null) {
							eventoBusqueda.setEstado("ERR");
							eventoDataSource.updateEvento(eventoBusqueda);
						}
						eventoDataSource.close();
					}

				}else if( recibido[0].startsWith(YACSmartProperties.COM_CONFIGURAR_PARAMETROS + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){

					if(nombreDispositivo.equals(recibido[1])) {
						equipoDataSource = new EquipoDataSource(datosAplicacion);
						equipoDataSource.open();
						equipoBusqueda = new Equipo();
						equipoBusqueda.setNumeroSerie(recibido[3]);
						equipoBusqueda = equipoDataSource.getEquipoNumSerie(equipoBusqueda);
						if(equipoBusqueda != null && equipoBusqueda.getId() != null) {

							//equipoBusqueda.setLuz(recibido[7]);
							//equipoBusqueda.setLuzWifi(recibido[8]);
							equipoBusqueda.setPuerta(recibido[4]);
							equipoBusqueda.setSensor(recibido[6]);
							equipoBusqueda.setMensajeInicial(recibido[7].trim());
							equipoBusqueda.setPuertoActivo(recibido[9]);
							equipoBusqueda.setTimbreExterno(recibido[13]);
						//	if(recibido.length > 15) {
								equipoBusqueda.setMensajeApertura(recibido[14]);
						//	}
						//	if(recibido.length > 16) {
								equipoBusqueda.setMensajePuerta(recibido[15]);
						//	}
							equipoBusqueda.setVolumen(Integer.valueOf(recibido[18]));
						//	equipoBusqueda.setTiempoEncendidoLuz(Integer.valueOf(recibido[19]));
						//	equipoBusqueda.setTimbreExterno(recibido[15]);
							equipoDataSource.updateEquipo(equipoBusqueda);
							if(datosAplicacion.getEquipoSeleccionado() != null && datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(equipoBusqueda.getNumeroSerie())){
								datosAplicacion.setEquipoSeleccionado(equipoBusqueda);
							}
						}
						equipoDataSource.close();
						y4HomeActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										y4HomeActivity);
								alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
										.setMessage("Su configuración fue actualizada")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {

											}
										});
								AlertDialog alertDialog = alertDialogBuilder.create();
								alertDialog.show();
							}
						});
					}

				}else if( recibido[0].startsWith(YACSmartProperties.COM_CONFIGURAR_PARAMETROS_EDIFICIO + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){

					if(nombreDispositivo.equals(recibido[1])) {

						y4HomeActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										y4HomeActivity);
								alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
										.setMessage("Su configuración fue actualizada")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {

											}
										});
								AlertDialog alertDialog = alertDialogBuilder.create();
								alertDialog.show();
							}
						});
					}
				}else if(recibido[0].equals(YACSmartProperties.COM_ACTIVAR_ZONA_TIMBRE + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){

					ZonaDataSource zonaDataSource = new ZonaDataSource(datosAplicacion);
					zonaDataSource.open();
					ZonaLuces zonaLuces = zonaDataSource.getZonaRouterId(recibido[4], recibido[5]);
					zonaLuces.setEncenderTimbre(recibido[6]);
					zonaDataSource.updateZona(zonaLuces);
					zonaDataSource.close();


				} else if (recibido[0].startsWith(YACSmartProperties.COM_SINCRONIZAR_TIMBRE + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && nombreDispositivo.equals(recibido[1])){
					Log.d("Timbre sincronizado", "sincronizado");
					try {
						eventoJson = new JSONObject(recibido[4]);
						eventoBusqueda = new Evento();
						eventoBusqueda.setId(eventoJson.getString("id"));
						eventoDataSource = new EventoDataSource(datosAplicacion);
						eventoDataSource.open();
						eventoBusqueda = eventoDataSource.getEventoId(eventoBusqueda);
						if(eventoBusqueda == null) {
							eventoBusqueda = new Evento();
							eventoBusqueda.setId(eventoJson.getString("id"));
							eventoBusqueda.setFecha(eventoJson.getString("fecha"));
							eventoBusqueda.setTipoEvento(eventoJson.getString("tipoEvento"));
							eventoBusqueda.setEstado(eventoJson.getString("estado"));
							eventoBusqueda.setMensaje("N");
							eventoBusqueda.setIdEquipo(datosAplicacion.getEquipoSeleccionado().getId());
							eventoBusqueda.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + datosAplicacion.getEquipoSeleccionado().getNombreEquipo());

							rec = AudioQueu.getComandoRecibido().get(contador);
							if(rec.has("valor")){
								fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + eventoBusqueda.getId() + ".jpg");
								fileOuputStream.write((byte[])rec.get("valor"));
								fileOuputStream.close();
								eventoBusqueda.setMensaje("S");
							}
							eventoDataSource.createEvento(eventoBusqueda);
						}else{
							eventoBusqueda.setFecha(eventoJson.getString("fecha"));
							eventoBusqueda.setTipoEvento(eventoJson.getString("tipoEvento"));
							eventoBusqueda.setEstado(eventoJson.getString("estado"));
							file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + eventoBusqueda.getId()  +".jpg");
							if(!file.exists()) {
								if(eventoJson.has("foto")) {
									eventoBusqueda.setMensaje("S");
									fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + eventoBusqueda.getId() + ".jpg");
									byte[] decodedString = Base64.decode(eventoJson.getString("foto"), Base64.DEFAULT);
									fileOuputStream.write(decodedString);
									fileOuputStream.close();
								}
							}

							eventoDataSource.updateEvento(eventoBusqueda);
						}
						eventoDataSource.close();
						if(y4HomeActivity != null){
							y4HomeActivity.actualizarPager();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else if (recibido[0].startsWith(YACSmartProperties.COM_SINCRONIZAR_LOG + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && nombreDispositivo.equals(recibido[1])){
					try {
						Log.d("Log sincronizado", (AudioQueu.getComandoRecibido().get(contador)).getString("mensaje"));
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}else if( recibido[0].startsWith(YACSmartProperties.COM_CONFIGURAR_BUZON  + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)) {
					if(y4HomeActivity != null){
						equipoBusqueda = datosAplicacion.getEquipoSeleccionado();
						equipoBusqueda.setBuzon(recibido[2]);
						equipoDataSource = new EquipoDataSource(y4HomeActivity);
						equipoDataSource.open();
						equipoDataSource.updateEquipo(equipoBusqueda);
						equipoDataSource.close();
						y4HomeActivity.btnHoy.post(new Runnable() {
							@Override
							public void run() {
								y4HomeActivity.actualizarBuzon(true);
							}
						});
					}
				} else if (recibido[0].equals(YACSmartProperties.COM_REPRODUCIR_TEXTO + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					if(y4HomeActivity != null && !AudioQueu.comunicacionAbierta) {
						y4HomeActivity.mostrarMensaje("Su mensaje fue enviado");
					}
				} else if (recibido[0].equals(YACSmartProperties.COM_ABRIR_PUERTA + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					if(y4HomeActivity != null && !AudioQueu.comunicacionAbierta) {

						y4HomeActivity.mostrarMensaje(recibido[2]);
					}
				}else if( recibido[0].startsWith(YACSmartProperties.COM_TERMINAR_HABLAR  + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					if(datosAplicacion.getMonitorIOActivity() != null) {
						datosAplicacion.getMonitorIOActivity().verificarResultadoHablar(YACSmartProperties.COM_TERMINAR_HABLAR + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO, true);
					}
				}else if( recibido[0].startsWith(YACSmartProperties.COM_INICIAR_HABLAR  + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					if(datosAplicacion.getMonitorIOActivity() != null) {
						datosAplicacion.getMonitorIOActivity().verificarResultadoHablar(YACSmartProperties.COM_INICIAR_HABLAR + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO, true);
					}
				}else if( recibido[0].startsWith(YACSmartProperties.COM_NOTIFICAR_IP_ACTUALIZADO + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					Log.d("ACTUALIZADO VERSION",  "respuestas " + recibido[5]);
					equipoDataSource = new EquipoDataSource(datosAplicacion);
					equipoDataSource.open();
					equipoBusqueda = new Equipo();
					equipoBusqueda.setNumeroSerie(recibido[1]);
					equipoBusqueda = equipoDataSource.getEquipoNumSerie(equipoBusqueda);
					if(equipoBusqueda != null && equipoBusqueda.getId() != null) {
						datosAplicacion.getEquipoSeleccionado().setIpLocal(recibido[2]);
						datosAplicacion.getEquipoSeleccionado().setLuz(recibido[18]);
						datosAplicacion.getEquipoSeleccionado().setBuzon(recibido[8]);
						datosAplicacion.getEquipoSeleccionado().setLuzWifi(recibido[19]);
						datosAplicacion.getEquipoSeleccionado().setPuerta(recibido[16]);
						datosAplicacion.getEquipoSeleccionado().setSensor(recibido[17]);
						datosAplicacion.getEquipoSeleccionado().setMensajeInicial(recibido[20].trim());
						datosAplicacion.getEquipoSeleccionado().setPuertoActivo(recibido[21]);
						datosAplicacion.getEquipoSeleccionado().setTimbreExterno(recibido[26]);
						datosAplicacion.getEquipoSeleccionado().setMensajeApertura(recibido[29]);
						datosAplicacion.getEquipoSeleccionado().setMensajePuerta(recibido[30]);
						datosAplicacion.getEquipoSeleccionado().setVolumen(Integer.valueOf(recibido[38]));
						datosAplicacion.getEquipoSeleccionado().setTiempoEncendidoLuz(Integer.valueOf(recibido[37]));
						AudioQueu.version = recibido[5];
						if(recibido[34].equals("1")) {
							datosAplicacion.getEquipoSeleccionado().setModo(YACSmartProperties.MODO_WIFI);
						}else{
							datosAplicacion.getEquipoSeleccionado().setModo(YACSmartProperties.MODO_CABLE);
						}
						equipoDataSource.updateEquipo(datosAplicacion.getEquipoSeleccionado());
						if(datosAplicacion.getEquipoSeleccionado() != null
								&& datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(equipoBusqueda.getNumeroSerie())
								&& !AudioQueu.comunicacionAbierta && datosAplicacion.getY4HomeActivity() != null){
						//	datosAplicacion.setEquipoSeleccionado(equipoBusqueda);
							datosAplicacion.getY4HomeActivity().actualizarEstado(recibido, false);
						}

						Integer nuevaVersionPerfil = Integer.valueOf(recibido[27]);
						if(!nuevaVersionPerfil.equals(0)){
							//C30R;
							// YH-1487704598745;
							// 192.168.1.3;
							// Casa La Estacion;
							// 4;
							// 56;
							// America/Bogota;
							// 201.217.103.92;
							// 0;
							// CESAR ALVAREZ Q;
							// 0500175385;
							// dC-DhqCmwaI:APA91bG-u7yh0aMK5nJCVybE-UJE5KwdnBheipuqxoz9evAdC90qCTWZZ70U5oWvY2MsvNA3WzDSSEy9lgdiujUIF0fAR09VKqahfa4-IUqwIvIS3CGdqHAZznszZO4L0xZRZDeTgAMc;
							// 0;
							// 0;
							// 0;
							// 0;
							// 1;
							// 1;
							// 1;
							// 0;
							// un momento por favor;
							// 0;
							// 1100;
							// 30000;
							// 6500;
							// 20000;
							// 1;
							// 1;
							// 3;
							// por seguridad cierre la puerta;
							// pase por favor;
							// 181.112.82.59;
							// true;
							// INS;
							// 0;
							// 2017/04/05 10:08:06;
							// false;
							if(Integer.valueOf(datosAplicacion.getEquipoSeleccionado().getVersionFoto()) < nuevaVersionPerfil){
								//Actualizar foto
									String datosConfT = YACSmartProperties.COM_SOLICITAR_FOTO_PERFIL + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + nuevaVersionPerfil + ";";
								AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
								AudioQueu.contadorComandoEnviado++;
							}
						}
					}
					equipoDataSource.close();

					AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_TOMAR_FOTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";0;");
					AudioQueu.contadorComandoEnviado++;

				}else if( recibido[0].startsWith(YACSmartProperties.COM_TOMAR_FOTO + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					if(datosAplicacion.getEquipoSeleccionado() != null
							&& datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(recibido[3])
							&& !AudioQueu.comunicacionAbierta && datosAplicacion.getY4HomeActivity() != null){

							rec = AudioQueu.getComandoRecibido().get(contador);
						if(rec.has("valor")){
							try {
								datosAplicacion.getY4HomeActivity().actualizarPager((byte[])rec.get("valor"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

				}else if( recibido[0].startsWith(YACSmartProperties.COM_SOLICITAR_FOTO_INICIAL + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){
					file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + recibido[4]  +".jpg");
					if(!file.exists()) {
						try {
							rec = AudioQueu.getComandoRecibido().get(contador);
							if(rec.has("valor")) {
								//byte[] decodedString = Base64.decode(recibido[5], Base64.DEFAULT);
								try {
									AudioQueu.fotoTimbre = (byte[]) rec.get("valor");
									AudioQueu.imagenRecibida = true;
									Log.d("FOTO OBTENIDA", "SOCKET");
									fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + recibido[4] + ".jpg");
									fileOuputStream.write((byte[]) rec.get("valor"));
									fileOuputStream.close();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}else if( recibido[0].startsWith(YACSmartProperties.COM_SOLICITAR_FOTO_PERFIL + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)){

					try {
						rec = AudioQueu.getComandoRecibido().get(contador);
						if(rec.has("valor")) {
							fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + recibido[3] + ".jpg");
							//	byte[] decodedString = Base64.decode(recibido[5], Base64.DEFAULT);
							fileOuputStream.write((byte[]) rec.get("valor"));
							fileOuputStream.close();
							datosAplicacion.getEquipoSeleccionado().setVersionFoto(recibido[4]);
							EquipoDataSource dataSource = new EquipoDataSource(datosAplicacion);
							dataSource.open();
							dataSource.updateEquipo(datosAplicacion.getEquipoSeleccionado());
							dataSource.close();

							if (datosAplicacion.getY4HomeActivity() != null && datosAplicacion.getEquipoSeleccionado() != null && datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(recibido[3])) {
								datosAplicacion.getY4HomeActivity().actualizarFotoPerfil();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}


				}else if( recibido[0].startsWith(YACSmartProperties.COM_SOLICITAR_FOTO_TIMBRE + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)) {
					file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + recibido[4] + ".jpg");
					if (!file.exists()) {
						try {
							rec = AudioQueu.getComandoRecibido().get(contador);
							if(rec.has("valor")) {

								fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + recibido[4] + ".jpg");
								fileOuputStream.write((byte[]) rec.get("valor"));
								fileOuputStream.close();

								if (datosAplicacion.getEventosActivity() != null) {
									datosAplicacion.getEventosActivity().actualizarLista();
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}else if( recibido[0].startsWith(YACSmartProperties.COM_DISPOSITIVO_CONTESTO + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)) {
					if (recibido[1].equals("OCU") && !recibido[2].equals(nombreDispositivo)) {
						if(datosAplicacion.getMonitorIOActivity() != null)
							datosAplicacion.getMonitorIOActivity().porteroOcupado(recibido[2]);
					}

				}else if(recibido[0].equals(YACSmartProperties.COM_INICIAR_COMUNICACION  + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante){

					if(PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(datosAplicacion, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
							PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(datosAplicacion, android.Manifest.permission.RECORD_AUDIO)) {


						equipoDataSource = new EquipoDataSource(datosAplicacion);
						equipoDataSource.open();
						equipoBusqueda = new Equipo();
						equipoBusqueda.setNumeroSerie(recibido[6]);
						equipoBusqueda = equipoDataSource.getEquipoNumSerie(equipoBusqueda);
						equipoDataSource.close();

						if(equipoBusqueda != null && equipoBusqueda.getId() != null) {
							datosAplicacion.setEquipoSeleccionado(equipoBusqueda);
							eventoDataSource = new EventoDataSource(datosAplicacion);
							eventoDataSource.open();

							//Verificar si el id existe
							eventoBusqueda = new Evento();
							eventoBusqueda.setId(recibido[4]);
							eventoBusqueda = eventoDataSource.getEventoId(eventoBusqueda);


							if (eventoBusqueda == null) {

								AudioQueu.llamadaEntrante = true;
								Log.d("AudioQueu.llamadaEntrante7","true");
								AudioQueu.imagenRecibida = false;


								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								Date date = new Date();
								eventoBusqueda = new Evento();
								eventoBusqueda.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + recibido[1]);
								eventoBusqueda.setId(recibido[5]);
								eventoBusqueda.setFecha(dateFormat.format(date));
								eventoBusqueda.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
								eventoBusqueda.setComando(recibido[0]);
								eventoBusqueda.setTipoEvento(TipoEventoEnum.TIMBRAR.getCodigo());
								eventoBusqueda.setIdEquipo(equipoBusqueda.getId());
								eventoDataSource.createEvento(eventoBusqueda);
								eventoDataSource.close();

								esComunicacionDirecta = false;

								intent = new Intent(y4HomeActivity, LlamadaEntrantePorteroActivity.class);
								intent.putExtra("comando", recibido[0]);
								intent.putExtra("timbrando", true);
								intent.removeExtra("foto");
								intent.putExtra("idEvento", eventoBusqueda.getId());
								intent.putExtra("foto", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + eventoBusqueda.getId() + ".jpg");
								intent.putExtra("esComunicacionDirecta", false);
								intent.setAction("android.intent.action.MAIN");
								intent.addCategory("android.intent.category.LAUNCHER");
								y4HomeActivity.startActivity(intent);

							} else {
								eventoDataSource.close();
							}
						}


					}
				}else if(recibido[0].equals(YACSmartProperties.COM_BUSCAR_ROUTER  + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante){
					Log.d("COM_BUSCAR_ROUTER","COM_BUSCAR_ROUTER");
//					if(datosAplicacion.getAdministrarRoutersActivity() != null && recibido.length > 4){
//						Log.d("COM_BUSCAR_ROUTER","COM_BUSCAR_ROUTER 1 " + recibido[4]);
//						datosAplicacion.getAdministrarRoutersActivity().actualizarRouters(recibido[4]);
//					}

//				}else if(recibido[0].equals(YACSmartProperties.COM_LINK_ROUTER + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante){
//					EquipoDataSource routerDataSource = new EquipoDataSource(datosAplicacion);
//					routerDataSource.open();
//
//					Equipo equipoBusqueda = new Equipo();
//					equipoBusqueda.setNumeroSerie(recibido[5]);
//					Equipo routerLuces = routerDataSource.getEquipoNumSerie(equipoBusqueda);
//					if(routerLuces == null){
//						routerLuces = new Equipo();
//						routerLuces.setEstadoEquipo("INS");
//						routerLuces.setNumeroSerie(recibido[5]);
//						routerLuces.setNombreEquipo(recibido[6]);
//						routerLuces.setIpLocal(recibido[4]);
//						routerLuces.setId(recibido[5]);
//						routerLuces.setTipoEquipo(TipoEquipoEnum.LUCES.getCodigo());
//						//routerLuces.setIdEquipo(datosAplicacion.getEquipoSeleccionado().getId());
//						routerDataSource.createEquipo(routerLuces);
//					}else{
//						routerLuces.setEstadoEquipo("INS");
//						routerLuces.setNumeroSerie(recibido[5]);
//						routerLuces.setNombreEquipo(recibido[6]);
//						routerLuces.setIpLocal(recibido[4]);
//						//routerLuces.setIdEquipo(datosAplicacion.getEquipoSeleccionado().getId());
//						routerDataSource.updateEquipo(routerLuces);
//					}
//					routerDataSource.close();
//
//					if(datosAplicacion.getAdministrarRoutersActivity() != null){
//						datosAplicacion.getAdministrarRoutersActivity().verificarRouter(YACSmartProperties.COM_LINK_ROUTER, true);
//					}
//				}else if(recibido[0].equals(YACSmartProperties.COM_UNLINK_ROUTER + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante){
//					EquipoDataSource routerDataSource = new EquipoDataSource(datosAplicacion);
//					routerDataSource.open();
//					routerDataSource.deleteEquipo(recibido[5]);
//					routerDataSource.close();
//
//					ZonaDataSource zonaDataSource = new ZonaDataSource(datosAplicacion);
//					zonaDataSource.open();
//					zonaDataSource.deleteZonaImac(recibido[5]);
//					zonaDataSource.close();
//
//					if(datosAplicacion.getAdministrarRoutersActivity() != null){
//						datosAplicacion.getAdministrarRoutersActivity().verificarRouter(YACSmartProperties.COM_UNLINK_ROUTER, true);
//					}

//				}else if(recibido[0].equals(YACSmartProperties.COM_SINCRONIZAR_ZONA_LUCES  + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante) {
//					Log.d("COM_SINCRONIZAR_ZONA_LUCES","COM_SINCRONIZAR_ZONA_LUCES 1 " + recibido.length);
//					if(datosAplicacion.getAdministrarZonasActivity() != null && recibido.length > 4){
//						datosAplicacion.getAdministrarZonasActivity().actualizarZonas(recibido[5]);
//					}
//				}else if(recibido[0].equals(YACSmartProperties.COM_CREAR_ZONA_LUCES + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante){
//					if(recibido[7].equals("OK")) {
//						ZonaDataSource zonaDataSource = new ZonaDataSource(datosAplicacion);
//						zonaDataSource.open();
//						//zonaDataSource.deleteZonaImac(recibido[4]);
//						ZonaLuces zonaLuces = new ZonaLuces();
//						zonaLuces.setEncenderTimbre("0");
//						zonaLuces.setIdRouter(recibido[4]);
//						zonaLuces.setNombreZona(recibido[5]);
//						zonaLuces.setId(recibido[6]);
//						zonaLuces.setIdEquipo(datosAplicacion.getEquipoSeleccionado().getId());
//						zonaDataSource.createZona(zonaLuces);
//
//						zonaDataSource.close();
//
//						if (datosAplicacion.getAdministrarZonasActivity() != null) {
//							datosAplicacion.getAdministrarZonasActivity().verificarZonas(YACSmartProperties.COM_CREAR_ZONA_LUCES, true);
//						}
//					}else{
//						if (datosAplicacion.getAdministrarZonasActivity() != null) {
//							datosAplicacion.getAdministrarZonasActivity().verificarZonas(YACSmartProperties.COM_CREAR_ZONA_LUCES, false);
//						}
//					}
//				}else if(recibido[0].equals(YACSmartProperties.COM_ELIMINAR_ZONA_LUCES + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante){
//					if(recibido[6].equals("OK")) {
//
//						if (datosAplicacion.getAdministrarZonasActivity() != null) {
//							datosAplicacion.getAdministrarZonasActivity().verificarZonas(YACSmartProperties.COM_CREAR_ZONA_LUCES, true);
//						}
//					}else{
//						if (datosAplicacion.getAdministrarZonasActivity() != null) {
//							datosAplicacion.getAdministrarZonasActivity().verificarZonas(YACSmartProperties.COM_CREAR_ZONA_LUCES, false);
//						}
//					}
//				}else if(recibido[0].equals(YACSmartProperties.COM_CREAR_PROGRAMACION) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante){
//					if(recibido[12].equals("OK")) {
//						ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(datosAplicacion);
//						programacionDataSource.open();
//						ProgramacionLuces programacionLuces = new ProgramacionLuces();
//						;
//						programacionLuces.setIdRouter(recibido[4]);
//						programacionLuces.setIdZona(recibido[5]);
//						programacionLuces.setNombre(recibido[6]);
//						programacionLuces.setAccion(recibido[7]);
//						programacionLuces.setHoraInicio(recibido[8]);
//						programacionLuces.setDuracion(recibido[9]);
//						programacionLuces.setDias(recibido[10]);
//						programacionLuces.setId(recibido[11]);
//						programacionDataSource.createProgramacion(programacionLuces);
//						programacionDataSource.close();
//
//						if (datosAplicacion.getProgramacionActivity() != null) {
//							datosAplicacion.getProgramacionActivity().verificarProgramacion(YACSmartProperties.COM_CREAR_PROGRAMACION, true);
//						}
//					}else{
//						if (datosAplicacion.getProgramacionActivity() != null) {
//							datosAplicacion.getProgramacionActivity().verificarProgramacion(YACSmartProperties.COM_CREAR_PROGRAMACION, false);
//						}
//					}
				}
				AudioQueu.getComandoRecibido().remove(contador);
				contador++;

			}

			if(AudioQueu.getComandoEnviado().containsKey(contadorEnviados) ){
				Log.d("contadorEnviados", String.valueOf(contadorEnviados));
				JSONObject obj = new JSONObject();
				try {
					obj.put("room", datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
					obj.put("mensaje", AudioQueu.getComandoEnviado().get(contadorEnviados) + ";");

				} catch (JSONException e) {
					e.printStackTrace();
				}
				AudioQueu.mSocketComando.emit("chat message1", obj);

				Log.d("onComando", AudioQueu.getComandoEnviado().get(contadorEnviados) + contadorEnviados);
				AudioQueu.getComandoEnviado().remove(contadorEnviados);
				contadorEnviados++;

			}
		}

		AudioQueu.socketComando = false;
		AudioQueu.mSocketComando.disconnect();
		AudioQueu.mSocketComando.off(Socket.EVENT_CONNECT, onConnectC);
		AudioQueu.mSocketComando.off(Socket.EVENT_DISCONNECT, onDisconnectC);
		AudioQueu.mSocketComando.off(Socket.EVENT_CONNECT_ERROR, onConnectErrorC);
		AudioQueu.mSocketComando.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorC);
		AudioQueu.mSocketComando.off("chat message", onComando);
		AudioQueu.mSocketComando.close();
		AudioQueu.mSocketComando = null;
		Log.d("ProcesarComando", "ComandoIOScheduledTask");
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
			AudioQueu.getComandoRecibido().put(contadorRecibido, (JSONObject) args[0]);
			contadorRecibido++;
		}
	};
}