package es.tributasenasturias.services.ancert.solicitudEscritura.seguridad;

import org.w3c.dom.Document;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.PreferenciasException;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.XMLDOMDocumentException;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.XMLDOMUtils;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.firma.FirmaFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.firma.FirmaHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.wssecurity.ComprobadorWS;
import es.tributasenasturias.services.ancert.solicitudEscritura.wssecurity.WSSecurityException;
import es.tributasenasturias.services.ancert.solicitudEscritura.wssecurity.WSSecurityFactory;

/**
 * Validador de firma recibida
 * 
 * 
 */
public class FirmaValidator implements IContextReader{
	
	private CallContext context;
	protected FirmaValidator() {
		
	}
	/**
	 * 
	 * @param datosXML
	 * @return
	 */
	public boolean firmaValida(Document documento) throws SeguridadException{		
		boolean valido = false;
		// Recuperamos el certificado.
		String datosXML;
		LogHelper log= LogFactory.getLogAplicacionContexto(context);
		try
		{
			//Si las preferencias dicen que no se valide la firma, se da por bueno.
			Preferencias pref = PreferenciasFactory.newInstance();
			if (pref.getValidaFirma().equalsIgnoreCase("N"))
			{
				return true;
			}
			//Tenemos que recuperar el contenido de "SERVICIOS_ESCRITURA", que es el nodo contenedor de la firma.
			datosXML=XMLDOMUtils.getXMLText(XMLDOMUtils.selectSingleNode(documento, "//*[local-name()='"+pref.getNodoContenedorFirma()+"']"));
			FirmaHelper firma=FirmaFactory.newFirmaHelper(context);
			log.debug ("Se va a validar la firma digital del documento:" + datosXML);
			valido = firma.validaFirma(datosXML);
		}
		catch (XMLDOMDocumentException ex)
		{
			log.error("Se ha producido un error de XML al validar la firma:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			valido = false;
		}
		catch (PreferenciasException ex)
		{
			log.error("Se ha producido un error de preferencias al validar la firma:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			valido = false;
		}
		log.debug ("El resultado de validación de firma digital es:"+String.valueOf(valido));
		return valido;
	}
	
	public boolean firmaValidaWSSecurity(Document documento) throws SeguridadException{		
		boolean valido = false;
		// Recuperamos el certificado.
		String datosXML;
		LogHelper log= LogFactory.getLogAplicacionContexto(context);
		try
		{
			//Si las preferencias dicen que no se valide la firma, se da por bueno.
			Preferencias pref = PreferenciasFactory.newInstance();
			if (pref.getValidaFirma().equalsIgnoreCase("N"))
			{
				return true;
			}
			//Tenemos que recuperar el contenido de "SERVICIOS_ESCRITURA", que es el nodo contenedor de la firma.
			//datosXML=XMLDOMUtils.getXMLText(XMLDOMUtils.selectSingleNode(documento, "//*[local-name()='"+pref.getNodoContenedorFirma()+"']"));
			datosXML=XMLDOMUtils.getXMLText(documento);
			ComprobadorWS wssecurity= WSSecurityFactory.newConstructorResultado(pref, new SoapClientHandler(log.getSessionId(), pref));
			log.debug ("Se va a validar la firma digital del documento:" + datosXML);
			valido = wssecurity.validaFirmaSinCertificado(datosXML);
		}
		catch (XMLDOMDocumentException ex)
		{
			log.error("Se ha producido un error de XML al validar la firma:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			valido = false;
		}
		catch (PreferenciasException ex)
		{
			log.error("Se ha producido un error de preferencias al validar la firma:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			valido = false;
		}
		catch (WSSecurityException ex)
		{
			log.error("Se ha producido un error de WSSecurity al validar la firma:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			valido = false;
		}
		log.debug ("El resultado de validación de firma digital es:"+String.valueOf(valido));
		return valido;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context =ctx;
	}
}
