package es.tributasenasturias.services.ancert.recepcionescritura.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.w3c.dom.Document;

import com.sun.xml.internal.ws.client.BindingProviderProperties;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.firmadigital.FirmaDigital;
import es.tributasenasturias.services.firmadigital.WsFirmaDigital;



public class FirmaHelper implements IContextReader{
	
	private CallContext context;
	//Timeout de la firma, la operación no puede tardar más de estos milisegundos.
	private static final String TIMEOUT="60000";
	/**
	 * Genera un port de firma digital con el endpoint que se pasa por parámetro.
	 * Además, configura el log de la firma digital.
	 * @param endpoint Endpoint de firma digital
	 * @return FirmaDigital generado que apunta al endpoint indicado.
	 */
	@SuppressWarnings("unchecked")
	private FirmaDigital getPort (String endpoint)
	{
		WsFirmaDigital srv = new WsFirmaDigital();
		FirmaDigital srPort = srv.getServicesPort(); 
		if (!endpoint.equals(""))
		{
			BindingProvider bpr = (BindingProvider) srPort;
			bpr.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpoint);
			//Se establece un tiempo de Timeout. 
			bpr.getRequestContext().put (BindingProviderProperties.REQUEST_TIMEOUT,TIMEOUT);
			//Se procesan los mensajes, guardándolos en el log
			//Este log será inmenso, no estoy seguro de que deba ser este, sino uno quizá por día.
			Binding bi = bpr.getBinding();
			List <Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			   handlerList = new ArrayList<Handler>();
			handlerList.add(new SoapClientHandler((String)context.get(CallContextConstants.IDSESION)));
			bi.setHandlerChain(handlerList);
		}
		return srPort;
	}
	/**
	 * Genera un Documento (org.w3c.dom) firmado a partir de un Documento de entrada.
	 * @param doc Documento a firmar
	 * @return Documento firmado o null si no es posible firmar.
	 */
	public final Document firmaMensaje (Document doc)
	{
		LogHelper log=LogFactory.getLogAplicacionContexto(context);
		Document firmado=null;
		Preferencias pr;
		pr = PreferenciasFactory.getPreferenciasContexto(context);
		if (pr!=null)
		{
			String endpointFirma = pr.getEndPointFirma();
			FirmaDigital srPort = getPort (endpointFirma);
			try
			{
				String msgText=XMLDOMUtils.getXMLText(doc);
				//CRUBENCVS 47084. 06/02/2023. Se utiliza la nueva firma que permite SHA2
				//La validación también acepta firmas SHA2, pero no cambia la llamada.
				//String msgFirmado = srPort.firmarXML(msgText, pr.getAliasCertificadoFirma(), pr.getIdNodoFirmar(), pr.getNodoContenedorFirma(), pr.getNsNodoContenedorFirma());
				String msgFirmado = srPort.firmarXMLAlgoritmo(msgText, pr.getAliasCertificadoFirma(), pr.getIdNodoFirmar(), pr.getNodoContenedorFirma(), pr.getNsNodoContenedorFirma(), pr.getUriAlgoritmoFirma(), pr.getUriAlgoritmoDigest());
				//FIN CRUBENCVS 47084
				log.debug("Msg Firmado:" + msgFirmado);
				firmado = XMLDOMUtils.parseXml(msgFirmado);
			}
			catch (Exception ex)
			{
				log.error ("Error al firmar el mensaje de salida: " + ex.getMessage());
				log.trace(ex.getStackTrace());
				firmado=null;
			}
		}
		else
		{
			log.error("Error al firmar el mensaje de salida: no se pueden recuperar las preferencias.");
			firmado=null;
		}
		return firmado;
	}
	/**
	 * Valida la firma que viene en un documento.
	 * @param xml Cadena que contiene el documento xml firmado.
	 * @return true si el documento tiene una firma correcta o false en otro caso.
	 */
	public boolean validaFirma (String xml)
	{
		boolean valido;
		LogHelper log;
		log = LogFactory.getLogAplicacionContexto(context);
		Preferencias pr = PreferenciasFactory.getPreferenciasContexto(context);
		if (pr!=null)
		{
				
			String endpointFirma = pr.getEndPointFirma();
			FirmaDigital srPort = getPort (endpointFirma);
			try{
				valido  = srPort.validar(xml);
			}
			catch (Exception ex)
			{
				log.error ("Error al validar el mensaje de entrada: " + ex.getMessage());
				log.trace(ex.getStackTrace());
				valido=false;
			}
		}
		else
		{
			log.error("Error al validar el mensaje de entrada: no se pueden recuperar las preferencias.");
			valido=false;
		}
		return valido;
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
