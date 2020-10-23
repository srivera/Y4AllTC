package ec.com.yacare.y4all.activities.luces;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.focos.ProgramacionActivity;
import ec.com.yacare.y4all.asynctask.ws.EliminarProgramacionAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.GuardarZonaAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.io.ComandoIOEncenderLucesScheduledTask;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.ProgramacionLuces;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.focos.ComandoFoco;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.ProgramacionDataSource;
import ec.com.yacare.y4all.lib.sqllite.ZonaDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static android.view.View.inflate;
import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;
import static ec.com.yacare.y4all.lib.resources.YACSmartProperties.COM_LINK_FOCOS;


public class DetalleLucesFragment extends AppCompatActivity {

	private Equipo equipoSeleccionado;
	private DatosAplicacion datosAplicacion;

	private ArrayList<ProgramacionLuces> programaciones;
	private DetalleLucesFragment.ProgramacionAdapter programacionAdapter;

	public ListView listProgramacion;

	private EditText editNombreZona;
	private Button btnActualizar, btnAgregar, btnEliminar;
	private Button btnLink1, btnLink2, btnLink3;
	private Button btn0, btn1, btn2, btn3, btn4, btn5, btnConfigurarLuz;
	private FloatingActionButton btnNuevaProgramacion;

	private ZonaLuces zonaLuces;

	private ImageButton fabSalir;

	private String idBorrar;
	private int linkSeleccionado, tiempoEnviar = 0;

