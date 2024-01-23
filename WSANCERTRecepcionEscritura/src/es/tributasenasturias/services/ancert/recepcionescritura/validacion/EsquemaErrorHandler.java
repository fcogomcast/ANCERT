package es.tributasenasturias.services.ancert.recepcionescritura.validacion;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;

public class EsquemaErrorHandler implements ErrorHandler {

	LogHelper log;
	/**
	 * Constructor para incluir un objeto de log directamente.
	 * @param l
	 */
	public EsquemaErrorHandler(LogHelper l)
	{
		log=l;
	}
	@Override
	public final void error(SAXParseException exception) throws SAXException {
		if (exception.getMessage()!=null)
		{
			log.error(exception.getMessage());
		}
		throw new SAXException(exception); 
	}

	@Override
	public final void fatalError(SAXParseException exception) throws SAXException {
		if (exception.getMessage()!=null)
		{
			log.error(exception.getMessage());
		}
		throw exception;
	}

	@Override
	public final void warning(SAXParseException exception) throws SAXException {
		log.error("Aviso de validación de esquema:" + exception.getMessage());

	}

}
