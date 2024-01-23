package es.stpa.plusvalias;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.documentos.MediadorEscrituras;
import es.stpa.plusvalias.documentos.MediadorWSDocumentos;
import es.stpa.plusvalias.domain.DirectorCalculo;
import es.stpa.plusvalias.domain.DirectorImpresion;
import es.stpa.plusvalias.domain.Finca;
import es.stpa.plusvalias.domain.LiquidacionContribuyente;
import es.stpa.plusvalias.domain.PeticionOperacionType;
import es.stpa.plusvalias.domain.RespuestaOperacionType;
import es.stpa.plusvalias.domain.CalculadoraLiquidacion.LiquidacionCalculada;
import es.stpa.plusvalias.domain.intercambio.DireccionInmueble;
import es.stpa.plusvalias.domain.intercambio.DocumentoType;
import es.stpa.plusvalias.domain.intercambio.ErrorType;
import es.stpa.plusvalias.domain.intercambio.ImporteType;
import es.stpa.plusvalias.domain.intercambio.Inmueble;
import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.domain.intercambio.ResultadoType;
import es.stpa.plusvalias.domain.intercambio.TipoVia;
import es.stpa.plusvalias.exceptions.PlusvaliaException;
import es.stpa.plusvalias.mensajes.ConversorPeticionPresentacion;
import es.stpa.plusvalias.mensajes.ConversorRespuestaPresentacion;
import es.stpa.plusvalias.mensajes.ConversorPeticionPresentacion.EstadoProcesoConversion;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.presentacion.PresentacionLiquidacion;
import es.stpa.plusvalias.presentacion.pago.MediadorPago;
import es.tributasenasturias.log.ILog;

/**
 * Gestiona el pago y presentaci�n de una presentaci�n de plusval�a por parte de CGN
 * @author crubencvs
 *
 */
public class PagoPresentacionImpl {

	private ILog log;
	private Preferencias  pref;
	private String idLlamada;
	
	public PagoPresentacionImpl(Preferencias pref, ILog logger, String idLlamada){
		this.log=logger;
		this.pref= pref;
		this.idLlamada= idLlamada;
	}
	
	
	/**
	 * Devuelve una respuesta de error
	 * @param peticion
	 * @param codigo
	 * @return
	 */
	private RespuestaOperacionType devolverRespuestaError(PeticionOperacionType peticion,String codigo){
		
		CodigosTerminacion c= CodigosTerminacion.newInstance(codigo);
		
		return devolverRespuestaError(peticion, c.getCodigo(), c.getMensaje());
	}
	/**
	 * Devuelve una respuesta de error basada en un c�digo y mensaje
	 * @param peticion
	 * @param codigo
	 * @param mensajeError
	 * @return
	 */
	private RespuestaOperacionType devolverRespuestaError(PeticionOperacionType peticion,String codigo, String mensajeError){
		RespuestaOperacionType respuesta= new RespuestaOperacionType(peticion);
		ResultadoType r = new ResultadoType();
		ErrorType e = new ErrorType();
		r.setEstadoError(true);
		e.setCodigo(codigo);
		if (mensajeError!=null && !mensajeError.equals("")){
			e.setDescripcion(mensajeError);
		} else {
			e.setDescripcion(CodigosTerminacion.getMessage(codigo));
		}
		r.setError(e);
		respuesta.setResultado(r);
		return respuesta;
	}
	
	/**
	 * Establece la respuesta para un contribuyente, con los datos de su c�lculo
	 * @param r
	 * @param liq
	 * @param pdf
	 */
	private void setRespuestaContribuyente(RespuestaOperacionType r,LiquidacionContribuyente liq, byte[] pdf){
		if (r.getDocumentos()==null){
			r.setDocumentos(new ArrayList<DocumentoType>());
		}
		DocumentoType doc= new DocumentoType();
		Persona contribuyente = liq.getSujetoPasivo().getDatosbasicos();
		doc.setIdsujeto(contribuyente.getIdentificador());
		ImporteType imp = new ImporteType();
		imp.setImportetotal(liq.getImporte().getImportetotal());
		imp.setImporterecargo(liq.getImporte().getImporterecargo());
		imp.setInformarecargo(liq.getImporte().informarecargo());
		doc.setImporte(imp);
		doc.setPdf(pdf);
		r.getDocumentos().add(doc);
	}
	/**
	 * Transforma los datos de la finca a datos de Inmueble de la respuesta
	 * @param r
	 * @param finca
	 */
	private void setInmueble(RespuestaOperacionType r, Finca finca){
		Inmueble inmb= new Inmueble();
		inmb.setReferenciacatastral(finca.getReferenciaCatastral());
		inmb.setSuperficieInmueble(finca.getSuperficie());
		DireccionInmueble dir= new DireccionInmueble();
		try {
			TipoVia tipoVia= TipoVia.fromValue(finca.getSiglaVia());
			dir.setTipovia(tipoVia);
		} catch (Exception e){
			//Si no se puede reconocer, pues ponemos por defecto calle, ya que no permiten vac�o
			dir.setTipovia(TipoVia.CL);
		}
		dir.setVia(finca.getNombreVia());
		dir.setNumerovia(finca.getNumeroVia());
		//Los valores de duplicado no coinciden con los que manejamos (ellos tienen "D", "T" y "C"), as� que no lo pongo
		dir.setEscalera(finca.getEscalera());
		dir.setPlanta(finca.getPlanta());
		dir.setPuerta(finca.getPuerta());
		//Provincia y municipio, las mismas que en la entrada. As� nos aseguramos
		//que el c�digo INE de municipio es igual, porque nosotros no 
		//trabajamos con d�gito de control.
		dir.setIneprovincia(r.getPeticionOperacion().getInmueble().getDireccion().getIneprovincia());
		dir.setInemunicipio(r.getPeticionOperacion().getInmueble().getDireccion().getInemunicipio());
		inmb.setDireccion(dir);
		r.setInmueble(inmb);
	}
	
