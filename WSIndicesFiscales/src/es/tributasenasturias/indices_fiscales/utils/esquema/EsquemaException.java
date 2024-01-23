package es.tributasenasturias.indices_fiscales.utils.esquema;

public class EsquemaException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5243358564538732206L;

	public EsquemaException(String message, Throwable cause) {
		super("EsquemaException:"+ message, cause);
	}

	public EsquemaException(String message) {
		super("EsquemaException:"+message);
	}
	
}
