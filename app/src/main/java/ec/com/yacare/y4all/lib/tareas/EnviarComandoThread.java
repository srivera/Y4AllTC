package ec.com.yacare.y4all.lib.tareas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.activities.respuesta.AdministrarRespuestasActivity;
import ec.com.yacare.y4all.activities.respuesta.NuevaRespuestaActivity;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.dto.Respuesta;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.sqllite.RespuestaDataSource;
import ec.com.yacare.y4all.lib.sqllite.ZonaDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class EnviarComandoThread extends Thread  {

	private Activity activity;
	private String comando;
	private Respuesta respuesta;
	private MonitorIOActivity monitorActivity;
	private AdministrarRespuestasActivity administrarRespuestasActivity;

	private String ipEquipoInternet;
	private Integer puertoComandoEnviar;
	private NuevaRespuestaActivity nuevaRespuestaActivity;

	private DatosAplicacion datosAplicacion;

	public EnviarComandoThread(Activity activity, String comando, Respuesta respuesta,  MonitorIOActivity monitorActivity, AdministrarRespuestasActivity administrarRespuestasActivity, String ipEquipoInternet,
							   Integer puertoComandoEnviar, NuevaRespuestaActivity nuevaRespuestaActivity) {
		super();
		this.activity = activity;
		this.comando = comando;
		this.respuesta = respuesta;
		this.monitorActivity = monitorActivity;
		this.administrarRespuestasActivity = administrarRespuestasActivity;
		this.ipEquipoInternet = ipEquipoInternet;
		this.puertoComandoEnviar = puertoComandoEnviar;
		this.nuevaRespuestaActivity = nuevaRespuestaActivity;
	}

	String comandoInicial;
	boolean vaciar = false;

	byte[] video;
	DatagramPacket receivePacketA;

	@Override
	public void run() {

		try {
			datosAplicacion = (DatosAplicacion)activity.getApplicationContext();
			InetAddress ipEquipo;
			ipEquipo = InetAddress.getByName(datosAplicacion.getEquipoSeleccionado().getIpLocal());

			DatagramSocket clientSocket = new DatagramSocket();
			
			String datosConfS = comando + ";" ;

			byte[] datosConfB = datosConfS.getBytes();
			byte[] datosComando = new byte[512];
			
			System.arraycopy(datosConfB, 0, datosComando, 0, datosConfB.length);

			DatagramPacket sendPacketConf = new DatagramPacket(datosComando,
					datosComando.length, ipEquipo,
					puertoComandoEnviar);
			clientSocket.send(sendPacketConf);
			Log.d("comando / tamano", "comando  " + datosConfS +"/"+ datosComando.length);
			
			comandoInicial = comando.substring(0, 3);
			Integer TAMANO_BUFFER_AUDIO = 1024 * Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("tamano.paquete.audio"));
			Integer TAMANO_BUFFER_FOTO = 1024 * 200;
			
			if(comandoInicial.equals(YACSmartProperties.COM_INICIAR_HABLAR)){
				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(2000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					if(monitorActivity != null){
						monitorActivity.imagenInicial.post(new Runnable() {
							@Override
							public void run() {
								monitorActivity.verificarResultadoHablarudp(comandoInicial, true);
							}
						});
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					if(monitorActivity != null){
						monitorActivity.imagenInicial.post(new Runnable() {
							@Override
							public void run() {
								monitorActivity.verificarResultadoHablarudp(comandoInicial, false);
							}
						});
					}
				}

			}else if(comandoInicial.equals(YACSmartProperties.COM_CONFIGURAR_PARAMETROS)){

				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(8000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					EquipoDataSource dataSource = new EquipoDataSource(datosAplicacion);
					dataSource.open();
					String valores[] = comando.split(";");
					Equipo equipoBusqueda = new Equipo();
					equipoBusqueda.setNumeroSerie(valores[3]);
					equipoBusqueda = dataSource.getEquipoNumSerie(equipoBusqueda);
					if(equipoBusqueda != null && equipoBusqueda.getId() != null) {
						///equipoBusqueda.setLuz(valores[7]);
						//equipoBusqueda.setLuzWifi(valores[8]);
						equipoBusqueda.setPuerta(valores[4]);
						equipoBusqueda.setSensor(valores[6]);
						equipoBusqueda.setMensajeInicial(valores[7]);
						equipoBusqueda.setPuertoActivo(valores[9]);
						equipoBusqueda.setTimbreExterno(valores[13]);

						//if(valores.length > 15) {
							equipoBusqueda.setMensajeApertura(valores[14]);
						//}
						//if(recibido.length > 16) {
							equipoBusqueda.setMensajePuerta(valores[15]);
						//}
						equipoBusqueda.setVolumen(Integer.valueOf(valores[18]));
						//equipoBusqueda.setTiempoEncendidoLuz(Integer.valueOf(valores[19]));

						if(datosAplicacion.getEquipoSeleccionado() != null && datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(equipoBusqueda.getNumeroSerie())){
							datosAplicacion.setEquipoSeleccionado(equipoBusqueda);
						}
					}
					dataSource.updateEquipo(equipoBusqueda);
					dataSource.close();
					datosAplicacion.getY4HomeActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									datosAplicacion.getY4HomeActivity());
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
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					datosAplicacion.getY4HomeActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									datosAplicacion.getY4HomeActivity());
							alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
									.setMessage("Error al actualizar la configuración. Intente en un momento")
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

			}else if(comandoInicial.equals(YACSmartProperties.COM_CONFIGURAR_PARAMETROS_EDIFICIO)){

				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(8000);
					clientSocket.receive(receivePacket);
					clientSocket.close();

					datosAplicacion.getY4HomeActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									datosAplicacion.getY4HomeActivity());
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
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					datosAplicacion.getY4HomeActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									datosAplicacion.getY4HomeActivity());
							alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
									.setMessage("Error al actualizar la configuración. Intente en un momento")
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
			}else if(comandoInicial.equals(YACSmartProperties.HOTSPOT_MODO_HOTSPOT)){

				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(2000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					EquipoDataSource dataSource = new EquipoDataSource(datosAplicacion);
					dataSource.open();
					String valores[] = comando.split(";");
					Equipo equipoBusqueda = new Equipo();
					equipoBusqueda.setNumeroSerie(valores[3]);
					equipoBusqueda = dataSource.getEquipoNumSerie(equipoBusqueda);
					if(equipoBusqueda != null && equipoBusqueda.getId() != null) {
						equipoBusqueda.setModo(YACSmartProperties.MODO_AP);
						if(datosAplicacion.getEquipoSeleccionado() != null && datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(equipoBusqueda.getNumeroSerie())){
							datosAplicacion.setEquipoSeleccionado(equipoBusqueda);
						}
						dataSource.updateEquipo(equipoBusqueda);
					}
					dataSource.close();
					datosAplicacion.getY4HomeActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									datosAplicacion.getY4HomeActivity());
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
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					datosAplicacion.getY4HomeActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									datosAplicacion.getY4HomeActivity());
							alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
									.setMessage("Error al actualizar la configuración. Intente en un momento")
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

			}else if(comandoInicial.equals(YACSmartProperties.COM_ACTIVAR_ZONA_TIMBRE)){

				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(2000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					String valores[] = comando.split(";");
					ZonaDataSource zonaDataSource = new ZonaDataSource(datosAplicacion);
					zonaDataSource.open();
					ZonaLuces zonaLuces = zonaDataSource.getZonaRouterId(valores[4], valores[5]);
					zonaLuces.setEncenderTimbre(valores[6]);
					zonaDataSource.updateZona(zonaLuces);
					zonaDataSource.close();

					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									activity);
							alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
									.setMessage("La zona fue actualizada")
									.setCancelable(false)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {

										}
									});
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
						}
					});
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									activity);
							alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
									.setMessage("Error al actualizar la zona. Intente en un momento")
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


			} else if (comandoInicial.equals(YACSmartProperties.COM_TERMINAR_HABLAR)){
				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(2000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					if(monitorActivity != null){
						monitorActivity.imagenInicial.post(new Runnable() {
							@Override
							public void run() {
								monitorActivity.verificarResultadoHablarudp(comandoInicial, true);
							}
						});
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
//					return comandoInicial + "false";
					if(monitorActivity != null){
						monitorActivity.imagenInicial.post(new Runnable() {
							@Override
							public void run() {
								monitorActivity.verificarResultadoHablarudp(comandoInicial, false);
							}
						});
					}
				}
			} else if (comandoInicial.equals(YACSmartProperties.COM_REPRODUCIR_TEXTO)){
				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(2000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					if(activity instanceof Y4HomeActivity){
						((Y4HomeActivity) activity).mostrarMensaje("Su mensaje fue enviado");
					}

				} catch (SocketTimeoutException e) {
					e.printStackTrace();

				}
			} else if (comandoInicial.equals(YACSmartProperties.COM_SOLICITAR_FOTO_INICIAL)){
				byte[] recibido = new byte[1024 * 64];
				String[] com = comando.split(";");
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(2000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					AudioQueu.imagenRecibida = true;
					AudioQueu.fotoTimbre = receivePacket.getData();
					FileOutputStream fileOuputStream;
					try {
						fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + com[4] + ".jpg");
						fileOuputStream.write(receivePacket.getData());
						fileOuputStream.close();
						AudioQueu.imagenRecibida = true;
						EventoDataSource datasource = new EventoDataSource(activity.getApplicationContext());
						datasource.open();
						//Verificar si el id existe
						Evento eventoBusqueda = new Evento();
						eventoBusqueda.setId( com[4]);
						Evento evento = datasource.getEventoId(eventoBusqueda);
						if (evento != null && evento.getId() != null) {
							evento.setMensaje("S");
							datasource.updateEvento(evento);
						}
						datasource.close();
						Log.d("TimbrarAsyncTask", "8");
					} catch (FileNotFoundException e) {
						AudioQueu.buscandoFoto = false;
						e.printStackTrace();
					} catch (IOException e) {
						AudioQueu.buscandoFoto = false;
						e.printStackTrace();
					}

				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
				}


			}else if(comandoInicial.equals(YACSmartProperties.COM_ENCENDER_LUZ)){
				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(2000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					if(monitorActivity != null){
						monitorActivity.imagenInicial.post(new Runnable() {
							@Override
							public void run() {
								monitorActivity.verificarResultadoComando(comandoInicial, true);
							}
						});
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					if(monitorActivity != null){
						monitorActivity.imagenInicial.post(new Runnable() {
							@Override
							public void run() {
								monitorActivity.verificarResultadoComando(comandoInicial, false);
							}
						});
					}
				}

			}else if(comandoInicial.equals(YACSmartProperties.COM_ELIMINAR_RESPUESTA)){
				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(4000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					if(administrarRespuestasActivity != null){
						administrarRespuestasActivity.mListView.post(new Runnable() {
							@Override
							public void run() {
								administrarRespuestasActivity.verificarEliminarRespuesta(comandoInicial, true);
							}
						});
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					//return comandoInicial + "false";
					if(administrarRespuestasActivity != null){
						administrarRespuestasActivity.mListView.post(new Runnable() {
							@Override
							public void run() {
								administrarRespuestasActivity.verificarEliminarRespuesta(comandoInicial, false);
							}
						});
					}
				}

			}else if(comandoInicial.equals(YACSmartProperties.LISTAR_RESPUESTAS)){
				//Grabar respuestas
				byte[] recibido = new byte[1024 * 4];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(8000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					String[] resultado = new String(receivePacket.getData()).split(";");
					Log.d("resultado", "respuestas:" + resultado);
					String[] respuestas = resultado[1].split(":");
					RespuestaDataSource respuestaDataSource = new RespuestaDataSource(activity);
					respuestaDataSource.open();

					for(int i = 0; i < respuestas.length - 1; i++){
						String[] respuesta = respuestas[i].split("/");
						respuestaDataSource.deleteRespuesta(respuesta[0]);
						Respuesta respuesta1 = respuestaDataSource.getRespuestaId(respuesta[0]);
						if(respuesta1 == null){
							respuesta1 = new Respuesta();
							respuesta1.setId(respuesta[0]);
							respuesta1.setNombre(respuesta[1]);
							respuesta1.setTipo(respuesta[2]);
							respuesta1.setTipoVoz(respuesta[3]);
							respuesta1.setIdEquipo(datosAplicacion.getEquipoSeleccionado().getId());
							respuestaDataSource.createRespuesta(respuesta1);
						}

					}
					respuestaDataSource.close();
					if(administrarRespuestasActivity != null){
						administrarRespuestasActivity.verificarListarRespuestas();
					}

				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
				}
			}else if(comandoInicial.equals(YACSmartProperties.COM_CONFIGURAR_BUZON)){
				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(4000);
					clientSocket.receive(receivePacket);
					clientSocket.close();

					String valores[] = comando.split(";");
					Equipo equipo = datosAplicacion.getEquipoSeleccionado();
					equipo.setBuzon(valores[2]);
					EquipoDataSource equipoDataSource = new EquipoDataSource(activity.getApplicationContext());
					equipoDataSource.open();
					equipoDataSource.updateEquipo(equipo);
					equipoDataSource.close();

					if(datosAplicacion.getY4HomeActivity() != null) {
						datosAplicacion.getY4HomeActivity().actualizarBuzon(true);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					//return comandoInicial + "false";
					if(datosAplicacion.getY4HomeActivity() != null) {
						datosAplicacion.getY4HomeActivity().actualizarBuzon(false);
					}
				}
			}else if( comandoInicial.startsWith(YACSmartProperties.COM_BUZON_MENSAJES) ||
					comandoInicial.startsWith(YACSmartProperties.COM_SOLICITAR_VIDEO_INICIAL) ||
					comandoInicial.startsWith(YACSmartProperties.COM_SOLICITAR_VIDEO_SENSOR)){

				String valores[] = comando.split(";");
				byte[] recibido = new byte[256];
				DatagramPacket receivePacket = new DatagramPacket(recibido,
						recibido.length);
				clientSocket.setSoTimeout(6000);
				clientSocket.receive(receivePacket);
				String[] valRec = (new String(recibido)).split(";");


				if(valRec.length > 4) {
					Log.d("Sensor", "valRec " +valRec.length + " / " + valRec[0] + " / " + valRec[5]);
					if (!valRec[5].equals("ERR")) {
						Log.d("Sensor", "total " + Integer.parseInt(valRec[6]) + " / " + valRec[5]);

						int contador = 0;
						ArrayList<byte[]> archivo = new ArrayList<byte[]>();
						while (contador < Integer.parseInt(valRec[6])) {
							video = new byte[1024 * 60];
							receivePacketA = new DatagramPacket(video,
									video.length);
							try {
								clientSocket.setSoTimeout(5000);
								clientSocket.receive(receivePacketA);
								archivo.add(video);
//								outputStream.write(video, 0, receivePacketA.getLength());
								Log.d("Sensor", "indice  " + contador + " / " + video.length + " / " + receivePacketA.getLength());
								contador = contador + 1;
							}catch(SocketTimeoutException e){
								e.printStackTrace();
//								clientSocket.close();
								Log.d("Sensor", "fin  ");
								break;
							}

						}
						clientSocket.close();
						if(contador == Integer.parseInt(valRec[6])) {
							FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + valRec[5]);
							BufferedOutputStream outputStream = new BufferedOutputStream(fos);
							for (int i = 0; i < archivo.size(); i++) {
								outputStream.write(archivo.get(i));
							}
							outputStream.close();

						}
						Log.d("Sensor", "fin  ");

					} else {
						//No hay archivo
						Evento eventoBusqueda = new Evento();
						eventoBusqueda.setId(valores[4]);
						EventoDataSource eventoDataSource = new EventoDataSource(activity);
						eventoDataSource.open();
						eventoBusqueda = eventoDataSource.getEventoId(eventoBusqueda);
						if (eventoBusqueda != null) {
							eventoBusqueda.setEstado("ERR");
							eventoDataSource.updateEvento(eventoBusqueda);
						}
						eventoDataSource.close();
						clientSocket.close();
					}
					if (datosAplicacion.getEventosActivity() != null) {
						datosAplicacion.getEventosActivity().actualizarLista();
					} else if (datosAplicacion.getY4HomeActivity() != null) {
						datosAplicacion.getY4HomeActivity().actualizarLista();
					}
				}
			}else if(comandoInicial.equals(YACSmartProperties.ADM_RECIBIR_AUDIO_PREDEFINIDO)){
				int i = 0;
				ByteArrayOutputStream audio = new ByteArrayOutputStream();
				
				while (i < respuesta.getAudioQueu().size() ) {
					byte[] datos = respuesta.getAudioQueu().get(i);
					DatagramPacket sendPacket = new DatagramPacket(datos,
									datos.length, ipEquipo,
							puertoComandoEnviar);
					audio.write(respuesta.getAudioQueu().get(i), 0,
							respuesta.getAudioQueu().get(i).length);
					try {
						clientSocket.send(sendPacket);
						Thread.sleep(100);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++;
				}
				audio.close();
				
				datosConfS = YACSmartProperties. ADM_RECIBIR_AUDIO_TOTAL_PAQUETES + ";" + respuesta.getId() + ";" + respuesta.getTipo() + ";"
						+ respuesta.getNombre() + ";" + i + ";";
				Log.d("comando", "comando  " + datosConfS);
				datosConfB = datosConfS.getBytes();

				sendPacketConf = new DatagramPacket(datosConfB,
						datosConfB.length, ipEquipo,
						puertoComandoEnviar);
				clientSocket.send(sendPacketConf);

				//Se espera la respuesta
				byte[] paquetesRecibidos = new byte[TAMANO_BUFFER_AUDIO];
				DatagramPacket receivePacket = new DatagramPacket(paquetesRecibidos,
						paquetesRecibidos.length);
				try {
					clientSocket.setSoTimeout(2000);
					clientSocket.receive(receivePacket);
					if(nuevaRespuestaActivity != null){
						nuevaRespuestaActivity.editNombreDispositivoReg.post(new Runnable() {
							@Override
							public void run() {
								nuevaRespuestaActivity.mostrarResultado(true);
							}
						});
					}
				} catch (SocketTimeoutException e){
					if(nuevaRespuestaActivity != null){
						nuevaRespuestaActivity.editNombreDispositivoReg.post(new Runnable() {
							@Override
							public void run() {
								nuevaRespuestaActivity.mostrarResultado(false);
							}
						});
					}
				}
			}else if(comandoInicial.startsWith(YACSmartProperties.COM_TOMAR_FOTO )){
				String[] com = comando.split(";");
				if(datosAplicacion.getEquipoSeleccionado() != null
						&& datosAplicacion.getEquipoSeleccionado().getNumeroSerie().equals(com[3])
						&& !AudioQueu.comunicacionAbierta && datosAplicacion.getY4HomeActivity() != null){
					try{
						byte[] recibido = new byte[1024 * 64];
						DatagramPacket receivePacket = new DatagramPacket(recibido,
								recibido.length);
						clientSocket.setSoTimeout(4000);
						clientSocket.receive(receivePacket);
						clientSocket.close();
						datosAplicacion.getY4HomeActivity().actualizarPager(recibido);
						} catch (SocketTimeoutException e) {
						e.printStackTrace();
						clientSocket.close();
					}
				}
			}else if(comandoInicial.equals(YACSmartProperties.COM_ABRIR_PUERTA_UDP)){
				byte[] recibido = new byte[256];
				try{
					DatagramPacket receivePacket = new DatagramPacket(recibido,
							recibido.length);
					clientSocket.setSoTimeout(8000);
					clientSocket.receive(receivePacket);
					clientSocket.close();
					String[] resultado = new String(receivePacket.getData()).split(";");
					Log.d("resultado", "respuestas:" + resultado);
					if(activity instanceof Y4HomeActivity){
						if(resultado[2].equals("ERR")){
							((Y4HomeActivity) activity).mostrarMensaje(resultado[3]);
						}else{
							((Y4HomeActivity) activity).mostrarMensaje("Puerta activada");
						}

					}

				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					clientSocket.close();
					if(activity instanceof Y4HomeActivity){
						((Y4HomeActivity) activity).mostrarMensaje("Intente nuevamente");
					}

				}
			}
			clientSocket.close();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * Obtiene el array de bytes descomprimido a partir de otro array de bytes
	 * comprimido
	 * 
	 * @param file
	 *            los datos comprimidos
	 * @return los datos descomprimidos.
	 * @throws IOException
	 *             de vez en cuando
	 */
	public byte[] descomprimirGZIP(byte[] file, Integer paquete)  {
		ByteArrayInputStream gzdata = new ByteArrayInputStream(file);
		GZIPInputStream gunzipper;
		try {
			gunzipper = new GZIPInputStream(gzdata, file.length);
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			byte[] readed = new byte[paquete];
			int actual = 1;
			while ((actual = gunzipper.read(readed)) > 0) {
				data.write(readed, 0, actual);
			}
			gzdata.close();
			gunzipper.close();
			byte[] returndata = data.toByteArray();
			return returndata;
		} catch (IOException e) {
		}
		return new byte[paquete];
	}

}
