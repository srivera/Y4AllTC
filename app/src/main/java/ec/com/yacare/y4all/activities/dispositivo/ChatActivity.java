package ec.com.yacare.y4all.activities.dispositivo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.adapter.MensajeArrayAdapter;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Mensaje;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.MensajeDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class ChatActivity extends Activity {

	private ListView listaChat;

	private MensajeArrayAdapter mensajeArrayAdapter;

	private MensajeDataSource mensajeDataSource;

	private ArrayList<Mensaje> mensajes;

	private Button enviarChat;

	private EditText editMensaje;

	private DatosAplicacion datosAplicacion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(AudioQueu.getRequestedOrientation());
		datosAplicacion = (DatosAplicacion) getApplicationContext();
//		getActionBar().setTitle(datosAplicacion.getDispositivoChatActual().getNombreDispositivo());
		getActionBar().setIcon(R.drawable.profile);

		setContentView(R.layout.activity_chat);


//		datosAplicacion.setChatActivity(ChatActivity.this);

		enviarChat = (Button) findViewById(R.id.btnEnviarChat);

		editMensaje = (EditText) findViewById(R.id.editMensaje);


		listaChat = (ListView) findViewById(R.id.mensajes);
		mensajeDataSource = new MensajeDataSource(getApplicationContext());
		mensajeDataSource.open();
		mensajes = mensajeDataSource.getMensajeByDispositivo(getIntent().getExtras().getString("idDispositivo"));
		mensajeArrayAdapter = new MensajeArrayAdapter(getApplicationContext(), mensajes);
		mensajeDataSource.close();
		listaChat.setAdapter(mensajeArrayAdapter);
		listaChat.setSelection(mensajeArrayAdapter.getCount() - 1);


		editMensaje.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				listaChat.setSelection(mensajeArrayAdapter.getCount() - 1);
			}
		});

		enviarChat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editMensaje.getText().toString().length() > 0) {

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
					SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
					Date date = new Date();

					MensajeDataSource mensajeDataSource = new MensajeDataSource(getApplicationContext());
					mensajeDataSource.open();
					Mensaje mensajeRecibido = new Mensaje();
					mensajeRecibido.setTipo(YACSmartProperties.COM_MENSAJE_CHAT);
					mensajeRecibido.setEstado("REC");
					mensajeRecibido.setHora(hourFormat.format(date));
					mensajeRecibido.setFecha(dateFormat.format(date));
//					mensajeRecibido.setIdDispositivo(datosAplicacion.getDispositivoChatActual().getId());
					mensajeRecibido.setMensaje(editMensaje.getText().toString());
					mensajeRecibido.setId(UUID.randomUUID().toString());
					mensajeDataSource.createMensajeNew(mensajeRecibido);
					mensajeDataSource.close();

					listaChat.setAdapter(mensajeArrayAdapter);

//					String mens = editMensaje.getText().toString();
					editMensaje.setText("");

					mensajes.add(mensajeRecibido);
					mensajeArrayAdapter = new MensajeArrayAdapter(getApplicationContext(), mensajes);

					listaChat.setSelection(mensajeArrayAdapter.getCount() - 1);

//					EnviarMensajeChatAsyncTask enviarMensajeChat = new EnviarMensajeChatAsyncTask(ChatActivity.this,
//							datosAplicacion.getDispositivoOrigen().getId(), datosAplicacion.getDispositivoChatActual().getId() , mens);
//					enviarMensajeChat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		});

	}


	public void actualizarPantalla() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mensajeDataSource = new MensajeDataSource(getApplicationContext());
				mensajeDataSource.open();
				mensajes = mensajeDataSource.getMensajeByDispositivo(getIntent().getExtras().getString("idDispositivo"));
				mensajeArrayAdapter = new MensajeArrayAdapter(getApplicationContext(), mensajes);
				mensajeDataSource.close();
				listaChat.setAdapter(mensajeArrayAdapter);
				listaChat.setSelection(mensajeArrayAdapter.getCount() - 1);
			}
		});
	}


	
}
