package es.tributasenasturias.services.ancert.recepcionescritura.objetos;


import org.w3c.dom.Document;


import es.tributasenasturias.services.ancert.recepcionescritura.RecepcionEscrituraResponse;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.XMLDOMDocumentException;
import es.tributasenasturias.services.ancert.recepcionescritura.factory.DomainObjectsFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.types.ENVIOESCRITURAS;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.FirmaFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.FirmaHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.XMLDOMUtils;

/**
 * Construye la respuesta del servicio web.
 * @author crubencvs
 *
 */
public class ServiceResponseBuilder implements IContextReader{
	@SuppressWarnings("unused")
	//Originalmente se devolv�a la respuesta en este campo. Por el momento
	//se ha eliminado, para devolver una cadena siempre.
	private RecepcionEscrituraResponse envResp=null;
	private RespuestaBuilder respBuilder=null;
	private CallContext context;
	/**
	 * Constructor del objeto
	 */
	public ServiceResponseBuilder(CallContext context)
	{
		this.context=context;
		envResp = new RecepcionEscrituraResponse();
		respBuilder = new DomainObjectsFactory().newRespuestaBuilder(context);
	}
	/**
	 * Pone un resultado definido por el programador en la respuesta de servicio.
	 * Este procedimiento no se utiliza de forma interna, para reducir el n�mero de 
	 * llamadas internas.
	 * @param codResultado C�digo de resultado
	 * @param textoResultado Texto del resultado
	 * @param isError true=es un error, false=no es error
	 */
	public final void setResultado(String codResultado, String textoResultado, boolean isError)
	{
		respBuilder.setResultado(codResultado, textoResultado, isError);
	}
	/**
	 * Cambia el estado de respuesta a "Error general".
	 */
	public final void setErrorGeneral ()
	{
		respBuilder.setResultado("9999", "Error general en la recepci�n.", true);
	}
	/**
	 * Cambia el estado de respuesta a "xml no v�lido". Esto no significa que est� mal formado, sino 
	 * que no es v�lido seg�n el esquema.
	 */
	public final void setErrorSeguridad()
	{
		respBuilder.setResultado("9998", "Error en la seguridad del mensaje recibido.", true);
	}
	/**
	 * Cambia el estado de respuesta a "xml no v�lido". Esto no significa que est� mal formado, sino 
	 * que no es v�lido seg�n el esquema.
	 */
	public final void setXMLInvalido()
	{
		respBuilder.setResultado("0002", "El xml de entrada no es v�lido seg�n el esquema establecido", true);
	}
	/**
	 * Cambia el estado de respuesta a "OK".
	 */
	public final void setResultadoOK()
	{
		respBuilder.setResultado("0000", "Env�o realizado correctamente", false);
	}
	/**
	 * Cambia el estado de respuesta a "Duplicado". Esto significa que ya se ha insertado anteriormente.
	 */
	public final void setDuplicado()
	{
		respBuilder.setResultado("0001", "Env�o duplicado, ya se ha realizado anteriormente.", false);
	}
	/**
	 * Hay datos necesarios en el mensaje que no se han informado.
	 */
	public final void setFaltanDatos()
	{
		respBuilder.setResultado("0003", "Faltan datos necesarios en el mensaje.", true);
	}
	/**
	 * La escritura est� vac�a.
	 */
	public final void setEscrituraVacia()
	{
		respBuilder.setResultado("0004", "La escritura enviada est� vac�a.", true);
	}
	/**
	 * Ha habido un error en el alta de la escritura.
	 */
	public final void setErrorAlta()
	{
		respBuilder.setResultado("0005", "No se ha podido dar de alta la escritura. Int�ntelo de nuevo en unos minutos.", true);
	}
	/**
	 * Se ha recibido una denegaci�n de escritura,  pero no se encuentra la solicitud previa.
	 */
	public final void setErrorNoSolicitudDenegacion()
	{
		respBuilder.setResultado("0006", "Se ha recibido una denegaci�n de solicitud de escritura pero no consta la solicitud relacionada", true);
	}
	private String getSalidaFirmada() throws SystemException, XMLDOMDocumentException
	{
		ConversorMensajesEntradaSalida conv=new DomainObjectsFactory().newConversorMensajes();
		//Firmamos la respuesta.
		FirmaHelper fir = FirmaFactory.newFirmaHelper(context);
		Document mensajeFirmado= fir.firmaMensaje(conv.generaMensajeSalida((ENVIOESCRITURAS)respBuilder.getRespuesta()).getOwnerDocument());
		return XMLDOMUtils.getXMLText(mensajeFirmado);
	}
	/**
	 * Construye la cabecera del mensaje de respuesta. 
	 * @throws SystemException si se produce alg�n problema. 
	 */
	public final void construirCabecera() throws SystemException
	{
		try
		{
		respBuilder.setCabecera();
		}catch (SystemException e)
		{
			setErrorGeneral();
			throw new SystemException("No se puede construir la cabecera:"+ e.getMessage(),e);
		}
	}
	/**
	 * Recupera la respuesta del servicio, tal como se construy� con el c�digo de esta clase.
	 * Se devuelve como texto, para corregir un error con el enlace JAXB, ya que el objeto de 
	 * retorno tiene un atributo, que es el ID, y al introducirlo en un "Any" estropea
	 * el namespace del elemento "REPLY". Se utilizar� una cadena para responder a la 
	 * petici�n de servicio, firm�ndola y haciendo que sean los manejadores SOAP los que
	 * lo conviertan al mensaje de respuesta.
	 * @return Cadena con la respuesta de servicio.
	 */
	public String getRespuesta() throws XMLDOMDocumentException,SystemException
	{
		return getSalidaFirmada();
	}
	/*public RecepcionEscrituraResponse getRespuesta() throws SystemException
	{
		ConversorMensajesEntradaSalida conv=new DomainObjectsFactory().newConversorMensajes();
		envResp.setAny(conv.generaMensajeSalida((ENVIOESCRITURAS)respBuilder.getRespuesta()));
		return envResp;
	}
	*/
	/**
	 * Establece el n�mero de presentaci�n que se devolver� en el mensaje de salida.
	 * @param numeroPresentacion N�mero de presentaci�n (no puede ser nulo o estar vac�o).
	 */
	public void setNumeroPresentacion(String numeroPresentacion)
	{
		if (numeroPresentacion!=null && !numeroPresentacion.equals(""))
		{
			respBuilder.setNumPresentacion(numeroPresentacion);
		}
		else
		{
			respBuilder.setNumPresentacion("1"); //Un valor cualquiera.
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
