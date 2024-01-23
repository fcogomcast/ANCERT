package es.tributasenasturias.indices_fiscales.utils.bbdd;

import org.w3c.dom.Node;

/**
 * Implementa un objeto estructura de la respuesta de 
 * @author crubencvs
 *
 */
public class EstructuraLanzador {

	private String name;
	
	private Node nodoEstructura;
	

	@SuppressWarnings("unused")
	private EstructuraLanzador(){};
	
	protected EstructuraLanzador(Node nodoEstructuras,String nombreEstructura)
	{
		name=nombreEstructura;
		this.nodoEstructura = XMLUtils.selectSingleNode(nodoEstructuras, "estruc[@nombre='"+nombreEstructura+"']");
	}

	/**
	 * @return {@link Node} que contiene la estructura interna que modela este objeto.
	 */
	public Node getNodoEstructura() {
		return nodoEstructura;
	}

	/**
	 * @param La estructura interna {@link Node} que sirve de base a este objeto.
	 */
	public void setNodoEstructura(Node nodoEstructura) {
		this.nodoEstructura = nodoEstructura;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Recupera la fila con el ordinal especificado de la lista de filas de la estructura.
	 * @param numFila Número de fila a recuperar, con el ordinal 1 para primera fila.
	 * @return La fila encontrada o null si no hay fila.
	 */
	public FilaLanzador getFila(int numFila)
	{
		return new FilaLanzador(nodoEstructura,numFila);
	}
}
