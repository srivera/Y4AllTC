package ec.com.yacare.y4all.activities.principal;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.dto.Mensaje;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.MensajeDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class PrincipalMenuActivity extends FragmentActivity implements  TextToSpeech.OnInitListener {

//	public SpeechRecognizer speech = null;

	public Intent recognizerIntent;
	
	private TextToSpeech textToSpeech;
	
	private Dispositivo dispositivoDestino;
	
	private Dispositivo dispositivoOrigen;
	
	private String ultimoMensaje;

	public static Boolean escucharAuto = false;
	
	public static Boolean contestarAutomatico = false;
	
	public AudioManager audioManager;
	
	private HashMap<Integer, byte[]> audioMensaje = new HashMap<Integer, byte[]>();
	
	public static HashMap<Integer, byte[]> audioMensajeRecibido = new HashMap<Integer, byte[]>();
	
	private AudioRecord audio_recorder = null;

	private ByteArrayOutputStream audio;
	private Mensaje mensajeRecibido;

//	private DrawerLayout drawerLayout;
//	private ActionBarDrawerToggle drawerToggle;
//	private List<SlideMenuItem> list = new ArrayList<>();
//	private ViewAnimator viewAnimator;
	private LinearLayout linearLayout;
	//private ContentFragment contentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(isScreenLarge()) {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		setContentView(R.layout.ac_portero_menu_principal);


	//	agregarToolbar();

//		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		if(getTextToSpeech() == null){
			setTextToSpeech(new TextToSpeech(this, this));
		}

		audioManager  =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		
//		speech = SpeechRecognizer.createSpeechRecognizer(this);
//		speech.setRecognitionListener(this);
//		Fragment fragmentoGenerico = new Y4HomeActivity();
//		contentFragment = ContentFragment.newInstance(R.drawable.add);
//		getSupportFragmentManager().beginTransaction()
//				.replace(R.id.content_frame, fragmentoGenerico, "Y4HomeActivity")
//				.commit();
//		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//		drawerLayout.setScrimColor(Color.TRANSPARENT);
//		linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
//		linearLayout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				drawerLayout.closeDrawers();
//			}
//		});

//		setActionBar();
//		createMenuList();
//		viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);

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
	}

	public static int RESULT_ACTUALIZAR_LISTA_EQUIPO = 20;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		agregarToolbar();
		if(resultCode == RESULT_ACTUALIZAR_LISTA_EQUIPO){
			FragmentManager fragmentManager = getSupportFragmentManager();
//			CuentaActivity myFragment = (CuentaActivity) (fragmentManager.findFragmentByTag("CuentaActivity"));
//			if (myFragment != null && myFragment.isVisible()) {
//				myFragment.actualizarListaEquipos();
//				Log.d("Refrescar lista","");
//			}
		}
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			Locale locSpanish = new Locale("es");
			int result = textToSpeech.setLanguage(locSpanish);
