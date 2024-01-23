package es.tributasenasturias.servicios.ibi.deuda.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;


import es.tributasenasturias.seguridad.servicio.FirmaHelper;
import es.tributasenasturias.seguridad.servicio.SeguridadException;
import es.tributasenasturias.seguridad.servicio.SeguridadFactory;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.MensajesException;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesFactory;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion.MensajeAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.PreferenciasException;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.PreferenciasFactory;
import es.tributasenasturias.servicios.ibi.deuda.util.Constantes;
import es.tributasenasturias.servicios.ibi.deuda.util.Utils;
import es.tributasenasturias.servicios.ibi.deuda.wssecurity.WSSecurityException;
import es.tributasenasturias.servicios.ibi.deuda.wssecurity.WSSecurityFactory;
import es.tributasenasturias.servicios.ibi.deuda.xml.XMLDOMDocumentException;
import es.tributasenasturias.servicios.ibi.deuda.xml.XMLDOMUtils;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;

public class GestionFirmaWS implements SOAPHandler<SOAPMessageContext>{
	
	private void eliminarFirmaWSSecurity(SOAPMessageContext ctx) 
		throws XMLDOMDocumentException, SOAPException {
		SOAPMessage msg = ctx.getMessage();
		//Recuperamos la cabecera
		Node header = msg.getSOAPHeader();
		if (header==null)
		{
			//No hay cabecera, no hay WSSecurity. 
			return;
		}
		//Recuperamos el nodo de WSSecurity
		
		Node hijo=null;
		if (header.hasChildNodes())
		{
			for (hijo=header.getFirstChild();hijo!=null;hijo=hijo.getNextSibling())
			{
				if (hijo.getNodeType()==Node.ELEMENT_NODE && "Security".equals(hijo.getLocalName()))
				{
					header.removeChild(hijo);
					break;
				}
			}
		}
	}
	/**
	 * Gestiona la firma de mensaje de entrada al servicio, según WS-Security.
	 * @param context Contexto del mensaje SOAP.
	 */
	private void firmaMensaje(SOAPMessageContext context)
	{
		MensajesAplicacion mensajes = (MensajesAplicacion) context.get(CallContextConstants.MENSAJES);
		Logger logAplicacion=null;
		ByteArrayInputStream bi=null;
		ByteArrayOutputStream bo=null;
		try
		{
			Preferencias pref = (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
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
				mensajes = MensajesFactory.newMensajesAplicacion(pref.getFicheroMensajesAplicacion(), pref.getFicheroMapeoMensajes());
			}
			//Comprobamos si firmamos o validamos.
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (salida.booleanValue())
			{
				//Firmamos.
				if ("S".equalsIgnoreCase(pref.getFirmarSalida()))
				{
					if ("S".equalsIgnoreCase(pref.getDebugSOAP()))
					{
						logAplicacion.debug("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Se firma.");
					}
					//Recuperamos el texto de mensaje.
					bo=new ByteArrayOutputStream();
					context.getMessage().writeTo(bo);
					//Recuperamos qué mensaje firmamos, el de consulta o pago.
					String xmlFirmado = XMLDOMUtils.getPrettyXMLText(context.getMessage().getSOAPPart());
					xmlFirmado=WSSecurityFactory.newConstructorResultado(pref,new SoapClientHandler(idSesion)).firmaMensaje(xmlFirmado);
					if ("S".equalsIgnoreCase(pref.getDebugSOAP()))
					{
						logAplicacion.debug("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Mensaje firmado:"+xmlFirmado);
					}
					//bi = new ByteArrayInputStream(xmlFirmado.getBytes("ISO-8859-1"));
					bi = new ByteArrayInputStream(xmlFirmado.getBytes());
					context.getMessage().getSOAPPart().setContent(new StreamSource(bi));
				}
			}
			else
			{
				//Validamos.
				if ("S".equalsIgnoreCase(pref.getValidarFirma()))
				{
					if ("S".equalsIgnoreCase(pref.getDebugSOAP()))
					{
						logAplicacion.debug("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Validamos la firma WSSecurity.");
					}
					bo=new ByteArrayOutputStream();
					context.getMessage().writeTo(bo);
					if ("S".equalsIgnoreCase(pref.getDebugSOAP()))
					{
						logAplicacion.debug("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Mensaje para validar firma WSSecurity:" + new String(bo.toByteArray()));
					}
					boolean firmaCorrecta = WSSecurityFactory.newConstructorResultado(pref,new SoapClientHandler(idSesion)).validaFirmaSinCertificado(new String(bo.toByteArray()));
					if (firmaCorrecta)
					{
						logAplicacion.info("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Firma WSSecurity validada");
						//Ahora, si se trata de un mensaje de Pago debemos eliminar la cabecera WSSecurity y validar la firma digital del documento
						if (new String(bo.toByteArray()).contains("<PAGO_DEUDAS"))
						{
							eliminarFirmaWSSecurity(context);
							bo = new ByteArrayOutputStream();
							context.getMessage().writeTo(bo);
							//Validamos la firma
							FirmaHelper fh = SeguridadFactory.newFirmaHelper(pref.getEndpointFirma(), new SoapClientHandler(idSesion));
							if (fh.esValido(new String(bo.toByteArray())))
							{
								logAplicacion.info("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Firma Mensaje PAGO_DEUDAS validada");
							}
							else
							{
								MensajeAplicacion mensaje = mensajes.getMensaje(Constantes.idMensajeErrorSeguridad);
								logAplicacion.error("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Firma Mensaje PAGO_DEUDAS no válida.");
								Utils.generateSOAPErrMessage(context.getMessage(), mensaje.getTextoMensaje(), mensaje.getCodMensaje(), "SOAP Handler",false);
							}
						}
					}
					else
					{
						MensajeAplicacion mensaje = mensajes.getMensaje(Constantes.idMensajeErrorSeguridad);
						logAplicacion.error("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Firma de mensaje WSSecurity no válida.");
						Utils.generateSOAPErrMessage(context.getMessage(), mensaje.getTextoMensaje(), mensaje.getCodMensaje(), "SOAP Handler",false);
					}
				}
			}
		}catch (PreferenciasException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error en preferencias:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler",true);
		}
		catch (MensajesException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		} catch (SOAPException e) {
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		} catch (IOException e) {
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		} 
		catch (WSSecurityException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		} catch (XMLDOMDocumentException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		}catch (SeguridadException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
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
	}
	@Override
	public Set<QName> getHeaders() {
		//Indicamos que entendemos la cabecera de seguridad de WS-Security.
		QName security= new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd","Security");
		HashSet<QName> headersEntendidos= new HashSet<QName>();
		headersEntendidos.add(security);
		return headersEntendidos;
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
		firmaMensaje(context);
		return true;
	}
	

}
