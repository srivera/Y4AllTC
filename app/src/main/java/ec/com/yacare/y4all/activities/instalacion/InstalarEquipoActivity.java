package ec.com.yacare.y4all.activities.instalacion;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.InicioActivity;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import me.drozdzynski.library.steppers.OnCancelAction;
import me.drozdzynski.library.steppers.OnFinishAction;
import me.drozdzynski.library.steppers.SteppersItem;
import me.drozdzynski.library.steppers.SteppersView;


public class InstalarEquipoActivity extends AppCompatActivity {


	private Toolbar toolbar;

	private Equipo equipo;

	public SteppersView steppersView;

	private DatosAplicacion datosAplicacion;

	public ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_instalar_equipo);

		if (isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		datosAplicacion = (DatosAplicacion) getApplicationContext();

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		SteppersView.Config steppersViewConfig = new SteppersView.Config();
		steppersViewConfig.setOnFinishAction(new OnFinishAction() {
			@Override
			public void onFinish() {
				InstalarEquipoActivity.this.startActivity(new Intent(InstalarEquipoActivity.this, InstalarEquipoActivity.class));
				InstalarEquipoActivity.this.finish();
			}
		});

		steppersViewConfig.setOnCancelAction(new OnCancelAction() {
			@Override
			public void onCancel() {
				InstalarEquipoActivity.this.startActivity(new Intent(InstalarEquipoActivity.this, InstalarEquipoActivity.class));
				InstalarEquipoActivity.this.finish();
			}
		});

		steppersViewConfig.setFragmentManager(getSupportFragmentManager());
		ArrayList<SteppersItem> steps = new ArrayList<>();

		if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("primerEquipo") && !getIntent().getExtras().getBoolean("primerEquipo")){
			SteppersItem item = new SteppersItem();
			item.setLabel("Configurar equipo");
			item.setPositiveButtonEnable(true);
			AgregarEquipoFragment agregarEquipoFragment = new AgregarEquipoFragment();
			item.setSubLabel("Conecte su celular a la red wifi del equipo");
			item.setFragment(agregarEquipoFragment);
			item.setTag("1");
			steps.add(item);
			steppersView = (SteppersView) findViewById(R.id.steppersView);
			steppersView.setConfig(steppersViewConfig);
			steppersView.setItems(steps);
			steppersView.build();


		}else {
			SteppersItem item = new SteppersItem();
			item.setLabel("Configurar equipo");
			item.setPositiveButtonEnable(true);
			AgregarEquipoFragment agregarEquipoFragment = new AgregarEquipoFragment();
			item.setSubLabel("Conecte su celular a la red wifi del equipo" );
			item.setFragment(agregarEquipoFragment);
			item.setTag("1");
			steps.add(item);

			CrearCuentaFragment crearCuentaFragment = new CrearCuentaFragment();
			SteppersItem item2 = new SteppersItem();
			item2.setLabel("Crear cuenta");
			item2.setPositiveButtonEnable(true);
			item2.setSubLabel("Ingrese sus datos" );
			item2.setFragment(crearCuentaFragment);
			item2.setTag("2");

			steps.add(item2);

			ValidarCuentaFragment crearCuentaFragment2 = new ValidarCuentaFragment();
			SteppersItem item3 = new SteppersItem();
			item3.setLabel("Confirmar su cuenta");
			item3.setPositiveButtonEnable(true);
			item3.setSubLabel("Ingrese los datos recibidos en su correo" );
			item3.setFragment(crearCuentaFragment2);
			item3.setTag("3");
			steps.add(item3);

			steppersView = (SteppersView) findViewById(R.id.steppersView);
			steppersView.setConfig(steppersViewConfig);
			steppersView.setItems(steps);
			steppersView.build();

			if (datosAplicacion.getEquipoSeleccionado() != null && datosAplicacion.getEquipoSeleccionado().getEstadoEquipo().equals(EstadoDispositivoEnum.CONFIGURACION.getCodigo()) && datosAplicacion.getCuenta() == null) {
				steppersView.steppersAdapter.currentStep = 1;
			} else if (datosAplicacion.getCuenta() != null) {
				steppersView.steppersAdapter.currentStep = 2;
			}
		}


	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(datosAplicacion.getEquipoSeleccionado() == null){
			Intent i = new Intent(InstalarEquipoActivity.this, InicioActivity.class);
			startActivity(i);
		}
	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

}
