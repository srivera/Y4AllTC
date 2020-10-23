package ec.com.yacare.y4all.activities.focos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.asynctask.ws.GuardarProgramacionAsyncTask;
import ec.com.yacare.y4all.lib.dto.ProgramacionLuces;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.focos.ComandoFoco;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.ProgramacionDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.http.HttpManage;
import io.xlink.wifi.pipe.manage.CmdManage;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.GetSubscribeKeyListener;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;
import io.xlink.wifi.sdk.listener.SubscribeDeviceListener;

import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;


public class ProgramacionActivity extends FragmentActivity {

	private DatosAplicacion datosAplicacion;

	private Button btnProgramar;

	private ToggleButton tLunes, tMartes, tMiercoles, tJueves, tViernes, tSabado, tDomingo ;

	private TimePicker timePicker;

	private SeekBar seekDuracion;

	private EditText editNombre;

	private TextView txtDuracion;

	private ZonaLuces zonaLuces;

	private Integer hours, minutes;

	private String diasCalculo;

	private ImageButton fabSalir;

	private ProgramacionLuces programacionGuardar;

	private String horaOriginal;

	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_programar_luces);

		zonaLuces = (ZonaLuces) getIntent().getSerializableExtra("zona");

		TextView txtNombre = (TextView) findViewById(R.id.txtNombre);
		if(zonaLuces.getId() != null) {
			txtNombre.setText("Alarma de luces: " + zonaLuces.getNombreZona());
		}else {
			txtNombre.setText("Alarma de luces");
		}

		if(isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		datosAplicacion = (DatosAplicacion) getApplicationContext();
		datosAplicacion.setProgramacionActivity(ProgramacionActivity.this);

		tLunes = (ToggleButton) findViewById(R.id.tLunes);
		tMartes = (ToggleButton) findViewById(R.id.tMartes);
		tMiercoles = (ToggleButton) findViewById(R.id.tMiercoles);
		tJueves = (ToggleButton) findViewById(R.id.tJueves);
		tViernes = (ToggleButton) findViewById(R.id.tViernes);
		tSabado = (ToggleButton) findViewById(R.id.tSabado);
		tDomingo = (ToggleButton) findViewById(R.id.tDomingo);

		btnProgramar = (Button) findViewById(R.id.btnProgramar);


		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String activarIbox = sharedPrefs.getString("prefActivarIbox", "0");
	/*	if(activarIbox.equals("0")){
			new SweetAlertDialog(ProgramacionActivity.this, SweetAlertDialog.NORMAL_TYPE)
					.setTitleText(YACSmartProperties.intance.getMessageForKey("foco.titulo.activar"))
					.setContentText(YACSmartProperties.intance.getMessageForKey("foco.mensaje.activar"))
					.setConfirmText("cAtivar")
					.setCancelText("Cancelar")
					.showCancelButton(true)
					.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							finish();

						}
					})
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
								XlinkAgent.getInstance().login(DatosAplicacion.getApp().getAppid(), DatosAplicacion.getApp().getAuth());
							}

							loginUser("5CF8A1DB1B91@futlight.com", SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
							sDialog.cancel();
						}
					})

					.show();

		}
*/

		editNombre = (EditText) findViewById(R.id.editNombreProgramacion);

		timePicker = (TimePicker) findViewById(R.id.timePicker);

		fabSalir = (ImageButton) findViewById(R.id.fabSalir);
		fabSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		txtDuracion = (TextView) findViewById(R.id.txtDuracion);
		seekDuracion = (SeekBar) findViewById(R.id.seekDuracion);

		String tiempo = getIntent().getStringExtra("tiempo");
		if(tiempo != null){
			String[] detalle = tiempo.split(":");
			timePicker.setCurrentHour(Integer.valueOf(detalle[0]));
			timePicker.setCurrentMinute(Integer.valueOf(detalle[1]));
			Integer dia = Integer.valueOf(detalle[2]);
			if(dia.equals(1)){
				tDomingo.setChecked(true);
			}else if(dia.equals(2)){
				tLunes.setChecked(true);
			}else if(dia.equals(3)){
				tMartes.setChecked(true);
			}else if(dia.equals(4)){
				tMiercoles.setChecked(true);
			}else if(dia.equals(5)){
				tJueves.setChecked(true);
			}else if(dia.equals(6)){
				tViernes.setChecked(true);
			}else if(dia.equals(7)){
				tSabado.setChecked(true);
			}
		}

		seekDuracion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				hours = progress / 12;
				minutes = (progress % 12) * 5;
				txtDuracion.setText("Duracion: " + hours + " hrs " + minutes + " min");

			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		btnProgramar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean grabar = true;
				if(editNombre.getText().toString().equals("")){
					grabar = false;
				}
				if(seekDuracion.getProgress() == 0){
					grabar = false;
				}
				if(!tLunes.isChecked() && !tMartes.isChecked() && !tMiercoles.isChecked() && !tJueves.isChecked()
						&& !tViernes.isChecked() && !tSabado.isChecked() && !tDomingo.isChecked()){
					grabar = false;
				}else{
					diasCalculo = (tDomingo.isChecked()?"1":"0") + (tLunes.isChecked()?"1":"0")
					+ (tMartes.isChecked()?"1":"0") + (tMiercoles.isChecked()?"1":"0")
					+ (tJueves.isChecked()?"1":"0") + (tViernes.isChecked()?"1":"0")
					+ (tSabado.isChecked()?"1":"0");
				}


				if(grabar){
					progress = null;
					progress = new ProgressDialog(ProgramacionActivity.this);
					progress.setMessage("Guardando...");
					progress.setCancelable(false);
					progress.show();


						Calendar mCalendar = new GregorianCalendar();
						TimeZone mTimeZone = mCalendar.getTimeZone();
						int mGMTOffset = mTimeZone.getRawOffset();

						programacionGuardar = new ProgramacionLuces();

						programacionGuardar.setIdRouter(zonaLuces.getIdRouter());
						programacionGuardar.setIdZona(zonaLuces.getId());
						programacionGuardar.setNombre(editNombre.getText().toString());
						programacionGuardar.setAccion("1");

						long horaGMT = timePicker.getCurrentHour() - TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
						if(timePicker.getCurrentHour().toString().length() == 1){
							horaOriginal = "0" + timePicker.getCurrentHour().toString() + ":";
						}else{
							horaOriginal = timePicker.getCurrentHour().toString() + ":";
						}

						if(timePicker.getCurrentMinute().toString().length() == 1){
							horaOriginal = horaOriginal + "0" + timePicker.getCurrentMinute().toString();
						}else{
							horaOriginal = horaOriginal + timePicker.getCurrentMinute().toString();
						}

						if(horaGMT > 24){
							horaGMT = horaGMT - 24;
						}else if(horaGMT < 0){
							horaGMT = horaGMT + 24;
						}

					programacionGuardar.setHoraInicio(horaGMT + ":" + timePicker.getCurrentMinute());
					programacionGuardar.setDuracion(hours + ":" + minutes);
					programacionGuardar.setDias(diasCalculo);

					GuardarProgramacionAsyncTask guardarProgramacionAsyncTask = new GuardarProgramacionAsyncTask(ProgramacionActivity.this);
					guardarProgramacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}

			}
		});
	}

	public void verificarProgramacion(String respuesta) {
		//Nueva programacion
		progress.dismiss();
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			JSONObject programacionJSON = null;
			Boolean status = null;
			JSONObject respuestaJSON = null;
			try {
				respuestaJSON = new JSONObject(respuesta);
				status = respuestaJSON.getBoolean("statusFlag");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(status != null && status) {
				if (respuestaJSON.has("programacion")) {
					try {
						programacionJSON = new JSONObject(respuestaJSON.get("programacion").toString());
						if(programacionJSON.has("id")) {
							ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(getApplicationContext());
							programacionDataSource.open();
							programacionGuardar.setHoraInicio(horaOriginal);
							programacionGuardar.setId(programacionJSON.get("id").toString());
							programacionDataSource.createProgramacion(programacionGuardar);
							programacionDataSource.close();
							new SweetAlertDialog(ProgramacionActivity.this, SweetAlertDialog.SUCCESS_TYPE)
									.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
									.setContentText(YACSmartProperties.intance.getMessageForKey("exito.programacion"))
									.setConfirmText("Aceptar")
									.showCancelButton(true)
									.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(SweetAlertDialog sDialog) {
											Intent returnIntent = new Intent();
											setResult(1,returnIntent);
											finish();
										}
									})
									.show();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				new SweetAlertDialog(ProgramacionActivity.this, SweetAlertDialog.ERROR_TYPE)
						.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setContentText(YACSmartProperties.intance.getMessageForKey("conflicto.programacion"))
						.setConfirmText("Aceptar")
						.showCancelButton(true)
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.dismiss();
							}
						})
						.show();

			}
		}else{
			new SweetAlertDialog(ProgramacionActivity.this, SweetAlertDialog.ERROR_TYPE)
					.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setContentText(YACSmartProperties.intance.getMessageForKey("error.router"))
					.setConfirmText("Aceptar")
					.showCancelButton(true)
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							sDialog.dismiss();
						}
					})
					.show();

		}
	}
	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		datosAplicacion.setProgramacionActivity(null);
	}

	public ProgramacionLuces getProgramacionGuardar() {
		return programacionGuardar;
	}


	public void loginUser(final String user, final String pwd) {
		HttpManage.getInstance().login(user, pwd, new HttpManage.ResultCallback<Map<String, Object>>() {
			@Override
			public void onError(Header[] headers, HttpManage.Error error) {
			}

			@Override
			public void onSuccess(int code, Map<String, Object> response) {
				String authKey = (String) response.get("authorize");
				String accessToken = (String) response.get("access_token");
				int appid = ((Double) response.get("user_id")).intValue();
				SharedPreferencesUtil.keepShared("appId", appid);
				SharedPreferencesUtil.keepShared("authKey", authKey);
				DatosAplicacion.getApp().setAccessToken(accessToken);
				DatosAplicacion.getApp().setAppid(appid);
				DatosAplicacion.getApp().setAuth(authKey);
				DatosAplicacion.getApp().auth = true;
				if (!XlinkAgent.getInstance().isConnectedLocal()) {
					XlinkAgent.getInstance().start();
				}
				if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
					XlinkAgent.getInstance().login(DatosAplicacion.getApp().getAppid(), DatosAplicacion.getApp().getAuth());
				}

				int ret = XlinkAgent.getInstance().scanDeviceByProductId(
						Constant.PRODUCTID, scanListener);
				new SweetAlertDialog(ProgramacionActivity.this, SweetAlertDialog.NORMAL_TYPE)
						.setTitleText(YACSmartProperties.intance.getMessageForKey("foco.titulo.activar"))
						.setContentText(YACSmartProperties.intance.getMessageForKey("foco.mensaje.activar"))
						.setConfirmText("Continuar")
						.setCancelText("Cancelar")
						.showCancelButton(true)
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								finish();

							}
						})
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								connectDevice();
								sDialog.cancel();
							}
						})

						.show();


				//if (ret < 0) {



