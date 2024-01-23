package es.tributasenasturias.services.ancert.enviodiligencias;

public class SystemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7922738612837912856L;
	
	public SystemException(String error, Throwable original, Object lanzador, String modulo){
		super("["+SystemException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error,original);
	}
	public SystemException(String error, Object lanzador, String modulo){
		super("["+SystemException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error);
	}
	public SystemException(String error, Throwable original){
		super("["+SystemException.class.getSimpleName()+"]::"+error,original);
	}
	public SystemException (String error)
	{
		super("["+SystemException.class.getSimpleName()+"]::"+error);
	}

}
