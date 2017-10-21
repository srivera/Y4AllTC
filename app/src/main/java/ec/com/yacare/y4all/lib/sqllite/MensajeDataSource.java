package ec.com.yacare.y4all.lib.sqllite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ec.com.yacare.y4all.lib.dto.Mensaje;

public class MensajeDataSource {



	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_MENSAJE_ID,
			MySQLiteHelper.COLUMN_DISP_ID, MySQLiteHelper.COLUMN_DISP_MENSAJE,  MySQLiteHelper.COLUMN_DISP_FECHA,  MySQLiteHelper.COLUMN_DISP_HORA ,
			MySQLiteHelper.COLUMN_DISP_ESTADO, MySQLiteHelper.COLUMN_DISP_TIPO, MySQLiteHelper.COLUMN_DISP_DIRECCION};

	public MensajeDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Mensaje createMensajeNew(Mensaje mensaje) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_MENSAJE_ID, mensaje.getId());
		values.put(MySQLiteHelper.COLUMN_DISP_ID, mensaje.getIdDispositivo());
		values.put( MySQLiteHelper.COLUMN_DISP_MENSAJE, mensaje.getMensaje());
		values.put(MySQLiteHelper.COLUMN_DISP_FECHA, mensaje.getFecha());
		values.put(MySQLiteHelper.COLUMN_DISP_HORA , mensaje.getHora());
		values.put(MySQLiteHelper.COLUMN_DISP_ESTADO, mensaje.getEstado());
		values.put(MySQLiteHelper.COLUMN_DISP_TIPO, mensaje.getTipo());
		values.put(MySQLiteHelper.COLUMN_DISP_DIRECCION, mensaje.getDireccion());

		database.insert(MySQLiteHelper.TABLA_MENSAJE, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLA_MENSAJE,
				allColumns, MySQLiteHelper.COLUMN_MENSAJE_ID + " = '" + mensaje.getId() + "'"  , null,
				null, null, null);
		cursor.moveToFirst();
		Mensaje mensajeNew = cursorToMensaje(cursor);
		cursor.close();
		return mensajeNew;
	}

	public void deleteMensaje(String idMensaje) {
//		System.out.println("deleteMensajeOffLineNew with id: " + idMensaje);
		database.delete(MySQLiteHelper.TABLA_MENSAJE, MySQLiteHelper.COLUMN_MENSAJE_ID
				+ " = '" + idMensaje + "'", null);
	}

	public void deleteAllMensaje() {
		database.delete(MySQLiteHelper.TABLA_MENSAJE, null, null);
	}
	
	public void updateMensaje(Mensaje mensaje) {
//		System.out.println("updateMensajeOffLine with id: " + mensaje.getId());
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_DISP_ID, mensaje.getIdDispositivo());
		values.put( MySQLiteHelper.COLUMN_DISP_MENSAJE, mensaje.getMensaje());
		values.put(MySQLiteHelper.COLUMN_DISP_FECHA, mensaje.getFecha());
		values.put(MySQLiteHelper.COLUMN_DISP_HORA , mensaje.getHora());
		values.put(MySQLiteHelper.COLUMN_DISP_ESTADO, mensaje.getEstado());
		values.put(MySQLiteHelper.COLUMN_DISP_TIPO, mensaje.getTipo());
		values.put(MySQLiteHelper.COLUMN_DISP_DIRECCION, mensaje.getDireccion());

		database.update(MySQLiteHelper.TABLA_MENSAJE, values, MySQLiteHelper.COLUMN_MENSAJE_ID + " = '" + mensaje.getId() + "'", null);
	}

	public ArrayList<Mensaje> getMensajeByDispositivo(String idDispositivo) {
		ArrayList<Mensaje> mensajes = new ArrayList<Mensaje>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_MENSAJE,
				allColumns, MySQLiteHelper.COLUMN_DISP_ID + " = '" + idDispositivo + "'" , null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Mensaje mensaje = cursorToMensaje(cursor);
			mensajes.add(mensaje);
			cursor.moveToNext();
		}
		cursor.close();
		return mensajes;
	}

	private Mensaje cursorToMensaje(Cursor cursor) {
		Mensaje mensaje = new Mensaje();
		mensaje.setId(cursor.getString(0));
		mensaje.setIdDispositivo(cursor.getString(1));
		mensaje.setMensaje(cursor.getString(2));
		mensaje.setFecha(cursor.getString(3));
		mensaje.setHora(cursor.getString(4));
		mensaje.setEstado(cursor.getString(5));
		mensaje.setTipo(cursor.getString(6));
		mensaje.setDireccion(cursor.getString(7));
		return mensaje;
	}
}
