package ec.com.yacare.y4all.activities.socket;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.adapter.MensajeTextoArrayAdapter;
import ec.com.yacare.y4all.lib.asynctask.io.RecibirVideoIOScheduledTask;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.MensajeTexto;
import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.MensajeTextoDataSource;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.tareas.RecibirAudiowfThread;
import ec.com.yacare.y4all.lib.tareas.RecibirVideoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.util.indicator.AVLoadingIndicatorView;
import ec.com.yacare.y4all.lib.ws.MonitoreoPorteroIOAsyncTask;

import static ec.com.yacare.y4all.lib.util.AudioQueu.esComunicacionDirecta;


public class MonitorIOActivity extends AppCompatActivity implements  View.OnClickListener {

	//Imagenes y video
	public ImageView imagenInicial;

	//Botones
	private ImageButton btnLuz;
	private ImageButton btnMensaje;

	//DataSources respuestas
//	private RespuestaDataSource datasource;
//	private SeleccionarRespuestaAdapter adapter;
//	private AlertDialog.Builder alertDialog;

	//Audio Manager
	public AudioManager audioManager;

	//Equipo conectado
	private Equipo equipoSeleccionado;
	private Boolean encenderAltavoz = false;
	private EquipoDataSource equipoDataSource;
	private ArrayList<Equipo> equipos;
	private DatosAplicacion datosAplicacion;

	private ImageButton fabSpeaker, fabMic, fabPuerta, fabSalir;

	private Button fabCerrar;
	public RelativeLayout videoPanel;
	public LinearLayout loadingPanel;
	public RelativeLayout constraintLayout;

	public TextView txtMensaje, txtMensajeMicrofono, txtAudio, txtVideo;

	private Typeface fontRegular;

	private String nombreDispositivo;

	private ListView respuestasTexto;

	private TextView textoTimer;
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	private long timeInMilliseconds = 0L;
	private long timeSwapBuff = 0L;
	private long updatedTime = 0L;

//	private Bitmap imageBitmap;

	private ArrayList<MensajeTexto> mensajes;
	private MensajeTextoArrayAdapter mensajeTextoArrayAdapter;

//	private ArrayList<Respuesta> respuestas;
//	private SeleccionarRespuestaAdapter respuestaAdapter;
//	private Boolean botonTexto = true;

	private WakeLock wl;
	private PowerManager pm;

	private KeyguardManager km;
	private KeyguardLock kl;

	public String idMonitoreo;
	private ImageView imagensenal;
	private AVLoadingIndicatorView indicatorMic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_portero);
		Log.d("monitor ingreso", "monitor " );
		if (isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		datosAplicacion = (DatosAplicacion) getApplicationContext();
		datosAplicacion.setMonitorIOActivity(MonitorIOActivity.this);

		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		kl = km.newKeyguardLock("INFO");
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "INFO");
		wl.acquire();

