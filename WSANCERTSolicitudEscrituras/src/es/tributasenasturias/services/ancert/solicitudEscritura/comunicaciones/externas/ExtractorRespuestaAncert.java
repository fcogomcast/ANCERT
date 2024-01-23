package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.tributasenasturias.services.ANCERT.serviceDispatcher.types.RESULTADO;
import es.tributasenasturias.services.ANCERT.serviceDispatcher.types.SERVICIOSESCRITURA;
import es.tributasenasturias.services.ancert.solicitudEscritura.ResultadoType;
import es.tributasenasturias.services.ancert.solicitudEscritura.SolicitudResponse;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SolicitudException;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Mensajes;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.XMLDOMUtils;

public class ExtractorRespuestaAncert {
	private boolean error=false;
	private String codigoError="";
	private String textoError="";
	
	private SolicitudResponse respuestaSolicitud=null;
	
	public SolicitudResponse getResultado() {
		return respuestaSolicitud;
	}
	protected ExtractorRespuestaAncert()
	{
		super();
	}
	public String getCodigoError() {
		return codigoError;
	}

	public String getTextoError() {
		return textoError;
	}
	
	public boolean isError() {
		return error;
	}
	private void setRespuesta(Document resp) throws MensajeriaException,SystemException
	{
		try
		{
		//Recuperamos el nodo SERVICE_DISPATCHER_RESPONSE
		NodeList nodos = XMLDOMUtils.getAllNodes(resp, "SERVICE_DISPATCHER_RESPONSE");
		if (nodos.getLength()==0)
		{
			throw new MensajeriaException ("No se ha encontrado el nodo SERVICE_DISPATCHER_RESPONSE en la respuesta.");
		}
		Node serviceResponse = nodos.item(0);
		if (!serviceResponse.hasChildNodes())
		{
			throw new MensajeriaException ("El nodo SERVICE_DISPATCHER_RESPONSE no tiene resultado.");
		}
		Node xmlRespuesta=serviceResponse.getFirstChild();
		//Convertimos a objetos de esquema
		JAXBContext jbc = JAXBContext.newInstance(SERVICIOSESCRITURA.class.getPackage().getName());
		Unmarshaller unm = jbc.createUnmarshaller();
		SERVICIOSESCRITURA respuesta=(SERVICIOSESCRITURA)unm.unmarshal(xmlRespuesta);
		RESULTADO nodoResultado = respuesta.getREPLY().getRESULTADO();
		error=nodoResultado.isTIPOERROR();
		codigoError=nodoResultado.getCODIGOERROR();
		textoError=nodoResultado.getTEXTOERROR();
		}
		catch (JAXBException ex)
		{
			throw new SystemException ("Error al convertir el xml de respuesta a objetos del esquema."+ex.getCause().getMessage(), ex.getCause());
		}
	}
	/**
	 * Procesa el resultado de nuestro servicio en función de la respuesta del servicio de CGN.
	 * @param respServicio - Respuesta del servicio de solicitud de escritura de CGN.
	 * @return - Respuesta de la solicitud que ha hecho el cliente a nuestro servicio, con los datos de código y mensaje.
	 * @throws SolicitudException - Si existe algún error grave en el proceso.
	 */
	public void procesaResultado(Document respServicio) throws MensajeriaException,SystemException
	{
		respuestaSolicitud = new SolicitudResponse();
		setRespuesta(respServicio);
		String codigoErrorAncert=getCodigoError();
		ResultadoType r=null;
		if (codigoErrorAncert!=null && !codigoErrorAncert.equals(""))
		{
			r=new ResultadoType();
			String codigoPropio=Mensajes.getCodeFromANCERT(codigoErrorAncert);
			r.setCodigo(codigoPropio);
			r.setMensaje(Mensajes.getMessage(codigoPropio));
		}
		respuestaSolicitud.setResultado(r);
	}
	
}
