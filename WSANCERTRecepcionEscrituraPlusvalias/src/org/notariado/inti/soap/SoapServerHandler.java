package org.notariado.inti.soap;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.notariado.inti.contexto.CallContextConstants;
import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.preferencias.PreferenciasException;
import org.notariado.inti.preferencias.PreferenciasFactory;
import org.notariado.inti.utils.Utils;
import org.notariado.inti.utils.log.LogFactory;
import org.notariado.inti.utils.log.Logger;




/**
 * Clase que intercepta los mensajes de entrada y los escribe en los log.
 * @author crubencvs
 *
 */
public class SoapServerHandler implements SOAPHandler<SOAPMessageContext> {

	private boolean log(SOAPMessageContext context) 
	{
		Logger log=null;
		try
		{
			Preferencias pref= (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
			if (pref==null)
			{
				pref = PreferenciasFactory.newInstance();
			}
			String idSesion="Sin número de sesión recuperada.";
			idSesion=(String)context.get(CallContextConstants.IDSESION);
			if (idSesion==null)
			{
				idSesion="Sin número de sesión recuperada.";
			}
			log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogServer(), idSesion);
			if ("S".equals(pref.getDebugSOAP()))
			{
				
				Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				String direccion=(salida)?"Envío":"Recepción";
				SOAPMessage msg = context.getMessage();
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		        msg.writeTo(byteArray);
		        String soapMessage = new String(byteArray.toByteArray());
		        log.info(direccion+"::"+soapMessage);
		        //Ponemos en el contexto, pero sólo de los manejadores, el log, para que lo utilice
		        //la validación de firma y certificado para escribir sus mensajes.
		        context.put(CallContextConstants.LOG, log);
		        context.setScope(CallContextConstants.LOG, MessageContext.Scope.HANDLER);
			}
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			log.info("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			Utils.generateSOAPErrMessage(context.getMessage(), "Error grave en proceso de mensaje SOAP."	, "0001", "SOAP Handler", true);
			return false;
		}
		catch (java.io.IOException ex)
		{
			log.info("Error en la grabación de log de SOAP servidor por error de Entrada/Salida:" + ex.getMessage());
			Utils.generateSOAPErrMessage(context.getMessage(),"Error grave en proceso de mensaje SOAP.","0001","SOAP Handler I/O Exception", true);
			return false;
		}
		catch (PreferenciasException ex)
		{
			//En este punto es seguro que no tenemos log. Grabamos en la consola de servidor.
			System.err.println ("Recepción de Escrituras de Plusvalías: Error de preferencias en manejador SOAP ("+SoapServerHandler.class.getName()+":"+ex.getMessage());
			Utils.generateSOAPErrMessage(context.getMessage(),"Error grave en proceso de mensaje SOAP.","0001","SOAP Handler I/O Exception", true);
			return false;
		}
		catch (Exception e)
		{
			Utils.generateSOAPErrMessage(context.getMessage(),"Error grave en proceso de mensaje SOAP.","0001","SOAP Handler I/O Exception", true);
			return false;
		}
		return true;
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
		return log(context);
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		return log(context);
	}

	

}
