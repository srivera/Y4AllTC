package ec.com.yacare.y4all.activities.respuesta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.Respuesta;
import ec.com.yacare.y4all.lib.enumer.TipoRespuestaEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.RespuestaDataSource;
import ec.com.yacare.y4all.lib.tareas.EnviarComandoThread;
import ec.com.yacare.y4all.lib.util.AudioQueu;

import static ec.com.yacare.y4all.activities.R.id.nombreDispositivo;

public class AdministrarRespuestasActivity extends AppCompatActivity {

	private RespuestaDataSource datasource;
	private Respuesta respuestaGrabada;

	private ArrayList<Respuesta> mAppList;
	private AppAdapter mAdapter;
	public ListView mListView;

	private Equipo equipoSeleccionado;
	private DatosAplicacion datosAplicacion;

	private FloatingActionButton fabNuevo;

	public static final int REQUEST_NUEVA_RESPUESTA = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_respuesta);

		if (isScreenLarge()) {
//			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		datosAplicacion = (DatosAplicacion) getApplicationContext();
		equipoSeleccionado = datosAplicacion.getEquipoSeleccionado();

		datasource = new RespuestaDataSource(getApplicationContext());
		datasource.open();

		Respuesta respuestaBusqueda = new Respuesta();
		respuestaBusqueda.setIdEquipo(equipoSeleccionado.getId());
		mAppList = datasource.getRespuestasEquipo(respuestaBusqueda);
		datasource.close();

		EnviarComandoThread genericoAsyncTask = new EnviarComandoThread(AdministrarRespuestasActivity.this, YACSmartProperties.LISTAR_RESPUESTAS + ";", null, null, null, datosAplicacion.getEquipoSeleccionado().getIpLocal(),
				YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
		genericoAsyncTask.start();

		mListView = (ListView) findViewById(R.id.listView);

		mAdapter = new AppAdapter(getApplicationContext(), R.layout.li_respuesta);
		mListView.setAdapter(mAdapter);

		fabNuevo = (FloatingActionButton) findViewById(R.id.fabNuevo);
		fabNuevo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(AdministrarRespuestasActivity.this, NuevaRespuestaActivity.class);
				startActivityForResult(i, REQUEST_NUEVA_RESPUESTA);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_NUEVA_RESPUESTA){
			if(data != null && data.getSerializableExtra("respuesta") != null) {
				Respuesta respuesta = (Respuesta) data.getSerializableExtra("respuesta");
				mAppList.add(respuesta);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	public void verificarEliminarRespuesta(String comando, boolean resultado) {
		if (resultado) {
			datasource = new RespuestaDataSource(getApplicationContext());
			datasource.open();
			datasource.deleteRespuesta(respuestaGrabada.getId());
			datasource.close();

			mListView.setAdapter(mAdapter);
			mAppList.remove(respuestaGrabada);
			mAdapter.notifyDataSetChanged();
		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					AdministrarRespuestasActivity.this);
			alertDialogBuilder.setTitle(YACSmartProperties.intance.getMessageForKey("titulo.error"))
					.setMessage("No se pudo eliminar la respuesta del portero, vuelva a intentarlo")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
		}
	}
	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	public void verificarListarRespuestas() {
		datasource = new RespuestaDataSource(getApplicationContext());
		datasource.open();

		Respuesta respuestaBusqueda = new Respuesta();
		respuestaBusqueda.setIdEquipo(equipoSeleccionado.getId());
		mAppList = datasource.getRespuestasEquipo(respuestaBusqueda);
		datasource.close();
		mAdapter.notifyDataSetChanged();

	}

	public Respuesta getRespuestaGrabada() {
		return respuestaGrabada;
	}

	public void setRespuestaGrabada(Respuesta respuestaGrabada) {
		this.respuestaGrabada = respuestaGrabada;
	}

	class AppAdapter extends ArrayAdapter<Respuesta> {

		public AppAdapter(Context context, int resource) {
			super(context, resource);
		}

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public Respuesta getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.li_respuesta, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			final Respuesta item = getItem(position);
			holder.nombreRespuesta.setText(item.getNombre());
			String tipo = item.getTipo();

			if (item.getTipoVoz().equals(YACSmartProperties.getInstance().getMessageForKey("tipo.voz.mujer"))) {
				holder.imgGenero.setImageResource(R.drawable.mujer1);
			} else {
				holder.imgGenero.setImageResource(R.drawable.hombre1);
			}

			if (item.getTipo().equals(TipoRespuestaEnum.TR02.getCodigo())) {
				tipo = (TipoRespuestaEnum.TR02.getDescripcion());
			} else if (item.getTipo().equals(TipoRespuestaEnum.TR03.getCodigo())) {
				tipo = (TipoRespuestaEnum.TR03.getDescripcion());
			} else {
				tipo = (TipoRespuestaEnum.TR04.getDescripcion());
			}

			holder.tipoRespuesta.setText(tipo);
			Typeface fontRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
			holder.tipoRespuesta.setTypeface(fontRegular);
			holder.nombreRespuesta.setTypeface(fontRegular);

			holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setRespuestaGrabada(item);
					DatosAplicacion datosAplicacion = (DatosAplicacion) getApplicationContext();
					EnviarComandoThread genericoAsyncTask = new EnviarComandoThread(AdministrarRespuestasActivity.this,
							YACSmartProperties.COM_ELIMINAR_RESPUESTA + ";" + item.getId(),
							null, null, AdministrarRespuestasActivity.this,
							datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					genericoAsyncTask.start();
				}
			});

			holder.btnPlay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DatosAplicacion datosAplicacion = (DatosAplicacion) getApplicationContext();
					setRespuestaGrabada(new Respuesta());
					getRespuestaGrabada().setId(item.getId());

					String datosConfT = YACSmartProperties.COM_REPRODUCIR_RESPUESTA + ";" + nombreDispositivo + ";" + "ANDROID" + ";" + equipoSeleccionado.getNumeroSerie() + ";" + item.getId() + ";" ;
					EnviarComandoThread genericoAsyncTask = new EnviarComandoThread(AdministrarRespuestasActivity.this, datosConfT, null, null, null,
							datosAplicacion.getEquipoSeleccionado().getIpLocal(), YACSmartProperties.PUERTO_COMANDO_DEFECTO, null);
					genericoAsyncTask.start();
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView nombreRespuesta;
			TextView tipoRespuesta;
			ImageView imgGenero;
			ImageButton btnPlay;
			ImageButton btnBorrar;

			public ViewHolder(View view) {
				nombreRespuesta = (TextView) view.findViewById(R.id.nombreRespuesta);
				tipoRespuesta = (TextView) view.findViewById(R.id.tipoRespuesta);
				imgGenero = (ImageView) view.findViewById(R.id.imgGenero);
				btnPlay = (ImageButton) view.findViewById(R.id.btnPlay);
				btnBorrar = (ImageButton) view.findViewById(R.id.btnBorrar);
				view.setTag(this);
			}
		}
	}
}
