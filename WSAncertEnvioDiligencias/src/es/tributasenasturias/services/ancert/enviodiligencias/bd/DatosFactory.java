/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.bd;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;





/** Implementa la funcionalidad de creaci�n de objetos de datos. Deber�a utilizarse esta, y no la creaci�n directa,
 *  porque se ocupa de la inicializaci�n correctamente.
 * @author crubencvs
 *
 */
public class DatosFactory {

	/**
	 * Devuelve una instancia de objeto de datos de informe.
	 * @param numAutoliquidacion: n�mero de autoliquidaci�n de la que se van a recuperar datos. 
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
	 * Devuelve una instancia de objeto que podr� contener los datos necesarios para un justificante de presentaci�n.
	 * @return instancia de JustificantePresentacionDO
	 */
	public static JustificantePresentacionDO newJustificantePresentacionDO()
	{
			JustificantePresentacionDO obj = new JustificantePresentacionDO();
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podr� contener datos del expediente sobre el que se presenta el justificante.
	 * @return instancia de DatosExpedienteDO
	 */
	public static DatosExpedienteDO newDatosExpedienteDO()
	{
			DatosExpedienteDO obj = new DatosExpedienteDO();
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podr� contener datos de la persona que se mostrar�n en el informe.
	 * @return instancia de DatosPersonaDO
	 */
	public static DatosPersonaDO newDatosPersonaDO()
	{
			DatosPersonaDO obj = new DatosPersonaDO();
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podr� contener datos de la solicitud.
	 * @return instancia de DatosSolicitudDO
	 */
	public static DatosSolicitudDO newDatosSolicitudDO()
	{
			DatosSolicitudDO obj = new DatosSolicitudDO();
			return obj;
	}
	/**
	 * Devuelve una instancia de objeto que podr� usarse para recuperar una plantilla XML de base de datos..
	 * @return instancia de DatosPersonaDO
	 */
	public static PlantillaXML newPlantillaXML(CallContext context)
	{
			PlantillaXML obj = new PlantillaXML();
			obj.setCallContext(context);
			return obj;
	}
	
}
