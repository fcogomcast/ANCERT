package es.tributasenasturias.servicios.ibi.deuda.procesadores;


import es.tributasenasturias.servicios.ibi.deuda.MESSAGEREQUEST;
import es.tributasenasturias.servicios.ibi.deuda.MESSAGERESPONSE;
import es.tributasenasturias.servicios.ibi.deuda.CONSULTADEUDAS.REQUEST;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader;
import es.tributasenasturias.servicios.ibi.deuda.doin.DocumentoDoin;
import es.tributasenasturias.servicios.ibi.deuda.doin.DoinFactory;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.ImpresionException;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.MensajesException;
import es.tributasenasturias.servicios.ibi.deuda.impresion.ImpresionFactory;
import es.tributasenasturias.servicios.ibi.deuda.impresion.ReimpresionDocumento;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.ConstructorMensajeRespuestaConsulta;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesFactory;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion.MensajeAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.PreferenciasFactory;
import es.tributasenasturias.servicios.ibi.deuda.soap.SoapClientHandler;
import es.tributasenasturias.servicios.ibi.deuda.util.Constantes;
import es.tributasenasturias.services.lanzador.client.LanzadorException;
import es.tributasenasturias.services.lanzador.client.LanzadorFactory;
import es.tributasenasturias.services.lanzador.client.ParamType;
import es.tributasenasturias.services.lanzador.client.ProcedimientoAlmacenado;
import es.tributasenasturias.services.lanzador.client.TLanzador;
import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;
import es.tributasenasturias.utils.log.Logger;


public class ConsultaDeudaProcesador implements InterfazDeudaIBI,IContextReader {

