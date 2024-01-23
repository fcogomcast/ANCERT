package es.stpa.plusvalias;




import es.stpa.plusvalias.exceptions.PlusvaliaException;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.soap.SoapClientHandler;
import es.tributasenasturias.log.TributasLogger;
import es.tributasenasturias.services.lanzador.client.LanzadorException;
import es.tributasenasturias.services.lanzador.client.LanzadorFactory;
import es.tributasenasturias.services.lanzador.client.ParamType;
import es.tributasenasturias.services.lanzador.client.ProcedimientoAlmacenado;
import es.tributasenasturias.services.lanzador.client.TLanzador;
import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;

/**
 * Implementación de la operación de recuperación  de exenciones y bonificaciones
 * @author crubencvs
 *
 */
public class ExencionBonificacionImpl {

	private TributasLogger log;
	private Preferencias  pref;
	private String idLlamada;
	
	public ExencionBonificacionImpl(Preferencias pref, TributasLogger log, String idLlamada){
		this.log= log;
		this.pref= pref;
		this.idLlamada= idLlamada;
	}
	
	/**
	 * Añade una bonificación o exención a la lista
	 * @param reply Objeto de respuesta
	 * @param porcentaje de la bonificación o exención
	 * @param concepto Concepto de la bonificación o exención
	 * @param descripcion de la bonificación o exención 
	 */
	private void addBeneficio(EXENCIONESBONIFICACIONES.REPLY reply, String porcentaje, String concepto, String descripcion, String tipo){
		if (reply.getBONIFICACIONES()==null){
			EXENCIONESBONIFICACIONES.REPLY.BONIFICACIONES bonif= new EXENCIONESBONIFICACIONES.REPLY.BONIFICACIONES();
			reply.setBONIFICACIONES(bonif);
		}
		if (reply.getEXENCIONES()==null){
			EXENCIONESBONIFICACIONES.REPLY.EXENCIONES exen= new EXENCIONESBONIFICACIONES.REPLY.EXENCIONES();
			reply.setEXENCIONES(exen);
		}
		if ("B".equals(tipo)){
			EXENCIONESBONIFICACIONES.REPLY.BONIFICACIONES.BONIFICACION bonif= new EXENCIONESBONIFICACIONES.REPLY.BONIFICACIONES.BONIFICACION();
			bonif.setPORCENTAJE(porcentaje.replace('.', ','));
			bonif.setDESCRIPCION(descripcion);
			bonif.setCONCEPTO(concepto);
			reply.getBONIFICACIONES().getBONIFICACION().add(bonif);
		} else if ("E".equals(tipo)){
			EXENCIONESBONIFICACIONES.REPLY.EXENCIONES.EXENCION exen= new EXENCIONESBONIFICACIONES.REPLY.EXENCIONES.EXENCION();
			exen.setPORCENTAJE(porcentaje.replace('.',','));
			exen.setDESCRIPCION(descripcion);
			exen.setCONCEPTO(concepto);
			reply.getEXENCIONES().getEXENCION().add(exen);
		}
	}
	/**
	 * Establece la parte de resultado de la respuesta
	 * @param reply
	 * @param codigo
	 * @param descripcion
	 * @param esError
	 */
	private void setEstructuraResultado(EXENCIONESBONIFICACIONES.REPLY reply, String codigo, String descripcion, boolean esError){
		EXENCIONESBONIFICACIONES.REPLY.RESULTADO res= new EXENCIONESBONIFICACIONES.REPLY.RESULTADO();
		EXENCIONESBONIFICACIONES.REPLY.RESULTADO.ERROR err = new EXENCIONESBONIFICACIONES.REPLY.RESULTADO.ERROR();
		err.setCODIGO(codigo);
		err.setDESCRIPCION(descripcion);
		res.setERROR(err);
		res.setRETORNO(esError);
		reply.setRESULTADO(res);
	}
	