//			textToSpeech.setSpeechRate(0.95f); 
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("error", "This Language is not supported");
			} else {
				
			}
		} else {
			Log.e("error", "Initilization Failed!");
		}
	}
	

	public void convertTextToSpeech(String textoLeer, boolean responder) {
		Log.d("convertTextToSpeech", textoLeer);
		if(escucharAuto){
			audioManager.startBluetoothSco();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(dispositivoDestino != null){
			textToSpeech.speak(textoLeer, TextToSpeech.QUEUE_FLUSH, null);
			

			 while (textToSpeech.isSpeaking()){
				 Log.d("Hablando", "Hablando");
		     } 
			 if(responder){
				 convertTextToSpeechPregunta("Quiere responder?");
			 }
		}else{
			textToSpeech.speak(textoLeer, TextToSpeech.QUEUE_FLUSH, null);
			 while (textToSpeech.isSpeaking()){
				 Log.d("Hablando", "Hablando");
		     } 
		}
		if(escucharAuto){
			audioManager.stopBluetoothSco();
		}
	}
	



	public void convertTextToSpeechPregunta(String textoLeer) {
		Log.d("convertText", textoLeer);
//		if(escucharAuto){
//			audioManager.startBluetoothSco();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		if(dispositivoDestino != null){
			textToSpeech.speak(textoLeer, TextToSpeech.QUEUE_FLUSH, null);
			while (textToSpeech.isSpeaking()){
				Log.d("Hablando", "Hablando");
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							PrincipalMenuActivity.this);
					alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("y4chat.titulo"))
							.setMessage(YACSmartProperties.intance.getMessageForKey("y4chat.pregunta") + " " + dispositivoDestino.getNombreDispositivo() + "?")
							.setCancelable(false)
							.setNegativeButton("NO", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									if (escucharAuto) {
										audioManager.stopBluetoothSco();
									}
								}
							})
							.setPositiveButton("SI", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {

									SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
									SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
									Date date = new Date();

									MensajeDataSource mensajeDataSource = new MensajeDataSource(getApplicationContext());
									mensajeDataSource.open();
									mensajeRecibido = new Mensaje();
									mensajeRecibido.setTipo(YACSmartProperties.COM_MENSAJE_VOZ);
									mensajeRecibido.setEstado("REC");
									mensajeRecibido.setHora(hourFormat.format(date));
									mensajeRecibido.setFecha(dateFormat.format(date));
									mensajeRecibido.setIdDispositivo(getDispositivoDestino().getId());
									mensajeRecibido.setMensaje("");
									mensajeRecibido.setId(UUID.randomUUID().toString());
									mensajeRecibido.setDireccion("ENV");
									mensajeDataSource.createMensajeNew(mensajeRecibido);
									mensajeDataSource.close();


									android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
									audio = new ByteArrayOutputStream();

									audioMensaje = new HashMap<Integer, byte[]>();

									final int minBufferSize = AudioRecord.getMinBufferSize(
											8000, AudioFormat.CHANNEL_IN_MONO,
											AudioFormat.ENCODING_PCM_16BIT);

									audio_recorder = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER| MediaRecorder.AudioSource.MIC, 8000,
											AudioFormat.CHANNEL_IN_MONO,
											AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);


									Thread thread = new Thread(new Runnable() {
										public void run() {
											int bytes_read = 0;
											int TAMANO_PAKETE = 372;
											byte[] sendData = new byte[TAMANO_PAKETE];

											int i = 0;
											audio_recorder.startRecording();
											while (audio_recorder != null) {
												try {
													sendData = new byte[TAMANO_PAKETE];
													bytes_read = audio_recorder.read(sendData, 0, TAMANO_PAKETE);
													audioMensaje.put(i, sendData);
													audio.write(sendData, 0, sendData.length);
													i++;
												} catch (IllegalStateException e) {

												}
											}


											try {
												OutputStream os = new FileOutputStream(new File("/sdcard/" + mensajeRecibido.getId() + ".txt"));
												BufferedOutputStream bos = new BufferedOutputStream(os);
												DataOutputStream outFile = new DataOutputStream(bos);

												outFile.write(audio.toByteArray());
												outFile.flush();
												outFile.close();
											} catch (IOException e) {
												e.printStackTrace();
											}

										}
									});
									thread.start();

									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											PrincipalMenuActivity.this);
									alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("y4chat.titulo"))
											.setMessage(YACSmartProperties.intance.getMessageForKey("y4chat.enviar.mensaje"))
											.setCancelable(false)
											.setNegativeButton(YACSmartProperties.intance.getMessageForKey("boton.cancelar"), new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int id) {
													audio_recorder.stop();
													audio_recorder.release();
													audio_recorder = null;
//									if(escucharAuto){
//										audioManager.stopBluetoothSco();
//									}
												}
											})
											.setPositiveButton(YACSmartProperties.intance.getMessageForKey("enviar.mensaje"), new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int id) {
//									if(escucharAuto){
//										audioManager.stopBluetoothSco();
//									}
													audio_recorder.stop();
													audio_recorder.release();
													audio_recorder = null;

//									EnviarMensajeChatAudioAsyncTask enviarMensajeChatAudio = new EnviarMensajeChatAudioAsyncTask(PrincipalActivity.this,
//									dispositivoOrigen.getId(), dispositivoDestino.getId());
//									enviarMensajeChatAudio.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
												}
											});

									AlertDialog alertDialog = alertDialogBuilder.create();
									alertDialog.show();
								}
							});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();

				}
			});
		}

	}
	
	

	public TextToSpeech getTextToSpeech() {
		return textToSpeech;
	}

	public void setTextToSpeech(TextToSpeech textToSpeech) {
		this.textToSpeech = textToSpeech;
	}



	public Dispositivo getDispositivoDestino() {
		return dispositivoDestino;
	}


	public void setDispositivoDestino(Dispositivo dispositivoDestino) {
		this.dispositivoDestino = dispositivoDestino;
	}


	public Dispositivo getDispositivoOrigen() {
		return dispositivoOrigen;
	}


	public void setDispositivoOrigen(Dispositivo dispositivoOrigen) {
		this.dispositivoOrigen = dispositivoOrigen;
	}


	public String getUltimoMensaje() {
		return ultimoMensaje;
	}

	public void setUltimoMensaje(String ultimoMensaje) {
		this.ultimoMensaje = ultimoMensaje;
	}

	
	/**
	 * Showing google speech input dialog
	 * */
