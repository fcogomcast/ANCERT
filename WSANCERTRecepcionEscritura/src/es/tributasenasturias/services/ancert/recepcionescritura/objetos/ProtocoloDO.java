/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.objetos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.tributasenasturias.services.ancert.recepcionescritura.exceptions.SystemException;

/**
 * Clase que encapsula al protocolo. 
 * @author crubencvs
 *
 */
public class ProtocoloDO {

	private String protocolo=null;
	private String protocoloBis=null;
	public String getProtocolo() {
		return protocolo;
	}
	public final void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}
	public final String getProtocoloBis() {
		return protocoloBis;
	}
	public final void setProtocoloBis(String protocoloBis) {
		this.protocoloBis = protocoloBis;
	}
	public final void setProtocoloCompuesto(String protocolo) throws SystemException
	{
		boolean valid=false;
		//Por el momento, sólo valida el formato de protocolo:
		// NNNNNNNNNNNNN = 13 dígitos
		// NNNNNNNNNNNNNBPPP = 13 dígitos, una "B" y tres dígitos de protocolo  "PPP".
		try
		{
			Pattern patron= Pattern.compile("([0-9]{1,13})B?([0-9]{1,3})?");
			Matcher match= patron.matcher(protocolo);
			valid=match.matches();
			//Si concuerda, aprovechamos para recuperar el número de protocolo y el protocolo bis, que estarán
			//disponibles en este objeto.
			if (valid)
			{
				this.protocolo=match.group(1); // Si es válido, siempre habrá protocolo.
				if (match.group(2)!=null) // Grupo 0 es la expresión completa. Hay protocolo y protocolo bis.
				{
					this.protocoloBis=match.group(2);
				}
				else
				{
					this.protocoloBis="0";
				}
			}
			else
			{
				//Excepción. El formato de protocolo es incorrecto.
				throw new SystemException("Formato de protocolo inválido.");
			}
		}
		catch (Exception ex)
		{
			throw new SystemException ("Error inesperado al recuperar el protocolo :"+ex.getMessage(),ex);
		}
	}
	/**
	 * Constructor sin parámetros. Será necesario emplear el método "set" para dar los valores.
	 */
	public ProtocoloDO()
	{
		this.protocolo="";
		this.protocoloBis="";
	}
	
	/**
	 * Comprueba si el formato de protocolo es válido.
	 * @param protocolo
	 * @return
	 */
	public static boolean checkFormat(String protocolo)
	{
		Pattern patron= Pattern.compile("([0-9]{1,13})B?([0-9]{1,3})?");
		Matcher match= patron.matcher(protocolo);
		return (match.matches());	
	}
}
