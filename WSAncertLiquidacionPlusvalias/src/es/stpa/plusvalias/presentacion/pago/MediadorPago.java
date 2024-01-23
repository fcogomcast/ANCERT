package es.stpa.plusvalias.presentacion.pago;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;


import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.soap.SoapClientHandler;
import es.tributasenasturias.webservices.PasarelaPagoST;
import es.tributasenasturias.webservices.PasarelaPagoST_Service;
import es.tributasenasturias.webservices.ResultadoPeticion;

/**
 * Permite la comunicación con la Pasarela de pago
 * @author crubencvs
 *
 */
public class MediadorPago {

	private Preferencias pref;
	private String idLlamada;
	
	public MediadorPago(Preferencias pref, String idLlamada){
		this.pref= pref;
		this.idLlamada= idLlamada;
	}
	
	public static class DatosPago{
		private String ccc;
		private String nifTitular;
		private String importeCentimos;
		private Persona contribuyente;
		private String numeroModelo;
		private String emisoraModelo;
		private String fechaDevengo;
		private String datoEspecifico;
		private String modelo;
		
		public String getCcc() {
			return ccc;
		}
		public void setCcc(String ccc) {
			this.ccc = ccc;
		}
		public String getNifTitular() {
			return nifTitular;
		}
		public void setNifTitular(String nifTitular) {
			this.nifTitular = nifTitular;
		}
		public String getImporteCentimos() {
			return importeCentimos;
		}
		public void setImporteCentimos(String importeCentimos) {
			this.importeCentimos = importeCentimos;
		}
		public Persona getContribuyente() {
			return contribuyente;
		}
		public void setContribuyente(Persona contribuyente) {
			this.contribuyente = contribuyente;
		}
		public String getNumeroModelo() {
			return numeroModelo;
		}
		public void setNumeroModelo(String numeroModelo) {
			this.numeroModelo = numeroModelo;
			if (numeroModelo!=null && numeroModelo.length()>=3){
				this.modelo= numeroModelo.substring(0, 3);
			}
		}
		public String getEmisoraModelo() {
			return emisoraModelo;
		}
		public void setEmisoraModelo(String emisoraModelo) {
			this.emisoraModelo = emisoraModelo;
		}
		public String getModelo() {
			return modelo;
		}
		public void setModelo(String modelo) {
			this.modelo = modelo;
		}
		public String getFechaDevengo() {
			return fechaDevengo;
		}
		public void setFechaDevengo(String fechaDevengo) {
			this.fechaDevengo = fechaDevengo;
		}
		public String getDatoEspecifico() {
			return datoEspecifico;
		}
		public void setDatoEspecifico(String datoEspecifico) {
			this.datoEspecifico = datoEspecifico;
		}
	}
	
	public static class ResultadoPago{
		private boolean error;
		private String mensajeError;
		public boolean isError() {
			return error;
		}
		public String getMensajeError() {
			return mensajeError;
		}
		
		public ResultadoPago(boolean error, String mensajeError) {
			super();
			this.error = error;
			this.mensajeError = mensajeError;
		}
		
		
	}
	/**
	 * Formatea la cadena de fecha que se le pasa con formato DD/MM/YYYY
	 * a DDMMYYYY
	 * @param fecha Fecha en formato DD/MM/YYYY
	 * @return Cadena con fecha en formato DDMMYYYY o cadena vacía si la 
	 * cadena de entrada no tiene el formato esperado.
	 */
	private String formateaFechaDevengo(String fecha){
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date d=sdf.parse(fecha);
			sdf= new SimpleDateFormat("ddMMyyyy");
			return sdf.format(d);
		} catch (ParseException pe){
			return "";
		}
	}
	
	@SuppressWarnings("unchecked")
	public ResultadoPago realizaPago(DatosPago datosPago){
		PasarelaPagoST_Service srv = new PasarelaPagoST_Service();
		PasarelaPagoST port= srv.getPasarelaPagoSTPort();
		
		BindingProvider bpr=(BindingProvider) port;
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointPasarelaPago());
		
		Binding bi = bpr.getBinding();
		List<Handler> handlerList = bi.getHandlerChain();
		if (handlerList == null)
		{
			handlerList = new ArrayList<Handler>();
		}
		handlerList.add(new SoapClientHandler(idLlamada));
		bi.setHandlerChain(handlerList);
		//Uso el mismo origen que en la presentación de modelo 600.
		ResultadoPeticion res= port.peticion(
					  "S1", 
				      "3", 
					  "", //cliente
					  "", //entidad
					  datosPago.getEmisoraModelo(), 
					  datosPago.getModelo(), 
					  datosPago.getContribuyente().getDatospersonales().getNumerodocumento(), 
					  "", //Nombre de contribuyente
					  formateaFechaDevengo(datosPago.getFechaDevengo()), 
					  datosPago.getDatoEspecifico(), //dato específico. TODO: ¿Qué ponemos? 
					  "", //identificación
					  "", //referencia 
					  datosPago.getNumeroModelo(), // número de autoliquidación
					  "", // expediente
					  datosPago.getImporteCentimos(), 
					  "", //Tarjeta 
					  "", //Fecha caducidad 
					  datosPago.getCcc(), 
					  datosPago.getNifTitular(), 
					  "", //Aplicación 
					  "", //Número único
					  "", //Número petición
					  "", // libre
					  "" //mac
					  );
		String codigo=res.getRespuesta().getError();
		//Sería extraño, pero podrá haberse pagado ya.
		//Podría suceder si no se ha registrado el estado de "Ya pagado"
		//en el estado de la presentación, y se intenta pagar de nuevo.
		if ("0000".equalsIgnoreCase(codigo) || "0001".equalsIgnoreCase(codigo)){
			return new ResultadoPago(false,"");
		} else {
			return new ResultadoPago(true, res.getRespuesta().getResultado());
		}
	}
}
