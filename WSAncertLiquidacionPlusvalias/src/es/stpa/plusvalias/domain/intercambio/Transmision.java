package es.stpa.plusvalias.domain.intercambio;

import java.math.BigDecimal;

/**
 * Datos de una transmisión de bien, para utilizar en las transmisiones anteriores 
 * @author crubencvs
 *
 */
public class Transmision{
    protected String fecha;
    protected String porcentaje;
    protected BigDecimal valor;
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getPorcentaje() {
		return porcentaje;
	}
	public void setPorcentaje(String porcentaje) {
		this.porcentaje = porcentaje;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}