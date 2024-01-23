package es.tributasenasturias.services.ancert.enviodiligencias;


import org.w3c.dom.Node;

import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosException;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosInforme;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.ComunicacionFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.CreadorMensajeSalidaServicio;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.ComunicacionExternaFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.ConstruccionMensajeException;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.MensajeANCERT;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.MensajeriaException;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.MensajeroAncert;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContextConstants;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.DocumentoException;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.DocumentosFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.InsertadorDocumento;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.XMLUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.informe.GeneradorInforme;
import es.tributasenasturias.services.ancert.enviodiligencias.informe.InformeException;
import es.tributasenasturias.services.ancert.enviodiligencias.informe.InformeFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogAplicacion;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.seguridad.CertificadoValidator;
import es.tributasenasturias.services.ancert.enviodiligencias.seguridad.FirmaValidator;
import es.tributasenasturias.services.ancert.enviodiligencias.seguridad.SeguridadFactory;

/**
 * Implementa la funcionalidad de envío de diligencias.
 * @author crubencvs
 *
 */
public class EnvioDiligencias implements IContextReader{
	private CallContext context;
	public EnvioDiligenciasMessageOut enviarDiligencias(
			EnvioDiligenciasMessageInType parameters) {
		LogAplicacion log = LogFactory.getLogAplicacionContexto(context);
		CreadorMensajeSalidaServicio cMensajes = ComunicacionFactory.newCreadorMensajeSalida();
		EnvioDiligenciasMessageOut out=null;
		if (context==null)
		{
			log.info("Contexto nulo. Se sale de la llamada.");
			out = cMensajes.creaMensajeErrorTecnico(parameters);
			return out;
		}
		else
		{
			//Añadimos las preferencias y las insertamos en contexto.
			try {
				Preferencias pref= PreferenciasFactory.newInstance();
				context.setItem(CallContextConstants.PREFERENCIAS, pref);
			} catch (PreferenciasException ex) {
				log.info("Error al recuperar preferencias:"+ ex.getMessage());
				out = cMensajes.creaMensajeErrorTecnico(parameters);
				return out;
			}
		}
		log.info("=====Inicio de la llamada al servicio web.");
		//Parámetro de entrada
		String numAutoliquidacion=parameters.getNumeroAutoliquidacion();
		log.info("1. Recuperación de parámetro de entrada");
		if (numAutoliquidacion==null || numAutoliquidacion.equals(""))
		{
			out= cMensajes.creaMensajeAutoliquidacionVacia(parameters);
			return out;
		}
		//Recuperamos los datos del informe.
		try
		{
			log.info("2. Recuperación de los datos para el informe, de la base de datos.");
			DatosInforme datosInforme = DatosFactory.newDatosInforme(context,numAutoliquidacion);
			//Comprobamos si hay datos. Si no, salimos del servicio porque no se han encontrado datos.
			if (datosInforme.isVacio())
			{
				log.info("2.1. No se han encontrado datos, se sale.");
				out= cMensajes.creaMensajeAutoliquidacionNoExiste(parameters);
				return out;
			}
			//Comprobamos que está autorizada para el envío. Si no lo está, no podemos enviar nada,
			//porque el notario no tiene permiso del Sujeto Pasivo para recibirlo.
			if (!datosInforme.getDatosSolicitud().isAutorizadaEnvio()){
				log.info("2.1. El Sujeto Pasivo no ha autorizado el envío de diligencias de presentación relacionadas con esa autoliquidación.");
				out= cMensajes.creaMensajeNoAutorizadoEnvio(parameters);
				return out;
			}
			//Imprimimos el informe.
			String pdf="";
			log.info("3. Generamos el informe.");
			GeneradorInforme generador=InformeFactory.newGeneradorInforme(context);
			pdf=generador.getPDFDocument(numAutoliquidacion, datosInforme);
			if (generador.seHaGeneradoDocumento())
			{
				log.info("3.1. El informe no existía en la base de datos, se inserta.");
				//Insertamos el documento.
				InsertadorDocumento ins = DocumentosFactory.newInsertadorDocumento(context);
				ins.altaDocumento(pdf, datosInforme);
			}
			else
			{
				log.info("3.1. El informe existía en la base de datos, se recupera.");
			}
			//Construimos el mensaje a enviar a ANCERT
			log.info("4. Construimos el mensaje que se enviará a  ANCERT.");
			MensajeANCERT consMensaje = ComunicacionExternaFactory.newMensajeANCERT(context);
			consMensaje.construirMensaje(datosInforme,pdf);
			//Ya tenemos el mensaje construido. Ahora lo enviaremos a ANCERT.
			log.info("5. Se envía el mensaje a ANCERT.");
			MensajeroAncert mensajero = ComunicacionExternaFactory.newMensajeroAncert(context);
			Node respuestaEnvioDiligencia=mensajero.enviarDiligencia(consMensaje.getMensajeAncert());
			//Comprobamos la seguridad en el mensaje a la vuelta.
			log.info("6. Comprobamos si el mensaje recibido tiene como origen ANCERT.");
			FirmaValidator firmVal= SeguridadFactory.newFirmaValidator(context);
			String xml = XMLUtils.getXMLText(respuestaEnvioDiligencia);
			if (firmVal.firmaValida(xml))
			{
				log.info("6.1. Firma válida.");
				CertificadoValidator certVal = SeguridadFactory.newCertificadoValidator(context);
				if (!certVal.tienePermisos(xml))
				{
					log.info("6.2. El certificado del mensaje no es válido.");
					out=cMensajes.creaMensajeErrorSeguridad(parameters);
					return out;
				}
			}
			else
			{
				log.info("6.1. La firma del mensaje de entrada no es válida");
				out=cMensajes.creaMensajeErrorSeguridad(parameters);
				return out;
			}
			//Comprobamos si la respuesta de Ancert es la correcta.
			log.info("7. Comprobamos la respuesta de ANCERT.");
			out=cMensajes.crearMensajeSalidaServicio(parameters, ComunicacionExternaFactory.newTraductorRespuestas(context).getResultado(respuestaEnvioDiligencia));
			//Decimos que todo ha acabado bien.
			//out = cMensajes.creaMensajeOk(parameters);
		}
		catch (DatosException ex)
		{
			log.error("Error en la recuperación de datos del informe desde la base:" + ex.getMessage(),ex);
			out=cMensajes.creaMensajeErrorTecnico(parameters);
		}
		catch (InformeException e)
		{
			log.error ("Error al generar el informe:" + e.getMessage(),e);
			out=cMensajes.creaMensajeErrorTecnico(parameters);
		}
		catch (DocumentoException e)
		{
			log.error ("Error al insertar el documento en la base de datos:" + e.getMessage(),e);
			out=cMensajes.creaMensajeErrorTecnico(parameters);
		}
		catch (ConstruccionMensajeException e)
		{
			log.error ("Error al construir el mensaje con ANCERT:" + e.getMessage(),e);
			out=cMensajes.creaMensajeErrorTecnico(parameters);
		}
		catch (MensajeriaException e)
		{
			log.error ("Error en comunicación con ANCERT:" + e.getMessage(),e);
			out=cMensajes.creaMensajeErrorTecnico(parameters);
		}
		catch (SystemException e)
		{
			log.error ("Error técnico durante la ejecución del servicio, comprobar el resto del log:" + e.getMessage(),e);
			log.trace(e.getStackTrace());
			out=cMensajes.creaMensajeErrorTecnico(parameters);
		}
		catch (Exception e) 
		{
			log.error ("Error inesperado en la ejecución del servicio:" + e.getMessage());
			log.trace (e.getStackTrace());
			out=cMensajes.creaMensajeErrorGrave(parameters);
		}
		finally
		{
			log.info("=====Fin de llamada al servicio web.");
		}
		return out;
	}
	
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}
}
