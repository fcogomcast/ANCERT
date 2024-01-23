package es.tributasenasturias.services.ancert.solicitudEscritura.informes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.DocumentoException;
import es.tributasenasturias.services.ancert.solicitudEscritura.bd.ConversorParametrosLanzador;
import es.tributasenasturias.services.ancert.solicitudEscritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.Base64;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.servicios.documentos.WSDocumentos;
import es.tributasenasturias.servicios.documentos.WSDocumentos_Service;

/**
 * Acceso a informes del gestor documental
 * @author crubencvs
 *
 */
public class GestorDocumental {

	private Preferencias pref;
	private String idSesion;
	private WSDocumentos_Service srv;
	private WSDocumentos port;
	
	public GestorDocumental(String idSesion,Preferencias p) throws DocumentoException{
		this.idSesion= idSesion; 
		pref= p;
		if (this.pref==null){
			throw new DocumentoException("[GestorDocumental]: se ha recibido un objeto Preferencias nulo");
		}
		srv = new WSDocumentos_Service();
		port = srv.getWSDocumentosPort();
		establecePort();
	}
	
	/**
	 * Establece las propiedades del port de conexión con el servicio
	 * @throws DocumentoException
	 */
	private void establecePort() throws DocumentoException{
		String endpointServicio;
		endpointServicio = pref.getEndpointDocumentos();
		
		if (!"".equals(endpointServicio)){
			
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port; 
			bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpointServicio); 
			Binding bi = bpr.getBinding();
			List <Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			   handlerList = new ArrayList<Handler>();
			handlerList.add(new SoapClientHandler(this.idSesion, pref));
			bi.setHandlerChain(handlerList);
		}
	}
	/**
	 * Genera el informe de solicitud de escritura según el gestor documental.
	 * @param numeroExpediente
	 * @param codigoNotario
	 * @param codigoNotaria
	 * @param numeroProtocolo
	 * @param anioAutorizacion
	 * @param nifSolicitante
	 * @param nombreSolicitante
	 * @param cargoSolicitante
	 * @param nifOtorgante
	 * @param nombreOtorgante
	 * @param tipoProcedimiento
	 * @param destinatario
	 * @param finalidad
	 * @param idActuacion
	 * @return Contenido binario informe generado
	 * @throws DocumentoException
	 */
	public byte[] getInformeSolicitudEscritura (String numeroExpediente,
			                                    String codigoNotario,
			                                    String codigoNotaria,
			                                    String numeroProtocolo,
			                                    String protocoloBis,
			                                    String anioAutorizacion,
			                                    String nifSolicitante,
			                                    String nombreSolicitante,
			                                    String cargoSolicitante,
			                                    String nifOtorgante,
			                                    String nombreOtorgante,
			                                    String tipoProcedimiento,
			                                    String destinatario,
			                                    String finalidad,
			                                    String idActuacion) throws DocumentoException
	{
		String pdf;
		String procGenerarInforme;
		procGenerarInforme = pref.getProcInformeSolicitud();
		if ("".equals(procGenerarInforme)){
			throw new DocumentoException("[GestorDocumental].getInformeSolicitudEscritura: Error de configuración, falta el nombre de procedimiento de impresión");
		}
		try {
			ConversorParametrosLanzador conv = new ConversorParametrosLanzador();
			conv.setProcedimientoAlmacenado(procGenerarInforme);
			conv.setParametro("1", ConversorParametrosLanzador.TIPOS.Integer);
			conv.setParametro("1", ConversorParametrosLanzador.TIPOS.Integer);
			conv.setParametro("USU_WEB_SAC", ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro("33", ConversorParametrosLanzador.TIPOS.Integer);
			conv.setParametro(numeroExpediente, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(codigoNotario, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(codigoNotaria, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(numeroProtocolo, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(protocoloBis, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(anioAutorizacion, ConversorParametrosLanzador.TIPOS.Integer);
			conv.setParametro(nifSolicitante, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(nombreSolicitante, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(cargoSolicitante, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(nifOtorgante, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(nombreOtorgante, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(tipoProcedimiento, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(destinatario, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(finalidad, ConversorParametrosLanzador.TIPOS.String);
			conv.setParametro(idActuacion, ConversorParametrosLanzador.TIPOS.Integer);
		
			String invocacion = conv.Codifica();
			
			pdf = port.impresionGD(invocacion, "");
			if ("".equals(pdf)){
				throw new DocumentoException ("[GestorDocumental].getInformeSolicitudEscritura: no se ha podido generar el informe de solicitud de escritura");
			}
			return Base64.decode(pdf.toCharArray());
		} catch (Exception e){
			if (!(e instanceof DocumentoException)) {
				throw new DocumentoException("[GestorDocumental].getInformeSolicitudEscritura: "+ e.getMessage(), e);
			} 
			else {
				throw (DocumentoException)e;
			}
		}
		
		
	}
}
