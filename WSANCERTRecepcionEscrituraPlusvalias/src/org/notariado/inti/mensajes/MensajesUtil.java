package org.notariado.inti.mensajes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.notariado.inti.exceptions.MensajesException;




/**
 * Clase de utilidad para el manejo de mensajes de la aplicación.
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
			throw new MensajesException("Error al recuperar los mensajes de la aplicación:"+ e.getMessage(),e);
		} catch (IOException e) {
			throw new MensajesException("Error al recuperar los mensajes de la aplicación:"+ e.getMessage(),e);
		} finally {
			if (in!=null){
				try {in.close();} catch(Exception e) {}
			}
		}
		
	}
	/**
	 * Recupera el código de terminación OK
	 * @return código de terminación ok
	 */
	public String getCodigoOk()
	{
		return props.getProperty("ok.code");
	}
	/**
	 * Recupera el código de error genérico, que indica al notario que no vuelva a repetir el envío.
	 * @return código de terminación de error genérico
	 */
	public String getCodigoErrorGenerico()
	{
		return props.getProperty("error.generico.code");
	}
	/**
	 * Recupera el código de error temporal, que indica al notario que vuelva a repetir el envío.
	 * @return código de terminación de error temporal
	 */
	public String getCodigoErrorTemporal()
	{
		return props.getProperty("error.temporal.code");
	}
	/**
	 * Recupera un mensaje del fichero de mensajes de la aplicación.
	 * @param clave Clave del mensaje.
	 * @return Mensaje correspondiente a la clave, o null si no se encuentra.
	 */
	public String getMensaje(String clave)
	{
		return props.getProperty(clave);
	}

	
	
}
