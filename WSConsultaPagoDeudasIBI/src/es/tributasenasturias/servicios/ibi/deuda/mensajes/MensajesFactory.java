package es.tributasenasturias.servicios.ibi.deuda.mensajes;

import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.MensajesException;



/**
 * Permite construir objetos de manejo de mensajes.
 * @author crubencvs
 *
 */
public class MensajesFactory {

	/**
	 * Recupera un objeto que permite la recuperaci�n de mensajes de aplicaci�n, ya sea mediante un c�digo interno
	 * o un c�digo devuelto por una aplicaci�n externa.
	 * @param ficheroMensajes Fichero de mensajes de la aplicaci�n.
	 * @param ficheroMapeoMensajes Fichero de mapeo de Mensajes de aplicaci�n.
	 * @return Objeto que permite recuperar los mensajes.
	 * @throws MensajesException
	 */
	public static MensajesAplicacion newMensajesAplicacion(String ficheroMensajes, String ficheroMapeoMensajes) throws MensajesException
	{
		return new MensajesAplicacion(ficheroMensajes, ficheroMapeoMensajes);
	}
	/**
	 * Devuelve un objeto que permite construir la salida del servicio.
	 * @return
	 */
	public static ConstructorMensajeRespuestaConsulta newConstructorMensajeRespuestaConsulta ()
	{
		return new ConstructorMensajeRespuestaConsulta();
	}
	/**
	 * Devuelve un objeto que permite construir la salida del servicio.
	 * @return
	 */
	public static ConstructorMensajeRespuestaPago newConstructorMensajeRespuestaPago ()
	{
		return new ConstructorMensajeRespuestaPago();
	}
	/**
	 * Devuelve el manejador de mensajes de retorno de la aplicaci�n tom�ndolo de contexto de llamada.
	 * @param context Contexto de llamada.
	 * @return
	 * @throws MensajesException
	 */
	public static MensajesAplicacion getMensajesAplicacionFromContext(CallContext context) throws MensajesException
	{
		if (context==null)
		{
			throw new MensajesException ("No existe contexto de llamada de donde recuperar los mensajes.");
		}
		MensajesAplicacion men = (MensajesAplicacion)context.get(CallContextConstants.MENSAJES);
		if (men==null)
		{
			throw new MensajesException ("No existe lista de mensajes adminidos en el contexto de llamada. Compruebe las preferencias.");
		}
		return men;
	}
}
