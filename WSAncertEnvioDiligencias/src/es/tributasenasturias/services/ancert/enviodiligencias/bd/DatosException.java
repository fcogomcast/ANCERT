/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.bd;

/**
 * @author crubencvs
 *
 */
public class DatosException extends Exception {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3051800579294077241L;
	public DatosException(String error, Throwable original, Object lanzador, String modulo){
		super("["+DatosException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error,original);
	}
	public DatosException(String error, Object lanzador, String modulo){
		super("["+DatosException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error);
	}
	public DatosException(String error, Throwable original){
		super("["+DatosException.class.getSimpleName()+"]::"+error,original);
	}
	public DatosException (String error)
	{
		super("["+DatosException.class.getSimpleName()+"]::"+error);
	}
	

}
