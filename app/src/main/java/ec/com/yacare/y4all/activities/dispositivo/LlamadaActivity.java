package ec.com.yacare.y4all.activities.dispositivo;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.asynctask.dispositivo.EnviarAudioIntercomAsyncTask;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class LlamadaActivity extends Activity {

	
	//Audio Manager
	public AudioManager audioManager;
		
	private TextView textoPaqEnviado;
	private TextView textoPaqRecibido;
	
	//Video
//	private CamaraView mPreview;
//	private Camera mCamera;
//	private ImageView iv;
//	private LinearLayout layoutVideo;
	
	//Botones
	private Button btnCerrar;

//	private boolean tieneCamaraFrontal;
//	
//	private int idCamaraActiva;
//	private String ipComunicar;
//	
//	private TextView nombreTablet;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_llamada);
		
	audioManager  =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
	
//	nombreTablet = (TextView) findViewById(R.id.txtNombre);
		
		textoPaqEnviado = (TextView) findViewById(R.id.textoPaqEnviado);
		textoPaqRecibido = (TextView) findViewById(R.id.textoPaqRecibido);
		
//		layoutVideo = (LinearLayout) findViewById(R.id.llamadaVideoIntercom);
//		mPreview = (CamaraView)findViewById(R.id.cam);
		
		btnCerrar = (Button) findViewById(R.id.btnCerrarIntercom);

		
		btnCerrar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AudioQueu.setComunicacionAbierta(false);

			}
		});
		
//		AudioQueu.setVideoConferencia(true);
//		
//		if(AudioQueu.getVideoConferencia()){
////			layoutVideo.setVisibility(View.VISIBLE);
//			try{
//				Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//		        for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
//		            CameraInfo camInfo = new CameraInfo();
//		            Camera.getCameraInfo(camNo, camInfo);
//		            if (camInfo.facing==(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
//		                mCamera = Camera.open(camNo);
//		                idCamaraActiva = camNo;
//		                tieneCamaraFrontal = true;
//		            }
//		        }
//		        if (mCamera == null) {
//		             mCamera = Camera.open();
//		             idCamaraActiva = Camera.CameraInfo.CAMERA_FACING_BACK;
//		             tieneCamaraFrontal = false;
//		        }
//	
//		        Parameters parameters = mCamera.getParameters();
//		        Display display = getWindowManager()
//		                .getDefaultDisplay();
//	
//		        int rotation = getResources().getConfiguration().orientation;
//		        Log.i("CameraPreviews", "screen rotation is " + rotation);
//		        Log.i("CameraPreviews", "display rotation is " + display.getRotation());
//		        if (display.getRotation() == Surface.ROTATION_0) {
//	
//		            if (rotation == Configuration.ORIENTATION_LANDSCAPE) {
//		                mCamera.setDisplayOrientation(0);
//		            } else {
//		                mCamera.setDisplayOrientation(90);
//		            }
//		        }
//	
//		        else if (display.getRotation() == Surface.ROTATION_90) {
//		            if (rotation == Configuration.ORIENTATION_PORTRAIT) {
//		                mCamera.setDisplayOrientation(270);
//		            }
//		        }
//	
//		        else if (display.getRotation() == Surface.ROTATION_180) {
//		            if (rotation == Configuration.ORIENTATION_LANDSCAPE) {
//		            	mCamera.setDisplayOrientation(180);
//		            }else {
//		                mCamera.setDisplayOrientation(270);
//		            }
//		        }
//	
//		        else if (display.getRotation() == Surface.ROTATION_270) {
//		            if (rotation == Configuration.ORIENTATION_PORTRAIT) {
//		            	mCamera.setDisplayOrientation(90);
//		            } else {
//		                mCamera.setDisplayOrientation(180);
//		            }
//		        }
//	
//		        try {
//		        	mCamera.setParameters(parameters);
//		        } catch (Exception e) {
//		            e.printStackTrace();
//		        }
//		        mPreview.setCamera(mCamera);
//				iv=(ImageView)findViewById(R.id.lpimg);
//				
//				mPreview.Initialize();
//				mCamera.startPreview();
//
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			
//		}else{
//			layoutVideo.setVisibility(View.INVISIBLE);
//		}
		
