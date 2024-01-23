package es.tributasenasturias.indices_fiscales;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Modela la cabecera de comunicación que se recibe en el mensaje. Esta información la podremos utilizar para 
 * devolver el mensaje con los mismos datos de cabecera que en el recibido.
 * @author crubencvs
 *
 */
public class CabeceraComunicacion extends DefaultHandler{
	
	private final String marca="FINALIZA";
	private XMLReader xr;
	private StringBuilder sb;
	private String timestamp;
	private String numMsj;
	private String idApl;
	private String funcion;
	private String emisor;
	private String tipoRecep;
	private String recep;
	
	private Stack<String> elementos = new Stack<String>();
	
	public String getTimestamp() {
		return timestamp;
	}
	public String getNumMsj() {
		return numMsj;
	}
	public String getIdApl() {
		return idApl;
	}
	public String getFuncion() {
		return funcion;
	}
	public String getEmisor() {
		return emisor;
	}
	public String getTipoRecep() {
		return tipoRecep;
	}
	public String getRecep() {
		return recep;
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (sb!=null)
		{
			sb.append(ch, start,length);
		}
	}
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if ("CABECERA".equals(localName))
		{
			//Terminamos la cabecera, salimos. Es necesario terminar con una excepción que se puede controlar
			throw new SAXException(marca);
		}
		else if (elementos.size()>0 && elementos.peek().equals(localName))
		{
			elementos.pop();
			//Añadimos su texto a la variable correspondiente
			if ("TIMESTAMP".equals(localName)){
				this.timestamp=sb.toString();
			}else if ("NUM_MSJ".equals(localName)){
				this.numMsj=sb.toString();
			}else if ("ID_APL".equals(localName)){
				this.idApl= sb.toString();
			}else if ("FUNCION".equals(localName)){
				this.funcion=sb.toString();
			}else if ("EMISOR".equals(localName)){
				this.emisor=sb.toString();
			}else if ("TIPO_RECEP".equals(localName)){
				this.tipoRecep=sb.toString();
			}else if("RECEP".equals(localName)){
				this.recep=sb.toString();
			}
			sb=null;
		}
	}
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		//Si el elemento padre es cabecera, guardamos en la pila de elementos
		if ("CABECERA".equals(localName))
		{
			elementos.push(localName);
		}
		else if (elementos.size()>0 && "CABECERA".equals(elementos.peek()))
		{
			elementos.push(localName);
			sb = new StringBuilder();
		}
	}
	/**
	 * Procesa la cabecera, dejando en los atributos del objeto los valores que nos pueden interesar.
	 * @param xml Contenido del xml
	 * @throws IndicesFiscalesException
	 */
	public void procesaCabecera(byte[]xml) throws IndicesFiscalesException{
		ByteArrayInputStream bis=null;
		try
		{
			bis = new ByteArrayInputStream(xml);
			InputSource is = new InputSource(bis);
			xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			xr.parse(is);
		}catch (SAXException sax)
		{
			if (marca.equals(sax.getMessage()))
			{
				//Terminación normal, no hacemos nada
				return;
			}
			else
			{
				throw new IndicesFiscalesException("Error al extraer la cabecera del mensaje:" + sax.getMessage(), sax);
			}
		}
		catch (IOException io)
		{
			throw new IndicesFiscalesException("Error al extraer la cabecera del mensaje:"+ io.getMessage(), io);
		}
		finally 
		{
			if (bis!=null)
			{
				try{ bis.close();} catch(Exception e){}
			}
		}
	}
	
	
}
