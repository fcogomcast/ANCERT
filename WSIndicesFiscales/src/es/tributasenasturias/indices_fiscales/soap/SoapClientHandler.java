package es.tributasenasturias.indices_fiscales.soap;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.indices_fiscales.utils.Utils;
import es.tributasenasturias.indices_fiscales.utils.contextoLlamada.CallContextConstants;
import es.tributasenasturias.indices_fiscales.utils.log.LogFactory;
import es.tributasenasturias.indices_fiscales.utils.log.Logger;
import es.tributasenasturias.indices_fiscales.utils.preferencias.Preferencias;
import es.tributasenasturias.indices_fiscales.utils.preferencias.PreferenciasException;
import es.tributasenasturias.indices_fiscales.utils.preferencias.PreferenciasFactory;



public class SoapClientHandler implements SOAPHandler<SOAPMessageContext> {

	//Id de la sesi�n en que se utiliza el manejador. Identificar� a la llamada del servicio web.
	private String sesionId;
	public SoapClientHandler(String idsesion)
	{
		sesionId=idsesion;
	}
	private void log(SOAPMessageContext context)
	{
		Logger log=null;
		try
		{
			Preferencias pref= (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
			if (pref==null)
			{
				pref = PreferenciasFactory.newInstance();
			}
				log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogClient(), sesionId);
				Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				String direccion=(salida)?"Env�o":"Recepci�n";
				SOAPMessage msg = context.getMessage();
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		        msg.writeTo(byteArray);
		        String soapMessage = new String(byteArray.toByteArray());
		        log.info(direccion+":"+soapMessage);
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
				log.info("Error en la grabaci�n de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en proceso de comunicaci�n Consulta Deuda <--> Servicio Remoto."	, "0002", "SOAP Handler", true);
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.info("Error en la grabaci�n de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en proceso de comunicaci�n Consulta Deuda <--> Servicio Remoto."	, "0002", "SOAP Handler", true);
		}
		catch (PreferenciasException ex)
		{
			//En este punto es seguro que no tenemos log. Grabamos en la consola de servidor.
			System.err.println ("IndicesFiscales: Error de preferencias en manejador SOAP ("+SoapServerHandler.class.getName()+":"+ex.getMessage());
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en proceso de comunicaci�n Consulta Deuda <--> Servicio Remoto."	, "0002", "SOAP Handler", true);
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
		log(context);
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		log(context);
		return true;
	}
	
}