//		CuentaDataSource datasource;
//		datasource = new CuentaDataSource(getApplicationContext());
//		datasource.open();
//		String email = datasource.getEmailNumeroSerie(getIntent().getExtras().getString("numeroSerie"));
//		datasource.close();
//		email = "sheyla_alvarezr@hotmail.com";

		
		
		if(getIntent().getExtras().getString("comando").equals(YACSmartProperties.INT_INICIAR_LLAMADA)){
//			AudioQueu.setModoMonitoreo(false);
//			nombreTablet.setText(getIntent().getExtras().getString("nombreEquipo"));
//			if(getIntent().getExtras().getBoolean("esComunicacionDirecta")){
//				ipComunicar= getIntent().getExtras().getString("ipOrigen");
//				ContestarAyudaTiendaAsyncTask contestarLlamadaAsyncTask = new ContestarAyudaTiendaAsyncTask(LlamadaActivity.this, getIntent().getExtras().getString("origen"), "D");
//				contestarLlamadaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				AudioQueu.setComunicacionAbierta(true);
//		
//				EnviarAudioIntercomDirectoAsyncTask intercomAsyncTask = new EnviarAudioIntercomDirectoAsyncTask(getApplicationContext(), textoPaqEnviado, 
//						audioManager, textoPaqRecibido, getIntent().getExtras().getString("ipOrigen"), LlamadaActivity.this);
//				intercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				
//				if(AudioQueu.getVideoConferencia()){
//					EnviarVideoIntercomDirectoAsyncTask videoIntercomAsyncTask = new EnviarVideoIntercomDirectoAsyncTask(getIntent().getExtras().getString("ipOrigen"), iv, mPreview, textoPaqRecibido);
//					videoIntercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				}
//			}else{
//				ContestarAyudaTiendaAsyncTask contestarLlamadaAsyncTask = new ContestarAyudaTiendaAsyncTask(LlamadaActivity.this, getIntent().getExtras().getString("origen"), "I");
//				contestarLlamadaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				AudioQueu.setComunicacionAbierta(true);
				EnviarAudioIntercomAsyncTask intercomAsyncTask = new EnviarAudioIntercomAsyncTask(getApplicationContext(), textoPaqEnviado, 
						audioManager, textoPaqRecibido, Integer.valueOf(getIntent().getExtras().getString("puertoAudio")), LlamadaActivity.this);
				intercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				if(AudioQueu.getVideoConferencia()){
//					EnviarVideoIntercomAsyncTask videoIntercomAsyncTask = new EnviarVideoIntercomAsyncTask(getApplicationContext(), Integer.valueOf(getIntent().getExtras().getString("puertoVideo")), textoPaqEnviado, textoPaqRecibido, iv);
//					videoIntercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				}
//			}
//		}else if(getIntent().getExtras().getString("comando").equals(YACSmartProperties.INT_CONTESTO_MONITOREO)){
//			AudioQueu.getDialog().dismiss();
//			if(Boolean.valueOf(getIntent().getExtras().getString("esComunicacionDirecta"))){
//				ipComunicar= getIntent().getExtras().getString("ipDestino");
//				AudioQueu.setComunicacionAbierta(true);
//				EnviarAudioIntercomDirectoAsyncTask intercomAsyncTask = new EnviarAudioIntercomDirectoAsyncTask(getApplicationContext(), textoPaqEnviado, 
//						audioManager, textoPaqRecibido, getIntent().getExtras().getString("ipDestino"), LlamadaActivity.this);
//				intercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//
//
//				if(AudioQueu.getVideoConferencia()){
//					EnviarVideoIntercomDirectoAsyncTask videoIntercomAsyncTask = new EnviarVideoIntercomDirectoAsyncTask(getIntent().getExtras().getString("ipDestino"), iv, mPreview, textoPaqRecibido);
//					videoIntercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//				}
//			}else{
//				AudioQueu.setComunicacionAbierta(true);
//
//				EnviarAudioIntercomAsyncTask intercomAsyncTask = new EnviarAudioIntercomAsyncTask(getApplicationContext(), textoPaqEnviado, 
//						audioManager, textoPaqRecibido, AudioQueu.getPuertoComunicacionIntercomIndirecto(), LlamadaActivity.this);
//				intercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				if(AudioQueu.getVideoConferencia()){
//					EnviarVideoIntercomAsyncTask videoIntercomAsyncTask = new EnviarVideoIntercomAsyncTask(getApplicationContext(), AudioQueu.getPuertoComunicacionIntercomIndirectoVideo(), textoPaqEnviado, textoPaqRecibido, iv);
//					videoIntercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				}
//			}

		}
		
		//else 
			
			
//			if(getIntent().getExtras().getString("comando").equals(YACSmartProperties.INT_CONTESTO_LLAMADA)){
//			if(Boolean.valueOf(getIntent().getExtras().getString("esComunicacionDirecta"))){
//				ipComunicar= getIntent().getExtras().getString("ipDestino");
//				AudioQueu.setComunicacionAbierta(true);
//				EnviarAudioIntercomDirectoAsyncTask intercomAsyncTask = new EnviarAudioIntercomDirectoAsyncTask(getApplicationContext(), textoPaqEnviado, 
//						audioManager, textoPaqRecibido, getIntent().getExtras().getString("ipDestino"), SolicitarAyudaActivity.this);
//				intercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				if(AudioQueu.getVideoConferencia()){
//					EnviarVideoIntercomDirectoAsyncTask videoIntercomAsyncTask = new EnviarVideoIntercomDirectoAsyncTask(getIntent().getExtras().getString("ipDestino"), iv, mPreview, textoPaqRecibido);
//					videoIntercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				}
//			}else{
//				AudioQueu.setComunicacionAbierta(true);
//				
//				EnviarAudioIntercomAsyncTask intercomAsyncTask = new EnviarAudioIntercomAsyncTask(getApplicationContext(), textoPaqEnviado, 
//						audioManager, textoPaqRecibido, AudioQueu.getPuertoComunicacionIntercomIndirecto(), SolicitarAyudaActivity.this);
//				intercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				if(AudioQueu.getVideoConferencia()){
//					EnviarVideoIntercomAsyncTask videoIntercomAsyncTask = new EnviarVideoIntercomAsyncTask(getApplicationContext(), AudioQueu.getPuertoComunicacionIntercomIndirectoVideo(), textoPaqEnviado, textoPaqRecibido, iv);
//					videoIntercomAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				}
//			}
//			
//			
//		}
	}
	
//	@Override
//	protected void onResume() 
//	{
//		super.onResume();
////		wl.acquire();
//		if(mCamera==null)
//		{
//			mCamera = Camera.open();
//			mPreview.setCamera(mCamera);
//			mCamera.startPreview();
//		}
//	}
//
//	@Override
//	protected void onPause() 
//	{
//		super.onPause();
////		wl.release();
//		mPreview.releaseAll();
//		if (mCamera != null) 
//		{
//			mCamera.release();
//			mCamera = null;
//		}
//	}
}