//	public void promptSpeechInput(int result) {
//		if(!requiereAudio){
//			runOnUiThread(new Runnable() {
//	
//	             @Override
//	             public void run() {
//	            	 speech.destroy();
//	            	 speech = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
//	            	 recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//	         		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
//	         				"en");
//	         		recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
//	         				getPackageName());
//	         		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//	         				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
//	         		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
//	         		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
//	         		speech.setRecognitionListener(PrincipalActivity.this);
//	            	 speech.startListening(recognizerIntent);
//	             }
//	         });
//		}
//	}




	public void sendRecognizeIntent()
	{
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search");
	    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
	    intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
	    intent.putExtra("android.speech.extra.GET_AUDIO", true);
//	    startActivityForResult(intent, REQ_CODE_RESPUESTA);
	}

	//@Override
	//protected void onActivityResult(int requestCode, int resultCode, Intent data)
	//{
		// the resulting text is in the getExtras:
		
		//TODO null
	  //  Bundle bundle = data.getExtras();
//	    ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
	    // the recording url is in getData:
	  //  Uri audioUri = data.getData();
	 //   ContentResolver contentResolver = getContentResolver();
//	    MediaPlayer mediaPlayer = new MediaPlayer();
//		try {
//			mediaPlayer.setDataSource(getApplicationContext(), audioUri);
//			mediaPlayer.prepare();
//			mediaPlayer.start();
//		} catch (IllegalArgumentException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (SecurityException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IllegalStateException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
		
//	    try {
//			InputStream filestream = contentResolver.openInputStream(audioUri);
//			
//			bytesAudio = getBytesFromInputStream(filestream);
//			
//			EnviarMensajeChatAudioAsyncTask enviarMensajeChatAudio = new EnviarMensajeChatAudioAsyncTask(this, 
//					dispositivoOrigen.getId(), dispositivoDestino.getId());
//			enviarMensajeChatAudio.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//			
//			
////			mediaPlayer.setDataSource(filestream.);
////			mediaPlayer.prepare();
////			mediaPlayer.start();
//			System.out.println("preuba");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//	}
	
//	public static byte[] getBytesFromInputStream(InputStream is) throws IOException{
//	
//	    	ByteArrayOutputStream os = new ByteArrayOutputStream();
//	        byte[] buffer = new byte[0xFFFF];
//
//	        for (int len; (len = is.read(buffer)) != -1;)
//	            os.write(buffer, 0, len);
//
//	        os.flush();
//
//	        return os.toByteArray();
//
//	}

