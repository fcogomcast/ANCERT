package es.tributasenasturias.indices_fiscales;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import es.tributasenasturias.indices_fiscales.utils.Base64;
import es.tributasenasturias.indices_fiscales.utils.contextoLlamada.CallContext;
import es.tributasenasturias.indices_fiscales.utils.contextoLlamada.CallContextConstants;
import es.tributasenasturias.indices_fiscales.utils.log.LogFactory;
import es.tributasenasturias.indices_fiscales.utils.log.Logger;
import es.tributasenasturias.indices_fiscales.utils.preferencias.Preferencias;

/**
 * Gestiona los backups de los ficheros recibidos.
 * @author crubencvs
 *
 */
public class GestorBackups {

	/**
	 * Realiza el backup del fichero zip recibido.
	 * Lo guardará con el mismo nombre que el id de sesión, para que sea más fácil encontrarlo
	 * ya que no tenemos el nombre de fichero con el que llegó.
	 * No lanza errores nunca, pero si fallase grabará un error en el fichero de servidor.
	 * @param zip contenido del fichero
	 * @param context Contexto de llamada, para recuperar las preferencias y su id 
	 */
	public void hacerBackup(byte[] zip, CallContext context) {
		BufferedOutputStream bo = null;
		String idSesion="";
		Preferencias pref=null;
		try {
			idSesion = (String) context.get(CallContextConstants.IDSESION);
			pref = (Preferencias) context.get(CallContextConstants.PREFERENCIAS);
			//Se graba en server también si  se pone en debug
			Logger server = LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogServer(), idSesion);
			if ("ALL".equals(pref.getModoLog()) || "DEBUG".equals(pref.getModoLog())) {
				server.debug(new String(Base64.encode(zip)));
			}
			if (pref.hacerBackup())
			{
				String ruta = pref.getRutaBackup();
				if (ruta==null)
				{
					return;
				}
				if (ruta.charAt(ruta.length()-1)=='/')
				{
					ruta = ruta.substring(0, ruta.length()-1);
				}
				File p = new File(ruta);
				if (!p.exists()) {
					if (!p.mkdir()){
						return;
					}
				}
				String fichero  = ruta+"/"+idSesion;
				bo = new BufferedOutputStream(new FileOutputStream(fichero));
				bo.write(zip);
				server.info("Realizado backup en " + fichero);
			}
		}
		catch (Exception e)
		{
			try {
				//Intentamos hacer log del error. Si no podemos, no hacemos nada
				if (pref!=null)
				{
					Logger server = LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogServer(), idSesion);
					server.error("Error al realizar backup del fichero");
				}
			} catch (Exception ep) 
			{} 
		}
		finally 
		{
			if (bo!=null) 
			{
				try { bo.close(); } catch (Exception e)  {}
			}
		}
	}
}
