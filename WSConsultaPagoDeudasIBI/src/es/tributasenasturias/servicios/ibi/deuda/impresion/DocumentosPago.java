package es.tributasenasturias.servicios.ibi.deuda.impresion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.services.lanzador.client.LanzadorException;
import es.tributasenasturias.services.lanzador.client.LanzadorFactory;
import es.tributasenasturias.services.lanzador.client.ParamType;
import es.tributasenasturias.services.lanzador.client.ProcedimientoAlmacenado;
import es.tributasenasturias.services.lanzador.client.TLanzador;
import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.ImpresionException;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.PreferenciasFactory;
import es.tributasenasturias.servicios.ibi.deuda.soap.SoapClientHandler;
import es.tributasenasturias.webservice.DocumentoPago;
import es.tributasenasturias.webservice.DocumentoPagoService;

/**
 * Clase para controlar la impresión del documento de pago
 * @author crubencvs
 *
 */
public class DocumentosPago implements IContextReader{

	/* (non-Javadoc)
	 * @see es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader#getCallContext()
	 */
	@Override
	public CallContext getCallContext() {
		return context;
	}
	/* (non-Javadoc)
	 * @see es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader#setCallContext(es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext)
	 */
	@Override
	public void setCallContext(CallContext ctx) {
		this.context = ctx;
		
	}
	CallContext context;
	protected DocumentosPago(CallContext context)
	{
		this.context= context;
	}
	
	/**
	 * Recupera el justificante de pago asociado a la referencia de grupo que se ha utilizado para
	 * pagar.
	 * @param referencia Número de la referencia de grupo
	 * @return Documento de justificante de pago en Base64
	 * @throws ImpresionException
	 */
	@SuppressWarnings("unchecked")
	public String getJustificantePago (String referencia) throws ImpresionException
	{
		try
		{
			String doc=null;
			DocumentoPagoService srv = new DocumentoPagoService();
			DocumentoPago port = srv.getDocumentoPagoPort();
			Preferencias pref = PreferenciasFactory.getPreferenciasContexto(context);
			String idSesion = (String) context.get(CallContextConstants.IDSESION);
			//Se modifica el endpoint y se enlaza con el log de cliente.
			BindingProvider bp = (BindingProvider) port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pref.getEndpointDocumentosPago());
			List<Handler> handlers = bp.getBinding().getHandlerChain();
			if (handlers==null)
			{
				handlers = new ArrayList<Handler>();
			}
			handlers.add(new SoapClientHandler(idSesion));
			bp.getBinding().setHandlerChain(handlers);
			doc=port.justificantePagoReferencia(referencia, true); //Se da de alta el documento siempre.
			if (doc==null)
			{
				throw new ImpresionException ("No se ha podido recuperar el justificante de pago.");
			}
			return doc;
		}
		catch (ImpresionException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new ImpresionException ("Error al recuperar el justificante de pago:" + e.getMessage(),e);
		}
	}
	/**
	 * Recupera el código de verificación con el que se ha grabado el justificante de pago
	 * Si no existe aún justificante de pago grabado, devuelve cadena vacía
	 * @param referencia Referencia de emisión para la que buscar justificante de pago
	 * @return Código de verificación o cadena vacía
	 * @throws ImpresionException
	 */
	public String getCodVerificacionJustPago (String referencia) throws ImpresionException
	{
		RespuestaLanzador respuesta;
		TLanzador lanzador;
		try {
			Preferencias pref = PreferenciasFactory.getPreferenciasContexto(context);
			String idSesion = (String) context.get(CallContextConstants.IDSESION);
			lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(),new SoapClientHandler(idSesion));
			ProcedimientoAlmacenado pa= new ProcedimientoAlmacenado(pref.getProcCodVerifJustPago(),pref.getEsquemaBD());
			pa.param(referencia, ParamType.CADENA);
			pa.param("P", ParamType.CADENA);
			String resp=lanzador.ejecutar(pa);
			respuesta=new RespuestaLanzador(resp); 
			if (respuesta.esErronea())
			{
				throw new ImpresionException ("La ejecución del procedimiento almacenado de recuperación del código de verificación ha fallado:" + respuesta.getTextoError());
			}
			String cod = respuesta.getValue("CADE_CADENA", 1, "STRING_CADE");
			return cod;
		} catch (LanzadorException e) {
			throw new ImpresionException ("Error en la comunicación con base de datos al recuperar el código de verificación de justificante:" + e.getMessage());
		}
	}
}
