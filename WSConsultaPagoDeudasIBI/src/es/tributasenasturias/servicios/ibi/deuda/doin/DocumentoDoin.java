package es.tributasenasturias.servicios.ibi.deuda.doin;


import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;



import es.tributasenasturias.servicios.doin.WSConsultaDoinDocumentos;
import es.tributasenasturias.servicios.doin.WSConsultaDoinDocumentos_Service;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.PreferenciasFactory;
import es.tributasenasturias.servicios.ibi.deuda.soap.SoapClientHandler;

/**
 * Permite la gestión de documentos de DOIN_DOCUMENTOS_INTERNET
 * @author crubencvs
 *
 */
public class DocumentoDoin implements IContextReader{
	CallContext context;
	private String documento;
	
	/**
	 * @return the documento
	 */
	public String getDocumento() {
		return documento;
	}

	/**
	 * @param documento the documento to set
	 */
	public void setDocumento(String documento) {
		this.documento = documento;
	}

	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context= ctx;
	}

	@SuppressWarnings("unused")
	private DocumentoDoin()
	{
		
	}
	protected DocumentoDoin(CallContext context)
	{
		this.context=context;
	}
	@SuppressWarnings("unchecked")
	public String altaDocumento (String nombreDoin, String tipo, String codigoVerificacion, String nifSujetoPasivo, String nifPresentador, String documento)
	{
		WSConsultaDoinDocumentos_Service srv = new WSConsultaDoinDocumentos_Service();
		WSConsultaDoinDocumentos port = srv.getWSConsultaDoinDocumentosSOAP();
		Preferencias pref = PreferenciasFactory.getPreferenciasContexto(context);
		String idSesion = (String) context.get(CallContextConstants.IDSESION);
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
		return error.value;
	}
	/**
	 * Recupera un documento de la tabla de doin
	 * @param nombre Nombre doin
	 * @param tipo Tipo de documento
	 * @param descomprimir Si se debe descomprimir
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getDocumentoDoin(String nombre, String tipo, boolean descomprimir)
	{
		WSConsultaDoinDocumentos_Service srv = new WSConsultaDoinDocumentos_Service();
		WSConsultaDoinDocumentos port = srv.getWSConsultaDoinDocumentosSOAP();
		Preferencias pref = PreferenciasFactory.getPreferenciasContexto(context);
		String idSesion = (String) context.get(CallContextConstants.IDSESION);
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
		port.consultaDoinDocumento(nombre, tipo, descomprimir, doc, error, resultado);
		this.documento= doc.value;
		return error.value;
	}
	
	

}
