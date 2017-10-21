package ec.com.yacare.y4all.activities.cuenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.asynctask.ws.GuardarFotoEquipoAsyncTask;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;


public class ListaEquiposActivity extends Fragment {

	private EquipoDataSource datasource;
	private ArrayList<Equipo> mAppList;
	private EquipoAdapter mAdapter;
	public ListView mListView;

	private DatosAplicacion datosAplicacion;
	private String userChoosenTask;
	private int REQUEST_CAMERA = 0, SELECT_FILE = 1, PIC_CROP = 2;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.ac_lista_equipo, container, false);

		datosAplicacion = (DatosAplicacion) getActivity().getApplicationContext();
		datasource = new EquipoDataSource(getActivity().getApplicationContext());
		datasource.open();

		mAppList = datasource.getAllEquipo();
		datasource.close();

		mListView = (ListView) view.findViewById(R.id.listView);

		mAdapter = new EquipoAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

		return view;
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

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Activity.RESULT_OK) {
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
						GuardarFotoEquipoAsyncTask guardarFotoEquipoAsyncTask = new GuardarFotoEquipoAsyncTask(getActivity(), bitmapdata, datosAplicacion.getEquipoSeleccionado().getNumeroSerie());
						guardarFotoEquipoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				mAdapter.notifyDataSetChanged();

			}
		}
	}

	private void onSelectFromGalleryResult(Intent data) {
		Uri picUri = data.getData();
		performCrop(picUri);

	}

	public void actualizarLista(){
		datasource = new EquipoDataSource(getActivity().getApplicationContext());
		datasource.open();

		mAppList = datasource.getAllEquipo();
		datasource.close();
		mAdapter = new EquipoAdapter();
		mListView.setAdapter(mAdapter);
	}


	class EquipoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public Equipo getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getActivity().getApplicationContext(),
						R.layout.li_equipo, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			Equipo equipo = getItem(position);



			File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + equipo.getNumeroSerie()+".jpg");
			if(foto.exists()){
				Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + equipo.getNumeroSerie()+".jpg");
				if(bmImg != null){
					mostrarImagen(holder.iv_icon, bmImg);

				}

			}else {
				Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.logo2)).getBitmap();
				mostrarImagen(holder.iv_icon, bitmap);
			}
			Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
			holder.nombreEquipo.setText(equipo.getNombreEquipo());
			holder.nombreEquipo.setTypeface(fontRegular);

			holder.txtIpLocal.setText("Ip local: " + equipo.getIpLocal());
			holder.txtIpLocal.setTypeface(fontRegular);

			holder.btnConfigurarEquipo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent i = new Intent(getActivity(), ConfigPorteroActivity.class);
//					startActivityForResult(i, PrincipalMenuActivity.RESULT_ACTUALIZAR_LISTA_EQUIPO);

				}
			});
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView nombreEquipo;
			TextView txtIpLocal;
			ImageButton btnConfigurarEquipo;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.ivFoto);
				nombreEquipo = (TextView) view.findViewById(R.id.txtNombreEquipo);
				txtIpLocal = (TextView) view.findViewById(R.id.txtIpLocal);
				btnConfigurarEquipo = (ImageButton) view.findViewById(R.id.btnConfigurarEquipo);
				view.setTag(this);

			}
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
}