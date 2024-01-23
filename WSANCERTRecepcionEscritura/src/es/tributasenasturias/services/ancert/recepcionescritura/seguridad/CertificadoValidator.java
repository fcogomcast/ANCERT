package es.tributasenasturias.services.ancert.recepcionescritura.seguridad;

import es.tributasenasturias.services.ancert.recepcionescritura.bd.DatosFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.bd.GestorCertificadosBD;
import es.tributasenasturias.services.ancert.recepcionescritura.bd.GestorCertificadosBD.PermisosServicio;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.XMLDOMUtils;

/**
 * Validador de certificado de cabecera
 * 
 * @author davidsa
 * 
 */
public class CertificadoValidator implements IContextReader {

	private CallContext context;

	public String getCertificado(String datosXML) {
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
			// org.w3c.dom.NodeList certificados = m_resultadoXML
			// .getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#",
			// "X509Certificate");
			// org.w3c.dom.NodeList certificados = m_resultadoXML
			// .getElementsByTagName("dsig:X509Certificate");
			org.w3c.dom.NodeList certificados = XMLDOMUtils
					.getAllNodesCondicion(
							m_resultadoXML,
							"//*[local-name()='Signature']/*[local-name()='KeyInfo']/*[local-name()='X509Data']/*[local-name()='X509Certificate']");
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
	 * Valida los permisos del certificado que viene en la firma para ejecutar
	 * el servicio.
	 * 
	 * @param xml
	 *            Documento que contiene una firma xml.
	 * @return true si el certificado que se recupera de la firma de xml tiene
	 *         permisos sobre el servicio. false en otro caso.
	 */
	public boolean tienePermisos(String xml) throws SeguridadException {
		boolean valido = false;
		LogHelper log = LogFactory.getLogAplicacionContexto(context);
		try {
			// Si las preferencias dicen que no se valide el certificado, se da
			// por bueno.
			Preferencias pref = PreferenciasFactory.newInstance();
			if ("N".equalsIgnoreCase(pref.getValidaCertificado())) {
				return true;
			}
			String certificado = getCertificado(xml);
			GestorCertificadosBD bd = null;
			GestorCertificadosBD.PermisosServicio autorizacion = null;

			if (certificado != null && !certificado.equals("")) {
				// Lo enviamos al servicio del Principado, para que nos indique
				// si
				// es válido.
				AutenticacionPAHelper aut = SeguridadFactory
						.newAutenticacionPAHelper(context);
				try {
					// Llamamos al servicio de autenticación del Principado
					String strCIFNIF = aut.login(certificado);
					if (strCIFNIF != null) {
						log
								.info("HA SUPERADO LA AUTENTICACION DE CERTIFICADO DEL PRINCIPADO:");
						// Validamos el Id que nos han pasado.
						bd = DatosFactory.newGestorCertificadosBD(context);
						autorizacion = bd.permisoServicio(strCIFNIF);
						if (PermisosServicio.NO_AUTORIZADO.equals(autorizacion)) {
							// "No está autorizado para utilizar el servicio"
							valido = false;
						} else if (PermisosServicio.ERROR.equals(autorizacion)) {
							valido = false;
						} else {
							valido = true;
						}
					}
					if (strCIFNIF == null) {
						log.info("NO SUPERA AUTENTICACION DEL PRINCIPADO");
						valido = false;
					}
				} catch (SystemException ex) {
					log.error(
							"Se ha producido un error al validar el certificado:"
									+ ex.getMessage(), ex);
					log.trace(ex.getStackTrace());
					valido = false;
				}
			} else {
				valido = false;
			}
		} catch (PreferenciasException ex) {
			log.error(
					"Se ha producido un error de preferencias al validar el certificado:"
							+ ex.getMessage(), ex);
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
		context = ctx;
	}
}
