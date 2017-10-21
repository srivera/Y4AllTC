package ec.com.yacare.y4all.activities.dispositivo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.adapter.DispositivoArrayAdapter;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.sqllite.DispositivoDataSource;

public class DispositivoActivity extends AppCompatActivity {

	private DispositivoDataSource datasource;
	private DispositivoArrayAdapter adapter;

	protected Object mActionMode;
	public int selectedItem = -1;

	private ListView swipeListView;
	
	private Button btnEscucharAuto;
	private Button btnAnterior;
	private Button btnEscucharActual;
	private Button btnSiguiente;
	private Button btnContestarAutomatico;
	
	private Button btnEnviarMensaje;
	
	private TextView iconoUsuario;
	private TextView iconoMensaje;
	
	private TextView nombreUsuario;
	private TextView ultimoMensaje;
	
	private EditText mensaje;
	
	private DatosAplicacion datosAplicacion;

	public static Boolean escucharAuto = false;

	public static Boolean contestarAutomatico = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dispositivo);
		
		datosAplicacion  = (DatosAplicacion) getApplicationContext();

		datasource = new DispositivoDataSource(getApplicationContext());
		datasource.open();

		ArrayList<Dispositivo> dispositivos = datasource.getAllDispositivo();
		adapter = new DispositivoArrayAdapter(getApplicationContext(), dispositivos, this);

		datasource.close();
		swipeListView = (ListView) findViewById(android.R.id.list);

		swipeListView.setAdapter(adapter);

		String idDispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
		for(Dispositivo dispositivo : dispositivos){
			if(idDispositivo.equals(dispositivo.getImei())){
//				datosAplicacion.setDispositivoOrigen(dispositivo);
//				dispositivos.remove(dispositivo);
				break;
			}
		}

		btnEscucharAuto = (Button) findViewById(R.id.btnEscucharAuto);
		btnAnterior = (Button) findViewById(R.id.btnAnterior);
		btnSiguiente = (Button)findViewById(R.id.btnSiguiente);
		btnEscucharActual = (Button) findViewById(R.id.btnEscucharActual);
		btnContestarAutomatico = (Button) findViewById(R.id.btnContestarAutomatico);

		btnEnviarMensaje = (Button) findViewById(R.id.btnEnviarMensaje);

		Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

		btnEscucharAuto = (Button) findViewById(R.id.btnEscucharAuto);
		btnAnterior = (Button) findViewById(R.id.btnAnterior);
		btnSiguiente = (Button)findViewById(R.id.btnSiguiente);
		btnEscucharActual = (Button) findViewById(R.id.btnEscucharActual);
		btnContestarAutomatico = (Button) findViewById(R.id.btnContestarAutomatico);


		btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				if( (datosAplicacion.getDispositivoOrigen() != null && mensaje.getText().equals(""))){
//					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//					SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
//					Date date = new Date();
//
//					MensajeDataSource mensajeDataSource = new MensajeDataSource(getApplicationContext());
//					mensajeDataSource.open();
//					Mensaje mensajeRecibido = new Mensaje();
//					mensajeRecibido.setTipo(YACSmartProperties.COM_MENSAJE_CHAT);
//					mensajeRecibido.setEstado("REC");
//					mensajeRecibido.setHora(hourFormat.format(date));
//					mensajeRecibido.setFecha(dateFormat.format(date));
//					mensajeRecibido.setIdDispositivo(datosAplicacion.getDispositivoChatActual().getId());
//					mensajeRecibido.setMensaje(mensaje.getText().toString());
//					mensajeRecibido.setId(UUID.randomUUID().toString());
//					mensajeDataSource.createMensajeNew(mensajeRecibido);
//					mensajeDataSource.close();
//					EnviarMensajeChatAsyncTask enviarMensajeChat = new EnviarMensajeChatAsyncTask(DispositivoActivity.this,
//							datosAplicacion.getDispositivoOrigen().getId(),
//							datosAplicacion.getDispositivoChatActual().getId() , mensaje.getText().toString());
//					enviarMensajeChat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				}
			}
		});

