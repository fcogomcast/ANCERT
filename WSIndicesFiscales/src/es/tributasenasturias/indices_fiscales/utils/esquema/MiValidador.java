package es.tributasenasturias.indices_fiscales.utils.esquema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Clase de validación de esquema 
 * @author crubencvs
 *
 */
public class MiValidador{

	private EsquemaException causa;
	/**
	 * Comprueba si el xml indicado es válido respecto al esquema
	 * @param xml Contenido del XML
	 * @param nombreEsquema Nombre (con ruta relativa a la raíz del classpath) del esquema a utilizar 
	 * @return true si es válido
	 * @throws EsquemaException false si no
	 */
	public boolean isValid (byte[] xml, String nombreEsquema) 
	{
		ByteArrayInputStream bis=null;
		boolean esvalido=false;
		try
		{
			bis= new ByteArrayInputStream(xml);
			StreamSource s = new StreamSource(bis);
			URL schemaURL = Thread.currentThread().getContextClassLoader().getResource(nombreEsquema);
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(schemaURL);
			Validator validator= schema.newValidator();
			validator.validate(s);
			esvalido=true;
		}catch (SAXException se)
		{
			causa=new EsquemaException("Formato de XML inválido:"+ se.getMessage(),se);
		} catch (IOException io) {
			causa=new EsquemaException("No se puede validar el formato del XML.Error en la lectura del XML:"+ io.getMessage(),io);
		}
		finally {
			if (bis!=null) {
				try { bis.close();
				
				} catch (Exception e)
				{}
			}
		}
		return esvalido;
	}
	/**
	 * En caso de que no se supere la validación, contendrá información acerca del motivo de error, modelado como un objeto {@link EsquemaException}
	 * @return Causa de que el xml no valide
	 */
	public EsquemaException getCausaErrorValidacion() {
		return causa;
	}
}
