package es.stpa.plusvalias.presentacion;

/**
 * Estado de generación de documentación
 * @author crubencvs
 *
 */
public class EstadoGenerarDocumento implements Estado{
	private PresentacionLiquidacion p;
	
	public EstadoGenerarDocumento (PresentacionLiquidacion p){
		this.p= p;
	}
	
	@Override
	public void operar() {
		p.setEstadoActual(new EstadoFin(p));
		
	}
}
