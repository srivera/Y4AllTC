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
import android.widget.ToggleButton;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;

public class ConfiguracionArrayAdapter extends ArrayAdapter<String> {
	private final Y4HomeActivity context;
	private final String[] values;
	private DatosAplicacion datosAplicacion;

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
		View rowView;

		if(s[0].equals("0")) {
			if (!context.isScreenLarge() || (context.orientacionPortrait != null && context.orientacionPortrait)){
				rowView = inflater.inflate(R.layout.configuracion_list_botones, parent, false);
				LinearLayout filaBotones = (LinearLayout) rowView.findViewById(R.id.filaBotones);
				Button btnAbrirPuerta = (Button) rowView.findViewById(R.id.btnAbrirPuerta);
				Button btnEncenderLuz = (Button) rowView.findViewById(R.id.btnEncenderLuz);
				ImageButton btnMonitorear = (ImageButton) rowView.findViewById(R.id.btnMonitorear);
				btnMonitorear.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("click ingreso", "click " );
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
				}else{
					filaBotones.setBackgroundColor(getContext().getResources().getColor(R.color.principalAlfa));
				}

				if(btnAbrirPuerta != null){
					btnAbrirPuerta.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
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
//												String datosConfT = COM_ABRIR_PUERTA_UDP + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + "" + ";";
//												EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null, null,
//														null, context.equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
//												enviarComandoThread.start();
												AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
														YACSmartProperties.COM_ABRIR_PUERTA_UDP + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" );
												AudioQueu.contadorComandoEnviado++;
												sDialog.cancel();

											}
										})
										.show();
							}else{
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
												input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_TEXT_VARIATION_PASSWORD);
												input.setRawInputType(Configuration.KEYBOARD_12KEY);
												alert.setView(input);
												alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int whichButton) {
														AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
																YACSmartProperties.COM_ABRIR_PUERTA + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + YACSmartProperties.Encriptar(input.getText().toString()) + ";");
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
//							new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//									.setTitleText(YACSmartProperties.intance.getMessageForKey("abrir.puerta"))
//									.setContentText(YACSmartProperties.intance.getMessageForKey("abrir.puerta.subtitulo"))
//									.setCancelText("NO")
//									.setConfirmText("SI")
//									.showCancelButton(true)
//									.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//										@Override
//										public void onClick(SweetAlertDialog sDialog) {
//											sDialog.cancel();
//
//										}
//									})
//									.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//										@Override
//										public void onClick(SweetAlertDialog sDialog) {
//											String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_UDP + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + "" + ";";
//											EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null, null,
//													null, context.equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
//											enviarComandoThread.start();
//											sDialog.cancel();
//										}
//									})
//									.show();


//							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//									context);
//							alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.confirmar"))
//									.setMessage(YACSmartProperties.intance.getMessageForKey("abrir.puerta"))
//									.setCancelable(false)
//									.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//										public void onClick(DialogInterface dialog, int which) {
//											dialog.cancel();
//
//										}
//									})
//									.setPositiveButton("SI", new DialogInterface.OnClickListener() {
//										public void onClick(DialogInterface dialog, int which) {
//											String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_UDP + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + "" + ";";
//											EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null, null,
//													null, context.equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
//											enviarComandoThread.start();
//
//										}
//									});
//
//							AlertDialog alertDialog = alertDialogBuilder.create();
//							alertDialog.show();

						}
					});
				}
				if(btnEncenderLuz != null){
					btnEncenderLuz.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String datosConfT = "C70" + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + context.equipoSeleccionado.getNumeroSerie() + ";" + "-1" + ";";
							if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
								EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT, null,
										null, null, context.equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
								enviarComandoThread.start();
							}
						}
					});
				}


			}else{
				rowView = inflater.inflate(R.layout.configuracion_list_vacio, parent, false);
			}
		}else{
			rowView = inflater.inflate(R.layout.configuracion_list, parent, false);
			TextView titulo = (TextView) rowView.findViewById(R.id.nombreOpcion);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
			ToggleButton toggleActivo = (ToggleButton) rowView.findViewById(R.id.toggleActivo);

			if(s[0].equals("2")){
				toggleActivo.setVisibility(View.VISIBLE);

				if(datosAplicacion.getEquipoSeleccionado().getBuzon() != null && datosAplicacion.getEquipoSeleccionado().getBuzon().equals("1")){
					toggleActivo.setChecked(true);
				}
			}
			titulo.setText(s[1]);
			titulo.setTypeface(fontRegular);

			//System.out.println(s);
			imageView.setImageResource(R.drawable.botonlista);

			toggleActivo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

					String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

					String datosConfT = YACSmartProperties.COM_CONFIGURAR_BUZON + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";

					if(AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
						if (datosAplicacion.getEquipoSeleccionado().getBuzon() != null && datosAplicacion.getEquipoSeleccionado().getBuzon().equals("1")) {
							EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT + "0" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), null,
									null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
							enviarComandoThread.start();
						} else {
							EnviarComandoThread enviarComandoThread = new EnviarComandoThread(context, datosConfT + "1" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), null,
									null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
							enviarComandoThread.start();
						}
					}else{
						if (datosAplicacion.getEquipoSeleccionado().getBuzon() != null && datosAplicacion.getEquipoSeleccionado().getBuzon().equals("1")) {
							AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT + "0" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";");
							AudioQueu.contadorComandoEnviado++;
						} else {
							AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT + "1" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";");
							AudioQueu.contadorComandoEnviado++;
						}
					}
				}
			});
		}


		return rowView;
	}
}