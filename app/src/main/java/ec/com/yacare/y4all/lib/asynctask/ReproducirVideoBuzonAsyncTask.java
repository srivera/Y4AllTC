package ec.com.yacare.y4all.lib.asynctask;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class ReproducirVideoBuzonAsyncTask extends AsyncTask<String, Float,  String> implements OnPreparedListener, android.media.MediaPlayer.OnCompletionListener{
	
	
		private MediaPlayer mMediaPlayer;
	private SurfaceView mPreview;
	private SurfaceHolder mHolder;
	
	private String idBuzon;
	private Button cerrarBuzon;
	
	public ReproducirVideoBuzonAsyncTask(String idBuzon, SurfaceView mPreview, Button cerrarBuzon ) {
		super();
		this.idBuzon = idBuzon;
		this.mPreview = mPreview;
		this.cerrarBuzon = cerrarBuzon;
	}


	@Override
	protected  String doInBackground(String... arg0) {
		try{

			mPreview.post(new Runnable() {
				@Override
				public void run() {
					mPreview.setVisibility(View.VISIBLE);
			    	mPreview.bringToFront();
				}
			});
		
			cerrarBuzon.post(new Runnable() {
				@Override
				public void run() {
					cerrarBuzon.setVisibility(View.VISIBLE);
					cerrarBuzon.bringToFront();
				}
			});	
			
			cerrarBuzon.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (mMediaPlayer != null) {
//						AudioQueu.setDetenerVideo(true);
						mMediaPlayer.stop();
						mMediaPlayer.release();
						mMediaPlayer = null;
						mPreview.post(new Runnable() {
							@Override
							public void run() {
								mPreview.setVisibility(View.INVISIBLE);
						    	
							}
						});
						cerrarBuzon.post(new Runnable() {
							@Override
							public void run() {
								cerrarBuzon.setVisibility(View.INVISIBLE);
							}
						});	
					}
				}
			});
			
			
			mHolder = mPreview.getHolder();
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mMediaPlayer = new MediaPlayer();
			
			mMediaPlayer.setDataSource(Environment.getExternalStorageDirectory()+ "/dcim/" +  idBuzon +".mp4");
			mMediaPlayer.setDisplay(mHolder);
			mMediaPlayer.prepare();
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnCompletionListener(this);
//			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			
		}catch (Exception e1){
			e1.printStackTrace();
		}
		return "";
	}
	

	@Override
	public void onPrepared(MediaPlayer arg0) {
		mMediaPlayer.start();
	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		if(mMediaPlayer != null){
			mMediaPlayer.reset();
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mPreview.post(new Runnable() {
				@Override
				public void run() {
					mPreview.setVisibility(View.INVISIBLE);
			    	
				}
			});
			cerrarBuzon.post(new Runnable() {
				@Override
				public void run() {
					cerrarBuzon.setVisibility(View.INVISIBLE);
				}
			});	
		}
	}

	
}
