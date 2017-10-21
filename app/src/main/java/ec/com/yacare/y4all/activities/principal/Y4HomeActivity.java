package ec.com.yacare.y4all.activities.principal;

import android.Manifest;
import android.app.AlertDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.PreferenciasActivity;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.SplashActivity;
import ec.com.yacare.y4all.activities.cuenta.IngresarClaveActivity;
import ec.com.yacare.y4all.activities.evento.EventosActivity;
import ec.com.yacare.y4all.activities.instalacion.InstalarEquipoActivity;
import ec.com.yacare.y4all.activities.luces.LucesFragment;
import ec.com.yacare.y4all.activities.portero.LlamadaEntrantePorteroActivity;
import ec.com.yacare.y4all.activities.recomendacion.RecomendarActivity;
import ec.com.yacare.y4all.activities.respuesta.AdministrarRespuestasActivity;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.adapter.ConfiguracionArrayAdapter;
import ec.com.yacare.y4all.adapter.DispositivoListaArrayAdapter;
import ec.com.yacare.y4all.adapter.EventoListaArrayAdapter;
import ec.com.yacare.y4all.adapter.EventoListaBuzonArrayAdapter;
import ec.com.yacare.y4all.adapter.SeleccionarEquipoAdapter;
import ec.com.yacare.y4all.asynctask.ws.InvitarCuentaAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.RecibirComandoRemotoAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.io.ComandoIOScheduledTask;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.DispositivoDataSource;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.util.ViewReloj;
import ec.com.yacare.y4all.lib.util.ViewRelojHora;
import ec.com.yacare.y4all.lib.util.ViewRelojSegundo;
import ec.com.yacare.y4all.lib.util.indicator.AVLoadingIndicatorView;


