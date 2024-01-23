package es.tributasenasturias.servicios.ibi.deuda.pago;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.tributasenasturias.services.lanzador.client.LanzadorException;
import es.tributasenasturias.services.lanzador.client.LanzadorFactory;
import es.tributasenasturias.services.lanzador.client.ParamType;
import es.tributasenasturias.services.lanzador.client.ProcedimientoAlmacenado;
import es.tributasenasturias.services.lanzador.client.TLanzador;
import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;
import es.tributasenasturias.servicios.ibi.deuda.ORDENPAGOType.PARAMETROSPAGO.DATOSBANCARIOS;
import es.tributasenasturias.servicios.ibi.deuda.PAGODEUDAS.REQUEST;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContextConstants;
import es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader;
import es.tributasenasturias.servicios.ibi.deuda.doin.DocumentoDoin;
import es.tributasenasturias.servicios.ibi.deuda.doin.DoinFactory;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.ImpresionException;
import es.tributasenasturias.servicios.ibi.deuda.impresion.DocumentosPago;
import es.tributasenasturias.servicios.ibi.deuda.impresion.ImpresionFactory;
import es.tributasenasturias.servicios.ibi.deuda.impresion.ReimpresionDocumento;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.mensajes.MensajesAplicacion.MensajeAplicacion;
import es.tributasenasturias.servicios.ibi.deuda.preferencias.Preferencias;
import es.tributasenasturias.servicios.ibi.deuda.soap.SoapClientHandler;
import es.tributasenasturias.servicios.ibi.deuda.util.Constantes;
import es.tributasenasturias.servicios.ibi.deuda.util.Utils;
import es.tributasenasturias.utils.log.Logger;

/**
 * Gestiona los estados de pago de IBI
 * @author crubencvs
 *
 */
public class GestorEstadoPagoIBI implements IContextReader{

	private CallContext context;
	private Preferencias pref;
	private Logger log;
	private String idSesion;
	private MensajesAplicacion mensajes;
	
	private boolean error;
	private String codError;
	private String textError;
	
	private String codNotario;
	private String refCatastral;
	private String municipio;
	private long importe;
	
	private String refEmision;
	private String identificacion;
	private String nifContribuyente;
	private String emisora;
	private String fechaPago;
	
	private String ccc;
	private String nifTitular;
	
	private long idRegistroBD;
	
	
	public enum EstadosPago {PAGO_INICIADO, GENERACION_DOCUMENTOS_INICIADA, FINALIZADO};

	private EstadosPago estadoPago;
	
