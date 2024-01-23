package es.tributasenasturias.services.ancert.recepcionescritura.exceptions;

/**
 * La excepción genérica de fallo técnico, todas las excepciones técnicas (fallo en conexiones, etc) deberán
 * convertirse en esta.
 * @author crubencvs
 *
 */
public class SystemException extends BaseException {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1266712526326620272L;

	public SystemException(String error, Throwable original) {
		super(error, original);
	}

	public SystemException(String error) {
		super(error);
	}

}
