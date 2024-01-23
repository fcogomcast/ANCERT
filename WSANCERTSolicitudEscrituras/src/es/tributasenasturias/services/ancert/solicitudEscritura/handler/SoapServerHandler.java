package es.tributasenasturias.services.ancert.solicitudEscritura.handler;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.PreferenciasException;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.PreferenciasFactory;

/**
 * Clase que intercepta los mensajes de entrada y los escribe en los log.
 * @author crubencvs
 *
 */
public class SoapServerHandler implements SOAPHandler<SOAPMessageContext> {

	private void log(SOAPMessageContext context) 
	{
		LogHelper log=null;
		try
		{
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue())
			{
				creaContexto(context);
			}
			
			String idLog=(String)context.get(CallContextConstants.ID_SESION);
			Preferencias pref = (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
			log=LogFactory.newSoapServerLog(idLog, pref);
			String direccion=(salida)?"Envío":"Recepción";
			SOAPMessage msg = context.getMessage();
			context.put("IdLog", idLog);
			context.setScope("IdLog", MessageContext.Scope.APPLICATION);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	        msg.writeTo(byteArray);
	        String soapMessage = new String(byteArray.toByteArray());
	        log.doLogSoapServer(direccion+"::"+soapMessage);
	        if (salida.booleanValue())
			{
				destruirContextoSesion(context);
			}
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
				log.doLogSoapServer("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			}
			ex.printStackTrace();
			throw Utils.createSOAPFaultException("Error mientras se procesaba la petición.");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.doLogSoapServer("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			}
			ex.printStackTrace();
			throw Utils.createSOAPFaultException("Error mientras se procesaba la petición.");
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

	/**
	 * Crea el objeto de contexto de llamada y le asigna los valores de Preferencias, log e id de llamada
	 * que se utilizarán a lo largo de la llamada al servicio web.
	 * @param context Contexto SOAP
	 */
	private void creaContexto(SOAPMessageContext context)
	{
		LogHelper log=null;
		try
		{
			String idSesion=Utils.getIdLlamada();
			context.put(CallContextConstants.ID_SESION, idSesion);
			context.setScope(CallContextConstants.ID_SESION, MessageContext.Scope.APPLICATION);
			Preferencias pref = PreferenciasFactory.newInstance();
			context.put (CallContextConstants.PREFERENCIAS,pref);
			context.setScope(CallContextConstants.PREFERENCIAS, MessageContext.Scope.APPLICATION);
			log=LogFactory.newApplicationLog(idSesion, pref);
			context.put(CallContextConstants.LOG_APLICACION, log);
			context.setScope(CallContextConstants.LOG_APLICACION, MessageContext.Scope.APPLICATION);

		}
		catch (PreferenciasException ex)
		{
			//Si no hay preferencias, no debemos tener log.
			System.err.println ("Solicitud de Escrituras:: error en preferencias ::"+ex.getMessage());
			ex.printStackTrace();
			throw Utils.createSOAPFaultException("Error en las preferencias del servicio al hacer log:"+ ex.getMessage());
		}
	}
	
	/**
	 * Destruye las variables almacenadas en el contexto. Se hace así
	 * para asegurar que se libera toda la memoria.
	 * Estas variables se almacenan en el contexto del hilo de ejecución, con lo cual
	 * estarían vivas un tiempo indeterminado. De esta forma, las marcamos 
	 * para que el recolector de basura las elimine.
	 * Aunque no se hiciera no sería muy grave, porque nunca habría más de una copia
	 * por hilo, y la siguiente petición que entrara al hilo machacaría estos 
	 * datos.
	 * @param context
	 */
	private void destruirContextoSesion(SOAPMessageContext context)
	{
		//A la salida, nos cargamos los datos de contexto.
		context.remove(CallContextConstants.ID_SESION);
		context.remove(CallContextConstants.PREFERENCIAS);
		context.remove(CallContextConstants.LOG_APLICACION);
	}


	

}
