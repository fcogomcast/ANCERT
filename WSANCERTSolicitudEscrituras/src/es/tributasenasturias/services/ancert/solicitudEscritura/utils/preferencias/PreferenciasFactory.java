/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.PreferenciasException;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextConstants;

/** Implementa una factor�a de preferencias. Se podr� crear una nueva instancia o bien recuperar
 *  la que exista en un contexto de llamada.
 * @author crubencvs
 *
 */
public class PreferenciasFactory {
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
