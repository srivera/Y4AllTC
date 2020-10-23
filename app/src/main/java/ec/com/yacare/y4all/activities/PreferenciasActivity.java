package ec.com.yacare.y4all.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import ec.com.yacare.y4all.asynctask.ws.GuardarFotoEquipoAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.hotspot.ComandoHotSpotScheduledTask;
import ec.com.yacare.y4all.lib.asynctask.io.ComandoProbarReinicioScheduledTask;
import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;

public class PreferenciasActivity extends FragmentActivity {

	private ToggleButton toggleApertura;
	private ToggleButton toggleSensor;
	private ToggleButton toggleTono;
	private ToggleButton togglePuertos;
	private ToggleButton toggleTimbreExterno;
	private ToggleButton toggleModo;
	private ToggleButton toggleWifi;
	private ToggleButton togglePorton;
	private Spinner spinnerTiempoPorton;
	//private ToggleButton toggleVolumenAlto;
	//private CheckBox checkBoxVecesTimbre;
	private TableRow rowPorton,rowPorton1, rowPorton2, rowSonido,rowSonido2,rowSonido3,rowSonido4, rowSonido5,rowSonido6;

	private EditText editMensajeTimbrar;
	private EditText editClavePuerta;
	private EditText editNombreWifi;
	private EditText editClaveWifi;
	private EditText editMensajeApertura;
	private EditText editMensajePuerta;

	private TextView txtTipoVozSeleccionada ;
	private String codigoVoz;

	private ArrayList<String> voces;
	private ArrayList<String> tiempo;

	private Button btnGuardarPreferencias, btnReiniciarNumeroSerie, btnReinicio, btnActualizarVersion, btnCasa, btnEdificio;
	private ImageButton btnPlay;

	private RadioGroup opciones_timbre;
	private RadioGroup opciones_volumen;

	private DatosAplicacion datosAplicacion;

	private String chosenRingtone = "";

	private String clavePuerta;

	private ImageButton btnProfileDispositivo;

	private ImageView fotoPerfilDispositivo;

	private TextView txtMensaje, txtSensorPuerta,tituloTimbre,txtTimbreExterno,txtVecesTimbre,txtVolumenMensajes;

	private String userChoosenTask;
	private int REQUEST_CAMERA = 0, SELECT_FILE = 1, PIC_CROP = 2;