public class Y4HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

	public TextView textoNombre;

	private ImageView mimageView;

	private static final String[] opciones =
			new String[]{"0:Botones", "1:Controlar la luz", "2:Contestación automática", "3:Revisar mis visitas", "4:Recomienda Wii Bell", "5:Intégralo a tu barrio", "6:Acerca de"};


	public Equipo equipoSeleccionado;
	private ArrayList<Evento> eventoCol;

	private TextView minuto1;
	private SeekBar seekbarExterno;

	private Typeface fontRegular;

	private AlertDialog.Builder alertDialog;
	private SeleccionarEquipoAdapter seleccionarEquipoAdapter;

	private DatosAplicacion datosAplicacion;

	private String[] respuesta;

	private String nombreDispositivo;

	private static final int NUM_PAGES = 5;

	private ViewPager mPager;

	private FotosPagerAdapter mPagerAdapter;

	private AVLoadingIndicatorView indicatorView;

	public Button btnHoy;
	private ImageButton btnVisitas;
	private ImageButton btnVideo;
	private ImageButton btnDispositivos;
	private ImageButton btnSensor;

	private TextView txtVisitas;
	private TextView txtVideo;
	private TextView txtDispositivo;
	private TextView txtEstado;
	private TextView txtNombreEquipo;
	private TextView txtSensor;

	private Button btnEncenderLuz;
	private Button btnAbrirPuerta;
	private ImageButton btnMonitorear;

	public RelativeLayout constraintLayout, badge_layout4, badge_layout1, badge2, badge3, badge4;

	public TextView txtMensaje;

	private FloatingActionButton btnCuenta, btnRespuesta, btnPreferencias, btnAplicacion, btnNuevo, btnCompartir, btnVista;
	private FloatingActionMenu menuDown;

	private ListView listVisitas, listBuzon, listDispositivos, listSensor;
	private ListView listOpciones;

	private ArrayList<Evento> eventosHoy;

	private DispositivoListaArrayAdapter dispositivoArrayAdapter;

	private static int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
	private int numeroPermisos = 0;

	private int numeroVisitasAnterior = 0;

	public static ViewRelojSegundo circleProgressSegundo;
	public static ViewReloj circleProgressMinuto;
	public static ViewRelojHora circleProgressHora;
	public static LinearLayout layoutReloj;
	public static TextView textYacare;

	private LinearLayout layoutVacio;
	private ImageView imgVacio;
	private TextView txtVacio;
	private ImageView question;

	//Ayuda
	private GridLayout tabInferior;

	public TextView textoAyuda;
	public Button btnAyuda;

	public TextView tituloAyuda;
	private ImageView imagenAyuda;
	private LinearLayout layoutAyuda;
	private Integer contadorAyuda = 0;

	private static String titulo0 = "SACALE EL MAXIMO PROVECHO";
	private static String titulo1 = "CONOCE QUE PERSONAS TE VISITARON HOY";
	private static String titulo2 = "BUZONES GUARDADOS";
	private static String titulo3 = "APERTURA DE PUERTA";
	private static String titulo4 = "LISTA DE DISPOSITIVOS AUTORIZADOS";
	private static String titulo5 = "CONOCE LA CALIDAD DE SEÑAL";


	private static String leyenda0  = "Tienes acceso a internet? te explicamos que puedes hacer al seleccionar del menu las siguientes opciones:\n\n- Monitorear mi casa, te permite mirar y escuchar en cualquier momento lo que ocurre fuera de casa, su buen uso es tu responsabilidad.\n\n- Luz externa, un foco tradicional te dará comodidad y seguridad al llegar a casa por la noche; además de ver con claridad al visitante, colócalo sobre el portero y su utilidad sera genial.\n\n- Si compraste nuestras luces wifi, podrás controlar y programar cuando encenderlas. Si sales de viaje podrás progarmarlas para que se enciendan y simulen presencia, y porque no pensar en encenderlas de un color rosado en la fiesta de 15 años de tu hija, pon a prueba tu creatividad y usa colores especiales para alegrar el día o crear un ambiente romántico (aún no los compraste presiona aquí y nos pondremos en contacto contigo).\n\n- Activa el contestador automático si no vas a estar en casa; así cuando, no puedas atender WiiBell responderá por tí creando un buzón de mensajes para revisarlo cuando tengas acceso a internet\n\n- Revisar mis visitas, en el momento que lo necesites revisa que personas visitaron tú casa, y marca como favoritas aquellas visitas que mas te interesen\n\n - Recomienda a otras personas y activa nuevas funcionalidades\n\n- También puedes integrarlo a tu comunidad o barrio";
	private static String leyenda1  = "Una vista rápida de las visitas de hoy \n\n- Puedes visualizar una foto y hora de la visita\n\n- Junto a la foto al seleccionar el ícono podrás descargar un video corto del visitante\n\n- Cada día esta lista se vacía\n- Si quieres ver mas grande la foto, solo seleccionala y la podrás observar en un tamaño amplido";
	private static String leyenda2  = "Si activaste la contestación automática y no pudiste atender, aquí una vista rápida de los buzones creados, descárgalo y puedes reproducir.";
	private static String leyenda3  = "Si activaste el sensor de apertura de puerta, siempre estarás informado cuando se abre la puerta de tu casa. Adicionalmente, recibirás un video de lo que sucedió en el momento que se abrió la puerta.";
	private static String leyenda4  = "Una lista de los teléfonos que se conectan a tu portero";
	private static String leyenda5  = "Si tu portero utiliza cable de red un color naranja se presenta, esta conexión es la recomendada\n\nSi tu portero utiliza wifi, se despliegan 3 colores\n\n- Rojo, la señal es mala, recomendamos colocar una repetidora para mejorar la señal o utilizar cable de red\n- Amarillo, la calidad es aceptable pero no se debe descartar buscar opciones para mejorarla\n- Verde, muy buena señal.";

	private EventoListaArrayAdapter eventoListaArrayAdapter;
	private EventoListaBuzonArrayAdapter eventoListaBuzonArrayAdapter;
	private ConfiguracionArrayAdapter configuracionArrayAdapter;

	private Boolean vistaAvanzada = true;

	private Boolean comandoLocalCorriendo = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isScreenLarge()) {
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			vistaAvanzada = sharedPrefs.getBoolean("prefVistaAvanzada", false);
			if (vistaAvanzada) {
				setContentView(R.layout.ac_y4home);
			} else {
				setContentView(R.layout.ac_y4home_simple);
			}
		}else{
			setContentView(R.layout.ac_y4home);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(ContextCompat.getColor(Y4HomeActivity.this, R.color.primaryDarkColor));
		}
		View decorView = getWindow().getDecorView();

		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		decorView.setSystemUiVisibility(uiOptions);

		AudioQueu.isInBackground = false;

		if(PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
				PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)){
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/");
			if (!file.exists()) {
				if (!file.mkdirs()) {


				}
			}
		}else{
			if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
					PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)) {
				numeroPermisos = 2;
				ActivityCompat.requestPermissions(Y4HomeActivity.this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
						REQUEST_WRITE_EXTERNAL_STORAGE);
			}else if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
				numeroPermisos = 1;
				ActivityCompat.requestPermissions(Y4HomeActivity.this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						REQUEST_WRITE_EXTERNAL_STORAGE);
			}else if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)){
				numeroPermisos = 1;
				ActivityCompat.requestPermissions(Y4HomeActivity.this,
						new String[]{Manifest.permission.RECORD_AUDIO},
						REQUEST_WRITE_EXTERNAL_STORAGE);
			}
		}
		validarIngreso();

	}

	private void validarIngreso() {

		menuDown = (FloatingActionMenu) findViewById(R.id.menu_down);
		menuDown.bringToFront();

		datosAplicacion = (DatosAplicacion) getApplicationContext();
		fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();
		ingresarY4Home();
		if (isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	public Boolean orientacionPortrait = false;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			layoutReloj.setVisibility(View.VISIBLE);
			orientacionPortrait = false;
			View rectangle_at_the_top = findViewById(R.id.rectangle_at_the_top);
			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) rectangle_at_the_top.getLayoutParams();
			params.height = 50;
			LinearLayout.LayoutParams paramsG = (LinearLayout.LayoutParams) tabInferior.getLayoutParams();
			paramsG.weight =0.09f;
			LinearLayout detalleTab = (LinearLayout) findViewById(R.id.detalleTab);
			LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) detalleTab.getLayoutParams();
			paramsL.weight =0.18f;
			RelativeLayout layout_inicial =  (RelativeLayout) findViewById(R.id.layout_inicial);
			LinearLayout.LayoutParams paramsR = (LinearLayout.LayoutParams) layout_inicial.getLayoutParams();
			paramsR.weight =0.13f;
			GridLayout layout_inferior =  (GridLayout) findViewById(R.id.layout_inferior);
			LinearLayout.LayoutParams paramsGr = (LinearLayout.LayoutParams) layout_inferior.getLayoutParams();
			paramsGr.weight =0.07f;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			layoutReloj.setVisibility(View.GONE);
			orientacionPortrait = true;
			View rectangle_at_the_top = findViewById(R.id.rectangle_at_the_top);
			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) rectangle_at_the_top.getLayoutParams();
			params.height = 85;
			LinearLayout.LayoutParams paramsG = (LinearLayout.LayoutParams) tabInferior.getLayoutParams();
			paramsG.weight =0.07f;
			LinearLayout detalleTab = (LinearLayout) findViewById(R.id.detalleTab);
			LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) detalleTab.getLayoutParams();
			paramsL.weight =0.25f;
			RelativeLayout layout_inicial =  (RelativeLayout) findViewById(R.id.layout_inicial);
			LinearLayout.LayoutParams paramsR = (LinearLayout.LayoutParams) layout_inicial.getLayoutParams();
			paramsR.weight =0.1f;
			GridLayout layout_inferior =  (GridLayout) findViewById(R.id.layout_inferior);
			LinearLayout.LayoutParams paramsGr = (LinearLayout.LayoutParams) layout_inferior.getLayoutParams();
			paramsGr.weight =0.05f;
		}
		configuracionArrayAdapter.notifyDataSetChanged();
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

		if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
			if (grantResults.length > 1
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/");
				if (!file.exists()) {
					if (!file.mkdirs()) {


					}
				}
			} else 	if (grantResults.length == 1
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/");
				if (!file.exists()) {
					if (!file.mkdirs()) {


					}
				}
			}
		} else {
			new AlertDialog.Builder(Y4HomeActivity.this)
					.setMessage("La aplicación no funcionara correctamente sin el permiso solicitado")
					.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).show();
		}
	}




	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	private void ingresarY4Home() {

//		AudioQueu.isInBackground = true;
//
//		VerificarRedAsyncTask checkearRedAsyncTask = new VerificarRedAsyncTask(Y4HomeActivity.this);
//		checkearRedAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		circleProgressSegundo = (ViewRelojSegundo) findViewById(R.id.circleProgress);
		circleProgressMinuto = (ViewReloj) findViewById(R.id.circleProgress1);
		circleProgressHora = (ViewRelojHora) findViewById(R.id.circleProgress2);
		layoutReloj = (LinearLayout) findViewById(R.id.layoutReloj);
		textYacare = (TextView) findViewById(R.id.txtYacare);
		tabInferior = (GridLayout) findViewById(R.id.tabInferior);
		question = (ImageView) findViewById(R.id.question);
		imagenAyuda = (ImageView) findViewById(R.id.imagenAyuda);
		textoAyuda = (TextView) findViewById(R.id.textoAyuda);
		tituloAyuda = (TextView) findViewById(R.id.tituloAyuda);
		btnAyuda = (Button) findViewById(R.id.btnAyuda);
		layoutAyuda = (LinearLayout) findViewById(R.id.layoutAyuda);
		btnAbrirPuerta = (Button) findViewById(R.id.btnAbrirPuerta);
		btnEncenderLuz = (Button) findViewById(R.id.btnEncenderLuz);
		btnMonitorear = (ImageButton) findViewById(R.id.btnMonitorear);

		badge_layout4 = (RelativeLayout) findViewById(R.id.badge_layout4);
		badge_layout1 = (RelativeLayout) findViewById(R.id.badge_layout1);
		badge2 = (RelativeLayout) findViewById(R.id.badge2);
		badge3 = (RelativeLayout) findViewById(R.id.badge3);
		badge4 = (RelativeLayout) findViewById(R.id.badge4);


//		layoutEstado = (RelativeLayout) findViewById(R.id.layoutEstado);
		question.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				constraintLayout.setVisibility(View.GONE);
				mPager.setVisibility(View.GONE);
				layoutAyuda.setVisibility(View.VISIBLE);
				btnHoy.callOnClick();
				imagenAyuda.setImageBitmap(getBitmapFromView(tabInferior));
				contadorAyuda = 0;
				tituloAyuda.setText(Y4HomeActivity.titulo0);
				textoAyuda.setText(Y4HomeActivity.leyenda0);

			}
		});

		btnAyuda.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				contadorAyuda = contadorAyuda + 1;
				if(contadorAyuda.equals(1)){
					btnVisitas.callOnClick();
					imagenAyuda.setImageBitmap(getBitmapFromView(tabInferior));
					tituloAyuda.setText(Y4HomeActivity.titulo1);
					textoAyuda.setText(Y4HomeActivity.leyenda1);
				}else if(contadorAyuda.equals(2)){
					btnVideo.callOnClick();
					imagenAyuda.setImageBitmap(getBitmapFromView(tabInferior));
					tituloAyuda.setText(Y4HomeActivity.titulo2);
					textoAyuda.setText(Y4HomeActivity.leyenda2);
				}else if(contadorAyuda.equals(3)){
					btnSensor.callOnClick();
					imagenAyuda.setImageBitmap(getBitmapFromView(tabInferior));
					tituloAyuda.setText(Y4HomeActivity.titulo3);
					textoAyuda.setText(Y4HomeActivity.leyenda3);
				}else if(contadorAyuda.equals(4)){
					btnDispositivos.callOnClick();
					imagenAyuda.setImageBitmap(getBitmapFromView(tabInferior));
					tituloAyuda.setText(Y4HomeActivity.titulo4);
					textoAyuda.setText(Y4HomeActivity.leyenda4);
				}else if(contadorAyuda.equals(5)){
					btnHoy.callOnClick();
					imagenAyuda.setImageBitmap(getBitmapFromView(indicatorView));
					tituloAyuda.setText(Y4HomeActivity.titulo5);
					textoAyuda.setText(Y4HomeActivity.leyenda5);
				}else if(contadorAyuda.equals(6)){
					mPager.setVisibility(View.VISIBLE);
					layoutAyuda.setVisibility(View.GONE);
					constraintLayout.setVisibility(View.VISIBLE);
				}else if(contadorAyuda.equals(7)){
				}
			}
		});

		if(btnAbrirPuerta != null){
			btnAbrirPuerta.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
						//Wifi
						new SweetAlertDialog(Y4HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
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
										String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_UDP + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + "" + ";";
//										EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT, null, null,
//												null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
//										enviarComandoThread.start();

										AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
												YACSmartProperties.COM_ABRIR_PUERTA_UDP + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + "" + ";");
										AudioQueu.contadorComandoEnviado++;
										sDialog.cancel();

									}
								})
								.show();
					}else{
						//Internet
						new SweetAlertDialog(Y4HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
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
										AlertDialog.Builder alert = new AlertDialog.Builder(Y4HomeActivity.this);
										alert.setTitle("Ingrese su clave");
										final EditText input = new EditText(Y4HomeActivity.this);
										input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_TEXT_VARIATION_PASSWORD);
										input.setRawInputType(Configuration.KEYBOARD_12KEY);
										alert.setView(input);
										alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
														YACSmartProperties.COM_ABRIR_PUERTA + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + YACSmartProperties.Encriptar(input.getText().toString()) + ";");
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



				}
			});
		}
		if(btnMonitorear != null){
			btnMonitorear.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					monitorear();
				}
			});
		}
		if(btnEncenderLuz != null){
			btnEncenderLuz.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					String datosConfT = "C70" + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + "-1" + ";";
					String datosConfT = YACSmartProperties.COM_ENCENDER_LUZ + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + "-1" + ";";
					if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT, null,
								null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();
					}
				}
			});
		}
