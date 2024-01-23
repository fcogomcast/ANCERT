package es.stpa.plusvalias.exceptions;

public class ConversionException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4509165130035290738L;
	private String codigoError;
	private String mensajeError;

	public ConversionException(String codigoError,String message, Throwable cause ) {
		super(message,cause);
		this.codigoError = codigoError;
	}
	
	public ConversionException(String codigoError, String mensajeError, String message, Throwable cause ) {
		super(message,cause);
		this.codigoError = codigoError;
		this.mensajeError= mensajeError;
	}
	
	public ConversionException(String codigoError,Throwable cause) {
		super(cause);
		this.codigoError = codigoError;
	}

    
	public ConversionException(String codigoError) {
		super();
		this.codigoError = codigoError;
	}
	
	public ConversionException(String codigoError, String mensajeError) {
		super();
		this.codigoError = codigoError;
		this.mensajeError= mensajeError;
	}

	public String getCodigoError() {
		return codigoError;
	}
	
	public String getMensajeError(){
		return mensajeError;
	}
	
	
	
}
