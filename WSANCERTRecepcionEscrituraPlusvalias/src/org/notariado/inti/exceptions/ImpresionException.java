package org.notariado.inti.exceptions;

/**
 * Se produce durante la impresión o reimpresión de documento.
 * @author crubencvs
 *
 */
public class ImpresionException extends Exception{



	/**
	 * 
	 */
	private static final long serialVersionUID = 7133950827131490986L;

	/**
	 * 
	 */
	

	public ImpresionException() {
		super();
	}

	public ImpresionException(String message, Throwable cause) {
		super("["+ImpresionException.class.getName()+"]:"+message, cause);
	}

	public ImpresionException(String message) {
		super("["+ImpresionException.class.getName()+"]:"+message);
	}

}
