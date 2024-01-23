/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.log;



/** Implementa el logger de tributas. A pesar de indicar que los métodos son "synchronized" no debería
 *  ser necesario porque cada hilo debe tener su propia instancia. Sin embargo, por si el servidor hace 
 *  algo que no se espera, lo ponemos. Si no se crea más de una instancia por hilo, no generará sobrecarga.
 * @author crubencvs
 *
 */
public class LoggerNulo {

		private String _LOG_FILE = "";
		private String _LOG_DIR = "";
		private String _nombre="";//Nombre descriptivo del log
		public enum LEVEL {NONE,DEBUG, INFO, WARNING, TRACE,ERROR,ALL}
		/**
		 * Se indica el identificador de log, que se mostrará en cada entrada.
		 * @param id Identificador del log.
		 */
		public  void setSesionId(String id)
		{
			
		}
		
		/**
		 * Constructor al que se le pasa el nombre de fichero de log y el directorio.
		 * @param file Nombre de fichero de log
		 * @param dir Ruta del directorio donde se dejará el fichero. Sin "/" final.
		 */
		public LoggerNulo( String file, String dir)
		{
			
		}
		/**
		 * Constructor al que se le pasa el nombre de fichero, el directorio y el nombre de log.
		 * @param file Nombre de fichero de log
		 * @param dir Ruta del directorio donde se dejará el fichero. Sin "/" final.
		 * @param nombre Nombre de log. Será el primer elemento de todas las líneas de log. Cualquier texto descriptivo.
		 */
		public LoggerNulo(String file, String dir, String nombre)
		{
			
		}
		/**
		 * Método que realiza log. Se especifica que sea sincronizado, aunque en realidad no se 
		 * espera que una instancia se ejecute en varios hilos, pero como no se sabe qué hace
		 * el servidor de aplicaciones, se deja así por si acaso.
		 * @param message Mensaje a logear
		 * @param level Nivel de mensajes.
		 */
		public  void doLog(String message, LEVEL level)
		{
			
		}
		
		/**
		 * Utilidad para generar una entrada de error si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public void error(String msg) {
			
		}
		/** 
		 * Utilidad para generar una entrada de depuración si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public void debug(String msg) {
			
		}
		/**
		 * Utilidad para generar una entrada de información si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public void info(String msg) {
			

		}
		/**
		 * Utilidad para generar una entrada de alerta si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public void warning(String msg) {
			
		}
		/**
		 * Utilidad para generar una entrada de traza si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param message Mensaje a mostrar.
		 */
		public final void trace(String message)
		{	
			
		}
		/**
		 * Utilidad para generar una entrada de traza con la pila de errores de excepción si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param stackTraceElements Pila de excepción.
		 */
		public final void trace(StackTraceElement[] stackTraceElements)
		{
			 
		}
		/**
		 * Recupera el nombre de fichero de log
		 * @return {@link String} con el nombre de fichero de log.
		 */
		public  String getLogFile() {
			return _LOG_FILE;
		}
		/**
		 * Modifica el nombre de fichero de log
		 * @param logFile {@link String} nombre de fichero de log.
		 */
		public  void setLogFile(String logFile) {
			
		}
		/**
		 * Recupera el nombre de directorio de log.
		 * @return {@link String} con el nombre del directorio.
		 */
		public  String getLogDir() {
			return _LOG_DIR;
		}
		/**
		 * Modifica el nombre de fichero de log
		 * @param logDir {@link String} con el nuevo nombre de directorio
		 */
		public  void setLogDir(String logDir) {
			
		}
		/**
		 * Recupera el nombre de log (identificador interno de ese log, aparece en las líneas de log)
		 * @return {@link String} que indicará el nombre de log
		 */
		public  String getNombre() {
			return _nombre;
		}
		/**
		 * Modifica el nombre de log (identificador interno, aparece en las líneas de log).
		 * @param nombre {@link String} con el nuevo nombre de log
		 */
		public  void setNombre(String nombre) {
			
		}
}
