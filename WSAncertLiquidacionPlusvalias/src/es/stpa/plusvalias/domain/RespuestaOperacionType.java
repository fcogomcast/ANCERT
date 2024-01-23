package es.stpa.plusvalias.domain;

import java.util.List;

import es.stpa.plusvalias.domain.intercambio.DocumentoType;
import es.stpa.plusvalias.domain.intercambio.Inmueble;
import es.stpa.plusvalias.domain.intercambio.ResultadoType;


/**
 * Respuesta de la operación
 * @author crubencvs
 *
 */
public class RespuestaOperacionType{
	protected PeticionOperacionType peticionOperacion;
	protected Inmueble inmueble;
    protected double importetotal;
    protected List<DocumentoType> documentos;
    protected ResultadoType resultado;
    
    public RespuestaOperacionType(PeticionOperacionType peticion){
    	this.peticionOperacion= peticion;
    }

	public PeticionOperacionType getPeticionOperacion() {
		return peticionOperacion;
	}

	public void setPeticionOperacion(PeticionOperacionType peticionOperacion) {
		this.peticionOperacion = peticionOperacion;
	}

	public double getImportetotal() {
		return importetotal;
	}

	public void setImportetotal(double importetotal) {
		this.importetotal = importetotal;
	}

	public List<DocumentoType> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocumentoType> documentos) {
		this.documentos = documentos;
	}

	public ResultadoType getResultado() {
		return resultado;
	}

	public void setResultado(ResultadoType resultado) {
		this.resultado = resultado;
	}

	public Inmueble getInmueble() {
		return inmueble;
	}

	public void setInmueble(Inmueble inmueble) {
		this.inmueble = inmueble;
	}
    
}