package es.tributasenasturias.indices_fiscales.utils.bbdd;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Clase de utilidad para tratar las respuestas de lanzador.
 * No se ha buscado la eficiencia, sino la sencillez de uso.
 * Para uso más eficiente, debería parsearse la respuesta del lanzador
 * con un procesador SAX o StAX. Para respuestas cortas, este sirve perfectamente.
 * @author crubencvs
 *
 */
public class RespuestaLanzador {
	private Document doc;
	
	private Node estructuras; //Nodo que contiene las estructuras de retorno del lanzador.
	
	/**
	 * Almacena una respuesta de lanzador
	 * @param xml Texto de la respuesta de lanzador
	 * @throws LanzadorException Si no puede interpretar la respuesta de lanzador como un XML válido.
	 */
	public RespuestaLanzador(String xml) throws LanzadorException
	{
		doc=XMLUtils.parseXml(xml);
		estructuras = XMLUtils.selectSingleNode(doc, "estructuras");
		if (estructuras==null)
		{
			throw new LanzadorException ("La respuesta de lanzador no parece contener ninguna estructura.");
		}
	}
	/**
	 * Indica si la respuesta obtenido de lanzador contiene un error de llamada. Los errores de llamada 
	 * son los que devuelve la base de datos a través del lanzador, no errores de conexión con base de datos,
	 * por ejemplo.
	 * @return true si hay error en la respuesta, false si no.
	 */
	public boolean esErronea()
	{
		if (XMLUtils.selectSingleNode(this.doc, "//estructuras/error")!=null)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	/**
	 * Recupera el texto de error del lanzador si hay alguno.
	 * @return Texto del error de lanzador, o cadena vacía si no. 
	 */
	public String getTextoError()
	{
		Node texto=XMLUtils.selectSingleNode(this.doc, "//estructuras/error/text()");
		if (texto!=null)
		{
			return texto.getNodeValue();
		}
		else
		{
			return "";
		}
	}
	/**
	 * Recupera la estructura con el nombre indicado de todas las estruturas devueltas por el lanzador.
	 * @param nombreEstructura String que contiene el nombre de la estructura a buscar, tal como la devuelve el lanzador.
	 * @return {@link EstructuraLanzador} apuntando a la estructura de lanzador, o null si no se ha encontrado la estructura.
	 */
	public EstructuraLanzador getEstructura(String nombreEstructura)
	{
		EstructuraLanzador es= new EstructuraLanzador(estructuras,nombreEstructura);
		return es;
	}
	/**
	 * Recupera el valor de un nodo concreto de la respuesta del lanzador.
	 * @param estructura Nombre de la estructura devuelta por el lanzador
	 * @param numFila Número de fila de la estructura devuelta por el lanzador
	 * @param nombreCampo Nombre de campo dentro de la fila para la que queremos recuperar valor.
	 * @return Valor del ccampo, o bien "" si no se ha podido encontrar ese campo.
	 */
	public String getValue (String estructura, int numFila, String nombreCampo)
	{
		Node texto=XMLUtils.selectSingleNode(this.doc, "/estructuras/estruc[@nombre='"+estructura+"']/fila["+numFila+"]/"+nombreCampo+"/text()");
		if (texto!=null)
		{
			return texto.getNodeValue();
		}
		else
		{
			return "";
		}
	}
	/**
	 * Recupera el número de filas de la estructura indicada en la respuesta de lanzador.
	 * @param estructura Nombre de la estructura de la que se quiere saber el número de filas.
	 * @return Número de filas devueltas para la estructura, como un <b>int </b>.
	 */
	public int getNumFilasEstructura (String estructura)
	{
		return XMLUtils.selectNodesCount(this.doc, "count(/estructuras/estruc[@nombre='"+estructura+"']/fila)");
	}
	/**
	 * @return recupera el {@link Document} que contiene los datos de la respuesta.
	 */
	public Document getContentDoc() {
		return doc;
	}


	/**
	 * @param establece el {@link Document} que contiene los datos de la respuesta.
	 */
	public void setContentDoc(Document doc) {
		this.doc = doc;
	}
	
	
}
