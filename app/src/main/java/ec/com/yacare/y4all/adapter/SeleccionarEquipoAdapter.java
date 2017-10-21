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
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.Equipo;

public class SeleccionarEquipoAdapter extends ArrayAdapter<Equipo> {
	private final Context context;
	private final ArrayList<Equipo> values;


	public SeleccionarEquipoAdapter(Context context,  ArrayList<Equipo> values) {
		super(context, R.layout.equipo_list, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.seleccionar_equipo_item, parent, false);
		Typeface  fontRegular = Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
		final Equipo equipo = values.get(position);
		TextView textView = (TextView) rowView.findViewById(R.id.nombreEquipo);
		textView.setText(equipo.getNombreEquipo());
		textView.setTypeface(fontRegular);
		TextView textView2 = (TextView) rowView.findViewById(R.id.numeroSerie);
		textView2.setText(equipo.getNumeroSerie());
		textView2.setTypeface(fontRegular);

		ImageView imagen = (ImageView) rowView.findViewById(R.id.imgEquipo);

		File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + equipo.getNumeroSerie() + ".jpg");
		if (foto.exists()) {
			Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + equipo.getNumeroSerie() + ".jpg");
			if (bmImg != null) {
				mostrarImagen(bmImg, imagen);

			}
		} else {
			Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.logo8)).getBitmap();
			mostrarImagen(bitmap, imagen);
		}

		return rowView;
	}

	Bitmap output;
	private void mostrarImagen( Bitmap bitmap, ImageView imagen) {
		output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 25;

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

		imagen.setImageBitmap(output);
	}
}
