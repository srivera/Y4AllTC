package ec.com.yacare.y4all.lib.dto;

import android.widget.Button;

import java.io.Serializable;

import ec.com.yacare.y4all.lib.enumer.TipoConexionEnum;
import io.xlink.wifi.pipe.bean.Device;


public class Equipo implements Serializable{
	private String id;
	private String numeroSerie;
	private String tipoEquipo;
	private String nombreEquipo;
	private String fechaFabricacion;
	private String estadoEquipo;
	private String tipoRed;
	private String nombreWiFi;
	private String claveWiFi;
	private String ipPublica;
	private String ipLocal;
	private Integer tiempoEsperaBuzon;
	private Integer tiempoGrabarBuzon;
	private String tono;
	private String fechaRegistro;
	private String idEquipoPadre;
	private String clavePrivada;
	private String nombreRedHotSpot;
	private String claveHotSpot;
	private String idCuenta;
	private String luz;
	private String luzWifi;
	private String buzon;
	private String sensor;
	private String puerta;
	private String mensajeInicial;
	private String socketComando;
	private String puertoActivo;
	private String zonas;
	private String timbreExterno;
	private String versionFoto;
	private String modo;
	private String mensajeApertura;
	private String vozMensaje;
	private String mensajePuerta;
	private Integer tiempoEncendidoLuz;
	private Integer volumen;
	private Device device;

	private Equipo equipoPadre;

	//No persistentes
	private String ipSeleccionada;
	private TipoConexionEnum tipoConexion;

	private Button btnModificar;

