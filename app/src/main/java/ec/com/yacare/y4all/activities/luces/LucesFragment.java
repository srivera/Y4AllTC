package ec.com.yacare.y4all.activities.luces;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.instalacion.InstalarEquipoActivity;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.adapter.SeleccionarEquipoAdapter;
import ec.com.yacare.y4all.asynctask.ws.ActualizarEquipoAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.io.ComandoIOFocoScheduledTask;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.focos.BuscarRutersAsyncTask;
import ec.com.yacare.y4all.lib.focos.ComandoFoco;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.ZonaDataSource;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.util.Connectivity;
import ec.com.yacare.y4all.lib.util.ViewCircle;
import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.http.HttpManage;
import io.xlink.wifi.pipe.manage.CmdManage;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.GetSubscribeKeyListener;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;
import io.xlink.wifi.sdk.listener.SubscribeDeviceListener;

import static android.view.View.inflate;


public class LucesFragment extends AppCompatActivity {

	public Device device;
	private boolean isRun;
	private boolean isOnline = false;
	boolean isAuthConnect = true;
	private boolean isRegisterBroadcast = false;


	private Equipo equipoSeleccionado;
	private DatosAplicacion datosAplicacion;

	private ArrayList<ZonaLuces> zonas;
	private LucesFragment.LucesAdapter zonaAdapter;

	public ListView listZonas;

	private RelativeLayout constraintLayoutTotal;

	private ViewCircle circleTodos;;

	private Boolean encendido = false;
	private Boolean actualizar = false;

	private FloatingActionButton btnAzulT, btnRosadoT, btnNaranjaT, btnBlancoT, btnVerdeT, btnRojoT, btnAmarilloT, btnMoradoT, btnLimaT, btnAquaT;
	private float textSizeCircle;

	private String numeroSerieComando;

	private TextView txtEstado;

	private ImageView mimageView;
	private SeleccionarEquipoAdapter seleccionarEquipoAdapter;

	private FloatingActionMenu menuDown;

//	private FloatingActionButton btnCuenta;
//	private FloatingActionButton btnAplicacion;
	private FloatingActionButton btnNuevo;
	private FloatingActionButton btnCompartir;

	private TextView txtNombreEquipo;

	private LinearLayout filaTotal;

	private String nombreDispositivo;
	private TextView nombreZonaT;
	private Button btnBuscarIbox;

	private static final int REQUEST_CAMERA = 0;

	private int appid;
	private String authKey;

	private Boolean brillo = true;
	private Boolean brilloT = true;
	TextView radio_brilloT, radio_saturacionT, txtLeyendaT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_luces);
		XlinkUtils.shortTips("Ingreso LucesFragment");
		datosAplicacion = (DatosAplicacion) getApplicationContext();
		datosAplicacion.setCurrentActivity(this);
		if (isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
			textSizeCircle = 16f;
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			textSizeCircle = 30f;
		}

		if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)) {
			ActivityCompat.requestPermissions(LucesFragment.this,
					new String[]{Manifest.permission.CAMERA},
					REQUEST_CAMERA);
		}

		btnBuscarIbox = (Button) findViewById(R.id.btnBuscarIbox);
		filaTotal = (LinearLayout) findViewById(R.id.filaTotal);
		constraintLayoutTotal = (RelativeLayout) findViewById(R.id.constraintLayoutTotal);
		circleTodos = (ViewCircle) findViewById(R.id.circleTodos);
		listZonas = (ListView) findViewById(R.id.listZonas);

		menuDown = (FloatingActionMenu) findViewById(R.id.menu_down);
		menuDown.bringToFront();

		txtNombreEquipo = (TextView) findViewById(R.id.txtNombreEquipo);
		txtEstado = (TextView) findViewById(R.id.txtEstado);
		nombreZonaT = (TextView) findViewById(R.id.nombreZonaT);

//		indicatorView = (AVLoadingIndicatorView) findViewById(R.id.indicator);

//		btnAplicacion = (FloatingActionButton) findViewById(R.id.btnApp);
		btnNuevo = (FloatingActionButton) findViewById(R.id.btnNuevo);
//		btnCuenta = (FloatingActionButton) findViewById(btnCuenta);
		btnCompartir = (FloatingActionButton) findViewById(R.id.btnCompartir);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();

		mimageView = (ImageView) findViewById(R.id.fotoperfil);

		mimageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
				equipoDataSource.open();
				Equipo equipoBusqueda = new Equipo();
				equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.INSTALADO.getCodigo());
				ArrayList<Equipo> equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
				equipoDataSource.close();

				if (equipos != null && !equipos.isEmpty() && equipos.size() > 1) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(LucesFragment.this);
					LayoutInflater inflater1 = getLayoutInflater();
					View convertView = (View) inflater1.inflate(R.layout.seleccionar_equipo, null);

					View convertViewTitulo = (View) inflater1.inflate(R.layout.seleccionar_equipo_titulo, null);
					alertDialog.setCustomTitle(convertViewTitulo);

					convertView.setBackgroundColor(Color.parseColor("#546E7A"));
					alertDialog.setView(convertView);
					seleccionarEquipoAdapter = new SeleccionarEquipoAdapter(LucesFragment.this, equipos);
					alertDialog.setCancelable(true);

					ListView lista = (ListView) convertView.findViewById(R.id.lv);
					lista.setAdapter(seleccionarEquipoAdapter);
					final AlertDialog ad = alertDialog.show();
					lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Equipo equipo = seleccionarEquipoAdapter.getItem(position);
							datosAplicacion.setEquipoSeleccionado(equipo);
							equipoSeleccionado = equipo;
							if(equipo.getTipoEquipo().equals(TipoEquipoEnum.LUCES.getCodigo())) {
								ingresarRouter();
								ad.dismiss();
							}else{
								Intent i = new Intent(LucesFragment.this, Y4HomeActivity.class);
								startActivity(i);
								finish();
							}
						}
					});
					//alertDialog.show();
				} else {
					//Un equipo entra directamente
					Equipo equipo = (Equipo) equipos.toArray()[0];
					datosAplicacion.setEquipoSeleccionado(equipo);
					equipoSeleccionado = equipo;
					ingresarRouter();
				}

			}
		});

		btnNuevo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				menuDown.close(true);
				Intent i = new Intent(LucesFragment.this, InstalarEquipoActivity.class);
				i.putExtra("primerEquipo", false);
				startActivity(i);
			}
		});

