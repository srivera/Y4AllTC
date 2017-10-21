package ec.com.yacare.y4all.lib.dto;

import android.widget.Button;

import java.io.Serializable;



public class Dispositivo implements Serializable{
	private String id;
	private String imei;
	private String nombreDispositivo;
	private String tipo;
	private String versionFoto;

	private Button btnLocalizar;
	
	private Button btnBuscar;

	private Button btnDesactivar;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombreDispositivo() {
		return nombreDispositivo;
	}
	public void setNombreDispositivo(String nombreDispositivo) {
		this.nombreDispositivo = nombreDispositivo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}

	public Button getBtnLocalizar() {
		return btnLocalizar;
	}
	public void setBtnLocalizar(Button btnLocalizar) {
		this.btnLocalizar = btnLocalizar;
	}
	public Button getBtnBuscar() {
		return btnBuscar;
	}
	public void setBtnBuscar(Button btnBuscar) {
		this.btnBuscar = btnBuscar;
	}

	public Button getBtnDesactivar() {
		return btnDesactivar;
	}

	public void setBtnDesactivar(Button btnDesactivar) {
		this.btnDesactivar = btnDesactivar;
	}

	public String getVersionFoto() {
		return versionFoto;
	}

	public void setVersionFoto(String versionFoto) {
		this.versionFoto = versionFoto;
	}
}