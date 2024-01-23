package es.tributasenasturias.services.lanzador.client;
import java.util.ArrayList;
import java.util.List;


import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;



import stpa.services.LanzaPL;
import stpa.services.LanzaPLService;


/**
 * Clase de utilidad que oculta los detalles de servicio y puerto que ofrece
 * la librería generada a partir del servicio lanzador
 * @author noelianbb
 *
 */

public class TLanzador {

	//----------------------------Atributos
	private String endpointLanzador = null;
	private SOAPHandler<SOAPMessageContext> soapHandler;
	LanzaPLService lPlSrv;
	LanzaPL port;

	//----------------------------Getters y Setters
	/**
	 * Recupera el manejador de mensajes SOAP para la comunicación con el lanzador.
	 */
	public SOAPHandler<SOAPMessageContext> getSoapHandler() {
		return soapHandler;
	}
	
	/**
	 * Establece el manejador de mensajes SOAP para la comunicación con el lanzador.
	 * @param soapHandler {@link SOAPHandler} para el manejo de los mensajes.
	 */
	public void setSoapHandler(
			SOAPHandler<SOAPMessageContext> soapHandler) {
		this.soapHandler = soapHandler;
		changeHandler(soapHandler);
	}
	/**
	 * Recupera endpoint del servicio.
	 * @return {@link String}
	 */
	public String getEndPointLanzador() {
		return endpointLanzador;
	}
	/**
	 * Establece el endpoint del servicio.
	 * @param endpointLanzador
	 */
	public void setEndPointLanzador(String enpoint) {
		this.endpointLanzador = enpoint;
	}
	/**
	 * Recupera la clase de implementación de comunicación con el lanzador. Esta clase es interna y sólo se debería
	 * utilizar si se conoce lo que se hace.
	 * @return {@link LanzaPLService}
	 */
	public LanzaPLService getInnerService() {
		return lPlSrv;
	}
	/**
	 * Establece la clase de implementación de comunicación con el lanzador. En principio no hay necesidad de utilizarla
	 * directamente, se utiliza a través de un objeto {@link TLanzador}
	 * @param LanzaPLService Clase de comunicación con el lanzador.
	 */
	public void setInnerService(LanzaPLService lanzaPLService) {
		lPlSrv = lanzaPLService;
	}
	/**
	 * Recupera el port de comunicación con el lanzador. Este objeto permite ejecutar llamadas al lanzador.
	 * No debería usarse directamente.
	 * @return {@link LanzaPL}
	 */
	public LanzaPL getPort() {
		return port;
	}
	/**
	 * Establece el port de comunicación con el lanzador. Este objeto permite ejecutar las llamadas al lanzador.
	 * No debería haber necesidad de utilizarlo.
	 * @param port {@link LanzaPL} Instancia que permite ejecutar llamadas al lanzador.
	 */
	public void setPort(LanzaPL port) {
		this.port = port;
	}

	//-----------------------------Constructores

	//No se permite instanciar esta clase sin parámetros.
	@SuppressWarnings("unused")
	private TLanzador(){};

	/**
	 * Constructor que permite indicar el endpoint del lanzador.
	 * @param endpointLanzador: string con la ubicacion del WDSL
	 * @throws {@link LanzadorException} si no se puede construir el objeto.
	 */
	protected TLanzador(String endPointLanzador) throws LanzadorException{
		//Se modifica de forma dinámica la localización del WSDL, que debe estar en el
		//jar donde se define LanzaPLService, y debe estar situado en 
		//META-INF/wsdls/PXLanzador.
		//Esto no es peor que lo que genera el cliente por defecto, que también lo mete,
		//pero al menos esto funciona bien.
		//Desactivado, sólo tiene sentido si se va a utilizar en una librería jar.
		//WebServiceClient ann = stpa.services.LanzaPLService.class.getAnnotation(WebServiceClient.class);
		//URL urlWsdl = stpa.services.LanzaPLService.class.getResource("/META-INF/wsdls/PXLanzador.wsdl");
		//lPlSrv = new stpa.services.LanzaPLService (urlWsdl,new QName(ann.targetNamespace(), ann.name()));
		lPlSrv = new stpa.services.LanzaPLService();
		port = lPlSrv.getLanzaPLSoapPort();
		this.establecerEndPoint(endPointLanzador);
	}
	/**
	 * Constructor que permite indicar el endpoint del lanzador.
	 * También indica un manejador SOAP para el intercambio de mensajes con el servicio lanzador.
	 * @param endpointLanzador: string con la ubicacion del WDSL
	 * @throws {@link LanzadorException} si no se puede construir el objeto.
	 */
	protected TLanzador(String endPointLanzador, SOAPHandler<SOAPMessageContext> handler) throws LanzadorException{
		this(endPointLanzador);
		changeHandler(handler);
	}

