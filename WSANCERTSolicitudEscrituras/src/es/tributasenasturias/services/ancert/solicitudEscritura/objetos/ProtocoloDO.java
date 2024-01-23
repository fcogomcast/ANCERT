/**
 * 
 */
package es.tributasenasturias.services.ancert.solicitudEscritura.objetos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions.ProtocoloException;

/**
 * Clase que encapsula al protocolo. 
 * @author crubencvs
 *
 */
public class ProtocoloDO {

	//Formatos de protocolo
	private final static String BIS="^([0-9]{1,6})(BIS)$";
	private final static String BIS_NUM="^([0-9]{1,6})(BIS([0-9]{1,3})?)?$";
	private final static String B_NUM = "^([0-9]{1,6})(B([0-9]{1,3})?)?$";
	private String protocolo=null;
	private String protocoloBis=null;
	public String getProtocolo() {
		return protocolo;
	}
	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}
	public String getProtocoloBis() {
		return protocoloBis;
	}
	public void setProtocoloBis(String protocoloBis) {
		this.protocoloBis = protocoloBis;
	}
	public void setProtocoloCompuesto(String protocolo) throws ProtocoloException
	{
		boolean valid=false;
		//Formatos reconocidos:
		//NNNNNNBIS[PPP], donde
		// NNNNNN = protocolo, 6 d�gitos m�ximo
		// BIS = literal
		// PPP = protocolo bis, 3 d�gitos m�ximo. Para el primer bis, no se pone nada.
		// Como caso especial, si el formato que se tiene es NNNNNNBIS,
		// el protocolo bis es 1.
		//Tambi�n puede darse un segundo formato de protocolo:
		// NNNNNN = 6 d�gitos
		// NNNNNNBPPP = 6 d�gitos, una "B" y tres d�gitos de protocolo  bis "PPP".
		try
		{
			Pattern patron= Pattern.compile(BIS);
			Matcher match= patron.matcher(protocolo.toUpperCase());
			valid=match.matches();
			if (valid) //Formato NNNNNNBIS, sin n�mero de protocolo bis. En este caso el protocolo bis se considera 1
			{
			 	this.protocolo=match.group(1); // Si es v�lido, siempre habr� protocolo.
				this.protocoloBis="1";
			}
			else
			{
				//Formato NNNNNNBISPPP
				patron= Pattern.compile(BIS_NUM);
				match= patron.matcher(protocolo.toUpperCase());
				valid=match.matches();
				if (valid)
				{
					this.protocolo=match.group(1); // Si es v�lido, siempre habr� protocolo.
					if (match.group(3)!=null) // Grupo 0 es la expresi�n completa. Hay protocolo y protocolo bis.
					{
						this.protocoloBis=match.group(3);
					}
					else
					{
						this.protocoloBis="0";
					}
				}
				else
				{
					//Formato NNNNNNBPPP
					
					patron= Pattern.compile(B_NUM);
					match= patron.matcher(protocolo.toUpperCase());
					valid=match.matches();
					if (valid)
					{
						this.protocolo=match.group(1); // Si es v�lido, siempre habr� protocolo.
						if (match.group(3)!=null) // Grupo 0 es la expresi�n completa. Hay protocolo y protocolo bis.
						{
							this.protocoloBis=match.group(3);
						}
						else
						{
							this.protocoloBis="0";
						}
					}
					else
					{
						//Excepci�n. El formato de protocolo es incorrecto.
						throw new ProtocoloException("Formato de protocolo inv�lido.");
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw new ProtocoloException ("Error inesperado al recuperar el protocolo :"+ex.getMessage(),ex);
		}
	}
	/**
	 * Constructor sin par�metros. Ser� necesario emplear el m�todo "set" para dar los valores.
	 */
	public ProtocoloDO()
	{
		this.protocolo="";
		this.protocoloBis="";
	}
	
	/**
	 * Comprueba si el formato de protocolo es v�lido.
	 * @param protocolo
	 * @return
	 */
	public static boolean checkFormat(String protocolo)
	{
		if (Pattern.matches(BIS, protocolo.toUpperCase())
			|| Pattern.matches(BIS_NUM, protocolo.toUpperCase())
			|| Pattern.matches(B_NUM, protocolo.toUpperCase()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
