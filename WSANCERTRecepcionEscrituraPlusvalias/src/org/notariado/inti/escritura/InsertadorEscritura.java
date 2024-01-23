package org.notariado.inti.escritura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.notariado.inti.COMUNICACION.REQUEST;
import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.soap.SoapClientHandler;
import org.notariado.inti.utils.Base64;
import org.notariado.inti.utils.Utils;

import es.tributasenasturias.services.fichasnotariales.FichasNotariales;
import es.tributasenasturias.services.fichasnotariales.FichasNotariales_Service;
import es.tributasenasturias.services.fichasnotariales.IdEscrituraType;
import es.tributasenasturias.webservice.EscriturasAncert;
import es.tributasenasturias.webservice.EscriturasAncertService;

/**
 * Gestiona la inserción de escritura en base de datos.
 * @author crubencvs
 *
 */
public class InsertadorEscritura {

	Preferencias pref;
	String idSesion;
	protected InsertadorEscritura(Preferencias pre, String idSesion) {
		pref = pre;
		this.idSesion = idSesion;
	}
	/**
	 * Establece el endpoint
	 * @param endPoint cadena que contiene la localización del WSDL
	 */
	private void establecerEndPoint(EscriturasAncert port,String endPoint){
		BindingProvider bpr=(BindingProvider) port;
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endPoint); 
	}
	/**
	 * Establece el manejador de llamada para las operaciones contra este servicio
	 * @param manejador Implementación de {@link SOAPHandler} que permitirá operar sobre cada llamada a servicio.
	 */
	@SuppressWarnings("unchecked")
	private void changeHandler(EscriturasAncert port,SOAPHandler<SOAPMessageContext> manejador)
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
	
	private enum TipoArchivo {PDF, ZIP};
	//Esto puede ocupar mucha memoria si decodificamos de base64, así que decodificamos los primeros ocho bytes
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
	private boolean insertaFichaNotarial(REQUEST peticion) 
	{
		boolean resultado;
		final String ORIGEN_PLUSVALIAS = "NP";
		
		String notario = peticion.getDATOSCOMUNICACION().getCODIGOULTIMASVOLUNTADES();
		String notaria = peticion.getDATOSCOMUNICACION().getCODIGONOTARIA();
		String protocolo= String.valueOf(peticion.getDATOSCOMUNICACION().getNUMERODOCUMENTO());
		String protocoloBis=String.valueOf(peticion.getDATOSCOMUNICACION().getNUMERODOCUMENTOBIS());
		if ("null".equals(protocoloBis))
		{
			protocoloBis = "0";
		}
		String fechaAutorizacion = Utils.calendarToString(peticion.getDATOSCOMUNICACION().getFECHAAUTORIZACION(),"dd-MM-yyyy");
		
		FichasNotariales_Service srv = new FichasNotariales_Service();
		FichasNotariales port = srv.getFichasNotarialesSOAP();

		String endpoint=pref.getEndpointFichas();
		if (endpoint !=null)
		{
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port; // enlazador de protocolo para el servicio.
			bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpoint); // Cambiamos el endpoint
			Binding bi = bpr.getBinding();
			List <Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			   handlerList = new ArrayList<Handler>();
			handlerList.add(new SoapClientHandler(idSesion));
			bi.setHandlerChain(handlerList);
		}
		
		IdEscrituraType idesc = new IdEscrituraType();
		idesc.setCodigoNotario(notario);
		idesc.setCodigoNotaria(notaria);
		idesc.setNumeroProtocolo(protocolo);
		idesc.setProtocoloBis(protocoloBis);
		idesc.setFechaAutorizacion(fechaAutorizacion);
		
		Holder<IdEscrituraType> datosEscritura= new Holder<IdEscrituraType>();
		datosEscritura.value= idesc;
		Holder<Boolean>esError = new Holder<Boolean>();
		Holder<String>codigo   = new Holder<String>();
		Holder<String>mensaje  = new Holder<String>();
		//En caso de fichas, podemos decodificar el base64, porque sabemos que 
		//será pequeño.
		byte[] contenidoZip = Base64.decode(peticion.getDATOSCOMUNICACION().getFICHERO().toCharArray());
		port.altaFicha(datosEscritura, 
				       ORIGEN_PLUSVALIAS, 
				       peticion.getDATOSCOMUNICACION().getCODIGOAYUNTAMIENTO(),  
				       "N", //No autorizado diligencias. Las de plusvalías no tienen este flag. 
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
	
	private boolean insertarPDF (REQUEST peticion) {
		String notario = peticion.getDATOSCOMUNICACION().getCODIGOULTIMASVOLUNTADES();
		String notaria = peticion.getDATOSCOMUNICACION().getCODIGONOTARIA();
		String protocolo= String.valueOf(peticion.getDATOSCOMUNICACION().getNUMERODOCUMENTO());
		String protocolo_bis=String.valueOf(peticion.getDATOSCOMUNICACION().getNUMERODOCUMENTOBIS());
		if ("null".equals(protocolo_bis))
		{
			protocolo_bis = "0";
		}
		String fechaAutorizacion = Utils.calendarToString(peticion.getDATOSCOMUNICACION().getFECHAAUTORIZACION(),"dd/MM/yyyy");
		EscriturasAncertService srv = new EscriturasAncertService();
		EscriturasAncert p = srv.getEscriturasAncertPort();
		establecerEndPoint(p, pref.getEndpointEscritura());
		changeHandler (p,new SoapClientHandler(idSesion));
		String resultado = p.altaEscrituraPlusvalia(notario,
							notaria, 
							protocolo, 
							protocolo_bis, 
							fechaAutorizacion,
							"[No disponible]", 
							peticion.getDATOSCOMUNICACION().getFICHERO(), 
							peticion.getDATOSCOMUNICACION().getCODIGOAYUNTAMIENTO()); //Origen de la solicitud
		return "OK".equalsIgnoreCase(resultado)? true:false;
	}
/**
	 * Realizar la inserción de una escritura, según los parámetros que se reciben en la petición del servicio.
	 * Si detecta que se trata de un archivo comprimido, intenta insertar la ficha notarial
	 * @param peticion
	 * @return true si ha conseguido insertar la escritura, false si no.
	 */
	public boolean insertarEscritura(REQUEST peticion)
	{
		boolean resultado;
		TipoArchivo tipoArchivo = tipoArchivo(peticion.getDATOSCOMUNICACION().getFICHERO());
		if (TipoArchivo.ZIP.equals(tipoArchivo)){
			resultado = insertaFichaNotarial(peticion);
		} 
		else  {  //Por defecto, como hasta el momento. PDF 
			resultado = insertarPDF(peticion);
		} 
		return resultado;
	}
}
