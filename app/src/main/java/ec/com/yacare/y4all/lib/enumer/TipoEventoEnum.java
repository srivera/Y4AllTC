package ec.com.yacare.y4all.lib.enumer;

public enum TipoEventoEnum {
	TIMBRAR("TIMBRAR", "Timbrar"), BUZON("BUZON", "Buzon Portero"), FOTO("FOTO", "Buzon Portero"),  RESPUESTA(
			"RESPUESTA", "Respuesta Grabada"), INICIO("INICIO", "Equipo Iniciado"), NOTIFICACION("NOTIFICACION", "Notificacion"),
	PUERTA("PUERTA", "Puerta");

	private final String codigo;

	private final String descripcion;

	private TipoEventoEnum(String codigo, String descripcion) {
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}
}
