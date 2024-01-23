package es.tributasenasturias.services.ancert.recepcionescritura.log;

public interface ILog {

	/**
	 * Utilidad para generar una entrada de error si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param msg Mensaje a mostrar.
	 */
	void error(String msg);

	/** 
	 * Utilidad para generar una entrada de depuración si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param msg Mensaje a mostrar.
	 */
	void debug(String msg);

	/**
	 * Utilidad para generar una entrada de información si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param msg Mensaje a mostrar.
	 */
	void info(String msg);

	/**
	 * Utilidad para generar una entrada de alerta si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param msg Mensaje a mostrar.
	 */
	void warning(String msg);

	/**
	 * Utilidad para generar una entrada de traza si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param message Mensaje a mostrar.
	 */
	void trace(String message);

	/**
	 * Utilidad para generar una entrada de traza con la pila de errores de excepción si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param stackTraceElements Pila de excepción.
	 */
	void trace(StackTraceElement[] stackTraceElements);
	
	/**
	 * Se indica el identificador de log, que se mostrará en cada entrada.
	 * @param id Identificador del log.
	 */
	public  void setSesionId(String id);

}