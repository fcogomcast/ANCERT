package es.tributasenasturias.services.ancert.recepcionescritura.validacion;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.types.ENVIOESCRITURAS;

public class ValidadorEsquema implements IContextReader{
	private CallContext context;
	public void validar(ENVIOESCRITURAS src) throws ValidacionException,SystemException
	{
		//Si el envío de escrituras es nulo, o no tiene REQUEST, se elimina
		if (src==null || src.getREQUEST()==null)
		{
			throw new ValidacionException ("El mensaje de entrada parece nulo.");
		}
		try {
			Preferencias pr = PreferenciasFactory.newInstance();
			LogHelper log=LogFactory.getLogAplicacionContexto(context);
			if (pr==null)
			{
				throw new SystemException ("No se pueden recuperar las preferencias.");
			}
			if ("S".equalsIgnoreCase(pr.getValidarEsquema()))
			{
				JAXBContext jb = JAXBContext.newInstance(src.getClass().getPackage().getName());
				SchemaFactory sf=SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = sf.newSchema(this.getClass().getClassLoader().getResource("xsd/envio_escrituras.xsd"));
				Validator validator = schema.newValidator();
				validator.setErrorHandler(new EsquemaErrorHandler(log));
				validator.validate(new JAXBSource(jb,src));
			}
		} catch (SAXException e) {
			throw new ValidacionException ("El mensaje de entrada no es válido. :" + e.getMessage(),e); //No debería lanzarse nunca
		} catch (IOException e) {
			throw new SystemException ("Error de E/S al validar el esquema de entrada:" + e.getMessage(),e);
		} catch (JAXBException e) {
			throw new SystemException ("Error de conversión al validar el esquema de entrada:" + e.getMessage(),e);
		} catch (PreferenciasException e) {
			throw new SystemException ("Error de preferencias al validar el esquema de entrada:" + e.getMessage(),e);
		}
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
}
