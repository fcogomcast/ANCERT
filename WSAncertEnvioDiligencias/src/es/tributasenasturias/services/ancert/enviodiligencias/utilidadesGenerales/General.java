package es.tributasenasturias.services.ancert.enviodiligencias.utilidadesGenerales;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Utilidades de propósito general.
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
	 * Recupera un checkcum basado en un identificador único. Se asegura que el identificador es único.
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
	 * Recupera una representación de cadena, con el formato DD-MM-YYYY::H24:MI:SS de la fecha actual
	 * @return Representación de cadena de la fecha.
	 */
	public static String getTime()
	{
		GregorianCalendar cal=new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
		String fecha=cal.get(Calendar.DAY_OF_MONTH)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR)+"::"+
					 cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
		return fecha;
	}
	//Para convertir de la plantilla recuperada en base de datos a un formato XML válido.
	private static HashMap<String,String> htmlEntities;
	  static {
	    htmlEntities = new HashMap<String,String>();
	    htmlEntities.put("&lt;","<")    ; htmlEntities.put("&gt;",">");
	    htmlEntities.put("&amp;","&")   ; htmlEntities.put("&quot;","\"");
	    htmlEntities.put("&agrave;","à"); htmlEntities.put("&Agrave;","À");
	    htmlEntities.put("&acirc;","â") ; htmlEntities.put("&auml;","ä");
	    htmlEntities.put("&Auml;","Ä")  ; htmlEntities.put("&Acirc;","Â");
	    htmlEntities.put("&aring;","å") ; htmlEntities.put("&Aring;","Å");
	    htmlEntities.put("&aelig;","æ") ; htmlEntities.put("&AElig;","Æ" );
	    htmlEntities.put("&ccedil;","ç"); htmlEntities.put("&Ccedil;","Ç");
	    htmlEntities.put("&eacute;","é"); htmlEntities.put("&Eacute;","É" );
	    htmlEntities.put("&egrave;","è"); htmlEntities.put("&Egrave;","È");
	    htmlEntities.put("&ecirc;","ê") ; htmlEntities.put("&Ecirc;","Ê");
	    htmlEntities.put("&euml;","ë")  ; htmlEntities.put("&Euml;","Ë");
	    htmlEntities.put("&iuml;","ï")  ; htmlEntities.put("&Iuml;","Ï");
	    htmlEntities.put("&ocirc;","ô") ; htmlEntities.put("&Ocirc;","Ô");
	    htmlEntities.put("&ouml;","ö")  ; htmlEntities.put("&Ouml;","Ö");
	    htmlEntities.put("&oslash;","ø") ; htmlEntities.put("&Oslash;","Ø");
	    htmlEntities.put("&szlig;","ß") ; htmlEntities.put("&ugrave;","ù");
	    htmlEntities.put("&Ugrave;","Ù"); htmlEntities.put("&ucirc;","û");
	    htmlEntities.put("&Ucirc;","Û") ; htmlEntities.put("&uuml;","ü");
	    htmlEntities.put("&Uuml;","Ü")  ; htmlEntities.put("&nbsp;"," ");
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
