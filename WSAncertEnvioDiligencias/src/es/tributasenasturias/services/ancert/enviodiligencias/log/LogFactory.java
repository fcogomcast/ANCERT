/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.log;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContextConstants;



/** Implementa la funcionalidad de creaci�n de logs. Deber�a utilizarse esta, y no la creaci�n directa,
 *  porque se ocupa de la inicializaci�n correctamente.
 * @author crubencvs
 *
 */
public class LogFactory {

	/**
	 * Devuelve una instancia {@link LogAplicacion} que permite realizar log de aplicaci�n.
	 * @param idsesion Id de la sesi�n que aparecer� en todas las l�neas de log, o null si no se desea identificar la sesi�n.
	 * @return instancia de LogAplicacion
	 */
	public static LogAplicacion newApplicationLog(String idsesion)
	{
		LogAplicacion log=new LogAplicacion(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia de log de aplicaci�n, como un {@link LogAplicacion} con id de sesi�n de contexto. 
	 * Si no existiera contexto, crea un nuevo log sin informaci�n de sesi�n.
	 * @param context Contexto {@link CallContext} que contiene objetos de contexto. Se asegura que nunca devuelve un log nulo.
	 * @return instancia de log que extrae del contexto.
	 */
	
	public static LogAplicacion getLogAplicacionContexto(CallContext context)
	{
		if (context==null)
			return newApplicationLog("Sin sesi�n conocida");
		String idsesion=(String)context.get(CallContextConstants.IDSESION);
		LogAplicacion log=LogFactory.newApplicationLog(idsesion);
		if (log==null)
		{
			//No se ha podido recuperar de contexto.
			log=newApplicationLog("Sin informaci�n de sesi�n.");
		}
		return log;
	}
	/**
	 * Devuelve una instancia {@link LogSoapserver} que permite realizar log de mensajes que entran al servicio.
	 * @param idsesion Id de la sesi�n que aparecer� en todas las l�neas de log, o null si no se desea identificar la sesi�n.
	 * @return instancia de LogSoapServer
	 */
	public static LogSoapserver newLogSoapServer(String idsesion)
	{
		LogSoapserver log=new LogSoapserver(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia de log de mensajes petici�n y respuesta al servicio, como un {@link LogSoapserver} con id de sesi�n de contexto. 
	 * Si no existiera contexto, crea un nuevo log sin informaci�n de sesi�n.
	 * @param context Contexto {@link CallContext} que contiene objetos de contexto. Se asegura que nunca devuelve un log nulo.
	 * @return instancia de log que extrae del contexto.
	 */
	
	public static LogSoapserver getLogSoapServerContexto(CallContext context)
	{
		if (context==null)
			return newLogSoapServer("Sin sesi�n conocida");
		String idsesion=(String)context.get(CallContextConstants.IDSESION);
		LogSoapserver log=LogFactory.newLogSoapServer(idsesion);
		if (log==null)
		{
			//No se ha podido recuperar de contexto.
			log=newLogSoapServer("Sin informaci�n de sesi�n.");
		}
		return log;
	}
	/**
	 * Devuelve una instancia {@link LogSoapClient} que permite realizar log de mensajes que realiza el servicio a otros.
	 * @param idsesion Id de la sesi�n que aparecer� en todas las l�neas de log, o null si no se desea identificar la sesi�n.
	 * @return instancia de LogSoapClient
	 */
	public static LogSoapClient newLogSoapClient(String idsesion)
	{
		LogSoapClient log=new LogSoapClient(idsesion);
		return log;
	}
	/**
	 * Devuelve una instancia de log de comunicaci�n del servicio con otros servicios, como un {@link LogSoapClient} con id de sesi�n de contexto. 
	 * Si no existiera contexto, crea un nuevo log sin informaci�n de sesi�n.
	 * @param context Contexto {@link CallContext} que contiene objetos de contexto. Se asegura que nunca devuelve un log nulo.
	 * @return instancia de log que extrae del contexto.
	 */
	
	public static LogSoapClient getLogSoapClientContexto(CallContext context)
	{
		if (context==null)
			return newLogSoapClient("Sin sesi�n conocida");
		String idsesion=(String)context.get(CallContextConstants.IDSESION);
		LogSoapClient log=LogFactory.newLogSoapClient(idsesion);
		if (log==null)
		{
			//No se ha podido recuperar de contexto.
			log=newLogSoapClient("Sin informaci�n de sesi�n.");
		}
		return log;
	}
}
