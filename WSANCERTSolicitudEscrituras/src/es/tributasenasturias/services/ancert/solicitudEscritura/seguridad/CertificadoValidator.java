package es.tributasenasturias.services.ancert.solicitudEscritura.seguridad;

import org.w3c.dom.Document;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.PreferenciasException;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.XMLDOMDocumentException;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.DatosFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.GestorCertificadosBD;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.GestorCertificadosBD.PermisosServicio;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.XMLDOMUtils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.PreferenciasFactory;

/**
 * Validador de certificado de cabecera
 * 
 * @author davidsa
 * 
 */
public class CertificadoValidator implements IContextReader{
	
	private CallContext context;
	public String getCertificadoCabecera(String datosXML) {
		String certificado = "";
		// Recuperamos la lista de nodos de la cabecera con certificado. Sólo
		// debería haber uno.
		try {
			javax.xml.parsers.DocumentBuilder db;
			javax.xml.parsers.DocumentBuilderFactory fact = javax.xml.parsers.DocumentBuilderFactory
					.newInstance();

			db = fact.newDocumentBuilder();
			fact.setNamespaceAware(true);
			org.xml.sax.InputSource inStr = new org.xml.sax.InputSource();
			inStr.setCharacterStream(new java.io.StringReader(datosXML));
			org.w3c.dom.Document m_resultadoXML;
			m_resultadoXML = db.parse(inStr);
			//org.w3c.dom.NodeList certificados = m_resultadoXML
			//		.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
			org.w3c.dom.NodeList certificados = m_resultadoXML
					.getElementsByTagName("dsig:X509Certificate");
			if (certificados != null && certificados.getLength() != 0) {
				certificado = certificados.item(0).getTextContent();

			} else {
				certificado = null;
			}

		} catch (Exception ex) {
			certificado = null;
		}

		return certificado;
	}

	protected CertificadoValidator() {
		
	}
	/**
	 * Valida los permisos del certificado que viene en la firma para ejecutar el servicio. 
	 * @param documento Documento recibido.
	 * @return
	 */
	public boolean tienePermisos(Document documento) throws SeguridadException{		
		boolean valido = false;
		// Recuperamos el certificado.
		String datosXML;
		LogHelper log= LogFactory.getLogAplicacionContexto(context);
		try
		{
			//Si las preferencias dicen que no se valide el certificado, se da por bueno.
			Preferencias pref = PreferenciasFactory.newInstance();
			if (pref.getValidaCertificado().equalsIgnoreCase("N"))
			{
				return true;
			}
			datosXML=XMLDOMUtils.getXMLText(documento);
			String certificado = getCertificadoCabecera(datosXML);
			GestorCertificadosBD bd = null;
			GestorCertificadosBD.PermisosServicio autorizacion = null;
			log.debug ("Certificado a validar:"+certificado);
			if (certificado != null && !certificado.equals("")) {
				// Lo enviamos al servicio del Principado, para que nos indique si
				// es válido.
				AutenticacionPAHelper aut =SeguridadFactory.newAutenticacionPAHelper(context);
				try {
					// Llamamos al servicio de autenticación del Principado
					String strCIFNIF = aut.login(certificado);				
					if (strCIFNIF!=null)
					{
						log.debug ("HA SUPERADO LA AUTENTICACION DE CERTIFICADO DEL PRINCIPADO.");
						// Validamos el Id que nos han pasado.
						bd = DatosFactory.newGestorCertificadosBD(context);
						autorizacion = bd.permisoServicio(strCIFNIF);
						if (PermisosServicio.NO_AUTORIZADO.equals( autorizacion)) {
								//"No está autorizado para utilizar el servicio"
							valido=false;
						} else if (PermisosServicio.ERROR.equals(autorizacion)) {
							valido=false;
						} else {
							valido = true;
						}
					}
					if (strCIFNIF==null) {					
							log.debug ("NO SUPERA AUTENTICACION DEL PRINCIPADO");
							valido=false;
					}
				} catch (SystemException ex) {
					log.error("Se ha producido un error al validar el certificado:"+ex.getMessage(),ex);
					log.trace(ex.getStackTrace());
					valido = false;
				}
			} else {
				valido = false;
			}
		}
		catch (XMLDOMDocumentException ex)
		{
			log.error("Se ha producido un error de XML al validar el certificado:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			valido = false;
		}
		catch (PreferenciasException ex)
		{
			log.error("Se ha producido un error de preferencias al validar el certificado:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			valido = false;
		}
		return valido;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context =ctx;
	}
}
