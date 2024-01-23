package org.notariado.inti.exceptions;

/**
 * Excepción lanzada durante la construcción de un mensaje de respuesta.
 * @author crubencvs
 *
 */
public class ConstructorRespuestaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2446127668432239517L;

	/**
	 * 
	 */

	public ConstructorRespuestaException() {
		super();
	}

	public ConstructorRespuestaException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConstructorRespuestaException(String message) {
		super(message);
	}
	

}
