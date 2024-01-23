/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.preferencias;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextConstants;



/** Implementa una factoría de preferencias. Se podrá crear una nueva instancia o bien recuperar
 *  la que exista en un contexto de llamada.
 * @author crubencvs
 *
 */
public class PreferenciasFactory {
	/**
	 * Recupera una nueva instancia de Preferencias
	 * @return Preferencias
	 * @throws PreferenciasException Si no se pueden crear las preferencias.
	 */
	public static Preferencias newInstance() throws PreferenciasException
	{
		return new Preferencias();
	}
	public static Preferencias getPreferenciasContexto(CallContext context)
	{
		if (context==null)
		{
			return null;
		}
		Preferencias pref=(Preferencias)context.get(CallContextConstants.PREFERENCIAS);
		return pref;
		
	}
	
}
