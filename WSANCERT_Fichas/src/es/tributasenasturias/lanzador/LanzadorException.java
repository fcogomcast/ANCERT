/**
 * 
 */
package es.tributasenasturias.lanzador;

/**
 * @author crubencvs
 *
 */
public class LanzadorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1630455891396167056L;

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