//		btnCuenta.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				menuDown.close(true);
//				Intent i = new Intent(LucesFragment.this, IngresarClaveActivity.class);
//				startActivity(i);
//			}
//		});
//
//		btnCompartir.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				menuDown.close(true);
//				final EditText input1 = new EditText(LucesFragment.this);
//				LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.MATCH_PARENT,
//						LinearLayout.LayoutParams.MATCH_PARENT);
//				input1.setLayoutParams(lp1);
//				input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//				final AlertDialog d1 = new AlertDialog.Builder(LucesFragment.this)
//						.setTitle(YACSmartProperties.intance.getMessageForKey("email.invitado"))
//						.setCancelable(true)
//						.setView(input1)
//						.setPositiveButton("OK",
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog, int which) {
//										if (!input1.getText().toString().equals("")) {
//											InvitarCuentaAsyncTask invitarCuentaAsyncTask = new InvitarCuentaAsyncTask(LucesFragment.this,
//													equipoSeleccionado.getNumeroSerie(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),
//													datosAplicacion.getCuenta().getEmail(), input1.getText().toString(), datosAplicacion.getToken());
//											invitarCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//											dialog.cancel();
//										}
//									}
//								}).create();
//
//				d1.show();
//			}
//		});

		if(equipoSeleccionado != null) {
			for (Device device1 : DeviceManage.getInstance().getDevices()) {
				if (device1.getMacAddress().equals(equipoSeleccionado.getNumeroSerie())) {
					equipoSeleccionado.setDevice(device1);
					device = device1;
				}
			}
		}

		actualizarFotoPerfil();
		ingresarRouter();
		btnBuscarIbox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BuscarRutersAsyncTask buscarRutersAsyncTask = new BuscarRutersAsyncTask(LucesFragment.this);
				buscarRutersAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});

		SeekBar brightnessT = (SeekBar) findViewById(R.id.brightnessT);
		//SeekBar saturationT = (SeekBar) findViewById(R.id.saturationT);
		//saturationT.setVisibility(View.GONE);
		btnAzulT = (FloatingActionButton) findViewById(R.id.btnAzulT);
		btnRosadoT = (FloatingActionButton) findViewById(R.id.btnRosadoT);
		btnNaranjaT = (FloatingActionButton) findViewById(R.id.btnNaranjaT);
		btnBlancoT = (FloatingActionButton) findViewById(R.id.btnBlancoT);
		btnVerdeT = (FloatingActionButton) findViewById(R.id.btnVerdeT);
		btnRojoT = (FloatingActionButton) findViewById(R.id.btnRojoT);
		btnAmarilloT = (FloatingActionButton) findViewById(R.id.btnAmarilloT);
		btnMoradoT = (FloatingActionButton) findViewById(R.id.btnMoradoT);
		btnLimaT = (FloatingActionButton) findViewById(R.id.btnLimaT);
		btnAquaT = (FloatingActionButton) findViewById(R.id.btnAquaT);

		ImageView btnPopupT = (ImageView) findViewById(R.id.btnPopupT);
		btnPopupT.setColorFilter(Color.BLACK);
		btnPopupT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		brightnessT.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Integer valor = seekBar.getProgress();
				if (equipoSeleccionado != null) {
					if(brilloT) {
						String datosConfT = YACSmartProperties.COM_LUZ_BRIGHTNESS_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + valor + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
						ejecutarAccionLuces(datosConfT);
					}else{
						String datosConfT = YACSmartProperties.COM_LUZ_SATURATION_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + valor + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
						ejecutarAccionLuces(datosConfT);
					}
				}
			}
		});

		radio_brilloT = (TextView) findViewById(R.id.radio_brilloT);
		radio_saturacionT = (TextView) findViewById(R.id.radio_saturacionT);
		txtLeyendaT = (TextView) findViewById(R.id.txtLeyendaT);

		radio_brilloT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				radio_brilloT.setTextColor(Color.parseColor("#000000"));
				radio_saturacionT.setTextColor(Color.parseColor("#6E6E6E"));
				brilloT = true;
				txtLeyendaT.setText("el brillo esta disponible en cualquier tipo de foco");
			}
		});
		radio_saturacionT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				radio_brilloT.setTextColor(Color.parseColor("#6E6E6E"));
				radio_saturacionT.setTextColor(Color.parseColor("#000000"));
				brilloT = false;
				txtLeyendaT.setText("saturación disponible en focos de 8w, lámparas de exterior y dicroicos de 4w");
			}
		});

		btnAzulT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "BA" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()+ ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor(Color.BLUE);
				}
			}
		});

		btnRosadoT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "FF" + ";" +datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor(Color.parseColor("#E91E63"));
				}
			}
		});

		btnNaranjaT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "23" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor(Color.parseColor("#EF6C00"));
				}
			}
		});

		btnBlancoT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_WHITE_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor(Color.parseColor("#000000"));
				}
			}
		});

		btnVerdeT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "6E" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor(Color.GREEN);
				}
			}
		});

		btnRojoT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "19" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor(Color.RED);
				}
			}
		});
		btnAmarilloT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "3B" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor(Color.YELLOW);
				}
			}
		});

		btnMoradoT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "D9" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor( Color.parseColor("#AA00FF"));
				}
			}
		});

		btnAquaT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "85" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor( Color.parseColor("#AA00FF"));
				}
			}
		});

		btnLimaT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(equipoSeleccionado != null) {
					String datosConfT = YACSmartProperties.COM_LUZ_COLOR_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + "54" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);
					animarEncenderColor( Color.parseColor("#69F0AE"));
				}
			}
		});

		circleTodos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				actualizar = true;
				if (!encendido) {
					encendido = true;
					for(ZonaLuces zonaLuces1 : zonas){
						zonaLuces1.setToggleActivo(true);
					}
					String datosConfT = YACSmartProperties.COM_ENCENDER_LUZ_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";"+ datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" ;
					ejecutarAccionLuces(datosConfT);

					new Thread(new Runnable() {
						@Override
						public void run() {
							int percent = 0;
							while(percent != 100){
								try {
									Thread.sleep(15);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								percent += 1;

								circleTodos.changePercentage(percent,"on", Color.YELLOW, Color.BLACK, textSizeCircle);
							}
						}
					}).start();

					ViewGroup.LayoutParams params = constraintLayoutTotal.getLayoutParams();
					ViewGroup.LayoutParams paramsV = filaTotal.getLayoutParams();
					params.height = paramsV.height;
					constraintLayoutTotal.setLayoutParams(params);
					Animation scaleAnimation = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
					scaleAnimation.setDuration(750);
					scaleAnimation.setFillEnabled(true);
					scaleAnimation.setFillAfter(true);
					constraintLayoutTotal.startAnimation(scaleAnimation);

				} else {
					encendido = false;
					for(ZonaLuces zonaLuces1 : zonas){
						zonaLuces1.setToggleActivo(false);
					}
					String datosConfT = YACSmartProperties.COM_APAGAR_LUZ_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + "0" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
					ejecutarAccionLuces(datosConfT);

					new Thread(new Runnable() {
						@Override
						public void run() {
							int percent = 0;
							while(percent  != 100){
								try {
									Thread.sleep(15);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								percent += 1;
								circleTodos.changePercentage(percent,"off", Color.BLACK, Color.GRAY, textSizeCircle);
							}
						}
					}).start();

					constraintLayoutTotal.bringToFront();
					ViewGroup.LayoutParams params = constraintLayoutTotal.getLayoutParams();
					ViewGroup.LayoutParams paramsV = filaTotal.getLayoutParams();
					params.height = paramsV.height;
					constraintLayoutTotal.setLayoutParams(params);

					Animation scaleAnimation = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
					scaleAnimation.setDuration(750);
					scaleAnimation.setFillEnabled(true);
					scaleAnimation.setFillAfter(true);
					constraintLayoutTotal.startAnimation(scaleAnimation);
				}
				zonaAdapter.notifyDataSetChanged();
			}
		});

		XlinkUtils.shortTips("Inicia manejo internet");


		DatosAplicacion.getApp().setAppid(appid);
		DatosAplicacion.getApp().setAuth(authKey);


		if (!XlinkAgent.getInstance().isConnectedLocal()) {
			XlinkAgent.getInstance().start();
		}
		if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
			XlinkAgent.getInstance().login(DatosAplicacion.getApp().getAppid(), DatosAplicacion.getApp().getAuth());
		}


		initWidget();

		loginUser(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID), SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
	}
	private String id;

	TelephonyManager tm;






	public void initWidget() {
		for(Device device1 : DeviceManage.getInstance().getDevices()){
			if(device1.getMacAddress().equals(equipoSeleccionado.getNumeroSerie())){
				equipoSeleccionado.setDevice(device1);
			}
		}
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
				if (ret < 0) {

						connectDevice();

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
				} else {

				}



			}
		});
	}



	private ScanDeviceListener scanListener = new ScanDeviceListener() {

		@Override
		public void onGotDeviceByScan(XDevice device2) {
			//XlinkUtils.shortTips("扫描到设备:" + device.getMacAddress());
			final Device dev = new Device(device2);
			if (device2.getAccessKey() > 0) {
				dev.setAccessKey(device2.getAccessKey());
			}
			DeviceManage.getInstance().addDevice(dev);

			for(Device device1 : DeviceManage.getInstance().getDevices()){
				if(device1.getMacAddress().equals(equipoSeleccionado.getNumeroSerie())){
					device =device1;

					equipoSeleccionado.setDevice(device1);
					XlinkAgent.getInstance().initDevice(device1.getXDevice());
					if(device1.getXDevice().getDeviceId() != 0){
						SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						SharedPreferences.Editor editor = sharedPrefs.edit();
						editor.putInt(equipoSeleccionado.getNumeroSerie(), device1.getXDevice().getDeviceId());
						editor.apply();
						editor.commit();
					}
				}
			}
		}
	};



	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	public void connectDevice() {
		if (isOnline) {
			return;
		}
		if (device.getXDevice().getVersion() >= 3 && device.getXDevice().getSubKey() <= 0) {
			XlinkAgent.getInstance().getInstance().getDeviceSubscribeKey(device.getXDevice(), device.getXDevice().getAccessKey(), new GetSubscribeKeyListener() {
				@Override
				public void onGetSubscribekey(XDevice xdevice, int code, int subKey) {
					device.getXDevice().setSubKey(subKey);
					DeviceManage.getInstance().updateDevice(device);
				}
			});
		}
		if (!device.isSubscribe()) {
			XlinkAgent.getInstance().subscribeDevice(device.getXDevice(), device.getXDevice().getSubKey(), new SubscribeDeviceListener() {
				@Override
				public void onSubscribeDevice(XDevice xdevice, int code) {
					if (code == XlinkCode.SUCCEED) {
						device.setSubscribe(true);
					}
				}
			});
		}

		int ret = XlinkAgent.getInstance().connectDevice(device.getXDevice(), device.getXDevice().getAccessKey(), device.getXDevice().getSubKey(), connectDeviceListener);
		if (ret < 0) {
			//Debe hacer por socket

			String comandoSocket;
			String[] com = comandoPendiente.split(";");
			int deviceId = device.getXDevice().getDeviceId();
			if(device.getXDevice().getDeviceId() == 0){
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				deviceId = sharedPrefs.getInt(equipoSeleccionado.getNumeroSerie(), 0);
			}
			if(deviceId != 0) {
				nombreZonaT.setText("internet ");
				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");
				if (comandoPendiente != null && !comandoPendiente.equals("")) {
					if (comandoPendiente.startsWith(YACSmartProperties.COM_APAGAR_LUZ_WIFI)) {
						//apagar
						comandoSocket = YACSmartProperties.COM_APAGAR_LUZ_WIFI + ";" + deviceId + ";" + equipoSeleccionado.getNumeroSerie() + ";ANDROID;" + com[4] + ";";
					} else {
						//encender
						comandoSocket = YACSmartProperties.COM_ENCENDER_LUZ_WIFI + ";" + deviceId + ";" + equipoSeleccionado.getNumeroSerie() + ";ANDROID;" + com[4] + ";";
					}
					ComandoIOFocoScheduledTask comandoIOFocoScheduledTask = new ComandoIOFocoScheduledTask(comandoSocket);
					comandoIOFocoScheduledTask.start();
				}
			}


			//[NSString stringWithFormat:@"%@;%d;%@;%@;%@;%d;%@;",LUZ_INTERNET_LUZ_COLOR,device.deviceID,idEquipo,nombreDispositivo,fila.zona.numeroZona,[fila.zona.numeroZona containsString:@"R"],color];

//		}else{
//			if (comandoPendiente != null && !comandoPendiente.equals("")) {
//				ComandoFoco comandoFoco = new ComandoFoco(comandoPendiente, getApplicationContext());
//				comandoFoco.start();
//			}
		}
	}

	Boolean mobile = false;
	private ConnectDeviceListener connectDeviceListener = new ConnectDeviceListener() {

		@Override
		public void onConnectDevice(XDevice xDevice, int result) {
			String pass;
			String tips;

			if(Connectivity.isConnectedMobile(getApplicationContext())) {
				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");
				nombreZonaT.setText("Internet");
				mobile = true;
			}else{
//				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");
				txtEstado.setText("Internet");
				nombreZonaT.setText("Internet");
			}
			switch (result) {
				case XlinkCode.DEVICE_STATE_LOCAL_LINK:
					if(CmdManage.sesionFocos == null) {

						pass = Integer.toHexString(xDevice.getSessionId());
						CmdManage.sesionFocos = hexStringToByteArray(pass);
						ComandoFoco comandoFoco = new ComandoFoco(comandoPendiente, getApplicationContext());
						comandoFoco.start();
					}
					DeviceManage.getInstance().updateDevice(xDevice);
					XlinkAgent.getInstance().sendProbe(xDevice);
					break;
				case XlinkCode.DEVICE_STATE_OUTER_LINK:
					if(CmdManage.sesionFocos == null) {
						pass = Integer.toHexString(xDevice.getSessionId());
						CmdManage.sesionFocos = hexStringToByteArray(pass);
						ComandoFoco comandoFoco = new ComandoFoco(comandoPendiente, getApplicationContext());
						comandoFoco.start();
					}
					DeviceManage.getInstance().updateDevice(xDevice);
					DeviceManage.getInstance().addDevice(xDevice);
					break;
//				case XlinkCode.CONNECT_DEVICE_INVALID_KEY:
//					setDeviceStatus(false);
//					openDevicePassword();
//					Log.e(TAG, "Device:" + xDevice.getMacAddress() + "设备认证失败");
//					XlinkUtils.shortTips("设备认证失败");
//					break;
//				// 设备不在线
//				case XlinkCode.CONNECT_DEVICE_OFFLINE:
//					setDeviceStatus(false);
//					// Log.e(TAG, "Device:" + xDevice.getMacAddress() + "设备不在线");
//					XlinkUtils.shortTips("设备不在线");
//					Log("设备不在线");
//					break;
//
//				// 连接设备超时了，（设备未应答，或者服务器未应答）
//				case XlinkCode.CONNECT_DEVICE_TIMEOUT:
//					setDeviceStatus(false);
//					// Log.e(TAG, "Device:" + xDevice.getMacAddress() + "连接设备超时");
//					XlinkUtils.shortTips("连接设备超时");
//					break;
//
//				case XlinkCode.CONNECT_DEVICE_SERVER_ERROR:
//					setDeviceStatus(false);
//					XlinkUtils.shortTips("连接设备失败，服务器内部错误");
//
//					break;
//				case XlinkCode.CONNECT_DEVICE_OFFLINE_NO_LOGIN:
//					setDeviceStatus(false);
//					XlinkUtils.shortTips("连接设备失败，设备未在局域网内，且当前手机只有局域网环境");
//
//					break;
				default:
				//	XlinkUtils.shortTips("Otro error:" + result);
					break;
			}

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

	@Override
	public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

		if (requestCode == REQUEST_CAMERA) {
			new AlertDialog.Builder(LucesFragment.this)
					.setMessage("Puede utilizar la opción de capturar colores con su cámara")
					.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
		} else {
			new AlertDialog.Builder(LucesFragment.this)
					.setMessage("No podrá utilizar su cámara para capturar colores si no autoriza este permiso")
					.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
		}
	}




	private void animarEncenderColor(final int color) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int percent = 0;
				while(percent != 100){
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					percent += 1;

					circleTodos.changePercentage(percent,"on", Color.YELLOW, color, textSizeCircle);
				}
			}
		}).start();

	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}
	public void actualizarFotoPerfil() {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + equipoSeleccionado.getNumeroSerie() + ".jpg");
		if (file.exists()) {
			Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + equipoSeleccionado.getNumeroSerie() + ".jpg");
			if (bmImg != null) {

				File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + equipoSeleccionado.getNumeroSerie() + ".jpg");
				if (foto.exists()) {
					bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + equipoSeleccionado.getNumeroSerie() + ".jpg");
					if (bmImg != null) {
						mostrarImagen(bmImg);

					}
				}
			}
		} else {
			Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.logo8)).getBitmap();
			mostrarImagen(bitmap);

		}
	}




	Bitmap output;
	private void mostrarImagen( Bitmap bitmap) {
		output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 25;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(10);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mimageView.setImageBitmap(output);
				mimageView.bringToFront();
			}
		});

	}

	public void actualizarRouters(String routerRespuesta){
		String[] listaRouters = routerRespuesta.split("#");

		Boolean encontro = false;
		for(int i = 0; i < listaRouters.length; i++){
			String router = listaRouters[i];
			Log.d("COM_BUSCAR_ROUTER", "COM_BUSCAR_ROUTER 3 " + router);
			String[] r = router.split(",");
			if(r.length > 3) {
				if(equipoSeleccionado.getNumeroSerie().equals(r[1])){
					nombreZonaT.setText("TODAS\n" + equipoSeleccionado.getIpLocal());
					btnBuscarIbox.setVisibility(View.GONE);
					nombreZonaT.setVisibility(View.VISIBLE);
					equipoSeleccionado.setLuzWifi("WIFI");
					txtEstado.setText("wifi");
					encontro = true;

					ActualizarEquipoAsyncTask actualizarEquipoAsyncTask = new ActualizarEquipoAsyncTask(equipoSeleccionado);
					actualizarEquipoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					break;
				}
			}
		}
		if(!encontro){

			equipoSeleccionado.setLuzWifi("INTERNET");
			//nombreZonaT.setVisibility(View.GONE);
			//btnBuscarIbox.setVisibility(View.VISIBLE);
		}

	}

	private void ingresarRouter() {
		actualizarFotoPerfil();
		BuscarRutersAsyncTask buscarRutersAsyncTask = new BuscarRutersAsyncTask(LucesFragment.this);
		buscarRutersAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();
		txtNombreEquipo.setText(equipoSeleccionado.getNombreEquipo());
		if(equipoSeleccionado.getIdEquipoPadre() != null && !equipoSeleccionado.getIdEquipoPadre().equals("")){
			EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
			equipoDataSource.open();
			Equipo equipoPadre  = equipoDataSource.getEquipoId(equipoSeleccionado.getIdEquipoPadre());
			equipoDataSource.close();
			equipoSeleccionado.setEquipoPadre(equipoPadre);
		}

		constraintLayoutTotal.bringToFront();

		ViewGroup.LayoutParams params = constraintLayoutTotal.getLayoutParams();
		ViewGroup.LayoutParams paramsV = filaTotal.getLayoutParams();
		params.height = paramsV.height;
		constraintLayoutTotal.setLayoutParams(params);

		Animation scaleAnimation = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
		scaleAnimation.setDuration(750);
		scaleAnimation.setFillEnabled(true);
		scaleAnimation.setFillAfter(true);

		constraintLayoutTotal.startAnimation(scaleAnimation);

		ZonaDataSource zonaDataSource = new ZonaDataSource(getApplicationContext());
		zonaDataSource.open();
		zonas = zonaDataSource.getAllZonaRouter(datosAplicacion.getEquipoSeleccionado().getNumeroSerie().trim());
		zonaDataSource.close();
		zonaAdapter = new LucesFragment.LucesAdapter(getApplicationContext(), zonas);
		listZonas.setAdapter(zonaAdapter);

		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();
		if (equipoSeleccionado.getEquipoPadre() != null) {
			numeroSerieComando = datosAplicacion.getEquipoSeleccionado().getEquipoPadre().getNumeroSerie();
		} else {
			numeroSerieComando = datosAplicacion.getEquipoSeleccionado().getNumeroSerie();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				int percent = 0;
				while(percent != 100){
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					percent += 1;

					circleTodos.changePercentage(percent,"off", Color.BLACK, Color.GRAY, textSizeCircle);
				}
			}
		}).start();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ZonaDataSource zonaDataSource = new ZonaDataSource(getApplicationContext());
		zonaDataSource.open();
		zonas = zonaDataSource.getAllZonaRouter(datosAplicacion.getEquipoSeleccionado().getNumeroSerie().trim());
		zonaDataSource.close();
		zonaAdapter.notifyDataSetChanged();
	}

	class LucesAdapter extends ArrayAdapter<ZonaLuces> {
		private final Context context;
		private final ArrayList<ZonaLuces> values;

		public LucesAdapter(Context context, ArrayList<ZonaLuces> values) {
			super(context, R.layout.evento_list_item, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public int getCount() {
			return zonas.size();
		}

		@Override
		public ZonaLuces getItem(int position) {
			return zonas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Boolean primeraVez = false;
			if (convertView == null) {
				convertView = inflate(getApplicationContext(),
						R.layout.li_luces, null);
				new LucesFragment.LucesAdapter.ViewHolder(convertView);
				primeraVez = true;
			}
			final LucesFragment.LucesAdapter.ViewHolder holder = (LucesFragment.LucesAdapter.ViewHolder) convertView.getTag();
			final ZonaLuces item = getItem(position);
			holder.nombreZona.setText(item.getNombreZona());
			Typeface fontBold = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
			holder.nombreZona.setTypeface(fontBold);

			if(primeraVez) {
				holder.constraintLayout.bringToFront();
				ViewGroup.LayoutParams params = holder.constraintLayout.getLayoutParams();
				ViewGroup.LayoutParams paramsV =  holder.layoutLucesPrincipal.getLayoutParams();
				params.height = paramsV.height;
				holder.constraintLayout.setLayoutParams(params);

				Animation scaleAnimation = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
				scaleAnimation.setDuration(750);
				scaleAnimation.setFillEnabled(true);
				scaleAnimation.setFillAfter(true);

				holder.constraintLayout.startAnimation(scaleAnimation);
				new Thread(new Runnable() {
					@Override
					public void run() {
						int percent = 0;
						while (percent != 100) {
							try {
								Thread.sleep(15);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							percent += 1;

							holder.circle.changePercentage(percent, "off", Color.WHITE, Color.GRAY, textSizeCircle);
						}
					}
				}).start();
			}
			if(actualizar) {
				if (item.getToggleActivo()) {
					animarEncender(holder, Color.WHITE);
				}else{
					animarApagar(holder, Color.GRAY);
				}
				if(position == values.size() - 1){
					actualizar = false;
				}
			}

			holder.circle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!item.getToggleActivo()) {
						String comando = YACSmartProperties.COM_ENCENDER_LUZ_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_ENCENDER_LUZ_BOX_WIFI;
						}
						item.setToggleActivo(true);
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";"+ item.getIdRouter() + ";" ;
						ejecutarAccionLuces(datosConfT);
						animarEncender(holder, Color.WHITE);
						holder.btnApagarEncender.setText("Encender");
					} else {
						String comando = YACSmartProperties.COM_APAGAR_LUZ_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_APAGAR_LUZ_BOX_WIFI;
						}
						item.setToggleActivo(false);
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarApagar(holder, Color.GRAY);
						holder.btnApagarEncender.setText("Apagar");
					}
				}
			});
			holder.btnAzul.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "BA" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.BLUE);
					}
				}
			});

			holder.btnRosado.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "FF" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.parseColor("#E91E63"));
					}
				}
			});

			holder.btnNaranja.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "23" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.parseColor("#EF6C00"));
					}
				}
			});

			holder.btnBlanco.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_WHITE_WIFI;
						if (item.getNumeroZona().equals("R")) {
							comando = YACSmartProperties.COM_LUZ_WHITE_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + item.getIdRouter() + ";";;
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.parseColor("#FFFFFF"));
					}
				}
			});

			holder.btnNight.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null && !item.getNumeroZona().equals("R")) {
						String datosConfT = YACSmartProperties.COM_LUZ_NIGHT_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + item.getIdRouter() + ";";;
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.parseColor("#FFFFFF"));
					}
				}
			});
			holder.btnCalido.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null && !item.getNumeroZona().equals("R")) {
						String datosConfT = YACSmartProperties.COM_LUZ_CALIDA_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + item.getIdRouter() + ";";;
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.parseColor("#FFFFFF"));
					}
				}
			});
			holder.btnVerde.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "6E" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.GREEN);
					}
				}
			});

			holder.btnRojo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "19" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.RED);
					}
				}
			});
			holder.btnAmarillo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "3B" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.YELLOW);
					}
				}
			});

			holder.btnMorado.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "D9" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.parseColor("#AA00FF"));
					}
				}
			});

			holder.btnAqua.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "85" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.parseColor("#26C6DA"));
					}
				}
			});

			holder.btnLima.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(equipoSeleccionado != null) {
						String comando = YACSmartProperties.COM_LUZ_COLOR_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_LUZ_COLOR_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + "54" + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
						animarEncenderColor(holder, Color.parseColor("#69F0AE"));
					}
				}
			});

			holder.btnPopup.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PopupMenu popup = new PopupMenu(LucesFragment.this, v);
					if(item.getNumeroZona().equals("R")){
						popup.getMenuInflater().inflate(R.menu.popup_router, popup.getMenu());
					}else{
						popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
					}

					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem menuItem) {
							switch (menuItem.getItemId()) {
								case R.id.menu_camara:
									Intent i = new Intent(LucesFragment.this, ColoresAmbienteActivity.class);
									i.putExtra("zona", zonas.get(position));
									startActivity(i);
									break;
								case R.id.menu_programar:
									i = new Intent(LucesFragment.this, DetalleLucesFragment.class);
									i.putExtra("zona", zonas.get(position));
									startActivityForResult(i, 1);
									break;
							}
							return false;
						}
					});
					popup.show();
				}

			});
			holder.brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					Integer valor = seekBar.getProgress();
					if (equipoSeleccionado != null) {
						if(brillo) {
							String comando = YACSmartProperties.COM_LUZ_BRIGHTNESS_WIFI;
							if (item.getNumeroZona().equals("R")) {
								comando = YACSmartProperties.COM_LUZ_BRIGHTNESS_BOX_WIFI;
							}
							String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + valor + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
							ejecutarAccionLuces(datosConfT);
						}else{
							if (equipoSeleccionado != null) {
								String datosConfT = YACSmartProperties.COM_LUZ_SATURATION_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona()  + ";" + valor + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
								ejecutarAccionLuces(datosConfT);
							}
						}

					}
				}
			});


