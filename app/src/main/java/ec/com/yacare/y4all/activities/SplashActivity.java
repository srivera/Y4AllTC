package ec.com.yacare.y4all.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import ec.com.yacare.y4all.activities.instalacion.InstalarEquipoActivity;
import ec.com.yacare.y4all.activities.luces.LucesFragment;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.asynctask.ws.GuardarDispositivoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.LoginCuentaInternoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.ObtenerDatosCuentaAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.ObtenerEquipoPorNumeroSerieAsyncTask;
import ec.com.yacare.y4all.gcm.QuickstartPreferences;
import ec.com.yacare.y4all.gcm.RegistrationIntentService;
import ec.com.yacare.y4all.lib.asynctask.ComandoCrearEquipoLoginAsyncTask;
import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.CuentaDataSource;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.util.ConexionInternet;
import ec.com.yacare.y4all.lib.util.Connectivity;
import io.xlink.wifi.pipe.util.XlinkUtils;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class SplashActivity extends Activity {

	private DatosAplicacion datosAplicacion;
	public String numeroSerie;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private GoogleCloudMessaging gcm;
	Cuenta cuenta;
	public static String SENDER_ID = "850213101412";

	Boolean primerEquipo = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("SplashActivity", "INGRESAR");

//		Calendar mCalendar = new GregorianCalendar();
//		TimeZone mTimeZone = mCalendar.getTimeZone();
//		int mGMTOffset = mTimeZone.getRawOffset();
//		System.out.printf("GMT offset is %s hours", TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS));

		setContentView(R.layout.activity_splash);
		ImageView imageFotoLoc = (ImageView) findViewById(R.id.imageFotoLoc);
		Drawable backgrounds[] = new Drawable[2];
		Resources res = getResources();
		backgrounds[0] = res.getDrawable(R.drawable.splash);
		backgrounds[1] = res.getDrawable(R.drawable.splash2);

		TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
		imageFotoLoc.setImageDrawable(crossfader);

		crossfader.startTransition(3000);
		validarIngreso();



	}


	private void validarIngreso() {
		XlinkUtils.shortTips("Entro a validarIngreso()");
		if(isScreenLarge()) {

		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		Log.d("Densidad", "Densidad: " + getResources().getDisplayMetrics().density);
		Boolean fast = Connectivity.isConnectedFast(getApplicationContext());
		gcm = GoogleCloudMessaging.getInstance(this);
		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SharedPreferences sharedPreferences =
						PreferenceManager.getDefaultSharedPreferences(context);
				boolean sentToken = sharedPreferences
						.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
				if (sentToken) {
					Log.d(TAG, "CORRECTO");
				} else {
					Log.d(TAG, "ERROR");
				}
			}
		};

		if (checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(SplashActivity.this, RegistrationIntentService.class);
			startService(intent);
		}

//		CrearEquiposAsyncTask crearEquiposAsyncTask = new CrearEquiposAsyncTask("DES");
//		crearEquiposAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//		crearEquiposAsyncTask = new CrearEquiposAsyncTask("DES");
//		crearEquiposAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//		ActualizarVersionEquiposAsyncTask actualizarVersionEquiposAsyncTask = new ActualizarVersionEquiposAsyncTask();
//		actualizarVersionEquiposAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//		CrearArticulosAsyncTask crearArticulosAsyncTask = new CrearArticulosAsyncTask();
//		crearArticulosAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		XlinkUtils.shortTips("validarIngreso 1");
		datosAplicacion = (DatosAplicacion) getApplicationContext();
		XlinkUtils.shortTips("validarIngreso 1.1 " + datosAplicacion);
		CuentaDataSource datasource;
		datasource = new CuentaDataSource(getApplicationContext());
		XlinkUtils.shortTips("validarIngreso 1.2 " + datasource);
		datasource.open();
		XlinkUtils.shortTips("validarIngreso 1.3 " );
		cuenta = datasource.getCuentaCliente();
		XlinkUtils.shortTips("validarIngreso 1.4 " + cuenta);
		datasource.close();
		datosAplicacion.setCuenta(cuenta);
		XlinkUtils.shortTips("validarIngreso 2");
		EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
		equipoDataSource.open();
		equipoDataSource.crearColumnas();
		equipoDataSource.close();

