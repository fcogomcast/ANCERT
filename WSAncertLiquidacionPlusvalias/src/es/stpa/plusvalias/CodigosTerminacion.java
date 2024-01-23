package es.stpa.plusvalias;


import java.util.HashMap;
import java.util.Map;

public final class CodigosTerminacion {
	
	private static Map<String,String> codigos= new HashMap<String,String>();
	
	static {
		codigos.put("0000", "Terminaci�n correcta");
		codigos.put("0050", "Error en la consulta de Exenciones y Bonificaciones");
		codigos.put("0100", "Error en el c�lculo de la liquidaci�n de plusval�a: {0}");
		codigos.put("0105", "La suma de porcentajes de adquisici�n anteriores no suma el 100%");
		codigos.put("0106", "Problema t�cnico. El c�lculo genera un importe de liquidaci�n vac�o");
		codigos.put("0107", "Se ha indicado una clase de derecho \"usufructo vitalicio\", pero no se ha indicado la edad del usufructuario");
		codigos.put("0108", "Se ha indicado una clase de derecho \"usufructo temporal\", pero no se han indicado los a�os de usufructo");
		codigos.put("0109", "Se ha indicado una clase de derecho \"uso o habitaci�n vitalicio\", pero no se ha indicado la edad de uso");
		codigos.put("0110", "Se ha indicado una clase de derecho \"uso o habitaci�n temporal\", pero no se han indicado los a�os de uso");		
		codigos.put("0111", "Se ha indicado una clase de derecho \"nuda propiedad\", pero no se ha indicado o bien la edad del usufructuario, si es vitalicio, o los a�os de usufructo, si es temporal");
		codigos.put("0112", "El c�digo de notario titular no existe en nuestro sistema para la fecha de autorizaci�n");
		codigos.put("0113", "El c�digo de notario autorizante no existe en nuestro sistema para la fecha de autorizaci�n");
		codigos.put("0114", "El c�digo de acto jur�dico no tiene una correspondencia en nuestro sistema para ese c�digo de acto, clase de derecho y edad de usufructuario/a�os de usufructo, en su caso");
		codigos.put("0115", "No se han recibido transmitentes/donantes/causantes");
		codigos.put("0116", "Se ha recibido un causante sin fecha de defunci�n");
		codigos.put("0117", "No se han podido recuperar datos de la finca con esa referencia catastral y municipio");
		codigos.put("0118", "No se han recibido adquirentes/donatarios/herederos");
		codigos.put("0119", "El NIF {0} del transmitente con ID {1} es inv�lido");
		codigos.put("0120", "El NIF {0} del adquirente con ID {1} es inv�lido");
		codigos.put("0121", "El NIF {0} del representante con ID {1} es inv�lido");
		codigos.put("0122", "El ayuntamiento {0} no est� soportado por el sistema");
		//CRUBENCVS 20/07/2023. No se pueden realizar c�lculos anteriores al cambio de normativa
		codigos.put("0123", "No se pueden realizar c�lculos con fecha de devengo anterior a 10/11/2021");
		codigos.put("0200", "Error en la presentaci�n de la liquidaci�n de plusval�a: {0}");
		codigos.put("0205", "Error, una de las �rdenes de pago referencia al ID_SUJETO {0} inexistente");
		codigos.put("0206", "Error, una de las �rdenes de pago presenta un importe que no coincide con el del c�lculo");
		codigos.put("0207", "No se han enviado datos de pago, y no se est� exento del mismo.");
		codigos.put("0208", "Se han indicado datos bancarios pero no se indica ning�n importe de pago");
		codigos.put("0209", "No se ha enviado orden de pago para el contribuyente con ID {0}");
		codigos.put("0210", "Error en pago del contribuyente con ID {0}:{1}");
		codigos.put("0211", "Error en integraci�n de modelo en base de datos");
		codigos.put("0212", "Error en presentaci�n del modelo");
		codigos.put("0213", "Las �rdenes de pago no se corresponden con los contribuyentes del c�lculo");
		

		codigos.put("9999", "Error en el proceso");
	}
	
