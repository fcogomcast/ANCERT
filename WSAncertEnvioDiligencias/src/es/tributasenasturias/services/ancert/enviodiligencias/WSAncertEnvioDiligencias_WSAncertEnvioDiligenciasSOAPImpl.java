package es.tributasenasturias.services.ancert.enviodiligencias;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContextConstants;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContextManager;

/**
 * Servicio de env�o de diligencia a notarios. Se enviar�n las diligencias
 * relaciondas con escrituras que hayan sido autorizadas para env�o de
 * diligencia.
 * 
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.3-06/19/2008
 * 07:03 PM(bt) Generated source version: 2.1
 * 
 */
@WebService(portName = "WSAncertEnvioDiligenciasSOAP", serviceName = "WSAncertEnvioDiligencias", targetNamespace = "http://enviodiligencias.ancert.services.tributasenasturias.es/WSAncertEnvioDiligencias/", wsdlLocation = "/wsdls/WSAncertEnvioDiligencias.wsdl", endpointInterface = "es.tributasenasturias.services.ancert.enviodiligencias.WSAncertEnvioDiligencias")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain (file="HandlerChain.xml")
public class WSAncertEnvioDiligencias_WSAncertEnvioDiligenciasSOAPImpl
		implements WSAncertEnvioDiligencias {

	@Resource
	WebServiceContext wcontext;
	public WSAncertEnvioDiligencias_WSAncertEnvioDiligenciasSOAPImpl() {
	}

	/**
	 * Operaci�n de env�o de diligencias. En base a los par�metros de entrada,
	 * enviar� las diligencias a notarios. Actualizar� si es necesario la tabla
	 * de documentos de internet con los documentos enviados, y marcar� el env�o
	 * 
	 * @param parameters
	 * @return returns
	 *         es.tributasenasturias.services.ancert.enviodiligencias.EnvioDiligenciasMessageOut
	 */
	public EnvioDiligenciasMessageOut enviarDiligencias(
			EnvioDiligenciasMessageInType parameters) {
		CallContext context;
		context = CallContextManager.newCallContext();
		//Recuperamos el id de sesi�n del contexto de servicio web.
		String idSesion = (String)wcontext.getMessageContext().get("IdSesion");
		if (idSesion==null)
		{
			idSesion="No se ha podido recuperar la sesi�n";
		}
		context.setItem(CallContextConstants.IDSESION, idSesion);
		EnvioDiligencias env = new EnvioDiligencias();
		env.setCallContext(context);
		return env.enviarDiligencias(parameters);
	}

}
