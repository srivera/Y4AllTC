package ec.com.yacare.y4all.lib.enumer;

public enum TipoRespuestaEnum {
	TR02("TR02", "Respuesta Automatica"), TR03(
			"TR03", "Respuesta Predefinida"), TR04("TR04", "Despedida");

	private final String codigo;

	private final String descripcion;

	private TipoRespuestaEnum(String codigo, String descripcion) {
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
