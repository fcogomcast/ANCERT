package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.domain.AdquisicionesAnteriores.InfoAdquisiciones;
import es.stpa.plusvalias.domain.CalculadoraLiquidacionContribuyente.ConceptoPlusvaliaCalculada;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.ImporteType;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

/**
 * Datos de una liquidación de plusvalía para un contribuyente. 
 * @author crubencvs
 *
 */
public class LiquidacionContribuyente{
	private boolean error=false;
	private String codigoError;
	private Adquirente adquirente; //En un sentido amplio. Adquirente, Heredero, donatario
	private Transmitente transmitente;
	private Interviniente sujetoPasivo;
	private ImporteType importe;
	private ConceptoPlusvaliaCalculada conceptoPlusvaliaCalculada;
	private Tramite tramite;
	private Finca finca;
	private String fechaFinVoluntaria;
	private String valorSueloActual;
	private String baseImponibleDirecta;
	private String valorTerreno;
	private String valorCompra;
	private String valorVenta;
	private String valorCatastralActual;
	private String cuotaResultante;
	private String tipoCalculoElegido;
	private String retrasoPresentacion;
	private String porcentajeRecargo;
	private CalculoAdquisicionesAnteriores calculoAdquisicionesAnteriores;
	
	
	public Adquirente getAdquirente() {
		return adquirente;
	}
	public ImporteType getImporte() {
		return importe;
	}
	public boolean isError() {
		return error;
	}
	public String getCodigoError() {
		return codigoError;
	}
	public ConceptoPlusvaliaCalculada getConceptoPlusvaliaCalculada() {
		return conceptoPlusvaliaCalculada;
	}
	
	public Transmitente getTransmitente() {
		return transmitente;
	}
	public Tramite getTramite() {
		return tramite;
	}
	
	public String getFechaFinVoluntaria() {
		return fechaFinVoluntaria;
	}
	public String getValorSueloActual() {
		return valorSueloActual;
	}
	public String getBaseImponibleDirecta() {
		return baseImponibleDirecta;
	}
	public String getValorCompra() {
		return valorCompra;
	}
	public String getValorVenta() {
		return valorVenta;
	}
	public String getTipoCalculoElegido() {
		return tipoCalculoElegido;
	}
	public String getRetrasoPresentacion() {
		return retrasoPresentacion;
	}
	public String getPorcentajeRecargo() {
		return porcentajeRecargo;
	}
	
	public Finca getFinca() {
		return finca;
	}
	
	public String getCuotaResultante() {
		return cuotaResultante;
	}
	
	public CalculoAdquisicionesAnteriores getCalculoAdquisicionesAnteriores() {
		return calculoAdquisicionesAnteriores;
	}
	
	public String getValorTerreno() {
		return valorTerreno;
	}
	
	public String getValorCatastralActual() {
		return valorCatastralActual;
	}
	
	public Interviniente getSujetoPasivo() {
		return sujetoPasivo;
	}
	
	/** 
	 * Constructor completo
	 * @param adquirente
	 * @param importe
	 * @param cpc
	 * @param erronea
	 * @param codigoError
	 */
	public static class LiquidacionContribuyenteBuilder {
		private boolean error= false;
		private String codigoError;
		private Adquirente adquirente; 
		private Transmitente transmitente;
		private Finca finca;
		private ImporteType importe;
		private ConceptoPlusvaliaCalculada conceptoPlusvaliaCalculada;
		private Tramite tramite;
		private String fechaFinVoluntaria;
		private String valorSueloActual;
		private String baseImponibleDirecta;
		private String valorCompra;
		private String valorVenta;
		private String valorCatastralActual;
		private String cuotaResultante;
		private String tipoCalculoElegido;
		private String valorTerreno;
		private String retrasoPresentacion;
		private String porcentajeRecargo;
		private CalculoAdquisicionesAnteriores calculoAdquisicionesAnteriores;
		
