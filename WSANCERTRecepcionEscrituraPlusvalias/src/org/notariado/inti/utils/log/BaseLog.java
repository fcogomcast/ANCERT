package org.notariado.inti.utils.log;


import java.util.ArrayList;
import java.util.List;

public class BaseLog {

	private List<ILogWriter> writers;
	public BaseLog() {
		super();
		writers =new ArrayList<ILogWriter>();
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.services.ancert.enviodiligencias.log.ILog#doLog(java.lang.String, es.tributasenasturias.services.ancert.enviodiligencias.log.NIVEL_LOG)
	 */
	/**
	 * Genera una entrada en el log, con el mensaje indicado y un nivel de log asociado.
	 * Genera la entrada en cada uno de los {@link ILogWriter} asociados, con lo que el mismo mensaje
	 * se puede escribir a ficheros diferentes.
	 * @param msg Mensaje a escribir.
	 * @param level {@link NIVEL_LOG} asociado.
	 */
	public void doLog(String msg, NIVEL_LOG level) {
		for (ILogWriter writer:writers)
		{
			switch (level)
			{
			case DEBUG:
				writer.debug(msg);
				break;
			case INFO:
				writer.info(msg);
				break;
			case WARNING:
				writer.warning(msg);
				break;
			case TRACE:
				writer.trace(msg);
				break;
			case ERROR:
				writer.error (msg);
				break;
			default:
				writer.info(msg);
			}
		}
	}
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de DEBUG
	 * Internamente utiliza doLog, así que lo escribe a todos los {@link ILogWriter} asociados.
	 * @param msg Mensaje a escribir.
	 */
	public void debug(String msg) {
		doLog(msg,NIVEL_LOG.DEBUG);
	}
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de ERROR
	 * Internamente utiliza doLog, así que lo escribe a todos los {@link ILogWriter} asociados.
	 * @param msg Mensaje a escribir.
	 */
	public void error(String msg) {
		doLog(msg,NIVEL_LOG.ERROR);
	}
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de INFO
	 * Internamente utiliza doLog, así que lo escribe a todos los {@link ILogWriter} asociados. 
	 * @param msg Mensaje a escribir.
	 */
	public void info(String msg) {
		doLog(msg,NIVEL_LOG.INFO);
	}
	/**
	 * Genera una entrada de log con el volcado de pila que se pasa.
	 * Internamente utiliza doLog, así que lo escribe a todos los {@link ILogWriter} asociados. 
	 * @param stackTraceElements Elementos de la pila
	 */
	public void trace(StackTraceElement[] stackTraceElements) {
		
		 if (stackTraceElements == null)
		 {
	            return;
		 }
		 for (ILogWriter writer:writers)
		{
			 writer.trace(stackTraceElements);
		 }
	}
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de TRACE
	 * Internamente utiliza doLog, así que lo escribe a todos los {@link ILogWriter} asociados. 
	 * @param msg Mensaje a escribir.
	 */
	public void trace(String msg) {
		doLog (msg,NIVEL_LOG.TRACE);
	}
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de WARNING
	 * Internamente utiliza doLog, así que lo escribe a todos los {@link ILogWriter} asociados. 
	 * @param msg Mensaje a escribir.
	 */
	public void warning(String msg) {
		doLog(msg,NIVEL_LOG.WARNING);
	}
	/**
	 * Genera una entrada de log con el mensaje indicado y el nivel de log de ERROR.
	 * También recupera información de la excepción y la escribe.
	 * Internamente utiliza doLog, así que lo escribe a todos los {@link ILogWriter} asociados.
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
	 * Recupera la lista de {@link ILogWriter} asociada a este objeto.
	 * @return Lista de {@link ILogWriter}.
	 */
	public List<ILogWriter> getWriters() {
		return writers;
	}
	/**
	 * Indica la lista de {@link ILogWriter} que se utilizará para escribir los mensajes de log.
	 * @param writers {@link List} de {@link ILogWriter}.
	 */
	public void setWriters(List<ILogWriter> writers) {
		this.writers = writers;
	}

}