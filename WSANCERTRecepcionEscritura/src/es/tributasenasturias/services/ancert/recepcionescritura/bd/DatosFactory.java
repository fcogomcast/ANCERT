/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.bd;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextManager;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;


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
	 * Recupera un objeto que permite realizar comprobaciones sobre el certificado en base de datos.
	 * @param context Contexto de llamada.
	 * @return GestorCertificadosBD.
	 * @throws SystemException
	 */
	public static GestorCertificadosBD newGestorCertificadosBD(CallContext context) throws SystemException
	{
		GestorCertificadosBD dat=new GestorCertificadosBD(context);
		dat.setCallContext(getContext(context));
		return dat;
	}
}
