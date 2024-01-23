package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.domain.intercambio.Persona;

/**
 * Clase para caracterizar a los intervinientes, sólo
 * para transmitentes o adquirentes. 
 * Así permitirá basar el sujeto pasivo en uno de ambos.
 * @author crubencvs
 *
 */
public abstract class Interviniente {
	protected Persona datosbasicos;
	protected Long edadusufructuario;
    protected Long anyosusufructo;
    protected Long edaduso;
    protected Long anyosuso;
    
	public Persona getDatosbasicos() {
		return datosbasicos;
	}

	public void setDatosbasicos(Persona datosbasicos) {
		this.datosbasicos = datosbasicos;
	}
	
	public Long getEdadusufructuario() {
		return edadusufructuario;
	}
	public void setEdadusufructuario(Long edadusufructuario) {
		this.edadusufructuario = edadusufructuario;
	}
	public Long getAnyosusufructo() {
		return anyosusufructo;
	}
	public void setAnyosusufructo(Long anyosusufructo) {
		this.anyosusufructo = anyosusufructo;
	}
	public Long getEdaduso() {
		return edaduso;
	}
	public void setEdaduso(Long edaduso) {
		this.edaduso = edaduso;
	}
	public Long getAnyosuso() {
		return anyosuso;
	}
	public void setAnyosuso(Long anyosuso) {
		this.anyosuso = anyosuso;
	}
	
}
