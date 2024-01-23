package es.tributasenasturias.services.ancert.solicitudEscritura.utils.preferencias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.PreferenciasException;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Utils;

public class Preferencias 
{
	//static private Preferencias _pref = new Preferencias();
	private Preferences m_preferencias;
	private final static String FICHERO_PREFERENCIAS = "preferenciasSolicitudEscrituras.xml";
	private final static String DIRECTORIO_PREFERENCIAS = Utils.getAppDirectory();
 
	private HashMap<String, String> tablaPreferencias = new HashMap<String, String>();
	
	//nombres de las preferencias
	private final static String KEY_PREF_LOG = "ModoLog";
	private final static String KEY_PREF_ENDPOINT_FIRMA = "EndPointFirma";
	private final static String KEY_PREF_ALIAS_CERTIFICADO_FIRMA = "AliasCertificadoFirma";
	private final static String KEY_PREF_NODO_CONTENEDOR_FIRMA = "NodoContenedorFirma";
	private final static String KEY_PREF_NS_NODO_CONTENEDOR_FIRMA = "NamespaceNodoContenedorFirma";
	private final static String KEY_PREF_ID_NODO_FIRMAR = "IDNodoFirmar";
	private final static String KEY_PREF_ENDPOINT_AUTENTICACION_PA = "EndPointAutenticacionPA";
	private final static String KEY_PREF_IP_AUTORIZACION = "IpAutorizacion";
	private final static String KEY_PREF_EMISOR_SOLICITUD = "EmisorSolicitud";
	private final static String KEY_PREF_CODIGO_SERVICIO = "CodigoServicio";
	private final static String KEY_PREF_TIPO_MENSAJE = "TipoMensaje";
	private final static String KEY_PREF_RECEPTOR_SOLICITUD = "ReceptorSolicitud";
	private final static String KEY_PREF_APLICACION = "Aplicacion";
	private final static String KEY_PREF_OPERACION = "Operacion";
	private final static String KEY_PREF_TIPO_COPIA = "TipoCopia";
	private final static String KEY_ID_REQUEST = "RequestId";
	private final static String KEY_PREF_ENDPOINT_SOLICITUD = "EndpointSolicitud";
	private final static String KEY_PREF_ENDPOINT_LANZADOR = "EndPointLanzador";
	private final static String KEY_PREF_ESQUEMA = "EsquemaBaseDatos";
	private final static String KEY_PREF_PROCPERMISO_SERVICIO = "PAPermisoServicio";
	private final static String KEY_PREF_PLANTILLA_SOLICITUD = "PlantillaSolicitud";
	private final static String KEY_PREF_VALIDA_CERTIFICADO = "ValidaCertificado";
	private final static String KEY_PREF_VALIDA_FIRMA = "ValidaFirma";
	private final static String KEY_PREF_FIRMA_MENSAJE = "FirmaSalida";
	private final static String KEY_PREF_PROCOBTENER_PLANTILLA = "PAObtenerPlantilla";
	private final static String KEY_PREF_PLANTILLA_CERTIFICADO = "PlantillaCertificado";
	private final static String KEY_PREF_PROCSOL_DUPLICADA = "PASolicitudDuplicada";
	private final static String KEY_PREF_PROCACTUALIZAR_SOLICITUD = "PAActualizarSolicitud";
	private final static String KEY_PREF_PROCALTA_SOLICITUD = "PAAltaSolicitud";
	private final static String KEY_PREF_ALIAS_SERVICIO = "AliasPermisoServicio";
	//23/01/2020. 38243 Nueva versión de servicio de solicitud de escrituras
	private final static String KEY_PREF_ENDPOINT_SEGURIDAD_SW = "EndpointSeguridadSW";
	private final static String KEY_PREF_ENDPOINT_DOCUMENTOS  =  "EndpointDocumentos";
	private final static String KEY_PREF_PROC_INFORME_SOLICITUD ="PAInformeSolicitud";
	//Fichero de log
	private final static String KEY_PREF_APP_FICHERO_LOG="FicheroLogApp";
	private final static String KEY_PREF_SS_FICHERO_LOG="FicheroLogSoapServer";
	private final static String KEY_PREF_SC_FICHERO_LOG="FicheroLogSoapClient";
	//Depuración de SOAP
	private final static String KEY_PREF_DEBUG_SOAP = "DebugSOAP";
	//Fin 38243
	
