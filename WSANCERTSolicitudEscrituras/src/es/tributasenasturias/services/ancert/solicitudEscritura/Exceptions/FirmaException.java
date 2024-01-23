/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions;


/**
 * @author crubencvs
 *
 */
public class FirmaException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6533734080044276496L;

	public FirmaException(String error, Throwable original) {
		super("FirmaException::"+error, original);
	}

	public FirmaException(String error) {
		super("FirmaException::" + error);
	}

}
