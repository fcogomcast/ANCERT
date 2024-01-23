package es.stpa.plusvalias.documentos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;


import es.stpa.plusvalias.Constantes;
import es.stpa.plusvalias.exceptions.PlusvaliaException;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.soap.GestionFirmaWS;
import es.stpa.plusvalias.soap.SoapClientHandler;
import es.stpa.recepcionescritura.CABECERAType;
import es.stpa.recepcionescritura.COMUNICACION;
import es.stpa.recepcionescritura.SOAPGateway;
import es.stpa.recepcionescritura.SOAPGatewayService;

/**
 * Interfaz con el servicio de escrituras de plusvalías.
 * @author crubencvs
 *
 */
public class MediadorEscrituras {
	private Preferencias pref;
	private String idSesion;
	
	public MediadorEscrituras(Preferencias pref, String idSesion) {
		super();
		this.pref = pref;
		this.idSesion = idSesion;
	}

	/**
	 * Permite la recepción de una escritura en nuestros sistemas
	 * @param ayuntamiento
	 * @param codigoNotario
	 * @param codigoNotaria
	 * @param protocolo
	 * @param protocoloBis
	 * @param fechaAutorizacion
	 * @param nombreNotario
	 * @param notaria
	 * @param nombreFichero
	 * @param escritura
	 * @throws PlusvaliaException
	 */
	@SuppressWarnings("unchecked")
	public void recibirEscritura(String receptor,
								 String ayuntamiento,
								 String codigoNotario,
								 String codigoNotaria,
								 Long protocolo,
								 Long protocoloBis,
								 String fechaAutorizacion,
								 String nombreNotario,
								 String notaria,
								 String nombreFichero,
								 byte[] escritura
								 ) throws PlusvaliaException{
		if (escritura==null  || escritura.length==0) {
			return; //Lo damos por bueno. No hay escritura, no se inserta.
		}
		SOAPGatewayService srv= new SOAPGatewayService();
		SOAPGateway port= srv.getSOAPGateway();
		BindingProvider bpr=(BindingProvider) port;
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointRecepcionEscritura());
		  
		Binding bi = bpr.getBinding();
		List<Handler> handlerList = bi.getHandlerChain();
		if (handlerList == null)
		{
			handlerList = new ArrayList<Handler>();
		}
		handlerList.add(new SoapClientHandler(idSesion));
		//Y este para firmar la salida...
		//handlerList.add(new GestionFirmaWS_Salida(pref, idSesion));
		//Paso en contexto preferencias y sesión, para reutilizar 
		//la implementación.
		bpr.getRequestContext().put(Constantes.PREFERENCIAS, pref);
		bpr.getRequestContext().put(Constantes.IDSESION, idSesion);
		handlerList.add(new GestionFirmaWS());
		bi.setHandlerChain(handlerList);
		
		COMUNICACION comunicacion= new COMUNICACION();
		COMUNICACION.REQUEST request= new COMUNICACION.REQUEST();
		
		CABECERAType cabecera = new CABECERAType();
		cabecera.setEMISOR("STPA"); //Pongo a servicios Tributarios como emisor, para diferenciar
		cabecera.setRECEPTOR(receptor);
		try {
			cabecera.setTIMESTAMP(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		} catch (DatatypeConfigurationException e) {
			throw new PlusvaliaException("Error de construcción de mensaje al insertar la escritura: "+e,e);
		}
		
		request.setCABECERA(cabecera);
		
		COMUNICACION.REQUEST.DATOSCOMUNICACION datos= new COMUNICACION.REQUEST.DATOSCOMUNICACION();
		datos.setCODIGOAYUNTAMIENTO(ayuntamiento);
		datos.setTIPOCOMUNICACION("COPIA"); //Probablemente no lo estemos usando.
		datos.setCODIGOULTIMASVOLUNTADES(codigoNotario);
		datos.setCODIGONOTARIA(codigoNotaria);
		datos.setTIPODOCUMENTO("1"); //No sé a qué se corresponde, pero es lo que envían.
		if (protocolo!=null){
			datos.setNUMERODOCUMENTO(protocolo.longValue());
		}
		if (protocoloBis!=null){
			datos.setNUMERODOCUMENTOBIS(protocoloBis);
		}
		
		try {
			XMLGregorianCalendar fAutorizacion;
			
			SimpleDateFormat sd= new SimpleDateFormat("dd/MM/yyyy");
			Date now=sd.parse(fechaAutorizacion);
			
			GregorianCalendar gc= (GregorianCalendar)GregorianCalendar.getInstance();
			gc.setTime(now);
			fAutorizacion=DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			datos.setFECHAAUTORIZACION(fAutorizacion);
		} catch (ParseException e) {
			throw new PlusvaliaException("Error de construcción de mensaje al insertar la escritura: "+e,e);
		} catch (DatatypeConfigurationException e) {
			throw new PlusvaliaException("Error de construcción de mensaje al insertar la escritura: "+e,e);
		}
		datos.setNOMBREFICHERO(nombreFichero);
		datos.setFICHERO(escritura);
		
		request.setDATOSCOMUNICACION(datos);
		
		comunicacion.setREQUEST(request);
		
		COMUNICACION respComm= port.process(comunicacion);
		if (respComm==null || respComm.getRESPONSE()==null){
			throw new PlusvaliaException("Error al insertar escritura, no se ha recibido respuesta");
		}
		
		if (respComm.getRESPONSE().getCODERROR()!=0){
			throw new PlusvaliaException ("Error al insertar escritura: ("+respComm.getRESPONSE().getCODERROR()+
										  ")-"+respComm.getRESPONSE().getERROR());
		}
		return;
		
	}
	
}
