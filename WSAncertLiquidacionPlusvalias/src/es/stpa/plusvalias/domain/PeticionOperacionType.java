package es.stpa.plusvalias.domain;

import java.util.List;

import es.stpa.plusvalias.domain.intercambio.ActoJuridico;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Cabecera;
import es.stpa.plusvalias.domain.intercambio.DatosEscritura;
import es.stpa.plusvalias.domain.intercambio.Inmueble;
import es.stpa.plusvalias.domain.intercambio.Notario;
import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.domain.intercambio.Protocolo;
import es.stpa.plusvalias.domain.intercambio.Transmitente;

/**
 * Datos de una petición de operación
 * @author crubencvs
 *
 */
public class PeticionOperacionType{
    protected Cabecera cabecera;
    protected Notario notariotitular;
    protected Notario notarioautorizante;
    protected Protocolo protocolo;
    protected ActoJuridico actojuridico;
    protected List<Transmitente> transmitentes;
    protected List<Adquirente> adquirentes;
    protected Inmueble inmueble;
    protected List<Persona> representantes;
    protected DatosEscritura datosEscritura;
    protected DocumentoNotarial docNotarial;
    protected Finca finca;
    protected Tramite tramite;
    protected OrdenesPago ordenesPago;
	public Cabecera getCabecera() {
		return cabecera;
	}
	public void setCabecera(Cabecera cabecera) {
		this.cabecera = cabecera;
	}
	public Notario getNotariotitular() {
		return notariotitular;
	}
	public void setNotariotitular(Notario notariotitular) {
		this.notariotitular = notariotitular;
	}
	public Notario getNotarioautorizante() {
		return notarioautorizante;
	}
	public void setNotarioautorizante(Notario notarioautorizante) {
		this.notarioautorizante = notarioautorizante;
	}
	public Protocolo getProtocolo() {
		return protocolo;
	}
	public void setProtocolo(Protocolo protocolo) {
		this.protocolo = protocolo;
	}
	public ActoJuridico getActojuridico() {
		return actojuridico;
	}
	public void setActojuridico(ActoJuridico actojuridico) {
		this.actojuridico = actojuridico;
	}
	public List<Transmitente> getTransmitentes() {
		return transmitentes;
	}
	public void setTransmitentes(List<Transmitente> transmitentes) {
		this.transmitentes = transmitentes;
	}
	public List<Adquirente> getAdquirentes() {
		return adquirentes;
	}
	public void setAdquirentes(List<Adquirente> adquirentes) {
		this.adquirentes = adquirentes;
	}
	public Inmueble getInmueble() {
		return inmueble;
	}
	public void setInmueble(Inmueble inmueble) {
		this.inmueble = inmueble;
	}
	public List<Persona> getRepresentantes() {
		return representantes;
	}
	public void setRepresentantes(List<Persona> representantes) {
		this.representantes = representantes;
	}
	public DocumentoNotarial getDocNotarial() {
		return docNotarial;
	}
	public void setDocNotarial(DocumentoNotarial docNotarial) {
		this.docNotarial = docNotarial;
	}
	
	public DatosEscritura getDatosEscritura() {
		return datosEscritura;
	}
	public void setDatosEscritura(DatosEscritura datosEscritura) {
		this.datosEscritura = datosEscritura;
	}
	public Finca getFinca() {
		return finca;
	}
	public void setFinca(Finca finca) {
		this.finca = finca;
	}
	public Tramite getTramite() {
		return tramite;
	}
	public void setTramite(Tramite tramite) {
		this.tramite = tramite;
	}
	public OrdenesPago getOrdenesPago() {
		return ordenesPago;
	}
	public void setOrdenesPago(OrdenesPago ordenesPago) {
		this.ordenesPago = ordenesPago;
	}
	
	
}