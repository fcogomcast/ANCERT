package es.tributasenasturias.services.ancert.recepcionescritura.seguridad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.tributasenasturias.services.ancert.recepcionescritura.bd.DatosFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.XMLDOMDocumentException;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.XMLDOMUtils;



/**
 * 
 * @author crubencvs
 * Clase que encapsulará la llamada al servicio de autenticación del Principado de Asturias.
 */
public class AutenticacionPAHelper implements IContextReader{

	private Preferencias pref;
	private LogHelper log;
	private CallContext context;
	
	protected AutenticacionPAHelper() {		
		try {
			pref=PreferenciasFactory.newInstance();
		} catch (PreferenciasException e) {
			pref=null;
		}
	}
	protected AutenticacionPAHelper (CallContext context)
	{
		this();
		log = LogFactory.getLogAplicacionContexto(context);
	}
	
	private String getIdentificadorRespuesta (String xml) throws XMLDOMDocumentException 
	{
		Document doc =null; 
		NodeList nodos=null;
		NodeList nodosFault=null;
		Node nodoId=null;
		String identificador =null;
		doc = XMLDOMUtils.parseXml (xml);
		//Comprobamos si ha habido un problema. Si es así, no se continúa.
		nodosFault=XMLDOMUtils.getAllNodes (doc,"Fault");
		if (nodosFault.getLength()==0)
		{
			//Recuperamos el nodo "item" que contiene el identificador. Probamos primero con CIF.
			nodos = XMLDOMUtils.getAllNodesCondicion(doc,"//item[contains(name,'CIF')]");
			//Si no existe, probamos con NIF
			if (nodos.getLength()!=0) 
			{
				Node nodo = nodos.item(0);//Sólo se espera uno.
				nodoId = XMLDOMUtils.getFirstChildNode(nodo, "value");
				identificador = XMLDOMUtils.getNodeText(nodoId);
			}
			else //NIF
			{
				nodos = XMLDOMUtils.getAllNodesCondicion(doc,"//item[contains(name,'NIF')]");
				if (nodos.getLength()!=0)
				{
					Node nodo = nodos.item(0);
					nodoId = XMLDOMUtils.getFirstChildNode(nodo, "value");
					identificador = XMLDOMUtils.getNodeText(nodoId);
				}
				else
				{
					identificador=null;
				}
			}
		}
		else
		{
			identificador=null;
		}
		return identificador;
		
	}
	/**
	 * Construye la petición en función del esqueleto XML que tendremos en fichero.
	 * @return String que representa el XML de la petición a enviar al servicio de autenticación.
	 */
	private String construyePeticion(String certificado) throws SeguridadException
	{
		String xml=null;
		try
		{
			String plantillaXML = DatosFactory.newGestorCertificadosBD(context).recuperaPlantillaCertificado();
			Document doc =null; 
			doc = XMLDOMUtils.parseXml(plantillaXML);
			NodeList nodos = XMLDOMUtils.getAllNodes(doc,"certificate");
			Node nodoCer=null;
			if (nodos.getLength()!=0)
			{
				nodoCer = nodos.item(0);
				XMLDOMUtils.setNodeText(doc,nodoCer, certificado);
			}				
			//Ahora se sustituye el valor de la IP.
			nodos = XMLDOMUtils.getAllNodesCondicion(doc,"//item[contains(name,'IP')]");
			//Dentro de los objetos "item", queremos aquel que tenga un hijo "Name" con valor "IP".
			if (nodos.getLength()!=0)
			{
				Node nodo=nodos.item(0); //Sólo esperamos un "item" que contenga un "name" "IP".
				Node nodoIpValor = XMLDOMUtils.getFirstChildNode(nodo, "value");
				XMLDOMUtils.setNodeText(doc,nodoIpValor, pref.getIpAutorizacion());
			}
			xml = XMLDOMUtils.getXMLText(doc);
		}
		catch (XMLDOMDocumentException ex)
		{
			log.error ("Error construyendo la petición contra el servicio de certificado:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			xml=null;
		}
		catch (Exception ex)
		{
			log.error ("Error construyendo la petición contra el servicio de certificado:" + ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			xml=null;
		}
		return xml;
	}
	/**
	 * Realiza la llamada a la función "login" del servicio de autenticación.
	 * @param certificado
	 * @return
	 */
	public String login (String certificado) throws SeguridadException,SystemException
	{
		InputStreamReader in = null;
		OutputStream out=null;
        BufferedReader buf = null;
		try
		{
			String peti = construyePeticion(certificado);
			String dirServicio = pref.getEndPointAutenticacion();
			
			if (dirServicio!=null && !dirServicio.equals(""))
			{		URL url= new URL(dirServicio);
					HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
					//se trae el contenido del fichero a pasar como petición.
			        //Se recuperan los bytes que conforman la petición.
					byte []petiB = peti.getBytes("UTF-8");
					//Se monta la conexión
			        con.setRequestProperty( "Content-Length", String.valueOf( petiB.length ) );
			        con.setRequestProperty("Content-Type","text/xml; charset=utf-8");
			        con.setRequestMethod( "POST" );
			        con.setDoOutput(true); //Se usa la conexión para salida
			        con.setDoInput(true); // Se usa paraa entrada.
			        //Se envían los datos a la URL remota.
			        out = con.getOutputStream();
			        out.write(petiB); //Enviamos la petición.
			        out.close();
			        //Leemos lo que nos ha devuelto.
			        in = new InputStreamReader(con.getInputStream());
			        buf = new BufferedReader (in);
			        StringBuilder xmlResp = new StringBuilder();
			        String linea;
			        while ((linea=buf.readLine())!=null)
			        {
			        	xmlResp.append(linea);
			        }
			        
			        buf.close();
			        if (xmlResp==null)
			        {
			        	log.debug("El mensaje de vuelta del servicio de certificado es nulo.");
			        }
			        else
			        {
			        	log.debug("El mensaje de vuelta del servicio de certificado es "+ xmlResp.toString());
			        }
			        //Recuperamos el CIF o NIF.
			        String id=getIdentificadorRespuesta(xmlResp.toString());
			        return id;
			}
		}
		catch (MalformedURLException ex)
		{
			throw new SystemException ("Error al verificar el certificado. "+ ex.getMessage(), ex);
		}		
		catch (IOException ex)
		{			
			throw new SystemException ("Error al verificar el certificado. " + ex.getMessage(),ex);
		}
		catch (Exception ex)
		{
			throw new SystemException ("Error al verificar el certificado. "+ex.getMessage(),ex);
		}
		finally
		{
			if  (buf!=null)
			{
				try
				{
					buf.close();
				}
				catch (IOException ex)
				{
					
				}
			}
			if (in!=null)
			{
				try
				{
					in.close();
				}
				catch (IOException ex)
				{
					
				}
			}
			if (out!=null)
			{
				try
				{
					out.close();
				}
				catch (IOException ex)
				{
					
				}
			}
		}
		return null;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;		
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}
}
