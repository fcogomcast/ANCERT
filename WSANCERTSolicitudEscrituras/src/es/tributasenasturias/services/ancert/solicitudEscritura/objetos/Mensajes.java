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
		mensajes.put(duplicado, "Solicitud duplicada. Existe una solicitud previa de la que no se ha recibido a�n respuesta.");
		mensajes.put(notarioInvalido, "El c�digo de notario no es v�lido.");
		mensajes.put(errorGeneral, "Error inesperado en el proceso de solicitud.");
		mensajes.put(protocoloIncorrecto, "El protocolo indicado no tiene el formato correcto.");
		mensajes.put(faltanParametros, "Faltan par�metros requeridos en la entrada de servicio.");
		mensajes.put(errorBD, "Se ha producido un error inesperado en el acceso a la base de datos.");
		mensajes.put(errorDesconocidoAncert, "Recibido error desconocido del servicio de solicitud de escritura de CGN.");
		mensajes.put(errorSeguridad, "El mensaje recibido de ANCERT no cumple las especificaciones de seguridad (firma/certificado).");
		mensajes.put(errorMensajeria, "Se ha producido un error durante la construcci�n o el env�o del mensaje de solicitud a ANCERT.");
		mensajes.put(errorTecnicoAncert, "El mensaje enviado a ANCERT ha sido rechazado por motivos t�cnicos.");
		//
		//
		//
		//Mensajes de ANCERT
		mensajesANCERT=new HashMap<String,String>();
		mensajesANCERT.put("E00000", "Correcto.");
		mensajesANCERT.put("E00001","Error t�cnico");
		mensajesANCERT.put("E00002","Error de validaci�n, el documento no cumple el schema.");
		mensajesANCERT.put("E00003","Error de formato en xml.");
		mensajesANCERT.put("E00004","No se ha encontrado el notario.");
		mensajesANCERT.put("E00005","Error validando la firma.");
		mensajesANCERT.put("E00007","Petici�n duplicada.La petici�n ya hab�a sido procesada anteriormente.");
		mensajesANCERT.put("E00008","El identificador de emisor no es v�lido.");
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
	 * @return c�digo de mensaje correcto
	 */
	public static String getOk() {
		return Ok;
	}
	/**
	 * @return C�digo de mensaje duplicado
	 */
	public static String getDuplicado() {
		return duplicado;
	}
	/**
	 * @return C�digo de mensaje de notario inv�lido.
	 */
	public static String getNotarioInvalido() {
		return notarioInvalido;
	}
	/**
	 * @return c�digo de mensaje de error General
	 */
	public static String getErrorGeneral() {
		return errorGeneral;
	}
	/**
	 * @return C�digo de mensaje de error en base de Datos.
	 */
	public static String getErrorBD() {
		return errorBD;
	}
	/**
	 * @return C�digo de mensaje de Protocolo Incorrecto.
	 */
	public static String getProtocoloIncorrecto() {
		return protocoloIncorrecto;
	}
	/**
	 * @return C�digo de mensaje faltan par�metros requeridos en la entrada.
	 */
	public static String getFaltanParametrosRequeridos() {
		return faltanParametros;
	}
	/**
	 * @return c�digo de mensaje de error T�cnico Ancert
	 */
	public static String getTecnicoAncert() {
		return errorTecnicoAncert;
	}
	/**
	 * @return C�digo de mensaje de error de seguridad.
	 */
	public static String getErrorSeguridad() {
		return errorSeguridad;
	}
	/**
	 * @return C�digo de mensaje de error en mensajer�a con la comunicaci�n ANCERT.
	 */
	public static String getErrorMensajeria() {
		return errorMensajeria;
	}
	/**
	 * Recupera un mensaje de error del servicio en funci�n del c�digo que se pasa.
	 * @param code - c�digo de error de mensaje (propio).
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
	 * Devuelve un texto de error de nuestro servicio en funci�n del c�digo de mensaje de ANCERT.
	 * @param codeANCERT -C�digo del mensaje de ANCERT.
	 * @return -  Texto del mensaje propio equivalente al de ANCERT.
	 */
	public static String getMessageFromANCERT(String codeANCERT)
	{
		String mensaje="";
		String codeErrorPropio=""; //El c�digo de error propio, de nuestro servicio.
		codeErrorPropio=getCodeFromANCERT(codeANCERT);
		mensaje=getMessage(codeErrorPropio);
		return mensaje;
	}
	/**
	 * Devuelve el c�digo de error de nuestro servicio equivalente al c�digo de error de ANCERT
	 * @param codeANCERT - C�digo del mensaje de ANCERT
	 * @return - C�digo de error propio.
	 */
	public static String getCodeFromANCERT(String codeANCERT)
	{
		String codeErrorPropio=""; //El c�digo de error propio, de nuestro servicio.
		if (mapeoMensajes.containsKey(codeANCERT))
		{   //C�digo propio equivalente al de ANCERT.
			codeErrorPropio=mapeoMensajes.get(codeANCERT); 
		}
		else
		{
			codeErrorPropio=errorDesconocidoAncert;
		}
		return codeErrorPropio;
	}
	
}
