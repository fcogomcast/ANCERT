package es.tributasenasturias.services.ancert.enviodiligencias.bd;

import java.util.ArrayList;
import java.util.List;

/**
 * Guarda datos del justificante de presentación recuperados de la base de datos
 * @author crubencvs
 *
 */
public class JustificantePresentacionDO {
	//Guarda datos del expediente, y de n Personas.
	private DatosExpedienteDO datosExpediente;
	private List<DatosPersonaDO> personas;
	private String textoTitulo;
	private String textoFijo;
	public DatosExpedienteDO getDatosExpediente() {
		return datosExpediente;
	}
	public void setDatosExpediente(DatosExpedienteDO datosInforme) {
		this.datosExpediente = datosInforme;
	}
	public List<DatosPersonaDO> getPersonas() {
		return personas;
	}
	public void setPersonas(List<DatosPersonaDO> personas) {
		this.personas = personas;
	}
	public String getTextoFijo() {
		return textoFijo;
	}
	public void setTextoFijo(String textoFijo) {
		this.textoFijo = textoFijo;
	}
	public String getTextoTitulo() {
		return textoTitulo;
	}
	public void setTextoTitulo(String textoTitulo) {
		this.textoTitulo = textoTitulo;
	}
	public DatosPersonaDO getDatosPresentador()
	{
		for (DatosPersonaDO persona:this.personas)
		{
			if ("1".equals(persona.getTipoPersona()))
			{
				return persona;
			}
		}
		return null;
	}
	public DatosPersonaDO getDatosSujetoPasivo()
	{
		for (DatosPersonaDO persona:this.personas)
		{
			if ("2".equals(persona.getTipoPersona()))
			{
				return persona;
			}
		}
		return null;
	}
	protected JustificantePresentacionDO()
	{
		datosExpediente = DatosFactory.newDatosExpedienteDO();
		personas = new ArrayList<DatosPersonaDO>();
	}
}