		public LiquidacionContribuyenteBuilder setAdquirente(Adquirente adquirente){
			this.adquirente= adquirente;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setTransmitente(Transmitente transmitente){
			this.transmitente= transmitente;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setImportes(ImporteType importe){
			this.importe= importe;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setConceptoPlusvaliaCalculada(ConceptoPlusvaliaCalculada conc){
			this.conceptoPlusvaliaCalculada= conc;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setTramite(Tramite tramite){
			this.tramite= tramite;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setFechaFinVoluntaria(String fechaFinVoluntaria){
			this.fechaFinVoluntaria= fechaFinVoluntaria;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setValorSueloActual(String valorSueloActual){
			this.valorSueloActual= valorSueloActual;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setBaseImponibleDirecta(String baseImponibleDirecta){
			this.baseImponibleDirecta= baseImponibleDirecta;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setValorCompra(String valorCompra){
			this.valorCompra= valorCompra;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setValorVenta(String valorVenta){
			this.valorVenta= valorVenta;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setTipoCalculoElegido(String tipoCalculoElegido){
			this.tipoCalculoElegido= tipoCalculoElegido;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setRetrasoPresentacion(String retrasoPresentacion){
			this.retrasoPresentacion= retrasoPresentacion;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setPorcentajeRecargo(String porcentajeRecargo){
			this.porcentajeRecargo= porcentajeRecargo;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setValorTerreno(String valorTerreno){
			this.valorTerreno= valorTerreno;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setValorCatastralActual(String valorCatastralActual){
			this.valorCatastralActual= valorCatastralActual;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setCuotaResultante(String cuotaResultante){
			this.cuotaResultante= cuotaResultante;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setFinca(Finca finca){
			this.finca= finca;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setError(boolean error){
			this.error= error;
			return this;
		}
		
		public LiquidacionContribuyenteBuilder setCodigoError(String codigoError){
			this.codigoError= codigoError;
			return this;
		}
		
		
		public LiquidacionContribuyenteBuilder setCalculoAdquisicionesAnteriores(InfoAdquisiciones adq,
																				 String[] aniosTranscurridos,
																				 String[] coeficientesAplicables,
																				 String[] basesImponiblesObjetivas,
																				 String[] tiposImpositivos,
																				 String[] cuotasLiquidacion,
																				 String coeficienteAplicableAnual) {
			this.calculoAdquisicionesAnteriores= new CalculoAdquisicionesAnteriores(adq, aniosTranscurridos, coeficientesAplicables, basesImponiblesObjetivas, tiposImpositivos, cuotasLiquidacion, coeficienteAplicableAnual);
			return this;
		}
		public LiquidacionContribuyente build() throws PlusvaliaException{

			if (adquirente==null){
				throw new PlusvaliaException("Error técnico: no se puede crear el objeto LiquidacionContribuyente porque no se ha informado el adquirente");
			}
			
			if (transmitente==null){
				throw new PlusvaliaException("Error técnico: no se puede crear el objeto LiquidacionContribuyente porque no se ha informado el transmitente");
			}
			
			if (tramite==null){
				throw new PlusvaliaException("Error técnico: no se puede crear el objeto LiquidacionContribuyente porque no se han informado las características del trámite");
			}
			
			LiquidacionContribuyente liq= new LiquidacionContribuyente();
			liq.error= this.error;
			liq.codigoError= this.codigoError;
			liq.adquirente= this.adquirente;
			liq.transmitente= this.transmitente;
			liq.importe= this.importe;
			liq.conceptoPlusvaliaCalculada= this.conceptoPlusvaliaCalculada;
			liq.finca= this.finca;
			liq.tramite= this.tramite;
			liq.fechaFinVoluntaria= this.fechaFinVoluntaria;
			liq.valorSueloActual= this.valorSueloActual;
			liq.baseImponibleDirecta= this.baseImponibleDirecta;
			liq.valorCompra= this.valorCompra;
			liq.valorVenta= this.valorVenta;
			liq.valorCatastralActual= this.valorCatastralActual;
			liq.tipoCalculoElegido= this.tipoCalculoElegido;
			liq.retrasoPresentacion= this.retrasoPresentacion;
			liq.porcentajeRecargo= this.porcentajeRecargo;
			liq.calculoAdquisicionesAnteriores= this.calculoAdquisicionesAnteriores;
			liq.cuotaResultante= this.cuotaResultante;
			liq.valorTerreno= this.valorTerreno;
			if (TipoTramite.TRANSMISION.equals(this.tramite.getTipoTramite())){
				liq.sujetoPasivo= liq.transmitente;
			} else {
				liq.sujetoPasivo= liq.adquirente;
			}
			return liq;
			
		}
		
	}




}