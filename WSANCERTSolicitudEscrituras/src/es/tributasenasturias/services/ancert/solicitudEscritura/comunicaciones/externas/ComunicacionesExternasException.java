/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.BaseException;


/**
 * @author crubencvs
 *
 */
public class ComunicacionesExternasException extends BaseException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6412666492637642807L;

	public ComunicacionesExternasException(String error, Throwable original, Object lanzador) {
		super("["+ComunicacionesExternasException.class.getName()+"::"+lanzador.getClass().getName()+"]::"+error, original);
	}
	
	public ComunicacionesExternasException(String error, Object lanzador) {
		super("["+ComunicacionesExternasException.class.getName()+"::"+lanzador.getClass().getName()+"::]"+error);
	}
	
	public ComunicacionesExternasException(String error, Throwable original) {
		super("["+ComunicacionesExternasException.class.getName()+"]::"+error, original);
	}

	public ComunicacionesExternasException(String error) {
		super("["+ComunicacionesExternasException.class.getName()+"]::" + error);
	}

}