	Boolean mensajeWifi = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_preferences);
		if (isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		datosAplicacion = (DatosAplicacion) getApplicationContext();

		fotoPerfilDispositivo = (ImageView) findViewById(R.id.fotoPerfilDispositivo);
		Bitmap bitmap ;
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()+".jpg");
		if(file.exists()){
			Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" +  datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ".jpg");
			if(bmImg != null){

				File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" +  datosAplicacion.getEquipoSeleccionado().getNumeroSerie() +".jpg");
				if(foto.exists()){
					bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" +  datosAplicacion.getEquipoSeleccionado().getNumeroSerie() +".jpg");
					if(bmImg != null){
						mostrarImagen(fotoPerfilDispositivo, bmImg);

					}
				}
			}
		}else {
			bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.usuario)).getBitmap();
			mostrarImagen(fotoPerfilDispositivo, bitmap);
		}

		btnProfileDispositivo = (ImageButton) findViewById(R.id.btnProfileDispositivo);
		btnProfileDispositivo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = { "Tomar Foto", "Galeria",
						"Cancelar" };
				AlertDialog.Builder builder = new AlertDialog.Builder(PreferenciasActivity.this);
				builder.setTitle("Foto de Perfil");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {

						if (items[item].equals("Tomar Foto")) {
							userChoosenTask="Tomar Foto";
							cameraIntent();
						} else if (items[item].equals("Galeria")) {
							userChoosenTask="Galeria";
							galleryIntent();
						} else if (items[item].equals("Cancelar")) {
							dialog.dismiss();
						}
					}
				});
				builder.show();

			}
		});

		rowPorton = (TableRow) findViewById(R.id.rowPorton);
		rowPorton1 = (TableRow) findViewById(R.id.rowPorton1);
		rowPorton2 = (TableRow) findViewById(R.id.rowPorton2);

		rowSonido = (TableRow) findViewById(R.id.rowSonido);
		rowSonido2 = (TableRow) findViewById(R.id.rowSonido2);
		rowSonido3 = (TableRow) findViewById(R.id.rowSonido3);
		rowSonido4 = (TableRow) findViewById(R.id.rowSonido4);
		rowSonido5 = (TableRow) findViewById(R.id.rowSonido5);
		rowSonido6 = (TableRow) findViewById(R.id.rowSonido6);

		toggleApertura = (ToggleButton) findViewById(R.id.toggleApertura);
		toggleSensor = (ToggleButton) findViewById(R.id.toggleSensor);
		toggleTono = (ToggleButton) findViewById(R.id.toggleTono);
		togglePuertos = (ToggleButton) findViewById(R.id.togglePuertos);
		toggleTimbreExterno = (ToggleButton) findViewById(R.id.toggleTimbreExterno);
		toggleModo = (ToggleButton) findViewById(R.id.toggleModo);
		toggleWifi = (ToggleButton) findViewById(R.id.toggleWifi);
		togglePorton = (ToggleButton) findViewById(R.id.togglePorton);
		//toggleVolumenAlto = (ToggleButton) findViewById(toggleVolumenAlto);

		//checkBoxVecesTimbre = (CheckBox) findViewById(checkBoxVecesTimbre);

		editMensajeTimbrar = (EditText) findViewById(R.id.editMensajeTimbrar);
		editClavePuerta = (EditText) findViewById(R.id.editClavePuerta);
		editNombreWifi = (EditText) findViewById(R.id.editNombreWifi);
		editMensajeApertura = (EditText) findViewById(R.id.editMensajeApertura);
		editClaveWifi = (EditText) findViewById(R.id.editClaveWifi);
		editMensajePuerta = (EditText) findViewById(R.id.editMensajePuerta);
		txtMensaje = (TextView) findViewById(R.id.txtMensaje);
		txtSensorPuerta = (TextView) findViewById(R.id.txtSensorPuerta);
		tituloTimbre = (TextView) findViewById(R.id.tituloTimbre);
		txtTimbreExterno = (TextView) findViewById(R.id.txtTimbreExterno);
		txtVecesTimbre = (TextView) findViewById(R.id.txtVecesTimbre);
		txtVolumenMensajes = (TextView) findViewById(R.id.txtVolumenMensajes);
		spinnerTiempoPorton = (Spinner) findViewById(R.id.spinnerTiempoPorton);

		ImageButton fabSalir = (ImageButton) findViewById(R.id.fabSalir);
		fabSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		editClavePuerta.setText("");

		txtTipoVozSeleccionada = (TextView) findViewById(R.id.txtTipoVozSeleccionada);

		btnPlay = (ImageButton) findViewById(R.id.btnPlay);

		btnGuardarPreferencias = (Button) findViewById(R.id.btnGuardarPreferencias);
		btnReiniciarNumeroSerie = (Button) findViewById(R.id.btnReiniciarNumeroSerie);
		opciones_timbre = (RadioGroup) findViewById(R.id.opciones_timbre);
		opciones_volumen = (RadioGroup) findViewById(R.id.opciones_volumen);
		btnReinicio = (Button) findViewById(R.id.btnReinicio);

		btnReiniciarNumeroSerie.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

				String datosConfT = "Z99" //0
						+ ";" + nombreDispositivo //1
						+ ";" + "ANDROID" //2
						+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
						+ ";";

				EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
						null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
				enviarComandoThread.start();
			}
		});


		btnActualizarVersion = (Button) findViewById(R.id.btnActualizarVersion);

		btnActualizarVersion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

					String datosConfT = "V01"//0
							+ ";" + nombreDispositivo //1
							+ ";" + "ANDROID" //2
							+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
							+ ";";

					EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
							null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					enviarComandoThread.start();
				} else {

					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

					String datosConfT =  "V01" //0
							+ ";" + nombreDispositivo //1
							+ ";" + "ANDROID" //2
							+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
							+ ";";

					AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
					AudioQueu.contadorComandoEnviado++;
				}
			}
		});

		btnCasa = (Button) findViewById(R.id.btnCasa);

        btnCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String datosConfT = "V07"
                        + ";" + "C" //2
                        + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
                        + ";";

                EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
                        null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
                enviarComandoThread.start();
            }
        });

        btnEdificio = (Button) findViewById(R.id.btnEdificio);

        btnEdificio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String datosConfT = "V07"
                        + ";" + "E" //2
                        + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
                        + ";";

                EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
                        null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
                enviarComandoThread.start();

            }
        });
		btnReinicio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				ComandoProbarReinicioScheduledTask enviarComandoThread = new ComandoProbarReinicioScheduledTask(datosAplicacion.getEquipoSeleccionado());
				enviarComandoThread.start();
			}
		});

