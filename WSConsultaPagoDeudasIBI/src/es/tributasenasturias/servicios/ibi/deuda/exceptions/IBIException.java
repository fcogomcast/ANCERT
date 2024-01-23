package es.tributasenasturias.servicios.ibi.deuda.exceptions;

public class IBIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1915753148277701057L;
	private String codError;
	private String textError;
	
	/**
	 * @return the codError
	 */
	public String getCodError() {
		return codError;
	}

	/**
	 * @return the textError
	 */
	public String getTextError() {
		return textError;
	}

	public IBIException() {
		super();
	}

	public IBIException(String message, Throwable cause) {
		super(message, cause);
	}

	public IBIException(String message) {
		super(message);
	}
	public IBIException(String message, String codError, String textError) {
		super(message);
		this.codError= codError;
		this.textError = textError;
	}
	public IBIException(String message, String codError, String textError, Throwable cause)
	{
		super(message,cause);
		this.codError = codError;
		this.textError=  textError;
	}
	

}
