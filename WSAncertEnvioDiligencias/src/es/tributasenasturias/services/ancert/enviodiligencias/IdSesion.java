package es.tributasenasturias.services.ancert.enviodiligencias;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;




/**
 * Clase que intercepta el mensaje de entrada, y genera un Id único de sesión para él,
 *  para identificar sus entradas en los log. 
 * @author crubencvs
 *
 */
public class IdSesion implements SOAPHandler<SOAPMessageContext> {

	private static final int MASK_15 = 0x0f;
	private static final int MASK_240 = 0xf0;
	private static final int SHIFT_DIVIDIR_16 = 4;
	private static String hexEncode( byte[] aInput){
	    StringBuilder result = new StringBuilder();
	    char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
	    for ( int idx = 0; idx < aInput.length; ++idx) {
	      byte b = aInput[idx];
	      result.append( digits[ (b&MASK_240) >> SHIFT_DIVIDIR_16 ] );
	      result.append( digits[ b&MASK_15] );
	    }
	    return result.toString();
	  }
	/**
	 * Recupera un checkcum basado en un identificador único. Se asegura que el identificador es único.
	 */
	public String getIdLlamada()
	{
		UUID id=UUID.randomUUID();
		String identificador=null;
		byte[] mensaje = id.toString().getBytes();
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(mensaje);
			identificador=hexEncode(md.digest());

		}catch(NoSuchAlgorithmException nsae){
			identificador="";
		}
		return identificador;
	}
	@Override
	public final Set<QName> getHeaders() {
		return Collections.emptySet();
	}

	@Override
	public final void close(MessageContext context) {
	}

	@Override
	public final boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public final boolean handleMessage(SOAPMessageContext context) {
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!salida.booleanValue())
		{
			//Lo generamos, para que exista.
			String idSesion=getIdLlamada();
			context.put("IdSesion", idSesion);
			context.setScope("IdSesion", MessageContext.Scope.APPLICATION);
		}
		return true;
	}

	

}
