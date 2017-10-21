package ec.com.yacare.y4all.activities.instalacion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.asynctask.ws.CrearCuentaClienteAsyncTask;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.CuentaDataSource;

public class CrearCuentaFragment extends Fragment {

	private Button crearCuenta;
	
	private EditText nombreDispositivo;
	private EditText email;
	private EditText clave;
	private EditText repetirClave;
	
	private Cuenta cuenta ;
	private DatosAplicacion datosAplicacion;
	private Equipo equipoSeleccionado;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_registro, container, false);

		datosAplicacion = (DatosAplicacion) getActivity().getApplicationContext();
		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();

		email  = (EditText) view.findViewById(R.id.editEmail);
		clave  = (EditText) view.findViewById(R.id.editClave);
		repetirClave  = (EditText) view.findViewById(R.id.editRepetirClave);
		nombreDispositivo = (EditText) view.findViewById(R.id.editNombreDispositivoReg);
		Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
		email.setTypeface(fontRegular);
		clave.setTypeface(fontRegular);
		repetirClave.setTypeface(fontRegular);
		nombreDispositivo.setTypeface(fontRegular);
		
		crearCuenta = (Button) view.findViewById(R.id.btnCrearCuenta);
		crearCuenta.setTypeface(fontRegular);
		crearCuenta.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Validar campos
				boolean grabar = true;
				if (email.getText().toString().isEmpty()) {
					email.setError(YACSmartProperties.intance.getMessageForKey("email.vacio"));
					grabar = false;
				}
				if (clave.getText().toString().isEmpty()) {
					clave.setError(YACSmartProperties.intance.getMessageForKey("clave.vacio"));
					grabar = false;
				}
				if (repetirClave.getText().toString().isEmpty()) {
					repetirClave.setError(YACSmartProperties.intance.getMessageForKey("repite.clave.vacio"));
					grabar = false;
				}

				if (!repetirClave.getText().toString().equals(clave.getText().toString())) {
					clave.setError(YACSmartProperties.intance.getMessageForKey("clave.diferente"));
					grabar = false;
				}
				if (email.getText().toString() != null && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
					email.setError(YACSmartProperties.intance.getMessageForKey("formato.email.invalido"));
					grabar = false;
				}
				if (nombreDispositivo.getText().toString().isEmpty()) {
					nombreDispositivo.setError(YACSmartProperties.intance.getMessageForKey("nombre.dispositivo.vacio"));
					grabar = false;
				}

				//Guardar en BD local y llamar al ws
				if(grabar){
					crearCuenta.setEnabled(false);
					((InstalarEquipoActivity)getActivity()).progress = ProgressDialog.show(getActivity(), "Creando su cuenta",
							"Espere un momento", true);

					((InstalarEquipoActivity)getActivity()).progress.show();
					//nuevo
					//Guardar en el Shared el nombre del dispositivo
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putString("prefNombreDispositivo", nombreDispositivo.getText().toString());
					editor.apply();
					editor.commit();

					Date date = new Date();
					DatosAplicacion datosAplicacion = ((DatosAplicacion)  getActivity().getApplicationContext());

					cuenta = new Cuenta();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					cuenta.setEmail(email.getText().toString());
					cuenta.setClave(clave.getText().toString());
					cuenta.setEstadoCuenta(YACSmartProperties.getInstance().getMessageForKey("cuenta.estado.temporal"));
					cuenta.setFechaCuenta(dateFormat.format(date));
					cuenta.setIdMensajePush(datosAplicacion.getRegId());
					CrearCuentaClienteAsyncTask crearCuentaAsyncTask = new CrearCuentaClienteAsyncTask(CrearCuentaFragment.this, null, "");
					crearCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		});

		return view;
	}

	public void verificarEstadoCrearCuenta(String respuesta) {
		((InstalarEquipoActivity)getActivity()).progress.dismiss();
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultCuenta = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				resultCuenta = respuestaJSON.getString("resultado");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(resultCuenta != null){
				CuentaDataSource datasource = new CuentaDataSource(getActivity().getApplicationContext());
				datasource.open();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				DatosAplicacion datosAplicacion = ((DatosAplicacion)  getActivity().getApplicationContext());

				cuenta = new Cuenta();
				cuenta.setId(resultCuenta);
				cuenta.setEmail(email.getText().toString());
				cuenta.setClave(clave.getText().toString());
				cuenta.setEstadoCuenta(YACSmartProperties.getInstance().getMessageForKey("cuenta.estado.pendiente"));
				cuenta.setFechaCuenta(dateFormat.format(date));
				cuenta.setIdMensajePush(datosAplicacion.getRegId());
				cuenta = datasource.createCuenta(cuenta);
				datasource.close();

				( (DatosAplicacion) getActivity().getApplicationContext()).setCuenta(cuenta);
				
				//En la pantalla desahilitar campos y que ingrese el numero de confirmacion
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
				.setMessage(YACSmartProperties.intance.getMessageForKey("revise.numero.confirmacion"))
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						((InstalarEquipoActivity)getActivity()).steppersView.steppersAdapter.nextStep();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}else{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
				.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						crearCuenta.setEnabled(true);
					}
				});
	
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}else{
			//Error general
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
			.setMessage(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
			.setCancelable(false)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					crearCuenta.setEnabled(true);
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}

	public Cuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}
}
