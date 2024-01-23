package es.stpa.plusvalias.domain;

import java.util.ArrayList;
import java.util.List;

import es.stpa.plusvalias.domain.intercambio.Persona;

/**
 * Lista de las órdenes de pago que se han presentado.
 * 
 * @author crubencvs
 *
 */
public class OrdenesPago {

	private List<OrdenPago> ordenes= new ArrayList<OrdenPago>();

	/**
	 * Añade una orden de pago
	 * @param op
	 */
	public void add(OrdenPago op){
		ordenes.add(op);
	}
	
	/**
	 * Recupera la orden de pago para la persona que se indica
	 * @param interviniente
	 * @return
	 */
	public OrdenPago getOrdenPagoPersona(Persona interviniente){
		if (interviniente==null){
			return null;
		}

		return getOrdenPagoPersona(String.valueOf(interviniente.getIdentificador()));
	}
	
	/**
	 * Recupera la orden de pago en función del identificador de interviniente
	 * @param idInterviniente
	 * @return
	 */
	public OrdenPago getOrdenPagoPersona(String idInterviniente){
		for (OrdenPago p: ordenes){
			if (idInterviniente.equals(String.valueOf(p.getContribuyente().getIdentificador()))) {
				return p;
			}
		}
		return null;
	}
	/**
	 * Devuelve el total de órdenes de pago
	 * @return
	 */
	public int length(){
		return ordenes.size();
	}
}
