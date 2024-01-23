/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;


/** Implementa el logger de tributas. A pesar de indicar que los m�todos son "synchronized" no deber�a
 *  ser necesario porque cada hilo debe tener su propia instancia. Sin embargo, por si el servidor hace 
 *  algo que no se espera, lo ponemos. Si no se crea m�s de una instancia por hilo, no generar� sobrecarga.
 * @author crubencvs
 *
 */
public class DailyLogger implements ILog{

		private String _LOG_FILE = "";
		private String _LOG_DIR = "";
		private Preferencias pr=null;	
		private String _nombre="";//Nombre descriptivo del log
		public enum LEVEL {NONE,DEBUG, INFO, WARNING, TRACE,ERROR,ALL}
		private String sesionId;//Id de la sesi�n que imprime, si hubiera.
		
		/**
		 * Recupera el nombre del fichero de log del d�a en que se realiza la escritura.
		 * Corresponde al nombre de fichero original (Por ejemplo, SOAP_SERVER.log) pero con un 
		 * sufijo AAAA_MM_DD (a�o-mes-d�a) justo antes de la extensi�n
		 * 
		 */
		private String getDailyLog(String original){
			String nombreLog;
			Date now= new Date();
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy_MM_dd"); 
			//En teor�a puede lanzar excepci�n, pero no lo considero.
			//En circunstancias t�cnicas normales (no fallo de servidor), no va a ocurrir formateando la fecha actual.
			String sufijo= sdf.format(now);
			//En el nombre original puede venir extensi�n, o no.
			//Lo normal es que s�, pero no lo doy por seguro.
			if (original.indexOf('.')<0){
				nombreLog=original+"_"+sufijo;
			} else {
				//Sustituyo lo que hay antes del punto= nombre de fichero (grupo $1) y la extensi�n (grupo $2)
				// por el nombre concatenado con el sufijo, y luego con la extensi�n
				nombreLog= original.replaceAll("(^.*)(\\..*)", "$1_"+sufijo+"$2");			
			}
			return nombreLog;
		}
		
		/**
		 * Se indica el identificador de log, que se mostrar� en cada entrada.
		 * @param id Identificador del log.
		 */
		public  void setSesionId(String id)
		{
			sesionId=id;
		}
		/**
		 * Constructor por defecto.
		 */
		public DailyLogger()
		{
			try
			{
				pr= PreferenciasFactory.newInstance();
			}
			catch (PreferenciasException ex)
			{
				pr=null;
			}
		}
		public DailyLogger (Preferencias pref)
		{
			pr=pref;
		}
		/**
		 * Constructor al que se le pasa el nombre de fichero de log y el directorio.
		 * @param file Nombre de fichero de log
		 * @param dir Ruta del directorio donde se dejar� el fichero. Sin "/" final.
		 */
		public DailyLogger( String file, String dir)
		{
			this();
			this._LOG_FILE=getDailyLog(file);
			this._LOG_DIR=dir;
		}
		/**
		 * Constructor al que se le pasa el nombre de fichero, el directorio y el nombre de log.
		 * @param file Nombre de fichero de log
		 * @param dir Ruta del directorio donde se dejar� el fichero. Sin "/" final.
		 * @param nombre Nombre de log. Ser� el primer elemento de todas las l�neas de log. Cualquier texto descriptivo.
		 */
		public DailyLogger(String file, String dir, String nombre)
		{
			this(file,dir);
			this._nombre=nombre;
		}
		//M�todo privado que comprueba el nivel de detalle de log definido en el fichero de preferencias
		// y devuelve si en base a un nivel de detalle de log que se indica por par�metros se han de 
		// imprimir los mensajes o no.
		private  boolean esImprimible(LEVEL nivel)
		{
			boolean res=false;
			try
			{	
				String modo;
				if (pr!=null)
				{
					modo = pr.getModoLog();
				}
				else
				{
					modo=LEVEL.INFO.toString();//Si no se puede leer el fichero de preferencias, se logea de info hacia abajo.
				}
				if (LEVEL.ALL.toString().equalsIgnoreCase(modo)) //Imprimir todo. Siempre verdadero.
				{res=true;}
				else if (LEVEL.DEBUG.toString().equalsIgnoreCase(modo))
				{
					if (LEVEL.DEBUG.equals(nivel)||LEVEL.INFO.equals(nivel)|| LEVEL.WARNING.equals(nivel)|| LEVEL.ERROR.equals(nivel))
					{res=true;}
					else {res=false;}
				}
				else if (LEVEL.INFO.toString().equalsIgnoreCase(modo))
				{
					if (LEVEL.INFO.equals(nivel)|| LEVEL.WARNING.equals(nivel)|| LEVEL.ERROR.equals(nivel))
					{res=true;}
					else {res=false;}
				}
				else if (LEVEL.WARNING.toString().equalsIgnoreCase(modo))
				{
					if (LEVEL.WARNING.equals(nivel)|| LEVEL.ERROR.equals(nivel))
					{res=true;}
					else {res=false;}
				}
				else if (LEVEL.TRACE.toString().equals(modo))
				{
					if (LEVEL.TRACE.equals(nivel)|| LEVEL.ERROR.equals(nivel))
					{res=true; } 
					else { res=false;}
				}
				else if (LEVEL.ERROR.toString().equalsIgnoreCase(modo))
				{
					if (LEVEL.ERROR.equals(nivel))
					{res=true;}
					else {res=false;}
				}
				else if (LEVEL.NONE.toString().equalsIgnoreCase(modo))
				{
					res=false;
				}
			}
			catch (Exception ex) // En principio s�lo deber�a ocurrir porque hay un error al abrir el fichero
								// de preferencias. En ese caso, devolvemos true para que imprima todo se pase el 
								// par�metro que se pase, inclu�do error.
			{
				res=true;
			}
			return res;
		}
		/**
		 * M�todo que realiza log. Se especifica que sea sincronizado, aunque en realidad no se 
		 * espera que una instancia se ejecute en varios hilos, pero como no se sabe qu� hace
		 * el servidor de aplicaciones, se deja as� por si acaso.
		 * @param message Mensaje a logear
		 * @param level Nivel de mensajes.
		 */
		public   void doLog(String message, LEVEL level)
		{
			File file;
	        FileWriter fichero = null;
	        PrintWriter pw=null;

	        try
	        {
	            Date today = new Date();
	            String completeMsg = _nombre+" :: " + today + " :: " + level + " :: " + message;
	            if (sesionId!=null)
	            {
	            	completeMsg+="::Sesi�n:"+sesionId;
	            }
	            file = new File(_LOG_DIR);
	            if(file.exists() == false)
	                if (file.mkdirs()==false)
	                	{
	                		throw new IOException("No se puede crear el directorio de log para el log " + _nombre +"."); 
	                	}
	            
	            fichero = new FileWriter(_LOG_DIR + "/" + _LOG_FILE, true);//true para que agregemos al final del fichero
	            if (fichero!=null)
	            {
	            	pw = new PrintWriter(fichero);
	            
	            	pw.println(completeMsg);
	            }
	            
	        }
	        catch (IOException e)
	        {
	            System.out.println("Error escribiendo log " + _nombre + " :'"+message+"' -> "+e.getMessage());
	            e.printStackTrace();
	        }
	        finally
	        {
	            if(fichero != null)
	            {
	            	try
	            	{
	            		pw.close();
	            	}
	            	catch (Exception e) // En principio no deber�a devolver, nunca, una excepci�n. Se controla 
	            						// por si hay cambios en la implementaci�n.
	            	{
	            		System.out.println ("Error cerrando flujo de impresi�n para el log "+ _nombre + ": " + e.getMessage());
	            		e.printStackTrace();
	            	}
	                try
	                {
	                    fichero.close();
	                }
	                catch(Exception e)
	                {
	                    System.out.println("Error cerrando fichero de log " + _nombre + "-> "+e.getMessage());
	                    e.printStackTrace();
	                }
	            }
	        }
		}
		
