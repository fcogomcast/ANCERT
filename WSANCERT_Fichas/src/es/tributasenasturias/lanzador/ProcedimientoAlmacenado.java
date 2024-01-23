/**
 * 
 */
package es.tributasenasturias.lanzador;

/**
 * Clase de utilidad que permitirá modelar las peticiones de manera similar a las llamadas de base de 
 * datos del ASP.
 * El propósito es facilitar el uso de las llamadas a base de datos.
 * @author crubencvs
 *
 */
public class ProcedimientoAlmacenado {
	@SuppressWarnings("unused")
	private ProcedimientoAlmacenado() {};
	private TPeticion peticion;
	
	/**
	 * Crea un nuevo objeto, para realizar una llamada a un procedimiento almacenado.
	 * @param nombreProcedimiento Nombre del procedimiento que se quiere llamar
	 * @param esquema Nombre del esquema de base de datos que se va a utilizar para llamar a base de datos.
	 */
	public ProcedimientoAlmacenado (String nombreProcedimiento, String esquema)
	{
		peticion = LanzadorFactory.newTPeticion(nombreProcedimiento, esquema);
	}
	/**
	 * Añade un parámetro a la llamada.
	 * @param valor Valor del parámetro
	 * @param tipo Tipo del parámetro, de los tipos {@link ParamType}
	 */
	public void param (String valor, ParamType tipo)
	{
		this.param(valor,tipo,"");
	}
	/**
	 * Añade un parámetro a la llamada
	 * @param valor Valor del parámetro
	 * @param tipo Tipo del parámetro, de los tipos {@link ParamType}
	 * @param formato Formato del parámetro, sólo se utiliza para fechas.
	 */
	public void param (String valor, ParamType tipo, String formato)
	{
		peticion.addParam(LanzadorFactory.newTParam(tipo, valor, formato));
	}
	/**
	 * @return petición interna que utiliza este objeto para llamar a base de datos
	 */
	public TPeticion getPeticion() {
		return peticion;
	}
	/**
	 * @param peticion La petición interna que utiliza este objeto para llamar a base de datos.
	 */
	public void setPeticion(TPeticion peticion) {
		this.peticion = peticion;
	}
}
