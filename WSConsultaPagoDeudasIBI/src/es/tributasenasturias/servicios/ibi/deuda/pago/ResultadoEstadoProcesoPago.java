package es.tributasenasturias.servicios.ibi.deuda.pago;

import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.MensajesException;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesFactory;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion.MensajeAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.util.Constantes;
import es.tributasenasturias.utils.log.Logger;

/**
 * Contiene los datos de una consulta de referencia viva para el pago
 * @author crubencvs
 *
 */
public class ResultadoEstadoProcesoPago{
	
	private static final int NUM_REGISTRO = 1;
	private static final String ESUN = "ESUN_ESTRUCTURA_UNIVERSAL";
	private String codMensajeError;
	private String mensajeError;
	public enum Estados{PAGO_INICIADO, GENERACION_DOCUMENTOS, PROCESO_FINALIZADO}
	private Estados estado;
	private boolean error;
	private long idregistro;
	public static class DatosPago
	{
		private String emisora;
		private String referencia;
		private String identificacion;
		private String nifSP;
		private String fechaPago;
		
		/**
		 * @return the emisora
		 */
		public String getEmisora() {
			return emisora;
		}
		/**
		 * @return the referencia
		 */
		public String getReferencia() {
			return referencia;
		}
		/**
		 * @return the identificacion
		 */
		public String getIdentificacion() {
			return identificacion;
		}
		/**
		 * @param emisora the emisora to set
		 */
		public void setEmisora(String emisora) {
			this.emisora = emisora;
		}
		/**
		 * @param referencia the referencia to set
		 */
		public void setReferencia(String referencia) {
			this.referencia = referencia;
		}
		/**
		 * @param identificacion the identificacion to set
		 */
		public void setIdentificacion(String identificacion) {
			this.identificacion = identificacion;
		}
		/**
		 * @return the nifSP
		 */
		public String getNifSP() {
			return nifSP;
		}
		/**
		 * @param nifSP the nifSP to set
		 */
		public void setNifSP(String nifSP) {
			this.nifSP = nifSP;
		}
		/**
		 * @return the fechaPago
		 */
		public String getFechaPago() {
			return fechaPago;
		}
		/**
		 * @param fechaPago the fechaPago to set
		 */
		public void setFechaPago(String fechaPago) {
			this.fechaPago = fechaPago;
		}
		
	}
	private DatosPago datosPago;
	
	/**
	 * @return the codMensajeError
	 */
	public String getCodMensajeError() {
		return codMensajeError;
	}
	/**
	 * @param codMensajeError the codMensajeError to set
	 */
	public void setCodMensajeError(String codMensajeError) {
		this.codMensajeError = codMensajeError;
	}
	/**
	 * @return the mensajeError
	 */
	public String getMensajeError() {
		return mensajeError;
	}
	/**
	 * @param mensajeError the mensajeError to set
	 */
	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}
	
	/**
	 * @return the estado
	 */
	public Estados getEstado() {
		return estado;
	}
	/**
	 * @param estado the estado to set
	 */
	public void setEstado(Estados estado) {
		this.estado = estado;
	}
	
	/**
	 * @return the datosPago
	 */
	public DatosPago getDatosPago() {
		return datosPago;
	}
	/**
	 * @param datosPago the datosPago to set
	 */
	public void setDatosPago(DatosPago datosPago) {
		this.datosPago = datosPago;
	}
	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(boolean error) {
		this.error = error;
	}
	
	/**
	 * @return the idregistro
	 */
	public long getIdregistro() {
		return idregistro;
	}
	/**
	 * @param idregistro the idregistro to set
	 */
	public void setIdregistro(long idregistro) {
		this.idregistro = idregistro;
	}
	/**
	 * No se puede instanciar directamente
	 */
	private ResultadoEstadoProcesoPago()
	{
	}
	/**
	 * Crea una nueva instancia de objeto inicializado con los datos necesarios para interpretar el resultado de la consulta
	 * @param respuesta Respuesta del lanzador en la consulta de referencia viva de pago
	 * @param ctx Contexto de llamada
	 * @return Objeto con datos del resultado de la consulta
	 * @throws IBIException
	 */
	public static ResultadoEstadoProcesoPago newInstance(RespuestaLanzador respuesta, CallContext ctx) throws IBIException
	{
		ResultadoEstadoProcesoPago r = new ResultadoEstadoProcesoPago();
		MensajesAplicacion men;
		Logger log=null;
		try
		{
			log = (Logger)ctx.get(CallContextConstants.LOG);
		} catch (Exception e)
		{
			//Lo ignoramos. En todo caso, no escribimos a log
		}
		try {
			men = MensajesFactory.getMensajesAplicacionFromContext(ctx);
		} catch (MensajesException e) {
			throw new IBIException(e.getMessage(),e);
		}
		//Comprobamos el mapeado de mensaje. Si se trata de un error, lo tratamos, si no, continuamos.
		String codResultado = respuesta.getValue(ESUN,NUM_REGISTRO,"C1");
		MensajeAplicacion mensajeDevuelto = men.getMapeoMensaje(Constantes.seccionBD,codResultado);
		String estado = respuesta.getValue(ESUN, NUM_REGISTRO, "C2");
		if (!mensajeDevuelto.isError())
		{
			
		    if ("INI_PAG".equalsIgnoreCase (estado))
		    {
		    	r.setEstado(Estados.PAGO_INICIADO);
		    	r.setIdregistro(Long.parseLong(respuesta.getValue(ESUN, NUM_REGISTRO, "N1")));
		    	DatosPago p = new DatosPago();
		    	r.setError(false);
				p.setIdentificacion(respuesta.getValue(ESUN,NUM_REGISTRO,"C4"));
				p.setReferencia(respuesta.getValue(ESUN,NUM_REGISTRO,"C3"));
				p.setNifSP(respuesta.getValue(ESUN,NUM_REGISTRO,"C5"));
				p.setEmisora(respuesta.getValue(ESUN,NUM_REGISTRO,"C6"));
				r.setDatosPago(p);
		    }
		    else if ("INI_DOCU".equalsIgnoreCase(estado))
		    {
		    	r.setEstado(Estados.GENERACION_DOCUMENTOS);
		    	r.setIdregistro(Long.parseLong(respuesta.getValue(ESUN, NUM_REGISTRO, "N1")));
		    	DatosPago p = new DatosPago();
		    	r.setError(false);
		    	p.setReferencia(respuesta.getValue(ESUN,NUM_REGISTRO,"C3"));
		    	p.setFechaPago(respuesta.getValue(ESUN, NUM_REGISTRO, "C4"));
		    	p.setNifSP(respuesta.getValue(ESUN,NUM_REGISTRO,"C5"));
		    	r.setDatosPago(p);
		    }
		    else if ("FIN".equalsIgnoreCase(estado))
		    {
		    	r.setEstado(Estados.PROCESO_FINALIZADO);
		    	DatosPago p = new DatosPago();
		    	r.setError(false);
		    	p.setReferencia(respuesta.getValue(ESUN,NUM_REGISTRO,"C3"));
		    	r.setDatosPago(p);
		    }
		}
		else
		{
			if (log!=null)
			{
				if (!"".equals(estado))
				{
					log.error("===Error devuelto de BD:" + codResultado + "=" + estado);
				}
				else
				{
					log.error("===Error devuelto de BD:" + codResultado);
				}
			}
			r.setError(true);
			r.setCodMensajeError(mensajeDevuelto.getCodMensaje());
			r.setMensajeError(mensajeDevuelto.getTextoMensaje());
			
		}
		return r;
	}
	
}
