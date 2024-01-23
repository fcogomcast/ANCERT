package es.tributasenasturias.services.ancert.solicitudEscritura.seguridad;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.BaseException;

public class SeguridadException extends BaseException {



	/**
	 * 
	 */
	private static final long serialVersionUID = -3836802667520948506L;

	public SeguridadException(String error, Throwable original) {
		super("SeguridadException::" +  error, original);
	}

	public SeguridadException(String error) {
		super("SeguridadException::"+ error);
	}

}
