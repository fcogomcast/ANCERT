package org.notariado.inti.exceptions;

/**
 * Se produce en la capa de acceso a datos del servicio web.
 * @author crubencvs
 *
 */
public class BDException extends Exception{



	/**
	 * 
	 */
	private static final long serialVersionUID = 7133950827131490986L;

	/**
	 * 
	 */
	

	public BDException() {
		super();
	}

	public BDException(String message, Throwable cause) {
		super("["+BDException.class.getName()+"]:"+message, cause);
	}

	public BDException(String message) {
		super("["+BDException.class.getName()+"]:"+message);
	}

}
