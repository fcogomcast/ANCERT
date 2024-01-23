package es.stpa.plusvalias.domain;

public enum TipoTramite {
	TRANSMISION("TO"),
	DONACION("DO"),
	SUCESION("SU");
	
	private String valor;
	
	TipoTramite(String valor){
		this.valor= valor;
	}
	/**
	 * Devuelve el valor de la instancia de la enumeración como un {@link String}
	 * @return
	 */
	public String valor(){
		return this.valor;
	}
	
	/**
	 * Recupera un trámite según su código
	 * @param codigoTramite
	 * @return
	 */	
	public static TipoTramite fromValue(String codigoTramite){
		if ("TO".equals(codigoTramite)){
			return TipoTramite.TRANSMISION;
		} else if ("DO".equals(codigoTramite)){
			return TipoTramite.DONACION;
		} else if ("SU".equals(codigoTramite)){
			return TipoTramite.SUCESION;
		} else {
			return null;
		}
	}
		
}
