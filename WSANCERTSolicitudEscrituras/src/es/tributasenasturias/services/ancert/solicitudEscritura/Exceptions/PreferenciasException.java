/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions;

/**
 * @author crubencvs
 *
 */
public class PreferenciasException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3402340116336043150L;
	
	
	public PreferenciasException(String error, Throwable original){
		super("PreferenciasException::"+error,original);
	}
	public PreferenciasException (String error)
	{
		super("PreferenciasException::"+error);
	}
	

}
