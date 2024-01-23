package es.stpa.plusvalias;


import java.util.ArrayList;
import java.util.Map;

import es.stpa.plusvalias.datos.MediadorBD;
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
import es.stpa.plusvalias.mensajes.ConversorPeticionCalculo;
import es.stpa.plusvalias.mensajes.ConversorRespuestaCalculo;
import es.stpa.plusvalias.mensajes.ConversorPeticionCalculo.EstadoProcesoConversion;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.tributasenasturias.log.ILog;


public class CalculoPlusvaliaImpl {

	private ILog log;
	private Preferencias  pref;
	private String idLlamada;

	public CalculoPlusvaliaImpl (Preferencias pref, ILog logger, String idLlamada){
		this.pref= pref;
		this.log= logger;
		this.idLlamada = idLlamada;
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
	 * Devuelve una respuesta de error basada en un código y mensaje
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
			//Si no se puede reconocer, pues ponemos por defecto calle, ya que no permiten vacío
			dir.setTipovia(TipoVia.CL);
		}
		dir.setVia(finca.getNombreVia());
		dir.setNumerovia(finca.getNumeroVia());
		//Los valores de duplicado no coinciden con los que manejamos (ellos tienen "D", "T" y "C"), así que no lo pongo
		dir.setEscalera(finca.getEscalera());
		dir.setPlanta(finca.getPlanta());
		dir.setPuerta(finca.getPuerta());
		//Provincia y municipio, las mismas que en la entrada. Así nos aseguramos
		//que el código INE de municipio es igual, porque nosotros no 
		//trabajamos con dígito de control.
		dir.setIneprovincia(r.getPeticionOperacion().getInmueble().getDireccion().getIneprovincia());
		dir.setInemunicipio(r.getPeticionOperacion().getInmueble().getDireccion().getInemunicipio());
		inmb.setDireccion(dir);
		r.setInmueble(inmb);
	}
	/**
	 * Establece la respuesta para un contribuyente, con los datos de su cálculo
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
	 * Prepara la respuesta del cálculo
	 * @param liq Liquidación calculada del impuesto
	 */
	private RespuestaOperacionType construirRespuesta(PeticionOperacionType peticion,LiquidacionCalculada liq, Map<LiquidacionContribuyente, byte[]> impresos){
		RespuestaOperacionType respuesta= new RespuestaOperacionType(peticion);
		double sumaImportes=0;
		if (liq!=null) { //No debería ocurrir a menos que haya habido un error durante el proceso previo
			if (respuesta.getDocumentos()==null){
				respuesta.setDocumentos(new ArrayList<DocumentoType>());
			}
			for (LiquidacionContribuyente l: liq.getLiquidaciones()){
				byte[] pdf= impresos.get(l);
				setRespuestaContribuyente(respuesta, l, pdf);
				sumaImportes+= l.getImporte().getImportetotal();
			}
		}
		setInmueble(respuesta, peticion.getFinca());
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
	 * Cálculo de la liquidación
	 * @param peticion
	 * @return
	 * @throws PlusvaliaException
	 */
	public CALCULOLIQUIDACION.REPLY calcularLiquidacion (CALCULOLIQUIDACION.REQUEST peticion) throws PlusvaliaException{
		RespuestaOperacionType respuesta;
		MediadorBD bd= new MediadorBD(pref, idLlamada);
		ConversorPeticionCalculo conv= new ConversorPeticionCalculo(bd);
		EstadoProcesoConversion epc= conv.from(peticion);
		if (!epc.isError()){
			try {
				MediadorWSDocumentos docus= new MediadorWSDocumentos(pref, idLlamada);
				DirectorCalculo dirCalc= new DirectorCalculo(epc.getPeticion(), bd);
				LiquidacionCalculada liq= dirCalc.calcularPlusvalia();
				if (!liq.isError()){
					DirectorImpresion dirIm= new DirectorImpresion(docus);
					Map<LiquidacionContribuyente, byte[]> impresos= dirIm.imprimirJustificantes(liq, epc.getPeticion().getFinca(), epc.getPeticion().getDocNotarial());
					respuesta=construirRespuesta(epc.getPeticion(), liq, impresos);
				} else {
					log.error("Error en el cálculo de liquidación de plusvalía:"+ liq.getCodigoError());
					respuesta=devolverRespuestaError(epc.getPeticion(), liq.getCodigoError(), liq.getMensajeError());
				}
			} catch (PlusvaliaException pe){
				log.error("Error en el cálculo de liquidación de plusvalía:"+ pe.getMessage());
				respuesta=devolverRespuestaError(epc.getPeticion(), CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_GENERAL_CALCULO, pe.getMessage()));
			} catch (Exception e){
				log.error("Error inesperado en el cálculo de liquidación de plusvalía:"+ e.getMessage());
				respuesta=devolverRespuestaError(epc.getPeticion(),CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_GENERAL_CALCULO, e.getMessage()));
			}
		} else {
			log.error("Error en la conversión del objeto de entrada:"+ CodigosTerminacion.getMessage(epc.getCodigoError()));
			respuesta=devolverRespuestaError(epc.getPeticion(), epc.getCodigoError());
		}
		return new ConversorRespuestaCalculo().from(respuesta);
	}
}
