/**
 * 
 */
package org.notariado.inti.utils.log;

/**
 * @author crubencvs
 *
 */
public class LogException extends Exception {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7854390033881822842L;
	
	public LogException(String error, Throwable original){
		super(LogException.class.getName()+"::"+error,original);
	}
	public LogException (String error)
	{
		super(LogException.class.getName()+"::"+error);
	}
	

}
