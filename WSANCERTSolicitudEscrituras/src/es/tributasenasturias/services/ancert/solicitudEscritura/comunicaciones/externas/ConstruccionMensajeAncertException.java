/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.BaseException;


/**
 * @author crubencvs
 *
 */
public class ConstruccionMensajeAncertException extends BaseException {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8812593719864110478L;

	public ConstruccionMensajeAncertException(String error, Throwable original, Object lanzador) {
		super("ConstruccionMensajeAncertException::"+lanzador.getClass().getName()+"::"+error, original);
	}
	
	public ConstruccionMensajeAncertException(String error, Object lanzador) {
		super("ConstruccionMensajeAncertException::"+lanzador.getClass().getName()+"::"+error);
	}
	
	public ConstruccionMensajeAncertException(String error, Throwable original) {
		super("ConstruccionMensajeAncertException::"+error, original);
	}

	public ConstruccionMensajeAncertException(String error) {
		super("ConstruccionMensajeAncertException::" + error);
	}

}