	/**
	 * Prepara la respuesta correcta de la presentaci�n
	 * @param liq Liquidaci�n calculada del impuesto
	 */
	private RespuestaOperacionType construirRespuestaOK(PeticionOperacionType peticion, List<PresentacionLiquidacion> presentaciones, Map<PresentacionLiquidacion, byte[]> impresos){
		RespuestaOperacionType respuesta= new RespuestaOperacionType(peticion);
		setInmueble(respuesta, peticion.getFinca());
		double sumaImportes=0;
		if (presentaciones!=null) { //No deber�a ocurrir a menos que haya habido un error durante el proceso previo
			if (respuesta.getDocumentos()==null){
				respuesta.setDocumentos(new ArrayList<DocumentoType>());
			}
			for (PresentacionLiquidacion p: presentaciones){
				byte[] pdf= impresos.get(p);
				setRespuestaContribuyente(respuesta, p.getLiquidacionContribuyente(), pdf);
				sumaImportes+= p.getLiquidacionContribuyente().getImporte().getImportetotal();
			}
		}
		respuesta.setImportetotal(sumaImportes);
		ResultadoType r = new ResultadoType();
		ErrorType e = new ErrorType();
		r.setEstadoError(false);
		CodigosTerminacion c=CodigosTerminacion.newInstance(CodigosTerminacion.OK);
		e.setCodigo(c.getCodigo());
		e.setDescripcion(c.getMensaje());
		r.setError(e);
		respuesta.setResultado(r);
		return respuesta;
	}
	
