package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.externas;

import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import es.tributasenasturias.services.ANCERT.serviceDispatcher.types.IDESCRITURAType;
import es.tributasenasturias.services.ANCERT.serviceDispatcher.types.SERVICIOSESCRITURA;
import es.tributasenasturias.services.ANCERT.serviceDispatcher.types.SERVICIOSESCRITURA.REQUEST;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.DocumentoException;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.informes.GestorDocumental;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Otorgante;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.SolicitudDO;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.firma.FirmaFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.firma.FirmaHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.PreferenciasFactory;

/**
 * Contiene los datos del mensaje a ANCERT. Permite construirlo en función de los datos de solicitud
 * que se han obtenido de los parámetros de entrada y de las preferencias de servicio.
 * @author crubencvs
 *
 */
public class MensajeANCERT implements IContextReader{

	private CallContext context;
	
	protected MensajeANCERT()
	{
		
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
	/**
	 * Recupera un timestamp para uso en XML.
	 * @return XMLGregorianCalendar con los datos de timestamp.
	 * @throws SystemException
	 */
	private XMLGregorianCalendar getTimeStamp() throws SystemException 
	{
		GregorianCalendar cal=null;
		XMLGregorianCalendar xcal=null;
		try {
			cal=new GregorianCalendar();
			cal.setTime(new java.util.Date());
			xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		} catch (DatatypeConfigurationException e) {
			throw new SystemException ("Error al recuperar el timestamp",e);
		}
		return xcal;
	}
	/**
     * Recupera el documento firmado o null si no se puede firmar.
     * @param doc Documento a firmar
     * @return Documento firmado. Si no se puede, nulo.
     */
	private Document firmar (Document doc)
	{
		Document firmado=null;
		FirmaHelper firma = FirmaFactory.newFirmaHelper(context);
		firmado = firma.firmaMensaje(doc);
		return firmado;
	}
	//Función para construir el cuerpo de la petición (incluye un cuerpo y una cabecera).
	private REQUEST construyeCuerpo(SolicitudDO solicitud,Preferencias pr) throws ConstruccionMensajeAncertException,SystemException
	{
		REQUEST req=new REQUEST();
		REQUEST.CABECERA cab=new REQUEST.CABECERA();
		IDESCRITURAType esc=new IDESCRITURAType();
		XMLGregorianCalendar stamp=null;
		try
		{
			stamp=getTimeStamp();
			cab.setEMISOR(pr.getEmisorSolicitud());
			cab.setRECEPTOR(pr.getReceptorSolicitud());
			// Es necesario no pasar más que la parte de fecha, ya que si no, pasa también hora
			// y entonces no cumple la restricción de esquema de xsd:date.
			XMLGregorianCalendar fec=null;
			try {
				fec=DatatypeFactory.newInstance().newXMLGregorianCalendar();
				fec.setYear(stamp.getYear());
				fec.setMonth(stamp.getMonth());
				fec.setDay(stamp.getDay());
			} catch (DatatypeConfigurationException ex) {
				throw new ConstruccionMensajeAncertException ("Error al generar la fecha de operación:"+ ex.getMessage(),ex,this);
			}
			cab.setFECHA(fec);
			String hora=String.format("%02d",stamp.getHour())+":"+String.format("%02d",stamp.getMinute()) +":"+String.format("%02d",stamp.getSecond());
			cab.setHORA(hora);
			cab.setAPLICACION(pr.getAplicacion());
			cab.setIDCOMUNICACION(Long.parseLong(solicitud.getIdSolicitud())); 
			cab.setOPERACION(pr.getOperacion());
			req.setCABECERA(cab);
			//Cuerpo.
			req.setID(pr.getRequestId());
			req.setDESTINATARIO(solicitud.getDestinatario());
			req.setFINALIDAD(solicitud.getFinalidad());
			req.setTIPOCOPIA(pr.getTipoCopia());
			//CRUBENCVS  Versión 2 de servicio 38243
			SERVICIOSESCRITURA.REQUEST.PERSONASOLICITANTE solicitante= new SERVICIOSESCRITURA.REQUEST.PERSONASOLICITANTE();
			solicitante.setDNIRESPONSABLE(solicitud.getPersonaSolicitante().getNif());
			solicitante.setNOMBRERESPONSABLE(solicitud.getPersonaSolicitante().getNombre());
			solicitante.setCARGORESPONSABLE(solicitud.getPersonaSolicitante().getCargo());
			req.setPERSONASOLICITANTE(solicitante);
			
			for (Otorgante ot: solicitud.getOtorgantes())
			{
				SERVICIOSESCRITURA.REQUEST.OTORGANTES ot1 = new SERVICIOSESCRITURA.REQUEST.OTORGANTES();
				ot1.setDNIOTORGANTE(ot.getNif());
				ot1.setNOMBREOTORGANTE(ot.getNombre());
				req.getOTORGANTES().add(ot1);
			}
			req.setTIPOPROCEDIMIENTO(solicitud.getTipoProcedimiento());
			req.setNUMEROEXPEDIENTE(solicitud.getNumeroExpediente());
			String idSesion = (String)context.get(CallContextConstants.ID_SESION);
			GestorDocumental gd = new GestorDocumental(idSesion,pr); 
			Otorgante otorgante1; 
			if (solicitud.getOtorgantes().size()>0) {
				otorgante1 = solicitud.getOtorgantes().get(0); 
			} else {
				otorgante1 = new Otorgante("","");
			}
			req.setFICHERO(gd.getInformeSolicitudEscritura(solicitud.getNumeroExpediente(), 
														   solicitud.getCodigoNotario(), 
														   solicitud.getCodigoNotaria(), 
														   solicitud.getNumProtocolo(),
														   solicitud.getProtocoloBis(),
														   Integer.toString(solicitud.getAnioAutorizacion()), 
														   solicitud.getPersonaSolicitante().getNif(), 
														   solicitud.getPersonaSolicitante().getNombre(), 
														   solicitud.getPersonaSolicitante().getCargo(), 
														   otorgante1.getNif(), 
														   otorgante1.getNombre(), 
														   solicitud.getTipoProcedimiento(), 
														   solicitud.getDestinatario(), 
														   solicitud.getFinalidad(), 
														   solicitud.getIdOrigen()));
			//FIN CRUBENCVS 38243
			//Id escritura
			esc.setCODNOTARIO(solicitud.getCodigoNotario());
			esc.setCODNOTARIA(solicitud.getCodigoNotaria());
			esc.setNUMPROTOCOLO(BigInteger.valueOf(Long.parseLong(solicitud.getNumProtocolo())));
			esc.setNUMBIS(Short.parseShort(solicitud.getProtocoloBis()));
			esc.setANYOAUTORIZACION(solicitud.getAnioAutorizacion());
			esc.setTIPODOCUMENTO("1"); //1 = Protocolo Ordinario. 06/07/2020. Lo ponemos fijo, no podemos saber si es otro.
			req.setIDESCRITURA(esc);
		}
		catch (NumberFormatException ex)
		{
			throw new SystemException ("Error al convertir el protocolo a numérico:"+ ex.getMessage(),ex);
		}
		catch (DocumentoException doc){
			throw new SystemException ("Error al imprimir el informe de solicitud:" + doc.getMessage(),  doc);
		} 
		return req;
	}
	/**
	 * Se construye la petición, en forma de documento DOM para que podamos firmarlo y pasarlo 
	 * como parámetro "ANY", ya que si pasásemos directamente los objetos, nos encontraríamos con
	 * que no puede validar los esquemas de estos objetos de la petición, por no estar publicados
	 * en el WSDL.
	 * @param solicitudData Datos de la solicitud.
	 * @param idSolicitud ID que identificará la solicitud que estamos dando de alta.
	 * @return Documento formado.
	 * @throws ConstruccionMensajeAncertException
	 */
	public Document construyeMensajeSolicitud(SolicitudDO solicitudData,String idSolicitud) throws ConstruccionMensajeAncertException,SystemException
	{
		Document doc=null;
		Preferencias pr=null;
		try {
			//Recuperamos las preferencias del contexto.
			pr = PreferenciasFactory.getPreferenciasContexto(context);
			if (pr==null)
			{
				throw new ConstruccionMensajeAncertException ("No se pueden recuperar las preferencias para la construcción del mensaje con Ancert.",this);
			}
			SERVICIOSESCRITURA serv=new SERVICIOSESCRITURA();
			solicitudData.setIdSolicitud(idSolicitud);
			REQUEST req=construyeCuerpo(solicitudData,pr);
			serv.setREQUEST(req);
			//Convertimos a DOM
			//Contexto donde buscar la definición de los objetos que tenemos montados.
			doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			JAXBContext jbc = JAXBContext.newInstance(SERVICIOSESCRITURA.class.getPackage().getName());//JAXBContext.newInstance(REQUEST.class.getPackage().getName());//JAXBContext.newInstance("es.tributasenasturias.services.ANCERT.serviceDispatcher.types");
			if (jbc!=null)
			{
				//Creamos el objeto que convertirá a DOM
				Marshaller mr = jbc.createMarshaller();
				//Codificará a ISO-8859-1
				mr.setProperty("jaxb.encoding", "ISO-8859-1");
				//Convertimos el objeto de petición a un DOM.
				//Convertimos el SERVICIOSESCRITURA directamente, porque es el que tiene un XmlRootElement
				mr.marshal(serv, doc);
				//Lo firmamos, si la preferencia lo indica.
				if (pr.getFirmaSalida().equalsIgnoreCase("S"))
				{
					doc=firmar(doc);
					if (doc==null)
					{
						throw new ConstruccionMensajeAncertException ("Imposible firmar el mensaje solicitando la escritura a Ancert. No se continúa.",this);
					}
				}
			}
			else{ //Error, debería poder crearse ese contexto.
				throw new ConstruccionMensajeAncertException ("Error.Imposible crear el objeto de mensaje con Ancert",this);
			}
			return doc;
		} 
		catch (JAXBException e) {
			throw new SystemException ("Error al convertir el texto petición al servicio de Ancert a un objeto DOM:" + e.getCause().getMessage(),e.getCause());
		}
		catch (ParserConfigurationException e) {
			throw new SystemException ("Error al crear un nuevo documento DOM para enviar la petición al servicio de Ancert:"+ e.getMessage(),e.getCause());
		}
	}
}
