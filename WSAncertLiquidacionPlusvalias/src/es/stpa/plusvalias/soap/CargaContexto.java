package es.stpa.plusvalias.soap;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import es.stpa.plusvalias.Constantes;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.preferencias.PreferenciasException;
import es.tributasenasturias.log.ILog;
import es.tributasenasturias.log.TributasLogger;




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
	 * Intenta extraer el primer adjunto que se reciba, como escritura.
	 * Debería llegar sólo para los pagos, en otros casos se extraerá pero
	 * se va a ignorar.
	 * Si no hay, pues nada.
	 * @param context
	 * @throws SOAPException
	 */
	@SuppressWarnings("unchecked")
	private void extraerEscritura(SOAPMessageContext context) throws SOAPException{
		SOAPMessage message= context.getMessage();
		if (message.countAttachments()>0){
			Iterator<AttachmentPart> adjuntos= message.getAttachments();
			AttachmentPart adjunto=null;
			//Sólo me interesa el primero, los demás se perderán.
			if (adjuntos.hasNext()){
				adjunto=adjuntos.next();
			}
			if (adjunto!=null){
				context.put (Constantes.ESCRITURA,adjunto.getRawContentBytes());
				context.setScope(Constantes.ESCRITURA, MessageContext.Scope.APPLICATION);
			}
		}
		
	}
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
	private void destruirContextoSesion(SOAPMessageContext context)
	{
		//A la salida, nos cargamos los datos de contexto.
		context.remove(Constantes.IDSESION);
		context.remove(Constantes.PREFERENCIAS);
		context.remove(Constantes.LOG);
		context.remove(Constantes.CARPETA_LOG);
		context.remove(Constantes.ESCRITURA);
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
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!salida.booleanValue())
		{
		destruirContextoSesion(context);
		}
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		try
		{
			//A la entrada, cargamos lo necesario, o paramos y no continuamos si hay algún fallo.
			if (!salida.booleanValue())
			{
				//Lo generamos, para que exista.
				String idLlamada=SOAPUtils.getIdLlamada();
				context.put(Constantes.IDSESION, idLlamada);
				context.setScope(Constantes.IDSESION, MessageContext.Scope.APPLICATION);
				Preferencias pref = new Preferencias();
				context.put (Constantes.PREFERENCIAS,pref);
				context.setScope(Constantes.PREFERENCIAS, MessageContext.Scope.APPLICATION);
				ILog log= new TributasLogger(pref.getModoLog(),pref.getDirectorioRaizLog(),pref.getFicheroLogAplicacion(),idLlamada);
				context.put (Constantes.LOG, log);
				context.setScope(Constantes.LOG,MessageContext.Scope.APPLICATION);
				extraerEscritura(context);
			}
			else
			{
				destruirContextoSesion(context);
			}
		}
		catch (PreferenciasException ex)
		{
			//Si no hay preferencias, no debemos tener log.
			System.err.println ("Servicio de plusvalías para ANCERT:: error en preferencias ::"+ex.getMessage());
			ex.printStackTrace();
			SOAPUtils.generateSOAPErrMessage(context.getMessage(),"Error en proceso de mensaje SOAP.","0001","Error al crear el entorno de la llamada", true);
		}
		catch (SOAPException ex)
		{
			//Si no hay preferencias, no debemos tener log.
			System.err.println ("Servicio de plusvalías para ANCERT:: error en manejo de mensaje SOAP a la entrada ::"+ex.getMessage());
			ex.printStackTrace();
			SOAPUtils.generateSOAPErrMessage(context.getMessage(),"Error en proceso de mensaje SOAP.","0001","Error al crear el entorno de la llamada", true);
		} 
		return true;
	}

	


}
