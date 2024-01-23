/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.seguridad;

import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;

/** Implementa la funcionalidad de creaci�n de logs. Deber�a utilizarse esta, y no la creaci�n directa,
 *  porque se ocupa de la inicializaci�n correctamente.
 * @author crubencvs
 *
 */
public class SeguridadFactory {

	/**
	 * Devuelve una instancia {@link AutenticacionPAHelper} que permite realizar conexi�n con el principado para validar el certificado.
	 * @param context Contexto de la sesi�n.
	 * @return instancia AutenticacionPAHelper
	 */
	public static AutenticacionPAHelper newAutenticacionPAHelper(CallContext context)
	{
		AutenticacionPAHelper obj=new AutenticacionPAHelper(context);
		return obj;
	}
	/**
	 * Devuelve una instancia {@link CertificadoValidator} que permite validar el certificado contra base de datos.
	 * @param context Contexto de la sesi�n.
	 * @return instancia CertificadoValidator
	 */
	public static CertificadoValidator newCertificadoValidator(CallContext context)
	{
		CertificadoValidator obj=new CertificadoValidator();
		obj.setCallContext(context);
		return obj;
	}
	/**
	 * Devuelve una instancia {@link CertificadoValidator} que permite validar la firma de xml
	 * @param context Contexto de la sesi�n.
	 * @return instancia FirmaValidator
	 */
	public static FirmaValidator newFirmaValidator(CallContext context)
	{
		FirmaValidator obj=new FirmaValidator();
		obj.setCallContext(context);
		return obj;
	}


}
