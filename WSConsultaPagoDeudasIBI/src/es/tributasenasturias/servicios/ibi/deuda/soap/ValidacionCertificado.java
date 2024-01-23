package es.tributasenasturias.servicios.ibi.deuda.soap;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import org.w3c.dom.Node;

import es.tributasenasturias.seguridad.servicio.InfoCertificado;
import es.tributasenasturias.seguridad.servicio.PropertyConfigurator;
import es.tributasenasturias.seguridad.servicio.SeguridadException;
import es.tributasenasturias.seguridad.servicio.SeguridadFactory;
import es.tributasenasturias.seguridad.servicio.VerificadorCertificado;
import es.tributasenasturias.seguridad.servicio.VerificadorPermisoServicio;
import es.tributasenasturias.seguridad.servicio.InfoCertificado.Validez;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.PreferenciasException;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.PreferenciasFactory;
import es.tributasenasturias.servicios.ibi.deuda.util.Utils;
import es.tributasenasturias.servicios.ibi.deuda.xml.XMLDOMDocumentException;
import es.tributasenasturias.servicios.ibi.deuda.xml.XMLDOMUtils;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;

public class ValidacionCertificado implements SOAPHandler<SOAPMessageContext>{

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
	/**
	 * Valida el certificado incluido en el mensaje de entrada, si así se indica en parámetros.
	 * @param context
	 */
	private void validarCertificado(SOAPMessageContext context)
	{
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		//Logger log=null;
		Logger logAplicacion=null;
		if (!salida.booleanValue())
		{
			Preferencias pref = (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
			try
			{
				//Recuperamos si es necesario validar el certificado.
				if (pref==null)
				{
					pref=PreferenciasFactory.newInstance();
				}
				String idSesion = (String) context.get(CallContextConstants.IDSESION);
				if (idSesion==null)
				{
					idSesion = "Sin sesión";
				}
				//Generamos el log de aplicación, escribirá a un fichero diferente al anterior, que es el de mensajes SOAP.
				if (logAplicacion==null)
				{
					logAplicacion= LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogAplicacion(), idSesion);
				}
				if ("S".equalsIgnoreCase(pref.getValidarCertificado()))
				{
					String certificado="";
					certificado = extraerCertificado(context.getMessage().getSOAPPart());
					if ("".equals(certificado))
					{
						logAplicacion.error ("Certificado vacío o no se encuentra en el nodo adecuado.");
						Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", false);
					}
					//Lanzamos la validación.
					VerificadorCertificado verCer = SeguridadFactory.newVerificadorCertificado(pref.getEndpointAutenticacion(), new SoapClientHandler(idSesion));
					InfoCertificado infoCertificado=verCer.login(certificado);
					//
					if (Validez.INVALIDO.equals(infoCertificado.getValidez()))
					{
						logAplicacion.error ("Certificado no válido o de tipo no aceptado.");
						Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", false);
					}
					if ("S".equals(pref.getValidarPermisos()))
						{
							String cif = infoCertificado.getCif()!=null?infoCertificado.getCif():infoCertificado.getNifNie();
							logAplicacion.debug ("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].Extraemos CIF/NIF del certificado:"+certificado);
							logAplicacion.debug ("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].Verificamos permisos del servicio para cif/nif:"+cif);
							VerificadorPermisoServicio per = SeguridadFactory
							.newVerificadorPermisoServicio(new PropertyConfigurator(
										pref.getEndpointAutenticacion(), pref.getEndpointLanzador(), 
										pref.getProcAlmacenadoPermisosServicio(), pref.getEsquemaBD(),
										new SoapClientHandler(idSesion)));
							if (!per.tienePermisosCIF(cif, pref.getAliasServicio()))
							{
								
								logAplicacion.error ("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].El id:" + cif + " no tiene permisos para ejecutar el servicio " + pref.getAliasServicio());
								Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", false);
							}
							logAplicacion.debug ("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].El id:" + cif + " tiene permisos para ejecutar el servicio " + pref.getAliasServicio());
						}
					//Es válido.
				}
			}catch (PreferenciasException e)
			{
				//Redundante. Si preferencias no existe, no puede haberse dado de alta el log. Lo dejo por si
				//un día se pone un valor por defecto para las preferencias de log.
				if (logAplicacion!=null) {logAplicacion.error("Manejador SOAP " + ValidacionCertificado.class.getName()+".Error en preferencias:"+e.getMessage());}
				else {System.err.println("Servicio WSConsultaPagoDeudasIBI: Error en preferencias : "+ e.getMessage());};
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", true);
			} catch (SeguridadException e)
			{
				if (logAplicacion!=null) {logAplicacion.error("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].Error:"+e.getMessage());};
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", true);
			}
			 catch (XMLDOMDocumentException e)
			 {
				 if (logAplicacion!=null) {logAplicacion.error("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].Error:"+e.getMessage());};
					Utils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", true);
			 }
		}
	}
	@Override
	public Set<QName> getHeaders() {
		return Collections.emptySet();
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		validarCertificado(context);
		return true;
	}

}
