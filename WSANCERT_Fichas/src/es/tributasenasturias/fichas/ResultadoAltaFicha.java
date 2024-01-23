package es.tributasenasturias.fichas;


public class ResultadoAltaFicha {

	private IdEscrituraType idEscritura;
	private boolean esError;
	private String codigo;
	private String mensaje;
	public IdEscrituraType getIdEscritura() {
		return idEscritura;
	}
	public void setIdEscritura(IdEscrituraType idEscritura) {
		this.idEscritura = idEscritura;
	}
	public boolean isEsError() {
		return esError;
	}
	public void setEsError(boolean esError) {
		this.esError = esError;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
}