//	public void verificarEnvioMensajeAudio(String respuesta) {
//		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
//			//Tomar puerto
//
//			String resultPuerto = null;
//			try {
//				JSONObject respuestaJSON = new JSONObject(respuesta);
//				resultPuerto = respuestaJSON.getString("resultado");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			if(resultPuerto != null){
//				String puertos[] = resultPuerto.split(";");
//				if(TextUtils.isDigitsOnly(puertos[0].toString())){
//					Integer puertoAudio =Integer.valueOf(puertos[0]);
//					Log.d("PUERTO","PUERTO " + puertoAudio);
//					ComandoEnviarMensajeChatAsyncTask enviarMensajeChatAudio = new ComandoEnviarMensajeChatAsyncTask(this, puertoAudio, audioMensaje );
//							enviarMensajeChatAudio.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//				}
//			}
//		}
//	}
			



//	public static String getErrorText(int errorCode) {
//		String message;
//		switch (errorCode) {
//		case SpeechRecognizer.ERROR_AUDIO:
//			message = "Audio recording error";
//			break;
//		case SpeechRecognizer.ERROR_CLIENT:
//			message = "Client side error";
//			break;
//		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
//			message = "Insufficient permissions";
//			break;
//		case SpeechRecognizer.ERROR_NETWORK:
//			message = "Network error";
//			break;
//		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
//			message = "Network timeout";
//			break;
//		case SpeechRecognizer.ERROR_NO_MATCH:
//			message = "No match";
//			break;
//		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
//			message = "RecognitionService busy";
//			break;
//		case SpeechRecognizer.ERROR_SERVER:
//			message = "error from server";
//			break;
//		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
//			message = "No speech input";
//			break;
//		default:
//			message = "Didn't understand, please try again.";
//			break;
//		}
//		return message;
//	}
	
//	@Override
//	public void onResults(Bundle results) {
//		ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//		String text = "";
//		
//		if(!requiereAudio){
//			for (String result : matches)
//			if(result.equals("si")){
//				 Log.d("SI QUIERE RESPONDER", "SI QUIERE RESPONDER");
//				 requiereAudio = true;
////				 promptSpeechInput(REQ_CODE_RESPUESTA);
//				 sendRecognizeIntent();
//			}
//		}else{
//			recognizerIntent.getExtras();
//			recognizerIntent.getData();
//			Log.d("CAPTURAR AUDIO", "CAPTURAR AUDIO");
//			
//		}
//	}


	
	@Override
	public void onDestroy() {
	    super.onDestroy();
//	    speech.destroy();
	}

	private void agregarToolbar() {
//		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//		setSupportActionBar(toolbar);
//		final ActionBar ab = getSupportActionBar();
//		if (ab != null) {
//			// Poner ícono del drawer toggle
//			ab.setHomeAsUpIndicator(R.drawable.y4homeb);
//			ab.setDisplayHomeAsUpEnabled(true);
//			//ab.setSubtitle(datosAplicacion.getEquipoSeleccionado().getNombreEquipo());
//			//ab.setTitle("Y4Home");
//			ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FFFFFF"));
//			ab.setBackgroundDrawable(colorDrawable);
//
//			ab.setHomeAsUpIndicator(R.drawable.drawer_toggle);
//			ab.setDisplayHomeAsUpEnabled(true);
////			ab.setIcon(colorDrawable);
//		}

	}


