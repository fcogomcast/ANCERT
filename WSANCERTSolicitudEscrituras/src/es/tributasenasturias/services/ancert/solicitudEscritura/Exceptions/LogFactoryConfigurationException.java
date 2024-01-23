package es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions;

public class LogFactoryConfigurationException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1930920525907430571L;

	public LogFactoryConfigurationException(String error, Throwable original) {
		super("SeguridadException::" +  error, original);
	}

	public LogFactoryConfigurationException(String error) {
		super("SeguridadException::"+ error);
	}

}
