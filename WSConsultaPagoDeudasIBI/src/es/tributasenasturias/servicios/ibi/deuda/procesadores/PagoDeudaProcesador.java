package es.tributasenasturias.servicios.ibi.deuda.procesadores;


import es.tributasenasturias.servicios.ibi.deuda.MESSAGEREQUEST;
import es.tributasenasturias.servicios.ibi.deuda.MESSAGERESPONSE;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.MensajesException;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.ConstructorMensajeRespuestaPago;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesFactory;
import es.tributasenasturias.servicios.ibi.deuda.pago.GestorEstadoPagoIBI;
import es.tributasenasturias.utils.log.Logger;

public class PagoDeudaProcesador implements InterfazDeudaIBI, IContextReader{

	private CallContext context;
	private Logger log;
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context = ctx;
	}
	/**
	 * Constructor, no debería llamarse directamente.
	 */
	protected PagoDeudaProcesador() {
	}
	/**
	 * Constructor que acepta un contexto de llamada para pasar datos acerca de una llamada concreta del servicio.
	 * @param ctx Contexto de llamada como un {@link CallContext}
	 */
	protected PagoDeudaProcesador(CallContext ctx)
	{
		context=ctx;
		log = (Logger)context.get(CallContextConstants.LOG);
	}
	@Override
	public MESSAGERESPONSE process(MESSAGEREQUEST partRequest) throws IBIException{
		MESSAGERESPONSE msgresp;
		MensajesAplicacion men;
		try {
			men = MensajesFactory.getMensajesAplicacionFromContext(context);
		} catch (MensajesException e1) {
			throw new IBIException ("Imposible cargar mensajes de la aplicación:" + e1.getMessage(),e1);
		}
		GestorEstadoPagoIBI gestorPago = GestorEstadoPagoIBI.newInstance(context, men);
		String idComunicacion = String.valueOf(partRequest.getPAGODEUDAS().getREQUEST().getCABECERA().getIDCOMUNICACION());
		String codOper = String.valueOf(partRequest.getPAGODEUDAS().getREQUEST().getCABECERA().getCODOPER());
		log.info("**Entrada al servicio de pago de IBI.");
		//Validar la entrada
		//1. Comprobamos el estado del pago, que podrá ser nuevo, pagado pero sin documentos generados o completado
		log.info ("====Estado de pago y datos asociados a cada estado.");
		gestorPago.getEstadoPeticion(partRequest.getPAGODEUDAS().getREQUEST());
		if (!gestorPago.isError())
		{
			gestorPago.procesarEstado();
			if (!gestorPago.isError())
			{
				//Mensaje correcto
				ConstructorMensajeRespuestaPago resp = MensajesFactory.newConstructorMensajeRespuestaPago();
				msgresp = resp.construirMensajeRespuesta(idComunicacion, codOper, partRequest.getPAGODEUDAS().getREQUEST().getORDENPAGO().getPARAMETROSPAGO().getIMPORTEDEUDA(), gestorPago.getDocumentos().getJustificantePago(),gestorPago.getDocumentos().getCodVerifJustificante(), gestorPago.getDocumentos().getCertificadoNoDeuda());
			}
			else
			{
				//Impresión de error
				ConstructorMensajeRespuestaPago resp = MensajesFactory.newConstructorMensajeRespuestaPago();
				msgresp = resp.construirMensajeError(gestorPago.getCodError(), gestorPago.getTextError(), idComunicacion, codOper);
			}
		}
		else
		{
			//Impresión de error
			ConstructorMensajeRespuestaPago resp = MensajesFactory.newConstructorMensajeRespuestaPago();
			msgresp = resp.construirMensajeError(gestorPago.getCodError(), gestorPago.getTextError(), idComunicacion, codOper);
		}
		log.info ("**Salida del servicio de pago de IBI.");
		return msgresp;	
	}
}
