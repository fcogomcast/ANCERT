/**
 * 
 */
package es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas;

import java.util.HashMap;
import java.util.Map;

/** Implementa un contexto de llamada. Guardará información relacionada con cada llamada.
 * @author crubencvs
 *
 */
public class CallContext {
	/**
	 * Constructor. Protegido, se deberá utilizar un objeto {@link CallContextManager} para recuperarlo.
	 */
	protected CallContext()
	{
		items=new HashMap<String,Object>();
	}
	//Elementos de contexto. El tipo específico es desconocido, se convierten a Object.
	//Los objetos que pregunten por ellos deberán saber el tipo que van a manejar.
	private Map<String,Object> items; 
	/**
	 * Añade un elemento al contexto
	 * @param name Nombre del elemento.
	 * @param value Valor del elemento
	 */
	public void setItem(String name, Object value)
	{
		items.put(name,value);
	}
	/**
	 * Recupera un elemento del contexto
	 * @param name Nombre {@link java.lang.String} del elemento a recuperar.
	 * @return Elemento con ese nombre, o null si no se encuentra.
	 */
	public Object get(String name)
	{
		if (items.isEmpty())
		{
			return null;
		}
		if (items.containsKey(name))
		{
			return items.get(name);
		}
		else
		{
			return null;
		}
	}
}
