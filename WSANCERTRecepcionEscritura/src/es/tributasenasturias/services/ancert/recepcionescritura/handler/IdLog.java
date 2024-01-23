package es.tributasenasturias.services.ancert.recepcionescritura.handler;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import es.tributasenasturias.services.ancert.recepcionescritura.utils.General;


/**
 * Clase que intercepta el mensaje de entrada, y genera un Id único de sesión para él,
 *  para identificar sus entradas en los log. 
 * @author crubencvs
 *
 */
public class IdLog implements SOAPHandler<SOAPMessageContext> {

	

	@Override
	public final Set<QName> getHeaders() {
		return Collections.emptySet();
	}

	@Override
	public final void close(MessageContext context) {
	}

	@Override
	public final boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public final boolean handleMessage(SOAPMessageContext context) {
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!salida.booleanValue())
		{
			String idLog="";
			//Lo generamos, para que exista.
			idLog=General.getIdLlamada();
			context.put("IdLog", idLog);
			context.setScope("IdLog", MessageContext.Scope.APPLICATION);
		}
		return true;
	}

	

}
