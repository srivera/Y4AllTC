package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.dispositivo.DispositivoActivity;
import ec.com.yacare.y4all.activities.dispositivo.MapaActivity;
import ec.com.yacare.y4all.lib.dto.Dispositivo;

public class DispositivoArrayAdapter extends ArrayAdapter<Dispositivo> {
	private final Context context;
	private final ArrayList<Dispositivo> values;
	private DispositivoActivity dispositivoActivity;
	private DatosAplicacion datosAplicacion;

	public DispositivoArrayAdapter(Context context,  ArrayList<Dispositivo> values, DispositivoActivity dispositivoActivity) {
		super(context, R.layout.dispositivo_list, values);
		this.context = context;
		this.values = values;
		this.dispositivoActivity = dispositivoActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.dispositivo_list, parent, false);
		final Dispositivo dispositivo = values.get(position);
		TextView textView = (TextView) rowView.findViewById(R.id.nombreDispositivo);
		textView.setText(dispositivo.getNombreDispositivo());

		TextView textView1 = (TextView) rowView.findViewById(R.id.tipoDispositivo);
		textView1.setText(dispositivo.getTipo());


		dispositivo.setBtnLocalizar((Button) rowView.findViewById(R.id.btnlocalizar));
		dispositivo.setBtnBuscar((Button) rowView.findViewById(R.id.btnbuscar));
		textView.setTag(dispositivo);

		Typeface font = Typeface.createFromAsset(dispositivoActivity.getAssets(), "fontawesome-webfont.ttf" );
		dispositivo.getBtnLocalizar().setTypeface(font);
		dispositivo.getBtnBuscar().setTypeface(font);

		datosAplicacion = (DatosAplicacion) dispositivoActivity.getApplicationContext();

//		dispositivo.getBtnChatear().setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//
//				final EditText input = new EditText(dispositivoActivity);
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.MATCH_PARENT,
//						LinearLayout.LayoutParams.MATCH_PARENT);
//				input.setLayoutParams(lp);
//
//				final AlertDialog d = new AlertDialog.Builder(dispositivoActivity)
//						.setTitle(YACSmartProperties.intance.getMessageForKey("chatear.dispositivo") + " " + dispositivo.getNombreDispositivo())
//						.setCancelable(true)
//						.setView(input)
//						.setPositiveButton("OK",
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog, int which) {
//
//										if (!input.getText().toString().equals("")) {
//
//											SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//											SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
//											Date date = new Date();
//
//											MensajeDataSource mensajeDataSource = new MensajeDataSource(context);
//											mensajeDataSource.open();
//											Mensaje mensajeRecibido = new Mensaje();
//											mensajeRecibido.setTipo(YACSmartProperties.COM_MENSAJE_CHAT);
//											mensajeRecibido.setEstado("REC");
//											mensajeRecibido.setHora(hourFormat.format(date));
//											mensajeRecibido.setFecha(dateFormat.format(date));
//											mensajeRecibido.setIdDispositivo(dispositivo.getId());
//											mensajeRecibido.setMensaje(input.getText().toString());
//											mensajeRecibido.setId(UUID.randomUUID().toString());
//											mensajeDataSource.createMensajeNew(mensajeRecibido);
//											mensajeDataSource.close();
//
//											EnviarMensajeChatAsyncTask enviarMensajeChat = new EnviarMensajeChatAsyncTask(dispositivoActivity,
//													datosAplicacion.getDispositivoOrigen().getId(), dispositivo.getId(), input.getText().toString());
//											enviarMensajeChat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//											dialog.cancel();
//										}
//									}
//								}).create();
//
//				d.show();
//			}
//		});

		dispositivo.getBtnBuscar().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				EncontrarDispositivoAsyncTask encontrarDispositivoAsyncTask = new EncontrarDispositivoAsyncTask(dispositivoActivity,
//						datosAplicacion.getDispositivoOrigen().getId(), dispositivo.getId());
//				encontrarDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			}
		});

//		dispositivo.getBtnLlamar().setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Log.d("LLAMAR", "Enviar Llamada");
//
//			}
//		});


		dispositivo.getBtnLocalizar().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("LOCALIZAR", "Enviar a Localizar");
				Intent i = new Intent(dispositivoActivity, MapaActivity.class);
				dispositivoActivity.startActivity(i);
			}
		});
		return rowView;
	}



}
