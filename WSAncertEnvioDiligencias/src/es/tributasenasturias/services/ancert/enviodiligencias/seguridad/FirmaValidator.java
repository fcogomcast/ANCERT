package es.tributasenasturias.services.ancert.enviodiligencias.seguridad;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogAplicacion;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;





/**
 * Validador de firma recibida
 * 
 * 
 */
public class FirmaValidator implements IContextReader{
	
	private CallContext context;
	protected FirmaValidator() {
		
	}
	/**
	 * Indica si un xml contiene una firma XMLdsig válida.
	 * @param xml Cadena que contiene el xml a validar.
	 * @return
	 */
	public boolean firmaValida(String xml) throws SeguridadException{		
		boolean valido = false;
		LogAplicacion log= LogFactory.getLogAplicacionContexto(context);
		try
		{
			//Si las preferencias dicen que no se valide la firma, se da por bueno.
			Preferencias pref = PreferenciasFactory.newInstance();
			if (pref.getValidaFirma().equalsIgnoreCase("N"))
			{
				return true;
			}
			FirmaHelper firma=SeguridadFactory.newFirmaHelper(context);
			
			valido = firma.validaFirma(xml);
		}
		catch (PreferenciasException ex)
		{
			log.error("Se ha producido un error de preferencias al validar la firma:"+ex.getMessage(),ex);
			log.trace(ex.getStackTrace());
			valido = false;
		}
		return valido;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context =ctx;
	}
}
