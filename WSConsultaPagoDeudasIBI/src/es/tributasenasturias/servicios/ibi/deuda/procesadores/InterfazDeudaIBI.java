package es.tributasenasturias.servicios.ibi.deuda.procesadores;

import es.tributasenasturias.servicios.ibi.deuda.MESSAGEREQUEST;
import es.tributasenasturias.servicios.ibi.deuda.MESSAGERESPONSE;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;

/**
 * Interfaz que tienen que implementar todas las clases que quieran procesar el mensaje de entrada.
 * Ser�n las que ofrezcan el proceso de la consulta y del pago de deuda.
 * @author crubencvs
 *
 */
public interface InterfazDeudaIBI {
	/**
	 * Procesa la petici�n.
	 * @param partRequest Mensaje de entrada
	 * @return MESSAGERESPONSE con el mensaje de respuesta.
	 */
	public MESSAGERESPONSE process(MESSAGEREQUEST partRequest) throws IBIException;
}
