package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import es.tributasenasturias.services.ancert.enviodiligencias.ResultadoType;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.ComunicacionFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.ConstructorResultado;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.types.ENVIODILIGENCIA;
import es.tributasenasturias.services.ancert.enviodiligencias.types.RESULTADO;

/**
 * Traduce las respuestas de Ancert a diferentes estados que la aplicación puede entender. Después, la aplicación
 * puede decidir qué hacer con ellos.
 * @author crubencvs
 *
 */
public class TraductorRespuestas implements IContextReader{

	private TraductorRespuestas() {
		super();
	}
	private CallContext context;
	
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
	

	public final static Map<String, EstadosAncert> respuestas=new HashMap<String,EstadosAncert>();
	static
	{
		respuestas.put("100",EstadosAncert.XML_INVALIDO);
		respuestas.put ("101",EstadosAncert.SIN_FIRMA);
		respuestas.put ("102",EstadosAncert.FIRMA_NO_VALIDA);
		respuestas.put ("103",EstadosAncert.ESQUEMA_INVALIDO);
		respuestas.put ("201",EstadosAncert.NOTARIO_VACIO);
		respuestas.put ("202",EstadosAncert.PROTOCOLO_VACIO);
		respuestas.put ("203",EstadosAncert.PROTOCOLO_BIS_ERRONEO);
		respuestas.put ("204",EstadosAncert.ANIO_AUTORIZACION_VACIO);
		respuestas.put ("205",EstadosAncert.CONCEPTO_INCORRECTO);
		respuestas.put ("206",EstadosAncert.DATOS_INCORRECTOS);
		respuestas.put ("301",EstadosAncert.NOTARIO_INVALIDO);
		respuestas.put ("999",EstadosAncert.ERROR_DESCONOCIDO);
		
	}
	/**
	 * Constructor al que se pasa el contexto de llamada.
	 * @param context Contexto de la llamada
	 */
	protected TraductorRespuestas(CallContext context)
	{
		this();
		this.context=context;
	}
	public ResultadoType getResultado (Node respuestaAncert) throws MensajeriaException
	{
		ResultadoType res;
		ConstructorResultado consResult = ComunicacionFactory.newConstructorResultado();
		MensajeroAncert mensajero=ComunicacionExternaFactory.newMensajeroAncert(context);
		ENVIODILIGENCIA respuesta=mensajero.extraeMensajeDeRespuesta(respuestaAncert);
		RESULTADO resultado=respuesta.getREPLY().getRESULTADO();
		//Buscamos en la tabla el equivalente.
		if (!resultado.isTIPOERROR()) //No es error, lo consideramos OK
		{
			res= consResult.crearResultadoOk();
		}
		else if (respuestas.containsKey(resultado.getCODIGOERROR().trim()))
		{
			EstadosAncert estadoResultado=respuestas.get(resultado.getCODIGOERROR().trim());
			switch (estadoResultado)
			{
			case XML_INVALIDO:
				res=consResult.crearResultadoErrorXMLInvalido();
				break;
			case FIRMA_NO_VALIDA:
				res=consResult.crearResultadoErrorFirmaNoValida();
				break;
			case SIN_FIRMA:
				res=consResult.crearResultadoErrorFirmaVacia();
				break;
			case ESQUEMA_INVALIDO:
				res=consResult.crearResultadoErrorEsquemaInvalido();
				break;
			case NOTARIO_VACIO:
				res=consResult.crearResultadoErrorNotarioVacio();
				break;
			case PROTOCOLO_VACIO:
				res=consResult.crearResultadoErrorProtocoloVacio();
				break;
			case PROTOCOLO_BIS_ERRONEO:
				res=consResult.crearResultadoErrorProtocoloBis();
				break;
			case ANIO_AUTORIZACION_VACIO:
				res=consResult.crearResultadoErrorAnioAutorizacion();
				break;
			case CONCEPTO_INCORRECTO:
				res=consResult.crearResultadoErrorConceptoIncorrecto();
				break;
			case DATOS_INCORRECTOS:
				res=consResult.crearResultadoErrorDatosIncorrectos();
				break;
			case NOTARIO_INVALIDO:
				res=consResult.crearResultadoErrorNotarioIncorrecto();
				break;
			case ERROR_DESCONOCIDO:
				res=consResult.crearResultadoErrorDesconocido();
				break;
			default:
				res=consResult.crearResultadoErrorDesconocido();
			}
		}
		else
		{
			res=consResult.crearResultadoErrorRecibidoDesconocido();
		}
		return res;
	}
}
