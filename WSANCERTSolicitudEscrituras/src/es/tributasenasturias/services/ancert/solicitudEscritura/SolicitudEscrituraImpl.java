package es.tributasenasturias.services.ancert.solicitudEscritura;


import org.w3c.dom.Document;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.GestorSolicitudesBD;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.entrada.ComunicacionesEntradaException;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.entrada.ComunicacionesEntradaFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.entrada.ExtractorDatosSolicitud;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas.ComunicacionesExternasFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas.ConstruccionMensajeAncertException;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas.ExtractorRespuestaAncert;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas.MensajeANCERT;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas.MensajeriaException;
import es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas.MensajeroANCERT;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.factory.ObjectFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Mensajes;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.SolicitudDO;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils;
import es.tributasenasturias.services.ancert.solicitudEscritura.seguridad.CertificadoValidator;
import es.tributasenasturias.services.ancert.solicitudEscritura.seguridad.FirmaValidator;
import es.tributasenasturias.services.ancert.solicitudEscritura.seguridad.SeguridadException;
import es.tributasenasturias.services.ancert.solicitudEscritura.seguridad.SeguridadFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.validacion.ValidadorParametros;
import es.tributasenasturias.services.ancert.solicitudEscritura.validacion.ValidadorParametros.EstadoValidacion;



/**
 * Implementación de la interfaz de servicio de solicitud de escritura.
 * @author crubencvs
 *
 */
public class SolicitudEscrituraImpl implements IContextReader{
	
	
	private CallContext context;
	