//		if (datosAplicacion.getCuenta() != null && !datosAplicacion.getCuenta().getId().equals("")) {
//			if(datosAplicacion.getCuenta().getIp() != null) {
//				YACSmartProperties.IP_CORP = datosAplicacion.getCuenta().getIp();
//				YACSmartProperties.actualizarURL();
//			}
//		} else {
//			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//			String ipCorp = sharedPrefs.getString("prefIpCorp", "").toString();
//			if(ipCorp != null && !ipCorp.equals("")) {
//				YACSmartProperties.IP_CORP = ipCorp;
//				YACSmartProperties.actualizarURL();
//			}
//		}

		File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" );
		if(!directory.exists()) {
			directory.mkdirs();
		}
		XlinkUtils.shortTips("validarIngreso 3");
		if(cuenta != null && cuenta.getId() != null && cuenta.getEstadoCuenta().equals(YACSmartProperties.getInstance().getMessageForKey("cuenta.estado.activo"))){
			//Login para verificar que el usuario y el celular este activo si hay internet
			ConexionInternet conexion = new ConexionInternet(getApplicationContext());
			if(conexion.isInternetOn(getApplicationContext())){
				XlinkUtils.shortTips("validarIngreso 4");
				LoginCuentaInternoAsyncTask loginCuentaInternoAsyncTask = new LoginCuentaInternoAsyncTask(SplashActivity.this, null, cuenta.getEmail(), cuenta.getClave());
				loginCuentaInternoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}else{
				ingresarAplicacion();
			}
//		}else if(cuenta != null && cuenta.getId() != null && cuenta.getEstadoCuenta().equals(YACSmartProperties.getInstance().getMessageForKey("cuenta.estado.temporal"))){
//			//Cuenta registrada y no enviada al web service de crearCuenta
//			//Intentar volver a enviar
//			EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
//			equipoDataSource.open();
//			Equipo equipoBusqueda = new Equipo();
//			equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.CONFIGURACION.getCodigo());
//			ArrayList<Equipo> equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
//			equipoDataSource.close();
//			if(equipos != null && equipos.size() > 0){
//				datosAplicacion.setEquipoSeleccionado((Equipo) equipos.toArray()[0]);
//			}
//			Intent i = new Intent(SplashActivity.this, RegistroActivity.class);
//			i.putExtra("estadoCuenta", cuenta.getEstadoCuenta());
//			SplashActivity.this.startActivity(i);
		}else if(cuenta != null && cuenta.getId() != null && cuenta.getEstadoCuenta().equals(YACSmartProperties.getInstance().getMessageForKey("cuenta.estado.pendiente"))){
			//Debe ingresar la clave de confirmacion enviada al mail
			equipoDataSource = new EquipoDataSource(getApplicationContext());
			equipoDataSource.open();
			Equipo equipoBusqueda = new Equipo();
			equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.CONFIGURACION.getCodigo());
			ArrayList<Equipo> equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
			equipoDataSource.close();
			if(equipos != null && equipos.size() > 0){
				numeroSerie = ((Equipo) equipos.toArray()[0]).getNumeroSerie();
				ObtenerEquipoPorNumeroSerieAsyncTask numeroSerieAsyncTask = new ObtenerEquipoPorNumeroSerieAsyncTask(null, SplashActivity.this);
				numeroSerieAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				datosAplicacion.setEquipoSeleccionado((Equipo) equipos.toArray()[0]);
			}
