package es.tributasenasturias.fichas.log;

public class LogException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -170764639191838070L;
	
	public LogException(String error, Throwable original){
		super(LogException.class.getName()+"::"+error,original);
	}
	public LogException (String error)
	{
		super(LogException.class.getName()+"::"+error);
	}
}
