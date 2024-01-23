package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.datos.MediadorBD.DatosNotario;
import es.stpa.plusvalias.domain.intercambio.Notario;
import es.stpa.plusvalias.exceptions.DatosException;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

/**
 * Clase base de notario en el documento notarial.
 * Se diferencia de la clase {@link Notario} en que esta tiene los apellidos en
 * un solo campo, y el código de notaria.
 * Además, 
 * @author crubencvs
 *
 */
public final class NotarioInterviniente {
	private String codigo;
	private String nif;
	private String nombre;
	private String apellidos;
	//Esto no pertenece aquí, pero como tenemos un ámbito tan reducido,
	//no creo una clase para la plaza
	private String codNotaria;
	private String calleNotaria;
	private String codigoProvinciaNotaria;
	private String provinciaNotaria;
	private String codigoMunicipioNotaria;
	private String municipioNotaria;
	private String codigoPostalNotaria;
	private String plaza;
	
	private NotarioInterviniente(){
		
	}
	/**
	 * Instancia un nuevo objeto con los datos del notario en esa fecha.
	 * @param codigo
	 * @param fecha
	 * @param bd
	 * @return
	 * @throws PlusvaliaException
	 */
	public static NotarioInterviniente getNotario(String codigo, String fecha, MediadorBD bd) throws PlusvaliaException{
		if (codigo==null || "".equals(codigo)){
			return null;
		}
		try {
			DatosNotario d= bd.getDatosNotario(codigo, fecha);
			if (d.existe()){
				NotarioInterviniente n= new NotarioInterviniente();
				n.codigo = d.getCodigo();
				n.nif= d.getNif();
				n.nombre= d.getNombre();
				n.apellidos= d.getApellidos();
				n.codNotaria= d.getCodNotaria();
				n.plaza = d.getPlaza();
				n.calleNotaria= d.getCalleNotaria();
				n.codigoProvinciaNotaria= d.getCodProvinciaNotaria();
				n.provinciaNotaria= d.getProvinciaNotaria();
				n.codigoMunicipioNotaria= d.getMunicipioNotaria();
				n.municipioNotaria= d.getMunicipioNotaria();
				n.codigoPostalNotaria= d.getCodigoPostalNotaria();
				
				return n;
			} else {
				return null;
			}
		} catch (DatosException de){
			throw new PlusvaliaException(de);
		}
	}

	public String getCodigo() {
		return codigo;
	}

	public String getNif() {
		return nif;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public String getCodNotaria() {
		return codNotaria;
	}
	public String getCalleNotaria() {
		return calleNotaria;
	}
	public String getCodigoProvinciaNotaria() {
		return codigoProvinciaNotaria;
	}
	public String getProvinciaNotaria() {
		return provinciaNotaria;
	}
	public String getCodigoMunicipioNotaria() {
		return codigoMunicipioNotaria;
	}
	public String getMunicipioNotaria() {
		return municipioNotaria;
	}
	public String getCodigoPostalNotaria() {
		return codigoPostalNotaria;
	}
	public String getPlaza() {
		return plaza;
	}

	
	
}
