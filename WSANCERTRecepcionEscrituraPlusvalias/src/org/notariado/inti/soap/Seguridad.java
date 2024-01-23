package org.notariado.inti.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
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
import org.w3c.dom.Node;

import es.tributasenasturias.seguridad.servicio.InfoCertificado;
import es.tributasenasturias.seguridad.servicio.PropertyConfigurator;
import es.tributasenasturias.seguridad.servicio.SeguridadException;
import es.tributasenasturias.seguridad.servicio.SeguridadFactory;
import es.tributasenasturias.seguridad.servicio.VerificadorCertificado;
import es.tributasenasturias.seguridad.servicio.VerificadorPermisoServicio;
import es.tributasenasturias.seguridad.servicio.InfoCertificado.Validez;




public class Seguridad implements SOAPHandler<SOAPMessageContext>{
	
	
	/**
	 * Extrae el contenido del certificado de la firma de WSSecurity
	 * @param mensaje Mensaje firmado.
	 * @return Cadena que contiene el certificado de la firma.
	 * @throws XMLDOMDocumentException
	 */
	private String extraerCertificado(Node mensaje)
			throws XMLDOMDocumentException {
		Node certificado = XMLDOMUtils
				.selectSingleNode(
						mensaje,
						"/*[local-name()='Envelope']/*[local-name()='Header']/*[local-name()='Security']/*[local-name()='BinarySecurityToken']/text()");
		if (certificado==null)
		{
			return "";
		}
		return certificado.getNodeValue();

	}
	
	public boolean validarCertificado(SOAPMessageContext context,Preferencias pref, Logger logAplicacion, MensajesUtil mensajes,ConstructorMensajeRespuestaConsulta con,String idSesion) throws SeguridadException, XMLDOMDocumentException
	{
		if ("S".equalsIgnoreCase(pref.getValidarCertificado()))
		{
			String certificado="";
			certificado = extraerCertificado(context.getMessage().getSOAPPart());
			if ("".equals(certificado))
			{
				logAplicacion.error ("Certificado vacío o no se encuentra en el nodo adecuado.");
				Utils.generarErrorBody(con,true, context.getMessage(), "No se ha superado la validación de mensaje.", pref, mensajes);
				return false;
			}
			//Lanzamos la validación.
			VerificadorCertificado verCer = SeguridadFactory.newVerificadorCertificado(pref.getEndpointAutenticacion(), new SoapClientHandler(idSesion));
			InfoCertificado infoCertificado=verCer.login(certificado);
			//
			if (Validez.INVALIDO.equals(infoCertificado.getValidez()))
			{
				logAplicacion.error ("Certificado no válido o de tipo no aceptado.");
				Utils.generarErrorBody(con,true, context.getMessage(), "No se ha superado la validación de mensaje.", pref, mensajes);
				return false;
			}
			if ("S".equals(pref.getValidarPermisos()))
				{
					String cif = infoCertificado.getCif()!=null?infoCertificado.getCif():infoCertificado.getNifNie();
					logAplicacion.debug ("Manejador SOAP [" + Seguridad.class.getName()+"].Extraemos CIF/NIF del certificado:"+certificado);
					logAplicacion.debug ("Manejador SOAP [" + Seguridad.class.getName()+"].Verificamos permisos del servicio para cif/nif:"+cif);
					VerificadorPermisoServicio per = SeguridadFactory
					.newVerificadorPermisoServicio(new PropertyConfigurator(
								pref.getEndpointAutenticacion(), pref.getEndpointLanzador(), 
								pref.getProcAlmacenadoPermisosServicio(), pref.getEsquemaBD(),
								new SoapClientHandler(idSesion)));
					if (!per.tienePermisosCIF(cif, pref.getAliasServicio()))
					{
						
						logAplicacion.error ("Manejador SOAP [" + Seguridad.class.getName()+"].El id:" + cif + " no tiene permisos para ejecutar el servicio " + pref.getAliasServicio());
						Utils.generarErrorBody(con,true, context.getMessage(), "No se ha superado la validación de mensaje.", pref, mensajes);
						return false;
					}
					logAplicacion.debug ("Manejador SOAP [" + Seguridad.class.getName()+"].El id:" + cif + " tiene permisos para ejecutar el servicio " + pref.getAliasServicio());
				}
			//Es válido.
		}
		return true;
	}
	
