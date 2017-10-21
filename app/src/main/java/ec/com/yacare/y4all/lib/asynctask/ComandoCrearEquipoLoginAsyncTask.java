package ec.com.yacare.y4all.lib.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.android.gms.gcm.GcmPubSub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.LoginActivity;
import ec.com.yacare.y4all.activities.SplashActivity;
import ec.com.yacare.y4all.asynctask.ws.SolicitarFotoDispositivoAsyncTask;
import ec.com.yacare.y4all.asynctask.ws.SolicitarFotoEquipoAsyncTask;
import ec.com.yacare.y4all.lib.dto.Dispositivo;
import ec.com.yacare.y4all.lib.dto.Equipo;
import ec.com.yacare.y4all.lib.dto.ProgramacionLuces;
import ec.com.yacare.y4all.lib.dto.ZonaLuces;
import ec.com.yacare.y4all.lib.enumer.EstadoDispositivoEnum;
import ec.com.yacare.y4all.lib.enumer.TipoEquipoEnum;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import ec.com.yacare.y4all.lib.sqllite.DispositivoDataSource;
import ec.com.yacare.y4all.lib.sqllite.EquipoDataSource;
import ec.com.yacare.y4all.lib.sqllite.ProgramacionDataSource;
import ec.com.yacare.y4all.lib.sqllite.ZonaDataSource;
import io.xlink.wifi.pipe.util.XlinkUtils;

public class ComandoCrearEquipoLoginAsyncTask extends AsyncTask<String, Float, String> {

	private Activity activity;
	private String comando;
	private JSONArray dispositivos;
	private String nombreDispositivo;
	private LoginActivity loginActivity;
	private JSONArray equipos;
	private SplashActivity splashActivity;

	public ComandoCrearEquipoLoginAsyncTask(Activity activity, String comando, JSONArray equipos, String nombreDispositivo, LoginActivity loginActivity, JSONArray dispositivos,
											SplashActivity splashActivity) {
		super();
		this.activity = activity;
		this.comando = comando;
		this.equipos = equipos;
		this.nombreDispositivo  = nombreDispositivo;
		this.loginActivity  = loginActivity;
		this.dispositivos  = dispositivos;
		this.splashActivity  = splashActivity;
	}

