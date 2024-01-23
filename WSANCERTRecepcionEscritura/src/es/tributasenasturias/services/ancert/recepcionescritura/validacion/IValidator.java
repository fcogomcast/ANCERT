package es.tributasenasturias.services.ancert.recepcionescritura.validacion;

import java.util.List;

/**
 * Funcionalidad común de los validadores.<br/>
 * Deberán implementar un método "validate" que validará el objeto,<br/>
 * un método "isValid" que indicará si el objeto validado es correcto o no,<br/>
 * y un método "getMessages" que devolverá los mensajes de validación.<br/>
 * Aparte de esto, cada objeto que implemente este interfaz podrá implementar los métodos necesarios.
 * @author crubencvs
 * @param <T>
 *
 */
public interface IValidator<T> {
	
	/**
	 * Valida el objeto, según los criterios que cada instancia considere correctos.
	 * @param objeto . Objeto a validar.
	 * @return Nada
	 */
	void validate(T objeto);
	/**Método que devuelve un objeto indicando si el objeto a validar es correcto o no.
	 * @return - true si el objeto es válido, false si no.
	 */

	boolean isValid();
	/**Devuelve un objeto que representa los mensajes generados durante la validación.
	 * 
	 * @return - Resultados de validación (en formato de array)
	 */
	List<String> getMessages();
	/**
	 * Devuelve una cadena formateada con todos los mensajes generados durante la validación.
	 * @return - Resultados de validación (como cadena).
	 */
	String getStringMessages();
}
