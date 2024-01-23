/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.utils.log;

import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper.TIPOLOG;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;

/** Implementa la funcionalidad de creación de logs. Debería utilizarse esta, y no la creación directa,
 *  porque se ocupa de la inicialización correctamente.
 * @author crubencvs
 *
 */
public class LogFactory {

	/**
	 * Devuelve una instancia {@link LogHelper} que permite realizar log de aplicación.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @param pref Preferencias de la llamada
	 * @return instancia de LogHelper
	 */
	public static LogHelper newApplicationLog(String idsesion ,Preferencias pref)
	{
		LogHelper log=new LogHelper(TIPOLOG.APLICACION, pref);
		log.setSessionId(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia {@link LogHelper} que permite realizar log de mensajes de servidor.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @param pref Preferencias de la llamada
	 * @return instancia de LogHelper
	 */
	public static LogHelper newSoapServerLog (String idsesion, Preferencias pref)
	{
		LogHelper log=new LogHelper(TIPOLOG.SOAPSERVER, pref);
		log.setSessionId(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia {@link LogHelper} que permite realizar log de mensajes hacia servicios web externos.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @param pref Preferencias de la llamada
	 * @return instancia de LogHelper
	 */
	public static LogHelper newSoapClientLog(String idsesion, Preferencias pref)
	{
		LogHelper log=new LogHelper(TIPOLOG.SOAPCLIENTE, pref);
		log.setSessionId(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia de log de aplicación, como un {@link LogHelper} que extrae de contexto. 
	 * @param context Contexto {@link CallContext} que contiene objetos de contexto.
	 * @return instancia de log que extrae del contexto.
	 */
	public static LogHelper getLogAplicacionContexto(CallContext context)
	{
		if (context==null)
			return null;
		LogHelper log=(LogHelper)context.get(CallContextConstants.LOG_APLICACION);
		if (log==null)
		{
			//No se ha podido recuperar de contexto.
			log=newApplicationLog("Sin información de sesión.", (Preferencias) context.get(CallContextConstants.PREFERENCIAS));
		}
		return log;
	}
}
