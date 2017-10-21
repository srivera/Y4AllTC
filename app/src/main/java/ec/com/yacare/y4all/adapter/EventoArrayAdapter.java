package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.util.Item;
import ec.com.yacare.y4all.lib.util.SectionItem;

public class EventoArrayAdapter extends ArrayAdapter<Item> {
	private final Context context;
	private final ArrayList<Item> values;
	
	public EventoArrayAdapter(Context context,  ArrayList<Item> values) {
		super(context, R.layout.evento_list_item, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Item i = values.get(position);

		Typeface fontRegular = Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");

		if (i != null) {
			if (i.isSection()) {
				SectionItem si = (SectionItem) i;
				v = inflater.inflate(R.layout.evento_section, null);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);

				final TextView dia = (TextView) v
						.findViewById(R.id.dia);
				dia.setText(si.getTitle().substring(8,10));
				dia.setTypeface(fontRegular);

				try {
					final String mesTexto = context.getResources().getStringArray(R.array.meses)[Integer.valueOf(si.getTitle().substring(5,7)) - 1];
					final TextView mes = (TextView) v
							.findViewById(R.id.mes);
					mes.setText(mesTexto);
					mes.setTypeface(fontRegular);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}

				final TextView anio = (TextView) v
						.findViewById(R.id.anio);
				anio.setText(si.getTitle().substring(0,4));
				anio.setTypeface(fontRegular);
				try {
					final Calendar calendar = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					calendar.setTime(sdf.parse(si.getTitle()));
					final String nombrediaTexto = context.getResources().getStringArray(R.array.dias)[calendar.get(Calendar.DAY_OF_WEEK) - 1];
					final TextView nombredia = (TextView) v
							.findViewById(R.id.nombredia);
					nombredia.setText(nombrediaTexto);
					nombredia.setTypeface(fontRegular);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}else{
//				if(position % 2 == 0){
					Evento evento = (Evento) i;
					v = inflater.inflate(R.layout.evento_list_item, null);

					TextView textView3 = (TextView) v.findViewById(R.id.hora);
					textView3.setText(evento.getFecha().substring(11, 19));

					File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + evento.getId()+".jpg");
					ImageView imageView = (ImageView) v.findViewById(R.id.foto);
					if(file.exists()){

						Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/"+ evento.getId() + ".jpg");
						if(bmImg != null){
							imageView.setImageBitmap(bmImg);
							imageView.setVisibility(View.VISIBLE);
						}
					}
//				}else{
//					Evento evento = (Evento) i;
//					v = inflater.inflate(R.layout.evento_list_item, null);
//
//					TextView textView3 = (TextView) v.findViewById(R.id.hora1);
//					textView3.setText(evento.getFecha().substring(11, 19));
//
//					File file = new File(context.getCacheDir() + "/" + evento.getId()+".jpg");
//					ImageView imageView = (ImageView) v.findViewById(R.id.foto1);
//					if(file.exists()){
//
//						Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + evento.getId() + ".jpg");
//						if(bmImg != null){
//							imageView.setImageBitmap(bmImg);
//							imageView.setVisibility(View.VISIBLE);
//						}
//					}
//				}

			}
		}
	    return v;
	  }
}