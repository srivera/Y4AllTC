package ec.com.yacare.y4all.activities.dispositivo;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class BuscarTelefonoActivity extends Activity {


	private Button btnCerrar;


	private static MediaPlayer mp1;

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
		setContentView(R.layout.activity_buscar_telefono);

		btnCerrar = (Button) findViewById(R.id.btnCerrar);
		btnCerrar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mp1.stop();
				AudioQueu.buscarCelular = false;
				BuscarTelefonoActivity.this.finish();
			}
		});


		if(getIntent().getExtras() != null &&  getIntent().getExtras().get("buscar") != null) {
			PowerManager pm;
			WakeLock wl;
			KeyguardManager km;
//			KeyguardLock kl;

			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//			kl = km.newKeyguardLock("INFO");
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "INFO");
			wl.acquire(); //wake up the screen
////			kl.disableKeyguard();// dismiss the keyguard
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

			if(!AudioQueu.buscarCelular) {
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
				mp1 = MediaPlayer.create(getApplicationContext(), notification);
				mp1.start();
				AudioQueu.buscarCelular = true;
			}
		}

	}



	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}
}

