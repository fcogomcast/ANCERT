package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

public class CalculadoraSucesionContribuyente extends CalculadoraLiquidacionContribuyente{

	public CalculadoraSucesionContribuyente(Finca finca, Tramite tramite,MediadorBD bd){
		super(finca, tramite, bd);
	}

	/**
	 * Calcula la liquidaci�n con las reglas de sucesi�n, para un causante y heredero
	 * @param causante
	 * @param heredero
	 * @return
	 * @throws PlusvaliaException
	 */
	public LiquidacionContribuyente calcular(Transmitente causante,Adquirente heredero) throws PlusvaliaException{
		return super.calcular(causante, heredero, heredero);
	}
}
