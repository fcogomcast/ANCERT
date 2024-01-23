package es.tributasenasturias.services.ancert.solicitudEscritura.objetos;

import java.util.List;

/**
 * Encapsula los los datos de una solicitud.
 * @author crubencvs
 *
 */
public class SolicitudDO {
	private String idSolicitud;
	private String codigoNotario;
	private String codigoNotaria;
	private String numProtocolo;
	private String protocoloBis;
	private Integer anioAutorizacion;
	private String destinatario;
	private String finalidad;
	private Solicitante personaSolicitante;
	private String tipoProcedimiento;
	private String numeroExpediente;
	private List<Otorgante> otorgantes;
	private String estadoSolicitud;
	private String resultadoSolicitud;
	private String idOrigen;
	public String getCodigoNotario() {
		return codigoNotario;
	}
	public void setCodigoNotario(String codigoNotario) {
		this.codigoNotario = codigoNotario;
	}
	public String getCodigoNotaria() {
		return codigoNotaria;
	}
	public void setCodigoNotaria(String codigoNotaria) {
		this.codigoNotaria = codigoNotaria;
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
	public Integer getAnioAutorizacion() {
		return anioAutorizacion;
	}
	public void setAnioAutorizacion(Integer anioAutorizacion) {
		this.anioAutorizacion = anioAutorizacion;
	}
	public String getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	public String getFinalidad() {
		return finalidad;
	}
	public void setFinalidad(String finalidad) {
		this.finalidad = finalidad;
	}
	public String getEstadoSolicitud() {
		return estadoSolicitud;
	}
	public void setEstadoSolicitud(String estadoSolicitud) {
		this.estadoSolicitud = estadoSolicitud;
	}
	public String getResultadoSolicitud() {
		return resultadoSolicitud;
	}
	public void setResultadoSolicitud(String resultadoSolicitud) {
		this.resultadoSolicitud = resultadoSolicitud;
	}
	public String getIdSolicitud() {
		return idSolicitud;
	}
	public void setIdSolicitud(String idSolicitud) {
		this.idSolicitud = idSolicitud;
	}
	public String getIdOrigen() {
		return idOrigen;
	}
	public void setIdOrigen(String idOrigen) {
		this.idOrigen = idOrigen;
	}
	public Solicitante getPersonaSolicitante() {
		return personaSolicitante;
	}
	public void setPersonaSolicitante(Solicitante personaSolicitante) {
		this.personaSolicitante = personaSolicitante;
	}
	public String getTipoProcedimiento() {
		return tipoProcedimiento;
	}
	public void setTipoProcedimiento(String tipoProcedimiento) {
		this.tipoProcedimiento = tipoProcedimiento;
	}
	public String getNumeroExpediente() {
		return numeroExpediente;
	}
	public void setNumeroExpediente(String numeroExpediente) {
		this.numeroExpediente = numeroExpediente;
	}
	public List<Otorgante> getOtorgantes() {
		return otorgantes;
	}
	public void setOtorgantes(List<Otorgante> otorgantes) {
		this.otorgantes = otorgantes;
	}
	
	
}