	private LinearLayout layoutLuzWifi, layoutLuzWifi2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_detalle_luces);
		datosAplicacion = (DatosAplicacion) getApplicationContext();
		if (isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		editNombreZona = (EditText) findViewById(R.id.editNombreZona);
		btnActualizar = (Button) findViewById(R.id.btnActualizar);
		btnAgregar = (Button) findViewById(R.id.btnAgregar);
		btnAgregar.setEnabled(false);
		btnEliminar = (Button) findViewById(R.id.btnEliminar);
		fabSalir = (ImageButton) findViewById(R.id.fabSalir);

		btnLink1 = (Button) findViewById(R.id.btnLink1);
		btnLink2 = (Button) findViewById(R.id.btnLink2);
		btnLink3 = (Button) findViewById(R.id.btnLink3);
		btnLink1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linkSeleccionado = 1;
				btnAgregar.setEnabled(true);
				btnLink1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_seleccionado));
				btnLink1.setTextColor(Color.parseColor("#FEFE03"));
				btnLink2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_no_seleccionado));
				btnLink2.setTextColor(Color.parseColor("#AAAAAA"));
				btnLink3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_no_seleccionado));
				btnLink3.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});
		btnLink2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linkSeleccionado = 2;
				btnAgregar.setEnabled(true);
				btnLink2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_seleccionado));
				btnLink2.setTextColor(Color.parseColor("#FEFE03"));
				btnLink1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_no_seleccionado));
				btnLink1.setTextColor(Color.parseColor("#AAAAAA"));
				btnLink3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_no_seleccionado));
				btnLink3.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});
		btnLink3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linkSeleccionado = 3;
				btnAgregar.setEnabled(true);
				btnLink3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_seleccionado));
				btnLink3.setTextColor(Color.parseColor("#FEFE03"));
				btnLink2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_no_seleccionado));
				btnLink2.setTextColor(Color.parseColor("#AAAAAA"));
				btnLink1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_no_seleccionado));
				btnLink1.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});

		btn0 = (Button) findViewById(R.id.btn0);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		btn5 = (Button) findViewById(R.id.btn5);
		btnConfigurarLuz = (Button) findViewById(R.id.btnConfigurarLuz);

		btn0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tiempoEnviar = 0;
				btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
				btn0.setTextColor(Color.parseColor("#FEFE03"));
				btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn1.setTextColor(Color.parseColor("#AAAAAA"));
				btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn2.setTextColor(Color.parseColor("#AAAAAA"));
				btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn3.setTextColor(Color.parseColor("#AAAAAA"));
				btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn4.setTextColor(Color.parseColor("#AAAAAA"));
				btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn5.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tiempoEnviar = 1 * 60 * 1000;
				btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
				btn1.setTextColor(Color.parseColor("#FEFE03"));
				btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn0.setTextColor(Color.parseColor("#AAAAAA"));
				btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn2.setTextColor(Color.parseColor("#AAAAAA"));
				btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn3.setTextColor(Color.parseColor("#AAAAAA"));
				btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn4.setTextColor(Color.parseColor("#AAAAAA"));
				btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn5.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tiempoEnviar = 2 * 60 * 1000;
				btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
				btn2.setTextColor(Color.parseColor("#FEFE03"));
				btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn1.setTextColor(Color.parseColor("#AAAAAA"));
				btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn0.setTextColor(Color.parseColor("#AAAAAA"));
				btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn3.setTextColor(Color.parseColor("#AAAAAA"));
				btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn4.setTextColor(Color.parseColor("#AAAAAA"));
				btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn5.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tiempoEnviar = 3 * 60 * 1000;
				btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
				btn3.setTextColor(Color.parseColor("#FEFE03"));
				btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn1.setTextColor(Color.parseColor("#AAAAAA"));
				btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn2.setTextColor(Color.parseColor("#AAAAAA"));
				btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn0.setTextColor(Color.parseColor("#AAAAAA"));
				btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn4.setTextColor(Color.parseColor("#AAAAAA"));
				btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn5.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tiempoEnviar = 4 * 60 * 1000;
				btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
				btn4.setTextColor(Color.parseColor("#FEFE03"));
				btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn1.setTextColor(Color.parseColor("#AAAAAA"));
				btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn2.setTextColor(Color.parseColor("#AAAAAA"));
				btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn3.setTextColor(Color.parseColor("#AAAAAA"));
				btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn0.setTextColor(Color.parseColor("#AAAAAA"));
				btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn5.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});
		btn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tiempoEnviar = 5 * 60 * 1000;
				btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
				btn5.setTextColor(Color.parseColor("#FEFE03"));
				btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn1.setTextColor(Color.parseColor("#AAAAAA"));
				btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn2.setTextColor(Color.parseColor("#AAAAAA"));
				btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn3.setTextColor(Color.parseColor("#AAAAAA"));
				btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn4.setTextColor(Color.parseColor("#AAAAAA"));
				btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
				btn0.setTextColor(Color.parseColor("#AAAAAA"));
			}
		});


		layoutLuzWifi = (LinearLayout) findViewById(R.id.layoutLuzWifi);
		layoutLuzWifi2 = (LinearLayout) findViewById(R.id.layoutLuzWifi2);
		if (!datosAplicacion.getPorteroInstalado()) {
			layoutLuzWifi.setVisibility(View.GONE);
			layoutLuzWifi2.setVisibility(View.GONE);
		}else{
			layoutLuzWifi.setVisibility(View.VISIBLE);
			layoutLuzWifi2.setVisibility(View.VISIBLE);
		}

		fabSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnNuevaProgramacion = (FloatingActionButton) findViewById(R.id.btnNuevaProgramacion);

		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();

		zonaLuces = (ZonaLuces) getIntent().getSerializableExtra("zona");
		editNombreZona.setText(zonaLuces.getNombreZona());

		listProgramacion = (ListView) findViewById(R.id.listProgramacion);

		ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(getApplicationContext());
		programacionDataSource.open();
		programaciones = programacionDataSource.getAllProgByRouterZona(datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), zonaLuces.getId());
		programacionDataSource.close();
		programacionAdapter = new DetalleLucesFragment.ProgramacionAdapter(getApplicationContext(), programaciones);
		listProgramacion.setAdapter(programacionAdapter);

		if(zonaLuces.getEncenderTimbre() != null && !zonaLuces.getEncenderTimbre().equals("")){
			String valorLuz[] = zonaLuces.getEncenderTimbre().split(";");
			if(valorLuz[0].equals("1")){
				int valor = Integer.valueOf(valorLuz[1]) / 60 / 1000;
				if(valor == 0){
					btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
					btn0.setTextColor(Color.parseColor("#FEFE03"));
					btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn1.setTextColor(Color.parseColor("#AAAAAA"));
					btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn2.setTextColor(Color.parseColor("#AAAAAA"));
					btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn3.setTextColor(Color.parseColor("#AAAAAA"));
					btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn4.setTextColor(Color.parseColor("#AAAAAA"));
					btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn5.setTextColor(Color.parseColor("#AAAAAA"));
				}else if(valor == 1){
					btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
					btn1.setTextColor(Color.parseColor("#FEFE03"));
					btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn0.setTextColor(Color.parseColor("#AAAAAA"));
					btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn2.setTextColor(Color.parseColor("#AAAAAA"));
					btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn3.setTextColor(Color.parseColor("#AAAAAA"));
					btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn4.setTextColor(Color.parseColor("#AAAAAA"));
					btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn5.setTextColor(Color.parseColor("#AAAAAA"));
				}else if(valor == 2){
					btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
					btn2.setTextColor(Color.parseColor("#FEFE03"));
					btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn1.setTextColor(Color.parseColor("#AAAAAA"));
					btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn0.setTextColor(Color.parseColor("#AAAAAA"));
					btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn3.setTextColor(Color.parseColor("#AAAAAA"));
					btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn4.setTextColor(Color.parseColor("#AAAAAA"));
					btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn5.setTextColor(Color.parseColor("#AAAAAA"));
				}else if(valor == 3){
					btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
					btn3.setTextColor(Color.parseColor("#FEFE03"));
					btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn1.setTextColor(Color.parseColor("#AAAAAA"));
					btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn2.setTextColor(Color.parseColor("#AAAAAA"));
					btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn0.setTextColor(Color.parseColor("#AAAAAA"));
					btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn4.setTextColor(Color.parseColor("#AAAAAA"));
					btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn5.setTextColor(Color.parseColor("#AAAAAA"));
				}else if(valor == 4){
					btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
					btn4.setTextColor(Color.parseColor("#FEFE03"));
					btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn1.setTextColor(Color.parseColor("#AAAAAA"));
					btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn2.setTextColor(Color.parseColor("#AAAAAA"));
					btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn3.setTextColor(Color.parseColor("#AAAAAA"));
					btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn0.setTextColor(Color.parseColor("#AAAAAA"));
					btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn5.setTextColor(Color.parseColor("#AAAAAA"));
				}else if(valor == 5){
					btn5.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_seleccionado));
					btn5.setTextColor(Color.parseColor("#FEFE03"));
					btn1.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn1.setTextColor(Color.parseColor("#AAAAAA"));
					btn2.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn2.setTextColor(Color.parseColor("#AAAAAA"));
					btn3.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn3.setTextColor(Color.parseColor("#AAAAAA"));
					btn4.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn4.setTextColor(Color.parseColor("#AAAAAA"));
					btn0.setBackgroundDrawable( getResources().getDrawable(R.drawable.borde_boton_redondo_no_seleccionado));
					btn0.setTextColor(Color.parseColor("#AAAAAA"));
				}
			}
		}

		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();

		btnActualizar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!editNombreZona.getText().toString().equals("") && !editNombreZona.getText().toString().equals(zonaLuces.getNombreZona())){
					GuardarZonaAsyncTask guardarZonaAsyncTask = new GuardarZonaAsyncTask(DetalleLucesFragment.this, zonaLuces, editNombreZona.getText().toString());
					guardarZonaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		});

		btnAgregar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.SUCCESS_TYPE)
						.setTitleText(YACSmartProperties.intance.getMessageForKey("foco.titulo.link"))
						.setContentText(YACSmartProperties.intance.getMessageForKey("foco.mensaje.link"))
						.setConfirmText("Anadir")
						.setCancelText("Regresar")
						.showCancelButton(true)
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.cancel();

							}
						})
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								String datosConfT = COM_LINK_FOCOS + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + zonaLuces.getNumeroZona() + ";" + zonaLuces.getIdRouter() + ";" + linkSeleccionado + ";";
								ComandoFoco comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
								comandoFoco.start();

							}
						})
						.show();


			}
		});

		btnEliminar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.SUCCESS_TYPE)
						.setTitleText(YACSmartProperties.intance.getMessageForKey("foco.titulo.unlink"))
						.setContentText(YACSmartProperties.intance.getMessageForKey("foco.mensaje.unlink"))
						.setConfirmText("Retirar")
						.setCancelText("Regresar")
						.showCancelButton(true)
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.cancel();

							}
						})
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								String datosConfT = YACSmartProperties.COM_UNLINK_FOCOS + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie() + ";" + zonaLuces.getNumeroZona() + ";" + zonaLuces.getIdRouter() + ";";
								ComandoFoco comandoFoco = new ComandoFoco(datosConfT, getApplicationContext());
								comandoFoco.start();
							}
						})
						.show();


			}
		});

		btnConfigurarLuz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (datosAplicacion.getPorteroInstalado()) {
					String valorLuz;
					if (tiempoEnviar > 0) {
						//Enviar trama activo y guardar en la tabla zona
						valorLuz = "1;" + tiempoEnviar + ";";
					} else {
						//Enviar trama desactivo y guardar en la tabla zona
						valorLuz = "0;0;";
					}
					String datosConfT = YACSmartProperties.COM_ACTIVAR_ZONA_TIMBRE + ";"
							+ nombreDispositivo + ";"
							+ "ANDROID" + ";"
							+ equipoSeleccionado.getNumeroSerie() + ";"
							+ zonaLuces.getNumeroZona() + ";"
							+ valorLuz + ";";

					EquipoDataSource equipoDataSource = new EquipoDataSource(getApplicationContext());
					equipoDataSource.open();
					Equipo equipoBusqueda = new Equipo();
					equipoBusqueda.setTipoEquipo(TipoEquipoEnum.PORTERO.getCodigo());
					ArrayList<Equipo> equipos = equipoDataSource.getEquipoTipoEquipo(equipoBusqueda);
					equipoDataSource.close();

					ComandoIOEncenderLucesScheduledTask comandoIOEncenderLucesScheduledTask = new ComandoIOEncenderLucesScheduledTask(datosConfT, equipos );
					comandoIOEncenderLucesScheduledTask.start();
					new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
							.setContentText("La zona fue actualizada")
							.setConfirmText("Aceptar")
							.showCancelButton(true)
							.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.cancel();

								}
							})
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.cancel();

								}
							})
							.show();

				}
			}
		});

		btnNuevaProgramacion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DetalleLucesFragment.this, ProgramacionActivity.class);
				i.putExtra("zona", zonaLuces);
				startActivityForResult(i, 1);
			}
		});


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(getApplicationContext());
		programacionDataSource.open();
		programaciones = programacionDataSource.getAllProgByRouterZona(datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), zonaLuces.getId());
		programacionDataSource.close();
		programacionAdapter = new DetalleLucesFragment.ProgramacionAdapter(getApplicationContext(), programaciones);
		listProgramacion.setAdapter(programacionAdapter);
	}


	public void verificarZonas(String respuesta) {
		//Nueva zona
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String estadoEquipo = null;
			JSONObject zonaJSON = null;
			Boolean status = null;
			JSONObject respuestaJSON = null;
			try {
				respuestaJSON = new JSONObject(respuesta);
				status = respuestaJSON.getBoolean("statusFlag");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(status != null && status) {
				if (respuestaJSON.has("zona")) {
					try {
						zonaJSON = new JSONObject(respuestaJSON.get("zona").toString());
						if(zonaJSON.has("id")) {
							ZonaDataSource zonaDataSource = new ZonaDataSource(getApplicationContext());
							zonaDataSource.open();
							zonaLuces.setNombreZona(zonaJSON.get("nombre").toString());
							zonaDataSource.updateZona(zonaLuces);
							zonaDataSource.close();
							new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.SUCCESS_TYPE)
									.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
									.setContentText("La zona fue actualizada")
									.setConfirmText("Aceptar")
									.showCancelButton(true)
									.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(SweetAlertDialog sDialog) {
											sDialog.cancel();

										}
									})
									.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(SweetAlertDialog sDialog) {
											sDialog.cancel();

										}
									})
									.show();

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.ERROR_TYPE)
						.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setContentText(YACSmartProperties.intance.getMessageForKey("error.router"))
						.setConfirmText("Aceptar")
						.showCancelButton(true)
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.cancel();

							}
						})
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.cancel();

							}
						})
						.show();

			}
		}else{
			new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.ERROR_TYPE)
					.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setContentText(YACSmartProperties.intance.getMessageForKey("error.router"))
					.setConfirmText("Aceptar")
					.showCancelButton(true)
					.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							sDialog.cancel();

						}
					})
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							sDialog.cancel();

						}
					})
					.show();

		}
	}



	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}


	public void verificarEliminarProgramacion(String respuesta) {
		//Nueva zona
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultado = null;
			Boolean status = null;
			JSONObject respuestaJSON = null;
			try {
				respuestaJSON = new JSONObject(respuesta);
				status = respuestaJSON.getBoolean("statusFlag");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(status != null && status) {
				try {
					resultado = respuestaJSON.getString("result");
					if (resultado.equals("OK")) {
						ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(getApplicationContext());
						programacionDataSource.open();
						programacionDataSource.deleteProgramacionById(idBorrar);
						programaciones = programacionDataSource.getAllProgByRouterZona(datosAplicacion.getEquipoSeleccionado().getNumeroSerie(), zonaLuces.getId());
						programacionDataSource.close();

						programacionAdapter.notifyDataSetChanged();
						new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.SUCCESS_TYPE)
								.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
								.setContentText(YACSmartProperties.intance.getMessageForKey("exito.router"))
								.setConfirmText("Aceptar")
								.showCancelButton(true)
								.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.cancel();

									}
								})
								.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.cancel();

									}
								})
								.show();


					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}else{
				new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.ERROR_TYPE)
						.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setContentText(YACSmartProperties.intance.getMessageForKey("error.router"))
						.setConfirmText("Aceptar")
						.showCancelButton(true)
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.cancel();

							}
						})
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.cancel();

							}
						})
						.show();
			}
		}else{
			new SweetAlertDialog(DetalleLucesFragment.this, SweetAlertDialog.ERROR_TYPE)
					.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setContentText(YACSmartProperties.intance.getMessageForKey("error.router"))
					.setConfirmText("Aceptar")
					.showCancelButton(true)
					.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							sDialog.cancel();

						}
					})
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							sDialog.cancel();

						}
					})
					.show();
		}
	}

	class ProgramacionAdapter extends ArrayAdapter<ProgramacionLuces> {
		private final Context context;
		private final ArrayList<ProgramacionLuces> values;

		public ProgramacionAdapter(Context context, ArrayList<ProgramacionLuces> values) {
			super(context, R.layout.evento_list_item, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public int getCount() {
			return programaciones.size();
		}

		@Override
		public ProgramacionLuces getItem(int position) {
			return programaciones.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflate(getApplicationContext(),
						R.layout.li_programacion, null);
				new DetalleLucesFragment.ProgramacionAdapter.ViewHolder(convertView);
			}
			final DetalleLucesFragment.ProgramacionAdapter.ViewHolder holder = (DetalleLucesFragment.ProgramacionAdapter.ViewHolder) convertView.getTag();
			final ProgramacionLuces item = getItem(position);
			holder.txtNombre.setText(item.getNombre());
			Typeface fontBold = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
			holder.txtNombre.setTypeface(fontBold);

//			Calendar mCalendar = new GregorianCalendar();
//			TimeZone mTimeZone = mCalendar.getTimeZone();
//			int mGMTOffset = mTimeZone.getRawOffset();
//
//			String[] hora = item.getHoraInicio().split(":");
//			Integer horaGMT = Integer.valueOf(hora[0]) + ((Long) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS)).intValue();
//			if(horaGMT >= 24){
//				horaGMT = horaGMT - 24;
//			}else if(horaGMT < 0){
//				horaGMT = horaGMT + 24;
//			}



//			if(horaGMT.toString().length() == 1){
//				hora[0] = "0" + horaGMT.toString();
//			}else{
//				hora[0] = horaGMT.toString();
//			}
//			if(hora[1].toString().length() == 1){
//				hora[1] = "0" + hora[1];
//			}


			holder.btnEliminarProgramacion.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					idBorrar = item.getId();
					EliminarProgramacionAsyncTask eliminarProgramacionAsyncTask = new EliminarProgramacionAsyncTask(DetalleLucesFragment.this, idBorrar);
					eliminarProgramacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

				}
			});


			holder.txtHora.setText(item.getHoraInicio());

			String duracion[] = item.getDuracion().split(":");
			if(duracion[0].length() == 1){
				duracion[0] = "0" + duracion[0] + "h";
			}else{
				duracion[0] = duracion[0] + "h";
			}
			if(duracion[1].toString().length() == 1){
				duracion[1] = "0" + duracion[1] + "m";
			}else{
				duracion[1] = duracion[1] + "m";
			}

			holder.txtDuracion.setText("duracion " + duracion[0] + ":" + duracion[1]);

			String dias = item.getDias();
			if(dias.substring(0,1).equals("1")){
				holder.txtDomingo.setTextColor(Color.parseColor("#1C94AB"));
			}else{
				holder.txtDomingo.setTextColor(Color.parseColor("#FFFFFF"));
			}
			if(dias.substring(1,2).equals("1")){
				holder.txtLunes.setTextColor(Color.parseColor("#1C94AB"));
			}else{
				holder.txtLunes.setTextColor(Color.parseColor("#FFFFFF"));
			}
			if(dias.substring(2,3).equals("1")){
				holder.txtMartes.setTextColor(Color.parseColor("#1C94AB"));
			}else{
				holder.txtMartes.setTextColor(Color.parseColor("#FFFFFF"));
			}
			if(dias.substring(3,4).equals("1")){
				holder.txtMiercoles.setTextColor(Color.parseColor("#1C94AB"));
			}else{
				holder.txtMiercoles.setTextColor(Color.parseColor("#FFFFFF"));
			}
			if(dias.substring(4,5).equals("1")){
				holder.txtJueves.setTextColor(Color.parseColor("#1C94AB"));
			}else{
				holder.txtJueves.setTextColor(Color.parseColor("#FFFFFF"));
			}
			if(dias.substring(5,6).equals("1")){
				holder.txtViernes.setTextColor(Color.parseColor("#1C94AB"));
			}else{
				holder.txtViernes.setTextColor(Color.parseColor("#FFFFFF"));
			}
			if(dias.substring(6,7).equals("1")){
				holder.txtSabado.setTextColor(Color.parseColor("#1C94AB"));
			}else{
				holder.txtSabado.setTextColor(Color.parseColor("#FFFFFF"));
			}
			return convertView;
		}


		class ViewHolder {
			TextView txtHora, txtDuracion, txtNombre, txtDomingo, txtLunes, txtMartes, txtMiercoles, txtJueves, txtViernes, txtSabado;
			FloatingActionButton btnEliminarProgramacion;


			public ViewHolder(View view) {
				txtHora = (TextView) view.findViewById(R.id.txtHora);
				txtDuracion = (TextView) view.findViewById(R.id.txtDuracion);
				txtNombre = (TextView) view.findViewById(R.id.txtNombre);
				txtDomingo = (TextView) view.findViewById(R.id.txtDomingo);
				txtLunes = (TextView) view.findViewById(R.id.txtLunes);
				txtMartes = (TextView) view.findViewById(R.id.txtMartes);
				txtMiercoles = (TextView) view.findViewById(R.id.txtMiercoles);
				txtJueves = (TextView) view.findViewById(R.id.txtJueves);
				txtViernes = (TextView) view.findViewById(R.id.txtViernes);
				txtSabado = (TextView) view.findViewById(R.id.txtSabado);
				btnEliminarProgramacion = (FloatingActionButton) view.findViewById(R.id.btnEliminarProgramacion);

				view.setTag(this);
			}
		}
	}


}