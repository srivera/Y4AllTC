package ec.com.yacare.y4all.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.evento.ImageSliderActivity;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;

public class SliderArrayAdapter extends PagerAdapter {
	private ImageSliderActivity _activity;
	private ArrayList<String> _imagePaths;
	private LayoutInflater inflater;


	// constructor
	public SliderArrayAdapter(ImageSliderActivity activity,
								  ArrayList<String> imagePaths) {
		this._activity = activity;
		this._imagePaths = imagePaths;
	}

	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		TouchImageView imgDisplay;
		ImageButton btnClose;
		ImageButton btnEliminar, btnCompartir;
		final ImageView  favorito;
		final TextView txtNombrePortero;
		final TextView  txtFechaHora;
		final VideoView videoView;
		Typeface fontBold = Typeface.createFromAsset(_activity.getAssets(), "Roboto-Bold.ttf");


		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
				false);

		EventoDataSource datasource = new EventoDataSource(_activity.getApplicationContext());
		datasource.open();
		String idEvento = _imagePaths.get(position);
		String[] parts = idEvento.split("/");
		String id = parts[parts.length - 1];
		Evento  eventoBusqueda = new Evento();
		if(_activity.getIntent().getExtras().getString("videoPuerta") != null) {
			eventoBusqueda.setId(id.substring(0, id.indexOf("P.")));
		}else{
			eventoBusqueda.setId(id.substring(0, id.indexOf(".")));
		}
		final Evento  evento = datasource.getEventoId(eventoBusqueda);
		datasource.close();

		txtNombrePortero = (TextView) viewLayout.findViewById(R.id.txtNombrePortero);
		txtFechaHora = (TextView) viewLayout.findViewById(R.id.txtFechaHora);
		imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
		btnClose = (ImageButton) viewLayout.findViewById(R.id.btnClose);
		btnEliminar = (ImageButton) viewLayout.findViewById(R.id.btnEliminar);
		btnCompartir = (ImageButton) viewLayout.findViewById(R.id.btnCompartir);
		favorito = (ImageView) viewLayout.findViewById(R.id.favorito);
		videoView = (VideoView) viewLayout.findViewById(R.id.videoView);

		btnClose.bringToFront();
		txtNombrePortero.setTypeface(fontBold);
		txtFechaHora.setTypeface(fontBold);
		DatosAplicacion datosAplicacion = (DatosAplicacion) _activity.getApplicationContext();
		txtNombrePortero.setText(datosAplicacion.getEquipoSeleccionado().getNombreEquipo());
		if (evento != null) {
			txtFechaHora.setText(evento.getFecha());
			if (idEvento.toUpperCase().endsWith("MP4")) {
				imgDisplay.setVisibility(View.GONE);
				videoView.setVisibility(View.VISIBLE);
				if(videoView.isPlaying()){
					videoView.stopPlayback();
				}

				try {
					if(evento.getTipoEvento().equals("BUZON")) {
						videoView.setVideoURI(Uri.parse(evento.getVideoBuzon()));
					}else{
						videoView.setVideoURI(Uri.parse(evento.getVideoPuerta()));
					}

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				videoView.requestFocus();

				videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

					public void onPrepared(MediaPlayer mediaPlayer) {
						videoView.seekTo(position);
						if (position == 0) {
							videoView.start();
						}

//						mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//							@Override
//							public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//								_activity.mediaController.setAnchorView(videoView);
//							}
//						});
					}
				});


//				if (_activity.mediaController == null) {
					_activity.mediaController = new MediaController(_activity);
					videoView.setMediaController(_activity.mediaController);
					_activity.mediaController.setAnchorView(videoView);
//				}
			}
		}
		txtNombrePortero.bringToFront();
		txtFechaHora.bringToFront();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
		imgDisplay.setImageBitmap(bitmap);

		if (evento != null) {
			if (evento.getEstado().equals("FAB")) {
				favorito.setImageResource(R.drawable.staryellow);
				favorito.setColorFilter(Color.YELLOW);
			} else {
				favorito.setImageResource(R.drawable.star);
			}
		}

		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				_activity.onBackPressed();
			}
		});

		btnEliminar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						_activity);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("confirmacion.eliminar"))
						.setCancelable(false)
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						})
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (evento != null) {
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
									fileVideo = new File(_activity.getIntent().getExtras().getString("foto"));
									if (fileVideo.exists()) {
										fileVideo.delete();
									}
									EventoDataSource datasource = new EventoDataSource(_activity.getApplicationContext());
									datasource.open();
									datasource.deleteEvento(evento.getId());
									datasource.close();

									if(position < (getCount()-1)) {
										_activity.viewPager.setCurrentItem(position-1);
									} else {
										_activity.viewPager.setCurrentItem(getCount()-1);
									}
									_imagePaths.remove(position);
									//View view = _activity.viewPager.getChildAt(position);
									notifyDataSetChanged();
									_activity.viewPager.getAdapter().notifyDataSetChanged();

								}
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();


			}
		});


		favorito.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (evento != null) {
					if (evento.getEstado().equals("FAB")) {
						_activity.accion = "OK";
						favorito.setColorFilter(Color.WHITE);
						evento.setEstado("OK");
						EventoDataSource datasource = new EventoDataSource(_activity.getApplicationContext());
						datasource.open();
						datasource.updateEvento(evento);
						datasource.close();
						favorito.setImageResource(R.drawable.star);
					} else {
						_activity.accion = "FAB";
						favorito.setColorFilter(Color.YELLOW);
						evento.setEstado("FAB");
						EventoDataSource datasource = new EventoDataSource(_activity.getApplicationContext());
						datasource.open();
						datasource.updateEvento(evento);
						datasource.close();
						favorito.setImageResource(R.drawable.staryellow);
					}
				}
			}
		});


		btnCompartir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(_activity.getIntent().getExtras().getString("foto") != null) {
					Bitmap bitmap;
					OutputStream output;
					bitmap = BitmapFactory.decodeFile(_activity.getIntent().getExtras().getString("foto"));
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
						_activity.startActivity(Intent.createChooser(share, "Compartir Y4Home"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					File f = new File(_activity.getIntent().getExtras().getString("video"));
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
					_activity.startActivity(Intent.createChooser(shareIntent,
							"Compartir Wii Bell"));

				}
			}
		});
		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}

}
