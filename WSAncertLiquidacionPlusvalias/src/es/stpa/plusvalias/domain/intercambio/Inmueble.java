package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos de un inmueble
 * @author crubencvs
 *
 */
public class Inmueble{
	private String referenciacatastral;
	private DireccionInmueble direccion;
	private String superficieInmueble;
    private String idEperFinca;
    private String anioValorCatastral;
    private String valorCatastral;
    private String valorCatastralTotalInicial;
    private String anioRevisionCatastral;
    private String municipioCompletoNoINE;
	public String getReferenciacatastral() {
		return referenciacatastral;
	}
	public void setReferenciacatastral(String referenciacatastral) {
		this.referenciacatastral = referenciacatastral;
	}
	public DireccionInmueble getDireccion() {
		return direccion;
	}
	public void setDireccion(DireccionInmueble direccion) {
		this.direccion = direccion;
	}
	public String getSuperficieInmueble() {
		return superficieInmueble;
	}
	public void setSuperficieInmueble(String superficieInmueble) {
		this.superficieInmueble = superficieInmueble;
	}
	public String getIdEperFinca() {
		return idEperFinca;
	}
	public void setIdEperFinca(String idEperFinca) {
		this.idEperFinca = idEperFinca;
	}
	public String getAnioValorCatastral() {
		return anioValorCatastral;
	}
	public void setAnioValorCatastral(String anioValorCatastral) {
		this.anioValorCatastral = anioValorCatastral;
	}
	public String getValorCatastral() {
		return valorCatastral;
	}
	public void setValorCatastral(String valorCatastral) {
		this.valorCatastral = valorCatastral;
	}
	public String getValorCatastralTotalInicial() {
		return valorCatastralTotalInicial;
	}
	public void setValorCatastralTotalInicial(String valorCatastralTotalInicial) {
		this.valorCatastralTotalInicial = valorCatastralTotalInicial;
	}
	public String getAnioRevisionCatastral() {
		return anioRevisionCatastral;
	}
	public void setAnioRevisionCatastral(String anioRevisionCatastral) {
		this.anioRevisionCatastral = anioRevisionCatastral;
	}
	public String getMunicipioCompletoNoINE() {
		return municipioCompletoNoINE;
	}
	public void setMunicipioCompletoNoINE(String municipioCompletoNoINE) {
		this.municipioCompletoNoINE = municipioCompletoNoINE;
	}
	
	
	
}