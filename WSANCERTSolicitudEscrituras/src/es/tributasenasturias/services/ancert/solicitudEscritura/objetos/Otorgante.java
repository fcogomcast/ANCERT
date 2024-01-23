package es.tributasenasturias.services.ancert.solicitudEscritura.objetos;

/**
 * Clase que representa a un otorgante de la solicitud de escritura
 * @author crubencvs
 *
 */
public class Otorgante {
	private String nif;
	private String nombre;
	
	
	public Otorgante(String nif, String nombre) {
		super();
		this.nif = nif;
		this.nombre = nombre;
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
	
}
