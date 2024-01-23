package es.stpa.plusvalias.presentacion;

import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.exceptions.DatosException;



/**
 * Estado de finalización
 * @author crubencvs
 *
 */
public class EstadoFin implements Estado{
	@SuppressWarnings("unused")
	private PresentacionLiquidacion p;
	
	public EstadoFin(PresentacionLiquidacion p){
		this.p= p;
	}
	
	@Override
	public void operar() {
		try {
			p.getLogger().debug("Tratamos el estado \"Finalizar\"");
			p.getBd().actualizaEstado(p.getNumeroJustificante().getNumeroJustificante(), ValorEstado.FINALIZADO);
			p.finalizarPresentacion();
		} catch (DatosException de){
			p.getLogger().debug("Error en el estado \"Finalizar\":" + de.getMessage());
			p.setEstadoActual(new EstadoError(p,CodigosTerminacion.ERROR_GENERAL_PRESENTACION, de.getMessage()));
		}
	}
}