	//-----------------------------Métodos---------------------

	/**
	 * Establece el endpoint
	 * @param endPoint cadena que contiene la localización del WSDL
	 */
	private void establecerEndPoint(String endPoint) throws LanzadorException{
		BindingProvider bpr=(BindingProvider) port;
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endPoint); // Cambiamos el endpoint
	}

	/**
	 * Modifica el manejador de mensajes SOAP que utilizará esta clase en las comunicaciones con 
	 * el lanzador. Este manejador permite realizar log de las peticiones, entre otras cosas.
	 * @param manejador {@link SOAPHandler} para manejar los mensajes.
	 */
	@SuppressWarnings("unchecked")
	private void changeHandler(SOAPHandler<SOAPMessageContext> manejador)
	{
		javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port; // enlazador de protocolo para el servicio.
		if (manejador!=null)
		{
			Binding bi = bpr.getBinding();
			List<Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			{
			   handlerList = new ArrayList<Handler>();
			}
			handlerList.add(manejador);
			bi.setHandlerChain(handlerList);
		}	
	}

	/**
	 * Permite invocar un procedimiento almacenado
	 * @param esquema
	 * @param peticion : string XML con la petición
	 * @param IP : IP desde la que se lanza la petición
	 * @param NIF Nif con el que se lanza.
	 * @param nombre Nombre con el que se lanza
	 * @param certificado Certificado con el que se lanza
	 * @return string con XML resultado de la petición
	 * @throws LanzadorException en caso de error.
	 */
	public  String execute(String esquema, String peticion,String IP, String NIF, String nombre, String certificado) throws LanzadorException
	{
		try 
		{
			//Realizamos la llamada al servicio web.
			String result = port.executePL(esquema, peticion, IP, NIF, nombre, certificado);
			//Retornamos el resultado
			return result;
		} 
		catch (Exception e) {
			throw new LanzadorException ("Error en la consulta:" + e.getMessage(),e);
		}
	}

	/**
	 * Permite invocar un procedimiento almacenado
	 * @param esquema
	 * @param peticion : objeto petición
	 * @param IP : IP desde la que se lanza la petición
	 * @param NIF Nif con el que se lanza.
	 * @param nombre Nombre con el que se lanza
	 * @param certificado Certificado con el que se lanza
	 * @return  string con XML resultado de la petición o mensaje explicativo en caso de error
	 * @throws LanzadorException en caso de error. 
	 */
	public String execute(String esquema, TPeticion peticion,String IP, String NIF, String nombre, String certificado) throws LanzadorException
	{
		if (peticion==null)
		{
			throw new LanzadorException ("La petición a base de datos está vacía.");
		}
		return execute(esquema, peticion.toXml(), IP, NIF, nombre, certificado);
	}
	
	/**
	 * Permite invocar un procedimiento almacenado utilizando un objeto ProcedimientoAlmacenado.
	 * @param proc {@link ProcedimientoAlmacenado} con los datos de procedimiento a llamar.
	 * @param IP : IP desde la que se lanza la petición
	 * @param NIF Nif con el que se lanza.
	 * @param nombre Nombre con el que se lanza
	 * @param certificado Certificado con el que se lanza
	 * @return  string con XML resultado de la petición o mensaje explicativo en caso de error
	 * @throws LanzadorException en caso de error. 
	 */
	public String ejecutar(ProcedimientoAlmacenado proc,String IP, String NIF, String nombre, String certificado) throws LanzadorException
	{
		if (proc==null)
		{
			throw new LanzadorException ("No se ha pasado un procedimiento almacenado válido.");
		}
		return execute(proc.getPeticion().getEsquema(), proc.getPeticion().toXml(), IP, NIF, nombre, certificado);
	}
	
	/**
	 * Permite invocar un procedimiento almacenado utilizando un objeto ProcedimientoAlmacenado.
	 * La mayoría de parámetros del lanzador se pasan vacíos, todos aquellos que no se contemplan
	 * en el objeto de procedimiento almacenado. Serán IP, NIF de lanzador, nombre y certificado.
	 * @param proc {@link ProcedimientoAlmacenado} con los datos de procedimiento a llamar.
	 * @return  string con XML resultado de la petición o mensaje explicativo en caso de error
	 * @throws LanzadorException en caso de error. 
	 */
	public String ejecutar(ProcedimientoAlmacenado proc) throws LanzadorException
	{
		if (proc==null)
		{
			throw new LanzadorException ("No se ha pasado un procedimiento almacenado válido.");
		}
		return execute(proc.getPeticion().getEsquema(), proc.getPeticion().toXml(), "", "", "", "");
	}


}
