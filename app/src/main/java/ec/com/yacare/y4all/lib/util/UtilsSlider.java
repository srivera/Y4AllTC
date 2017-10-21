package ec.com.yacare.y4all.lib.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class UtilsSlider {

	private Context _context;

	// constructor
	public UtilsSlider(Context context) {
		this._context = context;
	}
	// Number of columns of Grid View
	public static final int NUM_OF_COLUMNS = 3;

	// Gridview image padding
	public static final int GRID_PADDING = 8; // in dp

	// SD card image directory
	public static final String PHOTO_ALBUM = "NAT";

	// supported file formats
	public static final List<String> FILE_EXTN = Arrays.asList("jpg");
	public static final List<String> FILE_EXTN_VIDEO = Arrays.asList("mp4");

	/*
	 * Reading file paths from SDCard
	 */
	public ArrayList<String> getFilePaths(String idEvento) {
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
					if (IsSupportedFile(filePath)) {
						// Add image path to array list

						filePaths.add(filePath);
						if(listFiles[i].getName().equals(idEvento + ".jpg")){
							Log.d("idEvento","idEvento");
						}
					}
				}
			} else {
				// image directory is empty
				Toast.makeText(
						_context,
						UtilsSlider.PHOTO_ALBUM
								+ " is empty. Please load some images in it !",
						Toast.LENGTH_LONG).show();
			}

		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(_context);
			alert.setTitle("Error!");
			alert.setMessage(UtilsSlider.PHOTO_ALBUM
					+ " directory path is not valid! Please set the image directory name AppConstant.java class");
			alert.setPositiveButton("OK", null);
			alert.show();
		}

		return filePaths;
	}

	/*
	 * Check supported file extensions
	 *
	 * @returns boolean
	 */
	private boolean IsSupportedFile(String filePath) {
		String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
				filePath.length());

		if (UtilsSlider.FILE_EXTN
				.contains(ext.toLowerCase(Locale.getDefault())))
			return true;
		else
			return false;

	}

	/*
	 * getting screen width
	 */
	public int getScreenWidth() {
		int columnWidth;
		WindowManager wm = (WindowManager) _context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		columnWidth = point.x;
		return columnWidth;
	}
}
