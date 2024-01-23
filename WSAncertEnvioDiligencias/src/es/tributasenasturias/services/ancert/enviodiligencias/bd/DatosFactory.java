/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.bd;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;





/** Implementa la funcionalidad de creación de objetos de datos. Debería utilizarse esta, y no la creación directa,
 *  porque se ocupa de la inicialización correctamente.
 * @author crubencvs
 *
 */
public class DatosFactory {

	/**
	 * Devuelve una instancia de objeto de datos de informe.
	 * @param numAutoliquidacion: número de autoliquidación de la que se van a recuperar datos. 
	 * @return instancia de DatosInforme
	 */
	public static DatosInforme newDatosInforme(CallContext context,String numAutoliquidacion) throws DatosException,es.tributasenasturias.services.ancert.enviodiligencias.SystemException
	{
			DatosInforme obj = new DatosInforme();
			obj.setCallContext(context);
			obj.recuperaDatosAutoliquidacion(numAutoliquidacion);
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podrá contener los datos necesarios para un justificante de presentación.
	 * @return instancia de JustificantePresentacionDO
	 */
	public static JustificantePresentacionDO newJustificantePresentacionDO()
	{
			JustificantePresentacionDO obj = new JustificantePresentacionDO();
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podrá contener datos del expediente sobre el que se presenta el justificante.
	 * @return instancia de DatosExpedienteDO
	 */
	public static DatosExpedienteDO newDatosExpedienteDO()
	{
			DatosExpedienteDO obj = new DatosExpedienteDO();
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podrá contener datos de la persona que se mostrarán en el informe.
	 * @return instancia de DatosPersonaDO
	 */
	public static DatosPersonaDO newDatosPersonaDO()
	{
			DatosPersonaDO obj = new DatosPersonaDO();
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podrá contener datos de la solicitud.
	 * @return instancia de DatosSolicitudDO
	 */
	public static DatosSolicitudDO newDatosSolicitudDO()
	{
			DatosSolicitudDO obj = new DatosSolicitudDO();
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podrá usarse para recuperar una plantilla XML de base de datos..
	 * @return instancia de DatosPersonaDO
	 */
	public static PlantillaXML newPlantillaXML(CallContext context)
	{
			PlantillaXML obj = new PlantillaXML();
			obj.setCallContext(context);
			return obj;
	}
	
}
