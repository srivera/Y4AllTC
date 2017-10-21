package ec.com.yacare.y4all.activities.portero;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.asynctask.ws.ObtenerFotoAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.io.ComandoIOScheduledTask;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class LlamadaEntrantePorteroActivity extends Activity {


	public ImageView imagenInicial;
	private TextView nombrePortero;
	private TextView txtResponder;
	private TextView txtRechazar;
	private TextView txtCargando;
	private TextView txtHora;

	private ImageButton btnResponder;
	private ImageButton btnRechazar;

	public Boolean llamadaAtendida;

	public String rutaImagen;

	private LinearLayout indicator;

	public Boolean esComunicacionDirecta;

	public Date horaTimbre = null;

	private DatosAplicacion datosAplicacion;
	private boolean imagen  = false;

	Ringtone r;
	Bitmap bmImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_llamada_entrante_portero);

		if(isScreenLarge()) {
//			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		datosAplicacion = (DatosAplicacion) getApplicationContext();
		if(!AudioQueu.socketComando) {
			AudioQueu.setComandoRecibido(new ConcurrentHashMap<Integer, JSONObject>());
			AudioQueu.setComandoEnviado(new ConcurrentHashMap<Integer, String>());
			AudioQueu.contadorComandoEnviado = 0;
			ComandoIOScheduledTask comandoIOScheduledTask = new ComandoIOScheduledTask(null, datosAplicacion, datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
			comandoIOScheduledTask.start();
			AudioQueu.socketComando = true;
			AudioQueu.isInBackground = false;
		}
		AudioQueu.monitorearPortero = true;

		ImageView mimageView = (ImageView) findViewById(R.id.fotocasa);
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()+".jpg");
		if(file.exists()){
			Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()+".jpg");
			if(bmImg != null){

				File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()+".jpg");
				if(foto.exists()){
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inSampleSize = 2;
					bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/"+ datosAplicacion.getEquipoSeleccionado().getNumeroSerie()+".jpg", options);
					if(bmImg != null){
						mostrarImagen(mimageView, bmImg);
					}
				}
			}
		}else {
			Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.logo2)).getBitmap();
			mostrarImagen(mimageView, bitmap);
		}

		esComunicacionDirecta = getIntent().getExtras().getBoolean("esComunicacionDirecta");

		llamadaAtendida = false;

		AudioQueu.imagenRecibida = false;

		horaTimbre = new Date();

		imagenInicial = (ImageView) findViewById(R.id.imgFotoVisitanteTim);

		nombrePortero = (TextView) findViewById(R.id.txtNombrePorteroTim);

		txtResponder = (TextView) findViewById(R.id.txtResponder);

		txtRechazar = (TextView) findViewById(R.id.txtRechazar);

		txtCargando = (TextView) findViewById(R.id.txtCargando);

		txtHora = (TextView) findViewById(R.id.txtHora);

		btnResponder = (ImageButton) findViewById(R.id.btnResponderTimbrada);

		btnRechazar = (ImageButton) findViewById(R.id.btnRechazarTimbrada);

		indicator = (LinearLayout) findViewById(R.id.indicator);

		btnRechazar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				llamadaAtendida = true;
				AudioQueu.llamadaEntrante = false;
				Log.d("AudioQueu.llamadaEntrante","false");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				AudioQueu.monitorearPortero = false;
				finish();

			}
		});


		btnResponder.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View arg0) {
				llamadaAtendida = true;
				AudioQueu.llamadaEntrante = false;

				AudioQueu.monitorearPortero = true;
				Intent i = new Intent(LlamadaEntrantePorteroActivity.this, MonitorIOActivity.class);
				i.putExtra("monitorear", true);
				startActivity(i);

			}
		});




		if(getIntent().getExtras() != null &&  getIntent().getExtras().get("timbrando") != null){
			PowerManager pm;
			WakeLock wl;
			KeyguardManager km;
//			KeyguardLock kl;

			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			km =(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
//			kl = km.newKeyguardLock("INFO");
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "INFO");
			wl.acquire();
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);


			//Colocar foto
			rutaImagen = getIntent().getStringExtra("foto");

			datosAplicacion = (DatosAplicacion) getApplicationContext();

			String visitante = getIntent().getExtras().getString("visitante");
			if(visitante == null || visitante.equals("null")){
				visitante = "";
			}

			Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");

			nombrePortero.setText(datosAplicacion.getEquipoSeleccionado().getNombreEquipo() + visitante);
			nombrePortero.setTypeface(fontRegular);
			txtRechazar.setTypeface(fontRegular);
			txtResponder.setTypeface(fontRegular);
			txtCargando.setTypeface(fontRegular);
			txtHora.setTypeface(fontRegular);

			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			txtHora.setText(dateFormat.format(date));

			TimbrarAsyncTask genericoAsyncTask = new TimbrarAsyncTask(getApplicationContext(), LlamadaEntrantePorteroActivity.this);
			genericoAsyncTask.start();
			wl.release();

			Intent intentReloj = new Intent();
			intentReloj.setAction("ec.com.yacare.action.PORTERO");
			intentReloj.putExtra("origen", datosAplicacion.getEquipoSeleccionado().getNombreEquipo());
			intentReloj.putExtra("mensaje","TIMBRANDO");
			sendBroadcast(intentReloj);
			try {
				Thread.sleep(500);
				sendBroadcast(intentReloj);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!llamadaAtendida){
			AudioQueu.monitorearPortero = false;
		}
	}

	private void mostrarImagen(ImageView mimageView, Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 2000;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		mimageView.setImageBitmap(output);


	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}



	class TimbrarAsyncTask extends Thread{

		Context context;
		LlamadaEntrantePorteroActivity llamadaEntrantePorteroActivity;
		Ringtone r;
		Bitmap bmImg;

		public TimbrarAsyncTask(Context context, LlamadaEntrantePorteroActivity llamadaEntrantePorteroActivity) {
			super();
			this.context = context;
			this.llamadaEntrantePorteroActivity = llamadaEntrantePorteroActivity;
		}



		public void run() {
			String ring = datosAplicacion.getEquipoSeleccionado().getTono();

			Uri notification;
			if(ring == null || ring.equals("0")){
				notification = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ding);
			}else{
				notification = Uri.parse(ring);
			}
			r = RingtoneManager.getRingtone(context, notification);

			Log.d("TimbrarAsyncTask","1");
			long tiempoEspera = (long) (new Date()).getTime() - horaTimbre.getTime();;

			AudioQueu.buscandoFoto = false;
			int vecesFoto = 500;
			while(!llamadaEntrantePorteroActivity.llamadaAtendida && AudioQueu.llamadaEntrante && tiempoEspera <= 30000){
				tiempoEspera = (long) (new Date()).getTime() - horaTimbre.getTime();
				if(AudioQueu.imagenRecibida && !imagen){
					bmImg = BitmapFactory.decodeByteArray(AudioQueu.fotoTimbre, 0, AudioQueu.fotoTimbre.length);
					if(bmImg != null ) {
						llamadaEntrantePorteroActivity.imagenInicial.post(new Runnable() {
							@Override
							public void run() {
								imagenInicial.setVisibility(View.VISIBLE);
								indicator.setVisibility(View.GONE);
								llamadaEntrantePorteroActivity.imagenInicial.setImageBitmap(bmImg);
								imagen = true;
							}
						});
					}
				}else{
					Log.d("vecesFoto", "" + vecesFoto);
					if(!imagen){
						if(vecesFoto == 500){
							ObtenerFotoAsyncTask genericoAsyncTask = new ObtenerFotoAsyncTask(LlamadaEntrantePorteroActivity.this, llamadaEntrantePorteroActivity.getIntent().getExtras().getString("idEvento"));
							genericoAsyncTask.start();
							vecesFoto = 0;
						}else{
							vecesFoto = vecesFoto + 1;
						}


					}else{
						vecesFoto = vecesFoto + 1;
					}

				}
				r.play();
			}
			AudioQueu.llamadaEntrante = false;
			r.stop();
			finish();
		}
	}
}
