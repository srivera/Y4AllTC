package ec.com.yacare.y4all.lib.enumer;

public enum EstadoEventoEnum {
	RECIBIDO("RECIBIDO", "Recibido");

	private final String codigo;

	private final String descripcion;

	private EstadoEventoEnum(String codigo, String descripcion) {
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
