package es.tributasenasturias.services.ancert.enviodiligencias.log;

public class LogSoapserver extends BaseLog {
	protected LogSoapserver(String sessionId)
	{
		super(sessionId);
		FormateadorMensaje formateador=new FormateadorMensaje();
		formateador.setSesionId(sessionId);
		formateador.setIdentificadorLog("Soap Server");
		try
		{
		log=new TributasLogWriter("SoapServer.log","proyectos/WSAncertEnvioDiligencias/log");
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