		/**
		 * Utilidad para generar una entrada de error si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public  void error(String msg) {
			if (esImprimible(LEVEL.ERROR))
			{doLog(msg,LEVEL.ERROR);}
		}
		/** 
		 * Utilidad para generar una entrada de depuraci�n si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public  void debug(String msg) {
			if (esImprimible(LEVEL.DEBUG))
			{doLog(msg,LEVEL.DEBUG);}
		}
		/**
		 * Utilidad para generar una entrada de informaci�n si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public  void info(String msg) {
			if (esImprimible(LEVEL.INFO))
			{doLog(msg,LEVEL.INFO);}

		}
		/**
		 * Utilidad para generar una entrada de alerta si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param msg Mensaje a mostrar.
		 */
		public  void warning(String msg) {
			if (esImprimible(LEVEL.WARNING))
			{doLog(msg,LEVEL.WARNING);}
		}
		/**
		 * Utilidad para generar una entrada de traza si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param message Mensaje a mostrar.
		 */
		public  final void trace(String message)
		{	
			if (esImprimible(LEVEL.INFO))
			{doLog(message,LEVEL.INFO);}
		}
		/**
		 * Utilidad para generar una entrada de traza con la pila de errores de excepci�n si el nivel de mensaje que se indica en preferencias lo permite.
		 * @param stackTraceElements Pila de excepci�n.
		 */
		public  final void trace(StackTraceElement[] stackTraceElements)
		{
			 if (stackTraceElements == null)
		            return;
			 if (esImprimible(LEVEL.INFO))
			 {
		        for (int i = 0; i < stackTraceElements.length; i++)
		        {
		            doLog("StackTrace -> " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getClassName() + " :: " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getMethodName() + " :: " + stackTraceElements[i].getLineNumber(),LEVEL.INFO);
		        }
			 }
		}
		/**
		 * Recupera el nombre de fichero de log
		 * @return {@link String} con el nombre de fichero de log.
		 */
		public   String getLogFile() {
			return _LOG_FILE;
		}
		/**
		 * Modifica el nombre de fichero de log
		 * @param logFile {@link String} nombre de fichero de log.
		 */
		public   void setLogFile(String logFile) {
			_LOG_FILE = getDailyLog(logFile);
		}
		/**
		 * Recupera el nombre de directorio de log.
		 * @return {@link String} con el nombre del directorio.
		 */
		public   String getLogDir() {
			return _LOG_DIR;
		}
		/**
		 * Modifica el nombre de fichero de log
		 * @param logDir {@link String} con el nuevo nombre de directorio
		 */
		public  void setLogDir(String logDir) {
			_LOG_DIR = logDir;
		}
		/**
		 * Recupera el nombre de log (identificador interno de ese log, aparece en las l�neas de log)
		 * @return {@link String} que indicar� el nombre de log
		 */
		public   String getNombre() {
			return _nombre;
		}
		/**
		 * Modifica el nombre de log (identificador interno, aparece en las l�neas de log).
		 * @param nombre {@link String} con el nuevo nombre de log
		 */
		public   void setNombre(String nombre) {
			_nombre = nombre;
		}
}