//		if(datosAplicacion.getEquipoSeleccionado().getTiempoEncendidoLuz() == null ){
//			txtValorEncenderLuz.setText("5 min");
//		}else{
//			txtValorEncenderLuz.setText( datosAplicacion.getEquipoSeleccionado().getTiempoEncendidoLuz() / 60 / 1000 + " min" );
//		}

//		txtValorEncenderLuz.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
//				AlertDialog.Builder alertDialog = new AlertDialog.Builder(PreferenciasActivity.this);
//				LayoutInflater inflater1 = getLayoutInflater();
//				View convertView = (View) inflater1.inflate(R.layout.seleccionar_equipo, null);
//
//				View convertViewTitulo = (View) inflater1.inflate(R.layout.seleccionar_equipo_titulo, null);
//				TextView titulo = (TextView) convertViewTitulo.findViewById(R.id.titulo);
//				titulo.setText("Seleccione el tiempo");
//				titulo.setTypeface(fontRegular);
//				alertDialog.setCustomTitle(convertViewTitulo);
//				alertDialog.setView(convertView);
//
//				tiempo = new ArrayList<String>();
//				tiempo.add("1 min");
//				tiempo.add("2 min");
//				tiempo.add("3 min");
//				tiempo.add("4 min");
//				tiempo.add("5 min");
//
//				ArrayAdapter<String> adapterVoces = new ArrayAdapter<String>( getApplicationContext(), R.layout.li_mensaje_texto,R.id.nombreMensaje, tiempo);
//
//
//				alertDialog.setCancelable(true);
//				alertDialog.setSingleChoiceItems(adapterVoces, 0, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						txtValorEncenderLuz.setText(tiempo.get(which));
//						datosAplicacion.getEquipoSeleccionado().setTiempoEncendidoLuz((which + 1) * 60 * 1000);
//						dialog.dismiss();
//					}
//				});
//				alertDialog.show();
//			}
//		});
//		txtTiempoEncenderLuz.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
//				AlertDialog.Builder alertDialog = new AlertDialog.Builder(PreferenciasActivity.this);
//				LayoutInflater inflater1 = getLayoutInflater();
//				View convertView = (View) inflater1.inflate(R.layout.seleccionar_equipo, null);
//
//				View convertViewTitulo = (View) inflater1.inflate(R.layout.seleccionar_equipo_titulo, null);
//				TextView titulo = (TextView) convertViewTitulo.findViewById(R.id.titulo);
//				titulo.setText("Seleccione el tiempo");
//				titulo.setTypeface(fontRegular);
//				alertDialog.setCustomTitle(convertViewTitulo);
//				alertDialog.setView(convertView);
//
//				tiempo = new ArrayList<String>();
//				tiempo.add("1 min");
//				tiempo.add("2 min");
//				tiempo.add("3 min");
//				tiempo.add("4 min");
//				tiempo.add("5 min");
//
//				ArrayAdapter<String> adapterVoces = new ArrayAdapter<String>( getApplicationContext(), R.layout.li_mensaje_texto,R.id.nombreMensaje, tiempo);
//
//
//				alertDialog.setCancelable(true);
//				alertDialog.setSingleChoiceItems(adapterVoces, 0, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						txtValorEncenderLuz.setText(tiempo.get(which));
//						datosAplicacion.getEquipoSeleccionado().setTiempoEncendidoLuz((which + 1) * 60 * 1000);
//						dialog.dismiss();
//					}
//				});
//				alertDialog.show();
//			}
//		});

		if(datosAplicacion.getEquipoSeleccionado().getPuerta() != null && datosAplicacion.getEquipoSeleccionado().getPuerta().equals("1")){
			toggleApertura.setChecked(true);
		}
		if(datosAplicacion.getEquipoSeleccionado().getSensor() != null && datosAplicacion.getEquipoSeleccionado().getSensor().equals("1")){
			toggleSensor.setChecked(true);
		}
//		if(datosAplicacion.getEquipoSeleccionado().getLuz() != null && datosAplicacion.getEquipoSeleccionado().getLuz().equals("1")){
//			toggleLuzExterna.setChecked(true);
//		}
//		if(datosAplicacion.getEquipoSeleccionado().getLuzWifi() != null && datosAplicacion.getEquipoSeleccionado().getLuzWifi().equals("1")){
//			toggleLuzWifi.setChecked(true);
//		}
		if(datosAplicacion.getEquipoSeleccionado().getTono() != null && !datosAplicacion.getEquipoSeleccionado().getTono().equals("0")){
			toggleTono.setChecked(true);
		}
