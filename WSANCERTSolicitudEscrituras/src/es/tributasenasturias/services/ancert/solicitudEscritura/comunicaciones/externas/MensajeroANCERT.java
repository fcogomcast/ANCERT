package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.XMLDOMDocumentException;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.GestorSolicitudesBD;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.factory.ObjectFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.XMLDOMUtils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.wssecurity.ComprobadorWS;
import es.tributasenasturias.services.ancert.solicitudEscritura.wssecurity.WSSecurityException;
import es.tributasenasturias.services.ancert.solicitudEscritura.wssecurity.WSSecurityFactory;

/**
 * Implementa la interfaz con el servicio web de CGN. Este servicio se conectará mediante SOAP sobre http.
 * @author crubencvs
 *
 */
public class MensajeroANCERT implements IContextReader{
	

	private Document peticion=null;
	private Preferencias pr=null;
	CallContext context;
	
	private String recuperarResultadoComoString(SOAPMessage reply) throws TransformerConfigurationException,SOAPException, TransformerException
	{
		String respuesta;
		TransformerFactory transformerFactory =TransformerFactory.newInstance();
		Transformer transformer =transformerFactory.newTransformer();
		//Extract the content of the reply
		Source sourceContent = reply.getSOAPPart().getContent();
		//Set the output for the transformation
		StringWriter sw=new StringWriter();
		StreamResult result = new StreamResult(sw);
		transformer.transform(sourceContent, result);
		respuesta =sw.toString();
		return respuesta;
	}
	/**
	 * Recupera la plantilla que se va a enviar como mensaje.
	 * @return Plantilla de mensaje.
	 */
	private String recuperaPlantilla() throws SystemException
	{
		String plantilla="";
		//Recupera la plantilla.
		GestorSolicitudesBD dt = new ObjectFactory().newDatos(context);
		plantilla= dt.recuperaPlantillaSolicitud();
		return plantilla;
	}
	/**
	 * Construye la cabecera de información del mensaje SOAP al CGN.
	 * @param peticion Documento que contiene la plantilla de petición.
	 * @return Documento con los datos de cabecera insertados.
	 * @throws MensajeriaException
	 */
	private Document construyeCabecera(Document peticion) throws MensajeriaException
	{
		GregorianCalendar cal=null;
		XMLGregorianCalendar xcal=null;
		try {
			cal=new GregorianCalendar();
			cal.setTime(new java.util.Date());
			xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		} catch (DatatypeConfigurationException e) {
			throw new MensajeriaException ("Error al recuperar el timestamp." + e.getMessage(),e);
		}
		NodeList nodos = XMLDOMUtils.getAllNodes(peticion,"SERVICE_DISPATCHER");
		if (nodos.getLength()==0)
		{
			return peticion; // Lo devolvemos sin cambiar.
		}
		
		Node cabecera = nodos.item(0); //Si hay cabecera, es el primer nodo.
		//Cabecera
		 Node timestamp=XMLDOMUtils.getFirstChildNode( cabecera, "TIMESTAMP");
		 if (timestamp!=null)
		 {
			 XMLDOMUtils.setNodeText(peticion, timestamp, xcal.toString());
		 }
		Node emisor = XMLDOMUtils.getFirstChildNode( cabecera, "EMISOR");
		if (emisor!=null)
		{
			XMLDOMUtils.setNodeText(peticion, emisor, pr.getEmisorSolicitud());
		}
		Node servicio = XMLDOMUtils.getFirstChildNode(cabecera, "SERVICIO");
		if (servicio!=null)
		{
			XMLDOMUtils.setNodeText(peticion, servicio, pr.getCodigoServicio());
		}
		Node recep = XMLDOMUtils.getFirstChildNode( cabecera, "RECEP");
		if (recep!=null)
		{
			XMLDOMUtils.setNodeText(peticion, recep, pr.getReceptorSolicitud());
		}
		Node tipoMsj= XMLDOMUtils.getFirstChildNode( cabecera, "TIPO_MSJ");
		if (tipoMsj!=null)
		{
			XMLDOMUtils.setNodeText(peticion, tipoMsj, pr.getTipoMensaje());
		}
		return peticion;
	}
	public MensajeroANCERT(CallContext context) throws MensajeriaException,SystemException
	{
		try {
			this.context=context; //Asignamos el contexto.
			//Recuperamos las preferencias del contexto.
			pr=PreferenciasFactory.getPreferenciasContexto(context);
			if (pr==null)//Fallo, no se han podido recuperar
			{
				throw new MensajeriaException ("Error en preferencias al crear el interfaz con el servicio remoto: no se han podido recuperar las preferencias.");
			}
			//Recuperamos la plantilla
			String plantilla=recuperaPlantilla();
			// y montamos la petición.

			this.peticion= XMLDOMUtils.parseXml(plantilla);
			this.peticion= construyeCabecera(this.peticion);
		} catch (XMLDOMDocumentException e) {
			this.peticion=null;
			throw new SystemException ("Error de XML al crear el interfaz con el servicio remoto:" + e.getMessage(),e);
		}
		catch (Exception e)
		{
			throw new SystemException ("Error inesperado al crear el interfaz con el servicio remoto:" + e.getMessage(),e);
		}
		
	}
		
