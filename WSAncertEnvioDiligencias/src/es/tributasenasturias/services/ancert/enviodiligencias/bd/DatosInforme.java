package es.tributasenasturias.services.ancert.enviodiligencias.bd;



import org.w3c.dom.Document;
import org.w3c.dom.Element;


import es.tributasenasturias.services.ancert.enviodiligencias.SystemException;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.Utils;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.DocumentoException;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.SHAUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.XMLUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.lanzador.LanzaPL;
import es.tributasenasturias.services.lanzador.LanzaPLService;

/**
 * Clase que recupera los datos necesarios para el informe. Esta clase se utilizará aunque no se 
 * genere el informe sino que se recupere de base de datos, porque los datos que contiene el informe
 * se necesitan siempre para generar el envío hacia ANCERT.
 * @author crubencvs
 *
 */
public class DatosInforme implements IContextReader{
	
	private JustificantePresentacionDO datosJustificante;//Guarda los datos del justificante que se han recuperado.
	private DatosSolicitudDO datosSolicitud;
	private String codigoVerificacion;
	private boolean vacio;
	private CallContext context;
	/**
	 * Recupera la clave de consejería para generación del código de verificación.
	 * @return Clave de consejería
	 * @throws DatosException Si ocurre un error relacionado con la recuperación de datos.
	 * @throws SystemException Si ocurre un error técnico en la recuperación.
	 */
	private String recuperarClaveConsejerias() throws DatosException,SystemException{
		String respuesta="";
		try {
			Preferencias pref;
			try
			{
				pref=PreferenciasFactory.newInstance();
			}
			catch (PreferenciasException e)
			{
				throw new DatosException("Error al recuperar datos del Justificante:" + e.getMessage(),e);
			}
			ConversorParametrosLanzador cpl;
			cpl = new ConversorParametrosLanzador();

			cpl
					.setProcedimientoAlmacenado(pref.getPAClaveConsejeria());
			// conexion oracle
			cpl.setParametro("P", ConversorParametrosLanzador.TIPOS.String);

			LanzaPLService lanzaderaWS = new LanzaPLService();
			LanzaPL lanzaderaPort;
			lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();

			// enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
			// Cambiamos el endpoint
			bpr.getRequestContext().put(
					javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					pref.getEndPointLanzador());
			//Se añade el SoapClientLog
			Utils.setHandlerChain(bpr,context);
			respuesta = "";
			respuesta = lanzaderaPort.executePL(pref.getEsquemaBaseDatos(), cpl.codifica(), "", "", "", "");
			cpl.setResultado(respuesta);
			if (!cpl.getError().equals(""))
			{
				throw new DocumentoException ("Excepción de BD al recuperar la clave de consejería:" + cpl.getError());
			}
			if (respuesta.trim().equals(""))
			{
				throw new DocumentoException ("Excepción de BD al recuperar la clave de consejería. Se ha recibido una respuesta vacía.");
			}
		} catch (Exception e) {
			if (!(e instanceof DocumentoException) && !(e instanceof SystemException))
			{
				throw new SystemException("Excepcion generica al recuperar la clave de consejería: "
								+ e.getMessage(),e);
			}
		}
		return respuesta;
	}
	/**
	 * Recupera los datos del justificante de presentación, en forma de XML.
	 * @param numAutoliquidacion Número de autoliquidación para la que se quieren consultar datos.
	 * @return Cadena que contiene XML con los datos que se han recuperados del lanzador para esa petición.
	 * @throws DatosException 
	 */
	private String recuperarDatosJustificantePr(String numAutoliquidacion) throws DatosException,SystemException{
		String respuesta="";
		try {
			Preferencias pref;
			try
			{
				pref=PreferenciasFactory.newInstance();
			}
			catch (PreferenciasException e)
			{
				throw new DatosException("Error al recuperar datos del Justificante:" + e.getMessage(),e);
			}
			ConversorParametrosLanzador cpl;
			cpl = new ConversorParametrosLanzador();

			cpl
					.setProcedimientoAlmacenado(pref.getPADatInforme());
			// conexion
			cpl.setParametro("1", ConversorParametrosLanzador.TIPOS.Integer);
			// peticion
			cpl.setParametro("1", ConversorParametrosLanzador.TIPOS.Integer);
			// usuario
			cpl.setParametro("USU_WEB_SAC",ConversorParametrosLanzador.TIPOS.String);
			// organismo
			cpl.setParametro("33", ConversorParametrosLanzador.TIPOS.Integer);
			// codTactInforme
			cpl.setParametro("NA", ConversorParametrosLanzador.TIPOS.String);
			// numAutoliquidacion
			cpl.setParametro(numAutoliquidacion, ConversorParametrosLanzador.TIPOS.String);
			// conexion oracle
			cpl.setParametro("P", ConversorParametrosLanzador.TIPOS.String);

			LanzaPLService lanzaderaWS = new LanzaPLService();
			LanzaPL lanzaderaPort;
			lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();

			// enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
			// Cambiamos el endpoint
			bpr.getRequestContext().put(
					javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					pref.getEndPointLanzador());
			//Se añade el SoapClientLog
			Utils.setHandlerChain(bpr,context);
			respuesta = "";
			respuesta = lanzaderaPort.executePL(pref.getEsquemaBaseDatos(), cpl.codifica(), "", "", "", "");
			cpl.setResultado(respuesta);
			if (!cpl.getError().equals(""))
			{
				throw new DocumentoException ("Excepción de BD al recuperar los datos del documento de presentación:" + cpl.getError());
			}
			if (respuesta.trim().equals(""))
			{
				throw new DocumentoException ("Excepción de BD al recuperar los datos del documento de presentación. Se ha recibido una respuesta vacía.");
			}
		} catch (Exception e) {
			if (!(e instanceof DocumentoException) && !(e instanceof SystemException))
			{
				throw new SystemException("Excepcion generica al recuperar los datos del documento de presentación: "
								+ e.getMessage(),e);
			}
		}
		return respuesta;
	}
	/**
	 * Genera un código de verificación.
	 * @param valor Valor sobre el que se generará el código de verificación.
	 * @return Cadena con código de verificación.
	 * @throws Exception
	 */
	private String generarVerificacion(String valor) throws Exception{
		String resultado = SHAUtils.hex_hmac_sha1(recuperarClaveConsejerias(),
				valor);
		return resultado.substring(resultado.length() - 16, resultado
				.length());
	}
	
