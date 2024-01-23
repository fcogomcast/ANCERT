/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.documentos;

import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosInforme;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;




/** Implementa la funcionalidad de creación de documentos. Debería utilizarse esta, y no la creación directa,
 *  porque se ocupa de la inicialización correctamente.
 * @author crubencvs
 *
 */
public class DocumentosFactory {

	/**
	 * Devuelve una instancia de objeto de justificante de presentación.
	 * @param context : Contexto de la llamada.
	 * @param numAutoliquidacion: número de autoliquidación sobre la que se hará el justificante de presentación.
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
			throw new DocumentoException("Imposible crear el objeto de justificante de presentación:" + ex.getMessage(),ex);
		}
	}
	/**
	 * Devuelve una instancia de objeto que permite conocer si el documento ya está generado en base de datos.
	 * @param context : Contexto de la llamada.
	 * @param numAutoliquidacion: número de autoliquidación sobre la que se hará el justificante de presentación. 
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
			throw new DocumentoException("Imposible crear el objeto de justificante de presentación:" + ex.getMessage(),ex);
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
