package es.tributasenasturias.services.ancert.recepcionescritura.objetos;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.types.ENVIOESCRITURAS;

public class ConversorMensajesEntradaSalida {

	/**
	 * Recupera el mensaje 
	 * @param entrada xml de entrada al servicio.
	 * @return ENVIOESCRITURAS
	 * @throws SystemException
	 */
	public ENVIOESCRITURAS recuperaMensajeEntrada(String entrada) throws SystemException
	{
		ENVIOESCRITURAS env=null;
		if (entrada==null||"".equals(entrada))
		{
			throw new SystemException ("No se ha recibido mensaje a la entrada o es un mensaje vacío");
		}
		try {
			JAXBContext jb = JAXBContext.newInstance(ENVIOESCRITURAS.class.getPackage().getName());
			Unmarshaller unmar=jb.createUnmarshaller();
			env=(ENVIOESCRITURAS)unmar.unmarshal(new StreamSource(new StringReader(entrada)));
		} catch (JAXBException e) {
			throw new SystemException (this.getClass().getCanonicalName()+".Error (JAXB) al tratar el mensaje de entrada:" + e.getMessage(),e);
		} 
		return env;
	}
	public Element generaMensajeSalida(ENVIOESCRITURAS salida) throws SystemException
	{
		Document doc=null;
		try {
			DocumentBuilderFactory docf =DocumentBuilderFactory.newInstance();
			docf.setNamespaceAware(true);
			doc=docf.newDocumentBuilder().newDocument();
			JAXBContext jb = JAXBContext.newInstance(ENVIOESCRITURAS.class.getPackage().getName());
			Marshaller mar = jb.createMarshaller();
			mar.marshal(salida, doc);
		} catch (JAXBException e) {
			throw new SystemException (this.getClass().getCanonicalName()+".Error (JAXB) al tratar el mensaje de salida:" + e.getMessage(),e);
		} catch (ParserConfigurationException e) {
			throw new SystemException ("Error (DOM) al tratar el mensaje de salida::" + e.getMessage(),e);
		}
		return doc.getDocumentElement();
	}
}
