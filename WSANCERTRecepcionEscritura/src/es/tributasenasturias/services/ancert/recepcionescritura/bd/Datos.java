package es.tributasenasturias.services.ancert.recepcionescritura.bd;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import com.sun.xml.ws.client.BindingProviderProperties;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.EscrituraDO;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;



public class Datos implements IContextReader
{
	private final static String STRING_CADE = "STRING_CADE";
	private final static String STRING1_CANU ="STRING1_CANU";
	private final static String ERRORNODE = "error";
	
	//Tiempo de Timeout. 60 segundos para respuesta de lanzador, no debería tardar más nunca.
	private final static String TIMEOUT="60000";
	private stpa.services.LanzaPLService lanzaderaWS; // Servicio Web
	private stpa.services.LanzaPL lanzaderaPort; // Port (operaciones) a las que se llamas
	private ConversorParametrosLanzador conversor;
	private Preferencias preferencias;
	private String errorLlamada;
	private CallContext context;
	
	@SuppressWarnings("unchecked")
	public Datos(CallContext context) throws SystemException
	{
		try
		{
			this.context=context;
			preferencias=PreferenciasFactory.newInstance();
			if (preferencias==null)
			{
				throw new SystemException ("No se pueden recuperar las preferencias.");
			}
			LogHelper log=LogFactory.getLogAplicacionContexto(context);
			String endPointLanzador=preferencias.getEndPointLanzador();
			lanzaderaWS = new stpa.services.LanzaPLService();
			if (!endPointLanzador.equals(""))
			{
				log.debug ("Se utiliza el endpoint de lanzadera: " + endPointLanzador);
				lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();
				javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; // enlazador de protocolo para el servicio.
				bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endPointLanzador); // Cambiamos el endpoint
				//Se establece un tiempo de Timeout. 
				bpr.getRequestContext().put (BindingProviderProperties.REQUEST_TIMEOUT,TIMEOUT); 
				Binding bi = bpr.getBinding();
				List <Handler> handlerList = bi.getHandlerChain();
				if (handlerList == null)
				   handlerList = new ArrayList<Handler>();
				handlerList.add(new SoapClientHandler(log.getSessionId(), preferencias));
				bi.setHandlerChain(handlerList);
			}
			else
			{
				lanzaderaPort =lanzaderaWS.getLanzaPLSoapPort();
				javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; // enlazador de protocolo para el servicio.
				//Se establece un tiempo de Timeout. 
				bpr.getRequestContext().put (BindingProviderProperties.REQUEST_TIMEOUT,TIMEOUT);
				Binding bi = bpr.getBinding();
				List <Handler> handlerList = bi.getHandlerChain();
				if (handlerList == null)
				   handlerList = new ArrayList<Handler>();
				handlerList.add(new SoapClientHandler(log.getSessionId(), preferencias));
				bi.setHandlerChain(handlerList);
				log.debug ("Se utiliza el endpoint de lanzadera por defecto: " + bpr.getRequestContext().get (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
			}
			conversor = new ConversorParametrosLanzador();
		}
		catch (RuntimeException ex)
		{
			throw ex; //Esta excepción es grave, no se debería convertir.
		}
		catch (Exception ex)
		{
			throw new SystemException ("Problema al construir el objeto de datos." + ex.getMessage(),ex);
		}
	}

	/**
	 * Devuelve un 'S' si la escritura está duplicada en la tabla, o 'N' si no.
	 * @param idescritura Datos de la escritura.
	 * @return 'S' si está duplicada, 'N' si no.
	 * @throws SystemException En caso de que haya un problema al comprobar si la escritura está duplicada
	 */
	public String escrituraDuplicada(EscrituraDO idescritura) throws SystemException
	{
		conversor.setProcedimientoAlmacenado(preferencias.getPAEscrituraDuplicada());
		conversor.setParametro(idescritura.getCodNotario(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getCodNotaria(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getNumProtocolo(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getProtocoloBis(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getFechaEscritura(), ConversorParametrosLanzador.TIPOS.Date,"DD-MM-YYYY");
		
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		
		try
		{
			String resultadoEjecutarPL = Ejecuta();
			conversor.setResultado(resultadoEjecutarPL);
			this.setErrorLlamada(conversor.getNodoResultado(ERRORNODE));
			
			//Recogemos el resultado.
			String res = conversor.getNodoResultado(STRING_CADE);
			return res;
		}
		catch (RemoteException ex)
		{
			throw new SystemException ("Problema al comprobar si la escritura está duplicada.",ex);
		}
	}
	/**
	 * Devuelve el id de solicitud, que puede ser el que existía si la solicitud se hizo desde
	 * EPST, o el que que se ha creado al insertar la solicitud automática si la solicitud se creó
	 * porque nos llegó una escritura que no habíamos solicitado.
	 * @param idescritura Datos de la escritura.
	 * @return Id de solicitud o cadena vacía si no existe
	 * @throws SystemException En caso de que haya un problema al recuperar el id de solicitud
	 */
	public String getIdSolicitud(EscrituraDO idescritura) throws SystemException
	{
		conversor.setProcedimientoAlmacenado(preferencias.getPARecuperaIdSolicitud());
		conversor.setParametro(idescritura.getCodNotario(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getCodNotaria(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getNumProtocolo(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getProtocoloBis(), ConversorParametrosLanzador.TIPOS.String);
		if (idescritura.getFechaEscritura()!=null){
			conversor.setParametro(idescritura.getFechaEscritura(), ConversorParametrosLanzador.TIPOS.Date,"DD-MM-YYYY");
		} else if (idescritura.getAnyoEscritura()!=null){
			conversor.setParametro(idescritura.getAnyoEscritura(), ConversorParametrosLanzador.TIPOS.Integer);
		}
		
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		
		try
		{
			String resultadoEjecutarPL = Ejecuta();
			conversor.setResultado(resultadoEjecutarPL);
			this.setErrorLlamada(conversor.getNodoResultado(ERRORNODE));
			
			//Recogemos el resultado.
			String res = conversor.getNodoResultado(STRING1_CANU).trim();
			return res;
		}
		catch (RemoteException ex)
		{
			throw new SystemException ("Problema al comprobar si la escritura está duplicada.",ex);
		}
	}
	public String finalizarRecepcionEscritura(EscrituraDO idescritura) throws SystemException
	{
		conversor.setProcedimientoAlmacenado(preferencias.getPAFinalizarEscritura());
		conversor.setParametro(idescritura.getCodNotario(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getCodNotaria(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getNumProtocolo(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getProtocoloBis(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(idescritura.getFechaEscritura(), ConversorParametrosLanzador.TIPOS.Date,"DD-MM-YYYY");
		conversor.setParametro(idescritura.isAutorizacionEnvioDiligencias()?"S":"N", ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		
		try
		{
			String resultadoEjecutarPL = Ejecuta();
			conversor.setResultado(resultadoEjecutarPL);
			this.setErrorLlamada(conversor.getNodoResultado(ERRORNODE));
			
			//Recogemos el resultado.
			String res = conversor.getNodoResultado(STRING1_CANU).trim();
			return res;
		}
		catch (RemoteException ex)
		{
			throw new SystemException ("Problema al finalizar la recepción de escritura.",ex);
		}
	}
	
	public String denegarEscritura(String idSolicitud, String motivo) throws SystemException
	{
		conversor.setProcedimientoAlmacenado(preferencias.getPADenegacionEscritura());
		conversor.setParametro(idSolicitud, ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(motivo, ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		
		try
		{
			String resultadoEjecutarPL = Ejecuta();
			conversor.setResultado(resultadoEjecutarPL);
			this.setErrorLlamada(conversor.getNodoResultado(ERRORNODE));
			
			//Recogemos el resultado.
			String res = conversor.getNodoResultado(STRING1_CANU).trim();
			return res;
		}
		catch (RemoteException ex)
		{
			throw new SystemException ("Problema al comprobar si la escritura está duplicada.",ex);
		}
	}
	private String Ejecuta() throws RemoteException
	{
		//_log.info("Ejecutando llamada al servicio lanzadera para acceso a base de datos: "+conversor.Codifica());
		return lanzaderaPort.executePL(
				preferencias.getEsquemaBaseDatos(),
				conversor.codifica(),
				"", "", "", "");
	}
	/**
	 * Método que devuelve el error en la ultima llamada a un procedimiento de esta clase.
	 * Los errores en llamada son devueltos en formato XML, este procedimiento devuelve el error
	 * extraído en formato de cadena.
	 * @return
	 */
	public String getErrorLlamada() {
		return errorLlamada;
	}

	private void  setErrorLlamada(String errorLlamada) {
		this.errorLlamada = errorLlamada;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
}

