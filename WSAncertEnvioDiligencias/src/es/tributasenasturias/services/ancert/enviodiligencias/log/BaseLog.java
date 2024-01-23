package es.tributasenasturias.services.ancert.enviodiligencias.log;

public class BaseLog implements ILog {

	protected ILogWriter log;
	protected String sessionId;

	public BaseLog() {
		super();
		log=new DummyLogWriter();
	}
	public BaseLog (String sessionid)
	{
		this();
		sessionId=sessionid;
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#doLog(java.lang.String, es.tributasenasturias.services.ancert.enviodiligencias.log.NIVEL_LOG)
	 */
	public void doLog(String msg, NIVEL_LOG level) {
		if (log!=null)
		{
			switch (level)
			{
			case DEBUG:
				log.debug(msg);
				break;
			case INFO:
				log.info(msg);
				break;
			case WARNING:
				log.warning(msg);
				break;
			case TRACE:
				log.trace(msg);
				break;
			case ERROR:
				log.error (msg);
				break;
			default:
				log.info(msg);
			}
		}
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#debug(java.lang.String)
	 */
	public void debug(String msg) {
		doLog(msg,NIVEL_LOG.DEBUG);
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#error(java.lang.String)
	 */
	public void error(String msg) {
		doLog(msg,NIVEL_LOG.ERROR);
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#info(java.lang.String)
	 */
	public void info(String msg) {
		doLog(msg,NIVEL_LOG.INFO);
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#trace(java.lang.StackTraceElement[])
	 */
	public void trace(StackTraceElement[] stackTraceElements) {
		 if (stackTraceElements == null)
	            return;
		 if (log!=null)
		 {
			 log.trace(stackTraceElements);
		 }
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#trace(java.lang.String)
	 */
	public void trace(String msg) {
		doLog (msg,NIVEL_LOG.TRACE);
	}

	public void trace(String msg, Throwable excepcion) {
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
		doLog (mensaje,NIVEL_LOG.TRACE);
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#warning(java.lang.String)
	 */
	public void warning(String msg) {
		doLog(msg,NIVEL_LOG.WARNING);
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#getSessionId()
	 */
	public String getSessionId() {
		return sessionId;
	}
	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#error(java.lang.String)
	 */
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
		doLog(mensaje,NIVEL_LOG.ERROR);
	}

}