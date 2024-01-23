package es.tributasenasturias.servicios.ibi.deuda.mensajes;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import es.tributasenasturias.servicios.ibi.deuda.exceptions.MensajesException;
import es.tributasenasturias.utils.mensajes.Mensajes;
import es.tributasenasturias.utils.mensajes.Mensajes.Mensaje;
import es.tributasenasturias.utils.mensajes.mapeo.MapeoMensajes;
import es.tributasenasturias.utils.mensajes.mapeo.MapeoMensajes.Seccion;

/**
 * Gestiona los mensajes de respuesta de aplicación.
 * No es la implementación más eficiente, pero al ser el número de mensajes relativamente pequeño 
 * no se nota un descenso en la velocidad.
 * @author crubencvs
 *
 */
public class MensajesAplicacion {

	Mensajes mensajes;
	MapeoMensajes mapeoMensajes;
	
	public static class MensajeAplicacion
	{
		private String codMensaje;
		private String textoMensaje;
		private boolean error;
		private boolean interno;
		private boolean externo;
		
		/**
		 * Constructor por defecto, error por defecto.
		 */
		private MensajeAplicacion()
		{
			codMensaje = "9999";
			textoMensaje="Se ha producido un error inesperado en la ejecución del servicio.";
			error=true;
		}
		/**
		 * Constructor a partir de un mensaje recuperado del fichero de mensajes.
		 * @param mensaje
		 */
		private MensajeAplicacion (Mensaje mensaje)
		{
			if (mensaje!=null)
			{
				codMensaje = mensaje.getCodigo();
				textoMensaje = mensaje.getTexto();
				error = "S".equals(mensaje.getEsError())?true:false;
				interno = "S".equals(mensaje.getInterno())?true:false;
				externo = "S".equals(mensaje.getExterno())?true:false;
			}
		}

