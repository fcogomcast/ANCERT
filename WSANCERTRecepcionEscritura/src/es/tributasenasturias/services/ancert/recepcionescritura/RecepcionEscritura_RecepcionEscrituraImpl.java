package es.tributasenasturias.services.ancert.recepcionescritura;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.xml.ws.WebServiceContext;


import es.tributasenasturias.services.ancert.recepcionescritura.factory.DomainObjectsFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.handler.HandlerConstant;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.General;
/**
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.3-06/19/2008
 * 07:03 PM(bt) Generated source version: 2.1
 * 
 */
@WebService(portName = "RecepcionEscritura", serviceName = "RecepcionEscritura", targetNamespace = "http://recepcionescritura.ancert.services.tributasenasturias.es", wsdlLocation = "/wsdls/RecepcionEscritura.wsdl", endpointInterface = "es.tributasenasturias.services.ancert.recepcionescritura.RecepcionEscritura")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain (file="HandlerChain.xml")
public class RecepcionEscritura_RecepcionEscrituraImpl implements
		RecepcionEscritura {

	public RecepcionEscritura_RecepcionEscrituraImpl() {
	}

	//Recuperamos el contexto del servicio. En este punto, se deber�a tener un id �nico para los logs.
	//Si no se tiene, significa que no se ha hecho en las capas de manejadores, y se har� ahora.
	//Tambi�n se recuperar� aqu� el XML de servicio, que se habr� eliminado de la entrada.
	
	@Resource WebServiceContext wContext;
	/**
	 * 
	 * @param parameters
	 * @return returns
	 *         es.tributasenasturias.services.ancert.recepcionescritura.RecepcionEscrituraResponse
	 */
	public RecepcionEscrituraResponse recepcionEscritura(
			RecepcionEscritura_Type parameters) {
		RecepcionEscrituraImpl env=null;
		RecepcionEscrituraResponse res=null;
		LogHelper log=null;
		try
		{
			String idLog=(String)wContext.getMessageContext().get(HandlerConstant.getIdLog());
			//String idLog=getIdSesion(wContext.getMessageContext());
			if (idLog==null || idLog.equals(""))
			{
				//Recuperamos en este momento el identificador �nico.
				idLog=General.getIdLlamada();
			}
			//Recuperamos el contenido del mensaje. En caso de que no haya nada, continuamos porque
			//el servicio est� preparado para tratar este caso.
			String xml=(String)wContext.getMessageContext().get(HandlerConstant.getXmlServicio());
			//Nada m�s recibirlo, intentamos destruirlo.
			wContext.getMessageContext().remove(HandlerConstant.getXmlServicio());
			//Posteriormente lo utilizaremos para enviar el mensaje de salida.
			log=LogFactory.newApplicationLog(idLog);
			env=new DomainObjectsFactory().newRecepcionEscritura(idLog);
			res=new RecepcionEscrituraResponse();
			//Introducimos la respuesta obtenido de la ejecuci�n del objeto de implementaci�n
			//del servicio en el contexto.
			wContext.getMessageContext().put(HandlerConstant.getXmlServicio(), env.envioEscritura(xml));
		}
		catch (Exception e)
		{
			log.error("Error no controlado en endpoint de Recepci�n de escrituras:" + e.getMessage());
			log.trace(e.getStackTrace());
			//TODO: Devolver algo.
			throw new WebServiceException ("Error no controlado en endpoint de recepci�n de escrituras.");
		}
		finally
		{
			env=null;
			log=null;
		}
		return res;
	}

}
