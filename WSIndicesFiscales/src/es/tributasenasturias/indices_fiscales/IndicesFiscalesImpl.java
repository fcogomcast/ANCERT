package es.tributasenasturias.indices_fiscales;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import es.tributasenasturias.indices_fiscales.ComunicadorBD.ResultadoLlamada;
import es.tributasenasturias.indices_fiscales.ComunicadorBD.ResultadoLlamada.TipoFinalizacion;
import es.tributasenasturias.indices_fiscales.utils.MensajesResultado;
import es.tributasenasturias.indices_fiscales.utils.bbdd.LanzadorException;
import es.tributasenasturias.indices_fiscales.utils.contextoLlamada.CallContext;
import es.tributasenasturias.indices_fiscales.utils.contextoLlamada.CallContextConstants;
import es.tributasenasturias.indices_fiscales.utils.esquema.MiValidador;
import es.tributasenasturias.indices_fiscales.utils.log.Logger;
import es.tributasenasturias.indices_fiscales.utils.preferencias.Preferencias;

public class IndicesFiscalesImpl {

	private Logger log;
	private Preferencias pref;
	private String idSesion;
	private String nombreXml;
	private byte[] contenidoXml;
	
	
	public IndicesFiscalesImpl(CallContext context) {
		log = (Logger)context.get(CallContextConstants.LOG);
		idSesion = (String) context.get(CallContextConstants.IDSESION);
		pref= (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
	}
	
	public INDFISCALREPLY processRequest(byte[] contenido){
		INDFISCALREPLY reply;
		CabeceraComunicacion cabecera=null;
		try
		{
			log.debug("Descomprimimos el fichero recibido");
			if (contenido.length==0)
			{
				reply=ReplyBuilder.buildReply(MensajesResultado.NO_MENSAJE, true);
			}
			else
			{
				descomprimir(contenido);
				log.debug("Fichero descomprimido. Validamos esquema.");
				if (!validarEsquema(this.contenidoXml))
				{
					reply=ReplyBuilder.buildReply(MensajesResultado.ESQUEMA_INVALIDO, true);
				}
				else
				{
					log.debug("Esquema válido. Extracción de datos de cabecera");
					cabecera = new CabeceraComunicacion();
					try{
					cabecera.procesaCabecera(contenidoXml);
					} catch (IndicesFiscalesException ie)
					{
						cabecera=null;
					}
					log.debug("Carga del fichero en base de datos.");
					ComunicadorBD db = new ComunicadorBD(idSesion, pref);
					ResultadoLlamada res=db.enviarFichero(nombreXml,contenidoXml);
					if (TipoFinalizacion.CORRECTA.equals(res.getTipoFinalizacion()))
					{
						log.debug ("Terminación correcta.");
						reply=ReplyBuilder.buildReply(MensajesResultado.CORRECTO, false, cabecera);
					} else 
					{
						log.error("Envío de fichero a BD finalizado con error:" + res.getMensaje());
						if (TipoFinalizacion.DUPLICADO.equals(res.getTipoFinalizacion()))
						{
							log.error("El fichero de índices fiscales " + nombreXml + " ya se había recibido con anterioridad.");
							reply = ReplyBuilder.buildReply(MensajesResultado.FICHERO_DUPLICADO, true, cabecera);
						} else 
						{
							reply = ReplyBuilder.buildReply(MensajesResultado.ERROR_TECNICO, true, cabecera);
						}
					}
				}
			}
		}
		catch (IOException io)
		{
			log.error("Error al descomprimir el fichero zip:" + io.getMessage(),io);
			reply=ReplyBuilder.buildReply(MensajesResultado.ERROR_DESCOMPRESION, true);
		} catch (IllegalArgumentException iae) {
			log.error ("Error al descomprimir el fichero zip :"+iae.getMessage(),iae);
			reply = ReplyBuilder.buildReply(MensajesResultado.ERROR_TECNICO, true, cabecera);
		}
		catch (LanzadorException le){
			log.error ("Error al almacenar el fichero:" + le.getMessage(),le);
			reply = ReplyBuilder.buildReply(MensajesResultado.ERROR_TECNICO, true, cabecera);
		}
		return reply;
	}
	
	/**
	 * Descomprime el array de bytes que representa el contenido de un fichero zip, y deja  sus datos en variables de clase 
	 * @param zip
	 * @throws IOException
	 */
	private void descomprimir(byte[] zip) throws IOException, IllegalArgumentException{
		ByteArrayOutputStream baos=null;
		ZipInputStream zis=null;
		int ficheros=0;
		try
		{
			zis = new ZipInputStream(new ByteArrayInputStream(zip));
			//Suponemos una entrada o ninguna en el zip.
			ZipEntry ze;
			baos = new ByteArrayOutputStream();
			String name="";
			while ((ze=zis.getNextEntry())!=null)
			{
				if (!ze.isDirectory())
				{
					//Si es fichero, recuperamos su nombre. No hago nada con él por el momento.
					name = ze.getName().replace('\\', '/');
					if (name.indexOf('/')>-1)
					{
						String [] tokens = name.split("/");
						name = tokens[tokens.length-1]; //último elemento será el nombre de fichero
					}
					this.nombreXml=name;
					int leidos;
					byte[] buffer= new byte[8192];
					while ((leidos=zis.read(buffer))!=-1)
					{
						baos.write(buffer, 0, leidos);
					}
					this.contenidoXml= baos.toByteArray();
					ficheros++;
				}
			}
			if (ficheros>1) {
				throw new IllegalArgumentException("Más de un fichero en el archivo.");
			}
		}
		finally
		{
			if (baos!=null)
			{
				try{ baos.close(); } catch( Exception e){}
			}
			if (zis!=null)
			{
				try {zis.close();} catch(Exception e){}
			}
		}
	}
	private boolean validarEsquema(byte[] xml){
		
		MiValidador v = new MiValidador();
		if (!v.isValid(xml, "indice_fiscal.xsd"))
		{
			log.error("Error en validación de mensaje contra esquema:" + v.getCausaErrorValidacion().getMessage());
			return false;
		}
		return true;
	}
	
}
