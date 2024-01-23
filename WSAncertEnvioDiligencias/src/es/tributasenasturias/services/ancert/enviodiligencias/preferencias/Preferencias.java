package es.tributasenasturias.services.ancert.enviodiligencias.preferencias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;


public class Preferencias 
{
	private Preferences m_preferencias;
	private final static String FICHERO_PREFERENCIAS = "preferenciasEnvioDiligencias.xml";
	private final static String DIRECTORIO_PREFERENCIAS = "proyectos/WSAncertEnvioDiligencias";
 
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
	private final static String KEY_PREF_PROCALTA_DOCUMENTO = "pAAltaDocumento";
	private final static String KEY_PREF_EMISOR = "Emisor";
	private final static String KEY_PREF_CODIGO_SERVICIO = "CodigoServicio";
	private final static String KEY_PREF_TIPO_MENSAJE = "TipoMensaje";
	private final static String KEY_PREF_RECEPTOR = "Receptor";
	private final static String KEY_PREF_APLICACION = "Aplicacion";
	private final static String KEY_PREF_OPERACION = "Operacion";
	private final static String KEY_ID_REQUEST = "RequestId";
	private final static String KEY_PREF_ENDPOINT_ENVIO_DILIGENCIA = "EndpointEnvioDiligencia";
	private final static String KEY_PREF_ENDPOINT_LANZADOR = "EndPointLanzador";
	private final static String KEY_PREF_ESQUEMA = "EsquemaBaseDatos";
	private final static String KEY_PREF_PROCCONSULTA_DOCUMENTO = "pAConsultaDocumento";
	private final static String KEY_PREF_PLANTILLA_ANCERT = "plantillaMensajeAncert";
	private final static String KEY_PREF_PROC_SOLICITUD = "pARecuperarDatosSolicitud";
	private final static String KEY_PREF_VALIDA_CERTIFICADO = "ValidaCertificado";
	private final static String KEY_PREF_VALIDA_FIRMA = "ValidaFirma";
	private final static String KEY_PREF_FIRMA_MENSAJE = "FirmaSalida";
	private final static String KEY_PREF_PROCPERMISO_SERVICIO = "pAPermisoServicio";
	private final static String KEY_PREF_ALIAS_SERVICIO = "AliasPermisoServicio";
	private final static String KEY_PREF_PROCOBTENER_PLANTILLA = "pAObtenerPlantilla";
	private final static String KEY_PREF_PLANTILLA_CERTIFICADO = "PlantillaCertificado";
	private final static String KEY_PREF_PROCDAT_INFORME = "pADatosInforme";
	private final static String KEY_PREF_PROCCLAVE_CONSEJERIAS = "pAClaveConsejerias";
	//CRUBENCVS 47084. 06/02/2023. Algoritmos de firma y  de digest
	private final static String KEY_PREF_SIGN_ALG = "AlgoritmoFirmaMensaje";
	private final static String KEY_PREF_DIG_ALG  = "AlgoritmoDigestMensaje";
	//FIN CRUBENCVS 47084
	
	

	
	
	
	protected Preferencias() throws PreferenciasException 
	{		
		cargarPreferencias();
	}
	public void cargarPreferencias() throws PreferenciasException
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
		
