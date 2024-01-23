package es.tributasenasturias.lanzador;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * @author noelianbb
 * Clase que representa a un objeto petición. 
 * Contendrá un nombre de procedimiento al que invocar 
 * y 0 ó más parámetros
 */
public class TPeticion {
	
	//----------------------------Atributos
	/**
	 * Nombre del procedimiento almacenado al que se pretende llamar
	 */
	private String procName;
	/**
	 * Instancia de base de datos contra la que ejecutar esta petición.
	 */
	private String esquema;
	/**
	 * Array de parámetros necesarios en la llamada al proc. almacenado.
	 */
	private ArrayList<TParam> params;
	
	
	//-------------------------Getters y Setters
	/**
	 * Obtiene el nombre del proc. almacenado
	 */
	public String getProcName(){
		return this.procName;
		
	}
	
	/**
	 * Establece el nombre del procedimiento almacenado
	 * @param procName Nombre del procedimiento almacenado
	 */
	public void setProcName(String procName){
		this.procName = procName;
	}
	
	
	
	//-------------------------Constructor
	/**
	 * Constructor por defecto
	 */
@SuppressWarnings("unused")
private TPeticion()
{
	
}
/**
 * Instancia un objeto petición, sobre un nombre de procedimiento concreto y en un
 * esquema de base de datos.
 * @param procName Nombre de procedimiento almacenado que se va a ejecutar.
 * @param esquema Nombre de esquema donde se va a ejecutar el procedimiento almacenado.
 */
protected TPeticion(String procName, String esquema)
{
	this.procName=procName;
	this.esquema=esquema ;
	this.params = new ArrayList<TParam>();
}

//--------------------------------Métodos
/**
 * Añade un nuevo parámetro a la lista de parámetros
 * @param TParam a añadir
 */
public void addParam(TParam param)
{
	//Establecemos el id (ordinal en la lista)
	param.setId(this.params.size());
	//Añadimos a la lista
	this.params.add(param);
}


/**
 * 
 * @return XML de la petición
 */
public String toXml() throws LanzadorException
{
	StringWriter sw=null;
	try {
       
        //Creamos un documento XML vacío
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        dbfac.setNamespaceAware(true);
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        //Creamos la estructura XML -> contenido del mensaje
        //creamos el elemento raiz (etiqueta 'peti')y lo añadimos al documento
        Element peti = doc.createElement(CommonResources.PETI_NODE);
        //creamos un elemento 'etiqueta proc', le agregamos un atributo y se lo añadimos
        //al elemento raiz
        Element proc = doc.createElement(CommonResources.PROC_NODE);
        proc.setAttribute(CommonResources.NAME_PROC_NODE, this.procName);
        //Añadimos los parámetros (etiqueta 'param') de la llamada
        for (int i = 0; i < this.params.size(); i++) {
        	proc.appendChild(this.params.get(i).toXMLNode(doc));
		}
        peti.appendChild(proc);
        doc.appendChild(peti);
        //Establecemos el transformador
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        //creamos el xml
        sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        String xmlString = sw.toString();
        return xmlString;
    } catch (Exception e) {
        throw new LanzadorException ("Error al transformar la petición a XML:" + e.getMessage(),e);
    }
    finally
    {
    	if (sw!=null)
    	{
    		try {sw.close();}
    		catch (Exception e){}
    	}
    }
	}

/**
 * 
 * @return valor de Esquema
 */
public String getEsquema() {
	return esquema;
}

/**
 * 
 * @param esquema establece el esquema de base de datos a donde se van a dirigir las peticiones.
 */
public void setEsquema(String esquema) {
	this.esquema = esquema;
}
/**
 * Recupera el array de TParam que forma sus parámetros.
 * @return
 */
public ArrayList<TParam> getParams() {
	return params;
}
/**
 * Establece el array de TParam que se va a tomar como parámetros de la petición.
 * @param params
 */
public void setParams(ArrayList<TParam> params) {
	this.params = params;
}
	

}
