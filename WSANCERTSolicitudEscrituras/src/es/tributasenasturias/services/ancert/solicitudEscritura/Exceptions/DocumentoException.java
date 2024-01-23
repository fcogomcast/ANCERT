package es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions;

public class DocumentoException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2525652599164624472L;

	public DocumentoException(String message, Throwable cause) {
		super(DocumentoException.class.getName()+":"+ message, cause);
		
	}

	public DocumentoException(String message) {
		super(DocumentoException.class.getName()+":"+message);
	}

	public DocumentoException(Throwable cause) {
		super(cause);
	}
	
}
