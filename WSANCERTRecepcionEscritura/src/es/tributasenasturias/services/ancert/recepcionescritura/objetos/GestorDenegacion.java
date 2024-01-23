package es.tributasenasturias.services.ancert.recepcionescritura.objetos;

import javax.xml.datatype.XMLGregorianCalendar;

import es.tributasenasturias.services.ancert.recepcionescritura.bd.Datos;
import es.tributasenasturias.services.ancert.recepcionescritura.context.CallContext;
import es.tributasenasturias.services.ancert.recepcionescritura.context.IContextReader;
import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;
import es.tributasenasturias.services.ancert.recepcionescritura.factory.DomainObjectsFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogFactory;
import es.tributasenasturias.services.ancert.recepcionescritura.log.LogHelper;
import es.tributasenasturias.services.ancert.recepcionescritura.types.IDESCRITURAType;

/**
 * Gestiona el mensaje de denegación de escritura
 * @author crubencvs
 *
 */
public class GestorDenegacion implements IContextReader{
	
	private CallContext context;
	
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
		
	}
	public static enum Resultado {OK, NO_SOLICITUD, FALTAN_DATOS, ERROR};
	public GestorDenegacion(CallContext c) {
		this.context = c;
	}
	/**
	 * Marca la solicitud como denegada, con el motivo que se indica.
	 * @param datosEscritura Datos identificativos de la escritura
	 * @param motivo Motivo de la denegación
	 * @return Resultado de la denegación
	 * @throws SystemException
	 */
	public Resultado gestionarDenegacion(IDESCRITURAType datosEscritura, String motivo) throws SystemException{
		Resultado r;
		String nulo="null";
		EscrituraDO datescritura= new EscrituraDO();
		//Inicializamos datos
		datescritura.setCodNotario(datosEscritura.getCODNOTARIO());
		datescritura.setCodNotaria(datosEscritura.getCODNOTARIA());
		ProtocoloDO protocolo=new DomainObjectsFactory().newProtocoloDO();
		if (datosEscritura.getNUMBIS()==null)
		{
			//Podrían enviarlo compuesto
			protocolo.setProtocoloCompuesto(String.valueOf(datosEscritura.getNUMPROTOCOLO()));
		}
		else
		{
			protocolo.setProtocolo(String.valueOf(datosEscritura.getNUMPROTOCOLO()));
			protocolo.setProtocoloBis(String.valueOf(datosEscritura.getNUMBIS()));
		}
		datescritura.setNumProtocolo(protocolo.getProtocolo());
		datescritura.setProtocoloBis(protocolo.getProtocoloBis());
		if (datosEscritura.getFECHAAUTORIZACION()!=null){
			XMLGregorianCalendar fec=datosEscritura.getFECHAAUTORIZACION();
			String fecha=String.format("%02d",fec.getDay())+"-"+String.format("%02d",fec.getMonth()) +"-"+String.format("%04d",fec.getYear());
			datescritura.setFechaEscritura(fecha);
		} else if (datosEscritura.getANYOAUTORIZACION()!=null) {
			datescritura.setAnyoEscritura(String.valueOf(datosEscritura.getANYOAUTORIZACION()));
		} else {
			throw new SystemException ("Error. No se ha recibido ni año de escritura ni fecha de escritura.");
		}

		if (datescritura.getCodNotario()==null ||
				datescritura.getCodNotaria()==null||
				datescritura.getNumProtocolo()==null||
				datescritura.getProtocoloBis()==null||
				(datescritura.getFechaEscritura()==null && datescritura.getAnyoEscritura()==null)||
				"".equals(datescritura.getCodNotario()) ||
				"".equals(datescritura.getCodNotaria())||
				"".equals(datescritura.getNumProtocolo()) ||
				"".equals(datescritura.getProtocoloBis()) ||
				("".equals(datescritura.getFechaEscritura()) && "".equals(datescritura.getAnyoEscritura()))||
				nulo.equalsIgnoreCase(datescritura.getCodNotaria())|| 
				nulo.equalsIgnoreCase(datescritura.getCodNotaria())||
				nulo.equalsIgnoreCase(datescritura.getNumProtocolo())||
				nulo.equalsIgnoreCase(datescritura.getProtocoloBis())||
				(nulo.equalsIgnoreCase(datescritura.getFechaEscritura()) && nulo.equalsIgnoreCase(datescritura.getAnyoEscritura()))
				|| "0".equals(datescritura.getAnyoEscritura())
			    )
		{
				r= Resultado.FALTAN_DATOS;
		}
		else {
		
			LogHelper log=LogFactory.getLogAplicacionContexto(context);
			Datos dat= new DomainObjectsFactory().newDatos(context);
			String idSolicitud=dat.getIdSolicitud(datescritura);
			if (!"".equals(dat.getErrorLlamada())) 
			{
				throw new SystemException ("Error en la llamada para recuperar el id de solicitud:"+ dat.getErrorLlamada());
			}
			
			if (!"".equals(idSolicitud)){
				String resultado = dat.denegarEscritura(idSolicitud, motivo);
				if ("OK".equalsIgnoreCase(resultado)){
					r = Resultado.OK;
				}
				else {
					log.error("Error al denegar la escritura: " + resultado);
					r = Resultado.ERROR;
				}
			}
			else {
				r = Resultado.NO_SOLICITUD;
			}
		}
		return r;
		
	}
}
