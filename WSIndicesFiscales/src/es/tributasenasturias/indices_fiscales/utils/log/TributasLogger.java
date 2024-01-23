package es.tributasenasturias.indices_fiscales.utils.log;

import java.io.File;


/**
 * Clase que realiza el log de aplicación. Se apoya en un escritor de log y en un {@link IFormateadorMensaje}
 * @author crubencvs
 *
 */
public class TributasLogger extends BaseLog{
		/**
		 * Crea un fichero de log para tributas, indicando el nivel de mensajes de log que servirá como filtro.
		 * @param nivel Nivel de log que se va a utilizar.
		 * @param ficheroLog Fichero de Log
		 */
		protected TributasLogger(NIVEL_LOG nivel,String ficheroLog)
		{
			super();
			FormateadorTributas formateador=new FormateadorTributas();
			try
			{
			File fichero = new File (ficheroLog);
			ILogWriter logWriter =new TributasLogWriter(fichero); 
			logWriter.setNivelLog(nivel);
			this.getWriters().add(logWriter);
			logWriter.setFormateador(formateador);
			}
			catch (LogException ex)
			{
				//No se ha podido crear el log. Escribimos en consola para informar de ello, y 
				//nos aseguramos que el log sea el Dummy, para que no haya problemas en las llamadas.
				this.getWriters().add(new DummyLogWriter());
				System.err.println ("Error al construir el log:"+ ex.getMessage());
				ex.printStackTrace();
			}
		}
		/**
		 * Crea un fichero de log para tributas, indicando el nivel de mensajes de log que servirá como filtro
		 * y el juego de caracteres en que se escribirán las entradas al log.
		 * @param nivel Nivel de log que se va a utilizar.
		 * @param ficheroLog Fichero de Log
		 */
		protected TributasLogger(NIVEL_LOG nivel,String ficheroLog, String charsetName)
		{
			super();
			FormateadorTributas formateador=new FormateadorTributas();
			try
			{
			File fichero = new File (ficheroLog);
			ILogWriter logWriter =new TributasLogWriter(fichero,nivel,charsetName); 
			logWriter.setNivelLog(nivel);
			this.getWriters().add(logWriter);
			logWriter.setFormateador(formateador);
			}
			catch (LogException ex)
			{
				//No se ha podido crear el log. Escribimos en consola para informar de ello, y 
				//nos aseguramos que el log sea el Dummy, para que no haya problemas en las llamadas.
				this.getWriters().add(new DummyLogWriter());
				System.err.println ("Error al construir el log:"+ ex.getMessage());
				ex.printStackTrace();
			}
		}
		/**
		 * Establece la información extra que se incluirá en cada línea de log
		 * @param infoExtra
		 */
		public void setInfoExtra (String infoExtra)
		{
			for (ILogWriter writer:this.getWriters())
			{
				if (writer!=null && writer.getFormateador() instanceof FormateadorTributas)
				{
					((FormateadorTributas)writer.getFormateador()).setInfoExtra(infoExtra);
				}
			}
		}
}
