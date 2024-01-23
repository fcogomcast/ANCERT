package es.stpa.plusvalias.domain;


import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.domain.CalculadoraLiquidacion.LiquidacionCalculada;
import es.stpa.plusvalias.domain.ValidadorIntervinientes.ResultadoValidacion;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

/**
 * Dirige la operativa de cálculo
 * @author crubencvs
 *
 */
public class DirectorCalculo {

	private MediadorBD bd;
		private PeticionOperacionType peticion;
	
	public DirectorCalculo(PeticionOperacionType peticion,MediadorBD bd){
		this.peticion= peticion;
		this.bd= bd;
	}
	
	private boolean hayTransmitentes(PeticionOperacionType peticion){
		return peticion.getTransmitentes()!=null && peticion.getTransmitentes().size()>0;
	}
	
	private boolean hayAdquirentes(PeticionOperacionType peticion){
		return peticion.getAdquirentes()!=null && peticion.getAdquirentes().size()>0;
	}
	/**
	 * Valida los intervinientes del acto.
	 * @param peticion
	 * @return Objeto con los datos del resultado de la validación
	 */
	private ResultadoValidacion validaIntervinientes(PeticionOperacionType peticion){
		ResultadoValidacion r=null;
		if (peticion.getTramite().getTipoTramite().equals(TipoTramite.TRANSMISION)){
			for (Transmitente t: peticion.getTransmitentes()){
				r= ValidadorIntervinientes.validarSujeto(t);
				if (r.isError()){
					break;
				}
			}
		} else if (peticion.getTramite().getTipoTramite().equals(TipoTramite.DONACION)){
			for (Adquirente a: peticion.getAdquirentes()){
				r= ValidadorIntervinientes.validarSujeto(a);
				if (r.isError()){
					break;
				}
			}
		} else if (peticion.getTramite().getTipoTramite().equals(TipoTramite.SUCESION)){
			for (Transmitente t: peticion.getTransmitentes()){
				r= ValidadorIntervinientes.validarCausante(t);
				if (r.isError()){
					break;
				}
			}
			for (Adquirente a: peticion.getAdquirentes()){
				r= ValidadorIntervinientes.validarSujeto(a);
				if (r.isError()){
					break;
				}
			}
		}
		if (r!=null){
			return r;
		} else {
			r=new ResultadoValidacion();
			r.setError(true);
			r.setCodigo(CodigosTerminacion.ERROR_GENERAL);
			r.setMensajeError(CodigosTerminacion.getMessage(r.getCodigo()));
			return r;
		}
	}
	
	/**
	 * Realiza los cálculos de la plusvalía
	 * @throws PlusvaliaException
	 */
	private LiquidacionCalculada realizarCalculos(PeticionOperacionType peticion, MediadorBD bd) throws PlusvaliaException{
		CalculadoraLiquidacion calc= new CalculadoraLiquidacion(peticion,bd);
		return calc.calcularLiquidacion();
	}
	
	
	/**
	 * Llama a la operación de cálculo de plusvalía.
	 * @return {@link LiquidacionCalculada} con atributo error a true si hubo errores,
	 * o con atributo error a false si no, y con la lista de liquidaciones calculadas,
	 * una por contribuyente
	 * @throws PlusvaliaException
	 */
	public LiquidacionCalculada calcularPlusvalia() throws PlusvaliaException{
		LiquidacionCalculada liq=null;
		String codigoError="";
		String mensajeError="";
		boolean error=false;
		if (!hayTransmitentes(peticion)){
			codigoError=CodigosTerminacion.ERROR_NO_TRANSMITENTES;
			error=true;
		}
		if (!error){
			if (!hayAdquirentes(peticion)){
				codigoError=CodigosTerminacion.ERROR_NO_ADQUIRENTES;
				error=true;
			}
		}
		
		if (!error){
			ValidadorCabeceraPeticion.ResultadoValidacion rv= ValidadorCabeceraPeticion.validarReceptor(peticion.getCabecera());
			if (rv.isError()){
				error=true;
				codigoError= rv.getCodigo();
				mensajeError= rv.getMensajeError();
			}
		}
		
		if (!error){
			ValidadorIntervinientes.ResultadoValidacion rv= ValidadorIntervinientes.validaNifs(peticion);
			if (rv.isError()){
				error=true;
				codigoError= rv.getCodigo();
				mensajeError= rv.getMensajeError();
			}
		}
		
		if (!error){
			ValidadorIntervinientes.ResultadoValidacion rv=validaIntervinientes(peticion);
			if (rv.isError()){
				error=true;
				codigoError= rv.getCodigo();
				mensajeError= rv.getMensajeError();
			}
		}
		
		
		if (!error){
			//	Calculo
			liq=realizarCalculos(peticion, bd);
		}
		if (error){
			liq= new LiquidacionCalculada();
			liq.setError(codigoError, mensajeError);
		}
		return liq;
	}
	
	
	
	
	
	
}
