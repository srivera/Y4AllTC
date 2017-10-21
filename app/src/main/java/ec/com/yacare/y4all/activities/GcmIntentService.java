/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ec.com.yacare.y4all.activities;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory.Options;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.com.yacare.y4all.activities.dispositivo.BuscarTelefonoActivity;
import ec.com.yacare.y4all.activities.dispositivo.LlamadaEntranteActivity;
import ec.com.yacare.y4all.activities.portero.LlamadaEntrantePorteroActivity;
import ec.com.yacare.y4all.asynctask.ws.SolicitarBuzonAsyncTask;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.dto.Mensaje;
import ec.com.yacare.y4all.lib.enumer.EstadoEventoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEventoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.CuentaDataSource;
import ec.com.yacare.y4all.lib.sqllite.DispositivoDataSource;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.sqllite.MensajeDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.lib.util.AudioQueu.esComunicacionDirecta;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    
    
    
    
    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "Portero";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { 

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(extras);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(extras);
            } else  {
                sendNotification(extras);
                Log.i("MENSAJE PUSSSSSSSH", "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle extras) {
   		String comando = extras.getString("comando");
   		String numeroSerie = extras.getString("numeroSerie");
   		String idMensaje = extras.getString("idMensaje");
   		String origen = extras.getString("origen");
   		String mensaje = extras.getString("mensaje");
   		String ip = extras.getString("ip");
   		
   		
   		DatosAplicacion datosAplicacion = (DatosAplicacion) getApplicationContext();
   		

		mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Se verifica el tipo de equipo que esta enviando el mensaje
		EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
		equipoDataSource.open();
		Equipo equipo = new Equipo();
		equipo.setNumeroSerie(numeroSerie);
		equipo = equipoDataSource.getEquipoNumSerie(equipo);
		equipoDataSource.close();
		
		String tipoEquipo = null;
		if(equipo != null && equipo.getId() != null){
			tipoEquipo = TipoEquipoEnum.getTipoEquipo(equipo.getTipoEquipo()).getDescripcion();
		}
		
		//Si es un portero y esta timbrando o va al buzon de mensajes
		if(tipoEquipo != null && equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo()) && (comando.equals(YACSmartProperties.COM_INICIAR_COMUNICACION) || comando.equals(YACSmartProperties.COM_BUZON_MENSAJES))){

			if(comando.equals(YACSmartProperties.COM_INICIAR_COMUNICACION) && !AudioQueu.llamadaEntrante && !AudioQueu.comunicacionAbierta){

				if(PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
						PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO)) {
					datosAplicacion.setEquipoSeleccionado(equipo);

					//Boolean esComunicacionDirecta = esComunicacionDirecta();
					String visitante = extras.getString("visitante");

					//Timbrando
					EventoDataSource datasource = new EventoDataSource(getApplicationContext());
					datasource.open();


					//Verificar si el id existe
					Evento eventoBusqueda = new Evento();
					eventoBusqueda.setId(idMensaje);
					Evento evento = datasource.getEventoId(eventoBusqueda);

					if (evento == null) {
						AudioQueu.llamadaEntrante = true;
						Log.d("AudioQueu.llamadaEntrante1","true");
						AudioQueu.imagenRecibida = false;
						AudioQueu.fotoTimbre = null;
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date date = new Date();
						evento = new Evento();
						evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + extras.getString("nombreEquipo"));
						evento.setId(idMensaje);
						evento.setFecha(dateFormat.format(date));
						evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
						evento.setComando(comando);
						evento.setTipoEvento(TipoEventoEnum.TIMBRAR.getCodigo());
						evento.setIdEquipo(equipo.getId());

						datasource.createEvento(evento);
						datasource.close();

						//AudioQueu.esComunicacionDirecta = esComunicacionDirecta;

//						ObtenerFotoAsyncTask genericoAsyncTask = new ObtenerFotoAsyncTask(getApplicationContext(), evento.getId());
//						genericoAsyncTask.start();
						Options thumbOpts = new Options();
						thumbOpts.inSampleSize = 4;

						Intent intentReloj = new Intent();
						intentReloj.setAction("ec.com.yacare.action.PORTERO");
						intentReloj.putExtra("origen", origen);
						intentReloj.putExtra("mensaje", mensaje);
						sendBroadcast(intentReloj);

						Intent intentOpen = new Intent(this, LlamadaEntrantePorteroActivity.class);
						intentOpen.putExtra("comando", comando);
						intentOpen.putExtra("ip", ip);
						intentOpen.putExtra("visitante", visitante);
						intentOpen.setAction("android.intent.action.MAIN");
						intentOpen.addCategory("android.intent.category.LAUNCHER");

						intentOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intentOpen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

						intentOpen.putExtra("timbrando", true);
						intentOpen.putExtra("idEvento", idMensaje);
						intentOpen.removeExtra("foto");
						intentOpen.putExtra("foto", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + evento.getId() + ".jpg");
						intentOpen.putExtra("esComunicacionDirecta", esComunicacionDirecta);
						Log.i("GCM", "VALOR " + String.valueOf(esComunicacionDirecta));

						//Probar esta linea
						Log.i("MENSAJE PUSH1", "Received: " + extras.toString());
						startActivity(intentOpen);
						Log.i("MENSAJE PUSH2", "Received: " + extras.toString());

					} else {
						//Ya fue capturado la timbrada localmente por el puerto remoto
						datasource.close();
					}
				}
			}else if (comando.equals(YACSmartProperties.COM_BUZON_MENSAJES)){
				//El portero respondio por usted
				Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		    	Date date = new Date();
		    	
				EventoDataSource datasource = new EventoDataSource(getApplicationContext());
		        datasource.open();
		        Evento eventoBusqueda = new Evento();
		        eventoBusqueda.setId(idMensaje);
		        Evento evento = datasource.getEventoId(eventoBusqueda);
		        if(evento != null){
		        	//Si existe ese evento se actualiza a tipo buzon
					evento.setIdGrabado(extras.getString("idBuzon"));
					evento.setTipoEvento(TipoEventoEnum.BUZON.getCodigo());
					datasource.updateEvento(evento);
		        }else{
		        	//Si no existe el evento se crea
		        	evento = new Evento();
					evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() +": "+ origen);
					evento.setId(idMensaje);
					evento.setFecha(dateFormat.format(date));
					evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
					evento.setComando(comando);
					evento.setTipoEvento(TipoEventoEnum.BUZON.getCodigo());
					evento.setIdEquipo(equipo.getId());
					evento.setMensaje(mensaje);
					evento.setIdGrabado(extras.getString("idBuzon"));
					datasource.createEvento(evento);

		        }
				datasource.close();
				

				
				NotificationCompat.Builder mBuilder =
		        		new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.y4homeb)
		        .setContentTitle(origen)
		        .setStyle(new NotificationCompat.BigTextStyle()
		        .bigText(mensaje))
		        .setContentText(mensaje)
		        .setSound(soundUri)
		        .setAutoCancel(true);

				Intent intent = new Intent(this, SplashActivity.class);
				intent.putExtra("comando", comando);
				intent.setAction("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.LAUNCHER");

		        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		      
		        mBuilder.setContentIntent(contentIntent);
		        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

				SolicitarBuzonAsyncTask solicitarBuzonAsyncTask = new SolicitarBuzonAsyncTask(numeroSerie ,getApplicationContext(), idMensaje);
				solicitarBuzonAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			}
		}else if (comando.equals(YACSmartProperties.COM_ACTIVACION_SENSOR)){

			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();

			EventoDataSource datasource = new EventoDataSource(getApplicationContext());
			datasource.open();
			Evento eventoBusqueda = new Evento();
			eventoBusqueda.setId(idMensaje);
			Evento evento = datasource.getEventoId(eventoBusqueda);
			if(evento == null){

				if(!idMensaje.equals("1")) {
					//Si no existe el evento se crea
					evento = new Evento();
					evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + origen);
					evento.setId(idMensaje);
					evento.setFecha(dateFormat.format(date));
					evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
					evento.setComando(comando);
					evento.setTipoEvento(TipoEventoEnum.PUERTA.getCodigo());
					evento.setIdEquipo(equipo.getId());
					evento.setMensaje(mensaje);
					evento.setIdGrabado(extras.getString("idBuzon"));
					datasource.createEvento(evento);
					datasource.close();
				}
				NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(this)
								.setSmallIcon(R.drawable.y4homeb)
								.setContentTitle(origen)
								.setStyle(new NotificationCompat.BigTextStyle()
										.bigText(mensaje))
								.setContentText(mensaje)
								.setSound(soundUri)
								.setAutoCancel(true);

				Intent intent = new Intent(this, SplashActivity.class);
				intent.putExtra("comando", comando);
				intent.setAction("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.LAUNCHER");

				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
				String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
//				AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SOLICITAR_VIDEO_SENSOR + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipo.getNumeroSerie() + ";" + evento.getId() + ";");
//				AudioQueu.contadorComandoEnviado++;
			}


		}else if(comando.equals(YACSmartProperties.COM_MENSAJE_CHAT)){
			//Buscar dispositivo
			DispositivoDataSource dispositivoDataSource = new DispositivoDataSource(getApplicationContext());
			dispositivoDataSource.open();
			Dispositivo dispositivo = new Dispositivo();
			dispositivo.setId(idMensaje);
			dispositivo = dispositivoDataSource.getDispositivoId(dispositivo);
			dispositivoDataSource.close();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
			Date date = new Date();

			MensajeDataSource mensajeDataSource = new MensajeDataSource(getApplicationContext());
			mensajeDataSource.open();
			Mensaje mensajeRecibido = new Mensaje();
			mensajeRecibido.setTipo(YACSmartProperties.COM_MENSAJE_CHAT);
			mensajeRecibido.setEstado("REC");
			mensajeRecibido.setHora(hourFormat.format(date));
			mensajeRecibido.setFecha(dateFormat.format(date));
			mensajeRecibido.setIdDispositivo(idMensaje);
			mensajeRecibido.setMensaje(mensaje);
			mensajeRecibido.setId(UUID.randomUUID().toString());
			mensajeRecibido.setDireccion("REC");
			mensajeDataSource.createMensajeNew(mensajeRecibido);
			mensajeDataSource.close();

			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
							.setSmallIcon(R.drawable.y4homeb)
							.setContentTitle(origen)
							.setStyle(new NotificationCompat.BigTextStyle()
									.bigText(mensaje))
							.setContentText(mensaje)
							.setSound(soundUri)
							.setAutoCancel(true);

			Intent intent = new Intent(this, SplashActivity.class);
			intent.putExtra("comando", comando);
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


		}else if(comando.equals(YACSmartProperties.COM_BUSCAR_DISPOSITIVO)){
			Intent intentOpen = new Intent(this, BuscarTelefonoActivity.class);
			intentOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//Probar esta linea
			intentOpen.putExtra("buscar", comando);
			intentOpen.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
			startActivity(intentOpen);



		}else if(comando.equals(YACSmartProperties.COM_DISPOSITIVO_CONTESTO)){
			AudioQueu.llamadaEntrante = false;
			
			if(!AudioQueu.comunicacionAbierta){
				Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				NotificationCompat.Builder mBuilder =
		        		new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.y4homeb)
		        .setContentTitle(TipoEquipoEnum.PORTERO.getDescripcion() +": " + equipo.getNombreEquipo())
		        .setStyle(new NotificationCompat.BigTextStyle()
		        .bigText(mensaje))
		        .setContentText(extras.getString("nombreEquipo") + " se  ha conectado a su portero.")
		        .setSound(soundUri)
		        .setAutoCancel(true);
	
				Intent intent = new Intent(this, SplashActivity.class);
				intent.putExtra("comando", comando);
				intent.setAction("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.LAUNCHER");
	
		        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		      
		        mBuilder.setContentIntent(contentIntent);
		        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		        
			}
			
		}else if(comando.equals(YACSmartProperties.INT_INICIAR_LLAMADA)){
			//I01;NUMSERIE;PUERTOAUDIO;IPORIGEN
			Intent intent = new Intent(this, LlamadaEntranteActivity.class);
			intent.putExtra("comando", comando);
			intent.putExtra("numeroSerie", extras.getString("numeroSerie"));
			intent.putExtra("puertoAudio", extras.getString("puertoAudio"));
			intent.putExtra("ipOrigen", extras.getString("ip"));
			intent.putExtra("origen", extras.getString("origen"));
			intent.putExtra("video", extras.getString("video"));
			intent.putExtra("puertoVideo", extras.getString("puertoVideo"));
			intent.putExtra("esComunicacionDirecta", esComunicacionDirecta(extras.getString("ip")));
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
//		}else if(comando.equals(YACSmartProperties.COM_ACTUALIZAR_IP_CORP)){
//			if(validate(ip) && !ip.equals(YACSmartProperties.IP_CORP)) {
//				if (datosAplicacion.getCuenta() != null) {
//					datosAplicacion.getCuenta().setIp(ip);
//					CuentaDataSource cuentaDataSource = new CuentaDataSource(getApplicationContext());
//					cuentaDataSource.open();
//					cuentaDataSource.updateCuenta(datosAplicacion.getCuenta());
//					cuentaDataSource.close();
//					YACSmartProperties.IP_CORP = ip;
//					YACSmartProperties.actualizarURL();
//				} else {
//					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//					String ipCorp = sharedPrefs.getString("prefIpCorp", "").toString();
//					SharedPreferences.Editor editor = sharedPrefs.edit();
//					editor.putString("prefIpCorp", ip);
//					editor.apply();
//					editor.commit();
//					YACSmartProperties.IP_CORP = ip;
//					YACSmartProperties.actualizarURL();
//				}
//			}

		}else{
			//Cualquier otro mensaje
			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

			
			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.y4homeb)
			.setContentTitle(origen)
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(mensaje))
			.setContentText(mensaje)
			.setSound(soundUri)
			.setAutoCancel(true);

			Intent intent = new Intent(this, SplashActivity.class);
			intent.putExtra("comando", comando);
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");
			
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

		}
	}
	public static boolean validate(String ip){
		Pattern patternIp = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		Matcher matcher = patternIp.matcher(ip);
		return matcher.matches();

	}

	
	public Boolean esComunicacionDirecta(String ipDispositivoLlama) {
		String ipAddressPublic = null;
		Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if ( pattern.matcher(inetAddress.getHostAddress()).matches() && !inetAddress.getHostAddress().equals("127.0.0.1")) {
						ipAddressPublic = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		
		Boolean redLocal = true;
		String[] ipLlamada = ipDispositivoLlama.split("\\.");
		String[] miIp = ipAddressPublic.split("\\.");
		
		for (int i = 0; i < 3; i++){
			if(!ipLlamada[i].equalsIgnoreCase(miIp[i])){
				redLocal = false;
				
				break;
			}
		}
		return redLocal;
	}


	public Boolean esComunicacionDirecta() {
		DatagramSocket clientSocket = null;

		long tiempo;
		try {
			clientSocket = new DatagramSocket();
			DatosAplicacion datosAplicacion = (DatosAplicacion) getApplicationContext();
			Equipo equipo = datosAplicacion.getEquipoSeleccionado();
			InetAddress ipEquipo = null;
			Integer puertoComando = YACSmartProperties.PUERTO_COMANDO_DEFECTO;
			ipEquipo = InetAddress.getByName(equipo.getIpLocal());

			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

			String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

			String datosConfS = YACSmartProperties.COM_PING + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipo.getNumeroSerie() + ";" + ";";
			byte[] datosConfB = datosConfS.getBytes();
			byte[] datosComando = new byte[512];

			System.arraycopy(datosConfB, 0, datosComando, 0, datosConfB.length);

			DatagramPacket sendPacketConf = new DatagramPacket(datosComando,
					datosComando.length, ipEquipo,
					puertoComando);
			Date inicial = new Date();
			clientSocket.send(sendPacketConf);

			byte[] datos = new byte[512];
			DatagramPacket receivePacket = new DatagramPacket(datos,
					datos.length);

			clientSocket.setSoTimeout(3000);

			clientSocket.receive(receivePacket);
			Date final1 = new Date();
			tiempo = final1.getTime() - inicial.getTime();
			clientSocket.close();
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			clientSocket.close();
			return false;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			clientSocket.close();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			clientSocket.close();
			return false;
		}
	}
}
