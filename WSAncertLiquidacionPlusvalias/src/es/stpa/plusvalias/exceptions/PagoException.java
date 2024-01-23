package es.stpa.plusvalias.exceptions;

public class PagoException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4463966329625418229L;

	private static String prefix(String message){
		return PagoException.class.getName()+":"+message;
	}
	public PagoException(String message, Throwable cause) {
		super(prefix(message), cause);
	}

	public PagoException(String message) {
		super(prefix(message));
	}

	public PagoException(Throwable cause) {
		super(cause);
	}
}
