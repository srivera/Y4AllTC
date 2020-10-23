package ec.com.yacare.y4all.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ec.com.yacare.y4all.activities.instalacion.InstalarEquipoActivity;
import ec.com.yacare.y4all.activities.luces.LucesFragment;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.asynctask.ws.GuardarDispositivoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.LoginCuentaAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.ObtenerDatosCuentaAsyncTask;
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
import io.xlink.wifi.pipe.util.XlinkUtils;

//import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class LoginActivity extends AppCompatActivity {

	private EditText email;
	private EditText clave;
	private EditText nombreDispositivo;
	private Button ingresar;
	
	private Boolean claveIncorrecta = false;
	private String emailTexto;
	private String claveTexto;

	private DatosAplicacion datosAplicacion;
	public ProgressDialog progress;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	//private GoogleCloudMessaging gcm;

	public static String SENDER_ID = "850213101412";


	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		if(isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});


		datosAplicacion = (DatosAplicacion) getApplicationContext();

		overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

		email = (EditText) findViewById(R.id.editLoginEmail);
		clave = (EditText) findViewById(R.id.editLoginClave);
		nombreDispositivo = (EditText) findViewById(R.id.editNombreDispositivo);
		ingresar = (Button) findViewById(R.id.btnIngresar);
		
		if(getIntent().getExtras() != null && getIntent().getExtras().getString("estado") != null && getIntent().getExtras().getString("estado").equals(YACSmartProperties.intance.getMessageForKey("clave.incorrecta"))){
			claveIncorrecta = true;
			nombreDispositivo.setVisibility(View.INVISIBLE);
		}
		
		ingresar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				boolean grabar = true;
				if (email.getText().toString().isEmpty()) {
					email.setError(YACSmartProperties.intance.getMessageForKey("email.vacio"));
					grabar = false;
				}
				if (email.getText().toString() != null && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
					email.setError(YACSmartProperties.intance.getMessageForKey("formato.email.invalido"));
					grabar = false;
				}
				if (clave.getText().toString().isEmpty()) {
					clave.setError(YACSmartProperties.intance.getMessageForKey("clave.vacio"));
					grabar = false;
				}
				if (nombreDispositivo.getText().toString().isEmpty() && !claveIncorrecta) {
					nombreDispositivo.setError(YACSmartProperties.intance.getMessageForKey("nombre.dispositivo.vacio"));
					grabar = false;
				}
				if(grabar){
					progress = ProgressDialog.show(LoginActivity.this, "Conectando con su equipo",
							"Espere un momento", true);
					emailTexto = email.getText().toString();
					claveTexto = clave.getText().toString();
					XlinkUtils.shortTips("Antes de ws login");
					LoginCuentaAsyncTask loginCuentaAsyncTask = new LoginCuentaAsyncTask(LoginActivity.this);
					loginCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
				}
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//TableLayout tablaLogin = (TableLayout) findViewById(R.id.tablaLogin);
//		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tablaLogin.getLayoutParams();
//			params.width = 500;
//		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tablaLogin.getLayoutParams();
//			params.width = 400;
//		}
	}

	Boolean verificarGuardarDispositivo = false;

	public void verificarLoginCuenta(String respuesta) {
		XlinkUtils.shortTips("Entro a verificarLoginCuenta");
		if(progress != null) {
			progress.dismiss();
		}
		if(respuesta != null && !respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultToken = null;
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
					resultToken = respuestaJSON.getString("token");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(resultToken != null) {
					// Guardar el token en la app y llamar al ws de datos
					DatosAplicacion datosAplicacion = ((DatosAplicacion) getApplicationContext());
					datosAplicacion.setToken(resultToken);

					if (claveIncorrecta) {

						CuentaDataSource datasource;
						datasource = new CuentaDataSource(getApplicationContext());
						datasource.open();
						Cuenta cuenta = datasource.getCuentaCliente();
						//Actualizar la cuenta
						cuenta.setClave(clave.getText().toString());
						datasource.updateCuenta(cuenta);
						datosAplicacion.setCuenta(cuenta);
						datasource.close();

						ingresarAplicacion();

					} else {
						XlinkUtils.shortTips("Antes de obtener datos cuenta");
						ObtenerDatosCuentaAsyncTask obtenerDatosCuentaAsyncTask = new ObtenerDatosCuentaAsyncTask(LoginActivity.this, null);
						obtenerDatosCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					}

				}
			}else if(respuesta.contains("SAG02")){
					//Guardar en el Shared el nombre del dispositivo
					verificarGuardarDispositivo = true;
					XlinkUtils.shortTips("SAG02");
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putString("prefNombreDispositivo", nombreDispositivo.getText().toString());
					editor.apply();
					editor.commit();

					DatosAplicacion datosAplicacion = ((DatosAplicacion)getApplicationContext());
					Cuenta cuenta = new Cuenta();
					cuenta.setId(UUID.randomUUID().toString());
					cuenta.setClave(clave.getText().toString());
					cuenta.setEmail(email.getText().toString());
					cuenta.setIdMensajePush(datosAplicacion.getRegId());

					datosAplicacion.setCuenta(cuenta);

					GuardarDispositivoAsyncTask guardarDispositivoAsyncTask = new GuardarDispositivoAsyncTask(getApplicationContext(), null, LoginActivity.this, null);
					guardarDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}else if(respuesta.contains("SAG01")) {
				XlinkUtils.shortTips("SAG01");
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						LoginActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("clave.incorrecta"))
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}

		}else{
			XlinkUtils.shortTips("Error general");
			//Error general
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					LoginActivity.this);
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

	public void verificarGuardarDispositivo(String respuesta) {
		if(verificarGuardarDispositivo) {
			if (!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))) {
				String resultToken = null;
				Boolean status = null;
				JSONObject respuestaJSON = null;
				try {
					respuestaJSON = new JSONObject(respuesta);
					status = respuestaJSON.getBoolean("statusFlag");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (status != null && status) {
				/*try {
					respuestaJSON = new JSONObject(respuesta);
					resultToken = respuestaJSON.getString("token");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if(resultToken != null ) {
					DatosAplicacion datosAplicacion = ((DatosAplicacion) getApplicationContext());
					datosAplicacion.setToken(resultToken);

					ObtenerDatosCuentaAsyncTask obtenerDatosCuentaAsyncTask = new ObtenerDatosCuentaAsyncTask(LoginActivity.this, null);
					obtenerDatosCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

				}*/
					LoginCuentaAsyncTask loginCuentaAsyncTask = new LoginCuentaAsyncTask(LoginActivity.this);
					loginCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

				} else {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							LoginActivity.this);
					alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
							.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
			} else {
				//Error general
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						LoginActivity.this);
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
	}
	public void verificarDatosCuenta(String respuesta) {
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

				if(resultCuenta != null ) {
					DatosAplicacion datosAplicacion = ((DatosAplicacion) getApplicationContext());

					//Crear cuenta, equipo y respuestas
					JSONObject cuentaJSON = null;
					try {
						XlinkUtils.shortTips("antes de crear cuenta ");
						DatosAplicacion aplicacion = ((DatosAplicacion) getApplication());
						cuentaJSON = new JSONObject(respuestaJSON.getString("cuenta"));

						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date date = new Date();

						Cuenta cuenta = new Cuenta();
						cuenta.setId(UUID.randomUUID().toString());
						cuenta.setClave(clave.getText().toString());
						cuenta.setEmail(email.getText().toString());
						cuenta.setEstadoCuenta(YACSmartProperties.getInstance().getMessageForKey("cuenta.estado.activo"));
						cuenta.setFechaCuenta(dateFormat.format(date));
						cuenta.setIdMensajePush(datosAplicacion.getRegId());
						CuentaDataSource datasource = new CuentaDataSource(getApplicationContext());
						datasource.open();
						datasource.createCuenta(cuenta);
						datasource.close();
						aplicacion.setCuenta(cuenta);
						XlinkUtils.shortTips("despues de crear cuenta ");
						JSONArray equipos = new JSONArray(cuentaJSON.getString("equipo"));

						JSONArray dispositivos = new JSONArray(cuentaJSON.getString("dispositivo"));
						ComandoCrearEquipoLoginAsyncTask genericoAsyncTask = new ComandoCrearEquipoLoginAsyncTask(LoginActivity.this,
								YACSmartProperties.ADM_RECIBIR_ID_PUSH, equipos, nombreDispositivo.getText().toString(), LoginActivity.this, dispositivos, null);

						genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						XlinkUtils.shortTips("despues de crear equipos ");
						//Guardar en el Shared el nombre del dispositivo
						SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						SharedPreferences.Editor editor = sharedPrefs.edit();
						editor.putString("prefNombreDispositivo", nombreDispositivo.getText().toString());
						editor.apply();
						editor.commit();

						GuardarDispositivoAsyncTask guardarDispositivoAsyncTask = new GuardarDispositivoAsyncTask(getApplicationContext(), null, LoginActivity.this, null);
						guardarDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}else{
				XlinkUtils.shortTips("error sin conexion ");
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						LoginActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
				.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
	
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}else{
			//Error general
			XlinkUtils.shortTips("error general 2 ");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					LoginActivity.this);
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

	public void verificarCrearEquipos() {
		progress.dismiss();
//		gcm = GoogleCloudMessaging.getInstance(this);
//		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				SharedPreferences sharedPreferences =
//						PreferenceManager.getDefaultSharedPreferences(context);
//				boolean sentToken = sharedPreferences
//						.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
//				if (sentToken) {
//					Log.d(TAG, "CORRECTO");
//				} else {
//					Log.d(TAG, "ERROR");
//				}
//			}
//		};
		XlinkUtils.shortTips("antes de registrar google play ");
		if (checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(LoginActivity.this, RegistrationIntentService.class);
			startService(intent);
		}
		ingresarAplicacion();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		/*GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				//Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}*/
		return true;
	}


	private void ingresarAplicacion() {
		//Si hay un solo equipo y esta en estado configuracion
		XlinkUtils.shortTips("ingresar equipo ");
		EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
		equipoDataSource.open();
		Equipo equipoBusqueda = new Equipo();
		equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.CONFIGURACION.getCodigo());
		ArrayList<Equipo> equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
		equipoDataSource.close();
		if(equipos != null && equipos.size() > 0){
			//Cuenta activa y equipo en configuracion
			Equipo equipo = (Equipo) equipos.toArray()[0];
			datosAplicacion.setEquipoSeleccionado(equipo);
			Intent i = null;
			if(equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())){
//				i = new Intent(SplashActivity.this, Y4HomeActivity.class);
				i = new Intent(LoginActivity.this, InstalarEquipoActivity.class);
				startActivity(i);
			}

		}else{
			//Cuenta activa y no hay dispositivos configurandose
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
			equipoDataSource.close();

			if(equipoPreferencia != null && equipoPreferencia.getId() != null  && equipoPreferencia.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
				datosAplicacion.setEquipoSeleccionado(equipoPreferencia);
				Intent i = new Intent(LoginActivity.this, Y4HomeActivity.class);
				startActivity(i);
			}else if(equipoPreferencia != null  && equipoPreferencia.getId() != null  && equipoPreferencia.getTipoEquipo().equals(TipoEquipoEnum.LUCES.getCodigo())) {
				datosAplicacion.setEquipoSeleccionado(equipoPreferencia);
				Intent i = new Intent(LoginActivity.this, LucesFragment.class);
				startActivity(i);
			}else{
				if(datosAplicacion.getPorteroInstalado()){
					Intent i = new Intent(LoginActivity.this, Y4HomeActivity.class);
					startActivity(i);
				}else{
					equipoDataSource = new EquipoDataSource(getApplicationContext());
					equipoDataSource.open();
					equipoBusqueda = new Equipo();
					equipoBusqueda.setTipoEquipo(TipoEquipoEnum.LUCES.getCodigo());
					equipos = equipoDataSource.getEquipoTipoEquipo(equipoBusqueda);
					equipoDataSource.close();
					datosAplicacion.setEquipoSeleccionado(equipos.get(0));
					Intent i = new Intent(LoginActivity.this, LucesFragment.class);
					startActivity(i);
				}
			}
		}
	}



