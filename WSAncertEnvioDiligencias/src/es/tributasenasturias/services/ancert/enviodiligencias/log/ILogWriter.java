package es.tributasenasturias.services.ancert.enviodiligencias.log;

public interface ILogWriter {

	/**
	 * Indica el formateador que se utilizará para construir los mensajes a escribir.
	 * @param formateador
	 */
	public abstract void setFormateador(IFormateadorMensaje formateador);

	/**
	 * Método que realiza log. Se especifica que sea sincronizado, aunque en realidad no se 
	 * espera que una instancia se ejecute en varios hilos, pero como no se sabe qué hace
	 * el servidor de aplicaciones, se deja así por si acaso.
	 * @param message Mensaje a logear
	 * @param level Nivel de mensajes.
	 */
	public abstract void doLog(String message, NIVEL_LOG level);

	/**
	 * Utilidad para generar una entrada de error si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param msg Mensaje a mostrar.
	 */
	public abstract void error(String msg);

	/** 
	 * Utilidad para generar una entrada de depuración si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param msg Mensaje a mostrar.
	 */
	public abstract void debug(String msg);

	/**
	 * Utilidad para generar una entrada de información si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param msg Mensaje a mostrar.
	 */
	public abstract void info(String msg);

	/**
	 * Utilidad para generar una entrada de alerta si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param msg Mensaje a mostrar.
	 */
	public abstract void warning(String msg);

	/**
	 * Utilidad para generar una entrada de traza si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param message Mensaje a mostrar.
	 */
	public abstract void trace(String message);

	/**
	 * Utilidad para generar una entrada de traza con la pila de errores de excepción si el nivel de mensaje que se indica en preferencias lo permite.
	 * @param stackTraceElements Pila de excepción.
	 */
	public abstract void trace(StackTraceElement[] stackTraceElements);

	/**
	 * Recupera el nombre de fichero de log
	 * @return {@link String} con el nombre de fichero de log.
	 */
	public abstract String getLogFile();

	/**
	 * Modifica el nombre de fichero de log
	 * @param logFile {@link String} nombre de fichero de log.
	 */
	public abstract void setLogFile(String logFile);

	/**
	 * Recupera el nombre de directorio de log.
	 * @return {@link String} con el nombre del directorio.
	 */
	public abstract String getLogDir();

	/**
	 * Modifica el nombre de fichero de log
	 * @param logDir {@link String} con el nuevo nombre de directorio
	 */
	public abstract void setLogDir(String logDir);

}