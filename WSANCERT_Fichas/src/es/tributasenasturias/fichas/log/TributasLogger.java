package es.tributasenasturias.fichas.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Clase para poder realizar log
 * @author crubencvs
 *
 */
public class TributasLogger implements ILog{


	private NIVEL_LOG nivel;
	private String fichero;
	private String idSesion;
	/**
	 * Constructor, permite indicar el nivel deseado de log, el fichero y el identificador de sesión
	 * que se incluirá en cada línea
	 * @param nivel
	 * @param ficheroLog
	 * @param idSesion
	 */
	public TributasLogger(NIVEL_LOG nivel,String ficheroLog, String idSesion)
	{
		this.nivel= nivel;
		this.idSesion= idSesion;
		this.fichero= ficheroLog;
	}
	/**
	 * Permite indicar el nivel deseado de log y el fichero. No incluye identificador de sesión
	 * @param nivel
	 * @param ficheroLog
	 */
	public TributasLogger(NIVEL_LOG nivel, String ficheroLog)
	{
		this(nivel, ficheroLog, null);
	}
	/**
	 * Constructor, permite indicar el nivel deseado de log, el fichero y el identificador de sesión
	 * que se incluirá en cada línea
	 * @param nivel
	 * @param ficheroLog
	 * @param idSesion
	 */
	public TributasLogger(String nivel,String ficheroLog, String idSesion)
	{
		this.nivel= string2Nivel(nivel);
		this.idSesion= idSesion;
		this.fichero= ficheroLog;
	}
	/**
	 * Permite indicar el nivel deseado de log y el fichero. No incluye identificador de sesión
	 * @param nivel
	 * @param ficheroLog
	 */
	public TributasLogger(String nivel, String ficheroLog)
	{
		this(nivel, ficheroLog, null);
	}
	/**
	 * Construye un {@link NIVEL_LOG} en base a una cadena que corresponda con el identificador de ese valor.
	 * @param snivel Texto que representra un identificador de un enum
	 * @return Objeto {@link NIVEL_LOG}
	 */
	private static NIVEL_LOG string2Nivel(String snivel)
	{
		NIVEL_LOG nivel;
		try
		{
			nivel = NIVEL_LOG.valueOf(snivel);
		}
		catch (Exception ex) //Probablemente IllegalArgumentException
		{
			//Por defecto, INFO si no es un nivel válido.
			nivel = NIVEL_LOG.INFO;
		}
		return nivel;
	}
    /**
     * Método privado que comprueba el nivel de detalle de log y lo compara con un nivel objetivo para saber si se han de imprimir los mensajes
     * nivel de objeto TributasLogger=ALL ==> se imprime siempre
     *       DEBUG==> se imprimen mensajes de DEBUG, INFO, WARNING, TRACE o ERROR
     *       INFO==>  se imprimen mensajes de INFO, WARNING, TRACE o ERROR
     *       WARNING==> se imprimen mensajes de WARNING, TRACE o ERROR
     *       TRACE==> se imprimen mensajes de TRACE o ERROR
     *       ERROR==> se imprimen mensajes de ERROR
     *       NONE==> no se imprime nada  
     * @param nivel Nivel del mensaje que se quiere imprimir
     * @return true si el mensaje se puede imprimir según el nivel objetivo y el nivel de mensaje
     */
	private boolean esImprimible(NIVEL_LOG nivel)
	{
		boolean res=false;
		try
		{	
			if (nivel==null)
			{
				nivel=NIVEL_LOG.INFO;//Si no se especifica nivel, se usa INFO.
			}
			if (NIVEL_LOG.ALL.equals(this.nivel)) //Imprimir todo. Siempre verdadero.
			{res=true;}
			else if (NIVEL_LOG.DEBUG.equals(this.nivel))
			{
				if (NIVEL_LOG.DEBUG.equals(nivel)||NIVEL_LOG.INFO.equals(nivel)|| NIVEL_LOG.WARNING.equals(nivel)||NIVEL_LOG.TRACE.equals(nivel)|| NIVEL_LOG.ERROR.equals(nivel))
				{res=true;}
				else {res=false;}
			}
			else if (NIVEL_LOG.INFO.equals(this.nivel))
			{
				if (NIVEL_LOG.INFO.equals(nivel)|| NIVEL_LOG.WARNING.equals(nivel)|| NIVEL_LOG.TRACE.equals(nivel)|| NIVEL_LOG.ERROR.equals(nivel))
				{res=true;}
				else {res=false;}
			}
			else if (NIVEL_LOG.WARNING.equals(this.nivel))
			{
				if (NIVEL_LOG.WARNING.equals(nivel)|| NIVEL_LOG.TRACE.equals(nivel) || NIVEL_LOG.ERROR.equals(nivel))
				{res=true;}
				else {res=false;}
			}
			else if (NIVEL_LOG.TRACE.equals(this.nivel))
			{
				if (NIVEL_LOG.TRACE.equals(nivel)|| NIVEL_LOG.ERROR.equals(nivel))
				{res=true; } 
				else { res=false;}
			}
			else if (NIVEL_LOG.ERROR.equals(this.nivel))
			{
				if (NIVEL_LOG.ERROR.equals(nivel))
				{res=true;}
				else {res=false;}
			}
			else if (NIVEL_LOG.NONE.equals(this.nivel))
			{
				res=false;
			}
		}
		catch (Exception ex) // En principio sólo debería ocurrir porque hay un error de tiempo de ejecución. En ese caso, devolvemos true para que imprima todo se pase el 
							// parámetro que se pase, incluído error.
		{
			res=true;
		}
		return res;
	}
	/**
	 * 
	 * @param message
	 * @param nivel
	 * @return
	 */
	public String formatea(String message,NIVEL_LOG nivel)
	{
			Date today = new Date();
            String completeMsg = today + " :: " + nivel + " :: " + message;
            if (this.idSesion!=null)
            {
            	completeMsg+="::"+this.idSesion;
            }
            return completeMsg;
	}