	//CRUBENCVS 47084. 06/02/2023. Algoritmos de firma y  de digest
	private final static String KEY_PREF_SIGN_ALG = "AlgoritmoFirmaMensaje";
	private final static String KEY_PREF_DIG_ALG  = "AlgoritmoDigestMensaje";
	//FIN CRUBENCVS 47084
	
	
	protected Preferencias() throws PreferenciasException 
	{		
		CargarPreferencias();
	}
	protected void CargarPreferencias() throws PreferenciasException
 {
		try
		{
			if(CompruebaFicheroPreferencias())
			{		       
			
		        FileInputStream inputStream = new FileInputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
		        Preferences.importPreferences(inputStream);
		        inputStream.close();
		
		        m_preferencias = Preferences.systemNodeForPackage(this.getClass());
		        
		        String[] keys = m_preferencias.keys();
		        String msgKeys ="Leyendo las siguientes claves -> ";
		        for(int i=0;i<keys.length;i++)
		        {
		        	msgKeys += "["+keys[i]+"] ";
		        }
		        //Logger.debug(msgKeys);
		        
		        for(int i=0;i<keys.length;i++)
		        {
		        	String value = m_preferencias.get(keys[i], "");
		        	tablaPreferencias.put(keys[i], value);
		        }
			}
		}
		catch (Exception ex)
		{
			throw new PreferenciasException ("Error al cargar las preferencias: " +ex.getMessage(),ex);
		}
		
 }
	
