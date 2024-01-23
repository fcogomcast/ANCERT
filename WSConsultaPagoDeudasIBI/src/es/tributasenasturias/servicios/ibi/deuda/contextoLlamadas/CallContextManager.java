/**
 * 
 */
package es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas;

/**Implementa un factory para recuperar un contexto de llamada que se podrá utilizar en el servicio
 * para guardar información relacionada con esa llamada, como por ejemplo las preferencias a aplicar.
 * @author crubencvs
 *
 */
public class CallContextManager {
	/**
	 * Recupera una instancia de CallContext para almacenar el contexto de la llamada.
	 * @return instancia {@link CallContext} 
	 */
	public static CallContext newCallContext()
	{
		return new CallContext();
	}
}
