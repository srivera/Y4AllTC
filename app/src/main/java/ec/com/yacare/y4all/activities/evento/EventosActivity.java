package ec.com.yacare.y4all.activities.evento;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.adapter.SectionedGridRecyclerViewAdapter;
import ec.com.yacare.y4all.adapter.SimpleAdapter;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.sqllite.EventoDataSource;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.util.EndlessRecyclerViewScrollListener;


public class EventosActivity extends AppCompatActivity {

	private DatosAplicacion datosAplicacion;

	private Typeface fontRegular;

	private Button btnVisitas, btnFotos, btnBuzon, btnSensor;

	public ArrayList<Evento> eventoCol;

	protected static final int PAGESIZE = 20;

	private int paginacionActual = 0;

	private RecyclerView listEvento;
	private SimpleAdapter mAdapter;

	public int tabActivo = 0;

	private EndlessRecyclerViewScrollListener scrollListener;

	private SectionedGridRecyclerViewAdapter mSectionedAdapter;

	private SectionedGridRecyclerViewAdapter.Section[] dummy;

	private List<SectionedGridRecyclerViewAdapter.Section> sections;

	private String fecha;

	private int i;

	public static final int REQUEST_DETALLE_VISITA = 0;

	public Boolean multipleSelection = false;
	public ArrayList<Evento> eventoSeleccionadoCol = new ArrayList<>();


