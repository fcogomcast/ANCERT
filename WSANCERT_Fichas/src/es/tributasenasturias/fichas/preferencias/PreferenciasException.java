/**
 * 
 */
package es.tributasenasturias.fichas.preferencias;

/**
 * @author crubencvs
 *
 */
public class PreferenciasException extends Exception {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1924603551464302394L;
	public PreferenciasException(String error, Throwable original){
		super("PreferenciasException::"+error,original);
	}
	public PreferenciasException (String error)
	{
		super("PreferenciasException::"+error);
	}
	

}
