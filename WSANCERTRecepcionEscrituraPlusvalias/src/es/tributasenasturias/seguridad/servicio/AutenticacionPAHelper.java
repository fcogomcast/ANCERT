package es.tributasenasturias.seguridad.servicio;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.notariado.inti.xml.XMLDOMDocumentException;
import org.notariado.inti.xml.XMLDOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * 
 * @author crubencvs
 * Clase que encapsulará la llamada al servicio de autenticación del Principado de Asturias.
 */
public class AutenticacionPAHelper {

	private String rutaXmlAutenticacion;
	private String ipAutenticacion;
	private String endpointAutenticacion;
	public AutenticacionPAHelper(String rutaXmlAutenticacion, String ipAutenticacion, String endpointAutenticacion) {		
		this.rutaXmlAutenticacion= rutaXmlAutenticacion;
		this.ipAutenticacion= ipAutenticacion;
		this.endpointAutenticacion= endpointAutenticacion;
	}
	
	private String getIdentificadorRespuesta (String xml) {
		Document doc =null; 
		NodeList nodos=null;
		Node nodoId=null;
		String identificador =null;
		try{
			doc = XMLDOMUtils.parseXml (xml);
			//Recuperamos el nodo "item" que contiene el identificador. Probamos primero con CIF.
			nodos = XMLDOMUtils.getAllNodesCondicion(doc,"//item[contains(name,'CIF')]");
			//Si no existe, probamos con NIF
			if (nodos.getLength()!=0){
				Node nodo = nodos.item(0);//Sólo se espera uno.
				nodoId = XMLDOMUtils.getFirstChildNode(nodo, "value");
				identificador = XMLDOMUtils.getNodeText(nodoId);
			}else{ //NIF
				nodos = XMLDOMUtils.getAllNodesCondicion(doc,"//item[contains(name,'NIF')]");
				if (nodos.getLength()!=0){
					Node nodo = nodos.item(0);
					nodoId = XMLDOMUtils.getFirstChildNode(nodo, "value");
					identificador = XMLDOMUtils.getNodeText(nodoId);
				}else{
					identificador=null;
				}
			}
		}catch (XMLDOMDocumentException ex){
			identificador=null;
		}
		return identificador;
		
	}
	/**
	 * Construye la petición en función del esqueleto XML que tendremos en fichero.
	 * @return String que representa el XML de la petición a enviar al servicio de autenticación.
	 */
	private String construyePeticion(String certificado) throws SeguridadException{
		String xml=null;
		try{
			String nombreFichero = rutaXmlAutenticacion;
						
			if (nombreFichero!=null && !nombreFichero.equals("")){
				File fXml = new File (nombreFichero);
				Document doc =null; 
				doc = XMLDOMUtils.parseXML(fXml);
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
					XMLDOMUtils.setNodeText(doc,nodoIpValor, ipAutenticacion);
				}
				xml = XMLDOMUtils.getXMLText(doc);
				
			}
		}
		catch (XMLDOMDocumentException ex){			
			throw new SeguridadException ("Error al construir la petición de autorización: "+ ex.getMessage(),ex);
		}catch (Exception ex){
			throw new SeguridadException ("Error al construir la petición de autorización:"+ex.getMessage(),ex);
		}
		
		return xml;
	}
	/**
	 * Realiza la llamada a la función "login" del servicio de autenticación.
	 * @param certificado
	 * @return
	 */
	public String login (String certificado) throws SeguridadException{
		InputStreamReader in = null;
		OutputStream out=null;
        BufferedReader buf = null;
		try{									 					
			String peti = construyePeticion(certificado);																
			String dirServicio = endpointAutenticacion; 										
			
			if (dirServicio!=null && !dirServicio.equals(""))
			{
					URL url= new URL(dirServicio);
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
			        //Recuperamos el CIF o NIF.
			        String id=getIdentificadorRespuesta(xmlResp.toString());
			        return id;
			}
		}catch (MalformedURLException ex){
			throw new SeguridadException ("Error al verificar la autenticación:"+ ex.getMessage(),ex);
		}catch (IOException ex){			
			throw new SeguridadException ("Error al verificar la autenticación:"+ ex.getMessage(),ex);
		}catch (Exception ex){			
			throw new SeguridadException ("Error al verificar la autenticación:"+ ex.getMessage(),ex);
		}finally{
			if  (buf!=null){
				try{
					buf.close();
				}catch (IOException ex){}
			}
			if (in!=null){
				try{
					in.close();
				}catch (IOException ex){}
			}
			if (out!=null){
				try{
					out.close();
				}catch (IOException ex){}
			}
		}
		return null;
	}
}

