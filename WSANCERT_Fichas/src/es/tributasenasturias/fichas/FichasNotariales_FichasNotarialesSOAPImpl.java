package es.tributasenasturias.fichas;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

import es.tributasenasturias.fichas.log.ILog;
import es.tributasenasturias.fichas.preferencias.Preferencias;

/**
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.3-06/19/2008
 * 07:03 PM(bt) Generated source version: 2.1
 * 
 */
@WebService(portName = "FichasNotarialesSOAP", serviceName = "FichasNotariales", targetNamespace = "http://services.tributasenasturias.es/FichasNotariales/", wsdlLocation = "/wsdls/WSDLAncertFichas.wsdl", endpointInterface = "es.tributasenasturias.fichas.FichasNotariales")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain (file="HandlerChain.xml")
public class FichasNotariales_FichasNotarialesSOAPImpl implements
		FichasNotariales {

	@Resource WebServiceContext wContext;
	
	public FichasNotariales_FichasNotarialesSOAPImpl() {
	}

	/**
	 * 
	 * @param codigo
	 * @param codigoAyuntamiento
	 * @param autorizacionEnvioDiligencias
	 * @param origenFicha
	 * @param mensaje
	 * @param esError
	 * @param contenidoArchivoComprimido
	 * @param idEscritura
	 */
	public void altaFicha(Holder<IdEscrituraType> idEscritura,
			String origenFicha, String codigoAyuntamiento,
            String autorizacionEnvioDiligencias,
			byte[] contenidoArchivoComprimido, Holder<Boolean> esError,
			Holder<String> codigo, Holder<String> mensaje) {
		ILog log=null;
		try
		{
			Preferencias pref= (Preferencias) wContext.getMessageContext().get(Constantes.PREFERENCIAS);
			if (pref==null)
			{
				pref = new Preferencias();
			}
			log= (ILog) wContext.getMessageContext().get(Constantes.LOG);
			String idLlamada = (String) wContext.getMessageContext().get(Constantes.IDSESION);
			FichasNotarialesImpl im = new FichasNotarialesImpl();
			ResultadoAltaFicha res =  im.altaFicha(idEscritura.value, 
											  origenFicha, 
											  codigoAyuntamiento, 
											  autorizacionEnvioDiligencias,
											  contenidoArchivoComprimido, 
											  pref, 
											  log, 
											  idLlamada);
			esError.value = res.isEsError();
			codigo.value= res.getCodigo();
			mensaje.value= res.getMensaje();
		}
		catch (Exception e)
		{
			if (log!=null)
			{	
				log.error("Error no controlado en endpoint de Recepción de fichas notariales:" + e.getMessage());
				log.trace(e.getStackTrace());
			}

			throw new WebServiceException ("Error no controlado en endpoint de recepción de fichas notariales.");
		}
	}

}