	private void InicializaTablaPreferencias()
	{
		
		tablaPreferencias.clear();
		
		tablaPreferencias.put(KEY_PREF_LOG,									"INFO");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_FIRMA,						"http://bus:7101/WSInternos/ProxyServices/PXFirmaDigital");
		tablaPreferencias.put(KEY_PREF_ALIAS_CERTIFICADO_FIRMA,					"");
		tablaPreferencias.put(KEY_PREF_NODO_CONTENEDOR_FIRMA,				"SERVICIOS_ESCRITURA");
		tablaPreferencias.put(KEY_PREF_NS_NODO_CONTENEDOR_FIRMA,		"http://inti.notariado.org/XML/FICHA");
		tablaPreferencias.put(KEY_PREF_ID_NODO_FIRMAR	,			"");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_AUTENTICACION_PA,				"http://bus:7101/WSAutenticacionPA/ProxyServices/PXAutenticacionPA");
		tablaPreferencias.put(KEY_PREF_IP_AUTORIZACION,							"10.112.10.35");
		tablaPreferencias.put(KEY_PREF_EMISOR_SOLICITUD,								"CEH");
		tablaPreferencias.put(KEY_PREF_CODIGO_SERVICIO,							"EP0003");
		tablaPreferencias.put(KEY_PREF_TIPO_MENSAJE,							"1");
		tablaPreferencias.put(KEY_PREF_RECEPTOR_SOLICITUD,						"CGN");
		tablaPreferencias.put(KEY_PREF_APLICACION,								"APP");
		tablaPreferencias.put(KEY_PREF_OPERACION,								"07");
		tablaPreferencias.put(KEY_PREF_TIPO_COPIA,								"AUTORIZADA");
		tablaPreferencias.put(KEY_ID_REQUEST,									"ESCRITURAS");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_SOLICITUD,						"http://bus:7101/WSANCERT/ProxyServices/PXSolicitudEscrituraANCERT");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_LANZADOR,						"http://bus:7101/WSInternos/ProxyServices/PXLanzador");
		tablaPreferencias.put(KEY_PREF_ESQUEMA,									"EXPLOTACION");
		tablaPreferencias.put(KEY_PREF_PROCPERMISO_SERVICIO,					"INTERNET.permisoServicio");
		tablaPreferencias.put(KEY_PREF_PLANTILLA_SOLICITUD,						"CGNGENERIC");
		tablaPreferencias.put(KEY_PREF_VALIDA_CERTIFICADO,						"S");
		tablaPreferencias.put(KEY_PREF_VALIDA_FIRMA,							"S");
		tablaPreferencias.put(KEY_PREF_FIRMA_MENSAJE,							"S");
		tablaPreferencias.put(KEY_PREF_PROCOBTENER_PLANTILLA,					"SERVICIOS_WEB_APP.obtenerPlantillaApp");
		tablaPreferencias.put(KEY_PREF_PLANTILLA_CERTIFICADO,					"VALIDCERT");
		tablaPreferencias.put(KEY_PREF_PROCSOL_DUPLICADA,						"ANCERT.solEscrituraDuplicadaExterno");
		tablaPreferencias.put(KEY_PREF_PROCACTUALIZAR_SOLICITUD,				"ANCERT.actualizarSolEscrituraExterno");
		tablaPreferencias.put(KEY_PREF_PROCALTA_SOLICITUD,						"ANCERT.altaSolicitudEscrituraExterno");
		tablaPreferencias.put(KEY_PREF_ALIAS_SERVICIO,							"AN_DILIGEN");
		//INI 38243
		tablaPreferencias.put(KEY_PREF_ENDPOINT_SEGURIDAD_SW,					"http://bus:7101/WSInternos/ProxyServices/PXSeguridadWS");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_DOCUMENTOS,	    				"http://bus:7101/WSInternos/ProxyServices/PXDocumentos");
	    tablaPreferencias.put(KEY_PREF_PROC_INFORME_SOLICITUD,	 				"GD_SERVICIOS.informe_solicitud_escritura");
		tablaPreferencias.put(KEY_PREF_APP_FICHERO_LOG,						    "proyectos/WSANCERTSolicitudEscrituras/log/Application.log");	
		tablaPreferencias.put(KEY_PREF_SS_FICHERO_LOG,						    "proyectos/WSANCERTSolicitudEscrituras/log/SoapServer.log");
		tablaPreferencias.put(KEY_PREF_SC_FICHERO_LOG,						    "proyectos/WSANCERTSolicitudEscrituras/log/SoapClient.log");
		tablaPreferencias.put(KEY_PREF_DEBUG_SOAP,								"N");

		//FIN 38243
		//CRUBENCVS 47084. 06/02/2023. Algoritmos de firma y  de digest
		tablaPreferencias.put(KEY_PREF_SIGN_ALG,								"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
		tablaPreferencias.put(KEY_PREF_DIG_ALG,									"http://www.w3.org/2001/04/xmlenc#sha256");
		//FIN  CRUBENCVS 47084
	}
	
	private boolean CompruebaFicheroPreferencias() throws PreferenciasException
 {
		boolean existeFichero = false;
		
     File f = new File(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
     existeFichero = f.exists();
     if (existeFichero == false)
     {
         CrearFicheroPreferencias();
     }
     
     return existeFichero;
 }
	
	 /***********************************************************
  * 
  * Creamos el fichero de preferencias con los valores por 
  * defecto
  * 
  ***************************************************************/
 private void CrearFicheroPreferencias() throws PreferenciasException
 {
 	
     //preferencias por defecto
     m_preferencias = Preferences.systemNodeForPackage(this.getClass());
     
     InicializaTablaPreferencias();
     
     //recorremos la tabla cargada con las preferencias por defecto
     Iterator<Map.Entry<String, String>> itr = tablaPreferencias.entrySet().iterator();
     while(itr.hasNext())
     {
     	Map.Entry<String, String> e = (Map.Entry<String,String>)itr.next();
     	//Logger.debug("Cargando en fichero preferencias ['"+e.getKey()+"' - '"+e.getValue()+"']");
     	
     	m_preferencias.put(e.getKey(),e.getValue());
     }

     FileOutputStream outputStream = null;
     File fichero;
     try
     {
         fichero = new File(DIRECTORIO_PREFERENCIAS);
         if(fichero.exists() == false)
             if (fichero.mkdirs()==false)
             	{
             	 throw new java.io.IOException ("No se puede crear el directorio de las preferencias.");
             	}
         
         outputStream = new FileOutputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
         m_preferencias.exportNode(outputStream);
     }
     catch (Exception e)
     {
    	throw new PreferenciasException("Error al crear el fichero de preferencias:" + e.getMessage(),e);
     }
     finally
     {
         try
         {
             if(outputStream != null)
                 outputStream.close();
         }
         catch(Exception e)
         {
        	 throw new PreferenciasException ("Error al cerrar el flujo del fichero de preferencias:" + e.getMessage(),e);
         }
     }
 }
 
 public void recargaPreferencias() throws PreferenciasException
 {
 	CargarPreferencias();
 }
 
 private String getValueFromTablaPreferencias(String key)
 {
 	String toReturn="";
 	
 	if(tablaPreferencias.containsKey(key))
 	{
 		toReturn = tablaPreferencias.get(key);
 	}
 	
 	
 	return toReturn;
 }
 
 private void setValueIntoTablaPreferencias(String key, String value)
 {
 	tablaPreferencias.put(key, value);
 }
	
	public String getEsquemaBaseDatos() {
		return getValueFromTablaPreferencias(KEY_PREF_ESQUEMA);
	}
	public void setEsquemaBaseDatos(String esquema) {
		setValueIntoTablaPreferencias(KEY_PREF_ESQUEMA, esquema);
	}
    public String getEndPointLanzador() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_LANZADOR);
	}
	public void setEndPointLanzador(String endPointLanzador) {
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_LANZADOR, endPointLanzador);
	}
	public String getModoLog() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG);
	}
	public void setModoLog(String modo) {
		setValueIntoTablaPreferencias(KEY_PREF_LOG, modo);
	}
	public String getEndPointFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_FIRMA);
	}
	public void setEndPointFirma(String endpointFirma) {
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_FIRMA, endpointFirma);
	}
	public String getAliasCertificadoFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_CERTIFICADO_FIRMA);
	}
	public void setAliasCertificadoFirma(String certificadoFirma) {
		setValueIntoTablaPreferencias(KEY_PREF_ALIAS_CERTIFICADO_FIRMA, certificadoFirma);
	}
	
	public String getNodoContenedorFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_NODO_CONTENEDOR_FIRMA);
	}
	public void setNodoContenedorFirma(String nodo) {
		setValueIntoTablaPreferencias(KEY_PREF_NODO_CONTENEDOR_FIRMA, nodo);
	}
	public String getNsNodoContenedorFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_NS_NODO_CONTENEDOR_FIRMA);
	}
	public void setNsNodoContenedorFirma(String namespace) {
		setValueIntoTablaPreferencias(KEY_PREF_NS_NODO_CONTENEDOR_FIRMA, namespace);
	}
	public String getIdNodoFirmar() {
		return getValueFromTablaPreferencias(KEY_PREF_ID_NODO_FIRMAR);
	}
	public void setIdNodoFirmar(String idNodo) {
		setValueIntoTablaPreferencias(KEY_PREF_ID_NODO_FIRMAR, idNodo);
	}
	public String getEndPointAutenticacion() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_AUTENTICACION_PA);
	}
	public void setEndPointAutenticacion(String endpointAutenticacion) {
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_AUTENTICACION_PA, endpointAutenticacion);
	}
	public String getIpAutorizacion() {
		return getValueFromTablaPreferencias(KEY_PREF_IP_AUTORIZACION);
	}
	public void setIpAutorizacion(String ipAutorizacion) {
		setValueIntoTablaPreferencias(KEY_PREF_IP_AUTORIZACION, ipAutorizacion);
	}
	public String getEmisorSolicitud() {
		return getValueFromTablaPreferencias(KEY_PREF_EMISOR_SOLICITUD);
	}
	public void setEmisorSolicitud(String emisorSolicitud) {
		setValueIntoTablaPreferencias(KEY_PREF_EMISOR_SOLICITUD, emisorSolicitud);
	}
	public String getCodigoServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_CODIGO_SERVICIO);
	}
	public void setCodigoServicio(String codigoServicio) {
		setValueIntoTablaPreferencias(KEY_PREF_CODIGO_SERVICIO, codigoServicio);
	}
	public String getTipoMensaje() {
		return getValueFromTablaPreferencias(KEY_PREF_TIPO_MENSAJE);
	}
	public void setTipoMensaje(String tipoMensaje) {
		setValueIntoTablaPreferencias(KEY_PREF_TIPO_MENSAJE, tipoMensaje);
	}
	public String getReceptorSolicitud() {
		return getValueFromTablaPreferencias(KEY_PREF_RECEPTOR_SOLICITUD);
	}
	public void setReceptorSolicitud(String receptorSolicitud) {
		setValueIntoTablaPreferencias(KEY_PREF_RECEPTOR_SOLICITUD, receptorSolicitud);
	}
	public String getAplicacion() {
		return getValueFromTablaPreferencias(KEY_PREF_APLICACION);
	}

	public void setAplicacion(String aplicacion) {
		setValueIntoTablaPreferencias(KEY_PREF_APLICACION, aplicacion);
	}
	public String getOperacion() {
		return getValueFromTablaPreferencias(KEY_PREF_OPERACION);
	}

	public void setOperacion(String operacion) {
		setValueIntoTablaPreferencias(KEY_PREF_OPERACION, operacion);
	}
	public String getTipoCopia() {
		return getValueFromTablaPreferencias(KEY_PREF_TIPO_COPIA);
	}

	public void setTipoCopia(String tipoCopia) {
		setValueIntoTablaPreferencias(KEY_PREF_TIPO_COPIA, tipoCopia);
	}	
	public String getRequestId() {
		return getValueFromTablaPreferencias(KEY_ID_REQUEST);
	}
	public void setRequestId(String requestId) {
		setValueIntoTablaPreferencias(KEY_ID_REQUEST, requestId);
	}

	
	public String getEndpointSolicitud() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_SOLICITUD);
	}
	public void setEndpointSolicitud(String endpointSolicitud) {
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_SOLICITUD, endpointSolicitud);
	}
	public String getPAPermisoServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCPERMISO_SERVICIO);
	}
	public void setPAPermisoServicio(String pAPermisoServicio) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCPERMISO_SERVICIO, pAPermisoServicio);
	}
	public String getNombrePlantillaSolicitud() {
		return getValueFromTablaPreferencias(KEY_PREF_PLANTILLA_SOLICITUD);
	}
	public void setNombrePlantillaSolicitud(String nombrePlantillaSolicitud) {
		setValueIntoTablaPreferencias(KEY_PREF_PLANTILLA_SOLICITUD, nombrePlantillaSolicitud);
	}
	public String getValidaCertificado() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_CERTIFICADO);
	}
	public void setValidaCertificado(String validaCertificado) {
		setValueIntoTablaPreferencias(KEY_PREF_VALIDA_CERTIFICADO, validaCertificado);
	}
	public String getValidaFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_FIRMA);
	}
	public void setValidaFirma(String validaFirma) {
		setValueIntoTablaPreferencias(KEY_PREF_VALIDA_FIRMA, validaFirma);
	}
	public String getFirmaSalida() {
		return getValueFromTablaPreferencias(KEY_PREF_FIRMA_MENSAJE);
	}
	public void setFirmaSalida(String firmaSalida) {
		setValueIntoTablaPreferencias(KEY_PREF_FIRMA_MENSAJE, firmaSalida);
	}
	public String getPAObtenerPlantilla() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCOBTENER_PLANTILLA);
	}
	public void setPAObtenerPlantilla(String pAObtenerPlantilla) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCOBTENER_PLANTILLA, pAObtenerPlantilla);
	}
	public String getNombrePlantillaCertificado() {
		return getValueFromTablaPreferencias(KEY_PREF_PLANTILLA_CERTIFICADO);
	}
	public void setNombrePlantillaCertificado(String nombrePlantilla) {
		setValueIntoTablaPreferencias(KEY_PREF_PLANTILLA_CERTIFICADO, nombrePlantilla);
	}
	public String getAliasPermisoServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_SERVICIO);
	}
	public String getPASolicitudDuplicada() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCSOL_DUPLICADA);
	}
	public void setPASolicitudDuplicada(String solicitudDuplicada) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCSOL_DUPLICADA, solicitudDuplicada );
	}
	public String getPAActualizarSolicitud() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCACTUALIZAR_SOLICITUD);
	}
	public void setPAActualizarSolicitud(String actualizarSolicitud) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCACTUALIZAR_SOLICITUD, actualizarSolicitud );
	}
	public String getPAAltaSolicitud() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCALTA_SOLICITUD);
	}
	public void setPAAltaSolicitud(String altaSolicitud) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCALTA_SOLICITUD, altaSolicitud );
	}
	//INI 23/01/2020. 38243
	public String getEndpointSeguridadSW() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_SEGURIDAD_SW);
	}
	public String getFicheroLogAplicacion() {
		return getValueFromTablaPreferencias(KEY_PREF_APP_FICHERO_LOG);
	}
	public String getFicheroLogClient() {
		return getValueFromTablaPreferencias(KEY_PREF_SC_FICHERO_LOG);
	}
	public String getFicheroLogServer() {
		return getValueFromTablaPreferencias(KEY_PREF_SS_FICHERO_LOG);
	}

	public String getDebugSOAP() {
		return getValueFromTablaPreferencias(KEY_PREF_DEBUG_SOAP);
	}
	
	public String getEndpointDocumentos() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_DOCUMENTOS);
	}
	public String getProcInformeSolicitud() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_INFORME_SOLICITUD);
	}
	//FIN 23/01/2010. 38243
	
	//CRUBENCVS 47084. 06/02/2023. Algoritmos de firma y  de digest
	public String getUriAlgoritmoFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_SIGN_ALG);
	}
	
	public String getUriAlgoritmoDigest() {
		return getValueFromTablaPreferencias(KEY_PREF_DIG_ALG);
	}
	//FIN CRUBENCVS 47084
}
