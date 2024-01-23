package org.notariado.inti.mensajes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.notariado.inti.exceptions.MensajesException;




/**
 * Clase de utilidad para el manejo de mensajes de la aplicaci�n.
 * @author crubencvs
 *
 */
public class MensajesUtil {
	Properties props = new Properties();
	/**
	 * Constructor
	 * @param context Contexto de llamada
	 * @throws MensajesException
	 */
	protected MensajesUtil(String ficheroMensajes) throws MensajesException{
		FileInputStream in=null;
		try {
			in = new FileInputStream(ficheroMensajes);
			props.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			throw new MensajesException("Error al recuperar los mensajes de la aplicaci�n:"+ e.getMessage(),e);
		} catch (IOException e) {
			throw new MensajesException("Error al recuperar los mensajes de la aplicaci�n:"+ e.getMessage(),e);
		} finally {
			if (in!=null){
				try {in.close();} catch(Exception e) {}
			}
		}
		
	}
	/**
	 * Recupera el c�digo de terminaci�n OK
	 * @return c�digo de terminaci�n ok
	 */
	public String getCodigoOk()
	{
		return props.getProperty("ok.code");
	}
	/**
	 * Recupera el c�digo de error gen�rico, que indica al notario que no vuelva a repetir el env�o.
	 * @return c�digo de terminaci�n de error gen�rico
	 */
	public String getCodigoErrorGenerico()
	{
		return props.getProperty("error.generico.code");
	}
	/**
	 * Recupera el c�digo de error temporal, que indica al notario que vuelva a repetir el env�o.
	 * @return c�digo de terminaci�n de error temporal
	 */
	public String getCodigoErrorTemporal()
	{
		return props.getProperty("error.temporal.code");
	}
	/**
	 * Recupera un mensaje del fichero de mensajes de la aplicaci�n.
	 * @param clave Clave del mensaje.
	 * @return Mensaje correspondiente a la clave, o null si no se encuentra.
	 */
	public String getMensaje(String clave)
	{
		return props.getProperty(clave);
	}

	
	
}
