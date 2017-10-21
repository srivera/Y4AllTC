package ec.com.yacare.y4all.lib.dto;

import android.graphics.Bitmap;

import java.io.Serializable;


public class Contacto implements Serializable{
	private String nombre;
	private String correo;
	private String id;
	private Bitmap foto;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Bitmap getFoto() {
		return foto;
	}

	public void setFoto(Bitmap foto) {
		this.foto = foto;
	}
}