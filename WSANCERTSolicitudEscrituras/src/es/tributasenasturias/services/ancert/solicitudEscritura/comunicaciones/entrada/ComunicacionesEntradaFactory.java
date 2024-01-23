/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.entrada;

import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextManager;

/** Contiene m�todos de factor�a para los elementos de las comunicaciones externas.
 * @author crubencvs
 *
 */
public class ComunicacionesEntradaFactory {
	/**
	 * Recupera el contexto, bien el que se le pasa por par�metro si existe, o uno nuevo que crea.
	 * @param context Contexto {@link CallContext} que se devolver� si no es nulo.
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
	 * Devuelve un objeto {@link ExtractorDatosSolicitud} para extraer los datos de solicitud de los par�metros de entrada. 
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n 
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
