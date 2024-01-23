package org.notariado.inti.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.notariado.inti.COMUNICACION;
import org.notariado.inti.exceptions.ConstructorRespuestaException;
import org.notariado.inti.mensajes.ConstructorMensajeRespuestaConsulta;
import org.notariado.inti.mensajes.MensajesUtil;
import org.notariado.inti.preferencias.Preferencias;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Utils {
	
	private static Node getNodoValor (Node nodo)
	{
		Node nodoValor=null;
		Node hijo=null;
		if (nodo!=null)
		{
			if (nodo.hasChildNodes())
			{
				for (hijo=nodo.getFirstChild();hijo!=null;hijo=hijo.getNextSibling())
				{
					if (hijo.getNodeType()==Node.TEXT_NODE||hijo.getNodeType()==Node.CDATA_SECTION_NODE)
					{
						nodoValor = hijo;// El texto del nodo está en el nodo texto hijo.
						break;
					}
				}
			}
		}
		return nodoValor;
	}
	private static  void setNodeText (Document doc,Node nodo, String texto)
	{
		Node valorAnterior = getNodoValor (nodo);
		if (valorAnterior != null) //Existía un nodo previo con valor
		{
			valorAnterior.setNodeValue(texto);
		}
		else
		{
			Node textN = doc.createTextNode(texto);
			nodo.appendChild(textN);
		}
	}
	private static String hexEncode( byte[] aInput){
		int MASK_240 = 0xf0;
		int SHIFT_DIVIDIR_16 = 4;
		int MASK_15 = 0x0f;
	    StringBuilder result = new StringBuilder();
	    char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
	    for ( int idx = 0; idx < aInput.length; ++idx) {
	      byte b = aInput[idx];
	      result.append( digits[ (b&MASK_240) >> SHIFT_DIVIDIR_16 ] );
	      result.append( digits[ b&MASK_15] );
	    }
	    return result.toString();
	  }
	/**
	 * Recupera un checkcum basado en un identificador único. Se asegura que el identificador es único.
	 */
	public static String getIdLlamada()
	{
		UUID id=UUID.randomUUID();
		String identificador=null;
		byte[] mensaje = id.toString().getBytes();
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(mensaje);
			identificador=hexEncode(md.digest());

		}catch(NoSuchAlgorithmException nsae){
			identificador="";
		}
		return identificador;
	}
	/**
	 * Genera un SOAP Fault
	 * @param msg Mensaje SOAP sobre el que se generará el error. Se utilizará para añadirle la sección SOAP Fault 
	 * @param pFaultString Texto de razón de error (aparecerá en el faultString  )
	 */
	public static final SOAPFaultException createSOAPFaultException(String pFaultString) throws SOAPException
	 {
		 try
		 {
		 SOAPFaultException sex=null;
		 SOAPFault fault = SOAPFactory.newInstance().createFault();
		 fault.setFaultString(pFaultString);
		 fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "WSConsultaPagoDeudasIBI"));
		 sex= new SOAPFaultException(fault);
		 return sex;
		 }
		 catch (Exception ex)
		 {
			 throw new SOAPException ("WSConsultaPagoDeudasIBI:==> Error grave al construir la excepción SOAP." + ex.getMessage());
		 }
	} 
	/**
	 * Genera un SOAP Fault. El fault contendrá una sección de detalle.
	 * @param msg Mensaje SOAP sobre el que se generará el error. Se utilizará para añadirle la sección SOAP Fault 
	 * @param reason Texto de razón de error (aparecerá en el faultString  )
	 * @param codigo Código de error que aparecerá en el detalle.
	 * @param mensaje Mensaje que aparecerá en el detalle.
	 * @param server true si el SOAPFault está producido por un error en el proceso de servidor, false si está producido por un error en los datos enviados por el cliente. 
	 */
	@SuppressWarnings("unchecked")
	public static void generateSOAPErrMessage(SOAPMessage msg, String reason, String codigo, String mensaje,boolean server) {
	       try {
	    	  SOAPEnvelope soapEnvelope= msg.getSOAPPart().getEnvelope();
	          SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
	          SOAPFault soapFault = soapBody.addFault();
	          if (server)
	          {
	        	  soapFault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE,"Server"));
	          }
	          else
	          {
	        	  soapFault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE,"Client"));
	          }
	          soapFault.setFaultString(reason);
	          Detail det= soapFault.addDetail();
	          Name name = soapEnvelope.createName("id");
	          det.addDetailEntry(name);
	          
	          name = soapEnvelope.createName("mensaje");
	          det.addDetailEntry(name);
	          DetailEntry entry;
	          Iterator<DetailEntry> it=det.getDetailEntries();
	          while (it.hasNext())
	          {
	        	  entry=it.next();
	        	  if (entry.getLocalName().equals("id"))
	        	  {	
	        		setNodeText (entry.getOwnerDocument(),entry, codigo);  
	        	  }
	        	  if (entry.getLocalName().equals("mensaje"))
	        	  {	
	        		setNodeText (entry.getOwnerDocument(),entry, mensaje);  
	        	  }
	        		  
	          }
	          throw new SOAPFaultException(soapFault); 
	       }
	       catch(SOAPException e) { }
	} 

	public static void generarErrorBody(ConstructorMensajeRespuestaConsulta cons,boolean generico, SOAPMessage msg, String mensaje, Preferencias pref, MensajesUtil mensajes )
	{
		if (pref==null || mensajes==null)
		{
			generateSOAPErrMessage(msg,"Error de plataforma al construir el mensaje de respuesta.","001",mensaje,true);
		}
		COMUNICACION com=null;
		try 
		{
		if (generico)
		{
			com=cons.buildResultadoErrorGenerico(mensaje);
		}
		else
		{
				com=cons.buildResultadoErrorTemporal(mensaje);
		}

			msg.getSOAPBody().removeContents();
			JAXBContext jxb = JAXBContext.newInstance("org.notariado.inti");
			Marshaller mr = jxb.createMarshaller();
			mr.marshal(com, msg.getSOAPBody());
		} catch (JAXBException e) {
			generateSOAPErrMessage(msg,"Error de plataforma al construir el mensaje de respuesta.","001",mensaje,true);
		}catch (SOAPException e) {
			generateSOAPErrMessage(msg,"Error de plataforma al construir el mensaje de respuesta.","001",mensaje,true);
		} catch (ConstructorRespuestaException e) {
			generateSOAPErrMessage(msg,"Error de plataforma al construir el mensaje de respuesta.","001",mensaje,true);
		}
	}
	/**
	 * Convierte un xmlgregorian calendar a cadena DD/MM/YYYY
	 * @param cal Objeto XMLGregorianCalendar
	 * @param format formato de la cadena  resultado
	 * @return cadena en format DD/MM/YYYY correspondiente a la fecha de calendario de entrada.
	 */ 
	public static String calendarToString (XMLGregorianCalendar cal, String format)
	{
		TimeZone zona = TimeZone.getTimeZone("Europe/Madrid");
		Calendar c= cal.toGregorianCalendar(zona, new Locale("es","ES"), null);
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(c.getTime());
	}
	

}
