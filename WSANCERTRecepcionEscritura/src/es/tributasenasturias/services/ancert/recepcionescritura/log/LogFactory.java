/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.log;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper.TIPOLOG;


/** Implementa la funcionalidad de creación de logs. Debería utilizarse esta, y no la creación directa,
 *  porque se ocupa de la inicialización correctamente.
 * @author crubencvs
 *
 */
public class LogFactory {

	/**
	 * Devuelve una instancia {@link LogHelper} que permite realizar log de aplicación.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @return instancia de LogHelper
	 */
	public static LogHelper newApplicationLog(String idsesion)
	{
		LogHelper log=new LogHelper(TIPOLOG.APLICACION);
		log.setSessionId(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia {@link LogHelper} que permite realizar log de mensajes de servidor.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @return instancia de LogHelper
	 */
	public static LogHelper newSoapServerLog (String idsesion)
	{
		LogHelper log=new LogHelper(TIPOLOG.SOAPSERVER);
		log.setSessionId(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia {@link LogHelper} que permite realizar log de mensajes hacia servicios web externos.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @return instancia de LogHelper
	 */
	public static LogHelper newSoapClientLog(String idsesion)
	{
		LogHelper log=new LogHelper(TIPOLOG.SOAPCLIENTE);
		log.setSessionId(idsesion);
		return log;
	}
	
	/**
	 * Devuelve una instancia de log de aplicación, como un {@link LogHelper} que extrae de contexto. 
	 * Si no existiera contexto, crea un nuevo log sin información de sesión.
	 * @param context Contexto {@link CallContext} que contiene objetos de contexto.
	 * @return instancia de log que extrae del contexto.
	 */
	
	public static LogHelper getLogAplicacionContexto(CallContext context)
	{
		if (context==null)
			return newApplicationLog("Sin sesión conocida");
		String idsesion=(String)context.get(CallContextConstants.IDSESION);
		LogHelper log=LogFactory.newApplicationLog(idsesion);
		if (log==null)
		{
			//No se ha podido recuperar de contexto.
			log=newApplicationLog("Sin información de sesión.");
		}
		return log;
	}
	
}