//		VerificarRedAsyncTask checkearRedAsyncTask = new VerificarRedAsyncTask(Y4HomeActivity.this);
//		checkearRedAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		datosAplicacion.setY4HomeActivity(this);

		textoNombre = (TextView) findViewById(R.id.textoNombreUsuario);
		btnHoy = (Button) findViewById(R.id.btnHoy);
		btnVideo = (ImageButton) findViewById(R.id.btnVideo);
		btnVisitas = (ImageButton) findViewById(R.id.btnVisitas);
		btnDispositivos = (ImageButton) findViewById(R.id.btnDispositivos);
		btnSensor= (ImageButton) findViewById(R.id.btnSensor);
		txtVisitas = (TextView) findViewById(R.id.badgeVisitas);
		txtVideo = (TextView) findViewById(R.id.badgeVideo);
		txtDispositivo = (TextView) findViewById(R.id.badgeDispositivos);
		txtSensor = (TextView) findViewById(R.id.badgeSensor);

		btnAplicacion = (FloatingActionButton) findViewById(R.id.btnApp);
		btnNuevo = (FloatingActionButton) findViewById(R.id.btnNuevo);
		btnCompartir = (FloatingActionButton) findViewById(R.id.btnCompartir);
		btnVista = (FloatingActionButton) findViewById(R.id.btnVista);


		btnCuenta = (FloatingActionButton) findViewById(R.id.btnCuenta);
		//btnTelefono = (FloatingActionButton) findViewById(R.id.btnTelefono);
		btnRespuesta = (FloatingActionButton) findViewById(R.id.btnRespuesta);
		btnPreferencias = (FloatingActionButton) findViewById(R.id.btnPreferencias);
		txtEstado  = (TextView) findViewById(R.id.txtEstado);
		txtNombreEquipo  = (TextView) findViewById(R.id.txtNombreEquipo);
		listVisitas = (ListView) findViewById(R.id.listVisitas);
		listBuzon = (ListView) findViewById(R.id.listBuzon);
		listDispositivos = (ListView) findViewById(R.id.listDispositivos);
		listSensor = (ListView) findViewById(R.id.listSensor);
		constraintLayout = (RelativeLayout) findViewById(R.id.constraintLayout);
		txtMensaje = (TextView) findViewById(R.id.txtMensaje);
		layoutVacio = (LinearLayout) findViewById(R.id.layoutVacio);
		txtVacio = (TextView) findViewById(R.id.txtVacio);
		imgVacio = (ImageView) findViewById(R.id.imgVacio);

		txtEstado.setText("buscando red...");
		txtNombreEquipo.setText(equipoSeleccionado.getNombreEquipo());

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
		Date date = new Date();

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

		if(btnVista != null) {
			btnVista.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					String titulo = " Avanzada";
//					if (vistaAvanzada) {
//						titulo = " Simplificada";
//					}
//					new SweetAlertDialog(Y4HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
//							.setTitleText(YACSmartProperties.intance.getMessageForKey("cambiar.vista"))
//							.setContentText(YACSmartProperties.intance.getMessageForKey("cambiar.vista.subtitulo") + titulo)
//							.setCancelText("NO")
//							.setConfirmText("SI")
//							.showCancelButton(true)
//							.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//								@Override
//								public void onClick(SweetAlertDialog sDialog) {
//									sDialog.cancel();
//									menuDown.close(true);
//								}
//							})
//							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//								@Override
//								public void onClick(SweetAlertDialog sDialog) {
									SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

									if (vistaAvanzada) {
										SharedPreferences.Editor editor = sharedPrefs.edit();
										editor.putBoolean("prefVistaAvanzada", false);
										editor.apply();
										editor.commit();
										setContentView(R.layout.ac_y4home_simple);
									} else {
										SharedPreferences.Editor editor = sharedPrefs.edit();
										editor.putBoolean("prefVistaAvanzada", true);
										editor.apply();
										editor.commit();
										setContentView(R.layout.ac_y4home);
									}

									vistaAvanzada = !vistaAvanzada;

									finish();
									Intent i = new Intent(Y4HomeActivity.this, SplashActivity.class);
									startActivity(i);

//								}
//							})
//							.show();

				}
			});
		}

