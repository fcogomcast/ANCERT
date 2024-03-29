package es.tributasenasturias.servicios.ibi.deuda.exceptions;

/**
 * Errores relacionados con carga y conversión de mensajes de la aplicación.
 * @author crubencvs
 *
 */
public class MensajesException  extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -210495803024480941L;

	public MensajesException() {
		super();
	}

	public MensajesException(String message, Throwable cause) {
		super("["+MensajesException.class.getName()+"]:"+ message, cause);
	}

	public MensajesException(String message) {
		super("["+MensajesException.class.getName() +"]"+message);
	}

}
