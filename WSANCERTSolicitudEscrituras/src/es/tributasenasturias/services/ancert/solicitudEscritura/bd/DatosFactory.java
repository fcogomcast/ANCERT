/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.bd;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.GestorSolicitudesBD;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextManager;

/** Contiene m�todos de factor�a para cada elemento que necesita el servicio y est�n bajo
 *   es.tributasenasturias.services.ancert.solicitudEscritura.
 * @author crubencvs
 *
 */
public class DatosFactory {
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
	 * Devuelve un objeto {@link GestorSolicitudesBD} para conexi�n con base de datos. 
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n 
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
