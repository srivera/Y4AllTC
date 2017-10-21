package ec.com.yacare.y4all.lib.dto;


import java.io.Serializable;

public class ProgramacionLuces implements Serializable{
	private String id;
	private String idRouter;
	private String idZona;
	private String nombre;
	private String accion;
	private String horaInicio;
	private String duracion;
	private String dias;

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getDias() {
		return dias;
	}

	public void setDias(String dias) {
		this.dias = dias;
	}

	public String getDuracion() {
		return duracion;
	}

	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdRouter() {
		return idRouter;
	}

	public void setIdRouter(String idRouter) {
		this.idRouter = idRouter;
	}

	public String getIdZona() {
		return idZona;
	}

	public void setIdZona(String idZona) {
		this.idZona = idZona;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}
}
