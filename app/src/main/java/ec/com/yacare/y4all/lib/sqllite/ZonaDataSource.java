package ec.com.yacare.y4all.lib.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;


public class ZonaDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ZONA_ID,
			MySQLiteHelper.COLUMN_ZONA_NOMBRE, MySQLiteHelper.COLUMN_ZONA_ENCENDER_TIMBRE,
			MySQLiteHelper.COLUMN_ZONA_ID_EQUIPO, MySQLiteHelper.COLUMN_ZONA_ID_ROUTER,
			MySQLiteHelper.COLUMN_ZONA_NUMERO
	};

	public ZonaDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public ZonaLuces createZona(ZonaLuces zonaLuces) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ZONA_ID, zonaLuces.getId());
		values.put(MySQLiteHelper.COLUMN_ZONA_NOMBRE, zonaLuces.getNombreZona());
		values.put(MySQLiteHelper.COLUMN_ZONA_ENCENDER_TIMBRE, zonaLuces.getEncenderTimbre());
		values.put(MySQLiteHelper.COLUMN_ZONA_ID_EQUIPO, zonaLuces.getIdEquipo());
		values.put(MySQLiteHelper.COLUMN_ZONA_ID_ROUTER, zonaLuces.getIdRouter());
		values.put(MySQLiteHelper.COLUMN_ZONA_NUMERO, zonaLuces.getNumeroZona());

		database.insert(MySQLiteHelper.TABLA_ZONA_LUCES, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLA_ZONA_LUCES,
				allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID + " = '" + zonaLuces.getId() + "'"  , null,
				null, null, null);
		cursor.moveToFirst();
		ZonaLuces newZona = cursorToZona(cursor);
		cursor.close();
		return newZona;
	}

	public void updateZona(ZonaLuces zonaLuces) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ZONA_NOMBRE, zonaLuces.getNombreZona());
		values.put(MySQLiteHelper.COLUMN_ZONA_ENCENDER_TIMBRE, zonaLuces.getEncenderTimbre());
		values.put(MySQLiteHelper.COLUMN_ZONA_ID_EQUIPO, zonaLuces.getIdEquipo());
		values.put(MySQLiteHelper.COLUMN_ZONA_ID_ROUTER, zonaLuces.getIdRouter());
		values.put(MySQLiteHelper.COLUMN_ZONA_NUMERO, zonaLuces.getNumeroZona());
		database.update(MySQLiteHelper.TABLA_ZONA_LUCES, values, MySQLiteHelper.COLUMN_EQUIPO_ID + " = '" + zonaLuces.getId() + "'", null);
	}

	public void deleteZonaImac(String imac) {
		database.delete(MySQLiteHelper.TABLA_ZONA_LUCES, MySQLiteHelper.COLUMN_ZONA_ID_ROUTER + " = '" + imac + "'", null);
	}

	public ArrayList<ZonaLuces> getAllZonaRouter(String idRouter) {
		ArrayList<ZonaLuces> zonas = new ArrayList<ZonaLuces>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_ZONA_LUCES,
				allColumns, MySQLiteHelper.COLUMN_ZONA_ID_ROUTER  + " = '"+ idRouter + "'", null, null, null, MySQLiteHelper.COLUMN_ZONA_NUMERO);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ZonaLuces zonaLuces = cursorToZona(cursor);
			zonas.add(zonaLuces);
			cursor.moveToNext();
		}
		cursor.close();
		return zonas;
	}
	public ArrayList<ZonaLuces> getAllZonaRouterProg(String idRouter) {
		ArrayList<ZonaLuces> zonas = new ArrayList<ZonaLuces>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_ZONA_LUCES,
				allColumns, MySQLiteHelper.COLUMN_ZONA_ID_ROUTER  + " = '"+ idRouter + "' and " +
						MySQLiteHelper.COLUMN_ZONA_NUMERO + " != 'R'", null, null, null, MySQLiteHelper.COLUMN_ZONA_NUMERO);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ZonaLuces zonaLuces = cursorToZona(cursor);
			zonas.add(zonaLuces);
			cursor.moveToNext();
		}
		cursor.close();
		return zonas;
	}
	private ZonaLuces cursorToZona(Cursor cursor) {
		ZonaLuces zonaLuces = new ZonaLuces();
		zonaLuces.setId(cursor.getString(0));
		zonaLuces.setNombreZona(cursor.getString(1));
		zonaLuces.setEncenderTimbre(cursor.getString(2));
		zonaLuces.setIdEquipo(cursor.getString(3));
		zonaLuces.setIdRouter(cursor.getString(4));
		zonaLuces.setNumeroZona(cursor.getString(5));
		return zonaLuces;
	}
	

	public ZonaLuces getZonaRouterId(String idRouter, String idZona) {
		ArrayList<ZonaLuces> zonas = new ArrayList<ZonaLuces>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_ZONA_LUCES,
				allColumns, MySQLiteHelper.COLUMN_ZONA_ID + " = '"+ idZona +"' and " + MySQLiteHelper.COLUMN_ZONA_ID_ROUTER  + " = '"+ idRouter + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ZonaLuces zonaLuces = cursorToZona(cursor);
			zonas.add(zonaLuces);
			cursor.moveToNext();
		}
		cursor.close();
		if(zonas.size() > 0){
			return zonas.get(0);
		}else{
			return null;
		}
	}
}
