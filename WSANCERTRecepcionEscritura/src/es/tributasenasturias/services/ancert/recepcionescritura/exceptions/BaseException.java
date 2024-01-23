/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.exceptions;

/**
 * @author crubencvs
 *
 */
public class BaseException extends Exception {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6090623581426000429L;
	
	private String error;
	private Throwable original;
	
	public BaseException(String error, Throwable original){
		super(error,original);
		this.error = error;
		this.original = original;
	}
	public BaseException (String error)
	{
		super(error);
		this.error = error;
		original=null;
	}
	
	public final Throwable getCause(){
		return original;
	}
	
	public final String getMessage(){
		StringBuffer strBuffer = new StringBuffer();
		if(super.getMessage()!=null){
			strBuffer.append(super.getMessage());
		}
		if(original !=null){
			strBuffer.append("[");
			strBuffer.append(original.getMessage());
			strBuffer.append("]");
		}
		return strBuffer.toString();
	}
	
	public final String getError(){
		return error;
	}

}
