package es.tributasenasturias.utils.log;

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
public enum NIVEL_LOG {
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
