package es.tributasenasturias.services.ancert.enviodiligencias;

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

import es.tributasenasturias.services.ancert.enviodiligencias.log.LogFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogSoapClient;



public class SoapClientHandler implements SOAPHandler<SOAPMessageContext> {

	//Id de sesión en que se crea este objeto.
	private String idSesion;
	/**
	 * Constructor del objeto. El objeto se crea relacionado con un contexto  ({@link CallContext})de llamada.
	 * @param contx Contexto de llamada
	 */
	public SoapClientHandler(String idsesion)
	{
		idSesion=idsesion;
	}
	 public static final SOAPFaultException createSOAPFaultException(String pFaultString) throws SOAPException
	 {
		 try
		 {
		 SOAPFaultException sex=null;
		 SOAPFault fault = SOAPFactory.newInstance().createFault();
		 fault.setFaultString(pFaultString);
		 fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "EnvíoDiligencias"));
		 sex= new SOAPFaultException(fault);
		 return sex;
		 }
		 catch (Exception ex)
		 {
			 throw new SOAPException ("EnvioDiligencias:==> Error grave al construir la excepción SOAP." + ex.getMessage());
		 }
	} 
	private void log(SOAPMessageContext context) throws SOAPException
	{
		LogSoapClient  log=null;
		try
		{
			log=LogFactory.newLogSoapClient(idSesion);
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			String direccion=(salida)?"Envío":"Recepción";
			SOAPMessage msg = context.getMessage();
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	        msg.writeTo(byteArray);
	        String soapMessage = new String(byteArray.toByteArray());
	        if (log!=null)
	        {
	        	log.info(direccion + "::"+ soapMessage);
	        }
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
			log.error("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			log.trace(ex.getStackTrace());
			}
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de diligencias a otro servicio.");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
			log.error("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			log.trace(ex.getStackTrace());
			}
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de diligencias a otro servicio.");
			
		}
	}
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
		try {
			log(context);
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return true;
	}
	
}
