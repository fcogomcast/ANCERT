package es.tributasenasturias.services.ancert.solicitudEscritura.objetos;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import es.tributasenasturias.services.ancert.solicitudEscritura.ResultadoType;
import es.tributasenasturias.services.ancert.solicitudEscritura.SolicitudResponse;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.XMLDOMUtils;

public class Utils {

	static private String hexEncode(byte[] aInput) {
		StringBuilder result = new StringBuilder();
		char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		for (int idx = 0; idx < aInput.length; ++idx) {
			byte b = aInput[idx];
			result.append(digits[(b & 0xf0) >> 4]);
			result.append(digits[b & 0x0f]);
		}
		return result.toString();
	}

	/**
	 * Recupera un checkcum basado en un identificador �nico. Se asegura que el
	 * identificador es �nico.
	 */
	public static String getIdLlamada() {
		UUID id = UUID.randomUUID();
		String identificador = null;
		byte[] mensaje = id.toString().getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(mensaje);
			identificador = hexEncode(md.digest());

		} catch (NoSuchAlgorithmException nsae) {
			identificador = "";
		}
		return identificador;
	}

	/**
	 * Devuelve un objeto de respuesta con el resultado establecido en funci�n
	 * de un c�digo de mensaje que se le pasa.
	 * 
	 * @param res -
	 *            Objeto de respuesta a modificar.
	 * @param mensajeCode -
	 *            C�digo de mensaje.
	 * @return - Objeto de respuesta modificado.
	 */
	public static SolicitudResponse setResponse(SolicitudResponse res,
			String mensajeCode) {
		SolicitudResponse r = res;
		ResultadoType rtype = new ResultadoType();
		rtype.setCodigo(mensajeCode);
		rtype.setMensaje(Mensajes.getMessage(mensajeCode));
		r.setResultado(rtype);
		return r;
	}

	/**
	 * Recupera el directorio relativo al dominio donde se situar�n los ficheros
	 * generados de log o preferencias.
	 * 
	 * @return - Directorio de aplicaci�n.
	 */
	public static String getAppDirectory() {
		return "proyectos/WSANCERTSolicitudEscrituras";
	}

	public static SOAPFaultException createSOAPFaultException(
			String pFaultString) {
		SOAPFaultException sex = null;
		try {
			SOAPFault fault = SOAPFactory.newInstance().createFault();
			fault.setFaultString(pFaultString);
			fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE,
					"SolicitudEscrituras"));
			sex = new SOAPFaultException(fault);
		} catch (Exception ex) {
			System.out
					.println("Solicitud Escrituras:===>Error grave al construir la excepci�n de SOAP."
							+ ex.getMessage());
			throw new RuntimeException(
					"Excepci�n grave al devolver el mensaje.");

		}
		return sex;
	}

	// ** Para convertir de lo que nos devuelve el lanzador a un xml con tags
	// correctos.
	private static HashMap<String, String> htmlEntities;
	static {
		htmlEntities = new HashMap<String, String>();
		htmlEntities.put("&lt;", "<");
		htmlEntities.put("&gt;", ">");
		htmlEntities.put("&amp;", "&");
		htmlEntities.put("&quot;", "\"");
		htmlEntities.put("&agrave;", "�");
		htmlEntities.put("&Agrave;", "�");
		htmlEntities.put("&acirc;", "�");
		htmlEntities.put("&auml;", "�");
		htmlEntities.put("&Auml;", "�");
		htmlEntities.put("&Acirc;", "�");
		htmlEntities.put("&aring;", "�");
		htmlEntities.put("&Aring;", "�");
		htmlEntities.put("&aelig;", "�");
		htmlEntities.put("&AElig;", "�");
		htmlEntities.put("&ccedil;", "�");
		htmlEntities.put("&Ccedil;", "�");
		htmlEntities.put("&eacute;", "�");
		htmlEntities.put("&Eacute;", "�");
		htmlEntities.put("&egrave;", "�");
		htmlEntities.put("&Egrave;", "�");
		htmlEntities.put("&ecirc;", "�");
		htmlEntities.put("&Ecirc;", "�");
		htmlEntities.put("&euml;", "�");
		htmlEntities.put("&Euml;", "�");
		htmlEntities.put("&iuml;", "�");
		htmlEntities.put("&Iuml;", "�");
		htmlEntities.put("&ocirc;", "�");
		htmlEntities.put("&Ocirc;", "�");
		htmlEntities.put("&ouml;", "�");
		htmlEntities.put("&Ouml;", "�");
		htmlEntities.put("&oslash;", "�");
		htmlEntities.put("&Oslash;", "�");
		htmlEntities.put("&szlig;", "�");
		htmlEntities.put("&ugrave;", "�");
		htmlEntities.put("&Ugrave;", "�");
		htmlEntities.put("&ucirc;", "�");
		htmlEntities.put("&Ucirc;", "�");
		htmlEntities.put("&uuml;", "�");
		htmlEntities.put("&Uuml;", "�");
		htmlEntities.put("&nbsp;", " ");
		htmlEntities.put("&copy;", "\u00a9");
		htmlEntities.put("&reg;", "\u00ae");
		htmlEntities.put("&euro;", "\u20a0");
	}

	public static final String unescapeHTML(String source) {
		int i, j;

		boolean continueLoop;
		int skip = 0;
		do {
			continueLoop = false;
			i = source.indexOf("&", skip);
			if (i > -1) {
				j = source.indexOf(";", i);
				if (j > i) {
					String entityToLookFor = source.substring(i, j + 1);
					String value = (String) htmlEntities.get(entityToLookFor);
					if (value != null) {
						source = source.substring(0, i) + value
								+ source.substring(j + 1);
						continueLoop = true;
					} else {
						skip = i + 1;
						continueLoop = true;
					}
				}
			}
		} while (continueLoop);
		return source;
	}

	/**
	 * Genera un SOAP Fault. El fault contendr� una secci�n de detalle.
	 * 
	 * @param msg
	 *            Mensaje SOAP sobre el que se generar� el error. Se utilizar�
	 *            para a�adirle la secci�n SOAP Fault
	 * @param reason
	 *            Texto de raz�n de error (aparecer� en el faultString )
	 * @param codigo
	 *            C�digo de error que aparecer� en el detalle.
	 * @param mensaje
	 *            Mensaje que aparecer� en el detalle.
	 */
	@SuppressWarnings("unchecked")
	public static void generateSOAPErrMessage(SOAPMessage msg, String reason,
			String codigo, String mensaje) {
		try {
			SOAPEnvelope soapEnvelope = msg.getSOAPPart().getEnvelope();
			SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
			SOAPFault soapFault = soapBody.addFault();
			soapFault.setFaultString(reason);
			Detail det = soapFault.addDetail();
			Name name = soapEnvelope.createName("id");
			det.addDetailEntry(name);

			name = soapEnvelope.createName("mensaje");
			det.addDetailEntry(name);
			DetailEntry entry;
			Iterator<DetailEntry> it = det.getDetailEntries();
			while (it.hasNext()) {
				entry = it.next();
				if (entry.getLocalName().equals("id")) {
					XMLDOMUtils.setNodeText(entry.getOwnerDocument(), entry,
							codigo);
				}
				if (entry.getLocalName().equals("mensaje")) {
					XMLDOMUtils.setNodeText(entry.getOwnerDocument(), entry,
							mensaje);
				}

			}
			throw new SOAPFaultException(soapFault);
		} catch (SOAPException e) {
		}
	}

}
