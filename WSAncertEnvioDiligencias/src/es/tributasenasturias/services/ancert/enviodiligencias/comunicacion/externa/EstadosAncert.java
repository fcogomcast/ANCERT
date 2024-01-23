package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa;

/**
 * Estados de respuesta de  Ancert.
 * @author crubencvs
 *
 */
public enum EstadosAncert {
	XML_INVALIDO,SIN_FIRMA,FIRMA_NO_VALIDA,ESQUEMA_INVALIDO,NOTARIO_VACIO,PROTOCOLO_VACIO,PROTOCOLO_BIS_ERRONEO,
	ANIO_AUTORIZACION_VACIO,CONCEPTO_INCORRECTO,DATOS_INCORRECTOS,NOTARIO_INVALIDO,ERROR_DESCONOCIDO,OK
}
