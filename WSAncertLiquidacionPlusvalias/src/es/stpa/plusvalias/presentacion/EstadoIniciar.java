package es.stpa.plusvalias.presentacion;

import es.stpa.plusvalias.datos.MediadorBD.DatosEstadoPresentacion;
import es.stpa.plusvalias.exceptions.DatosException;


/**
 * Maneja el estado de inicio de una presentación de liquidación,
 * en donde se va a intentar recuperar uno de los estados posibles
 * y derivar al siguiente estado a partir de él.
 * @author crubencvs
 *
 */
public class EstadoIniciar implements Estado{
	private PresentacionLiquidacion p;
	public EstadoIniciar(PresentacionLiquidacion p){
		this.p=p;
	}
	
	
	
	@Override
	public void operar() {
		p.getLogger().debug("Tratamos el estado \"Iniciar\"");
		try {
			String protocoloBis;
			if (p.getPeticion().getDocNotarial().getProtocolo().getNumerobis()!=null){
				protocoloBis= String.valueOf(p.getPeticion().getDocNotarial().getProtocolo().getNumerobis());
			} else {
				protocoloBis="";
			}
			DatosEstadoPresentacion est=p.getBd().estadoPresentacion(p.getPeticion().getDocNotarial().getNotarioAutorizante().getCodigo(), 
																	 String.valueOf(p.getPeticion().getDocNotarial().getProtocolo().getNumero()), 
																	 protocoloBis,
																	 p.getPeticion().getDocNotarial().getProtocolo().getFechaautorizacion(),
																	 p.getPeticion().getActojuridico().getCodigo(), 
																	 p.getPeticion().getFinca().getReferenciaCatastral(), 
																	 p.getLiquidacionContribuyente().getSujetoPasivo().getDatosbasicos().getDatospersonales().getNumerodocumento()
																	 );
			p.setNumeroJustificante(new es.stpa.plusvalias.domain.Justificante(est.getEmisora(),est.getNumeroJustificante()));
			p.getLogger().info("Detectado estado " +est.getEstadoPresentacion() + " en base de datos, se procesa");
			ValorEstado v = ValorEstado.get(est.getEstadoPresentacion());
			switch (v){
			case INICIADO:
				p.getLogger().info("Estado iniciado, se procede al pago");
				p.setEstadoActual(new EstadoPago(p));
				break;
			case PAGADO:
				p.getLogger().info("Estado pagado, se procede a insertar los datos de modelo");
				p.setEstadoActual(new EstadoInsertarModelo(p));
				break;
			case INTEGRADO:
				p.getLogger().info("Estado \"datos integrados\", se procede a la presentación de modelo");
				p.setEstadoActual(new EstadoPresentacion(p));
				break;
			case PRESENTADO:
				p.getLogger().info("Estado \"modelo presentado\", se procede a finalizar");
				//Faltaría  el de generar el documento
				p.setEstadoActual(new EstadoFin(p));
				break;
			case FINALIZADO:
				p.getLogger().info("Ya se había presentado la liquidación. No se hace nada.");
				p.setEstadoActual(new EstadoFin(p));
				p.finalizarPresentacion();
				break;
			default:
				EstadoError err= new EstadoError(p,"9999","Error, estado en base de datos incorrecto");
				p.setEstadoActual(err);
				break;
			} 
		} catch (DatosException de){
			p.getLogger().debug("Error en el estado \"Iniciar\":" + de.getMessage());
			EstadoError err= new EstadoError(p,"9999",de.getMessage());
			p.setEstadoActual(err);
		} catch (IllegalArgumentException iae){
			p.getLogger().debug("Error en el estado \"Iniciar\", el estado recibido de BD no se puede procesar:" + iae.getMessage());
			EstadoError err= new EstadoError(p,"9999",iae.getMessage());
			p.setEstadoActual(err);
		}
	}
}
