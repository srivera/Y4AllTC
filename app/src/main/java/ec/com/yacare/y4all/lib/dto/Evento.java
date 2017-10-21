package ec.com.yacare.y4all.lib.dto;

import android.os.Environment;

import java.io.Serializable;

import ec.com.yacare.y4all.lib.util.Item;


public class Evento implements Item, Serializable{
	private String id;
	private String origen;
	private String fecha;
	private String estado;
	private String idGrabado;
	private String idEquipo;
	private String tipoEvento;
	private String mensaje;
	private String comando;
	private boolean seleccionado = false;
	private int colorFondo;
	private int colorLetra;
	private byte[] fotoActual;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getIdGrabado() {
		return idGrabado;
	}
	public void setIdGrabado(String idGrabado) {
		this.idGrabado = idGrabado;
	}
	public String getIdEquipo() {
		return idEquipo;
	}
	public void setIdEquipo(String idEquipo) {
		this.idEquipo = idEquipo;
	}
	public String getTipoEvento() {
		return tipoEvento;
	}
	public void setTipoEvento(String tipoEvento) {
		this.tipoEvento = tipoEvento;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public String getComando() {
		return comando;
	}
	public void setComando(String comando) {
		this.comando = comando;
	}
	@Override
	public boolean isSection() {
		return false;
	}

	public String getVideoInicial() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + id+"T.mp4";
	}

	public String getVideoBuzon() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + id+".mp4";
	}

	public String getVideoPuerta() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/" + id+"P.mp4";
	}

	public String getFoto() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Y4Home/"+ id + ".jpg";
	}

	public String getFecha12() {
		String fecha12 = fecha.substring(11, 16);
		if(fecha12.startsWith("0")){
			fecha12 = fecha12.substring(1,5);

		}
		Integer hora = Integer.valueOf(fecha12.substring(0, fecha12.indexOf(":")));
		if(hora > 12){
			String horaAnterior = String.valueOf(hora) + ":";
			hora = hora - 12;
			fecha12 = fecha12.replace(horaAnterior,String.valueOf(hora) + ":");
		}
		return fecha12;
	}

	public String getAmPm() {
		String fecha12 = fecha.substring(11, 16);
		Integer hora = Integer.valueOf(fecha12.substring(0, fecha12.indexOf(":")));
		String tiempo = "AM";
		if(hora > 12){
			tiempo = "PM";
		}
		return tiempo;
	}

	public boolean isSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

	public int getColorLetra() {
		return colorLetra;
	}

	public void setColorLetra(int colorLetra) {
		this.colorLetra = colorLetra;
	}

	public int getColorFondo() {
		return colorFondo;
	}

	public void setColorFondo(int colorFondo) {
		this.colorFondo = colorFondo;
	}

	public byte[] getFotoActual() {
		return fotoActual;
	}

	public void setFotoActual(byte[] fotoActual) {
		this.fotoActual = fotoActual;
	}
}