	CallContext context;
	Logger log;
	String sesion;
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context = ctx;
	}

	/**
	 * No debería llamarse nunca directamente.
	 */
	protected ConsultaDeudaProcesador() {
	}
	/**
	 * Constructor que acerta un contexto de llamada donde podrán pasarse datos acerca de esta ejecución
	 * de servicio.
	 * @param ctx Context de llamada {@link CallContext}
	 */
	protected ConsultaDeudaProcesador(CallContext ctx)
	{
		context=ctx;
		log = (Logger)context.get(CallContextConstants.LOG);
		sesion = (String) context.get(CallContextConstants.IDSESION);
	}
	
	@Override
	public MESSAGERESPONSE process(MESSAGEREQUEST partRequest) throws IBIException{
		MESSAGERESPONSE msgresp;
		log.info("**Entrada al servicio de consulta.");
		Preferencias pref= PreferenciasFactory.getPreferenciasContexto(context);
		//Validación de entrada, aún no hecho.
		//Al llegar a este punto deberíamos saber que existe el id de comunicación.
		String idComunicacion = String.valueOf(partRequest.getCONSULTADEUDAS().getREQUEST().getCABECERA().getIDCOMUNICACION());
		String codOper = String.valueOf(partRequest.getCONSULTADEUDAS().getREQUEST().getCABECERA().getCODOPER());
		log.info ("1.Realizamos consulta de datos en BD.");
		RespuestaLanzador respuestaBD=getDatosConsulta (pref, partRequest);
		//Comprobamos qué código de resultado nos ha devuelto.
		String resultadoBD = recuperaResultadoBD (respuestaBD);
		try
		{
			MensajesAplicacion men = MensajesFactory.getMensajesAplicacionFromContext(context);
			//Comprobamos el mapeado de mensaje. Si se trata de un error, lo tratamos, si no, continuamos.
			MensajeAplicacion mensajeDevuelto = men.getMapeoMensaje(Constantes.seccionBD,resultadoBD);
			if (!mensajeDevuelto.isError()) //Todo OK. Valor fijo, no está mapeado.
			{
				log.info ("2.Consulta correcta, reimprimimos el documento.");
				//Llamada a la reimpresión del documento.
				String reimpresion="";
				reimpresion = getDocumentoReimpreso(respuestaBD,context);
				if (reimpresion==null || "".equals(reimpresion))
				{
					log.info ("3.Reimpresión incorrecta.");
					ConstructorMensajeRespuestaConsulta conResp = MensajesFactory.newConstructorMensajeRespuestaConsulta();
					MensajeAplicacion menError = men.getMensaje(Constantes.idMensajeDefecto);
					msgresp = conResp.construirMensajeError(menError.getCodMensaje(),menError.getTextoMensaje(), idComunicacion, codOper);
				}
				else
				{
					//Alta del documento.
					log.info ("3.Reimpresión correcta, alta de documento.");
					String resultadoAlta=altaDocumento(reimpresion, respuestaBD, context);
					mensajeDevuelto = men.getMapeoMensaje(Constantes.seccionDoin, resultadoAlta);
					if (!mensajeDevuelto.isError())
					{
						log.info ("4.Construcción del mensaje de salida.");
						ConstructorMensajeRespuestaConsulta conResp = MensajesFactory.newConstructorMensajeRespuestaConsulta();
						msgresp = conResp.construirMensajeRespuesta(respuestaBD, reimpresion, idComunicacion, codOper);
					}
					else
					{
						log.info ("4.Alta de documento errónea, construcción de mensaje de salida.");
						ConstructorMensajeRespuestaConsulta conResp = MensajesFactory.newConstructorMensajeRespuestaConsulta();
						msgresp = conResp.construirMensajeError(mensajeDevuelto.getCodMensaje(), mensajeDevuelto.getTextoMensaje(), idComunicacion, codOper);
					}
				}
			}
			else 
			{
				log.info ("2. Consulta incorrecta, construcción de mensaje de salida.");
				ConstructorMensajeRespuestaConsulta conResp = MensajesFactory.newConstructorMensajeRespuestaConsulta();
				msgresp = conResp.construirMensajeError(mensajeDevuelto.getCodMensaje(), mensajeDevuelto.getTextoMensaje(), idComunicacion, codOper);
			}
		}
		catch (ImpresionException e) {
			throw new IBIException (e.getMessage(),e);
		}
		catch (MensajesException e) {
			throw new IBIException (e.getMessage(),e);
		}
		log.info ("**Salida del servicio de consulta.");
		return msgresp;
	}
	/**
	 * Devuelve los datos de consulta de IBI y los necesarios para los pasos siguientes de proceso.
	 * @param pref {@link Preferencias}
	 * @param partRequest Parámetros de entrada.
	 * @return Objeto para procesar los datos devueltos por la consulta {@link RespuestaLanzador}
	 * @throws IBIException
	 */
	private RespuestaLanzador getDatosConsulta(Preferencias pref, MESSAGEREQUEST partRequest) throws IBIException
	{
		RespuestaLanzador respuesta;
		TLanzador lanzador;
		try {
			REQUEST request = partRequest.getCONSULTADEUDAS().getREQUEST();
			lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(),new SoapClientHandler(sesion));
			ProcedimientoAlmacenado pa= new ProcedimientoAlmacenado(pref.getProcAlmacenadoDatosConsulta(),pref.getEsquemaBD());
			pa.param("1", ParamType.NUMERO);
			pa.param("1", ParamType.NUMERO);
			pa.param("USU_WEB_SAC", ParamType.CADENA);
			pa.param("33", ParamType.CADENA);
			pa.param(request.getPARAMETROSCONSULTA().getREFERENCIACATASTRAL(), ParamType.CADENA);
			pa.param(request.getPARAMETROSCONSULTA().getCODIGOMUNICIPIO(), ParamType.NUMERO);
			pa.param(request.getIDNOTARIO().getCODIGONOTARIO(), ParamType.CADENA);
			String resp=lanzador.ejecutar(pa);
			respuesta=new RespuestaLanzador(resp); 
			if (respuesta.esErronea())
			{
				throw new IBIException ("La ejecución del procedimiento almacenado ha fallado:" + respuesta.getTextoError());
			}
			return respuesta;
		} catch (LanzadorException e) {
			throw new IBIException ("Error en la comunicación con base de datos:" + e.getMessage());
		}
	}
	/**
	 * Recupera el documento que se indica en la respuesta de base de datos, como reimpresión
	 * Además le pasa un código de verificación para 
	 * @param datosBD Datos devueltos de la base de datos.
	 * @return Documento en base 64
	 * @throws ImpresionException
	 */
	private String getDocumentoReimpreso (RespuestaLanzador datosBD, CallContext context) throws ImpresionException
	{
		String doc;
		String idElemento = datosBD.getValue("CANU_CADENAS_NUMEROS", 1, "NUME1_CANU");
		String tipo = datosBD.getValue("CANU_CADENAS_NUMEROS", 1, "STRING1_CANU");
		String numeroSerie = datosBD.getValue("CANU_CADENAS_NUMEROS", 1, "STRING3_CANU");
		String mac = datosBD.getValue("CANU_CADENAS_NUMEROS", 1, "STRING4_CANU");
		String letraCodVerificacion = datosBD.getValue("CANU_CADENAS_NUMEROS", 1, "STRING5_CANU");
		String codigoVerificacion ="";
		if (mac!=null && !"".equals(mac))
		{
			 codigoVerificacion=letraCodVerificacion+numeroSerie+"-"+mac;
		}
		ReimpresionDocumento rimp = ImpresionFactory.getReimpresion(context); 
		doc = rimp.getReimpresion(idElemento, tipo, codigoVerificacion);
		return doc;
	}
	
	private String recuperaResultadoBD(RespuestaLanzador datosBD)
	{
		String resultado = datosBD.getValue("CADE_CADENA", 1, "STRING_CADE");
		return resultado;
	}
	
	private String altaDocumento(String doc, RespuestaLanzador datosDocumento,CallContext context)
	{
		DocumentoDoin altaDoc = DoinFactory.newDocumentoDoin(context);
		String nifNotario = datosDocumento.getValue("CANU_CADENAS_NUMEROS", 1, "STRING2_CANU");
		String nif = datosDocumento.getValue("PEDB_PERSONA_DATOS_BASICOS", 1, "NIF_PEDB");
		String numeroSerie = datosDocumento.getValue("CANU_CADENAS_NUMEROS", 1, "STRING3_CANU");
		String mac = datosDocumento.getValue("CANU_CADENAS_NUMEROS", 1, "STRING4_CANU");
		String letraCodVerificacion = datosDocumento.getValue("CANU_CADENAS_NUMEROS", 1, "STRING5_CANU");
		return altaDoc.altaDocumento(numeroSerie, letraCodVerificacion, mac, nif, nifNotario, doc);
	}
}
