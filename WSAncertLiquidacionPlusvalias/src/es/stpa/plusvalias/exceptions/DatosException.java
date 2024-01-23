package es.stpa.plusvalias.exceptions;

public class DatosException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7315981825347540803L;

	public DatosException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatosException(String message) {
		super(message);
	}

}
