/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.entrada;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.BaseException;


/**
 * @author crubencvs
 *
 */
public class ComunicacionesEntradaException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6533734080044276496L;

	public ComunicacionesEntradaException(String error, Throwable original, Object lanzador) {
		super("ComunicacionesEntradaException::"+lanzador.getClass().getName()+"::"+error, original);
	}
	
	public ComunicacionesEntradaException(String error, Object lanzador) {
		super("ComunicacionesEntradaException::"+lanzador.getClass().getName()+"::"+error);
	}
	
	public ComunicacionesEntradaException(String error, Throwable original) {
		super("ComunicacionesEntradaException::"+error, original);
	}

	public ComunicacionesEntradaException(String error) {
		super("ComunicacionesEntradaException::" + error);
	}

}
