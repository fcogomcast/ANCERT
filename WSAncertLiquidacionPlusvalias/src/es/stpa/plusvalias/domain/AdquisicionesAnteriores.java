package es.stpa.plusvalias.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.stpa.plusvalias.domain.intercambio.Transmision;
import es.stpa.plusvalias.domain.intercambio.Transmitente;

/**
 * Adquisiciones anteriores, por transmitente.
 * @author crubencvs
 *
 */
public class AdquisicionesAnteriores {

	private Map<Long, InfoAdquisiciones> listAdquisiciones = new HashMap<Long, InfoAdquisiciones>();
	
	/**
	 * Información de las adquisiciones anteriores.
	 * @author crubencvs
	 *
	 */
	public static class InfoAdquisiciones{
		
		private List<String> fechasAdquisicionAnteriores= new ArrayList<String>();
		private List<String> porcentajesAnteriores= new ArrayList<String>();
		private List<BigDecimal> valoresAdquisicion= new ArrayList<BigDecimal>();
		
		private long sumaValoresAdquisicion = 0;
		
		public List<String> getFechasAdquisicionAnteriores() {
			return fechasAdquisicionAnteriores;
		}

		public List<String> getPorcentajesAnteriores() {
			return porcentajesAnteriores;
		}

		public List<BigDecimal> getValoresAdquisicion() {
			return valoresAdquisicion;
		}

		public long getSumaValoresAdquisicion() {
			return sumaValoresAdquisicion;
		}

		public void setSumaValoresAdquisicion(long sumaValoresAdquisicion) {
			this.sumaValoresAdquisicion = sumaValoresAdquisicion;
		}

	
	}
	/**
	 * Constructor, informará los datos 
	 * @param peticion
	 */
	public AdquisicionesAnteriores(PeticionOperacionType peticion){
		for (Transmitente t: peticion.getTransmitentes()){
			InfoAdquisiciones info= tratarTransmitente(t);
			listAdquisiciones.put(t.getDatosbasicos().getIdentificador(), info);
		}
	}
	
	private InfoAdquisiciones tratarTransmitente(Transmitente transmitente){
		InfoAdquisiciones i = new InfoAdquisiciones();
		for (Transmision a: transmitente.getTransmisionesanteriores()){
			i.fechasAdquisicionAnteriores.add(a.getFecha());
			i.porcentajesAnteriores.add(a.getPorcentaje());
			i.valoresAdquisicion.add(a.getValor());
		}
		for (BigDecimal d: i.getValoresAdquisicion()){
			i.sumaValoresAdquisicion+= d.longValue()*100;
		}
		return i;
	}
	/**
	 * Devuelve los datos de las adquisiciones anteriores de un transmitente
	 * @param t Datos del Transmitente, {@link Transmitente}
	 * @return
	 */
	public InfoAdquisiciones getAdquisicionesAnterioresTransmitente(Transmitente t){
		if (this.listAdquisiciones.containsKey(t.getDatosbasicos().getIdentificador())){
			return listAdquisiciones.get(t.getDatosbasicos().getIdentificador());
		} else {
			//Lo calculamos en el momento, si se puede.
			InfoAdquisiciones i= tratarTransmitente(t);
			listAdquisiciones.put(t.getDatosbasicos().getIdentificador(), i);
			return i;
		}
	}
	
}