//			Intent i = new Intent(SplashActivity.this, InstalarEquipoActivity.class);
//			i.putExtra("estadoCuenta", cuenta.getEstadoCuenta());
//			i.putExtra("primerEquipo", true);
//			SplashActivity.this.startActivity(i);
		}else{
			//Cuenta sin registrar
			//Verificamos si tiene un equipo en configuracion
			equipoDataSource = new EquipoDataSource(getApplicationContext());
			equipoDataSource.open();
			Equipo equipoBusqueda = new Equipo();
			equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.CONFIGURACION.getCodigo());
			ArrayList<Equipo> equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
			equipoDataSource.close();
			if(equipos != null && equipos.size() > 0){
				numeroSerie = ((Equipo) equipos.toArray()[0]).getNumeroSerie();
				ObtenerEquipoPorNumeroSerieAsyncTask numeroSerieAsyncTask = new ObtenerEquipoPorNumeroSerieAsyncTask(null, SplashActivity.this);
				numeroSerieAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				datosAplicacion.setEquipoSeleccionado((Equipo) equipos.toArray()[0]);
			}else{

				equipoDataSource.open();
				equipoBusqueda = new Equipo();
				equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.FABRICADO.getCodigo());
				equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
				equipoDataSource.close();
				if(equipos != null && equipos.size() > 0){
					numeroSerie = ((Equipo) equipos.toArray()[0]).getNumeroSerie();
					ObtenerEquipoPorNumeroSerieAsyncTask numeroSerieAsyncTask = new ObtenerEquipoPorNumeroSerieAsyncTask(null, SplashActivity.this);
					numeroSerieAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					datosAplicacion.setEquipoSeleccionado((Equipo) equipos.toArray()[0]);
				}else {
					Intent i = new Intent(SplashActivity.this, InicioActivity.class);
					SplashActivity.this.startActivity(i);
				}
			}
		}


		new Thread() {
			@Override
			public void run() {
				handler.sendMessageDelayed(handler.obtainMessage(), 500);
			};
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onStart() {
		super.onStart();
		
	}

	public boolean isScreenLarge() {
	    final int screenSize = getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK;
	    return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
	            || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}
	
	//Handler que se invoca en el start para determinar a que actividad se debe ir la primera vez
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {


		}
	};
	
	
	public void verificarLoginInterno(String respuesta) {
		XlinkUtils.shortTips("validarIngreso 6");
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultToken = null;
			Boolean status = null;
			JSONObject respuestaJSON = null;
			try {
				respuestaJSON = new JSONObject(respuesta);
				status = respuestaJSON.getBoolean("statusFlag");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//Pueden darse 4 casos:
			// 1. Login correcto
			// 2. Clave invalida
			// 3. Dispositivo no autorizado
			// 4. No hay internet o no hay acceso a los WS. Le dejamos entrar pero si no hay token cuando quiera utilizar los equipos no le va  a permitir
			if(status != null && status) {
				try {
				resultToken = respuestaJSON.getString("token");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(resultToken != null ){
					datosAplicacion.setToken(resultToken);
					XlinkUtils.shortTips("validarIngreso 7");
					ObtenerDatosCuentaAsyncTask obtenerDatosCuentaAsyncTask = new ObtenerDatosCuentaAsyncTask(null, SplashActivity.this);
					obtenerDatosCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					GuardarDispositivoAsyncTask guardarDispositivoAsyncTask = new GuardarDispositivoAsyncTask(getApplicationContext(), null, null, null);
					guardarDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
			}else if(respuesta.contains("SAG01")){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						SplashActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("clave.incorrecta.mensaje"))
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent i = new Intent(SplashActivity.this, LoginActivity.class);
								i.putExtra("estado", YACSmartProperties.intance.getMessageForKey("clave.incorrecta"));
								SplashActivity.this.startActivity(i);
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

			}else if(respuesta.contains("SAG02")){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						SplashActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("dispositivo.no.autorizado.mensaje"))
						.setCancelable(false)
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								finish();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}else{
				ingresarAplicacion();
			}
		}else{
			ingresarAplicacion();
		}
	}

	
	public void verificarDatosCuenta(String respuesta) {
		XlinkUtils.shortTips("validarIngreso 8");
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultCuenta = null;
			Boolean status = null;
			JSONObject respuestaJSON = null;
			try {
				respuestaJSON = new JSONObject(respuesta);
				status = respuestaJSON.getBoolean("statusFlag");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(status != null && status) {
				try {
					respuestaJSON = new JSONObject(respuesta);
					resultCuenta = respuestaJSON.getString("cuenta");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(resultCuenta != null ){
					//Crear cuenta, equipo y respuestas
					XlinkUtils.shortTips("validarIngreso 9");
					JSONObject cuentaJSON = null;
					try {
						cuentaJSON = new JSONObject( respuestaJSON.getString("cuenta"));

						JSONArray equipos = new JSONArray(cuentaJSON.getString("equipo"));

						JSONArray dispositivos = new JSONArray(cuentaJSON.getString("dispositivo"));

						SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

						ComandoCrearEquipoLoginAsyncTask genericoAsyncTask = new ComandoCrearEquipoLoginAsyncTask(SplashActivity.this,
								YACSmartProperties.ADM_RECIBIR_ID_PUSH, equipos, sharedPrefs.getString("prefNombreDispositivo", ""), null, dispositivos,
								SplashActivity.this);
						genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

						//ingresarAplicacion();

					} catch (JSONException e) {
						e.printStackTrace();
					}
					setResult(InicioActivity.CERRAR_PANTALLA);
				}

			}else{
				ingresarAplicacion();
			}

		}else{
			ingresarAplicacion();
		}
	}
	public void verificarCrearEquipos() {

		ingresarAplicacion();
	}
	private void ingresarAplicacion() {
//		Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
//
//		ImageView image = (ImageView)findViewById(R.id.image);
//		image.setImageDrawable(crossfader);
//
//		crossfader.startTransition(3000);
//		imageFotoLoc.startAnimation(fade_in);
//		imageFotoLoc.setImageResource(R.drawable.splash2);
	//Si hay un solo equipo y esta en estado configuracion
		XlinkUtils.shortTips("validarIngreso 11");
		EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
		equipoDataSource.open();
		equipoDataSource.crearColumnas();
		Equipo equipoBusqueda = new Equipo();
		equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.CONFIGURACION.getCodigo());
		ArrayList<Equipo> equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
		equipoDataSource.close();
		if(equipos != null && equipos.size() > 0){
			//Cuenta activa y equipo en configuracion
			Equipo equipo = (Equipo) equipos.toArray()[0];
			datosAplicacion.setEquipoSeleccionado(equipo);
//			Intent i = null;
			if(equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())){
				numeroSerie = ((Equipo) equipos.toArray()[0]).getNumeroSerie();
				primerEquipo = false;
				ObtenerEquipoPorNumeroSerieAsyncTask numeroSerieAsyncTask = new ObtenerEquipoPorNumeroSerieAsyncTask(null, SplashActivity.this);
				numeroSerieAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				i = new Intent(SplashActivity.this, InstalarEquipoActivity.class);
//				startActivity(i);
			}

		}else{
			//Cuenta activa y no hay dispositivos configurandose
			XlinkUtils.shortTips("validarIngreso 12");
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String numeroSerie = sharedPrefs.getString("prefEquipo", "").toString();
			equipoDataSource = new EquipoDataSource(getApplicationContext());
			equipoDataSource.open();
			equipoBusqueda = new Equipo();
			equipoBusqueda.setTipoEquipo(TipoEquipoEnum.PORTERO.getCodigo());
			equipos = equipoDataSource.getEquipoTipoEquipo(equipoBusqueda);

			if(equipos != null && equipos.size() > 0) {
				datosAplicacion.setEquipoSeleccionado(equipos.get(0));
				datosAplicacion.setPorteroInstalado(true);
			}else{
				datosAplicacion.setPorteroInstalado(false);
			}

			equipoBusqueda = new Equipo();
			equipoBusqueda.setNumeroSerie(numeroSerie);
			Equipo equipoPreferencia = equipoDataSource.getEquipoNumSerie(equipoBusqueda);
			equipoPreferencia.setModo(YACSmartProperties.MODO_WIFI);
//			equipoPreferencia.setNombreWiFi("yacareWIFI");
//			equipoDataSource.updateEquipo(equipoPreferencia);
//			equipoDataSource.close();

			if(equipoPreferencia != null && equipoPreferencia.getId() != null  && equipoPreferencia.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
				XlinkUtils.shortTips("validarIngreso 13");
				datosAplicacion.setEquipoSeleccionado(equipoPreferencia);
				Intent i = new Intent(SplashActivity.this, Y4HomeActivity.class);
				startActivity(i);
			}else if(equipoPreferencia != null && equipoPreferencia.getId() != null  && equipoPreferencia.getTipoEquipo().equals(TipoEquipoEnum.LUCES.getCodigo())) {
				XlinkUtils.shortTips("validarIngreso 14");
					datosAplicacion.setEquipoSeleccionado(equipoPreferencia);
				Intent i = new Intent(SplashActivity.this, LucesFragment.class);
				startActivity(i);
			}else{
				XlinkUtils.shortTips("validarIngreso 15");
				if(datosAplicacion.getPorteroInstalado()){
					XlinkUtils.shortTips("validarIngreso 16");
					Intent i = new Intent(SplashActivity.this, Y4HomeActivity.class);
					startActivity(i);
				}else{
					XlinkUtils.shortTips("validarIngreso 17");
					equipoDataSource = new EquipoDataSource(getApplicationContext());
					equipoDataSource.open();
					equipoBusqueda = new Equipo();
					equipoBusqueda.setTipoEquipo(TipoEquipoEnum.LUCES.getCodigo());
					equipos = equipoDataSource.getEquipoTipoEquipo(equipoBusqueda);
					equipoDataSource.close();
					datosAplicacion.setEquipoSeleccionado(equipos.get(0));
					Intent i = new Intent(SplashActivity.this, LucesFragment.class);
					startActivity(i);
				}
			}

//			datosAplicacion.getEquipoSeleccionado().setNombreWiFi("yacareWIFI");
//			equipoDataSource.updateEquipo(datosAplicacion.getEquipoSeleccionado());
			equipoDataSource.close();
		}
	}

	public void mostrarRespuestaIngresarEquipo(String respuesta) {
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String estadoEquipo = null;
			JSONObject equipoJSON = null;
			Boolean status = null;
			JSONObject respuestaJSON = null;
			try {
				respuestaJSON = new JSONObject(respuesta);
				status = respuestaJSON.getBoolean("statusFlag");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(status != null && status) {
				if (respuestaJSON.has("equipo")) {
					try {
						equipoJSON = new JSONObject(respuestaJSON.get("equipo").toString());
						EquipoDataSource datasource = new EquipoDataSource(SplashActivity.this);
						datasource.open();
						if (equipoJSON.has("ipLocal") ) {
							datosAplicacion.getEquipoSeleccionado().setIpLocal(equipoJSON.getString("ipLocal"));
						}
						if (equipoJSON.has("rutaSocketIo")) {
							datosAplicacion.getEquipoSeleccionado().setSocketComando(equipoJSON.getString("rutaSocketIo"));
						}
						if(equipoJSON.has("estado")){
							datosAplicacion.getEquipoSeleccionado().setEstadoEquipo(equipoJSON.getString("estado"));
						}

						datasource.updateEquipo(datosAplicacion.getEquipoSeleccionado());
						datasource.close();

						if(datosAplicacion.getEquipoSeleccionado().getEstadoEquipo().equals(EstadoDispositivoEnum.CONFIGURACION.getCodigo())){
							Intent i = new Intent(SplashActivity.this, InstalarEquipoActivity.class);
							i.putExtra("primerEquipo", primerEquipo);
							if(cuenta != null) {
								i.putExtra("estadoCuenta", cuenta.getEstadoCuenta());
							}
							startActivity(i);
						}else if(datosAplicacion.getEquipoSeleccionado().getEstadoEquipo().equals(EstadoDispositivoEnum.INSTALADO.getCodigo())){
							Intent i = new Intent(SplashActivity.this, Y4HomeActivity.class);
							startActivity(i);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}else{
				Intent i = new Intent(SplashActivity.this, InicioActivity.class);
				startActivity(i);
			}
		}else{
			Intent i = new Intent(SplashActivity.this, InicioActivity.class);
			startActivity(i);
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
}
