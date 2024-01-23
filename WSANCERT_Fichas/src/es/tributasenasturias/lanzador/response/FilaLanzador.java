package es.tributasenasturias.lanzador.response;

import org.w3c.dom.Node;

public class FilaLanzador {

	private Node nodoFila;
	private Node nodoEstructura;
	private int numero;
	
	protected FilaLanzador(Node estructura, int numFila)
	{
		nodoEstructura=estructura;
		numero = numFila;
		if (nodoEstructura!=null)
		{
			nodoFila = XMLUtils.selectSingleNode(nodoEstructura, "fila["+String.valueOf(numero)+"]");
		}
	}
	public String getCampo(String nombreCampo)
	{
		String valor=null;
		if (nodoFila!=null)
		{
			Node nodo = XMLUtils.selectSingleNode(nodoFila, nombreCampo+"/text()");
			if (nodo!=null)
			{
				valor = nodo.getNodeValue();
			}
		}
		return valor;
	}
}
