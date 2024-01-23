package es.tributasenasturias.servicios.ibi.deuda;

import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;
import es.tributasenasturias.servicios.ibi.deuda.procesadores.InterfazDeudaIBI;
import es.tributasenasturias.servicios.ibi.deuda.procesadores.ProcesadorFactory;

/**
 * Utilizado para recuperar el objeto que tratará el mensaje de entrada. 
 * Como el mensaje es único, hay que utilizarla para que devuelva un objeto que trate
 * la consulta o el pago de la deuda
 * @author crubencvs
 *
 */
public class SelectorOperacion {
	public static InterfazDeudaIBI seleccionaProcesador (MESSAGEREQUEST partRequest, CallContext contexto) throws IBIException
	{
		InterfazDeudaIBI procesador;
		if (partRequest.getCONSULTADEUDAS()!=null)
		{
			procesador = ProcesadorFactory.getProcesadorConsulta(contexto);
		}
		else if (partRequest.getPAGODEUDAS()!=null)
		{
			procesador = ProcesadorFactory.getProcesadorPago(contexto);
		}
		else
		{
			throw new IBIException ("No se han recibido datos ni de la consulta ni del pago de deuda de IBI.");
		}
		return procesador;
	}
}