//		btnEscucharActual.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				if(((PrincipalMenuActivity)getActivity()).audioMensajeRecibido.size() > 0){
//					if(((PrincipalMenuActivity)getActivity()).escucharAuto){
//						((PrincipalMenuActivity)getActivity()).audioManager.startBluetoothSco();
//						try {
//							Thread.sleep(800);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					Log.d("MENSAJE VOZ","reproducir");
//					AudioTrack track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000,
//							AudioFormat.CHANNEL_CONFIGURATION_MONO,
//							AudioFormat.ENCODING_PCM_16BIT,372,
//							AudioTrack.MODE_STREAM);
//					track.play();
//					int contador = 0;
//					while(contador < ((PrincipalMenuActivity)getActivity()).audioMensajeRecibido.size()){
//						track.write(((PrincipalMenuActivity)getActivity()).audioMensajeRecibido.get(contador), 0, ((PrincipalMenuActivity)getActivity()).audioMensajeRecibido.get(contador).length);
//						contador++;
//					}
//					track.stop();
//					track.release();
//					if(((PrincipalMenuActivity)getActivity()).escucharAuto){
//						((PrincipalMenuActivity)getActivity()).audioManager.stopBluetoothSco();
//					}
//				}else
//				if(((PrincipalMenuActivity)getActivity()).getUltimoMensaje() != null){
//					((PrincipalMenuActivity)getActivity()).convertTextToSpeech(((PrincipalMenuActivity)getActivity()).getUltimoMensaje(), false);
//				}
//			}
//		});
//
//
//		btnEscucharAuto.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				if(DispositivoActivity.escucharAuto){
//					DispositivoActivity.escucharAuto = false;
//					btnEscucharAuto.setTextColor(Color.parseColor("#13977D"));
//					((PrincipalMenuActivity)getActivity()).audioManager.setBluetoothScoOn(false);
//
//				}else{
//					((PrincipalMenuActivity)getActivity()).audioManager.setMode(AudioManager.MODE_IN_CALL);
//					((PrincipalMenuActivity)getActivity()). audioManager.setBluetoothScoOn(true);
//
//					SimpleDateFormat dateFormatHora = new SimpleDateFormat("HH");
//					SimpleDateFormat dateFormatMin = new SimpleDateFormat("mm");
//					Date date = new Date();
//
//					DispositivoActivity.escucharAuto = true;
//					btnEscucharAuto.setTextColor(Color.BLACK);
//
//					((PrincipalMenuActivity)getActivity()).convertTextToSpeech("Why for car ..  otro producto de yacare technology .. sistema de informacion inteligente para su vehiculo ..  " +
//					"son las " + dateFormatHora.format(date) +  " " +dateFormatMin.format(date) + " le mantendre informado sobre acontecimientos cercanos a su ubicacion", false);
//
//				}
//			}
//		});
//
//
		btnContestarAutomatico.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(DispositivoActivity.contestarAutomatico){
					DispositivoActivity.contestarAutomatico = false;
					btnContestarAutomatico.setTextColor(Color.parseColor("#93780D"));
				}else{
					DispositivoActivity.contestarAutomatico = true;
					btnContestarAutomatico.setTextColor(Color.BLACK);
				}
			}
		});


//		btnEscucharAuto.setTypeface(font);
//		btnAnterior.setTypeface(font);
//		btnSiguiente.setTypeface(font);
//		btnEscucharActual.setTypeface(font);
//		btnEnviarMensaje.setTypeface(font);
//		btnContestarAutomatico.setTypeface(font);
//
//		iconoUsuario = (TextView) findViewById(R.id.textDispositivoIcono);
//		iconoMensaje = (TextView) findViewById(R.id.textMensajeIcono);
//		iconoUsuario.setTypeface(font);
//		iconoMensaje.setTypeface(font);
//
		nombreUsuario = (TextView) findViewById(R.id.textNombreUsuario);
		ultimoMensaje = (TextView) findViewById(R.id.textUltimoMensaje);

		mensaje = (EditText) findViewById(R.id.editMensajeChat);

		actualizarPantalla() ;

		swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
//				Dispositivo dispositivo = (Dispositivo) adapter.getItemAtPosition(position);
//				Intent i = new Intent(DispositivoActivity.this, ChatActivity.class);
//				i.putExtra("idDispositivo", dispositivo.getId());
//				i.putExtra("nombreDispositivo", dispositivo.getNombreDispositivo());
//				datosAplicacion.setDispositivoChatActual(dispositivo);
//				startActivity(i);
			}
		});

	}

	
	public void actualizarPantalla() {
//		if((getActivity()) != null && ((PrincipalMenuActivity)getActivity()).getUltimoMensaje() != null){
//			ultimoMensaje.setText(((PrincipalMenuActivity)getActivity()).getUltimoMensaje());
//			nombreUsuario.setText(((PrincipalMenuActivity)getActivity()).getDispositivoDestino().getNombreDispositivo());
//		}
	}
	
}
