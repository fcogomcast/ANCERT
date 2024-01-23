/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.bd;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.GestorSolicitudesBD;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextManager;

/** Contiene métodos de factoría para cada elemento que necesita el servicio y están bajo
 *   es.tributasenasturias.services.ancert.solicitudEscritura.
 * @author crubencvs
 *
 */
public class DatosFactory {
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
	 * Devuelve un objeto {@link GestorSolicitudesBD} para conexión con base de datos. 
	 * @param context Contexto de llamada en el que se creará este objeto. El contexto tendrá información 
	 *   sobre la llamada particular.
	 * @return Objeto GestorSolicitudesBD
	 * @throws SystemException En caso de fallo al construir.
	 */
	public static GestorSolicitudesBD newGestorSolicitudesBD(CallContext context) throws SystemException
	{
		GestorSolicitudesBD dat=new GestorSolicitudesBD();
		dat.setCallContext(getContext(context));
		dat.preparaConexion();
		return dat;
	}
	public static GestorCertificadosBD newGestorCertificadosBD(CallContext context) throws SystemException
	{
		GestorCertificadosBD dat=new GestorCertificadosBD(context);
		dat.setCallContext(getContext(context));
		return dat;
	}
}
