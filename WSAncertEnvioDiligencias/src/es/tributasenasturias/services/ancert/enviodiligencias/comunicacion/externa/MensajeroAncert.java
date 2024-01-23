package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import es.tributasenasturias.services.ancert.enviodiligencias.SystemException;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosException;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.PlantillaXML;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.XMLDOMDocumentException;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.XMLUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogSoapClient;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.seguridad.SeguridadFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ObjectFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.utilidadesGenerales.Constantes;

/**
 * Esta clase envía el mensaje contruido a ANCERT.
 * Se encargará de añadir la funcionalidad que necesitemos, como firma.
 * No se llama al servicio como a los demás 
 * @author crubencvs
 *
 */
public class MensajeroAncert implements IContextReader{

	
	private CallContext context;
	@SuppressWarnings("unused")
	private MensajeroAncert() {
		super();
	}
	/**
	 * Crear un objeto pasándole los parámetros de contexto de llamada.
	 * @param context {@link CallContext} Contexto de llamada
	 */
	public MensajeroAncert (CallContext context)
	{
		this();
		this.context=context;
	}
	/**
	 * Convierte el mensaje a enviar de los objetos que lo contienen, a una cadena XML.
	 * @param mensajeOriginal
	 * @return
	 * @throws MensajeriaException
	 */
	private String getTextoMensaje(ENVIODILIGENCIA mensajeOriginal) throws MensajeriaException
	{
		String mensaje="";
		StringWriter str;
		try
		{
			JAXBContext ctx = JAXBContext.newInstance(mensajeOriginal.getClass().getPackage().getName());
			Marshaller mar = ctx.createMarshaller();
			mar.setProperty("jaxb.encoding", Constantes.getCodificacionMensajeAncert());
			str=new StringWriter();
			mar.marshal(mensajeOriginal,str);
			mensaje = str.toString();
		}
		catch (JAXBException e)
		{
			throw new MensajeriaException ("Error al procesar el mensaje para enviar:" + e.getMessage(),e);
		}
		return mensaje;
	}
	/**
	 * Convierte el mensaje recibido de cadena XML a objeto Java de Esquema.
	 * @param soapRespuesta {@link Document} que contiene la respuesta al mensaje
	 * @return
	 * @throws MensajeriaException
	 */
	public ENVIODILIGENCIA extraeMensajeDeRespuesta(Node soapRespuesta) throws MensajeriaException
	{
		Node mensaje = extraeNodoMensaje(soapRespuesta);
		ENVIODILIGENCIA respuesta=new ObjectFactory().createENVIODILIGENCIA();
		try
		{
			JAXBContext ctx = JAXBContext.newInstance(respuesta.getClass().getPackage().getName());
			Unmarshaller unm=ctx.createUnmarshaller(); 
			respuesta=(ENVIODILIGENCIA)unm.unmarshal(mensaje);
		}
		catch (JAXBException e)
		{
			throw new MensajeriaException ("Error al procesar el mensaje recibido de ANCERT:" + e.getMessage(),e);
		}
		return respuesta;
	}
	/**
	 * Extrae el nodo "ENVIO_DILIGENCIA" de la respuesta recibida de ANCERT
	 * @param mensajeRespuesta Mensaje de respuesta de ANCERT. Esquema SOAP.
	 * @return Nodo  que contiene el mensaje ENVIO_DILIGENCIA
	 * @throws MensajeriaException
	 */
	private Node extraeNodoMensaje (Node mensajeRespuesta) throws MensajeriaException
	{
		Node mensaje;
		NodeList nodos= XMLUtils.getAllNodes(mensajeRespuesta, "ENVIO_DILIGENCIA");
		if (nodos!=null && nodos.getLength()>0)
		{
			mensaje=nodos.item(0);
		}
		else
		{
			throw new MensajeriaException ("El formato de mensaje recibido de ANCERT no contiene el nodo ENVIO_DILIGENCIA.");
		}
		return mensaje;
	}
	/**
	 * Recupera la plantilla que se va a enviar como mensaje.
	 * @return Plantilla de mensaje.
	 */
	private String recuperaPlantilla() throws DatosException,SystemException
	{
		String plantilla="";
		//Recupera la plantilla.
		PlantillaXML plant = DatosFactory.newPlantillaXML(context);
		plantilla= plant.recuperaPlantillaEnvioDiligencia();
		return plantilla;
	}
	/**
	 * Extrae el contenido de la respuesta del servidor como un mensaje SOAP, y lo muestra.
	 * @param reply Respuesta del servidor en forma de mensaje SOAP que se va a convertir.
	 * @return Texto del mensaje de respuesta.
	 * @throws TransformerConfigurationException Error de configuración de la transformación
	 * @throws SOAPException 
	 * @throws TransformerException Si no puede convertir el mensaje a formato SOAP.
	 */
	private String recuperarResultadoComoString(SOAPMessage reply) throws TransformerConfigurationException,SOAPException, TransformerException
	{
		String respuesta;
		TransformerFactory transformerFactory =TransformerFactory.newInstance();
		Transformer transformer =transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING,
		        Constantes.getCodificacionMensajeAncert());
		//Extrae el contenido
		Source sourceContent = reply.getSOAPPart().getContent();
		//Lo devuelve
		StringWriter sw=new StringWriter();
		StreamResult result = new StreamResult(sw);
		transformer.transform(sourceContent, result);
		respuesta =sw.toString();
		return respuesta;
	}
	/**
	 * Construye la cabecera de información del mensaje SOAP al CGN.
	 * @param peticion Documento que contiene la plantilla de petición.
	 * @return Documento con los datos de cabecera insertados.
	 * @throws SolicitudException
	 */
	private Document construyeCabecera(Document peticion) throws MensajeriaException
	{
		GregorianCalendar cal=null;
		XMLGregorianCalendar xcal=null;
		Preferencias pref;
		try {
			pref = PreferenciasFactory.newInstance();
		} catch (PreferenciasException e) {
			throw new MensajeriaException ("Error al construir la cabecera del mensaje SOAP de envío de diligencia a ANCERT:"+e.getMessage(),e);
		}
		try {
			cal=new GregorianCalendar();
			cal.setTime(new java.util.Date());
			xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		} catch (DatatypeConfigurationException e) {
			throw new MensajeriaException ("Error al recuperar el timestamp para construir la cabecera SOAP hacia ANCERT." + e.getMessage(),e);
		}
		NodeList nodos = XMLUtils.getAllNodes(peticion,"SERVICE_DISPATCHER");
		if (nodos.getLength()==0)
		{
			return peticion; // Lo devolvemos sin cambiar.
		}
		
		Node cabecera = nodos.item(0); //Si hay cabecera, es el primer nodo.
		//Cabecera
		 Node timestamp=XMLUtils.getFirstChildNode( cabecera, "TIMESTAMP");
		 if (timestamp!=null)
		 {
			 XMLUtils.setNodeText(peticion, timestamp, xcal.toString());
		 }
		Node emisor = XMLUtils.getFirstChildNode( cabecera, "EMISOR");
		if (emisor!=null)
		{
			XMLUtils.setNodeText(peticion, emisor, pref.getEmisor());
		}
		Node servicio = XMLUtils.getFirstChildNode(cabecera, "SERVICIO");
		if (servicio!=null)
		{
			XMLUtils.setNodeText(peticion, servicio, pref.getCodigoServicio());
		}
		Node recep = XMLUtils.getFirstChildNode( cabecera, "RECEP");
		if (recep!=null)
		{
			XMLUtils.setNodeText(peticion, recep, pref.getReceptor());
		}
		Node tipoMsj= XMLUtils.getFirstChildNode( cabecera, "TIPO_MSJ");
		if (tipoMsj!=null)
		{
			XMLUtils.setNodeText(peticion, tipoMsj, pref.getTipoMensaje());
		}
		return peticion;
	}
	/**
	 * Inserta el mensaje a enviar en la plantilla de servicio
	 * @param peticion Documento que contiene la plantilla de mensaje SOAP a ANCERT.
	 * @param message Mensaje a enviar, en forma de un documento XML válido.
	 */
	public void setMessage(Document peticion,Document message) throws MensajeriaException
	{
		if (message==null)
		{
			throw new MensajeriaException ("Error. El mensaje que se intenta enviar está vacío.");
		}
		Element mensaje = message.getDocumentElement();
		NodeList nodos= XMLUtils.getAllNodes(peticion, "SERVICE_DISPATCHER_REQUEST");
		if (nodos.getLength()==0) //Sería extraño que no hubiera, a menos que se tratase de una plantilla mal realizada.
		{
			throw new MensajeriaException ("Error. Falta el nodo que ha de contener la petición (SERVICE_DISPATCHER_REQUEST) en plantilla.");
		}
		Node request = nodos.item(0);
		Node nuevo= request.getOwnerDocument().importNode(mensaje,true);
		if (nuevo!=null)
		{
			request.appendChild(nuevo);
		}
	}
	/**
	 * Realiza el envío de diligencia a través de mensajería SOAP
	 * @param peticion
	 * @return
	 * @throws MensajeriaException
	 */
	private Document enviarDiligenciaSOAP(Document peticion)  throws MensajeriaException,SystemException
	{
		Document resultado=null;
		Preferencias pref;
		try {
			pref = PreferenciasFactory.newInstance();
		} catch (PreferenciasException e) {
			throw new MensajeriaException ("Error al enviar el mensaje SOAP de envío de diligencia a ANCERT, relacionado con Preferencias:"+e.getMessage(),e);
		}
		String dir = pref.getEndpointEnvioDiligencia();
		String peticionData;
		SOAPConnection conn=null;
		try {
			LogSoapClient log=LogFactory.getLogSoapClientContexto(context);
			peticionData = XMLUtils.getXMLText(peticion);
			SOAPConnectionFactory soapfact=SOAPConnectionFactory.newInstance();
			conn= soapfact.createConnection();
			MessageFactory msgfactory = MessageFactory.newInstance();
			//Convertimos al formato que requieren en ANCERT.
			MimeHeaders mimeHeaders = new MimeHeaders();
		    mimeHeaders.addHeader("Content-Type","text/xml; charset="+Constantes.getCodificacionMensajeAncert());
			SOAPMessage msg = msgfactory.createMessage(mimeHeaders, new ByteArrayInputStream(peticionData.getBytes(Constantes.getCodificacionMensajeAncert())));
			msg.saveChanges();//Guardamos los cambios al mensaje. Redundante, pero si se incluye alguna modificación antes de esta línea, necesario.
			//Se grabala petición
			log.info("Mensaje enviado a ANCERT:" + peticionData);
			SOAPMessage reply = conn.call(msg, dir); // Enviamos la petición. 
			String msgEntrada=recuperarResultadoComoString(reply);
			log.info("Mensaje recibido de ANCERT:" + msgEntrada);
			//Resultado
			resultado=XMLUtils.parseXml(msgEntrada);
		} catch (XMLDOMDocumentException e) {
			throw new SystemException ("Error en el envío de diligencia debido a un problema en el xml de petición que se construyó:"+ e.getMessage(),e,this,"enviarDiligenciaSOAP");
		} catch (UnsupportedOperationException e) {
			 throw new SystemException ("Error al enviar diligencia debido a una operación no soportada:"+ e.getMessage(),e,this,"enviarDiligenciaSOAP");
		} catch (SOAPException e) {
			throw new SystemException ("Error al enviar diligencia debido a un problema de SOAP:"+ e.getMessage(),e,this,"enviarDiligenciaSOAP");
		} catch (TransformerConfigurationException e) {
			throw new SystemException ("Error al enviar diligencia debido a la transformación del mensaje :"+ e.getMessage(),e,this,"enviarDiligenciaSOAP");
		} catch (TransformerException e) {
			throw new SystemException ("Error al enviar diligencia debido a la transformación del mensaje  :"+ e.getMessage(),e,this,"enviarDiligenciaSOAP");
		} catch (UnsupportedEncodingException e) {
			throw new SystemException ("Error al enviar diligencia debido a la codificación del mensaje  :"+ e.getMessage(),e,this,"enviarDiligenciaSOAP");
		} catch (IOException e) {
			throw new SystemException ("Error al enviar diligencia debido a un error de entrada/salida :"+ e.getMessage(),e,this,"enviarDiligenciaSOAP");
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
	/**
	 * Envía la diligencia expresada en la instancia {@link ENVIODILIGENCIA} a ANCERT.
	 * Devuelve el mensaje de respuesta (no el mensaje SOAP).
	 * @param req mensaje de envío de diligencia.
	 * @return Node org.w3c.dom.Node con el mensaje respuesta recibido en forma de XML
	 * @throws MensajeriaException 
	 */
	public Node enviarDiligencia(ENVIODILIGENCIA req) throws MensajeriaException,SystemException
	{
		Document mensajeDevueltoAncert=null;
		Document mensajeRequest; //La request que hemos montado.
		Document mensajeSOAPEnviarAncert;
		String textoPlantilla="";
		//Convertimos el mensaje de entrada a un documento.
		String textoRequest=getTextoMensaje(req);
		//Recuperamos las preferencias.
		try {
			mensajeRequest=XMLUtils.parseXml(textoRequest);
			//Firmamos el mensaje.
			mensajeRequest = SeguridadFactory.newFirmaHelper(context).firmaMensaje(mensajeRequest);
		} catch (XMLDOMDocumentException e) {
			throw new SystemException ("No se ha podido interpretar la petición construída en base a los datos de la autoliquidación como XML válido:"+ e.getMessage()+"-"+textoRequest,e,this,"enviarDiligencia");
		}
		//Recuperamos el mensaje SOAP de la plantilla guardada en base.
		try
		{
			textoPlantilla = recuperaPlantilla();
			mensajeSOAPEnviarAncert=XMLUtils.parseXml(textoPlantilla);
		}
		catch (DatosException e)
		{
			throw new MensajeriaException ("No se ha podido recuperar la plantilla para construir el mensaje SOAP:"+ e.getMessage(),e);
		}
		catch (XMLDOMDocumentException e) {
			throw new MensajeriaException ("No se ha podido interpretar la plantilla recuperada de la base de datos como XML válido:"+ e.getMessage()+"-Plantilla"+textoPlantilla,e);
		}
		//Con el mensaje de request, se inserta en el mensaje SOAP
		setMessage(mensajeSOAPEnviarAncert,mensajeRequest);
		//Y ponemos los datos de cabecera
		mensajeSOAPEnviarAncert=construyeCabecera(mensajeSOAPEnviarAncert);
		mensajeDevueltoAncert=enviarDiligenciaSOAP(mensajeSOAPEnviarAncert);
		//Extraemos la respuesta, si es posible, o lanzamos error.
		Node recepcionResultado=extraeNodoMensaje(mensajeDevueltoAncert);
		return recepcionResultado;
	}
	//public String 
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
}
