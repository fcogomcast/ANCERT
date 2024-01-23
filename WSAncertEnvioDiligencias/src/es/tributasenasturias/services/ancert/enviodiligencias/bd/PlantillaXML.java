package es.tributasenasturias.services.ancert.enviodiligencias.bd;

import java.util.HashMap;


import es.tributasenasturias.services.ancert.enviodiligencias.SystemException;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.Utils;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.lanzador.LanzaPL;
import es.tributasenasturias.services.lanzador.LanzaPLService;

/** Implementa la recuperación de plantillas XML para crear mensajes.
 * 
 * @author crubencvs
 *
 */
public class PlantillaXML implements IContextReader{
	private CallContext context;
	protected PlantillaXML()
	{
		super();
	}
	//** Para convertir de lo que nos devuelve el lanzador a un xml con tags correctos.
	  private static HashMap<String,String> htmlEntities;
	  static {
	    htmlEntities = new HashMap<String,String>();
	    htmlEntities.put("&lt;","<")    ; htmlEntities.put("&gt;",">");
	    htmlEntities.put("&amp;","&")   ; htmlEntities.put("&quot;","\"");
	    htmlEntities.put("&agrave;","à"); htmlEntities.put("&Agrave;","À");
	    htmlEntities.put("&acirc;","â") ; htmlEntities.put("&auml;","ä");
	    htmlEntities.put("&Auml;","Ä")  ; htmlEntities.put("&Acirc;","Â");
	    htmlEntities.put("&aring;","å") ; htmlEntities.put("&Aring;","Å");
	    htmlEntities.put("&aelig;","æ") ; htmlEntities.put("&AElig;","Æ" );
	    htmlEntities.put("&ccedil;","ç"); htmlEntities.put("&Ccedil;","Ç");
	    htmlEntities.put("&eacute;","é"); htmlEntities.put("&Eacute;","É" );
	    htmlEntities.put("&egrave;","è"); htmlEntities.put("&Egrave;","È");
	    htmlEntities.put("&ecirc;","ê") ; htmlEntities.put("&Ecirc;","Ê");
	    htmlEntities.put("&euml;","ë")  ; htmlEntities.put("&Euml;","Ë");
	    htmlEntities.put("&iuml;","ï")  ; htmlEntities.put("&Iuml;","Ï");
	    htmlEntities.put("&ocirc;","ô") ; htmlEntities.put("&Ocirc;","Ô");
	    htmlEntities.put("&ouml;","ö")  ; htmlEntities.put("&Ouml;","Ö");
	    htmlEntities.put("&oslash;","ø") ; htmlEntities.put("&Oslash;","Ø");
	    htmlEntities.put("&szlig;","ß") ; htmlEntities.put("&ugrave;","ù");
	    htmlEntities.put("&Ugrave;","Ù"); htmlEntities.put("&ucirc;","û");
	    htmlEntities.put("&Ucirc;","Û") ; htmlEntities.put("&uuml;","ü");
	    htmlEntities.put("&Uuml;","Ü")  ; htmlEntities.put("&nbsp;"," ");
	    htmlEntities.put("&copy;","\u00a9");
	    htmlEntities.put("&reg;","\u00ae");
	    htmlEntities.put("&euro;","\u20a0");
	  }

	  private String unescapeHTML(String source) {
	      int i, j;

	      boolean continueLoop;
	      int skip = 0;
	      do {
	         continueLoop = false;
	         i = source.indexOf("&", skip);
	         if (i > -1) {
	           j = source.indexOf(";", i);
	           if (j > i) {
	             String entityToLookFor = source.substring(i, j + 1);
	             String value = (String) htmlEntities.get(entityToLookFor);
	             if (value != null) {
	               source = source.substring(0, i)
	                        + value + source.substring(j + 1);
	               continueLoop = true;
	             }
	             else if (value == null){
	                skip = i+1;
	                continueLoop = true;
	             }
	           }
	         }
	      } while (continueLoop);
	      return source;
	  }
	  /**
	   * Recupera la plantilla de envío de diligencias a ANCERT.
	   * @return Texto XML de la plantilla.
	   * @throws DatosException Si se produce un error al recuperar el texto de la plantilla.
	   */
	public String recuperaPlantillaEnvioDiligencia() 
			throws DatosException,SystemException {
		try {
			Preferencias pref = PreferenciasFactory.newInstance();
			pref.cargarPreferencias();
			ConversorParametrosLanzador cpl;
			cpl = new ConversorParametrosLanzador();
			cpl.setProcedimientoAlmacenado(pref.getPAObtenerPlantilla());
			cpl.setParametro(pref.getNombrePlantillaMensaje(), ConversorParametrosLanzador.TIPOS.String);
			cpl.setParametro ("P",ConversorParametrosLanzador.TIPOS.String);
			
			LanzaPLService lanzaderaWS = new LanzaPLService();
			LanzaPL lanzaderaPort;

			lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();
			// enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
			// Cambiamos el endpoint
			bpr.getRequestContext().put(
					javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					pref.getEndPointLanzador());
			Utils.setHandlerChain(bpr, context);
			String respuesta = new String();
			respuesta = lanzaderaPort.executePL(pref.getEsquemaBaseDatos(), cpl
					.codifica(), "", "", "", "");
			cpl.setResultado(respuesta);
			if (!cpl.getError().equals("")) {
				throw new DatosException(
						"Error recibido de la base de datos en el alta de documento:" + cpl.getError());
			}
			//La plantilla vendrá con caracteres de escape (para poder viajar como XML), los eliminamos.
			String plantilla= cpl.getNodoResultado("C1");
			plantilla = unescapeHTML(plantilla);
			return plantilla;
		} catch (Exception e) {
			throw new SystemException("Excepción al recuperar la plantilla de mensaje para enviar a ANCERT:"
					+ e.getMessage(), e);
		}
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
}
