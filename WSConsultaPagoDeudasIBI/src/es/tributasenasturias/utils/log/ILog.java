package es.tributasenasturias.utils.log;

/**
 * Interfaz que implementarán las clases que actuén como logger
 * @author crubencvs
 *
 */
public interface ILog {

	/**
	 * Genera una entrada en el log, con el mensaje indicado y un nivel de log asociado.
	 * @param msg Mensaje a escribir.
	 * @param level {@link NIVEL_LOG} asociado.
	 */
	public abstract void doLog(String msg, NIVEL_LOG level);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de DEBUG
	 * @param msg Mensaje a escribir.
	 */
	public abstract void debug(String msg);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de ERROR
	 * @param msg Mensaje a escribir.
	 */
	public abstract void error(String msg);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de ERROR.
	 * También recupera información de la excepción y la escribe.
	 * @param msg Mensaje a escribir.
	 * @param excepcion excepción de la que se recuperará información extra para escribir.
	 */
	public abstract void error (String msg, Throwable excepcion);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de INFO
	 * @param msg Mensaje a escribir.
	 */
	public abstract void info(String msg);

	/**
	 * Genera una entrada de log con el volcado de pila que se pasa.
	 * @param stackTraceElements Elementos de la pila
	 */
	public abstract void trace(StackTraceElement[] stackTraceElements);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de TRACE
	 * @param msg Mensaje a escribir.
	 */
	public abstract void trace(String msg);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de WARNING
	 * @param msg Mensaje a escribir.
	 */
	public abstract void warning(String msg);
	

}