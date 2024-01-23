package es.stpa.plusvalias.domain;

import java.util.ArrayList;
import java.util.List;

import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.PlusvaliaException;



public class CalculadoraLiquidacion {

	
	private PeticionOperacionType peticion;
	private MediadorBD bd;
	
	public CalculadoraLiquidacion(PeticionOperacionType peticion, MediadorBD bd){
		this.bd= bd;
		this.peticion= peticion;
	}
	
	/**
	 * @author crubencvs
	 *
	 */
	public static class LiquidacionCalculada{
		private boolean error;
		private String codigoError;
		private String mensajeError;
		List<LiquidacionContribuyente> liquidaciones;
		
		public LiquidacionCalculada(){
			this.error=false;
			this.codigoError="";
			this.liquidaciones=null;
			this.mensajeError="";
		}
		public boolean isError() {
			return error;
		}
		public String getCodigoError() {
			return codigoError;
		}
		public List<LiquidacionContribuyente> getLiquidaciones() {
			return liquidaciones;
		}
		public String getMensajeError() {
			return mensajeError;
		}
		/**
		 * Permite establecer el error, y su mensaje asociado
		 * @param codigoError
		 * @param mensajeError
		 */
		public void setError(String codigoError, String mensajeError) {
			this.error = true;
			this.codigoError= codigoError;
			this.mensajeError= mensajeError;
		}
		/**
		 * Permite establecer el error. El mensaje será el correspondiente
		 * al código pasado en CodigosTerminacion
		 * @param codigoError
		 */
		public void setError (String codigoError){
			setError(codigoError, CodigosTerminacion.getMessage(codigoError));
		}
		
	}
	/**
	 * Calcula la liquidación, utilizando las reglas del tipo de trámite que originó
	 * la plusvalía (donación, transmisión,  sucesión)
	 * @return
	 * @throws PlusvaliaException
	 */
	public LiquidacionCalculada calcularLiquidacion() throws PlusvaliaException{
		if (peticion.getTramite().getTipoTramite().equals(TipoTramite.TRANSMISION)){
			return calculoTransmision();
		} else if (peticion.getTramite().getTipoTramite().equals(TipoTramite.DONACION)){
			return calculoDonacion();
		} else if (peticion.getTramite().getTipoTramite().equals(TipoTramite.SUCESION)){
			return calculoSucesion();
		} 
		//Si hay algún error en las liquidaciones, se saldrá con excepción, así que no hay que controlar nada.
		return null;
	}
	/**
	 * Devuelve los cálculos de cada uno de los contribuyentes de la petición,
	 * interpretando la petición como plusvalía  asociada a una transmisión 
	 * @return LiquidacionCalculada 
	 * @throws PlusvaliaException
	 */
	private LiquidacionCalculada calculoTransmision() throws PlusvaliaException{
		LiquidacionCalculada liq= new LiquidacionCalculada();
		List<LiquidacionContribuyente> liquidaciones= new ArrayList<LiquidacionContribuyente>();
		if (peticion==null){
			liq.setError(CodigosTerminacion.ERROR_GENERAL_CALCULO);
			return liq;
		}
		//En transmisiones, sólo necesito a los transmitentes porque serán 
		//los contribuyentes. En el caso de los transmitentes con domicilio
		// en el extranjero, no se tratan de forma diferente. Si lo hiciesen
		// los adquirentes los consideraríamos sujetos 
		// pasivos
		// Lo que sí necesito es un adquirente, pero sólo uno. Si hay más de uno,
		// recupero el primero
		liq.error=false;
		List<Transmitente> transmitentes= peticion.getTransmitentes();
		Adquirente adquirente= peticion.getAdquirentes().get(0); 
		CalculadoraTransmisionContribuyente calc=new CalculadoraTransmisionContribuyente(peticion.getFinca(), peticion.getTramite(),bd);
		for (Transmitente t: transmitentes){
			LiquidacionContribuyente l=calc.calcular(t,adquirente);
			liquidaciones.add(l);
			if (l.isError()){
				liq.setError(l.getCodigoError());
				break; //No tiene sentido continuar si hay algún error.
			}
		}
		liq.liquidaciones= liquidaciones;
		return liq;
	}
	
	/**
	 * Devuelve los cálculos de cada uno de los contribuyentes de la petición,
	 * interpretando la petición como plusvalía  asociada a una donación 
	 * @return LiquidacionCalculada
	 * @throws PlusvaliaException
	 */
	private LiquidacionCalculada calculoDonacion() throws PlusvaliaException{
		LiquidacionCalculada liq= new LiquidacionCalculada();
		List<LiquidacionContribuyente> liquidaciones= new ArrayList<LiquidacionContribuyente>();
		if (peticion==null){
			liq.setError(CodigosTerminacion.ERROR_GENERAL_CALCULO);
			return liq;
		}
		liq.error=false;
		List<Transmitente> donantes= peticion.getTransmitentes();
		List<Adquirente> donatarios= peticion.getAdquirentes();
		CalculadoraDonacionContribuyente calc=new CalculadoraDonacionContribuyente(peticion.getFinca(), peticion.getTramite(),bd);
		for (Adquirente a: donatarios){
			for (Transmitente t: donantes){
				LiquidacionContribuyente l=calc.calcular(t,a);
				liquidaciones.add(l);
				if (l.isError()){
					liq.setError(l.getCodigoError());
					break; //No tiene sentido continuar si hay algún error.
				}
			}
		}
		
		liq.liquidaciones= liquidaciones;
		return liq;
	}
	
	/**
	 * Devuelve los cálculos de cada uno de los contribuyentes de la petición,
	 * interpretando la petición como plusvalía  asociada a una sucesión 
	 * @return LiquidacionCalculada
	 * @throws PlusvaliaException
	 */
	private LiquidacionCalculada calculoSucesion() throws PlusvaliaException{
		LiquidacionCalculada liq= new LiquidacionCalculada();
		List<LiquidacionContribuyente> liquidaciones= new ArrayList<LiquidacionContribuyente>();
		if (peticion==null){
			liq.setError(CodigosTerminacion.ERROR_GENERAL_CALCULO);
			return liq;
		}
		liq.error=false;
		List<Transmitente> causantes= peticion.getTransmitentes();
		List<Adquirente> herederos= peticion.getAdquirentes();
		CalculadoraSucesionContribuyente calc=new CalculadoraSucesionContribuyente(peticion.getFinca(), peticion.getTramite(),bd);
		for (Adquirente a: herederos){
			for (Transmitente t: causantes){
				LiquidacionContribuyente l=calc.calcular(t,a);
				liquidaciones.add(l);
				if (l.isError()){
					liq.setError(l.getCodigoError());
					break; //No tiene sentido continuar si hay algún error.
				}
			}
		}
		
		liq.liquidaciones= liquidaciones;
		return liq;
	}
	
	
	
}