	boolean vaciar = false;
	@Override
	protected String doInBackground(String... arg0) {

		try {
			
//			// Get System TELEPHONY service reference
//			TelephonyManager tManager = (TelephonyManager) activity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
//			// Get carrier name (Network Operator Name)
//			String carrierName = tManager.getNetworkOperatorName();
//			// Get Phone model and manufacturer name
//			String manufacturer = Build.MANUFACTURER;
//			String model = Build.MODEL;

			Calendar mCalendar = new GregorianCalendar();
			TimeZone mTimeZone = mCalendar.getTimeZone();
			int mGMTOffset = mTimeZone.getRawOffset();


			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			
			EquipoDataSource equipoDatasource = new EquipoDataSource(activity.getApplicationContext());
			equipoDatasource.open();

			for (int i = 0; i < equipos.length(); i++) {
				JSONObject equipoInArray = equipos.getJSONObject(i);
				
				Equipo equipoBusqueda = new Equipo();
				equipoBusqueda.setNumeroSerie(equipoInArray.getString("numeroSerie"));
				equipoBusqueda = equipoDatasource.getEquipoNumSerie(equipoBusqueda);
				
				if(equipoBusqueda == null || equipoBusqueda.getId() == null){
					Equipo equipo = new Equipo();
					equipo.setEstadoEquipo(equipoInArray.getString("estado"));
					equipo.setFechaRegistro(dateFormat.format(date));
					equipo.setId(UUID.randomUUID().toString());
					if(equipoInArray.has("ipLocal")){
						equipo.setIpLocal(equipoInArray.getString("ipLocal"));
					}
					if(equipoInArray.has("ipPublica")){
						equipo.setIpPublica(equipoInArray.getString("ipPublica"));
					}
					if(equipoInArray.has("ipPublica")){
						equipo.setIpPublica(equipoInArray.getString("ipPublica"));
					}

					if(equipoInArray.has("rutaSocketIo")){
						equipo.setSocketComando(equipoInArray.getString("rutaSocketIo"));
					}



					equipo.setNumeroSerie(equipoInArray.getString("numeroSerie"));
					equipo.setTipoEquipo(equipoInArray.getString("tipoEquipo").trim());
					equipo.setNombreEquipo(equipoInArray.getString("nombre").trim());

					if(!equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
						if (equipoInArray.has("versionfoto") && !equipoInArray.getString("versionfoto").trim().equals("")) {
							equipo.setVersionFoto(equipoInArray.getString("versionfoto"));
								if (!equipo.getVersionFoto().equals("0")) {
									SolicitarFotoEquipoAsyncTask solicitarFotoEquipoAsyncTask = new SolicitarFotoEquipoAsyncTask(activity.getApplicationContext(), equipo.getNumeroSerie());
									solicitarFotoEquipoAsyncTask.start();
								}
						}else{
							equipo.setVersionFoto("0");
						}
					}else {
						equipo.setVersionFoto("0");
					}

					equipo.setModo(YACSmartProperties.MODO_WIFI);
					equipoDatasource.createEquipo(equipo);
					if(equipoInArray.has("zonas")){
						JSONArray zonas = new JSONArray(equipoInArray.getString("zonas"));

						if(zonas.length() > 0){
							//delete zonas
							ZonaDataSource zonaDataSource = new ZonaDataSource(activity.getApplicationContext());
							zonaDataSource.open();
							zonaDataSource.deleteZonaImac(equipo.getNumeroSerie());
							for (int j = 0; j < zonas.length(); j++) {
								JSONObject zonaInArray = zonas.getJSONObject(j);
								ZonaLuces zona = new ZonaLuces();
								zona.setId(zonaInArray.get("id").toString());
								zona.setNumeroZona(zonaInArray.get("numeroZona").toString().trim());
								zona.setIdEquipo(equipo.getNumeroSerie());
								zona.setIdRouter(equipo.getNumeroSerie());
								zona.setNombreZona(zonaInArray.get("nombre").toString());
								zona.setEncenderTimbre(zonaInArray.get("encenderTimbre").toString());
								zonaDataSource.createZona(zona);


								if (zonaInArray.has("programaciones")) {
									JSONArray programaciones = new JSONArray(zonaInArray.getString("programaciones"));
									ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(activity.getApplicationContext());
									programacionDataSource.open();
									programacionDataSource.deleteProgramacionByImacZona(equipo.getNumeroSerie(), zona.getId());
									if (programaciones.length() > 0) {
										//delete zonas

										for (int k = 0; k < programaciones.length(); k++) {
											JSONObject progInArray = programaciones.getJSONObject(k);
											ProgramacionLuces prog = new ProgramacionLuces();
											prog.setAccion(progInArray.get("accion").toString());
											prog.setDias(progInArray.get("dias").toString());
											prog.setDuracion(progInArray.get("duracion").toString());

											String[] hora = progInArray.get("hora").toString().split(":");
											Integer horaGMT = Integer.valueOf(hora[0]) + ((Long) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS)).intValue();
											if(horaGMT >= 24){
												horaGMT = horaGMT - 24;
											}else if(horaGMT < 0){
												horaGMT = horaGMT + 24;
											}

											if(horaGMT.toString().length() == 1){
												hora[0] = "0" + horaGMT.toString();
											}else{
												hora[0] = horaGMT.toString();
											}
											if(hora[1].toString().length() == 1){
												hora[1] = "0" + hora[1];
											}
											prog.setHoraInicio(hora[0] + ":" + hora[1]);

											prog.setId(progInArray.get("id").toString());
											prog.setNombre(progInArray.get("nombre").toString());
											prog.setIdZona(zona.getId());
											prog.setIdRouter(equipo.getNumeroSerie());
											programacionDataSource.createProgramacion(prog);
										}


									}
									programacionDataSource.close();
								}


							}
							zonaDataSource.close();
						}
					}

				}else{
					Equipo equipo = equipoBusqueda;
					if(equipoBusqueda.getEstadoEquipo().equals(EstadoDispositivoEnum.INSTALADO.getCodigo())){
						//Verificar si debe actualizarse
						if(!equipoBusqueda.getNombreEquipo().equals(equipoInArray.getString("nombre").trim())){
							equipoBusqueda.setNombreEquipo(equipoInArray.getString("nombre").trim());
						}
						if(equipoInArray.has("rutaSocketIo")){
							equipoBusqueda.setSocketComando(equipoInArray.getString("rutaSocketIo"));
						}
						if(equipoInArray.has("ipLocal") && (equipoBusqueda.getIpLocal() == null || equipoBusqueda.getIpLocal().equals("") ||  !equipoBusqueda.getIpLocal().equals(equipoInArray.getString("ipLocal")) )){
							equipoBusqueda.setIpLocal(equipoInArray.getString("ipLocal").trim());
						}
						
						if(equipoInArray.has("ipPublica") && (equipoBusqueda.getIpPublica() == null || equipoBusqueda.getIpPublica().equals(""))
								||  !equipoBusqueda.getIpPublica().equals(equipoInArray.getString("ipPublica").trim())){
							equipoBusqueda.setIpPublica(equipoInArray.getString("ipPublica").trim());
						}
//						if(!equipo.getTipoEquipo().equals(TipoEquipoEnum.PORTERO.getCodigo())) {
//							if (equipoInArray.has("versionfoto") && !equipoInArray.getString("versionfoto").trim().equals("")) {
//								Integer versionAnterior = Integer.valueOf(equipo.getVersionFoto());
//								Integer versionActual = Integer.valueOf(equipoInArray.getString("versionfoto"));
//								equipo.setVersionFoto(equipoInArray.getString("versionfoto"));
//
//								if (!equipo.getVersionFoto().equals("0") || versionAnterior < versionActual) {
//									SolicitarFotoEquipoAsyncTask solicitarFotoEquipoAsyncTask = new SolicitarFotoEquipoAsyncTask(activity.getApplicationContext(), equipo.getNumeroSerie());
//									solicitarFotoEquipoAsyncTask.start();
//
//								}else{
//									File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + equipo.getNumeroSerie() + ".jpg");
//									if (!foto.exists()) {
//										SolicitarFotoEquipoAsyncTask solicitarFotoEquipoAsyncTask = new SolicitarFotoEquipoAsyncTask(activity.getApplicationContext(), equipo.getNumeroSerie());
//										solicitarFotoEquipoAsyncTask.start();
//									}
//								}
//							}else{
//								equipo.setVersionFoto("0");
//							}
//						}else {
//							equipo.setVersionFoto("0");
//						}
						equipoDatasource.updateEquipo(equipoBusqueda);

					}else{
						if(!equipoBusqueda.getEstadoEquipo().equals(equipoInArray.getString("estado").trim())){
							equipoBusqueda.setEstadoEquipo(equipoInArray.getString("estado").trim());
							equipoDatasource.updateEquipo(equipoBusqueda);
						}
					}

					if(equipoInArray.has("zonas")){
						JSONArray zonas = new JSONArray(equipoInArray.getString("zonas"));

						if(zonas.length() > 0){
							//delete zonas
							ZonaDataSource zonaDataSource = new ZonaDataSource(activity.getApplicationContext());
							zonaDataSource.open();
							zonaDataSource.deleteZonaImac(equipo.getNumeroSerie());
							for (int j = 0; j < zonas.length(); j++) {
								JSONObject zonaInArray = zonas.getJSONObject(j);
								ZonaLuces zona = new ZonaLuces();
								zona.setId(zonaInArray.get("id").toString());
								zona.setNumeroZona(zonaInArray.get("numeroZona").toString().trim());
								zona.setIdEquipo(equipo.getNumeroSerie());
								zona.setIdRouter(equipo.getNumeroSerie());
								zona.setNombreZona(zonaInArray.get("nombre").toString());
								zona.setEncenderTimbre(zonaInArray.get("encenderTimbre").toString());
								zonaDataSource.createZona(zona);


								if (zonaInArray.has("programaciones")) {
									JSONArray programaciones = new JSONArray(zonaInArray.getString("programaciones"));
									ProgramacionDataSource programacionDataSource = new ProgramacionDataSource(activity.getApplicationContext());
									programacionDataSource.open();
									programacionDataSource.deleteProgramacionByImacZona(equipo.getNumeroSerie(), zona.getId());
									if (programaciones.length() > 0) {
										//delete zonas

										for (int k = 0; k < programaciones.length(); k++) {
											JSONObject progInArray = programaciones.getJSONObject(k);
											ProgramacionLuces prog = new ProgramacionLuces();
											prog.setAccion(progInArray.get("accion").toString());
											prog.setDias(progInArray.get("dias").toString());
											prog.setDuracion(progInArray.get("duracion").toString());


											String[] hora = progInArray.get("hora").toString().split(":");
											Integer horaGMT = Integer.valueOf(hora[0]) + ((Long) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS)).intValue();
											if(horaGMT >= 24){
												horaGMT = horaGMT - 24;
											}else if(horaGMT < 0){
												horaGMT = horaGMT + 24;
											}

											if(horaGMT.toString().length() == 1){
												hora[0] = "0" + horaGMT.toString();
											}else{
												hora[0] = horaGMT.toString();
											}
											if(hora[1].toString().length() == 1){
												hora[1] = "0" + hora[1];
											}
											prog.setHoraInicio(hora[0] + ":" + hora[1]);

											prog.setId(progInArray.get("id").toString());
											prog.setNombre(progInArray.get("nombre").toString());
											prog.setIdZona(zona.getId());
											prog.setIdRouter(equipo.getNumeroSerie());
											programacionDataSource.createProgramacion(prog);
										}


									}
									programacionDataSource.close();
								}


							}
							zonaDataSource.close();
						}
					}
				}
			}


//			Equipo equipoBusqueda = new Equipo();
//			equipoBusqueda.setEstadoEquipo(EstadoDispositivoEnum.INSTALADO.getCodigo());
//			ArrayList<Equipo> equipos = equipoDatasource.getEquipoEstado(equipoBusqueda);

