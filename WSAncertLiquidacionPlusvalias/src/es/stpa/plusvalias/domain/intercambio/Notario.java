package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos del notario en la petición
 * @author crubencvs
 *
 */
public final class Notario{
    private String codigo;
    private String nombre;
    private String apellido1;
    private String apellido2;
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido1() {
		return apellido1;
	}
	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}
	public String getApellido2() {
		return apellido2;
	}
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}
}