//		wl = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
//		wl.acquire();

		AudioQueu.speakerExterno = true;

		equipoDataSource = new EquipoDataSource(getApplicationContext());
		equipoDataSource.open();
		Equipo equipoBusqueda = new Equipo();
		equipoBusqueda.setTipoEquipo(TipoEquipoEnum.PORTERO.getCodigo());
		equipos = equipoDataSource.getEquipoTipoEquipo(equipoBusqueda);
		equipoDataSource.close();

		if (datosAplicacion.getEquipoSeleccionado() == null) {
			datosAplicacion.setEquipoSeleccionado((Equipo) equipos.toArray()[0]);
			equipoSeleccionado = (Equipo) equipos.toArray()[0];
		} else {
			equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();
		}

		fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

		TextView txtNombreEquipo = (TextView) findViewById(R.id.txtNombreEquipo);
		txtNombreEquipo.setTypeface(fontRegular);
		txtNombreEquipo.setText(equipoSeleccionado.getNombreEquipo());

		textoTimer = (TextView) findViewById(R.id.textoTimer);
		textoTimer.bringToFront();

		respuestasTexto = (ListView) findViewById(R.id.respuestasTexto);
		imagensenal = (ImageView) findViewById(R.id.imagensenal);
		indicatorMic = (AVLoadingIndicatorView) findViewById(R.id.indicatorMic);

		MensajeTextoDataSource mensajeTextoDataSource = new MensajeTextoDataSource(getApplicationContext());
		mensajeTextoDataSource.open();
		mensajes = mensajeTextoDataSource.getAllMensajes();
		mensajeTextoDataSource.close();

		mensajeTextoArrayAdapter = new MensajeTextoArrayAdapter(getApplicationContext(), mensajes, MonitorIOActivity.this, nombreDispositivo);
		respuestasTexto.setAdapter(mensajeTextoArrayAdapter);

		imagenInicial = (ImageView) findViewById(R.id.imagenInicialFoto);

		txtAudio = (TextView) findViewById(R.id.txtAudio);

		txtVideo = (TextView) findViewById(R.id.txtVideo);

		txtMensaje = (TextView) findViewById(R.id.txtMensaje);
		btnMensaje = (ImageButton) findViewById(R.id.btnMensaje);
		btnMensaje.setColorFilter(Color.WHITE);
		btnMensaje.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input1 = new EditText(MonitorIOActivity.this);
				LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				input1.setLayoutParams(lp1);
				input1.setTextSize(12);
				final AlertDialog d1 = new AlertDialog.Builder(MonitorIOActivity.this)
						.setTitle(YACSmartProperties.intance.getMessageForKey("ingrese.mensaje")).setIcon(R.drawable.mail)
						.setCancelable(true)
						.setView(input1)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										if (!input1.getText().toString().equals("")) {
											String datosConfT = YACSmartProperties.COM_REPRODUCIR_TEXTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + input1.getText().toString() + ";" + "-1" + ";" +  YACSmartProperties.VOZ_HOMBRE1 + ";";
											if (esComunicacionDirecta) {
												EnviarComandoThread enviarComandoThread = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null,
														MonitorIOActivity.this, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
												enviarComandoThread.start();
											} else {
												attemptComando(datosConfT);
											}
											dialog.cancel();
										}
									}
								}).create();

				d1.show();
			}
		});

		btnLuz = (ImageButton) findViewById(R.id.btnLuz);
		btnLuz.setColorFilter(Color.WHITE);
		btnLuz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(AudioQueu.encenderLuz) {
					AudioQueu.encenderLuz = false;
					//btnEncenderLuz.setText("apagar luz");
					String datosConfT = YACSmartProperties.COM_ENCENDER_LUZ_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + "-1" + ";";
					if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null,
								null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();
					} else {
						AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
								datosConfT);
						AudioQueu.contadorComandoEnviado++;
					}
				}else{
					AudioQueu.encenderLuz = true;
					//btnEncenderLuz.setText("encender luz");
					String datosConfT = YACSmartProperties.COM_APAGAR_LUZ_WIFI + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + "-1" + ";";
					if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null,
								null, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();
					} else {
						AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado,
								datosConfT);
						AudioQueu.contadorComandoEnviado++;
					}
				}
			}
		});

		fabSpeaker = (ImageButton) findViewById(R.id.fabSpeaker);
		fabSpeaker.setColorFilter(Color.WHITE);
		fabCerrar = (Button) findViewById(R.id.fabCerrar);
		fabMic = (ImageButton) findViewById(R.id.fabMic);
		fabPuerta = (ImageButton) findViewById(R.id.fabPuerta);
		fabPuerta.setColorFilter(Color.WHITE);
		fabSalir = (ImageButton) findViewById(R.id.fabSalir);
		fabSpeaker.setOnClickListener(this);
		fabCerrar.setOnClickListener(this);
		fabMic.setOnClickListener(this);
		fabPuerta.setOnClickListener(this);
		fabSalir.setOnClickListener(this);
		fabSalir.bringToFront();
		fabMic.bringToFront();

		videoPanel = (RelativeLayout) findViewById(R.id.videoPanel);
		loadingPanel = (LinearLayout) findViewById(R.id.loadingPanel);
		constraintLayout = (RelativeLayout) findViewById(R.id.constraintLayout);

		loadingPanel.setVisibility(View.VISIBLE);
		videoPanel.setVisibility(View.GONE);
		imagenInicial.setVisibility(View.GONE);

		fabMic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AudioQueu.hablar) {
					String datosConfT = YACSmartProperties.COM_TERMINAR_HABLAR + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";";

					if (esComunicacionDirecta) {
						EnviarComandoThread enviarComandoThread1 = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null,
								MonitorIOActivity.this, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread1.start();
					} else {
						attemptComando(datosConfT);
					}
					AudioQueu.hablar = false;

					fabMic.setImageResource(R.drawable.micceleste);
				} else {
					String datosConfT = YACSmartProperties.COM_INICIAR_HABLAR + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";";
					if (esComunicacionDirecta) {
						EnviarComandoThread enviarComandoThread1 = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null,
								MonitorIOActivity.this, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread1.start();
					} else {
						attemptComando(datosConfT);
					}
					AudioQueu.hablar = true;

					fabMic.setImageResource(R.drawable.microjo);
				}
			}
		});


		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		idMonitoreo = UUID.randomUUID().toString();
		if (getIntent().getExtras() != null && getIntent().getExtras().get("timbrando") != null) {
			imagenInicial.setVisibility(View.VISIBLE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "INFO");
			wl.acquire();

			AudioQueu.llamadaEntrante = true;
			Log.d("llamadaEntrante3","true");
			CheckearRedIOAsyncTask checkearRedAsyncTask = new CheckearRedIOAsyncTask(MonitorIOActivity.this);
			checkearRedAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		} else {
			if (getIntent().getExtras() != null && getIntent().getExtras().get("monitorear") != null) {
				AudioQueu.llamadaEntrante = true;
				Log.d("llamadaEntrante4","true");
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "INFO");
				wl.acquire();

				CheckearRedIOAsyncTask checkearRedAsyncTask = new CheckearRedIOAsyncTask(MonitorIOActivity.this);
				checkearRedAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}

		}
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			LinearLayout layoutDetalle = (LinearLayout) findViewById(R.id.layoutDetalle);
			LinearLayout layoutDetalle1 = (LinearLayout) findViewById(R.id.layoutDetalle1);
			LinearLayout layoutDetalle2 = (LinearLayout) findViewById(R.id.layoutDetalle2);
			layoutDetalle1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f) );
			layoutDetalle2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f) );
			layoutDetalle.setOrientation(LinearLayout.HORIZONTAL);

			RelativeLayout layoutInicial = (RelativeLayout) findViewById(R.id.layoutInicial);
			LinearLayout.LayoutParams paramsR = (LinearLayout.LayoutParams) layoutInicial.getLayoutParams();
			paramsR.weight =0.09f;

			RelativeLayout layoutMensaje = (RelativeLayout) findViewById(R.id.layoutMensaje);
			LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) layoutMensaje.getLayoutParams();
			paramsL.weight =0.08f;

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			LinearLayout layoutDetalle = (LinearLayout) findViewById(R.id.layoutDetalle);
			LinearLayout layoutDetalle1 = (LinearLayout) findViewById(R.id.layoutDetalle1);
			LinearLayout layoutDetalle2 = (LinearLayout) findViewById(R.id.layoutDetalle2);
			layoutDetalle1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.48f) );
			layoutDetalle2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.28f) );
			layoutDetalle.setOrientation(LinearLayout.VERTICAL);

			RelativeLayout layoutInicial = (RelativeLayout) findViewById(R.id.layoutInicial);
			LinearLayout.LayoutParams paramsR = (LinearLayout.LayoutParams) layoutInicial.getLayoutParams();
			paramsR.weight =0.05f;

			RelativeLayout layoutMensaje = (RelativeLayout) findViewById(R.id.layoutMensaje);
			LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) layoutMensaje.getLayoutParams();
			paramsL.weight =0.05f;

		}

	}

	String mensajeDesplegar;
	public void mostrarMensaje(String mensaje) {
		mensajeDesplegar = mensaje;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				constraintLayout.bringToFront();
				txtMensaje.setText(mensajeDesplegar);
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
		});

	}


	private void activarComunicacion() {

		if (!encenderAltavoz) {
			audioManager.setSpeakerphoneOn(true);
			fabSpeaker.setImageResource(R.drawable.menuspeaker);
			encenderAltavoz = true;
			AudioQueu.speakerExterno = true;
		} else {
			audioManager.setSpeakerphoneOn(false);
			fabSpeaker.setImageResource(R.drawable.menumute);
			encenderAltavoz = false;
			AudioQueu.speakerExterno = false;
			String datosConfT = YACSmartProperties.COM_INICIAR_HABLAR + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";";
			if (esComunicacionDirecta) {
				EnviarComandoThread enviarComandoThread1 = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null,
						MonitorIOActivity.this, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
				enviarComandoThread1.start();
			} else {
				attemptComando(datosConfT);
			}
			AudioQueu.hablar = true;
		}

		startTime = 0L;
		customHandler = new Handler();
		timeInMilliseconds = 0L;
		timeSwapBuff = 0L;
		updatedTime = 0L;
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);

		AudioQueu.setComunicacionAbierta(true);
		imagenInicial.setVisibility(View.VISIBLE);
		fabSpeaker.setEnabled(true);
		fabPuerta.setEnabled(true);
		fabMic.setEnabled(true);
		btnMensaje.setEnabled(true);
		btnLuz.setEnabled(true);

	}

	int secsanterior = 0;
	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			timeInMilliseconds = (SystemClock.uptimeMillis() - startTime) / 1000;
			updatedTime = timeSwapBuff + timeInMilliseconds;
			int secs = (int) (updatedTime);
			int mins = secs / 60;
			secs = secs % 60;
			Log.i("segundos", "secs " + secs + " secsanterior " + secsanterior);

			secsanterior = secs;
			textoTimer.setText("" + mins + ":"
					+ String.format("%02d", secs));
			customHandler.postDelayed(this, 1000);

			txtVideo.setText("v: " + AudioQueu.paqRecibidoVideo);
			AudioQueu.paqRecibidoVideo = 0;

			txtAudio.setText("a: " + AudioQueu.paqRecibido + " ") ;
			if(AudioQueu.paqRecibido > 65){
				//orange
				imagensenal.setColorFilter(Color.parseColor("#FFA500"));
				indicatorMic.setIndicatorColor(Color.parseColor("#FFA500"));
			}else if(AudioQueu.paqRecibido <= 65 && AudioQueu.paqRecibido > 35){
				//verde
				imagensenal.setColorFilter(Color.GREEN);
				indicatorMic.setIndicatorColor(Color.GREEN);
			}else if(AudioQueu.paqRecibido <= 35 && AudioQueu.paqRecibido > 30){
				//amarillo
				imagensenal.setColorFilter(Color.YELLOW);
				indicatorMic.setIndicatorColor(Color.YELLOW);
			}else if(AudioQueu.paqRecibido <= 30 && AudioQueu.paqRecibido > 20){
				//orange
				imagensenal.setColorFilter(Color.parseColor("#FFA500"));
				indicatorMic.setIndicatorColor(Color.parseColor("#FFA500"));
			}else if(AudioQueu.paqRecibido < 20){
				//rojo
				imagensenal.setColorFilter(Color.RED);
				indicatorMic.setIndicatorColor(Color.RED);
			}
			AudioQueu.paqRecibido = 0;




		}
	};


