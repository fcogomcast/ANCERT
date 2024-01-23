package es.tributasenasturias.servicios.ibi.deuda.pago;

import es.tributasenasturias.webservices.ResultadoPeticion;

/**
 * Contiene información acerca del resultado de un pago de IBI
 * @author crubencvs
 *
 */
public class ResultadoPago {

	private boolean error;
	private String codigoResultado;
	private String mensajeResultado;
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
	 * @return the codigoResultado
	 */
	public String getCodigoResultado() {
		return codigoResultado;
	}
	/**
	 * @param codigoResultado the codigoResultado to set
	 */
	public void setCodigoResultado(String codigoResultado) {
		this.codigoResultado = codigoResultado;
	}
	/**
	 * @return the mensajeResultado
	 */
	public String getMensajeResultado() {
		return mensajeResultado;
	}
	/**
	 * @param mensajeResultado the mensajeResultado to set
	 */
	public void setMensajeResultado(String mensajeResultado) {
		this.mensajeResultado = mensajeResultado;
	}
	/**
	 * Constructor. No se permitirá instanciar directamente el objeto
	 */
	private ResultadoPago()
	{
	}
	/**
	 * Devuelve una nueva instancia de resultado de petición
	 * @return nueva instancia
	 */
	public static ResultadoPago newInstance()
	{
		return new ResultadoPago();
	}
	/**
	 * Procesa un resultado de petición y devuelve el resultado traducido
	 * @param res Resultado de Peticion de pago del servicio de pasarela de pago
	 * @return Objeto ResultadoPago con el resultado del pago en una forma que puede ser utilizada 
	 * por el servicio de pago de IBI
	 */
	public ResultadoPago process(ResultadoPeticion res)
	{
		//Pago correcto
		if ("0000".equals(res.getRespuesta().getError())|| "0001".equals(res.getRespuesta().getError()))
		{
			this.setError(false);
		}
		else
		{
			this.setError(true);
		}
		this.setCodigoResultado(res.getRespuesta().getError());
		this.setMensajeResultado(res.getRespuesta().getResultado());
		return this;
	}
	
}
