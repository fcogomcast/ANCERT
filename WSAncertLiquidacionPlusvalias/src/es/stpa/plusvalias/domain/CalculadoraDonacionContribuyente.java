package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

public class CalculadoraDonacionContribuyente extends CalculadoraLiquidacionContribuyente{

	public CalculadoraDonacionContribuyente(Finca finca, Tramite tramite,MediadorBD bd){
		super(finca, tramite, bd);
	}

	/**
	 * Calcula la liquidación con las reglas de donación, para un donatario y donante
	 * @param donante
	 * @param donatario
	 * @return
	 * @throws PlusvaliaException
	 */
	public LiquidacionContribuyente calcular(Transmitente donante,Adquirente donatario) throws PlusvaliaException{
		return super.calcular(donante, donatario, donatario);
	}
}
