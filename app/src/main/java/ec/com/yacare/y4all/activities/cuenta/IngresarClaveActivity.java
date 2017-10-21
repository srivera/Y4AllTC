package ec.com.yacare.y4all.activities.cuenta;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.lib.util.AudioQueu;


public class IngresarClaveActivity extends AppCompatActivity {

	private Button btnUno, btnDos, btnTres, btnCuatro, btnCinco, btnSeis, btnSiete, btnOcho, btnNueve, btnCero, btnBorrar, btnOk;

	private EditText editPassword;

	private TextView txtTituloPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ingresar_clave_cuenta);
		if (isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		btnUno = (Button) findViewById(R.id.btnUno);
		btnDos = (Button) findViewById(R.id.btnDos);
		btnTres = (Button) findViewById(R.id.btnTres);
		btnCuatro = (Button) findViewById(R.id.btnCuatro);
		btnCinco = (Button) findViewById(R.id.btnCinco);
		btnSeis = (Button) findViewById(R.id.btnSeis);
		btnSiete = (Button) findViewById(R.id.btnSiete);
		btnOcho = (Button) findViewById(R.id.btnOcho);
		btnNueve = (Button) findViewById(R.id.btnNueve);
		btnCero = (Button) findViewById(R.id.btnCero);
		btnBorrar = (Button) findViewById(R.id.btnBorrar);
		btnOk = (Button) findViewById(R.id.btnOk);

		editPassword = (EditText) findViewById(R.id.editPassword);
		txtTituloPassword = (TextView) findViewById(R.id.txtTituloPassword);

		btnUno.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "1");
				}
			}
		});

		btnDos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "2");
				}
			}
		});
		btnTres.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "3");
				}
			}
		});
		btnCuatro.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "4");
				}
			}
		});
		btnCinco.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "5");
				}
			}
		});
		btnSeis.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "6");
				}
			}
		});
		btnSiete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "7");
				}
			}
		});
		btnOcho.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "8");
				}
			}
		});
		btnNueve.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "9");
				}
			}
		});
		btnCero.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() < 4) {
					editPassword.setText(editPassword.getText().toString() + "0");
				}
			}
		});
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Validar
				DatosAplicacion datosAplicacion = (DatosAplicacion) getApplicationContext();
				if (editPassword.getText().toString().equals(datosAplicacion.getCuenta().getClave())) {
					Intent i = new Intent(IngresarClaveActivity.this, CuentaActivity.class);
					startActivity(i);
				}

			}
		});
		btnBorrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = editPassword.getText().toString();
				if(password.length() == 4) {
					editPassword.setText(editPassword.getText().toString().substring(0,3));
				}else if(password.length() == 3) {
					editPassword.setText(editPassword.getText().toString().substring(0,2));
				}else if(password.length() == 2) {
					editPassword.setText(editPassword.getText().toString().substring(0,1));
				}else if(password.length() == 1) {
					editPassword.setText("");
				}
			}
		});

	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutClave);
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
			params.leftMargin = 400;
			params.rightMargin = 400;
//			LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
//			paramsL.setMargins(200, 10, 200, 10);
//			linearLayout.setLayoutParams(paramsL);

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutClave);
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
			params.leftMargin = 75;
			params.rightMargin = 75;
//			LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
//			paramsL.setMargins(75, 10, 75, 10);
//			linearLayout.setLayoutParams(paramsL);

		}
	}
}