	public SolicitudResponse solicitar(EntradaType parameters) throws SolicitudEscrituraException
	{
		SolicitudResponse ret=null;
		LogHelper log=null;
		log=LogFactory.getLogAplicacionContexto(context);
		log.info("1.Se validan los parámetros de entrada.");
		ret = new SolicitudResponse();
		try
		{
			//Comprobación de parámetros.
			ValidadorParametros pVal=new ValidadorParametros();
			pVal.validate(parameters);
			if (pVal.isValid())
			{
				log.info("2.Parámetros de entrada validados o bien la validación se ha desactivado.");
				SolicitudDO solicitud=null;
				GestorSolicitudesBD dt=null;
				try
				{
					ObjectFactory fact=new ObjectFactory();
					ExtractorDatosSolicitud extractorSol=
						ComunicacionesEntradaFactory.newExtractorDatosSolicitud(context);
				//*******************************
				//**   Cuerpo
				//*******************************
				log.info("3.Se extraen los datos de la solicitud de la entrada de servicio.");
				//Recuperamos el XML que formará la petición.
				//Se extraen los datos de solicitud de los parámetros.
				extractorSol.extraeDatosSolicitud(parameters);
				//Se almacena la solicitud
				solicitud = extractorSol.getSolicitudDO();
				dt = fact.newDatos(context);
				log.info("4. Alta de solicitud.");
				//Da de alta la solicitud y recupera los datos en la estructura.
				String resultadoAlta=dt.altaSolicitud(solicitud);
				if ("OK".equalsIgnoreCase(resultadoAlta))
				{
					log.info ("4.1.Solicitud dada de alta en base de datos. Se envía a ANCERT.");
					log.debug("Creación del objeto de comunicación con el servidor remoto.");
					//Creamos el objeto que se comunicará con el servicio remoto
					log.debug ("Se construye un XML de mensaje en función de los parámetros de entrada.");
					MensajeANCERT constructorMensaje = ComunicacionesExternasFactory.newMensajeANCERT(context);
					Document doc=constructorMensaje.construyeMensajeSolicitud(solicitud,solicitud.getIdSolicitud());
					log.debug("Recuperación de la plantilla XML que se utilizará para el envío hacia ANCERT.");
					MensajeroANCERT port= ComunicacionesExternasFactory.newMensajeroANCERT(context);
					//Se asigna el mensaje 
					port.setMessage(doc);
					log.info ("5.Se envía la solicitud a ANCERT.");
					Document respuesta=port.solicitar();
					log.info ("6.Se valida si la respuesta recibida cumple las especificaciones de firma y/o certificado.");
					CertificadoValidator certValid=SeguridadFactory.newCertificadoValidator(context);
					FirmaValidator firmVal = SeguridadFactory.newFirmaValidator(context);
					//CRUBENCVS 38243. Dado que viene firmado tanto por WSSecurity como por Xades, si 
					// cumple WSSecurity ya no miramos más.
					//if (firmVal.firmaValida(respuesta) && certValid.tienePermisos(respuesta))
					if (firmVal.firmaValidaWSSecurity(respuesta) && certValid.tienePermisos(respuesta))
					{
						log.info("6.1.Respuesta válida o seguridad desactivada.");
						ExtractorRespuestaAncert prs = ComunicacionesExternasFactory.newExtractorRespuestaAncert(context);
						prs.procesaResultado(respuesta);
						ret =prs.getResultado(); 
						log.info("7.Se actualiza en base de datos con los datos recibidos de ANCERT.");
						//Se actualizan los datos en base de datos
						if (!"S".equalsIgnoreCase(solicitud.getEstadoSolicitud()))
						{
							String codError = Mensajes.getCodeFromANCERT(prs.getCodigoError());
							if (!(codError.equalsIgnoreCase(Mensajes.getOk())))
							 {
								solicitud.setEstadoSolicitud("E");
							 }
							else
							{
								solicitud.setEstadoSolicitud("S");
							}
							solicitud.setResultadoSolicitud(prs.getCodigoError()+"-"+prs.getTextoError());
							dt.actualizarSolicitud(solicitud);
							//Controlamos que termine correctamente.
							if (!dt.getErrorLlamada().equals(""))
							{
								log.error("No se ha actualizado correctamente el dato en la base. Error:" + dt.getErrorLlamada());
								ret=Utils.setResponse(ret, Mensajes.getErrorBD());
							}
						}
					}
					else
					{
						log.info("6.1.El mensaje recibido de ANCERT no cumple las especificaciones de certificado.");
						marcarErrorSolicitud(dt, solicitud,"Error firma/certificado ANCERT");
						ret=Utils.setResponse(ret, Mensajes.getErrorSeguridad());
					}
				}
				else
				{
					log.error("No se ha dado de alta correctamente la solicitud. Error:" + dt.getErrorLlamada());
					ret=Utils.setResponse(ret, Mensajes.getErrorBD());
				}
				}
				catch (ComunicacionesEntradaException ex)
				{
					log.error("Error al extraer los datos de los parámetros de solicitud de escritura:"+ex.getMessage()+"::" +((ex.getCause()!=null)?"::Causa::"+ex.getCause().getMessage():""),ex);
					ret=Utils.setResponse(ret, Mensajes.getErrorGeneral());
				
				}catch (ConstruccionMensajeAncertException ex)
				{
					log.error ("Error al construir el mensaje para envío de solicitud de escritura contra el servidor de notarios:" + ex.getMessage(),ex);
					ret=Utils.setResponse(ret, Mensajes.getErrorGeneral());
				}catch (SystemException ex) {
					log.error("Error técnico en la solicitud de escritura:"+ex.getMessage()+"::" +((ex.getCause()!=null)?"::Causa::"+ex.getCause().getMessage():""),ex);
					ret=Utils.setResponse(ret, Mensajes.getErrorGeneral());
				} 
				catch (MensajeriaException ex)
				{
					log.error ("Error de mensajería con ANCERT en la solicitud de escritura:" + ex.getMessage(),ex);
					marcarErrorSolicitud(dt, solicitud,"Error mensajeria ANCERT");
					ret=Utils.setResponse(ret, Mensajes.getErrorMensajeria());
				}
				catch (SeguridadException ex)
				{
					log.error ("Error al comprobar la firma o el certificado del mensaje recibido de ANCERT:" + ex.getMessage(),ex);
					marcarErrorSolicitud(dt, solicitud,"Error firma/certificado ANCERT");
					ret = Utils.setResponse(ret, Mensajes.getErrorGeneral());
				}
			}
			else
			{
				log.error("No se ha superado la validación del mensaje de entrada :" + pVal.getStringMessages());
				if (pVal.getEstadoValidacion().equals(EstadoValidacion.PROTOCOLO_INCORRECTO))
				{
					ret=Utils.setResponse(ret, Mensajes.getProtocoloIncorrecto());
				}
				else if (pVal.getEstadoValidacion().equals(EstadoValidacion.FALTAN_PARAMETROS))
				{
					ret=Utils.setResponse(ret, Mensajes.getFaltanParametrosRequeridos());
				}
				else
				{
					ret=Utils.setResponse(ret,Mensajes.getErrorGeneral());
				}
			}
		}
		catch (Exception ex)
		{
			log.error ("Error en la solicitud de escritura a ANCERT:" + ex.getMessage(),ex);
			ret = Utils.setResponse(ret, Mensajes.getErrorGeneral());
		}
		//Parámetros de la entrada a la salida.
		ret.setSolicitud(parameters);
		return ret;

	}
	/**
	 * Marca la solicitud como errónea
	 * @param dt Gestor de solicitudes a base de datos, para actualizar
	 * @param solicitud Objeto de datos de la solicitud
	 * @param mensaje Mensaje de resultado. Sólo tiene en cuental los primeros 40 caracteres
	 * @throws SystemException
	 */
	private void marcarErrorSolicitud (GestorSolicitudesBD dt, SolicitudDO solicitud, String mensaje) throws SystemException
	{
		if (dt!=null && solicitud!=null)
		{
			solicitud.setEstadoSolicitud("E");
			solicitud.setResultadoSolicitud(mensaje.length()>=40?mensaje.substring(0,40):mensaje);
			dt.actualizarSolicitud(solicitud);
		}
	}
	
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
		
	}
}
