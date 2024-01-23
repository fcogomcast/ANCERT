package es.tributasenasturias.services.ancert.recepcionescritura.preferencias;

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
	private final String FICHERO_PREFERENCIAS = "preferenciasRecepcionEscrituras.xml";
	private final String DIRECTORIO_PREFERENCIAS = "proyectos/WSANCERTRecepcionEscritura";
 
	private HashMap<String, String> tablaPreferencias = new HashMap<String, String>();
	
	//nombres de las preferencias
	private final static String KEY_PREF_LOG = "ModoLog";
	private final static String KEY_PREF_ENDPOINT_FIRMA = "EndPointFirma";
	private final static String KEY_PREF_ALIAS_CERTIFICADO_FIRMA = "AliasCertificadoFirma";
	private final static String KEY_PREF_NODO_CONTENEDOR_FIRMA = "NodoContenedorFirma";
	private final static String KEY_PREF_NS_NODO_CONTENEDOR_FIRMA = "NamespaceNodoContenedorFirma";
	private final static String KEY_PREF_ID_NODO_FIRMAR = "IDNodoFirmar";
	private final static String KEY_PREF_ENDPOINT_AUTENTICACION_PA = "EndPointAutenticacionPA";
	private final static String KEY_PREF_XML_AUTORIZACION = "XmlAutorizacion";
	private final static String KEY_PREF_IP_AUTORIZACION = "IpAutorizacion";
	private final static String KEY_PREF_EMISOR_ENVIO = "EmisorEnvio";
	private final static String KEY_PREF_CODIGO_SERVICIO = "CodigoServicio";
	private final static String KEY_PREF_TIPO_MENSAJE = "TipoMensaje";
	private final static String KEY_PREF_RECEPTOR_ENVIO = "ReceptorEnvio";
	private final static String KEY_PREF_APLICACION = "Aplicacion";
	private final static String KEY_PREF_OPERACION = "Operacion";
	private final static String KEY_ID_REQUEST = "RequestId";
	private final static String KEY_PREF_ENDPOINT_LANZADOR = "EndPointLanzador";
	private final static String KEY_PREF_ESQUEMA = "EsquemaBaseDatos";
	private final static String KEY_PREF_ENDPOINT_ESCRITURA = "EndPointEscritura";
	private final static String KEY_PREF_VALIDAR_ESQUEMA = "ValidarEsquema";
	private final static String KEY_PREF_PROCPERMISO_SERVICIO = "PAPermisoServicio";
	private final static String KEY_PREF_PLANTILLA_SOLICITUD = "PlantillaSolicitud";
	private final static String KEY_PREF_VALIDA_CERTIFICADO = "ValidaCertificado";
	private final static String KEY_PREF_VALIDA_FIRMA = "ValidaFirma";
	private final static String KEY_PREF_FIRMA_MENSAJE = "FirmaSalida";
	private final static String KEY_PREF_PROCOBTENER_PLANTILLA = "PAObtenerPlantilla";
	private final static String KEY_PREF_PLANTILLA_CERTIFICADO = "PlantillaCertificado";
	private final static String KEY_PREF_PROCESCR_DUPLICADA = "PAEscrituraDuplicada";
	private final static String KEY_PREF_PROCRECUPERA_ID = "PARecuperaIdSolicitud";
	private final static String KEY_PREF_PROCFINALIZA_ESCRITURA = "PAFinalizarRecepcionEscritura";
	private final static String KEY_PREF_ALIAS_SERVICIO = "AliasPermisoServicio";
	private final static String KEY_PREF_PROC_DENEGAR_ESCRITURA = "PADenegacionEscritura";
	private final static String KEY_PREF_ENDPOINT_FICHAS ="EndpointFichasNotariales";
	//CRUBENCVS 47084. 06/02/2023. Algoritmos de firma y  de digest
	private final static String KEY_PREF_SIGN_ALG = "AlgoritmoFirmaMensaje";
	private final static String KEY_PREF_DIG_ALG  = "AlgoritmoDigestMensaje";
	//FIN CRUBENCVS 47084 
	
	
	
	protected Preferencias() throws PreferenciasException
	{		
		cargarPreferencias();
	}
	protected void cargarPreferencias() throws PreferenciasException
 {
		 FileInputStream inputStream=null;
		try
		{
			if(CompruebaFicheroPreferencias())
			{		       
		        inputStream = new FileInputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
		        Preferences.importPreferences(inputStream);
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
		finally
		{
			if (inputStream!=null)
			{
				try{inputStream.close();}catch (Exception ex){}
			}
		}
		
 }
	
	private void InicializaTablaPreferencias()
	{
		
		tablaPreferencias.clear();
		
		tablaPreferencias.put(KEY_PREF_LOG,									"INFO");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_FIRMA,						"http://bus:7101/WSInternos/ProxyServices/PXFirmaDigital");
		tablaPreferencias.put(KEY_PREF_ALIAS_CERTIFICADO_FIRMA,					"Tributas");
		//tablaPreferencias.put(KEY_PREF_CLAVE_FIRMA,							"");
		tablaPreferencias.put(KEY_PREF_NODO_CONTENEDOR_FIRMA,				"ENVIO_ESCRITURA");
		tablaPreferencias.put(KEY_PREF_NS_NODO_CONTENEDOR_FIRMA,			"http://inti.notariado.org/XML/FICHA");
		tablaPreferencias.put(KEY_PREF_ID_NODO_FIRMAR	,			"");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_AUTENTICACION_PA,			"http://bus:7101/WSAutenticacionPA/ProxyServices/PXAutenticacionPA");
		tablaPreferencias.put(KEY_PREF_XML_AUTORIZACION,					"ConsultaCertificados/PeticionAutorizacion.xml");
		tablaPreferencias.put(KEY_PREF_IP_AUTORIZACION,						"10.112.10.35");
		tablaPreferencias.put(KEY_PREF_EMISOR_ENVIO,							"CGN");
		tablaPreferencias.put(KEY_PREF_CODIGO_SERVICIO,							"EP0003");
		tablaPreferencias.put(KEY_PREF_TIPO_MENSAJE,							"1");
		tablaPreferencias.put(KEY_PREF_RECEPTOR_ENVIO,							"PDA");
		tablaPreferencias.put(KEY_PREF_APLICACION,								"APP");
		tablaPreferencias.put(KEY_PREF_OPERACION,								"07");
		tablaPreferencias.put(KEY_ID_REQUEST,									"ENVIO_ESCRITURAS");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_LANZADOR,						"http://bus:7101/WSInternos/ProxyServices/PXLanzador");
		tablaPreferencias.put(KEY_PREF_ESQUEMA,									"EXPLOTACION");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_ESCRITURA,						"http://bus:7101/WSANCERT/ProxyServices/PXEscrituras");
		tablaPreferencias.put(KEY_PREF_VALIDAR_ESQUEMA,							"S");
		tablaPreferencias.put(KEY_PREF_PROCPERMISO_SERVICIO,					"INTERNET.permisoServicio");
		tablaPreferencias.put(KEY_PREF_PLANTILLA_SOLICITUD,						"CGNGENERIC");
		tablaPreferencias.put(KEY_PREF_VALIDA_CERTIFICADO,						"S");
		tablaPreferencias.put(KEY_PREF_VALIDA_FIRMA,							"S");
		tablaPreferencias.put(KEY_PREF_FIRMA_MENSAJE,							"S");
		tablaPreferencias.put(KEY_PREF_PROCOBTENER_PLANTILLA,					"SERVICIOS_WEB_APP.obtenerPlantillaApp");
		tablaPreferencias.put(KEY_PREF_PLANTILLA_CERTIFICADO,					"VALIDCERT");
		tablaPreferencias.put(KEY_PREF_PROCESCR_DUPLICADA,						"ANCERT.escrituraDuplicadaExterno");
		tablaPreferencias.put(KEY_PREF_PROCRECUPERA_ID,							"ANCERT.recuperaidSolicitudExterno");
		tablaPreferencias.put(KEY_PREF_PROCFINALIZA_ESCRITURA,					"ANCERT.finalizarRecEscrituraExterno");
		tablaPreferencias.put(KEY_PREF_ALIAS_SERVICIO,							"AN_DILIGEN");
		tablaPreferencias.put(KEY_PREF_PROC_DENEGAR_ESCRITURA,					"ANCERT.denegarEscrituraExterno");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_FICHAS,							"http://bus:7101/WSANCERT/ProxyServices/PXFichasNotariales");
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
 	
 	//Logger.debug("Se ha pedido la preferencia '"+key+"' a lo que el sistema devuelve '"+toReturn+"'");
 	
 	return toReturn;
 }
 
 private void setValueIntoTablaPreferencias(String key, String value)
 {
 	tablaPreferencias.put(key, value);
 }
	
	// Este método devolverá la instancia de clase.
 /*public static Preferencias getPreferencias () throws PreferenciasException
 {
 	if (_pref==null)
 	{
 		throw new PreferenciasException("No se han podido recuperar las preferencias.");
 	}
 	_pref.recargaPreferencias();
 	return _pref;
 }
 */
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
	/*
	public String getClaveFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_CLAVE_FIRMA);
	}
	public void setClaveFirma(String claveFirma) {
		setValueIntoTablaPreferencias(KEY_PREF_CLAVE_FIRMA, claveFirma);
	}
	*/
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
	public String getXmlAutorizacion() {
		return getValueFromTablaPreferencias(KEY_PREF_XML_AUTORIZACION);
	}
	public void setXmlAutorizacion(String xmlAutorizacion) {
		setValueIntoTablaPreferencias(KEY_PREF_XML_AUTORIZACION, xmlAutorizacion);
	}
	public String getIpAutorizacion() {
		return getValueFromTablaPreferencias(KEY_PREF_IP_AUTORIZACION);
	}
	public void setIpAutorizacion(String ipAutorizacion) {
		setValueIntoTablaPreferencias(KEY_PREF_IP_AUTORIZACION, ipAutorizacion);
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
	public String getRequestId() {
		return getValueFromTablaPreferencias(KEY_ID_REQUEST);
	}
	public void setRequestId(String requestId) {
		setValueIntoTablaPreferencias(KEY_ID_REQUEST, requestId);
	}
	public String getEmisorEnvio() {
		return getValueFromTablaPreferencias(KEY_PREF_EMISOR_ENVIO);
	}
	public void setEmisorEnvio(String emisorEnvio) {
		setValueIntoTablaPreferencias(KEY_PREF_EMISOR_ENVIO, emisorEnvio);
	}
	public String getReceptorEnvio() {
		return getValueFromTablaPreferencias(KEY_PREF_RECEPTOR_ENVIO);
	}
	public void setReceptorEnvio(String receptorEnvio) {
		setValueIntoTablaPreferencias(KEY_PREF_RECEPTOR_ENVIO, receptorEnvio);
	}
	public String getEndpointEscritura() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_ESCRITURA);
	}
	public void setEndpointEscritura(String endpointEscritura) {
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_ESCRITURA, endpointEscritura);
	}
	public String getValidarEsquema() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDAR_ESQUEMA);
	}
	public void setValidarEsquema(String validar) {
		setValueIntoTablaPreferencias(KEY_PREF_VALIDAR_ESQUEMA, validar);
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
	public String getPAEscrituraDuplicada() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCESCR_DUPLICADA);
	}
	public void setPAEscrituraDuplicada(String escrituraDuplicada) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCESCR_DUPLICADA, escrituraDuplicada );
	}
	public String getPARecuperaIdSolicitud() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCRECUPERA_ID);
	}
	public void setPARecuperaIdSolicitud(String recuperaIdSolicitud) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCRECUPERA_ID, recuperaIdSolicitud );
	}
	public String getPAFinalizarEscritura() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCFINALIZA_ESCRITURA);
	}
	public void setPAFinalizarEscritura(String finalizarEscritura) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCFINALIZA_ESCRITURA, finalizarEscritura );
	}
	public String getAliasPermisoServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_SERVICIO);
	}
	public String getPADenegacionEscritura() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_DENEGAR_ESCRITURA);
	}
	
	public String getEndpointFichas() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_FICHAS);
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