	/**
	 * Gestiona la firma de mensaje de entrada o la generación de firma en salida al servicio, según WS-Security.
	 * @param context Contexto del mensaje SOAP.
	 */
	private boolean verificarSeguridad(SOAPMessageContext context)
	{
		MensajesUtil mensajes = (MensajesUtil) context.get(CallContextConstants.MENSAJES);
		ConstructorMensajeRespuestaConsulta cons = (ConstructorMensajeRespuestaConsulta) context.get(CallContextConstants.CONSTRUCTOR_RESPUESTA);
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
			//Comprobamos la dirección.
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue())
			{
				//Validamos.
				if ("S".equalsIgnoreCase(pref.getValidarFirma()))
				{
					logAplicacion.debug("Manejador SOAP:[" + Seguridad.class.getName()+"].Validamos la firma.");
					bo=new ByteArrayOutputStream();
					context.getMessage().writeTo(bo);
					logAplicacion.debug("Manejador SOAP:[" + Seguridad.class.getName()+"].Mensaje para validar firma:" + new String(bo.toByteArray()));
					boolean firmaCorrecta = WSSecurityFactory.newConstructorResultado(pref,new SoapClientHandler(idSesion)).validaFirmaSinCertificado(new String(bo.toByteArray()));
					if (firmaCorrecta)
					{
						logAplicacion.info("Manejador SOAP:[" + Seguridad.class.getName()+"].Firma validada");
						//Validamos el certificado
						return validarCertificado(context, pref, logAplicacion, mensajes, cons,idSesion);
					}
					else
					{
						logAplicacion.error("Manejador SOAP:[" + Seguridad.class.getName()+"].Firma de mensaje no válida.");
						Utils.generarErrorBody(cons,true, context.getMessage(), "No se ha superado la validación de mensaje.", pref, mensajes);
						return false;
					}
				}
			}
		}catch (PreferenciasException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + Seguridad.class.getName()+"].Error en preferencias:"+e.getMessage(),e);}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler",true);
		}
		catch (MensajesException e)
		{
			logAplicacion.error ("Manejador SOAP:[" + Seguridad.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", true);
		} catch (SOAPException e) {
			logAplicacion.error ("Manejador SOAP:[" + Seguridad.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", true);
		} catch (IOException e) {
			logAplicacion.error ("Manejador SOAP:[" + Seguridad.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", true);
		} 
		catch (WSSecurityException e)
		{
			logAplicacion.error ("Manejador SOAP:[" + Seguridad.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generarErrorBody(cons,true, context.getMessage(), "No se ha superado la validación de mensaje.", pref, mensajes);
			return false;
		} catch (XMLDOMDocumentException e)
		{
		logAplicacion.error ("Manejador SOAP:[" + Seguridad.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generarErrorBody(cons,true, context.getMessage(), "No se ha superado la validación de mensaje.", pref, mensajes);
			return false;
		} catch (SeguridadException e) {
			logAplicacion.error ("Manejador SOAP:[" + Seguridad.class.getName()+"].Error:"+e.getMessage(),e);
			Utils.generarErrorBody(cons,true, context.getMessage(), "No se ha superado la validación de mensaje.", pref, mensajes);
			return false;
		} 
		 catch (Exception e)
		 {
			 if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + Seguridad.class.getName()+"].Error:"+e.getMessage(),e);}
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", true);
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
		return verificarSeguridad(context);
	}
	

}
