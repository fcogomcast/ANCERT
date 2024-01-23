package es.tributasenasturias.fichas.excepciones;

public class ComprimidoException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6420147006155267735L;

	public ComprimidoException(String message, Throwable cause) {
		super(ComprimidoException.class.getName()+"::"+ message, cause);

	}

	public ComprimidoException(String message) {
		super(ComprimidoException.class.getName()+"::"+message);
	}

	public ComprimidoException(Throwable cause) {
		super(cause);
	}

	
}
