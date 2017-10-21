package ec.com.yacare.y4all.activities.respuesta;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.UUID;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Respuesta;
import ec.com.yacare.y4all.lib.enumer.TipoRespuestaEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.RespuestaDataSource;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;

public class NuevaRespuestaActivity extends AppCompatActivity {

	public EditText editNombreDispositivoReg;

	private TextView nombrePortero;

	private ImageButton btnGrabar, btnReproducirR, btnGuardar;

	private AudioRecord audio_recorder = null;
	private HashMap<Integer, byte[]> audioQueu = new HashMap<Integer, byte[]>();

	private int modoAudioAnterior;
	private AudioManager audioManager;

	private AudioTrack track = null;

	private int mSampleRate = Integer.valueOf(YACSmartProperties.getInstance().getMessageForKey("sample.rate"));
	boolean mStartRecording = true;
	boolean mStartPlaying = true;

	private Respuesta respuestaGrabada;
	private DatosAplicacion datosAplicacion;

	private ProgressDialog progressDialog;

	LinearLayout linearLayout;

	private RadioGroup radioTipo, radioTipoRespuesta;

	private Boolean grabando = false;

	private ImageView imgWoman,imgMan;
	private RadioButton femenino, masculino;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vi_nueva_respuesta);
		if (isScreenLarge()) {
//			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		datosAplicacion = (DatosAplicacion) getApplicationContext();

		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		nombrePortero = (TextView) findViewById(R.id.txtNombrePorteroM);
		nombrePortero.setTypeface(fontRegular);
		nombrePortero.setText(datosAplicacion.getEquipoSeleccionado().getNombreEquipo());

		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

		editNombreDispositivoReg = (EditText) findViewById(R.id.editNombreDispositivoReg);

		btnGrabar = (ImageButton) findViewById(R.id.btnGrabar);
		btnReproducirR = (ImageButton) findViewById(R.id.btnReproducirR);
		btnGuardar = (ImageButton) findViewById(R.id.btnGuardar);

		radioTipo = (RadioGroup)findViewById(R.id.radioTipoVoz);
		radioTipoRespuesta = (RadioGroup)findViewById(R.id.radioTipoRespuesta);
		femenino = (RadioButton)findViewById(R.id.femenino);
		masculino = (RadioButton)findViewById(R.id.masculino);

		imgWoman = (ImageView) findViewById(R.id.imgWoman);
		imgWoman.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				femenino.setChecked(true);
				masculino.setChecked(false);
			}
		});

		imgMan = (ImageView) findViewById(R.id.imgMan);
		imgMan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				femenino.setChecked(false);
				masculino.setChecked(true);
			}
		});

		btnReproducirR.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onPlay(mStartPlaying);
				mStartPlaying = !mStartPlaying;
			}
		});

		btnGuardar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean grabar = true;

				int checkedRadioButtonId = radioTipo.getCheckedRadioButtonId();
				if (checkedRadioButtonId == -1) {
					grabar = false;
				}

				int checkedRadioButtonIdTipoRespuesta = radioTipoRespuesta.getCheckedRadioButtonId();
				if (checkedRadioButtonIdTipoRespuesta == -1) {
					grabar = false;
				}

				if (editNombreDispositivoReg.getText() == null || editNombreDispositivoReg.getText().equals("")) {
					grabar = false;
					editNombreDispositivoReg.setError("Ingrese el nombre de la respuesta");
				}

				if(audioQueu.size() < 1 ){
					grabar = false;
				}

				if (grabar) {
					progressDialog = new ProgressDialog(NuevaRespuestaActivity.this);
					progressDialog.setMessage("Espere un momento ....");
					progressDialog.setTitle("Enviando la Respuesta");
					progressDialog.show();

					Respuesta respuesta = new Respuesta();
					respuesta.setNombre(editNombreDispositivoReg.getText().toString());
					respuesta.setId(UUID.randomUUID().toString());

					if (checkedRadioButtonIdTipoRespuesta == R.id.automatica) {
						respuesta.setTipo(TipoRespuestaEnum.TR02.getCodigo());
					} else if (checkedRadioButtonIdTipoRespuesta == R.id.despedida) {
						respuesta.setTipo(TipoRespuestaEnum.TR04.getCodigo());
					} else {
						respuesta.setTipo(TipoRespuestaEnum.TR03.getCodigo());
					}

					if (checkedRadioButtonId == R.id.femenino) {
						respuesta.setTipoVoz(YACSmartProperties.getInstance().getMessageForKey("tipo.voz.mujer"));
					} else {
						respuesta.setTipoVoz(YACSmartProperties.getInstance().getMessageForKey("tipo.voz.hombre"));
					}
					respuesta.setIdEquipo(datosAplicacion.getEquipoSeleccionado().getId());

					respuestaGrabada = respuesta;
					respuesta.setAudioQueu(audioQueu);
					String comando = YACSmartProperties.ADM_RECIBIR_AUDIO_PREDEFINIDO + ";" + respuestaGrabada.getId() + ";" + respuestaGrabada.getTipo() + ";"
							+ respuestaGrabada.getNombre() + ";" + respuestaGrabada.getTipoVoz();

					EnviarComandoThread comandoGenericoAsyncTask = new EnviarComandoThread(NuevaRespuestaActivity.this, comando, respuesta, null, null,
							datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, NuevaRespuestaActivity.this);
					comandoGenericoAsyncTask.start();

					if (audio_recorder != null) {
						audio_recorder.stop();
						audio_recorder.release();
						audio_recorder = null;
					}
				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							NuevaRespuestaActivity.this);
					alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
							.setMessage("Los datos ingresados están incompletos")
							.setCancelable(false)
							.setPositiveButton("OK",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
								}
							});
				}
			}

		});

		btnGrabar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!grabando){
					grabando = true;
					btnGrabar.setImageResource(R.drawable.microjo);
					onRecord(mStartRecording);
					mStartRecording = !mStartRecording;
				}else{
					grabando = false;
					btnGrabar.setImageResource(R.drawable.micceleste);
					onRecord(mStartRecording);
					mStartRecording = !mStartRecording;
				}
			}
		});

	}

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void startRecording() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		final int minBufferSize = AudioRecord.getMinBufferSize(
				mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		audio_recorder = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER| MediaRecorder.AudioSource.MIC, mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				audioQueu = new HashMap<Integer, byte[]>();
				int bytes_read = 0;
				int TAMANO_PAKETE = 372;
				byte[] sendData = new byte[TAMANO_PAKETE];

				int i = 0;

				audio_recorder.startRecording();
				while(audio_recorder != null){
					try {
						sendData = new byte[TAMANO_PAKETE];
						bytes_read =audio_recorder.read(sendData, 0, TAMANO_PAKETE);
						audioQueu.put(i, sendData);
						i++;
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}

	private void stopRecording() {
		audio_recorder.stop();
		audio_recorder.release();
		audio_recorder= null;
	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}


	private void startPlaying() {
		Integer encoding = AudioFormat.ENCODING_PCM_16BIT;
		audioManager  =(AudioManager) getSystemService(Context.AUDIO_SERVICE);
		modoAudioAnterior = audioManager.getMode();
	    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		audioManager.setSpeakerphoneOn(true);
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, mSampleRate,
				AudioFormat.CHANNEL_OUT_MONO,
				encoding,  AudioTrack.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT),
				AudioTrack.MODE_STREAM);
		try {
			track.play();
			Integer contador = 0;

			while (contador < audioQueu.size() ) {
				byte[] receiveData = audioQueu.get(contador);
				if (receiveData != null) {
					track.write(receiveData, 0, receiveData.length);
				}
				contador++;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		 audioManager.setMode(modoAudioAnterior);
	}

	private void stopPlaying() {
		track.release();
		track = null;
		audioManager.setMode(modoAudioAnterior);
		audioManager.setSpeakerphoneOn(false);
	}

	public void mostrarResultado(Boolean exito){
		if(exito) {
			RespuestaDataSource dataSource = new RespuestaDataSource(getApplicationContext());
			dataSource.open();
			dataSource.createRespuesta(respuestaGrabada);
			dataSource.close();
			progressDialog.dismiss();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					NuevaRespuestaActivity.this);
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
					.setMessage("La respuesta ha sido enviada a su portero")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent output = new Intent();
							output.putExtra("respuesta", respuestaGrabada);
							setResult(RESULT_OK, output);
							finish();
						}
					});
			alertDialogBuilder.show();
		}else{
			progressDialog.dismiss();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					NuevaRespuestaActivity.this);
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage("Vuelva a intentar enviar la respuesta")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					});
			alertDialogBuilder.show();
		}
	}
	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

}