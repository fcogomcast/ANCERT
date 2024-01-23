package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.services.ancert.enviodiligencias.SoapClientHandler;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContextConstants;

public class Utils {
	/**
	 * Establece el manejador de mensajes del servicio para llamar a otros.
	 * @param bp BindingProvider del port de servicio.
	 * @param idSesion Id de la sesión con la que se inicializará el manejador de Client Soap.
	 */
	@SuppressWarnings("unchecked")
	public static void setHandlerChain(javax.xml.ws.BindingProvider bp, CallContext context)
	{
		Binding bi= bp.getBinding();
		List<Handler> handlerList= bi.getHandlerChain();
		if (handlerList==null)
		{
			handlerList=new ArrayList<Handler>();
		}
		String idSesion="Sin Sesión.";
        if (context!=null)
        {
        	idSesion = (String) context.get(CallContextConstants.IDSESION);
        }
		handlerList.add(new SoapClientHandler(idSesion));
		bi.setHandlerChain(handlerList);
	}
}
