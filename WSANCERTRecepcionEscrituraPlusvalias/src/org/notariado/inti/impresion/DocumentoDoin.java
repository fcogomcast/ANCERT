package org.notariado.inti.impresion;


import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.soap.SoapClientHandler;



import es.tributasenasturias.servicios.doin.WSConsultaDoinDocumentos;
import es.tributasenasturias.servicios.doin.WSConsultaDoinDocumentos_Service;

/**
 * Permite la gestión de documentos de DOIN_DOCUMENTOS_INTERNET
 * @author crubencvs
 *
 */
public class DocumentoDoin{

	Preferencias pref;
	String idSesion;
	@SuppressWarnings("unused")
	private DocumentoDoin()
	{
		
	}
	protected DocumentoDoin(Preferencias pref, String idSesion)
	{
		this.pref = pref;
		this.idSesion = idSesion;
	}
	@SuppressWarnings("unchecked")
	public String altaDocumento (String nombreDoin, String tipo, String codigoVerificacion, String nifSujetoPasivo, String nifPresentador, String documento)
	{
		WSConsultaDoinDocumentos_Service srv = new WSConsultaDoinDocumentos_Service();
		WSConsultaDoinDocumentos port = srv.getWSConsultaDoinDocumentosSOAP();
		BindingProvider bp = (BindingProvider) port;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pref.getEndpointDocumentos());
		List<Handler> handlerList = bp.getBinding().getHandlerChain();
		if (handlerList==null) // puede que esto no sea posible
		{
			handlerList = new ArrayList<Handler>();
		}
		//Añadimos el manejador.
		handlerList.add(new SoapClientHandler(idSesion));
		bp.getBinding().setHandlerChain(handlerList);
		Holder<String>doc = new Holder<String>();
		Holder<String>error = new Holder<String>(); //Aquí recibimos el resultado.
		Holder<String>resultado = new Holder<String>();
		port.altaDoinDocumento(nombreDoin, tipo, codigoVerificacion, nifSujetoPasivo, nifPresentador, documento, "PDF", true, doc, error, resultado);
		//Devolvemos el resultado.
		return error.value;
	}
	

}
