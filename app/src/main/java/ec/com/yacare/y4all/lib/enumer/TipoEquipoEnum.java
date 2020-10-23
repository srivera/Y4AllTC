package ec.com.yacare.y4all.lib.enumer;

public enum TipoEquipoEnum {
	PORTERO("PORTERO", "PORTERO WII BELL", false),
	REGLETA("REGLET", "REGLETA", false), 
	LUCES("LUCES", "LUCES WIFI", false),
	CAMARA("CAMARA", "CAMARA", false),
	TELEPRESENCIA("TELPRE", "TELEPRESENCIA", false),
	CARROJUGUETE("CARJUG", "CARRO JUGUETE", false),
	LOCALIZADOR("LOC", "LOCALIZADOR", true),
	MONITORBEBE("MONBEB", "MONITOR BEBE", false),
	MUSICA("MUSICA", "MUSICA", false),
	INTERCOMUNICADOR("INTCOM", "INTERCOMUNICADOR", false),
	MICROFONO("MIC", "MICROFONO", false),
	CENTRALTELEFONICA("CENTEL", "CENTRAL TELEFONICA", false);

	private final String codigo;

	private final String descripcion;
	
	private final Boolean udpCorporativo;

	private TipoEquipoEnum(String codigo, String descripcion,  Boolean udpCorporativo ) {
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.udpCorporativo = udpCorporativo;
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

	public Boolean getUdpCorporativo() {
		return udpCorporativo;
	}
	
	public static TipoEquipoEnum getTipoEquipo(String codigo) {
		TipoEquipoEnum tipoEquipoEnum = null;
		if(codigo.equals(TipoEquipoEnum.PORTERO.getCodigo())){
			tipoEquipoEnum = TipoEquipoEnum.PORTERO;
		}else if(codigo.equals(TipoEquipoEnum.LOCALIZADOR.getCodigo())){
			tipoEquipoEnum = TipoEquipoEnum.LOCALIZADOR;
		}else if(codigo.equals(TipoEquipoEnum.MICROFONO.getCodigo())){
			tipoEquipoEnum = TipoEquipoEnum.MICROFONO;
		}else if(codigo.equals(TipoEquipoEnum.REGLETA.getCodigo())){
			tipoEquipoEnum = TipoEquipoEnum.REGLETA;
		}else if(codigo.equals(TipoEquipoEnum.CARROJUGUETE.getCodigo())){
			tipoEquipoEnum = TipoEquipoEnum.CARROJUGUETE;
		}else if(codigo.equals(TipoEquipoEnum.MONITORBEBE.getCodigo())){
			tipoEquipoEnum = TipoEquipoEnum.MONITORBEBE;
		}else if(codigo.equals(TipoEquipoEnum.INTERCOMUNICADOR.getCodigo())){
			tipoEquipoEnum = TipoEquipoEnum.INTERCOMUNICADOR;
		}else if(codigo.equals(TipoEquipoEnum.LUCES.getCodigo())){
			tipoEquipoEnum = TipoEquipoEnum.LUCES;
		}
			
		
		return tipoEquipoEnum;
	}
}
