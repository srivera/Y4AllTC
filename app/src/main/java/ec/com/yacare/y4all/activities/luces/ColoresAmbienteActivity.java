package ec.com.yacare.y4all.activities.luces;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.widget.ImageView;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.asynctask.luces.ProcesarImagenesLucesScheduledTask;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.util.CamaraView;

public class ColoresAmbienteActivity extends Activity{

	private AudioManager audioManager;
	public CamaraView mPreview;
	public Camera mCamera;

	
	private ImageView vi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_luces_ambiente);
		
		mPreview = (CamaraView)findViewById(R.id.cam);

		vi = (ImageView) findViewById(R.id.lpimg);

    	iniciarVideo();
		ZonaLuces	zonaLuces = (ZonaLuces) getIntent().getSerializableExtra("zona");

		AudioQueu.modoCamaraLuces = true;
		ProcesarImagenesLucesScheduledTask lucesScheduledTask = new ProcesarImagenesLucesScheduledTask(vi, getApplicationContext(), zonaLuces.getNumeroZona() );
		lucesScheduledTask.start();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		AudioQueu.modoCamaraLuces = false;
		mPreview.releaseAll();
		finish();
	}

	public void iniciarVideo() {

			try{
  	        	 for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
	 	            CameraInfo camInfo = new CameraInfo();
	 	            Camera.getCameraInfo(camNo, camInfo);
	 	            if (camInfo.facing!=(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
	 	                mCamera = Camera.open(camNo);
	 	            }
					 if(Camera.getNumberOfCameras() == 1){
						 mCamera = Camera.open(camNo);
					 }
	 	        }
	 	        if (mCamera == null) {
	 	             mCamera = Camera.open();
	 	        }
	
		        Parameters parameters = mCamera.getParameters();
		        Display display = getWindowManager()
		                .getDefaultDisplay();
	
		        int rotation = getResources().getConfiguration().orientation;
		        Log.i("CameraPreviews", "screen rotation is " + rotation);
		        Log.i("CameraPreviews", "display rotation is " + display.getRotation());
		        if (display.getRotation() == Surface.ROTATION_0) {
	
		            if (rotation == Configuration.ORIENTATION_LANDSCAPE) {
		                mCamera.setDisplayOrientation(0);
		            } else {
		                mCamera.setDisplayOrientation(90);
		            }
		        }
	
		        else if (display.getRotation() == Surface.ROTATION_90) {
		            if (rotation == Configuration.ORIENTATION_PORTRAIT) {
		                mCamera.setDisplayOrientation(270);
		            }
		        }
	
		        else if (display.getRotation() == Surface.ROTATION_180) {
		            if (rotation == Configuration.ORIENTATION_LANDSCAPE) {
		            	mCamera.setDisplayOrientation(180);
		            }else {
		                mCamera.setDisplayOrientation(270);
		            }
		        }
	
		        else if (display.getRotation() == Surface.ROTATION_270) {
		            if (rotation == Configuration.ORIENTATION_PORTRAIT) {
		            	mCamera.setDisplayOrientation(90);
		            } else {
		                mCamera.setDisplayOrientation(180);
		            }
		        }
	
		        try {
		        	mCamera.setParameters(parameters);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        mPreview.setCamera(mCamera);
				mPreview.Initialize();
				mCamera.startPreview();
				mPreview.refreshDrawableState();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
}