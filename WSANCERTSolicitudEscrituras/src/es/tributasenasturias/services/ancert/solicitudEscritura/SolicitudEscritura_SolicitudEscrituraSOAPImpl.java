package es.tributasenasturias.services.ancert.solicitudEscritura;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.xml.ws.WebServiceContext;

import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextManager;
import es.tributasenasturias.services.ancert.solicitudEscritura.factory.ObjectFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;



/**
 * 
 * Servicio de solicitud de escritura a ANCERT.
 * 
 * 
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.3-06/19/2008
 * 07:03 PM(bt) Generated source version: 2.1
 * 
 */
@WebService(portName = "SolicitudEscrituraSOAP", serviceName = "SolicitudEscritura", targetNamespace = "http://services.tributasenasturias.es/ancert/solicitudEscritura", wsdlLocation = "/wsdls/wsdlSolicitudEscritura.wsdl", endpointInterface = "es.tributasenasturias.services.ancert.solicitudEscritura.SolicitudEscritura")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain (file="HandlerChain.xml")
public class SolicitudEscritura_SolicitudEscrituraSOAPImpl implements
		SolicitudEscritura {

	//Recuperamos el contexto del servicio. En este punto, se deber�a tener un id �nico para los logs.
	//Si no se tiene, significa que no se ha hecho en las capas de manejadores, y se har� ahora.
	@Resource WebServiceContext wContext;
	
	public SolicitudEscritura_SolicitudEscrituraSOAPImpl() {
	}

	/**
	 * 
	 * Operaci�n de solicitud de escritura. Entrada: -C�digo de notario. -C�digo
	 * de notaria. -Protocolo. -A�o de autorizaci�n
	 * 
	 * 
	 * @param parameters
	 * @return returns
	 *         es.tributasenasturias.services.ancert.solicitudEscritura.SolicitudResponse
	 * @throws SolicitudEscrituraException
	 */
	public SolicitudResponse solicitar(EntradaType parameters)
			throws SolicitudEscrituraException {
		SolicitudResponse ret=null;
		Preferencias pref;
		CallContext context;
		LogHelper log=null;
		try
		{
			String idLog=(String)wContext.getMessageContext().get(CallContextConstants.ID_SESION);
			if (idLog==null || idLog.equals(""))
			{
				//Recuperamos en este momento el identificador �nico.
				idLog=Utils.getIdLlamada();
			}
			pref = (Preferencias)wContext.getMessageContext().get(CallContextConstants.PREFERENCIAS);
			log = (LogHelper) wContext.getMessageContext().get(CallContextConstants.LOG_APLICACION);
			//Se recuperan las preferencias. Se introducir�n en contexto para que en toda la llamada
			//est�n disponibles.
			context=CallContextManager.newCallContext();
			//A�adimos preferencias al contexto de llamada.
			context.setItem(CallContextConstants.PREFERENCIAS, pref);
			//Se a�ade el log
			log=LogFactory.newApplicationLog(idLog, pref);
			context.setItem(CallContextConstants.LOG_APLICACION, log);
			log.info("*************************************************Inicio de llamada al servicio de solicitud de escrituras para la sesi�n:" + idLog);
			SolicitudEscrituraImpl impl=new ObjectFactory().newSolicitudEscritura(context);
			//Llamamos.
			ret=impl.solicitar(parameters);
			log.info("*************************************************Se termina el proceso de llamada al servicio de solicitud de escrituras para la sesi�n:" + idLog);
		}
		catch (Exception ex)
		{
				
			log.error("Error inesperado en el endpoint del servicio:"+ex.getMessage());
			SolicitudFault fault=new SolicitudFault();
			fault.setSolicitudFault("Error inesperado en la llamada al servicio. No se puede completar la solicitud.");
			throw new SolicitudEscrituraException ("Error en llamada.",fault);
		}
		finally
		{
			//LogHelper.destroyLogger();
		}
		return ret;
	}

}
