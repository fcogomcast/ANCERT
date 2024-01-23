/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.comunicacion;

import es.tributasenasturias.services.ancert.enviodiligencias.ObjectFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.ResultadoType;

/** Constructor de estructuras de resultado, para devolver tipos de resultado.
 * @author crubencvs
 *
 */
public class ConstructorResultado {
	/**
	 * Crea un objeto de tipo resultado con el c�digo, mensaje e indicaci�n de si es error que especifica el desarrollador
	 * @param codigo C�digo de resultado.
	 * @param mensaje Mensaje de resultado.
	 * @param esError Indicador de si es error o no.
	 * @return Objeto ResultadoType con los datos que se han pasado.
	 */
	private ResultadoType crearResultado(String codigo, String mensaje, boolean esError)
	{
		ObjectFactory fact = new ObjectFactory();
		ResultadoType re =  fact.createResultadoType();
		re.setCodigo(codigo);
		re.setDescripcion(mensaje);
		re.setEsError(esError);
		return re;
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el env�o de diligencia se ha hecho de forma correcta.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoOk()
	{
		return crearResultado ("00000","Env�o realizado correctamente",false);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el env�o de diligencia no se ha podido hacer porque no se ha enviado un n�mero de autoliquidaci�n.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoAutoliquidacionVacia()
	{
		return crearResultado ("00002","El n�mero de autoliquidaci�n recibido est� vac�o.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el env�o de diligencia no se ha podido hacer porque no se encuentran datos para la autoliquidaci�n.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoAutoliquidacionNoExiste()
	{
		return crearResultado ("00003","No se encuentran datos para la autoliquidaci�n",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que ha habido un error t�cnico.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorTecnico()
	{
		return crearResultado ("00004","Error t�cnico. Por favor, p�ngase en contacto con el soporte del servicio.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que ha habido un error t�cnico.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorRecuperacionDatos()
	{
		return crearResultado ("00005","Error t�cnico en la recuperaci�n de datos del informe desde la base de datos.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el XML enviado es inv�lido.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorXMLInvalido()
	{
		return crearResultado ("00006","El xml enviado al CGN es inv�lido.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que la firma enviada a CGN no es v�lida.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorFirmaNoValida()
	{
		return crearResultado ("00007","La firma enviada al CGN no es v�lida.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que .
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorFirmaVacia()
	{
		return crearResultado ("00008","No se ha enviado firma de mensaje al CGN.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el esquema enviado es inv�lido.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorEsquemaInvalido()
	{
		return crearResultado ("00009","El mensaje enviado a CGN no cumple esquema.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el notario enviado tiene valor vac�o.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorNotarioVacio()
	{
		return crearResultado ("00010","El notario enviado al CGN est� vac�o.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el protocolo enviado tiene valor vac�o.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorProtocoloVacio()
	{
		return crearResultado ("00011","El protocolo enviado al CGN est� vac�o.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el protocolo bis es incorrecto.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorProtocoloBis()
	{
		return crearResultado ("00012","El protocolo bis enviado es incorrecto.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el a�o de autorizaci�n es incorrecto.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorAnioAutorizacion()
	{
		return crearResultado ("00013","El a�o de autorizacion enviado a CGN es incorrecto.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el notario enviado tiene valor vac�o.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorConceptoIncorrecto()
	{
		return crearResultado ("00014","El concepto enviado a CGN es incorrecto.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que los datos enviados son incorrectos.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorDatosIncorrectos()
	{
		return crearResultado ("00015","Los datos enviados son incorrectos.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el notario es incorrecto.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorNotarioIncorrecto()
	{
		return crearResultado ("00016","El notario enviado al CGN es incorrecto.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que se ha producido un error desconocido.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorDesconocido()
	{
		return crearResultado ("00017","Se ha producido un error desconocido en el CGN con el mensaje enviado.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el mensaje recibido de ANCERT no sabemos qu� significa.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorRecibidoDesconocido()
	{
		return crearResultado ("00018","El c�digo de resultado recibido de CGN es desconocido.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el env�o de diligencia no se ha podido hacer porque no se encuentran datos para la autoliquidaci�n.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoNoAutorizadaEnvio()
	{
		return crearResultado ("00019","El Sujeto Pasivo no ha autorizado el env�o de diligencias de presentaci�n relacionadas con esa autoliquidaci�n .",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que el mensaje recibido de CGN no cumple las especificaciones de seguridad.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorSeguridad()
	{
		return crearResultado ("09998","El mensaje recibido de CGN no cumple la especificaci�n de seguridad.",true);
	}
	/**
	 * Devuelve un objeto ResultadoType que indica que ha habido un error inesperado.
	 * @return Objeto ResultadoType.
	 */
	public ResultadoType crearResultadoErrorInesperado()
	{
		return crearResultado ("09999","Error inesperado en la ejecuci�n del servicio",true);
	}
	
	protected ConstructorResultado()
	{
		super();
	}
	
}
