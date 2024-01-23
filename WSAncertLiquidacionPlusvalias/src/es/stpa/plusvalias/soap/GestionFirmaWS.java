package es.stpa.plusvalias.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;



import es.stpa.plusvalias.Constantes;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.preferencias.PreferenciasException;
import es.stpa.plusvalias.wssecurity.WSSecurityException;
import es.stpa.plusvalias.wssecurity.WSSecurityFactory;
import es.tributasenasturias.log.TributasLogger;
import es.tributasenasturias.xml.XMLDOMDocumentException;
import es.tributasenasturias.xml.XMLDOMUtils;


public class GestionFirmaWS implements SOAPHandler<SOAPMessageContext>{
	
	/**
	 * Recupera el contenido del mensaje SOAP de entrada, sin los adjuntos.
	 * @param message
	 * @return
	 * @throws SOAPException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	private byte[] contenidoMensajeSinAdjuntos(SOAPMessage message) throws SOAPException, TransformerFactoryConfigurationError, TransformerException{
		Source src = message.getSOAPPart().getContent();
		Transformer tr = TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT,"no");
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
		StreamResult dest = new StreamResult(baos);
		tr.transform(src, dest);
		return baos.toByteArray();
		
	}
	/**
	 * Gestiona la firma de mensaje de entrada al servicio, según WS-Security.
	 * @param context Contexto del mensaje SOAP.
	 */
	protected void gestionFirma(SOAPMessageContext context)
	{
		TributasLogger logAplicacion=null;
		ByteArrayInputStream bi=null;
		ByteArrayOutputStream bo=null;
		try
		{
			Preferencias pref = (Preferencias) context.get(Constantes.PREFERENCIAS);
			String idSesion = (String) context.get(Constantes.IDSESION);
			boolean debug= false;
			if (idSesion==null)
			{
				idSesion = "Sin sesión";
			}
			if (pref==null)
			{
				pref=new Preferencias();
			}
			logAplicacion= new TributasLogger(pref.getModoLog(),pref.getDirectorioRaizLog(),pref.getFicheroLogAplicacion(), idSesion);
			
			debug= "DEBUG".equalsIgnoreCase(pref.getModoLog()) || "ALL".equalsIgnoreCase(pref.getModoLog());
			
			//Comprobamos si firmamos o validamos.
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (salida.booleanValue())
			{
				//Firmamos.
				if ("S".equalsIgnoreCase(pref.getFirmarSalida()))
				{
					if (debug)
					{
						logAplicacion.debug("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Se firma.");
					}
					//Recuperamos el texto de mensaje.
					bo=new ByteArrayOutputStream();
					context.getMessage().writeTo(bo);
					//Recuperamos qué mensaje firmamos, el de consulta o pago.
					String xmlFirmado = XMLDOMUtils.getPrettyXMLText(context.getMessage().getSOAPPart());
					xmlFirmado=WSSecurityFactory.newConstructorResultado(pref,new SoapClientHandler(idSesion)).firmaMensajeTimestamp(xmlFirmado, Integer.parseInt(pref.getIntervaloTimestamp()));
					if (debug)
					{
						logAplicacion.debug("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Mensaje firmado:"+xmlFirmado);
					}
					//bi = new ByteArrayInputStream(xmlFirmado.getBytes("ISO-8859-1"));
					bi = new ByteArrayInputStream(xmlFirmado.getBytes());
					context.getMessage().getSOAPPart().setContent(new StreamSource(bi));
				}
			}
			else
			{
				//Validamos.
				if ("S".equalsIgnoreCase(pref.getValidarFirma()))
				{
					if (debug)
					{
						logAplicacion.debug("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Validamos la firma WSSecurity.");
					}
					//bo=new ByteArrayOutputStream();
					byte[] mensajeSinAdjuntos= contenidoMensajeSinAdjuntos(context.getMessage());
					//context.getMessage().writeTo(bo);
					if (debug)
					{
						logAplicacion.debug("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Mensaje para validar firma WSSecurity:" + new String(mensajeSinAdjuntos));
					}
					//boolean firmaCorrecta = WSSecurityFactory.newConstructorResultado(pref,new SoapClientHandler(idSesion)).validaFirmaSinCertificado(new String(bo.toByteArray()));
					boolean firmaCorrecta = WSSecurityFactory.newConstructorResultado(pref,new SoapClientHandler(idSesion)).validaFirmaSinCertificado(new String(mensajeSinAdjuntos));
					if (firmaCorrecta)
					{
						logAplicacion.info("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Firma WSSecurity validada");
					}
					else
					{
						logAplicacion.error("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Firma de mensaje WSSecurity no válida.");
						SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error al validar la firma WSSecurity", "0002", "SOAP Handler",false);
					}
				}
			}
		}catch (PreferenciasException e)
		{
			//Si no hay preferencias, no hay log.
			//if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error en preferencias:"+e.getMessage(),e);}
			System.err.println ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error en preferencias:"+e.getMessage());
			SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad de mensaje.", "0002", "SOAP Handler",true);
		}
		 catch (SOAPException e) {
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		} catch (IOException e) {
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		} 
		catch (TransformerFactoryConfigurationError e) {
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		}
		catch (TransformerException e) {
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		}
		catch (WSSecurityException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		} catch (XMLDOMDocumentException e)
		{
			if (logAplicacion!=null) {logAplicacion.error ("Manejador SOAP:[" + GestionFirmaWS.class.getName()+"].Error:"+e.getMessage(),e);}
			SOAPUtils.generateSOAPErrMessage(context.getMessage(), "Error en seguridad SOAP.", "0002", "SOAP Handler", false);
		}
		finally
		{
			if (bi!=null)
			{
				try {bi.close();} catch(Exception e){}
			}
			if (bo!=null)
			{
				try {bo.close();} catch (Exception e){}
			}
		}
	}
	@Override
	public Set<QName> getHeaders() {
		//Indicamos que entendemos la cabecera de seguridad de WS-Security.
		QName security= new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd","Security");
		HashSet<QName> headersEntendidos= new HashSet<QName>();
		headersEntendidos.add(security);
		return headersEntendidos;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		gestionFirma(context);
		return true;
	}
	

}
