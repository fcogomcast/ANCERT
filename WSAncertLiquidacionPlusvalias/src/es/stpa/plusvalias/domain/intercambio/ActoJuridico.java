package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos del acto jur�dico
 * @author crubencvs
 *
 */
public class ActoJuridico{
    protected String codigo;
    protected String descripcion;
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}