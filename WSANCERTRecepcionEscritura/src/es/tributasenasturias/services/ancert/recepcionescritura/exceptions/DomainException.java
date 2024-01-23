/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.exceptions;

/**Esta es la excepción genérica de servicio. Todas las excepciones de dominio deberán ser convertidas a esta.
 * Las excepciones de dominio son las de un origen diferente al técnico.
 * @author crubencvs
 *
 */
public class DomainException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1378716369396767670L;

	/**
	 * @param error
	 * @param original
	 */
	public DomainException(String error, Throwable original) {
		super(error, original);
	}

	/**
	 * @param error
	 */
	public DomainException(String error) {
		super(error);
	}

}
