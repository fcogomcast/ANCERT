package es.tributasenasturias.services.ancert.solicitudEscritura.bd;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.PreferenciasFactory;

/**
 * Gestiona la conexión con base de datos necesaria para la operativa con los certificados que vienen
 * en mensajes, para comprobar si tienen permisos para utilizar el servicio.
 * @author crubencvs
 *
 */
public class GestorCertificadosBD implements IContextReader{
	private stpa.services.LanzaPLService lanzaderaWS; // Servicio Web
	private stpa.services.LanzaPL lanzaderaPort; // Port (operaciones) a las que se llamas
	private Preferencias preferencias;
	private ConversorParametrosLanzador conversor;
	private CallContext context;
	public static enum PermisosServicio {AUTORIZADO,NO_AUTORIZADO,ERROR};
	@SuppressWarnings("unchecked")
	protected GestorCertificadosBD(CallContext context) throws SystemException
	{
		try
		{
			conversor=new ConversorParametrosLanzador();
			LogHelper log=LogFactory.getLogAplicacionContexto(context);
			//Preparamos las preferencias en función del contexto.
			preferencias=PreferenciasFactory.newInstance();
			if (preferencias==null)
			{
				throw new SystemException ("No se pueden recuperar las preferencias.");
			}
			String endPointLanzador=preferencias.getEndPointLanzador();
			lanzaderaWS = new stpa.services.LanzaPLService();
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
				//El manejador de cliente enviará su log con el mismo id de sesión que el log de aplicación.
				handlerList.add(new SoapClientHandler(log.getSessionId(), preferencias));
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
				//El manejador de cliente enviará su log con el mismo id de sesión que el log de aplicación.
				handlerList.add(new SoapClientHandler(log.getSessionId(), preferencias));
				bi.setHandlerChain(handlerList);
				log.debug (this.getClass().getName()+" .Se utiliza el endpoint de lanzadera por defecto: " + bpr.getRequestContext().get (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
			}
		}
		catch (Exception ex)
		{
			if (!(ex instanceof SystemException))
			{
				throw new SystemException ("Problema al preparar la conexión." + ex.getMessage(),ex);
			}
		}
	}
	private String Ejecuta() throws RemoteException
	{
		return lanzaderaPort.executePL(
				preferencias.getEsquemaBaseDatos(),
				conversor.Codifica(),
				"", "", "", "");
	}
	/**
	 * Método para recuperar la plantilla de consulta de certificado.
	 * @return
	 * @throws SystemException
	 */
	public String recuperaPlantillaCertificado() throws SystemException {
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
				throw new SystemException (this.getClass().getName()+" .No se ha podido recuperar la plantilla para consulta de certificado:" + errorLlamada);
			}
			// Recogemos la plantilla
			String plantilla = conversor.getNodoResultado("C1");
			plantilla = es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils
					.unescapeHTML(plantilla);
			return plantilla;
		} catch (RemoteException ex) {
			throw new SystemException(
					this.getClass().getName()+" .Problema al recuperar la plantilla de base de datos."
							+ ex.getMessage(), ex);
		}
	}
	/**
	 * Método que llama al procedimiento de comprobación de permisos para el servicio.
	 * @param cifNif - el NIF/CIF de la persona cuyos permisos sobre el servicio se van a comprobar.
	 * @return - un valor que indicará el retorno del servicio, si ha sido correcto o no.
	 * @throws SystemException
	 */
	public PermisosServicio permisoServicio(String cifNif) throws SystemException
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
				throw new SystemException (this.getClass().getName()+" .Error producido al consultar el permiso de ejecución del servicio:"+ errorLlamada);
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
			throw new SystemException (ex.getMessage());
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
