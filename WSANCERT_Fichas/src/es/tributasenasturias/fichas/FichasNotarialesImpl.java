package es.tributasenasturias.fichas;

import es.tributasenasturias.fichas.comprimido.GestorFichaComprimida;
import es.tributasenasturias.fichas.datos.GestorAccesoDatos;
import es.tributasenasturias.fichas.excepciones.AccesoDatosException;
import es.tributasenasturias.fichas.excepciones.AltaFichaException;
import es.tributasenasturias.fichas.excepciones.ComprimidoException;
import es.tributasenasturias.fichas.log.ILog;
import es.tributasenasturias.fichas.preferencias.Preferencias;


public class FichasNotarialesImpl {

	public ResultadoAltaFicha altaFicha (IdEscrituraType idEscritura,
										 String origenFicha, 
										 String codigoAyuntamiento,
										 String autorizacionEnvioDiligencias,
										 byte[] contenidoArchivoComprimido,
										 Preferencias pref,
										 ILog log,
										 String idSesion) throws AltaFichaException
	{
		boolean incompleto=false;
		ResultadoAltaFicha res = new ResultadoAltaFicha();
		res.setIdEscritura(idEscritura);
		
		try {
			log.info ("Se validan los parámetros de entrada");
			ValidadorDatos.ValidacionResult resVal = ValidadorDatos.validarDatosAlta(
					                                  idEscritura, 
 										              origenFicha, 
										              codigoAyuntamiento, 
										              autorizacionEnvioDiligencias, 
										              contenidoArchivoComprimido);
			if (!resVal.isValido()){
				res.setEsError(true);
				res.setCodigo("0030");
				res.setMensaje(resVal.getMensaje());
			}
			else {
				log.info ("Se procede a descomprimir el contenido del archivo");
				GestorFichaComprimida.Contenido contenidoArchivo = GestorFichaComprimida.descomprimir(contenidoArchivoComprimido);
				log.info("Contenido descomprimido. Se valida el contenido");
				if (contenidoArchivo.getContenidoPdf()==null && contenidoArchivo.getContenidoXml()==null){
					log.error("No se ha recibido el contenido del PDF ni del XML. Se termina el proceso");
					res.setEsError(true);
					res.setCodigo("0100");
					res.setMensaje("No se ha recibido el contenido del PDF ni del XML");
				} else{ 
					
					if (contenidoArchivo.getContenidoPdf()==null){
						log.error("No se ha recibido el contenido del PDF. Se grabará igualmente el XML ");
						incompleto=true;
					}
					if (contenidoArchivo.getContenidoXml()==null){
						log.error("No se ha recibido el contenido del XML. Se grabará igualmente el PDF");
						incompleto=true;
					}
					
					log.info ("Se graban la ficha en base de datos");
					//Grabamos lo que podemos.
					GestorAccesoDatos datos = new GestorAccesoDatos(pref, idSesion);
					GestorAccesoDatos.ResultadoInsercionFicha resInsercion = 
						datos.insertarFicha(idEscritura.getCodigoNotario(), 
											idEscritura.getCodigoNotaria(), 
											idEscritura.getNumeroProtocolo(), 
											idEscritura.getProtocoloBis(), 
											idEscritura.getFechaAutorizacion(), 
											contenidoArchivo.getNombrePdf(), 
											contenidoArchivo.getNombreXml(), 
											contenidoArchivo.getContenidoPdf(), 
											contenidoArchivo.getContenidoXml(), 
											origenFicha, 
											codigoAyuntamiento,
											autorizacionEnvioDiligencias);
					if (!resInsercion.isError()){
						log.info("Ficha grabada");
						//Grabado, puede ser completo o parcial
						if (!incompleto){
							res.setCodigo("0000");
							res.setMensaje("Ficha insertada correctamente");
						} else {
							res.setCodigo("0001");
							res.setMensaje("Ficha insertada parcialmente. No se ha insertado uno de los dos ficheros por venir vacío");
						}
						res.setEsError(false);
					}
					else {
						log.info("Ficha no grabada:" + resInsercion.getMensajeError());
						res.setEsError(true);
						res.setCodigo("0200");
						res.setMensaje("No se ha podido almacenar el contenido de la ficha:"+ resInsercion.getMensajeError());
					}
				}
			}
			return res;
		} catch (ComprimidoException ce){
			throw new AltaFichaException("Error en descompresión:" + ce.getMessage(),ce);
		} catch (AccesoDatosException ade){
			throw new AltaFichaException("Error en acceso a base de datos:" + ade.getMessage(), ade);
		}
	}
}
