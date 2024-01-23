package es.tributasenasturias.fichas.excepciones;

public class AltaFichaException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8078440429474499902L;

	public AltaFichaException(String message, Throwable cause) {
		super(AltaFichaException.class.getName()+"::"+ message, cause);

	}

	public AltaFichaException(String message) {
		super(AltaFichaException.class.getName()+"::"+message);
	}

	public AltaFichaException(Throwable cause) {
		super(cause);
	}

	
}
