package ec.com.yacare.y4all.lib.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLA_RESPUESTAS = "respuestas";
    public static final String COLUMN_RESPUESTA_ID = "_id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_TIPO = "tipo";
    public static final String COLUMN_EQUIPO_ID_R_FK = "idEquipo";
    public static final String COLUMN_TIPO_VOZ = "tipoVoz";

    public static final String TABLA_EVENTOS = "eventos";
    public static final String COLUMN_EVENTO_ID = "_id";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_ORIGEN = "origen";
    public static final String COLUMN_ESTADO = "estado";
    public static final String COLUMN_ID_GRABACION = "grabado";
    public static final String COLUMN_EQUIPO_ID_T_FK = "idEquipo";
    public static final String COLUMN_TIPO_EVENTO = "tipoEvento";
    public static final String COLUMN_MENSAJE = "mensaje";
    public static final String COLUMN_COMANDO = "comando";

    public static final String TABLA_CUENTA = "cuenta";
    public static final String COLUMN_CUENTA_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TIPO_CUENTA = "tipoCuenta";
    public static final String COLUMN_PRIMER_NOMBRE = "primerNombre";
    public static final String COLUMN_SEGUNDO_NOMBRE = "segundoNombre";
    public static final String COLUMN_PRIMER_APELLIDO = "primerApellido";
    public static final String COLUMN_SEGUNDO_APELLIDO = "segundoApellido";
    public static final String COLUMN_RAZON_SOCIAL = "razonSocial";
    public static final String COLUMN_CLAVE = "clave";
    public static final String COLUMN_ID_MSJ_PUSH = "mensajePush";
    public static final String COLUMN_ESTADO_CUENTA = "estado";
    public static final String COLUMN_FECHA_CUENTA = "fecha";
    public static final String COLUMN_TELEFONO_CASA = "telefonoCasa";
    public static final String COLUMN_TELEFONO_OFIC = "telefonoOficina";
    public static final String COLUMN_TELEFONO_CELULAR = "telefonoCelular";
    public static final String COLUMN_CARGO = "cargo";
    public static final String COLUMN_IP = "ip";


    public static final String TABLA_EQUIPO = "equipo";
    public static final String COLUMN_EQUIPO_ID = "_id";
    public static final String COLUMN_NUMERO_SERIE = "numeroSerie";
    public static final String COLUMN_TIPO_EQUIPO = "tipoEquipo";
    public static final String COLUMN_NOMBRE_EQUIPO = "nombreEquipo";
    public static final String COLUMN_FECHA_FABRICACION = "fechaFabricacion";
    public static final String COLUMN_ESTADO_EQUIPO = "estadoEquipo";
    public static final String COLUMN_TIPO_RED = "tipoRed";
    public static final String COLUMN_NOMBRE_WIFI = "nombreWiFi";
    public static final String COLUMN_CLAVE_WIFI = "claveWiFi";
    public static final String COLUMN_IP_PUBLICA = "ipPublica";
    public static final String COLUMN_IP_LOCAL = "ipLocal";
    public static final String COLUMN_TIEMPO_ESPERA_BUZON = "tiempoBuzon";
    public static final String COLUMN_TIEMPO_GRABAR_BUZON = "tiempoGrabarBuzon";
    public static final String COLUMN_TONO = "tono";
    public static final String COLUMN_FECHA_REGISTRO = "fechaRegistro";
    public static final String COLUMN_EQUIPO_PADRE_ID = "idEquipoPadre";
    public static final String COLUMN_CLAVE_PRIVADA = "clavePrivada";
    public static final String COLUMN_CLAVE_ABRIR_PUERTA = "clavePuerta";
    public static final String COLUMN_NOMBRE_HOTSPOT = "nombreHotSpot";
    public static final String COLUMN_CLAVE_HOTSPOT = "claveHotSpot";
    public static final String COLUMN_EQ_ID_CUENTA = "idCuenta";
    public static final String COLUMN_LUZ = "luz";
    public static final String COLUMN_LUZ_WIFI = "luzWifi";
    public static final String COLUMN_BUZON_ACTIVO = "buzon";
    public static final String COLUMN_SENSOR = "sensor";
    public static final String COLUMN_PUERTA = "puerta";
    public static final String COLUMN_MENSAJE_INICIAL = "mensajeInicial";
    public static final String COLUMN_SOCKET_COMANDO = "socketComando";
    public static final String COLUMN_PUERTO_ACTIVO = "puertoActivo";
    public static final String COLUMN_ZONAS = "zonas";
    public static final String COLUMN_TIMBRE_EXTERNO = "timbreExterno";
    public static final String COLUMN_VERSION_FOTO = "versionFoto";
    public static final String COLUMN_MODO = "modo";
    public static final String COLUMN_MENSAJE_APERTURA = "mensajeApertura";
    public static final String COLUMN_VOZ_MENSAJE = "vozMensaje";
    public static final String COLUMN_MENSAJE_PUERTA = "mensajePuerta";
    public static final String COLUMN_TIEMPO_ENCENDIDO_LUZ = "tiempoEncendidoLuz";
    public static final String COLUMN_VOLUMEN = "volumen";
    public static final String COLUMN_TIPO_PORTERO = "tipoPortero";
    public static final String COLUMN_NUM_DEPTO = "numeroDepartamento";

    public static final String TABLA_DISPOSITIVO = "dispositivo";
    public static final String COLUMN_DISPOSITIVO_ID = "_id";
    public static final String COLUMN_DISPOSITIVO_NOMBRE = "nombre";
    public static final String COLUMN_DISPOSITIVO_TIPO = "tipo";
    public static final String COLUMN_DISPOSITIVO_IMEI = "imei";
    public static final String COLUMN_DISPOSITIVO_VERSION_FOTO = "versionFoto";

    public static final String TABLA_MENSAJE = "mensaje";
    public static final String COLUMN_MENSAJE_ID = "_id";
    public static final String COLUMN_DISP_ID = "_id_dispositivo";
    public static final String COLUMN_DISP_MENSAJE = "mensaje";
    public static final String COLUMN_DISP_FECHA = "fecha";
    public static final String COLUMN_DISP_HORA = "hora";
    public static final String COLUMN_DISP_ESTADO = "estado";
    public static final String COLUMN_DISP_TIPO = "tipo";
    public static final String COLUMN_DISP_DIRECCION = "direccion";


    public static final String TABLA_ROUTER_LUCES = "routerLuces";
    public static final String COLUMN_ROUTER_ID = "_id";
    public static final String COLUMN_ROUTER_NOMBRE = "nombre";
    public static final String COLUMN_ROUTER_ESTADO = "estado";
    public static final String COLUMN_ROUTER_ID_EQUIPO = "idEquipo";
    public static final String COLUMN_ROUTER_IP_ROUTER = "ipRouter";
    public static final String COLUMN_ROUTER_IMAC_ROUTER = "imacRouter";

    public static final String TABLA_ZONA_LUCES = "zonaLuces";
    public static final String COLUMN_ZONA_ID = "_id";
    public static final String COLUMN_ZONA_NOMBRE = "nombre";
    public static final String COLUMN_ZONA_ENCENDER_TIMBRE = "encenderTimbre";
    public static final String COLUMN_ZONA_ID_EQUIPO = "idEquipo";
    public static final String COLUMN_ZONA_ID_ROUTER = "idRouter";
    public static final String COLUMN_ZONA_NUMERO = "numeroZona";

    public static final String TABLA_PROG_LUCES = "progLuces";
    public static final String COLUMN_PROG_ID = "_id";
    public static final String COLUMN_PROG_ID_ROUTER = "idRouter";
    public static final String COLUMN_PROG_ID_ZONA = "idZona";
    public static final String COLUMN_PROG_NOMBRE = "nombre";
    public static final String COLUMN_PROG_ACCION = "accion";
    public static final String COLUMN_PROG_HORA = "horaInicio";
    public static final String COLUMN_PROG_DURACION = "duracion";
    public static final String COLUMN_PROG_DIAS = "dias";


    public static final String TABLA_MENSAJE_TEXTO = "mensajeTexto";
    public static final String COLUMN_MSJ_ID = "_id";
    public static final String COLUMN_MSJ_TEXTO = "texto";

    private static final String DATABASE_NAME = "portero.db";
    private static final int DATABASE_VERSION = 1;


    private static final String CREATE_MENSAJE= "create table "
            + TABLA_MENSAJE + "(" + COLUMN_MENSAJE_ID
            + " text primary key, " + COLUMN_DISP_MENSAJE
            + " text, "    +   COLUMN_DISP_ID
            + " text, "   +   COLUMN_DISP_FECHA
            + " text, "  +   COLUMN_DISP_HORA
            + " text, "  +   COLUMN_DISP_ESTADO
            + " text, "  +   COLUMN_DISP_TIPO
            + " text, "  +   COLUMN_DISP_DIRECCION
            + " text);";


    private static final String CREATE_CUENTA = "create table "
            + TABLA_CUENTA + "(" + COLUMN_CUENTA_ID
            + " text primary key, " + COLUMN_EMAIL
            + " text, "  + COLUMN_TIPO_CUENTA
            + " text, "  + COLUMN_PRIMER_NOMBRE
            + " text, "  + COLUMN_SEGUNDO_NOMBRE
            + " text, "  + COLUMN_PRIMER_APELLIDO
            + " text, " + COLUMN_SEGUNDO_APELLIDO
            + " text, " + COLUMN_RAZON_SOCIAL
            + " text, " + COLUMN_CLAVE
            + " text not null, " + COLUMN_ID_MSJ_PUSH
            + " text, " + COLUMN_ESTADO_CUENTA
            + " text, " + COLUMN_TELEFONO_CASA
            + " text, " + COLUMN_TELEFONO_OFIC
            + " text, " + COLUMN_TELEFONO_CELULAR
            + " text, " + COLUMN_CARGO
            + " text, " +   COLUMN_FECHA_CUENTA
            + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +   COLUMN_IP
            + " text);";


    private static final String CREATE_EQUIPO = "create table "
            + TABLA_EQUIPO + "(" + COLUMN_EQUIPO_ID
            + " text primary key, " + COLUMN_NUMERO_SERIE
            + " text not null, "  + COLUMN_TIPO_EQUIPO
            + " text not null, "  + COLUMN_NOMBRE_EQUIPO
            + " text, "  + COLUMN_FECHA_FABRICACION
            + " text, "  + COLUMN_ESTADO_EQUIPO
            + " text, " + COLUMN_TIPO_RED
            + " text, " + COLUMN_NOMBRE_WIFI
            + " text, " + COLUMN_CLAVE_WIFI
            + " text, " + COLUMN_IP_PUBLICA
            + " text, " + COLUMN_IP_LOCAL
            + " text, " +   COLUMN_TIEMPO_ESPERA_BUZON
            + " integer, " +   COLUMN_TIEMPO_GRABAR_BUZON
            + " integer, " +   COLUMN_TONO
            + " text, " +   COLUMN_FECHA_REGISTRO
            + " text, " +   COLUMN_EQUIPO_PADRE_ID
            + " text, " +   COLUMN_CLAVE_PRIVADA
            + " text, " +   COLUMN_CLAVE_ABRIR_PUERTA
            + " text, " +   COLUMN_NOMBRE_HOTSPOT
            + " text, " +   COLUMN_CLAVE_HOTSPOT
            + " text, " +  COLUMN_EQ_ID_CUENTA
            + " text, " +  COLUMN_LUZ
            + " text, " +  COLUMN_LUZ_WIFI
            + " text, " +  COLUMN_BUZON_ACTIVO
            + " text, " +  COLUMN_SENSOR
            + " text, " +  COLUMN_PUERTA
            + " text, " +  COLUMN_MENSAJE_INICIAL
            + " text, " +  COLUMN_SOCKET_COMANDO
            + " text, " +  COLUMN_PUERTO_ACTIVO
            + " text, " +  COLUMN_ZONAS
            + " text, " +  COLUMN_TIMBRE_EXTERNO
            + " text, " +  COLUMN_VERSION_FOTO
            + " text, " +  COLUMN_MODO
            + " text, " +  COLUMN_MENSAJE_APERTURA
            + " text, " +  COLUMN_VOZ_MENSAJE
            + " text, " +  COLUMN_MENSAJE_PUERTA
            + " text, " +  COLUMN_TIEMPO_ENCENDIDO_LUZ
            + " integer, " +  COLUMN_VOLUMEN
            + " integer, " +  COLUMN_TIPO_PORTERO
            + " text, " +  COLUMN_NUM_DEPTO
            + " text  );";

    private static final String CREATE_RESPUESTA = "create table "
            + TABLA_RESPUESTAS + "(" + COLUMN_RESPUESTA_ID
            + " text primary key, " + COLUMN_NOMBRE
            + " text not null, "    +   COLUMN_TIPO
            + " text, "   +   COLUMN_TIPO_VOZ
            + " text, "  +   COLUMN_EQUIPO_ID_R_FK
            + " text);";

    private static final String CREATE_EVENTO = "create table "
            + TABLA_EVENTOS + "(" + COLUMN_EVENTO_ID
            + " text primary key, " + COLUMN_ORIGEN
            + " text not null, "  + COLUMN_ESTADO
            + " text not null, "  + COLUMN_ID_GRABACION
            + " text, " +   COLUMN_FECHA
            + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + COLUMN_EQUIPO_ID_T_FK
            + " text, " +   COLUMN_TIPO_EVENTO
            + " text, " +   COLUMN_MENSAJE
            + " text, " +   COLUMN_COMANDO
            + " text);";

    private static final String CREATE_DISPOSITIVO = "create table "
            + TABLA_DISPOSITIVO + "(" + COLUMN_DISPOSITIVO_ID
            + " text primary key, " + COLUMN_DISPOSITIVO_NOMBRE
            + " text not null, "    +   COLUMN_TIPO
            + " text, "    +   COLUMN_DISPOSITIVO_IMEI
            + " text, "    +   COLUMN_DISPOSITIVO_VERSION_FOTO
            + " text);";


    private static final String CREATE_ZONA = "create table "
            + TABLA_ZONA_LUCES + "(" + COLUMN_ZONA_ID
            + " text, "  +   COLUMN_ZONA_NOMBRE
            + " text, "  +   COLUMN_ZONA_ENCENDER_TIMBRE
            + " text, "  +   COLUMN_ZONA_ID_ROUTER
            + " text, "  +   COLUMN_ZONA_ID_EQUIPO
            + " text, "  +   COLUMN_ZONA_NUMERO
            + " text);";

    private static final String CREATE_ROUTER = "create table "
            + TABLA_ROUTER_LUCES + "(" + COLUMN_ROUTER_ID
            + " text, "  +   COLUMN_ROUTER_NOMBRE
            + " text, "  +   COLUMN_ROUTER_ESTADO
            + " text, "  +   COLUMN_ROUTER_ID_EQUIPO
            + " text, "  +   COLUMN_ROUTER_IP_ROUTER
            + " text, "  +   COLUMN_ROUTER_IMAC_ROUTER
            + " text);";

    private static final String CREATE_PROG_LUCES = "create table "
            + TABLA_PROG_LUCES + "(" + COLUMN_PROG_ID
            + " text, "  +   COLUMN_PROG_ID_ROUTER
            + " text, "  +   COLUMN_PROG_ID_ZONA
            + " text, "  +   COLUMN_PROG_NOMBRE
            + " text, "  +   COLUMN_PROG_ACCION
            + " text, "  +   COLUMN_PROG_HORA
            + " text, "  +   COLUMN_PROG_DURACION
            + " text, "  +   COLUMN_PROG_DIAS
            + " text);";

    private static final String CREATE_MENSAJE_TEXTO = "create table "
            + TABLA_MENSAJE_TEXTO + "(" + COLUMN_MSJ_ID
            + " text, "  +   COLUMN_MSJ_TEXTO
            + " text);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_CUENTA);
        database.execSQL(CREATE_EQUIPO);
        database.execSQL(CREATE_RESPUESTA);
        database.execSQL(CREATE_EVENTO);
        database.execSQL(CREATE_DISPOSITIVO);
        database.execSQL(CREATE_MENSAJE);
        database.execSQL(CREATE_ZONA);
        database.execSQL(CREATE_ROUTER);
        database.execSQL(CREATE_PROG_LUCES);
        database.execSQL(CREATE_MENSAJE_TEXTO);
        database.execSQL("INSERT INTO mensajeTexto(_id, texto) values('1', 'A quien busca');");
        database.execSQL("INSERT INTO mensajeTexto(_id, texto) values('2', 'Un momento por favor');");
        database.execSQL("INSERT INTO mensajeTexto(_id, texto) values('3', 'Salgo en un momento');");
        database.execSQL("INSERT INTO mensajeTexto(_id, texto) values('4', 'No gracias');");
//        System.out.println("termino scripts");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_RESPUESTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_EVENTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_CUENTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_EQUIPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_MENSAJE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_ZONA_LUCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_ROUTER_LUCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PROG_LUCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_MENSAJE_TEXTO);
        onCreate(db);
    }

} 
