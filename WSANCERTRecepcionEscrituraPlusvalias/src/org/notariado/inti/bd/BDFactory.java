package org.notariado.inti.bd;


import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.utils.log.Logger;



/**
 * Permite construir objetos de acceso a datos.
 * @author crubencvs
 *
 */
public class BDFactory {

	/**
	 * Recupera un objeto que permite realizar llamadas a base de datos
	 * @param preferencias Preferencias del servicio. El objeto leerá el endpoint de lanzador, el esquema y los nombres de procedimiento de aquí
	 * @param log Logger, por si necesita hacer log
	 * @param idSesion Identificador de la llamada actual.
	 * @return {@link GestorLlamadasBD}
	 */
	public static GestorLlamadasBD newGestorLlamadasBD(Preferencias preferencias, Logger log,String idSesion) 
	{
		return new GestorLlamadasBD(preferencias, log, idSesion);
	}

	
}
