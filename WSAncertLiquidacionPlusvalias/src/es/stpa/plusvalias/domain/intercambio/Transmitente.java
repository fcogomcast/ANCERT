package es.stpa.plusvalias.domain.intercambio;

import java.math.BigDecimal;
import java.util.List;

import es.stpa.plusvalias.domain.Interviniente;


/**
 * Datos de un transmitente
 * @author crubencvs
 *
 */
public class Transmitente extends Interviniente{
    protected String porcentajetransmitido;
    protected String clasederecho;
    protected String fechadefuncion;
    protected List<Transmision> transmisionesanteriores;
    protected Bonificacion bonificacion;
    protected Exencion exencion;
    protected List<Long> representantes;
    protected BigDecimal valorTransmision;
    protected boolean contribuyente;

    public String getPorcentajetransmitido() {
		return porcentajetransmitido;
	}
	public void setPorcentajetransmitido(String porcentajetransmitido) {
		this.porcentajetransmitido = porcentajetransmitido;
	}
	public String getClasederecho() {
		return clasederecho;
	}
	public void setClasederecho(String clasederecho) {
		this.clasederecho = clasederecho;
	}
	public String getFechadefuncion() {
		return fechadefuncion;
	}
	public void setFechadefuncion(String fechadefuncion) {
		this.fechadefuncion = fechadefuncion;
	}
	public List<Transmision> getTransmisionesanteriores() {
		return transmisionesanteriores;
	}
	public void setTransmisionesanteriores(
			List<Transmision> transmisionesanteriores) {
		this.transmisionesanteriores = transmisionesanteriores;
	}
	public Bonificacion getBonificacion() {
		return bonificacion;
	}
	public void setBonificacion(Bonificacion bonificacion) {
		this.bonificacion = bonificacion;
	}
	public Exencion getExencion() {
		return exencion;
	}
	public void setExencion(Exencion exencion) {
		this.exencion = exencion;
	}
	public List<Long> getRepresentantes() {
		return representantes;
	}
	public void setRepresentantes(List<Long> representantes) {
		this.representantes = representantes;
	}
	public BigDecimal getValorTransmision() {
		return valorTransmision;
	}
	public void setValorTransmision(BigDecimal valorTransmision) {
		this.valorTransmision = valorTransmision;
	}
	
}