package ec.com.yacare.y4all.lib.asynctask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.SplashActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.enumer.EstadoEventoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEventoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.activities.GcmIntentService.NOTIFICATION_ID;

public class RecibirComandoRemotoAsyncTask  extends  AsyncTask<String, Float, String> {

	private Activity activityOrigen;
	private Class activityDestino;
	private String numeroSerie;
	private Context context;

	private DatosAplicacion datosAplicacion;
	private String mensaje[];

	public RecibirComandoRemotoAsyncTask(Activity activityOrigen, Class activityDestino, Context context, String numeroSerie) {
		super();
		this.activityOrigen = activityOrigen;
		this.activityDestino = activityDestino;
		this.numeroSerie = numeroSerie;
		this.context = context;
	}

	@Override
	protected String doInBackground(String... arg0) {
		datosAplicacion = (DatosAplicacion) context;
		String grupoMulticast = YACSmartProperties.GRUPO_COMANDO_REMOTO_MULTICAST_CABLE_RED;
		try {
			byte[] receiveData = new byte[512];
			MulticastSocket socketGrupo = new MulticastSocket(YACSmartProperties.PUERTO_COMANDO_REMOTO_MULTICAST);
			socketGrupo.joinGroup(InetAddress.getByName(grupoMulticast));

			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			EquipoDataSource equipoDataSource;
			Equipo equipo;
			EventoDataSource eventoDataSource;
			Evento eventoBusqueda;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date;
			Intent intent;
			Integer nuevaVersionPerfil;
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(datosAplicacion);
			String nombreDispositivo;
			String datosConfT;
			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			NotificationCompat.Builder mBuilder;
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Log.d("MENSAJE", "RecibirComandoRemotoAsyncTask Inicio" );
			while (datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(numeroSerie) && !AudioQueu.isInBackground) {
				socketGrupo.receive(receivePacket);

				mensaje = (new String(receiveData)).split(";");
				Log.d("MENSAJE", "MENSAJE" + mensaje[0] + AudioQueu.comunicacionAbierta + " /" + AudioQueu.llamadaEntrante);

				if (mensaje[0].equals(YACSmartProperties.COM_INICIAR_COMUNICACION) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante) {

					if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
							PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO)) {

						//C11;ORIGEN;MENSAJE;HORA;IP;PUERTOAUDIO;PUERTOVIDEO;PUERTOREMOTO;IDMENSAJE;NUMEROSERIE;CALIDADVIDEO;NOMBREEQUIPO
						//Verificar si el id existe
						//Timbrando
						equipoDataSource = new EquipoDataSource(context);
						equipoDataSource.open();
						equipo = new Equipo();
						equipo.setNumeroSerie(mensaje[9]);
						equipo = equipoDataSource.getEquipoNumSerie(equipo);
						equipoDataSource.close();

						if (equipo != null && equipo.getId() != null) {
							datosAplicacion.setEquipoSeleccionado(equipo);
							eventoDataSource = new EventoDataSource(context);
							eventoDataSource.open();

							//Verificar si el id existe
							eventoBusqueda = new Evento();
							eventoBusqueda.setId(mensaje[8]);
							Evento evento = eventoDataSource.getEventoId(eventoBusqueda);

							guardarConfiguracionRed(mensaje);

							if (evento == null) {
								AudioQueu.llamadaEntrante = true;
								AudioQueu.imagenRecibida = false;
								AudioQueu.fotoTimbre = null;

								date = new Date();
								evento = new Evento();
								evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + mensaje[1]);
								evento.setId(mensaje[8]);
								evento.setFecha(dateFormat.format(date));
								evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
								evento.setComando(mensaje[0]);
								evento.setTipoEvento(TipoEventoEnum.TIMBRAR.getCodigo());
								evento.setIdEquipo(equipo.getId());
								eventoDataSource.createEvento(evento);
								eventoDataSource.close();
								AudioQueu.esComunicacionDirecta = true;

								intent = new Intent(activityOrigen, activityDestino);
								intent.putExtra("comando", mensaje[0]);
								intent.putExtra("timbrando", true);
								intent.removeExtra("foto");
								intent.putExtra("idEvento", evento.getId());
								intent.putExtra("foto", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + evento.getId() + ".jpg");
								intent.putExtra("esComunicacionDirecta", true);
								intent.setAction("android.intent.action.MAIN");
								intent.addCategory("android.intent.category.LAUNCHER");
								activityOrigen.startActivity(intent);
							} else {
								eventoDataSource.close();
							}
						}
					}

				}else if (mensaje[0].equals(YACSmartProperties.COM_NOTIFICAR_BUZON) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante) {
					//El portero respondio por usted
					equipoDataSource = new EquipoDataSource(context);
					equipoDataSource.open();
					equipo = new Equipo();
					equipo.setNumeroSerie(mensaje[3]);
					equipo = equipoDataSource.getEquipoNumSerie(equipo);
					equipoDataSource.close();

					if (equipo != null && equipo.getId() != null) {
						date = new Date();

						eventoDataSource = new EventoDataSource(context);
						eventoDataSource.open();
						eventoBusqueda = new Evento();
						eventoBusqueda.setId(mensaje[4]);
						eventoBusqueda = eventoDataSource.getEventoId(eventoBusqueda);
						if (eventoBusqueda != null) {
							//Si existe ese evento se actualiza a tipo buzon
							eventoBusqueda.setIdGrabado(mensaje[4]);
							eventoBusqueda.setTipoEvento(TipoEventoEnum.BUZON.getCodigo());
							eventoDataSource.updateEvento(eventoBusqueda);
						} else {
							//Si no existe el evento se crea
							eventoBusqueda = new Evento();
							eventoBusqueda.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + equipo.getNombreEquipo());
							eventoBusqueda.setId(mensaje[4]);
							eventoBusqueda.setFecha(dateFormat.format(date));
							eventoBusqueda.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
							eventoBusqueda.setComando(mensaje[0]);
							eventoBusqueda.setTipoEvento(TipoEventoEnum.BUZON.getCodigo());
							eventoBusqueda.setIdEquipo(equipo.getId());
							eventoBusqueda.setMensaje(mensaje[4]);
							eventoBusqueda.setIdGrabado(mensaje[4]);
							eventoDataSource.createEvento(eventoBusqueda);

						}
						eventoDataSource.close();
						mBuilder =	new NotificationCompat.Builder(context)
								.setSmallIcon(R.drawable.y4homeb)
								.setContentTitle(equipo.getNombreEquipo())
										.setSmallIcon(R.drawable.y4homeb)
										.setStyle(new NotificationCompat.BigTextStyle()
												.bigText(mensaje[2]))
										.setContentText(mensaje[2])
										.setSound(soundUri)
										.setAutoCancel(true);

						intent = new Intent(activityOrigen, SplashActivity.class);
						intent.putExtra("comando", mensaje[0]);
						intent.setAction("android.intent.action.MAIN");
						intent.addCategory("android.intent.category.LAUNCHER");

						PendingIntent contentIntent = PendingIntent.getActivity(activityOrigen, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

						mBuilder.setContentIntent(contentIntent);
						mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
					}

				} else if (mensaje[0].equals(YACSmartProperties.COM_NOTIFICAR_IP_ACTUALIZADO) && !AudioQueu.comunicacionAbierta && !AudioQueu.llamadaEntrante) {
					equipoDataSource = new EquipoDataSource(context);
					equipoDataSource.open();
					if (datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(mensaje[1])) {
						datosAplicacion.getEquipoSeleccionado().setIpLocal(mensaje[2]);
						datosAplicacion.getEquipoSeleccionado().setBuzon(mensaje[8]);
						datosAplicacion.getEquipoSeleccionado().setLuz(mensaje[18]);
						datosAplicacion.getEquipoSeleccionado().setLuzWifi(mensaje[19]);
						datosAplicacion.getEquipoSeleccionado().setPuerta(mensaje[16]);
						datosAplicacion.getEquipoSeleccionado().setSensor(mensaje[17]);
						datosAplicacion.getEquipoSeleccionado().setMensajeInicial(mensaje[20].trim());
						datosAplicacion.getEquipoSeleccionado().setPuertoActivo(mensaje[21]);
						datosAplicacion.getEquipoSeleccionado().setTimbreExterno(mensaje[26]);
						datosAplicacion.getEquipoSeleccionado().setMensajeApertura(mensaje[29]);
						datosAplicacion.getEquipoSeleccionado().setMensajePuerta(mensaje[30]);
						datosAplicacion.getEquipoSeleccionado().setVolumen(Integer.valueOf(mensaje[38]));
						datosAplicacion.getEquipoSeleccionado().setTiempoEncendidoLuz(Integer.valueOf(mensaje[37]));
						if (mensaje[34].equals("1")) {
							datosAplicacion.getEquipoSeleccionado().setModo(YACSmartProperties.MODO_WIFI);
						} else {
							datosAplicacion.getEquipoSeleccionado().setModo(YACSmartProperties.MODO_CABLE);
						}

						if (datosAplicacion.getY4HomeActivity() != null) {
							datosAplicacion.getY4HomeActivity().actualizarEstado(mensaje, true);
						}
						nuevaVersionPerfil = Integer.valueOf(mensaje[27]);
						if (!nuevaVersionPerfil.equals(0)) {
							nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
							if (Integer.valueOf(datosAplicacion.getEquipoSeleccionado().getVersionFoto()) < nuevaVersionPerfil) {
								//Actualizar foto
								datosConfT = YACSmartProperties.COM_SOLICITAR_FOTO_PERFIL + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + nuevaVersionPerfil + ";";
								AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
								AudioQueu.contadorComandoEnviado++;
							}
						}
						equipoDataSource.updateEquipo(datosAplicacion.getEquipoSeleccionado());

					}
					equipoDataSource.close();
					AudioQueu.setTipoConexion(TipoConexionEnum.WIFI.getCodigo());

				} else if (mensaje[0].equals(YACSmartProperties.COM_DISPOSITIVO_CONTESTO)) {
					equipoDataSource = new EquipoDataSource(context);
					equipoDataSource.open();
					equipo = new Equipo();
					equipo.setNumeroSerie(mensaje[3]);
					equipo = equipoDataSource.getEquipoNumSerie(equipo);
					equipoDataSource.close();
					 nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
					if (equipo != null && equipo.getId() != null && mensaje[1].equals("OCU") && !mensaje[2].equals(nombreDispositivo)) {
						AudioQueu.llamadaEntrante = false;
						if (datosAplicacion.getMonitorIOActivity() != null) {
							datosAplicacion.getMonitorIOActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											datosAplicacion.getMonitorIOActivity());
									alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
											.setMessage(mensaje[2] + " está conectado a Wii Bell")
											.setCancelable(false)
											.setPositiveButton("OK", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int id) {
													AudioQueu.comunicacionAbierta = false;
													datosAplicacion.getMonitorIOActivity().finish();
												}
											});
									AlertDialog alertDialog = alertDialogBuilder.create();
									alertDialog.show();
								}
							});

						}


						mBuilder =
								new NotificationCompat.Builder(context)
										.setSmallIcon(R.drawable.y4homeb)
										.setContentTitle(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + mensaje[1])
										.setStyle(new NotificationCompat.BigTextStyle()
												.bigText(mensaje[2] + " está conectado a Wii Bell"))
										.setContentText(mensaje[2] + " está conectado a Wii Bell")
										.setSound(soundUri)
										.setAutoCancel(true);


						mNotificationManager.notify(1, mBuilder.build());
					}

				} else if (mensaje[0].equals(YACSmartProperties.COM_ACTIVACION_SENSOR)) {
					equipoDataSource = new EquipoDataSource(context);
					equipoDataSource.open();
					equipo = new Equipo();
					equipo.setNumeroSerie(mensaje[5]);
					equipo = equipoDataSource.getEquipoNumSerie(equipo);
					equipoDataSource.close();

					if (equipo != null) {
						dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						date = new Date();

						eventoDataSource = new EventoDataSource(context);
						eventoDataSource.open();
						eventoBusqueda = new Evento();
						eventoBusqueda.setId(mensaje[4]);
						Evento evento = eventoDataSource.getEventoId(eventoBusqueda);
						if (evento == null) {
							evento = new Evento();
							evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " );
							evento.setId(mensaje[4]);
							evento.setFecha(dateFormat.format(date));
							evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
							evento.setComando(mensaje[0]);
							evento.setTipoEvento(TipoEventoEnum.PUERTA.getCodigo());
							evento.setIdEquipo(equipo.getId());
							evento.setMensaje(mensaje[2]);
							eventoDataSource.createEvento(evento);

						}
						eventoDataSource.close();

						mBuilder =	new NotificationCompat.Builder(context)
										.setSmallIcon(R.drawable.y4homeb)
										.setContentTitle(mensaje[1])
										.setStyle(new NotificationCompat.BigTextStyle()
												.bigText(mensaje[2]))
										.setContentText(mensaje[2])
										.setSound(soundUri)
										.setAutoCancel(true);

						intent = new Intent(context, SplashActivity.class);
						intent.putExtra("comando", mensaje[0]);
						intent.setAction("android.intent.action.MAIN");
						intent.addCategory("android.intent.category.LAUNCHER");

						PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

						mBuilder.setContentIntent(contentIntent);


						mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

//						nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
//						AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SOLICITAR_VIDEO_SENSOR + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipo.getNumeroSerie() + ";" + evento.getId() + ";");
//						AudioQueu.contadorComandoEnviado++;
					}

				} else if (mensaje[0].equals(YACSmartProperties.COM_PING)) {
					DatagramPacket sendPacketDestino = new DatagramPacket(
							receiveData,
							receiveData.length,
							receivePacket.getAddress(),
							receivePacket.getPort());
					socketGrupo.send(sendPacketDestino);
				} else {
					 mBuilder =	new NotificationCompat.Builder(context)
									.setSmallIcon(R.drawable.y4homeb)
									.setContentTitle("WiiBell")
									.setStyle(new NotificationCompat.BigTextStyle()
											.bigText(mensaje[2]))
									.setContentText(mensaje[2])
									.setSound(soundUri)
									.setAutoCancel(true);


					intent = new Intent(context, SplashActivity.class);
					intent.putExtra("comando", mensaje[0]);
					intent.setAction("android.intent.action.MAIN");
					intent.addCategory("android.intent.category.LAUNCHER");

					PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

					mBuilder.setContentIntent(contentIntent);

					mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("RecibirComandoRemotoAsyncTask","Saliendo RecibirComandoRemotoAsyncTask");
		return  "";
	}

	

	private void guardarConfiguracionRed(String mensaje[]) {
		EquipoDataSource equipoDataSource = new EquipoDataSource(context);
		equipoDataSource.open();
		Equipo equipoBusqueda = new Equipo();
		equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.INSTALADO.getCodigo());
		equipoBusqueda.setNumeroSerie(mensaje[9]);
		Equipo equipo = equipoDataSource.getEquipoNumSerie(equipoBusqueda);
		if(equipo != null && equipo.getId() != null){
			DatosAplicacion datosAplicacion = (DatosAplicacion) context;
			boolean grabar = false;

			if(equipo.getIpLocal() != null && !equipo.getIpLocal().equals(mensaje[4])){
				grabar = true;
				equipo.setIpLocal(mensaje[4]);
			}
			if(equipo.getNombreEquipo() != null && !equipo.getNombreEquipo().equals(mensaje[10])){
				grabar = true;
				equipo.setNombreEquipo(mensaje[10]);
			}
			if(grabar){
				equipoDataSource.updateEquipo(equipo);
			}
			datosAplicacion.setEquipoSeleccionado(equipo);
			equipoDataSource.close();
		}else{
			equipoDataSource.close();
		}
	}
}