		/** Recupera el código de mensaje. */
		public String getCodMensaje() {
			return codMensaje;
		}
		/** Recupera el texto de mensaje */
		public String getTextoMensaje() {
			return textoMensaje;
		}
		/** Recupera si se trata de un error */
		public boolean isError() {
			return error;
		}
		/** Recupera si se trata de un error interno a la aplicación */
		public boolean isInterno() {
			return interno;
		}
		/** Recupera si se trata de un error de interés a los clientes del servicio */
		public boolean isExterno() {
			return externo;
		}
		
		
	}
	/**
	 * Constructor, no debería utilizarse.
	 */
	@SuppressWarnings("unused")
	private MensajesAplicacion()
	{
		
	}
	/**
	 * Crea e inicializa las estructuras internas de un nuevo objeto que permitirá recuperar mensajes
	 * de la aplicación de los ficheros de configuración creados a tal efecto.
	 * @param ficheroMensajes Ruta y nombre del fichero de mensajes de la aplicación.
	 * @param ficheroMapeo Ruta y nombre del fichero de mapeo de mensajes entre mensajes externos a la 
	 * aplicación y mensajes internos.
	 * @throws MensajesException
	 */
	protected MensajesAplicacion(String ficheroMensajes, String ficheroMapeo) throws MensajesException
	{
		if (ficheroMensajes==null||"".equals(ficheroMensajes))
		{
			throw new MensajesException ("No se ha indicado fichero de mensajes.");
		}
		try {
			JAXBContext jb = JAXBContext.newInstance(Mensajes.class);
			Unmarshaller unmars = jb.createUnmarshaller();
			mensajes=(Mensajes)unmars.unmarshal(new File(ficheroMensajes));
			if (ficheroMapeo!=null && !"".equals(ficheroMapeo))
			{
				//Comprobamos que de todas maneras exista el fichero.
				File f =new File(ficheroMapeo); 
				if (f.exists())
				{
					jb = JAXBContext.newInstance(MapeoMensajes.class);
					unmars = jb.createUnmarshaller();
					mapeoMensajes = (MapeoMensajes) unmars.unmarshal(f);
				}
				else
				{
					mapeoMensajes = new MapeoMensajes();
				}
			}
			else
			{
				mapeoMensajes= new MapeoMensajes(); //Para evitar referencias nulas.
			}
		} catch (JAXBException e) {
			if (e.getLinkedException()!=null) //En ocasiones, JAXB devuelve el error aquí.
			{
				throw new MensajesException ("Error al crear el manejador de mensajes de la aplicación:"+e.getMessage()+":"+e.getLinkedException().getMessage(),e);
			}
			throw new MensajesException ("Error al crear el manejador de mensajes de la aplicación:"+e.getMessage(),e);
		}
	}
	
	
	/**
	 * Recupera un objeto de mensaje interno a partir de su identificador.
	 * @param id Identificador del mensaje interno.
	 * @return Objeto mensaje con los datos del mensaje interno.
	 */
	private Mensaje getMensajeInternoFromId (String id)
	{
		if (id==null) return null;
		Mensaje mensajeInterno=null;
		for (Mensaje interno:mensajes.getMensaje())
		{
			if (id.equals(interno.getId()))
			{
				mensajeInterno = interno;
				break;
			}
		}
		if (mensajeInterno==null)
		{
			mensajeInterno = getMensajePorDefecto();
		}
		return mensajeInterno;
	}
	/**
	 * Recupera un mensaje interno a partir de un mensaje de mapeo (externo)
	 * @param mapeo Mensaje externo (entrada de mapeo)
	 * @return
	 */
	private Mensaje getMensajeInternoFromMapeo (es.tributasenasturias.utils.mensajes.mapeo.MapeoMensajes.Seccion.Mensaje mapeo)
	{
		Mensaje mensajeInterno=null;
		for (Mensaje interno:mensajes.getMensaje())
		{
			if (mapeo.getAplicacion().equals(interno.getId()))
			{
				mensajeInterno = interno;
				break;
			}
		}
		return mensajeInterno;
	}
	/**
	 * Devuelve un objeto con los datos del mensaje de aplicación (datos que podrá utilizar el programa)
	 * a partir de un código de mensaje externo. Se buscará en el mapeo de mensajes y se devolverá el correspondiente,
	 * o el mensaje por defecto si no se encuentra. <b>El mensaje por defecto se considera un error,
	 * porque siempre debería haber un mensaje equivalente</b>
	 * @param codMensajeExterno
	 * @return
	 */
	public MensajeAplicacion getMapeoMensaje (String seccion,String codMensajeExterno)
	{
		MensajeAplicacion men=null;
		Seccion s=null;
		for (Seccion secc:mapeoMensajes.getSeccion())
		{
			if (seccion.equals(secc.getId()))
			{
				s=secc;
				break;
			}
		}
		if (s!=null)
		{
			for (es.tributasenasturias.utils.mensajes.mapeo.MapeoMensajes.Seccion.Mensaje externo:s.getMensaje())
			{
				if (codMensajeExterno.equals(externo.getExterno()))
				{
					men = new MensajeAplicacion(getMensajeInternoFromMapeo(externo));
				}
			}
		}	
		if (men==null)
		{
			men = new MensajeAplicacion(getMensajePorDefecto()); //Mensaje por defecto del fichero, id=default
		}
		if (men==null)
		{
			//Si aún así no hay mensaje, el mensaje de error.
			men= new MensajeAplicacion();
		}
		return men;
	}
	
	public MensajeAplicacion getMensaje (String idMensaje)
	{
		if (idMensaje==null || "".equals(idMensaje))
		{
			return new MensajeAplicacion();
		}
		return new MensajeAplicacion (getMensajeInternoFromId(idMensaje));
	}
	/**
	 * Devuelve el mensaje por defecto del fichero de mensajes. Es el mensaje con id="default"
	 * @return mensaje por defecto.
	 */
	private Mensaje getMensajePorDefecto()
	{
		Mensaje defecto=null;
		defecto = getMensajeInternoFromId("default");
		return defecto;
		
	}
}
