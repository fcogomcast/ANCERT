package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos del protocolo
 * @author crubencvs
 *
 */
public class Protocolo{
    protected long numero;
    protected Long numerobis;
    protected String fechaautorizacion;
	public long getNumero() {
		return numero;
	}
	public void setNumero(long numero) {
		this.numero = numero;
	}
	public Long getNumerobis() {
		return numerobis;
	}
	public void setNumerobis(Long numerobis) {
		this.numerobis = numerobis;
	}
	public String getFechaautorizacion() {
		return fechaautorizacion;
	}
	public void setFechaautorizacion(String fechaautorizacion) {
		this.fechaautorizacion = fechaautorizacion;
	}
}