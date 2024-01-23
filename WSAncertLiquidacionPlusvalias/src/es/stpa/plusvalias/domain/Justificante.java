package es.stpa.plusvalias.domain;

/**
 * Almacena los datos del número de justificante a utilizar en la presentación.
 * Habrá una instancia por cada liquidación
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
