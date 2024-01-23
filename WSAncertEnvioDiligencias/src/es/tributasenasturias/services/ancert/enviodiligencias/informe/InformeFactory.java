package es.tributasenasturias.services.ancert.enviodiligencias.informe;


import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
/**
 * Implementa la funcionalidad de creación de objetos de informe. Debería utilizarse esta, y no la creación directa,
 *  porque se ocupa de la inicialización correctamente.
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
