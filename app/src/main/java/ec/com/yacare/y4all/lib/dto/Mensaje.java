package ec.com.yacare.y4all.lib.dto;


import android.widget.Button;

public class Mensaje {
	private String id;
	private String mensaje;
	private String estado; //recibido, visto
	private String idDispositivo;
	private String fecha;
	private String hora;
	private String tipo; //chat, voz
	private String direccion; //env, rec
	private Button resporducirMensaje;


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getIdDispositivo() {
		return idDispositivo;
	}

	public void setIdDispositivo(String idDispositivo) {
		this.idDispositivo = idDispositivo;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Button getResporducirMensaje() {
		return resporducirMensaje;
	}

	public void setResporducirMensaje(Button resporducirMensaje) {
		this.resporducirMensaje = resporducirMensaje;
	}
}
