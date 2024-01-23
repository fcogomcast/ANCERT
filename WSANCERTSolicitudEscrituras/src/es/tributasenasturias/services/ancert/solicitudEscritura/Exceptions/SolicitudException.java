package es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions;

public class SolicitudException extends BaseException {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1336610443652662245L;
	public SolicitudException(String error) {
		super(error);
	}
	public SolicitudException(String error, Throwable original) {
		super(error, original);
	}

}
