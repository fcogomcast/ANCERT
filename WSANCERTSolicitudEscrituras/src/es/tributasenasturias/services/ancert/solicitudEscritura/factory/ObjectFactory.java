/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.factory;

import es.tributasenasturias.services.ancert.solicitudEscritura.SolicitudEscrituraImpl;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.GestorSolicitudesBD;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextManager;

/** Contiene métodos de factoría para cada elemento que necesita el servicio y están bajo
 *   es.tributasenasturias.services.ancert.solicitudEscritura.
 * @author crubencvs
 *
 */
public class ObjectFactory {
	/**
	 * Recupera el contexto, bien el que se le pasa por parámetro si existe, o uno nuevo que crea.
	 * @param context Contexto {@link CallContext} que se devolverá si no es nulo.
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
	 * Devuelve un objeto {@link GestorSolicitudesBD} para conexión con base de datos. 
	 * @param context Contexto de llamada en el que se creará este objeto. El contexto tendrá información 
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
	 * Este método debería llamarse desde el endpoint de servicio, para realizar la solicitud.
	 * @param context Contexto de llamada en el que se creará este objeto. El contexto tendrá información
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
