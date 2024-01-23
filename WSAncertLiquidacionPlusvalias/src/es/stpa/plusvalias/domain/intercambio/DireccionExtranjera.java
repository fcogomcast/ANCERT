package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos de la dirección extranjera de un sujeto
 * @author crubencvs
 *
 */
public class DireccionExtranjera{
    protected String pais;
    protected String estprv;
    protected String descmunicipio;
    protected String direccionnoestructurada;
    protected String distritopostal;
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getEstprv() {
		return estprv;
	}
	public void setEstprv(String estprv) {
		this.estprv = estprv;
	}
	public String getDescmunicipio() {
		return descmunicipio;
	}
	public void setDescmunicipio(String descmunicipio) {
		this.descmunicipio = descmunicipio;
	}
	public String getDireccionnoestructurada() {
		return direccionnoestructurada;
	}
	public void setDireccionnoestructurada(String direccionnoestructurada) {
		this.direccionnoestructurada = direccionnoestructurada;
	}
	public String getDistritopostal() {
		return distritopostal;
	}
	public void setDistritopostal(String distritopostal) {
		this.distritopostal = distritopostal;
	}
}