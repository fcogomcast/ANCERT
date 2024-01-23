package es.tributasenasturias.servicios.ibi.deuda.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.util.Utils;
import es.tributasenasturias.servicios.ibi.deuda.xml.XMLDOMDocumentException;
import es.tributasenasturias.servicios.ibi.deuda.xml.XMLDOMUtils;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;

public class ValidadorEsquema implements SOAPHandler<SOAPMessageContext>{

	private static final String CONSULTA = "CONSULTA_DEUDAS";
	private static final String PAGO = "PAGO_DEUDAS";

	/**
	 * Extrae el contenido del certificado de la firma de WSSecurity
	 * @param mensaje Mensaje firmado.
	 * @return Cadena que contiene el certificado de la firma.
	 * @throws XMLDOMDocumentException
	 */
	private Node extraerRequest(Node mensaje, String nombreNodo, boolean salida)
			throws XMLDOMDocumentException {
		Node request;
		if (!salida)
		{
			request = XMLDOMUtils
					.selectSingleNode(
							mensaje,
							"/*[local-name()='Envelope']/*[local-name()='Body']/*[local-name()='MESSAGE_REQUEST']/*[local-name()='"+nombreNodo+"']");
		}
		else
		{
			request = XMLDOMUtils
			.selectSingleNode(
					mensaje,
					"/*[local-name()='Envelope']/*[local-name()='Body']/*[local-name()='MESSAGE_RESPONSE']/*[local-name()='"+nombreNodo+"']");
		}
		if (request==null)
		{
			return null;
		}
		return request;

	}
	/**
	 * Valida el mensaje de entrada o salida contra el esquema.
	 * @param context
	 */
	private void validarEsquema(SOAPMessageContext context)
	{
		Boolean salida = (Boolean) context
		.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		// Validamos tanto a la entrada como a la salida.
		// La diferencia será el error que devolvamos.
		
		SOAPMessage msg = context.getMessage();
		Node request;
		Source payload;
		String idSesion;
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		Logger logAplicacion = null;
		try {
			Preferencias pref = (Preferencias) context
					.get(CallContextConstants.PREFERENCIAS);
			idSesion = (String) context.get(CallContextConstants.IDSESION);
			logAplicacion = LogFactory.newLogger(pref.getModoLog(), pref
					.getFicheroLogAplicacion(), idSesion);
			logAplicacion.info("Manejador SOAP:["
					+ ValidadorEsquema.class.getName()
					+ "].Comienza la validación.");
			msg.writeTo(bo);
			if (new String(bo.toByteArray()).contains(CONSULTA))
			{
				request=extraerRequest(msg.getSOAPPart(),CONSULTA,salida);
			}
			else //Suponemos PAGO, por el momento
			{
				request=extraerRequest(msg.getSOAPPart(),PAGO,salida);
			}
			if (request==null)
			{
				logAplicacion.error("Error, no se puede extraer el contenido del mensaje para validar el esquema.");
				Utils.generateSOAPErrMessage(context.getMessage(), "Error técnico en validación de mensaje de "+ (salida?"salida":"entrada"), "0002", "SOAP Handler", true);
			}
			
			payload=new DOMSource(request);
		
			SchemaFactory fact = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source fScm = new StreamSource(
					getClass()
							.getClassLoader()
							.getResourceAsStream(
									"es/tributasenasturias/servicios/ibi/deuda/wsdl/xsd/EsquemaValidacion.xsd"));
			Schema scm = fact.newSchema(fScm);
			Validator val = scm.newValidator();
			logAplicacion.info("Manejador SOAP:["
					+ ValidadorEsquema.class.getName() + "].Mensaje validado.");
			val.validate(payload);
			return;
		} catch (TransformerFactoryConfigurationError e) {
			if (logAplicacion != null) {
				logAplicacion.error("Manejador SOAP:["
						+ ValidadorEsquema.class.getName()
						+ "].Error técnico en validación de mensaje de "
						+ (salida ? "salida" : "entrada") + ":"
						+ e.getMessage(), e);
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error técnico en validación de mensaje de "+ (salida?"salida":"entrada"), "0002", "SOAP Handler", true);
		} catch (SAXException e) {
			if (logAplicacion != null) {
				logAplicacion.error("Manejador SOAP:["
						+ ValidadorEsquema.class.getName() + "].El mensaje de "
						+ (salida ? "salida" : "entrada")
						+ " no cumple esquema:" + e.getMessage(), e);
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "El mensaje de  "+ (salida?"salida":"entrada") + " no cumple esquema.", "0002", "SOAP Handler", salida);
		} catch (IOException e) {
			if (logAplicacion != null) {
				logAplicacion.error("Manejador SOAP:["
						+ ValidadorEsquema.class.getName()
						+ "].Error técnico en validación de mensaje de "
						+ (salida ? "salida" : "entrada") + ":"
						+ e.getMessage(), e);
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error técnico en validación de mensaje de "+ (salida?"salida":"entrada"), "0002", "SOAP Handler", true);
			
		} catch (XMLDOMDocumentException e) {
			if (logAplicacion != null) {
				logAplicacion.error("Manejador SOAP:["
						+ ValidadorEsquema.class.getName()
						+ "].Error técnico en validación de mensaje de "
						+ (salida ? "salida" : "entrada") + ":"
						+ e.getMessage(), e);
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error técnico en validación de mensaje de "+ (salida?"salida":"entrada"), "0002", "SOAP Handler", true);
		}
		catch (SOAPException e) {
			if (logAplicacion != null) {
				logAplicacion.error("Manejador SOAP:["
						+ ValidadorEsquema.class.getName()
						+ "].Error técnico en validación de mensaje de "
						+ (salida ? "salida" : "entrada") + ":"
						+ e.getMessage(), e);
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error técnico en validación de mensaje de "+ (salida?"salida":"entrada"), "0002", "SOAP Handler", true);
			
		}
		finally
		{
			if (logAplicacion!=null)
			{
				logAplicacion.info("Manejador SOAP:["
						+ ValidadorEsquema.class.getName()
						+ "].Finaliza la validación.");
			}
			if (bo!=null)
			{
				try {bo.close();} catch (Exception e){}
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
		validarEsquema(context);
		return true;
	}

}
