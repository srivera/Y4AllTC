package ec.com.yacare.y4all.lib.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ec.com.yacare.y4all.lib.dto.Respuesta;

public class RespuestaDataSource {

	// Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_RESPUESTA_ID,
	      MySQLiteHelper.COLUMN_NOMBRE, MySQLiteHelper.COLUMN_TIPO, MySQLiteHelper.COLUMN_EQUIPO_ID_R_FK ,MySQLiteHelper.COLUMN_TIPO_VOZ  };

	  public RespuestaDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Respuesta createRespuesta(Respuesta respuesta) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_RESPUESTA_ID, respuesta.getId());
	    values.put(MySQLiteHelper.COLUMN_NOMBRE, respuesta.getNombre());
	    values.put(MySQLiteHelper.COLUMN_TIPO, respuesta.getTipo());
	    values.put(MySQLiteHelper.COLUMN_EQUIPO_ID_R_FK, respuesta.getIdEquipo());
	    values.put(MySQLiteHelper.COLUMN_TIPO_VOZ, respuesta.getTipoVoz());
	    database.insert(MySQLiteHelper.TABLA_RESPUESTAS, null, values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLA_RESPUESTAS,
	        allColumns, MySQLiteHelper.COLUMN_RESPUESTA_ID + " = '" + respuesta.getId() + "'"  , null,
	        null, null, null);
	    cursor.moveToFirst();
	    Respuesta newComment = cursorToRespuesta(cursor);
	    cursor.close();
	    return newComment;
	  }

	  public void deleteRespuesta(String idRespuesta) {
//	    System.out.println("deleteRespuesta with id: " + idRespuesta);
	    database.delete(MySQLiteHelper.TABLA_RESPUESTAS, MySQLiteHelper.COLUMN_RESPUESTA_ID
	        + " = '" + idRespuesta + "'", null);
	  }

	  
	  public void deleteRespuestaEquipoTipo(String idEquipo, String tipo) {
		    database.delete(MySQLiteHelper.TABLA_RESPUESTAS, MySQLiteHelper.COLUMN_EQUIPO_ID_R_FK
		        + " = '" + idEquipo + "' and " + MySQLiteHelper.COLUMN_TIPO  + " = '" + tipo + "'", null);
		  }

	  public void updateRespuesta(Respuesta respuesta) {
//		    System.out.println("updateRespuesta with id: " + respuesta.getId());
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_NOMBRE, respuesta.getNombre());
		    values.put(MySQLiteHelper.COLUMN_TIPO, respuesta.getTipo());
		    values.put(MySQLiteHelper.COLUMN_EQUIPO_ID_R_FK, respuesta.getIdEquipo());
		    database.update(MySQLiteHelper.TABLA_RESPUESTAS, values,  MySQLiteHelper.COLUMN_RESPUESTA_ID + " = '" + respuesta.getId() + "'", null);
	  }
	  
	  public ArrayList<Respuesta> getAllRespuestas() {
	    ArrayList<Respuesta> respuestas = new ArrayList<Respuesta>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLA_RESPUESTAS,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Respuesta respuesta = cursorToRespuesta(cursor);
	      respuestas.add(respuesta);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return respuestas;
	  }

	  private Respuesta cursorToRespuesta(Cursor cursor) {
	    Respuesta respuesta = new Respuesta();
	    respuesta.setId(cursor.getString(0));
	    respuesta.setNombre(cursor.getString(1));
	    respuesta.setTipo(cursor.getString(2));
	    respuesta.setIdEquipo(cursor.getString(3));
	    respuesta.setTipoVoz(cursor.getString(4));
	    return respuesta;
	  }
	  
	  public ArrayList<String> getAllNombreRespuestas() {
		    ArrayList<String> respuestas = new ArrayList<String>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLA_RESPUESTAS,
		        allColumns, null, null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Respuesta respuesta = cursorToRespuesta(cursor);
		      respuestas.add(respuesta.getNombre());
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return respuestas;
		  }
	  
		public ArrayList<Respuesta> getRespuestasEquipo(Respuesta respuestaEquipo) {
			ArrayList<Respuesta> respuestas = new ArrayList<Respuesta>();

			Cursor cursor = database.query(MySQLiteHelper.TABLA_RESPUESTAS,
					allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID_R_FK + " = '" + respuestaEquipo.getIdEquipo() + "'", null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Respuesta respuesta = cursorToRespuesta(cursor);
				respuestas.add(respuesta);
				cursor.moveToNext();
			}
			cursor.close();
			return respuestas;
		}

	public Respuesta getRespuestaId(String idRespuesta) {
		ArrayList<Respuesta> respuestas = new ArrayList<Respuesta>();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_RESPUESTAS,
				allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID + " = '" + idRespuesta + "'", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Respuesta respuesta = cursorToRespuesta(cursor);
			respuestas.add(respuesta);
			cursor.moveToNext();
		}
		cursor.close();
		if(respuestas.size() > 0) {
			return respuestas.get(0);
		}else{
			return null;
		}
	}
}
