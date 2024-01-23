package es.stpa.plusvalias.domain;



import es.stpa.plusvalias.domain.intercambio.Cabecera;

/**
 * Clase para validar datos de la cabecera recibida en la petición
 * @author crubencvs
 *
 */
public class ValidadorCabeceraPeticion {

	private ValidadorCabeceraPeticion(){}
	
	public static class ResultadoValidacion{
		private boolean error;
		private String codigo;
		private String mensajeError;
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public String getCodigo() {
			return codigo;
		}
		public void setCodigo(String codigo) {
			this.codigo = codigo;
		}
		public String getMensajeError() {
			return mensajeError;
		}
		public void setMensajeError(String mensajeError) {
			this.mensajeError = mensajeError;
		}
	}

	
	
	/**
	 * Validaciones propias cuando se trata de un causante. 
	 * @param t Transmitente, cuando se trata de un causante
	 * @return
	 */
	public static ResultadoValidacion validarReceptor(Cabecera c){
		ResultadoValidacion resultado= new ResultadoValidacion();
		resultado.setError(false);
		
		/*if (!resultado.isError()){
			if (c.getReceptor()==null || "".equals(c.getReceptor())){
				resultado.setCodigo(CodigosTerminacion.ERROR_AYUNTAMIENTO);
				resultado.setMensajeError(CodigosTerminacion.getMessage(resultado.getCodigo(), "[Vacío]"));
				resultado.setError(true);
			} else {
				try {
					Integer ayto;
					if (c.getReceptor().length()>5){ //Nos envían código INE, así que tendrá 6 caracteres
						ayto=Integer.valueOf(c.getReceptor().substring(0, 5));
					} else {
						ayto=Integer.valueOf(c.getReceptor());
					}
					
					if (ayto<33001 || ayto > 33078){
						resultado.setCodigo(CodigosTerminacion.ERROR_AYUNTAMIENTO);
						resultado.setMensajeError(CodigosTerminacion.getMessage(resultado.getCodigo(), c.getReceptor()));
						resultado.setError(true);
					}
				} catch(NumberFormatException nfe){
					resultado.setCodigo(CodigosTerminacion.ERROR_AYUNTAMIENTO);
					resultado.setMensajeError(CodigosTerminacion.getMessage(resultado.getCodigo(), c.getReceptor()));
					resultado.setError(true);
				}
			}
			
		}*/
		return resultado;
	}
	
	
}
