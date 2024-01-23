package es.tributasenasturias.services.ancert.recepcionescritura.seguridad;


public class SeguridadException extends Exception {



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
