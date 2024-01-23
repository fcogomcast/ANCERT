package es.tributasenasturias.services.ancert.solicitudEscritura.handler;

import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils;

/**
 * Clase que intercepta el mensaje de entrada, y genera un Id �nico de sesi�n para �l,
 *  para identificar sus entradas en los log. 
 * @author crubencvs
 *
 */
public class IdLog implements SOAPHandler<SOAPMessageContext> {

	@Override
	public Set<QName> getHeaders() {
		return Collections.emptySet();
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!salida.booleanValue())
		{
			//En entrada, generamos un nuevo n�mero �nico para los log de este servicio, y lo propagamos
			// a trav�s del contexto del mensaje.
			// El n�mero ser� la sesi�n.
			HttpServletRequest req = (HttpServletRequest) context.get(SOAPMessageContext.SERVLET_REQUEST);
			String idLog="";
			if (req!=null)
			{
				String id=req.getSession().getId();
				idLog = id.substring(id.length()-12);
			}
			else
			{ //Lo generamos, para que exista.
				idLog=Utils.getIdLlamada();
			}
			context.put("IdLog", idLog);
			context.setScope("IdLog", MessageContext.Scope.APPLICATION);
		}
		return true;
	}

	

}
