package org.notariado.inti.impresion;

import org.notariado.inti.preferencias.Preferencias;



/**
 * Genera objetos utilizados para la impresión de documentos.
 * @author crubencvs
 *
 */
public class ImpresionFactory {

	/**
	 * Recupera un objeto que permite la reimpresión de documentos.
	 * @param pref Preferencias de servicio
	 * @param idSesion Identificador de sesión del servicio.
	 * @return
	 */
	public static ReimpresionDocumento getReimpresion(Preferencias pref, String idSesion)
	{
		return new ReimpresionDocumento(pref, idSesion);
	}
	/**
	 * Recupera un objeto que permite gestionar los documentos almacenados en DOIN.
	 * @param pref Preferencias del servicio
	 * @param idSesion Identificador de sesión del servicio.
	 * @return
	 */
	public static DocumentoDoin getGestorDoin(Preferencias pref, String idSesion)
	{
		return new DocumentoDoin(pref, idSesion);
	}
}