	private Integer numeroColumnas;
	private GridLayoutManager gridLayoutManager;
	public ImageButton fabEliminarSeleccion, fabSalir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_evento_paginada);

		datosAplicacion = (DatosAplicacion) getApplicationContext();
		datosAplicacion.setEventosActivity(EventosActivity.this);
		numeroColumnas = 3;
		if(isScreenLarge()) {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				numeroColumnas = 4;
			}
		} else {
			AudioQueu.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		}

		btnVisitas = (Button) findViewById(R.id.btnVisitas);
		btnFotos = (Button) findViewById(R.id.btnFotos);
		btnBuzon = (Button) findViewById(R.id.btnBuzon);
		btnSensor = (Button) findViewById(R.id.btnSensor);

		listEvento = (RecyclerView) findViewById(R.id.listEvento);
		listEvento.setHasFixedSize(true);

		fabEliminarSeleccion = (ImageButton) findViewById(R.id.fabEliminarSeleccion);
		fabEliminarSeleccion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for(Evento evento: eventoSeleccionadoCol){
					File fileVideo = new File(evento.getVideoBuzon());
					if (fileVideo.exists()) {
						fileVideo.delete();
					}
					fileVideo = new File(evento.getVideoPuerta());
					if (fileVideo.exists()) {
						fileVideo.delete();
					}
					fileVideo = new File(evento.getVideoInicial());
					if (fileVideo.exists()) {
						fileVideo.delete();
					}
					fileVideo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + evento.getId() + ".jpg");
					if (fileVideo.exists()) {
						fileVideo.delete();
					}
					EventoDataSource datasource = new EventoDataSource(getApplicationContext());
					datasource.open();
					datasource.deleteEvento(evento.getId());
					datasource.close();
				}
				paginacionActual = 0;
				EventoDataSource datasource = new EventoDataSource(getApplicationContext());
				datasource.open();
				if(tabActivo == 0){
					eventoCol = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('TIMBRAR','BUZON')", datosAplicacion.getEquipoSeleccionado().getId());
				}else if(tabActivo == 1){
					eventoCol = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('FOTO')", datosAplicacion.getEquipoSeleccionado().getId());
				}else if(tabActivo == 2){
					eventoCol = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('BUZON')", datosAplicacion.getEquipoSeleccionado().getId());
				}else{
					eventoCol = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('PUERTA')", datosAplicacion.getEquipoSeleccionado().getId());
				}
				datasource.close();
				cargarListaPrimeraVez();
				fabEliminarSeleccion.setVisibility(View.GONE);
			}
		});

			fabSalir = (ImageButton) findViewById(R.id.fabSalir);
		fabSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		gridLayoutManager = new GridLayoutManager(EventosActivity.this, numeroColumnas);


		listEvento.setLayoutManager(gridLayoutManager);

		fontRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

		btnVisitas.setTypeface(fontRegular);
		btnFotos.setTypeface(fontRegular);
		btnBuzon.setTypeface(fontRegular);
		btnSensor.setTypeface(fontRegular);

		RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
		itemAnimator.setAddDuration(1000);
		itemAnimator.setRemoveDuration(1000);
		listEvento.setItemAnimator(itemAnimator);

		scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
				Log.d("paginar","paginar");
				paginacionActual = paginacionActual + 1;
				ArrayList<Evento> eventoCol2 = null;
				EventoDataSource datasource = new EventoDataSource(getApplicationContext());
				datasource.open();
				if(tabActivo == 0){
					eventoCol2 = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('TIMBRAR','BUZON')", datosAplicacion.getEquipoSeleccionado().getId());
				}else if(tabActivo == 1){
					eventoCol2 = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('FOTO')", datosAplicacion.getEquipoSeleccionado().getId());
				}else if(tabActivo == 2){
					eventoCol2 = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('BUZON')", datosAplicacion.getEquipoSeleccionado().getId());
				}else{
					eventoCol2 = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('PUERTA')", datosAplicacion.getEquipoSeleccionado().getId());
				}
				datasource.close();
				actualizarLista(eventoCol2);
			}
		};
		listEvento.addOnScrollListener(scrollListener);
		btnVisitas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tabActivo != 0) {
					tabActivo = 0;
					paginacionActual = 0;
					cargarEventoVisitas();
				}
			}
		});

		btnFotos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tabActivo != 1) {
					tabActivo = 1;
					paginacionActual = 0;
					EventoDataSource datasource = new EventoDataSource(getApplicationContext());
					datasource.open();
					eventoCol = datasource.getPaginaEventosTipoEvento(0, PAGESIZE, "('FOTO')", datosAplicacion.getEquipoSeleccionado().getId());
					datasource.close();
					cargarListaPrimeraVez();
				}
			}
		});

		btnBuzon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tabActivo != 2) {
					tabActivo = 2;
					paginacionActual = 0;
					EventoDataSource datasource = new EventoDataSource(getApplicationContext());
					datasource.open();
					eventoCol = datasource.getPaginaEventosTipoEvento(0, PAGESIZE, "('BUZON')", datosAplicacion.getEquipoSeleccionado().getId());
					datasource.close();
					cargarListaPrimeraVez();
				}
			}
		});

		btnSensor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tabActivo != 3) {
					tabActivo = 3;
					paginacionActual = 0;
					cargarEventoPuerta();
				}
			}
		});

		cargarEventoVisitas();
		if(isScreenLarge()) {
			onConfigurationChanged(getResources().getConfiguration());
		}
	}

	public Boolean orientacionPortrait = false;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if(isScreenLarge()) {
				numeroColumnas = 4;
			}
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			if(isScreenLarge()) {
				numeroColumnas = 3;
			}
		}
		gridLayoutManager = new GridLayoutManager(EventosActivity.this, numeroColumnas);
		gridLayoutManager.setSpanSizeLookup(onSpanSizeLookup);
		dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
		mSectionedAdapter = new
				SectionedGridRecyclerViewAdapter(EventosActivity.this, R.layout.evento_section, R.id.dia, listEvento, mAdapter);
		mSectionedAdapter.setSections(sections.toArray(dummy));

		listEvento.setAdapter(mSectionedAdapter);

	}
	GridLayoutManager.SpanSizeLookup onSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
		@Override
		public int getSpanSize(int position) {
			if(isScreenLarge()) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					return 4;
				}else{
					return 3;
				}
			}
			return 3;
		}
	};
	private void cargarListaPrimeraVez() {
		fabEliminarSeleccion.setVisibility(View.GONE);
		mAdapter = new SimpleAdapter(EventosActivity.this, eventoCol, EventosActivity.this);
		sections = new ArrayList<SectionedGridRecyclerViewAdapter.Section>();
		fecha = "";
		i = 0;
		for (Evento evento : eventoCol) {
			if (fecha.equals("") || !fecha.equals(evento.getFecha().substring(0, 10))) {
				sections.add(new SectionedGridRecyclerViewAdapter.Section(i, evento.getFecha().substring(0, 10)));
				fecha = evento.getFecha().substring(0, 10);
			}
			i++;

		}

		dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
		mSectionedAdapter = new
				SectionedGridRecyclerViewAdapter(EventosActivity.this, R.layout.evento_section, R.id.dia, listEvento, mAdapter);
		mSectionedAdapter.setSections(sections.toArray(dummy));

		listEvento.setAdapter(mSectionedAdapter);
//		listEvento.addOnScrollListener(scrollListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		paginacionActual = 0;
		EventoDataSource datasource = new EventoDataSource(getApplicationContext());
		datasource.open();
		if(tabActivo == 0){
			eventoCol = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('TIMBRAR','BUZON')", datosAplicacion.getEquipoSeleccionado().getId());
		}else if(tabActivo == 1){
			eventoCol = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('FOTO')", datosAplicacion.getEquipoSeleccionado().getId());
		}else if(tabActivo == 2){
			eventoCol = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('BUZON')", datosAplicacion.getEquipoSeleccionado().getId());
		}else{
			eventoCol = datasource.getPaginaEventosTipoEvento(PAGESIZE * paginacionActual, PAGESIZE, "('PUERTA')", datosAplicacion.getEquipoSeleccionado().getId());
		}
		datasource.close();
		cargarListaPrimeraVez();
		fabEliminarSeleccion.setVisibility(View.GONE);
		listEvento.addOnScrollListener(scrollListener);

	}

	private void actualizarLista(ArrayList<Evento> eventoCol2) {
		for(Evento evento: eventoCol2){
            if(fecha.equals("") || !fecha.equals(evento.getFecha().substring(0,10))){
                sections.add(new SectionedGridRecyclerViewAdapter.Section(i, evento.getFecha().substring(0,10)));
                fecha = evento.getFecha().substring(0,10);
            }
            i++;

        }
		eventoCol.addAll(eventoCol2);
		mSectionedAdapter.setSections(sections.toArray(dummy));
		mSectionedAdapter.notifyDataSetChanged();
	}

	private void cargarEventoPuerta() {
		EventoDataSource datasource = new EventoDataSource(getApplicationContext());
		datasource.open();
		eventoCol = datasource.getPaginaEventosTipoEvento(0, PAGESIZE, "('PUERTA')", datosAplicacion.getEquipoSeleccionado().getId());
		datasource.close();
		cargarListaPrimeraVez();
	}

	public void actualizarLista(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mSectionedAdapter.notifyDataSetChanged();
			}
		});
	}

	private void cargarEventoVisitas() {
		EventoDataSource datasource = new EventoDataSource(getApplicationContext());
		datasource.open();
		eventoCol = datasource.getPaginaEventosTipoEvento(0, PAGESIZE, "('TIMBRAR','BUZON')", datosAplicacion.getEquipoSeleccionado().getId());
		datasource.close();
		cargarListaPrimeraVez();
	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		datosAplicacion.setEventosActivity(null);


	}
}