//	public void showImage(Bitmap imageBitmap1, String titulo) {
//		imageBitmap = imageBitmap1;
//		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
//
//		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//		View layout = inflater.inflate(R.layout.vi_foto_preview, (ViewGroup) findViewById(R.id.root));
//
//
//		Bitmap blurred = YACSmartProperties.fastblur(imageBitmap, 1, 10);//second parametre is radius
//		BitmapDrawable ob = new BitmapDrawable(getResources(), blurred);
//		layout.setBackgroundDrawable(ob);
//
//
//		ImageView imagen = (ImageView) layout.findViewById(R.id.preview_foto);
//		imagen.setImageBitmap(imageBitmap);
//
//		TextView leyenda = (TextView) layout.findViewById(R.id.txtFechaPreview);
//		leyenda.setText(titulo);
//		leyenda.setTypeface(fontRegular);
//
//		AlertDialog.Builder adb = new AlertDialog.Builder(this);
//
//		adb.setView(layout);
//		adb.setPositiveButton("Cerrar", new
//				DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//					}
//
//					public void nClick(DialogInterface dialog, int which) {
//					}
//				});
//		adb.setNeutralButton("Guardar", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//				EventoDataSource datasource = new EventoDataSource(getApplicationContext());
//				datasource.open();
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//				Date date = new Date();
//				Evento evento = new Evento();
//				evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + equipoSeleccionado.getNombreEquipo());
//				evento.setId(UUID.randomUUID().toString());
//				evento.setFecha(dateFormat.format(date));
//				evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
//				evento.setComando("FOTO");
//				evento.setTipoEvento(TipoEventoEnum.FOTO.getCodigo());
//				evento.setIdEquipo(equipoSeleccionado.getId());
//
//				FileOutputStream fileOuputStream = null;
//				try {
//
//					ByteArrayOutputStream stream = new ByteArrayOutputStream();
//					imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//					fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + evento.getId() + ".jpg");
//					fileOuputStream.write(stream.toByteArray());
//					fileOuputStream.close();
//					evento.setMensaje("S");
//					datasource.createEvento(evento);
//					datasource.close();
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		adb.show();
//	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.fabCerrar:
				cerrarComunicacion(false);
				finish();
				break;
			case R.id.fabSpeaker:
				if (!encenderAltavoz) {
					audioManager.setSpeakerphoneOn(true);
					fabSpeaker.setImageResource(R.drawable.menuspeaker);
					encenderAltavoz = true;
					AudioQueu.speakerExterno = true;
				} else {
					audioManager.setSpeakerphoneOn(false);
					fabSpeaker.setImageResource(R.drawable.menumute);
					encenderAltavoz = false;
					AudioQueu.speakerExterno = false;
				}
				break;
			case R.id.fabPuerta:
				if (esComunicacionDirecta) {
					//Wifi
					abrirPuertaWifi();
				}else{
					//Internet
					abrirPuertaInternet();
				}
				break;
			case R.id.fabSalir:
				cerrarComunicacion(false);
				finish();
				break;
		}
	}

	private void abrirPuertaInternet() {
		if (Integer.valueOf(equipoSeleccionado.getTimbreExterno()) <= 2) {
			new SweetAlertDialog(MonitorIOActivity.this, SweetAlertDialog.WARNING_TYPE)
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
							AlertDialog.Builder alert = new AlertDialog.Builder(MonitorIOActivity.this);
							alert.setTitle("Ingrese su clave");
							final EditText input = new EditText(getApplicationContext());
							input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
							input.setRawInputType(Configuration.KEYBOARD_12KEY);
							alert.setView(input);
							alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
									String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date()) + ";",
											datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
									String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA
											+ ";" + nombreDispositivo + ";" + cadenaEnc + ";"
											+ YACSmartProperties.Encriptar(input.getText().toString(), equipoSeleccionado.getNumeroSerie()) + ";";

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
			AlertDialog.Builder builder = new AlertDialog.Builder(MonitorIOActivity.this);
			builder.setTitle("Abrir Puerta");
			builder.setMessage("Seleccione la puerta que desea abrir o presione cancelar?");
			// add the buttons
			builder.setPositiveButton("Puerta Principal", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					AlertDialog.Builder alert = new AlertDialog.Builder(MonitorIOActivity.this);
					alert.setTitle("Ingrese su clave");
					final EditText input = new EditText(getApplicationContext());
					input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_TEXT_VARIATION_PASSWORD);
					input.setRawInputType(Configuration.KEYBOARD_12KEY);
					alert.setView(input);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
							String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date())  + ";",
									datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
							String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA
									+ ";"+ nombreDispositivo + ";" +  cadenaEnc + ";"
									+ YACSmartProperties.Encriptar(input.getText().toString(), equipoSeleccionado.getNumeroSerie()) + ";";

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
			});
			builder.setNegativeButton("Puerta Secundaria", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					AlertDialog.Builder alert = new AlertDialog.Builder(MonitorIOActivity.this);
					alert.setTitle("Ingrese su clave");
					final EditText input = new EditText(getApplicationContext());
					input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_TEXT_VARIATION_PASSWORD);
					input.setRawInputType(Configuration.KEYBOARD_12KEY);
					alert.setView(input);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
							String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date())  + ";",
									datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
							String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_OPCIONAL
									+ ";"+ nombreDispositivo + ";" +  cadenaEnc + ";"
									+ YACSmartProperties.Encriptar(input.getText().toString(), equipoSeleccionado.getNumeroSerie()) + ";";

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
		if (Integer.valueOf(equipoSeleccionado.getTimbreExterno()) <= 2) {
			new SweetAlertDialog(MonitorIOActivity.this, SweetAlertDialog.WARNING_TYPE)
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
						String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date())  + ";",
								datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
						String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_UDP
								+ ";"+ nombreDispositivo + ";" +  cadenaEnc + ";";

						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null, null,
								null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();

						sDialog.cancel();

					}
				})
				.show();
		} else {
			//2 puertas
			AlertDialog.Builder builder = new AlertDialog.Builder(MonitorIOActivity.this);
			builder.setTitle("Abrir Puerta");
			builder.setMessage("Seleccione la puerta que desea abrir o presione cancelar?");
			// add the buttons
			builder.setPositiveButton("Puerta Principal", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
					String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date())  + ";",
							datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
					String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_UDP
							+ ";"+ nombreDispositivo + ";" +  cadenaEnc + ";";

					EnviarComandoThread enviarComandoThread = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null, null,
							null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					enviarComandoThread.start();

					dialog.cancel();

				}
			});
			builder.setNeutralButton("Puerta Secundaria", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
					String cadenaEnc = YACSmartProperties.Encriptar("ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + dateFormatGmt.format(new Date())  + ";",
							datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
					String datosConfT = YACSmartProperties.COM_ABRIR_PUERTA_OPCIONAL_UDP
							+ ";"+ nombreDispositivo + ";" +  cadenaEnc + ";";

					EnviarComandoThread enviarComandoThread = new EnviarComandoThread(MonitorIOActivity.this, datosConfT, null, null,
							null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					enviarComandoThread.start();

					dialog.cancel();

				}
			});
			builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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

	Boolean mostrarMensajeL;

	public void cerrarComunicacion(Boolean mostrarMensaje) {
		mostrarMensajeL = mostrarMensaje;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				timeSwapBuff += timeInMilliseconds;
				customHandler.removeCallbacks(updateTimerThread);

				if (imagenInicial.getVisibility() != View.VISIBLE) {
					imagenInicial.setVisibility(View.VISIBLE);
				}
				AudioQueu.hablar = false;
				fabMic.setImageResource(R.drawable.micceleste);
				AudioQueu.llamadaEntrante = false;

				fabSpeaker.setEnabled(false);
				audioManager.setSpeakerphoneOn(false);
				fabPuerta.setEnabled(false);
				fabMic.setEnabled(false);
				btnMensaje.setEnabled(false);
				btnLuz.setEnabled(false);
				encenderAltavoz = false;

				DatosAplicacion datosAplicacion = ((DatosAplicacion) getApplicationContext());
				Equipo equipo = datosAplicacion.getEquipoSeleccionado();

				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

				nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");


				if(AudioQueu.comunicacionAbierta) {
					String datosConfS = YACSmartProperties.COM_CERRAR_COMUNICACION + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipo.getNumeroSerie() + ";";

					AudioQueu.setComunicacionAbierta(false);
					if (esComunicacionDirecta) {
						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(MonitorIOActivity.this, datosConfS, null,
								MonitorIOActivity.this, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();
					} else {
						attemptComando(datosConfS);
					}
				}

			}
		});


	}

	@Override
	public void onPause() {
		super.onPause();
		AudioQueu.setSegundoPlano(true);
		Log.d("Pausa", "pausa");
		if (wl!= null && wl.isHeld()) {
			wl.release();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		AudioQueu.setSegundoPlano(false);
		Log.d("resume", "resume");
	}

	public void verificarResultadoMonitoreoSocket(String respuesta) {
		if (!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))) {
			String resultPuerto = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				resultPuerto = respuestaJSON.getString("resultado");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (resultPuerto != null) {
				String tipoComunicacion = "3G";
				String[] rutas = resultPuerto.split(";");

				String idDispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
				AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_DISPOSITIVO_CONTESTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
						+ ";" + rutas[0] + ";" + rutas[1] + ";" + tipoComunicacion + ";" + idDispositivo + ";" + "SOCKET" + ";" + idMonitoreo + ";");
				AudioQueu.contadorComandoEnviado++;

			AudioQueu.setTipoConexion(TipoConexionEnum.INTERNET.getCodigo());
				esComunicacionDirecta = false;
				activarComunicacion();


//			String texto = equipoSeleccionado.getNombreEquipo() + " ";
//
//				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//				ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				//For 3G check
//				boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//						.isConnectedOrConnecting();
//				//For WiFi Check
//				boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//						.isConnectedOrConnecting();

//				String tipoComunicacion = "3G";
//				if(isWifi){
//					tipoComunicacion = "WIFI";
//				}
//				System.out.println(is3g + " net " + isWifi);

//				String 	idDispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
//				AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_DISPOSITIVO_CONTESTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
//						+ ";" + rutas[0] + ";" + rutas[1]  + ";"  + tipoComunicacion + ";" + idDispositivo + ";");
//				AudioQueu.contadorComandoEnviado++;
//
//				AudioQueu.setComunicacionAbierta(true);


				AudioQueu.tipoComunicacion = "INDIRECTO";

				EnviarAudioIOScheduledTask enviarAudioScheduledTask = new EnviarAudioIOScheduledTask(this, null, rutas[0]);
				enviarAudioScheduledTask.start();

				RecibirVideoIOScheduledTask recibirVideoIOScheduledTask = new RecibirVideoIOScheduledTask(getApplicationContext(), imagenInicial, null, rutas[1], MonitorIOActivity.this);
				recibirVideoIOScheduledTask.start();
			}

		}
	}


	public void verificarResultadoDirecto(String respuesta) {
		if (!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))) {
			String resultPuerto = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				resultPuerto = respuestaJSON.getString("resultado");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (resultPuerto != null) {
				esComunicacionDirecta = false;
				RecibirAudiowfThread enviarAudioThread = new RecibirAudiowfThread(MonitorIOActivity.this, null,
						audioManager, null, equipoSeleccionado.getIpPublica(), YACSmartProperties.PUERTO_AUDIO_DEFECTO);
				enviarAudioThread.start();

				RecibirVideoThread recibirVideoThread = new RecibirVideoThread(getApplicationContext(), imagenInicial, null, YACSmartProperties.PUERTO_VIDEO_DEFECTO, equipoSeleccionado.getIpPublica(), MonitorIOActivity.this);
				recibirVideoThread.start();

				activarComunicacion();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MonitorIOActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				AudioQueu.llamadaEntrante = false;
			}
		} else {
			//Error general
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MonitorIOActivity.this);
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			AudioQueu.llamadaEntrante = false;
		}


	}

	public void verificarRed(String esComunicacionDirecta) {
		Log.d("esComunicacionDirecta", esComunicacionDirecta);
		if (esComunicacionDirecta.equals("true")) {
			AudioQueu.setTipoConexion(TipoConexionEnum.WIFI.getCodigo());
			AudioQueu.esComunicacionDirecta = true;
			RecibirAudiowfThread enviarAudioThread = new RecibirAudiowfThread(MonitorIOActivity.this, null,
					audioManager, null, equipoSeleccionado.getIpLocal(), YACSmartProperties.PUERTO_AUDIO_DEFECTO);
			enviarAudioThread.start();

			RecibirVideoThread recibirVideoThread = new RecibirVideoThread(getApplicationContext(), imagenInicial, null, YACSmartProperties.PUERTO_VIDEO_DEFECTO, equipoSeleccionado.getIpLocal(), MonitorIOActivity.this);
			recibirVideoThread.start();

			activarComunicacion();
			imagensenal.setImageResource(R.drawable.menuwifi);

		} else if (esComunicacionDirecta.equals("false")) {
			//Llamar WS monitorearPortero
			AudioQueu.setTipoConexion(TipoConexionEnum.INTERNET.getCodigo());
			MonitoreoPorteroIOAsyncTask monitorearPorteroAsyncTask = new MonitoreoPorteroIOAsyncTask(MonitorIOActivity.this);
			monitorearPorteroAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			//En uso
			if (!esComunicacionDirecta.equals(nombreDispositivo)) {
				porteroOcupado(esComunicacionDirecta);
			}
		}
	}

	String dispositivoUso;
	public void porteroOcupado(String dispositivoUso1) {
		AudioQueu.comunicacionAbierta = false;
		this.dispositivoUso = dispositivoUso1;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MonitorIOActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
						.setMessage(dispositivoUso + " está conectado a Wii Bell")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								AudioQueu.comunicacionAbierta = false;
								finish();
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});

	}


	public void attemptComando(String comando) {
		Log.d("ComandoEnviado ", comando + AudioQueu.contadorComandoEnviado);
		AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, comando);
		AudioQueu.contadorComandoEnviado++;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		datosAplicacion.setMonitorIOActivity(null);
		if (wl!= null) {
			try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

			wl.acquire();
			wl.release();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		AudioQueu.monitorearPortero = false;
	}


	public byte[] descomprimirGZIP(byte[] file, Integer paquete) {
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

	public void verificarResultadoComando(String comando, boolean resultado) {
		if (comando.equals(YACSmartProperties.COM_ENCENDER_LUZ)) {
			if ((resultado)) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MonitorIOActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
						.setMessage("Las luces fueron encendidas")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MonitorIOActivity.this);
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
	}

	public void verificarResultadoHablar(String comando, boolean resultado) {
		if (resultado) {
			Log.d("verificarResultadoHabl", comando + resultado);
			if (comando.equals(YACSmartProperties.COM_INICIAR_HABLAR + YACSmartProperties.CONSTANTE_RESPUESTA_COMANDO)) {
				AudioQueu.hablar = true;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						fabMic.setImageResource(R.drawable.microjo);
					}
				});

			} else {
				AudioQueu.hablar = false;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						fabMic.setImageResource(R.drawable.micceleste);
					}
				});
			}
		}
	}

	public void verificarResultadoHablarudp(String comando, boolean resultado) {
		if (resultado) {
			if (comando.equals(YACSmartProperties.COM_INICIAR_HABLAR)) {
				AudioQueu.hablar = true;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						fabMic.setImageResource(R.drawable.microjo);
					}
				});

			} else {
				AudioQueu.hablar = false;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						fabMic.setImageResource(R.drawable.micceleste);
					}
				});

			}
		}
	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		cerrarComunicacion(false);
		finish();
	}

}