//		if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("cambioVista")){
//			AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_NOTIFICAR_IP_ACTUALIZADO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";"+ UUID.randomUUID().toString() + ";");
//			AudioQueu.contadorComandoEnviado++;
//		}

		if(vistaAvanzada) {
			txtVisitas.setVisibility(View.GONE);
			txtVideo.setVisibility(View.GONE);
			txtDispositivo.setVisibility(View.GONE);
			txtSensor.setVisibility(View.GONE);
			listVisitas.setOnItemClickListener(Y4HomeActivity.this);

			constraintLayout.bringToFront();
			btnHoy.setText(Html.fromHtml("<b>HOY</b><br/><small>" + dateFormat.format(date) + "</small>"));

			btnVideo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					layoutVacio.setVisibility(View.GONE);
					btnHoy.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnHoy.setTextColor(Color.parseColor("#FFFFFF"));

					badge_layout4.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge_layout1.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge2.setBackgroundColor(Color.parseColor("#FFFFFF"));
					badge3.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge4.setBackgroundColor(Color.parseColor("#00C1E1"));


					btnVisitas.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnVisitas.setColorFilter(Color.parseColor("#FFFFFF"));

					btnDispositivos.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnDispositivos.setColorFilter(Color.parseColor("#FFFFFF"));

					btnVideo.setBackgroundColor(Color.parseColor("#FFFFFF"));
					btnVideo.setColorFilter(Color.parseColor("#00C1E1"));

					btnSensor.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnSensor.setColorFilter(Color.parseColor("#FFFFFF"));
					listSensor.setVisibility(View.GONE);

					listDispositivos.setVisibility(View.GONE);
					listVisitas.setVisibility(View.GONE);
					listBuzon.setVisibility(View.VISIBLE);
					listOpciones.setVisibility(View.GONE);

					EventoDataSource eventoDataSource = new EventoDataSource(getApplicationContext());
					eventoDataSource.open();
					eventosHoy = eventoDataSource.getEventosEquipoBuzonHoy(equipoSeleccionado.getId());
					eventoDataSource.close();
					eventoListaBuzonArrayAdapter = new EventoListaBuzonArrayAdapter(getApplicationContext(), eventosHoy, Y4HomeActivity.this);
					listBuzon.setAdapter(eventoListaBuzonArrayAdapter);
					listBuzon.setClickable(true);
					listBuzon.setOnItemClickListener(Y4HomeActivity.this);

					if (eventosHoy.size() == 0) {
						layoutVacio.setVisibility(View.VISIBLE);
						listBuzon.setVisibility(View.GONE);
						imgVacio.setImageResource(R.drawable.lista_vacia_buzon);
						txtVacio.setText("No tiene buzones de visitas de hoy");
					}

				}
			});

			btnVisitas.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					badge_layout4.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge_layout1.setBackgroundColor(Color.parseColor("#FFFFFF"));
					badge2.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge3.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge4.setBackgroundColor(Color.parseColor("#00C1E1"));

					layoutVacio.setVisibility(View.GONE);
					btnHoy.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnHoy.setTextColor(Color.parseColor("#FFFFFF"));

					btnVideo.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnVideo.setColorFilter(Color.parseColor("#FFFFFF"));

					btnDispositivos.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnDispositivos.setColorFilter(Color.parseColor("#FFFFFF"));

					btnVisitas.setBackgroundColor(Color.parseColor("#FFFFFF"));
					btnVisitas.setColorFilter(Color.parseColor("#00C1E1"));

					btnSensor.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnSensor.setColorFilter(Color.parseColor("#FFFFFF"));
					listSensor.setVisibility(View.GONE);

					listDispositivos.setVisibility(View.GONE);
					listVisitas.setVisibility(View.VISIBLE);
					listBuzon.setVisibility(View.GONE);
					listOpciones.setVisibility(View.GONE);

					EventoDataSource eventoDataSource = new EventoDataSource(getApplicationContext());
					eventoDataSource.open();
					eventosHoy = eventoDataSource.getEventosEquipoHoy(equipoSeleccionado.getId());
//				eventoDataSource.deleteEventoMayorHoy();
					String lista = eventoDataSource.getIdEventosEquipoHoy(equipoSeleccionado.getId());
					eventoDataSource.close();

					eventoListaArrayAdapter = new EventoListaArrayAdapter(getApplicationContext(), eventosHoy, Y4HomeActivity.this);
					listVisitas.setAdapter(eventoListaArrayAdapter);
					listVisitas.setClickable(true);
					listVisitas.setOnItemClickListener(Y4HomeActivity.this);

					if (eventosHoy.size() == 0 && numeroVisitas == 0) {
						layoutVacio.setVisibility(View.VISIBLE);
						listVisitas.setVisibility(View.GONE);
						imgVacio.setImageResource(R.drawable.lista_visitas_vacia);
						txtVacio.setText("No tiene visitas el dia de hoy");
					} else if (eventosHoy.size() != numeroVisitas) {
						if (!lista.equals("")){
							AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SINCRONIZAR_TIMBRE + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";IDE;" + lista + "");
							AudioQueu.contadorComandoEnviado++;
						}else{
							AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SINCRONIZAR_TIMBRE + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";FEC;0;");
							AudioQueu.contadorComandoEnviado++;
						}
					}

//					for(int i = 0; i <50;i++){
//						AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SINCRONIZAR_TIMBRE + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";0;");
//						AudioQueu.contadorComandoEnviado++;
//					}

				}
			});

			btnSensor.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					badge_layout4.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge_layout1.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge2.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge3.setBackgroundColor(Color.parseColor("#FFFFFF"));
					badge4.setBackgroundColor(Color.parseColor("#00C1E1"));

					layoutVacio.setVisibility(View.GONE);
					btnSensor.setBackgroundColor(Color.parseColor("#FFFFFF"));
					btnSensor.setColorFilter(Color.parseColor("#00C1E1"));

					btnHoy.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnHoy.setTextColor(Color.parseColor("#FFFFFF"));

					btnVideo.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnVideo.setColorFilter(Color.parseColor("#FFFFFF"));

					btnDispositivos.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnDispositivos.setColorFilter(Color.parseColor("#FFFFFF"));

					btnVisitas.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnVisitas.setColorFilter(Color.parseColor("#FFFFFF"));

					listSensor.setVisibility(View.VISIBLE);
					listDispositivos.setVisibility(View.GONE);
					listVisitas.setVisibility(View.GONE);
					listBuzon.setVisibility(View.GONE);
					listOpciones.setVisibility(View.GONE);

					EventoDataSource eventoDataSource = new EventoDataSource(getApplicationContext());
					eventoDataSource.open();
					eventosHoy = eventoDataSource.getEventosEquipoSensorHoy(equipoSeleccionado.getId());
					eventoDataSource.close();
					eventoListaBuzonArrayAdapter = new EventoListaBuzonArrayAdapter(getApplicationContext(), eventosHoy, Y4HomeActivity.this);
					listSensor.setAdapter(eventoListaBuzonArrayAdapter);
					listSensor.setClickable(true);
					listSensor.setOnItemClickListener(Y4HomeActivity.this);

					if (eventosHoy.size() == 0 && numeroSensor == 0) {
						layoutVacio.setVisibility(View.VISIBLE);
						listSensor.setVisibility(View.GONE);
						imgVacio.setImageResource(R.drawable.lista_sensor_vacia);
						txtVacio.setText("No hay registros de apertura de puerta");
					} else if (eventosHoy.size() != numeroSensor) {
						AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SINCRONIZAR_TIMBRE + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";0;");
						AudioQueu.contadorComandoEnviado++;
					}

				}
			});
			btnDispositivos.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					badge_layout4.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge_layout1.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge2.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge3.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge4.setBackgroundColor(Color.parseColor("#FFFFFF"));

					btnHoy.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnHoy.setTextColor(Color.parseColor("#FFFFFF"));

					btnVisitas.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnVisitas.setColorFilter(Color.parseColor("#FFFFFF"));

					btnVideo.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnVideo.setColorFilter(Color.parseColor("#FFFFFF"));

					btnDispositivos.setBackgroundColor(Color.parseColor("#FFFFFF"));
					btnDispositivos.setColorFilter(Color.parseColor("#00C1E1"));
					btnSensor.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnSensor.setColorFilter(Color.parseColor("#FFFFFF"));
					listSensor.setVisibility(View.GONE);

					listDispositivos.setVisibility(View.VISIBLE);
					listVisitas.setVisibility(View.GONE);
					listBuzon.setVisibility(View.GONE);
					listOpciones.setVisibility(View.GONE);

					DispositivoDataSource dispositivoDataSource = new DispositivoDataSource(getApplicationContext());
					dispositivoDataSource.open();
					ArrayList<Dispositivo> dispositivos = dispositivoDataSource.getAllDispositivo();
					dispositivoDataSource.close();
					dispositivoArrayAdapter = new DispositivoListaArrayAdapter(getApplicationContext(), dispositivos, Y4HomeActivity.this);
					listDispositivos.setAdapter(dispositivoArrayAdapter);

//					AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SINCRONIZAR_LOG + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";0;");
//					AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_TOMAR_FOTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";0;");
//					AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, "V06" + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";0;");
					//AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SINCRONIZAR_LOG + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";0;");
