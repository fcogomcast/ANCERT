/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.documentos;

import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosInforme;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;




/** Implementa la funcionalidad de creaci�n de documentos. Deber�a utilizarse esta, y no la creaci�n directa,
 *  porque se ocupa de la inicializaci�n correctamente.
 * @author crubencvs
 *
 */
public class DocumentosFactory {

	/**
	 * Devuelve una instancia de objeto de justificante de presentaci�n.
	 * @param context : Contexto de la llamada.
	 * @param numAutoliquidacion: n�mero de autoliquidaci�n sobre la que se har� el justificante de presentaci�n.
	 * @param datos Datos para componer el informe. 
	 * @return instancia de LogHelper
	 */
	public static JustificantePresentacion newJustificantePresentacion(CallContext context,String numAutoliquidacion, DatosInforme datos) throws DocumentoException
	{
		try
		{
			JustificantePresentacion obj = new JustificantePresentacion(context,numAutoliquidacion, datos);
			return obj;
		}
		catch (Exception ex)
		{
			throw new DocumentoException("Imposible crear el objeto de justificante de presentaci�n:" + ex.getMessage(),ex);
		}
	}
	/**
	 * Devuelve una instancia de objeto que permite conocer si el documento ya est� generado en base de datos.
	 * @param context : Contexto de la llamada.
	 * @param numAutoliquidacion: n�mero de autoliquidaci�n sobre la que se har� el justificante de presentaci�n. 
	 * @return instancia de LogHelper
	 */
	public static DocumentoDoin newDocumentoDoin(CallContext context,String numAutoliquidacion) throws DocumentoException
	{
		try
		{
			DocumentoDoin obj = new DocumentoDoin(numAutoliquidacion);
			obj.setCallContext(context);
			return obj;
		}
		catch (Exception ex)
		{
			throw new DocumentoException("Imposible crear el objeto de justificante de presentaci�n:" + ex.getMessage(),ex);
		}
	}
	/**
	 * Devuelve una instancia de objeto que permite dar de alta el documento en base de datos.
	 * @param context : Contexto de la llamada.
	 * @return instancia de InsertadorDocumento
	 */
	public static InsertadorDocumento newInsertadorDocumento(CallContext context)
	{
		InsertadorDocumento obj = new InsertadorDocumento();
		obj.setCallContext(context);
		return obj;
	}
	
}
