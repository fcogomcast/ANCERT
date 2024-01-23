package es.tributasenasturias.servicios.ibi.deuda.impresion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.servicios.documentos.WSDocumentos;
import es.tributasenasturias.servicios.documentos.WSDocumentos_Service;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.ImpresionException;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.PreferenciasFactory;
import es.tributasenasturias.servicios.ibi.deuda.soap.SoapClientHandler;

/**
 * Permite acceder a una reimpresi�n de documento.
 * @author crubencvs
 *
 */
public class ReimpresionDocumento implements IContextReader{

	CallContext context;
	/**
	 * Constructor, no deber�a utilizarse.
	 */
	protected ReimpresionDocumento()
	{
		
	}
	/**
	 * Constructor con contexto, deber�a utilizarse este.
	 * @param context
	 */
	public ReimpresionDocumento(CallContext context)
	{
		this.context= context;
	}
	/**
	 * Recupera una reimpresi�n de documento, pudiendo a�adir adem�s un c�digo de verificaci�n.
	 * @param elemento Identificador de elemento que permite localizar una reimpresi�n.
	 * @param tipoElemento Tipo de elemento que permite localizar una reimpresi�n.
	 * @param codigoVerificacion C�digo de verificaci�n a imprimir en cada p�gina de documento, o null si no se desea imprimir.
	 * @return Cadena en base64 conteniendo el documento reimpreso.
	 * @throws ImpresionException
	 */
	@SuppressWarnings("unchecked")
	public String getReimpresion (String elemento, String tipoElemento, String codigoVerificacion) throws ImpresionException
	{
		try
		{
			String doc=null;
			WSDocumentos_Service srv = new WSDocumentos_Service();
			WSDocumentos port = srv.getWSDocumentosPort();
			Preferencias pref = PreferenciasFactory.getPreferenciasContexto(context);
			String idSesion = (String) context.get(CallContextConstants.IDSESION);
			//Se modifica el endpoint y se enlaza con el log de cliente.
			BindingProvider bp = (BindingProvider) port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pref.getEndpointImpresion());
			List<Handler> handlers = bp.getBinding().getHandlerChain();
			if (handlers==null)
			{
				handlers = new ArrayList<Handler>();
			}
			handlers.add(new SoapClientHandler(idSesion));
			bp.getBinding().setHandlerChain(handlers);
			doc=port.obtenerReimprimible(elemento, tipoElemento, codigoVerificacion);
			return doc;
		}
		catch (Exception e)
		{
			throw new ImpresionException ("Error al recuperar la reimpresi�n de documento:" + e.getMessage(),e);
		}
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
