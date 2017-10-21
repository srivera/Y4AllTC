package ec.com.yacare.y4all.activities.focos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.asynctask.ws.GuardarProgramacionAsyncTask;
import ec.com.yacare.y4all.lib.dto.ProgramacionLuces;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.ProgramacionDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;


public class ProgramacionActivity extends FragmentActivity {

	private DatosAplicacion datosAplicacion;

	private Button btnProgramar;

	private ToggleButton tLunes, tMartes, tMiercoles, tJueves, tViernes, tSabado, tDomingo ;

	private TimePicker timePicker;

	private SeekBar seekDuracion;

	private EditText editNombre;

	private TextView txtDuracion;

	private ZonaLuces zonaLuces;

	private Integer hours, minutes;

	private String diasCalculo;

	private ImageButton fabSalir;

	private ProgramacionLuces programacionGuardar;

	private String horaOriginal;

	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_programar_luces);

		zonaLuces = (ZonaLuces) getIntent().getSerializableExtra("zona");

		TextView txtNombre = (TextView) findViewById(R.id.txtNombre);
		if(zonaLuces.getId() != null) {
			txtNombre.setText("Alarma de luces: " + zonaLuces.getNombreZona());
		}else {
			txtNombre.setText("Alarma de luces");
		}

		if(isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		datosAplicacion = (DatosAplicacion) getApplicationContext();
		datosAplicacion.setProgramacionActivity(ProgramacionActivity.this);

		tLunes = (ToggleButton) findViewById(R.id.tLunes);
		tMartes = (ToggleButton) findViewById(R.id.tMartes);
		tMiercoles = (ToggleButton) findViewById(R.id.tMiercoles);
		tJueves = (ToggleButton) findViewById(R.id.tJueves);
		tViernes = (ToggleButton) findViewById(R.id.tViernes);
		tSabado = (ToggleButton) findViewById(R.id.tSabado);
		tDomingo = (ToggleButton) findViewById(R.id.tDomingo);

		btnProgramar = (Button) findViewById(R.id.btnProgramar);

		editNombre = (EditText) findViewById(R.id.editNombreProgramacion);

		timePicker = (TimePicker) findViewById(R.id.timePicker);

		fabSalir = (ImageButton) findViewById(R.id.fabSalir);
		fabSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		txtDuracion = (TextView) findViewById(R.id.txtDuracion);
		seekDuracion = (SeekBar) findViewById(R.id.seekDuracion);

		String tiempo = getIntent().getStringExtra("tiempo");
		if(tiempo != null){
			String[] detalle = tiempo.split(":");
			timePicker.setCurrentHour(Integer.valueOf(detalle[0]));
			timePicker.setCurrentMinute(Integer.valueOf(detalle[1]));
			Integer dia = Integer.valueOf(detalle[2]);
			if(dia.equals(1)){
				tDomingo.setChecked(true);
			}else if(dia.equals(2)){
				tLunes.setChecked(true);
			}else if(dia.equals(3)){
				tMartes.setChecked(true);
			}else if(dia.equals(4)){
				tMiercoles.setChecked(true);
			}else if(dia.equals(5)){
				tJueves.setChecked(true);
			}else if(dia.equals(6)){
				tViernes.setChecked(true);
			}else if(dia.equals(7)){
				tSabado.setChecked(true);
			}
		}

		seekDuracion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				hours = progress / 12;
				minutes = (progress % 12) * 5;
				txtDuracion.setText("Duracion: " + hours + " hrs " + minutes + " min");

			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		btnProgramar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean grabar = true;
				if(editNombre.getText().toString().equals("")){
					grabar = false;
				}
				if(seekDuracion.getProgress() == 0){
					grabar = false;
				}
				if(!tLunes.isChecked() && !tMartes.isChecked() && !tMiercoles.isChecked() && !tJueves.isChecked()
						&& !tViernes.isChecked() && !tSabado.isChecked() && !tDomingo.isChecked()){
					grabar = false;
				}else{
					diasCalculo = (tDomingo.isChecked()?"1":"0") + (tLunes.isChecked()?"1":"0")
					+ (tMartes.isChecked()?"1":"0") + (tMiercoles.isChecked()?"1":"0")
					+ (tJueves.isChecked()?"1":"0") + (tViernes.isChecked()?"1":"0")
					+ (tSabado.isChecked()?"1":"0");
				}


				if(grabar){
					progress = null;
					progress = new ProgressDialog(ProgramacionActivity.this);
					progress.setMessage("Guardando...");
					progress.setCancelable(false);
					progress.show();


					Calendar mCalendar = new GregorianCalendar();
					TimeZone mTimeZone = mCalendar.getTimeZone();
					int mGMTOffset = mTimeZone.getRawOffset();

					programacionGuardar = new ProgramacionLuces();

					programacionGuardar.setIdRouter(zonaLuces.getIdRouter());
					programacionGuardar.setIdZona(zonaLuces.getId());
					programacionGuardar.setNombre(editNombre.getText().toString());
					programacionGuardar.setAccion("1");

					long horaGMT = timePicker.getCurrentHour() - TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
					if(timePicker.getCurrentHour().toString().length() == 1){
						horaOriginal = "0" + timePicker.getCurrentHour().toString() + ":";
					}else{
						horaOriginal = timePicker.getCurrentHour().toString() + ":";
					}

					if(timePicker.getCurrentMinute().toString().length() == 1){
						horaOriginal = horaOriginal + "0" + timePicker.getCurrentMinute().toString();
					}else{
						horaOriginal = horaOriginal + timePicker.getCurrentMinute().toString();
					}

					if(horaGMT > 24){
						horaGMT = horaGMT - 24;
					}else if(horaGMT < 0){
						horaGMT = horaGMT + 24;
					}

					programacionGuardar.setHoraInicio(horaGMT + ":" + timePicker.getCurrentMinute());
					programacionGuardar.setDuracion(hours + ":" + minutes);
					programacionGuardar.setDias(diasCalculo);

					GuardarProgramacionAsyncTask guardarProgramacionAsyncTask = new GuardarProgramacionAsyncTask(ProgramacionActivity.this);
					guardarProgramacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}

			}
		});
	}

	public void verificarProgramacion(String respuesta) {
		//Nueva programacion
		progress.dismiss();
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			JSONObject programacionJSON = null;
			Boolean status = null;
			JSONObject respuestaJSON = null;
			try {
				respuestaJSON = new JSONObject(respuesta);
				status = respuestaJSON.getBoolean("statusFlag");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(status != null && status) {
				if (respuestaJSON.has("programacion")) {
					try {
						programacionJSON = new JSONObject(respuestaJSON.get("programacion").toString());
						if(programacionJSON.has("id")) {
							ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(getApplicationContext());
							programacionDataSource.open();
							programacionGuardar.setHoraInicio(horaOriginal);
							programacionGuardar.setId(programacionJSON.get("id").toString());
							programacionDataSource.createProgramacion(programacionGuardar);
							programacionDataSource.close();
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									ProgramacionActivity.this);
							alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.exito"))
									.setMessage(YACSmartProperties.intance.getMessageForKey("exito.programacion"))
									.setCancelable(false)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											Intent returnIntent = new Intent();
											setResult(1,returnIntent);
											finish();
										}
									});

							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						ProgramacionActivity.this);
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage("Programación en conflicto con otra existente en la misma zona.")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}else{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					ProgramacionActivity.this);
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage(YACSmartProperties.intance.getMessageForKey("error.router"))
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}
	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		datosAplicacion.setProgramacionActivity(null);
	}

	public ProgramacionLuces getProgramacionGuardar() {
		return programacionGuardar;
	}

}
