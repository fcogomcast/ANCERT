package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.domain.intercambio.Persona;

/**
 * Modela una de las �rdenes de pago en la presentaci�n 
 * de la liquidaci�n.
 * Se est� considerando que hay siempre una orden de pago,
 * aunque sea de importe cero, por tanto no se validar� el importe
 * en la orden de pago, aunque no se pagar� nada.
 * Tambi�n, aunque siempre se paga con la misma cuenta, se indica
 * aqu� la misma porque tambi�n cubre el caso de diferentes
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
