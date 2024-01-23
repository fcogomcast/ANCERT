package es.tributasenasturias.servicios.ibi.deuda.preferencias;

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
	private final static String FICHERO_PREFERENCIAS = "prefsConsultaPagoDeudasIBI.xml";
	private final static String DIRECTORIO_PREFERENCIAS = "proyectos/WSConsultaPagoDeudasIBI";
 
	private HashMap<String, String> tablaPreferencias = new HashMap<String, String>();
	
	//nombres de las preferencias
	private final static String KEY_PREF_LOG = "ModoLog";
	private final static String KEY_PREF_ENDPOINT_AUTENTICACION = "EndpointAutenticacion";
	private final static String KEY_PREF_ENDPOINT_LANZADOR = "EndpointLanzador";
	private final static String KEY_PREF_ESQUEMA = "EsquemaBD";
	private final static String KEY_PREF_PROCPERMISO_SERVICIO = "ProcAlmacenadoPermisosServicio";
	private final static String KEY_PREF_LOG_APLICACION = "ficheroLogAplicacion";
	private final static String KEY_PREF_LOG_CLIENT = "ficheroLogClient";
	private final static String KEY_PREF_LOG_SERVER = "ficheroLogServer";
	private final static String KEY_PREF_DEBUG_SOAP = "DebugSOAP";
	private final static String KEY_PREF_PROCDATOS_CONSULTA = "ProcAlmacenadoDatosConsulta";
	private final static String KEY_PREF_ENDPOINT_IMPRESION = "EndpointImpresion";
	private final static String KEY_PREF_FIC_MENSAJES = "ficheroMensajesAplicacion";
	private final static String KEY_PREF_FIC_MAPEO = "ficheroMapeoMensajes";
	private final static String KEY_PREF_ENDPOINT_DOCUMENTO = "EndpointDocumentos";
	private final static String KEY_PREF_FIRMAR_SALIDA = "firmarMensajeSalida";
	private final static String KEY_PREF_VALIDAR_FIRMA = "validarFirmaMensajeEntrada";
	private final static String KEY_PREF_ALIAS_FIRMA = "aliasCertificadoFirma";
	private final static String KEY_PREF_NODO_FIRMAR = "idNodoFirmar";
	private final static String KEY_PREF_NODO_PADRE_FIRMA_CONSULTA = "nodoPadreFirmaConsulta";
	private final static String KEY_PREF_NAMESPACE_NODO_PADRE = "namespaceNodoPadreFirmaConsulta";
	private final static String KEY_PREF_ENDPOINT_FIRMA = "EndpointFirma";
	private final static String KEY_PREF_VALIDA_CERTIFICADO = "validarCertificado";
	private final static String KEY_PREF_VALIDA_PERMISOS = "validarPermisos";
	private final static String KEY_PREF_ALIAS_SERVICIO = "aliasServicio";
	private final static String KEY_PREF_ENDPOINT_SEGURIDAD= "EndpointSeguridadSW";
	private final static String KEY_PREF_ENDPOINT_PAGO= "EndpointPago";
	private final static String KEY_PREF_PROCESTADO_PAGO = "ProcDevolverEstadoPago";
	private final static String KEY_PREF_ENDPOINT_DOCUMENTO_PAGO = "EndpointDocumentosPago";
	private final static String KEY_PREF_PROC_ACT_ESTADO = "ProcActualizarEstadoPago";
	private final static String KEY_PREF_PROC_VERIF_DOIN = "ProcAlmacenadoCodVerifPago";
	private final static String KEY_PREF_PROC_CERT_NODEUDA = "ProcAlmacenadoCertNoDeuda";
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
		tablaPreferencias.put(KEY_PREF_ENDPOINT_AUTENTICACION,				    "http://bus:7101/WSAutenticacionPA/ProxyServices/PXAutenticacionEPST");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_LANZADOR,						"http://bus:7101/WSInternos/ProxyServices/PXLanzador");
		tablaPreferencias.put(KEY_PREF_ESQUEMA,									"EXPLOTACION");
		tablaPreferencias.put(KEY_PREF_ALIAS_SERVICIO,							"ANCERT_IBI");
		tablaPreferencias.put(KEY_PREF_PROCPERMISO_SERVICIO,					"INTERNET.permisoServicio");
		tablaPreferencias.put(KEY_PREF_LOG_APLICACION,						    "proyectos/WSConsultaPagoDeudasIBI/logs/Application.log");
		tablaPreferencias.put(KEY_PREF_LOG_CLIENT,						     	"proyectos/WSConsultaPagoDeudasIBI/logs/Soap_Client.log");
		tablaPreferencias.put(KEY_PREF_LOG_SERVER ,								"proyectos/WSConsultaPagoDeudasIBI/logs/Soap_Server.log");
		tablaPreferencias.put(KEY_PREF_DEBUG_SOAP,								"N");
		tablaPreferencias.put(KEY_PREF_PROCDATOS_CONSULTA ,						"ANCERT_IBI.consultaDeudasIbiAncert");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_IMPRESION,						"http://bus:7101/WSInternos/ProxyServices/PXDocumentosPago");
		tablaPreferencias.put(KEY_PREF_FIC_MENSAJES ,							"proyectos/WSConsultaPagoDeudasIBI/mensajes.xml");
		tablaPreferencias.put(KEY_PREF_FIC_MAPEO ,								"proyectos/WSConsultaPagoDeudasIBI/mapeomensajes.xml");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_DOCUMENTO ,						"http://bus:7101/WSInternos/ProxyServices/PXConsultaDoinDocumentos");
		tablaPreferencias.put(KEY_PREF_FIRMAR_SALIDA ,							"S");
		tablaPreferencias.put(KEY_PREF_VALIDAR_FIRMA ,							"S");
		tablaPreferencias.put(KEY_PREF_ALIAS_FIRMA ,							"Tributas");
		tablaPreferencias.put(KEY_PREF_NODO_FIRMAR ,							"REPLY");
		tablaPreferencias.put(KEY_PREF_NODO_PADRE_FIRMA_CONSULTA ,				"CONSULTA_DEUDAS");
		tablaPreferencias.put(KEY_PREF_NAMESPACE_NODO_PADRE ,					"http://inti.notariado.org/XML/CD");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_FIRMA ,							"http://bus:7101/WSInternos/ProxyServices/PXFirmaDigital");
		tablaPreferencias.put(KEY_PREF_VALIDA_CERTIFICADO ,						"S");
		tablaPreferencias.put(KEY_PREF_VALIDA_PERMISOS ,						"S");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_SEGURIDAD ,						"http://bus:7101/WSInternos/ProxyServices/PXSeguridadWS");
		tablaPreferencias.put(KEY_PREF_PROCESTADO_PAGO ,						"ANCERT_IBI.devuelve_estado_pago");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_PAGO ,							"http://bus:7101/WSPasarelaPago/ProxyServices/PXPasarelaPagoST");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_DOCUMENTO_PAGO ,				"http://bus:7101/WSInternos/ProxyServices/PXDocumentosPago");
		tablaPreferencias.put(KEY_PREF_PROC_ACT_ESTADO ,						"ANCERT_IBI.actualizaEstadoPagoIBI");
		tablaPreferencias.put(KEY_PREF_PROC_VERIF_DOIN ,						"ANCERT_IBI.get_cod_verif_justpago");
		tablaPreferencias.put(KEY_PREF_PROC_CERT_NODEUDA ,						"ANCERT_IBI.imprime_cert_nodeuda");
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
 	
 	return toReturn;
 }
 
 
	public String getEsquemaBD() {
		return getValueFromTablaPreferencias(KEY_PREF_ESQUEMA);
	}
    public String getEndpointLanzador() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_LANZADOR);
	}
	public String getModoLog() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG);
	}
	public String getEndpointAutenticacion() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_AUTENTICACION);
	}
	public String getAliasServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_SERVICIO);
	}
	public String getProcAlmacenadoPermisosServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCPERMISO_SERVICIO);
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
	public String getDebugSOAP() {
		return getValueFromTablaPreferencias(KEY_PREF_DEBUG_SOAP);
	}
	public String getProcAlmacenadoDatosConsulta() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCDATOS_CONSULTA);
	}
	public String getProcAlmacenadoEstadoPago() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCESTADO_PAGO);
	}
	public String getEndpointImpresion() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_IMPRESION);
	}
	public String getFicheroMensajesAplicacion() {
		return getValueFromTablaPreferencias(KEY_PREF_FIC_MENSAJES);
	}
	public String getFicheroMapeoMensajes() {
		return getValueFromTablaPreferencias(KEY_PREF_FIC_MAPEO);
	}
	public String getEndpointDocumentos() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_DOCUMENTO);
	}
	
	public String getFirmarSalida() {
		return getValueFromTablaPreferencias(KEY_PREF_FIRMAR_SALIDA);
	}
	public String getValidarFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDAR_FIRMA);
	}
	public String getAliasFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_FIRMA);
	}
	public String getNodoFirmar() {
		return getValueFromTablaPreferencias(KEY_PREF_NODO_FIRMAR);
	}
	public String getNodoPadreFirmaConsulta() {
		return getValueFromTablaPreferencias(KEY_PREF_NODO_PADRE_FIRMA_CONSULTA);
	}
	public String getNamespaceNodoPadre() {
		return getValueFromTablaPreferencias(KEY_PREF_NAMESPACE_NODO_PADRE);
	}
	public String getEndpointFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_FIRMA);
	}
	public String getValidarCertificado() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_CERTIFICADO);
	}
	public String getValidarPermisos() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_PERMISOS);
	}
	public String getEndpointDocumentosPago() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_DOCUMENTO_PAGO);
	}
	public String getEndpointSeguridadSW() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_SEGURIDAD);
	}
	public String getEndpointPago() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_PAGO);
	}
	public String getProcActualizaEstado() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_ACT_ESTADO);
	}
	public String getProcCodVerifJustPago() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_VERIF_DOIN);
	}
	public String getProcCertNoDeuda() {
		return getValueFromTablaPreferencias(KEY_PREF_PROC_CERT_NODEUDA);
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
