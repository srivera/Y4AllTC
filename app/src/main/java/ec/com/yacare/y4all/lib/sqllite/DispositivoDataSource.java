package ec.com.yacare.y4all.lib.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ec.com.yacare.y4all.lib.dto.Dispositivo;

public class DispositivoDataSource {

	// Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_DISPOSITIVO_ID,
	      MySQLiteHelper.COLUMN_DISPOSITIVO_NOMBRE, MySQLiteHelper.COLUMN_DISPOSITIVO_TIPO,
	      MySQLiteHelper.COLUMN_DISPOSITIVO_IMEI, MySQLiteHelper.COLUMN_DISPOSITIVO_VERSION_FOTO};

	  public DispositivoDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Dispositivo createDispositivo(Dispositivo dispositivo) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_ID, dispositivo.getId());
	    values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_NOMBRE, dispositivo.getNombreDispositivo());
	    values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_TIPO, dispositivo.getTipo());
	    values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_IMEI, dispositivo.getImei());
		values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_VERSION_FOTO, dispositivo.getVersionFoto());
	    database.insert(MySQLiteHelper.TABLA_DISPOSITIVO, null, values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLA_DISPOSITIVO,
	        allColumns, MySQLiteHelper.COLUMN_DISPOSITIVO_ID + " = '" + dispositivo.getId() + "'"  , null,
	        null, null, null);
	    cursor.moveToFirst();
	    Dispositivo dispositivo2 = cursorToDispositivo(cursor);
	    cursor.close();
	    return dispositivo2;
	  }

	  public void deleteDispositivo(String idDispositivo) {
//	    System.out.println("deleteDispositivo with id: " + idDispositivo);
	    database.delete(MySQLiteHelper.TABLA_DISPOSITIVO, MySQLiteHelper.COLUMN_DISPOSITIVO_ID
	        + " = '" + idDispositivo + "'", null);
	  }

	  public void deleteAll() {
		    database.delete(MySQLiteHelper.TABLA_DISPOSITIVO, null, null);
	  }
	  
	  
	  public void updateDispositivo(Dispositivo dispositivo) {
//		    System.out.println("updateDispositivo with id: " + dispositivo.getId());
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_ID, dispositivo.getId());
		    values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_NOMBRE, dispositivo.getNombreDispositivo());
		    values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_TIPO, dispositivo.getTipo());
		    values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_IMEI, dispositivo.getImei());
		  values.put(MySQLiteHelper.COLUMN_DISPOSITIVO_VERSION_FOTO, dispositivo.getVersionFoto());
		    database.update(MySQLiteHelper.TABLA_DISPOSITIVO, values,  MySQLiteHelper.COLUMN_DISPOSITIVO_ID + " = '" + dispositivo.getId() + "'", null);
	  }
	  
	  public Dispositivo getDispositivoId(Dispositivo dispositivoBusqueda) {
		  	Dispositivo dispositivo = null;
			Cursor cursor = database.query(MySQLiteHelper.TABLA_DISPOSITIVO,
					allColumns, MySQLiteHelper.COLUMN_DISPOSITIVO_ID + " = '" + dispositivoBusqueda.getId() + "'", null, null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				dispositivo = cursorToDispositivo(cursor);
			}
			cursor.close();
			return dispositivo;
		} 
	  
	  public Dispositivo getDispositivoImei(String dispositivoImei) {
		  	Dispositivo dispositivo = null;
			Cursor cursor = database.query(MySQLiteHelper.TABLA_DISPOSITIVO,
					allColumns, MySQLiteHelper.COLUMN_DISPOSITIVO_IMEI + " = '" + dispositivoImei + "'", null, null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				dispositivo = cursorToDispositivo(cursor);
			}
			cursor.close();
			return dispositivo;
		} 
	  
	  public ArrayList<Dispositivo> getAllDispositivo() {
	    ArrayList<Dispositivo> dispositivos = new ArrayList<Dispositivo>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLA_DISPOSITIVO,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Dispositivo dispositivo = cursorToDispositivo(cursor);
	      dispositivos.add(dispositivo);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return dispositivos;
	  }

	  private Dispositivo cursorToDispositivo(Cursor cursor) {
	    Dispositivo dispositivo = new Dispositivo();
	    dispositivo.setId(cursor.getString(0));
	    dispositivo.setNombreDispositivo(cursor.getString(1));
	    dispositivo.setTipo(cursor.getString(2));
	    dispositivo.setImei(cursor.getString(3));
		dispositivo.setVersionFoto(cursor.getString(4));
	    return dispositivo;
	  }


}
