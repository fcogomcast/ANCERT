/**
 * 
 */
package org.notariado.inti.utils.log;







/** Implementa la funcionalidad de creación de logs. Debería utilizarse esta, y no la creación directa,
 *  porque se ocupa de la inicialización correctamente.
 * @author crubencvs
 *
 */
public class LogFactory {

	private LogFactory(){}
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
	 * Devuelve una instancia {@link Logger} que permite realizar log.
	 * Las líneas de log se representarán en el juego de caracteres por defecto de la plataforma.
	 * @param nivel Representación en texto del nivel de log (ALL,DEBUG,INFO, TRACE,WARNING,ERROR,NONE)
	 * @param ficheroLog Nombre de fichero de log en el que escribir
	 * @return instancia de LogAplicacion
	 */
	public static Logger newLogger(String nivel,String ficheroLog)
	{
		TributasLogger slog=new TributasLogger(string2Nivel(nivel),ficheroLog);
		return new Logger(slog);
	}
	/**
	 * Devuelve una instancia {@link Logger} que permite realizar log, con una información extra
	 * que aparecerá en cada línea. Esta información extra es fija, es decir, aparecerá la misma en cada línea.
	 * Se puede utilizar para representar, por ejemplo, un identificador único de sesión.
	 * Las líneas de log se representarán en el juego de caracteres por defecto de la plataforma.
	 * @param nivel Representación en texto del nivel de log (ALL,DEBUG,INFO, TRACE,WARNING,ERROR,NONE)
	 * @param ficheroLog Nombre de fichero de log en el que escribir
	 * @param infoExtra Información extra que se incluirá en cada línea de log. Puede ser un identificador de sesión
	 * @return instancia de LogAplicacion
	 */
	public static Logger newLogger(String nivel,String ficheroLog,String infoExtra)
	{
		TributasLogger slog=new TributasLogger(string2Nivel(nivel),ficheroLog);
		slog.setInfoExtra(infoExtra);
		return new Logger(slog);
	}
	/**
	 * Devuelve una instancia {@link Logger} que permite realizar log, con una información extra
	 * que aparecerá en cada línea. Esta información extra es fija, es decir, aparecerá la misma en cada línea.
	 * Se puede utilizar para representar, por ejemplo, un identificador único de sesión.
	 * También se puede indicar el juego de caracteres en que se quiere representar cada línea de log.
	 * @param nivel Representación en texto del nivel de log (ALL,DEBUG,INFO, TRACE,WARNING,ERROR,NONE)
	 * @param ficheroLog Nombre de fichero de log en el que escribir
	 * @param infoExtra Información extra que se incluirá en cada línea de log. Puede ser un identificador de sesión
	 * @param charsetName nombre del juego de caracteres a utilizar para imprimir línea, como "ISO-8859-1" o "UTF-8".
	 * Si no se indica, se utiliza el juego de caracteres por defecto.
	 * @return instancia de LogAplicacion
	 */
	public static Logger newLogger(String nivel,String ficheroLog,String infoExtra, String charsetName)
	{
		TributasLogger slog=new TributasLogger(string2Nivel(nivel),ficheroLog,charsetName);
		slog.setInfoExtra(infoExtra);
		return new Logger(slog);
	}
}
