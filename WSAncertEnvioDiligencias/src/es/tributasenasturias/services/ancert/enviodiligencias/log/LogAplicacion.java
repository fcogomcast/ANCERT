package es.tributasenasturias.services.ancert.enviodiligencias.log;


/**
 * Clase que realiza el log de aplicación. Se apoya en un escritor de log y en un {@link IFormateadorMensaje}
 * @author crubencvs
 *
 */
public class LogAplicacion extends BaseLog{
		protected LogAplicacion(String sessionId)
		{
			super();
			FormateadorMensaje formateador=new FormateadorMensaje();
			formateador.setSesionId(sessionId);
			formateador.setIdentificadorLog("Aplicación");
			try
			{
			log=new TributasLogWriter("Application.log","proyectos/WSAncertEnvioDiligencias/log");
			log.setFormateador(formateador);
			}
			catch (LogException ex)
			{
				//No se ha podido crear el log. Escribimos en consola para informar de ello, y 
				//nos aseguramos que el log sea el Dummy, para que no haya problemas en las llamadas.
				log=new DummyLogWriter();
				System.err.println ("Error al construir el log:"+ ex.getMessage());
				ex.printStackTrace();
			}
		}
}
