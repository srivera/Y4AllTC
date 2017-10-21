package ec.com.yacare.y4all.lib.dto;


import java.io.Serializable;

public class ZonaLuces implements Serializable{
	private String id;
	private String nombreZona;
	private String encenderTimbre;
	private String idEquipo;
	private String idRouter;
	private String numeroZona;
	private Boolean seleccionado = false;
	private Boolean toggleActivo = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombreZona() {
		return nombreZona;
	}

	public void setNombreZona(String nombreZona) {
		this.nombreZona = nombreZona;
	}

	public String getEncenderTimbre() {
		return encenderTimbre;
	}

	public void setEncenderTimbre(String encenderTimbre) {
		this.encenderTimbre = encenderTimbre;
	}

	public String getIdEquipo() {
		return idEquipo;
	}

	public void setIdEquipo(String idEquipo) {
		this.idEquipo = idEquipo;
	}

	public String getIdRouter() {
		return idRouter;
	}

	public void setIdRouter(String idRouter) {
		this.idRouter = idRouter;
	}

	public String getNumeroZona() {
		return numeroZona;
	}

	public void setNumeroZona(String numeroZona) {
		this.numeroZona = numeroZona;
	}

	public Boolean getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

	public Boolean getToggleActivo() {
		return toggleActivo;
	}

	public void setToggleActivo(Boolean toggleActivo) {
		this.toggleActivo = toggleActivo;
	}
}
