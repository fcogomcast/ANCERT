package es.tributasenasturias.services.ancert.recepcionescritura.exceptions;

/**
 * La excepci�n gen�rica de fallo t�cnico, todas las excepciones t�cnicas (fallo en conexiones, etc) deber�n
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
