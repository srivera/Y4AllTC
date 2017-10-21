package ec.com.yacare.y4all.lib.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ec.com.yacare.y4all.lib.dto.ProgramacionLuces;


public class ProgramacionDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_PROG_ID,
			MySQLiteHelper.COLUMN_PROG_ID_ROUTER, MySQLiteHelper.COLUMN_PROG_ID_ZONA,
			MySQLiteHelper.COLUMN_PROG_NOMBRE, MySQLiteHelper.COLUMN_PROG_ACCION,MySQLiteHelper.COLUMN_PROG_HORA,
			MySQLiteHelper.COLUMN_PROG_DURACION, MySQLiteHelper.COLUMN_PROG_DIAS
	};

	public ProgramacionDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public ProgramacionLuces createProgramacion(ProgramacionLuces programacionLuces) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PROG_ID, programacionLuces.getId());
		values.put(MySQLiteHelper.COLUMN_PROG_ID_ROUTER, programacionLuces.getIdRouter());
		values.put(MySQLiteHelper.COLUMN_PROG_ID_ZONA, programacionLuces.getIdZona());
		values.put(MySQLiteHelper.COLUMN_PROG_NOMBRE, programacionLuces.getNombre());
		values.put(MySQLiteHelper.COLUMN_PROG_ACCION, programacionLuces.getAccion());
		values.put(MySQLiteHelper.COLUMN_PROG_HORA, programacionLuces.getHoraInicio());
		values.put(MySQLiteHelper.COLUMN_PROG_DURACION, programacionLuces.getDuracion());
		values.put(MySQLiteHelper.COLUMN_PROG_DIAS, programacionLuces.getDias());

		database.insert(MySQLiteHelper.TABLA_PROG_LUCES, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLA_PROG_LUCES,
				allColumns, MySQLiteHelper.COLUMN_PROG_ID + " = '" + programacionLuces.getId() + "'"  , null,
				null, null, null);
		cursor.moveToFirst();
		ProgramacionLuces newProg = cursorToProgramacion(cursor);
		cursor.close();
		return newProg;
	}

	public void updateProgramacion(ProgramacionLuces programacionLuces) {
//		System.out.println("updateProgramacion with id: " + programacionLuces.getId());
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PROG_ID_ROUTER, programacionLuces.getIdRouter());
		values.put(MySQLiteHelper.COLUMN_PROG_ID_ZONA, programacionLuces.getIdZona());
		values.put(MySQLiteHelper.COLUMN_PROG_NOMBRE, programacionLuces.getNombre());
		values.put(MySQLiteHelper.COLUMN_PROG_ACCION, programacionLuces.getAccion());
		values.put(MySQLiteHelper.COLUMN_PROG_HORA, programacionLuces.getHoraInicio());
		values.put(MySQLiteHelper.COLUMN_PROG_DURACION, programacionLuces.getDuracion());
		values.put(MySQLiteHelper.COLUMN_PROG_DIAS, programacionLuces.getDias());

		database.update(MySQLiteHelper.TABLA_PROG_LUCES, values, MySQLiteHelper.COLUMN_PROG_ID + " = '" + programacionLuces.getId() + "'", null);
	}

	public void deleteProgramacionById(String id) {
		database.delete(MySQLiteHelper.TABLA_PROG_LUCES, MySQLiteHelper.COLUMN_PROG_ID + " = '" + id + "'", null);
	}

	public void deleteProgramacionByImacZona(String imac, String idZona) {
		database.delete(MySQLiteHelper.TABLA_PROG_LUCES, MySQLiteHelper.COLUMN_PROG_ID_ZONA + " = '" + idZona + "'  and " + MySQLiteHelper.COLUMN_PROG_ID_ROUTER + " = '" + imac + "' ", null);
	}

	public ArrayList<ProgramacionLuces> getAllProgByRouter(String idRouter) {
		ArrayList<ProgramacionLuces> progs = new ArrayList<ProgramacionLuces>();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_PROG_LUCES,
				allColumns, MySQLiteHelper.COLUMN_PROG_ID_ROUTER  + " = '"+ idRouter + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ProgramacionLuces prog = cursorToProgramacion(cursor);
			progs.add(prog);
			cursor.moveToNext();
		}
		cursor.close();
		return progs;
	}

	public ArrayList<ProgramacionLuces> getAllProgByRouterZona(String idRouter,  String idZona) {
		ArrayList<ProgramacionLuces> progs = new ArrayList<ProgramacionLuces>();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_PROG_LUCES,
				allColumns, MySQLiteHelper.COLUMN_PROG_ID_ZONA + " = '" + idZona + "'  and " +  MySQLiteHelper.COLUMN_PROG_ID_ROUTER  + " = '"+ idRouter + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ProgramacionLuces prog = cursorToProgramacion(cursor);
			progs.add(prog);
			cursor.moveToNext();
		}
		cursor.close();
		return progs;
	}

	private ProgramacionLuces cursorToProgramacion(Cursor cursor) {
		ProgramacionLuces programacionLuces = new ProgramacionLuces();
		programacionLuces.setId(cursor.getString(0));
		programacionLuces.setIdRouter(cursor.getString(1));
		programacionLuces.setIdZona(cursor.getString(2));
		programacionLuces.setNombre(cursor.getString(3));
		programacionLuces.setAccion(cursor.getString(4));
		programacionLuces.setHoraInicio(cursor.getString(5));
		programacionLuces.setDuracion(cursor.getString(6));
		programacionLuces.setDias(cursor.getString(7));
		return programacionLuces;
	}
	

	
	public ProgramacionLuces getProgId(String id) {
		ArrayList<ProgramacionLuces> progs = new ArrayList<ProgramacionLuces>();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_PROG_LUCES,
				allColumns, MySQLiteHelper.COLUMN_PROG_ID + " = '"+ id +"'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ProgramacionLuces prog = cursorToProgramacion(cursor);
			progs.add(prog);
			cursor.moveToNext();
		}
		cursor.close();
		if(progs.size() > 0){
			return progs.get(0);
		}else{
			return null;
		}

	}
}
