/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.BaseException;


/**
 * @author crubencvs
 *
 */
public class MensajeriaException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6533734080044276496L;

	public MensajeriaException(String error, Throwable original, Object lanzador) {
		super("ComunicacionesEntradaException::"+lanzador.getClass().getName()+"::"+error, original);
	}
	
	public MensajeriaException(String error, Object lanzador) {
		super("ComunicacionesEntradaException::"+lanzador.getClass().getName()+"::"+error);
	}
	
	public MensajeriaException(String error, Throwable original) {
		super("ComunicacionesEntradaException::"+error, original);
	}

	public MensajeriaException(String error) {
		super("ComunicacionesEntradaException::" + error);
	}

}
