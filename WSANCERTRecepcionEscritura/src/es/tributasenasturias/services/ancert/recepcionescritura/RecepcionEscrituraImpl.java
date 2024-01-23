package es.tributasenasturias.services.ancert.recepcionescritura;




import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextConstants;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContextManager;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.XMLDOMDocumentException;
import es.tributasenasturias.services.ancert.recepcionescritura.factory.DomainObjectsFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.ConversorMensajesEntradaSalida;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.GestorDenegacion;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.InsercionEscrituraBuilder;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.ServiceResponseBuilder;
import es.tributasenasturias.services.ancert.recepcionescritura.objetos.InsercionEscrituraBuilder.InsercionBuilderResultado;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.seguridad.CertificadoValidator;
import es.tributasenasturias.services.ancert.recepcionescritura.seguridad.FirmaValidator;
import es.tributasenasturias.services.ancert.recepcionescritura.seguridad.SeguridadFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.types.ENVIOESCRITURAS;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.General;
import es.tributasenasturias.services.ancert.recepcionescritura.validacion.ValidacionException;
import es.tributasenasturias.services.ancert.recepcionescritura.validacion.ValidadorEsquema;

/**
 * Implementaci�n de la funcionalidad de env�o de escritura.
 * @author crubencvs
 *
 */
