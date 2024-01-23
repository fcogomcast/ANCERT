/**
 * 
 */
package org.notariado.inti.wssecurity;

/**
 * @author crubencvs
 *
 */
public class WSSecurityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6755057322886968509L;
	/**
	 * 
	 */
	
	public WSSecurityException(String error, Throwable original){
		super(WSSecurityException.class.getName()+"::"+error,original);
	}
	public WSSecurityException (String error)
	{
		super(WSSecurityException.class.getName()+"::"+error);
	}
	

}
