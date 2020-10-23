package ec.com.yacare.y4all.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class ConfiguracionArrayAdapter extends ArrayAdapter<String> {
	private final Y4HomeActivity context;
	private final String[] values;
	private DatosAplicacion datosAplicacion;
	Button btnEncenderLuz;
	String nombreDispositivo;
	public ConfiguracionArrayAdapter(Y4HomeActivity context,  String[] values) {
		super(context, R.layout.respuesta_list, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		datosAplicacion = (DatosAplicacion) context.getApplicationContext();
		String s[] =values[position].split(":");

		Typeface fontRegular = Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
		Typeface fontLight = Typeface.createFromAsset(context.getAssets(), "Lato-Light.ttf");
		View rowView;
		rowView = inflater.inflate(R.layout.configuracion_list, parent, false);
		TextView titulo = (TextView) rowView.findViewById(R.id.nombreOpcion);
		TextView descripcion = (TextView) rowView.findViewById(R.id.descripcionOpcion);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		if(s[0].equals("0")) {
			if (!context.isScreenLarge() || (context.orientacionPortrait != null && context.orientacionPortrait)) {
				rowView = inflater.inflate(R.layout.configuracion_list_botones, parent, false);
				LinearLayout filaBotones = (LinearLayout) rowView.findViewById(R.id.filaBotones);
				Button btnAbrirPuerta = (Button) rowView.findViewById(R.id.btnAbrirPuerta);
				btnEncenderLuz = (Button) rowView.findViewById(R.id.btnEncenderLuz);
				ImageButton btnMonitorear = (ImageButton) rowView.findViewById(R.id.btnMonitorear);
				btnMonitorear.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("click ingreso", "click ");
						context.monitorear();
					}
				});

				if (context.estadoWifi.equals("0")) {
					filaBotones.setBackgroundColor(getContext().getResources().getColor(R.color.rojoAlfa));
				} else if (context.estadoWifi.equals("1")) {
					filaBotones.setBackgroundColor(getContext().getResources().getColor(R.color.rojoAlfa));
				} else if (context.estadoWifi.equals("2")) {
					filaBotones.setBackgroundColor(getContext().getResources().getColor(R.color.amarilloAlfa));
				} else if (context.estadoWifi.equals("3")) {
					filaBotones.setBackgroundColor(getContext().getResources().getColor(R.color.verdeAlfa));
				} else if (context.estadoWifi.equals("4")) {
					filaBotones.setBackgroundColor(getContext().getResources().getColor(R.color.verdeAlfa));
				} else {
					filaBotones.setBackgroundColor(getContext().getResources().getColor(R.color.principalAlfa));
				}
				if (context.equipoSeleccionado.getLuzWifi() != null && !context.equipoSeleccionado.getLuzWifi().equals("1")) {
					btnEncenderLuz.setEnabled(false);
				}
				if (context.equipoSeleccionado.getPuerta() != null && !context.equipoSeleccionado.getPuerta().equals("1")) {
					btnAbrirPuerta.setEnabled(false);

				}
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
				nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
				if (btnAbrirPuerta != null) {
					if(datosAplicacion.getEquipoSeleccionado().getTipoPortero()!= null && datosAplicacion.getEquipoSeleccionado().getTipoPortero() != null && !datosAplicacion.getEquipoSeleccionado().getTipoPortero().equals(YACSmartProperties.PORTERO_EDIFICIO)) {
						btnAbrirPuerta.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
									abrirPuertaWifi();
								} else {
									//Internet
									abrirPuertaInternet();
								}
							}
						});
					}
					if(datosAplicacion.getEquipoSeleccionado().getTipoPortero() != null && datosAplicacion.getEquipoSeleccionado().getTipoPortero().equals(YACSmartProperties.PORTERO_EDIFICIO)) {
						btnAbrirPuerta.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								//Internet
								new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
										.setTitleText(YACSmartProperties.intance.getMessageForKey("abrir.puerta"))
										.setContentText(YACSmartProperties.intance.getMessageForKey("abrir.puerta.subtitulo"))
										.setCancelText("NO")
										.setConfirmText("SI")
										.showCancelButton(true)

										.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
											@Override
											public void onClick(SweetAlertDialog sDialog) {
												sDialog.cancel();

											}
										})
										.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
											@Override
											public void onClick(SweetAlertDialog sDialog) {
												sDialog.cancel();
												AlertDialog.Builder alert = new AlertDialog.Builder(context);
												alert.setTitle("Ingrese su clave");
												final EditText input = new EditText(context);
												input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
												input.setRawInputType(Configuration.KEYBOARD_12KEY);
												alert.setView(input);
												alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int whichButton) {

														SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
														dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
														String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date()) + ";",
																datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
														String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_EDIFICIO
																+ ";" + nombreDispositivo + ";" + cadenaEnc + ";"
																+ YACSmartProperties.Encriptar(input.getText().toString(), context.equipoSeleccionado.getNumeroSerie()) + ";" + context.equipoSeleccionado.getNumeroDepartamento() + ";" ;

														AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
																datosConfT);
														AudioQueu.contadorComandoEnviado++;

													}
												});
												alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int whichButton) {
														//Put actions for CANCEL button here, or leave in blank
													}
												});
												alert.show();

											}
										})
										.show();
							}

						});
					}
				}
				if (btnEncenderLuz != null) {

					btnEncenderLuz.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (AudioQueu.encenderLuz) {
								AudioQueu.encenderLuz = false;
								btnEncenderLuz.setText("apagar luz");
								String datosConfT = YACSmartProperties.COM_ENCENDER_LUZ_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + "-1" + ";";
								if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
									EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null,
											null, null, context.equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
									enviarComandoThread.start();
								} else {
									AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
											datosConfT);
									AudioQueu.contadorComandoEnviado++;
								}
							} else {
								AudioQueu.encenderLuz = true;
								btnEncenderLuz.setText("encender luz");
								String datosConfT = YACSmartProperties.COM_APAGAR_LUZ_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + "-1" + ";";
								if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
									EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null,
											null, null, context.equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
									enviarComandoThread.start();
								} else {
									AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
											datosConfT);
									AudioQueu.contadorComandoEnviado++;
								}
							}
						}
					});
				}


			} else {
				rowView = inflater.inflate(R.layout.configuracion_list_vacio, parent, false);
			}
		}else if(s[0].equals("7")) {
			//Recomienda
			titulo.setText(s[1]);
			titulo.setTypeface(fontRegular);
			descripcion.setText(s[2]);
			descripcion.setTypeface(fontLight);
			//System.out.println(s);
			imageView.setImageResource(R.drawable.botonlista);
		}else if(s[0].equals("4")) {
			//Recomienda
			titulo.setText(s[1]);
			titulo.setTypeface(fontRegular);
			descripcion.setText(s[2]);
			descripcion.setTypeface(fontLight);
			//System.out.println(s);
			imageView.setImageResource(R.drawable.botonlista);

		}else if(s[0].equals("6")) {
			//acerca de
			titulo.setText(s[1]);
			titulo.setTypeface(fontRegular);
			descripcion.setText(s[2]);
			descripcion.setTypeface(fontLight);
			//System.out.println(s);
			imageView.setImageResource(R.drawable.botonlista);


		}else{
//			rowView = inflater.inflate(R.layout.configuracion_list, parent, false);
//			TextView titulo = (TextView) rowView.findViewById(R.id.nombreOpcion);
//			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//			ToggleButton toggleActivo = (ToggleButton) rowView.findViewById(R.id.toggleActivo);
//
//			if(s[0].equals("2")){
//				toggleActivo.setVisibility(View.VISIBLE);
//
//				if(datosAplicacion.getEquipoSeleccionado().getBuzon() != null && datosAplicacion.getEquipoSeleccionado().getBuzon().equals("1")){
//					toggleActivo.setChecked(true);
//				}
//			}
//			titulo.setText(s[1]);
//			titulo.setTypeface(fontRegular);
//
//			//System.out.println(s);
//			imageView.setImageResource(R.drawable.botonlista);
//
//			toggleActivo.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//
//					String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
//
//					String datosConfT = YACSmartProperties.COM_CONFIGURAR_BUZON + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
//
//					if(AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
//						if (datosAplicacion.getEquipoSeleccionado().getBuzon() != null && datosAplicacion.getEquipoSeleccionado().getBuzon().equals("1")) {
//							EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT + "0" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), null,
//									null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
//							enviarComandoThread.start();
//						} else {
//							EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT + "1" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), null,
//									null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
//							enviarComandoThread.start();
//						}
//					}else{
//						if (datosAplicacion.getEquipoSeleccionado().getBuzon() != null && datosAplicacion.getEquipoSeleccionado().getBuzon().equals("1")) {
//							AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT + "0" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";");
//							AudioQueu.contadorComandoEnviado++;
//						} else {
//							AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT + "1" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";");
//							AudioQueu.contadorComandoEnviado++;
//						}
//					}
//				}
//			});
		}


		return rowView;
	}

	private void abrirPuertaInternet() {
		if(Integer.valueOf(datosAplicacion.getEquipoSeleccionado().getTimbreExterno())  <= 2) {
			new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
					.setTitleText(YACSmartProperties.intance.getMessageForKey("abrir.puerta"))
					.setContentText(YACSmartProperties.intance.getMessageForKey("abrir.puerta.subtitulo"))
					.setCancelText("NO")
					.setConfirmText("SI")
					.showCancelButton(true)
					.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							sDialog.cancel();

						}
					})
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							sDialog.cancel();
							AlertDialog.Builder alert = new AlertDialog.Builder(context);
							alert.setTitle("Ingrese su clave");
							final EditText input = new EditText(context);
							input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
							input.setRawInputType(Configuration.KEYBOARD_12KEY);
							alert.setView(input);
							alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {

									SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
									String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date()) + ";",
											datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
									String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA
											+ ";" + nombreDispositivo + ";" + cadenaEnc + ";"
											+ YACSmartProperties.Encriptar(input.getText().toString(), context.equipoSeleccionado.getNumeroSerie()) + ";";

									AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
											datosConfT);
									AudioQueu.contadorComandoEnviado++;

								}
							});
							alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									//Put actions for CANCEL button here, or leave in blank
								}
							});
							alert.show();

						}
					})
					.show();
			}else{
				//2 puertas
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Abrir Puerta");
				builder.setMessage("Seleccione la puerta que desea abrir o presione cancelar?");
				// add the buttons
				builder.setPositiveButton("Puerta Principal", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						AlertDialog.Builder alert = new AlertDialog.Builder(context);
						alert.setTitle("Ingrese su clave");
						final EditText input = new EditText(context);
						input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						input.setRawInputType(Configuration.KEYBOARD_12KEY);
						alert.setView(input);
						alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

								SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
								String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date()) + ";",
										datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
								String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA
										+ ";" + nombreDispositivo + ";" + cadenaEnc + ";"
										+ YACSmartProperties.Encriptar(input.getText().toString(), context.equipoSeleccionado.getNumeroSerie()) + ";";

								AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
										datosConfT);
								AudioQueu.contadorComandoEnviado++;

							}
						});
						alert.show();
					}
				});
				builder.setNegativeButton("Puerta Secundaria", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						AlertDialog.Builder alert = new AlertDialog.Builder(context);
						alert.setTitle("Ingrese su clave");
						final EditText input = new EditText(context);
						input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						input.setRawInputType(Configuration.KEYBOARD_12KEY);
						alert.setView(input);
						alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

								SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
								String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date()) + ";",
										datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
								String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_OPCIONAL
										+ ";" + nombreDispositivo + ";" + cadenaEnc + ";"
										+ YACSmartProperties.Encriptar(input.getText().toString(), context.equipoSeleccionado.getNumeroSerie()) + ";";

								AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
										datosConfT);
								AudioQueu.contadorComandoEnviado++;

							}
						});
						alert.show();
					}
				});
				builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				// create and show the alert dialog
				AlertDialog dialog = builder.create();
				dialog.show();
			}
	}

	private void abrirPuertaWifi() {
		if(Integer.valueOf(datosAplicacion.getEquipoSeleccionado().getTimbreExterno())  <= 2) {
		//Wifi
			new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
				.setTitleText(YACSmartProperties.intance.getMessageForKey("abrir.puerta"))
				.setContentText(YACSmartProperties.intance.getMessageForKey("abrir.puerta.subtitulo"))
				.setCancelText("NO")
				.setConfirmText("SI")
				.showCancelButton(true)
				.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {
						sDialog.cancel();

					}
				})
				.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {
						SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
						String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date()) + ";",
								datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
						String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_UDP
								+ ";" + nombreDispositivo + ";" + cadenaEnc + ";" + UUID.randomUUID().toString();

						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null, null,
								null, context.equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();

						sDialog.cancel();

					}
				})
				.show();
		}else{
			//2 puertas
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Abrir Puerta");
			builder.setMessage("Seleccione la puerta que desea abrir o presione cancelar?");
			// add the buttons
			builder.setPositiveButton("Puerta Principal", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do something like...
					SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
					String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + dateFormatGmt.format(new Date()) + ";",
							datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
					String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_UDP
							+ ";" + nombreDispositivo + ";" + cadenaEnc + ";";

					EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null, null,
							null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					enviarComandoThread.start();

					dialog.cancel();
				}
			});
			builder.setNegativeButton("Puerta Secundaria", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do something like...
					SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
					String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + dateFormatGmt.format(new Date()) + ";",
							datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
					String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_OPCIONAL_UDP
							+ ";" + nombreDispositivo + ";" + cadenaEnc + ";";

					EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null, null,
							null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					enviarComandoThread.start();

					dialog.cancel();
				}
			});
			builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			// create and show the alert dialog
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
}