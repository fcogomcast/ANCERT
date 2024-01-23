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
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogSoapserver;


/**
 * Clase que intercepta los mensajes de entrada y los escribe en los log.
 * @author crubencvs
 *
 */
public class SoapServerHandler implements SOAPHandler<SOAPMessageContext> {

	public static SOAPFaultException createSOAPFaultException(String pFaultString)  throws SOAPException
	 {
		 SOAPFaultException sex=null;
		 try {
		 SOAPFault fault = SOAPFactory.newInstance().createFault();
		 fault.setFaultString(pFaultString);
		 fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "EnvioDiligencias"));
		 sex= new SOAPFaultException(fault);
		 }
		 catch (Exception ex)
		 {
			 throw new SOAPException("Envío diligencias:===>Error grave al construir la excepción de SOAP."+ex.getMessage());
		 }
		 return sex;
	} 
	
	private void log(SOAPMessageContext context) throws SOAPException
	{
		
		LogSoapserver log=null;
		try
		{
		

			String idSesion="Sesion no inicializada";
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			
			idSesion=(String)context.get("IdSesion");
			if (idSesion==null)
			{
				idSesion="Sesion no inicializada";
			}
			String direccion=(salida)?"Envío":"Recepción";
			log=LogFactory.newLogSoapServer(idSesion);
			SOAPMessage msg = context.getMessage();
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	        msg.writeTo(byteArray);
	        String soapMessage = new String(byteArray.toByteArray());
	        if (log!=null)
	        {
	        	log.info(direccion+"::"+soapMessage);
	        }
	        
	        
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			log.error("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			log.trace(ex.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de diligencias.");
		}
		catch (java.io.IOException ex)
		{
			log.error("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			log.trace(ex.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de diligencias.");
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
