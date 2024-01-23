package es.tributasenasturias.services.ancert.solicitudEscritura.comunicaciones.entrada;





import java.util.ArrayList;
import java.util.List;

import es.tributasenasturias.services.ancert.solicitudEscritura.EntradaType;
import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.ProtocoloException;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.CallContext;
import es.tributasenasturias.services.ancert.solicitudEscritura.context.IContextReader;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Otorgante;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.ProtocoloDO;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.Solicitante;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.SolicitudDO;
import es.tributasenasturias.services.ancert.solicitudEscritura.OtorgantesType;

public class ExtractorDatosSolicitud implements IContextReader{
	
	private SolicitudDO solicitudData;
	private CallContext context;
	protected ExtractorDatosSolicitud()
	{
		super();
	}
	/**
	 * Recupera los datos de la solicitud, utilizando un objeto {@link SolicitudDO}
	 * @return SolicitudDO
	 */
	public SolicitudDO getSolicitudDO() {
		return solicitudData;
	}
	/**
	 * Extrae los datos de la solicitud de los parámetros de entrada, y los almacena en estructuras
	 * internas del objeto. Se pueden recuperar posteriormente mediante una llamada a getSolicitudDO
	 * @param entrada Parámetros de entrada.
	 */
	public void extraeDatosSolicitud(EntradaType entrada) throws ComunicacionesEntradaException
	{
		SolicitudDO solicitud=new SolicitudDO();
		solicitud.setCodigoNotario(entrada.getCodNotario());
		solicitud.setCodigoNotaria(entrada.getCodNotaria());
		ProtocoloDO prot=new ProtocoloDO();
		try {
			prot.setProtocoloCompuesto(entrada.getProtocolo());
		} catch (ProtocoloException e) {
			throw new ComunicacionesEntradaException ("Error al recuperar los datos del protocolo :"+ e.getMessage(),e,this);
		}
		solicitud.setNumProtocolo(prot.getProtocolo());
		solicitud.setProtocoloBis(prot.getProtocoloBis());
		solicitud.setAnioAutorizacion(entrada.getAnioAutorizacion());
		solicitud.setDestinatario(entrada.getDestinatario());
		solicitud.setFinalidad(entrada.getFinalidad());
		solicitud.setIdOrigen(entrada.getIdOrigen());
		//CRUBENCVS Versión 2 del servicio. 38243
		solicitud.setTipoProcedimiento(entrada.getTipoProcedimiento());
		solicitud.setPersonaSolicitante(new Solicitante (entrada.getPersonaSolicitante().getDniResponsable(),
														 entrada.getPersonaSolicitante().getNombreResponsable(),
														 entrada.getPersonaSolicitante().getCargoResponsable()));
		
		List<Otorgante> otorgantes= new ArrayList<Otorgante>(); 
		for (OtorgantesType o: entrada.getOtorgantes()){
			otorgantes.add(new Otorgante(o.getOtorgante().getDniOtorgante(),
										o.getOtorgante().getNombreOtorgante()));
		}
		solicitud.setOtorgantes(otorgantes);
		solicitud.setNumeroExpediente(entrada.getNumeroExpediente());
		//Fin CRUBENCVS 38243
		solicitudData=solicitud;
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
