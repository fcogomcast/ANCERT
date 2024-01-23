/**
 * 
 */
package es.tributasenasturias.utils.log;




/** Escritor de log de pega, para que siempre exista uno y no fallen las llamadas, aunque luego no impriman nada.
 * @author crubencvs
 *
 */
public class DummyLogWriter implements ILogWriter{

		
		@Override
		public void setNivelLog(NIVEL_LOG nivel) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public IFormateadorMensaje getFormateador()
		{
			return new FormateadorTributas();
		}
		@Override
		public void setFormateador(IFormateadorMensaje formateador) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Método de log de pega.
		 * @param message Mensaje a logear
		 * @param level Nivel de mensajes.
		 */
		public void doLog(String message, NIVEL_LOG level)
		{
			System.out.println ("Log por defecto. Debería configurar uno:" + message);
		}
		
		/**
		 * Utilidad para generar una entrada de error si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public void error(String msg) {
			doLog(msg,NIVEL_LOG.ERROR);
		}
		/** 
		 * Utilidad para generar una entrada de depuración si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public void debug(String msg) {
			doLog(msg,NIVEL_LOG.DEBUG);
		}
		/**
		 * Utilidad para generar una entrada de información si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public void info(String msg) {
			doLog(msg,NIVEL_LOG.INFO);

		}
		/**
		 * Utilidad para generar una entrada de alerta si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public void warning(String msg) {
			doLog(msg,NIVEL_LOG.WARNING);
		}
		/**
		 * Utilidad para generar una entrada de traza si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param message Mensaje a mostrar.
		 */
		public final void trace(String message)
		{	
			doLog(message,NIVEL_LOG.INFO);
		}
		/**
		 * Utilidad para generar una entrada de traza con la pila de errores de excepción si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param stackTraceElements Pila de excepción.
		 */
		public final void trace(StackTraceElement[] stackTraceElements)
		{
		    doLog("StackTrace -> ",NIVEL_LOG.INFO);
		}
		/**
		 * Recupera el nombre de fichero de log
		 * @return {@link String} con el nombre de fichero de log.
		 */
		public String getLogFile() {
			return "";
		}
		/**
		 * Modifica el nombre de fichero de log
		 * @param logFile {@link String} nombre de fichero de log.
		 */
		public void setLogFile(String logFile) {
		}
		/**
		 * Recupera el nombre de directorio de log.
		 * @return {@link String} con el nombre del directorio.
		 */
		public String getLogDir() {
			return "";
		}
		/**
		 * Modifica el nombre de fichero de log
		 * @param logDir {@link String} con el nuevo nombre de directorio
		 */
		public void setLogDir(String logDir) {
		}
		/**
		 * Recupera el nombre de log (identificador interno de ese log, aparece en las líneas de log)
		 * @return {@link String} que indicará el nombre de log
		 */
		public String getNombre() {
			return "";
		}
		/**
		 * Modifica el nombre de log (identificador interno, aparece en las líneas de log).
		 * @param nombre {@link String} con el nuevo nombre de log
		 */
		public void setNombre(String nombre) {
			
		}
}