	private void rellenaEstructuras(String xmlLanzador, String numAutoliquidacion) throws SystemException
	{
		if (!xmlLanzador.equals(null)) {
			try {
				Document docRespuesta = (Document) XMLUtils.compilaXMLObject(
						xmlLanzador, null);
				Element[] rsCanu = XMLUtils.selectNodes(docRespuesta,
						"//estruc[@nombre='CANU_CADENAS_NUMEROS']/fila");
				if (rsCanu.length>0)
				{
					vacio = false;
					DatosExpedienteDO expediente=this.datosJustificante.getDatosExpediente();
					// Oficina de alta
					expediente.setOficinaAlta(XMLUtils.selectSingleNode(rsCanu[0],
							"STRING2_CANU").getTextContent());
					// Organismo
					expediente.setOrganismo(XMLUtils.selectSingleNode(rsCanu[0],
					"STRING4_CANU").getTextContent());
					// Nº Expediente
					expediente.setNumExpediente(XMLUtils.selectSingleNode(rsCanu[0],
							"STRING1_CANU").getTextContent());
					// Fecha de alta
					expediente.setFechaAlta(XMLUtils.selectSingleNode(rsCanu[0],
					"FECHA1_CANU").getTextContent());
					// Oficina de destino
					expediente.setOficinaDestino(XMLUtils.selectSingleNode(rsCanu[0],
					"STRING3_CANU").getTextContent());
					// Hora de alta
					expediente.setHoraAlta(XMLUtils.selectSingleNode(rsCanu[0],
					"FECHA2_CANU").getTextContent());
					// Fecha de presentación
					expediente.setFechaPresentacion(XMLUtils.selectSingleNode(rsCanu[0],
					"FECHA1_CANU").getTextContent());
					
					// Tipo/subtipo de expediente
					expediente.setTipoExpediente(XMLUtils.selectSingleNode(rsCanu[0],
					"STRING5_CANU").getTextContent());
					expediente.setSubtipoExpediente(XMLUtils.selectSingleNode(rsCanu[1],
					"STRING1_CANU").getTextContent());
					//Número de protocolo
					expediente.setNumeroProtocolo(XMLUtils.selectSingleNode(rsCanu[1],
					"STRING2_CANU").getTextContent());
					//Notario
					expediente.setApellidosNombreNotario(XMLUtils.selectSingleNode(
							rsCanu[1], "STRING3_CANU").getTextContent());
					//Tipo de documento
					expediente.setTipoDocumento(XMLUtils.selectSingleNode(rsCanu[1],
					"STRING5_CANU").getTextContent());
					//Dato específico
					expediente.setDatoEspecifico(XMLUtils.selectSingleNode(rsCanu[2], "FECHA3_CANU").getTextContent());
					//NRC
					expediente.setNrc(XMLUtils.selectSingleNode(rsCanu[2],"STRING4_CANU").getTextContent());
					//Modelo
					expediente.setModelo(XMLUtils.selectSingleNode(rsCanu[2],"FECHA2_CANU").getTextContent());
					//Fecha de documento
					expediente.setFechaDocumento(XMLUtils.selectSingleNode(
							rsCanu[1], "FECHA1_CANU").getTextContent());
					//Concepto
					//Puede existir o no, dependiendo del modelo.
					if (rsCanu.length>3)
					{
						expediente.setConcepto(XMLUtils.selectSingleNode(rsCanu[3],
						"STRING1_CANU").getTextContent());
					}
					//Número autoliquidación
					expediente.setNumeroAutoliquidacion(numAutoliquidacion);
					//Tipo de sujeción (Código)
					expediente.setCodTipoSujecion(XMLUtils.selectSingleNode(rsCanu[2], "STRING2_CANU").getTextContent());
					expediente.setTipoSujecion(XMLUtils.selectSingleNode(
								rsCanu[2], "STRING3_CANU").getTextContent());
					expediente.setImporteAutoliq(XMLUtils.selectSingleNode(
								rsCanu[2], "NUME1_CANU").getTextContent());
					//Comenzamos a tratar las personas.
					//*****************************************
					DatosPersonaDO persona;
					Element[] rsPedb = XMLUtils.selectNodes(docRespuesta,
							"//estruc[@nombre='PEDB_PERSONA_DATOS_BASICOS']/fila");
					for (int i = 0; i < rsPedb.length; i++) {
						persona = DatosFactory.newDatosPersonaDO();
						//Tipo.
						persona.setTipoPersona(XMLUtils.selectSingleNode(rsPedb[i], "AEAT_PEDB")
								.getTextContent());
						//Se sacan todos los datos, para ambas personas.
						//Aunque esto hace que el rendimiento pueda ser ligeramente inferior
						//(del orden de milisegundos), hace que la lógica sobre qué datos 
						//va a pintar el informe se queden allí.
						persona.setNombre(XMLUtils.selectSingleNode(
								rsPedb[i], "NOMBRE_PEDB").getTextContent());
						persona.setNif(XMLUtils.selectSingleNode(
								rsPedb[i], "NIF_PEDB").getTextContent());
						persona.setTelefono(XMLUtils.selectSingleNode(
								rsPedb[i], "TELEFONO_PEDB").getTextContent());
						persona.setSigla(XMLUtils.selectSingleNode(
								rsPedb[i], "SIGLA_PEDB").getTextContent());
						persona.setCalle(XMLUtils.selectSingleNode(rsPedb[i],
						"CALLE_PEDB").getTextContent());
						persona.setCodPostal(XMLUtils.selectSingleNode(rsPedb[i],
						"CP_PEDB").getTextContent());
						persona.setPoblacion(XMLUtils.selectSingleNode(rsPedb[i],
						"POBLACION_PEDB").getTextContent());
						persona.setProvincia(XMLUtils.selectSingleNode(rsPedb[i],
						"PROVINCIA_PEDB").getTextContent());
						this.datosJustificante.getPersonas().add(persona);
					}
					//Nos quedan los textos fijos.
	
					Element[] rsMemo = XMLUtils.selectNodes(docRespuesta,
							"//estruc[@nombre='MEMO_MEMO']/fila");
	
					this.datosJustificante.setTextoTitulo(XMLUtils.selectSingleNode(
							rsMemo[9], "STRING_MEMO").getTextContent());
	
					String texto = new String(XMLUtils.selectSingleNode(rsMemo[10],
							"STRING_MEMO").getTextContent()
							+ "\r\r");
	
					texto = texto
							+ XMLUtils.selectSingleNode(rsMemo[11], "STRING_MEMO")
									.getTextContent() + "\r\r";
					this.datosJustificante.setTextoFijo(texto);
					codigoVerificacion=generarVerificacion("C"
									+ numAutoliquidacion
									+ XMLUtils.selectSingleNode(rsPedb[0], "NIF_PEDB")
											.getTextContent());
				}
				else //No se han recuperado elementos
				{
					//Se marca.
					vacio=true;
				}
			} catch (Exception e) {
				throw new SystemException("Error al extraer datos para el informe justificante de presentacion: "
								+ e.getMessage(),e);
			}
		}
		
	}
	/**
	 * Recupera los datos de la solicitud relacionada con esta autoliquidación.
	 * @param numAutoliquidacion Número de autoliquidación para la que se quieren consultar datos.
	 * @return Cadena que contiene XML con los datos que se han recuperados del lanzador para esa petición.
	 * @throws DatosException 
	 */
	private DatosSolicitudDO recuperarDatosSolicitud(String numAutoliquidacion) throws DatosException,SystemException{
		DatosSolicitudDO solicitud=DatosFactory.newDatosSolicitudDO();
		String respuesta="";
		try {
			Preferencias pref;
			try
			{
				pref=PreferenciasFactory.newInstance();
			}
			catch (PreferenciasException e)
			{
				throw new DatosException("Error al recuperar datos de la solicitud, hay un problema en las preferencias:" + e.getMessage(),e);
			}
			ConversorParametrosLanzador cpl;
			cpl = new ConversorParametrosLanzador();

			cpl
					.setProcedimientoAlmacenado(pref.getPADatosSolicitud());
			// numAutoliquidacion
			cpl.setParametro(numAutoliquidacion, ConversorParametrosLanzador.TIPOS.String);
			// conexion oracle
			cpl.setParametro("P", ConversorParametrosLanzador.TIPOS.String);

			LanzaPLService lanzaderaWS = new LanzaPLService();
			LanzaPL lanzaderaPort;
			lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();

			// enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
			// Cambiamos el endpoint
			bpr.getRequestContext().put(
					javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					pref.getEndPointLanzador());
			//Se añade el SoapClientLog
			Utils.setHandlerChain(bpr,context);
			respuesta = "";
			respuesta = lanzaderaPort.executePL(pref.getEsquemaBaseDatos(), cpl.codifica(), "", "", "", "");
			cpl.setResultado(respuesta); //Esto también lo interpreta como XML, así que después 
			//usamos las funciones de recuperar nodo por XML.
			if (!cpl.getError().equals(""))
			{
				throw new DocumentoException ("Excepción de BD al recuperar los datos del documento de presentación:" + cpl.getError());
			}
			//Se recuperan los datos
			solicitud.setCodNotario(cpl.getNodoResultadoX("STRING1_CANU"));
			solicitud.setCodNotaria(cpl.getNodoResultadoX("STRING2_CANU"));
			solicitud.setNumProtocolo(cpl.getNodoResultadoX("STRING3_CANU"));
			solicitud.setProtocoloBis(cpl.getNodoResultadoX("FECHA1_CANU"));
			solicitud.setAnioAutorizacion(cpl.getNodoResultadoX("FECHA2_CANU"));
			String autorizada = cpl.getNodoResultadoX("FECHA3_CANU");
			solicitud.setAutorizadaEnvio(autorizada.equalsIgnoreCase("S") ? true:false);
		} catch (Exception e) {
			if (!(e instanceof DocumentoException))
			{
				throw new SystemException("Excepcion generica al recuperar los datos de la solicitud asociada a la autoliquidación: "
								+ e.getMessage(),e);
			}
		}
		return solicitud;
	}
	/**
	 * Genera estructuras de los datos relativos al informe para una autoliquidación concreta.
	 * Estos datos después se podrán recuperar con los "getter" de esta clase.
	 * @param numAutoliquidacion Número de autoliquidación para la que se van a traer datos.
	 * @throws DatosException
	 */
	public void recuperaDatosAutoliquidacion(String numAutoliquidacion) throws DatosException,SystemException
	{
		String xmlBDLlamada = recuperarDatosJustificantePr(numAutoliquidacion);
		rellenaEstructuras (xmlBDLlamada, numAutoliquidacion);
		this.datosSolicitud=recuperarDatosSolicitud(numAutoliquidacion);
		//Si no hay datos de solicitud, no se considera un error, se controlará más adelante
		if (datosSolicitud.getCodNotario().equals("") || datosSolicitud.getCodNotaria().equals("") ||
			datosSolicitud.getNumProtocolo().equals("") || datosSolicitud.getProtocoloBis().equals("") ||
			datosSolicitud.getAnioAutorizacion().equals(""))
		{
			//
			this.datosSolicitud=null;
			this.vacio=true;
		}
	}
	public JustificantePresentacionDO getDatosJustificante() {
		return datosJustificante;
	}
	public void setDatosJustificante(JustificantePresentacionDO datosJustificante) {
		this.datosJustificante = datosJustificante;
	}
	public String getCodigoVerificacion() {
		return codigoVerificacion;
	}
	public void setCodigoVerificacion(String codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
	}
	protected DatosInforme()
	{
		datosJustificante =  DatosFactory.newJustificantePresentacionDO();
	}
	public boolean isVacio() {
		return vacio;
	}
	public void setVacio(boolean vacio) {
		this.vacio = vacio;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
	public DatosSolicitudDO getDatosSolicitud() {
		return datosSolicitud;
	}
	public void setDatosSolicitud(DatosSolicitudDO datosSolicitud) {
		this.datosSolicitud = datosSolicitud;
	}
}
