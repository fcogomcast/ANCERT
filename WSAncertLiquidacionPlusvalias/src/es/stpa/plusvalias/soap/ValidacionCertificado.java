package es.stpa.plusvalias.soap;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import org.w3c.dom.Node;

import es.stpa.plusvalias.Constantes;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.preferencias.PreferenciasException;
import es.tributasenasturias.log.TributasLogger;
import es.tributasenasturias.seguridad.servicio.InfoCertificado;
import es.tributasenasturias.seguridad.servicio.PropertyConfigurator;
import es.tributasenasturias.seguridad.servicio.SeguridadException;
import es.tributasenasturias.seguridad.servicio.SeguridadFactory;
import es.tributasenasturias.seguridad.servicio.VerificadorCertificado;
import es.tributasenasturias.seguridad.servicio.VerificadorPermisoServicio;
import es.tributasenasturias.seguridad.servicio.InfoCertificado.Validez;
import es.tributasenasturias.xml.XMLDOMDocumentException;
import es.tributasenasturias.xml.XMLDOMUtils;


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
	 * Valida el certificado incluido en el mensaje de entrada, si as� se indica en par�metros.
	 * @param context
	 */
	private void validarCertificado(SOAPMessageContext context)
	{
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		TributasLogger logAplicacion=null;
		if (!salida.booleanValue())
		{
			Preferencias pref = (Preferencias) context.get(Constantes.PREFERENCIAS);
			try
			{
				//Recuperamos si es necesario validar el certificado.
				if (pref==null)
				{
					pref=new Preferencias();
				}
				String idSesion = (String) context.get(Constantes.IDSESION);
				if (idSesion==null)
				{
					idSesion = "Sin sesi�n";
				}
				//Generamos el log de aplicaci�n, escribir� a un fichero diferente al anterior, que es el de mensajes SOAP.
				logAplicacion= new TributasLogger(pref.getModoLog(),pref.getDirectorioRaizLog(),pref.getFicheroLogAplicacion(), idSesion);

				if ("S".equalsIgnoreCase(pref.getValidaCertificado()))
				{
					String certificado="";
					certificado = extraerCertificado(context.getMessage().getSOAPPart());
					if ("".equals(certificado))
					{
						logAplicacion.error ("Certificado vac�o o no se encuentra en el nodo adecuado.");
						SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", false);
					}
					//Lanzamos la validaci�n.
					VerificadorCertificado verCer = SeguridadFactory.newVerificadorCertificado(pref.getEndpointAutenticacion(), new SoapClientHandler(idSesion));
					InfoCertificado infoCertificado=verCer.login(certificado);
					//
					if (Validez.INVALIDO.equals(infoCertificado.getValidez()))
					{
						logAplicacion.error ("Certificado no v�lido o de tipo no aceptado.");
						SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", false);
					}
					if ("S".equals(pref.getValidaPermisos()))
						{
							String cif = infoCertificado.getCif()!=null?infoCertificado.getCif():infoCertificado.getNifNie();
							logAplicacion.debug ("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].Extraemos CIF/NIF del certificado:"+certificado);
							logAplicacion.debug ("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].Verificamos permisos del servicio para cif/nif:"+cif);
							VerificadorPermisoServicio per = SeguridadFactory
							.newVerificadorPermisoServicio(new PropertyConfigurator(
										pref.getEndpointAutenticacion(), pref.getEndpointLanzador(), 
										pref.getProcPermisoServicio(), pref.getEsquemaBD(),
										new SoapClientHandler(idSesion)));
							if (!per.tienePermisosCIF(cif, pref.getAliasServicio()))
							{
								
								logAplicacion.error ("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].El id:" + cif + " no tiene permisos para ejecutar el servicio " + pref.getAliasServicio());
								SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", false);
							}
							logAplicacion.debug ("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].El id:" + cif + " tiene permisos para ejecutar el servicio " + pref.getAliasServicio());
						}
					//Es v�lido.
				}
			}catch (PreferenciasException e)
			{
				//Si preferencias no existe, no  hay log
				//if (logAplicacion!=null) {logAplicacion.error("Manejador SOAP " + ValidacionCertificado.class.getName()+".Error en preferencias:"+e.getMessage());}
				//else {System.err.println("Servicio WSAncertPlusvalias: Error en preferencias : "+ e.getMessage());};
				System.err.println("Servicio WSAncertPlusvalias: Error en preferencias : "+ e.getMessage());
				SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", true);
			} catch (SeguridadException e)
			{
				if (logAplicacion!=null) {logAplicacion.error("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].Error:"+e.getMessage());};
				SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", true);
			}
			 catch (XMLDOMDocumentException e)
			 {
				 if (logAplicacion!=null) {logAplicacion.error("Manejador SOAP [" + ValidacionCertificado.class.getName()+"].Error:"+e.getMessage());};
				 SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler", true);
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
