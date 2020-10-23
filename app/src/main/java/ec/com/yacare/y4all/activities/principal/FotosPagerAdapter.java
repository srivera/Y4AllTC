package ec.com.yacare.y4all.activities.principal;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaActionSound;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.enumer.EstadoEventoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEventoEnum;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;

class FotosPagerAdapter extends PagerAdapter {

	Activity mContext;
	LayoutInflater mLayoutInflater;

	ArrayList<Evento> eventos;
	public FotosPagerAdapter(Activity context, ArrayList<Evento> eventos) {
		mContext = context;
		this.eventos = eventos;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return eventos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View itemView = mLayoutInflater.inflate(R.layout.fragment_screen_slide_page, container, false);

		ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView1);
		ImageButton btnGuardarFotoActual = (ImageButton) itemView.findViewById(R.id.btnGuardarFotoActual);

		if(!eventos.get(position).getId().equals("1")) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + eventos.get(position).getId() + ".jpg");
			imageView.setImageBitmap(bitmap);
			btnGuardarFotoActual.setVisibility(View.GONE);
		}else{
			Bitmap bitmap = BitmapFactory.decodeByteArray(eventos.get(position).getFotoActual(), 0, eventos.get(position).getFotoActual().length);
			imageView.setImageBitmap(bitmap);
			btnGuardarFotoActual.setVisibility(View.VISIBLE);
		}
		Typeface font = Typeface.createFromAsset(mContext.getAssets(), "Lato-Regular.ttf");
		TextView fecha = (TextView) itemView.findViewById(R.id.textoFecha);
		fecha.setTypeface(font);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();

		if(eventos.get(position).getFecha().startsWith(dateFormat.format(date))){
			fecha.setText(eventos.get(position).getFecha().substring(11, 19));
		}else{
			fecha.setText(eventos.get(position).getFecha());
		}
		btnGuardarFotoActual.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MediaActionSound sound = null;
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
					sound = new MediaActionSound();
					sound.play(MediaActionSound.SHUTTER_CLICK);
				}


				DatosAplicacion datosAplicacion = (DatosAplicacion) mContext.getApplicationContext();
				EventoDataSource datasource = new EventoDataSource(mContext);
				datasource.open();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				Evento evento = new Evento();
				evento.setOrigen(TipoEquipoEnum.PORTERO.getDescripcion() + ": " + datosAplicacion.getEquipoSeleccionado().getNombreEquipo());
				evento.setId(UUID.randomUUID().toString());
				evento.setFecha(dateFormat.format(date));
				evento.setEstado(EstadoEventoEnum.RECIBIDO.getCodigo());
				evento.setComando("FOTO");
				evento.setTipoEvento(TipoEventoEnum.FOTO.getCodigo());
				evento.setIdEquipo(datosAplicacion.getEquipoSeleccionado().getId());

				FileOutputStream fileOuputStream = null;
				try {
					fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/"+  evento.getId() +".jpg");
					fileOuputStream.write(eventos.get(position).getFotoActual());
					fileOuputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				evento.setMensaje("S");
				datasource.createEvento(evento);
				datasource.close();
			}
		});
		container.addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
	}
}