//			holder.saturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//				@Override
//				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//				}
//
//				@Override
//				public void onStartTrackingTouch(SeekBar seekBar) {
//				}
//
//				@Override
//				public void onStopTrackingTouch(SeekBar seekBar) {
//					Integer valor = seekBar.getProgress();
//					if (equipoSeleccionado != null) {
//						String datosConfT = YACSmartProperties.COM_LUZ_SATURATION_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona()  + ";" + valor + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";";
//						ejecutarAccionLuces(datosConfT);
//					}
//				}
//			});
			holder.btnApagarEncender.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(holder.btnApagarEncender.getText().toString().equals("Encender")){
						String comando = YACSmartProperties.COM_ENCENDER_LUZ_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_ENCENDER_LUZ_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";"+ item.getIdRouter() + ";" ;
						ejecutarAccionLuces(datosConfT);

					} else {
						String comando = YACSmartProperties.COM_APAGAR_LUZ_WIFI;
						if(item.getNumeroZona().equals("R")){
							comando = YACSmartProperties.COM_APAGAR_LUZ_BOX_WIFI;
						}
						String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + item.getIdRouter() + ";";
						ejecutarAccionLuces(datosConfT);
					}
		}
			});
			holder.radio_brillo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					brillo = true;
					holder.radio_brillo.setTextColor(Color.parseColor("#FFFF00"));
					holder.radio_saturacion.setTextColor(Color.parseColor("#6E6E6E"));
					holder.txtLeyenda.setText("el brillo esta disponible en cualquier tipo de foco");
				}
			});
			holder.radio_saturacion.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					brillo = false;
					holder.radio_brillo.setTextColor(Color.parseColor("#6E6E6E"));
					holder.radio_saturacion.setTextColor(Color.parseColor("#FFFF00"));
					holder.txtLeyenda.setText("saturación disponible en focos de 8w, lámparas de exterior y dicroicos de 4w");
				}
			});
			holder.radio_efecto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ejecutarAmbiente(item);
				}
			});
			return convertView;



		}

		private void ejecutarAmbiente(final ZonaLuces item) {
			AlertDialog.Builder builder = new AlertDialog.Builder(LucesFragment.this);
			LayoutInflater inflater = getLayoutInflater();
			View dialoglayout = inflater.inflate(R.layout.vi_mascolores, null);
			TextView txtNombreEquipoI = (TextView) dialoglayout.findViewById(R.id.txtNombreEquipo);
			txtNombreEquipoI.setText("Ambientes: " + item.getNombreZona());
			ImageButton fabSalirI = (ImageButton) dialoglayout.findViewById(R.id.fabSalir);
			Button dplus = (Button) dialoglayout.findViewById(R.id.dplus);
			Button dminus = (Button) dialoglayout.findViewById(R.id.dminus);
			dplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_DISCO_FASTER_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_DISCO_FASTER_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);
                }
            });

			dminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_DISCO_SLOWLER_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_DISCO_SLOWLER_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";" + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);
                }
            });
			ImageButton colorChange = (ImageButton) dialoglayout.findViewById(R.id.colorChange);
			colorChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";09" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);
                }
            });

			ImageButton colorFade = (ImageButton) dialoglayout.findViewById(R.id.colorFade);
			colorFade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";02" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);
                }
            });

			ImageButton colorBlink = (ImageButton) dialoglayout.findViewById(R.id.colorBlink);
			colorBlink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";08" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);
                }
            });

			ImageButton whiteBlink = (ImageButton) dialoglayout.findViewById(R.id.whiteBlink);
			whiteBlink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";01" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);
                }
            });

			ImageButton greenBlink = (ImageButton) dialoglayout.findViewById(R.id.greenBlink);
			greenBlink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";06" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);

                }
            });

			ImageButton blueBlink = (ImageButton) dialoglayout.findViewById(R.id.blueBlink);
			blueBlink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";07" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);
                }
            });

			ImageButton redBlink = (ImageButton) dialoglayout.findViewById(R.id.redBlink);
			redBlink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";05" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);

                }
            });

			ImageButton rainbow = (ImageButton) dialoglayout.findViewById(R.id.rainbow);
			rainbow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";04" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);
                }
            });

			ImageButton discos = (ImageButton) dialoglayout.findViewById(R.id.discos);
			discos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comando = YACSmartProperties.COM_LUZ_MODE_WIFI;
                    if(item.getNumeroZona().equals("R")){
                        comando = YACSmartProperties.COM_LUZ_MODE_BOX_WIFI;
                    }
                    String datosConfT = comando + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + numeroSerieComando + ";" + item.getNumeroZona() + ";03" + ";"  + item.getIdRouter() + ";";
                    ejecutarAccionLuces(datosConfT);

                }
            });
			builder.setView(dialoglayout);
			final AlertDialog alertDialog = builder.create();
			fabSalirI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
			alertDialog.show();
		}

		private void animarApagar(final ViewHolder holder, final int color) {
			new Thread(new Runnable() {
                @Override
                public void run() {
                    int percent = 0;
                    while(percent  != 100){
                        try {
                            Thread.sleep(15);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        percent += 1;
                        holder.circle.changePercentage(percent,"off", Color.WHITE, color, textSizeCircle);
                    }
                }
            }).start();
			holder.constraintLayout.bringToFront();
			ViewGroup.LayoutParams params = holder.constraintLayout.getLayoutParams();
			ViewGroup.LayoutParams paramsV =  holder.layoutLucesPrincipal.getLayoutParams();
			params.height = paramsV.height;
			holder.constraintLayout.setLayoutParams(params);

			Animation scaleAnimation = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
			scaleAnimation.setDuration(750);
			scaleAnimation.setFillEnabled(true);
			scaleAnimation.setFillAfter(true);
			holder.constraintLayout.startAnimation(scaleAnimation);
		}

		private void animarEncender(final ViewHolder holder, final int color) {
			new Thread(new Runnable() {
                @Override
                public void run() {
                    int percent = 0;
                    while(percent != 100){
                        try {
                            Thread.sleep(15);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        percent += 1;

                        holder.circle.changePercentage(percent,"on", Color.YELLOW, color, textSizeCircle);
                    }
                }
            }).start();
			ViewGroup.LayoutParams params = holder.constraintLayout.getLayoutParams();
			ViewGroup.LayoutParams paramsV = holder.layoutLucesPrincipal.getLayoutParams();
			params.height = paramsV.height;
			holder.constraintLayout.setLayoutParams(params);
			Animation scaleAnimation = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
			scaleAnimation.setDuration(750);
			scaleAnimation.setFillEnabled(true);
			scaleAnimation.setFillAfter(true);
			holder.constraintLayout.startAnimation(scaleAnimation);
		}
		private void animarEncenderColor(final ViewHolder holder, final int color) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					int percent = 0;
					while(percent != 100){
						try {
							Thread.sleep(15);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						percent += 1;
						holder.circle.changePercentage(percent,"on", Color.YELLOW, color, textSizeCircle);
					}
				}
			}).start();

		}
		class ViewHolder {
			TextView nombreZona;
			ViewCircle circle;
			RelativeLayout constraintLayout;
			FloatingActionButton btnAzul, btnRosado, btnNaranja, btnBlanco, btnVerde, btnRojo, btnAmarillo, btnMorado, btnAqua, btnLima, btnNight, btnCalido;
			LinearLayout lucesLayout;
			RelativeLayout layoutLucesPrincipal;
			ImageView btnPopup;
			Button btnApagarEncender;
			SeekBar brightness;
			//saturation;
			TextView radio_brillo, radio_saturacion, radio_efecto, txtLeyenda;

			public ViewHolder(View view) {
				nombreZona = (TextView) view.findViewById(R.id.nombreZona);
				circle = (ViewCircle) view.findViewById(R.id.circle);
				constraintLayout = (RelativeLayout) view.findViewById(R.id.constraintLayout);
				btnAzul = (FloatingActionButton) view.findViewById(R.id.btnAzul);
				btnRosado = (FloatingActionButton) view.findViewById(R.id.btnRosado);
				btnNaranja = (FloatingActionButton) view.findViewById(R.id.btnNaranja);
				btnBlanco = (FloatingActionButton) view.findViewById(R.id.btnBlanco);
				btnVerde = (FloatingActionButton) view.findViewById(R.id.btnVerde);
				btnRojo = (FloatingActionButton) view.findViewById(R.id.btnRojo);
				btnAmarillo = (FloatingActionButton) view.findViewById(R.id.btnAmarillo);
				btnMorado = (FloatingActionButton) view.findViewById(R.id.btnMorado);
				btnAqua = (FloatingActionButton) view.findViewById(R.id.btnAqua);
				btnLima = (FloatingActionButton) view.findViewById(R.id.btnLima);
				btnNight = (FloatingActionButton) view.findViewById(R.id.btnNight);
				btnCalido = (FloatingActionButton) view.findViewById(R.id.btnCalido);
				lucesLayout = (LinearLayout) view.findViewById(R.id.lucesLayout);
				layoutLucesPrincipal = (RelativeLayout) view.findViewById(R.id.layoutLucesPrincipal);
				btnPopup = (ImageView) view.findViewById(R.id.btnPopup);
				btnPopup.setColorFilter(Color.WHITE);
				brightness = (SeekBar) view.findViewById(R.id.brightness);
				radio_brillo = (TextView) view.findViewById(R.id.radio_brillo);
				radio_efecto = (TextView) view.findViewById(R.id.radio_efecto);
				radio_saturacion = (TextView) view.findViewById(R.id.radio_saturacion);
				txtLeyenda = (TextView) view.findViewById(R.id.txtLeyenda);
				btnApagarEncender = (Button) view.findViewById(R.id.btnApagarEncender);

				view.setTag(this);
				//constraintLayout.bringToFront();
			}
		}
	}
	String comandoPendiente ="";

	private void ejecutarAccionLuces(String datosConfT) {
		if(equipoSeleccionado.getEquipoPadre() != null) {
             if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
                EnviarComandoThread enviarComandoThread = new EnviarComandoThread(LucesFragment.this, datosConfT, null,
                        null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
                enviarComandoThread.start();
            } else {
                AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
                AudioQueu.contadorComandoEnviado++;
            }
        }else{
            if(datosAplicacion.getEquipoSeleccionado().getLuzWifi() != null && datosAplicacion.getEquipoSeleccionado().getLuzWifi().equals("WIFI")) {
                //WIFI
				//XlinkUtils.shortTips("Antes de Comando Foco");
                ComandoFoco comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
                comandoFoco.start();
            }else {
				//INTERNET
				if (CmdManage.sesionFocos == null) {
					if(device != null) {
						comandoPendiente = datosConfT;
						connectDevice();

					}else{
						//Nunca se ha conectado en la wifi
						new SweetAlertDialog(LucesFragment.this, SweetAlertDialog.SUCCESS_TYPE)
								.setTitleText("Información")
								.setContentText("Para activar el control desde internet, debe conectarse a la red de su iBox.")
								.showCancelButton(true)
								.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.cancel();

									}
								})
								.show();
					}

				} else {
					//Controlando con xLink desde internet
					if (mobile){
						if(datosConfT.startsWith(YACSmartProperties.COM_APAGAR_LUZ_WIFI ) || datosConfT.startsWith(YACSmartProperties.COM_ENCENDER_LUZ_WIFI)) {
							ComandoFoco comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
							comandoFoco.start();
						}
					}else{
						ComandoFoco comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
						comandoFoco.start();
					}
				}
			}

        }
	}
}