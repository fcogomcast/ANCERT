package es.tributasenasturias.services.ancert.recepcionescritura.handler;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.General;

/**
 * Clase que intercepta los mensajes de entrada y los escribe en los log.
 * 
 * @author crubencvs
 * 
 */
public class SoapServerHandler implements SOAPHandler<SOAPMessageContext> {

	public static SOAPFaultException createSOAPFaultException(
			String pFaultString) throws SOAPException {
		SOAPFaultException sex = null;
		try {
			SOAPFault fault = SOAPFactory.newInstance().createFault();
			fault.setFaultString(pFaultString);
			fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE,
					"RecepciónEscrituras"));
			sex = new SOAPFaultException(fault);
		} catch (Exception ex) {
			throw new SOAPException(
					"Recepción Escrituras:===>Error grave al construir la excepción de SOAP."
							+ ex.getMessage());
		}
		return sex;
	}

	private String getIdSesion(MessageContext msg) {
		return General.getIdLlamada();
	}

	private void log(SOAPMessageContext context)
			throws SOAPException {
		LogHelper log = null;
		try {
			String idLog = "Sesion no inicializada";
			Boolean salida = (Boolean) context
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue()) {
				idLog = getIdSesion(context);
			} else {
				idLog = (String) context.get("IdLog");
				if (idLog == null) {
					idLog = "Sesion no inicializada";
				}
			}
			String direccion = (salida) ? "Envío" : "Recepción";
			context.put(HandlerConstant.getIdLog(), idLog);
			context.setScope(HandlerConstant.getIdLog(), MessageContext.Scope.APPLICATION);
			log = LogFactory.newSoapServerLog(idLog);
			SOAPMessage msg = context.getMessage();
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			msg.writeTo(byteArray);
			String soapMessage = new String(byteArray.toByteArray());
			if (log != null) {
				log.doLogSoapServer(direccion + "::" + soapMessage);
			}
		} catch (javax.xml.soap.SOAPException ex) {
			log
					.doLogSoapServer("Error en la grabación de log de SOAP servidor:"
							+ ex.getMessage());
			log.trace(ex.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de escrituras.");
		} catch (java.io.IOException ex) {
			log
					.doLogSoapServer("Error en la grabación de log de SOAP servidor:"
							+ ex.getMessage());
			log.trace(ex.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de escrituras.");
		}
	}

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
		try {
			log(context);
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return true;
	}
}
