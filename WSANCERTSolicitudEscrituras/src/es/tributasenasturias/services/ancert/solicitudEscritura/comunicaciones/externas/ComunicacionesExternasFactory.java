/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas.MensajeriaException;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas.MensajeroANCERT;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextManager;

/** Contiene m�todos de factor�a para los elementos de las comunicaciones externas.
 * @author crubencvs
 *
 */
public class ComunicacionesExternasFactory {
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
	 * Devuelve un objeto {@link MensajeroANCERT} para conexi�n con el servicio remoto de ANCERT
	 * que recibir� la solicitud de escritura.
	 * @param context Contexto {@link CallContext} de llamada en el que se crear� este objeto. El contexto
	 * tendr� informaci�n sobre la llamada particular.
	 * @return Objeto MensajeroANCERT
	 * @throws SystemException En caso de fallo en construcci�n.
	 */
	public static MensajeroANCERT newMensajeroANCERT(CallContext context) throws MensajeriaException,SystemException
	{
		MensajeroANCERT servDis=null;
		servDis = new MensajeroANCERT(getContext(context));
		return servDis;
	}
	/**
	 * Devuelve un objeto {@link MensajeANCERT} para construir el mensaje con ANCERT. 
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n 
	 *   sobre la llamada particular.
	 * @return Objeto MensajeANCERT
	 * @throws ConstruccionMensajeAncertException En caso de fallo al construir.
	 */
	public static MensajeANCERT newMensajeANCERT(CallContext context) throws ConstruccionMensajeAncertException
	{
		MensajeANCERT dat=new MensajeANCERT();
		dat.setCallContext(getContext(context));
		return dat;
	}
	/**
	 * Devuelve un objeto ExtractorRespuestaAncert para  extraer la respuesta recibida desde ANCERT.
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n
	 *  de la llamada particular.
	 * @return Objeto ExtractorRespuestaAncert
	 */
	public static ExtractorRespuestaAncert newExtractorRespuestaAncert(CallContext context)
	{
		ExtractorRespuestaAncert obj = new ExtractorRespuestaAncert();
		return obj;
	}
	
}
