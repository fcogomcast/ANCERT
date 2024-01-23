package es.tributasenasturias.services.ancert.solicitudEscritura.objetos;

import java.util.HashMap;
import java.util.Map;

public class Mensajes {
	private static final String Ok="0000";
	private static final String duplicado="0001";
	private static final String protocoloIncorrecto="0002";
	private static final String faltanParametros="0003";
	private static final String errorGeneral="9999";
	private static final String errorDesconocidoAncert="9998";
	private static final String errorTecnicoAncert="9997";
	private static final String errorSeguridad="0004";
	private static final String errorMensajeria="0005";
	private static final String notarioInvalido="0006";
	private static final String errorBD="0007";
	private static Map<String, String> mensajes;
	private static Map<String,String> mensajesANCERT;
	private static Map<String,String> mapeoMensajes;
	static
	{
		mensajes=new HashMap<String,String>();
		mensajes.put(Ok, "Solicitud enviada correctamente.");
		mensajes.put(duplicado, "Solicitud duplicada. Existe una solicitud previa de la que no se ha recibido aún respuesta.");
		mensajes.put(notarioInvalido, "El código de notario no es válido.");
		mensajes.put(errorGeneral, "Error inesperado en el proceso de solicitud.");
		mensajes.put(protocoloIncorrecto, "El protocolo indicado no tiene el formato correcto.");
		mensajes.put(faltanParametros, "Faltan parámetros requeridos en la entrada de servicio.");
		mensajes.put(errorBD, "Se ha producido un error inesperado en el acceso a la base de datos.");
		mensajes.put(errorDesconocidoAncert, "Recibido error desconocido del servicio de solicitud de escritura de CGN.");
		mensajes.put(errorSeguridad, "El mensaje recibido de ANCERT no cumple las especificaciones de seguridad (firma/certificado).");
		mensajes.put(errorMensajeria, "Se ha producido un error durante la construcción o el envío del mensaje de solicitud a ANCERT.");
		mensajes.put(errorTecnicoAncert, "El mensaje enviado a ANCERT ha sido rechazado por motivos técnicos.");
		//
		//
		//
		//Mensajes de ANCERT
		mensajesANCERT=new HashMap<String,String>();
		mensajesANCERT.put("E00000", "Correcto.");
		mensajesANCERT.put("E00001","Error técnico");
		mensajesANCERT.put("E00002","Error de validación, el documento no cumple el schema.");
		mensajesANCERT.put("E00003","Error de formato en xml.");
		mensajesANCERT.put("E00004","No se ha encontrado el notario.");
		mensajesANCERT.put("E00005","Error validando la firma.");
		mensajesANCERT.put("E00007","Petición duplicada.La petición ya había sido procesada anteriormente.");
		mensajesANCERT.put("E00008","El identificador de emisor no es válido.");
		//
		//
		//
		//Mapeo de mensajes de ANCERT a nuestro servicio.
		mapeoMensajes = new HashMap<String,String>();
		mapeoMensajes.put("E00000", Ok);
		mapeoMensajes.put("E00001", errorTecnicoAncert);
		mapeoMensajes.put("E00002", errorTecnicoAncert);
		mapeoMensajes.put("E00003",errorTecnicoAncert);
		mapeoMensajes.put("E00004", notarioInvalido);
		mapeoMensajes.put("E00005", errorTecnicoAncert);
		mapeoMensajes.put("E00007", duplicado);
		mapeoMensajes.put("E00008", errorTecnicoAncert);
	}
	/**
	 * @return código de mensaje correcto
	 */
	public static String getOk() {
		return Ok;
	}
	/**
	 * @return Código de mensaje duplicado
	 */
	public static String getDuplicado() {
		return duplicado;
	}
	/**
	 * @return Código de mensaje de notario inválido.
	 */
	public static String getNotarioInvalido() {
		return notarioInvalido;
	}
	/**
	 * @return código de mensaje de error General
	 */
	public static String getErrorGeneral() {
		return errorGeneral;
	}
	/**
	 * @return Código de mensaje de error en base de Datos.
	 */
	public static String getErrorBD() {
		return errorBD;
	}
	/**
	 * @return Código de mensaje de Protocolo Incorrecto.
	 */
	public static String getProtocoloIncorrecto() {
		return protocoloIncorrecto;
	}
	/**
	 * @return Código de mensaje faltan parámetros requeridos en la entrada.
	 */
	public static String getFaltanParametrosRequeridos() {
		return faltanParametros;
	}
	/**
	 * @return código de mensaje de error Técnico Ancert
	 */
	public static String getTecnicoAncert() {
		return errorTecnicoAncert;
	}
	/**
	 * @return Código de mensaje de error de seguridad.
	 */
	public static String getErrorSeguridad() {
		return errorSeguridad;
	}
	/**
	 * @return Código de mensaje de error en mensajería con la comunicación ANCERT.
	 */
	public static String getErrorMensajeria() {
		return errorMensajeria;
	}
	/**
	 * Recupera un mensaje de error del servicio en función del código que se pasa.
	 * @param code - código de error de mensaje (propio).
	 * @return - Texto de mensaje.
	 */
	public static String getMessage(String code)
	{
		String mensaje="";
		if (mensajes.containsKey(code))
		{
			mensaje=mensajes.get(code);
		}
		return mensaje;
	}
	/**
	 * Devuelve un texto de error de nuestro servicio en función del código de mensaje de ANCERT.
	 * @param codeANCERT -Código del mensaje de ANCERT.
	 * @return -  Texto del mensaje propio equivalente al de ANCERT.
	 */
	public static String getMessageFromANCERT(String codeANCERT)
	{
		String mensaje="";
		String codeErrorPropio=""; //El código de error propio, de nuestro servicio.
		codeErrorPropio=getCodeFromANCERT(codeANCERT);
		mensaje=getMessage(codeErrorPropio);
		return mensaje;
	}
	/**
	 * Devuelve el código de error de nuestro servicio equivalente al código de error de ANCERT
	 * @param codeANCERT - Código del mensaje de ANCERT
	 * @return - Código de error propio.
	 */
	public static String getCodeFromANCERT(String codeANCERT)
	{
		String codeErrorPropio=""; //El código de error propio, de nuestro servicio.
		if (mapeoMensajes.containsKey(codeANCERT))
		{   //Código propio equivalente al de ANCERT.
			codeErrorPropio=mapeoMensajes.get(codeANCERT); 
		}
		else
		{
			codeErrorPropio=errorDesconocidoAncert;
		}
		return codeErrorPropio;
	}
	
}