//	private void ingresarAplicacion() {
//		//Si hay un solo equipo y esta en estado configuracion
//		EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
//		equipoDataSource.open();
//		Equipo equipoBusqueda = new Equipo();
//		equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.CONFIGURACION.getCodigo());
//		ArrayList<Equipo> equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
//		equipoDataSource.close();
//
//		DatosAplicacion datosAplicacion = ((DatosAplicacion) getApplicationContext());
//
//		if(equipos != null && equipos.size() > 0){
//			//Cuenta activa y equipo en configuracion
//			Equipo equipo = (Equipo) equipos.toArray()[0];
//			datosAplicacion.setEquipoSeleccionado(equipo);
//			Intent i = null;
//			if(equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())){
////				i = new Intent(SplashActivity.this, Y4HomeActivity.class);
//				i = new Intent(LoginActivity.this, ConfigPorteroActivity.class);
//			}
//			startActivity(i);
//		}else{
//			//Cuenta activa y no hay dispositivos configurandose
//			equipoDataSource = new EquipoDataSource(getApplicationContext());
//			equipoDataSource.open();
//			equipoBusqueda = new Equipo();
//			equipoBusqueda.setTipoEquipo(TipoEquipoEnum.PORTERO.getCodigo());
//			equipos = equipoDataSource.getEquipoTipoEquipo(equipoBusqueda);
//			equipoDataSource.close();
//			if(equipos != null && equipos.size() > 0) {
//				datosAplicacion.setPorteroInstalado(true);
//				Intent i = new Intent(LoginActivity.this, Y4HomeActivity.class);
//				startActivity(i);
//			}else{
//				datosAplicacion.setPorteroInstalado(false);
//				Intent i = new Intent(LoginActivity.this, AdministrarRoutersActivity.class);
//				startActivity(i);
//			}
//		}
//	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DatosAplicacion datosAplicacion = (DatosAplicacion) getApplicationContext();
		if(datosAplicacion.getCuenta() == null){

			Intent i = new Intent(LoginActivity.this, InicioActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.from_middle, R.anim.to_middle);


		}
	}


	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}


	public EditText getEmail() {
		return email;
	}

	public void setEmail(EditText email) {
		this.email = email;
	}

	public EditText getClave() {
		return clave;
	}

	public void setClave(EditText clave) {
		this.clave = clave;
	}

	public String getEmailTexto() {
		return emailTexto;
	}

	public void setEmailTexto(String emailTexto) {
		this.emailTexto = emailTexto;
	}

	public String getClaveTexto() {
		return claveTexto;
	}

	public void setClaveTexto(String claveTexto) {
		this.claveTexto = claveTexto;
	}
}