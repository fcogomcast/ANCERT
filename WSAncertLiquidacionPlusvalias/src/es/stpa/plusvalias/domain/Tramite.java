package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.exceptions.DatosException;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

public class Tramite {
	private String actoJuridico;
	private TipoTramite tipoTramite;
	private String fechaTramite;
	public String getActoJuridico() {
		return actoJuridico;
	}
	public TipoTramite getTipoTramite() {
		return tipoTramite;
	}
	public String getFechaTramite() {
		return fechaTramite;
	}
	
	public void setFechaTramite(String fechaTramite){
		this.fechaTramite = fechaTramite;
	}
	
	/**
	 * Recupera un objeto trámite que se corresponde con el acto jurídico indicado por el notario
	 * @param actoJuridico
	 * @param bd
	 * @return
	 * @throws PlusvaliaException
	 */
	public static Tramite getTramite(String actoJuridico, MediadorBD bd) throws PlusvaliaException{
		if (actoJuridico==null || "".equals(actoJuridico)){
			return null;
		}
		
		try {
			String tipo= bd.recuperarTipoTransmision(actoJuridico);
			if (tipo==null){
				return null;
			}
			Tramite t= new Tramite();
			t.tipoTramite= TipoTramite.fromValue(tipo);
			t.actoJuridico= actoJuridico;
			return t;
		} catch (DatosException e){
			throw new PlusvaliaException("Error al recuperar datos del trámite: " + e.getMessage(),e);
		}
	}

}
