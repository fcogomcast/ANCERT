package es.stpa.plusvalias.presentacion;

public enum ValorEstado {
	INICIADO ("INI"),
	PAGADO ("PAGADO"),
	INTEGRADO("INTEG"),
	PRESENTADO("PRESEN"),
	FINALIZADO("FIN");
	
	private final String valor;
	
	private ValorEstado(String estado){
		this.valor= estado;
	}
  
	/**
	 * Recupera un estado en función del texto de estado
	 * @param estado Estado (seguramente recuperado de base de datos) del que se quiere recuperar el valor de enumeración
	 * @return
	 * @exception IllegalArgumentException si el estado indicado es incorrecto
	 */
	public static ValorEstado get(String estado) throws IllegalArgumentException{
		if (estado==null){
			throw new IllegalArgumentException("El estado es nulo");
		}
		//Podría mejorar si crease un mapa con todas las claves, y el estado
		//asociado. De todas maneras, como no va a ser dinámico, esto está más claro.
		if ("INI".equalsIgnoreCase(estado)){
			return ValorEstado.INICIADO;
		} else if ("PAGADO".equalsIgnoreCase(estado)){
			return ValorEstado.PAGADO;
		} else if ("INTEG".equalsIgnoreCase(estado)){
			return ValorEstado.INTEGRADO;
		} else if ("PRESEN".equalsIgnoreCase(estado)){
			return ValorEstado.PRESENTADO;
		} else if ("FIN".equalsIgnoreCase(estado)){
			return ValorEstado.FINALIZADO;
		}

		throw new IllegalArgumentException("El estado " + estado + " no es válido");
	}
	@Override
	public String toString(){
		return valor;
	}
}
