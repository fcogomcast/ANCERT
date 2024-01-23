package es.stpa.plusvalias.domain;

import java.math.BigDecimal;


import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.datos.MediadorBD.DatosCalculoLiquidacion;
import es.stpa.plusvalias.domain.AdquisicionesAnteriores.InfoAdquisiciones;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.ImporteType;
import es.stpa.plusvalias.domain.intercambio.Transmision;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.DatosException;
import es.stpa.plusvalias.exceptions.PlusvaliaException;
/**
 * Clase base para el cálculo de liquidación por contribuyente
 * @author crubencvs
 *
 */
public abstract class CalculadoraLiquidacionContribuyente {

	protected Finca finca;
	protected MediadorBD bd;
	protected Tramite tramite;

	public CalculadoraLiquidacionContribuyente(Finca finca, Tramite tramite,MediadorBD bd){
		this.bd= bd;
		this.tramite= tramite;
		this.finca= finca;
	}

	/**
	 * Datos del concepto de plusvalía asociada al cálculo de liquidación.
	 * Dado que depende del tipo de trámite y de datos concretos del sujeto, 
	 * como que existan años de usufructo, se devuelve junto a cada cálculo
	 * individual y no en el trámite, no se debe hacer una sola vez ya que quizá
	 * pueda cambiar entre presentación de nuda propiedad y usufructo, por ejemplo,
	 * dentro de la transmisión. No sé si eso se puede dar en el mensaje que nos mandan,
	 * que en el mismo mensaje liquiden para un sujeto nuda propiedad y para 
	 * otro usufructo.
	 * @author crubencvs
	 *
	 */
	public static class ConceptoPlusvaliaCalculada {
		private String codigoPltr;
		private String nombrePltr;
		private String codigoPlca;
		private String nombrePlca;
		private String codigoPlop;
		private String nombrePlop;
		
		public ConceptoPlusvaliaCalculada(
							String codigoPltr,
							String nombrePltr,
							String codigoPlca,
							String nombrePlca,
							String codigoPlop,
							String nombrePlop){
			this.codigoPltr= codigoPltr;
			this.nombrePltr= nombrePltr;
			this.codigoPlca= codigoPlca;
			this.nombrePlca= nombrePlca;
			this.codigoPlop= codigoPlop;
			this.nombrePlop= nombrePlop;
		}
		
		public String getCodigoPltr() {
			return codigoPltr;
		}
		public void setCodigoPltr(String codigoPltr) {
			this.codigoPltr = codigoPltr;
		}
		public String getNombrePltr() {
			return nombrePltr;
		}
		public void setNombrePltr(String nombrePltr) {
			this.nombrePltr = nombrePltr;
		}
		public String getCodigoPlca() {
			return codigoPlca;
		}
		public void setCodigoPlca(String codigoPlca) {
			this.codigoPlca = codigoPlca;
		}
		public String getNombrePlca() {
			return nombrePlca;
		}
		public void setNombrePlca(String nombrePlca) {
			this.nombrePlca = nombrePlca;
		}
		public String getCodigoPlop() {
			return codigoPlop;
		}
		public void setCodigoPlop(String codigoPlop) {
			this.codigoPlop = codigoPlop;
		}
		public String getNombrePlop() {
			return nombrePlop;
		}
		public void setNombrePlop(String nombrePlop) {
			this.nombrePlop = nombrePlop;
		}
	}
	/**
	 * Devuelve los datos de adquisiciones anteriores de un transmitente
	 * @param t
	 * @return
	 */
	private es.stpa.plusvalias.domain.AdquisicionesAnteriores.InfoAdquisiciones getAdquisicionesAnteriores(Transmitente t){
		InfoAdquisiciones i = new InfoAdquisiciones();
		for (Transmision a: t.getTransmisionesanteriores()){
			i.getFechasAdquisicionAnteriores().add(a.getFecha());
			// CRUBENCVS 10/03/2023.
			//Transformo los porcentajes del formato en que llegan (999,9999) a (999.9999), para después
			//poder pasarlo a PL separado por comas
			//Dado que no puede tener puntos de miles, cambio la "," por "."
			String porcentaje= a.getPorcentaje();
			i.getPorcentajesAnteriores().add(porcentaje);
			i.getValoresAdquisicion().add(a.getValor());
		}
		for (BigDecimal d: i.getValoresAdquisicion()){
			if (d!=null){
				i.setSumaValoresAdquisicion( i.getSumaValoresAdquisicion() + d.longValue()*100);
			}
		}
		return i;
	}
	
