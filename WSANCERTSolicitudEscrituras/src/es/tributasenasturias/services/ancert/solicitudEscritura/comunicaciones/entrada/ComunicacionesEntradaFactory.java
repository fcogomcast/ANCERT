/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.entrada;

import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextManager;

/** Contiene métodos de factoría para los elementos de las comunicaciones externas.
 * @author crubencvs
 *
 */
public class ComunicacionesEntradaFactory {
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
	 * Devuelve un objeto {@link ExtractorDatosSolicitud} para extraer los datos de solicitud de los parámetros de entrada. 
	 * @param context Contexto de llamada en el que se creará este objeto. El contexto tendrá información 
	 *   sobre la llamada particular.
	 * @return Objeto ExtractorDatosSolicitud
	 * @throws ComunicacionesEntradaException En caso de fallo al construir.
	 */
	public static ExtractorDatosSolicitud newExtractorDatosSolicitud(CallContext context) throws ComunicacionesEntradaException
	{
		ExtractorDatosSolicitud dat=new ExtractorDatosSolicitud();
		dat.setCallContext(getContext(context));
		return dat;
	}
	
}
