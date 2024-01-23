package es.tributasenasturias.services.ancert.enviodiligencias.informe;


import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
/**
 * Implementa la funcionalidad de creaci�n de objetos de informe. Deber�a utilizarse esta, y no la creaci�n directa,
 *  porque se ocupa de la inicializaci�n correctamente.
 * @author crubencvs
 *
 */
public class InformeFactory {
	/**
	 * Devuelve un objeto que permite generar un informe PDF
	 * @param context : Contexto de la llamada.
	 * @return GeneradorInforme
	 */
	public static GeneradorInforme newGeneradorInforme(CallContext context)
	{
			GeneradorInforme obj = new GeneradorInforme();
			obj.setCallContext(context);
			return obj;
	}
}
