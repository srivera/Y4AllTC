package ec.com.yacare.y4all.activities.evento;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import ec.com.yacare.y4all.activities.R;
import ec.com.yacare.y4all.adapter.SliderArrayAdapter;
import ec.com.yacare.y4all.lib.dto.Evento;
import ec.com.yacare.y4all.lib.util.UtilsSlider;

public class ImageSliderActivity extends Activity {

	private SliderArrayAdapter adapter;
	public ViewPager viewPager;
	Integer posicion;
	public String accion = "NOT";;
	public MediaController mediaController;
	ArrayList<Evento> eventoCol;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);

		viewPager = (ViewPager) findViewById(R.id.pager);

		Intent i = getIntent();

		Evento evento = (Evento) getIntent().getSerializableExtra("evento");
		eventoCol = (ArrayList<Evento>) getIntent().getSerializableExtra("eventos");


		int tabActivo = getIntent().getIntExtra("tabActivo", 0);

		ArrayList<String> archivos = null;

		if(tabActivo == 0  || tabActivo == 1) {
			archivos = getFilePathsOrder("FOTO");
			for (int contador = archivos.size() - 1; contador >= 0; contador--){
				if(archivos.get(contador).endsWith(evento.getId() + ".jpg")){
					posicion = contador;
					Log.d("idEvento","idEvento");
					break;
				}else if(archivos.get(contador).endsWith(evento.getId() + ".mp4")){
					posicion = contador;
					Log.d("idEvento","idEvento");
					break;
				}
			}
		}else if(tabActivo == 2) {
			archivos = getFilePathsOrder("BUZON");
			for (int contador = archivos.size() - 1; contador >= 0; contador--){
				if(archivos.get(contador).endsWith(evento.getId() + ".mp4")){
					posicion = contador;
					Log.d("idEvento","idEvento");
					break;
				}
			}
		}else if(tabActivo == 3) {
			archivos = getFilePathsOrder("PUERTA");
			for (int contador = archivos.size() - 1; contador >= 0; contador--){
				if(archivos.get(contador).endsWith(evento.getId() + "P" + ".mp4")){
					posicion = contador;
					Log.d("idEvento","idEvento");
					break;
				}
			}
		}
		adapter = new SliderArrayAdapter(ImageSliderActivity.this,
				archivos);
		viewPager.setAdapter(adapter);

		viewPager.setCurrentItem(posicion);
	}

	public Boolean isEvento(String nameFile) {
		Boolean encontro = false;

		String[] parts = nameFile.split("/");
		String id = parts[parts.length - 1];
		String idEvento = id.substring(0, id.indexOf("."));
		for(Evento evento: eventoCol){
			if(idEvento.startsWith(evento.getId())){
				encontro = true;
				break;
			}
		}
		return encontro;
	}
	public Boolean isEventoBuzon(String nameFile) {
		Boolean encontro = false;

		String[] parts = nameFile.split("/");
		String id = parts[parts.length - 1];
		String idEvento = id.substring(0, id.indexOf("."));
		for(Evento evento: eventoCol){
			if(idEvento.equals(evento.getId())){
				encontro = true;
				break;
			}
		}
		return encontro;
	}
	public ArrayList<String> getFilePaths(String tipo) {
		ArrayList<String> filePaths = new ArrayList<String>();

		File directory = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/");

		// check for directory
		if (directory.isDirectory()) {
			// getting list of file paths
			File[] listFiles = directory.listFiles();

			// Check for count
			if (listFiles.length > 0) {


				// loop through all files
				for (int i = 0; i < listFiles.length; i++) {

					// get file path
					String filePath = listFiles[i].getAbsolutePath();

					// check for supported file extension

					if (tipo.equals("FOTO")) {
						if (IsSupportedFile(filePath) && isEvento(filePath)) {
							// Add image path to array list
							filePaths.add(filePath);
						}else if (IsSupportedFileVideo(filePath) && isEvento(filePath)) {
							// Add image path to array list
							filePaths.add(filePath);

						}
					} else {
						if (IsSupportedFileVideo(filePath) && isEventoBuzon(filePath)) {
							// Add image path to array list
							filePaths.add(filePath);

						}
					}
				}
			} else {
				// image directory is empty
				Toast.makeText(
						getApplicationContext(),
						UtilsSlider.PHOTO_ALBUM
								+ " is empty. Please load some images in it !",
						Toast.LENGTH_LONG).show();
			}

		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
			alert.setTitle("Error!");
			alert.setMessage(UtilsSlider.PHOTO_ALBUM
					+ " directory path is not valid! Please set the image directory name AppConstant.java class");
			alert.setPositiveButton("OK", null);
			alert.show();
		}

		return filePaths;
	}

	public String buscarPath(String idEvento, File[] listFiles, String tipo) {
		for (int i = 0; i < listFiles.length; i++) {
			String filePath = listFiles[i].getAbsolutePath();

			if (tipo.equals("FOTO")) {
				if (IsSupportedFile(filePath) && filePath.endsWith(idEvento + ".jpg")) {
					return filePath;
				} else if (IsSupportedFileVideo(filePath) && filePath.endsWith(idEvento + ".mp4")) {
					return filePath;
				}
			} else if (tipo.equals("PUERTA")) {
				if (IsSupportedFile(filePath) && filePath.endsWith(idEvento + ".jpg")) {
					return filePath;
				} else if (IsSupportedFileVideo(filePath) && filePath.endsWith(idEvento + "P.mp4")) {
					return filePath;
				}
			} else {
				if (IsSupportedFileVideo(filePath) && filePath.endsWith(idEvento + ".mp4")) {
					return filePath;
				}
			}
		}
		return "";
	}

	public ArrayList<String> getFilePathsOrder(String tipo) {
		ArrayList<String> filePaths = new ArrayList<String>();

		File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/");

		if (directory.isDirectory()) {
			File[] listFiles = directory.listFiles();
			if (listFiles.length > 0) {
				for (Evento evento : eventoCol) {
					String path = buscarPath(evento.getId(), listFiles, tipo);
					if(!path.equals("")){
						filePaths.add(path);
					}
				}
			}

		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
			alert.setTitle("Error!");
			alert.setMessage(UtilsSlider.PHOTO_ALBUM
					+ " directory path is not valid! Please set the image directory name AppConstant.java class");
			alert.setPositiveButton("OK", null);
			alert.show();
		}

		return filePaths;
	}

	private boolean IsSupportedFile(String filePath) {
		String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
				filePath.length());

		if (UtilsSlider.FILE_EXTN
				.contains(ext.toLowerCase(Locale.getDefault())))
			return true;
		else
			return false;

	}

	private boolean IsSupportedFileVideo(String filePath) {
		String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
				filePath.length());

		if (UtilsSlider.FILE_EXTN_VIDEO
				.contains(ext.toLowerCase(Locale.getDefault())))
			return true;
		else
			return false;

	}

	@Override
	public void onBackPressed() {
		Intent output = new Intent();
		output.putExtra("accion", accion);
		output.putExtra("indice", getIntent().getStringExtra("indice"));
		setResult(RESULT_OK, output);
		super.onBackPressed();
	}

}