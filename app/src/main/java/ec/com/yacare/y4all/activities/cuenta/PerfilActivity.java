package ec.com.yacare.y4all.activities.cuenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.asynctask.ws.CambiarClaveAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.GuardarDispositivoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.GuardarFotoDispositivoAsyncTask;
import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.CuentaDataSource;


public class PerfilActivity extends Fragment {

	public PerfilActivity() {
	}

	private TextView textoNombre;
	private TextView textoEmail;
	private TextView titulo_informacion_usuario, titulo_contrasena, texto_password;

	private EditText editActual, editClave, editClave2, editNombre;
	private DatosAplicacion datosAplicacion;

	private Cuenta cuenta;
	private String nuevaClave;

	private ProgressDialog progressDialog;

	private Typeface fontRegular;

	private ImageButton btnProfileDispositivo;

	private ImageView fotoPerfilDispositivo;

	private String userChoosenTask;
	private int REQUEST_CAMERA = 0, SELECT_FILE = 1, PIC_CROP = 2;

	private String nombreDispositivoAnterior;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.ac_perfil, container, false);
		fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
		datosAplicacion  = (DatosAplicacion) getActivity().getApplicationContext();
		cuenta = datosAplicacion.getCuenta();

		fotoPerfilDispositivo = (ImageView) view.findViewById(R.id.fotoPerfilDispositivo);
		Bitmap bitmap ;
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + cuenta.getId()+".jpg");
		if(file.exists()){
			Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + cuenta.getId() + ".jpg");
			if(bmImg != null){

				File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + cuenta.getId() +".jpg");
				if(foto.exists()){
					bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + cuenta.getId() +".jpg");
					if(bmImg != null){
						mostrarImagen(fotoPerfilDispositivo, bmImg);

					}
				}
			}
		}else {
			bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.usuarioperfil)).getBitmap();
			mostrarImagen(fotoPerfilDispositivo, bitmap);
		}

		btnProfileDispositivo = (ImageButton) view.findViewById(R.id.btnProfileDispositivo);
		btnProfileDispositivo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = { "Tomar Foto", "Galeria",
						"Cancelar" };
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
		textoNombre = (TextView) view.findViewById(R.id.texto_nombre);
		textoNombre.setTypeface(fontRegular);

		textoNombre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				TableLayout ll = new TableLayout(getActivity());
				final TextView txtTitulo = new TextView(getActivity());
				LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				txtTitulo.setLayoutParams(lp0);
				txtTitulo.setText(YACSmartProperties.intance.getMessageForKey("actualizar.usuario"));

				txtTitulo.setTypeface(fontRegular);
				txtTitulo.setTextSize(16F);
				txtTitulo.setPadding(5, 20, 5, 20);
				txtTitulo.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
				ll.addView(txtTitulo, new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT,
						0));

				View linea = new View(getActivity());
				linea.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 5));
				linea.setBackgroundColor(Color.rgb(51, 51, 51));
				ll.addView(linea);

				editNombre = new EditText(getActivity());
				LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				editNombre.setLayoutParams(lp1);
				editNombre.setInputType(InputType.TYPE_CLASS_TEXT);
				editNombre.setHint(YACSmartProperties.intance.getMessageForKey("nombre.dispositivo.vacio"));
				editNombre.setTypeface(fontRegular);
				editNombre.setTextSize(14F);
				editNombre.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
				ll.addView(editNombre, new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT,
						0));
				final AlertDialog d1 = new AlertDialog.Builder(getActivity())
						.setView(ll)
						.setPositiveButton("Actualizar",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
										if (!editNombre.getText().toString().isEmpty()  && !editNombre.equals(textoNombre.getText().toString())) {
											SharedPreferences.Editor editor = sharedPrefs.edit();
											editor.putString("prefNombreDispositivo", editNombre.getText().toString());
											editor.apply();
											editor.commit();
											GuardarDispositivoAsyncTask guardarDispositivoAsyncTask = new GuardarDispositivoAsyncTask(getActivity().getApplicationContext(),
													null, null, PerfilActivity.this);
											guardarDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
										}else {
											new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
													.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
													.setContentText(YACSmartProperties.intance.getMessageForKey("error.actualizar.usuario"))
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
								})
						.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).create();
				d1.show();
			}
		});

		textoEmail = (TextView) view.findViewById(R.id.texto_email);
		textoEmail.setTypeface(fontRegular);
		textoEmail.setText(datosAplicacion.getCuenta().getEmail());

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		textoNombre.setText(sharedPrefs.getString("prefNombreDispositivo", "").toString());
		nombreDispositivoAnterior = sharedPrefs.getString("prefNombreDispositivo", "").toString();

		titulo_informacion_usuario = (TextView) view.findViewById(R.id.titulo_informacion_usuario);
		titulo_contrasena = (TextView) view.findViewById(R.id.titulo_contrasena);
		texto_password = (TextView) view.findViewById(R.id.texto_password);
		titulo_informacion_usuario.setTypeface(fontRegular);
		titulo_contrasena.setTypeface(fontRegular);
		texto_password.setTypeface(fontRegular);

		texto_password.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TableLayout ll = new TableLayout(getActivity());

				final TextView txtTitulo = new TextView(getActivity());
				LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				txtTitulo.setLayoutParams(lp0);
				txtTitulo.setText(YACSmartProperties.intance.getMessageForKey("cambio.clave"));

				txtTitulo.setTypeface(fontRegular);
				txtTitulo.setTextSize(16F);
				txtTitulo.setPadding(5, 20, 5, 20);
				txtTitulo.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
				ll.addView(txtTitulo, new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT,
						0));

				View linea = new View(getActivity());
				linea.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 5));
				linea.setBackgroundColor(Color.rgb(51, 51, 51));
				ll.addView(linea);

				editActual = new EditText(getActivity());
				LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				editActual.setLayoutParams(lp1);
				editActual.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				editActual.setHint(YACSmartProperties.intance.getMessageForKey("ingrese.clave.actual"));
				editActual.setTypeface(fontRegular);
				editActual.setTextSize(14F);
				editActual.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
				ll.addView(editActual, new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT,
						0));

				editClave = new EditText(getActivity());
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				editClave.setLayoutParams(lp2);
				editClave.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				editClave.setHint(YACSmartProperties.intance.getMessageForKey("ingrese.clave.nueva"));
				editClave.setTypeface(fontRegular);
				editClave.setTextSize(14F);
				editClave.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
				ll.addView(editClave, new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT,
						0));

				editClave2 = new EditText(getActivity());
				LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				editClave2.setLayoutParams(lp3);
				editClave2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				editClave2.setHint(YACSmartProperties.intance.getMessageForKey("ingrese.clave.repita"));
				editClave2.setTypeface(fontRegular);
				editClave2.setTextSize(14F);
				editClave2.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
				ll.addView(editClave2, new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT,
						0));

				final AlertDialog d1 = new AlertDialog.Builder(getActivity())
						.setView(ll)
						.setPositiveButton("Actualizar",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										boolean grabar = true;
										if (editActual.getText().toString().equals(datosAplicacion.getCuenta().getClave())) {
											if (editClave.getText().toString().isEmpty()) {
												grabar = false;
											}
											if (editClave2.getText().toString().isEmpty()) {
												grabar = false;
											}

											if (!editClave2.getText().toString().equals(editClave.getText().toString())) {
												grabar = false;
											}
											if (grabar) {
												nuevaClave = editClave2.getText().toString();
												progressDialog = ProgressDialog.show(getActivity(), "Actualizando clave", "Por favor espere...", true);
												CambiarClaveAsyncTask cambiarClaveAsyncTask = new CambiarClaveAsyncTask(PerfilActivity.this, editClave2.getText().toString());
												cambiarClaveAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
											} else {
												//Error claves nuevas vacias o diferentes
												new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
														.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
														.setContentText(YACSmartProperties.intance.getMessageForKey("error.cambio.clave"))
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
										} else {
											//Clave actual incorrecta
											new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
													.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
													.setContentText(YACSmartProperties.intance.getMessageForKey("error.clave"))
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
								})
						.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).create();

				d1.show();
			}
		});

		return view;
	}

	public void verificarGuardarDispositivo(String respuesta) {
		if(!respuesta.equals(YACSmartProperties.getInstance().getMessageForKey("error.general"))){
			String resultToken = null;
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
					respuestaJSON = new JSONObject(respuesta);
					resultToken = respuestaJSON.getString("resultado");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(resultToken != null && resultToken.equals("OK")) {
					new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
							.setContentText(YACSmartProperties.intance.getMessageForKey("exito.actualizar"))
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
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putString("prefNombreDispositivo", nombreDispositivoAnterior);
				editor.apply();
				editor.commit();
				textoNombre.setText(nombreDispositivoAnterior);
				new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
						.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setContentText(YACSmartProperties.intance.getMessageForKey("sin.conexion"))
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
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.putString("prefNombreDispositivo", nombreDispositivoAnterior);
			editor.apply();
			editor.commit();
			textoNombre.setText(nombreDispositivoAnterior);
			//Error general
			new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
					.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setContentText(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
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



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE) {
				onSelectFromGalleryResult(data);
			}else if (requestCode == REQUEST_CAMERA) {
				onCaptureImageResult(data);
			}else{
				//get the returned data
				Bundle extras = data.getExtras();
				//get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");

				FileOutputStream fileOuputStream = null;
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					thePic.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					byte[] bitmapdata = bos.toByteArray();
					fileOuputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + cuenta.getId() + ".jpg");
					fileOuputStream.write(bitmapdata);
					fileOuputStream.close();


					GuardarFotoDispositivoAsyncTask guardarFotoDispositivoAsyncTask = new GuardarFotoDispositivoAsyncTask(PerfilActivity.this, bitmapdata);
					guardarFotoDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

	private void onSelectFromGalleryResult(Intent data) {
		Uri picUri = data.getData();
		performCrop(picUri);
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


	public Cuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}

	public void verificarEstadoCrearCuenta(String respuesta) {
		progressDialog.dismiss();
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
					respuestaJSON = new JSONObject(respuesta);
					resultCuenta = respuestaJSON.getString("resultado");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if(resultCuenta != null && resultCuenta.equals(YACSmartProperties.getInstance().getMessageForKey("ok.general"))) {
					CuentaDataSource datasource = new CuentaDataSource(getActivity().getApplicationContext());
					datasource.open();
					datosAplicacion.getCuenta().setClave(nuevaClave);
					datasource.updateCuenta(datosAplicacion.getCuenta());
					datasource.close();
					new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.informacion"))
							.setContentText(YACSmartProperties.intance.getMessageForKey("exito.actualizar"))
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
				new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
						.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
						.setContentText(YACSmartProperties.intance.getMessageForKey("error.clave"))
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
			//Error general
			new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
					.setTitleText(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setContentText(YACSmartProperties.intance.getMessageForKey("verificar.internet"))
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
}