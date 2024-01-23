/**
 * 
 */
package es.tributasenasturias.seguridad.servicio;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.services.firmaDigital.Exception_Exception;
import es.tributasenasturias.services.firmaDigital.FirmaDigital;
import es.tributasenasturias.services.firmaDigital.WsFirmaDigital;

/**Clase de utilidad para realizar firma digital de XML y validación de firma.
 * Se utiliza la función de firma que permite el proceso de cualquier XML, dados unos parámetros
 * que indique qué se firma.
 * @author crubencvs
 *
 */
public class FirmaHelper {
	
	WsFirmaDigital srv;
	FirmaDigital port;
	
	@SuppressWarnings("unused")
	private FirmaHelper(){};
	/**
	 * Construye un objeto {@link FirmaHelper} que enviará sus peticiones de firma a un endpoint concreto.
	 * @param endpoint Endpoint del servicio de firma Digital.
	 */
	protected FirmaHelper (String endpoint)
	{
		srv = instanciarServicio();
		port = srv.getServicesPort();
		BindingProvider bpr=(BindingProvider) port;
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpoint);
	}
	/**
	 * Construye un objeto {@link FirmaHelper} que enviará sus peticiones  de firma a un endpoint concreto,
	 * y cada uno de sus mensajes intercambiados se procesará con el manejador de mensajes SOAP
	 * {@link SOAPHandler} especificado.
	 * @param endpoint Endpoint del servicio de firma Digital
	 * @param soapHandler {@link SOAPHandler} que procesará los mensajes SOAP intercambiados.
	 */
	protected FirmaHelper (String endpoint, SOAPHandler<SOAPMessageContext> soapHandler)
	{
		this (endpoint);
		changeHandler (soapHandler);
	}
	/**
	 * Modifica de forma dinámica la localización del WSDL
	 * @return Objeto servicio {@link WsFirmaDigital} que permite recuperar las operaciones de la firma digital.
	 */
	private WsFirmaDigital instanciarServicio()
	{
		//Se modifica de forma dinámica la localización del WSDL, que debe estar en el
		//jar donde se define WsFirmaDigital, y debe estar situado en 
		//META-INF/wsdls/PXFirmaDigital.
		//Esto no es peor que lo que genera el cliente por defecto, que también lo mete,
		//pero al menos esto funciona bien.
		//Desactivado, sólo tiene sentido si se va a utilizar en una librería jar.
		//WebServiceClient ann = WsFirmaDigital.class.getAnnotation(WebServiceClient.class);
		//URL urlWsdl = WsFirmaDigital.class.getResource("/META-INF/wsdls/PXFirmaDigital.wsdl");
		//return new WsFirmaDigital(urlWsdl,new QName(ann.targetNamespace(), ann.name()));
		return new WsFirmaDigital();
	}
	
	/**
	 * Firma un XML pasado, indicando qué nodo se firmará y dónde se dejará el mensaje.
	 * @param xmlData Cadena conteniendo el XML a firmar
	 * @param aliasCertificado Cadena conteniendo el alias de certificado con el que firmar el XML
	 * @param idNodoAFirmar Cadena del identificador del nodo a firmar.  Debe tener un atributo "ID" con este valor
	 * @param nodoPadre Cadena con el nombre del nodo padre sin cualificar, donde se dejará el nodo de firma
	 * @param nsNodoPadre Cadena que contiene el namespace del nodo padre. Si no tuviera, se puede pasar vacío.
	 * @return Cadena con el XML firmado.
	 * @throws SeguridadException Si se produce una excepción en la firma.
	 */
	public String firmaXML (String xmlData,
							String aliasCertificado, 
							String idNodoAFirmar, 
							String nodoPadre, 
							String nsNodoPadre,
							String uriAlgoritmoFirma,
							String uriAlgoritmoDigest) throws SeguridadException
	{
		String resultado;
		try {
			//CRUBENCVS 47084. 06/02/2023. Se utiliza la nueva firma que permite SHA2
			//La validación también acepta firmas SHA2, pero no cambia la llamada.
			//resultado = port.firmarXML(xmlData, aliasCertificado, , nodoPadre, nsNodoPadre);
			resultado = port.firmarXMLAlgoritmo(xmlData, aliasCertificado , idNodoAFirmar, nodoPadre, nsNodoPadre, uriAlgoritmoFirma, uriAlgoritmoDigest);
			//FIN CRUBENCVS 47084
			
		} catch (Exception e) {
			throw new SeguridadException ("Error al firmar el xml:" + e.getMessage(),e);
		}
		return resultado;
	}
	/**
	 * Indica si un XML firmado es o no válido.
	 * @param xmlFirmado Cadena que contiene el XML firmado.
	 * @return boolean true si es válido, o boolean false si no.
	 * @throws SeguridadException Si se produce una excepción durante la validación.
	 */
	public boolean esValido(String xmlFirmado) throws SeguridadException
	{
		try {
			return port.validar(xmlFirmado);
		} catch (Exception_Exception e) {
			throw new SeguridadException ("Error al validar el xml:" + e.getMessage(),e);
		}
	}
	/**
	 * Permite cambiar el manejador de mensajes SOAP que intercambiará el objeto con el servicio web
	 * de firma digital.
	 * @param manejador Objeto {@link SOAPHandler} que procesará los mensajes intercambiados con el 
	 * servicio web.
	 */
	@SuppressWarnings("unchecked")
	private void changeHandler(SOAPHandler<SOAPMessageContext> manejador)
	{
		javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port; 
		if (manejador!=null)
		{
			Binding bi = bpr.getBinding();
			List <Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			{
			   handlerList = new ArrayList<Handler>();
			}
			handlerList.add(manejador);
			bi.setHandlerChain(handlerList);
		}	
	}
}