		tablaPreferencias.put(KEY_PREF_LOG,										"INFO");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_FIRMA,							"http://bus:7101/WSInternos/ProxyServices/PXFirmaDigital");
		tablaPreferencias.put(KEY_PREF_ALIAS_CERTIFICADO_FIRMA,					"Tributas");
		tablaPreferencias.put(KEY_PREF_NODO_CONTENEDOR_FIRMA,					"ENVIO_DILIGENCIA");
		tablaPreferencias.put(KEY_PREF_NS_NODO_CONTENEDOR_FIRMA,				"http://inti.notariado.org/XML/TPAJD/diligencia");
		tablaPreferencias.put(KEY_PREF_ID_NODO_FIRMAR	,						"REQUEST");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_AUTENTICACION_PA,				"http://bus:7101/WSAutenticacionPA/ProxyServices/PXAutenticacionPA");
		tablaPreferencias.put(KEY_PREF_IP_AUTORIZACION,							"10.112.10.35");
		tablaPreferencias.put(KEY_PREF_EMISOR,									"PDA");
		tablaPreferencias.put(KEY_PREF_CODIGO_SERVICIO,							"TPDL01");
		tablaPreferencias.put(KEY_PREF_TIPO_MENSAJE,							"1");
		tablaPreferencias.put(KEY_PREF_RECEPTOR,								"CGN");
		tablaPreferencias.put(KEY_PREF_APLICACION,								"TPAJD");
		tablaPreferencias.put(KEY_PREF_OPERACION,								"00");
		tablaPreferencias.put(KEY_ID_REQUEST,									"ESCRITURAS");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_ENVIO_DILIGENCIA,				"http://bus:7101/WSANCERT/ProxyServices/PXEnvioDiligenciaANCERT");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_LANZADOR,						"http://bus:7101/WSInternos/ProxyServices/PXLanzador");
		tablaPreferencias.put(KEY_PREF_ESQUEMA,									"EXPLOTACION");
		tablaPreferencias.put(KEY_PREF_PROCCONSULTA_DOCUMENTO,					"INTERNET_DOCUMENTOSV2.consultadocumento");
		tablaPreferencias.put(KEY_PREF_PROCALTA_DOCUMENTO,						"INTERNET_DOCUMENTOSV2.altaDocumento");
		tablaPreferencias.put(KEY_PREF_PLANTILLA_ANCERT,						"CGNGENERIC");
		tablaPreferencias.put(KEY_PREF_PROC_SOLICITUD,							"ANCERT.datosSolicitudAutoliqExt");
		tablaPreferencias.put(KEY_PREF_VALIDA_CERTIFICADO,						"S");
		tablaPreferencias.put(KEY_PREF_VALIDA_FIRMA,							"S");
		tablaPreferencias.put(KEY_PREF_FIRMA_MENSAJE,							"S");
		tablaPreferencias.put(KEY_PREF_ALIAS_SERVICIO,							"AN_DILIGEN");
		tablaPreferencias.put(KEY_PREF_PROCOBTENER_PLANTILLA,					"SERVICIOS_WEB_APP.obtenerPlantillaApp");
		tablaPreferencias.put(KEY_PREF_PLANTILLA_CERTIFICADO,					"VALIDCERT");
		tablaPreferencias.put(KEY_PREF_PROCPERMISO_SERVICIO,					"INTERNET.permisoServicio");
		tablaPreferencias.put(KEY_PREF_PROCDAT_INFORME,							"PROGRAMAS_AYUDA4.obtenerDatosInformes");
		tablaPreferencias.put(KEY_PREF_PROCCLAVE_CONSEJERIAS,			 		"INTERNET.obtenerClave");
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
 private synchronized void CrearFicheroPreferencias() throws PreferenciasException
 {
 	
     //preferencias por defecto
     m_preferencias = Preferences.systemNodeForPackage(this.getClass());
     
     InicializaTablaPreferencias();
     
     //recorremos la tabla cargada con las preferencias por defecto
     Iterator<Map.Entry<String, String>> itr = tablaPreferencias.entrySet().iterator();
     while(itr.hasNext())
     {
     	Map.Entry<String, String> e = (Map.Entry<String,String>)itr.next();
     	
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
 	cargarPreferencias();
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
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_FIRMA, endpointAutenticacion);
	}
	public String getIpAutorizacion() {
		return getValueFromTablaPreferencias(KEY_PREF_IP_AUTORIZACION);
	}
	public void setIpAutorizacion(String ipAutorizacion) {
		setValueIntoTablaPreferencias(KEY_PREF_IP_AUTORIZACION, ipAutorizacion);
	}
	public String getEmisor() {
		return getValueFromTablaPreferencias(KEY_PREF_EMISOR);
	}
	public void setEmisor(String emisor) {
		setValueIntoTablaPreferencias(KEY_PREF_EMISOR, emisor);
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
	public String getReceptor() {
		return getValueFromTablaPreferencias(KEY_PREF_RECEPTOR);
	}
	public void setReceptor(String receptorSolicitud) {
		setValueIntoTablaPreferencias(KEY_PREF_RECEPTOR, receptorSolicitud);
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
	public String getPAConsultaDocumento()
	{
		return getValueFromTablaPreferencias(KEY_PREF_PROCCONSULTA_DOCUMENTO);
	}
	public void setPAConsultaDocumento(String nombreProcConsulta)
	{
		setValueIntoTablaPreferencias(KEY_PREF_PROCCONSULTA_DOCUMENTO, nombreProcConsulta);
	}
	public String getRequestId() {
		return getValueFromTablaPreferencias(KEY_ID_REQUEST);
	}
	public void setRequestId(String requestId) {
		setValueIntoTablaPreferencias(KEY_ID_REQUEST, requestId);
	}
	public String getpAAltaDocumento() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCALTA_DOCUMENTO);
	}
	public void setpAAltaDocumento(String pAltaDocumento) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCALTA_DOCUMENTO, pAltaDocumento);
	}
	public String getEndpointEnvioDiligencia() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_ENVIO_DILIGENCIA);
	}
	public void setEndpointEnvioDiligencia(String endpointEnvioDiligencia) {
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_ENVIO_DILIGENCIA, endpointEnvioDiligencia);
	}
	public String getNombrePlantillaMensaje() {
		return getValueFromTablaPreferencias(KEY_PREF_PLANTILLA_ANCERT);
	}
	public void setNombrePlantillaMensaje(String nombrePlantilla) {
		setValueIntoTablaPreferencias(KEY_PREF_PLANTILLA_ANCERT, nombrePlantilla);
	}
	public String getPADatosSolicitud()
	{
		return getValueFromTablaPreferencias(KEY_PREF_PROC_SOLICITUD);
	}
	public void setPADatosSolicitud(String nombrepADatosSolicitud)
	{
		setValueIntoTablaPreferencias(KEY_PREF_PROC_SOLICITUD, nombrepADatosSolicitud);
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
	public String getAliasPermisoServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_SERVICIO);
	}
	public String getPAObtenerPlantilla() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCOBTENER_PLANTILLA);
	}
	public void setPAObtenerPlantilla(String pAObtenerPlantilla) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCOBTENER_PLANTILLA, pAObtenerPlantilla);
	}
	public String getPAPermisoServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCPERMISO_SERVICIO);
	}
	public void setPAPermisoServicio(String pAPermisoServicio) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCPERMISO_SERVICIO, pAPermisoServicio);
	}
	public String getNombrePlantillaCertificado() {
		return getValueFromTablaPreferencias(KEY_PREF_PLANTILLA_CERTIFICADO);
	}
	public void setNombrePlantillaCertificado(String nombrePlantilla) {
		setValueIntoTablaPreferencias(KEY_PREF_PLANTILLA_CERTIFICADO, nombrePlantilla);
	}
	public String getPADatInforme() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCDAT_INFORME);
	}
	public void setPADatInforme(String paDatosInforme) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCDAT_INFORME, paDatosInforme);
	}
	public String getPAClaveConsejeria() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCCLAVE_CONSEJERIAS);
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
