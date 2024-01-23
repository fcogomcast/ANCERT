package es.stpa.plusvalias.preferencias;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;




public class Preferencias {
	private Preferences m_preferencias;
	private final static String DIRECTORIO_SERVICIO = "proyectos/WSAncertLiquidacionPlusvalias";
	private final static String FICHERO_PREFERENCIAS = "prefsAncertLiquidacionPlusvalias.xml";
	private final static String DIRECTORIO_PREFERENCIAS = DIRECTORIO_SERVICIO;

	private HashMap<String, String> tablaPreferencias = new HashMap<String, String>();

	// nombres de las preferencias
	private final static String KEY_PREF_LOG = "ModoLog";
	private final static String KEY_PREF_DIR_LOG = "directorioRaizLogs";
	private final static String KEY_PREF_LOG_APLICACION = "ficheroLogAplicacion";
	private final static String KEY_PREF_LOG_CLIENT = "ficheroLogClient";
	private final static String KEY_PREF_LOG_SERVER = "ficheroLogServer";
	private final static String KEY_PREF_ESQUEMA = "EsquemaBD";
	private final static String KEY_PREF_ENDPOINT_LANZADOR = "EndPointLanzador";
	private final static String KEY_PREF_PROC_BONIFICACIONES = "ProcAlmacenadoConsultaBonificaciones";
	private final static String KEY_PREF_ENDPOINT_SEGURIDAD= "EndpointSeguridadSW";
	private final static String KEY_PREF_VALIDA_CERTIFICADO = "validarCertificado";
	private final static String KEY_PREF_VALIDA_PERMISOS = "validarPermisos";
	private final static String KEY_PREF_PROCPERMISO_SERVICIO = "ProcAlmacenadoPermisosServicio";
	private final static String KEY_PREF_ALIAS_FIRMA = "aliasCertificadoFirma";
	private final static String KEY_PREF_ALIAS_SERVICIO = "aliasServicio";
	private final static String KEY_PREF_FIRMAR_SALIDA = "firmarMensajeSalida";
	private final static String KEY_PREF_VALIDAR_FIRMA = "validarFirmaMensajeEntrada";
	private final static String KEY_PREF_ENDPOINT_AUTENTICACION = "EndpointAutenticacion";
	private final static String KEY_PREF_PROC_DATOS_FINCA = "ProcAlmacenadoDatosFinca";
	private final static String KEY_PREF_PROC_TIPO_TRANSMISION = "ProcAlmacenadoTipoTransmision";
	private final static String KEY_PREF_PROC_CALCULAR_LIQUIDACION = "ProcAlmacenadoCalcularLiquidacion";
	private final static String KEY_PREF_PROC_IMPRESO_CONSULTA = "ProcAlmacenadoImpresoConsulta";
	private final static String KEY_PREF_PROC_VALIDACIONES_PREVIAS_CALCULO = "ProcAlmacenadoValidacionesPreviasCalculo";
	private final static String KEY_PREF_PROC_DATOS_NOTARIO = "ProcAlmacenadoDatosNotario";
	private final static String KEY_PREF_ENDPOINT_DOCUMENTOS = "EndpointDocumentos";
	private final static String KEY_PREF_PROC_REQUISITOS = "ProcAlmacenadoRecuperaRequisitosBeneficio";
	private final static String KEY_PREF_ENDPOINT_PASARELA = "EndpointPasarelaPago";
	private final static String KEY_PREF_PROC_INTEGRA_MODELO = "ProcAlmacenadoIntegraModelo";	
	private final static String KEY_PREF_PROC_PRESENTA_MODELO = "ProcAlmacenadoPresentaModelo";
	private final static String KEY_PREF_PROC_ESTADO_PRESEN = "ProcAlmacenadoEstadoPresentacion";
	private final static String KEY_PREF_PROC_ACTUALIZA_ESTADO= "ProcAlmacenadoActualizaEstado";
	private final static String KEY_PREF_ENDPOINT_ESCRITURA= "EndpointEscrituras";
	private final static String KEY_PREF_PROC_IMPRESO_PRESENTACION = "ProcAlmacenadoImpresoPresentacion";
	private final static String KEY_PREF_PROC_JUSTIF_PRES = "ProcAlmacenadoJustificantePresentacion";
	private final static String KEY_PREF_INTERVALO_TIMESTAMP = "IntervaloSegundosTimestampFirma";
	//CRUBENCVS 47084. 06/02/2023. Algoritmos de firma y  de digest
	private final static String KEY_PREF_SIGN_ALG = "AlgoritmoFirmaMensaje";
	private final static String KEY_PREF_DIG_ALG  = "AlgoritmoDigestMensaje";
	//FIN CRUBENCVS 47084
	
	
	public Preferencias() throws PreferenciasException {
		cargarPreferencias();
	}

