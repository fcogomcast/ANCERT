package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion;


public class ComunicacionFactory {
	/**
	 * Devuelve un objeto constructor de resultados ( ResultadoType )
	 * @return Objeto ConstructorResultado
	 */
	public static ConstructorResultado newConstructorResultado()
	{
			ConstructorResultado obj = new ConstructorResultado();
			return obj;
	}
	/**
	 * Devuelve un objeto constructor de mensajes de respuesta ( CreadorMensajeSalidaServicio )
	 * @return Objeto CreadorMensajeSalidaServicio
	 */
	public static CreadorMensajeSalidaServicio newCreadorMensajeSalida()
	{
			CreadorMensajeSalidaServicio obj = new CreadorMensajeSalidaServicio();
			return obj;
	}
}
