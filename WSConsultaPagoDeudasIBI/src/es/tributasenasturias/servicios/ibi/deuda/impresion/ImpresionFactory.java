package es.tributasenasturias.servicios.ibi.deuda.impresion;

import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;

/**
 * Genera objetos utilizados para la impresi�n de documentos.
 * @author crubencvs
 *
 */
public class ImpresionFactory {

	/**
	 * Recupera un objeto que permite la reimpresi�n de documentos.
	 * @param context Contexto de llamada, incluye objetos Preferencias y Log.
	 * @return
	 */
	public static ReimpresionDocumento getReimpresion(CallContext context)
	{
		return new ReimpresionDocumento(context);
	}
	/**
	 * Recupera un objeto que permite obtener documentos de pago
	 * @param context Contexto de llamada, incluye objetos Preferencias y Log.
	 * @return
	 */
	public static DocumentosPago getDocumentosPago(CallContext context)
	{
		return new DocumentosPago(context);
	}
}
