package ec.com.yacare.y4all.lib.dto;

import android.widget.Button;

import java.io.Serializable;
import java.util.HashMap;

public class Respuesta implements Serializable{
	 private String id;
	 private String nombre;
	 private String tipo;
	 private String idEquipo;
	 private String tipoVoz;
	 
	 private Button editar;
	 private Button reproducir;
	 private Button borrar;	 
	 private HashMap<Integer, byte[]> audioQueu = new HashMap<Integer, byte[]>();

	private Boolean esSeleccionado;
	 
	

	public HashMap<Integer, byte[]> getAudioQueu() {
		return audioQueu;
	}
	public void setAudioQueu(HashMap<Integer, byte[]> audioQueu) {
		this.audioQueu = audioQueu;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Button getEditar() {
		return editar;
	}
	public void setEditar(Button editar) {
		this.editar = editar;
	}
	public Button getReproducir() {
		return reproducir;
	}
	public void setReproducir(Button reproducir) {
		this.reproducir = reproducir;
	}
	public Button getBorrar() {
		return borrar;
	}
	public void setBorrar(Button borrar) {
		this.borrar = borrar;
	}
	public String getIdEquipo() {
		return idEquipo;
	}
	public void setIdEquipo(String idEquipo) {
		this.idEquipo = idEquipo;
	}
	public String getTipoVoz() {
		return tipoVoz;
	}
	public void setTipoVoz(String tipoVoz) {
		this.tipoVoz = tipoVoz;
	}

	public Boolean getEsSeleccionado() {
		return esSeleccionado;
	}

	public void setEsSeleccionado(Boolean esSeleccionado) {
		this.esSeleccionado = esSeleccionado;
	}
}
