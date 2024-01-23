package es.tributasenasturias.services.ancert.recepcionescritura.validacion;

import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.BaseException;

public class ValidacionException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4783570369479550470L;

	public ValidacionException(String error, Throwable original) {
		super(error, original);
	}

	public ValidacionException(String error) {
		super(error);
	}

}
