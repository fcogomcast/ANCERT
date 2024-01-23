package es.stpa.plusvalias.domain.intercambio;

/**
 * Modela los datos de una escritura recibida en la petición. No incluye el contenido,
 * que nos vendrá como adjunto. 
 * @author crubencvs
 *
 */
public class DatosEscritura {
	private String digest;
	private String nombre;
	private int tamano;
	private String mimetype;
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getTamano() {
		return tamano;
	}
	public void setTamano(int tamano) {
		this.tamano = tamano;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	
	
}
