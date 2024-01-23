package es.tributasenasturias.services.ancert.solicitudEscritura.bd;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.handler.SoapClientHandler;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.DatosException;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.SystemException;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.SolicitudDO;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogFactory;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.log.LogHelper;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias.PreferenciasFactory;

public class GestorSolicitudesBD implements IContextReader
{
	

	private final static String STRING_CADE = "STRING_CADE";
	private final static String STRING1_CANU = "STRING1_CANU";
	private final static String STRING2_CANU = "STRING2_CANU";
	private final static String STRING3_CANU = "STRING3_CANU";
	private final static String MEMO_C1 = "C1";
	private final static String ERRORNODE = "error";
	private final static String DUPLICADO ="DUPLICADO";
	private final static String IDSOLICITUD="IDSOLICITUD";
	
	
	private stpa.services.LanzaPLService lanzaderaWS; // Servicio Web
	private stpa.services.LanzaPL lanzaderaPort; // Port (operaciones) a las que se llamas
	private ConversorParametrosLanzador conversor;
	private CallContext context;
	private Preferencias preferencias;
	private String errorLlamada;
	@SuppressWarnings("unchecked")
	public void preparaConexion() throws SystemException
	{
		try
		{
			LogHelper log=LogFactory.getLogAplicacionContexto(context);
			//Preparamos las preferencias en función del contexto.
			preferencias=PreferenciasFactory.getPreferenciasContexto(context);
			if (preferencias==null)
			{
				throw new SystemException ("No se pueden recuperar las preferencias.");
			}
			String endPointLanzador=preferencias.getEndPointLanzador();
			lanzaderaWS = new stpa.services.LanzaPLService();
			if (!endPointLanzador.equals(""))
			{
				log.debug ("Se utiliza el endpoint de lanzadera: " + endPointLanzador);
				lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();
				javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; // enlazador de protocolo para el servicio.
				bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endPointLanzador); // Cambiamos el endpoint
				Binding bi = bpr.getBinding();
				List <Handler> handlerList = bi.getHandlerChain();
				if (handlerList == null)
				   handlerList = new ArrayList<Handler>();
				//El manejador de cliente enviará su log con el mismo id de sesión que el log de aplicación.
				handlerList.add(new SoapClientHandler(log.getSessionId(), preferencias));
				bi.setHandlerChain(handlerList);
			}
			else
			{
				lanzaderaPort =lanzaderaWS.getLanzaPLSoapPort();
				javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; // enlazador de protocolo para el servicio.
				Binding bi = bpr.getBinding();
				List <Handler> handlerList = bi.getHandlerChain();
				if (handlerList == null)
				   handlerList = new ArrayList<Handler>();
				//El manejador de cliente enviará su log con el mismo id de sesión que el log de aplicación.
				handlerList.add(new SoapClientHandler(log.getSessionId(), preferencias));
				bi.setHandlerChain(handlerList);
				log.debug ("Se utiliza el endpoint de lanzadera por defecto: " + bpr.getRequestContext().get (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
			}
			conversor = new ConversorParametrosLanzador();
		}
		catch (Exception ex)
		{
			if (!(ex instanceof SystemException))
			{
				throw new SystemException ("Problema al preparar la conexión." + ex.getMessage(),ex);
			}
		}
	}

	/**
	 * Devuelve un 'S' si la solicitud está duplicada o 'N' si no lo está.
	 * @param solicitud GestorSolicitudesBD de la solicitud.
	 * @return 'S' si la solicitud está duplicada, 'N' si no.
	 * @throws DatosException
	 */
	public Map<String,String> solicitudDuplicada(SolicitudDO solicitud) throws SystemException
	{
		conversor.setProcedimientoAlmacenado(preferencias.getPASolicitudDuplicada());
		conversor.setParametro(solicitud.getCodigoNotario(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getCodigoNotaria(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getNumProtocolo(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getProtocoloBis(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(Integer.toString(solicitud.getAnioAutorizacion()), ConversorParametrosLanzador.TIPOS.Integer);
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		try
		{
			String resultadoEjecutarPL = Ejecuta();
			//_log.info("Resultado: "+resultadoEjecutarPL);
			conversor.setResultado(resultadoEjecutarPL);
			this.setErrorLlamada(conversor.getNodoResultado(ERRORNODE));
			
			//Recogemos el resultado.
			String duplicado = conversor.getNodoResultado(STRING_CADE);
			String idSolicitud = conversor.getNodoResultado(STRING1_CANU);
			Map<String,String> resultado = new HashMap<String,String>();
			resultado.put("DUPLICADO", duplicado);
			resultado.put("IDSOLICITUD", idSolicitud);
			return resultado;
		}
		catch (RemoteException ex)
		{
			throw new SystemException ("Problema al comprobar si una solicitud está duplicada.",ex);
		}
	}
	/**
	 * Actualiza los datos de una solicitud.
	 * @param solicitud GestorSolicitudesBD de la solicitud.
	 * 
	 * @throws DatosException
	 */
	public void actualizarSolicitud(SolicitudDO solicitud) throws SystemException
	{
		conversor.setProcedimientoAlmacenado(preferencias.getPAActualizarSolicitud());
		conversor.setParametro(solicitud.getCodigoNotario(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getCodigoNotaria(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getNumProtocolo(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getProtocoloBis(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(Integer.toString(solicitud.getAnioAutorizacion()), ConversorParametrosLanzador.TIPOS.Integer);
		conversor.setParametro(solicitud.getEstadoSolicitud(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getResultadoSolicitud(), ConversorParametrosLanzador.TIPOS.String);
		//Autorizado Diligencias. No se pasa valor, en este punto aún no tiene sentido.
		conversor.setParametro("", ConversorParametrosLanzador.TIPOS.String);
		//22173. Se pasa el id de solicitud recibido en el alta, para que actualice sólo esa.
		//Antes había sólo una y no era necesario, ahora sí porque puede haber más de una en el estado que nos interesa.
		conversor.setParametro(solicitud.getIdSolicitud(), ConversorParametrosLanzador.TIPOS.Integer);
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		
		try
		{
			String resultadoEjecutarPL = Ejecuta();
			conversor.setResultado(resultadoEjecutarPL);
			this.setErrorLlamada(conversor.getNodoResultado(ERRORNODE));
			return;
		}
		catch (RemoteException ex)
		{
			throw new SystemException ("Problema al actualizar los datos de una solicitud.",ex);
		}
	}
	/**
	 * Realiza el alta de una solicitud, depositando los datos de la misma en la estructura que 
	 * se pasa como parámetro.
	 * Si la solicitud ya existía en base de datos, se termina de forma correcta  y se devuelve
	 * igualmente la estructura.
	 * @param solicitud GestorSolicitudesBD de la solicitud que se  devolverá.
	 * @return GestorSolicitudesBD de la solicitud actualizados
	 * @throws DatosException
	 */
	public String altaSolicitud(SolicitudDO solicitud) throws SystemException
	{
		conversor.setProcedimientoAlmacenado(preferencias.getPAAltaSolicitud());
		conversor.setParametro(solicitud.getCodigoNotario(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getCodigoNotaria(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getNumProtocolo(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getProtocoloBis(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(Integer.toString(solicitud.getAnioAutorizacion()), ConversorParametrosLanzador.TIPOS.Integer);
		conversor.setParametro(solicitud.getDestinatario(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getFinalidad(), ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro(solicitud.getIdOrigen(), ConversorParametrosLanzador.TIPOS.String); // 07/12/2011. Nuevo parámetro de id de origen
		// INI CRUBENCVS  38243  03/03/2020
		if (solicitud.getPersonaSolicitante()!=null) {
			conversor.setParametro(solicitud.getPersonaSolicitante().getNif(), ConversorParametrosLanzador.TIPOS.String);
			conversor.setParametro(solicitud.getPersonaSolicitante().getNombre(), ConversorParametrosLanzador.TIPOS.String);
			conversor.setParametro(solicitud.getPersonaSolicitante().getCargo(), ConversorParametrosLanzador.TIPOS.String);
		} else{
			conversor.setParametro("", ConversorParametrosLanzador.TIPOS.String);
			conversor.setParametro("", ConversorParametrosLanzador.TIPOS.String);
			conversor.setParametro("", ConversorParametrosLanzador.TIPOS.String);
		}
		conversor.setParametro(solicitud.getTipoProcedimiento(), ConversorParametrosLanzador.TIPOS.String);
		if (solicitud.getOtorgantes()!= null && solicitud.getOtorgantes().size()>0 && solicitud.getOtorgantes().get(0)!=null) {
			conversor.setParametro(solicitud.getOtorgantes().get(0).getNif(), ConversorParametrosLanzador.TIPOS.String);
			conversor.setParametro(solicitud.getOtorgantes().get(0).getNombre(), ConversorParametrosLanzador.TIPOS.String);
		} else {
			conversor.setParametro("", ConversorParametrosLanzador.TIPOS.String);
			conversor.setParametro("", ConversorParametrosLanzador.TIPOS.String);
		}
		conversor.setParametro(solicitud.getNumeroExpediente(), ConversorParametrosLanzador.TIPOS.String);
		// FIN CRUBENCVS  38243
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		
		try
		{
			String resultadoEjecutarPL = Ejecuta();
			//_log.info("Resultado: "+resultadoEjecutarPL);
			conversor.setResultado(resultadoEjecutarPL);
			this.setErrorLlamada(conversor.getNodoResultado(ERRORNODE));
			
			//Recogemos el resultado.
			solicitud.setEstadoSolicitud(conversor.getNodoResultado(STRING2_CANU));
			solicitud.setIdSolicitud(conversor.getNodoResultado(STRING3_CANU));
			return conversor.getNodoResultado(STRING1_CANU);
		}
		catch (RemoteException ex)
		{
			throw new SystemException ("Problema al dar de alta la solicitud en la base de datos.",ex);
		}
	}
	/**
	 * Recupera la plantilla de solicitud.
	 * @return Plantilla de solicitud en forma de cadena.
	 * @throws DatosException
	 */
	public String recuperaPlantillaSolicitud() throws SystemException {
		conversor.setProcedimientoAlmacenado(preferencias
				.getPAObtenerPlantilla());
		conversor.setParametro(preferencias.getNombrePlantillaSolicitud(),
				ConversorParametrosLanzador.TIPOS.String);
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);

		try {
			String resultadoEjecutarPL = Ejecuta();
			conversor.setResultado(resultadoEjecutarPL);
			this.setErrorLlamada(conversor.getNodoResultado(ERRORNODE));
			// Recogemos la plantilla
			String plantilla = conversor.getNodoResultado(MEMO_C1);
			plantilla = es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils
					.unescapeHTML(plantilla);
			return plantilla;
		} catch (RemoteException ex) {
			throw new SystemException(
					"Problema al recupera la plantilla de base de datos."
							+ ex.getMessage(), ex);
		}
	}
	private String Ejecuta() throws RemoteException
	{
		//_log.info("Ejecutando llamada al servicio lanzadera para acceso a base de datos: "+conversor.Codifica());
		return lanzaderaPort.executePL(
				preferencias.getEsquemaBaseDatos(),
				conversor.Codifica(),
				"", "", "", "");
	}
	/**
	 * Método que devuelve el error en la ultima llamada a un procedimiento de esta clase.
	 * Los errores en llamada son devueltos en formato XML, este procedimiento devuelve el error
	 * extraído en formato de cadena.
	 * @return
	 */
	public String getErrorLlamada() {
		return errorLlamada;
	}

	private void  setErrorLlamada(String errorLlamada) {
		this.errorLlamada = errorLlamada;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}

	public String getParamDuplicado() {
		return DUPLICADO;
	}

	public String getIDSOLICITUD() {
		return IDSOLICITUD;
	}
}