//					AudioQueu.contadorComandoEnviado++;


				}
			});

			btnHoy.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					badge_layout4.setBackgroundColor(Color.parseColor("#FFFFFF"));
					badge_layout1.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge2.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge3.setBackgroundColor(Color.parseColor("#00C1E1"));
					badge4.setBackgroundColor(Color.parseColor("#00C1E1"));


					btnHoy.setBackgroundColor(Color.parseColor("#FFFFFF"));
					btnHoy.setTextColor(Color.parseColor("#00C1E1"));

					badge_layout4.setBackgroundColor(Color.parseColor("#FFFFFF"));

					btnVisitas.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnVisitas.setColorFilter(Color.parseColor("#FFFFFF"));

					btnVideo.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnVideo.setColorFilter(Color.parseColor("#FFFFFF"));

					btnDispositivos.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnDispositivos.setColorFilter(Color.parseColor("#FFFFFF"));

					btnSensor.setBackgroundColor(Color.parseColor("#00C1E1"));
					btnSensor.setColorFilter(Color.parseColor("#FFFFFF"));
					listSensor.setVisibility(View.GONE);
					listDispositivos.setVisibility(View.GONE);
					listVisitas.setVisibility(View.GONE);
					listBuzon.setVisibility(View.GONE);
					listOpciones.setVisibility(View.VISIBLE);
				}
			});

			btnNuevo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					menuDown.close(true);
					Intent i = new Intent(Y4HomeActivity.this, InstalarEquipoActivity.class);
					i.putExtra("primerEquipo", false);
					startActivity(i);
				}
			});

			btnCompartir.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					menuDown.close(true);
					final EditText input1 = new EditText(Y4HomeActivity.this);
					LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					input1.setLayoutParams(lp1);
					input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
					final AlertDialog d1 = new AlertDialog.Builder(Y4HomeActivity.this)
							.setTitle(YACSmartProperties.intance.getMessageForKey("email.invitado"))
							.setCancelable(true)
							.setView(input1)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											if (!input1.getText().toString().equals("")) {
												InvitarCuentaAsyncTask invitarCuentaAsyncTask = new InvitarCuentaAsyncTask(Y4HomeActivity.this,
														equipoSeleccionado.getNumeroSerie(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),
														datosAplicacion.getCuenta().getEmail(), input1.getText().toString(), datosAplicacion.getToken());
												invitarCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
												dialog.cancel();
											}
										}
									}).create();

					d1.show();
				}
			});

			btnCuenta.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					menuDown.close(true);
					Intent i = new Intent(Y4HomeActivity.this, IngresarClaveActivity.class);
					startActivity(i);
				}
			});


			btnRespuesta.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					menuDown.close(true);
					Intent i = new Intent(Y4HomeActivity.this, AdministrarRespuestasActivity.class);
					startActivity(i);
				}
			});

			btnPreferencias.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					menuDown.close(true);
					AudioQueu.monitorearPortero = true;
					Intent i = new Intent(Y4HomeActivity.this, PreferenciasActivity.class);
					startActivity(i);
				}
			});
			btnAplicacion.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					ReiniciarEquipoAsyncTask crearEquiposAsyncTask = new ReiniciarEquipoAsyncTask(datosAplicacion.getEquipoSeleccionado());
//					crearEquiposAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			});
//		try {
//					Handler fh = new FileHandler("logging.txt");
//					fh.setFormatter(new SimpleFormatter());
//					Logger.getLogger("de.javawi.jstun").addHandler(fh);
//					Logger.getLogger("de.javawi.jstun").setLevel(Level.ALL);

