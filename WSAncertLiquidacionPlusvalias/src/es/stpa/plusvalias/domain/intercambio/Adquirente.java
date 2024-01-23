package es.stpa.plusvalias.domain.intercambio;

import java.util.List;

import es.stpa.plusvalias.domain.Interviniente;


/**
 * Datos de un adquirente
 * @author crubencvs
 *
 */
public class Adquirente extends Interviniente{
    protected String porcentajeadquirido;
    protected String  fechaProrrogaSolicitada;
    protected String clasederecho;
    protected List<Long> representantes;

	public String getPorcentajeadquirido() {
		return porcentajeadquirido;
	}
	public void setPorcentajeadquirido(String porcentajeadquirido) {
		this.porcentajeadquirido = porcentajeadquirido;
	}
	public String getFechaProrrogaSolicitada() {
		return fechaProrrogaSolicitada;
	}
	public void setFechaProrrogaSolicitada(String fechaProrrogaSolicitada) {
		this.fechaProrrogaSolicitada = fechaProrrogaSolicitada;
	}
	public String getClasederecho() {
		return clasederecho;
	}
	public void setClasederecho(String clasederecho) {
		this.clasederecho = clasederecho;
	}
	public List<Long> getRepresentantes() {
		return representantes;
	}
	public void setRepresentantes(List<Long> representantes) {
		this.representantes = representantes;
	}
	
}