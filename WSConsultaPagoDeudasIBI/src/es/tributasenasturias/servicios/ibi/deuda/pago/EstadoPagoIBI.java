package es.tributasenasturias.servicios.ibi.deuda.pago;

import es.tributasenasturias.services.lanzador.client.LanzadorException;
import es.tributasenasturias.services.lanzador.client.LanzadorFactory;
import es.tributasenasturias.services.lanzador.client.ParamType;
import es.tributasenasturias.services.lanzador.client.ProcedimientoAlmacenado;
import es.tributasenasturias.services.lanzador.client.TLanzador;
import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.soap.SoapClientHandler;
/**
 * Operaciones sobre el estado de pago de IBI
 * @author crubencvs
 *
 */
public class EstadoPagoIBI {

	public enum Estados{PAGO_REALIZADO, DOCUMENTOS_GENERADOS};
	/**
	 * Actualiza el estado de un registro de pago de IBI
	 * @param idRegistro Identificador del registro
	 * @param estado Estado en el que poner el registro
	 * @param pref Preferencias
	 * @param idsesion Identificador de sesión
	 * @throws IBIException
	 */
	private static void actualizaEstado(long idRegistro, Estados estado,Preferencias pref, String idsesion) throws IBIException
	{
		RespuestaLanzador respuesta;
		TLanzador lanzador;
		String strEstado="";
		if (Estados.PAGO_REALIZADO.equals(estado))
		{
			strEstado="INI_DOCU";
		}
		else if (Estados.DOCUMENTOS_GENERADOS.equals(estado))
		{
			strEstado="FIN";
		}
		try {
			lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(),new SoapClientHandler(idsesion));
			ProcedimientoAlmacenado pa= new ProcedimientoAlmacenado(pref.getProcActualizaEstado(),pref.getEsquemaBD());
			pa.param(String.valueOf(idRegistro), ParamType.NUMERO);
			pa.param(strEstado, ParamType.CADENA);
			pa.param("P", ParamType.CADENA);
			String resp=lanzador.ejecutar(pa);
			respuesta=new RespuestaLanzador(resp); 
			if (respuesta.esErronea())
			{
				throw new IBIException ("La ejecución del procedimiento almacenado ha fallado:" + respuesta.getTextoError());
			}
			String resultado=respuesta.getValue("CADE_CADENA", 1, "STRING_CADE");
			if  (!("OK".equalsIgnoreCase(resultado)))
			{
				throw new IBIException ("Error al actualizar el estado de pago de IBI " + idRegistro + ":"+ resultado);
			}

		} catch (LanzadorException e) {
			throw new IBIException ("Error en la comunicación con base de datos:" + e.getMessage());
		}
	}
	/**
	 * Establece el estado de PAGO de IBI a "Pagado", por lo que sólo queda generar los documentos
	 * @param idRegistro Identificador del registro de pago iniciado en ANPI_ANCERT_PAGOS_IBI
	 * @param pref Preferencias
	 * @param sesion Sesion
	 * @throws IBIException
	 */
	public static void setEstadoPagado(long idRegistro,Preferencias pref, String sesion) throws IBIException
	{
		actualizaEstado(idRegistro, Estados.PAGO_REALIZADO,pref, sesion);
	}
	/**
	 * Establece el estado de PAGO de IBI a "Finalizado". No queda nada por hacer
	 * @param idRegistro Identificador de registro de pago
	 * @param pref Preferencias
	 * @param sesion Sesion
	 * @throws IBIException
	 */
	public static void setEstadoFinalizado(long idRegistro, Preferencias pref, String sesion) throws IBIException
	{
		actualizaEstado (idRegistro, Estados.DOCUMENTOS_GENERADOS,pref, sesion);
	}
}