			equipoDatasource.close();

//			if(equipos.size() > 0) {
//				DatosAplicacion datosAplicacion = (DatosAplicacion) activity.getApplicationContext();
//				datosAplicacion.setEquipoSeleccionado((Equipo) equipos.toArray()[0]);
//			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


		try {

			DispositivoDataSource dispositivoDataSource = new DispositivoDataSource(activity.getApplicationContext());
			dispositivoDataSource.open();
			ArrayList<Dispositivo> dispositivosAnterior = dispositivoDataSource.getAllDispositivo();
			HashMap<String, String> dispositivosHash = new HashMap<String, String>();
			for(Dispositivo dAnterior : dispositivosAnterior){
				dispositivosHash.put(dAnterior.getImei(), dAnterior.getVersionFoto());
			}

			if(dispositivos.length() > 0){
				dispositivoDataSource.deleteAll();
			}

			for (int i = 0, size = dispositivos.length(); i < size; i++) {
				JSONObject dispositivoInArray = dispositivos.getJSONObject(i);
				Dispositivo dispositivo = new Dispositivo();
				dispositivo.setId(dispositivoInArray.getString("id").trim());

				dispositivo.setNombreDispositivo(dispositivoInArray.getString("nombre"));
				if(dispositivoInArray.has("tipodispositivo")){
					dispositivo.setTipo(dispositivoInArray.getString("tipodispositivo"));
				}
				if(dispositivoInArray.has("versionfoto")){
					dispositivo.setVersionFoto(dispositivoInArray.getString("versionfoto"));
				}else{
					dispositivo.setVersionFoto("0");
				}
				if(dispositivoInArray.has("iddispositivo")){
					dispositivo.setImei(dispositivoInArray.getString("iddispositivo"));
					if(dispositivosHash.containsKey(dispositivo.getImei())) {
						Integer version = Integer.valueOf(dispositivosHash.get(dispositivo.getImei()));
						if (!version.equals(0) && version < Integer.valueOf(dispositivosHash.get(dispositivo.getImei()))) {
							SolicitarFotoDispositivoAsyncTask solicitarFotoDispositivoAsyncTask = new SolicitarFotoDispositivoAsyncTask(activity.getApplicationContext(), dispositivoInArray.getString("iddispositivo"));
							solicitarFotoDispositivoAsyncTask.start();
						}else if (!version.equals(0)){
							File foto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Y4Home/" + dispositivo.getImei() + ".jpg");
							if (!foto.exists()) {
								SolicitarFotoDispositivoAsyncTask solicitarFotoDispositivoAsyncTask = new SolicitarFotoDispositivoAsyncTask(activity.getApplicationContext(), dispositivoInArray.getString("iddispositivo"));
								solicitarFotoDispositivoAsyncTask.start();
							}
						}
					}else{
						if (!Integer.valueOf(dispositivo.getVersionFoto()).equals(0)) {
							SolicitarFotoDispositivoAsyncTask solicitarFotoDispositivoAsyncTask = new SolicitarFotoDispositivoAsyncTask(activity.getApplicationContext(), dispositivoInArray.getString("iddispositivo"));
							solicitarFotoDispositivoAsyncTask.start();
						}
					}

				}
				dispositivoDataSource.createDispositivo(dispositivo);

			}
			dispositivoDataSource.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		DatosAplicacion aplicacion = (DatosAplicacion) activity.getApplicationContext();
		if(aplicacion.getRegId() != null) {
			try {
				subscribeTopics(aplicacion.getRegId(), aplicacion);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}


	protected void onPostExecute(String respuesta) {

			if (loginActivity != null) {
				loginActivity.verificarCrearEquipos();
				XlinkUtils.shortTips("termino de crear equipos ");
			} else {
				XlinkUtils.shortTips("validarIngreso 10");
				splashActivity.verificarCrearEquipos();
			}

	}

	private void subscribeTopics(String token, DatosAplicacion datosAplicacion) throws IOException {
		EquipoDataSource equipoDataSource = new EquipoDataSource(datosAplicacion);
		equipoDataSource.open();
		ArrayList<Equipo> equipos = equipoDataSource.getAllEquipo();
		equipoDataSource.close();
		GcmPubSub pubSub = GcmPubSub.getInstance(activity);
		for (Equipo equipo  : equipos) {
			pubSub.subscribe(token, "/topics/" + equipo.getNumeroSerie(), null);
		}
	}

	/**
	 * Obtiene el array de bytes descomprimido a partir de otro array de bytes
	 * comprimido
	 * 
	 * @param file
	 *            los datos comprimidos
	 * @return los datos descomprimidos.
	 * @throws IOException
	 *             de vez en cuando
	 */
	public byte[] descomprimirGZIP(byte[] file, Integer paquete)  {
		ByteArrayInputStream gzdata = new ByteArrayInputStream(file);
		GZIPInputStream gunzipper;
		try {
			gunzipper = new GZIPInputStream(gzdata, file.length);
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			byte[] readed = new byte[paquete];
			int actual = 1;
			while ((actual = gunzipper.read(readed)) > 0) {
				data.write(readed, 0, actual);
			}
			gzdata.close();
			gunzipper.close();
			byte[] returndata = data.toByteArray();
			return returndata;
		} catch (IOException e) {
		}
		return new byte[paquete];
	}
}
