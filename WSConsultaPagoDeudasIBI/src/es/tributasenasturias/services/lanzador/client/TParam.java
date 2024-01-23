package es.tributasenasturias.services.lanzador.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Representa cada uno de los parámetros del método que se invocará a través del
 * servicio lanzador.Cada parámetro tendrá un valor, un tipo y opcionalmente un
 * formato.
 * 
 * @author noelianbb
 * 
 */
public class TParam {

	// ----------------------------Atributos
	private String valor;
	private ParamType tipo;
	private String format;
	private int id;

	// ----------------------------Constructor
	/**
	 * Constructor por defecto
	 */
	protected TParam() {

		// Establecemos el formato de fecha por defecto
		this.format = CommonResources.DEFAULT_DATE_FORMAT;
	}

	/**
	 * Constructor sobrecargado
	 * 
	 * @param tipo
	 *            tipo del parámetro
	 * @param valor
	 *            valor del parámetro
	 * @param format
	 *            formato del parámetro si este es de tipo fecha (por defecto
	 *            'dd/MM/YYYY')
	 */
	protected TParam(ParamType tipo, String valor, String format) {
		// Establecemos el tipo
		this.tipo = tipo;

		// Establecemos el valor
		this.valor = valor;

		// Establecemos el formato de fecha para el tipo fecha, por defecto
		// DD/MM/YYYY
		switch (this.tipo) {
		case FECHA:
			if (CommonResources.CADENA_VACIA.equals(format))
				this.format = CommonResources.DEFAULT_DATE_FORMAT;
			else
				if (format!=null)
				{
					this.format = format;
				}
				else
				{
					this.format="";
				}
			break;

		default:
			if (format!=null)
			{
				this.format = format;
			}
			else
			{
				this.format="";
			}
			break;
		}

	}

	// --------------------------Getters y Setters
	/**
	 * @return el valor del parámetro
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor
	 *            del parámetro a establecer
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * @return el formato del parámetro fecha
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param establece
	 *            el formato del parámetro fecha
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return el tipo del parámetro
	 */
	public ParamType getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            de parámetro a establecer
	 */
	public void setTipo(ParamType tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return el id del parámetro (posición en la lista de parámetros)
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            el id a establecer
	 */
	public void setId(int id) {
		this.id = id;
	}

	// ---------------------------------Métodos----------------------------
	/**
	 * Genera el nodo XML que representa el parámetro
	 * 
	 * @param doc:
	 *            Documento al que va a pertenecer el nodo XML
	 * @return nodo XML que representa al parámetro o null en caso de error
	 * @throws LanzadorException
	 */
	public Element toXMLNode(Document doc) throws LanzadorException{

		try {

			// Creamos una etiqueta 'param' y la añadimos al documento
			Element param = doc.createElement(CommonResources.PARAM_NODE);
			// Establecemos el atributo id del nodo <param>
			param
					.setAttribute(CommonResources.ID_NODE, String
							.valueOf(this.id+1));
			param.setIdAttribute(CommonResources.ID_NODE, true);

			// Establecemos el valor del parámetro.Distinguimos entre CLOB_CDATA y el
			// resto de tipos.
			Element valor = doc.createElement(CommonResources.VALUE_NODE);
			if (ParamType.CLOB_CDATA.equals(tipo)) {
				valor.appendChild(doc.createCDATASection(this.valor));
			} else {
				valor.appendChild(doc.createTextNode(this.valor));
			}
			//valor.appendChild(doc.createTextNode(this.valor));
			param.appendChild(valor);

			// Establecemos el tipo del parámetro
			Element tipo = doc.createElement(CommonResources.TYPE_NODE);
			tipo.appendChild((doc.createTextNode(String.valueOf(this.tipo
					.getValor()))));
			param.appendChild(tipo);

			// En caso de fecha, establecemos el formato de la fecha
			Element formato = doc.createElement(CommonResources.FORMAT_NODE);
			if (this.format!=null)
			{
				formato.appendChild(doc.createTextNode(this.format));
			}
			else
			{
				formato.appendChild(doc.createTextNode(""));
			}
			param.appendChild(formato);

			// retornamos el elemento
			return param;

		} catch (Exception e) {
			throw new LanzadorException ("No se ha podido convertir el nodo a XML:" + e.getMessage(),e);
		}

	}

}
