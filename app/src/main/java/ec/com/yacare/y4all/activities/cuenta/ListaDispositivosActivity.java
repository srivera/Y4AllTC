package ec.com.yacare.y4all.activities.cuenta;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.asynctask.ws.DesactivarDispositivoAsyncTask;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.DispositivoDataSource;


public class ListaDispositivosActivity extends Fragment {

	private DispositivoDataSource datasource;
	private ArrayList<Dispositivo> mAppList;
	private DispositivoAdapter mAdapter;
	public ListView mListView;
	private String idDispositivoBorrar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.ac_lista_dispositivo, container, false);

		datasource = new DispositivoDataSource(getActivity().getApplicationContext());
		datasource.open();

		mAppList = datasource.getAllDispositivo();
		datasource.close();

		mListView = (ListView) view.findViewById(R.id.listView);

		mAdapter = new DispositivoAdapter();
		mListView.setAdapter(mAdapter);

		return view;
	}


	public void actualizarLista(String respuesta){
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
					resultToken = respuestaJSON.getString("resultado");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(resultToken != null && resultToken.equals("OK")) {
					datasource = new DispositivoDataSource(getActivity().getApplicationContext());
					datasource.open();
					datasource.deleteDispositivo(idDispositivoBorrar);
					mAppList = datasource.getAllDispositivo();
					datasource.close();
					mAdapter = new DispositivoAdapter();
					mListView.setAdapter(mAdapter);
					new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText(YACSmartProperties.intance.getMessageForKey("desactivar.dispositivo"))
							.setContentText(YACSmartProperties.intance.getMessageForKey("desactivar.dispositivo.exito"))
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
									//Llamar a ws de desactivar dispositivo

								}
							})
							.show();
				}else{
					//Error
					new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
							.setTitleText(YACSmartProperties.intance.getMessageForKey("desactivar.dispositivo"))
							.setContentText(YACSmartProperties.intance.getMessageForKey("desactivar.dispositivo.error"))
							.setCancelText("NO")
							.setConfirmText("SI")
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
									//Llamar a ws de desactivar dispositivo

								}
							})
							.show();
				}
			}
		}else{
			//Error
			new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
					.setTitleText(YACSmartProperties.intance.getMessageForKey("desactivar.dispositivo"))
					.setContentText(YACSmartProperties.intance.getMessageForKey("desactivar.dispositivo.error"))
					.setCancelText("NO")
					.setConfirmText("SI")
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
							//Llamar a ws de desactivar dispositivo

						}
					})
					.show();
		}


	}


	class DispositivoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public Dispositivo getItem(int position) {
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
						R.layout.li_dispositivo, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			final Dispositivo dispositivo = getItem(position);


			File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + dispositivo.getImei() +".jpg");
			if(foto.exists()){
				Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + dispositivo.getImei() +".jpg");
				if(bmImg != null){
					mostrarImagen(holder.iv_icon, bmImg);

				}

			}else {
				Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.usuarioperfil)).getBitmap();
				mostrarImagen(holder.iv_icon, bitmap);
			}
			Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
			holder.nombreDispositivo.setText(dispositivo.getNombreDispositivo());
			holder.nombreDispositivo.setTypeface(fontRegular);

			holder.btnDesactivarDispositivo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
							.setTitleText(YACSmartProperties.intance.getMessageForKey("desactivar.dispositivo"))
							.setContentText(dispositivo.getNombreDispositivo() + " " + YACSmartProperties.intance.getMessageForKey("desactivar.dispositivo.subtitulo"))
							.setCancelText("NO")
							.setConfirmText("SI")
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
									//Llamar a ws de desactivar dispositivo
									idDispositivoBorrar = dispositivo.getId();
									DesactivarDispositivoAsyncTask desactivarDispositivoAsyncTask = new DesactivarDispositivoAsyncTask(ListaDispositivosActivity.this, idDispositivoBorrar);
									desactivarDispositivoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								}
							})
							.show();
				}
			});
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView nombreDispositivo;
			ImageButton btnDesactivarDispositivo;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.ivFoto);
				nombreDispositivo = (TextView) view.findViewById(R.id.txtNombreDispositivo);
				btnDesactivarDispositivo = (ImageButton) view.findViewById(R.id.btnDesactivarDispositivo);
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