	public void cargarPreferencias() throws PreferenciasException {
		FileInputStream inputStream=null;
		try {
			if (CompruebaFicheroPreferencias()) {

				inputStream = new FileInputStream(
						DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
				Preferences.importPreferences(inputStream);
				inputStream.close();

				m_preferencias = Preferences.systemNodeForPackage(this
						.getClass());

				String[] keys = m_preferencias.keys();
				String msgKeys = "Leyendo las siguientes claves -> ";
				for (int i = 0; i < keys.length; i++) {
					msgKeys += "[" + keys[i] + "] ";
				}

				for (int i = 0; i < keys.length; i++) {
					String value = m_preferencias.get(keys[i], "");
					tablaPreferencias.put(keys[i], value);
				}
			}
		} catch (Exception ex) {
			throw new PreferenciasException(
					"Error al cargar las preferencias: " + ex.getMessage(), ex);
		} finally{
			if (inputStream!=null){
				try {inputStream.close();} catch (Exception e){}
			}
		}

	}

	private void InicializaTablaPreferencias() {

		tablaPreferencias.clear();

		tablaPreferencias.put(KEY_PREF_LOG, 				 		"INFO");
		tablaPreferencias.put(KEY_PREF_DIR_LOG		,	 	 		DIRECTORIO_SERVICIO	+ "/logs");
		tablaPreferencias.put(KEY_PREF_LOG_APLICACION,	 	 		"Application.log");
		tablaPreferencias.put(KEY_PREF_LOG_CLIENT, 		 	 		"Soap_Client.log");
		tablaPreferencias.put(KEY_PREF_LOG_SERVER, 			 		"Soap_Server.log");
		tablaPreferencias.put(KEY_PREF_VALIDA_CERTIFICADO ,	 		"S");
		tablaPreferencias.put(KEY_PREF_VALIDA_PERMISOS ,	 		"S");
		tablaPreferencias.put(KEY_PREF_VALIDAR_FIRMA ,		 		"S");
		tablaPreferencias.put(KEY_PREF_FIRMAR_SALIDA ,		 		"S");
		tablaPreferencias.put(KEY_PREF_PROCPERMISO_SERVICIO, 		"INTERNET.permisoServicio");
		tablaPreferencias.put(KEY_PREF_PROC_BONIFICACIONES,			"INTERNET_PLUSVALIAS.LISTA_EXENCION_BONIF");
		tablaPreferencias.put(KEY_PREF_ALIAS_SERVICIO,		 		"ANCRT_PLUS");
		tablaPreferencias.put(KEY_PREF_ALIAS_FIRMA ,		 		"Tributas");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_SEGURIDAD ,	 		"http://bus:7101/WSInternos/ProxyServices/PXSeguridadWS");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_LANZADOR,	 		"http://bus:7101/WSInternos/ProxyServices/PXLanzador");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_AUTENTICACION,		"http://bus:7101/WSAutenticacionEPST/ProxyServices/PXAutenticacionEPST");
		tablaPreferencias.put(KEY_PREF_ESQUEMA,				 		"EXPLOTACION");
		tablaPreferencias.put(KEY_PREF_PROC_DATOS_FINCA,			"ANCERT_PLUSVALIAS.datos_referencia_catastral");
		tablaPreferencias.put(KEY_PREF_PROC_TIPO_TRANSMISION,		"ANCERT_PLUSVALIAS.recupera_tipo_transmision");
		tablaPreferencias.put(KEY_PREF_PROC_CALCULAR_LIQUIDACION,	"ANCERT_PLUSVALIAS.calcular_plusvalia");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_DOCUMENTOS,		    "http://bus:7101/WSInternos/ProxyServices/PXDocumentos");
		tablaPreferencias.put(KEY_PREF_PROC_VALIDACIONES_PREVIAS_CALCULO,		"ANCERT_PLUSVALIAS.validaciones_previas_calculo");
		tablaPreferencias.put(KEY_PREF_PROC_DATOS_NOTARIO,			"ANCERT_PLUSVALIAS.datos_notario");
		tablaPreferencias.put(KEY_PREF_PROC_IMPRESO_CONSULTA,		"ANCERT_PLUSVALIAS.generar_impreso_consulta");
		tablaPreferencias.put(KEY_PREF_PROC_REQUISITOS,				"ANCERT_PLUSVALIAS.recupera_requisitos");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_PASARELA,			"http://bus.7101/WSPasarelaPago/ProxyServices/PXPasarelaPagoST");
		tablaPreferencias.put(KEY_PREF_PROC_INTEGRA_MODELO,			"INTERNET_PLUSVALIAS.insertarmodelo029");
		tablaPreferencias.put(KEY_PREF_PROC_PRESENTA_MODELO,		"INTERNET_PLUSVALIAS.integracion029");
		tablaPreferencias.put(KEY_PREF_PROC_ESTADO_PRESEN,			"ANCERT_PLUSVALIAS.estado_proceso_presentacion");
		tablaPreferencias.put(KEY_PREF_PROC_ACTUALIZA_ESTADO,		"ANCERT_PLUSVALIAS.actualiza_estado_presentacion");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_ESCRITURA,			"http://bus:7101/WSANCERT/ProxyServices/PXRecepcionEscrituraPlusvalias");
		tablaPreferencias.put(KEY_PREF_PROC_IMPRESO_PRESENTACION,	"ANCERT_PLUSVALIAS.generar_impreso_presentacion");
		tablaPreferencias.put(KEY_PREF_PROC_JUSTIF_PRES,			"ANCERT_PLUSVALIAS.recupera_justificante_pres");
		tablaPreferencias.put(KEY_PREF_INTERVALO_TIMESTAMP,			"300");
		//CRUBENCVS 47084. 06/02/2023. Algoritmos de firma y  de digest
		tablaPreferencias.put(KEY_PREF_SIGN_ALG,								"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
		tablaPreferencias.put(KEY_PREF_DIG_ALG,									"http://www.w3.org/2001/04/xmlenc#sha256");
		//FIN  CRUBENCVS 47084
	}

	private boolean CompruebaFicheroPreferencias()
			throws PreferenciasException {
		boolean existeFichero = false;

		File f = new File(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
		existeFichero = f.exists();
		if (existeFichero == false) {
			CrearFicheroPreferencias();
		}

		return existeFichero;
	}

	/***************************************************************************
	 * 
	 * Creamos el fichero de preferencias con los valores por defecto
	 * 
	 **************************************************************************/
	private void CrearFicheroPreferencias()
			throws PreferenciasException {

		// preferencias por defecto
		m_preferencias = Preferences.systemNodeForPackage(this.getClass());

		InicializaTablaPreferencias();

		// recorremos la tabla cargada con las preferencias por defecto
		Iterator<Map.Entry<String, String>> itr = tablaPreferencias.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Map.Entry<String, String> e = (Map.Entry<String, String>) itr
					.next();

			m_preferencias.put(e.getKey(), e.getValue());
		}

		FileOutputStream outputStream = null;
		File fichero;
		try {
			fichero = new File(DIRECTORIO_PREFERENCIAS);
			if (fichero.exists() == false)
				if (fichero.mkdirs() == false) {
					throw new java.io.IOException(
							"No se puede crear el directorio de las preferencias.");
				}

			outputStream = new FileOutputStream(DIRECTORIO_PREFERENCIAS + "/"
					+ FICHERO_PREFERENCIAS);
			m_preferencias.exportNode(outputStream);
		} catch (Exception e) {
			throw new PreferenciasException(
					"Error al crear el fichero de preferencias:"
							+ e.getMessage(), e);
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e) {
				throw new PreferenciasException(
						"Error al cerrar el flujo del fichero de preferencias:"
								+ e.getMessage(), e);
			}
		}
	}

	public void recargaPreferencias() throws PreferenciasException {
		cargarPreferencias();
	}

	private String getValueFromTablaPreferencias(String key) {
		String toReturn = "";

		if (tablaPreferencias.containsKey(key)) {
			toReturn = tablaPreferencias.get(key);
		}

		return toReturn;
	}


	public String getModoLog() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG);
	}

	public String getFicheroLogAplicacion() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG_APLICACION);
	}
	public String getFicheroLogClient() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG_CLIENT);
	}

	public String getFicheroLogServer() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG_SERVER);
	}
	
	public String getValidaCertificado() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_CERTIFICADO);
	}
	
	public String getValidaPermisos() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_PERMISOS);
	}
	
	public String getProcPermisoServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCPERMISO_SERVICIO);
	}
	
	public String getAliasServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_SERVICIO);
	}
	
	public String getAliasFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_FIRMA);
	}
	
	public String getEndpointSeguridad() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_SEGURIDAD);
	}
	
	public String getEndpointLanzador() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_LANZADOR);
	}
	
	public String getFirmarSalida() {
		return getValueFromTablaPreferencias(KEY_PREF_FIRMAR_SALIDA);
	}
	public String getEsquemaBD() {
		return getValueFromTablaPreferencias(KEY_PREF_ESQUEMA);
	}
	
	public String getEndpointAutenticacion() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_AUTENTICACION);
	}
	
	public String getProcListaExencionBonif() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_BONIFICACIONES);
	}
	
	public String getValidarFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDAR_FIRMA);
	}
	
	public String getProcDatosFinca() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_DATOS_FINCA);
	}
	
	public String getProcTipoTransmision() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_TIPO_TRANSMISION);
	}
	
	public String getProcCalcularLiquidacion() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_CALCULAR_LIQUIDACION);
	}
	
	public String getProcImpresoConsulta() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_IMPRESO_CONSULTA);
	}
	
	public String getEndpointDocumentos() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_DOCUMENTOS);
	}
	
	public String getDirectorioRaizLog() {
		return getValueFromTablaPreferencias(KEY_PREF_DIR_LOG);
	}
	
	public String getProcValidacionesPreviasCalculo() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_VALIDACIONES_PREVIAS_CALCULO);
	}
	
	public String getProcDatosNotario() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_DATOS_NOTARIO);
	}
	
	public String getProcRequisitosBeneficios() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_REQUISITOS);
	}
	
	public String getEndpointPasarelaPago() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_PASARELA);
	}
	
	public String getProcIntegraModelo() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_INTEGRA_MODELO);
	}
	
	public String getProcPresentaModelo() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_PRESENTA_MODELO);
	}
	
	public String getProcRecuperaEstadoPresentacion() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_ESTADO_PRESEN);
	}
	
	public String getProcActualizaEstado() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_ACTUALIZA_ESTADO);
	}
	
	public String getEndpointRecepcionEscritura() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_ESCRITURA);
	}
	
	public String getProcImpresoPresentacion() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_IMPRESO_PRESENTACION);
	}
	
	public String getProcJustificantePres() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_JUSTIF_PRES);
	}
	
	public String getIntervaloTimestamp() {
		return getValueFromTablaPreferencias(KEY_PREF_INTERVALO_TIMESTAMP);
	}
	//CRUBENCVS 47084. 06/02/2023. Algoritmos de firma y  de digest
	public String getUriAlgoritmoFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_SIGN_ALG);
	}
	
	public String getUriAlgoritmoDigest() {
		return getValueFromTablaPreferencias(KEY_PREF_DIG_ALG);
	}
	//FIN CRUBENCVS 47084
}
