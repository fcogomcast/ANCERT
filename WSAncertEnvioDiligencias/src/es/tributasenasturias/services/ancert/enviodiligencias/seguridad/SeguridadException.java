/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.seguridad;

/**
 * @author crubencvs
 *
 */
public class SeguridadException extends Exception {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3807758531921185004L;
	public SeguridadException(String error, Throwable original){
		super("FirmaException::"+error,original);
	}
	public SeguridadException (String error)
	{
		super("FirmaException::"+error);
	}
	

}
