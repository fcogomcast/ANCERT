package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion;

import es.tributasenasturias.services.ancert.enviodiligencias.EnvioDiligenciasMessageInType;
import es.tributasenasturias.services.ancert.enviodiligencias.EnvioDiligenciasMessageOut;
import es.tributasenasturias.services.ancert.enviodiligencias.ObjectFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.ResultadoType;

public class CreadorMensajeSalidaServicio {
	/**
	 * Crea un mensaje de salida para el servicio.
	 * @param parameters Parámetros de entrada del servicio.
	 * @return objeto {@link EnvioDiligenciasMessageOut}
	 */
	public EnvioDiligenciasMessageOut crearMensajeSalidaServicio(EnvioDiligenciasMessageInType parameters,ResultadoType resul) {
		ObjectFactory fact = new ObjectFactory();
		EnvioDiligenciasMessageOut out= fact.createEnvioDiligenciasMessageOut();
		out.setResultado(resul);
		out.setPeticion(parameters);
		return out;
	}
	/**
	 * Crea un mensaje de retorno indicando que el servicio ha termindo Ok
	 * @param parameters Parámetros de entrada al servicio <b>EnvioDiligenciasMessageInType</b>
	 * @return EnvioDiligenciasMessageOut
	 */
	public EnvioDiligenciasMessageOut creaMensajeOk(EnvioDiligenciasMessageInType parameters)
	{
		return crearMensajeSalidaServicio(parameters,ComunicacionFactory.newConstructorResultado().crearResultadoOk());
	}
	/**
	 * Crea un mensaje de retorno indicando que la autoliquidación está vacía.
	 * @param parameters Parámetros de entrada al servicio <b>EnvioDiligenciasMessageInType</b>
	 * @return EnvioDiligenciasMessageOut
	 */
	public EnvioDiligenciasMessageOut creaMensajeAutoliquidacionVacia(EnvioDiligenciasMessageInType parameters)
	{
		return crearMensajeSalidaServicio(parameters,ComunicacionFactory.newConstructorResultado().crearResultadoAutoliquidacionVacia());
	}
	/**
	 * Crea un mensaje de retorno indicando que la autoliquidación no existe.
	 * @param parameters Parámetros de entrada al servicio <b>EnvioDiligenciasMessageInType</b>
	 * @return EnvioDiligenciasMessageOut
	 */
	public EnvioDiligenciasMessageOut creaMensajeAutoliquidacionNoExiste(EnvioDiligenciasMessageInType parameters)
	{
		return crearMensajeSalidaServicio(parameters,ComunicacionFactory.newConstructorResultado().crearResultadoAutoliquidacionNoExiste());
	}
	
	/**
	 * Crea un mensaje de retorno indicando que ha habido un error técnico
	 * @param parameters Parámetros de entrada al servicio <b>EnvioDiligenciasMessageInType</b>
	 * @return EnvioDiligenciasMessageOut
	 */
	public EnvioDiligenciasMessageOut creaMensajeErrorTecnico(EnvioDiligenciasMessageInType parameters)
	{
		return crearMensajeSalidaServicio(parameters,ComunicacionFactory.newConstructorResultado().crearResultadoErrorTecnico());
	}
	/**
	 * Crea un mensaje de retorno indicando que el mensaje recibido de CGN no cumple la especificación de seguridad.
	 * @param parameters Parámetros de entrada al servicio <b>EnvioDiligenciasMessageInType</b>
	 * @return EnvioDiligenciasMessageOut
	 */
	public EnvioDiligenciasMessageOut creaMensajeErrorSeguridad(EnvioDiligenciasMessageInType parameters)
	{
		return crearMensajeSalidaServicio(parameters,ComunicacionFactory.newConstructorResultado().crearResultadoErrorSeguridad());
	}
	/**
	 * Crea un mensaje de retorno indicando que ha habido un error grave
	 * @param parameters Parámetros de entrada al servicio <b>EnvioDiligenciasMessageInType</b>
	 * @return EnvioDiligenciasMessageOut
	 */
	public EnvioDiligenciasMessageOut creaMensajeErrorGrave(EnvioDiligenciasMessageInType parameters)
	{
		return crearMensajeSalidaServicio(parameters,ComunicacionFactory.newConstructorResultado().crearResultadoErrorInesperado());
	}
	/**
	 * Crea un mensaje de retorno indicando que la solicitud no está autorizada para envío.
	 * @param parameters Parámetros de entrada al servicio <b>EnvioDiligenciasMessageInType</b>
	 * @return EnvioDiligenciasMessageOut
	 */
	public EnvioDiligenciasMessageOut creaMensajeNoAutorizadoEnvio(EnvioDiligenciasMessageInType parameters)
	{
		return crearMensajeSalidaServicio(parameters,ComunicacionFactory.newConstructorResultado().crearResultadoNoAutorizadaEnvio());
	}
}
