package ec.com.yacare.y4all.activities.instalacion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.SplashActivity;
import ec.com.yacare.y4all.asynctask.ws.ActivarCuentaClienteAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.CrearCuentaClienteAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.FinalizarInstalacionEquipoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.GuardarDispositivoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.LoginCuentaInternoAsyncTask;
import ec.com.yacare.y4all.lib.asynctask.dispositivo.ComandoConfigurarScheduledTask;
import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.CuentaDataSource;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;

public class ValidarCuentaFragment extends Fragment {

	private Button validarCuenta, btnReintentar;

	private EditText numeroConfirmacion;
	
	private Cuenta cuenta ;

	private String numeroConfirmacionText;
	private DatosAplicacion datosAplicacion;
	private Equipo equipoSeleccionado;
	private TextView txtLeyendaMail;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_validar_registro, container, false);

		datosAplicacion = (DatosAplicacion) getActivity().getApplicationContext();
		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();

		numeroConfirmacion  = (EditText) view.findViewById(R.id.editNumeroConfirmacion);
		Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
		numeroConfirmacion.setTypeface(fontRegular);

		setCuenta(datosAplicacion.getCuenta());

		txtLeyendaMail = (TextView) view.findViewById(R.id.txtLeyendaMail);
		txtLeyendaMail.setText("Se ha enviado un correo a la cuenta " + cuenta.getEmail() + " Verifique...");
		btnReintentar = (Button) view.findViewById(R.id.btnReintentar);
		btnReintentar.setTypeface(fontRegular);
		btnReintentar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert.setTitle("Verifique que el correo sea correcto");
				final EditText input = new EditText(getActivity());
				input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				input.setText(cuenta.getEmail());
				alert.setView(input);
				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if(!input.getText().toString().equals("")) {
							String cuentaAnterior = cuenta.getEmail();
							cuenta.setEmail(input.getText().toString());
							CuentaDataSource cuentaDataSource = new CuentaDataSource(getActivity().getApplicationContext());
							cuentaDataSource.open();
							cuentaDataSource.updateCuenta(cuenta);
							cuentaDataSource.close();
							CrearCuentaClienteAsyncTask crearCuentaAsyncTask = new CrearCuentaClienteAsyncTask(null, ValidarCuentaFragment.this, cuentaAnterior);
							crearCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
					}
				});
				alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Put actions for CANCEL button here, or leave in blank
					}
				});
				alert.show();
			}
		});


		validarCuenta = (Button) view.findViewById(R.id.btnValidarCuenta);
		validarCuenta.setTypeface(fontRegular);
		validarCuenta.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!numeroConfirmacion.getText().toString().equals("")) {
					((InstalarEquipoActivity)getActivity()).progress = ProgressDialog.show(getActivity(), "Verificando su cuenta",
							"Espere un momento", true);
					((InstalarEquipoActivity)getActivity()).progress.show();
					numeroConfirmacionText = numeroConfirmacion.getText().toString();
					setCuenta(datosAplicacion.getCuenta());
					ActivarCuentaClienteAsyncTask confirmarCuentaAsyncTask = new ActivarCuentaClienteAsyncTask(ValidarCuentaFragment.this);
					confirmarCuentaAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					
				}else{
					numeroConfirmacion.setError(YACSmartProperties.intance.getMessageForKey("numero.confirmacion.vacio"));
				}
			}
		});

		return view;
	}

	
	public void verificarConfirmacionCuenta(String respuesta) {

		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultCuenta = null;
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
					JSONObject respuestaJSON1 = new JSONObject(respuesta);
					resultCuenta = respuestaJSON1.getString("resultado");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if(resultCuenta != null && resultCuenta.equals(YACSmartProperties.getInstance().getMessageForKey("ok.general"))){
				//Se guarda el dispositivo
					GuardarDispositivoAsyncTask guardarDispositivoAsyncTask = new GuardarDispositivoAsyncTask(getActivity().getApplicationContext(), ValidarCuentaFragment.this, null, null);
					guardarDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					// Get System TELEPHONY service reference
					TelephonyManager tManager = (TelephonyManager) getActivity().getBaseContext()
							.getSystemService(Context.TELEPHONY_SERVICE);

					// Get carrier name (Network Operator Name
					String carrierName = tManager.getNetworkOperatorName();

					// Get Phone model and manufacturer name
					String manufacturer = Build.MANUFACTURER;
					String model = Build.MODEL;

					DatosAplicacion datosAplicacion = (DatosAplicacion) getActivity().getApplicationContext();

					EquipoDataSource equipoDataSource = new EquipoDataSource(getActivity().getApplicationContext());
					equipoDataSource.open();
					Equipo equipoBusqueda = new Equipo();
					equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.CONFIGURACION.getCodigo());
					ArrayList<Equipo> equipos = equipoDataSource.getEquipoEstado(equipoBusqueda);
					equipoDataSource.close();

					if(equipos != null  && equipos.size() > 0){
						Equipo equipo = equipos.get(0);
						if(equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())){

							FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(ValidarCuentaFragment.this, equipo, null);
							instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

							SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
							String nombre =	sharedPrefs.getString("prefNombreDispositivo", "");

							String comando =  YACSmartProperties.COM_CREAR_CUENTA
									+ ";" + datosAplicacion.getEquipoSeleccionado().getNumeroSerie()
									+ ";" + cuenta.getId()
									+ ";" + cuenta.getEmail()
									+ ";" + cuenta.getClave()
									+ ";" + cuenta.getFechaCuenta()
									+ ";" + Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)
									+ ";" + nombre
									+ ";" + "ANDROID";

							ComandoConfigurarScheduledTask genericoAsyncTask = new ComandoConfigurarScheduledTask(comando, ValidarCuentaFragment.this, null);
							genericoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}else{
							FinalizarInstalacionEquipoAsyncTask instalacionAsyncTask = new FinalizarInstalacionEquipoAsyncTask(ValidarCuentaFragment.this, equipo, null);
							instalacionAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

						}
					}

				}
			}else if(respuesta.contains("SAG08")){
				((InstalarEquipoActivity)getActivity()).progress.dismiss();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setMessage(YACSmartProperties.intance.getMessageForKey("codigo.incorrecto"))
						.setCancelable(false)
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}

		}else{
			//Error general
			((InstalarEquipoActivity)getActivity()).progress.dismiss();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
			.setMessage(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
			.setCancelable(false)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}

	public void verificarGuardarDispositivo(String respuesta) {
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultToken = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				resultToken = respuestaJSON.getString("token");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(resultToken != null ){
				LoginCuentaInternoAsyncTask loginCuentaInternoAsyncTask = new LoginCuentaInternoAsyncTask(null, ValidarCuentaFragment.this, cuenta.getEmail(), cuenta.getClave());
				loginCuentaInternoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}else{
//				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//						getActivity());
//				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
//						.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
//						.setCancelable(false)
//						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,int id) {
//							}
//						});
//
//				AlertDialog alertDialog = alertDialogBuilder.create();
//				alertDialog.show();
			}
		}else{
			//Error general
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}

	public void reportarError(String respuesta) {
		validarCuenta.setEnabled(true);
		((InstalarEquipoActivity)getActivity()).progress.dismiss();
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
				.setMessage(YACSmartProperties.intance.getMessageForKey("error.finalizar"))
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	public void verificarLoginInterno(String respuesta) {
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultToken = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				resultToken = respuestaJSON.getString("token");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(resultToken != null ){
				//Actualizar el token en los datos aplicacion
				DatosAplicacion datosAplicacion = (DatosAplicacion) getActivity().getApplicationContext();
				datosAplicacion.setToken(resultToken);
			}else{
//				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//						getActivity());
//				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
//				.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
//				.setCancelable(false)
//				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,int id) {
//					}
//				});
//
//				AlertDialog alertDialog = alertDialogBuilder.create();
//				alertDialog.show();
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

	public void verificarResultadoInstalacion(String respuesta) {
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultCuenta = null;
			try {
				JSONObject respuestaJSON = new JSONObject(respuesta);
				resultCuenta = respuestaJSON.getString("resultado");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(resultCuenta != null && resultCuenta.equals(YACSmartProperties.getInstance().getMessageForKey("ok.general"))){
				((InstalarEquipoActivity)getActivity()).progress.dismiss();
				DatosAplicacion datosAplicacion = (DatosAplicacion) getActivity().getApplicationContext();
				Equipo equipo = datosAplicacion.getEquipoSeleccionado();
				equipo.setEstadoEquipo(EstadoDispositivoEnum.INSTALADO.getCodigo());
				EquipoDataSource dataSource = new EquipoDataSource(getActivity().getApplicationContext());
				dataSource.open();
				dataSource.updateEquipo(equipo);
				dataSource.close();

				//Actualizar la cuenta a estado ACT
				CuentaDataSource datasource = new CuentaDataSource(getActivity().getApplicationContext());
				datasource.open();
				Cuenta  cuentaBusqueda = new Cuenta();
				cuentaBusqueda.setId(cuenta.getId());
				cuenta = datasource.getCuentaId(cuentaBusqueda);
				cuenta.setEstadoCuenta(YACSmartProperties.getInstance().getMessageForKey("cuenta.estado.activo"));
				datasource.updateCuenta(cuenta);
				datasource.close();


				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putString("prefEquipo", equipo.getNumeroSerie());
				editor.apply();
				editor.commit();


				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());

				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
				.setMessage(YACSmartProperties.intance.getMessageForKey("instalacion.terminada"))
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
//						if(equipoSeleccionado.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
//							Intent i = new Intent(getActivity(), Y4HomeActivity.class);
//							startActivity(i);
//						}else{
//							Intent i = new Intent(getActivity(), LucesPrincipalActivity.class);
//							startActivity(i);
//						}

						getActivity().finish();
						Intent i = new Intent(getActivity(), SplashActivity.class);
						startActivity(i);
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
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}


	}
//
//	@Override
//	public void verificarResultadoConfiguracion(String respuesta) {
//		progress.dismiss();
//		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
//			String resultCuenta = null;
//			try {
//				JSONObject respuestaJSON = new JSONObject(respuesta);
//				resultCuenta = respuestaJSON.getString("resultado");
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			if(resultCuenta != null && resultCuenta.equals(YACSmartProperties.getInstance().getMessageForKey("ok.general"))){
//				String mensaje = YACSmartProperties.intance.getMessageForKey("instalacion.terminada");
//
//
//				EquipoDataSource dataSource = new EquipoDataSource(getApplicationContext());
//				dataSource.open();
//				Equipo equipoActualizar = dataSource.getEquipoNumSerie(equipoSeleccionado);
//				equipoActualizar.setEstadoEquipo(EstadoDispositivoEnum.INSTALADO.getCodigo());
//				dataSource.updateEquipo(equipoActualizar);
//				dataSource.close();
//				datosAplicacion.setEquipoSeleccionado(equipoActualizar);
//
//				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//				SharedPreferences.Editor editor = sharedPrefs.edit();
//				editor.putString("prefEquipo", equipoActualizar.getNumeroSerie());
//				editor.apply();
//				editor.commit();
//
//				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//						CrearCuentaFragment.this);
//
//				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
//						.setMessage(mensaje)
//						.setCancelable(false)
//						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,int id) {
//								if(equipoSeleccionado.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
//									Intent i = new Intent(CrearCuentaFragment.this, Y4HomeActivity.class);
//									CrearCuentaFragment.this.startActivity(i);
//								}else{
//									Intent i = new Intent(CrearCuentaFragment.this, AdministrarRoutersActivity.class);
//									CrearCuentaFragment.this.startActivity(i);
//								}
//								}
//						});
//				AlertDialog alertDialog = alertDialogBuilder.create();
//				alertDialog.show();
//			}else{
//				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//						CrearCuentaFragment.this);
//				alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
//						.setMessage(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
//						.setCancelable(false)
//						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//							}
//						});
//
//				AlertDialog alertDialog = alertDialogBuilder.create();
//				alertDialog.show();
//			}
//		}else{
//			//Error general
//			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//					CrearCuentaFragment.this);
//			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
//					.setMessage(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
//					.setCancelable(false)
//					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//						}
//					});
//
//			AlertDialog alertDialog = alertDialogBuilder.create();
//			alertDialog.show();
//		}
		
//	}

	public String getNumeroConfirmacionText() {
		return numeroConfirmacionText;
	}

	public void setNumeroConfirmacionText(String numeroConfirmacionText) {
		this.numeroConfirmacionText = numeroConfirmacionText;
	}

	public Equipo getEquipoSeleccionado() {
		return equipoSeleccionado;
	}

	public void setEquipoSeleccionado(Equipo equipoSeleccionado) {
		this.equipoSeleccionado = equipoSeleccionado;
	}
}
