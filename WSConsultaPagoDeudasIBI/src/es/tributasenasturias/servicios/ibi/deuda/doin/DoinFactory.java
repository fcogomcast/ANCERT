package es.tributasenasturias.servicios.ibi.deuda.doin;

import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;

/**
 * Permite crear los objetos necesarios para la gesti�n de documentos en DOIN_DOCUMENTOS_INTERNET
 * @author crubencvs
 *
 */
public class DoinFactory {
	/**
	 * Devuelve un objeto de gesti�n de documentos en DOIN.
	 * @param context
	 * @return
	 */
	public static DocumentoDoin newDocumentoDoin(CallContext context)
	{
		return new DocumentoDoin(context);
	}
}