	public class Documentos
	{
		private String justificantePago;
		private String certificadoNoDeuda;
		private String codVerifJustificante;
		/**
		 * @return the justificantePago
		 */
		public String getJustificantePago() {
			return justificantePago;
		}
		/**
		 * @param justificantePago the justificantePago to set
		 */
		public void setJustificantePago(String justificantePago) {
			this.justificantePago = justificantePago;
		}
		/**
		 * @return the certificadoNoDeuda
		 */
		public String getCertificadoNoDeuda() {
			return certificadoNoDeuda;
		}
		/**
		 * @param certificadoNoDeuda the certificadoNoDeuda to set
		 */
		public void setCertificadoNoDeuda(String certificadoNoDeuda) {
			this.certificadoNoDeuda = certificadoNoDeuda;
		}
		/**
		 * @return the codVerifJustificante
		 */
		public String getCodVerifJustificante() {
			return codVerifJustificante;
		}
		/**
		 * @param codVerifJustificante the codVerifJustificante to set
		 */
		public void setCodVerifJustificante(String codVerifJustificante) {
			this.codVerifJustificante = codVerifJustificante;
		}
		
	}
	private Documentos documentos;
	/**
	 * Devuelve el estado de petición de pago de IBI en la base de datos para un notario y objeto tributario
	 * @param codNotario Código de notario
	 * @param refCatastral Referencia catastral
	 * @param municipio Municipio
	 * @param importe Importe
	 * @return ResultadoEstadoProcesoPago
	 * @throws IBIException
	 */
	private ResultadoEstadoProcesoPago devolverEstadoPago (String codNotario, String refCatastral, String municipio, long importe) throws IBIException
	{
		RespuestaLanzador respuesta;
		TLanzador lanzador;
		try {
			lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(),new SoapClientHandler(this.idSesion));
			ProcedimientoAlmacenado pa= new ProcedimientoAlmacenado(pref.getProcAlmacenadoEstadoPago(),pref.getEsquemaBD());
			pa.param(codNotario, ParamType.CADENA);
			pa.param(refCatastral, ParamType.CADENA);
			pa.param(municipio, ParamType.NUMERO);
			pa.param(String.valueOf(importe), ParamType.NUMERO);
			String resp=lanzador.ejecutar(pa);
			respuesta=new RespuestaLanzador(resp); 
			if (respuesta.esErronea())
			{
				this.error = true;
				this.textError= respuesta.getTextoError();
				return null;
			}
			else
			{
				this.error= false;
				return ResultadoEstadoProcesoPago.newInstance(respuesta, context);
			}
		} catch (LanzadorException e) {
			throw new IBIException ("Error en la comunicación con base de datos al recuperar el estado de pago:" + e.getMessage());
		}
	}
	/**
	 * Genera un certificado de no deuda, uno de los documentos que han de generarse para la respuesta
	 * @return
	 * @throws IBIException
	 */
	private RespuestaLanzador generarCertNoDeuda() throws IBIException
	{
		RespuestaLanzador respuesta;
		TLanzador lanzador;
		try {
			lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(),new SoapClientHandler(this.idSesion));
			ProcedimientoAlmacenado pa= new ProcedimientoAlmacenado(pref.getProcCertNoDeuda(),pref.getEsquemaBD());
			pa.param("33", ParamType.CADENA);
			pa.param(this.codNotario, ParamType.CADENA);
			pa.param(this.refCatastral, ParamType.CADENA);
			pa.param(this.municipio, ParamType.CADENA);
			pa.param(this.fechaPago, ParamType.FECHA,"DD/MM/YYYY");
			pa.param(this.refEmision, ParamType.CADENA);
			pa.param("P", ParamType.CADENA);
			String resp=lanzador.ejecutar(pa);
			respuesta=new RespuestaLanzador(resp);
			if (respuesta.esErronea())
			{
				throw new IBIException ("La ejecución del certificado de no deuda ha fallado:" + respuesta.getTextoError());
			}
			return respuesta;
		} catch (LanzadorException e) {
			throw new IBIException ("Error en la comunicación con base de datos al generar el certificado de no deuda:" + e.getMessage());
		}
	}
	/**
	 * Recupera la reimpresión del documento de no deuda
	 * @param datos Datos de la generación del documento
	 * @return Documento en base 64
	 * @throws ImpresionException
	 */
	private String getDocumentoNodeudaReimpreso (ResultadoGeneracionCertNoDeuda datos) throws ImpresionException
	{
		String doc;
		String idElemento = datos.getDatosDocumento().getIdElemento();
		String tipo = datos.getDatosDocumento().getTipoElemento();
		String codigoVerificacion =datos.getDatosDocumento().getCodVerificacion();
		ReimpresionDocumento rimp = ImpresionFactory.getReimpresion(context); 
		doc = rimp.getReimpresion(idElemento, tipo, codigoVerificacion);
		return doc;
	}
	/**
	 * Realiza el alta de un documento en DOIN
	 * @param doc Contenido del documento en Base64
	 * @param datosDocumento Datos del documento
	 * @return Resultado del alta
	 */
	private String altaDocumento(String doc, RespuestaLanzador datosDocumento)
	{
		DocumentoDoin altaDoc = DoinFactory.newDocumentoDoin(context);
		String mac = datosDocumento.getValue("CANU_CADENAS_NUMEROS", 1, "STRING4_CANU");
		String letraCodVerificacion = datosDocumento.getValue("CANU_CADENAS_NUMEROS", 1, "FECHA1_CANU");
		return altaDoc.altaDocumento(this.refEmision, letraCodVerificacion, mac, this.nifContribuyente, this.nifTitular, doc);
	}
	/* (non-Javadoc)
	 * @see es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader#getCallContext()
	 */
	@Override
	public CallContext getCallContext() {
		return context;
	}

	/* (non-Javadoc)
	 * @see es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.IContextReader#setCallContext(es.tributasenasturias.servicios.ibi.deuda.contextoLlamadas.CallContext)
	 */
	@Override
	public void setCallContext(CallContext ctx) {
		this.context = ctx;
		
	}
	
	
	/**
	 * @return the codError
	 */
	public String getCodError() {
		return codError;
	}

	/**
	 * @param codError the codError to set
	 */
	public void setCodError(String codError) {
		this.codError = codError;
	}

	
	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(boolean error) {
		this.error = error;
	}

	/**
	 * @return the textError
	 */
	public String getTextError() {
		return textError;
	}

	/**
	 * @param textError the textError to set
	 */
	public void setTextError(String textError) {
		this.textError = textError;
	}

	/**
	 * @return the refEmision
	 */
	public String getRefEmision() {
		return refEmision;
	}

	/**
	 * @param refEmision the refEmision to set
	 */
	public void setRefEmision(String refEmision) {
		this.refEmision = refEmision;
	}

	/**
	 * @return the identificacion
	 */
	public String getIdentificacion() {
		return identificacion;
	}

	/**
	 * @param identificacion the identificacion to set
	 */
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	/**
	 * @return the nifContribuyente
	 */
	public String getNifContribuyente() {
		return nifContribuyente;
	}

	/**
	 * @param nifContribuyente the nifContribuyente to set
	 */
	public void setNifContribuyente(String nifContribuyente) {
		this.nifContribuyente = nifContribuyente;
	}

	/**
	 * @return the emisora
	 */
	public String getEmisora() {
		return emisora;
	}

	/**
	 * @param emisora the emisora to set
	 */
	public void setEmisora(String emisora) {
		this.emisora = emisora;
	}

	/**
	 * @return the estadoPago
	 */
	public EstadosPago getEstadoPago() {
		return estadoPago;
	}

	/**
	 * @param estadoPago the estadoPago to set
	 */
	public void setEstadoPago(EstadosPago estadoPago) {
		this.estadoPago = estadoPago;
	}

	/**
	 * @return the idRegistroBD
	 */
	public long getIdRegistroBD() {
		return idRegistroBD;
	}

	/**
	 * @param idRegistroBD the idRegistroBD to set
	 */
	public void setIdRegistroBD(long idRegistroBD) {
		this.idRegistroBD = idRegistroBD;
	}

	/**
	 * @return the fechaPago
	 */
	public String getFechaPago() {
		return fechaPago;
	}

	/**
	 * @param fechaPago the fechaPago to set
	 */
	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
	}

	/**
	 * @return the codNotario
	 */
	public String getCodNotario() {
		return codNotario;
	}

	/**
	 * @param codNotario the codNotario to set
	 */
	public void setCodNotario(String codNotario) {
		this.codNotario = codNotario;
	}

	/**
	 * @return the refCatastral
	 */
	public String getRefCatastral() {
		return refCatastral;
	}

	/**
	 * @param refCatastral the refCatastral to set
	 */
	public void setRefCatastral(String refCatastral) {
		this.refCatastral = refCatastral;
	}

	/**
	 * @return the importe
	 */
	public long getImporte() {
		return importe;
	}

	/**
	 * @param importe the importe to set
	 */
	public void setImporte(long importe) {
		this.importe = importe;
	}

	/**
	 * @return the municipio
	 */
	public String getMunicipio() {
		return municipio;
	}

	/**
	 * @param municipio the municipio to set
	 */
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	/**
	 * @return the ccc
	 */
	public String getCcc() {
		return ccc;
	}

	/**
	 * @param ccc the ccc to set
	 */
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	/**
	 * @return the documentos
	 */
	public Documentos getDocumentos() {
		return documentos;
	}
	/**
	 * @param documentos the documentos to set
	 */
	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}
	/**
	 * 
	 * @param context
	 * @param men
	 */
	private GestorEstadoPagoIBI(CallContext context, MensajesAplicacion men) {
		this.context = context;
		this.idSesion =(String) context.get(CallContextConstants.IDSESION);
		this.pref = (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
		this.log = (Logger) context.get(CallContextConstants.LOG);
		this.documentos = new Documentos();
		this.mensajes = men;
	}
	/**
	 * Genera una nueva instancia del objeto
	 * @param context
	 * @param men
	 * @return
	 */
	public static GestorEstadoPagoIBI newInstance(CallContext context, MensajesAplicacion men)
	{
		return new GestorEstadoPagoIBI(context, men);
	}
	/**
	 * Establece los valores del estado de la petición en función de la identificación del notario y objeto tributario
	 * @param codNotario Código de notario
	 * @param refCatastral Referencia catastral
	 * @param municipio Municipio
	 * @param importe Importe a pagar
	 * @throws IBIException
	 */
	public void getEstadoPeticion(REQUEST peticionPago) throws IBIException
	{
		this.importe= Utils.getImporte(peticionPago.getORDENPAGO().getPARAMETROSPAGO().getIMPORTEDEUDA());
		this.codNotario = peticionPago.getIDNOTARIO().getCODIGONOTARIO();
		this.refCatastral = peticionPago.getORDENPAGO().getPARAMETROSPAGO().getREFERENCIACATASTRAL();
		this.municipio = peticionPago.getORDENPAGO().getPARAMETROSPAGO().getCODIGOMUNICIPIO();
		ResultadoEstadoProcesoPago estado = devolverEstadoPago  (codNotario, refCatastral, municipio, importe);
		if (!estado.isError())
		{
			this.error = false;
			if (ResultadoEstadoProcesoPago.Estados.PAGO_INICIADO==estado.getEstado())
			{
				this.estadoPago=EstadosPago.PAGO_INICIADO;
				this.refEmision = estado.getDatosPago().getReferencia();
				this.identificacion=estado.getDatosPago().getIdentificacion();
				this.emisora = estado.getDatosPago().getEmisora();
				this.nifContribuyente= estado.getDatosPago().getNifSP();
				this.idRegistroBD = estado.getIdregistro();
				//Añadimos los datos del pago, porque los vamos a necesitar
				DATOSBANCARIOS datos= peticionPago.getORDENPAGO().getPARAMETROSPAGO().getDATOSBANCARIOS();
				this.ccc = datos.getENTIDAD() + datos.getOFICINA()+ datos.getDC() + datos.getCUENTA();
				this.nifTitular = datos.getNIFTITULAR();
			}
			else if (ResultadoEstadoProcesoPago.Estados.GENERACION_DOCUMENTOS==estado.getEstado())
			{
				this.estadoPago= EstadosPago.GENERACION_DOCUMENTOS_INICIADA;
				this.refEmision = estado.getDatosPago().getReferencia();
				this.nifContribuyente = estado.getDatosPago().getNifSP();
				this.fechaPago = estado.getDatosPago().getFechaPago();
				this.idRegistroBD = estado.getIdregistro();
				DATOSBANCARIOS datos= peticionPago.getORDENPAGO().getPARAMETROSPAGO().getDATOSBANCARIOS();
				this.nifTitular = datos.getNIFTITULAR();
			}
			else if (ResultadoEstadoProcesoPago.Estados.PROCESO_FINALIZADO==estado.getEstado())
			{
				this.estadoPago= EstadosPago.FINALIZADO;
				this.refEmision = estado.getDatosPago().getReferencia();
			}
		}
		else
		{
			this.error = true;
			this.codError = estado.getCodMensajeError();
			this.textError = estado.getMensajeError();
		}
	}
	
	/**
	 * Procesa el estado previamente cargado
	 * @throws IBIException
	 */
	public void procesarEstado() throws IBIException
	{
		if (this.error)
		{
			return;
		}
		if (EstadosPago.PAGO_INICIADO == this.estadoPago)
		{
			//Hemos de realizar el pago
			realizarPago();
			if (!error)
			{
				generarDocumentos();
			}
		}
		else if (EstadosPago.GENERACION_DOCUMENTOS_INICIADA == this.estadoPago)
		{
			generarDocumentos();
			
		}
		else if (EstadosPago.FINALIZADO == this.estadoPago)
		{
			recuperaDocumentosGenerados();
		}
	}

	//CRUBENCVS 12/01/2023. Para solucionar problemas con NIF de seis dígitos,
	//he de "desnormalizar" el NIF que sube de base de datos.
	//Hay que tener precaución con los CIF.
	//La lógica que seguiré será:
	//Si el primer carácter es un número, relleno con ceros por la izquierda
	//hasta 9 caracteres.
	//Si el primer carácter es una letra, que corresponderá a NIF de persona
	//física de las letras L,K o M, o a un NIF de persona jurídica,
	//ignoro la primera letra y el resto de la cadena la relleno con ceros
	//por la izquierda hasta 8 caracteres.
	private String normalizaNif(String nif){
		if (nif==null || "".equals(nif.trim())){
			return nif;
		}
		String nifFormateado= nif.trim().toUpperCase();

		char primerCaracter= nifFormateado.charAt(0);
			
		String nifNormalizado;
		String cadenaRellenar;
		if ((primerCaracter>='A' && primerCaracter<='Z')){
			//Los nif o nie tienen estas letras. Si no, devolvemos lo mismo que entra
			if ((primerCaracter >= 'K' && primerCaracter <='M')
				|| (primerCaracter >= 'X')){ 
				cadenaRellenar = nifFormateado.substring(1).trim();//Ignoro el primer carácter
				nifNormalizado= primerCaracter + lpad(cadenaRellenar,'0',8);
			} else {
				nifNormalizado=nif;
			}
		} else if (primerCaracter >='0' && primerCaracter<='9'){
			cadenaRellenar = nifFormateado;
			nifNormalizado = lpad(cadenaRellenar,'0',9);
		} else {
			//No sabemos tratarlo, se devuelve el mismo que a la entrada
			nifNormalizado= nif;
		}
		return nifNormalizado;
	}
	/**
	 * Completa la longitud de una cadena hasta una longitud especificada,
	 * rellenando por la izquierda con un carácter que se le indica
	 * @param cad Cadena
	 * @param relleno carácter de relleno
	 * @param len Longitud final de la cadena
	 * @return Cadena de longitud len, rellenada por la izquierda con el carácter "relleno" 
	 */
	private String lpad(String cad, char relleno, int len){
		if (cad==null){
			return cad;
		}
		
		String tmp=cad.trim();
        if (tmp.length()>=len){
            return tmp.substring(0,len);
        }
		StringBuilder sb= new StringBuilder(len);
		for (int i=0;i<len - tmp.length();i++){
			sb.append(relleno);
		}
		return (sb.toString() + tmp).substring(0,len);
	}
	/**
	 * Realiza el pago de IBI
	 * @throws IBIException
	 */
	private void realizarPago()throws IBIException
	{
		log.info ("====Llamada a la pasarela de pago.");
		PagoHelper pago = PagoHelper.newInstance(context);
		//CRUBENCVS 12/01/2022 
		//El nif de contribuyente se normaliza para que ocupe nueve posiciones
		//El de operante no, ya que consideramos que el del notario es correcto.
		ResultadoPago resPago = pago.pagar(this.emisora,
										   this.identificacion,
										   this.refEmision,
										   normalizaNif(this.nifContribuyente), 
										   this.ccc, 
										   this.nifTitular, 
										   this.importe);
		if (!resPago.isError())
		{
			log.info ("====Pago correcto.");
			log.info ("====Se establece el estado de pago de IBI a PAGADO");
			EstadoPagoIBI.setEstadoPagado(this.idRegistroBD, pref, this.idSesion);
			//Ponemos la fecha de pago. Si acaba de hacerlo, es obvio que es hoy
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			this.fechaPago = sd.format(cal.getTime());
			this.error = false;
		}
		else
		{
			log.info ("====No se ha podido pagar. Código y Mensaje:"+ resPago.getCodigoResultado()+"--"+resPago.getMensajeResultado());
			MensajeAplicacion mensajeDevuelto = mensajes.getMapeoMensaje(Constantes.seccionApp, resPago.getCodigoResultado());
			this.error= true;
			this.codError = mensajeDevuelto.getCodMensaje();
			this.textError = mensajeDevuelto.getTextoMensaje();
		} 
	}
	/**
	 * Genera los documentos, justificante de pago y certificado de no deuda
	 * @throws IBIException
	 */
	private void generarDocumentos()throws IBIException
	{
		log.info ("====Se genera el justificante de pago");
		DocumentosPago dp = ImpresionFactory.getDocumentosPago(context);
		try
		{
			String justificante = dp.getJustificantePago(this.refEmision);
			if (justificante==null)
			{
				throw new IBIException ("No se ha podido generar el justificante de pago.");
			}
			documentos.setJustificantePago(justificante);
			//Recuperamos el código de verificación, para devolverlo en la respuesta
			documentos.setCodVerifJustificante(dp.getCodVerificacionJustPago(this.refEmision));
			log.info ("====Se genera el documento de no deuda");
			RespuestaLanzador respuestaBD=generarCertNoDeuda ();
			ResultadoGeneracionCertNoDeuda cert = ResultadoGeneracionCertNoDeuda.newInstance(respuestaBD, context);
			{
				String nodeuda= getDocumentoNodeudaReimpreso(cert);
				if (nodeuda==null || "".equals(nodeuda))
				{
					log.info ("====Reimpresión incorrecta de documento de no deuda.");
					MensajeAplicacion menError = mensajes.getMensaje(Constantes.idMensajeDefecto);
					this.error = true;
					this.codError = menError.getCodMensaje();
					this.textError = menError.getTextoMensaje();
				}
				else
				{
					documentos.setCertificadoNoDeuda(nodeuda);
					//Alta del documento.
					log.info ("====Reimpresión correcta, alta de documento de no deuda.");
					String resultadoAlta=altaDocumento(nodeuda,respuestaBD);
					MensajeAplicacion mensajeDevuelto = mensajes.getMapeoMensaje(Constantes.seccionDoin, resultadoAlta);
					if (!mensajeDevuelto.isError())
					{
						log.info ("====Construcción del mensaje de salida.");
						log.info ("====Se establece el estado de pago de IBI a Completado");
						EstadoPagoIBI.setEstadoFinalizado(this.idRegistroBD, pref, idSesion);
						this.error = false;
					}
					else
					{
						log.info ("====Alta de documento errónea.");
						this.error = true;
						this.codError = mensajeDevuelto.getCodMensaje();
						this.textError = mensajeDevuelto.getTextoMensaje();
					}
				}
			}
		}
		catch (ImpresionException e)
		{
			throw new IBIException (e.getMessage(),e);
		}
		 
	}
	/**
	 * Recupera los documentos generados si el proceso está en estado FINALIZADO
	 * @throws IBIException
	 */
	private void recuperaDocumentosGenerados() throws IBIException
	{
		log.info ("====El proceso de pago ya había finalizado correctamente. Se recuperan los documentos");
		DocumentoDoin doin = DoinFactory.newDocumentoDoin(context);
		String resDoc= doin.getDocumentoDoin(this.refEmision, "P", true); //Justificante de pago
		MensajeAplicacion mensajeDevuelto = mensajes.getMapeoMensaje(Constantes.seccionDoin, resDoc);
		if (!mensajeDevuelto.isError())
		{
			documentos.setJustificantePago(doin.getDocumento());
			resDoc = doin.getDocumentoDoin(this.refEmision, "J", true); //Certificado de no deuda
			mensajeDevuelto = mensajes.getMapeoMensaje(Constantes.seccionDoin, resDoc);
			if (!mensajeDevuelto.isError())
			{
				documentos.setCertificadoNoDeuda(doin.getDocumento());
				DocumentosPago dp = ImpresionFactory.getDocumentosPago(context);
				try
				{
					documentos.setCodVerifJustificante(dp.getCodVerificacionJustPago(this.refEmision));
				}
				catch (ImpresionException e)
				{
					throw new IBIException (e.getMessage(),e);
				}
			}
			else
			{
				this.error = true;
				this.codError = mensajeDevuelto.getCodMensaje();
				this.textError = mensajeDevuelto.getTextoMensaje();
			}
		}
	}
	
}
