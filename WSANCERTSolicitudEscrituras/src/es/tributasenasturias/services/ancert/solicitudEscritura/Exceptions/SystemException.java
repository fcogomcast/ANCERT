package es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions;

public class SystemException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7140058745390965930L;

	public SystemException(String error, Throwable original) {
		super(error, original);
	}

	public SystemException(String error) {
		super(error);
	}

}