public class RecepcionEscrituraImpl implements IContextReader{

	
	CallContext context;
	Preferencias pr;
	private String idSesion;
	public void setIdSesion(String idSesion)
	{
		this.idSesion=idSesion;
	}
	public String envioEscritura (String xml)
	{
		LogHelper log=null;
		log=LogFactory.newApplicationLog(idSesion);
		//El contexto de llamada se utilizar� para pasar los datos que se necesitan en cada clase creada,
		//como el id de log. Si no, habr�a que pasar cada uno, de esta manera se encapsulan en un
		//" contexto".
		context= CallContextManager.newCallContext();
		context.setItem(CallContextConstants.IDSESION, idSesion);
		try
		{
			pr=PreferenciasFactory.newInstance();
			context.setItem(CallContextConstants.PREFERENCIAS, pr);
		}
		catch (Exception ex) //Improbable, pero no imposible.
		{
			log.error("Imposible devolver una respuesta en el servicio debido a una excepci�n en la carga de preferencias: "+ex.getMessage());
			log.trace(ex.getStackTrace());
			return null;
		}
		//Para que los objetos se creen teniendo en cuenta el contexto, se ha creado una clase
		//que crea los diferentes objetos teniendo en cuenta el contexto (para pasar el id de log, por ahora).
		// 
		DomainObjectsFactory factory=new DomainObjectsFactory();
		//Objeto que permitir� construir el mensaje de respuesta del servicio teniendo en cuenta operaciones
		//sobre �l, como firmarlo.
		ServiceResponseBuilder srb = factory.newServiceResponseBuilder(context);
		String fecha=General.getTime();
		try
		{
			log.info ("==============INICIO===============");
			log.info("0.Inicio del proceso de la petici�n con sesi�n:" + log.getSessionId()+" en fecha:" + fecha);
			ENVIOESCRITURAS env=null;
			log.info("1. Recupera el mensaje de entrada del servicio");
			ConversorMensajesEntradaSalida envc=factory.newConversorMensajes();
			env=envc.recuperaMensajeEntrada(xml);
			log.info ("2.Validaci�n de la seguridad de mensaje");
			FirmaValidator firmVal= SeguridadFactory.newFirmaValidator(context);
			if (firmVal.firmaValida(xml))
			{
				log.info("2.1. Firma v�lida.");
				CertificadoValidator certVal = SeguridadFactory.newCertificadoValidator(context);
				if (!certVal.tienePermisos(xml))
				{
					log.info("2.2. El certificado del mensaje no es v�lido.");
					srb.setErrorSeguridad();
					srb.construirCabecera();
					return srb.getRespuesta();
				}
			}
			else
			{
				log.info("2.1. La firma del mensaje de entrada no es v�lida");
				srb.construirCabecera();
				srb.setErrorSeguridad();
				return srb.getRespuesta();
			}
			//Se valida. La validaci�n a�ade tiempo de proceso y memoria, bastante adem�s si no fuese correcto.
			//Si el rendimiento no fuese bueno, se podr�a eliminar si estamos seguros de que 
			//nunca van a enviarnos un xml que no cumpla el esquema.
			log.info("3.Se procede a validar el mensaje contra el esquema");
			try{
				ValidadorEsquema valEsquema= factory.newValidadorEsquema(context);
				valEsquema.validar(env);
			}
			catch (ValidacionException e)
			{
				log.error("El mensaje de entrada no es v�lido. Revise el log para obtener m�s informaci�n:"+e.getMessage());
				srb.construirCabecera();
				srb.setXMLInvalido();
				return srb.getRespuesta();
			}
			catch (SystemException e)
			{
				//Otro tipo de problemas
				log.error("No se ha podido validar el XML de entrada:"+ e.getMessage());
				log.trace(e.getStackTrace());
				srb.construirCabecera();
				srb.setErrorGeneral();
				return srb.getRespuesta();
			}
			log.info("3.1.Final de validaci�n del mensaje contra el esquema. El mensaje es correcto");
			//Se podr�an validar m�s datos, como los de cabecera o los de ID_ESCRITURA. 
			//Por el momento, suponemos que lo que nos env�an desde ANCERT va a tener alg�n sentido
			//... c�digos de aplicaci�n, c�digo de emisor-receptor... lo �nico que podr�a pasar
			//es que por confusi�n nos env�en una escritura que no nos corresponde.
			// CRUBENCVS  38243. Puede que el notario haya denegado la escritura.
			if (env.getREQUEST().getDENEGARSOLICITUD() == null && 
					env.getREQUEST().getDOCUMENTACION()!=null){ 
				//Guardamos la escritura en la base de datos.
				log.info("4.Se procede a insertar la escritura");
				InsercionEscrituraBuilder insBuilder= factory.newInsercionEscrituraBuilder(context);
				insBuilder.setDatosEscritura(env.getREQUEST().getIDESCRITURA());
				//Se indica si se ha autorizado el env�o de diligencias.
				if (env.getREQUEST().isAUTORIZACIONENVIODILIGENCIA()!=null)
				{
					insBuilder.setAutorizadoEnvioDiligencia(env.getREQUEST().isAUTORIZACIONENVIODILIGENCIA());
				}
				else
				{
					insBuilder.setAutorizadoEnvioDiligencia(false);
				}
				//Se inserta la escritura.
				insBuilder.insertarEscritura(env.getREQUEST().getDOCUMENTACION());
				log.info("5.Se procede a recuperar el resultado de la inserci�n.");
				InsercionBuilderResultado res=insBuilder.getResultado();
				//Seg�n el resultado, decimos OK o no.
				switch (res.getResultado())
				{
				case INSERTADO:
					log.info("5.1.La escritura se ha insertado.");
					srb.setResultadoOK();
					break;
				case DUPLICADO:
					log.info("5.2.La escritura estaba duplicada.");
					srb.setResultadoOK();
					break;
				case FALTAN_DATOS:
					log.info("5.3.Faltan datos obligatorios de la escritura.");
					srb.setFaltanDatos();
					break;
				case NO_INSERTADO:
					log.info("5.4.No se ha insertado la escritura.");
					srb.setErrorAlta();
					break;
				case ERROR_INSERCION:
					srb.setErrorAlta();
					if (res.getError()!=null)
					{
						log.error("Error en la inserci�n de la escritura:"+ res.getError().getMessage());
						log.trace(res.getError().getStackTrace());
					}
					break;
				case ESCRITURA_VACIA:
					log.info("5.5.El contenido est� vac�o.");
					srb.setEscrituraVacia();
					break;
				default:
					srb.setErrorGeneral();
				}
				
				//A�adimos el n�mero de presentaci�n.
				srb.setNumeroPresentacion(insBuilder.getNumeroPresentacion());
				insBuilder=null;
			} else if (env.getREQUEST().getDENEGARSOLICITUD() != null && 
					     env.getREQUEST().getDOCUMENTACION()==null){ 
				log.info("4.Se procede a actualizar el estado de solicitud con la denegaci�n de la escritura");
				String motivo = env.getREQUEST().getDENEGARSOLICITUD().getMOTIVO();
				GestorDenegacion g = new GestorDenegacion(this.context);
			    GestorDenegacion.Resultado resDeneg= g.gestionarDenegacion(env.getREQUEST().getIDESCRITURA(), motivo);
			    if (resDeneg.equals(GestorDenegacion.Resultado.OK)){
			    	log.info("4.1. Se ha marcado la solicitud como denegada.");
			    	srb.setResultadoOK();
			    } else if (resDeneg.equals(GestorDenegacion.Resultado.FALTAN_DATOS)){
			    	log.info("4.1. Falta datos identificativos de la escritura.");
			    	srb.setFaltanDatos();
			    } else if (resDeneg.equals(GestorDenegacion.Resultado.NO_SOLICITUD)){
			    	log.info("4.1. No hay solicitud previa que denegar.");
			    	srb.setErrorNoSolicitudDenegacion();
			    } else if (resDeneg.equals(GestorDenegacion.Resultado.FALTAN_DATOS)) {
			    	log.info("4.1. Error en la denegaci�n.");
			    	srb.setErrorGeneral();
			    }
			} else { //Si no hay ni informaci�n de por qu� se ha denegado, ni documento, es un error.
				//Deber�a haberlo capturado la validaci�n de esquema, pero si no lo hizo, devuelvo en todo caso su mismo error
				log.error("El mensaje de entrada no es v�lido, no hay denegaci�n de documento ni documento o hay ambos.");
				srb.setXMLInvalido();
				return srb.getRespuesta();
			}
			//A�adimos la cabecera a la respuesta.
			srb.construirCabecera();
			//srb.setResultadoOK();
			return srb.getRespuesta();
		}
		catch (Exception e)
		{
			//Capturamos el resto de exceptiones.
			log.error("Error inesperado: "+ e.getMessage());
			log.trace(e.getStackTrace());
			try
			{
			srb.setErrorGeneral();
			return srb.getRespuesta();
			}
			catch (SystemException ex)
			{
				log.error("Imposible devolver una respuesta en el servicio debido a una excepci�n controlada pero no esperada: "+ex.getMessage());
				log.trace(ex.getStackTrace());
				return null;
			}
			catch (XMLDOMDocumentException ex)
			{
				log.error("Imposible devolver una respuesta en el servicio debido a la construcci�n del mensaje de respuesta: "+ex.getMessage());
				log.trace(ex.getStackTrace());
				return null;
			}
		}
		finally
		{
			log.info("6.Fin del proceso de la petici�n con sesi�n: " + log.getSessionId()+" iniciada el:" + fecha);
			log.info ("=============FIN================");
		}
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
		
	}
}
