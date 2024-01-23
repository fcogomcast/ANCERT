/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas;

/**Funcionalidades de las clases que utilizarán los contextos de llamada para recuperar información.
 * @author crubencvs
 *
 */
public interface IContextReader {
	public void setCallContext(CallContext ctx);
	public CallContext getCallContext();
}
