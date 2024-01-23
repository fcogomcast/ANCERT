/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.wssecurity;

/**
 * @author crubencvs
 *
 */
public class WSSecurityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4931107957969862760L;
	public WSSecurityException(String error, Throwable original){
		super(WSSecurityException.class.getName()+"::"+error,original);
	}
	public WSSecurityException (String error)
	{
		super(WSSecurityException.class.getName()+"::"+error);
	}
	

}
