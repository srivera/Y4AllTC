package ec.com.yacare.y4all.lib.sqllite;


public class TipoEquipoDataSource {

//	// Database fields
//	private SQLiteDatabase database;
//	private MySQLiteHelper dbHelper;
//	private String[] allColumns = { MySQLiteHelper.COLUMN_TIPO_EQUIPO_ID,
//			MySQLiteHelper.COLUMN_NOMBRE_TIPO_EQUIPO, MySQLiteHelper.COLUMN_ESTADO_TIPO_EQUIPO};
//
//	public TipoEquipoDataSource(Context context) {
//		dbHelper = new MySQLiteHelper(context);
//	}
//
//	public void open() throws SQLException {
//		database = dbHelper.getWritableDatabase();
//	}
//
//	public void close() {
//		dbHelper.close();
//	}
//
//	public TipoEquipo createTipoEquipo(TipoEquipo tipoEquipo) {
//		ContentValues values = new ContentValues();
//		values.put(MySQLiteHelper.COLUMN_TIPO_EQUIPO_ID, tipoEquipo.getId());
//		values.put(MySQLiteHelper.COLUMN_NOMBRE_TIPO_EQUIPO, tipoEquipo.getNombre());
//		values.put(MySQLiteHelper.COLUMN_ESTADO_TIPO_EQUIPO, tipoEquipo.getEstado());
//		database.insert(MySQLiteHelper.TABLA_TIPO_EQUIPO, null, values);
//		Cursor cursor = database.query(MySQLiteHelper.TABLA_TIPO_EQUIPO,
//				allColumns, MySQLiteHelper.COLUMN_TIPO_EQUIPO_ID + " = '" + tipoEquipo.getId() + "'"  , null,
//				null, null, null);
//		cursor.moveToFirst();
//		TipoEquipo newTipoEquipo = cursorToTipoEquipo(cursor);
//		cursor.close();
//		return newTipoEquipo;
//	}
//
//	public void deleteTipoEquipo(String idTipoEquipo) {
//		System.out.println("Comment deleted with id: " + idTipoEquipo);
//		database.delete(MySQLiteHelper.TABLA_TIPO_EQUIPO, MySQLiteHelper.COLUMN_TIPO_EQUIPO_ID
//				+ " = '" + idTipoEquipo + "'", null);
//	}
//
//	public void updateTipoEquipo(TipoEquipo tipoEquipo) {
//		System.out.println("Comment deleted with id: " + tipoEquipo.getId());
//		ContentValues values = new ContentValues();
//		values.put(MySQLiteHelper.COLUMN_TIPO_EQUIPO_ID, tipoEquipo.getId());
//		values.put(MySQLiteHelper.COLUMN_NOMBRE_TIPO_EQUIPO, tipoEquipo.getNombre());
//		values.put(MySQLiteHelper.COLUMN_ESTADO_TIPO_EQUIPO, tipoEquipo.getEstado());
//		database.update(MySQLiteHelper.TABLA_TIPO_EQUIPO, values,  MySQLiteHelper.COLUMN_TIPO_EQUIPO_ID + " = '" + tipoEquipo.getId() + "'", null);
//	}
//
//	public ArrayList<TipoEquipo> getAllTipoEquipo() {
//		ArrayList<TipoEquipo> tipoEquipos = new ArrayList<TipoEquipo>();
//
//		Cursor cursor = database.query(MySQLiteHelper.TABLA_TIPO_EQUIPO,
//				allColumns, null, null, null, null, null);
//
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			TipoEquipo tipoEquipo = cursorToTipoEquipo(cursor);
//			tipoEquipos.add(tipoEquipo);
//			cursor.moveToNext();
//		}
//		cursor.close();
//		return tipoEquipos;
//	}
//
//	private TipoEquipo cursorToTipoEquipo(Cursor cursor) {
//		TipoEquipo tipoEquipo = new TipoEquipo();
//		tipoEquipo.setId(cursor.getString(0));
//		tipoEquipo.setNombre(cursor.getString(1));
//		tipoEquipo.setEstado(cursor.getString(2));
//		return tipoEquipo;
//	}
}