	/**
	 * Realiza el log del mensaje en el nivel que se indica
	 * @param message Mensaje a imprimir en log
	 * @param level {@link NIVEL_LOG} que se utilizará
	 */
	public void doLog(String message, NIVEL_LOG level)
	{
		OutputStreamWriter writer=null;
        PrintWriter pw=null;

        try
        {
        	File dest= new File(this.fichero);
        	String completeMsg="";
       		completeMsg = formatea(message, level);
        	createDirectories(dest);
        	
           	writer = new OutputStreamWriter(new FileOutputStream(dest,true));
            if (writer!=null)
            {
            	pw = new PrintWriter(writer);
            	pw.println(completeMsg);
            }
            
        }
        catch (IOException e)
        {
            System.out.println("Log:Error escribiendo log :'"+message+"' -> "+e.getMessage());
            e.printStackTrace();
        }
        catch (LogException e)
        {
        	System.out.println("Log:Error escribiendo log :'"+message+"' -> "+e.getMessage());
            e.printStackTrace();
        }
        finally
        {
        	try
        	{
        		if (pw!=null)
        		{
        			pw.close();
        		}
        	}
        	catch (Exception e) 
        	{
        	}
            try
            {
	            if(writer != null)
	            {
	            	writer.close();
	            }
            }
            catch(Exception e)
            {
            }
        }
	}
	
	/**
	 * Genera una entrada de Log con el mensaje indicado y el nivel de log de ERROR
	 * @param msg Mensaje
	 */
	public void error(String msg) {
		if (esImprimible(NIVEL_LOG.ERROR))
		{doLog(msg,NIVEL_LOG.ERROR);}
	}
	/**
	 * Genera una entrada de Log con el mensaje indicado y el nivel de log de DEBUG
	 * @param msg Mensaje
	 */
	public void debug(String msg) {
		if (esImprimible(NIVEL_LOG.DEBUG))
		{doLog(msg,NIVEL_LOG.DEBUG);}
	}
	/**
	 * Genera una entrada de Log con el mensaje indicado y el nivel de log de INFO
	 * @param msg Mensaje
	 */
	public void info(String msg) {
		if (esImprimible(NIVEL_LOG.INFO))
		{doLog(msg,NIVEL_LOG.INFO);}

	}
	/**
	 * Genera una entrada de Log con el mensaje indicado y el nivel de log de WARNING
	 * @param msg Mensaje
	 */
	public void warning(String msg) {
		if (esImprimible(NIVEL_LOG.WARNING))
		{doLog(msg,NIVEL_LOG.WARNING);}
	}
	/**
	 * Genera una entrada de Log con el mensaje indicado y el nivel de log de TRACE
	 * @param msg Mensaje
	 */
	public final void trace(String message)
	{	
		if (esImprimible(NIVEL_LOG.INFO))
		{
			doLog(message,NIVEL_LOG.TRACE);
		
		}
	}
	/**
	 * Genera una entrada de Log con el mensaje indicado y el nivel de log de TRACE
	 * Recupera información de la pila de ejecución y la imprime
	 * @param msg Mensaje
	 */
	public final void trace(StackTraceElement[] stackTraceElements)
	{
		 if (stackTraceElements == null)
		 {
	            return;
		 }
		 if (esImprimible(NIVEL_LOG.INFO))
		 {
	        for (int i = 0; i < stackTraceElements.length; i++)
	        {
	            doLog("StackTrace -> " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getClassName() + " :: " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getMethodName() + " :: " + stackTraceElements[i].getLineNumber(),NIVEL_LOG.TRACE);
	        }
		 }
	}
	
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de ERROR.
	 * También recupera información de la excepción y la escribe.
	 * @param msg Mensaje a escribir.
	 * @param excepcion excepción de la que se recuperará información extra para escribir.
	 */
	public void error(String msg, Throwable excepcion) {
		String mensaje="";
		if (excepcion!=null)
		{
				StackTraceElement[] stack=excepcion.getStackTrace();
				if (stack.length>0)
				{
				//Datos del punto donde se produjo el error.
				mensaje = mensaje + "["+stack[0].getClassName()+"."+stack[0].getMethodName()+" línea "+stack[0].getLineNumber()+"]::";
				}
		}
		mensaje =mensaje +msg;
		doLog(mensaje,NIVEL_LOG.ERROR);
	}
	
	/**
	 * Crear directorios del archivo, si no existen.
	 * @param fichero fichero de log
	 * @throws LogException En caso de no poder crear los directorios
	 */
	private void createDirectories(File fichero) throws LogException
	{
		String directorio = fichero.getParent();
		if (directorio==null) {
			return;
		}
		File dir = new File(directorio);
		//Podría haber un problema si más de un hilo detecta que los directorios no existen
		//e intentan crearlos a la vez. En ese caso uno de los hilos terminará bien y los otros
		//fallarán.  No se controla, ya que solo puede ocurrir la primera vez o si se borran
		//los directorios, y la opción es sincronizar los accesos que haría que entrasen
		//en serie todas las llamadas, ralentizando la ejecución.
		if (!dir.exists() ){
			if (!dir.mkdirs())
			{
				throw new LogException("Log:No se puede crear el directorio de log:" + dir.getAbsolutePath());
			}
		}
	}


}
