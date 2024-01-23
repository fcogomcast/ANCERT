package es.tributasenasturias.indices_fiscales;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import weblogic.auddi.util.InitializationException;


import es.tributasenasturias.indices_fiscales.utils.Utils;
import es.tributasenasturias.indices_fiscales.utils.contextoLlamada.CallContext;
import es.tributasenasturias.indices_fiscales.utils.contextoLlamada.CallContextConstants;
import es.tributasenasturias.indices_fiscales.utils.contextoLlamada.CallContextManager;
import es.tributasenasturias.indices_fiscales.utils.log.LogFactory;
import es.tributasenasturias.indices_fiscales.utils.log.Logger;
import es.tributasenasturias.indices_fiscales.utils.preferencias.Preferencias;
import es.tributasenasturias.indices_fiscales.utils.preferencias.PreferenciasException;
import es.tributasenasturias.indices_fiscales.utils.preferencias.PreferenciasFactory;

/**
 * Servlet implementation class for Servlet: IndicesFiscalesServlet
 *
 */
 public class IndicesFiscalesServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   

/**
	 * 
	 */
	private static final long serialVersionUID = 5047305406112553435L;
static final String ENCODING="ISO-8859-1";
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public IndicesFiscalesServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		throw new UnsupportedOperationException("No se pueden recibir peticiones mediante GET");
	}  	
	/**
	 * Devuelve la petición al Servlet como un array de bytes
	 * @param request {@link HttpServletRequest} con la petición al Servlet
	 * @return
	 */
	private byte[] getPeticion (HttpServletRequest request) throws IOException
	{
		byte[] buffer=new byte[4096];
		byte[] contenido=null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			int leidos;
			while ((leidos=request.getInputStream().read(buffer))!=-1)
			{
				bos.write(buffer, 0, leidos);
			}
			contenido = bos.toByteArray();
		} finally {
			if (bos!=null) {
				try { bos.close();}  catch(Exception e){}
			}
		}
		return contenido;
	}
	/**
	 * Inicializa el contexto de la llamada
	 * @return {@link CallContext} con los datos de Preferencias, Id de sesión y Log
	 * @throws InitializationException
	 */
	private CallContext inicializarContext() throws InitializationException
	{
		CallContext context = CallContextManager.newCallContext();
		Preferencias pref=null;
		try
		{
			pref = PreferenciasFactory.newInstance();}
		catch (PreferenciasException per)
		{
			throw new InitializationException(per, "Error en preferencias.");
		}
		String idSesion  = Utils.getIdLlamada();
		if ("".equals(idSesion))
		{
			throw new InitializationException("No se puede recuperar el identificador de sesión.");
		}
		Logger log = LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogAplicacion(), idSesion);
		context.setItem(CallContextConstants.IDSESION, idSesion);
		context.setItem(CallContextConstants.PREFERENCIAS, pref);
		context.setItem(CallContextConstants.LOG,log);
		return context;
	}
	public void replyCliente(INDFISCALREPLY reply, HttpServletResponse response) throws JAXBException, IOException
	{
		JAXBContext jbcontext = JAXBContext.newInstance(INDFISCALREPLY.class.getPackage().getName());
		Marshaller ml = jbcontext.createMarshaller();
		ml.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
		ml.marshal(reply, response.getOutputStream());
	}
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		byte[] zip = getPeticion(request);
		//El fichero zip será pequeño (del orden de Ks), por lo que podemos tenerlo
		//en memoria. Si no, sería mejor  trabajar con el stream mientras se pudiera.
		CallContext context;
		try 
		{
			context= inicializarContext();
		} catch (InitializationException ce)
		{
			System.err.println("IndicesFiscales. Excepción en inicialización");
			ce.printStackTrace(System.err);
			throw new ServletException("Error en implementación de servicio.");
		}
		Logger log = (Logger) context.get(CallContextConstants.LOG);
		log.info("Inicio de la recepción de fichero");
		IndicesFiscalesImpl il = new  IndicesFiscalesImpl(context);
		
		try
		{
			//Log de fichero zip
			new GestorBackups().hacerBackup(zip, context);
			INDFISCALREPLY reply= il.processRequest(zip);
			replyCliente(reply, response);
		} catch (JAXBException jb)
		{
			//Log y respuesta de error por defecto
			log.error("Error, no se puede construir el mensaje de retorno:"+ jb.getMessage());
			log.trace(jb.getStackTrace());
			response.getOutputStream().write(ReplyBuilder.buildDefaultErrorResponse());
		}
		catch (Exception e)
		{
			log.error("Excepción no controlada:"+ e.getMessage());
			log.trace(e.getStackTrace());
			response.getOutputStream().write(ReplyBuilder.buildDefaultErrorResponse());
		}
		finally {
			if (log!=null)
			{
				log.info("Fin de la recepción de fichero");
			}
		}
	} 
}