//			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
//			while (ifaces.hasMoreElements()) {
//				NetworkInterface iface = ifaces.nextElement();
//				Enumeration<InetAddress> iaddresses = iface.getInetAddresses();
//				while (iaddresses.hasMoreElements()) {
//					InetAddress iaddress = iaddresses.nextElement();
//					if (Class.forName("java.net.Inet4Address").isInstance(iaddress)
//							&& !iaddress.getHostAddress().substring(0, 3).equals("127")) {
//						if ((!iaddress.isLoopbackAddress()) && (!iaddress.isLinkLocalAddress())) {
//							Thread thread = new Thread(new DiscoveryTestDemo(iaddress, textoNombre));
//							thread.start();
//						}
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}

			Typeface fontBold = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
			textoNombre.setTypeface(fontBold);
			btnHoy.setTypeface(fontRegular);

			textoNombre.setText(sharedPrefs.getString("prefNombreDispositivo", "").toString());

			TextView txtCopyright = (TextView) findViewById(R.id.txtCopyright);
			txtCopyright.setTypeface(fontRegular);

			listOpciones = (ListView) findViewById(android.R.id.list);
			configuracionArrayAdapter = new ConfiguracionArrayAdapter(Y4HomeActivity.this, opciones);
			listOpciones.setAdapter(configuracionArrayAdapter);
			listOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					if (position == 1) {
						TextView titulo1;
						LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
						View layout = inflater.inflate(R.layout.vi_seek_bar, (ViewGroup) findViewById(R.id.root));
						seekbarExterno = (SeekBar) layout.findViewById(R.id.seekExterno);
						titulo1 = (TextView) layout.findViewById(R.id.txtTitulo1);
						minuto1 = (TextView) layout.findViewById(R.id.txtMinuto1);

						titulo1.setTypeface(fontRegular);
						minuto1.setTypeface(fontRegular);
						seekbarExterno.setProgress(0);

						//Externas
						seekbarExterno.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
							@Override
							public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
								minuto1.setText(progress + " min. ");
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {

							}

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {

							}
						});

						AlertDialog.Builder adb = new AlertDialog.Builder(Y4HomeActivity.this);

						adb.setView(layout);
						adb.setPositiveButton("Encender", new
								DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if (seekbarExterno.getProgress() > 0 || seekbarExterno.getProgress() > 0) {
											Long tiempoE = Long.valueOf(seekbarExterno.getProgress()) * 60000;
											String datosConfT = YACSmartProperties.COM_ENCENDER_LUZ + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + String.valueOf(tiempoE) + ";";
											if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
												EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT, null,
														null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
												enviarComandoThread.start();
											} else {
												AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
												AudioQueu.contadorComandoEnviado++;
											}
										}

									}

								});
						adb.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {


							}
						});
						adb.setNeutralButton("Apagar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								String datosConfT = YACSmartProperties.COM_APAGAR_LUZ + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + ";";

								if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
									EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT, null,
											null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
									enviarComandoThread.start();
								} else {
									AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
									AudioQueu.contadorComandoEnviado++;
								}
							}
						});
						adb.show();
					} else if (position == 2) {

						String datosConfT = YACSmartProperties.COM_CONFIGURAR_BUZON + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";";

						if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
							if (equipoSeleccionado.getBuzon() != null && equipoSeleccionado.getBuzon().equals("1")) {
								EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT + "0" + ";" + equipoSeleccionado.getNumeroSerie(), null,
										null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
								enviarComandoThread.start();
							} else {
								EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT + "1" + ";" + equipoSeleccionado.getNumeroSerie(), null,
										null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
								enviarComandoThread.start();
							}
						} else {
							if (equipoSeleccionado.getBuzon() != null && equipoSeleccionado.getBuzon().equals("1")) {
								AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT + "0" + ";" + equipoSeleccionado.getNumeroSerie() + ";");
								AudioQueu.contadorComandoEnviado++;
							} else {
								AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT + "1" + ";" + equipoSeleccionado.getNumeroSerie() + ";");
								AudioQueu.contadorComandoEnviado++;
							}
						}
					} else if (position == 3) {
						AudioQueu.monitorearPortero = true;
						Intent i = new Intent(Y4HomeActivity.this, EventosActivity.class);
						startActivity(i);

					} else if (position == 4) {
						AudioQueu.monitorearPortero = true;
						Intent i = new Intent(Y4HomeActivity.this, RecomendarActivity.class);
						startActivity(i);

					} else if (position == 5) {
						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, "V07;" + "E" + ";" + equipoSeleccionado.getNumeroSerie() + ";"  , null,
								null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();
					} else if (position == 6) {
						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, "V07;" + "C" + ";" + equipoSeleccionado.getNumeroSerie(), null,
								null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();
//					for(int i = 101; i < 300; i++){
//						TestIOScheduledTask comandoIOScheduledTask = new TestIOScheduledTask("Cuarto" + i);
//						comandoIOScheduledTask.start();
//					}

//					HSVColorPickerDialog mydialog = new HSVColorPickerDialog(getApplicationContext(), 1, Y4HomeActivity.this );
//					mydialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//					mydialog.show();
//					Intent i = new Intent(Y4HomeActivity.this, FocosActivity.class);
//					Intent i = new Intent(Y4HomeActivity.this, AdministrarRoutersActivity.class);

					}
				}
			});
		}else{
			//Vista simplificada
			Button btnEncenderLuzSimple, btnMonitorearSimple, btnAbrirPuertaSimple, btnHistorialSimple;
			btnEncenderLuzSimple = (Button) findViewById(R.id.btnEncenderLuzSimple);
			btnMonitorearSimple = (Button) findViewById(R.id.btnMonitorearSimple);
			btnAbrirPuertaSimple = (Button) findViewById(R.id.btnAbrirPuertaSimple);
			btnHistorialSimple = (Button) findViewById(R.id.btnHistorialSimple);

			btnEncenderLuzSimple.setTransformationMethod(null);
			btnMonitorearSimple.setTransformationMethod(null);
			btnAbrirPuertaSimple.setTransformationMethod(null);
			btnHistorialSimple.setTransformationMethod(null);

			Spannable spannable = new SpannableString("L\nEnciende la Luz");
			spannable.setSpan(new RelativeSizeSpan(2.8f), 0, 1,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable.setSpan(new RelativeSizeSpan(0.8f), 1, 13,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable.setSpan(new RelativeSizeSpan(1.2f), 13, 15,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable.setSpan(new RelativeSizeSpan(0.8f), 15, 17,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			btnEncenderLuzSimple.setText(spannable);

			Spannable spannable2 = new SpannableString("M\nMonitorea tu casa");
			spannable2.setSpan(new RelativeSizeSpan(2.8f), 0, 1,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable2.setSpan(new RelativeSizeSpan(0.8f), 1, 2,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable2.setSpan(new RelativeSizeSpan(1.2f), 2, 3,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable2.setSpan(new RelativeSizeSpan(0.8f), 3, 19,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			btnMonitorearSimple.setText(spannable2);

			Spannable spannable3 = new SpannableString("P\nAbre la Puerta");
			spannable3.setSpan(new RelativeSizeSpan(2.8f), 0, 1,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable3.setSpan(new RelativeSizeSpan(0.8f), 1, 9,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable3.setSpan(new RelativeSizeSpan(1.2f), 9, 11,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable3.setSpan(new RelativeSizeSpan(0.8f), 11, 16,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			btnAbrirPuertaSimple.setText(spannable3);

			Spannable spannable4 = new SpannableString("V\nRevisa tus Visitas");
			spannable4.setSpan(new RelativeSizeSpan(2.8f), 0, 1,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable4.setSpan(new RelativeSizeSpan(0.8f), 1, 13,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable4.setSpan(new RelativeSizeSpan(1.2f), 13, 14,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannable4.setSpan(new RelativeSizeSpan(0.8f), 14, 20,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			btnHistorialSimple.setText(spannable4);

			btnEncenderLuzSimple.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String datosConfT = YACSmartProperties.COM_ENCENDER_LUZ + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + "-1" + ";";
					if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT, null,
								null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();
					}
				}
			});

			btnMonitorearSimple.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					monitorear();
				}
			});

			btnAbrirPuertaSimple.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
						//Wifi
						new SweetAlertDialog(Y4HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
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
										String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_UDP + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + "" + ";";
//										EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT, null, null,
//												null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
//										enviarComandoThread.start();
//

										AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
												YACSmartProperties.COM_ABRIR_PUERTA_UDP + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" );
										AudioQueu.contadorComandoEnviado++;
										sDialog.cancel();

									}
								})
								.show();
					}else{
						//Internet
						new SweetAlertDialog(Y4HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
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
										AlertDialog.Builder alert = new AlertDialog.Builder(Y4HomeActivity.this);
										alert.setTitle("Ingrese su clave");
										final EditText input = new EditText(Y4HomeActivity.this);
										input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_TEXT_VARIATION_PASSWORD);
										input.setRawInputType(Configuration.KEYBOARD_12KEY);
										alert.setView(input);
										alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
														YACSmartProperties.COM_ABRIR_PUERTA + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + YACSmartProperties.Encriptar(input.getText().toString()) + ";");
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

				}
			});

			btnHistorialSimple.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AudioQueu.monitorearPortero = true;
					Intent i = new Intent(Y4HomeActivity.this, EventosActivity.class);
					startActivity(i);
				}
			});
		}
		mimageView = (ImageView) findViewById(R.id.fotoperfil);
		actualizarFotoPerfil();

		//btnProfile = (ImageButton) view.findViewById(R.id.btnprofile);
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
					alertDialog = new AlertDialog.Builder(Y4HomeActivity.this);
//					alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(Y4HomeActivity.this, android.R.style.Theme_Dialog));
					LayoutInflater inflater1 = getLayoutInflater();
					View convertView = (View) inflater1.inflate(R.layout.seleccionar_equipo, null);

					View convertViewTitulo = (View) inflater1.inflate(R.layout.seleccionar_equipo_titulo, null);
					TextView titulo = (TextView) convertViewTitulo.findViewById(R.id.titulo);
					titulo.setTypeface(fontRegular);
					alertDialog.setCustomTitle(convertViewTitulo);

					convertView.setBackgroundColor(Color.parseColor("#546E7A"));
					alertDialog.setView(convertView);
					seleccionarEquipoAdapter = new SeleccionarEquipoAdapter(Y4HomeActivity.this, equipos);
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
							if(equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
								ingresarY4Home();
								ad.dismiss();
							}else{
								Intent i = new Intent(Y4HomeActivity.this, LucesFragment.class);
								startActivity(i);
								finish();
							}
						}
					});

//					alertDialog.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//							dialog.cancel();
//						}
//					});


