package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos personales de un sujeto
 * @author crubencvs
 *
 */
public class DatosPersonales{
    protected String tipodocumento;
    protected String numerodocumento;
    protected String nombre;
    protected String apellido1RAZONSOCIAL;
    protected String apellido2;
	public String getTipodocumento() {
		return tipodocumento;
	}
	public void setTipodocumento(String tipodocumento) {
		this.tipodocumento = tipodocumento;
	}
	public String getNumerodocumento() {
		return numerodocumento;
	}
	public void setNumerodocumento(String numerodocumento) {
		this.numerodocumento = numerodocumento;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido1RAZONSOCIAL() {
		return apellido1RAZONSOCIAL;
	}
	public void setApellido1RAZONSOCIAL(String apellido1RAZONSOCIAL) {
		this.apellido1RAZONSOCIAL = apellido1RAZONSOCIAL;
	}
	public String getApellido2() {
		return apellido2;
	}
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}
}