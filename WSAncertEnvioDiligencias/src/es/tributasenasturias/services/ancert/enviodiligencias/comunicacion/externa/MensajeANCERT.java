package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosExpedienteDO;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosInforme;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosPersonaDO;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosSolicitudDO;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.Base64;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.types.DOCUMENTACION;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.IDDILIGENCIAType;
import es.tributasenasturias.services.ancert.enviodiligencias.types.DOCUMENTACION.DOCUMENTO;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.CABECERA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.DILIGENCIA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.DILIGENCIA.AUTOLIQUIDACION;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.DILIGENCIA.REGISTROTRIBUTARIOSALIDA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.DILIGENCIA.AUTOLIQUIDACION.SUJETO;

/**
 * Clase que implementa la generación de un mensaje para ANCERT de envío de diligencia de presentación.
 * @author crubencvs
 *
 */
public class MensajeANCERT implements IContextReader{

	
	private CallContext context;
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		this.context=ctx;
	}
	private ENVIODILIGENCIA mensaje;

	@SuppressWarnings("unused")
	private MensajeANCERT() {
		super();
	}
	public MensajeANCERT(CallContext context) {
		this();
		this.context=context;
	}
	/**
	 * Construye la parte de "Payload" de mensaje, es decir, no el mensaje SOAP completo sino la parte
	 * que indica el envío de diligencia. Se hace así porque después será necesario incrustarla en el 
	 * mensaje SOAP.
	 * @param datos Datos del informe.
	 * @param pdf PDF del informe.
	 * @throws ConstruccionMensajeException
	 */
	public void construirMensaje(DatosInforme datos, String pdf) throws ConstruccionMensajeException
	{
		mensaje = ComunicacionExternaFactory.newEnvioDiligencia();
		Preferencias pref;
		REQUEST request=ComunicacionExternaFactory.newRequest();
		try {
			pref = PreferenciasFactory.newInstance();
		} catch (PreferenciasException e) {
			throw new ConstruccionMensajeException ("Error al construir el elemento REQUEST de mensaje de salida a ANCERT:"+ e.getMessage(),e);
		}
		request.setID(pref.getIdNodoFirmar());
		//Cabecera
		CABECERA reqCabecera = construirCabecera(datos);
		//Diligencia.
		DILIGENCIA reqDiligencia = construirDiligencia(datos,pdf);
		request.setCABECERA(reqCabecera);
		request.setDILIGENCIA(reqDiligencia);
		//Datos de id de la diligencia
		request.setIDDILIGENCIA(construirIdDiligencia(datos));
		mensaje.setREQUEST(request);
	}
	
	private CABECERA construirCabecera(DatosInforme datos) throws ConstruccionMensajeException
	{
		CABECERA cab=ComunicacionExternaFactory.newCabecera();
		Preferencias pref;
		XMLGregorianCalendar cal;
		try {
			pref = PreferenciasFactory.newInstance();
		} catch (PreferenciasException e) {
			throw new ConstruccionMensajeException ("Error al construir la cabecera de mensaje de salida a ANCERT:"+ e.getMessage(),e);
		}
		cab.setAPLICACION(pref.getAplicacion());
		cab.setEMISOR(pref.getEmisor());
		cab.setRECEPTOR(pref.getReceptor());
		cab.setOPERACION(pref.getOperacion());
		
		try {
			//Calendar hoy=Calendar.getInstance();
			//GregorianCalendar gca = new GregorianCalendar(hoy.get(Calendar.YEAR),hoy.get(Calendar.MONTH),hoy.get(Calendar.DAY_OF_MONTH));
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
			if (cal!=null)
			{
				cab.setHORA(String.format("%1$02d:%2$02d:%3$02d",cal.getHour(),cal.getMinute(),cal.getSecond()));
			}
			//Eliminamos la hora de la fecha.
			cal.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
			//cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gca);
			cab.setFECHA(cal);
		} catch (DatatypeConfigurationException e) {
			throw new ConstruccionMensajeException ("Error al construir la cabecera de mensaje de salida a ANCERT:"+ e.getMessage(),e);
		}
		//En principio el expediente debería ser numérico, y parece que lo identifican por él.
		//¿Recuperar el número de solicitud?. Se podría...
		//Si falla, generamos un número fijo.
		//TODO:
		try
		{
			cab.setIDCOMUNICACION(Long.valueOf(datos.getDatosJustificante().getDatosExpediente().getNumExpediente()));
		}
		catch (NumberFormatException e)
		{
			cab.setIDCOMUNICACION(1L);
		}
		return cab;
	}
	private DOCUMENTACION construirDocumentacion(DatosInforme datos,String pdf) throws ConstruccionMensajeException
	{
		DOCUMENTACION docu = ComunicacionExternaFactory.newDocumentacion();
		DOCUMENTO doc = ComunicacionExternaFactory.newDocumento();
		String numeroAutoliquidacion;
		XMLGregorianCalendar cal;
		try {
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		} catch (DatatypeConfigurationException e) {
			throw new ConstruccionMensajeException ("Error al construir la documentación de mensaje de salida a ANCERT:"+ e.getMessage(),e);
		}
		if (cal!=null) //No debería pasar nunca.
		{
			//Eliminamos la hora de la fecha.
			cal.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
			doc.setFECHADOCUMENTO(cal);
		}
		doc.setFICHERO(Base64.decode(pdf.toCharArray()));
		numeroAutoliquidacion=datos.getDatosJustificante().getDatosExpediente().getNumeroAutoliquidacion();
		doc.setNOMBRE(numeroAutoliquidacion+".pdf");
		doc.setDESCRIPCION("Diligencia de autoliquidación "+numeroAutoliquidacion);
		try
		{
			doc.setTAMANO(BigInteger.valueOf(pdf.length()));
		}
		catch (NumberFormatException ex)
		{
			throw new ConstruccionMensajeException ("Imposible entender el tamaño del fichero a enviar como un número:" +ex.getMessage(),ex);
		}
		docu.setDOCUMENTO(doc);
		return docu;
		
	}
	private REGISTROTRIBUTARIOSALIDA construirRegistroSalida(DatosInforme datos) throws ConstruccionMensajeException
	{
		REGISTROTRIBUTARIOSALIDA registro=ComunicacionExternaFactory.newRegistroTributarioSalida();
		XMLGregorianCalendar cal;
		try {
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		} catch (DatatypeConfigurationException e) {
			throw new ConstruccionMensajeException ("Error al construir la sección de registro de salida del mensaje de salida a ANCERT:"+ e.getMessage(),e);
		}
		registro.setFECHAHORA(cal);
		//Igual que antes, el expediente debería ser numérico, pero si no es así, pondremos un número cualquiera.
		//TODO: Número de expediente.
		try
		{
			registro.setNUMERO(Long.valueOf(datos.getDatosJustificante().getDatosExpediente().getNumExpediente()));
		}
		catch (NumberFormatException ex)
		{
			registro.setNUMERO(1L);
		}
		return registro;
	}
	private AUTOLIQUIDACION construirAutoLiquidacion(DatosInforme datos) throws ConstruccionMensajeException
	{
		AUTOLIQUIDACION auto=ComunicacionExternaFactory.newAutoliquidacion();
		DatosExpedienteDO expediente = datos.getDatosJustificante().getDatosExpediente();
		auto.setMODELO(expediente.getModelo());
		//auto.setCSV("C"+expediente.getNumeroAutoliquidacion()+"-"+datos.getCodigoVerificacion());
		auto.setCONCEPTO(expediente.getDatoEspecifico());
		auto.setNUMERODOCUMENTO(expediente.getNumeroAutoliquidacion());
		auto.setNRC(expediente.getNrc());
		auto.setIMPORTEINGRESADO(BigDecimal.valueOf(Long.valueOf(expediente.getImporteAutoliq())/100)); //En la base está expresado en céntimos.
		SUJETO sujeto = ComunicacionExternaFactory.newSujeto();
		DatosPersonaDO sujetoPasivo=datos.getDatosJustificante().getDatosSujetoPasivo();
		sujeto.setNUMDOCID(sujetoPasivo.getNif());
		sujeto.setNOMBRERAZONSOCIAL(sujetoPasivo.getNombre());
		auto.setSUJETO(sujeto);
		return auto;
	}
	private IDDILIGENCIAType construirIdDiligencia (DatosInforme datos) throws ConstruccionMensajeException
	{
		DatosSolicitudDO sol = datos.getDatosSolicitud();
		IDDILIGENCIAType id= ComunicacionExternaFactory.newIDDiligencia();
		id.setCODNOTARIO(sol.getCodNotario());
		id.setCODNOTARIA(sol.getCodNotaria());
		if (!sol.getNumProtocolo().equals("") && !sol.getProtocoloBis().equals(""))
		{
			try
			{
				id.setNUMPROTOCOLO(BigInteger.valueOf(Long.valueOf(sol.getNumProtocolo())));
				id.setNUMBIS(Short.valueOf(sol.getProtocoloBis()));
			}
			catch (NumberFormatException ex)
			{
				throw new ConstruccionMensajeException ("Imposible entender el protocolo y protocolo bis de la solicitud como números:"+ ex.getMessage(),ex);
			}	
		}
		id.setANYOAUTORIZACION(Integer.valueOf(sol.getAnioAutorizacion()));
		return id;
	}
	private DILIGENCIA construirDiligencia(DatosInforme datos,String pdf) throws ConstruccionMensajeException
	{
		DILIGENCIA di=ComunicacionExternaFactory.newDiligencia();
		
		di.setIDEXPEDIENTE(datos.getDatosJustificante().getDatosExpediente().getNumExpediente());
		//TODO: Podemos utilizar el número de autoliquidación, o el de la solicitud.
		di.setNUMERODOCUMENTO(datos.getDatosJustificante().getDatosExpediente().getNumeroAutoliquidacion());
		di.setDOCUMENTACION(construirDocumentacion(datos,pdf));
		di.setREGISTROTRIBUTARIOSALIDA(construirRegistroSalida(datos));
		di.getAUTOLIQUIDACION().add(construirAutoLiquidacion(datos));
		di.setCSV("C"+datos.getDatosJustificante().getDatosExpediente().getNumeroAutoliquidacion()+"-"+datos.getCodigoVerificacion());
		return di;
	}
	public ENVIODILIGENCIA getMensajeAncert()
	{
		return mensaje;
	}
}
