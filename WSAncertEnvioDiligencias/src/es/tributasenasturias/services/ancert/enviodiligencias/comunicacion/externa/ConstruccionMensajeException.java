/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa;


/**
 * @author crubencvs
 *
 */
public class ConstruccionMensajeException extends Exception {

	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4199723003943430631L;
	public ConstruccionMensajeException(String error, Throwable original, Object lanzador, String modulo){
		super("["+ConstruccionMensajeException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error,original);
	}
	public ConstruccionMensajeException(String error, Object lanzador, String modulo){
		super("["+ConstruccionMensajeException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error);
	}
	public ConstruccionMensajeException(String error, Throwable original){
		super("["+ConstruccionMensajeException.class.getSimpleName()+"]::"+error,original);
	}
	public ConstruccionMensajeException (String error)
	{
		super("["+ConstruccionMensajeException.class.getSimpleName()+"]::"+error);
	}
	

}
