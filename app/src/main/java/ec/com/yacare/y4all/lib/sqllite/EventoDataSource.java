package ec.com.yacare.y4all.lib.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ec.com.yacare.y4all.lib.dto.Evento;

public class EventoDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_EVENTO_ID,
			MySQLiteHelper.COLUMN_ORIGEN, MySQLiteHelper.COLUMN_FECHA,  MySQLiteHelper.COLUMN_ESTADO,  MySQLiteHelper.COLUMN_ID_GRABACION ,
			MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK, MySQLiteHelper.COLUMN_TIPO_EVENTO, MySQLiteHelper.COLUMN_MENSAJE, MySQLiteHelper.COLUMN_COMANDO};

	public EventoDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Evento createEvento(Evento evento) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_EVENTO_ID, evento.getId());
		values.put(MySQLiteHelper.COLUMN_ORIGEN, evento.getOrigen());
		values.put(MySQLiteHelper.COLUMN_FECHA, evento.getFecha());
		values.put(MySQLiteHelper.COLUMN_ESTADO, evento.getEstado());
		values.put(MySQLiteHelper.COLUMN_ID_GRABACION, evento.getIdGrabado());
		values.put(MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK, evento.getIdEquipo());
		values.put(MySQLiteHelper.COLUMN_TIPO_EVENTO, evento.getTipoEvento());
		values.put(MySQLiteHelper.COLUMN_MENSAJE, evento.getMensaje());
		values.put(MySQLiteHelper.COLUMN_COMANDO, evento.getComando());
		database.insert(MySQLiteHelper.TABLA_EVENTOS, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, MySQLiteHelper.COLUMN_EVENTO_ID + " = '" + evento.getId() + "'"  , null,
				null, null, null);
		cursor.moveToFirst();
		Evento newEvento = cursorToEvento(cursor);
		cursor.close();
		return newEvento;
	}

	public void deleteEvento(String idEvento) {
//		System.out.println("deleteEvento with id: " + idEvento);
		database.delete(MySQLiteHelper.TABLA_EVENTOS, MySQLiteHelper.COLUMN_EVENTO_ID
				+ " = '" + idEvento + "'", null);
	}

	public void updateEvento(Evento evento) {
//		System.out.println("updateEvento with id: " + evento.getId());
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ORIGEN, evento.getOrigen());
		values.put(MySQLiteHelper.COLUMN_FECHA, evento.getFecha());
		values.put(MySQLiteHelper.COLUMN_ESTADO, evento.getEstado());
		values.put(MySQLiteHelper.COLUMN_ID_GRABACION, evento.getIdGrabado());
		values.put(MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK, evento.getIdEquipo());
		values.put(MySQLiteHelper.COLUMN_TIPO_EVENTO, evento.getTipoEvento());
		values.put(MySQLiteHelper.COLUMN_MENSAJE, evento.getMensaje());
		values.put(MySQLiteHelper.COLUMN_COMANDO, evento.getComando());
		database.update(MySQLiteHelper.TABLA_EVENTOS, values, MySQLiteHelper.COLUMN_EVENTO_ID + " = '" + evento.getId() + "'", null);
	}

	public ArrayList<Evento> getAllEventos() {
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, null, null, null, null, MySQLiteHelper.COLUMN_FECHA + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			eventos.add(evento);
			cursor.moveToNext();
		}
		cursor.close();
		return eventos;
	}

	private Evento cursorToEvento(Cursor cursor) {
		Evento evento = new Evento();
		evento.setId(cursor.getString(0));
		evento.setOrigen(cursor.getString(1));
		evento.setFecha(cursor.getString(2));
		evento.setEstado(cursor.getString(3));
		evento.setIdGrabado(cursor.getString(4));
		evento.setIdEquipo(cursor.getString(5));
		evento.setTipoEvento(cursor.getString(6));
		evento.setMensaje(cursor.getString(7));
		evento.setComando(cursor.getString(8));
		return evento;
	}

	public Evento getUltimaEvento(String idEquipo) {
		Evento evento = null;
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns,  MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK + " = '" + idEquipo + "'", null, null, null, MySQLiteHelper.COLUMN_FECHA + " DESC LIMIT 1");
		cursor.moveToFirst();
		if(cursor != null && cursor.getCount() > 0) {
			evento = cursorToEvento(cursor);
			evento = cursorToEvento(cursor);
			cursor.close();
		}
		return evento;
	}

	public ArrayList<Evento> getPaginaEventos(int limit, int offset) {
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, null, null, null, null,MySQLiteHelper.COLUMN_FECHA + " DESC", limit + "," + offset);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			eventos.add(evento);
			cursor.moveToNext();
		}
		cursor.close();
		return eventos;
	}
	public ArrayList<Evento> getPaginaEventosFoto(int limit, int offset, String idEquipo) {
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns,  MySQLiteHelper.COLUMN_TIPO_EVENTO + " in ('TIMBRAR','BUZON') and " + MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK + " = '" + idEquipo + "'", null, null, null,MySQLiteHelper.COLUMN_FECHA + " DESC", limit + "," + offset);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			eventos.add(evento);
			cursor.moveToNext();
		}
		cursor.close();
		return eventos;
	}

	public ArrayList<Evento> getPaginaEventosTipoEvento( int limit, int offset, String tipoEvento,String idEquipo) {
		ArrayList<Evento> eventos = new ArrayList<Evento>();

		Log.d("getPaginaEventosTipoEvento", "LIMIT: " +limit + "OFFSET " + offset );
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, MySQLiteHelper.COLUMN_TIPO_EVENTO + " in " + tipoEvento + " and " + MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK + " = '" + idEquipo + "'", null, null, null,MySQLiteHelper.COLUMN_FECHA + " DESC", limit + "," + offset);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			eventos.add(evento);
			cursor.moveToNext();
		}
		cursor.close();
		return eventos;
	}

	public ArrayList<Evento> getPaginaBuzon(int limit, int offset, String tipoEvento) {
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, MySQLiteHelper.COLUMN_TIPO_EVENTO + " = 'BUZON'", null, null, null,null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			eventos.add(evento);
			cursor.moveToNext();
		}
		cursor.close();
		return eventos;
	}

	public ArrayList<Evento> getEventosEquipoHoy(String idEquipo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK + " = '" + idEquipo + "' and " + MySQLiteHelper.COLUMN_FECHA + " LIKE '" + dateFormat.format(date) + "%'" + "and " + MySQLiteHelper.COLUMN_TIPO_EVENTO + " IN ('BUZON', 'TIMBRAR')"
						, null, null, null,  MySQLiteHelper.COLUMN_FECHA + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			eventos.add(evento);
			cursor.moveToNext();
		}
		cursor.close();
		return eventos;
	}

	public String getIdEventosEquipoHoy(String idEquipo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		String lista = "";
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK + " = '" + idEquipo + "' and " + MySQLiteHelper.COLUMN_FECHA + " LIKE '" + dateFormat.format(date) + "%'"
				, null, null, null,  MySQLiteHelper.COLUMN_FECHA + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			lista = lista + "'" + evento.getId() + "',";
			cursor.moveToNext();
		}
		cursor.close();
		if(!lista.equals("")) {
			lista = lista.substring(0, lista.length() - 1);
			lista = "(" + lista + ")";
		}

		Log.d("getIdEventosEquipoHoy","  getIdEventosEquipoHoy " + lista);
		return lista;
	}

	public void deleteEventoMayorHoy() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date date = new Date();
		database.delete(MySQLiteHelper.TABLA_EVENTOS, MySQLiteHelper.COLUMN_FECHA + " > '" + dateFormat.format(date) + "'", null);
	}
	public ArrayList<Evento> getEventosEquipoBuzonHoy(String idEquipo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK + " = '" + idEquipo + "' and " + MySQLiteHelper.COLUMN_FECHA + " LIKE '" + dateFormat.format(date) + "%'" + "and " + MySQLiteHelper.COLUMN_TIPO_EVENTO + " = 'BUZON'"
				, null, null, null,  MySQLiteHelper.COLUMN_FECHA + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			eventos.add(evento);
			cursor.moveToNext();
		}
		cursor.close();
		return eventos;
	}

	public ArrayList<Evento> getEventosEquipoSensorHoy(String idEquipo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID_T_FK + " = '" + idEquipo + "' and " + MySQLiteHelper.COLUMN_FECHA + " LIKE '" + dateFormat.format(date) + "%'" + "and " + MySQLiteHelper.COLUMN_TIPO_EVENTO + " IN ('PUERTA', 'MENSAJE', 'APERTURA')"
				, null, null, null,  MySQLiteHelper.COLUMN_FECHA + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Evento evento = cursorToEvento(cursor);
			eventos.add(evento);
			cursor.moveToNext();
		}
		cursor.close();
		return eventos;
	}
	public Evento getEventoId(Evento eventoEquipo) {
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EVENTOS,
				allColumns, MySQLiteHelper.COLUMN_EVENTO_ID + " = '" + eventoEquipo.getId() + "'", null, null, null, null);
		Evento evento = null;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			evento = cursorToEvento(cursor);
			cursor.moveToNext();
		}
		cursor.close();
		return evento;
	} 
	
	
	public long getTotal() {
		return DatabaseUtils.queryNumEntries(database, MySQLiteHelper.TABLA_EVENTOS);
	}


	public long getTotalDia() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();

		return DatabaseUtils.queryNumEntries(database, MySQLiteHelper.TABLA_EVENTOS,
				MySQLiteHelper.COLUMN_FECHA + " LIKE ? and "  +MySQLiteHelper.COLUMN_MENSAJE + " = 'S' and " + MySQLiteHelper.COLUMN_TIPO_EVENTO + " = 'TIMBRAR'", new String[]{dateFormat.format(date) + "%"});
	}
}
