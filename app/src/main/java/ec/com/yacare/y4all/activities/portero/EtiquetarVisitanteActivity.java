package ec.com.yacare.y4all.activities.portero;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ListView;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Face;

import java.util.ArrayList;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.adapter.VisitanteArrayAdapter;
import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.dto.Visitante;
import ec.com.yacare.y4all.lib.util.AudioQueu;
import ec.com.yacare.y4all.lib.util.CustomView;

public class EtiquetarVisitanteActivity extends Activity {

	private CustomView fotoVisitante;

	private ListView listaVisitantes;

	private Bitmap bitmap;

	private Visitante visitante;

	private DatosAplicacion datosAplicacion;

	private VisitanteArrayAdapter visitanteArrayAdapter;

	private SparseArray<Face> faces ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(AudioQueu.getRequestedOrientation());
		
		setContentView(R.layout.activity_etiquetar_visitante);
		datosAplicacion = (DatosAplicacion) getApplicationContext();

		fotoVisitante = (CustomView) findViewById(R.id.fotoVisitante);

		String nombreFoto = getIntent().getExtras().getString("nombreFoto");
		bitmap = BitmapFactory.decodeFile(nombreFoto);

		FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
				.setTrackingEnabled(false)
				.setLandmarkType(FaceDetector.ALL_LANDMARKS)
				.setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
				.build();

		// Create a frame from the bitmap and run face detection on the frame.
		Frame frame = new Frame.Builder().setBitmap(bitmap).build();

		faces = new SparseArray<Face>();
		faces = detector.detect(frame);


		fotoVisitante.setContent(bitmap, faces);

		detector.release();

		listaVisitantes = (ListView) findViewById(android.R.id.list);

//		listaVisitantes.setFocusable(true);
//		listaVisitantes.setClickable(true);
//		listaVisitantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				visitante = (Visitante) (listaVisitantes.getItemAtPosition(position));
//				final EditText input = new EditText(EtiquetarVisitanteActivity.this);
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.MATCH_PARENT,
//						LinearLayout.LayoutParams.MATCH_PARENT);
//				input.setLayoutParams(lp);
//
//				final AlertDialog d = new AlertDialog.Builder(EtiquetarVisitanteActivity.this)
//						.setTitle("Etiquete la persona")
//						.setCancelable(true)
//						.setView(input)
//						.setPositiveButton("OK",
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog, int which) {
//
//										if (!input.getText().toString().equals("")) {
//											visitante.setEtiqueta(input.getText().toString());
//											listaVisitantes.setAdapter(visitanteArrayAdapter);
//											ByteArrayOutputStream baos=new  ByteArrayOutputStream();
//											visitante.foto.compress(Bitmap.CompressFormat.PNG, 100, baos);
//											byte [] b=baos.toByteArray();
//											String fotoString = Base64.encodeToString(b, Base64.DEFAULT);
//
//											EtiquetarFotoAsyncTask etiquetarVisitanteActivity = new EtiquetarFotoAsyncTask(datosAplicacion.getEquipoSeleccionado().getNumeroSerie(),
//													getApplicationContext(), input.getText().toString(), fotoString);
//											etiquetarVisitanteActivity.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//											dialog.cancel();
//										}
//									}
//								}).create();
//
//				d.show();
//			}
//		});

		ArrayList<Visitante> visitantes = new ArrayList<Visitante>();


		for(int i = 0; i < faces.size(); i++) {
			Face face = faces.valueAt(i);
			if(face != null) {
				Visitante visitante = new Visitante();
				visitante.setLeft(Float.valueOf(face.getPosition().x).intValue());
				visitante.setTop(Float.valueOf(face.getPosition().y).intValue());
				visitante.setWidth(Float.valueOf(face.getWidth()).intValue());
				visitante.setHeight(Float.valueOf(face.getHeight()).intValue());
				visitante.bitmap = bitmap;
				visitantes.add(visitante);
			}

		}
		visitanteArrayAdapter = new VisitanteArrayAdapter(EtiquetarVisitanteActivity.this, visitantes);
		listaVisitantes.setAdapter(visitanteArrayAdapter);
	}



}