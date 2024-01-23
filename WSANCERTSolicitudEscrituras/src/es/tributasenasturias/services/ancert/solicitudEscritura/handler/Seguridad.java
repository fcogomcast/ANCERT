package es.tributasenasturias.services.ancert.solicitudEscritura.handler;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;


import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.XMLDOMUtils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.wssecurity.WSSecurityFactory;




public class Seguridad implements SOAPHandler<SOAPMessageContext> {

	private Preferencias pref=null;
	private String idSesion=null;
	public Seguridad(String idSesion, Preferencias pref){
		this.idSesion=idSesion;
		this.pref= pref;
	}
	/**
	 * Gestiona la seguridad del mensaje de salida
	 * Firma mensaje a la salida
	 * @param context {@link SOAPMessageContext} con el contexto del mensaje SOAP
	 */
	private void seguridadMensaje(SOAPMessageContext context) throws SOAPFaultException{
		LogHelper logAplicacion=null;
		try {
			logAplicacion= LogFactory.newApplicationLog(idSesion, pref);
			Boolean salida = (Boolean) context
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (salida && "S".equalsIgnoreCase(pref.getFirmaSalida())){
				String mensajeSalida = XMLDOMUtils.getXMLText(context.getMessage().getSOAPPart());
				mensajeSalida=WSSecurityFactory.newConstructorResultado(pref,new SoapClientHandler(idSesion, pref)).firmaMensaje(mensajeSalida);
				context.getMessage().getSOAPPart().setContent(new StreamSource(new StringReader(mensajeSalida)));
			}
		} 
		catch (Exception ex)
		{
			if (!(ex instanceof SOAPFaultException))
			{
				if (logAplicacion!=null)
				{
					logAplicacion.error("Error en las comprobaciones de seguridad del mensaje:"
							+ ex.getMessage(), ex);
				}
				else
				{
					System.err.println (new java.util.Date().toString()+"::Servicio de Solicitud de Escrituras::error en las comprobaciones de seguridad del mensaje::"+ex.getMessage());
					ex.printStackTrace();
					
				}
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación Solicitud de Escrituras <--> Servicio WSSecurity."	, "0002", "SOAP Handler");
			}
			else
			{
				throw (SOAPFaultException)ex;
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
		seguridadMensaje(context);
		return true;
	}

}
