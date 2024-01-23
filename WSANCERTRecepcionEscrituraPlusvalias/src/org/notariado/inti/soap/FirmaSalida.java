package org.notariado.inti.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.notariado.inti.contexto.CallContextConstants;
import org.notariado.inti.exceptions.MensajesException;
import org.notariado.inti.mensajes.ConstructorMensajeRespuestaConsulta;
import org.notariado.inti.mensajes.MensajesFactory;
import org.notariado.inti.mensajes.MensajesUtil;
import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.preferencias.PreferenciasException;
import org.notariado.inti.preferencias.PreferenciasFactory;
import org.notariado.inti.utils.Utils;
import org.notariado.inti.utils.log.LogFactory;
import org.notariado.inti.utils.log.Logger;
import org.notariado.inti.wssecurity.WSSecurityException;
import org.notariado.inti.wssecurity.WSSecurityFactory;
import org.notariado.inti.xml.XMLDOMDocumentException;
import org.notariado.inti.xml.XMLDOMUtils;





public class FirmaSalida implements SOAPHandler<SOAPMessageContext>{
	
	/**
	 * Destruye la cabecera, como preparación para la firma de mensaje. Si ha llegado a la implementación
	 * de servicio ya estará hecho, si ha fallado la seguridad de mensaje, no.
	 * @param context Contexto del mensaje
	 * @throws SOAPException
	 */
	private void destruirCabecera(SOAPMessageContext context) throws SOAPException
	{
		if (context.getMessage().getSOAPHeader()!=null)
		{
			context.getMessage().getSOAPHeader().removeContents();
		}
	}
	
	
	/**
	 * Gestiona la firma de mensaje de entrada o la generación de firma en salida al servicio, según WS-Security.
	 * @param context Contexto del mensaje SOAP.
	 */
	private boolean firmaMensaje(SOAPMessageContext context)
	{
		MensajesUtil mensajes = (MensajesUtil) context.get(CallContextConstants.MENSAJES);
		ConstructorMensajeRespuestaConsulta con = (ConstructorMensajeRespuestaConsulta) context.get(CallContextConstants.CONSTRUCTOR_RESPUESTA);
		Logger logAplicacion=null;
		ByteArrayInputStream bi=null;
		ByteArrayOutputStream bo=null;
		Preferencias pref=null;
		try
		{
			pref = (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
			String idSesion = (String) context.get(CallContextConstants.IDSESION);
			if (idSesion==null)
			{
				idSesion = "Sin sesión";
			}
			if (pref==null)
			{
				pref=PreferenciasFactory.newInstance();
			}
			if (logAplicacion==null)
			{
				logAplicacion= LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogAplicacion(),idSesion);
			}
			if (mensajes==null)
			{
				mensajes = MensajesFactory.newMensajesAplicacion(pref.getFicheroMensajesAplicacion());
			}
			//Comprobamos si firmamos o validamos.
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (salida.booleanValue())
			{
				destruirCabecera(context);
				//Firmamos.
				if ("S".equalsIgnoreCase(pref.getFirmarSalida()))
				{
					logAplicacion.debug("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Se firma.");
					//Recuperamos el texto de mensaje.
					bo=new ByteArrayOutputStream();
					context.getMessage().writeTo(bo);
					//Recuperamos qué mensaje firmamos, el de consulta o pago.
					String xmlFirmado = XMLDOMUtils.getPrettyXMLText(context.getMessage().getSOAPPart());
					xmlFirmado=WSSecurityFactory.newConstructorResultado(pref,new SoapClientHandler(idSesion)).firmaMensaje(xmlFirmado);
					logAplicacion.debug("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Mensaje firmado:"+xmlFirmado);
					bi = new ByteArrayInputStream(xmlFirmado.getBytes());
					context.getMessage().getSOAPPart().setContent(new StreamSource(bi));
				}
			}
		}catch (PreferenciasException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Error en preferencias:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error al firmar mensaje.", "0002", "SOAP Handler",true);
		}
		catch (MensajesException e)
		{
			logAplicacion.error ("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generateSOAPErrMessage(context.getMessage(), "Error al firmar mensaje.", "0002", "SOAP Handler", true);
		} catch (SOAPException e) {
			logAplicacion.error ("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generateSOAPErrMessage(context.getMessage(), "Error al firmar mensaje.", "0002", "SOAP Handler", true);
		} catch (IOException e) {
			logAplicacion.error ("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generateSOAPErrMessage(context.getMessage(), "Error al firmar mensaje.", "0002", "SOAP Handler", true);
		} 
		catch (WSSecurityException e)
		{
			logAplicacion.error ("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generarErrorBody(con,true, context.getMessage(), "Error al firmar mensaje.", pref, mensajes);
			return false;
		} catch (XMLDOMDocumentException e)
		{
			logAplicacion.error ("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generarErrorBody(con,true, context.getMessage(), "Error al firmar mensaje.", pref, mensajes);
			return false;
		} catch (Exception e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + FirmaSalida.class.getName()+"].Error:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error al firmar mensaje.", "0002", "SOAP Handler", true);
		}
		finally
		{
			if (bi!=null)
			{
				try {bi.close();} catch(Exception e){}
			}
			if (bo!=null)
			{
				try {bo.close();} catch (Exception e){}
			}
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
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		return firmaMensaje(context);
	}
	

}
