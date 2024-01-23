package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.domain.intercambio.Persona;

/**
 * Modela una de las órdenes de pago en la presentación 
 * de la liquidación.
 * Se está considerando que hay siempre una orden de pago,
 * aunque sea de importe cero, por tanto no se validará el importe
 * en la orden de pago, aunque no se pagará nada.
 * También, aunque siempre se paga con la misma cuenta, se indica
 * aquí la misma porque también cubre el caso de diferentes
 * cuentas para cada orden, aunque no creo que  sea necesario.
 * @author crubencvs
 *
 */
public class OrdenPago {

	private String ccc;
	private String nifTitular;
	private Persona contribuyente;
	private String importe;
	
	
	public OrdenPago(String ccc, String nifTitular, Persona contribuyente, String importe) {
		super();
		this.ccc = ccc;
		this.nifTitular = nifTitular;
		this.importe= importe;
		this.contribuyente = contribuyente;
	}
	
	public String getCcc() {
		return ccc;
	}
	public String getNifTitular() {
		return nifTitular;
	}
	public Persona getContribuyente() {
		return contribuyente;
	}

	public String getImporte() {
		return importe;
	}
	
	
	
	
	
}
