package es.tributasenasturias.services.ancert.enviodiligencias.log;

public interface ILog {

	/**
	 * Indica si un nivel de mensajes es imprimible.
	 * @param nivel
	 * @return
	 */
	public abstract void doLog(String msg, NIVEL_LOG level);

	public abstract void debug(String msg);

	public abstract void error(String msg);
	
	public abstract void error (String msg, Throwable excepcion);

	public abstract void info(String msg);

	public abstract void trace(StackTraceElement[] stackTraceElements);

	public abstract void trace(String msg);

	public abstract void warning(String msg);

	/**
	 * Recupera el id de sesión.
	 * @return
	 */
	public abstract String getSessionId();

}