	/**
	 * Establece el resultado de error en consulta.
	 * @param reply
	 */
	public void estableceResultadoErrorConsulta(EXENCIONESBONIFICACIONES.REPLY reply){
		setEstructuraResultado(reply, CodigosTerminacion.getErrorExencionBonificacion(), true);
	}
	/**
	 * Establece el resultado de consulta correcta
	 * @param reply
	 */
	public void estableceResultadoCorrecto(EXENCIONESBONIFICACIONES.REPLY reply) {
		setEstructuraResultado(reply, CodigosTerminacion.getTerminacionCorrecta(), false);
	}

	/**
	 * Establece el resultado de error general en consulta
	 * @param reply
	 */
	public void estableceErrorGeneral(EXENCIONESBONIFICACIONES.REPLY reply){
		setEstructuraResultado(reply, CodigosTerminacion.getErrorGeneral(), false);
	}
	/** Establece la parte de resultado de la respuesta
	 * 
	 * @param reply
	 * @param codigo
	 * @param descripcion
	 * @param esError
	 */
	private void setEstructuraResultado(EXENCIONESBONIFICACIONES.REPLY reply,CodigosTerminacion error, boolean esError){
		setEstructuraResultado(reply, error.getCodigo(), error.getMensaje(), esError);
	}
	/**
	 * Obtiene la lista de exenciones o bonificaciones
	 * @param request
	 * @throws PlusvaliaException
	 */
	public EXENCIONESBONIFICACIONES.REPLY obtenerListaExenciones(EXENCIONESBONIFICACIONES.REQUEST request){
		final String ESUN="ESUN_ESTRUCTURA_UNIVERSAL";
		final String PORCENTAJE="C0";
		final String CONCEPTO="C1";
		final String DESCRIPCION="CG1";
		final String TIPO="C2";
		if (request==null){
			return null;
		}
		EXENCIONESBONIFICACIONES.REPLY reply= new EXENCIONESBONIFICACIONES.REPLY();
		reply.setCABECERA(request.getCABECERA());
		try {
			TLanzador lanzador= LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcListaExencionBonif(), pref.getEsquemaBD());
			//Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("1", ParamType.NUMERO);
			//Municipio
			//Pasan el municipio INE con código de control al final. 
			String municipio= request.getINEPROVINCIA() + request.getINEMUNICIPIO().substring(0, request.getINEMUNICIPIO().length()-1);
			//Fecha. ""= fecha actual (para simplificar la llamada)
			String fecha= "";

			proc.param(municipio,ParamType.NUMERO);
			proc.param(fecha, ParamType.FECHA,"DD/MM/YYYY");
			proc.param("P", ParamType.CADENA);
			String soapResponse= lanzador.ejecutar(proc);
			RespuestaLanzador response= new RespuestaLanzador(soapResponse);
			if (!response.esErronea()){
				int filas= response.getNumFilasEstructura(ESUN);
				for (int i=1;i<=filas;i++){
					String porcentaje=response.getValue(ESUN, i, PORCENTAJE);
					String concepto=response.getValue(ESUN, i, CONCEPTO);
					String descripcion=response.getValue(ESUN, i, DESCRIPCION);
					String tipo= response.getValue(ESUN, i, TIPO);
					if (tipo.length()>50){
						tipo= tipo.substring(0,50);
					}
					addBeneficio(reply, porcentaje, concepto, descripcion,tipo);
				}
				estableceResultadoCorrecto(reply);
			} else {
				estableceResultadoErrorConsulta(reply);
			}
		} catch (LanzadorException le){
			log.error("Error al obtener la list de exenciones: "+ le.getMessage(),le);
			estableceResultadoErrorConsulta(reply);
		} catch (Exception e){
			log.error("Error general en la operación de Consulta de exenciones y bonificaciones: "+ e.getMessage(),e);
			estableceErrorGeneral(reply);
		}
		return reply;
	}
}
