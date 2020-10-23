package ec.com.yacare.y4all.lib.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;

public class CuentaDataSource {


	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_CUENTA_ID,
			MySQLiteHelper.COLUMN_EMAIL, MySQLiteHelper.COLUMN_TIPO_CUENTA, MySQLiteHelper.COLUMN_PRIMER_NOMBRE,  MySQLiteHelper.COLUMN_SEGUNDO_NOMBRE,
			MySQLiteHelper.COLUMN_PRIMER_APELLIDO, MySQLiteHelper.COLUMN_SEGUNDO_APELLIDO,  MySQLiteHelper.COLUMN_RAZON_SOCIAL,  MySQLiteHelper.COLUMN_CLAVE,
			MySQLiteHelper.COLUMN_ID_MSJ_PUSH, MySQLiteHelper.COLUMN_ESTADO_CUENTA,  MySQLiteHelper.COLUMN_FECHA_CUENTA, MySQLiteHelper.COLUMN_TELEFONO_CASA,
			MySQLiteHelper.COLUMN_TELEFONO_OFIC,  MySQLiteHelper.COLUMN_TELEFONO_CELULAR,  MySQLiteHelper.COLUMN_CARGO,  MySQLiteHelper.COLUMN_IP};

	public CuentaDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Cuenta createCuenta(Cuenta cuenta) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CUENTA_ID, cuenta.getId());
		values.put(MySQLiteHelper.COLUMN_EMAIL, cuenta.getEmail());
		values.put(MySQLiteHelper.COLUMN_TIPO_CUENTA, cuenta.getTipoCuenta());
		values.put(MySQLiteHelper.COLUMN_PRIMER_NOMBRE, cuenta.getPrimerNombre());
		values.put(MySQLiteHelper.COLUMN_SEGUNDO_NOMBRE, cuenta.getSegundoNombre());
		values.put(MySQLiteHelper.COLUMN_PRIMER_APELLIDO, cuenta.getPrimerApellido());
		values.put(MySQLiteHelper.COLUMN_SEGUNDO_APELLIDO, cuenta.getSegundoApellido());
		values.put(MySQLiteHelper.COLUMN_RAZON_SOCIAL, cuenta.getRazonSocial());
		values.put(MySQLiteHelper.COLUMN_CLAVE, cuenta.getClave());
		values.put(MySQLiteHelper.COLUMN_ID_MSJ_PUSH, cuenta.getIdMensajePush());
		values.put(MySQLiteHelper.COLUMN_ESTADO_CUENTA, cuenta.getEstadoCuenta());
		values.put(MySQLiteHelper.COLUMN_FECHA_CUENTA, cuenta.getFechaCuenta());
		values.put(MySQLiteHelper.COLUMN_TELEFONO_CASA, cuenta.getTelefonoCasa());
		values.put(MySQLiteHelper.COLUMN_TELEFONO_OFIC, cuenta.getTelefonoOficina());
		values.put(MySQLiteHelper.COLUMN_TELEFONO_CELULAR, cuenta.getTelefonoCelular());
		values.put(MySQLiteHelper.COLUMN_CARGO, cuenta.getCargo());
		values.put(MySQLiteHelper.COLUMN_IP, cuenta.getIp());

		database.insert(MySQLiteHelper.TABLA_CUENTA, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLA_CUENTA,
				allColumns, MySQLiteHelper.COLUMN_CUENTA_ID + " = '" + cuenta.getId() + "'"  , null,
				null, null, null);
		cursor.moveToFirst();
		Cuenta newCuenta = cursorToCuenta(cursor);
		cursor.close();
		return newCuenta;
	}

	public void deleteCuenta(String idCuenta) {
//		System.out.println("deleteCuenta with id: " + idCuenta);
		database.delete(MySQLiteHelper.TABLA_CUENTA, MySQLiteHelper.COLUMN_CUENTA_ID
				+ " = '" + idCuenta + "'", null);
	}

	public void updateCuenta(Cuenta cuenta) {
//		System.out.println("updateCuenta with id: " + cuenta.getId());
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_EMAIL, cuenta.getEmail());
		values.put(MySQLiteHelper.COLUMN_TIPO_CUENTA, cuenta.getTipoCuenta());
		values.put(MySQLiteHelper.COLUMN_PRIMER_NOMBRE, cuenta.getPrimerNombre());
		values.put(MySQLiteHelper.COLUMN_SEGUNDO_NOMBRE, cuenta.getSegundoNombre());
		values.put(MySQLiteHelper.COLUMN_PRIMER_APELLIDO, cuenta.getPrimerApellido());
		values.put(MySQLiteHelper.COLUMN_SEGUNDO_APELLIDO, cuenta.getSegundoApellido());
		values.put(MySQLiteHelper.COLUMN_RAZON_SOCIAL, cuenta.getRazonSocial());
		values.put(MySQLiteHelper.COLUMN_CLAVE, cuenta.getClave());
		values.put(MySQLiteHelper.COLUMN_ID_MSJ_PUSH, cuenta.getIdMensajePush());
		values.put(MySQLiteHelper.COLUMN_ESTADO_CUENTA, cuenta.getEstadoCuenta());
		values.put(MySQLiteHelper.COLUMN_FECHA_CUENTA, cuenta.getFechaCuenta());
		values.put(MySQLiteHelper.COLUMN_TELEFONO_CASA, cuenta.getTelefonoCasa());
		values.put(MySQLiteHelper.COLUMN_TELEFONO_OFIC, cuenta.getTelefonoOficina());
		values.put(MySQLiteHelper.COLUMN_TELEFONO_CELULAR, cuenta.getTelefonoCelular());
		values.put(MySQLiteHelper.COLUMN_CARGO, cuenta.getCargo());
		values.put(MySQLiteHelper.COLUMN_IP, cuenta.getIp());
		database.update(MySQLiteHelper.TABLA_CUENTA, values,  MySQLiteHelper.COLUMN_CUENTA_ID + " = '" + cuenta.getId() + "'", null);
	}

	private Cuenta cursorToCuenta(Cursor cursor) {
		Cuenta cuenta = new Cuenta();
		cuenta.setId(cursor.getString(0));
		cuenta.setEmail(cursor.getString(1));
		cuenta.setTipoCuenta(cursor.getString(2));
		cuenta.setPrimerNombre(cursor.getString(3));
		cuenta.setSegundoNombre(cursor.getString(4));
		cuenta.setPrimerApellido(cursor.getString(5));
		cuenta.setSegundoApellido(cursor.getString(6));
		cuenta.setRazonSocial(cursor.getString(7));
		cuenta.setClave(cursor.getString(8));
		cuenta.setIdMensajePush(cursor.getString(9));
		cuenta.setEstadoCuenta(cursor.getString(10));
		cuenta.setFechaCuenta(cursor.getString(11));
		cuenta.setTelefonoCasa(cursor.getString(12));
		cuenta.setTelefonoOficina(cursor.getString(13));
		cuenta.setTelefonoCelular(cursor.getString(14));
		cuenta.setCargo(cursor.getString(15));
		cuenta.setIp(cursor.getString(16));
		return cuenta;
	}

	public Cuenta getCuentaCliente() {
		Cuenta cuenta = new Cuenta();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_CUENTA,
				allColumns, null, null, null, null,  null);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			cuenta = cursorToCuenta(cursor);
			cursor.close();
			return cuenta;
		}else{
			cursor.close();
			return null;
		}
	}
	
	public Cuenta getCuentaIntercom() {
		Cuenta cuenta = new Cuenta();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_CUENTA,
				allColumns,  MySQLiteHelper.COLUMN_TIPO_CUENTA + " = '" + YACSmartProperties.getInstance().getMessageForKey("tipo.cuenta.intercomunicador") + "'", null, null, null,  null);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			cuenta = cursorToCuenta(cursor);
			cursor.close();
			return cuenta;
		}else{
			cursor.close();
			return null;
		}
	}
	
	public Cuenta getCuentaIntercomEmail(String email) {
		Cuenta cuenta = new Cuenta();

		Cursor cursor = database.query(MySQLiteHelper.TABLA_CUENTA,
				allColumns,  MySQLiteHelper.COLUMN_TIPO_CUENTA + " = '" + YACSmartProperties.getInstance().getMessageForKey("tipo.cuenta.intercomunicador") + "' and "
				+ MySQLiteHelper.COLUMN_EMAIL  + " = '" + email + "' ", 
				
				null, null, null,  null);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			cuenta = cursorToCuenta(cursor);
			cursor.close();
			return cuenta;
		}else{
			cursor.close();
			return null;
		}
	}	
	public Cuenta getCuentaId(Cuenta cuentaBusqueda) {
		Cuenta cuenta = new Cuenta();
		Cursor cursor = database.query(MySQLiteHelper.TABLA_CUENTA,
				allColumns, MySQLiteHelper.COLUMN_CUENTA_ID + " = '" + cuentaBusqueda.getId() + "'", null, null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			cuenta = cursorToCuenta(cursor);
		}
		cursor.close();
		return cuenta;
	} 
	
	public String getEmailNumeroSerie(String numeroSerie) {
		String MY_QUERY = "SELECT * FROM "+ MySQLiteHelper.TABLA_CUENTA + " C INNER JOIN " + MySQLiteHelper.TABLA_EQUIPO + " E ON C._id = E.idCuenta WHERE E.numeroSerie = ?";
		Cursor cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(numeroSerie)});
		cursor.moveToFirst();
		String email = null;
		if (!cursor.isAfterLast()) {
			email = cursor.getString(1);
		}
		cursor.close();
		return email;
	} 
}