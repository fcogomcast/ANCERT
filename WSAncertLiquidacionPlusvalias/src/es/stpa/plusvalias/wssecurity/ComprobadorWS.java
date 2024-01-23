package es.stpa.plusvalias.wssecurity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import es.stpa.plusvalias.preferencias.Preferencias;
import es.tributasenasturias.seguridadws.Seguridad;
import es.tributasenasturias.seguridadws.Seguridad_Service;

public class ComprobadorWS{

	
	private Seguridad port;
	Preferencias pref;
	@SuppressWarnings("unchecked")
	protected ComprobadorWS(Preferencias pref,SOAPHandler<SOAPMessageContext> manejador) {
		this.pref=pref;
		String endPoint=pref.getEndpointSeguridad();
		Seguridad_Service servicio;
		servicio=new Seguridad_Service();
		port=servicio.getSeguridadSOAP();
		BindingProvider bpr= (BindingProvider)port;
		if (!"".equals(endPoint))
		{
			bpr.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPoint);
		}
		if (manejador!=null)
		{
			Binding bi = bpr.getBinding();
			List<Handler> manejadores=(List<Handler>)bi.getHandlerChain();
			if (manejadores==null)
			{
				manejadores= new ArrayList<Handler>();
			}
			manejadores.add(manejador);
			bi.setHandlerChain(manejadores);
		}
	}
	/**
	 * Método que utiliza operaciones del servicio Web de seguridad según WS-Security
	 * para validar la firma de un mensaje sin validar que el certificado que ha firmado
	 * ese mensaje sea uno reconocido.
	 * @param soapMsg Mensaje SOAP con seguridad a validar.
	 * @return true si es válido, false si no.
	 */
	public boolean validaFirmaSinCertificado(String soapMsg) throws WSSecurityException
	{
		boolean valido=false;
		try
		{
			Holder<Boolean> esError=new Holder<Boolean>();
			Holder<String>codigo = new Holder<String>();
			Holder<String>mensaje= new Holder<String>();
			port.validacionFirmaSinCertificado(soapMsg, esError, codigo, mensaje);
			valido=!esError.value;
		}
		catch (Exception ex)
		{
			throw new WSSecurityException ("Error en validación WS: "+ ex.getMessage(),ex);
		}
		return valido;
	}
	/**
	 * Firma un mensaje SOAP con Signature WS-Security.
	 * @param soapMsg Mensaje SOAP a firmar
	 * @return Mensaje soap firmado o el original si no se ha podido firmar. Esto se hace así
	 * porque si el receptor del mensaje necesita firma, lo rechazará, y si no, procesará correctamente
	 * el mensaje.
	 */
	public String firmaMensaje (String soapMsg) throws WSSecurityException
	{
		try
		{
			return port.firmarMensaje(soapMsg, pref.getAliasFirma());
		}
		catch (Exception ex)
		{
			throw new WSSecurityException ("Error firmando WS:" + ex.getMessage(),ex);
		}
	}
	
	/**
	 * Firma un mensaje SOAP con Signature WS-Security.
	 * @param soapMsg Mensaje SOAP a firmar
	 * @param intervaloSegundosExpiracion segundos de validez de la firma del mensaje desde la creación de la cabecera de seguridad
	 * @return Mensaje soap firmado o el original si no se ha podido firmar. Esto se hace así
	 * porque si el receptor del mensaje necesita firma, lo rechazará, y si no, procesará correctamente
	 * el mensaje.
	 */
	public String firmaMensajeTimestamp (String soapMsg, int intervaloSegundosExpiracion) throws WSSecurityException
	{
		try
		{
			//CRUBENCVS 47084  20/02/2023
			// Se utiliza la versión que permite especificar el algoritmo de firma
			//return port.firmarMensajeConTimestamp(soapMsg, pref.getAliasFirma(), intervaloSegundosExpiracion);
			return port.firmarMensajeAlgoritmoConTimestamp(soapMsg, pref.getAliasFirma(), pref.getUriAlgoritmoFirma(), pref.getUriAlgoritmoDigest(),intervaloSegundosExpiracion);
			// FIN  CRUBENCVS 47084
		}
		catch (Exception ex)
		{
			throw new WSSecurityException ("Error firmando WS:" + ex.getMessage(),ex);
		}
		
	}
}