//		if(datosAplicacion.getEquipoSeleccionado().getPuertoActivo() != null && datosAplicacion.getEquipoSeleccionado().getPuertoActivo().equals("1")){
//			togglePuertos.setChecked(true);
//		}
		if(datosAplicacion.getEquipoSeleccionado().getVolumen() != null && datosAplicacion.getEquipoSeleccionado().getVolumen().equals(1)){
			opciones_volumen.check(R.id.radio_alto);
		}else{
			opciones_volumen.check(R.id.radio_medio);
		}

		List<String> list = new ArrayList<String>();
		list.add("3");
		list.add("4");
		list.add("5");
		list.add("6");
		list.add("7");
		list.add("8");
		list.add("9");
		list.add("10");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTiempoPorton.setAdapter(dataAdapter);

		if(datosAplicacion.getEquipoSeleccionado().getTimbreExterno() != null && datosAplicacion.getEquipoSeleccionado().getTimbreExterno().equals("1")){
			toggleTimbreExterno.setChecked(true);
			opciones_timbre.setVisibility(View.VISIBLE);
			opciones_timbre.setEnabled(true);
			opciones_timbre.check(R.id.radio_uno);
			togglePorton.setChecked(true);
			rowSonido.setVisibility(View.VISIBLE);
			rowSonido2.setVisibility(View.VISIBLE);
			rowSonido3.setVisibility(View.VISIBLE);
			rowSonido4.setVisibility(View.VISIBLE);
			rowSonido5.setVisibility(View.VISIBLE);
			rowSonido6.setVisibility(View.VISIBLE);

			rowPorton.setVisibility(View.GONE);
			rowPorton1.setVisibility(View.GONE);
			rowPorton2.setVisibility(View.GONE);
		}else if(datosAplicacion.getEquipoSeleccionado().getTimbreExterno() != null && datosAplicacion.getEquipoSeleccionado().getTimbreExterno().equals("2")){
			toggleTimbreExterno.setChecked(true);
			opciones_timbre.setVisibility(View.VISIBLE);
			opciones_timbre.setEnabled(true);
			opciones_timbre.check(R.id.radio_dos);
			togglePorton.setChecked(true);
			rowSonido.setVisibility(View.VISIBLE);
			rowSonido2.setVisibility(View.VISIBLE);
			rowSonido3.setVisibility(View.VISIBLE);
			rowSonido4.setVisibility(View.VISIBLE);
			rowSonido5.setVisibility(View.VISIBLE);
			rowSonido6.setVisibility(View.VISIBLE);

			rowPorton.setVisibility(View.GONE);
			rowPorton1.setVisibility(View.GONE);
			rowPorton2.setVisibility(View.GONE);
		}else if(datosAplicacion.getEquipoSeleccionado().getTimbreExterno() != null && datosAplicacion.getEquipoSeleccionado().getTimbreExterno().equals("0")){
			togglePorton.setChecked(true);
			toggleTimbreExterno.setChecked(false);
			opciones_timbre.setVisibility(View.VISIBLE);
			opciones_timbre.setEnabled(true);
			opciones_timbre.check(R.id.radio_dos);
			togglePorton.setChecked(true);
			rowSonido.setVisibility(View.VISIBLE);
			rowSonido2.setVisibility(View.VISIBLE);
			rowSonido3.setVisibility(View.VISIBLE);
			rowSonido4.setVisibility(View.VISIBLE);
			rowSonido5.setVisibility(View.VISIBLE);
			rowSonido6.setVisibility(View.VISIBLE);

			rowPorton.setVisibility(View.GONE);
			rowPorton1.setVisibility(View.GONE);
			rowPorton2.setVisibility(View.GONE);
		}else{
			//Porton
			togglePorton.setChecked(false);
			opciones_timbre.setVisibility(View.GONE);
			rowSonido.setVisibility(View.GONE);
			rowSonido2.setVisibility(View.GONE);
			rowSonido3.setVisibility(View.GONE);
			rowSonido4.setVisibility(View.GONE);
			rowSonido5.setVisibility(View.GONE);
			rowSonido6.setVisibility(View.GONE);

			rowPorton.setVisibility(View.VISIBLE);
			rowPorton1.setVisibility(View.VISIBLE);
			rowPorton2.setVisibility(View.VISIBLE);
			spinnerTiempoPorton.setSelection(list.indexOf(datosAplicacion.getEquipoSeleccionado().getTimbreExterno()));
		}

		toggleTimbreExterno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					opciones_timbre.setVisibility(View.VISIBLE);
					opciones_timbre.setEnabled(true);
					rowSonido4.setVisibility(View.VISIBLE);
					rowSonido5.setVisibility(View.VISIBLE);
					rowSonido6.setVisibility(View.VISIBLE);

				}else{
					rowSonido4.setVisibility(View.GONE);
					rowSonido5.setVisibility(View.GONE);
					rowSonido6.setVisibility(View.GONE);
					opciones_timbre.setVisibility(View.GONE);
				}
			}
		});

		togglePorton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					opciones_timbre.setVisibility(View.VISIBLE);
					opciones_timbre.setEnabled(true);
					rowSonido.setVisibility(View.VISIBLE);
					rowSonido2.setVisibility(View.VISIBLE);
					rowSonido3.setVisibility(View.VISIBLE);
					rowSonido4.setVisibility(View.VISIBLE);
					rowSonido5.setVisibility(View.VISIBLE);
					rowSonido6.setVisibility(View.VISIBLE);

					rowPorton.setVisibility(View.GONE);
					rowPorton1.setVisibility(View.GONE);
					rowPorton2.setVisibility(View.GONE);
				}else{
					opciones_timbre.setVisibility(View.GONE);
					rowSonido.setVisibility(View.GONE);
					rowSonido2.setVisibility(View.GONE);
					rowSonido3.setVisibility(View.GONE);
					rowSonido4.setVisibility(View.GONE);
					rowSonido5.setVisibility(View.GONE);
					rowSonido6.setVisibility(View.GONE);

					rowPorton.setVisibility(View.VISIBLE);
					rowPorton1.setVisibility(View.VISIBLE);
					rowPorton2.setVisibility(View.VISIBLE);
				}
			}
		});


		if(datosAplicacion.getEquipoSeleccionado().getModo() != null && datosAplicacion.getEquipoSeleccionado().getModo().equals(YACSmartProperties.MODO_WIFI)){
			toggleWifi.setChecked(true);
		}else{
			toggleWifi.setChecked(false);
		}
		if(datosAplicacion.getEquipoSeleccionado().getVozMensaje() != null){
			if(datosAplicacion.getEquipoSeleccionado().getVozMensaje().equals(YACSmartProperties.VOZ_HOMBRE1) ){
				txtTipoVozSeleccionada.setText("Voz de Hombre");
			}else if(datosAplicacion.getEquipoSeleccionado().getVozMensaje().equals(YACSmartProperties.VOZ_MUJER2)){
				txtTipoVozSeleccionada.setText("Voz de Mujer Latina");
			}else{
				txtTipoVozSeleccionada.setText("Voz de Mujer");
			}
			codigoVoz = datosAplicacion.getEquipoSeleccionado().getVozMensaje();
		}else{
			txtTipoVozSeleccionada.setText("Voz de Mujer Latina");
			codigoVoz = YACSmartProperties.VOZ_MUJER2;
		}
		txtTipoVozSeleccionada.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(PreferenciasActivity.this);
				LayoutInflater inflater1 = getLayoutInflater();
				View convertView = (View) inflater1.inflate(R.layout.seleccionar_equipo, null);

				View convertViewTitulo = (View) inflater1.inflate(R.layout.seleccionar_equipo_titulo, null);
				TextView titulo = (TextView) convertViewTitulo.findViewById(R.id.titulo);
				titulo.setText("Seleccione el tipo de voz");
				titulo.setTypeface(fontRegular);
				alertDialog.setCustomTitle(convertViewTitulo);
				alertDialog.setView(convertView);

				voces = new ArrayList<String>();
				voces.add("Voz de Hombre");
				voces.add("Voz de Mujer Latina");
				voces.add("Voz de Mujer");
				ArrayAdapter<String> adapterVoces = new ArrayAdapter<String>( getApplicationContext(), R.layout.li_mensaje_texto,R.id.nombreMensaje, voces );


				alertDialog.setCancelable(true);
				alertDialog.setSingleChoiceItems(adapterVoces, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 0){
							txtTipoVozSeleccionada.setText("Voz de Hombre");
							codigoVoz = YACSmartProperties.VOZ_HOMBRE1;
						}else if(which == 1){
							txtTipoVozSeleccionada.setText("Voz de Mujer Latina");
							codigoVoz = YACSmartProperties.VOZ_MUJER2;
						}else{
							txtTipoVozSeleccionada.setText("Voz de Mujer");
							codigoVoz = YACSmartProperties.VOZ_MUJER1;
						}

						dialog.dismiss();
					}
				});
				alertDialog.show();
			}
		});

		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					InputStream is = null;
					if(codigoVoz.equals(YACSmartProperties.VOZ_MUJER1)){
						is = getResources().openRawResource(R.raw.m1);
					}else if(codigoVoz.equals(YACSmartProperties.VOZ_MUJER2)){
						is = getResources().openRawResource(R.raw.m2);
					}else if(codigoVoz.equals(YACSmartProperties.VOZ_HOMBRE1)){
						is = getResources().openRawResource(R.raw.h1);
					}

					int size = is.available();
					byte[] buffer = new byte[size]; //declare the size of the byte array with size of the file
					is.read(buffer); //read file
					is.close();

					playStreamAudio(buffer);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		chosenRingtone = datosAplicacion.getEquipoSeleccionado().getTono();
		editMensajeTimbrar.setText(datosAplicacion.getEquipoSeleccionado().getMensajeInicial());
		editMensajeApertura.setText(datosAplicacion.getEquipoSeleccionado().getMensajeApertura());
		editMensajePuerta.setText(datosAplicacion.getEquipoSeleccionado().getMensajePuerta());

		toggleTono.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
					startActivityForResult(intent, 5);
				} else {
					chosenRingtone = "0";
				}
			}
		});



