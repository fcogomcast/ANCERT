package es.tributasenasturias.services.ancert.recepcionescritura.validacion;

import java.util.List;

/**
 * Funcionalidad com�n de los validadores.<br/>
 * Deber�n implementar un m�todo "validate" que validar� el objeto,<br/>
 * un m�todo "isValid" que indicar� si el objeto validado es correcto o no,<br/>
 * y un m�todo "getMessages" que devolver� los mensajes de validaci�n.<br/>
 * Aparte de esto, cada objeto que implemente este interfaz podr� implementar los m�todos necesarios.
 * @author crubencvs
 * @param <T>
 *
 */
public interface IValidator<T> {
	
	/**
	 * Valida el objeto, seg�n los criterios que cada instancia considere correctos.
	 * @param objeto . Objeto a validar.
	 * @return Nada
	 */
	void validate(T objeto);
	/**M�todo que devuelve un objeto indicando si el objeto a validar es correcto o no.
	 * @return - true si el objeto es v�lido, false si no.
	 */

	boolean isValid();
	/**Devuelve un objeto que representa los mensajes generados durante la validaci�n.
	 * 
	 * @return - Resultados de validaci�n (en formato de array)
	 */
	List<String> getMessages();
	/**
	 * Devuelve una cadena formateada con todos los mensajes generados durante la validaci�n.
	 * @return - Resultados de validaci�n (como cadena).
	 */
	String getStringMessages();
}
