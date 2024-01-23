package es.stpa.plusvalias.documentos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.exceptions.DatosException;
import es.stpa.plusvalias.exceptions.PlusvaliaException;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.soap.SoapClientHandler;
import es.tributasenasturias.services.lanzador.client.LanzadorException;
import es.tributasenasturias.services.lanzador.client.ParamType;
import es.tributasenasturias.services.lanzador.client.ProcedimientoAlmacenado;
import es.tributasenasturias.servicios.documentos.WSDocumentos;
import es.tributasenasturias.servicios.documentos.WSDocumentos_Service;

/**
 * Interfaz con el servicio de impresión de documentos
 * @author crubencvs
 *
 */
public class MediadorWSDocumentos {
  private Preferencias pref;
  private String idSesion;
  
  public MediadorWSDocumentos(Preferencias pref, String idSesion){
	  this.pref= pref;
	  this.idSesion =idSesion;
  }
  
  /**
   * Impresión del resumen de consulta
   * @param referenciaCatastral
   * @param codigoMunicipio
   * @param codigoNotario
   * @param notario
   * @param nifSujeto
   * @param nombreSujeto
   * @param importe
   * @param porcentajeTransmitido. No se utiliza, se puede pasar una cadena vacía
   * @return
   * @throws PlusvaliaException
   */
  @SuppressWarnings("unchecked")
public byte[] imprimirResumenConsulta(
		  								 String referenciaCatastral,
		  								 String codigoMunicipio,
		  								 String codigoNotario,
		  								 String notario,
		  								 String protocolo,
		  								 String fechaAutorizacion,
		  								 String nifSujeto,
		  								 String nombreSujeto,
		  								 String importe,
		  								 String porcentajeTransmitido) throws PlusvaliaException{
	  WSDocumentos_Service src = new WSDocumentos_Service();
	  WSDocumentos port= src.getWSDocumentosPort();
	  byte[] pdf;
	  ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcImpresoConsulta(), pref.getEsquemaBD());
	  proc.param("1", ParamType.NUMERO);
	  proc.param("1", ParamType.NUMERO);
	  proc.param("USU_WEB_SAC", ParamType.CADENA);
	  proc.param("33", ParamType.NUMERO);
	  proc.param(referenciaCatastral, ParamType.CADENA);
	  proc.param(codigoMunicipio, ParamType.NUMERO);
	  proc.param(codigoNotario, ParamType.CADENA);
	  proc.param(notario, ParamType.CADENA);
	  proc.param(protocolo, ParamType.CADENA);
	  proc.param(fechaAutorizacion, ParamType.FECHA,"DD/MM/YYYY");
	  proc.param(nifSujeto, ParamType.CADENA);
	  proc.param(nombreSujeto, ParamType.CADENA);
	  proc.param(importe, ParamType.NUMERO);
	  proc.param(porcentajeTransmitido, ParamType.NUMERO);
	  proc.param("P", ParamType.CADENA);
	  try {
		  BindingProvider bpr=(BindingProvider) port;
		  bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointDocumentos());
		  
		  Binding bi = bpr.getBinding();
		  List<Handler> handlerList = bi.getHandlerChain();
		  if (handlerList == null)
		  {
			 handlerList = new ArrayList<Handler>();
		  }
		  handlerList.add(new SoapClientHandler(idSesion));
		  bi.setHandlerChain(handlerList);
			
		  String pdfBase64=port.impresionGD(proc.getPeticion().toXml(), "", false);
		  if (!"".equals(pdfBase64)){
			  pdf=Base64.decode(pdfBase64.toCharArray());
		  } else {
			  throw new PlusvaliaException("No se ha podido generar el informe resultado de la consulta para el sujeto " + nifSujeto);
		  }
		  return pdf;
	  } catch (LanzadorException e){
		  throw new PlusvaliaException("Error al imprimir el pdf de la consulta para el sujeto "+nifSujeto,e);
	  }
	  
  }
  
  /**
   * Impresión del resumen de presentacion
   * @param referenciaCatastral
   * @param codigoMunicipio
   * @param codigoNotario
   * @param notario
   * @param nifSujeto
   * @param nombreSujeto
   * @param importe
   * @param porcentajeTransmitido. No se utiliza, se puede pasar una cadena vacía
   * @return
   * @throws PlusvaliaException
   */
  @SuppressWarnings("unchecked")
