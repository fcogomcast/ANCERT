/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.seguridad;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;




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
	 * Devuelve una instancia {@link CertificadoValidator} que permite validar el certificado.
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
	 * Devuelve una instancia {@link GestorCertificadosBD} que permite gestionar conexiones con base de datos para validar certificado.
	 * @param context Contexto de la sesi�n.
	 * @return instancia {@link GestorCertificadosBD}
	 * @throws SeguridadException si falla en la construcci�n
	 */
	public static GestorCertificadosBD newGestorCertificadosBD(CallContext context) throws SeguridadException
	{
		GestorCertificadosBD obj=new GestorCertificadosBD(context);
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
	/**
	 * Devuelve una instancia {@link FirmaHelper} que permite utilizar las funcionalidades de servicio de firma
	 * @param context Contexto de la sesi�n.
	 * @return instancia FirmaHelper
	 */
	public static FirmaHelper newFirmaHelper(CallContext context)
	{
		FirmaHelper obj=new FirmaHelper(context);
		return obj;
	}

}