	/**
	 * Inserta el mensaje a enviar en la plantilla de servicio.
	 * @param message Mensaje a enviar, en forma de un documento XML válido.
	 */
	public void setMessage(Document message) throws MensajeriaException
	{
		if (message==null)
		{
			throw new MensajeriaException ("Error. El mensaje que se intenta enviar está vacío.",this);
		}
		Element mensaje = message.getDocumentElement();
		NodeList nodos= XMLDOMUtils.getAllNodes(this.peticion, "SERVICE_DISPATCHER_REQUEST");
		if (nodos.getLength()==0) //Sería extraño que no hubiera, a menos que se tratase de una plantilla mal realizada.
		{
			throw new MensajeriaException ("Error. Falta el nodo que ha de contener la petición (SERVICE_DISPATCHER_REQUEST).",this);
		}
		Node request = nodos.item(0);
		Node nuevo= request.getOwnerDocument().importNode(mensaje,true);
		if (nuevo!=null)
		{
			request.appendChild(nuevo);
		}
	}
	
	public Document solicitar()  throws MensajeriaException,SystemException
	{
		Document resultado=null;
		String dir = pr.getEndpointSolicitud();
		String peticionData;
		SOAPConnection conn=null;
		try {
			peticionData = XMLDOMUtils.getXMLText(this.peticion);
			//38243 Tenemos que firmar la salida a ANCERT
			LogHelper logApp=LogFactory.getLogAplicacionContexto(context);
			ComprobadorWS wssecurity = WSSecurityFactory.newConstructorResultado(pr,new SoapClientHandler(logApp.getSessionId(), pr));
			peticionData= wssecurity.firmaMensaje(peticionData);
			//Eliminamos la declaración de XML, si hubiese, para que desde ANCERT validen la petición
			if(peticionData.contains("<?xml")){
				peticionData = peticionData.substring(peticionData.indexOf("?>")+2);
			}
			SOAPConnectionFactory soapfact=SOAPConnectionFactory.newInstance();
			conn= soapfact.createConnection();
			MessageFactory msgfactory = MessageFactory.newInstance();
			SOAPMessage msg = msgfactory.createMessage();
			SOAPPart soappart= msg.getSOAPPart();
			StreamSource peticion = new StreamSource(new StringReader(peticionData));
			soappart.setContent(peticion); 
			msg.saveChanges();//Guardamos los cambios al mensaje.
			LogHelper logsoapClient= LogFactory.newSoapClientLog(logApp.getSessionId(), pr);
			logsoapClient.doLogSoapClient("Envío::"+peticionData);
			SOAPMessage reply = conn.call(msg, dir); // Enviamos la petición. 
			String msgEntrada=recuperarResultadoComoString(reply);
			//Resultado
			logsoapClient.doLogSoapClient("Recepción::"+msgEntrada);
			resultado=XMLDOMUtils.parseXml(msgEntrada);
			
		} catch (XMLDOMDocumentException e) {
			throw new SystemException ("Error al solicitar una escritura debido a un problema en el xml de petición que se construyó:"+ e.getMessage(),e);
		} catch (UnsupportedOperationException e) {
			 throw new SystemException ("Error al solicitar una escritura debido a :"+ e.getMessage(),e);
		} catch (SOAPException e) {
			throw new SystemException ("Error al solicitar una escritura debido a un problema de SOAP:"+ e.getMessage(),e);
		} catch (TransformerConfigurationException e) {
			throw new SystemException ("Error al solicitar una escritura debido a la transformación del mensaje :"+ e.getMessage(),e);
		} catch (TransformerException e) {
			throw new SystemException ("Error al solicitar una escritura debido a la transformación del mensaje  :"+ e.getMessage(),e);
		} catch (WSSecurityException e) {
			throw new SystemException ("Error al solicitar una escritura al construir la firma WS-Security :"+ e.getMessage(),e);
		}
		finally
		{
			if (conn!=null)
			{
				try {conn.close();}catch (SOAPException e){}
			}
		}
		
		return resultado;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
}
