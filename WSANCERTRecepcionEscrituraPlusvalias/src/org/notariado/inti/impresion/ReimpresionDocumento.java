package org.notariado.inti.impresion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.notariado.inti.exceptions.ImpresionException;
import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.soap.SoapClientHandler;

import es.tributasenasturias.servicios.documentos.WSDocumentos;
import es.tributasenasturias.servicios.documentos.WSDocumentos_Service;


/**
 * Permite acceder a una reimpresión de documento.
 * @author crubencvs
 *
 */
public class ReimpresionDocumento{

	Preferencias pref;
	String idSesion;
	/**
	 * Constructor, no debería utilizarse.
	 */
	protected ReimpresionDocumento()
	{
		
	}
	/**
	 * Constructor con contexto, debería utilizarse este.
	 * @param context
	 */
	public ReimpresionDocumento(Preferencias pref, String idSesion)
	{
		this.pref= pref;
		this.idSesion = idSesion;
	}
	/**
	 * Recupera una reimpresión de documento, pudiendo añadir además un código de verificación
	 * @param elemento Identificador de elemento que permite localizar una reimpresión.
	 * @param tipoElemento Tipo de elemento que permite localizar una reimpresión.
	 * @param codigoVerificacion Código de verificación a imprimir en cada página de documento, o null si no se desea imprimir.
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
			throw new ImpresionException ("Error al recuperar la reimpresión de documento:" + e.getMessage(),e);
		}
	}
}
