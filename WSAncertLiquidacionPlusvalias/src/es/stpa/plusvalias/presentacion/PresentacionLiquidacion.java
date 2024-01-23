package es.stpa.plusvalias.presentacion;

import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.domain.LiquidacionContribuyente;
import es.stpa.plusvalias.domain.Justificante;
import es.stpa.plusvalias.domain.PeticionOperacionType;
import es.stpa.plusvalias.exceptions.PlusvaliaException;
import es.stpa.plusvalias.presentacion.pago.MediadorPago;
import es.tributasenasturias.log.ILog;

/**
 * Clase que dirigirá la presentación de la liquidación
 * @author crubencvs
 *
 */
public class PresentacionLiquidacion {
	private Estado estadoActual=null;
	//Nos permitirá controlar si se ha iterado sin cambiar el estado,
	//que no debería ser.
	private Estado estadoAnterior=null;
	
	private PeticionOperacionType peticion;
	
	private LiquidacionContribuyente liquidacionContribuyente;
	
	private MediadorBD bd;
	
	private MediadorPago pago;
	
	private Justificante numeroJustificante;
	
	private ILog logger;
	
	private boolean finalizado;
	

	/**
	 * Permite  informar del resultado de la presentación
	 * @author crubencvs
	 *
	 */
	public static class ResultadoPresentacion{
		private boolean error;
		private String codigoResultado;
		private String mensajeResultado;
		public boolean isError() {
			return error;
		}
		public String getCodigoResultado() {
			return codigoResultado;
		}
		public String getMensajeResultado() {
			return mensajeResultado;
		}
	}
	public PresentacionLiquidacion(PeticionOperacionType peticion, LiquidacionContribuyente liquidacion,MediadorBD bd, MediadorPago pago, ILog logger){
		this.peticion= peticion;
		this.bd= bd;
		this.liquidacionContribuyente= liquidacion;
		this.pago= pago;
		this.logger= logger;
		this.finalizado=false;
	}
	/**
	 * Recupera el estado actual de proceso
	 * @return
	 */
	public Estado getEstadoActual() {
		return estadoActual;
	}
	/**
	 * Establece el estado actual de proceso
	 * @param estadoActual
	 */
	public void setEstadoActual(Estado estadoActual) {
		this.estadoAnterior= this.estadoActual;
		this.estadoActual = estadoActual;
	}
	/**
	 * Recupera la petición de proceso original
	 * @return
	 */
	public PeticionOperacionType getPeticion() {
		return peticion;
	}
	
	/**
	 * Recupera la liquidación de contribuyente que se presenta
	 * @return
	 */	
	public LiquidacionContribuyente getLiquidacionContribuyente() {
		return liquidacionContribuyente;
	}
	/**
	 * Establece la liquidación de contribuyente que se presenta
	 * @param liquidacionContribuyente
	 */
	public void setLiquidacionContribuyente(
			LiquidacionContribuyente liquidacionContribuyente) {
		this.liquidacionContribuyente = liquidacionContribuyente;
	}
	/**
	 * Recupera el objeto de comunicación con Base de datos
	 * @return
	 */
	public MediadorBD getBd() {
		return bd;
	}
	
	/**
	 * Recupera los datos del número de justificante de una presentación
	 * @return
	 */
	public Justificante getNumeroJustificante() {
		return numeroJustificante;
	}
	/**
	 * Establece los datos del número de justificante de una presentación
	 * @param numeroJustificante
	 */
	public void setNumeroJustificante(Justificante numeroJustificante) {
		this.numeroJustificante = numeroJustificante;
	}
	
	/**
	 * Recupera el objeto de log
	 * @return
	 */
	public ILog getLogger() {
		return logger;
	}
	/**
	 * Recupera los datos del mediador de pagos
	 * @return
	 */
	public MediadorPago getMediadorPago() {
		return pago;
	}
	/**
	 * Indica si la presentación ha finalizado (se sobreentiende que correctamente)
	 * @return
	 */
	public boolean isFinalizado() {
		return finalizado;
	}
	/**
	 * Establece el estado de finalización de la presentación
	 */
	public void finalizarPresentacion () {
		this.finalizado = true;
	}
	/**
	 * Realiza la presentación de la liquidación de un contribuyente
	 * @return {@link ResultadoPresentacion}
	 * @throws PlusvaliaException
	 */
	public ResultadoPresentacion presentar() throws PlusvaliaException{
		this.setEstadoActual(new EstadoIniciar(this));
		do {
			this.estadoActual.operar();
			//Debería haber cambiado el estado, ya que 
			//ningún estado queda en sí mismo.
			if (this.estadoActual.equals(this.estadoAnterior)){
				throw new PlusvaliaException ("Error de programación, no se ha producido un cambio de estado de proceso después de ejecutar el estado actual.");
			}
		} while (!this.finalizado && !(this.estadoActual instanceof EstadoError));
		ResultadoPresentacion rp= new ResultadoPresentacion();
		if (this.finalizado){
			rp.error=false;
			rp.codigoResultado= CodigosTerminacion.OK;
		} else if (this.estadoActual instanceof EstadoError){
			rp.error=true;
			EstadoError ee= (EstadoError)this.estadoActual;
			rp.codigoResultado= ee.getCodigoError();
			rp.mensajeResultado= ee.getMensajeError();
		}
		return rp;
	}
}
