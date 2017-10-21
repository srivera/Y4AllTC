package ec.com.yacare.y4all.activities.dispositivo;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class LlamadaEntranteActivity extends Activity {

	
	public ImageView imagenInicial;
//	private TextView nombreTablet;
	
	private Button btnResponder;
	private Button btnRechazar;
	
	public Boolean llamadaAtendida;
	
	public String rutaImagen;
	
	public Boolean imagenRecibida;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_llamada_entrante);
		
		llamadaAtendida = false;
		
		imagenRecibida = false;
		
		imagenInicial = (ImageView) findViewById(R.id.imgFotoVisitanteTim);
		
//		nombreTablet = (TextView) findViewById(R.id.txtNombreTablet);
		
		btnResponder = (Button) findViewById(R.id.btnResponderTimbrada);
		
		btnRechazar = (Button) findViewById(R.id.btnRechazarTimbrada);
		
		btnRechazar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				llamadaAtendida = true;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
				
			}
		});
		timerDelayRemoveView(10000);
		
		btnResponder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				llamadaAtendida = true;
				
				Intent i = new Intent(LlamadaEntranteActivity.this, LlamadaActivity.class);
				i.putExtra("comando", getIntent().getExtras().getString("comando"));

				i.putExtra("numeroSerie", getIntent().getExtras().getString("numeroSerie"));
				i.putExtra("puertoAudio", getIntent().getExtras().getString("puertoAudio" +
						"4" +
						""));
				i.putExtra("ipOrigen", getIntent().getExtras().getString("ipOrigen"));
				i.putExtra("origen", getIntent().getExtras().getString("origen"));
				i.putExtra("video", getIntent().getExtras().getString("video"));
				i.putExtra("nombreEquipo", getIntent().getExtras().getString("nombreEquipo"));
				i.putExtra("puertoVideo", getIntent().getExtras().getString("puertoVideo"));
				i.putExtra("esComunicacionDirecta", getIntent().getExtras().getBoolean("esComunicacionDirecta"));
				
				i.setAction("android.intent.action.MAIN");
				i.addCategory("android.intent.category.LAUNCHER");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				

				i.putExtra("foto", getIntent().getExtras().getString("foto"));
				startActivity(i);	
				finish();
			}
		});
		
		if(getIntent().getExtras() != null &&  getIntent().getExtras().get("comando").equals(YACSmartProperties.INT_INICIAR_LLAMADA)){
			PowerManager pm;
			WakeLock wl;
			KeyguardManager km;
//			KeyguardLock kl;
			
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			km =(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
//			kl = km.newKeyguardLock("INFO");
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "INFO");
			wl.acquire(); //wake up the screen
////			kl.disableKeyguard();// dismiss the keyguard
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			    
			 
			//Colocar foto
			rutaImagen = getIntent().getStringExtra("foto");
			
			Bitmap bmImg = BitmapFactory.decodeFile(getIntent().getStringExtra("foto"));
			if(bmImg != null){
				imagenInicial.setRotation(180);
				imagenInicial.setImageBitmap(bmImg);
				imagenRecibida = true;
			}
//			nombreTablet.setText(getIntent().getExtras().getString("nombreEquipo"));
			
			TimbrarAsyncTask genericoAsyncTask = new TimbrarAsyncTask(getApplicationContext(), LlamadaEntranteActivity.this);
			genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			wl.release();
			 
		}
	}
	
	public void timerDelayRemoveView(long time) {
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() {           
	        public void run() {
	        	llamadaAtendida = true;
	        	finish();
	        }
	    }, time); 
	}
	
	
}

class TimbrarAsyncTask extends AsyncTask<String, Float, String> {

	Context context;
	LlamadaEntranteActivity llamadaEntranteActivity;
	Ringtone r;
	Bitmap bmImg;
	
	public TimbrarAsyncTask(Context context, LlamadaEntranteActivity llamadaEntranteActivity) {
		super();
		this.context = context;
		this.llamadaEntranteActivity = llamadaEntranteActivity;
	}



	@Override
	protected String doInBackground(String... arg0) {
//		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//		String ring = sharedPrefs.getString("prefRingTonePortero", null);
		Uri notification;
//		if(ring == null){
			notification = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
//		}else{
//		    notification = Uri.parse(ring);
//		}
		r = RingtoneManager.getRingtone(context, notification);
//		int vecesFoto = 0;
		while(!llamadaEntranteActivity.llamadaAtendida){
//			if(!llamadaEntranteActivity.imagenRecibida){
//				bmImg = BitmapFactory.decodeFile(llamadaEntranteActivity.rutaImagen);
//				if(bmImg != null){
//					llamadaEntranteActivity.imagenInicial.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							llamadaEntranteActivity.imagenInicial.setImageBitmap(bmImg);
//							llamadaEntranteActivity.imagenRecibida = true;
//							
//						}
//					});
//				}
//				
//			}
			r.play();
		}
		
		r.stop();
		return null;
	}



	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(r.isPlaying()){
			r.stop();
		}
	}
	
}

