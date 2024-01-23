/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.documentos;


/**
 * @author crubencvs
 *
 */
public class DocumentoException extends Exception {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3807758531921185004L;
	public DocumentoException(String error, Throwable original, Object lanzador, String modulo){
		super("["+DocumentoException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error,original);
	}
	public DocumentoException(String error, Object lanzador, String modulo){
		super("["+DocumentoException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error);
	}
	public DocumentoException(String error, Throwable original){
		super("["+DocumentoException.class.getSimpleName()+"]::"+error,original);
	}
	public DocumentoException (String error)
	{
		super("["+DocumentoException.class.getSimpleName()+"]::"+error);
	}
	

}
