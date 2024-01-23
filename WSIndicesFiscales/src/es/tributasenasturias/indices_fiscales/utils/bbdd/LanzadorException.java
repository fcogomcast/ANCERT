/**
 * 
 */
package es.tributasenasturias.indices_fiscales.utils.bbdd;

/**
 * @author crubencvs
 *
 */
public class LanzadorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8634000519134188967L;

	/**
	 * 
	 */
	public LanzadorException() {
		super();
	}

	/**
	 * @param message
	 */
	public LanzadorException(String message) {
		super(LanzadorException.class.getName()+"::"+message);
	}

	/**
	 * @param cause
	 */
	public LanzadorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LanzadorException(String message, Throwable cause) {
		super(LanzadorException.class.getName()+"::"+message, cause);
	}

}
