package es.tributasenasturias.services.ancert.recepcionescritura.objetos;

/**
 * Implementa los datos de una escritura.
 * @author crubencvs
 *
 */
public class EscrituraDO {

	private String codNotario;
	private String codNotaria;
	private String numProtocolo;
	private String protocoloBis;
	private String fechaEscritura;
	private String anyoEscritura;
	private boolean autorizacionEnvioDiligencias;
	
	public boolean isAutorizacionEnvioDiligencias() {
		return autorizacionEnvioDiligencias;
	}
	public void setAutorizacionEnvioDiligencias(boolean autorizacionEnvioDiligencias) {
		this.autorizacionEnvioDiligencias = autorizacionEnvioDiligencias;
	}
	public String getCodNotario() {
		return codNotario;
	}
	public final void setCodNotario(String codNotario) {
		this.codNotario = codNotario;
	}
	public final String getCodNotaria() {
		return codNotaria;
	}
	public final void setCodNotaria(String codNotaria) {
		this.codNotaria = codNotaria;
	}
	public final String getNumProtocolo() {
		return numProtocolo;
	}
	public final void setNumProtocolo(String numProtocolo) {
		this.numProtocolo = numProtocolo;
	}
	public final String getProtocoloBis() {
		return protocoloBis;
	}
	public final void setProtocoloBis(String protocoloBis) {
		this.protocoloBis = protocoloBis;
	}
	public final String getFechaEscritura() {
		return fechaEscritura;
	}
	public final void setFechaEscritura(String fechaEscritura) {
		this.fechaEscritura = fechaEscritura;
	}
	public String getAnyoEscritura() {
		return anyoEscritura;
	}
	public void setAnyoEscritura(String anyoEscritura) {
		this.anyoEscritura = anyoEscritura;
	}
	
	
}