class CheckearRedIOAsyncTask extends AsyncTask<String, Float,  String> {

	private MonitorIOActivity monitorActivity;

	public CheckearRedIOAsyncTask(MonitorIOActivity monitorActivity) {
		super();
		this.monitorActivity = monitorActivity;
	}

	@Override
	protected String doInBackground(String... params) {
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
			DatosAplicacion datosAplicacion = ((DatosAplicacion)monitorActivity.getApplicationContext());
			Equipo equipo = datosAplicacion.getEquipoSeleccionado();
			InetAddress ipEquipo = null;
			Integer puertoComando = YACSmartProperties.PUERTO_COMANDO_DEFECTO;
			ipEquipo = InetAddress.getByName(equipo.getIpLocal());

			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(monitorActivity.getApplicationContext());

			String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

			String datosConfS = YACSmartProperties.COM_DISPOSITIVO_CONTESTO + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipo.getNumeroSerie() + ";" + monitorActivity.idMonitoreo +  ";" ;

			byte[] datosConfB = datosConfS.getBytes();
			byte[] datosComando = new byte[512];

			System.arraycopy(datosConfB, 0, datosComando, 0, datosConfB.length);

			DatagramPacket sendPacketConf = new DatagramPacket(datosComando,
					datosComando.length, ipEquipo,
					puertoComando);

			clientSocket.send(sendPacketConf);
			clientSocket.send(sendPacketConf);
			clientSocket.send(sendPacketConf);

			byte[] datos = new byte[512];
			DatagramPacket receivePacket = new DatagramPacket(datos,
					datos.length);

			clientSocket.setSoTimeout(3000);

			clientSocket.receive(receivePacket);
			clientSocket.close();
			String[] resultado = (new String(receivePacket.getData())).split(";");
			if(resultado[1].equals("OK")){
				return "true";
			}else{
				return resultado[2];
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			clientSocket.close();
			return "false";
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			clientSocket.close();
			return "false";
		} catch (IOException e) {
			e.printStackTrace();
			clientSocket.close();
			return "false";
		}
	}

	@Override
	protected void onPostExecute(String  esComunicacionDirecta) {
		monitorActivity.verificarRed(esComunicacionDirecta);
	}
}