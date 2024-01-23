package org.notariado.inti.mensajes;

import org.notariado.inti.exceptions.MensajesException;



/**
 * Permite construir objetos de manejo de mensajes.
 * @author crubencvs
 *
 */
public class MensajesFactory {

	/**
	 * Recupera un objeto que permite obtener los mensajes de aplicación.
	 * @param ficheroMensajes Fichero de donde obtener los mensajes de aplicación.
	 * @return Objeto de manejo {@link MensajesUtil}
	 * @throws MensajesException
	 */
	public static MensajesUtil newMensajesAplicacion(String ficheroMensajes) throws MensajesException
	{
		return new MensajesUtil(ficheroMensajes);
	}

    /**
     * Devuelve un objeto que permite construir los mensajes de respuesta
     * @param pref Preferencias
     * @param mensajes Mensajes
     * @return
     */
	public static ConstructorMensajeRespuestaConsulta newConstructorMensajeRespuestaConsulta (String receptorSalida, String emisorSalida, MensajesUtil mensajes)
	{
		return new ConstructorMensajeRespuestaConsulta(receptorSalida, emisorSalida, mensajes);
	}
	
}
