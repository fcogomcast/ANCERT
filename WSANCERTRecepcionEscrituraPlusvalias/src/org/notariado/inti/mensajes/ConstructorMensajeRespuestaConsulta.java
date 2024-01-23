package org.notariado.inti.mensajes;

import java.util.GregorianCalendar;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.notariado.inti.CABECERAType;
import org.notariado.inti.COMUNICACION;
import org.notariado.inti.ObjectFactory;
import org.notariado.inti.COMUNICACION.RESPONSE;
import org.notariado.inti.exceptions.ConstructorRespuestaException;



/**
 * Construye el mensaje de respuesta de la consulta.
 * @author crubencvs
 *
 */
public class ConstructorMensajeRespuestaConsulta {

	private String emisor;
	private String receptor;
	private MensajesUtil mensajes;
	/**
	 * Protegido, sólo se debería usar desde el factory.
	 * @param receptorSalida Receptor de mensaje de salida, coincide con el emisor del mensaje de entrada
	 * @param emisorSalida Emisor del mensaje de salida, coincide con el receptor del mensaje de entrada
	 * @param mensajes Clase de gestión de mensajes de la aplicación. Se tomará de aquí el código de retorno, y los mensajes que se puedan devolver.
	 */
	protected ConstructorMensajeRespuestaConsulta(String receptorSalida, String emisorSalida, MensajesUtil mensajes)
	{
		this.mensajes= mensajes;
		emisor= emisorSalida;
		receptor =receptorSalida;
	}
	
	 public XMLGregorianCalendar getXMLGregorianCalendarNow() 
     throws DatatypeConfigurationException
     {
		 GregorianCalendar gregorianCalendar = new GregorianCalendar();
		 DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
		 XMLGregorianCalendar now = 
		     datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
		 return now;
     }
	 
	public RESPONSE setRESPONSE(CABECERAType cabecera, int codError, String msgError, String file)
	{
		RESPONSE resp = new ObjectFactory().createCOMUNICACIONRESPONSE();
		resp.setCABECERA(cabecera);
		resp.setCODERROR(codError);
		resp.setERROR(msgError);
		resp.setJUSTIFICANTERECEPCIONCOPIA(file);
		return resp;
	}
	
	public CABECERAType setCABECERA(String receptor, String emisor) throws DatatypeConfigurationException
	{
		CABECERAType cabecera = new ObjectFactory().createCABECERAType();
		cabecera.setRECEPTOR(receptor);
		cabecera.setEMISOR(emisor);
		cabecera.setTIMESTAMP(getXMLGregorianCalendarNow());
		return cabecera;
	}
	
public COMUNICACION setComunicacion(String receptor, String emisor, int codError, String msgError, String file) throws ConstructorRespuestaException
	{
		COMUNICACION com = new ObjectFactory().createCOMUNICACION();
		CABECERAType cabecera=null;
		try {
			cabecera = setCABECERA(receptor, emisor);
		} catch (DatatypeConfigurationException e) {
			//Lanzaremos una excepción. Esto es complicado, porque si no podemos montar la cabecera de respuesta ,
			// lo que queda es lanzar una SOAP Fault
			throw new ConstructorRespuestaException ("Error al generar la respuesta de la comunicación:" + e.getMessage(),e);
		}
		com.setRESPONSE(setRESPONSE(cabecera,codError, msgError, file));
		return com;
	}

public COMUNICACION buildResultado(int codigo, String msgResultado, String fichero) throws ConstructorRespuestaException
{
	return setComunicacion(receptor,emisor,codigo ,msgResultado, fichero);
}
public COMUNICACION buildResultadoOk (String msgResultado, String fichero) throws ConstructorRespuestaException
{
	try
	{
		int cod= Integer.parseInt(mensajes.getCodigoOk());
		return buildResultado(cod ,msgResultado, fichero);
	}
	catch (NumberFormatException e)
	{
		throw new ConstructorRespuestaException("Error de formateo de número, recuperando código de resultado OK:"+e.getMessage(),e);
	}
}

public COMUNICACION buildResultadoErrorGenerico (String msgResultado) throws ConstructorRespuestaException
{
	try
	{
		int cod= Integer.parseInt(mensajes.getCodigoErrorGenerico());
		return buildResultado(cod ,msgResultado, null);
	}
	catch (NumberFormatException e)
	{
		throw new ConstructorRespuestaException("Error de formateo de número, recuperando código de resultado error genérico:"+e.getMessage(),e);
	}
}

public COMUNICACION buildResultadoErrorTemporal (String msgResultado) throws ConstructorRespuestaException
{
	try
	{
		int cod= Integer.parseInt(mensajes.getCodigoErrorTemporal());
		return buildResultado(cod ,msgResultado, null);
	}
	catch (NumberFormatException e)
	{
		throw new ConstructorRespuestaException("Error de formateo de número, recuperando código de resultado error temporal:"+e.getMessage(),e);
	}
}

public String getEmisor() {
	return emisor;
}

public void setEmisor(String emisor) {
	this.emisor = emisor;
}

public String getReceptor() {
	return receptor;
}

public void setReceptor(String receptor) {
	this.receptor = receptor;
}

public MensajesUtil getMensajes() {
	return mensajes;
}

public void setMensajes(MensajesUtil mensajes) {
	this.mensajes = mensajes;
}
	
	
}
