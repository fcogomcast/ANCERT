package es.tributasenasturias.services.ancert.recepcionescritura.objetos;

import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.factory.DomainObjectsFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.types.ENVIOESCRITURAS;
import es.tributasenasturias.services.ancert.recepcionescritura.types.IDESCRITURAType;
import es.tributasenasturias.services.ancert.recepcionescritura.types.RESULTADO;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.FirmaHelper;

/**
 * Crea los objetos de respuesta, según el tipo ENVIOESCRITURAS.
 * @author crubencvs
 *
 */
public class RespuestaBuilder implements IContextReader{
	
	private ENVIOESCRITURAS env=null;
	private ENVIOESCRITURAS.REPLY reply=null;
	private String numeroPresentacion="1";
	private CallContext context;
	/**
	 * Crea un nuevo objeto de construcción de mensaje de respuesta.
	 */
	public RespuestaBuilder() 
	{
		env=new ENVIOESCRITURAS();
		reply=new ENVIOESCRITURAS.REPLY();
	}
	public final void setIdEscritura(IDESCRITURAType idEscritura)
	{
		reply.setIDESCRITURA(idEscritura);
	}
	/**
	 * Establece los datos de cabecera del mensaje de respuesta del envío.
	 * @throws SystemException
	 */
	public final void setCabecera() throws SystemException
	{
		try {
			Preferencias pr=PreferenciasFactory.getPreferenciasContexto(context);
			if (pr==null)
			{
				throw new SystemException ("No se pueden recuperar las preferencias.");
			}
			ENVIOESCRITURAS.REPLY.CABECERA cabIns=new ENVIOESCRITURAS.REPLY.CABECERA();
			cabIns.setEMISOR(pr.getReceptorEnvio()); // A la vuelta, el receptor será el emisor.
			cabIns.setAPLICACION(pr.getAplicacion());
			cabIns.setOPERACION(pr.getOperacion());
			cabIns.setRECEPTOR(pr.getEmisorEnvio()); //A la vuelta, se cambian.
			GregorianCalendar cal=new GregorianCalendar();
			XMLGregorianCalendar fec=DatatypeFactory.newInstance().newXMLGregorianCalendar();
			cal.setTime(new java.util.Date());
			XMLGregorianCalendar gcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			fec.setDay(gcal.getDay());
			fec.setMonth(gcal.getMonth());
			fec.setYear(gcal.getYear());
			cabIns.setFECHA(fec);
			String hora=String.format("%02d",gcal.getHour())+":"+String.format("%02d",gcal.getMinute()) +":"+String.format("%02d",gcal.getSecond());
			cabIns.setHORA(hora);
			cabIns.setIDCOMUNICACION(Long.valueOf(numeroPresentacion));
			reply.setCABECERA(cabIns);
		} catch (DatatypeConfigurationException e) {
			throw new SystemException ("Error al incorporar la cabecera a la respuesta debido a la creación de timestamp:" + e.getMessage(),e);
			}
	}
	/**
	 * Establece el resultado del mensaje de respuesta de servicio al indicado.
	 * @param codigo Código de resultado
	 * @param texto Texto de resultado
	 * @param isError Flag para indicar si el resultado representa error o no.
	 */
	public final void setResultado(String codigo, String texto, boolean isError)
	{
		RESULTADO result = new RESULTADO();
		result.setCODIGOERROR(codigo);
		result.setTEXTOERROR(texto);
		result.setTIPOERROR(isError);
		reply.setRESULTADO(result);
	}
	public final void setNumPresentacion(String numPresentacion)
	{
		reply.setNUMEROPRESENTACION(numPresentacion);
		this.numeroPresentacion=numPresentacion;
	}
	/**
	 * Devuelve un mensaje de respuesta como objeto de tipo JAXB de "ENVIOESCRITURAS"
	 * @return Mensaje de tipo es.tributasenasturias.services.ancert.recepcionescritura.ENVIOESCRITURAS
	 */
	public Object getRespuesta()
	{
		String id="";
		Preferencias pr=PreferenciasFactory.getPreferenciasContexto(context);
		if (pr==null)
		{
			id="";
		}
		else
		{
			id=pr.getRequestId();
		}
		reply.setID(id);
		env.setREPLY(reply);
		/*LogHelper log=LogFactory.getLogAplicacionContexto(context);
		
		try {
			firmaRespuesta();
		} catch (SystemException e) {
			log.info("No se ha podido firmar el mensaje de salida.");
		}
		*/
		return env;
	}
	/**
	 * Hace que el objeto que contiene la respuesta esté firmado.
	 * Ya no se utiliza, porque al usar enlace JAXB estropea la firma.
	 * @throws SystemException
	 */
	@Deprecated
	public final void firmaRespuesta() throws SystemException
	{
		Document doc=null;
		Document docFirmado=null;
		LogHelper log=LogFactory.getLogAplicacionContexto(context);
		try {
			Preferencias pr= PreferenciasFactory.getPreferenciasContexto(context);
			if (pr==null)
			{
				throw new SystemException("No se han podido recuperar las preferencias en la firma de la respuesta.");
			}
			if ("S".equalsIgnoreCase(pr.getFirmaSalida()))
			{
				JAXBContext jb = JAXBContext.newInstance(env.getClass().getPackage().getName());
				Marshaller mar= jb.createMarshaller();
				DocumentBuilderFactory docf =DocumentBuilderFactory.newInstance();
				docf.setNamespaceAware(true);
				doc=docf.newDocumentBuilder().newDocument();
				mar.marshal(env, doc);
				FirmaHelper firma=new DomainObjectsFactory().newFirmaHelper(context);
				docFirmado=firma.firmaMensaje(doc);
				if (docFirmado!=null){
					//Se pasa a objeto de nuevo.
					Unmarshaller unmar = jb.createUnmarshaller();
					env=(ENVIOESCRITURAS)unmar.unmarshal(docFirmado);
				}
				else
				{
					log.error("No se ha podido firmar el mensaje de salida.");
				}
			}
		} catch (JAXBException e) {
			throw new SystemException ("Error (JAXB) al recuperar el mensaje de respuesta como un objeto de clase org.w3c.dom.Document:" + e.getMessage(),e);
		} catch (ParserConfigurationException e) {
			throw new SystemException ("Error (JAXB) al recuperar el mensaje de respuesta como un objeto de clase org.w3c.dom.Document:" + e.getMessage(),e);
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
