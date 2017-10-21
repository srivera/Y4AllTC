package ec.com.yacare.y4all.lib.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ec.com.yacare.y4all.lib.dto.MensajeTexto;

public class MensajeTextoDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_MSJ_ID,
			MySQLiteHelper.COLUMN_MSJ_TEXTO};

	public MensajeTextoDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public MensajeTexto createMensaje(MensajeTexto mensajeTexto) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_MSJ_ID, mensajeTexto.getId());
		values.put(MySQLiteHelper.COLUMN_MSJ_TEXTO, mensajeTexto.getTexto());

		database.insert(MySQLiteHelper.TABLA_MENSAJE_TEXTO, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLA_MENSAJE_TEXTO,
				allColumns, MySQLiteHelper.COLUMN_MSJ_ID + " = '" + mensajeTexto.getId() + "'"  , null,
				null, null, null);
		cursor.moveToFirst();
		MensajeTexto newMensaje = cursorToMensaje(cursor);
		cursor.close();
		return newMensaje;
	}

	public void deleteMensaje(String idMensaje) {
//		System.out.println("deleteMensaje with id: " + idMensaje);
		database.delete(MySQLiteHelper.TABLA_MENSAJE_TEXTO, MySQLiteHelper.COLUMN_MSJ_ID
				+ " = '" + idMensaje + "'", null);
	}


	public ArrayList<MensajeTexto> getAllMensajes() {
		ArrayList<MensajeTexto> mensajes = new ArrayList<MensajeTexto>();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_MENSAJE_TEXTO,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MensajeTexto mensajeTexto = cursorToMensaje(cursor);
			mensajes.add(mensajeTexto);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return mensajes;
	}

	public ArrayList<String> getAllMensajesString() {
		ArrayList<String> mensajesString = new ArrayList<String>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_MENSAJE_TEXTO,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MensajeTexto mensajeTexto = cursorToMensaje(cursor);
			mensajesString.add(mensajeTexto.getTexto());
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return mensajesString;
	}

	private MensajeTexto cursorToMensaje(Cursor cursor) {
		MensajeTexto mensajeTexto = new MensajeTexto();
		mensajeTexto.setId(cursor.getString(0));
		mensajeTexto.setTexto(cursor.getString(1));
		return mensajeTexto;
	}


}