//					switch (ret) {
//						case XlinkCode.NO_CONNECT_SERVER:
//							//XlinkUtils.shortTips("No se ha podido conectar");
//							if (XlinkUtils.isWifi()) {
//								XlinkAgent.getInstance().start();
//							}
//							ret = XlinkAgent.getInstance().scanDeviceByProductId(
//									Constant.PRODUCTID, scanListener);
//							Log.d("resp", "resp " + ret);
//							break;
//						case XlinkCode.NETWORD_UNAVAILABLE:
//						//	XlinkUtils.shortTips("No tiene red");
//							break;
//						default:
//						//	XlinkUtils.shortTips("Error:" + ret);
//							break;
//					}
				return;
				//} else {

				//}



			}
		});


	}
	public Device device;

	private ScanDeviceListener scanListener = new ScanDeviceListener() {

		@Override
		public void onGotDeviceByScan(XDevice device2) {
			//XlinkUtils.shortTips("扫描到设备:" + device.getMacAddress());
			final Device dev = new Device(device2);
			if (device2.getAccessKey() > 0) {
				dev.setAccessKey(device2.getAccessKey());
			}
			DeviceManage.getInstance().addDevice(dev);
			Log.d("estado focos", "estado " + scanListener);
			for(Device device1 : DeviceManage.getInstance().getDevices()){
				if(device1.getMacAddress().equals(datosAplicacion.getEquipoSeleccionado().getNumeroSerie())){
					device =device1;
					XlinkAgent.getInstance().initDevice(device1.getXDevice());

					break;
				}
			}
		}
	};

	int intentos = 0;
	int intentosConectar = 0;
	private boolean isOnline = false;

	public void connectDevice() {
		if (isOnline) {
			return;
		}


		if (device != null && device.getXDevice() != null) {
			if (device != null && device.getXDevice() != null && device.getXDevice().getVersion() >= 3 && device.getXDevice().getSubKey() <= 0) {
				XlinkAgent.getInstance().getInstance().getDeviceSubscribeKey(device.getXDevice(), device.getXDevice().getAccessKey(), new GetSubscribeKeyListener() {
					@Override
					public void onGetSubscribekey(XDevice xdevice, int code, int subKey) {
						device.getXDevice().setSubKey(subKey);
						DeviceManage.getInstance().updateDevice(device);
					}
				});
			}
			if (device != null && !device.isSubscribe()) {
				XlinkAgent.getInstance().subscribeDevice(device.getXDevice(), device.getXDevice().getSubKey(), new SubscribeDeviceListener() {
					@Override
					public void onSubscribeDevice(XDevice xdevice, int code) {
						if (code == XlinkCode.SUCCEED) {
							device.setSubscribe(true);
						}
					}
				});
			}

			intentos++;
			int ret = XlinkAgent.getInstance().connectDevice(device.getXDevice(), device.getXDevice().getAccessKey(), device.getXDevice().getSubKey(), connectDeviceListener);
			Log.d("estado focos ret", "ret " + ret);
//			if (ret < 0) {
//				//Debe hacer por socket
//
//				String comandoSocket;
//				String[] com = comandoPendiente.split(";");
//				int deviceId = device.getXDevice().getDeviceId();
//				if (device.getXDevice().getDeviceId() == 0) {
//					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//					deviceId = sharedPrefs.getInt(equipoSeleccionado.getNumeroSerie(), 0);
//				}
//				if (deviceId != 0) {
//					nombreZonaT.setVisibility(View.GONE);
//					btnBuscarIbox.setVisibility(View.VISIBLE);
//					//nombreZonaT.setText("internet ");
//					txtEstado.setText("Verifique la conexion de su iBox");
//					if (comandoPendiente != null && !comandoPendiente.equals("")) {
//						if (comandoPendiente.startsWith(YACSmartProperties.COM_APAGAR_LUZ_WIFI)) {
//							//apagar
//							comandoSocket = YACSmartProperties.COM_APAGAR_LUZ_WIFI + ";" + deviceId + ";" + equipoSeleccionado.getNumeroSerie() + ";ANDROID;" + com[4] + ";";
//						} else {
//							//encender
//							comandoSocket = YACSmartProperties.COM_ENCENDER_LUZ_WIFI + ";" + deviceId + ";" + equipoSeleccionado.getNumeroSerie() + ";ANDROID;" + com[4] + ";";
//						}
//						ComandoIOFocoScheduledTask comandoIOFocoScheduledTask = new ComandoIOFocoScheduledTask(comandoSocket);
//						comandoIOFocoScheduledTask.start();
//					}
//				}else{
//					nombreZonaT.setVisibility(View.GONE);
//					btnBuscarIbox.setVisibility(View.VISIBLE);
//					//nombreZonaT.setText("internet ");
//					txtEstado.setText("Verifique la conexión de su iBox");
//				}


				//[NSString stringWithFormat:@"%@;%d;%@;%@;%@;%d;%@;",LUZ_INTERNET_LUZ_COLOR,device.deviceID,idEquipo,nombreDispositivo,fila.zona.numeroZona,[fila.zona.numeroZona containsString:@"R"],color];

				//		}else{
				//			if (comandoPendiente != null && !comandoPendiente.equals("")) {
				//				ComandoFoco comandoFoco = new ComandoFoco(comandoPendiente, getApplicationContext());
				//				comandoFoco.start();
				//			}
//			}
		}else{
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			if(intentosConectar < 3){
//				connectDevice();
//			}
//			intentosConectar++;
		}
	}

	Boolean mobile = false;
	private ConnectDeviceListener connectDeviceListener = new ConnectDeviceListener() {

		@Override
		public void onConnectDevice(XDevice xDevice, int result) {
			String pass;
			String tips;

//			if(Connectivity.isConnectedMobile(getApplicationContext())) {
//				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");
//				nombreZonaT.setText("Internet");
//				mobile = true;
//			}else{
//				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");

//			}
			Log.d("estado focos", "estado local 0 internet 1 " + result);
			switch (result) {
				case XlinkCode.DEVICE_STATE_LOCAL_LINK:
					if(CmdManage.sesionFocos == null) {
						pass = Integer.toHexString(xDevice.getSessionId());
						CmdManage.sesionFocos = hexStringToByteArray(pass);
//						ComandoFoco comandoFoco = new ComandoFoco(comandoPendiente, getApplicationContext());
//						comandoFoco.start();
					}
					DeviceManage.getInstance().updateDevice(xDevice);
					XlinkAgent.getInstance().sendProbe(xDevice);

//					String datosConfT = YACSmartProperties.COM_ENCENDER_LUZ_BOX_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + "0" + ";"+ datosAplicacion.getEquipoSeleccionado().getNumeroSerie().trim() + "R" + ";" ;
//					ComandoFoco comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
//					comandoFoco.start();
					String datosConfT = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI_CONEXION + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + "0" + ";"+ datosAplicacion.getEquipoSeleccionado().getNumeroSerie().trim() + "R" + ";" ;
					ComandoFoco comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
					comandoFoco.start();
					comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
					comandoFoco.start();
					break;
				case XlinkCode.DEVICE_STATE_OUTER_LINK:
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(CmdManage.sesionFocos == null) {
						pass = Integer.toHexString(xDevice.getSessionId());
						CmdManage.sesionFocos = hexStringToByteArray(pass);
					}
					DeviceManage.getInstance().updateDevice(xDevice);
					DeviceManage.getInstance().addDevice(xDevice);

					datosConfT = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI_CONEXION + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + 0 + ";09" + ";"  + datosAplicacion.getEquipoSeleccionado().getNumeroSerie().trim() + "R" + ";";
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
					comandoFoco.start();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
					comandoFoco.start();
					break;
				case XlinkCode.CONNECT_DEVICE_INVALID_KEY:
					if(intentos < 3){
						connectDevice();
					}else{

					}
					break;

				case XlinkCode.CONNECT_DEVICE_OFFLINE:
					if(intentos < 3){
						connectDevice();
					}else{

					}


					break;

				// 连接设备超时了，（设备未应答，或者服务器未应答）
				case XlinkCode.CONNECT_DEVICE_TIMEOUT:
					if(intentos < 3){
						connectDevice();
					}else{

					}

					break;

				case XlinkCode.CONNECT_DEVICE_SERVER_ERROR:
					if(intentos < 3){
						connectDevice();
					}else{

					}


					break;
				case XlinkCode.CONNECT_DEVICE_OFFLINE_NO_LOGIN:
					if(intentos < 3){
						connectDevice();
					}else{

					}

					break;
				default:
					//	XlinkUtils.shortTips("Otro error:" + result);
					break;
			}
			intentos++;

		}
	};

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}
}
