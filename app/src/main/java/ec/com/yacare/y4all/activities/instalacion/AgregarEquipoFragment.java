package ec.com.yacare.y4all.activities.instalacion;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.luces.LucesFragment;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.asynctask.ws.ActivarFinalizarEquipoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.FinalizarInstalacionEquipoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.ObtenerEquipoPorNumeroSerieAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.dispositivo.ComandoConfigurarScheduledTask;
import ec.com.yacare.y4all.lib.asynctask.hotspot.ComandoHotSpotScheduledTask;
import ec.com.yacare.y4all.lib.asynctask.hotspot.ConectarWifiAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.hotspot.ConfigurarFocosAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.hotspot.ConfigurarWifiFocosAsyncTask;
import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;


public class AgregarEquipoFragment extends Fragment {

	private Spinner spnTipoEquipo;
	private EditText editNombreEquipo;
	private CheckBox checkWifi;
	private EditText editNombreWifi;
	private EditText editClaveWiFi;

	private Button btnConfigurar;

	private Equipo equipo;

	private String numeroSerieText;

	private  DatosAplicacion datosAplicacion;

	public ProgressDialog progress;

	public TableLayout tablaPrimerPaso;

	private String estado = "INI", resultadoRouter = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.d("onActivityCreated" , "onCreateView");
		View view = inflater.inflate(R.layout.activity_agregar_equipo, container, false);

		datosAplicacion = (DatosAplicacion) getActivity().getApplicationContext();

		spnTipoEquipo = (Spinner) view.findViewById(R.id.spnTipoEquipo);
		editNombreEquipo = (EditText) view.findViewById(R.id.editNombreEquipo);
		editNombreWifi = (EditText) view.findViewById(R.id.editNombreWifi);
		editClaveWiFi = (EditText) view.findViewById(R.id.editClaveWiFi);
		checkWifi = (CheckBox) view.findViewById(R.id.checkBoxWiFi);
		btnConfigurar = (Button) view.findViewById(R.id.btnConfigurar);
		tablaPrimerPaso = (TableLayout) view.findViewById(R.id.tablaPrimerPaso);

		List<String> list = new ArrayList<String>();
		list.add(TipoEquipoEnum.LUCES.getDescripcion());
		list.add(TipoEquipoEnum.PORTERO.getDescripcion());

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnTipoEquipo.setAdapter(dataAdapter);

