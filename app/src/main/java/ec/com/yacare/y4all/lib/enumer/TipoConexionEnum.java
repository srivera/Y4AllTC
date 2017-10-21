package ec.com.yacare.y4all.lib.enumer;

public enum TipoConexionEnum {
	WIFI("WIFI", "WIFI"), REDLOCAL("REDLOCAL", "RED LOCAL"), INTERNET(
			"INTERNET", "INTERNET");

	private final String codigo;

	private final String descripcion;

	private TipoConexionEnum(String codigo, String descripcion) {
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
