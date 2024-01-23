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
	 * Calcula la liquidaci�n con las reglas de transmisi�n, para un transmitente concreto
	 * En este caso, dado que el contribuyente es el transmitente (no contemplamos caso de transmitentes residentes
	 * fuera de Espa�a), no necesitamos otro dato.
	 * Si se contemplase, tendr�a que pasarse tambi�n el adquirente y asociar el c�lculo a �l.
	 * @param transmitente Transmitente para el que calcular la plusval�a.
	 * @return
	 * @throws PlusvaliaException
	 */
	//El c�lculo de transmisiones depende s�lo del transmitente
	public LiquidacionContribuyente calcular(Transmitente transmitente, Adquirente adquirente) throws PlusvaliaException{
		return super.calcular(transmitente, adquirente, transmitente);
	}
}
