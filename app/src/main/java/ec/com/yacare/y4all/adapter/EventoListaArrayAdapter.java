package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.evento.PlayVideoActivity;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;

public class EventoListaArrayAdapter extends ArrayAdapter<Evento> {
	private final Context context;
	private final ArrayList<Evento> values;
	private Y4HomeActivity y4HomeActivity;

	public EventoListaArrayAdapter(Context context, ArrayList<Evento> values, Y4HomeActivity y4HomeActivity) {
		super(context, R.layout.evento_list_item, values);
		this.context = context;
		this.values = values;
		this.y4HomeActivity = y4HomeActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		Typeface fontRegular = Typeface.createFromAsset(y4HomeActivity.getAssets(), "Lato-Regular.ttf");

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Evento evento = values.get(position);
		v = inflater.inflate(R.layout.li_evento, null);

		TextView textFecha = (TextView) v.findViewById(R.id.txtFechaEvento);
		textFecha.setText(evento.getFecha12());
		textFecha.setTypeface(fontRegular);

		TextView txtAmPm = (TextView) v.findViewById(R.id.txtAmPm);
		txtAmPm.setText(Html.fromHtml("<small>" + evento.getAmPm() + "</small>"));
		txtAmPm.setTypeface(fontRegular);

		TextView textNumero = (TextView) v.findViewById(R.id.txtNumeroLinea);
		String numero =  String.format("%1$2s", String.valueOf(position + 1)).replace(' ', '0');
		textNumero.setText(numero);
		textNumero.setTypeface(fontRegular);

		TextView texHoy = (TextView) v.findViewById(R.id.txtHoy);
		texHoy.setText(Html.fromHtml("<small>HOY</small>"));
		texHoy.setTypeface(fontRegular);

		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + evento.getId()+".jpg");
		ImageView imageView = (ImageView) v.findViewById(R.id.imgEvento);
		if(file.exists()){
			Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + evento.getId() + ".jpg");
			if(bmImg != null){
				//imageView.setImageBitmap(bmImg);
				mostrarImagen(imageView, bmImg);
				imageView.setVisibility(View.VISIBLE);
			}
		}else{
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setImageResource(R.drawable.nopictures);
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(y4HomeActivity.getApplicationContext());
			String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

			AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SOLICITAR_FOTO_TIMBRE + ";" + nombreDispositivo + ";" + "ANDROID" + ";" +((DatosAplicacion) y4HomeActivity.getApplicationContext()).getEquipoSeleccionado().getNumeroSerie() + ";" + evento.getId() + ";" );
			AudioQueu.contadorComandoEnviado++;
		}

		File fileVideo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + evento.getId()+"T.mp4");
		ImageButton imgButtonVideoInicial = (ImageButton) v.findViewById(R.id.btnVideoInicial);
		if(fileVideo.exists()){
			Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(evento.getVideoInicial(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
			if(bitmap == null) {
				fileVideo.delete();
				imgButtonVideoInicial.setImageResource(R.drawable.download);
			}

			imgButtonVideoInicial.setImageResource(R.drawable.ic_media_play);
			imgButtonVideoInicial.setColorFilter(y4HomeActivity.getResources().getColor(R.color.colorprincipal));

		}else{
			if(evento.getEstado().equals("ERR")){
				if(evento.isSeleccionado()){
					imgButtonVideoInicial.setColorFilter(y4HomeActivity.getResources().getColor(R.color.white));
				}
			}else {
				imgButtonVideoInicial.setImageResource(R.drawable.download);
				imgButtonVideoInicial.setColorFilter(y4HomeActivity.getResources().getColor(R.color.colorprincipal));
			}
		}

		LinearLayout layoutFila =  (LinearLayout) v.findViewById(R.id.layoutFila);
		LinearLayout layoutFecha  =  (LinearLayout) v.findViewById(R.id.layoutFecha);
		if(evento.isSeleccionado()){
			layoutFila.setBackgroundResource(R.color.f_interclaro);
			layoutFecha.setBackgroundResource(R.color.f_interclaro);
			txtAmPm.setBackgroundResource(R.drawable.border_event_list);
			texHoy.setBackgroundResource(R.drawable.border_event_list_colorprincipal);
			textFecha.setBackgroundResource(R.drawable.border_event_list_colorprincipal);
			texHoy.setTextColor(y4HomeActivity.getResources().getColor(R.color.colorprincipal));
			textFecha.setTextColor(y4HomeActivity.getResources().getColor(R.color.colorprincipal));
			txtAmPm.setTextColor(y4HomeActivity.getResources().getColor(R.color.colorprincipal));


		}else{
			layoutFila.setBackgroundResource(R.color.white);
			layoutFecha.setBackgroundResource(R.color.white);
			txtAmPm.setBackgroundResource(R.drawable.border_event_list_inverse);
			texHoy.setBackgroundResource(R.drawable.border_event_list);
			textFecha.setBackgroundResource(R.drawable.border_event_list);
			texHoy.setTextColor(y4HomeActivity.getResources().getColor(R.color.f_gris));
			textFecha.setTextColor(y4HomeActivity.getResources().getColor(R.color.f_gris));

		}
		imgButtonVideoInicial.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File fileVideo = new File(evento.getVideoInicial());
				if(fileVideo.exists()){
					Intent i = new Intent(y4HomeActivity, PlayVideoActivity.class);
					i.putExtra("video", evento.getVideoInicial());
					i.putExtra("fecha", evento.getFecha());
					i.putExtra("evento", evento);
					y4HomeActivity.startActivity(i);
				}else{
					if(AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo()) && !AudioQueu.mSocketComando.connected()){
						String datosConfT = YACSmartProperties.COM_SOLICITAR_VIDEO_INICIAL + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + y4HomeActivity.getEquipoSeleccionado().getNumeroSerie() + ";" + evento.getId() + ";";
						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(y4HomeActivity, datosConfT, null, null,
								null, y4HomeActivity.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
						enviarComandoThread.start();
					}else {
						SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(y4HomeActivity.getApplicationContext());
						String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
						AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, YACSmartProperties.COM_SOLICITAR_VIDEO_INICIAL + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + y4HomeActivity.getEquipoSeleccionado().getNumeroSerie() + ";" + evento.getId() + ";");
						AudioQueu.contadorComandoEnviado++;
					}
					Toast.makeText(y4HomeActivity.getApplicationContext(), YACSmartProperties.intance.getMessageForKey("cargando.archivos.buzon"), Toast.LENGTH_LONG).show();
				}
			}
		});
	    return v;
	  }

	private void mostrarImagen(ImageView mimageView, Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 50;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(10);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		mimageView.setImageBitmap(output);


	}
}