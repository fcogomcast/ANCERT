package es.tributasenasturias.services.ancert.enviodiligencias.bd;

/**
 * Modela los datos de una solicitud que nos interesan en este servicio.
 * @author crubencvs
 *
 */
public class DatosSolicitudDO {
	private String codNotario;
	private String codNotaria;
	private String numProtocolo;
	private String protocoloBis;
	private String anioAutorizacion;
	//Importante. Comprobamos si la solicitud está autorizada. En otro caso, 
	//no deberíamos enviar nada, por motivos legales
	private boolean autorizadaEnvio;
	public String getCodNotario() {
		return codNotario;
	}
	public void setCodNotario(String codNotario) {
		this.codNotario = codNotario;
	}
	public String getCodNotaria() {
		return codNotaria;
	}
	public void setCodNotaria(String codNotaria) {
		this.codNotaria = codNotaria;
	}
	public String getNumProtocolo() {
		return numProtocolo;
	}
	public void setNumProtocolo(String numProtocolo) {
		this.numProtocolo = numProtocolo;
	}
	public String getProtocoloBis() {
		return protocoloBis;
	}
	public void setProtocoloBis(String protocoloBis) {
		this.protocoloBis = protocoloBis;
	}
	public String getAnioAutorizacion() {
		return anioAutorizacion;
	}
	public void setAnioAutorizacion(String anioAutorizacion) {
		this.anioAutorizacion = anioAutorizacion;
	}
	public boolean isAutorizadaEnvio() {
		return autorizadaEnvio;
	}
	public void setAutorizadaEnvio(boolean autorizadaEnvio) {
		this.autorizadaEnvio = autorizadaEnvio;
	}
}
