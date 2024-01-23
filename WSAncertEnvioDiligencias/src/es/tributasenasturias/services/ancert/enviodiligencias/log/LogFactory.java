/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.log;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContextConstants;



/** Implementa la funcionalidad de creación de logs. Debería utilizarse esta, y no la creación directa,
 *  porque se ocupa de la inicialización correctamente.
 * @author crubencvs
 *
 */
public class LogFactory {

	/**
	 * Devuelve una instancia {@link LogAplicacion} que permite realizar log de aplicación.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @return instancia de LogAplicacion
	 */
	public static LogAplicacion newApplicationLog(String idsesion)
	{
		LogAplicacion log=new LogAplicacion(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia de log de aplicación, como un {@link LogAplicacion} con id de sesión de contexto. 
	 * Si no existiera contexto, crea un nuevo log sin información de sesión.
	 * @param context Contexto {@link CallContext} que contiene objetos de contexto. Se asegura que nunca devuelve un log nulo.
	 * @return instancia de log que extrae del contexto.
	 */
	
	public static LogAplicacion getLogAplicacionContexto(CallContext context)
	{
		if (context==null)
			return newApplicationLog("Sin sesión conocida");
		String idsesion=(String)context.get(CallContextConstants.IDSESION);
		LogAplicacion log=LogFactory.newApplicationLog(idsesion);
		if (log==null)
		{
			//No se ha podido recuperar de contexto.
			log=newApplicationLog("Sin información de sesión.");
		}
		return log;
	}
	/**
	 * Devuelve una instancia {@link LogSoapserver} que permite realizar log de mensajes que entran al servicio.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @return instancia de LogSoapServer
	 */
	public static LogSoapserver newLogSoapServer(String idsesion)
	{
		LogSoapserver log=new LogSoapserver(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia de log de mensajes petición y respuesta al servicio, como un {@link LogSoapserver} con id de sesión de contexto. 
	 * Si no existiera contexto, crea un nuevo log sin información de sesión.
	 * @param context Contexto {@link CallContext} que contiene objetos de contexto. Se asegura que nunca devuelve un log nulo.
	 * @return instancia de log que extrae del contexto.
	 */
	
	public static LogSoapserver getLogSoapServerContexto(CallContext context)
	{
		if (context==null)
			return newLogSoapServer("Sin sesión conocida");
		String idsesion=(String)context.get(CallContextConstants.IDSESION);
		LogSoapserver log=LogFactory.newLogSoapServer(idsesion);
		if (log==null)
		{
			//No se ha podido recuperar de contexto.
			log=newLogSoapServer("Sin información de sesión.");
		}
		return log;
	}
	/**
	 * Devuelve una instancia {@link LogSoapClient} que permite realizar log de mensajes que realiza el servicio a otros.
	 * @param idsesion Id de la sesión que aparecerá en todas las líneas de log, o null si no se desea identificar la sesión.
	 * @return instancia de LogSoapClient
	 */
	public static LogSoapClient newLogSoapClient(String idsesion)
	{
		LogSoapClient log=new LogSoapClient(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia de log de comunicación del servicio con otros servicios, como un {@link LogSoapClient} con id de sesión de contexto. 
	 * Si no existiera contexto, crea un nuevo log sin información de sesión.
	 * @param context Contexto {@link CallContext} que contiene objetos de contexto. Se asegura que nunca devuelve un log nulo.
	 * @return instancia de log que extrae del contexto.
	 */
	
	public static LogSoapClient getLogSoapClientContexto(CallContext context)
	{
		if (context==null)
			return newLogSoapClient("Sin sesión conocida");
		String idsesion=(String)context.get(CallContextConstants.IDSESION);
		LogSoapClient log=LogFactory.newLogSoapClient(idsesion);
		if (log==null)
		{
			//No se ha podido recuperar de contexto.
			log=newLogSoapClient("Sin información de sesión.");
		}
		return log;
	}
}
