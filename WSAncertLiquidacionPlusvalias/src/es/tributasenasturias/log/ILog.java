package es.tributasenasturias.log;

/**
 * Interfaz que implementarán las clases que actuén como logger. Esto se usa para tener una jerarquía sencilla
 * que permita realizar log, sin tener que usar otras librerías. Podría ser más sencillo si tuviésemos una
 * sola clase que simplemente escribiese a fichero.
 * @author crubencvs
 *
 */
public interface ILog {

	/**
	 * Los diferentes nivels de log que existen.
	 * Cuando se utiliza un nivel para filtrar mensajes enviados al log, un determinado nivel incluye a los siguientes en esta lista:
	 * <ul>
	 * <li>DEBUG</li>
	 * <li>INFO</li>
	 * <li>WARNING</li>
	 * <li>TRACE</li>
	 * <li>ERROR</li>
	 * </ul>
	 * Es decir, si el nivel objetivo para los mensajes es "INFO", se mostrarán los mensajes que se identifiquen
	 * con los niveles "INFO", "WARNING", "TRACE" y "ERROR" pero no los identificados con el nivel "DEBUG".
	 * @author crubencvs
	 *
	 */
	static enum NIVEL_LOG {
		/**
		 * Indica que no se pasará al log ninguno de los mensajes posibles.
		 */
		NONE,
		/**
		 * Utilizado para mensajes de depuración.
		 */
		DEBUG,
		/**
		 * Utilizado para los mensajes de proceso normales, es en este nivel donde se mostrarán 
		 * los mensajes de proceso correcto y esperado.
		 */
		INFO,
		/**
		 * Utilizado como nivel de aviso, no se ha producido error pero sí una circunstancia 
		 * que se considera que se sale del proceso normal.
		 */
		WARNING,
		/**
		 * Utilizado sólo para trazas relacionadas con eventos dirigidos al 
		 * mantenimiento técnico, como volcados de pila. 
		 */
		TRACE,
		/**
		 * Utilizado para indicar que se han producido errores en el proceso.
		 */
		ERROR,
		/**
		 * Indica todos los niveles de log, agrupa a todos los anteriores excepto "NONE".
		 */
		ALL
	}
	/**
	 * Genera una entrada en el log, con el mensaje indicado y un nivel de log asociado.
	 * @param msg Mensaje a escribir.
	 * @param level {@link NIVEL_LOG} asociado.
	 */
	void doLog(String msg, NIVEL_LOG level);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de DEBUG
	 * @param msg Mensaje a escribir.
	 */
	void debug(String msg);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de ERROR
	 * @param msg Mensaje a escribir.
	 */
	void error(String msg);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de ERROR.
	 * También recupera información de la excepción y la escribe.
	 * @param msg Mensaje a escribir.
	 * @param excepcion excepción de la que se recuperará información extra para escribir.
	 */
	void error (String msg, Throwable excepcion);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de INFO
	 * @param msg Mensaje a escribir.
	 */
	void info(String msg);

	/**
	 * Genera una entrada de log con el volcado de pila que se pasa.
	 * @param stackTraceElements Elementos de la pila
	 */
	void trace(StackTraceElement[] stackTraceElements);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de TRACE
	 * @param msg Mensaje a escribir.
	 */
	void trace(String msg);
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de WARNING
	 * @param msg Mensaje a escribir.
	 */
	void warning(String msg);
	

}
