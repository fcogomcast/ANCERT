/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.objetos;


import java.util.Iterator;

import javax.xml.datatype.XMLGregorianCalendar;

import es.tributasenasturias.services.ancert.recepcionescritura.bd.Datos;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.DomainException;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.factory.DomainObjectsFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.types.DOCUMENTACION;
import es.tributasenasturias.services.ancert.recepcionescritura.types.IDESCRITURAType;
import es.tributasenasturias.services.ancert.recepcionescritura.types.DOCUMENTACION.DOCUMENTO;
import es.tributasenasturias.services.ancert.recepcionescritura.types.DOCUMENTACION.DOCUMENTO.FICHERO;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.Base64;

/** Implementa la funcionalidad para insertar una escritura. Esta clase ocultará
 *  los accesos necesarios.
 * @author crubencvs
 *
 */
public class InsercionEscrituraBuilder implements IContextReader{
	private EscrituraDO datescritura;
	private CallContext context;
	private String numeroPresentacion="";//Número de la presentación de escritura.
	public static enum ResultadoInsercion {DUPLICADO,INSERTADO,FALTAN_DATOS,ESCRITURA_VACIA,NO_INSERTADO,ERROR_INSERCION};
	public static class InsercionBuilderResultado
	{
		private ResultadoInsercion resultado;
		private String textoResultado;
		private Throwable error;
		public ResultadoInsercion getResultado() {
			return resultado;
		}
		public void setResultado(ResultadoInsercion resultado) {
			this.resultado = resultado;
		}
		public String getTextoResultado() {
			return textoResultado;
		}
		public void setTextoResultado(String textoResultado) {
			this.textoResultado = textoResultado;
		}
		public Throwable getError() {
			return error;
		}
		public void setError(Throwable error) {
			this.error = error;
		}
	}
	private InsercionBuilderResultado resObjeto=new InsercionBuilderResultado(); 
	public InsercionEscrituraBuilder()
	{
		datescritura=new DomainObjectsFactory().newEscrituraDO();
	}
	/**
	 * Establece el código de notario para esta inserción.
	 * @param codNotario Código de notario
	 */
	public final void setCodNotario(String codNotario)
	{
		datescritura.setCodNotario(codNotario);
	}
	/**
	 * Establece el código de notaría para esta inserción.
	 * @param codNotaria Código de notaría
	 */
	public final void setCodNotaria(String codNotaria)
	{
		datescritura.setCodNotaria(codNotaria);
	}
	/**
	 * Establece el Número de protocolo para esta inserción.
	 * @param numProtocolo Número de protocolo.
	 */
	public final void setNumProtocolo(String numProtocolo)
	{
		datescritura.setNumProtocolo(numProtocolo);
	}
	/**
	 * Establece el protocolo bis para esta inserción.
	 * @param protocoloBis Protocolo bis.
	 */
	public final void setProtocoloBis(String protocoloBis)
	{
		datescritura.setProtocoloBis(protocoloBis);
	}
	/**
	 * Establece la fecha de escritura para esta inserción.
	 * @param fechaEscritura Fecha de escritura.
	 */
	public final void setFechaEscritura(String fechaEscritura)
	{
		datescritura.setFechaEscritura(fechaEscritura);
	}
	/**
	 * Establece el indicador de si se ha permitido por parte del Sujeto Pasivo el envío de 
	 * diligencias al notario.
	 * @param autorizado. Indica si existe la autorización (true) o no (false).
	 */
	public final void setAutorizadoEnvioDiligencia(boolean autorizado)
	{
		datescritura.setAutorizacionEnvioDiligencias(autorizado);
	}
	/**
	 * Inserta los datos de la escritura que se reciben por parámetro.
	 * @param datosEscritura Datos de la escritura, tal como aparecen en el nodo {@link IDESCRITURAType}.
	 * @throws SystemException
	 */
	public final void setDatosEscritura (IDESCRITURAType datosEscritura) throws SystemException
	{
		datescritura.setCodNotario(datosEscritura.getCODNOTARIO());
		datescritura.setCodNotaria(datosEscritura.getCODNOTARIA());
		ProtocoloDO protocolo=new DomainObjectsFactory().newProtocoloDO();
		if (datosEscritura.getNUMBIS()==null)
		{
			//Podrían enviarlo compuesto, no se indica nada.
			protocolo.setProtocoloCompuesto(String.valueOf(datosEscritura.getNUMPROTOCOLO()));
		}
		else
		{
			protocolo.setProtocolo(String.valueOf(datosEscritura.getNUMPROTOCOLO()));
			protocolo.setProtocoloBis(String.valueOf(datosEscritura.getNUMBIS()));
		}
		datescritura.setNumProtocolo(protocolo.getProtocolo());
		datescritura.setProtocoloBis(protocolo.getProtocoloBis());
		XMLGregorianCalendar fec=datosEscritura.getFECHAAUTORIZACION();
		String fecha=String.format("%02d",fec.getDay())+"-"+String.format("%02d",fec.getMonth()) +"-"+String.format("%04d",fec.getYear());
		datescritura.setFechaEscritura(fecha);
	}
	/**
	 * Realiza la inserción de la escritura, devolviendo el resultado.
	 * @param documento Tipo {@link DOCUMENTACION} que se recibe en el mensaje de entrada.
	 * @throws DomainException En caso de producirse un estado excepcional relacionado con lógica del dominio.
	 * @throws SystemException En caso de producirse un error técnico en el procedimiento.
	 */
	public final void insertarEscritura(DOCUMENTACION documento) throws DomainException,SystemException
	{
		LogHelper log=null;
		String nulo="null";
		//Comprobar si hay algún dato erróneo.
		if (datescritura.getCodNotario()==null ||
			datescritura.getCodNotaria()==null||
			datescritura.getNumProtocolo()==null||
			datescritura.getProtocoloBis()==null||
			datescritura.getFechaEscritura()==null||
			datescritura.getCodNotario().equals("") ||
			datescritura.getCodNotaria().equals("")||
			datescritura.getNumProtocolo().equals("") ||
			datescritura.getProtocoloBis().equals("") ||
			datescritura.getFechaEscritura().equals("")||
			datescritura.getCodNotaria().equalsIgnoreCase(nulo)|| //FIXME: ¿Esto por qué?.
			datescritura.getCodNotaria().equalsIgnoreCase(nulo)||
			datescritura.getNumProtocolo().equalsIgnoreCase(nulo)||
			datescritura.getProtocoloBis().equalsIgnoreCase(nulo)||
			datescritura.getFechaEscritura().equalsIgnoreCase(nulo)
			
		    )
		{
			resObjeto.setResultado(ResultadoInsercion.FALTAN_DATOS);
			return;
		}
		//Comprobar si ya está insertado, en cuyo caso simplemente lo ignoramos.
		try {
			log=LogFactory.getLogAplicacionContexto(context);
			Datos dat= new DomainObjectsFactory().newDatos(context);
			//21396. Ya no se comprobará primero si la escritura estaba duplicada,
			//porque existe la posibilidad de actualizarla.
//			String duplicada=dat.escrituraDuplicada(datescritura);
//			if (dat.getErrorLlamada()!=null && !dat.getErrorLlamada().equals(""))
//			{
//				log.error("Error al comprobar si la solicitud estaba duplicada:" + dat.getErrorLlamada());
//				resObjeto.setResultado(ResultadoInsercion.NO_INSERTADO);
//				return;
//			}
//			if ("S".equalsIgnoreCase(duplicada))
//			{
//				log.info("La escritura ya existe en base de datos, no es necesario insertarla");
//				String idSolicitud=dat.getIdSolicitud(datescritura);
//				if (!dat.getErrorLlamada().equals("")) 
//				{
//					throw new SystemException ("Error en la llamada para recuperar el id de solicitud:"+ dat.getErrorLlamada());
//				}
//				//Puede que no tenga número de solicitud, si no existía solicitud previa,
//				//porque la escritura puede existir previamente pero sin haberse solicitado (si ha entrado desde 
//				// ANCERT). Este caso se daría si han enviado la escritura desde ANCERT, pero
//				//posteriormente la vuelven a enviar (quizá para indicar que es una escritura autorizada).
//				//En este caso, se llama directamente a la finalización de la recepción, que 
//				//generará una solicitud indicando que el envío de escritura se hace sin solicitud previa.
//				if ("".equals(idSolicitud))
//				{	log.debug("Va a finalizar la recepción");
//					idSolicitud = dat.finalizarRecepcionEscritura (datescritura);
//					if (!dat.getErrorLlamada().equals("")) 
//					{
//						throw new SystemException ("Error en la llamada para finalizar la recepción :"+ dat.getErrorLlamada());
//					}
//					if (idSolicitud.equals(""))
//					{
//						resObjeto.setResultado(ResultadoInsercion.ERROR_INSERCION);
//						return;
//					}
//				}
//				this.numeroPresentacion=idSolicitud;
//				resObjeto.setResultado(ResultadoInsercion.DUPLICADO);
//				return;
//			}
			//log.info("La escritura no existe en base de datos, se inserta.");
			log.info ("Se procede a insertar/actualizar la escritura.");
			//Supone una sola escritura.
			Iterator<DOCUMENTO> it = documento.getDOCUMENTO().iterator();
			if (it.hasNext())
			{
				DOCUMENTO doc= it.next();
				FICHERO fic= doc.getFICHERO();
				
					//Al servicio se le pasa un base64, pero weblogic lo convierte.
					//Hay que volver a codificarlo en base 64.
					//Lo convierte porque en el xsd está indicado que es Base64Binary.
					//Si nos lo envían mal formado, seguramente el servicio fallará.
					//Dado que no lo tratamos, la forma más segura de recibirlo
					//sería un String, pero para eso tenemos que cambiar el xsd
					//que genera las clases, con lo que ya no sería exactamente
					//el mismo que nos han enviado.
					String docEscritura = new String(Base64.encode(fic.getValue())); //Es Base64, así que no importa la codificación
					String firmaEscritura = new String (Base64.encode(doc.getFIRMAFICHERO()));
					if (docEscritura.length()!=0)
					{
						InsertadorEscrituras insEscritura=new DomainObjectsFactory().newInsertadorEscrituras(context);
						if (insEscritura.insertaEscritura(datescritura, docEscritura, firmaEscritura))
						{
							resObjeto.setResultado(ResultadoInsercion.INSERTADO);
							String idSolicitud=dat.getIdSolicitud(datescritura);
							if (!dat.getErrorLlamada().equals("")) 
							{
								throw new SystemException ("Error en la llamada para recuperar el id de solicitud:"+ dat.getErrorLlamada());
							}
							//Aquí debería tener un número de solicitud. Si no lo tuviera, lo consideramos
							//error, porque o bien existía previamente o bien debería haberse dado de 
							//alta al insertar la escritura.
							if (idSolicitud==null ||idSolicitud.equals(""))
							{
								resObjeto.setResultado(ResultadoInsercion.ERROR_INSERCION);
								return;
							}
							this.numeroPresentacion=idSolicitud;
						}
						else
						{
							resObjeto.setResultado(ResultadoInsercion.NO_INSERTADO);
						}
					}
					else
					{
						resObjeto.setResultado(ResultadoInsercion.ESCRITURA_VACIA);
					}
				
			}
			else
			{
				resObjeto.setResultado(ResultadoInsercion.FALTAN_DATOS);
			}
		} catch (SystemException e) {
			resObjeto.setResultado(ResultadoInsercion.ERROR_INSERCION);
			resObjeto.setError(e);
			return;
		}
		
	}
	public final InsercionBuilderResultado getResultado()
	{
		return resObjeto;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
		
	}
	public final String getNumeroPresentacion()
	{
		return numeroPresentacion;
	}
}
