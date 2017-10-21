package ec.com.yacare.y4all.activities.evento;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class PlayVideoActivity  extends AppCompatActivity {

	private VideoView videoView;
	private ImageView fotoView, favorito;
	private int position = 0;
	private MediaController mediaController;

	private TextView txtNombrePortero, txtNombrePorteroInferior;
	private TextView txtFechaHoraInferior, txtFechaHora;
	private ImageButton btnEliminar, btnCompartir;
	private ImageButton fabSalir;

	private Evento evento;

	private String accion = "NOT";
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.ac_video_play);

			Typeface fontBold = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
			Typeface fontThin = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
			Typeface fontBlack = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Regular.ttf");

			videoView = (VideoView) findViewById(R.id.videoView);
			fotoView = (ImageView) findViewById(R.id.fotoView);
			favorito = (ImageView) findViewById(R.id.favorito);
			txtNombrePortero = (TextView) findViewById(R.id.txtNombrePortero);
			txtNombrePorteroInferior = (TextView) findViewById(R.id.txtNombrePorteroInferior);
			txtFechaHoraInferior = (TextView) findViewById(R.id.txtFechaHoraInferior);
			txtFechaHora = (TextView) findViewById(R.id.txtFechaHora);
			LinearLayout layoutTitulo = (LinearLayout) findViewById(R.id.layoutTitulo);
			LinearLayout linearLayoutInferior = (LinearLayout) findViewById(R.id.linearLayoutInferior);
			fabSalir = (ImageButton) findViewById(R.id.fabSalir);
			btnEliminar = (ImageButton) findViewById(R.id.btnEliminar);
			btnCompartir = (ImageButton) findViewById(R.id.btnCompartir);


			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
				getWindow().setEnterTransition(slide);
			}
			if(isScreenLarge()) {
			} else {
				AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			evento = (Evento) getIntent().getSerializableExtra("evento");
			if(evento.getEstado() != null && evento.getEstado().equals("FAB")){
				favorito.setImageResource(R.drawable.staryellow);
//				favorito.bringToFront();
			}else {
				favorito.setImageResource(R.drawable.star);
//				favorito.bringToFront();
			}

			if(evento.getColorFondo() != 0) {
				linearLayoutInferior.setBackgroundColor(evento.getColorFondo());
				if(txtNombrePorteroInferior != null) {
					txtNombrePorteroInferior.setTextColor(evento.getColorLetra());
				}
				if(txtFechaHoraInferior != null) {
					txtFechaHoraInferior.setTextColor(evento.getColorLetra());
				}
				btnEliminar.setColorFilter(evento.getColorFondo());
				btnCompartir.setColorFilter(evento.getColorFondo());
			}



			fabSalir.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});


			btnEliminar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					File fileVideo = new File(evento.getVideoBuzon());
					if (fileVideo.exists()) {
						fileVideo.delete();
					}
					fileVideo = new File(evento.getVideoPuerta());
					if (fileVideo.exists()) {
						fileVideo.delete();
					}
					fileVideo = new File(evento.getVideoInicial());
					if (fileVideo.exists()) {
						fileVideo.delete();
					}

					if(getIntent().getExtras().getString("foto") != null) {
						fileVideo = new File(getIntent().getExtras().getString("foto"));
						if (fileVideo.exists()) {
							fileVideo.delete();
						}
					}
					EventoDataSource datasource = new EventoDataSource(getApplicationContext());
					datasource.open();
					datasource.deleteEvento(evento.getId());
					datasource.close();

					Intent output = new Intent();
					output.putExtra("accion", "DEL");
					output.putExtra("indice", getIntent().getStringExtra("indice"));
					setResult(RESULT_OK, output);
					finish();
				}
			});


			favorito.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(evento.getEstado().equals("FAB")){
						accion = "OK";
						Evento evento = (Evento) getIntent().getSerializableExtra("evento");
						evento.setEstado("OK");
						EventoDataSource datasource = new EventoDataSource(getApplicationContext());
						datasource.open();
						datasource.updateEvento(evento);
						datasource.close();
						favorito.setImageResource(R.drawable.star);
//						favorito.bringToFront();
					}else{
						accion = "FAB";
						Evento evento = (Evento) getIntent().getSerializableExtra("evento");
						evento.setEstado("FAB");
						EventoDataSource datasource = new EventoDataSource(getApplicationContext());
						datasource.open();
						datasource.updateEvento(evento);
						datasource.close();
						favorito.setImageResource(R.drawable.staryellow);
//						favorito.bringToFront();
//						favorito.bringToFront();
					}
				}
			});


			btnCompartir.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(getIntent().getExtras().getString("foto") != null) {
						Bitmap bitmap;
						OutputStream output;
						bitmap = BitmapFactory.decodeFile(getIntent().getExtras().getString("foto"));
						File filepath = Environment.getExternalStorageDirectory();
						File dir = new File(filepath.getAbsolutePath() + "/Y4HomeCompartir/");
						dir.mkdirs();
						File file = new File(dir, "sample_wallpaper.png");

						try {
							Intent share = new Intent(Intent.ACTION_SEND);

							share.setType("image/jpeg");

							output = new FileOutputStream(file);
							bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
							output.flush();
							output.close();
							Uri uri = Uri.fromFile(file);
							share.putExtra(Intent.EXTRA_STREAM, uri);
							startActivity(Intent.createChooser(share, "Compartir Y4Home"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						File f = new File(getIntent().getExtras().getString("video"));
						Uri uriPath = Uri.parse(f.getPath());

						Intent shareIntent = new Intent(
								Intent.ACTION_SEND);
						shareIntent.setType("video/*");
						shareIntent.putExtra(
								Intent.EXTRA_SUBJECT, "Video");
						shareIntent.putExtra(
								Intent.EXTRA_TITLE, "Video");
						shareIntent.putExtra(Intent.EXTRA_STREAM, uriPath);
						shareIntent
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
						startActivity(Intent.createChooser(shareIntent,
								"Compartir Wii Bell"));

					}
				}
			});

			txtNombrePortero.setTypeface(fontBold);
			txtFechaHora.setTypeface(fontBlack);

			DatosAplicacion datosAplicacion = (DatosAplicacion) getApplicationContext();
			if(txtNombrePorteroInferior != null) {
				txtNombrePorteroInferior.setTypeface(fontBlack);
				txtNombrePorteroInferior.setText(datosAplicacion.getEquipoSeleccionado().getNombreEquipo());
			}

			txtNombrePortero.setText(datosAplicacion.getEquipoSeleccionado().getNombreEquipo());

			if(txtFechaHoraInferior != null) {
				txtFechaHoraInferior.setText(evento.getFecha());
				txtFechaHoraInferior.setTypeface(fontBlack);
			}
			txtFechaHora.setText(evento.getFecha());

			if(getIntent().getExtras().getString("video") != null) {
				fotoView.setVisibility(View.GONE);
				try {
					videoView.setVideoURI(Uri.parse(getIntent().getExtras().getString("video")));

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				videoView.requestFocus();

				videoView.setOnPreparedListener(new OnPreparedListener() {

					public void onPrepared(MediaPlayer mediaPlayer) {
						videoView.seekTo(position);
						if (position == 0) {
							videoView.start();
						}

						mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
							@Override
							public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
								mediaController.setAnchorView(videoView);
							}
						});
					}
				});



				if (mediaController == null) {
					mediaController = new MediaController(PlayVideoActivity.this);
					videoView.setMediaController(mediaController);
					mediaController.setAnchorView(videoView);

				}

			}else{
				videoView.setVisibility(View.GONE);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inSampleSize = 2;
				Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getExtras().getString("foto"), options);
				fotoView.setImageBitmap(bitmap);
			}
			fabSalir.bringToFront();
			layoutTitulo.bringToFront();
		}
//
//	// Find ID corresponding to the name of the resource (in the directory raw).
//	public int getRawResIdByName(String resName) {
//		String pkgName = this.getPackageName();
//		// Return 0 if not found.
//		int resID = this.getResources().getIdentifier(resName, "raw", pkgName);
//		Log.i("AndroidVideoView", "Res Name: " + resName + "==> Res ID = " + resID);
//		return resID;
//	}

	@Override
	public void onBackPressed() {
		Intent output = new Intent();
		output.putExtra("accion", accion);
		output.putExtra("indice", getIntent().getStringExtra("indice"));
		setResult(RESULT_OK, output);
		super.onBackPressed();
	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	// When you change direction of phone, this method will be called.
	// It store the state of video (Current position)
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// Store current position.
		savedInstanceState.putInt("CurrentPosition", videoView.getCurrentPosition());
		videoView.pause();
	}


	// After rotating the phone. This method is called.
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// Get saved position.
		position = savedInstanceState.getInt("CurrentPosition");
		videoView.seekTo(position);
	}
}