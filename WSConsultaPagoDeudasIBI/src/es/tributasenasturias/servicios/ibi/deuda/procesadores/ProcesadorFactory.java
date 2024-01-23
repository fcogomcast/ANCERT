package es.tributasenasturias.servicios.ibi.deuda.procesadores;

import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;




/**
 * Factory para crear los objetos procesadores, los que implementan "InterfazDeudaIBI".
 * @author crubencvs
 *
 */
public class ProcesadorFactory {
	/**
	 * Devuelve el objeto que procesará la consulta de deuda.
	 * @return Objeto que implementa {@link InterfazDeudaIBI} y que procesará la consulta de deuda.
	 */
	public static InterfazDeudaIBI getProcesadorConsulta()
	{
		return new ConsultaDeudaProcesador();
	}
	/**
	 * Devuelve el objeto que procesará la consulta de deuda, pasando un contexto de llamada
	 * @param contexto Objeto que encapsula datos acerca de la llamada al servicio web en que se crea este objeto.
	 * @return Objeto que implementa {@link InterfazDeudaIBI} y que procesará la consulta de deuda.
	 */
	public static InterfazDeudaIBI getProcesadorConsulta(CallContext contexto)
	{
		return new ConsultaDeudaProcesador(contexto);
	}
	/**
	 * Devuelve el objeto que procesará el pago de la deuda.
	 * @return Objeto que implementa {@link InterfazDeudaIBI} y que procesará el pago de deuda.
	 */
	public static InterfazDeudaIBI getProcesadorPago()
	{
		return new PagoDeudaProcesador();
	}
	/**
	 * Devuelve el objeto que procesará el pago de la deuda, pasando un contexto de llamada
	 * @param contexto Objeto que encapsula datos acerca de la llamada al servicio web en que se crea este objeto.
	 * @return Objeto que implementa {@link InterfazDeudaIBI} y que procesará el pago de deuda.
	 */
	public static InterfazDeudaIBI getProcesadorPago(CallContext contexto)
	{
		return new PagoDeudaProcesador(contexto);
	}
}
