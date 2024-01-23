package es.stpa.plusvalias.presentacion;

/**
 * Estado de finalización
 * @author crubencvs
 *
 */
public class EstadoError implements Estado{
	@SuppressWarnings("unused")
	private PresentacionLiquidacion p; //No se utiliza, pero prefiero dejarlo por coherencia con el resto de estados
	private String codigoError;
	private String mensajeError;
	
	public EstadoError(PresentacionLiquidacion p, String codigoError, String mensajeError){
		this.p= p;
		this.codigoError= codigoError;
		this.mensajeError= mensajeError;
	}
	
	
	/**
	 * Recupera el código de error de este estado
	 * @return
	 */
	public String getCodigoError() {
		return codigoError;
	}

	/**
	 * Recupera el mensaje de error de este estado
	 * @return
	 */
	public String getMensajeError() {
		return mensajeError;
	}


	@Override
	public void operar() {
		//No cumple ninguna función, en los datos del estado está el error.
	}
}
