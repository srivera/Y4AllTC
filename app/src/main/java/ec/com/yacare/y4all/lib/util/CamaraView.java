package ec.com.yacare.y4all.lib.util;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;

public class CamaraView  extends SurfaceView implements SurfaceHolder.Callback{


	private SurfaceHolder mHolder;
	private Camera mCamera; 
	private boolean isPaused=false;
	public boolean isUpstreaming=true;


	private static final String TAG = "CamView [UPSTREAM]";
	private Integer contador;
	int frecuencia =0;
	int modulo = 5;

	int pH,pW;

	CamaraView(Context context) 
	{
		super(context);  
		contador = 0;
	}

	public CamaraView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		contador = 0;
	}

	public CamaraView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		contador = 0;
	}

	public void setCamera(Camera camera) 
	{
		mCamera=camera;
		if(mCamera != null)
		{
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(320, 240);
			mCamera.setParameters(parameters);
			Camera.Size s=parameters.getPreviewSize();
			pW=s.width;
			pH=s.height;
		}
	}

	public void Initialize()
	{
		mHolder=getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);               
	}

	public void Pause()
	{
		if(isUpstreaming)
			isPaused=true;

	}

	public void Resume()
	{
		if(isUpstreaming)
			isPaused=false;

	}


	public void surfaceCreated(SurfaceHolder holder)
	{
		if (mCamera != null)
		{

			mCamera.setPreviewCallback(new PreviewCallback()
			{
				public void onPreviewFrame(byte[] data, Camera c)
				{
					if(frecuencia % modulo == 0 ){
						final YuvImage imgPreview = new YuvImage(data, ImageFormat.NV21,
								mCamera.getParameters().getPreviewSize().width, mCamera
										.getParameters().getPreviewSize().height, null);
						byte[] buffer;
						ByteArrayOutputStream jpegOutStream = new ByteArrayOutputStream();
						imgPreview.compressToJpeg(new Rect(0, 0, imgPreview.getWidth(),
								imgPreview.getHeight()), 50, jpegOutStream);

						buffer = jpegOutStream.toByteArray();
						AudioQueu.getVideoIntercom().put(contador, buffer);
						contador++;
					}
					frecuencia = frecuencia + 1;

				}
			});
			try
			{
				mCamera.setPreviewDisplay(holder);
			}
			catch(Exception ex)
			{

			}


		}
	}


	public void releaseAll()
	{
		if(mCamera!=null)
		{
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
			Log.i("Camera","released...........");
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		// empty stub
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
	{
		// empty stub
	}

}
