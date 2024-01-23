package es.stpa.plusvalias.domain;

import java.util.Arrays;
import java.util.List;

import es.stpa.plusvalias.domain.AdquisicionesAnteriores.InfoAdquisiciones;

/**
 * Datos calculados sobre las adquisiciones anteriores
 * @author crubencvs
 *
 */
public class CalculoAdquisicionesAnteriores {

	private AdquisicionesAnteriores.InfoAdquisiciones adquisiciones;
	private List<String> aniosTranscurridos;
	private List<String> coeficientesAplicables;
	private List<String> basesImponiblesObjetivas;
	private List<String> tiposImpositivos;
	private List<String> cuotasLiquidacion;
	private String coeficienteAplicableAnual;
	
	public AdquisicionesAnteriores.InfoAdquisiciones getAdquisiciones() {
		return adquisiciones;
	}
	public List<String> getAniosTranscurridos() {
		return aniosTranscurridos;
	}
	public List<String> getCoeficientesAplicables() {
		return coeficientesAplicables;
	}
	public List<String> getBasesImponiblesObjetivas() {
		return basesImponiblesObjetivas;
	}
	
	public List<String> getTiposImpositivos() {
		return tiposImpositivos;
	}
	
	public List<String> getCuotasLiquidacion() {
		return cuotasLiquidacion;
	}
	
	public String getCoeficienteAplicableAnual() {
		return coeficienteAplicableAnual;
	}
	public CalculoAdquisicionesAnteriores (InfoAdquisiciones adq, String[] aniosTranscurridos, String[] coeficientesAplicables,  String[] basesImponibles, String[] tiposImpositivos, String[] cuotasLiquidacion, String coeficienteAplicableAnual){
		this.adquisiciones= adq;
		this.aniosTranscurridos= Arrays.asList(aniosTranscurridos);
		this.coeficientesAplicables= Arrays.asList(coeficientesAplicables);
		this.basesImponiblesObjetivas= Arrays.asList(basesImponibles);
		this.cuotasLiquidacion= Arrays.asList(cuotasLiquidacion);
		this.tiposImpositivos= Arrays.asList(tiposImpositivos);
		this.coeficienteAplicableAnual= coeficienteAplicableAnual;
	}
	
}
