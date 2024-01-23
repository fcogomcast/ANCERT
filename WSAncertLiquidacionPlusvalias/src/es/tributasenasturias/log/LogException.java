package es.tributasenasturias.log;

public class LogException extends Exception{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7602668323894736316L;
	public LogException(String error, Throwable original){
		super(LogException.class.getName()+"::"+error,original);
	}
	public LogException (String error)
	{
		super(LogException.class.getName()+"::"+error);
	}
}
