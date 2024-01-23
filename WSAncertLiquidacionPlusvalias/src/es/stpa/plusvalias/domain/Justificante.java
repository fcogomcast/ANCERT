package es.stpa.plusvalias.domain;

/**
 * Almacena los datos del n�mero de justificante a utilizar en la presentaci�n.
 * Habr� una instancia por cada liquidaci�n
 * @author crubencvs
 *
 */
public class Justificante {
	
	private String emisora;
	private String numeroJustificante;
	
	public String getEmisora() {
		return emisora;
	}
	public String getNumeroJustificante() {
		return numeroJustificante;
	}
	
	public Justificante(String emisora, String numeroJustificante){
		this.emisora= emisora;
		this.numeroJustificante= numeroJustificante;
	}

}
