package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos de una dirección de inmueble. Utilizado para el inmueble y para el sujeto
 * @author crubencvs
 *
 */
public class DireccionInmueble{
    protected String ineprovincia;
    protected String inemunicipio;
    protected TipoVia tipovia;
    protected String via;
    protected String numerovia;
    protected String escalera;
    protected String planta;
    protected String puerta;
    protected Duplicado duplicado;
    protected String bloque;
    protected Integer aprkm;
    protected String entidadmenor;
    protected String resto;
    protected String codigopostal;
	public String getIneprovincia() {
		return ineprovincia;
	}
	public void setIneprovincia(String ineprovincia) {
		this.ineprovincia = ineprovincia;
	}
	public String getInemunicipio() {
		return inemunicipio;
	}
	public void setInemunicipio(String inemunicipio) {
		this.inemunicipio = inemunicipio;
	}
	public TipoVia getTipovia() {
		return tipovia;
	}
	public void setTipovia(TipoVia tipovia) {
		this.tipovia = tipovia;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public String getNumerovia() {
		return numerovia;
	}
	public void setNumerovia(String numerovia) {
		this.numerovia = numerovia;
	}
	public String getEscalera() {
		return escalera;
	}
	public void setEscalera(String escalera) {
		this.escalera = escalera;
	}
	public String getPlanta() {
		return planta;
	}
	public void setPlanta(String planta) {
		this.planta = planta;
	}
	public String getPuerta() {
		return puerta;
	}
	public void setPuerta(String puerta) {
		this.puerta = puerta;
	}
	public Duplicado getDuplicado() {
		return duplicado;
	}
	public void setDuplicado(Duplicado duplicado) {
		this.duplicado = duplicado;
	}
	public String getBloque() {
		return bloque;
	}
	public void setBloque(String bloque) {
		this.bloque = bloque;
	}
	public Integer getAprkm() {
		return aprkm;
	}
	public void setAprkm(Integer aprkm) {
		this.aprkm = aprkm;
	}
	public String getEntidadmenor() {
		return entidadmenor;
	}
	public void setEntidadmenor(String entidadmenor) {
		this.entidadmenor = entidadmenor;
	}
	public String getResto() {
		return resto;
	}
	public void setResto(String resto) {
		this.resto = resto;
	}
	public String getCodigopostal() {
		return codigopostal;
	}
	public void setCodigopostal(String codigopostal) {
		this.codigopostal = codigopostal;
	}
}