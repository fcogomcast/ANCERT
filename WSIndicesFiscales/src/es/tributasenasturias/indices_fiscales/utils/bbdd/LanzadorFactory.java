/**
 * 
 */
package es.tributasenasturias.indices_fiscales.utils.bbdd;

import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * Utilizar para crear objetos del interfaz de Lanzador.
 * No crearlos directamente si es posible.
 * @author crubencvs
 *
 */
public class LanzadorFactory {
	private LanzadorFactory(){};
	
	/**
	 * Devuelve una instancia {@link TLanzador} para ejecutar peticiones al lanzador.
	 * @param endPoint Endpoint de lanzador. 
	 * @return instancia TLanzador
	 */
	public static TLanzador newTLanzador(String endPoint) throws LanzadorException
	{
		return new TLanzador(endPoint);
	}
	/**
	 * Devuelve una instancia {@link TLanzador} para ejecutar peticiones al lanzador. 
	 * Se permite incluir un SOAPHandler para indicar cuál será el manejador de los mensajes
	 * SOAP al lanzador.
	 * @param endPoint Endpoint de lanzador.
	 * @param soapHandler Una instancia de {@link SOAPHandler} para manejar mensajes. 
	 * @return instancia TLanzador
	 */
	public static TLanzador newTLanzador(String endPoint,SOAPHandler<SOAPMessageContext> soapHandler) throws LanzadorException
	{
		TLanzador t = new TLanzador(endPoint);
		t.setSoapHandler(soapHandler);
		return t;
	}
	/**
	 * Devuelve una instancia {@link TParam} para almacenar datos de un parámetro.
	 * @param  tipo Tipo del Parámetro
	 * @param valor Valor del parámetro
	 * @param formato Formato del parámetro,  para tipos de fecha.
	 * @return instancia TParam
	 */
	public static TParam newTParam(ParamType tipo, String valor, String formato)
	{
		return new TParam(tipo,valor,formato);
	}
	/**
	 * Devuelve una instancia {@link TPeticion} para construir una petición a base de datos.
	 * @param  procName Nombre del procedimiento almacenado que se desea ejecutar
	 * @param esquema Nombre del esquema de base de datos contra el que se ejecutará el procedimiento almacenado.
	 * @return instancia TPeticion
	 */
	public static TPeticion newTPeticion(String procName, String esquema) 
	{
		return new TPeticion(procName, esquema);
	}
}