public byte[] imprimirReciboPresentacion(
		  								 String referenciaCatastral,
		  								 String codigoMunicipio,
		  								 String codigoNotario,
		  								 String notario,
		  								 String protocolo,
		  								 String fechaAutorizacion,
		  								 String nifSujeto,
		  								 String nombreSujeto,
		  								 String importe,
		  								 String numeroAutoliquidacion
	) throws PlusvaliaException{
	  WSDocumentos_Service src = new WSDocumentos_Service();
	  WSDocumentos port= src.getWSDocumentosPort();
	  byte[] pdf;
	  ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcImpresoPresentacion(), pref.getEsquemaBD());
	  proc.param("1", ParamType.NUMERO);
	  proc.param("1", ParamType.NUMERO);
	  proc.param("USU_WEB_SAC", ParamType.CADENA);
	  proc.param("33", ParamType.NUMERO);
	  proc.param(referenciaCatastral, ParamType.CADENA);
	  proc.param(codigoMunicipio, ParamType.NUMERO);
	  proc.param(codigoNotario, ParamType.CADENA);
	  proc.param(notario, ParamType.CADENA);
	  proc.param(protocolo, ParamType.CADENA);
	  proc.param(fechaAutorizacion, ParamType.FECHA,"DD/MM/YYYY");
	  proc.param(nifSujeto, ParamType.CADENA);
	  proc.param(nombreSujeto, ParamType.CADENA);
	  proc.param(importe, ParamType.NUMERO);
	  proc.param(numeroAutoliquidacion, ParamType.CADENA);
	  proc.param("P", ParamType.CADENA);
	  try {
		  BindingProvider bpr=(BindingProvider) port;
		  bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointDocumentos());
		  
		  Binding bi = bpr.getBinding();
		  List<Handler> handlerList = bi.getHandlerChain();
		  if (handlerList == null)
		  {
			 handlerList = new ArrayList<Handler>();
		  }
		  handlerList.add(new SoapClientHandler(idSesion));
		  bi.setHandlerChain(handlerList);
			
		  String pdfBase64=port.impresionGD(proc.getPeticion().toXml(), "", false);
		  if (!"".equals(pdfBase64)){
			  pdf=Base64.decode(pdfBase64.toCharArray());
		  } else {
			  throw new PlusvaliaException("No se ha podido generar el informe resultado de la presentación para el sujeto " + nifSujeto);
		  }
		  return pdf;
	  } catch (LanzadorException e){
		  throw new PlusvaliaException("Error al imprimir el pdf de recibo de presentación para el sujeto "+nifSujeto,e);
	  }
	  
  }
  /**
   * Recupera el justificante de presentación para la autoliquidación
   * @param numeroAutoliquidacion
   * @param bd
   * @return
   * @throws PlusvaliaException
   */
  @SuppressWarnings("unchecked")
public byte[] getJustificantePresentacion(
		                                     String numeroAutoliquidacion,
		                                     MediadorBD bd
		                                   )
  throws PlusvaliaException
  {
	  WSDocumentos_Service src = new WSDocumentos_Service();
	  WSDocumentos port= src.getWSDocumentosPort();
	  byte[] pdf;
	  
	  try {
		  String idgdre=bd.recuperaIdreimprimibleJustificantePresentacion(numeroAutoliquidacion);
		  BindingProvider bpr=(BindingProvider) port;
		  bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointDocumentos());
		  
		  Binding bi = bpr.getBinding();
		  List<Handler> handlerList = bi.getHandlerChain();
		  if (handlerList == null)
		  {
			 handlerList = new ArrayList<Handler>();
		  }
		  handlerList.add(new SoapClientHandler(idSesion));
		  bi.setHandlerChain(handlerList);
			
		  String pdfBase64=port.obtenerReimprimibleGDRE(idgdre, "");
		  if (!"".equals(pdfBase64)){
			  pdf=Base64.decode(pdfBase64.toCharArray());
		  } else {
			  throw new PlusvaliaException("No se ha podido recuperar el justificante de presentación para la autoliquidación " + numeroAutoliquidacion);
		  }
		  return pdf;
	  } catch (DatosException e){
		  throw new PlusvaliaException("Error al recuperar el justificante de presentación para la autoliquidación "+ numeroAutoliquidacion,e);
	  }
  }
  
}
