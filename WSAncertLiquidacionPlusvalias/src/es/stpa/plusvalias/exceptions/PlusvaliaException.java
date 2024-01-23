package es.stpa.plusvalias.exceptions;

public class PlusvaliaException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2424299754278469247L;
	
	private final static String servicio="WSAncertPlusvalias";
	public PlusvaliaException(String message, Throwable cause) {
		super(servicio+":"+message, cause);
	}

	public PlusvaliaException(String message) {
		super(servicio+":"+message);
	}

	public PlusvaliaException(Throwable cause) {
		super(servicio,cause);
	}

}
