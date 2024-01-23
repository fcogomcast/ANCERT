/**
 * 
 */
package org.notariado.inti.preferencias;

import org.notariado.inti.contexto.CallContext;
import org.notariado.inti.contexto.CallContextConstants;





/** Implementa una factoría de preferencias. Se podrá crear una nueva instancia o bien recuperar
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