	public static final String OK="0000";
	public static final String ERROR_BONIFICACIONES="0050";
	public static final String ERROR_GENERAL_CALCULO="0100";
	public static final String ERROR_CALCULO_PORCENTAJES="0105";
	public static final String ERROR_CALCULO_IMPORTE_VACIO="0106";
	public static final String ERROR_EDAD_USUFRUCTUARIO="0107";
	public static final String ERROR_ANIOS_USUFRUCTO="0108";
	public static final String ERROR_EDAD_USO="0109";
	public static final String ERROR_ANIOS_USO="0110";
	public static final String ERROR_NUDA_PROPIEDAD_NO_EDAD_ANIOS="0111";
	public static final String ERROR_NOTARIO_TITULAR="0112";
	public static final String ERROR_NOTARIO_AUTORIZANTE="0113";
	public static final String ERROR_CORRESPONDENCIA_ACTO_JURIDICO="0114";
	public static final String ERROR_NO_TRANSMITENTES="0115";
	public static final String ERROR_CAUSANTE_NO_FECHA_DEF="0116";
	public static final String ERROR_REFERENCIA_CATASTRAL="0117";
	public static final String ERROR_NO_ADQUIRENTES="0118";
	public static final String ERROR_NIF_TRANSMITENTE="0119";
	public static final String ERROR_NIF_ADQUIRENTE="0120";
	public static final String ERROR_NIF_REPRESENTANTE="0121";
	public static final String ERROR_AYUNTAMIENTO="0122";
	public static final String ERROR_FECHA_CAMBIO_NORMATIVA="0123";
	public static final String ERROR_GENERAL_PRESENTACION="0200";
	public static final String ERROR_ID_SUJETO_INEXISTENTE="0205";
	public static final String ERROR_IMPORTE_PAGO_INCORRECTO="0206";
	public static final String ERROR_NO_DATOS_PAGO="0207";
	public static final String ERROR_NO_IMPORTE_PAGO="0208";
	public static final String ERROR_NO_ORDEN_CONTRIBUYENTE="0209";
	public static final String ERROR_PAGO="0210";
	public static final String ERROR_INTEGRACION_MODELO="0211";
	public static final String ERROR_PRESENTACION_MODELO="0212";
	public static final String ERROR_GENERAL_ORDENES="0213";
	public static final String ERROR_GENERAL="9999";
	private String codigo;
	private String mensaje;
	
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	private CodigosTerminacion(String codigo, String mensaje){
		this.codigo= codigo;
		this.mensaje= mensaje;
	}
	
	public static CodigosTerminacion newInstance (String codigo){
		if (codigos.containsKey(codigo)){
			return new CodigosTerminacion(codigo, codigos.get(codigo));
		} else {
			return new CodigosTerminacion(ERROR_GENERAL, codigos.get(ERROR_GENERAL));
		}
	}
	/**
	 * Permite recuperar el mensaje asociado a un c�digo. �til para log.
	 * @param codigo
	 * @return
	 */
	public static String getMessage(String codigo){
		return getMessage(codigo,new String[0]);
	}
	/**
	 * Devuelve el mensaje asociado al c�digo, pero con los par�metros posicionales
	 * que se indican en la cadena sustituidos por los argumentos.
	 * Si hay menos par�metros posicionales en la cadena que argumentos, se ignoran
	 * los argumentos extra.
	 * Si hay menos argumentos que par�metros posicionales, los par�metros posicionales
	 * que no tengan un argumento relacionado no tendr�n valor.
	 * @param codigo C�digo de terminaci�n
	 * @param argumentos Argumentos con los que sustituir los par�metros posicionales
	 * @return
	 */
	public static String getMessage(String codigo, String... argumentos) {
		//No uso MessageFormat porque tiene ciertas caracter�sticas que hay 
		//que recordar, como el uso de comillas simples, y lo que voy a 
		// hacer es muy simple.
		// Esta soluci�n genera m�s cadenas (una por par�metro de sustituci�n), pero se esperan pocos argumentos
		if (codigos.containsKey(codigo)){
			String mensaje= codigos.get(codigo);
			for (int i=0;i<argumentos.length;i++){
				//Mal rendimiento ante cadenas largas o muchos (>1000) argumentos
				mensaje= mensaje.replace("{"+i+"}", argumentos[i]);
			}
			return mensaje;
		} else {
			return codigos.get(ERROR_GENERAL);
		}
	}
	public static CodigosTerminacion getTerminacionCorrecta(){
		return newInstance("0000");
		
	}
	
	public static CodigosTerminacion getErrorExencionBonificacion(){
		return newInstance("0050");
	}
	
	public static CodigosTerminacion getErrorCalculoLiquidacion(){
		return newInstance("0100");
	}
	
	public static CodigosTerminacion getErrorGeneral(){
		return newInstance("9999");
	}
	
	
	
	
}
