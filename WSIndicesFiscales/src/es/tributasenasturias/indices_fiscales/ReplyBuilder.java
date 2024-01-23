package es.tributasenasturias.indices_fiscales;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import es.tributasenasturias.indices_fiscales.INDFISCALREPLY.RESULTADO;
import es.tributasenasturias.indices_fiscales.utils.MensajesResultado;

public class ReplyBuilder {

	private ReplyBuilder()
	{
		
	}
	/**
	 * Construye la respuesta de mensaje
	 * @param codigo Código de resultado
	 * @param mensaje Mensaje
	 * @param esError Indica si el resultado es un error
	 * @param cc Cabecera del XML, o null si no se tiene. En ese caso genera una cabecera por defecto
	 * @return
	 */
	public static INDFISCALREPLY buildReply(String codigo, String mensaje, boolean esError,CabeceraComunicacion cc){
		INDFISCALREPLY reply = new INDFISCALREPLY();
		CABECERAType cabecera = new CABECERAType();
		if (cc!=null)
		{
			cabecera.setEMISOR(cc.getEmisor());
			cabecera.setFUNCION(new BigInteger(cc.getFuncion()));
			cabecera.setIDAPL(cc.getIdApl());
			cabecera.setNUMMSJ(new BigInteger(cc.getNumMsj()));
			cabecera.setTIPORECEP(Byte.parseByte(cc.getTipoRecep()));
			cabecera.setTIPOMSJ(Byte.parseByte("2"));
			cabecera.setRECEP(cc.getRecep());
			try{
				XMLGregorianCalendar gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cc.getTimestamp());
				cabecera.setTIMESTAMP(gc);
			}catch (DatatypeConfigurationException e)
			{
				throw new IllegalStateException("Imposible crear Timestamp:"+ e.getMessage(),e);
			}
		}
		else {
			cabecera.setEMISOR("CGN");
			cabecera.setFUNCION(new BigInteger("1"));
			cabecera.setIDAPL("PLTINDFIS");
			cabecera.setNUMMSJ(new BigInteger("0"));
			cabecera.setRECEP("08");
			cabecera.setTIPOMSJ(Byte.parseByte("2"));
			cabecera.setTIPORECEP(Byte.parseByte("3"));
			GregorianCalendar calendar = new GregorianCalendar();
			try
			{
				cabecera.setTIMESTAMP(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
			}catch (DatatypeConfigurationException e)
			{
				//Esto no debería ocurrir nunca. Si ocurre, no podemos hacer nada
				throw new IllegalStateException("Imposible crear Timestamp:"+e.getMessage(),e);
			}
		}
		RESULTADO resultado = new RESULTADO();
		resultado.setCODIGOERROR(codigo);
		resultado.setTEXTOERROR(mensaje);
		resultado.setTIPOERROR(esError);
		reply.setCABECERA(cabecera);
		reply.setRESULTADO(resultado);
		return reply;
	}
	
	public static INDFISCALREPLY buildReply  (MensajesResultado mensaje, boolean esError,CabeceraComunicacion cc)
	{
		return buildReply(mensaje.getId(), mensaje.getDescripcion(),esError, cc);
	}
	public static INDFISCALREPLY buildReply  (MensajesResultado mensaje, boolean esError)
	{
		return buildReply(mensaje.getId(), mensaje.getDescripcion(),esError, null);
	}
	/**
	 * Construye la respuesta en caso de no tener la cabecera de mensaje (si aún no se ha leído el contenido por ejemplo)
	 * @param codigo Código de resultado
	 * @param mensaje Mensaje de resultado
	 * @param esError Indica si es o no error
	 * @return Respuesta de proceso
	 */
	public static INDFISCALREPLY buildReply(String codigo, String mensaje, boolean esError){
		return buildReply(codigo, mensaje,esError,null);
	}
	/**
	 * Devuelve una respuesta de error por defecto. Nunca debería darse, pero si sucede, al menos que reciban algo coherente.
	 * No es la mejor implementación, pero sirve.
	 * @return Cadena con XML de respuesta.
	 */
	public static byte[] buildDefaultErrorResponse() {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+1:00"));
		String fecha=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+"T"+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+"-"+cal.get(Calendar.SECOND);
		String respuesta= "<ns2:IND_FISCAL_REPLY xmlns:ns2='http://inti.notariado.org/XML'><ns2:CABECERA><ns2:TIMESTAMP>"+fecha+"</ns2:TIMESTAMP><ns2:NUM_MSJ>0</ns2:NUM_MSJ><ns2:ID_APL>PLTINDFIS</ns2:ID_APL><ns2:FUNCION>1</ns2:FUNCION><ns2:TIPO_MSJ>2</ns2:TIPO_MSJ><ns2:EMISOR>CGN</ns2:EMISOR><ns2:TIPO_RECEP>3</ns2:TIPO_RECEP>"+
		"<ns2:RECEP>03</ns2:RECEP></ns2:CABECERA><ns2:RESULTADO><ns2:TIPO_ERROR>true</ns2:TIPO_ERROR><ns2:CODIGO_ERROR>E0011</ns2:CODIGO_ERROR><ns2:TEXTO_ERROR>Error técnico</ns2:TEXTO_ERROR></ns2:RESULTADO></ns2:IND_FISCAL_REPLY>";
		return respuesta.getBytes(Charset.forName(IndicesFiscalesServlet.ENCODING));
	}
	
}
