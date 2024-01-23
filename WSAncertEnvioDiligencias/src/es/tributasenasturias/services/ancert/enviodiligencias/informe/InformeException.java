/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.informe;


/**
 * @author crubencvs
 *
 */
public class InformeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3937877566447300467L;
	
	public InformeException(String error, Throwable original, Object lanzador, String modulo){
		super("["+InformeException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error,original);
	}
	public InformeException(String error, Object lanzador, String modulo){
		super("["+InformeException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error);
	}
	public InformeException(String error, Throwable original){
		super("["+InformeException.class.getSimpleName()+"]::"+error,original);
	}
	public InformeException (String error)
	{
		super("["+InformeException.class.getSimpleName()+"]::"+error);
	}
	

}
