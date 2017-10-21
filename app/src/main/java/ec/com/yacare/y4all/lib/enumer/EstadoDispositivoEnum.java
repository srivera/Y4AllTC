package ec.com.yacare.y4all.lib.enumer;

public enum EstadoDispositivoEnum {
	FABRICADO("FAB", "FABRICADO"), CONFIGURACION("CON", "CONFIGURACION"), INSTALADO(
			"INS", "INSTALADO");

	private final String codigo;

	private final String descripcion;

	private EstadoDispositivoEnum(String codigo, String descripcion) {
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
