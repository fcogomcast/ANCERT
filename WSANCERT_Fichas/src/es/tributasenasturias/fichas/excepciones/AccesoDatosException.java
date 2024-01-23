package es.tributasenasturias.fichas.excepciones;

public class AccesoDatosException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8553844050571352447L;

	public AccesoDatosException(String message, Throwable cause) {
		super(AccesoDatosException.class.getName()+"::"+ message, cause);

	}

	public AccesoDatosException(String message) {
		super(AccesoDatosException.class.getName()+"::"+message);
	}

	public AccesoDatosException(Throwable cause) {
		super(cause);
	}

	
}