//					alertDialog.setSingleChoiceItems(seleccionarEquipoAdapter, 0, new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							Equipo equipo = seleccionarEquipoAdapter.getItem(which);
//							datosAplicacion.setEquipoSeleccionado(equipo);
//							equipoSeleccionado = equipo;
//							if(equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
//								ingresarY4Home();
//							}else{
//								Intent i = new Intent(Y4HomeActivity.this, LucesFragment.class);
//								startActivity(i);
//								finish();
//							}
//							dialog.dismiss();
//						}
//					});

				} else {
					//Un equipo entra directamente
					Equipo equipo = (Equipo) equipos.toArray()[0];
					datosAplicacion.setEquipoSeleccionado(equipo);
					equipoSeleccionado = equipo;
					ingresarY4Home();
				}

			}
		});

		EventoDataSource datasource = new EventoDataSource(getApplicationContext());
		datasource.open();
		eventoCol = datasource.getPaginaEventosFoto(0, 5, equipoSeleccionado.getId());

		datasource.close();

		mPager = (ViewPager) findViewById(R.id.pager);

		mPagerAdapter = new FotosPagerAdapter(Y4HomeActivity.this, eventoCol);

		mPager.setAdapter(mPagerAdapter);

		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				invalidateOptionsMenu();
			}
		});

		//drawPageSelectionIndicators(0);
		mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				//	drawPageSelectionIndicators(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		mPager.setPageTransformer(false, new ViewPager.PageTransformer() {

			private float MIN_SCALE = 0.75f;

			@Override
			public void transformPage(View view, float position) {
				int pageWidth = view.getWidth();

				if (position < -1) { // [-Infinity,-1)
					// This page is way off-screen to the left.
					view.setAlpha(0);

				} else if (position <= 0) { // [-1,0]
					// Use the default slide transition when moving to the left page
					view.setAlpha(1);
					view.setTranslationX(0);
					view.setScaleX(1);
					view.setScaleY(1);

				} else if (position <= 1) { // (0,1]
					// Fade the page out.
					view.setAlpha(1 - position);

					// Counteract the default slide transition
					view.setTranslationX(pageWidth * -position);

					// Scale the page down (between MIN_SCALE and 1)
					float scaleFactor = MIN_SCALE
							+ (1 - MIN_SCALE) * (1 - Math.abs(position));
					view.setScaleX(scaleFactor);
					view.setScaleY(scaleFactor);

				} else { // (1,+Infinity]
					// This page is way off-screen to the right.
					view.setAlpha(0);
				}
			}
		});

		indicatorView = (AVLoadingIndicatorView) findViewById(R.id.indicator);

		setupWindowAnimations();
	}

	public void monitorear() {
		if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)) {
            numeroPermisos = 2;
            ActivityCompat.requestPermissions(Y4HomeActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }else if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            numeroPermisos = 1;
            ActivityCompat.requestPermissions(Y4HomeActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }else if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)){
            numeroPermisos = 1;
            ActivityCompat.requestPermissions(Y4HomeActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }else {
            AudioQueu.monitorearPortero = true;
            Intent i = new Intent(Y4HomeActivity.this, MonitorIOActivity.class);
            i.putExtra("monitorear", true);
//						transitionToActivity(MonitorIOActivity.class, opciones[position]);
            startActivity(i);
        }
	}

	public void actualizarLista(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("Sensor", "lsita  ");
				if(eventoListaBuzonArrayAdapter != null) {
					eventoListaBuzonArrayAdapter.notifyDataSetChanged();
				}
				if(eventoListaArrayAdapter != null) {
					eventoListaArrayAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void setupWindowAnimations() {
		// Re-enter transition is executed when returning to this activity
		Slide slideTransition = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			slideTransition = new Slide();
			slideTransition.setSlideEdge(Gravity.LEFT);
			slideTransition.setDuration(500);
			getWindow().setReenterTransition(slideTransition);
			getWindow().setExitTransition(slideTransition);
		}

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

	public static Bitmap getBitmapFromView(View view) {
		view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.draw(canvas);
		return bitmap;
	}

	byte[] fotoActual;
	public void actualizarPager(final byte[] foto) {
		Log.d("actualizarPager", "actualizarPager");
		fotoActual= foto;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EventoDataSource datasource = new EventoDataSource(getApplicationContext());
				datasource.open();
				eventoCol = datasource.getPaginaEventosFoto(0, 4, equipoSeleccionado.getId());
				datasource.close();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				Evento evento = new Evento();
				evento.setId("1");
				evento.setFotoActual(fotoActual);
				evento.setFecha(dateFormat.format(date));
				ArrayList<Evento> eventoColOrden = new ArrayList<Evento>();
				eventoColOrden.add(evento);

				for(Evento e: eventoCol){
					eventoColOrden.add(e);
				}
				((FotosPagerAdapter) (mPager.getAdapter())).notifyDataSetChanged();
				mPagerAdapter = new FotosPagerAdapter(Y4HomeActivity.this, eventoColOrden);
				mPager.setAdapter(mPagerAdapter);
				txtMensaje.setText("Se ha tomado una foto actualizada");
				mostrarMensaje();

			}
		});

	}


	Boolean wifi;
	public String estadoWifi = "5";
	private int numeroVisitas = 0;
	private int numeroSensor = 0;
	public void actualizarEstado(String[] respuestaI, Boolean wifiP) {
		respuesta = respuestaI;
		wifi = wifiP;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if(wifi){
					txtEstado.setText("conexión wifi");
				}else{
					txtEstado.setText("conexión internet");
				}

				if(respuesta[34].equals("1")) {
					estadoWifi = respuesta[4];
					if (!respuesta[4].equals("")) {
						if (respuesta[4].equals("0")) {
							indicatorView.setIndicatorColor(Color.RED);
						} else if (respuesta[4].equals("1")) {
							indicatorView.setIndicatorColor(Color.RED);
						} else if (respuesta[4].equals("2")) {
							indicatorView.setIndicatorColor(Color.YELLOW);
						} else if (respuesta[4].equals("3")) {
							indicatorView.setIndicatorColor(Color.GREEN);
						} else if (respuesta[4].equals("4")) {
							indicatorView.setIndicatorColor(Color.GREEN);
						}
					}
				}else{
					estadoWifi = "5";
					indicatorView.setIndicatorColor(getResources().getColor(R.color.colorprincipal));
				}

				if (Integer.valueOf(respuesta[12]) > 0) {
					if(vistaAvanzada) {
						txtVisitas.setVisibility(View.VISIBLE);
						txtVisitas.setText(respuesta[12]);
					}
					txtMensaje.setText("Has tenido " + respuesta[12] + " visitas el día de hoy");
					numeroVisitas = Integer.valueOf(respuesta[12]);
				}else{
					txtMensaje.setText("Hoy no has tenido visitas " );
				}
				if(vistaAvanzada) {
					configuracionArrayAdapter.notifyDataSetChanged();

					if (Integer.valueOf(respuesta[14]) > 0) {
						txtVideo.setVisibility(View.VISIBLE);
						txtVideo.setText(respuesta[14]);

					}

					if (Integer.valueOf(respuesta[28]) > 0) {
						numeroSensor = Integer.valueOf(respuesta[28]);
						txtSensor.setVisibility(View.VISIBLE);
						txtSensor.setText(respuesta[28]);

					}
					mostrarMensaje();
				}

			}
		});


	}

	private void mostrarMensaje() {
		constraintLayout.bringToFront();
		if (!isScreenLarge()) {
            ViewGroup.LayoutParams params = constraintLayout.getLayoutParams();
            params.height = 200;
            params.width = 500;
            constraintLayout.setLayoutParams(params);

            Animation scaleAnimation = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
            scaleAnimation.setDuration(750);
            scaleAnimation.setFillEnabled(true);
            scaleAnimation.setFillAfter(true);

            constraintLayout.startAnimation(scaleAnimation);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    Animation scaleAnimation = new ScaleAnimation(1, 0, 1, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
                    scaleAnimation.setDuration(750);
                    scaleAnimation.setFillEnabled(true);
                    scaleAnimation.setFillAfter(true);
                    scaleAnimation.setStartOffset(3000);
                    constraintLayout.startAnimation(scaleAnimation);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            ViewGroup.LayoutParams params = constraintLayout.getLayoutParams();
            params.height = 200;
            params.width = 500;
            constraintLayout.setLayoutParams(params);

            Animation scaleAnimation = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
            scaleAnimation.setDuration(750);
            scaleAnimation.setFillEnabled(true);
            scaleAnimation.setFillAfter(true);

            constraintLayout.startAnimation(scaleAnimation);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    Animation scaleAnimation = new ScaleAnimation(1, 0, 1, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
                    scaleAnimation.setDuration(750);
                    scaleAnimation.setFillEnabled(true);
                    scaleAnimation.setFillAfter(true);
                    scaleAnimation.setStartOffset(3000);
                    constraintLayout.startAnimation(scaleAnimation);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
	}

	Boolean resultadoBuzon;

	public void actualizarBuzon(Boolean resultado) {
		resultadoBuzon = resultado;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (resultadoBuzon) {
					EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
					equipoDataSource.open();
					if (equipoSeleccionado.getBuzon() != null && equipoSeleccionado.getBuzon().equals("0")) {
						equipoSeleccionado.setBuzon("1");
					} else {
						equipoSeleccionado.setBuzon("0");
					}
					datosAplicacion.setEquipoSeleccionado(equipoSeleccionado);
					equipoDataSource.updateEquipo(equipoSeleccionado);
					equipoDataSource.close();


					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							Y4HomeActivity.this);
					alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
							.setMessage(YACSmartProperties.intance.getMessageForKey("exito.buzon"))
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				} else {
					listOpciones.setAdapter(new ConfiguracionArrayAdapter(Y4HomeActivity.this, opciones));
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							Y4HomeActivity.this);
					alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
							.setMessage(YACSmartProperties.intance.getMessageForKey("error.buzon"))
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
			}
		});


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
			}
		});
	}


	public void actualizarPager() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EventoDataSource datasource = new EventoDataSource(getApplicationContext());
				datasource.open();
				eventoCol = datasource.getPaginaEventosFoto(0, 5, equipoSeleccionado.getId());
				datasource.close();
				((FotosPagerAdapter) (mPager.getAdapter())).notifyDataSetChanged();
				mPagerAdapter = new FotosPagerAdapter(Y4HomeActivity.this, eventoCol);
				mPager.setAdapter(mPagerAdapter);

			}
		});

	}


	public void verificarRed(Boolean esComunicacionDirecta) {


		if(!AudioQueu.socketComando) {
			ComandoIOScheduledTask comandoIOScheduledTask = new ComandoIOScheduledTask(Y4HomeActivity.this, datosAplicacion,
					datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
			comandoIOScheduledTask.start();
			AudioQueu.contadorComandoEnviado  = 0;
			AudioQueu.socketComando = true;
			AudioQueu.isInBackground = false;

			AudioQueu.setComandoRecibido(new ConcurrentHashMap<Integer, JSONObject>());
			AudioQueu.setComandoEnviado(new ConcurrentHashMap<Integer, String>());
		}

//		AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_NOTIFICAR_IP_ACTUALIZADO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";"+ UUID.randomUUID().toString() + ";");
//		AudioQueu.contadorComandoEnviado++;

		if (!esComunicacionDirecta) {
			AudioQueu.setTipoConexion(TipoConexionEnum.INTERNET.getCodigo());
			AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_NOTIFICAR_IP_ACTUALIZADO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";"+ UUID.randomUUID().toString() + ";");
			AudioQueu.contadorComandoEnviado++;
		} else {


//			AudioQueu.socketComando = false;
			AudioQueu.setTipoConexion(TipoConexionEnum.WIFI.getCodigo());
			txtEstado.setText("conexión wifi");
			if(AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())){
				String datosConfT = YACSmartProperties.COM_TOMAR_FOTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";0;";
				EnviarComandoThread enviarComandoThread = new EnviarComandoThread(Y4HomeActivity.this, datosConfT, null, null,
						null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
				enviarComandoThread.start();
			}else {
				AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_TOMAR_FOTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";0;");
				AudioQueu.contadorComandoEnviado++;
			}
		}
		if(!comandoLocalCorriendo) {
			RecibirComandoRemotoAsyncTask genericoAsyncTask = new RecibirComandoRemotoAsyncTask(Y4HomeActivity.this, LlamadaEntrantePorteroActivity.class,
					getApplicationContext(), datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
			genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			comandoLocalCorriendo = true;
		}
		EventoDataSource eventoDataSource = new EventoDataSource(getApplicationContext());
		eventoDataSource.open();
		Evento evento = eventoDataSource.getUltimaEvento(equipoSeleccionado.getId());
		eventoDataSource.close();

		if(evento == null) {
			AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SINCRONIZAR_TIMBRE + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";FEC;0;");
			AudioQueu.contadorComandoEnviado++;
		}else{
			AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SINCRONIZAR_TIMBRE + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";FEC;" + evento.getFecha() + ";");
			AudioQueu.contadorComandoEnviado++;
		}
	}

	public void verificarResultadoComando(String comando, boolean resultado) {
		if (comando.equals(YACSmartProperties.COM_ENCENDER_LUZ + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)) {
			if ((resultado)) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Y4HomeActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
						.setMessage("Las luces fueron encendidas")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Y4HomeActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage("No se logro encender la luz. Vuelva a intentarlo")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}else if (comando.equals(YACSmartProperties.COM_APAGAR_LUZ + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)) {
			if ((resultado)) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Y4HomeActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
						.setMessage("La luz fue apagada")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Y4HomeActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage("No se logro apagar la luz. Vuelva a intentarlo")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}
		else if (comando.equals(YACSmartProperties.COM_SINCRONIZAR_TIMBRE + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)) {
			if ((resultado)) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Y4HomeActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
						.setMessage("La luz fue apagada")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						Y4HomeActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage("No se logro apagar la luz. Vuelva a intentarlo")
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

	public Equipo getEquipoSeleccionado() {
		return equipoSeleccionado;
	}

	public void setEquipoSeleccionado(Equipo equipoSeleccionado) {
		this.equipoSeleccionado = equipoSeleccionado;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		for(Evento evento1: eventosHoy){
			evento1.setSeleccionado(false);
		}
		if(listVisitas.getVisibility() == View.VISIBLE) {
			Evento evento = eventosHoy.get(position);
			evento.setSeleccionado(true);
			((EventoListaArrayAdapter) (listVisitas.getAdapter())).notifyDataSetChanged();
		}else if(listBuzon.getVisibility() == View.VISIBLE) {
			Evento evento = eventosHoy.get(position);
			evento.setSeleccionado(true);
			((EventoListaBuzonArrayAdapter) (listBuzon.getAdapter())).notifyDataSetChanged();
		}else if(listSensor.getVisibility() == View.VISIBLE) {
			Evento evento = eventosHoy.get(position);
			evento.setSeleccionado(true);
			((EventoListaBuzonArrayAdapter) (listSensor.getAdapter())).notifyDataSetChanged();

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!AudioQueu.monitorearPortero) {
			AudioQueu.isInBackground = true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(!AudioQueu.monitorearPortero && !AudioQueu.llamadaEntrante) {
			AudioQueu.isInBackground = true;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(!AudioQueu.monitorearPortero) {
			AudioQueu.isInBackground = false;
			comandoLocalCorriendo = false;

			VerificarRedAsyncTask checkearRedAsyncTask = new VerificarRedAsyncTask(Y4HomeActivity.this);
			checkearRedAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
}

class VerificarRedAsyncTask extends AsyncTask<String, Float,  Boolean> {

	private Y4HomeActivity y4HomeActivity;

	public VerificarRedAsyncTask(Y4HomeActivity y4HomeActivity) {
		super();
		this.y4HomeActivity = y4HomeActivity;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		DatagramSocket clientSocket = null;

		try {
			clientSocket = new DatagramSocket();
			DatosAplicacion datosAplicacion = ((DatosAplicacion) y4HomeActivity.getApplicationContext());
			Equipo equipo = datosAplicacion.getEquipoSeleccionado();
			InetAddress ipEquipo = null;
			Integer puertoComando = YACSmartProperties.PUERTO_COMANDO_DEFECTO;
			ipEquipo = InetAddress.getByName(equipo.getIpLocal());
//			puertoComando = equipo.getPuertoComando();


			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(y4HomeActivity.getApplicationContext());

			String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

			String datosConfS = YACSmartProperties.COM_PING + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipo.getNumeroSerie() + ";" + ";";

			byte[] datosConfB = datosConfS.getBytes();
			byte[] datosComando = new byte[512];

			System.arraycopy(datosConfB, 0, datosComando, 0, datosConfB.length);

			DatagramPacket sendPacketConf = new DatagramPacket(datosComando,
					datosComando.length, ipEquipo,
					puertoComando);

			clientSocket.send(sendPacketConf);

			byte[] datos = new byte[512];
			DatagramPacket receivePacket = new DatagramPacket(datos,
					datos.length);

			clientSocket.setSoTimeout(2000);

			clientSocket.receive(receivePacket);

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

	@Override
	protected void onPostExecute(Boolean esComunicacionDirecta) {
		y4HomeActivity.verificarRed(esComunicacionDirecta);
	}
}