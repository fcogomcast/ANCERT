package es.tributasenasturias.servicios.ibi.deuda.pago;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.soap.SoapClientHandler;
import es.tributasenasturias.servicios.ibi.deuda.util.Constantes;
import es.tributasenasturias.webservices.PasarelaPagoST;
import es.tributasenasturias.webservices.PasarelaPagoST_Service;
import es.tributasenasturias.webservices.ResultadoPeticion;

public class PagoHelper implements IContextReader{
	
	private Preferencias pref;
	private String idSesion;
	private PasarelaPagoST_Service srv;
	private PasarelaPagoST port;
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		this.context= ctx;
	}

	private CallContext context;
	private PagoHelper(CallContext ctx)
	{
		this.context= ctx;
		this.idSesion= (String)context.get(CallContextConstants.IDSESION);
		this.pref = (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
		srv = new PasarelaPagoST_Service();
		port = srv.getPasarelaPagoSTPort();
		preparaConexion(this.port);
	}

	public static PagoHelper newInstance(CallContext ctx)
	{
		return new PagoHelper(ctx);
	}
	/**
	 * Prepara la conexión con la pasarela de pago. Modifica el endpoint y añade un manejador para grabar 
	 * los intercambios de mensajes
	 * @param port Port del servicio de Pasarela de pago
	 */
	@SuppressWarnings("unchecked")
	private void preparaConexion (PasarelaPagoST port)
	{
		BindingProvider bpr = (BindingProvider) port;
		bpr.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pref.getEndpointPago());
		Binding bi = bpr.getBinding();
		List<Handler> handlers = bi.getHandlerChain();
		if (handlers==null)
		{
			handlers = new ArrayList<Handler>();
		}
		handlers.add(new SoapClientHandler(this.idSesion));
		bi.setHandlerChain(handlers);
	}
	
	/**
	 * Realiza la operación de pago
	 * @param emisora Emisora del pago
	 * @param identificacion Identificación de la liquidación
	 * @param referencia Referencia de cobro
	 * @param nifSP Nif del sujeto pasivo
	 * @param ccc Cuenta de pago
	 * @param nifTitular nif del titular
	 * @param importe Importe de pago
	 * @return Objeto ResultadoPago con la información necesaria para saber si el pago terminó correctamente o no
	 */
	public ResultadoPago pagar (String emisora, String identificacion, String referencia, String nifSP, String ccc, String nifTitular, long importe)
	{
		ResultadoPeticion res=port.peticion(Constantes.ORIGEN_PAGO,Constantes.MODALIDAD_PAGO,"",emisora,"",nifSP,"","","",identificacion, referencia,"","",String.valueOf(importe),"","",ccc,nifTitular,"","","","","");
		return ResultadoPago.newInstance().process(res);
	}
}
