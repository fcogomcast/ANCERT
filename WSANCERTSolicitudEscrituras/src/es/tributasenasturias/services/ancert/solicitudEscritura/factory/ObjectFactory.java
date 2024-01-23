/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.factory;

import es.tributasenasturias.services.ancert.solicitudEscritura.SolicitudEscrituraImpl;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.GestorSolicitudesBD;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextManager;

/** Contiene m�todos de factor�a para cada elemento que necesita el servicio y est�n bajo
 *   es.tributasenasturias.services.ancert.solicitudEscritura.
 * @author crubencvs
 *
 */
public class ObjectFactory {
	/**
	 * Recupera el contexto, bien el que se le pasa por par�metro si existe, o uno nuevo que crea.
	 * @param context Contexto {@link CallContext} que se devolver� si no es nulo.
	 * @return
	 */
	private CallContext getContext(CallContext context)
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
	 * Devuelve un objeto {@link GestorSolicitudesBD} para conexi�n con base de datos. 
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n 
	 *   sobre la llamada particular.
	 * @return Objeto GestorSolicitudesBD
	 * @throws SystemException En caso de fallo al construir.
	 */
	public GestorSolicitudesBD newDatos(CallContext context) throws SystemException
	{
		GestorSolicitudesBD dat=new GestorSolicitudesBD();
		dat.setCallContext(getContext(context));
		dat.preparaConexion();
		return dat;
	}
	/**
	 * Devuelve un objeto SolicitudEscrituraImpl para procesar la solicitud de escritura.
	 * Este m�todo deber�a llamarse desde el endpoint de servicio, para realizar la solicitud.
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n
	 *  de la llamada particular.
	 * @return Objeto SolicitudEscrituraImpl
	 */
	public SolicitudEscrituraImpl newSolicitudEscritura(CallContext context)
	{
		SolicitudEscrituraImpl sl = new SolicitudEscrituraImpl();
		sl.setCallContext(getContext(context));
		return sl;
	}
	
	
}
