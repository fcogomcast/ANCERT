package es.tributasenasturias.servicios.ibi.deuda.pago;

import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.MensajesException;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesFactory;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion.MensajeAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.util.Constantes;

/**
 * Contiene los datos de una invocación de generación de informe de no deuda
 * @author crubencvs
 *
 */
public class ResultadoGeneracionCertNoDeuda{
	
	private static final int NUM_REGISTRO = 1;
	private static final String CADE = "CADE_CADENA";
	private static final String CANU = "CANU_CADENAS_NUMEROS";
	private String codMensajeError;
	private String mensajeError;
	private boolean error;
	public static class DatosDocumento
	{
		private String idElemento;
		private String tipoElemento;
		private String nifNotario;
		private String nifContribuyente;
		private String numeroSerie;
		private String mac;
		private String letraCodVerificacion;
		private String codVerificacion;
		/**
		 * @return the idElemento
		 */
		public String getIdElemento() {
			return idElemento;
		}
		/**
		 * @param idElemento the idElemento to set
		 */
		public void setIdElemento(String idElemento) {
			this.idElemento = idElemento;
		}
		/**
		 * @return the tipoElemento
		 */
		public String getTipoElemento() {
			return tipoElemento;
		}
		/**
		 * @param tipoElemento the tipoElemento to set
		 */
		public void setTipoElemento(String tipoElemento) {
			this.tipoElemento = tipoElemento;
		}
		/**
		 * @return the nifNotario
		 */
		public String getNifNotario() {
			return nifNotario;
		}
		/**
		 * @param nifNotario the nifNotario to set
		 */
		public void setNifNotario(String nifNotario) {
			this.nifNotario = nifNotario;
		}
		/**
		 * @return the numeroSerie
		 */
		public String getNumeroSerie() {
			return numeroSerie;
		}
		/**
		 * @param numeroSerie the numeroSerie to set
		 */
		public void setNumeroSerie(String numeroSerie) {
			this.numeroSerie = numeroSerie;
		}
		/**
		 * @return the mac
		 */
		public String getMac() {
			return mac;
		}
		/**
		 * @param mac the mac to set
		 */
		public void setMac(String mac) {
			this.mac = mac;
		}
		/**
		 * @return the letraCodVerificacion
		 */
		public String getLetraCodVerificacion() {
			return letraCodVerificacion;
		}
		/**
		 * @param letraCodVerificacion the letraCodVerificacion to set
		 */
		public void setLetraCodVerificacion(String letraCodVerificacion) {
			this.letraCodVerificacion = letraCodVerificacion;
		}
		/**
		 * @return the nifContribuyente
		 */
		public String getNifContribuyente() {
			return nifContribuyente;
		}
		/**
		 * @param nifContribuyente the nifContribuyente to set
		 */
		public void setNifContribuyente(String nifContribuyente) {
			this.nifContribuyente = nifContribuyente;
		}
		/**
		 * @return the codVerificacion
		 */
		public String getCodVerificacion() {
			return codVerificacion;
		}
		/**
		 * @param codVerificacion the codVerificacion to set
		 */
		public void setCodVerificacion(String codVerificacion) {
			this.codVerificacion = codVerificacion;
		}
		
		
		
	}
	private DatosDocumento datosDocumento;
	
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
	 * @return the datosDocumento
	 */
	public DatosDocumento getDatosDocumento() {
		return datosDocumento;
	}
	/**
	 * @param datosDocumento the datosDocumento to set
	 */
	public void setDatosDocumento(DatosDocumento datosDocumento) {
		this.datosDocumento = datosDocumento;
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
	 * No se puede instanciar directamente
	 */
	private ResultadoGeneracionCertNoDeuda()
	{
	}
	/**
	 * Crea una nueva instancia de objeto inicializado con los datos necesarios para interpretar el resultado de la generación
	 * @param respuesta Respuesta del lanzador en la generación de certificado de deuda
	 * @param ctx Contexto de llamada
	 * @return Objeto con datos del resultado de la consulta
	 * @throws IBIException
	 */
	public static ResultadoGeneracionCertNoDeuda newInstance(RespuestaLanzador respuesta, CallContext ctx) throws IBIException
	{
		ResultadoGeneracionCertNoDeuda r = new ResultadoGeneracionCertNoDeuda();
		MensajesAplicacion men;
		try {
			men = MensajesFactory.getMensajesAplicacionFromContext(ctx);
		} catch (MensajesException e) {
			throw new IBIException(e.getMessage(),e);
		}
		//Comprobamos el mapeado de mensaje. Si se trata de un error, lo tratamos, si no, continuamos.
		String codResultado = respuesta.getValue(CADE,NUM_REGISTRO,"STRING_CADE");
		MensajeAplicacion mensajeDevuelto = men.getMapeoMensaje(Constantes.seccionBD,codResultado);
		if (!mensajeDevuelto.isError())
		{
		    	DatosDocumento d = new DatosDocumento();
		    	r.setError(false);
				d.setIdElemento(respuesta.getValue(CANU,NUM_REGISTRO,"NUME1_CANU"));
				d.setTipoElemento(respuesta.getValue(CANU,NUM_REGISTRO,"FECHA2_CANU"));
				d.setNifNotario(respuesta.getValue(CANU,NUM_REGISTRO,"STRING2_CANU"));
				d.setNumeroSerie(respuesta.getValue(CANU,NUM_REGISTRO,"STRING3_CANU"));
				d.setMac(respuesta.getValue(CANU,NUM_REGISTRO,"STRING4_CANU"));
				d.setLetraCodVerificacion(respuesta.getValue(CANU,NUM_REGISTRO,"FECHA1_CANU"));
				d.setCodVerificacion(respuesta.getValue(CANU,NUM_REGISTRO,"STRING1_CANU"));
				d.setNifContribuyente(respuesta.getValue(CANU, NUM_REGISTRO, "STRING5_CANU"));
				r.setDatosDocumento(d);
		}
		else
		{
			r.setError(true);
			r.setCodMensajeError(mensajeDevuelto.getCodMensaje());
			r.setMensajeError(mensajeDevuelto.getTextoMensaje());
		}
		return r;
	}
	
}
