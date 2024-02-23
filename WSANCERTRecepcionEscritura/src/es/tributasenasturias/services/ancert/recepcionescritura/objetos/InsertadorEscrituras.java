package es.tributasenasturias.services.ancert.recepcionescritura.objetos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

import com.sun.xml.ws.client.BindingProviderProperties;

import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.Base64;
import es.tributasenasturias.services.fichasnotariales.FichasNotariales;
import es.tributasenasturias.services.fichasnotariales.FichasNotariales_Service;
import es.tributasenasturias.services.fichasnotariales.IdEscrituraType;
import es.tributasenasturias.webservice.EscriturasAncert;
import es.tributasenasturias.webservice.EscriturasAncertService;

/**
 * Implementa la inserci�n de escrituras.
 * @author crubencvs
 *
 */
public class InsertadorEscrituras implements IContextReader{
	private CallContext context;
	//Timeout de la operaci�n de inserci�n de escritura.
	//Por encima de 90 segundos, lo consideramos demasiado,
	//y ya 90 es mucho, pero puede haber escrituras muy grandes, y pueden llegar en momentos  
	//en que el servidor est� muy ocupado.
	//private final static String TIMEOUT="90000";
	private static final int TIMEOUT= 60000;
	private enum TipoArchivo {PDF, ZIP};
	//Esto puede ocupar mucha memoria si decodificamos de base64, as� que decodificamos los primeros ocho bytes
	private TipoArchivo tipoArchivo(String contenidoArchivo){
		TipoArchivo detectado;
		byte[] pdf = new byte[] {(byte)0x25, (byte)0x50, (byte)0x44, (byte)0x46};
		byte[] zip = new byte[] {(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04};
		//Leemos los bytes necesarios para identificar PDF o RAR
		// https://asecuritysite.com/forensics/magic
		//Bytes PDF = 25 50 44 46(%PDF)  
		//Bytes ZIP = 50 4B 03 04 (PK)
		if (contenidoArchivo.length()>=8){
			try {
				String fragm = contenidoArchivo.substring(0,8);
				byte[] decoded8 = Base64.decode(fragm.toCharArray());
				byte[] decoded4 = new byte[4];
				System.arraycopy(decoded8, 0, decoded4, 0, 4);
				if (decoded4.length>=4){
					if (Arrays.equals(decoded4, pdf)){
						detectado= TipoArchivo.PDF;
					} else if (Arrays.equals(decoded4, zip)){
						detectado= TipoArchivo.ZIP;
					} else {
						detectado= TipoArchivo.PDF;
					}
				} 
				else {
					detectado= TipoArchivo.PDF;
				}
			} catch (Throwable t){ //Base64 lanza Error, no Exception.  Por eso lo recojo desde la clase base
				//Nos comportamos como hasta el momento
				detectado= TipoArchivo.PDF;
			}
		}
		else {
			//Como hasta el momento, consideramos PDF y que grabe lo que sea
			detectado= TipoArchivo.PDF;
		}
		return detectado;
	}
	
	@SuppressWarnings("unchecked")
	private boolean insertaEscrituraPDF(EscrituraDO idescritura, String escritura, String firmaEscritura, Preferencias pr) throws SystemException
	{
		boolean resultado;
		final String GESTIONAR_SOLICITUD = "S"; //Indica si se gestionar� la solicitud al insertar la escritura.
		EscriturasAncertService srv = new EscriturasAncertService();
		EscriturasAncert port = srv.getEscriturasAncertPort();
		//Indicamos d�nde va a incluirse.
		String endpoint=pr.getEndpointEscritura();
		if (endpoint !=null) //Si es nulo, ir� a donde diga el servicio en el wsdl.
		{
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port; // enlazador de protocolo para el servicio.
			bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpoint); // Cambiamos el endpoint
			//Se establece un tiempo de Timeout. 
			bpr.getRequestContext().put (BindingProviderProperties.REQUEST_TIMEOUT,TIMEOUT);
			Binding bi = bpr.getBinding();
			List <Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			   handlerList = new ArrayList<Handler>();
			handlerList.add(new SoapClientHandler((String)context.get(CallContextConstants.IDSESION),pr));
			bi.setHandlerChain(handlerList);
		}
		String insRespuesta=port.setEscrituraSolicitudes(idescritura.getCodNotario(), 
				idescritura.getCodNotaria(), idescritura.getNumProtocolo(),
				idescritura.getProtocoloBis(), idescritura.getFechaEscritura(),
				firmaEscritura, escritura,
				idescritura.isAutorizacionEnvioDiligencias()?"S":"N",
				GESTIONAR_SOLICITUD);
		if ("OK".equalsIgnoreCase(insRespuesta))
		{
			resultado=true;
		}
		else
		{
			resultado=false;
		}	
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	private boolean insertaFichaNotarial(EscrituraDO idescritura, String escritura, String firmaEscritura, Preferencias pr) throws SystemException
	{
		boolean resultado;
		final String ORIGEN_NOTARIAL = "N";
		FichasNotariales_Service srv = new FichasNotariales_Service();
		FichasNotariales port = srv.getFichasNotarialesSOAP();

		String endpoint=pr.getEndpointFichas();
		if (endpoint !=null)
		{
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port; // enlazador de protocolo para el servicio.
			bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpoint); // Cambiamos el endpoint
			//Se establece un tiempo de Timeout. 
			bpr.getRequestContext().put (BindingProviderProperties.REQUEST_TIMEOUT,TIMEOUT);
			Binding bi = bpr.getBinding();
			List <Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			   handlerList = new ArrayList<Handler>();
			handlerList.add(new SoapClientHandler((String)context.get(CallContextConstants.IDSESION), pr));
			bi.setHandlerChain(handlerList);
		}
		
		IdEscrituraType idesc = new IdEscrituraType();
		idesc.setCodigoNotario(idescritura.getCodNotario());
		idesc.setCodigoNotaria(idescritura.getCodNotaria());
		idesc.setNumeroProtocolo(idescritura.getNumProtocolo());
		idesc.setProtocoloBis(idescritura.getProtocoloBis());
		idesc.setFechaAutorizacion(idescritura.getFechaEscritura());
		
		Holder<IdEscrituraType> datosEscritura= new Holder<IdEscrituraType>();
		datosEscritura.value= idesc;
		Holder<Boolean>esError = new Holder<Boolean>();
		Holder<String>codigo   = new Holder<String>();
		Holder<String>mensaje  = new Holder<String>();
		//En caso de fichas, podemos decodificar el base64, porque sabemos que 
		//ser� peque�o.
		byte[] contenidoZip = Base64.decode(escritura.toCharArray());
		port.altaFicha(datosEscritura, 
				                           ORIGEN_NOTARIAL, 
				                           "", //C�digo de ayuntamiento vinculado, ninguno 
				                           idescritura.isAutorizacionEnvioDiligencias()?"S":"N", 
				                           contenidoZip, 
				                           esError, 
				                           codigo, 
				                           mensaje);
		if (!esError.value)
		{
			resultado=true;
		}
		else
		{
			resultado=false;
		}	
		return resultado;
	}
	
	public boolean insertaEscritura(EscrituraDO idescritura, String escritura, String firmaEscritura) throws SystemException
	{
		boolean resultado;
		Preferencias pr= PreferenciasFactory.getPreferenciasContexto(context);
		if (pr==null)
		{
			throw new SystemException("No se puede insertar la escritura por un problema al cargar las preferencias.");
		}
		TipoArchivo tipoArchivo = tipoArchivo(escritura);
		if (TipoArchivo.ZIP.equals(tipoArchivo)){
			resultado = insertaFichaNotarial(idescritura, escritura, firmaEscritura, pr);
		} 
		else  {  //Por defecto, como hasta el momento. PDF 
			resultado = insertaEscrituraPDF(idescritura, escritura, firmaEscritura, pr);
		} 
		return resultado;
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
