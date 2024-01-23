package es.tributasenasturias.services.ancert.recepcionescritura.utils;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextManager;


/** Permite construir los objetos de firma. 
 * @author crubencvs
 *
 */
public class FirmaFactory {
	/**
	 * Recupera el contexto, bien el que se le pasa por parámetro si existe, o uno nuevo que crea.
	 * @param context Contexto {@link CallContext} que se devolverá si no es nulo.
	 * @return
	 */
	private static CallContext getContext(CallContext context)
	{
		CallContext con=null;
		if (context !=null)
		{
			con=context;
		}
		else
		{
			con = CallContextManager.newCallContext();
		}
		return con;
	}
	/**
	 * Devuelve un objeto FirmaHelper para procesar las  firmas.
	 * @param context Contexto de llamada desde el que se creará este objeto. El contexto tendrá 
	 *  información de la llamada particular.
	 * @return
	 */
	public static FirmaHelper newFirmaHelper(CallContext context)
	{
		FirmaHelper fih= new FirmaHelper();
		fih.setCallContext(getContext(context));
		return fih;
	}
}