	private Button btnCompartir;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNumeroSerie() {
		return numeroSerie;
	}
	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}
	public String getTipoEquipo() {
		return tipoEquipo;
	}
	public void setTipoEquipo(String tipoEquipo) {
		this.tipoEquipo = tipoEquipo;
	}
	public String getNombreEquipo() {
		return nombreEquipo;
	}
	public void setNombreEquipo(String nombreEquipo) {
		this.nombreEquipo = nombreEquipo;
	}
	public String getFechaFabricacion() {
		return fechaFabricacion;
	}
	public void setFechaFabricacion(String fechaFabricacion) {
		this.fechaFabricacion = fechaFabricacion;
	}
	public String getEstadoEquipo() {
		return estadoEquipo;
	}
	public void setEstadoEquipo(String estadoEquipo) {
		this.estadoEquipo = estadoEquipo;
	}
	
	public String getTipoRed() {
		return tipoRed;
	}
	public void setTipoRed(String tipoRed) {
		this.tipoRed = tipoRed;
	}
	public String getNombreWiFi() {
		return nombreWiFi;
	}
	public void setNombreWiFi(String nombreWiFi) {
		this.nombreWiFi = nombreWiFi;
	}
	public String getClaveWiFi() {
		return claveWiFi;
	}
	public void setClaveWiFi(String claveWiFi) {
		this.claveWiFi = claveWiFi;
	}
	public String getIpPublica() {
		return ipPublica;
	}
	public void setIpPublica(String ipPublica) {
		this.ipPublica = ipPublica;
	}
	public String getIpLocal() {
		return ipLocal;
	}
	public void setIpLocal(String ipLocal) {
		this.ipLocal = ipLocal;
	}
	public String getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public String getIdEquipoPadre() {
		return idEquipoPadre;
	}
	public void setIdEquipoPadre(String idEquipoPadre) {
		this.idEquipoPadre = idEquipoPadre;
	}
	public String getClavePrivada() {
		return clavePrivada;
	}
	public void setClavePrivada(String clavePrivada) {
		this.clavePrivada = clavePrivada;
	}

	public TipoConexionEnum getTipoConexion() {
		return tipoConexion;
	}
	public String getNombreRedHotSpot() {
		return nombreRedHotSpot;
	}
	public void setNombreRedHotSpot(String nombreRedHotSpot) {
		this.nombreRedHotSpot = nombreRedHotSpot;
	}
	public String getClaveHotSpot() {
		return claveHotSpot;
	}
	public void setClaveHotSpot(String claveHotSpot) {
		this.claveHotSpot = claveHotSpot;
	}
	public String getIdCuenta() {
		return idCuenta;
	}
	public void setIdCuenta(String idCuenta) {
		this.idCuenta = idCuenta;
	}

	public Button getBtnModificar() {
		return btnModificar;
	}

	public void setBtnModificar(Button btnModificar) {
		this.btnModificar = btnModificar;
	}

	public Button getBtnCompartir() {
		return btnCompartir;
	}

	public void setBtnCompartir(Button btnCompartir) {
		this.btnCompartir = btnCompartir;
	}

	public String getLuz() {
		return luz;
	}

	public void setLuz(String luz) {
		this.luz = luz;
	}

	public String getLuzWifi() {
		return luzWifi;
	}

	public void setLuzWifi(String luzWifi) {
		this.luzWifi = luzWifi;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public String getPuerta() {
		return puerta;
	}

	public void setPuerta(String puerta) {
		this.puerta = puerta;
	}

	public String getMensajeInicial() {
		return mensajeInicial;
	}

	public void setMensajeInicial(String mensajeInicial) {
		this.mensajeInicial = mensajeInicial;
	}

	public String getSocketComando() {
		return socketComando;
	}

	public void setSocketComando(String socketComando) {
		this.socketComando = socketComando;
	}

	public String getIpSeleccionada() {
		return ipSeleccionada;
	}

	public void setIpSeleccionada(String ipSeleccionada) {
		this.ipSeleccionada = ipSeleccionada;
	}

	public void setTipoConexion(TipoConexionEnum tipoConexion) {
		this.tipoConexion = tipoConexion;
	}

	public String getBuzon() {
		return buzon;
	}

	public void setBuzon(String buzon) {
		this.buzon = buzon;
	}

	public Integer getTiempoEsperaBuzon() {
		return tiempoEsperaBuzon;
	}

	public void setTiempoEsperaBuzon(Integer tiempoEsperaBuzon) {
		this.tiempoEsperaBuzon = tiempoEsperaBuzon;
	}

	public Integer getTiempoGrabarBuzon() {
		return tiempoGrabarBuzon;
	}

	public void setTiempoGrabarBuzon(Integer tiempoGrabarBuzon) {
		this.tiempoGrabarBuzon = tiempoGrabarBuzon;
	}

	public String getTono() {
		return tono;
	}

	public void setTono(String tono) {
		this.tono = tono;
	}

	public String getPuertoActivo() {
		return puertoActivo;
	}

	public void setPuertoActivo(String puertoActivo) {
		this.puertoActivo = puertoActivo;
	}

	public String getZonas() {
		return zonas;
	}

	public void setZonas(String zonas) {
		this.zonas = zonas;
	}

	public String getTimbreExterno() {
		return timbreExterno;
	}

	public void setTimbreExterno(String timbreExterno) {
		this.timbreExterno = timbreExterno;
	}

	public String getVersionFoto() {
		return versionFoto;
	}

	public void setVersionFoto(String versionFoto) {
		this.versionFoto = versionFoto;
	}

	public String getModo() {
		return modo;
	}

	public void setModo(String modo) {
		this.modo = modo;
	}

	public String getMensajeApertura() {
		return mensajeApertura;
	}

	public void setMensajeApertura(String mensajeApertura) {
		this.mensajeApertura = mensajeApertura;
	}

	public String getVozMensaje() {
		return vozMensaje;
	}

	public void setVozMensaje(String vozMensaje) {
		this.vozMensaje = vozMensaje;
	}

	public String getMensajePuerta() {
		return mensajePuerta;
	}

	public void setMensajePuerta(String mensajePuerta) {
		this.mensajePuerta = mensajePuerta;
	}

	public Equipo getEquipoPadre() {
		return equipoPadre;
	}

	public void setEquipoPadre(Equipo equipoPadre) {
		this.equipoPadre = equipoPadre;
	}

	public Integer getTiempoEncendidoLuz() {
		return tiempoEncendidoLuz;
	}

	public void setTiempoEncendidoLuz(Integer tiempoEncendidoLuz) {
		this.tiempoEncendidoLuz = tiempoEncendidoLuz;
	}

	public Integer getVolumen() {
		return volumen;
	}

	public void setVolumen(Integer volumen) {
		this.volumen = volumen;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
}