	/**
	 * Comprueba que haya correspondencia entre el n�mero de �rdenes de pago y los sujetos
	 * de la liquidaci�n calculada.
	 * @param peticion
	 * @param liq
	 * @return
	 */
	private boolean correspondenciaSujetosOrdenesPago(PeticionOperacionType peticion, LiquidacionCalculada liq){
		if (liq.getLiquidaciones().size()!=peticion.getOrdenesPago().length()){
			return false;
		}
		Persona contribuyente;
		for (LiquidacionContribuyente l: liq.getLiquidaciones()){
			contribuyente= l.getSujetoPasivo().getDatosbasicos();
			if (peticion.getOrdenesPago().getOrdenPagoPersona(contribuyente)==null){
				return false;
			}
		}
		return true;
	}
	/**
	 * Presentaci�n de la liquidaci�n, que puede comprender a varios 
	 * transmitentes y adquirentes
	 * @param peticion Petici�n recibida en el servicio
	 * @param escritura Escritura recibida, si hubiese.
	 * @return Objeto de salida del servicio
	 * @throws PlusvaliaException
	 */
	public PAGOLIQUIDACION.REPLY presentarLiquidacion( es.stpa.plusvalias.PAGOLIQUIDACION.REQUEST peticion, byte[] escritura) throws PlusvaliaException{
		RespuestaOperacionType respuesta=null;
		MediadorBD bd= new MediadorBD(pref, idLlamada);
		MediadorPago pago= new MediadorPago(pref, idLlamada);
		MediadorWSDocumentos docus= new MediadorWSDocumentos(pref, idLlamada);
		boolean errorProducido=false;
		ConversorPeticionPresentacion conv= new ConversorPeticionPresentacion(bd);
		EstadoProcesoConversion epc= conv.from(peticion);
		if (!epc.isError()){
			try {
				//MediadorWSDocumentos docus= new MediadorWSDocumentos(pref, idLlamada);
				DirectorCalculo dirCalc= new DirectorCalculo(epc.getPeticion(), bd);
				log.info("Realizamos el c�lculo de las presentaciones");
				LiquidacionCalculada liq= dirCalc.calcularPlusvalia();
				if (!liq.isError()){
					if (!correspondenciaSujetosOrdenesPago(epc.getPeticion(), liq)){
						if (!correspondenciaSujetosOrdenesPago(epc.getPeticion(), liq)){
							log.error ("Error en la presentaci�n de la liquidaci�n de plusval�a. El n�mero o los sujetos de las �rdenes de pago no coinciden con las del c�lculo");
							respuesta=devolverRespuestaError(epc.getPeticion(), CodigosTerminacion.ERROR_GENERAL_ORDENES);
						}
					} else {
						//Hemos de insertar la escritura. Se hace fuera del bucle 
						//de sujetos, por razones obvias.
						if (escritura!=null){
							log.info("Inserci�n de la escritura adjunta");
							MediadorEscrituras escrituras=new MediadorEscrituras(pref, idLlamada);
							PeticionOperacionType peti = epc.getPeticion();
							escrituras.recibirEscritura(peti.getCabecera().getReceptor(), //Creo que es el ayuntamiento...
														peti.getFinca().getCodMunicipio(), //Este se pone mientras no sepamos qui�n es el receptor.
														peti.getDocNotarial().getNotarioAutorizante().getCodigo(), 
														peti.getDocNotarial().getNotarioAutorizante().getCodNotaria(),
														peti.getDocNotarial().getProtocolo().getNumero(), 
														peti.getDocNotarial().getProtocolo().getNumerobis(), 
														peti.getDocNotarial().getProtocolo().getFechaautorizacion(), 
														peti.getDocNotarial().getNotarioAutorizante().getNombre()+" "+ 
														peti.getDocNotarial().getNotarioAutorizante().getApellidos(), 
														peti.getDocNotarial().getNotarioAutorizante().getPlaza(), 
														peti.getDatosEscritura().getNombre(), 
														escritura);
						}
						List<PresentacionLiquidacion> presentaciones= new ArrayList<PresentacionLiquidacion>();
						log.info("Realizamos la presentaci�n de las liquidaciones individuales");
						for (LiquidacionContribuyente l: liq.getLiquidaciones()){
							PresentacionLiquidacion pl= new PresentacionLiquidacion(epc.getPeticion(),l,bd, pago, log);
							PresentacionLiquidacion.ResultadoPresentacion res=pl.presentar();
							if (res.isError()){
								respuesta= devolverRespuestaError(epc.getPeticion(), res.getCodigoResultado(), res.getMensajeResultado());
								errorProducido=true;
								break;
							} 
							presentaciones.add(pl);
						}
						if (!errorProducido){
							DirectorImpresion dirIm= new DirectorImpresion(docus);
							//CRUBENCVS 18/08/2022 Mientras no sepamos qu� imprimir, recupero los justificantes de presentaci�n de Tributas  
							Map<PresentacionLiquidacion, byte[]> impresos= dirIm.imprimirJustificantesPresentacion(presentaciones, bd);
							//Map<PresentacionLiquidacion, byte[]> impresos= dirIm.imprimirReciboPresentacion(presentaciones, epc.getPeticion().getFinca(), epc.getPeticion().getDocNotarial());
							respuesta= construirRespuestaOK(epc.getPeticion(), presentaciones,  impresos);
						}
					}
				} else {
					log.error("Error en la presentaci�n de liquidaci�n de plusval�a:"+ liq.getCodigoError());
					respuesta=devolverRespuestaError(epc.getPeticion(), liq.getCodigoError(), liq.getMensajeError());
				}
			} catch (PlusvaliaException pe){
				log.error("Error en la presentaci�n de liquidaci�n de plusval�a:"+ pe.getMessage());
				respuesta= devolverRespuestaError(epc.getPeticion(), CodigosTerminacion.ERROR_GENERAL_PRESENTACION, CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_GENERAL_PRESENTACION, pe.getMessage()));
			} catch (Exception e){
				log.error("Error inesperado en la presentaci�n de liquidaci�n de plusval�a:"+ e.getMessage());
				respuesta=devolverRespuestaError(epc.getPeticion(), CodigosTerminacion.ERROR_GENERAL_PRESENTACION, CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_GENERAL_PRESENTACION, e.getMessage()));
			}
		} else {
			log.error("Error en la conversi�n del objeto de entrada:"+ CodigosTerminacion.getMessage(epc.getCodigoError()));
			respuesta= devolverRespuestaError(epc.getPeticion(), epc.getCodigoError(), epc.getMensajeError());
		}
		ConversorRespuestaPresentacion cr= new ConversorRespuestaPresentacion();
		return cr.from(respuesta);
		
		 
	}
		
}
