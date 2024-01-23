package es.tributasenasturias.services.ancert.solicitudEscritura.validacion;

import java.util.ArrayList;
import java.util.List;
import es.tributasenasturias.services.ancert.solicitudEscritura.EntradaType;
import es.tributasenasturias.services.ancert.solicitudEscritura.objetos.ProtocoloDO;

public class ValidadorParametros implements IValidator<EntradaType> {

	private List<String> mensajes = null;
	private boolean valid=false;
	public ValidadorParametros() {
		mensajes=new ArrayList<String>();
	}
	/**
	 * @author crubencvs
	 */
	public static enum EstadoValidacion
	{
		/**
		 * Falta alguno de los parámetros requeridos. 
		 */
		FALTAN_PARAMETROS,
		/**
		 * El formato de protocolo no es correcto.
		 */
		PROTOCOLO_INCORRECTO
	}
	private EstadoValidacion estadoValidacion;
	
	@Override
	public List<String> getMessages() {
		return mensajes;
	}

	@Override
	public String getStringMessages() {
		StringBuffer res= new StringBuffer();
		for (String i:mensajes)
		{
			res.append(i);
			res.append(System.getProperty("line.separator"));
		}
		return res.toString();
	}
	@Override
	public boolean isValid() {
		return valid;
	}

	/**
	 * Validador de parámetros de entrada.
	 * 
	 */
	@Override
	public void validate(EntradaType parametros) {
		if (parametros.getCodNotario()==null || parametros.getCodNotario().length()==0 || 
			parametros.getCodNotaria()==null || parametros.getCodNotaria().length()==0 ||
			parametros.getProtocolo()==null  || parametros.getProtocolo().length()==0
			)
		{
			valid=false;
			estadoValidacion=EstadoValidacion.FALTAN_PARAMETROS;
			mensajes.add("Falta uno de los parámetros de entrada requeridos");
		}
		else
		{
			valid=true;
		}
		if (valid)
		{
			valid=ProtocoloDO.checkFormat(parametros.getProtocolo());
			if (!valid)
			{
				valid=false;
				estadoValidacion= EstadoValidacion.PROTOCOLO_INCORRECTO;
				mensajes.add("El formato de protocolo no es correcto.");
			}
		}
	}
	/**
	 * Devuelve el estado de la validación si no es correcto.
	 * @return Estado de la validación
	 */
	public EstadoValidacion getEstadoValidacion() {
		return estadoValidacion;
	}
	/**
	 * Establece el estado de la validación.
	 * @param estadoValidacion {@link EstadoValidacion}
	 */
	public void setEstadoValidacion(EstadoValidacion estadoValidacion) {
		this.estadoValidacion = estadoValidacion;
	}	

}
