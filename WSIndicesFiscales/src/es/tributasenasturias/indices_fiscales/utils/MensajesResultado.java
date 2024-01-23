package es.tributasenasturias.indices_fiscales.utils;

public enum MensajesResultado {

	CORRECTO("E0000","Correcto"),
	ERROR_TECNICO("E0011","Error técnico"),
	FICHERO_DUPLICADO("E0012","Envío duplicado. El Índice ya ha sido procesado anteriormente."),
	XML_INVALIDO ("E0092","Mensaje CARGAR_INDICE_FISCAL incorrecto"),
	ESQUEMA_INVALIDO("E0093","Mensaje CARGAR_INDICE_FISCAL no es válido según el esquema"),
	ACCION_INVALIDA("E0094","Acción solicitada no reconocida"),
	NO_MENSAJE("E0095","No se ha recibido ningún mensaje"),
	ERROR_DESCOMPRESION("E0096","No se ha podido descomprimir el fichero");
	
	private String id;
	private String descripcion;
	
	private MensajesResultado(String id, String desc) {
		this.id = id;
		this.descripcion=desc;
	}
	public String getId()
	{
		return id;
	}
	public String getDescripcion(){
		return descripcion;
	}
}
