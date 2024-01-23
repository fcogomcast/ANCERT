package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos de estado de finalizaci�n de la operaci�n
 * @author crubencvs
 *
 */
public class ResultadoType{
    protected boolean esEstadoError;
    // Error tal como se mostrar� al cliente del servicio
    protected ErrorType resultado;
	public boolean esEstadoError() {
		return esEstadoError;
	}
	public void setEstadoError(boolean esError) {
		this.esEstadoError = esError;
	}
	public ErrorType getError() {
		return resultado;
	}
	public void setError(ErrorType error) {
		this.resultado = error;
	}
    
}