/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;


/** Implementa el escritor de log de tributas. A pesar de indicar que los métodos son "synchronized" no debería
 *  ser necesario porque cada hilo debe tener su propia instancia. Sin embargo, por si el servidor hace 
 *  algo que no se espera, lo ponemos. Si no se crea más de una instancia por hilo, no generará sobrecarga.
 * @author crubencvs
 *
 */
public class TributasLogWriter implements ILogWriter {

		
		private String _LOG_FILE = "";
		private String _LOG_DIR = "";
		private Preferencias pr=null;	
		private IFormateadorMensaje formateador;
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#setFormateador(es.tributasenasturias.services.ancert.enviodiligencias.log.IFormateadorMensaje)
		 */
		public void setFormateador(IFormateadorMensaje formateador) {
			this.formateador = formateador;
		}
		/**
		 * Constructor por defecto.
		 */
		public TributasLogWriter() throws LogException
		{
			try {
				pr= PreferenciasFactory.newInstance();
			} catch (PreferenciasException e) {
				throw new LogException ("Excepción al construir el proceso que escribe en log:" + e.getMessage(),e);
			}
		}
		/**
		 * Constructor al que se le pasa el nombre de fichero de log y el directorio.
		 * @param file Nombre de fichero de log
		 * @param dir Ruta del directorio donde se dejará el fichero. Sin "/" final.
		 */
		public TributasLogWriter( String file, String dir) throws LogException
		{
			this();
			this._LOG_FILE=file;
			this._LOG_DIR=dir;
		}
		//Método privado que comprueba el nivel de detalle de log definido en el fichero de preferencias
		// y devuelve si en base a un nivel de detalle de log que se indica por parámetros se han de 
		// imprimir los mensajes o no.
		private boolean esImprimible(NIVEL_LOG nivel)
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
					modo=NIVEL_LOG.INFO.toString();//Si no se puede leer el fichero de preferencias, se logea de info hacia abajo.
				}
				if (NIVEL_LOG.ALL.toString().equalsIgnoreCase(modo)) //Imprimir todo. Siempre verdadero.
				{res=true;}
				else if (NIVEL_LOG.DEBUG.toString().equalsIgnoreCase(modo))
				{
					if (NIVEL_LOG.DEBUG.equals(nivel)||NIVEL_LOG.INFO.equals(nivel)|| NIVEL_LOG.WARNING.equals(nivel)|| NIVEL_LOG.ERROR.equals(nivel))
					{res=true;}
					else {res=false;}
				}
				else if (NIVEL_LOG.INFO.toString().equalsIgnoreCase(modo))
				{
					if (NIVEL_LOG.INFO.equals(nivel)|| NIVEL_LOG.WARNING.equals(nivel)|| NIVEL_LOG.ERROR.equals(nivel))
					{res=true;}
					else {res=false;}
				}
				else if (NIVEL_LOG.WARNING.toString().equalsIgnoreCase(modo))
				{
					if (NIVEL_LOG.WARNING.equals(nivel)|| NIVEL_LOG.ERROR.equals(nivel))
					{res=true;}
					else {res=false;}
				}
				else if (NIVEL_LOG.TRACE.toString().equals(modo))
				{
					if (NIVEL_LOG.TRACE.equals(nivel)|| NIVEL_LOG.ERROR.equals(nivel))
					{res=true; } 
					else { res=false;}
				}
				else if (NIVEL_LOG.ERROR.toString().equalsIgnoreCase(modo))
				{
					if (NIVEL_LOG.ERROR.equals(nivel))
					{res=true;}
					else {res=false;}
				}
				else if (NIVEL_LOG.NONE.toString().equalsIgnoreCase(modo))
				{
					res=false;
				}
			}
			catch (Exception ex) // En principio sólo debería ocurrir porque hay un error al abrir el fichero
								// de preferencias. En ese caso, devolvemos true para que imprima todo se pase el 
								// parámetro que se pase, incluído error.
			{
				res=true;
			}
			return res;
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#doLog(java.lang.String, es.tributasenasturias.services.ancert.enviodiligencias.log.NIVEL_LOG)
		 */
		public void doLog(String message, NIVEL_LOG level)
		{
			File file;
	        FileWriter fichero = null;
	        PrintWriter pw=null;

	        try
	        {
	        	String completeMsg="";
	        	if (formateador!=null)
	        	{
	        		completeMsg = formateador.formatea(message, level);
	        	}
	            file = new File(_LOG_DIR);
	            if(file.exists() == false)
	                if (file.mkdirs()==false)
	                	{
	                		throw new IOException("Envío de diligencias:No se puede crear el directorio de log."); 
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
	            System.out.println("Envío de diligencias:Error escribiendo log :'"+message+"' -> "+e.getMessage());
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
	            	catch (Exception e) // En principio no debería devolver, nunca, una excepción. Se controla 
	            						// por si hay cambios en la implementación.
	            	{
	            		System.out.println ("Envío de diligencias:Error cerrando flujo de impresión para el log : " + e.getMessage());
	            		e.printStackTrace();
	            	}
	                try
	                {
	                    fichero.close();
	                }
	                catch(Exception e)
	                {
	                    System.out.println("Envío de diligencias:Error cerrando fichero de log-> "+e.getMessage());
	                    e.printStackTrace();
	                }
	            }
	        }
		}
		
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#error(java.lang.String)
		 */
		public void error(String msg) {
			if (esImprimible(NIVEL_LOG.ERROR))
			{doLog(msg,NIVEL_LOG.ERROR);}
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#debug(java.lang.String)
		 */
		public void debug(String msg) {
			if (esImprimible(NIVEL_LOG.DEBUG))
			{doLog(msg,NIVEL_LOG.DEBUG);}
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#info(java.lang.String)
		 */
		public void info(String msg) {
			if (esImprimible(NIVEL_LOG.INFO))
			{doLog(msg,NIVEL_LOG.INFO);}

		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#warning(java.lang.String)
		 */
		public void warning(String msg) {
			if (esImprimible(NIVEL_LOG.WARNING))
			{doLog(msg,NIVEL_LOG.WARNING);}
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#trace(java.lang.String)
		 */
		public final void trace(String message)
		{	
			if (esImprimible(NIVEL_LOG.INFO))
			{
				doLog(message,NIVEL_LOG.TRACE);
			
			}
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#trace(java.lang.StackTraceElement[])
		 */
		public final void trace(StackTraceElement[] stackTraceElements)
		{
			 if (stackTraceElements == null)
		            return;
			 if (esImprimible(NIVEL_LOG.INFO))
			 {
		        for (int i = 0; i < stackTraceElements.length; i++)
		        {
		            doLog("StackTrace -> " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getClassName() + " :: " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getMethodName() + " :: " + stackTraceElements[i].getLineNumber(),NIVEL_LOG.TRACE);
		        }
			 }
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#getLogFile()
		 */
		public String getLogFile() {
			return _LOG_FILE;
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#setLogFile(java.lang.String)
		 */
		public void setLogFile(String logFile) {
			_LOG_FILE = logFile;
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#getLogDir()
		 */
		public String getLogDir() {
			return _LOG_DIR;
		}
		/* (non-Javadoc)
		 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILogWriter#setLogDir(java.lang.String)
		 */
		public void setLogDir(String logDir) {
			_LOG_DIR = logDir;
		}
}
