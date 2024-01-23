package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos de un sujeto, utilizado para modelar a las personas de la intervención
 * @author crubencvs
 *
 */
public class Persona{
	protected long identificador;
    protected DatosPersonales datospersonales;
    protected DireccionInmueble domicilio;
    protected DireccionExtranjera domicilioextranjero;
	public long getIdentificador() {
		return identificador;
	}
	public void setIdentificador(long identificador) {
		this.identificador = identificador;
	}
	public DatosPersonales getDatospersonales() {
		return datospersonales;
	}
	public void setDatospersonales(DatosPersonales datospersonales) {
		this.datospersonales = datospersonales;
	}
	public DireccionInmueble getDomicilio() {
		return domicilio;
	}
	public void setDomicilio(DireccionInmueble domicilio) {
		this.domicilio = domicilio;
	}
	public DireccionExtranjera getDomicilioextranjero() {
		return domicilioextranjero;
	}
	public void setDomicilioextranjero(DireccionExtranjera domicilioextranjero) {
		this.domicilioextranjero = domicilioextranjero;
	}
}