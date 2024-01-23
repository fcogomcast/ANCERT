package es.stpa.plusvalias.domain.intercambio;

/**
 * Dato de una exención
 * @author crubencvs
 *
 */
public class Exencion{
    protected String porcentaje;
    protected String concepto;
	public String getPorcentaje() {
		return porcentaje;
	}
	public void setPorcentaje(String porcentaje) {
		this.porcentaje = porcentaje;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
}