	/**
	 * Devuelve las bonificaciones de un transmitente
	 * @param t
	 * @return
	 */
	private String getBonificaciones(Transmitente t){
		String bonificacion="";
		if (t.getExencion()!=null && t.getExencion().getPorcentaje()!=null){
			bonificacion=t.getExencion().getPorcentaje();
		}
		
		if ("".equals(bonificacion) && t.getBonificacion()!=null){
			bonificacion=t.getBonificacion().getPorcentaje();
		}
		return bonificacion;
	}
	
	/**
	 * Convierte de los datos del cálculo de liquidación en BD a un objeto ImporteType
	 * con los datos de cálculo para ese contribuyente
	 * @param dat
	 * @return
	 */
	private ImporteType convierteImportesCalculo(DatosCalculoLiquidacion dat){
		double importeRecargo=0;
		double importeTotal=0;
		ImporteType importe= new ImporteType();
		//Sumo tanto el recargo como los intereses, ya que no los distinguen
		if (!"".equals(dat.getRecargo())){
			//importeRecargo+= Double.parseDouble(dat.getRecargo())/100.00;
			importeRecargo+= Double.parseDouble(dat.getRecargo());
		} 
		
		if (!"".equals(dat.getInteresesDemora())){
			//importeRecargo+= Double.parseDouble(dat.getInteresesDemora())/100.00;
			importeRecargo+= Double.parseDouble(dat.getInteresesDemora());
		}
		importeRecargo= Math.round(importeRecargo)/100.00;
		importe.setImporterecargo(importeRecargo);
		importeTotal=Math.round(Double.parseDouble(dat.getTotalIngresar()))/100.00;
		importe.setImportetotal(importeTotal);
		if (importeRecargo>0){
			importe.setInformarecargo(true);
		} else {
			importe.setInformarecargo(false);
		}
		return importe;
	}
	
	
	/**
	 * Realiza el cálculo de la liquidación
	 * @param transmitente Transmitente, Donante o Causante 
	 * @param adquirente Adquirente, Donantario o Heredero
	 * @param sujetoPasivo Transmitente o Adquirente que actúa como sujeto pasivo
	 * @return
	 * @throws PlusvaliaException
	 */
	public LiquidacionContribuyente calcular(Transmitente transmitente, Adquirente adquirente, Interviniente sujetoPasivo) throws PlusvaliaException{
		InfoAdquisiciones infoAdqs= getAdquisicionesAnteriores(transmitente);
		Long valorVenta=null;
		if (transmitente.getValorTransmision()!=null){
			valorVenta=transmitente.getValorTransmision().longValue()*100;
		}
		LiquidacionContribuyente liq;
		String fechaDevengo;
		if (TipoTramite.SUCESION.equals(this.tramite.getTipoTramite())){
			fechaDevengo= transmitente.getFechadefuncion();
		} else {
			fechaDevengo= this.tramite.getFechaTramite();
		}
		//CRUBENCVS 31/05/2023
		//Si se trata de transmisión, el porcentaje transmitido que usamos en el cálculo
		//es el del transmitente, que es el sujeto pasivo. Si es sucesión o donación,
		//utilizaremos el del adquirente, que es el porcentaje transmitido por el 
		//que tributa .
		String porcentajeTransmitido;
		//CRUBENCVS 20/07/2023. Siempre se estaba utilizando la clase de derecho del transmitente,
		//pero en lucrativo hay que usar la del adquirente.
		String claseDerecho; 
		if (TipoTramite.TRANSMISION.equals(this.tramite.getTipoTramite())){
			porcentajeTransmitido= transmitente.getPorcentajetransmitido();
			claseDerecho = transmitente.getClasederecho();
		} else {
			porcentajeTransmitido= adquirente.getPorcentajeadquirido();
			claseDerecho = adquirente.getClasederecho();
		}
		try {
			DatosCalculoLiquidacion dat= bd.calcularLiquidacion(
					   this.tramite.getTipoTramite().valor(), 
					   this.finca.getIdEperFinca(),
					   this.finca.getCodMunicipio(),
					   fechaDevengo,
					   this.tramite.getActoJuridico(),
					   claseDerecho, //CRUBENCVS 20/07/2023
					   porcentajeTransmitido, 
					   valorVenta,
					   finca.getAnioValorCatastral(), 
					   finca.getValorCatastral(),
					   finca.getValorCatastralTotalInicial(),
					   finca.getAnioRevisionCatastral(), 
					   infoAdqs.getFechasAdquisicionAnteriores().toArray(new String[1]), 
					   infoAdqs.getPorcentajesAnteriores().toArray(new String[1]), 
					   infoAdqs.getSumaValoresAdquisicion(),
					   sujetoPasivo.getEdadusufructuario()!=null?String.valueOf(sujetoPasivo.getEdadusufructuario()):"", 
					   sujetoPasivo.getAnyosusufructo()!=null?String.valueOf(sujetoPasivo.getAnyosusufructo()):"", 
					   sujetoPasivo.getAnyosuso()!=null?String.valueOf(sujetoPasivo.getAnyosuso()):"",
					   sujetoPasivo.getEdaduso()!=null?String.valueOf(sujetoPasivo.getEdaduso()):"",							   
					   getBonificaciones(transmitente));
			LiquidacionContribuyente.LiquidacionContribuyenteBuilder builder = new LiquidacionContribuyente.LiquidacionContribuyenteBuilder();
			if (dat.isCorrecto()){
				ImporteType importe=convierteImportesCalculo(dat);
				ConceptoPlusvaliaCalculada cp= new ConceptoPlusvaliaCalculada(dat.getCodigoPltr(),
																			  dat.getNombrePltr(),
																			  dat.getCodigoPlca(),
																			  dat.getNombrePlca(),
																			  dat.getCodigoPlop(),
																			  dat.getNombrePlop());
				
				liq= builder.setTramite(tramite)
				       .setAdquirente(adquirente)
				       .setTransmitente(transmitente)
				       .setImportes(importe)
				       .setFinca(finca)
				       .setConceptoPlusvaliaCalculada(cp)
				       .setFechaFinVoluntaria(dat.getFechaFinVoluntaria())
				       .setValorSueloActual(dat.getValorSueloActual())
				       .setValorTerreno(dat.getValorTerreno())
				       .setBaseImponibleDirecta(dat.getBaseImponibleDirecta())
				       .setValorCompra(dat.getValorCompra())
				       .setValorVenta(dat.getValorVenta())
				       .setValorCatastralActual(dat.getValorCatastralActual())
				       .setTipoCalculoElegido(dat.getTipoCalculoElegido())
				       .setCuotaResultante(dat.getCuotaResultante())
				       .setRetrasoPresentacion(dat.getRetrasoPresentacion())
				       .setPorcentajeRecargo(dat.getPorcentajeRecargo())
				       .setCalculoAdquisicionesAnteriores(infoAdqs, dat.getAniosTranscurridos(), dat.getCoeficientesAplicables(), dat.getBasesImponiblesObjetivas(), dat.getTiposImpositivos(), dat.getCuotasLiquidacion(), dat.getCoeficienteAplicableAnual())
				       .build();
				
			} else {
				liq= builder.setTramite(tramite)
					.setAdquirente(adquirente)
					.setTransmitente(transmitente)
					.setError(true)
					.setCodigoError(dat.getCodigoTerminacion())
					.build();
			}
			return liq;
		} catch (DatosException de){
			throw new PlusvaliaException ("Error en acceso a datos para el cálculo de liquidación para transmisiones:" + de.getMessage(), de);
		}
	}
	
}
