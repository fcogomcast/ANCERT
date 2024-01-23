package es.tributasenasturias.indices_fiscales;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;

import es.tributasenasturias.indices_fiscales.ComunicadorBD.ResultadoLlamada.TipoFinalizacion;
import es.tributasenasturias.indices_fiscales.soap.SoapClientHandler;
import es.tributasenasturias.indices_fiscales.utils.Base64;
import es.tributasenasturias.indices_fiscales.utils.bbdd.LanzadorException;
import es.tributasenasturias.indices_fiscales.utils.bbdd.LanzadorFactory;
import es.tributasenasturias.indices_fiscales.utils.bbdd.ParamType;
import es.tributasenasturias.indices_fiscales.utils.bbdd.ProcedimientoAlmacenado;
import es.tributasenasturias.indices_fiscales.utils.bbdd.RespuestaLanzador;
import es.tributasenasturias.indices_fiscales.utils.bbdd.TLanzador;
import es.tributasenasturias.indices_fiscales.utils.preferencias.Preferencias;

/**
 * Permite la comunicación con base de datos
 * @author crubencvs
 *
 */
public class ComunicadorBD {

	private String idSesion;
	private Preferencias pref;
	public static class ResultadoLlamada
	{
		public enum TipoFinalizacion
		{
			CORRECTA,
			DUPLICADO,
			ERROR
		}
		private boolean error;
		private String mensaje;
		private TipoFinalizacion tipoFinalizacion;
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public String getMensaje() {
			return mensaje;
		}
		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}
		public TipoFinalizacion getTipoFinalizacion() {
			return tipoFinalizacion;
		}
		public void setTipoFinalizacion(TipoFinalizacion tipoFinalizacion) {
			this.tipoFinalizacion = tipoFinalizacion;
		}
		
		
	}
	private ResultadoLlamada resultado;
	public ResultadoLlamada getResultado() {
		return resultado;
	}
	public ComunicadorBD(String idSesion,Preferencias p)
	{
		this.pref=p;
		this.idSesion= idSesion;
	}
	public ResultadoLlamada enviarFichero(String nombreXml,byte[] xml) throws LanzadorException
	{
		TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(),new SoapClientHandler(idSesion));
		ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcAlmacenadoGrabarFichero(),pref.getEsquemaBD());
		String datos = comprimirBase64(xml);
		proc.param(nombreXml, ParamType.CADENA);
		proc.param(datos, ParamType.CLOB);
		proc.param("P", ParamType.CADENA);
		String resultado=lanzador.ejecutar(proc);
		RespuestaLanzador rl = new RespuestaLanzador(resultado);
		ResultadoLlamada res= new ResultadoLlamada();
		if (rl.esErronea())
		{
			res.setError(true);
			res.setMensaje(rl.getTextoError());
			res.setTipoFinalizacion(TipoFinalizacion.ERROR);
		} else {
			String terminacion=rl.getValue("CANU_CADENAS_NUMEROS", 1, "STRING1_CANU");
			if ("000".equals(terminacion))
			{
				res.setError(false);
				res.setMensaje("Finalización correcta");
				res.setTipoFinalizacion(TipoFinalizacion.CORRECTA);
			}else if ("100".equals(terminacion))
			{
				res.setError(true);
				res.setMensaje("Fichero de índices duplicado.");
				res.setTipoFinalizacion(TipoFinalizacion.DUPLICADO);
			} else if ("999".equals(terminacion))  {
				res.setError(true);
				String mensaje = rl.getValue("CANU_CADENAS_NUMEROS", 1, "STRING2_CANU");
				res.setMensaje(mensaje);
				res.setTipoFinalizacion(TipoFinalizacion.ERROR);
			} else { //No debería pasar si se tienen los códigos correctos
				res.setError(true);
				res.setMensaje("Problema de comunicación con sistemas STPA");
				res.setTipoFinalizacion(TipoFinalizacion.ERROR);
			}
		}
		return res;
	}
	/**
	 * Comprime mediante GZIP el contenido que se le pasa, y después lo devuelve en un string en Base 64
	 * @param xml Contenido del xml
	 * @return String con la representación del contenido comprimido con el algoritmo GZIP
	 * @throws LanzadorException
	 */
	private String comprimirBase64(byte[] xml) throws LanzadorException
	{
		ByteArrayOutputStream baos=null;
		GZIPOutputStream gzip=null;
		ByteArrayInputStream bais=null; 
		try
		{
			bais = new ByteArrayInputStream(xml);
			baos  = new ByteArrayOutputStream();
			gzip = new GZIPOutputStream(baos);
			byte[] buffer= new byte[8192];
			int leidos;
			while ((leidos=bais.read(buffer))!=-1) {
				gzip.write(buffer,0,leidos);
			}
			gzip.flush();
			gzip.close();
			return String.valueOf(Base64.encode(baos.toByteArray()));
		}
		catch (IOException io)
		{
			throw new LanzadorException("Error al comprimir el contenido para enviar a BD:" + io.getMessage(),io);
		}
		finally 
		{
			if (gzip!=null)
			{
				try{ gzip.close();} catch (Exception e){}
			}
			if (baos!=null)
			{
				try{baos.close();} catch (Exception e){}
			}
			if (bais!=null)
			{
				try{bais.close();} catch(Exception e){}
			}
		}
	}

}
