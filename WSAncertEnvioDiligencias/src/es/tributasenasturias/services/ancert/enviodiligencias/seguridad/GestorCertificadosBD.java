package es.tributasenasturias.services.ancert.enviodiligencias.seguridad;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.services.ancert.enviodiligencias.SoapClientHandler;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.ConversorParametrosLanzador;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogAplicacion;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.utilidadesGenerales.General;
import es.tributasenasturias.services.lanzador.LanzaPL;
import es.tributasenasturias.services.lanzador.LanzaPLService;



/**
 * Gestiona la conexi�n con base de datos necesaria para la operativa con los certificados que vienen
 * en mensajes, para comprobar si tienen permisos para utilizar el servicio.
 * @author crubencvs
 *
 */
public class GestorCertificadosBD implements IContextReader{
	private LanzaPLService lanzaderaWS; // Servicio Web
	private LanzaPL lanzaderaPort; // Port (operaciones) a las que se llamas
	private Preferencias preferencias;
	private ConversorParametrosLanzador conversor;
	private CallContext context;
	public static enum PermisosServicio {AUTORIZADO,NO_AUTORIZADO,ERROR};
	@SuppressWarnings("unchecked")
	protected GestorCertificadosBD(CallContext context) throws SeguridadException
	{
		try
		{
			conversor=new ConversorParametrosLanzador();
			LogAplicacion log=LogFactory.getLogAplicacionContexto(context);
			//Preparamos las preferencias en funci�n del contexto.
			preferencias=PreferenciasFactory.newInstance();
			if (preferencias==null)
			{
				throw new SeguridadException ("No se pueden recuperar las preferencias.");
			}
			String endPointLanzador=preferencias.getEndPointLanzador();
			lanzaderaWS = new LanzaPLService();
			if (!endPointLanzador.equals(""))
			{
				log.debug ("Se utiliza el endpoint de lanzadera: " + endPointLanzador);
				lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();
				javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; // enlazador de protocolo para el servicio.
				bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endPointLanzador); // Cambiamos el endpoint
				Binding bi = bpr.getBinding();
				List <Handler> handlerList = bi.getHandlerChain();
				if (handlerList == null)
				   handlerList = new ArrayList<Handler>();
				//El manejador de cliente enviar� su log con el mismo id de sesi�n que el log de aplicaci�n.
				handlerList.add(new SoapClientHandler(log.getSessionId()));
				bi.setHandlerChain(handlerList);
			}
			else
			{
				lanzaderaPort =lanzaderaWS.getLanzaPLSoapPort();
				javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; // enlazador de protocolo para el servicio.
				Binding bi = bpr.getBinding();
				List <Handler> handlerList = bi.getHandlerChain();
				if (handlerList == null)
				   handlerList = new ArrayList<Handler>();
				//El manejador de cliente enviar� su log con el mismo id de sesi�n que el log de aplicaci�n.
				handlerList.add(new SoapClientHandler(log.getSessionId()));
				bi.setHandlerChain(handlerList);
				log.debug ("Se utiliza el endpoint de lanzadera por defecto: " + bpr.getRequestContext().get (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
			}
		}
		catch (Exception ex)
		{
			if (!(ex instanceof SeguridadException))
			{
				throw new SeguridadException ("Problema al preparar la conexi�n." + ex.getMessage(),ex);
			}
		}
	}
	private String Ejecuta() throws RemoteException
	{
		return lanzaderaPort.executePL(
				preferencias.getEsquemaBaseDatos(),
				conversor.codifica(),
				"", "", "", "");
	}
	/**
	 * M�todo para recuperar la plantilla de consulta de certificado.
	 * @return
	 * @throws SystemException
	 */
	public String recuperaPlantillaCertificado() throws SeguridadException {
		conversor.setProcedimientoAlmacenado(preferencias.getPAObtenerPlantilla());
		conversor.setParametro(preferencias.getNombrePlantillaCertificado(),
				ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		String errorLlamada;
		try {
			String resultadoEjecutarPL = Ejecuta();
			conversor.setResultado(resultadoEjecutarPL);
			errorLlamada = conversor.getNodoResultado("error");
			if (!errorLlamada.equals(""))
			{
				throw new SeguridadException ("No se ha podido recuperar la plantilla para consulta de certificado:" + errorLlamada);
			}
			// Recogemos la plantilla
			String plantilla = conversor.getNodoResultado("C1");
			plantilla = General.unescapeHTML(plantilla);
			return plantilla;
		} catch (RemoteException ex) {
			throw new SeguridadException(
					"Problema al recuperar la plantilla de base de datos."
							+ ex.getMessage(), ex);
		}
	}
	/**
	 * M�todo que llama al procedimiento de comprobaci�n de permisos para el servicio.
	 * @param cifNif - el NIF/CIF de la persona cuyos permisos sobre el servicio se van a comprobar.
	 * @return - un valor que indicar� el retorno del servicio, si ha sido correcto o no.
	 * @throws SeguridadException
	 */
	public PermisosServicio permisoServicio(String cifNif) throws SeguridadException
	{
		conversor.setProcedimientoAlmacenado(preferencias.getPAPermisoServicio());
		conversor.setParametro(preferencias.getAliasPermisoServicio(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(cifNif, ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		
		try
		{
			String errorLlamada="";
			String resultadoEjecutarPL = Ejecuta();
			
			conversor.setResultado(resultadoEjecutarPL);
			errorLlamada= conversor.getNodoResultado("error");
			if (!errorLlamada.equals(""))
			{
				throw new SeguridadException ("Error producido al consultar el permiso de ejecuci�n del servicio:"+ errorLlamada);
			}
			String autorizacion = conversor.getNodoResultado("STRING_CADE");
			PermisosServicio resultado;
			if (autorizacion.equals("01"))
			{
				resultado=PermisosServicio.NO_AUTORIZADO;
			}
			else if (autorizacion.equals("02"))
			{
				resultado=PermisosServicio.ERROR;
			}
			else
			{
				resultado=PermisosServicio.AUTORIZADO;
			}
			return resultado;
		}
		catch (RemoteException ex)
		{
			throw new SeguridadException (ex.getMessage());
		}
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