//		toggleModo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked) {
//					if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
//
//						SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//						String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
//
//						String datosConfT = YACSmartProperties.HOTSPOT_MODO_HOTSPOT //0
//								+ ";" + nombreDispositivo //1
//								+ ";" + "ANDROID" //2
//								+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
//								+ ";";
//
//						EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
//								null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
//						enviarComandoThread.start();
//					}
//
//				}
//			}
//		});

		toggleWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				if(mensajeWifi) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							PreferenciasActivity.this);
					alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
							.setMessage(YACSmartProperties.intance.getMessageForKey("texto.cambiar.wifi"))
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {

									if (isChecked) {
										if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
											SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
											String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

											String datosConfT = YACSmartProperties.COM_HABILITAR_WIFI //0
													+ ";" + nombreDispositivo //1
													+ ";" + "ANDROID" //2
													+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
													+ ";";

											EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
													null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
											enviarComandoThread.start();
										} else {

											SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
											String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

											String datosConfT = YACSmartProperties.COM_HABILITAR_WIFI //0
													+ ";" + nombreDispositivo //1
													+ ";" + "ANDROID" //2
													+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
													+ ";";

											AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
											AudioQueu.contadorComandoEnviado++;
										}

										datosAplicacion.getEquipoSeleccionado().setModo(YACSmartProperties.MODO_WIFI);
									} else {
										if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
											SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
											String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

											String datosConfT = YACSmartProperties.COM_DESHABILITAR_WIFI //0
													+ ";" + nombreDispositivo //1
													+ ";" + "ANDROID" //2
													+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
													+ ";";

											EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
													null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
											enviarComandoThread.start();
										} else {
											SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
											String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");

											String datosConfT = YACSmartProperties.COM_DESHABILITAR_WIFI //0
													+ ";" + nombreDispositivo //1
													+ ";" + "ANDROID" //2
													+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
													+ ";";

											AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
											AudioQueu.contadorComandoEnviado++;
										}
										datosAplicacion.getEquipoSeleccionado().setModo(YACSmartProperties.MODO_CABLE);
									}
									EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
									equipoDataSource.open();
									equipoDataSource.updateEquipo(datosAplicacion.getEquipoSeleccionado());
									equipoDataSource.close();
									mensajeWifi = true;
								}
							}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mensajeWifi = false;
							toggleWifi.setChecked(!toggleWifi.isChecked());
							dialog.cancel();
						}
					});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();

				}else{
					mensajeWifi = true;
				}

			}
		});

		btnGuardarPreferencias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(datosAplicacion.getEquipoSeleccionado().getModo().equals(YACSmartProperties.MODO_AP)){
				//if(true){
					if (!editNombreWifi.getText().toString().equals("") && !editClaveWifi.getText().toString().equals("") ) {


						TimeZone tz = TimeZone.getDefault();

						String comando = YACSmartProperties.HOTSPOT_WIFI + ";"+ "1" + ";" + editNombreWifi.getText().toString() + ";" + editClaveWifi.getText().toString() + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
								+ ";"  + datosAplicacion.getEquipoSeleccionado().getNombreEquipo() + ";" + tz.getID() + ";";

						//Validar el numero de serie
						ComandoHotSpotScheduledTask genericoAsyncTask = new ComandoHotSpotScheduledTask(null, comando);
						genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					}

				}else {
                    if(datosAplicacion.getEquipoSeleccionado().getTipoPortero() != null && !datosAplicacion.getEquipoSeleccionado().getTipoPortero().equals(YACSmartProperties.PORTERO_EDIFICIO)) {
                        clavePuerta = "";
                        if (!editClavePuerta.getText().toString().equals("")) {
                            clavePuerta = YACSmartProperties.Encriptar(editClavePuerta.getText().toString(), datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
                        }

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
                        TimeZone tz = TimeZone.getDefault();

                        String valorTimbreExterno = "0";
                        if(togglePorton.isChecked()) {
							if (toggleTimbreExterno.isChecked()) {
								if (opciones_timbre.getCheckedRadioButtonId() == R.id.radio_uno) {
									valorTimbreExterno = "1";
								} else {
									valorTimbreExterno = "2";
								}
							}
						}else{
                        	//Porton
							valorTimbreExterno = spinnerTiempoPorton.getSelectedItem().toString();
						}

                        String volumen = "1";

                        String datosConfT = YACSmartProperties.COM_CONFIGURAR_PARAMETROS //0
                                + ";" + nombreDispositivo //1
                                + ";" + "ANDROID" //2
                                + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
                                + ";" + (toggleApertura.isChecked() ? "1" : "0") //4
                                + ";" + clavePuerta //5
                                + ";" + (toggleSensor.isChecked() ? "1" : "0") //6
                                + ";" + editMensajeTimbrar.getText().toString() //7
                                + ";" + "1100" //8 Contador timbre sensibilidad para leer
                                + ";" + "2" //9
                                + ";" + "20000" //10 Tiempo de espera del buzon
                                + ";" + "6500" //11 Contador sensor de puerta sensibilidad
                                + ";" + "15000" //12 Tiempo de grabacion del buzon
                                + ";" + valorTimbreExterno //13
                                + ";" + editMensajeApertura.getText().toString() //14
                                + ";" + editMensajePuerta.getText().toString() //15
                                + ";" + tz.getID() //16
                                + ";" + datosAplicacion.getEquipoSeleccionado().getTiempoEncendidoLuz() //17
                                + ";" + volumen //18
                                + ";";


                        EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
                        equipoDataSource.open();
                        datosAplicacion.getEquipoSeleccionado().setTono(chosenRingtone);
                        datosAplicacion.getEquipoSeleccionado().setVozMensaje(codigoVoz);
                        equipoDataSource.updateEquipo(datosAplicacion.getEquipoSeleccionado());
                        equipoDataSource.close();


                        if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
                            EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
                                    null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
                            enviarComandoThread.start();
                        } else {
                            AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
                            AudioQueu.contadorComandoEnviado++;
                        }
                        finish();
                    }else{
                        clavePuerta = "";
                        if (!editClavePuerta.getText().toString().equals("")) {
                            clavePuerta = YACSmartProperties.Encriptar(editClavePuerta.getText().toString(), datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
                        }

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String nombreDispositivo = sharedPrefs.getString("prefNombreDispositivo", "");
                        TimeZone tz = TimeZone.getDefault();

                        String valorTimbreExterno = "0";
                        if (toggleTimbreExterno.isChecked()) {
                            if (opciones_timbre.getCheckedRadioButtonId() == R.id.radio_uno) {
                                valorTimbreExterno = "1";
                            } else {
                                valorTimbreExterno = "2";
                            }
                        } else {
                            valorTimbreExterno = "10";
                        }

                        String volumen = "1";
                        if (opciones_volumen.getCheckedRadioButtonId() == R.id.radio_alto) {
                            valorTimbreExterno = "1";
                        }
                        String datosConfT = YACSmartProperties.COM_CONFIGURAR_PARAMETROS_EDIFICIO //0
                                + ";" + nombreDispositivo //1
                                + ";" + "ANDROID" //2
                                + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() //3
                                + ";" + (toggleApertura.isChecked() ? "1" : "0") //4
                                + ";" + clavePuerta //5
                                + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroDepartamento() //6
                                + ";";


                        EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
                        equipoDataSource.open();
                        datosAplicacion.getEquipoSeleccionado().setTono(chosenRingtone);
                        datosAplicacion.getEquipoSeleccionado().setVozMensaje(codigoVoz);
                        equipoDataSource.updateEquipo(datosAplicacion.getEquipoSeleccionado());
                        equipoDataSource.close();


                        if (AudioQueu.getTipoConexion().equals(TipoConexionEnum.WIFI.getCodigo())) {
                            EnviarComandoThread enviarComandoThread = new EnviarComandoThread(PreferenciasActivity.this, datosConfT, null,
                                    null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
                            enviarComandoThread.start();
                        } else {
                            AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
                            AudioQueu.contadorComandoEnviado++;
                        }
                        finish();
                    }
				}
			}
		});
		if(datosAplicacion.getEquipoSeleccionado().getTipoPortero() != null && datosAplicacion.getEquipoSeleccionado().getTipoPortero().equals(YACSmartProperties.PORTERO_EDIFICIO)){
			editMensajeTimbrar.setVisibility(View.GONE);
			txtMensaje.setVisibility(View.GONE);
			txtSensorPuerta.setVisibility(View.GONE);
			tituloTimbre.setVisibility(View.GONE);
			txtTimbreExterno.setVisibility(View.GONE);
			txtVecesTimbre.setVisibility(View.GONE);
			txtVolumenMensajes.setVisibility(View.GONE);
            toggleApertura.setVisibility(View.GONE);
            toggleSensor.setVisibility(View.GONE);
            toggleTono.setVisibility(View.GONE);
            togglePuertos.setVisibility(View.GONE);
            toggleTimbreExterno.setVisibility(View.GONE);
            toggleModo.setVisibility(View.GONE);
            toggleWifi.setVisibility(View.GONE);
            opciones_timbre.setVisibility(View.GONE);
            opciones_volumen.setVisibility(View.GONE);
			editMensajePuerta.setVisibility(View.GONE);
			editMensajeApertura.setVisibility(View.GONE);
		}
	}


	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);


		if (resultCode == Activity.RESULT_OK && requestCode == 5)
		{
			Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

			if (uri != null)
			{
				this.chosenRingtone = uri.toString();
			}
			else
			{
				this.chosenRingtone = "";
			}
		}else if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE) {
				onSelectFromGalleryResult(intent);
			}else if (requestCode == REQUEST_CAMERA) {
				onCaptureImageResult(intent);
			}else{
				//get the returned data
				Bundle extras = intent.getExtras();
				//get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");

				FileOutputStream fileOuputStream = null;
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					thePic.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					byte[] bitmapdata = bos.toByteArray();
					fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ".jpg");
					fileOuputStream.write(bitmapdata);
					fileOuputStream.close();

					if(datosAplicacion.getEquipoSeleccionado().getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
						String imageString = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
						String datosConfT = YACSmartProperties.COM_GUARDAR_FOTO_PERFIL + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + imageString + ";";
						AudioQueu.getComandoEnviado().put(AudioQueu.contadorComandoEnviado, datosConfT);
						AudioQueu.contadorComandoEnviado++;
					}else {
						GuardarFotoEquipoAsyncTask guardarFotoEquipoAsyncTask = new GuardarFotoEquipoAsyncTask(PreferenciasActivity.this, bitmapdata, datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
						guardarFotoEquipoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//display the returned cropped image
				mostrarImagen(fotoPerfilDispositivo, thePic);


			}
		}
	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}


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
	private void cameraIntent()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_CAMERA);
	}

	private void galleryIntent()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Seleccione una foto"), SELECT_FILE);
	}
	private void onCaptureImageResult(Intent data) {
		Uri picUri = data.getData();
		performCrop(picUri);

	}


	private void performCrop(Uri picUri ){
		try {
			//call the standard crop action intent (the user device may not support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			//indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			//set crop properties
			cropIntent.putExtra("crop", "true");
			//indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			//indicate output X and Y
			cropIntent.putExtra("outputX", 256);
			cropIntent.putExtra("outputY", 256);
			//retrieve data on return
			cropIntent.putExtra("return-data", true);
			//start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);
		}
		catch(ActivityNotFoundException anfe){
			//display an error message

		}
	}


	private void onSelectFromGalleryResult(Intent data) {
		Uri picUri = data.getData();
		performCrop(picUri);

	}

	AudioTrack audioTrack;
	AudioManager audioManager;
	public void playStreamAudio( byte[] d) {
		initPlayer(d);
		audioTrack.write(d, 0, d.length);
		if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
			audioTrack.release();
		}
	}

	/**
	 * Initialize AudioTrack by getting buffersize
	 */
	private void initPlayer(byte[] d) {
		synchronized (this) {
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setSpeakerphoneOn(true);
			int bs = AudioTrack.getMinBufferSize(22050, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
			audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
					22050,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT,
					bs,
					AudioTrack.MODE_STREAM);
			if (audioTrack != null) {
				audioTrack.setNotificationMarkerPosition(d.length / 2);
				audioTrack.play();
				audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
					@Override
					public void onMarkerReached(AudioTrack track) {
						if(track.getState() == AudioTrack.STATE_INITIALIZED){
							audioManager.setSpeakerphoneOn(false);
							track.stop();
							track.release();
						}
					}

					@Override
					public void onPeriodicNotification(AudioTrack track) {

					}
				});

			}
		}
	}
}
