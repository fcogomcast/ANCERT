package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.datos.MediadorBD.DatosFinca;
import es.stpa.plusvalias.exceptions.DatosException;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

public final class Finca {
	private String idEperFinca;
	private String referenciaCatastral;
	private String siglaVia;
	private String nombreVia;
	private String numeroVia;
	private String duplicadoVia;
	private String escalera;
	private String planta;
	private String puerta;
	private String anioValorCatastral;
	private String valorCatastral;
	private String valorCatastralTotalInicial;
	private String anioRevisionCatastral;
	private String superficie;
	private String codMunicipio;
	private String numeroFijo;
	private String codigoVia; 
	private String codProvincia;
	private String provincia;
	private String municipio;
	public String getIdEperFinca() {
		return idEperFinca;
	}
	public String getReferenciaCatastral() {
		return referenciaCatastral;
	}
	public String getSiglaVia() {
		return siglaVia;
	}
	public String getNombreVia() {
		return nombreVia;
	}
	public String getNumeroVia() {
		return numeroVia;
	}
	public String getDuplicadoVia() {
		return duplicadoVia;
	}
	public String getEscalera() {
		return escalera;
	}
	public String getPlanta() {
		return planta;
	}
	public String getPuerta() {
		return puerta;
	}
	public String getAnioValorCatastral() {
		return anioValorCatastral;
	}
	public String getValorCatastral() {
		return valorCatastral;
	}
	public String getValorCatastralTotalInicial() {
		return valorCatastralTotalInicial;
	}
	public String getAnioRevisionCatastral() {
		return anioRevisionCatastral;
	}
	public String getSuperficie() {
		return superficie;
	}
	public String getCodMunicipio() {
		return codMunicipio;
	}
	
	public String getNumeroFijo() {
		return numeroFijo;
	}
	public String getCodigoVia() {
		return codigoVia;
	}
	public String getCodProvincia() {
		return codProvincia;
	}
	public String getProvincia() {
		return provincia;
	}
	public String getMunicipio() {
		return municipio;
	}
	private Finca(){
	}
	/**
	 * Recupera un objeto Finca, que corresponda a los datos de la referencia catastral, municipio y ejercicio.
	 * @param referenciaCatastral
	 * @param municipio
	 * @param ejercicio
	 * @param bd
	 * @return
	 * @throws PlusvaliaException
	 */
	public static Finca getFinca(String referenciaCatastral, String municipio, String ejercicio, MediadorBD bd) throws PlusvaliaException{
		if (referenciaCatastral==null || "".equals(referenciaCatastral)){
			return null;
		}
		try {
			DatosFinca d = bd.getDatosFinca(referenciaCatastral, municipio, ejercicio);
			if (d!=null){
				Finca f= new Finca();
				f.idEperFinca= d.getIdEperFinca();
				f.referenciaCatastral= d.getReferenciaCatastral();
				f.siglaVia= d.getSiglaVia();
				f.nombreVia= d.getNombreVia();
				f.numeroVia= d.getNumeroVia();
				f.duplicadoVia= d.getDuplicadoVia();
				f.escalera= d.getEscalera();
				f.planta= d.getPlanta();
				f.puerta= d.getPuerta();
				f.anioValorCatastral= d.getAnioValorCatastral();
				f.valorCatastralTotalInicial= d.getValorCatastralTotalInicial();
				f.superficie= d.getSuperficie();
				f.anioRevisionCatastral= d.getAnioRevisionCatastral();
				f.valorCatastral= d.getValorCatastral();
				f.codMunicipio= d.getCodMunicipio();
				f.municipio= d.getMunicipio();
				f.codProvincia= d.getCodProvincia();
				f.codigoVia= d.getCodigoVia();
				f.numeroFijo= d.getNumeroFijo();
				f.provincia= d.getProvincia();
				return f;
			} else {
				return null;
			}
		} catch (DatosException de){
			throw new PlusvaliaException(de);
		}
	}
	
}
