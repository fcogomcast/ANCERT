package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.types.DOCUMENTACION;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.IDDILIGENCIAType;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ObjectFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.types.DOCUMENTACION.DOCUMENTO;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.CABECERA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.DILIGENCIA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.DILIGENCIA.AUTOLIQUIDACION;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.DILIGENCIA.REGISTROTRIBUTARIOSALIDA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA.REQUEST.DILIGENCIA.AUTOLIQUIDACION.SUJETO;


public class ComunicacionExternaFactory {
	/**
	 * Devuelve un objeto ENVIODILIGENCIAS. Encapsula la llamada a otra factory, esta no tiene nombre 
	 * largo, pero las demás pueden ser ilegibles.
	 * @return Objeto ENVIODILIGENCIA
	 */
	public static ENVIODILIGENCIA newEnvioDiligencia()
	{
			ENVIODILIGENCIA obj = new ObjectFactory().createENVIODILIGENCIA();
			return obj;
	}
	/**
	 * Devuelve un objeto REQUEST. 
	 * @return Objeto REQUEST
	 */
	public static REQUEST newRequest()
	{
			REQUEST obj = new ObjectFactory().createENVIODILIGENCIAREQUEST();
			return obj;
	}
	/**
	 * Devuelve un objeto CABECERA. 
	 * @return Objeto CABECERA
	 */
	public static CABECERA newCabecera()
	{
			CABECERA obj = new ObjectFactory().createENVIODILIGENCIAREQUESTCABECERA();
			return obj;
	}
	/**
	 * Devuelve un objeto DILIGENCIA. 
	 * @return Objeto DILIGENCIA
	 */
	public static DILIGENCIA newDiligencia()
	{
			DILIGENCIA obj = new ObjectFactory().createENVIODILIGENCIAREQUESTDILIGENCIA();
			return obj;
	}
	/**
	 * Devuelve un objeto DOCUMENTACION. 
	 * @return Objeto DOCUMENTACION
	 */
	public static DOCUMENTACION newDocumentacion()
	{
			DOCUMENTACION obj = new ObjectFactory().createDOCUMENTACION();
			return obj;
	}
	/**
	 * Devuelve un objeto IDDILIGENCIAType. 
	 * @return Objeto IDDILIGENCIAType
	 */
	public static IDDILIGENCIAType newIDDiligencia()
	{
			IDDILIGENCIAType obj = new ObjectFactory().createIDDILIGENCIAType();
			return obj;
	}
	/**
	 * Devuelve un objeto DOCUMENTO. 
	 * @return Objeto DOCUMENTO
	 */
	public static DOCUMENTO newDocumento()
	{
			DOCUMENTO obj = new ObjectFactory().createDOCUMENTACIONDOCUMENTO();
			return obj;
	}
	/**
	 * Devuelve un objeto AUTOLIQUIDACION. 
	 * @return Objeto AUTOLIQUIDACION
	 */
	public static AUTOLIQUIDACION newAutoliquidacion()
	{
			AUTOLIQUIDACION obj = new ObjectFactory().createENVIODILIGENCIAREQUESTDILIGENCIAAUTOLIQUIDACION();
			return obj;
	}
	/**
	 * Devuelve un objeto SUJETO. 
	 * @return Objeto SUJETO
	 */
	public static SUJETO newSujeto()
	{
			SUJETO obj = new ObjectFactory().createENVIODILIGENCIAREQUESTDILIGENCIAAUTOLIQUIDACIONSUJETO();
			return obj;
	}
	/**
	 * Devuelve un objeto REGISTROTRIBUTARIOSALIDA. 
	 * @return Objeto REGISTROTRIBUTARIOSALIDA
	 */
	public static REGISTROTRIBUTARIOSALIDA newRegistroTributarioSalida()
	{
			REGISTROTRIBUTARIOSALIDA obj = new ObjectFactory().createENVIODILIGENCIAREQUESTDILIGENCIAREGISTROTRIBUTARIOSALIDA();
			return obj;
	}
	/**
	 * Devuelve un objeto que puede construir el mensaje a ANCERT. 
	 * @return Objeto MensajeANCERT
	 */
	public static MensajeANCERT newMensajeANCERT(CallContext context)
	{
			MensajeANCERT obj = new MensajeANCERT(context);
			return obj;
	}
	/**
	 * Devuelve un objeto que se encargará de enviar el mensaje construido a ANCERT. 
	 * @return Objeto MensajeroAncert
	 */
	public static MensajeroAncert newMensajeroAncert(CallContext context)
	{
			MensajeroAncert obj = new MensajeroAncert(context);
			return obj;
	}
	/**
	 * Devuelve un objeto que traducirá la respuesta recibida de ANCERT. 
	 * @return Objeto TraductorRespuestas
	 */
	public static TraductorRespuestas newTraductorRespuestas(CallContext context)
	{
			TraductorRespuestas obj = new TraductorRespuestas(context);
			return obj;
	}
}