//	private void createMenuList() {
//		SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.icn_close);
//		list.add(menuItem0);
//		SlideMenuItem menuItem = new SlideMenuItem(ContentFragment.Y4HOME, R.drawable.bell);
//		list.add(menuItem);
//		SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.CUENTA, R.drawable.profile);
//		list.add(menuItem2);
//		SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.TONOS, R.drawable.tono);
//		list.add(menuItem4);
//		SlideMenuItem menuItem6 = new SlideMenuItem(ContentFragment.RESPUESTAS, R.drawable.respuestas);
//		list.add(menuItem6);
//		SlideMenuItem menuItem7 = new SlideMenuItem(ContentFragment.DISPOSITIVO, R.drawable.devices);
//		list.add(menuItem7);
//		SlideMenuItem menuItem5 = new SlideMenuItem(ContentFragment.FEEDBACK, R.drawable.feedback);
//		list.add(menuItem5);
//	}
//
//
//	private void setActionBar() {
//		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//		setSupportActionBar(toolbar);
//		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00C1E1")));
//		getSupportActionBar().setHomeButtonEnabled(true);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		drawerToggle = new ActionBarDrawerToggle(
//				this,                  /* host Activity */
//				drawerLayout,         /* DrawerLayout object */
//				toolbar,  /* nav drawer icon to replace 'Up' caret */
//				R.string.abrir,  /* "open drawer" description */
//				R.string.cerrar  /* "close drawer" description */
//		) {
//
//			/** Called when a drawer has settled in a completely closed state. */
//			public void onDrawerClosed(View view) {
//				super.onDrawerClosed(view);
//				linearLayout.removeAllViews();
//				linearLayout.invalidate();
//			}
//
//			@Override
//			public void onDrawerSlide(View drawerView, float slideOffset) {
//				super.onDrawerSlide(drawerView, slideOffset);
//				if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
//					viewAnimator.showMenuContent();
//			}
//
//			/** Called when a drawer has settled in a completely open state. */
//			public void onDrawerOpened(View drawerView) {
//				super.onDrawerOpened(drawerView);
//			}
//		};
//		drawerLayout.setDrawerListener(drawerToggle);
//	}
//
//	@Override
//	protected void onPostCreate(Bundle savedInstanceState) {
//		super.onPostCreate(savedInstanceState);
//		drawerToggle.syncState();
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		drawerToggle.onConfigurationChanged(newConfig);
//	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
//		}
//		switch (item.getItemId()) {
//			case R.id.action_settings:
//				return true;
//			default:
//				return super.onOptionsItemSelected(item);
//		}
	}


//	private ScreenShotable replaceFragment(Fragment fragmentoGenerico, int topPosition, String tag) {
//		//setActionBar();
//		View view = findViewById(R.id.content_frame);
//		int finalRadius = Math.max(view.getWidth(), view.getHeight());
//		SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
//		animator.setInterpolator(new AccelerateInterpolator());
//		animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
//		animator.start();
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		fragmentManager
//				.beginTransaction()
//				.replace(R.id.content_frame, fragmentoGenerico, tag)
//				.commit();
//		return contentFragment;
//	}
//
//	@Override
//	public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
//		switch (slideMenuItem.getName()) {
//			case ContentFragment.CLOSE:
//				return screenShotable;
//			default:
//
//		}
//		Fragment fragmentoGenerico = null;
//
//		String tag = "";
//		switch (slideMenuItem.getName()) {
//			case ContentFragment.Y4HOME:
//				tag = "Y4HomeActivity";
//				fragmentoGenerico = new Y4HomeActivity();
//				break;
//			case ContentFragment.CUENTA:
//				tag = "CuentaActivity";
//				fragmentoGenerico = new CuentaActivity();
//				break;
//			case ContentFragment.TONOS:
//				Intent i = new Intent(PrincipalMenuActivity.this, PreferenciasActivity.class);
//				startActivity(i);
//				break;
//			case ContentFragment.FEEDBACK:
//				tag = "FeedBackActivity";
//				fragmentoGenerico = new FeedBackActivity();
//				break;
//			case ContentFragment.RESPUESTAS:
//				tag = "AdministrarRespuestasActivity";
//				fragmentoGenerico = new AdministrarRespuestasActivity();
//				break;
//			case ContentFragment.DISPOSITIVO:
//				tag = "DispositivoActivity";
//				fragmentoGenerico = new DispositivoActivity();
//				break;
//			case ContentFragment.CLOSE:
//				return screenShotable;
//
//		}
//		if (fragmentoGenerico != null) {
//			return replaceFragment(fragmentoGenerico, position, tag);
//
//		}else{
//			return screenShotable;
//		}
//
//
//	}
//
//	@Override
//	public void disableHomeButton() {
//
//	}
//
//	@Override
//	public void enableHomeButton() {
//
//	}

//	@Override
//	public void disableHomeButton() {
//		getSupportActionBar().setHomeButtonEnabled(false);
//
//	}
//
//	@Override
//	public void enableHomeButton() {
//		getSupportActionBar().setHomeButtonEnabled(true);
////		drawerLayout.closeDrawers();
//
//	}
//
//	@Override
//	public void addViewToContainer(View view) {
//		linearLayout.addView(view);
//	}

}


