package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.domain.intercambio.Protocolo;

public class DocumentoNotarial {
	private NotarioInterviniente notarioTitular;
	private NotarioInterviniente notarioAutorizante;
	private Protocolo protocolo;
	
	public DocumentoNotarial(NotarioInterviniente notarioTitular, NotarioInterviniente notarioAutorizante, Protocolo protocolo){
		this.notarioTitular= notarioTitular;
		this.notarioAutorizante= notarioAutorizante;
		this.protocolo= protocolo;
	}

	public NotarioInterviniente getNotarioTitular() {
		return notarioTitular;
	}

	public NotarioInterviniente getNotarioAutorizante() {
		return notarioAutorizante;
	}

	public Protocolo getProtocolo() {
		return protocolo;
	}
	
	
}
