package org.notariado.inti.soap;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
//import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.notariado.inti.contexto.CallContextConstants;
import org.notariado.inti.exceptions.ConstructorRespuestaException;
import org.notariado.inti.exceptions.MensajesException;
import org.notariado.inti.mensajes.ConstructorMensajeRespuestaConsulta;
import org.notariado.inti.mensajes.MensajesFactory;
import org.notariado.inti.mensajes.MensajesUtil;
import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.preferencias.PreferenciasException;
import org.notariado.inti.preferencias.PreferenciasFactory;
import org.notariado.inti.utils.Utils;
import org.notariado.inti.utils.log.LogFactory;
import org.notariado.inti.utils.log.Logger;
import org.notariado.inti.xml.XMLDOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import org.w3c.dom.NodeList;





/**
 * Carga datos en el contexto de servicio que serán utilizados
 * en el resto del servicio.
 * Estos objetos serán el log de aplicación, los mensajes de aplicación, las preferencias, y el id de sesión.
 * 
 * @author crubencvs
 *
 */
public class CargaContexto implements SOAPHandler<SOAPMessageContext> {

	/**
	 * Destruye las variables almacenadas en el contexto. Se hace así
	 * para asegurar que se libera toda la memoria.
	 * Estas variables se almacenan en el contexto del hilo de ejecución, con lo cual
	 * estarían vivas un tiempo indeterminado. De esta forma, las marcamos 
	 * para que el recolector de basura las elimine.
	 * Aunque no se hiciera no sería muy grave, porque nunca habría más de una copia
	 * por hilo, y la siguiente petición que entrara al hilo machacaría estos 
	 * datos.
	 * @param context
	 */
	public static void destruirContextoSesion(SOAPMessageContext context)
	{
		//A la salida, nos cargamos los datos de contexto.
		context.remove(CallContextConstants.IDSESION);
		context.remove(CallContextConstants.PREFERENCIAS);
		context.remove(CallContextConstants.MENSAJES);
		context.remove(CallContextConstants.CONSTRUCTOR_RESPUESTA);
	}
	/**
	 * Recupera el código de receptor del mensaje de entrada para poder utilizarlo después en construcción de mensajes como emisor de respuesta.
	 * @param mensaje Mensaje de entrada
	 * @return Código de receptor
	 * @throws ConstructorRespuestaException Si no puede recuperar el receptor
	 */
	private String getCodigoReceptor(Document mensaje) throws ConstructorRespuestaException
	{
		NodeList nl= XMLDOMUtils.getAllNodesCondicion(mensaje, 
				"//*[local-name()='COMUNICACION']/*[local-name()='REQUEST']/*[local-name()='CABECERA']/*[local-name()='RECEPTOR']");
		if (nl.getLength()==0) //Error, no viene receptor. No podemos duplicarlo.
		{
			throw new ConstructorRespuestaException ("Error al recuperar el receptor del mensaje de entrada para posteriormente devolverlo en la salida."); 
		}
		Node receptor = nl.item(0);
		return XMLDOMUtils.getNodeText(receptor);
	}
	/**
	 * Recupera el emisor de mensaje de entrada, que será el receptor en la salida.
	 * @param mensaje Mensaje que entra
	 * @return Código de emisor de mensaje de entrada.
	 * @throws ConstructorRespuestaException Si no se puede recuperar el emisor
	 */
	private String getCodigoEmisor(Document mensaje) throws ConstructorRespuestaException
	{
		NodeList nl= XMLDOMUtils.getAllNodesCondicion(mensaje, 
				"//*[local-name()='COMUNICACION']/*[local-name()='REQUEST']/*[local-name()='CABECERA']/*[local-name()='EMISOR']");
		if (nl.getLength()==0) //Error, no viene receptor. No podemos duplicarlo.
		{
			throw new ConstructorRespuestaException ("Error al recuperar el emisor del mensaje de entrada para posteriormente devolverlo en la salida."); 
		}
		Node emisor = nl.item(0);
		return XMLDOMUtils.getNodeText(emisor);
	}
	@Override
	public Set<QName> getHeaders() {
		return Collections.emptySet();
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override	
	public boolean handleFault(SOAPMessageContext context) {
		//Aunque se haya producido fallo, nos aseguramos de destruir el contexto
		destruirContextoSesion(context);
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		Logger log=null;
		try
		{
			//A la entrada, cargamos lo necesario, o paramos y no continuamos si hay algún fallo.
			if (!salida.booleanValue())
			{
				//Lo generamos, para que exista.
				String idSesion=Utils.getIdLlamada();
				context.put(CallContextConstants.IDSESION, idSesion);
				context.setScope(CallContextConstants.IDSESION, MessageContext.Scope.APPLICATION);
				Preferencias pref = PreferenciasFactory.newInstance();
				context.put (CallContextConstants.PREFERENCIAS,pref);
				context.setScope(CallContextConstants.PREFERENCIAS, MessageContext.Scope.APPLICATION);
				log = LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogAplicacion(), "Sesión::"+idSesion);
				MensajesUtil mensajes;
				mensajes = MensajesFactory.newMensajesAplicacion(pref.getFicheroMensajesAplicacion());
				context.put(CallContextConstants.MENSAJES, mensajes);
				context.setScope(CallContextConstants.MENSAJES, MessageContext.Scope.APPLICATION);
				ConstructorMensajeRespuestaConsulta cons = MensajesFactory.newConstructorMensajeRespuestaConsulta(getCodigoEmisor(context.getMessage().getSOAPPart()), getCodigoReceptor(context.getMessage().getSOAPPart()), mensajes);
				context.put (CallContextConstants.CONSTRUCTOR_RESPUESTA,cons);
				context.setScope(CallContextConstants.CONSTRUCTOR_RESPUESTA, MessageContext.Scope.APPLICATION);
			}
			else
			{
				destruirContextoSesion(context);
			}
		}
		catch (PreferenciasException ex)
		{
			destruirContextoSesion(context); //Destruimos el contexto ante un fallo
			//Si no hay preferencias, no debemos tener log.
			System.err.println ("Recepción de escrituras de Plusvalias:: error en preferencias durante la creación de contexto ::"+ex.getMessage());
			ex.printStackTrace();
			Utils.generateSOAPErrMessage(context.getMessage(),"Error en proceso de mensaje SOAP.","0001","Error al crear el entorno de la llamada", true);
		} catch (MensajesException e) {
			destruirContextoSesion(context);
			log.error("Error durante la creación de contexto de la llamada:"+ e.getMessage(),e);
			Utils.generateSOAPErrMessage(context.getMessage(),"Error en proceso de mensaje SOAP.","0001","Error al crear el entorno de la llamada", true);
		} catch (ConstructorRespuestaException e) {
			destruirContextoSesion(context);
			if (log!=null)
			{
				log.error("Error durante la creación de contexto de la llamada:"+ e.getMessage(),e);
			}
			Utils.generateSOAPErrMessage(context.getMessage(),"Error en proceso de mensaje SOAP.","0001","Error al crear el entorno de la llamada", true);
		}
		return true;
	}

}
