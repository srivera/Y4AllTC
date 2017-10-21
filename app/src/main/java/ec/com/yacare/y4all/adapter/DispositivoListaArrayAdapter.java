package ec.com.yacare.y4all.adapter;

import android.content.Context;
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
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.lib.dto.Dispositivo;

public class DispositivoListaArrayAdapter extends ArrayAdapter<Dispositivo> {
	private final Context context;
	private final ArrayList<Dispositivo> values;
	private Y4HomeActivity y4HomeActivity;
	private DatosAplicacion datosAplicacion;

	public DispositivoListaArrayAdapter(Context context, ArrayList<Dispositivo> values, Y4HomeActivity y4HomeActivity) {
		super(context, R.layout.respuesta_list, values);
		this.context = context;
		this.values = values;
		this.y4HomeActivity = y4HomeActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.dispositivo_list, parent, false);
		final Dispositivo dispositivo = values.get(position);
		TextView textView = (TextView) rowView.findViewById(R.id.nombreDispositivo);
		textView.setText(dispositivo.getNombreDispositivo());

		TextView tipo = (TextView) rowView.findViewById(R.id.tipoDispositivo);
		tipo.setText(dispositivo.getTipo());

		dispositivo.setBtnLocalizar((Button) rowView.findViewById(R.id.btnlocalizar));
		dispositivo.setBtnBuscar((Button) rowView.findViewById(R.id.btnbuscar));
		textView.setTag(dispositivo);

		Typeface font = Typeface.createFromAsset(y4HomeActivity.getAssets(), "fontawesome-webfont.ttf" );

		dispositivo.getBtnLocalizar().setTypeface(font);
		dispositivo.getBtnBuscar().setTypeface(font);

		datosAplicacion = (DatosAplicacion) y4HomeActivity.getApplicationContext();

		/*dispositivo.getBtnBuscar().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EncontrarDispositivoAsyncTask encontrarDispositivoAsyncTask = new EncontrarDispositivoAsyncTask(y4HomeActivity,
						datosAplicacion.getDispositivoOrigen().getId(), dispositivo.getId());
				encontrarDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			}
		});

		dispositivo.getBtnLocalizar().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("LOCALIZAR", "Enviar a Localizar");
				Intent i = new Intent(y4HomeActivity, MapaActivity.class);
				y4HomeActivity.startActivity(i);
			}
		});*/

		ImageView imagen = (ImageView) rowView.findViewById(R.id.imgDispositivo);


		File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + dispositivo.getImei() + ".jpg");
		if (foto.exists()) {
			Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + dispositivo.getImei() + ".jpg");
			if (bmImg != null) {
				mostrarImagen(imagen, bmImg);

			}
		}

		return rowView;
	}

	Bitmap output;
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

}
