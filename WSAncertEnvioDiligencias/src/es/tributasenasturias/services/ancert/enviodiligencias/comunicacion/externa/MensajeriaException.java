/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa;


/**
 * @author crubencvs
 *
 */
public class MensajeriaException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1619861403653105163L;
	
	public MensajeriaException(String error, Throwable original, Object lanzador, String modulo){
		super("["+MensajeriaException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error,original);
	}
	public MensajeriaException(String error, Object lanzador, String modulo){
		super("["+MensajeriaException.class.getSimpleName()+" en "+lanzador.getClass().getSimpleName()+"."+modulo+"]::"+error);
	}
	public MensajeriaException(String error, Throwable original){
		super("["+MensajeriaException.class.getSimpleName()+"]::"+error,original);
	}
	public MensajeriaException (String error)
	{
		super("["+MensajeriaException.class.getSimpleName()+"]::"+error);
	}
	

}
