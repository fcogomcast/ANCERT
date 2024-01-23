package es.tributasenasturias.services.ancert.solicitudEscritura.utils.firma;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.w3c.dom.Document;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.XMLDOMUtils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.firmadigital.FirmaDigital;
import es.tributasenasturias.services.firmadigital.WsFirmaDigital;



public class FirmaHelper implements IContextReader{
	

	private CallContext context;
	/**
	 * Genera un port de firma digital con el endpoint que se pasa por parámetro.
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
			Binding bi = bpr.getBinding();
			List <Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			   handlerList = new ArrayList<Handler>();
			//El manejador de cliente enviará su log con el mismo id de sesión que el log de aplicación.
			handlerList.add(new SoapClientHandler(LogFactory.getLogAplicacionContexto(context).getSessionId(), (Preferencias)context.get(CallContextConstants.PREFERENCIAS)));
			bi.setHandlerChain(handlerList);
		}
		return srPort;
	}
	/**
	 * Genera un Documento (org.w3c.dom) firmado a partir de un Documento de entrada.
	 * @param doc Documento a firmar
	 * @return Documento firmado o null si no es posible firmar.
	 * @throws SystemException 
	 */
	public Document firmaMensaje (Document doc)
	{
		Document firmado=null;
		LogHelper log;
		log = LogFactory.getLogAplicacionContexto(context);
		Preferencias pr = PreferenciasFactory.getPreferenciasContexto(context);
		if (pr!=null)
		{
				
			String endpointFirma = pr.getEndPointFirma();
			FirmaDigital srPort = getPort (endpointFirma);
			try
			{
				String msgText=XMLDOMUtils.getXMLText(doc);
				//CRUBENCVS 47084 20/02/2023
				//Utilizo la versión que permite firmar indicando el algoritmo
				//String msgFirmado = srPort.firmarXML(msgText, pr.getAliasCertificadoFirma(), pr.getIdNodoFirmar(), pr.getNodoContenedorFirma(), pr.getNsNodoContenedorFirma());
				String msgFirmado = srPort.firmarXMLAlgoritmo(msgText, pr.getAliasCertificadoFirma(), pr.getIdNodoFirmar(), pr.getNodoContenedorFirma(), pr.getNsNodoContenedorFirma(), pr.getUriAlgoritmoFirma(), pr.getUriAlgoritmoDigest());
				//FIN CRUBENCVS 47084 20/02/2023
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
	protected FirmaHelper()
	{
		super();
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
