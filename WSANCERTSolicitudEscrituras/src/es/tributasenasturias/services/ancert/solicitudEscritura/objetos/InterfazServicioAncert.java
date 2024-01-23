package es.tributasenasturias.services.ancert.solicitudEscritura.objetos;

import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.services.ancert.serviceDispatcher.ServiceDispatcherANCERT;
import es.tributasenasturias.services.ancert.serviceDispatcher.ServiceDispatcherService;
/**
 * Clase que encapsula algunas de las funcionalidades de rutina para llamar al servicio web.
 * @author crubencvs
 *
 */
@Deprecated
public class InterfazServicioAncert {
	private ServiceDispatcherANCERT port=null;
	private ServiceDispatcherService srv=null;
	Handler<SOAPMessageContext> manejador=null;
	public void setManejador(Handler<SOAPMessageContext> manejador) {
		this.manejador = manejador;
	}
	public ServiceDispatcherANCERT getPort() {
		return port;
	}
	/**
	 * Establece el endpoint del servicio.
	 * @param p - Port (ServiceDispatcherANCERT) en el que establecer el endpoint.
	 * @param endpoint - Cadena que contiene el endpoint a establecer. 
	 * @return - el port con el endpoint modificado.
	 */
	private ServiceDispatcherANCERT setEndpoint(ServiceDispatcherANCERT p,String endpoint)
	{
		ServiceDispatcherANCERT ret=p;
		BindingProvider bp=(BindingProvider)ret;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		return ret;
	}
	/**
	 * Establece el logger para mensajes SOAP con el endpoint de servicio.
	 * @param p - Port (ServiceDispatcherANCERT) que se logeará.
	 * @param manejador - Instancia de una clase manejador (implementará Handler). Se utilizará para 
	 * 					  los log de los mensajes intercambiados con el endpoint de servicio. 
	 * @return - el port con el logger establecido.
	 */
	@SuppressWarnings("unchecked")
	private ServiceDispatcherANCERT setLogger(ServiceDispatcherANCERT p,Handler manejador)
	{
		ServiceDispatcherANCERT ret=p;
		BindingProvider bp=(BindingProvider)ret;
		List<Handler> lista=bp.getBinding().getHandlerChain();
		lista.add(manejador);
		bp.getBinding().setHandlerChain(lista);
		return ret;
	}
	/**
	 * Recupera una nueva instancia de ServiceDispatcherANCERT contra el endpoint establecido.
	 * Si se ha llamado antes a "setLogger" para la instancia del objeto InterfazServicioAncert
	 * se utilizará también ese logger.
	 * @param endpoint
	 * @return
	 */
	public ServiceDispatcherANCERT getNewPort(String endpoint)
	{
		srv=new ServiceDispatcherService();
		port=srv.getServiceDispatcher();
		port=setEndpoint(port,endpoint);
		if (manejador!=null)
		{
			port=setLogger(port,manejador);
		}
		return port;
	}
}
