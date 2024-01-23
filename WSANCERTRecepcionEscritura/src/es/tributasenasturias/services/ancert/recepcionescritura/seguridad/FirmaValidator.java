package es.tributasenasturias.services.ancert.recepcionescritura.seguridad;


import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.factory.DomainObjectsFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasException;
import es.tributasenasturias.services.ancert.recepcionescritura.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.utils.FirmaHelper;


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
		LogHelper log= LogFactory.getLogAplicacionContexto(context);
		try
		{
			//Si las preferencias dicen que no se valide la firma, se da por bueno.
			Preferencias pref = PreferenciasFactory.newInstance();
			if (pref.getValidaFirma().equalsIgnoreCase("N"))
			{
				return true;
			}
			FirmaHelper firma=new DomainObjectsFactory().newFirmaHelper(context);
			
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
