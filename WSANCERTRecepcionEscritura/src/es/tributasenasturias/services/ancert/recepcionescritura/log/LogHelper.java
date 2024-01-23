package es.tributasenasturias.services.ancert.recepcionescritura.log;





/**
 * Utilidad para facilitar los logs.
 * @author crubencvs
 *
 */
public class LogHelper{

	private TributasLogger logAplicacion= null;
	private TributasLogger logSoapServer= null;
	private TributasLogger logSoapClient= null;

	private enum LEVELS {NONE,DEBUG, INFO, WARNING, TRACE,ERROR,ALL}
	private String sessionId;
	/**
	 * Tipos de log que se pueden instanciar en el helper.
	 * @author crubencvs
	 *
	 */
	protected enum TIPOLOG {APLICACION,SOAPSERVER,SOAPCLIENTE}
	public LogHelper(TIPOLOG tipo)
	{
		switch (tipo)
		{
		 case APLICACION:
			 logAplicacion=new TributasLogger("Application.log","proyectos/WSANCERTRecepcionEscritura/log","Aplicación");
			 break;
		 case SOAPSERVER:
			 logSoapServer=new TributasLogger("SoapServer.log","proyectos/WSANCERTRecepcionEscritura/log","SOAP SERVER");
			 break;
		 case SOAPCLIENTE:
			 logSoapClient=new TributasLogger("SoapClient.log","proyectos/WSANCERTRecepcionEscritura/log","SOAP CLIENT");
			 break;
		 default:
			 //Por defecto se crea el de aplicación.
			 logAplicacion=new TributasLogger("Application.log","proyectos/WSANCERTRecepcionEscritura/log","Aplicación");
		}
	}
	
	/**
	 * Indica si un nivel de mensajes es imprimible.
	 * @param nivel
	 * @return
	 */
	//Operaciones sincronizadas. Sólo podrá acceder un hilo a ellas.
	//De todas formas, el tiempo de espera debería estar en el orden de milisegundos.
	public void doLog (String msg, LEVELS level)
	{
		if (logAplicacion!=null)
		{
			switch (level)
			{
			case DEBUG:
				logAplicacion.debug(msg);
				break;
			case INFO:
				logAplicacion.info(msg);
				break;
			case WARNING:
				logAplicacion.warning(msg);
				break;
			case TRACE:
				logAplicacion.trace(msg);
				break;
			case ERROR:
				logAplicacion.error (msg);
				break;
			default:
				logAplicacion.info(msg);
			}
		}
	}
	public void debug(String msg) {
		doLog(msg,LEVELS.DEBUG);
	}
	
	public void error(String msg) {
		doLog(msg,LEVELS.ERROR);
	}
	public void error(String msg, Throwable excepcion) {
		String mensaje="";
		if (excepcion!=null)
		{
				StackTraceElement[] stack=excepcion.getStackTrace();
				if (stack.length>0)
				{
				//Datos del punto donde se produjo el error.
				mensaje = mensaje + "["+stack[0].getClassName()+"."+stack[0].getMethodName()+" línea "+stack[0].getLineNumber()+"]::";
				}
		}
		mensaje =mensaje +msg;
		doLog(mensaje,LEVELS.ERROR);
	}
	
	public void info(String msg) {
		doLog(msg,LEVELS.INFO);
	}

	public void trace(StackTraceElement[] stackTraceElements) {
		 if (stackTraceElements == null)
	            return;
		 if (logAplicacion!=null)
		 {
			 logAplicacion.trace(stackTraceElements);
		 }
		 else if (logSoapServer!=null)
		 {
			 logSoapServer.trace(stackTraceElements);
		 }
		 else if (logSoapClient!=null)
		 {
			 logSoapClient.trace(stackTraceElements);
		 }
	}
	
	public void trace(String msg) {
		doLog (msg,LEVELS.TRACE);
	}
	
	public void warning(String msg) {
		doLog(msg,LEVELS.WARNING);
	}
	/*
	public static void initLogger(String idLog)
	{
		MDC.put("IdLog",idLog);
	}
	public static void destroyLogger()
	{
		MDC.remove("IdLog");
	}
	*/
	public void doLogSoapServer (String msg, LEVELS level)
	{
		
		if (logSoapServer!=null)
		{
			switch (level)
			{
			case INFO:
				logSoapServer.info(msg);
				break;
			case ERROR:
				logSoapServer.error (msg);
				break;
			default:
				logSoapServer.info(msg);
			}
		}
		
	}
	public void doLogSoapServer (String msg, String direccion)
	{
		logSoapServer.info(msg);
	}
	public void doLogSoapServer (String msg)
	{
		logSoapServer.info(msg);
	}
	public void doLogSoapClient(String msg)
	{
		if (logSoapClient!=null)
		{
			logSoapClient.info(msg);
		}
	}
	/**
	 * Establece el id de sesión.
	 * @param id
	 */
	public void setSessionId (String id)
	{
		sessionId=id;
		if (logAplicacion!=null)
		{
			logAplicacion.setSesionId(id);
		}
		if (logSoapServer!=null)
		{
			logSoapServer.setSesionId(id);
		}
		if (logSoapClient!=null)
		{
			logSoapClient.setSesionId(id);
		}
	}
	/**
	 * Recupera el id de sesión.
	 * @return
	 */
	public String getSessionId()
	{
		return sessionId;
	}
}
