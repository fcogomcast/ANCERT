package es.stpa.plusvalias.presentacion;

import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.exceptions.DatosException;

public class EstadoPresentacion implements Estado{

	private PresentacionLiquidacion p;
	public EstadoPresentacion(PresentacionLiquidacion p){
		this.p=p;
	}
	@Override
	public void operar() {
		p.getLogger().debug("Tratamos el estado \"Presentar Modelo\"");
		try {
			p.getBd().presentaModelo(p.getNumeroJustificante().getNumeroJustificante());
			p.setEstadoActual(new EstadoGenerarDocumento(p));
			p.getBd().actualizaEstado(p.getNumeroJustificante().getNumeroJustificante(), ValorEstado.PRESENTADO);
		} catch (DatosException de){
			p.getLogger().debug("Error en el estado \"Presentar Modelo\":" + de.getMessage());
			p.setEstadoActual(new EstadoError(p,CodigosTerminacion.ERROR_PRESENTACION_MODELO, CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_PRESENTACION_MODELO)));
		}
	}
}
