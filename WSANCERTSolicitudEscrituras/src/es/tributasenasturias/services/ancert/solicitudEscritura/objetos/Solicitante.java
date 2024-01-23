package es.tributasenasturias.services.ancert.solicitudEscritura.objetos;

/**
 * Clase de la persona solicitante
 * @author crubencvs
 *
 */
public class Solicitante {
	private String nif;
	private String nombre;
	private String cargo;
	
	
	public Solicitante(String nif, String nombre, String cargo) {
		super();
		this.nif = nif;
		this.nombre = nombre;
		this.cargo = cargo;
	}
	
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	
}
