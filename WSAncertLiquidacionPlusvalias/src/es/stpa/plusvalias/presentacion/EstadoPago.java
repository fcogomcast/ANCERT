package es.stpa.plusvalias.presentacion;



import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.domain.OrdenPago;
import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.exceptions.DatosException;
import es.stpa.plusvalias.presentacion.pago.MediadorPago;

/**
 * Estado de pago del contribuyente
 * @author crubencvs
 *
 */
public class EstadoPago implements Estado{
	private PresentacionLiquidacion p;
	
	public EstadoPago (PresentacionLiquidacion p){
		this.p= p;
	}
	/**
	 * Convierte de una cadena que representa un importe en euros
	 * con separador de miles "." y coma decimal,  a céntimos.
	 * En principio lo único que hacemos es eliminar los puntos,
	 * modificar la coma decimal a punto (si no hay, no importa)
	 * y tratar de convertir la cadena resultante a Double. Después,
	 * multiplico por 100. 
	 * @param euros
	 * @return
	 */
	private Long importeCentimos(String euros){
		if (euros==null){
			return null;
		}
		try{
			Double valor=Double.parseDouble(euros.replace(".","").replace(',', '.'));
			return (long)(valor.doubleValue()*100);
		} catch (NumberFormatException nfe){ return null;}
	}
	/**
	 * Convierte de una cadena que representa un importe en euros,
	 * con separador de miles "." y coma decimal, a céntimos.
	 * En principio lo único que hacemos es eliminar los puntos,
	 * modificar la coma decimal a punto (si no hay, no importa)
	 * y tratar de convertir la cadena resultante a Double. Después,
	 * multiplico por 100. 
	 * @param euros
	 * @return
	 */
	private String euro2Centimo(String euros){
		String r= String.valueOf(importeCentimos(euros));
		if (r.equals("null")){
			r="";
		}
		return r;
	}
	@Override
	public void operar() {
		p.getLogger().debug("Tratamos el estado \"Pago\"");
		Persona contribuyente;
		boolean esError=false;
		String codigoTerminacionError="";
		String mensajeError="";
		boolean hayPago=false;
		contribuyente= p.getLiquidacionContribuyente().getSujetoPasivo().getDatosbasicos();
		
		OrdenPago op= p.getPeticion().getOrdenesPago().getOrdenPagoPersona(contribuyente);
		if (op==null){
			esError=true;
			codigoTerminacionError=CodigosTerminacion.ERROR_NO_ORDEN_CONTRIBUYENTE;
			mensajeError= CodigosTerminacion.getMessage(codigoTerminacionError, String.valueOf(contribuyente.getIdentificador()));
		}
		if (!esError){
			
			if (importeCentimos(op.getImporte())==0){
				hayPago=false;
			} else {
				hayPago=true;
			}
			if (hayPago){
				//Pago
				MediadorPago.DatosPago dat= new MediadorPago.DatosPago();
				dat.setCcc(op.getCcc());
				dat.setContribuyente(op.getContribuyente());
				dat.setEmisoraModelo(p.getNumeroJustificante().getEmisora());
				dat.setNumeroModelo(p.getNumeroJustificante().getNumeroJustificante());
				dat.setFechaDevengo(p.getLiquidacionContribuyente().getTramite().getFechaTramite());
				dat.setImporteCentimos(euro2Centimo(op.getImporte()));
				dat.setNifTitular(op.getNifTitular());
				//El dato específico es el código de municipio, a dos dígitos
				//y los dos últimos dígitos de la fecha de devengo.
				String fechaDevengo= p.getLiquidacionContribuyente().getTramite().getFechaTramite();
				String municipio= p.getLiquidacionContribuyente().getFinca().getCodMunicipio();
				String datoEspecifico= municipio.substring(municipio.length()-2) + fechaDevengo.substring(fechaDevengo.length()-2);
				dat.setDatoEspecifico(datoEspecifico);
				MediadorPago.ResultadoPago rpago= p.getMediadorPago().realizaPago(dat);
				esError= rpago.isError();
				if (esError) {
					codigoTerminacionError= CodigosTerminacion.ERROR_PAGO;
					mensajeError= CodigosTerminacion.getMessage(codigoTerminacionError, String.valueOf(contribuyente.getIdentificador()), rpago.getMensajeError());
				}
			} else {
				esError=false;
			}
		}
		if (!esError){
			p.setEstadoActual(new EstadoInsertarModelo(p));
			if (hayPago){
				try {
					p.getBd().actualizaEstado(p.getNumeroJustificante().getNumeroJustificante(), ValorEstado.PAGADO);
				} catch (DatosException de){
					p.getLogger().debug("Error en el estado \"Pago\":" + de.getMessage());
					p.setEstadoActual(new EstadoError(p,CodigosTerminacion.ERROR_PAGO, de.getMessage()));
				}
			}
		} else {
			p.getLogger().debug("No se ha podido pagar:" + codigoTerminacionError + " " + mensajeError);
			p.setEstadoActual(new EstadoError(p, codigoTerminacionError, mensajeError));
		}
	}
}
