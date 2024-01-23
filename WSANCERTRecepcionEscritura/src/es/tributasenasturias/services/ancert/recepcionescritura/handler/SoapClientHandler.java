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
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;


public class SoapClientHandler implements SOAPHandler<SOAPMessageContext> {

	//Id de sesión en que se crea este objeto.
	private String idSesion;
	private Preferencias pref;
	/**
	 * Constructor del objeto. 
	 * @param contx Contexto de llamada
	 */
	public SoapClientHandler(String idsesion)
	{
		this.idSesion=idsesion;
	}
	
	public SoapClientHandler(String idsesion, Preferencias pref)
	{
		this.idSesion=idsesion;
		this.pref= pref;
	}
	 public static final SOAPFaultException createSOAPFaultException(String pFaultString) throws SOAPException
	 {
		 try
		 {
		 SOAPFaultException sex=null;
		 SOAPFault fault = SOAPFactory.newInstance().createFault();
		 fault.setFaultString(pFaultString);
		 fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "EnvíoEscrituras"));
		 sex= new SOAPFaultException(fault);
		 return sex;
		 }
		 catch (Exception ex)
		 {
			 throw new SOAPException ("Recepción Escrituras:==> Error grave al construir la excepción SOAP." + ex.getMessage());
		 }
	} 
	private void log(SOAPMessageContext context) throws SOAPException
	{
		LogHelper log=null;
		try
		{
			//CRUBENCVS 05/05/2022. Sólo se hace log en modos "DEBUG" y "ALL"
			try {
				if (pref==null){
					pref= PreferenciasFactory.newInstance();
				}
				String modoLog=pref.getModoLog();
				if (!"DEBUG".equalsIgnoreCase(modoLog) &&
				    !"ALL".equalsIgnoreCase(modoLog)){
					return;
				}
			} catch (PreferenciasException pre){
				throw new SOAPException("Error técnico al cargar las preferencias en "+SoapClientHandler.class.getName()+":"+pre.getMessage(), pre);
			}
			//FIN CRUBENCVS 05/05/2022
			log=LogFactory.newSoapClientLog(idSesion);
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			String direccion=(salida)?"Envío":"Recepción";
			SOAPMessage msg = context.getMessage();
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	        msg.writeTo(byteArray);
	        String soapMessage = new String(byteArray.toByteArray());
	        if (log!=null)
	        {
	        	log.doLogSoapClient(direccion + "::"+ soapMessage);
	        }
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
			log.doLogSoapClient("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			log.trace(ex.getStackTrace());
			}
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de escrituras a otro servicio.");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
			log.doLogSoapClient("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			log.trace(ex.getStackTrace());
			}
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de escrituras a otro servicio.");
			
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
