package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

public class CalculadoraTransmisionContribuyente extends CalculadoraLiquidacionContribuyente{

	public CalculadoraTransmisionContribuyente(Finca finca, Tramite tramite,MediadorBD bd){
		super(finca, tramite, bd);
	}
	
	/**
	 * Calcula la liquidación con las reglas de transmisión, para un transmitente concreto
	 * En este caso, dado que el contribuyente es el transmitente (no contemplamos caso de transmitentes residentes
	 * fuera de España), no necesitamos otro dato.
	 * Si se contemplase, tendría que pasarse también el adquirente y asociar el cálculo a él.
	 * @param transmitente Transmitente para el que calcular la plusvalía.
	 * @return
	 * @throws PlusvaliaException
	 */
	//El cálculo de transmisiones depende sólo del transmitente
	public LiquidacionContribuyente calcular(Transmitente transmitente, Adquirente adquirente) throws PlusvaliaException{
		return super.calcular(transmitente, adquirente, transmitente);
	}
}
