package org.notariado.inti.bd;

/**
 * Clase con las estructuras de vuelta de las llamadas a Base de datos
 * @author crubencvs
 *
 */
public class ResultadosLlamadasBD {

	public static class ResultadoImpresionJustificante
	{
		private String idReimprimible;
		private String tipoReimprimible;
		private String tipoDocumento;
		private String numeroJustificante;
		private String nifPresentador;
		private String macCodigoVerificacion;
		
		/**
		 * Constructor de resultado de impresión de justificante
		 * @param idReimprimible Identificador del informe reimprimible
		 * @param tipoReimprimible Tipo de informe reimprimible
		 * @param tipoDocumento Tipo de documento ("J" para justificante)
		 * @param numeroJustificante Número de justificante generado
		 * @param nifPresentador Nif de presentador asociado al documento
		 * @param macCodigoVerificacion Mac del código de verificación
		 */
		protected ResultadoImpresionJustificante(String idReimprimible, String tipoReimprimible, 
												 String tipoDocumento, String numeroJustificante, 
												 String nifPresentador,
												 String macCodigoVerificacion)
		{
			this.idReimprimible= idReimprimible;
			this.tipoReimprimible = tipoReimprimible;
			this.tipoDocumento = tipoDocumento;
			this.numeroJustificante= numeroJustificante;
			this.nifPresentador = nifPresentador;
			this.macCodigoVerificacion = macCodigoVerificacion;
		}
		
		public String getIdReimprimible() {
			return idReimprimible;
		}
		public void setIdReimprimible(String idReimprimible) {
			this.idReimprimible = idReimprimible;
		}
		public String getTipoReimprimible() {
			return tipoReimprimible;
		}
		public void setTipoReimprimible(String tipoReimprimible) {
			this.tipoReimprimible = tipoReimprimible;
		}
		public String getTipoDocumento() {
			return tipoDocumento;
		}
		public void setTipoDocumento(String tipoDocumento) {
			this.tipoDocumento = tipoDocumento;
		}
		public String getNumeroJustificante() {
			return numeroJustificante;
		}
		public void setNumeroJustificante(String numeroJustificante) {
			this.numeroJustificante = numeroJustificante;
		}
		public String getMacCodigoVerificacion() {
			return macCodigoVerificacion;
		}
		public String getNifPresentador() {
			return nifPresentador;
		}

		public void setNifPresentador(String nifPresentador) {
			this.nifPresentador = nifPresentador;
		}

		public void setMacCodigoVerificacion(String macCodigoVerificacion) {
			this.macCodigoVerificacion = macCodigoVerificacion;
		}
		
	}
}
