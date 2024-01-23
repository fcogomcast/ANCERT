package es.tributasenasturias.services.ancert.solicitudEscritura.handler;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;

public class SoapClientHandler implements SOAPHandler<SOAPMessageContext> {

	//Id de la sesión en que se utiliza el manejador. Identificará a la llamada del servicio web.
	private String sesionId;
	Preferencias pref;
	public SoapClientHandler(String idsesion, Preferencias p)
	{
		sesionId=idsesion;
		pref=p;
		
	}
	private void log(SOAPMessageContext context)
	{
		LogHelper log=null;
		try
		{
			log=LogFactory.newSoapClientLog(sesionId, pref);
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			String direccion=(salida)?"Envío":"Recepción";
			SOAPMessage msg = context.getMessage();
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	        msg.writeTo(byteArray);
	        String soapMessage = new String(byteArray.toByteArray());
	        log.doLogSoapClient(direccion+":"+soapMessage);
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
				log.doLogSoapClient("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			}
			ex.printStackTrace();
			throw Utils.createSOAPFaultException("Error mientras se procesaba la petición del servicio de solicitud de escrituras al servidor remoto.");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.doLogSoapClient("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			}
			ex.printStackTrace();
			throw Utils.createSOAPFaultException("Error mientras se procesaba la petición del servicio de solicitud de escrituras al servidor remoto.");
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
		log(context);
		return true;
	}
	
}
