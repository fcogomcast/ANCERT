package es.tributasenasturias.services.ancert.enviodiligencias.utilidadesGenerales;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Utilidades de prop�sito general.
 * @author crubencvs
 *
 */
public class General {
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
	protected General()
	{
		
	}
	/**
	 * Recupera un checkcum basado en un identificador �nico. Se asegura que el identificador es �nico.
	 */
	public static String getIdLlamada()
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
	/**
	 * Recupera una representaci�n de cadena, con el formato DD-MM-YYYY::H24:MI:SS de la fecha actual
	 * @return Representaci�n de cadena de la fecha.
	 */
	public static String getTime()
	{
		GregorianCalendar cal=new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
		String fecha=cal.get(Calendar.DAY_OF_MONTH)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR)+"::"+
					 cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
		return fecha;
	}
	//Para convertir de la plantilla recuperada en base de datos a un formato XML v�lido.
	private static HashMap<String,String> htmlEntities;
	  static {
	    htmlEntities = new HashMap<String,String>();
	    htmlEntities.put("&lt;","<")    ; htmlEntities.put("&gt;",">");
	    htmlEntities.put("&amp;","&")   ; htmlEntities.put("&quot;","\"");
	    htmlEntities.put("&agrave;","�"); htmlEntities.put("&Agrave;","�");
	    htmlEntities.put("&acirc;","�") ; htmlEntities.put("&auml;","�");
	    htmlEntities.put("&Auml;","�")  ; htmlEntities.put("&Acirc;","�");
	    htmlEntities.put("&aring;","�") ; htmlEntities.put("&Aring;","�");
	    htmlEntities.put("&aelig;","�") ; htmlEntities.put("&AElig;","�" );
	    htmlEntities.put("&ccedil;","�"); htmlEntities.put("&Ccedil;","�");
	    htmlEntities.put("&eacute;","�"); htmlEntities.put("&Eacute;","�" );
	    htmlEntities.put("&egrave;","�"); htmlEntities.put("&Egrave;","�");
	    htmlEntities.put("&ecirc;","�") ; htmlEntities.put("&Ecirc;","�");
	    htmlEntities.put("&euml;","�")  ; htmlEntities.put("&Euml;","�");
	    htmlEntities.put("&iuml;","�")  ; htmlEntities.put("&Iuml;","�");
	    htmlEntities.put("&ocirc;","�") ; htmlEntities.put("&Ocirc;","�");
	    htmlEntities.put("&ouml;","�")  ; htmlEntities.put("&Ouml;","�");
	    htmlEntities.put("&oslash;","�") ; htmlEntities.put("&Oslash;","�");
	    htmlEntities.put("&szlig;","�") ; htmlEntities.put("&ugrave;","�");
	    htmlEntities.put("&Ugrave;","�"); htmlEntities.put("&ucirc;","�");
	    htmlEntities.put("&Ucirc;","�") ; htmlEntities.put("&uuml;","�");
	    htmlEntities.put("&Uuml;","�")  ; htmlEntities.put("&nbsp;"," ");
	    htmlEntities.put("&copy;","\u00a9");
	    htmlEntities.put("&reg;","\u00ae");
	    htmlEntities.put("&euro;","\u20a0");
	  }

	  public static final String unescapeHTML(String source) {
	      int i, j;

	      boolean continueLoop;
	      int skip = 0;
	      do {
	         continueLoop = false;
	         i = source.indexOf("&", skip);
	         if (i > -1) {
	           j = source.indexOf(";", i);
	           if (j > i) {
	             String entityToLookFor = source.substring(i, j + 1);
	             String value = (String) htmlEntities.get(entityToLookFor);
	             if (value != null) {
	               source = source.substring(0, i)
	                        + value + source.substring(j + 1);
	               continueLoop = true;
	             }
	             else if (value == null){
	                skip = i+1;
	                continueLoop = true;
	             }
	           }
	         }
	      } while (continueLoop);
	      return source;
	  }
}
