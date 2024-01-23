/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.factory;

import es.tributasenasturias.services.ancert.recepcionescritura.RecepcionEscrituraImpl;
import es.tributasenasturias.services.ancert.recepcionescritura.bd.Datos;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextManager;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.ConversorMensajesEntradaSalida;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.EscrituraDO;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.InsercionEscrituraBuilder;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.InsertadorEscrituras;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.ProtocoloDO;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.RespuestaBuilder;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.ServiceResponseBuilder;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.FirmaHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.validacion.ValidadorEsquema;


/** Contiene m�todos de factor�a para cada elemento que necesita el servicio y est�n bajo
 *   es.tributasenasturias.services.ancert.recepcionescritura.
 * @author crubencvs
 *
 */
public class DomainObjectsFactory {
	/**
	 * Recupera el contexto, bien el que se le pasa por par�metro si existe, o uno nuevo que crea.
	 * @param context Contexto {@link CallContext} que se devolver� si no es nulo.
	 * @return
	 */
	private CallContext getContext(CallContext context)
	{
		CallContext con=null;
		if (context !=null)
		{
			con=context;
		}
		else
		{
			con = CallContextManager.newCallContext();
		}
		return con;
	}
	/**
	 * Devuelve un objeto {@link Datos} para conexi�n con base de datos. 
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n 
	 *   sobre la llamada particular.
	 * @return Objeto Datos
	 * @throws SystemException En caso de fallo al construir.
	 */
	public Datos newDatos(CallContext context) throws SystemException
	{
		Datos dat=new Datos(getContext(context));
		return dat;
	}
	/**
	 * Devuelve un objeto {@link InsercionEscrituraBuilder} para el manejo de la inserci�n de escritura..
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n sobre
	 *  la llamada particular.
	 * @return Object InsercionEscrituraBuilder
	 */
	public InsercionEscrituraBuilder newInsercionEscrituraBuilder(CallContext context)
	{
		InsercionEscrituraBuilder pr=null;
		pr=new InsercionEscrituraBuilder();
		pr.setCallContext(getContext(context));
		return pr;
	}
	/**
	 * Devuelve un objeto {@link RecepcionEscrituraImpl} para procesar la recepcion de escritura.
	 * Este m�todo deber�a llamarse desde el endpoint de servicio, para realizar la recepci�n.
	 * @param IdSesion El id de la sesi�n en que se ha creado este objeto
	 * @return Objeto RecepcionEscrituraImpl
	 */
	public RecepcionEscrituraImpl newRecepcionEscritura(String idSesion)
	{
		RecepcionEscrituraImpl r = new RecepcionEscrituraImpl();
		r.setIdSesion(idSesion);
		return r;
	}
	
	/**
	 * Devuelve un objeto FirmaHelper para procesar las  firmas.
	 * @param context Contexto de llamada desde el que se crear� este objeto. El contexto tendr� 
	 *  informaci�n de la llamada particular.
	 * @return instancia FirmaHelper
	 */
	public FirmaHelper newFirmaHelper(CallContext context)
	{
		FirmaHelper fih= new FirmaHelper();
		fih.setCallContext(getContext(context));
		return fih;
	}
	/**
	 * Devuelve un objeto {@link RespuestaBuilder} para construir la respuesta del servicio.
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n
	 *  de la llamada particular.
	 * @return Objeto RespuestaBuilder
	 * 
	 */
	public RespuestaBuilder newRespuestaBuilder(CallContext context)
	{
		RespuestaBuilder reb = new RespuestaBuilder();
		reb.setCallContext(getContext(context));
		return reb;
	}
	/**
	 * Devuelve un objeto {@link InsertadorEscrituras} para insertar la escritura.
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n
	 *  de la llamada particular.
	 * @return Objeto InsertadorEscrituras
	 * 
	 */
	public InsertadorEscrituras newInsertadorEscrituras(CallContext context)
	{
		InsertadorEscrituras obj = new InsertadorEscrituras();
		obj.setCallContext(getContext(context));
		return obj;
	}
	/**
	 * Devuelve un objeto {@link EscrituraDO} que permite almacenar datos de escritura.
	 * @return Objeto EscrituraDO
	 * 
	 */
	public EscrituraDO newEscrituraDO()
	{
		EscrituraDO obj = new EscrituraDO();
		return obj;
	}
	
	/**
	 * Devuelve un objeto {@link ProtocoloDO} que encapsula el protocolo del env�o.
	 * @return Objeto ProtocoloDO
	 * 
	 */
	public ProtocoloDO newProtocoloDO()
	{
		ProtocoloDO obj = new ProtocoloDO();
		return obj;
	}
	/**
	 * Devuelve un objeto {@link ValidadorEsquema} para validar el mensaje contra el esquema.
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n sobre
	 *  la llamada particular.
	 * @return Object ValidadorEsquema
	 */
	public ValidadorEsquema newValidadorEsquema(CallContext context)
	{
		ValidadorEsquema obj=null;
		obj=new ValidadorEsquema();
		obj.setCallContext(getContext(context));
		return obj;
	}
	/**
	 * Devuelve un objeto {@link ServiceResponseBuilder} para contruir la respuesta del servicio.
	 * @param context Contexto de llamada en el que se crear� este objeto. El contexto tendr� informaci�n sobre
	 *  la llamada particular.
	 * @return Object ServiceResponseBuilder
	 */
	public ServiceResponseBuilder newServiceResponseBuilder(CallContext context)
	{
		ServiceResponseBuilder obj=null;
		obj=new ServiceResponseBuilder(getContext(context));
		return obj;
	}
	/**
	 * Devuelve un objeto {@link ConversorMensajesEntradaSalida } para contruir la respuesta del servicio.
	 * @return ConversorMensajesEntradaSalida
	 */
	public ConversorMensajesEntradaSalida newConversorMensajes()
	{
		return new ConversorMensajesEntradaSalida();
	}
}
