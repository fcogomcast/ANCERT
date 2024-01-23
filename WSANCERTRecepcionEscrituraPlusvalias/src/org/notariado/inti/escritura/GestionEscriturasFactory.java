/**
 * 
 */
package org.notariado.inti.escritura;

import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.utils.log.Logger;





/** Mediante esta clase pueden generarse instancias que permitan la inserción de escrituras en base de datos.
 * @author crubencvs
 *
 */
public class GestionEscriturasFactory {
	/**
	 * Devuelve un objeto que permite realizar la inserción de escritursa
	 * @param pref {@link Preferencias} de este servicio.
	 * @param idSesion Identificador de sesión que instancia este objeto.
	 * @return {@link InsertadorEscritura} que permite la inserción en nuestros sistemas de una escritura.
	 */
	public static  InsertadorEscritura newInsertadorEscritura (Preferencias pref, String idSesion)
	{
		return new InsertadorEscritura (pref, idSesion);
		
	}
	/**
	 * Devuelve un objeto que permite imprimir un justificante de presentación
	 * @param pref Preferencias del servicio
	 * @param log Objeto de log del servicio
	 * @param idSesion Identificador de sesión que instancia este objeto
	 * @return JustificantePresentacion que permite generar el justificante.
	 */
	public static JustificantePresentacion newJustificantePresentacion(Preferencias pref, Logger log, String idSesion)
	{
		return new JustificantePresentacion (pref, log, idSesion);
	}
}
