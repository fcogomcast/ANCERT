package org.notariado.inti.exceptions;

/**
 * Excepci�n lanzada durante la construcci�n de un mensaje de respuesta.
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
