package es.stpa.plusvalias.domain.intercambio;

/**
 * Código/Descripción del retorno 
 * @author crubencvs
 *
 */
public class ErrorType{
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