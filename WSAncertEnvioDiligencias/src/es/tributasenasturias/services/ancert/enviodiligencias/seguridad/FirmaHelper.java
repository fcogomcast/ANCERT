package es.tributasenasturias.services.ancert.enviodiligencias.seguridad;

import javax.xml.ws.BindingProvider;

import localhost._7001.FirmaDigital;
import localhost._7001.WsFirmaDigital;

import org.w3c.dom.Document;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.Utils;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.XMLUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogAplicacion;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;



public class FirmaHelper implements IContextReader{
	

	private CallContext context;
	protected FirmaHelper()
	{
		throw new UnsupportedOperationException("No se puede utilizar un constructor sin parámetros para "+this.getClass().getName());
	}
	protected FirmaHelper(CallContext context)
	{
		super();
		this.context=context;
	}
	/**
	 * Genera un port de firma digital con el endpoint que se pasa por parámetro.
	 * @param endpoint Endpoint de firma digital
	 * @return FirmaDigital generado que apunta al endpoint indicado.
	 */
	private FirmaDigital getPort (String endpoint)
	{
		WsFirmaDigital srv = new WsFirmaDigital();
		FirmaDigital srPort = srv.getServicesPort(); 
		if (!endpoint.equals(""))
		{
			BindingProvider bpr = (BindingProvider) srPort;
			bpr.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpoint);
			//Se añade el SoapClientLog
			Utils.setHandlerChain(bpr,this.context);
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
		LogAplicacion log=LogFactory.getLogAplicacionContexto(context);
		Document firmado=null;
		Preferencias pr;
		pr = PreferenciasFactory.getPreferenciasContexto(context);
		if (pr!=null)
		{
			String endpointFirma = pr.getEndPointFirma();
			FirmaDigital srPort = getPort (endpointFirma);
			try
			{
				String msgText=XMLUtils.getXMLText(doc);
				//CRUBENCVS 47084. 06/02/2023. Se utiliza la nueva firma que permite SHA2
				//La validación también acepta firmas SHA2, pero no cambia la llamada.
				//String msgFirmado = srPort.firmarXML(msgText, pr.getAliasCertificadoFirma(), pr.getIdNodoFirmar(), pr.getNodoContenedorFirma(), pr.getNsNodoContenedorFirma());
				String msgFirmado = srPort.firmarXMLAlgoritmo(msgText, pr.getAliasCertificadoFirma(), pr.getIdNodoFirmar(), pr.getNodoContenedorFirma(), pr.getNsNodoContenedorFirma(), pr.getUriAlgoritmoFirma(), pr.getUriAlgoritmoDigest());
				//FIN CRUBENCVS 47084
				
				log.debug("Msg Firmado:" + msgFirmado);
				firmado = XMLUtils.parseXml(msgFirmado);
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
		LogAplicacion log;
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
