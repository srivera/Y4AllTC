package ec.com.yacare.y4all.lib.dto;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;



public class UbicacionEquipo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String nombreUbicEquipo;
	private double latitude;
	private double longitud;
	private Integer radio;
	private String tipoAviso;
	private String estado;
	private String idEquipo;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getIdEquipo() {
		return idEquipo;
	}
	public void setIdEquipo(String idEquipo) {
		this.idEquipo = idEquipo;
	}
	public String getNombreUbicEquipo() {
		return nombreUbicEquipo;
	}
	public void setNombreUbicEquipo(String nombreUbicEquipo) {
		this.nombreUbicEquipo = nombreUbicEquipo;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitud() {
		return longitud;
	}
	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public Integer getRadio() {
		return radio;
	}
	public void setRadio(Integer radio) {
		this.radio = radio;
	}
	public String getTipoAviso() {
		return tipoAviso;
	}
	public void setTipoAviso(String tipoAviso) {
		this.tipoAviso = tipoAviso;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	
//	public JSONObject getJSONObject() {
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("id", id);
//            obj.put("tipoConfigEquipo", tipoConfigEquipo);
//            obj.put("valorConfigEquipo", valorConfigEquipo);
//            obj.put("idEquipo", idEquipo);
//        } catch (JSONException e) {
//            
//        }
//        return obj;
//    }
//
//	
//	public UbicacionEquipo getConfiguracionEquipo(String json) {
//		UbicacionEquipo configuracionEquipo = new UbicacionEquipo();
//      
//        try {
//        	JSONObject obj = new JSONObject(json);
//        	configuracionEquipo.setId(obj.getString("id"));
//        	configuracionEquipo.setTipoConfigEquipo(obj.getString("tipoConfigEquipo"));
//        	configuracionEquipo.setValorConfigEquipo(obj.getString("valorConfigEquipo"));
//        	configuracionEquipo.setIdEquipo(obj.getString("idEquipo"));
//        } catch (JSONException e) {
//            
//        }
//        return configuracionEquipo;
//    }
	
}