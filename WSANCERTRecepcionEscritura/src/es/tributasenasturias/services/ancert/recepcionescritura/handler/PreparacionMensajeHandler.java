package es.tributasenasturias.services.ancert.recepcionescritura.handler;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Node;

import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.XMLDOMDocumentException;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.XMLDOMUtils;

/**
 * Clase que intercepta el mensaje de entrada, pasa por contexto el XML de recepción original,
 * y elimina el XML del mensaje SOAP, de tal forma que no entren parámetros al servicio.
 * Los parámetros se pasarán por contexto de servicio, de tal manera que a la entrada
 * tengamos una copia --exacta-- de lo que nos ha enviado ellos.
 * Esto se hace así para que el servidor no interprete el XML de entrada utilizando
 * JAXB, ya que esto, además de ser lento, puede romper la firma de XML. 
 * @author crubencvs
 * 
 */
public class PreparacionMensajeHandler implements SOAPHandler<SOAPMessageContext> {
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

	private void destruirXmlContexto (SOAPMessageContext context)
	{
		if (((Boolean) context
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue()) {
			context.remove(HandlerConstant.getXmlServicio());
		}
	}
	private void extraeCarga(SOAPMessageContext context)
			throws SOAPException {
		LogHelper log = null;
		try {
			String idLog=(String) context.get(HandlerConstant.getIdLog());
			log = LogFactory.newSoapServerLog(idLog);
			Boolean salida = (Boolean) context
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue()) {
				//A la entrada, extraemos el XML.
				Node nodoMensaje=XMLDOMUtils.selectSingleNode(context.getMessage().getSOAPBody(), "//*[local-name()='ENVIO_ESCRITURAS']");
				Node nodoRecepcion=XMLDOMUtils.selectSingleNode(context.getMessage().getSOAPBody(), "//*[local-name()='RecepcionEscritura']");
				String xml="";
				if (nodoMensaje!=null) {
					xml = XMLDOMUtils.getXMLText(nodoMensaje);
				}
				context.put(HandlerConstant.getXmlServicio(), xml);
				context.setScope(HandlerConstant.getXmlServicio(), MessageContext.Scope.APPLICATION);
				//Eliminamos el contenido del nodo de la entrada. Esto se hace para ahorrar algo de memoria en mensajes grandes.
				//context.getMessage().getSOAPBody().removeChild(nodoMensaje);
				nodoRecepcion.removeChild(nodoMensaje);
			}
			else
			{
				//A la salida, el mensaje que está en contexto se hace el mensaje final.
				String mensaje=(String)context.get(HandlerConstant.getXmlServicio());
				Node nodoRecepcion=XMLDOMUtils.selectSingleNode(context.getMessage().getSOAPBody(), "//*[local-name()='RecepcionEscrituraResponse']");
				//Añadimos como primer hijo de este, el texto de xml de respuesta.
				Node respuesta = XMLDOMUtils.parseXml(mensaje).getDocumentElement();
				respuesta=nodoRecepcion.getOwnerDocument().importNode(respuesta, true);
				nodoRecepcion.appendChild(respuesta);
				destruirXmlContexto(context);
			}
		} catch (javax.xml.soap.SOAPException ex) {
			log
					.doLogSoapServer("Error en la grabación de log de SOAP servidor:"
							+ ex.getMessage());
			log.trace(ex.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de escrituras.");
		} catch (XMLDOMDocumentException e) {
			log
			.doLogSoapServer("Error en la extracción de XML de mensaje:"
					+ e.getMessage());
			log.trace(e.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de escrituras.");
		}
		catch (Exception ex)
		{
			log
			.doLogSoapServer("Error en la extracción de XML de mensaje:"
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
		destruirXmlContexto(context);
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		try {
			extraeCarga(context);
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return true;
	}
}
