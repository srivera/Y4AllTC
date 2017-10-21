package ec.com.yacare.y4all.lib.dto;


public class MensajeTexto {
	private String id;
	private String texto;
	private Boolean esSeleccionado;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Boolean getEsSeleccionado() {
		return esSeleccionado;
	}

	public void setEsSeleccionado(Boolean esSeleccionado) {
		this.esSeleccionado = esSeleccionado;
	}
}