		checkWifi.setVisibility(View.GONE);
		spnTipoEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					//Luces
					checkWifi.setVisibility(View.GONE);
					editClaveWiFi.setEnabled(true);
					editNombreWifi.setEnabled(true);
				}else if (position == 1){
					//Portero
					checkWifi.setVisibility(View.VISIBLE);
					checkWifi.setChecked(false);
					editClaveWiFi.setEnabled(false);
					editNombreWifi.setEnabled(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		checkWifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					editClaveWiFi.setEnabled(true);
					editNombreWifi.setEnabled(true);
					checkWifi.setButtonDrawable(android.R.drawable.checkbox_on_background);
				}else{
					checkWifi.setButtonDrawable(android.R.drawable.checkbox_off_background);
					editClaveWiFi.setEnabled(false);
					editClaveWiFi.setText("");
					editNombreWifi.setEnabled(false);
					editNombreWifi.setText("");
				}
			}
		});
		editNombreWifi.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				estado = "INI";
			}
		});
		editClaveWiFi.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				estado = "INI";
			}
		});
		WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		String name = wifiInfo.getSSID();
		if(!name.contains("Milight")){
			editNombreWifi.setText(wifiInfo.getSSID().replaceAll("\"", ""));
		}
		btnConfigurar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				boolean grabar = true;
				String wifi = "0";
				if(spnTipoEquipo.getSelectedItemPosition() == 0) {
					//Luces
					if (editNombreWifi.getText().toString().isEmpty()) {
						editNombreWifi.setError(YACSmartProperties.intance.getMessageForKey("clave.wifi.vacio"));
						grabar = false;
					}

					if (editClaveWiFi.getText().toString().isEmpty()) {
						editClaveWiFi.setError(YACSmartProperties.intance.getMessageForKey("clave.wifi.vacio"));
						grabar = false;
					}
					WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
					String name = wifiInfo.getSSID();
					if(estado.equals("INI") && !name.contains("Milight")){
						grabar = false;
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());

						alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.unmomento"))
								.setMessage("Para configurar iBox se requiere conectarse a la red Milight. Si no le aparece en su lista de redes, presione el botón que se encuentra debajo de su iBox por unos segundos hasta que parpadeen las 2 luces rojas. Apague y vuelva a encender.")
								.setCancelable(false)
								.setPositiveButton("Cambiar de WiFi", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										btnConfigurar.setEnabled(true);
										startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
										dialog.cancel();

									}
								})
								.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				}else if(spnTipoEquipo.getSelectedItemPosition() == 1) {
					//Portero
					if (checkWifi.isChecked()) {
						if (editNombreWifi.getText().toString().isEmpty()) {
							editNombreWifi.setError(YACSmartProperties.intance.getMessageForKey("clave.wifi.vacio"));
							grabar = false;
						}

						if (editClaveWiFi.getText().toString().isEmpty()) {
							editClaveWiFi.setError(YACSmartProperties.intance.getMessageForKey("clave.wifi.vacio"));
							grabar = false;
						}
						wifi = "1";
					}
					WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
					String name = wifiInfo.getSSID();
					if(estado.equals("INI") && !name.contains("Y4HOME")){
						grabar = false;
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());

						alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.unmomento"))
								.setMessage("Para configurar Wii Bell debe conectarse a la red Y4Home.")
								.setCancelable(false)
								.setPositiveButton("Cambiar de WiFi", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										btnConfigurar.setEnabled(true);
										startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
										dialog.cancel();

									}
								})
								.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				}

				if(grabar){
					btnConfigurar.setEnabled(false);
					progress = null;
					progress = new ProgressDialog(getActivity());
//					progress.setMessage("Conectando con su equipo. Espere un momento...");
//					progress.setCancelable(false);
//					progress.show();

					if(spnTipoEquipo.getSelectedItemPosition() == 0){
						//Luces
						if (estado.equals("INI")) {
							progress.setMessage("Conectando con su iBox. Espere un momento...");
							progress.setCancelable(false);
							progress.show();
							ConfigurarFocosAsyncTask configurarFocosAsyncTask = new ConfigurarFocosAsyncTask(AgregarEquipoFragment.this, editNombreWifi.getText().toString().trim(), editClaveWiFi.getText().toString().trim());
							configurarFocosAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}else{
							progress.setMessage("Conectando con su equipo. Espere un momento...");
							progress.setCancelable(false);
							progress.show();
							ConfigurarWifiFocosAsyncTask configurarFocosAsyncTask = new ConfigurarWifiFocosAsyncTask(AgregarEquipoFragment.this, editNombreWifi.getText().toString().trim(), editClaveWiFi.getText().toString().trim(), resultadoRouter);
							configurarFocosAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}

					}else if(spnTipoEquipo.getSelectedItemPosition() == 1){
						//Portero
						progress.setMessage("Conectando con su equipo. Espere un momento...");
						progress.setCancelable(false);
						progress.show();
						if(datosAplicacion.getEquipoSeleccionado() != null && datosAplicacion.getEquipoSeleccionado().getEstadoEquipo().equals(EstadoDispositivoEnum.CONFIGURACION.getCodigo())) {

							FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(null, datosAplicacion.getEquipoSeleccionado(), AgregarEquipoFragment.this);
							instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

							SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
							String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

							equipo = datosAplicacion.getEquipoSeleccionado();

							Cuenta cuenta = datosAplicacion.getCuenta();
							String comando =  YACSmartProperties.COM_CREAR_CUENTA
									+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
									+ ";" + cuenta.getId()
									+ ";" + cuenta.getEmail()
									+ ";" + cuenta.getClave()
									+ ";" + cuenta.getFechaCuenta()
									+ ";" + Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)
									+ ";" + nombreDispositivo
									+ ";" + "ANDROID";

							ComandoConfigurarScheduledTask genericoAsyncTask = new ComandoConfigurarScheduledTask(comando, null, AgregarEquipoFragment.this);
							genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}else {
							TimeZone tz = TimeZone.getDefault();
							String comando = YACSmartProperties.HOTSPOT_WIFI + ";" + wifi + ";" + editNombreWifi.getText().toString().trim() + ";" + editClaveWiFi.getText().toString().trim()
									+ ";" + editNombreEquipo.getText().toString() + ";" + tz.getID() + ";";

							ComandoHotSpotScheduledTask genericoAsyncTask = new ComandoHotSpotScheduledTask(AgregarEquipoFragment.this, comando);
							genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
					}
				}
			}
		});


		return view;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tablaPrimerPaso.getLayoutParams();
			params.width = 500;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tablaPrimerPaso.getLayoutParams();
			params.width = 400;
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(datosAplicacion.getEquipoSeleccionado() != null && datosAplicacion.getEquipoSeleccionado().getEstadoEquipo().equals(EstadoDispositivoEnum.CONFIGURACION.getCodigo())) {
			Log.d("onActivityCreated" , "if");
			spnTipoEquipo.setSelection(1);
			editNombreEquipo.setText(datosAplicacion.getEquipoSeleccionado().getNombreEquipo());
			if(datosAplicacion.getEquipoSeleccionado() != null && datosAplicacion.getEquipoSeleccionado().getNombreWiFi() != null && !datosAplicacion.getEquipoSeleccionado().getNombreWiFi().equals("")){
				checkWifi.setChecked(true);
				editNombreWifi.setText(datosAplicacion.getEquipoSeleccionado().getNombreWiFi());
				editClaveWiFi.setText("********");
			}
			spnTipoEquipo.setEnabled(false);
			editClaveWiFi.setEnabled(false);
			editNombreEquipo.setEnabled(false);
			editNombreWifi.setEnabled(false);
			checkWifi.setEnabled(false);
		}else{
			Log.d("onActivityCreated" , "else");
		}
	}

	public void verificarResultadoConfigurarFocos(String resultado, String routerEncontrado) {
		Log.d("COMANDO HOT SPOT", resultado);
		String[] datos = resultado.split(",");
		if(datos[0].equals("ERR")){
			btnConfigurar.setEnabled(true);
			progress.dismiss();

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage(datos[1])
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							btnConfigurar.setEnabled(true);
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}else if (datos[0].equals("OK")) {
			//Luces
			resultadoRouter = routerEncontrado;
			estado = "CON";
			btnConfigurar.setEnabled(true);
			progress.dismiss();

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.unmomento"))
					.setMessage("Configuración realizada, para confirmar su funcionamiento es necesario conectarse a " + editNombreWifi.getText().toString() )
					.setCancelable(false)
					.setPositiveButton("Cambiar de WiFi", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							btnConfigurar.setEnabled(true);
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
							dialog.cancel();
						}
					})
					.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}

	int  configurarWifi = 1;
	public void verificarResultadoWifiFocos(String resultado) {
		Log.d("COMANDO HOT SPOT", resultado +  " " + configurarWifi);
		String[] datos = resultado.split(",");
		if(datos[0].equals("ERR")){
			configurarWifi++;
			if(configurarWifi < 4) {
				ConfigurarWifiFocosAsyncTask configurarFocosAsyncTask = new ConfigurarWifiFocosAsyncTask(AgregarEquipoFragment.this, editNombreWifi.getText().toString().trim(), editClaveWiFi.getText().toString().trim(), resultadoRouter);
				configurarFocosAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}else{
				configurarWifi = 0;
				btnConfigurar.setEnabled(true);
				progress.dismiss();

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());

				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.unmomento"))
						.setMessage("Los datos de la wifi podrían estar incorrectos, las letras mayúsculas y minúsculas son importantes. Si los datos son correctos, apague y encienda el iBox y presione reintentar " + editNombreWifi.getText().toString() )
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}


		}else if (datos[0].equals("OK")) {
			Log.d("COMANDO HOT SPOT",  " ok " );

//			btnConfigurar.setEnabled(true);
//			progress.dismiss();

			numeroSerieText = datos[2];
			equipo = new Equipo();
			equipo.setIpLocal(datos[1]);
			Log.d("ipLocal2" , datos[1]);
			equipo.setNumeroSerie(datos[2]);
			if(editNombreEquipo.getText().toString().equals("")) {
				equipo.setNombreEquipo(datos[3].trim());
			}else{
				equipo.setNombreEquipo(editNombreEquipo.getText().toString());
			}
			ObtenerEquipoPorNumeroSerieAsyncTask numeroSerieAsyncTask = new ObtenerEquipoPorNumeroSerieAsyncTask(AgregarEquipoFragment.this, null);
			numeroSerieAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}else if(datos[0].equals("CON")){
			btnConfigurar.setEnabled(true);
			progress.dismiss();

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
					.setMessage(datos[1] )
					.setCancelable(false)
					.setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							btnConfigurar.setEnabled(true);
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

		}
	}
	public String getNumeroSerieText() {
		return numeroSerieText;
	}

	public void setNumeroSerieText(String numeroSerieText) {
		this.numeroSerieText = numeroSerieText;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public void verificarResultadoHotSpot(String comando) {
		Log.d("COMANDO HOT SPOT", comando);
		String[] comandos = comando.split(";");
		if(comando.equals("ERR")){
			progress.dismiss();

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage(YACSmartProperties.intance.getMessageForKey("hotspot.error"))
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							btnConfigurar.setEnabled(true);
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

		}else if (comandos[1].equals("ERR")) {
			progress.dismiss();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage(YACSmartProperties.intance.getMessageForKey("hotspot.numeroserie"))
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							btnConfigurar.setEnabled(true);
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}else if (comandos[1].equals("OK")) {
			EquipoDataSource dataSource = new EquipoDataSource(getActivity().getApplicationContext());
			dataSource.open();
			equipo = new Equipo();
			numeroSerieText = comandos[3];
			equipo.setNumeroSerie(comandos[3]);
			equipo.setNombreEquipo(editNombreEquipo.getText().toString());
			equipo.setTipoEquipo(TipoEquipoEnum.PORTERO.getCodigo());
			equipo.setEstadoEquipo(EstadoDispositivoEnum.FABRICADO.getCodigo());
			equipo.setLuzWifi("0");
			equipo.setPuerta("0");
			if(checkWifi.isChecked()) {
				equipo.setModo(YACSmartProperties.MODO_WIFI);
			}else{
				equipo.setModo(YACSmartProperties.MODO_CABLE);
			}
			if(checkWifi.isChecked()) {
				equipo.setNombreWiFi(editNombreWifi.getText().toString());
			}else{
				equipo.setNombreWiFi("");
			}
			equipo.setId(comandos[2]);
			dataSource.createEquipo(equipo);
			dataSource.close();

			AudioQueu.falloHotSpot = true;
			AudioQueu.verificarHotSpot = true;

			ConectarWifiAsyncTask verificarWifiConexionAsyncTask = new ConectarWifiAsyncTask(AgregarEquipoFragment.this, editNombreWifi.getText().toString(),  editClaveWiFi.getText().toString());
			verificarWifiConexionAsyncTask.start();
		}
	}

//	public void mostrarRespuestaConexion(Boolean resultado) {
//		((InstalarEquipoActivity)getActivity()).progress.dismiss();
//		if(resultado){
//			ObtenerEquipoPorNumeroSerieAsyncTask numeroSerieAsyncTask = new ObtenerEquipoPorNumeroSerieAsyncTask(AgregarEquipoFragment.this, null);
//							numeroSerieAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		}else{
//			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//					getActivity());
//
//			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
//					.setMessage(YACSmartProperties.intance.getMessageForKey("hotspot.error") + " 1")
//					.setCancelable(false)
//					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//							btnConfigurar.setEnabled(true);
//						}
//					});
//
//			AlertDialog alertDialog = alertDialogBuilder.create();
//			alertDialog.show();
//		}
//	}

	public void mostrarRespuestaIngresarEquipo(String respuesta) {
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String estadoEquipo = null;
			JSONObject equipoJSON = null;
			String tipoEquipo = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				if(respuestaJSON.has("equipo")){
					equipoJSON = new JSONObject(respuestaJSON.get("equipo").toString());
					estadoEquipo = equipoJSON.getString("estado");
					tipoEquipo = equipoJSON.getString("tipoEquipo");
				}else{
					estadoEquipo = YACSmartProperties.getInstance().getMessageForKey("error.general");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(estadoEquipo != null && estadoEquipo.equals(EstadoDispositivoEnum.FABRICADO.getCodigo())){
				if(tipoEquipo.equals(TipoEquipoEnum.LUCES.getCodigo())) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();

					try {
						equipo.setId(equipoJSON.getString("id"));
						equipo.setFechaFabricacion(equipoJSON.getString("fechaFabricacion"));
						equipo.setEstadoEquipo(estadoEquipo);
						equipo.setFechaRegistro(dateFormat.format(date));
						equipo.setTipoEquipo(equipoJSON.getString("tipoEquipo"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					EquipoDataSource datasource = new EquipoDataSource(getActivity());
					datasource.open();
					datasource.createEquipo(equipo);
					datasource.close();

					if(datosAplicacion.getCuenta() == null) {
						ActivarFinalizarEquipoAsyncTask activarEquipoAsyncTask = new ActivarFinalizarEquipoAsyncTask(AgregarEquipoFragment.this);
						activarEquipoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}else{
						FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(null, equipo, AgregarEquipoFragment.this);
						instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}

				}else{
					if(progress.isShowing()) {
						progress.dismiss();
					}
					btnConfigurar.setEnabled(true);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getActivity());

					alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
							.setMessage(YACSmartProperties.intance.getMessageForKey("conectar.equipo"))
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									btnConfigurar.setEnabled(true);
								}
							});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
			}else if(estadoEquipo != null && estadoEquipo.equals(EstadoDispositivoEnum.INSTALADO.getCodigo())){
				if(progress.isShowing()) {
					progress.dismiss();
				}
				if(tipoEquipo.equals(TipoEquipoEnum.LUCES.getCodigo())) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();

					try {
						equipo.setId(equipoJSON.getString("id"));
						equipo.setFechaFabricacion(equipoJSON.getString("fechaFabricacion"));
						equipo.setEstadoEquipo(estadoEquipo);
						equipo.setFechaRegistro(dateFormat.format(date));
						equipo.setTipoEquipo(equipoJSON.getString("tipoEquipo"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					EquipoDataSource datasource = new EquipoDataSource(getActivity());
					datasource.open();
					datasource.createEquipo(equipo);
					datasource.close();

					if(datosAplicacion.getCuenta() == null) {
						ActivarFinalizarEquipoAsyncTask activarEquipoAsyncTask = new ActivarFinalizarEquipoAsyncTask(AgregarEquipoFragment.this);
						activarEquipoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}else{
						FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(null, equipo, AgregarEquipoFragment.this);
						instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}

				}else {
					btnConfigurar.setEnabled(true);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getActivity());
					alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
							.setMessage(YACSmartProperties.intance.getMessageForKey("equipo.instalado.otro.dispositivo"))
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}

			}else if(estadoEquipo != null && estadoEquipo.equals(EstadoDispositivoEnum.CONFIGURACION.getCodigo())){
				if(tipoEquipo.equals(TipoEquipoEnum.PORTERO.getCodigo())) {

					//Guardar equipo
					EquipoDataSource datasource = new EquipoDataSource(getActivity());
					datasource.open();

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();

					try {
						equipo.setFechaFabricacion(equipoJSON.getString("fechaFabricacion"));
						equipo.setEstadoEquipo(estadoEquipo);
						if (equipoJSON.has("ipPublica")) {
							equipo.setIpPublica(equipoJSON.getString("ipPublica"));
						}
						if (equipoJSON.has("ipLocal")) {
							Log.d("ipLocal" , equipoJSON.getString("ipLocal"));
							equipo.setIpLocal(equipoJSON.getString("ipLocal"));
						}
						if (equipoJSON.has("rutaSocketIo")) {
							equipo.setSocketComando(equipoJSON.getString("rutaSocketIo"));
						}
						equipo.setFechaRegistro(dateFormat.format(date));

					} catch (JSONException e) {
						e.printStackTrace();
					}

					datasource.updateEquipo(equipo);
					datasource.close();
					datosAplicacion.setEquipoSeleccionado(equipo);

					if(datosAplicacion.getCuenta() != null) {


						FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(null, equipo, this);
						instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

						SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
						String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");


						Cuenta cuenta = datosAplicacion.getCuenta();
						String comando =  YACSmartProperties.COM_CREAR_CUENTA
								+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
								+ ";" + cuenta.getId()
								+ ";" + cuenta.getEmail()
								+ ";" + cuenta.getClave()
								+ ";" + cuenta.getFechaCuenta()
								+ ";" + Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)
								+ ";" + nombreDispositivo
								+ ";" + "ANDROID";

						ComandoConfigurarScheduledTask genericoAsyncTask = new ComandoConfigurarScheduledTask(comando, null, AgregarEquipoFragment.this);
						genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					}else {

						if(progress.isShowing()) {
							progress.dismiss();
						}

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());

						alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
								.setMessage(YACSmartProperties.intance.getMessageForKey("hotspot.exito"))
								.setCancelable(false)
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										((InstalarEquipoActivity) getActivity()).steppersView.steppersAdapter.nextStep();

									}
								});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				}else{
					//Luces CON
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();

					try {
						equipo.setId(equipoJSON.getString("id"));
						equipo.setFechaFabricacion(equipoJSON.getString("fechaFabricacion"));
						equipo.setEstadoEquipo(estadoEquipo);
						equipo.setFechaRegistro(dateFormat.format(date));
						equipo.setTipoEquipo(equipoJSON.getString("tipoEquipo"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					EquipoDataSource datasource = new EquipoDataSource(getActivity());
					datasource.open();
					datasource.createEquipo(equipo);
					datasource.close();

					if(datosAplicacion.getCuenta() == null) {
						ActivarFinalizarEquipoAsyncTask activarEquipoAsyncTask = new ActivarFinalizarEquipoAsyncTask(AgregarEquipoFragment.this);
						activarEquipoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}else{
						FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(null, equipo, AgregarEquipoFragment.this);
						instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
			}else if(estadoEquipo != null && estadoEquipo.equals(respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general")))){
				if(progress.isShowing()) {
					progress.dismiss();
				}
				btnConfigurar.setEnabled(true);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());

				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
				.setMessage(YACSmartProperties.intance.getMessageForKey("equipo.novalido"))
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}else{
				if(progress.isShowing()) {
					progress.dismiss();
				}
				btnConfigurar.setEnabled(true);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());

				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
				.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}else{
			//Error general
			if(progress.isShowing()) {
				progress.dismiss();
			}
			btnConfigurar.setEnabled(true);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
			.setMessage(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}


	public void mostrarRespuestaActivarEquipo(String respuesta) {
		progress.dismiss();
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			equipo.setEstadoEquipo("CON");
			EquipoDataSource datasource = new EquipoDataSource(getActivity());
			datasource.open();
			datasource.updateEquipo(equipo);
			datasource.close();

			datosAplicacion.setEquipoSeleccionado(equipo);

//			if(equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());

				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("hotspot.exito"))
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								datosAplicacion.setEquipoSeleccionado(equipo);

									if(equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
										Intent i = new Intent(getActivity(), Y4HomeActivity.class);
										startActivity(i);
									}else{
										if(datosAplicacion.getCuenta() != null) {
											Intent i = new Intent(getActivity(), LucesFragment.class);
											startActivity(i);
										}else{
											((InstalarEquipoActivity)getActivity()).steppersView.steppersAdapter.nextStep();
										}
									}

								}

						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
//		}

		}else{
			//Error general
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}


	public void verificarResultadoInstalacion(String respuesta) {
		if(progress.isShowing()){
			progress.dismiss();
		}
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultCuenta = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				resultCuenta = respuestaJSON.getString("resultado");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(resultCuenta != null && resultCuenta.equals(YACSmartProperties.getInstance().getMessageForKey("ok.general"))){
				DatosAplicacion datosAplicacion = (DatosAplicacion) getActivity().getApplicationContext();
				equipo = datosAplicacion.getEquipoSeleccionado();
				equipo.setEstadoEquipo(EstadoDispositivoEnum.INSTALADO.getCodigo());
				EquipoDataSource dataSource = new EquipoDataSource(getActivity().getApplicationContext());
				dataSource.open();
				dataSource.updateEquipo(equipo);
				dataSource.close();

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());

				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("instalacion.terminada"))
						.setCancelable(false)
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								if(equipo.getTipoEquipo().equals(TipoEquipoEnum.LUCES.getCodigo())){
									Intent i = new Intent(getActivity(), LucesFragment.class);
									startActivity(i);
								}else{
									Intent i = new Intent(getActivity(), Y4HomeActivity.class);
									startActivity(i);
								}

							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}else{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
						.setCancelable(false)
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}else{
			//Error general
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
					.setCancelable(false)
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}


	}

}
