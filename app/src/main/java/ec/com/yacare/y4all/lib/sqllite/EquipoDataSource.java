package ec.com.yacare.y4all.lib.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;

public class EquipoDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_EQUIPO_ID,
			MySQLiteHelper.COLUMN_NUMERO_SERIE, MySQLiteHelper.COLUMN_TIPO_EQUIPO,
			MySQLiteHelper.COLUMN_NOMBRE_EQUIPO, MySQLiteHelper.COLUMN_FECHA_FABRICACION,
			MySQLiteHelper.COLUMN_ESTADO_EQUIPO, MySQLiteHelper.COLUMN_TIPO_RED,
			MySQLiteHelper.COLUMN_NOMBRE_WIFI, MySQLiteHelper.COLUMN_CLAVE_WIFI,
			MySQLiteHelper.COLUMN_IP_PUBLICA, MySQLiteHelper.COLUMN_IP_LOCAL,
			MySQLiteHelper.COLUMN_TIEMPO_ESPERA_BUZON, MySQLiteHelper.COLUMN_TIEMPO_GRABAR_BUZON,
			MySQLiteHelper.COLUMN_TONO, MySQLiteHelper.COLUMN_FECHA_REGISTRO,
			MySQLiteHelper.COLUMN_EQUIPO_PADRE_ID, MySQLiteHelper.COLUMN_CLAVE_PRIVADA,
			MySQLiteHelper.COLUMN_NOMBRE_HOTSPOT, MySQLiteHelper.COLUMN_CLAVE_HOTSPOT,
			MySQLiteHelper.COLUMN_EQ_ID_CUENTA, MySQLiteHelper.COLUMN_LUZ,
			MySQLiteHelper.COLUMN_LUZ_WIFI, MySQLiteHelper.COLUMN_BUZON_ACTIVO,
			MySQLiteHelper.COLUMN_SENSOR, MySQLiteHelper.COLUMN_PUERTA,
			MySQLiteHelper.COLUMN_MENSAJE_INICIAL, MySQLiteHelper.COLUMN_SOCKET_COMANDO,
			MySQLiteHelper.COLUMN_PUERTO_ACTIVO, MySQLiteHelper.COLUMN_ZONAS,
			MySQLiteHelper.COLUMN_TIMBRE_EXTERNO, MySQLiteHelper.COLUMN_VERSION_FOTO,
			MySQLiteHelper.COLUMN_MODO, MySQLiteHelper.COLUMN_MENSAJE_APERTURA,
			MySQLiteHelper.COLUMN_VOZ_MENSAJE, MySQLiteHelper.COLUMN_MENSAJE_PUERTA,
			MySQLiteHelper.COLUMN_TIEMPO_ENCENDIDO_LUZ, MySQLiteHelper.COLUMN_VOLUMEN
	};

	public EquipoDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Equipo createEquipo(Equipo equipo) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_EQUIPO_ID, equipo.getId());
		values.put(MySQLiteHelper.COLUMN_NUMERO_SERIE, equipo.getNumeroSerie());
		values.put(MySQLiteHelper.COLUMN_TIPO_EQUIPO, equipo.getTipoEquipo());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_EQUIPO, equipo.getNombreEquipo());
		values.put(MySQLiteHelper.COLUMN_FECHA_FABRICACION, equipo.getFechaFabricacion());
		values.put(MySQLiteHelper.COLUMN_ESTADO_EQUIPO, equipo.getEstadoEquipo());
		values.put(MySQLiteHelper.COLUMN_TIPO_RED, equipo.getTipoRed());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_WIFI, equipo.getNombreWiFi());
		values.put(MySQLiteHelper.COLUMN_CLAVE_WIFI, equipo.getClaveWiFi());
		values.put(MySQLiteHelper.COLUMN_IP_PUBLICA, equipo.getIpPublica());
		values.put(MySQLiteHelper.COLUMN_IP_LOCAL, equipo.getIpLocal());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_ESPERA_BUZON, equipo.getTiempoEsperaBuzon());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_GRABAR_BUZON, equipo.getTiempoGrabarBuzon());
		values.put(MySQLiteHelper.COLUMN_TONO, equipo.getTono());
		values.put(MySQLiteHelper.COLUMN_FECHA_REGISTRO, equipo.getFechaRegistro());
		values.put(MySQLiteHelper.COLUMN_EQUIPO_PADRE_ID, equipo.getIdEquipoPadre());
		values.put(MySQLiteHelper.COLUMN_CLAVE_PRIVADA, equipo.getClavePrivada());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_HOTSPOT, equipo.getNombreRedHotSpot());
		values.put(MySQLiteHelper.COLUMN_CLAVE_HOTSPOT, equipo.getClaveHotSpot());
		values.put(MySQLiteHelper.COLUMN_EQ_ID_CUENTA, equipo.getIdCuenta());
		values.put(MySQLiteHelper.COLUMN_LUZ, equipo.getLuz());
		values.put(MySQLiteHelper.COLUMN_LUZ_WIFI, equipo.getLuzWifi());
		values.put(MySQLiteHelper.COLUMN_BUZON_ACTIVO, equipo.getBuzon());
		values.put(MySQLiteHelper.COLUMN_SENSOR, equipo.getSensor());
		values.put(MySQLiteHelper.COLUMN_PUERTA, equipo.getPuerta());
		values.put(MySQLiteHelper.COLUMN_MENSAJE_INICIAL, equipo.getMensajeInicial());
		values.put(MySQLiteHelper.COLUMN_SOCKET_COMANDO, equipo.getSocketComando());
		values.put(MySQLiteHelper.COLUMN_PUERTO_ACTIVO, equipo.getPuertoActivo());
		values.put(MySQLiteHelper.COLUMN_ZONAS, equipo.getZonas());
		values.put(MySQLiteHelper.COLUMN_TIMBRE_EXTERNO, equipo.getTimbreExterno());
		values.put(MySQLiteHelper.COLUMN_VERSION_FOTO, equipo.getVersionFoto());
		values.put(MySQLiteHelper.COLUMN_MODO, equipo.getModo());
		values.put(MySQLiteHelper.COLUMN_MENSAJE_APERTURA, equipo.getMensajeApertura());
		values.put(MySQLiteHelper.COLUMN_VOZ_MENSAJE, equipo.getVozMensaje());
		values.put(MySQLiteHelper.COLUMN_MENSAJE_PUERTA, equipo.getMensajePuerta());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_ENCENDIDO_LUZ, equipo.getTiempoEncendidoLuz());
		values.put(MySQLiteHelper.COLUMN_VOLUMEN, equipo.getVolumen());

		database.insert(MySQLiteHelper.TABLA_EQUIPO, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EQUIPO,
				allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID + " = '" + equipo.getId() + "'"  , null,
				null, null, null);
		cursor.moveToFirst();
		Equipo newEquipo = cursorToEquipo(cursor);
		cursor.close();
		return newEquipo;
	}

	public void deleteEquipo(String idEquipo) {
//		System.out.println("deleteEquipo with id: " + idEquipo);
		database.delete(MySQLiteHelper.TABLA_EQUIPO, MySQLiteHelper.COLUMN_EQUIPO_ID
				+ " = '" + idEquipo + "'", null);
	}

	public void updateEquipo(Equipo equipo) {
//		System.out.println("updateEquipo with id: " + equipo.getId());
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_EQUIPO_ID, equipo.getId());
		values.put(MySQLiteHelper.COLUMN_NUMERO_SERIE, equipo.getNumeroSerie());
		values.put(MySQLiteHelper.COLUMN_TIPO_EQUIPO, equipo.getTipoEquipo());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_EQUIPO, equipo.getNombreEquipo());
		values.put(MySQLiteHelper.COLUMN_FECHA_FABRICACION, equipo.getFechaFabricacion());
		values.put(MySQLiteHelper.COLUMN_ESTADO_EQUIPO, equipo.getEstadoEquipo());
		values.put(MySQLiteHelper.COLUMN_TIPO_RED, equipo.getTipoRed());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_WIFI, equipo.getNombreWiFi());
		values.put(MySQLiteHelper.COLUMN_CLAVE_WIFI, equipo.getClaveWiFi());
		values.put(MySQLiteHelper.COLUMN_IP_PUBLICA, equipo.getIpPublica());
		values.put(MySQLiteHelper.COLUMN_IP_LOCAL, equipo.getIpLocal());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_ESPERA_BUZON, equipo.getTiempoEsperaBuzon());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_GRABAR_BUZON, equipo.getTiempoGrabarBuzon());
		values.put(MySQLiteHelper.COLUMN_TONO, equipo.getTono());
		values.put(MySQLiteHelper.COLUMN_FECHA_REGISTRO, equipo.getFechaRegistro());
		values.put(MySQLiteHelper.COLUMN_EQUIPO_PADRE_ID, equipo.getIdEquipoPadre());
		values.put(MySQLiteHelper.COLUMN_CLAVE_PRIVADA, equipo.getClavePrivada());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_HOTSPOT, equipo.getNombreRedHotSpot());
		values.put(MySQLiteHelper.COLUMN_CLAVE_HOTSPOT, equipo.getClaveHotSpot());
		values.put(MySQLiteHelper.COLUMN_EQ_ID_CUENTA, equipo.getIdCuenta());
		values.put(MySQLiteHelper.COLUMN_LUZ, equipo.getLuz());
		values.put(MySQLiteHelper.COLUMN_LUZ_WIFI, equipo.getLuzWifi());
		values.put(MySQLiteHelper.COLUMN_BUZON_ACTIVO, equipo.getBuzon());
		values.put(MySQLiteHelper.COLUMN_SENSOR, equipo.getSensor());
		values.put(MySQLiteHelper.COLUMN_PUERTA, equipo.getPuerta());
		values.put(MySQLiteHelper.COLUMN_MENSAJE_INICIAL, equipo.getMensajeInicial());
		values.put(MySQLiteHelper.COLUMN_SOCKET_COMANDO, equipo.getSocketComando());
		values.put(MySQLiteHelper.COLUMN_PUERTO_ACTIVO, equipo.getPuertoActivo());
		values.put(MySQLiteHelper.COLUMN_ZONAS, equipo.getZonas());
		values.put(MySQLiteHelper.COLUMN_TIMBRE_EXTERNO, equipo.getTimbreExterno());
		values.put(MySQLiteHelper.COLUMN_VERSION_FOTO, equipo.getVersionFoto());
		values.put(MySQLiteHelper.COLUMN_MODO, equipo.getModo());
		values.put(MySQLiteHelper.COLUMN_MENSAJE_APERTURA, equipo.getMensajeApertura());
		values.put(MySQLiteHelper.COLUMN_VOZ_MENSAJE, equipo.getVozMensaje());
		values.put(MySQLiteHelper.COLUMN_MENSAJE_PUERTA, equipo.getMensajePuerta());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_ENCENDIDO_LUZ, equipo.getTiempoEncendidoLuz());
		values.put(MySQLiteHelper.COLUMN_VOLUMEN, equipo.getVolumen());
		database.update(MySQLiteHelper.TABLA_EQUIPO, values,  MySQLiteHelper.COLUMN_EQUIPO_ID + " = '" + equipo.getId() + "'", null);
	}

	public void updateEquipo(Equipo equipo, String idAnterior) {
//		System.out.println("updateEquipo with id: " + equipo.getId());
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_EQUIPO_ID, equipo.getId());
		values.put(MySQLiteHelper.COLUMN_NUMERO_SERIE, equipo.getNumeroSerie());
		values.put(MySQLiteHelper.COLUMN_TIPO_EQUIPO, equipo.getTipoEquipo());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_EQUIPO, equipo.getNombreEquipo());
		values.put(MySQLiteHelper.COLUMN_FECHA_FABRICACION, equipo.getFechaFabricacion());
		values.put(MySQLiteHelper.COLUMN_ESTADO_EQUIPO, equipo.getEstadoEquipo());
		values.put(MySQLiteHelper.COLUMN_TIPO_RED, equipo.getTipoRed());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_WIFI, equipo.getNombreWiFi());
		values.put(MySQLiteHelper.COLUMN_CLAVE_WIFI, equipo.getClaveWiFi());
		values.put(MySQLiteHelper.COLUMN_IP_PUBLICA, equipo.getIpPublica());
		values.put(MySQLiteHelper.COLUMN_IP_LOCAL, equipo.getIpLocal());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_ESPERA_BUZON, equipo.getTiempoEsperaBuzon());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_GRABAR_BUZON, equipo.getTiempoGrabarBuzon());
		values.put(MySQLiteHelper.COLUMN_TONO, equipo.getTono());
		values.put(MySQLiteHelper.COLUMN_FECHA_REGISTRO, equipo.getFechaRegistro());
		values.put(MySQLiteHelper.COLUMN_EQUIPO_PADRE_ID, equipo.getIdEquipoPadre());
		values.put(MySQLiteHelper.COLUMN_CLAVE_PRIVADA, equipo.getClavePrivada());
		values.put(MySQLiteHelper.COLUMN_NOMBRE_HOTSPOT, equipo.getNombreRedHotSpot());
		values.put(MySQLiteHelper.COLUMN_CLAVE_HOTSPOT, equipo.getClaveHotSpot());
		values.put(MySQLiteHelper.COLUMN_EQ_ID_CUENTA, equipo.getIdCuenta());
		values.put(MySQLiteHelper.COLUMN_LUZ, equipo.getLuz());
		values.put(MySQLiteHelper.COLUMN_LUZ_WIFI, equipo.getLuzWifi());
		values.put(MySQLiteHelper.COLUMN_BUZON_ACTIVO, equipo.getBuzon());
		values.put(MySQLiteHelper.COLUMN_SENSOR, equipo.getSensor());
		values.put(MySQLiteHelper.COLUMN_PUERTA, equipo.getPuerta());
		values.put(MySQLiteHelper.COLUMN_MENSAJE_INICIAL, equipo.getMensajeInicial());
		values.put(MySQLiteHelper.COLUMN_SOCKET_COMANDO, equipo.getSocketComando());
		values.put(MySQLiteHelper.COLUMN_PUERTO_ACTIVO, equipo.getPuertoActivo());
		values.put(MySQLiteHelper.COLUMN_ZONAS, equipo.getZonas());
		values.put(MySQLiteHelper.COLUMN_TIMBRE_EXTERNO, equipo.getTimbreExterno());
		values.put(MySQLiteHelper.COLUMN_VERSION_FOTO, equipo.getVersionFoto());
		values.put(MySQLiteHelper.COLUMN_MODO, equipo.getModo());
		values.put(MySQLiteHelper.COLUMN_VOZ_MENSAJE, equipo.getVozMensaje());
		values.put(MySQLiteHelper.COLUMN_MENSAJE_PUERTA, equipo.getMensajePuerta());
		values.put(MySQLiteHelper.COLUMN_TIEMPO_ENCENDIDO_LUZ, equipo.getTiempoEncendidoLuz());
		values.put(MySQLiteHelper.COLUMN_VOLUMEN, equipo.getVolumen());
		database.update(MySQLiteHelper.TABLA_EQUIPO, values,  MySQLiteHelper.COLUMN_EQUIPO_ID + " = '" + idAnterior + "'", null);
	}
	public ArrayList<Equipo> getAllEquipo() {
		ArrayList<Equipo> equipos = new ArrayList<Equipo>();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_EQUIPO,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Equipo equipo = cursorToEquipo(cursor);
			equipos.add(equipo);
			cursor.moveToNext();
		}
		cursor.close();
		return equipos;
	}

	private Equipo cursorToEquipo(Cursor cursor) {
		Equipo equipo = new Equipo();
		equipo.setId(cursor.getString(0));
		equipo.setNumeroSerie(cursor.getString(1));
		equipo.setTipoEquipo(cursor.getString(2));
		equipo.setNombreEquipo(cursor.getString(3));
		equipo.setFechaFabricacion(cursor.getString(4));
		equipo.setEstadoEquipo(cursor.getString(5));
		equipo.setTipoRed(cursor.getString(6));
		equipo.setNombreWiFi(cursor.getString(7));
		equipo.setClaveWiFi(cursor.getString(8));
		equipo.setIpPublica(cursor.getString(9));
		equipo.setIpLocal(cursor.getString(10));
		equipo.setTiempoEsperaBuzon(cursor.getInt(11));
		equipo.setTiempoGrabarBuzon(cursor.getInt(12));
		equipo.setTono(cursor.getString(13));
		equipo.setFechaRegistro(cursor.getString(14));
		equipo.setIdEquipoPadre(cursor.getString(15));
		equipo.setClavePrivada(cursor.getString(16));
		equipo.setNombreRedHotSpot(cursor.getString(17));
		equipo.setClaveHotSpot(cursor.getString(18));
		equipo.setIdCuenta(cursor.getString(19));
		equipo.setLuz(cursor.getString(20));
		equipo.setLuzWifi(cursor.getString(21));
		equipo.setBuzon(cursor.getString(22));
		equipo.setSensor(cursor.getString(23));
		equipo.setPuerta(cursor.getString(24));
		equipo.setMensajeInicial(cursor.getString(25));
		equipo.setSocketComando(cursor.getString(26));
		equipo.setPuertoActivo(cursor.getString(27));
		equipo.setZonas(cursor.getString(28));
		equipo.setTimbreExterno(cursor.getString(29));
		equipo.setVersionFoto(cursor.getString(30));
		equipo.setModo(cursor.getString(31));
		equipo.setMensajeApertura(cursor.getString(32));
		equipo.setVozMensaje(cursor.getString(33));
		equipo.setMensajePuerta(cursor.getString(34));
		equipo.setTiempoEncendidoLuz(cursor.getInt(35));
		equipo.setVolumen(cursor.getInt(36));
		return equipo;
	}
	
	public ArrayList<Equipo> getEquipoTipoEquipo(Equipo equipoBusqueda) {
		ArrayList<Equipo> equipos = new ArrayList<Equipo>();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_EQUIPO,
				allColumns, MySQLiteHelper.COLUMN_TIPO_EQUIPO + " = '" + equipoBusqueda.getTipoEquipo() + "' and " + 
						 MySQLiteHelper.COLUMN_ESTADO_EQUIPO + " = '" + EstadoDispositivoEnum.INSTALADO.getCodigo() + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Equipo equipo = cursorToEquipo(cursor);
			equipos.add(equipo);
			cursor.moveToNext();
		}
		cursor.close();
		return equipos;
	} 
	
	public ArrayList<Equipo> getEquipoEstado(Equipo equipoBusqueda) {
		ArrayList<Equipo> equipos = new ArrayList<Equipo>();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_EQUIPO,
				allColumns, MySQLiteHelper.COLUMN_ESTADO_EQUIPO + " = '" + equipoBusqueda.getEstadoEquipo() + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Equipo equipo = cursorToEquipo(cursor);
			equipos.add(equipo);
			cursor.moveToNext();
		}
		cursor.close();
		return equipos;
	} 
	
	public Equipo getEquipoNumSerie(Equipo equipoBusqueda) {
		Equipo equipo = new Equipo();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EQUIPO,
				allColumns, MySQLiteHelper.COLUMN_NUMERO_SERIE + " = '" + equipoBusqueda.getNumeroSerie() + "'", null, null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			equipo = cursorToEquipo(cursor);
		}
		cursor.close();
		return equipo;
	}


	public Equipo getEquipoId(String idEquipo) {
		Equipo equipo = new Equipo();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_EQUIPO,
				allColumns, MySQLiteHelper.COLUMN_EQUIPO_ID+ " = '" + idEquipo + "'", null, null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			equipo = cursorToEquipo(cursor);
		}
		cursor.close();
		return equipo;
	}

	public void crearColumnas() {
		Cursor cursor = null;
		try {
			cursor = database.rawQuery("SELECT * FROM " + "equipo" + " LIMIT 0", null);
			if (cursor.getColumnIndex("tiempoEncendidoLuz") != -1) {

			}else {
				database.execSQL("ALTER TABLE equipo ADD COLUMN volumen integer;");
				database.execSQL("UPDATE equipo SET volumen=1;");

				database.execSQL("ALTER TABLE equipo ADD COLUMN tiempoEncendidoLuz integer;");
				database.execSQL("UPDATE equipo SET tiempoEncendidoLuz=300000;");
			}

		} catch (SQLiteException Exp) {
		}
	}
}
