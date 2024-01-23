/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions;

/**
 * Excepción que puede lanzar la clase de protocolo.
 * @author crubencvs
 *
 */
public class ProtocoloException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5288339038113878492L;

	public ProtocoloException(String error, Throwable original) {
		super("ProtocoloDO::" + error, original);
	}

	public ProtocoloException(String error) {
		super("ProtocoloDO::"+ error);
	}

}
