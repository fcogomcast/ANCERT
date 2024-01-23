package es.stpa.plusvalias.mensajes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import es.stpa.plusvalias.CALCULOLIQUIDACION;
import es.stpa.plusvalias.DUPLICADOType;
import es.stpa.plusvalias.DireccionType;
import es.stpa.plusvalias.TIPOVIAType;
import es.stpa.plusvalias.SujetoType.DATOSPERSONALES;
import es.stpa.plusvalias.domain.RespuestaOperacionType;
import es.stpa.plusvalias.domain.intercambio.ActoJuridico;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Cabecera;
import es.stpa.plusvalias.domain.intercambio.Notario;
import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.domain.intercambio.Protocolo;
import es.stpa.plusvalias.domain.intercambio.ResultadoType;
import es.stpa.plusvalias.domain.intercambio.Transmision;
import es.stpa.plusvalias.domain.intercambio.Transmitente;

/**
 * Conversiones de/hacia {@link RespuestaOperacionType}
 * @author crubencvs
 *
 */
public class ConversorRespuestaCalculo {

	//CRUBENCVS  10/03/2023
	/**
	 * Para devolver los opcionales que sean nulos o no tengan valor como nulos,
	 * de tal forma que el mensaje de vuelta no contenga el nodo.
	 * @param campo
	 * @return
	 */
	private String toOptional(String campo){
		if (campo==null || "".equals(campo)){
			return null;
		}
		return campo;
	}
	/**
	 * Conversión de {@link Cabecera} a {@link es.stpa.plusvalias.CabeceraType}
	 * @param c
	 * @return
	 */
	private es.stpa.plusvalias.CabeceraType getCabecera(Cabecera c){
		if (c==null){
			return null;
		}
		es.stpa.plusvalias.CabeceraType cab = new es.stpa.plusvalias.CabeceraType();
		cab.setIDCOMUNICACION(c.getIdcomunicacion());
		cab.setOPERACION(c.getOperacion());
		cab.setEMISOR(c.getReceptor()); //Se invierten en la respuesta
		cab.setRECEPTOR(c.getEmisor());
		Calendar cal= new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		cab.setFECHA(sdf.format(cal.getTime()));
		sdf= new SimpleDateFormat("HH:mm:ss");
		cab.setHORA(sdf.format(cal.getTime()));
		cab.setAPLICACION(c.getAplicacion());
		return cab;
	}
	/**
	 * Conversión de {@link Notario} a {@link es.stpa.plusvalias.NotarioType} 
	 * @param n
	 * @return
	 */
	private es.stpa.plusvalias.NotarioType getNotario(Notario n){
		if (n==null){
			return null;
		}
		es.stpa.plusvalias.NotarioType not= new es.stpa.plusvalias.NotarioType();
		not.setCODIGO(n.getCodigo());
		not.setNOMBRE(n.getNombre());
		not.setAPELLIDO1(n.getApellido1());
		not.setAPELLIDO2(n.getApellido2());
		return not;
	}

	/**
	 * Conversión de {@link Protocolo} a  {@link es.stpa.plusvalias.ProtocoloType} 
	 * @param p
	 * @return
	 */
	private es.stpa.plusvalias.ProtocoloType getProtocolo(Protocolo p){
		if (p==null){
			return null;
		}
		es.stpa.plusvalias.ProtocoloType pro= new es.stpa.plusvalias.ProtocoloType();
		pro.setNUMERO(p.getNumero());
		pro.setNUMEROBIS(p.getNumerobis());
		pro.setFECHAAUTORIZACION(p.getFechaautorizacion());
		return pro;
	}
	/**
	 * Conversión de {@link ActoJuridico} a {@link es.stpa.plusvalias.ActoJuridicoType}
	 * @param a
	 * @return
	 */
	private es.stpa.plusvalias.ActoJuridicoType getActoJuridico(ActoJuridico a){
		if (a==null){
			return null;
		}
		es.stpa.plusvalias.ActoJuridicoType act= new es.stpa.plusvalias.ActoJuridicoType();
		act.setCODIGO(a.getCodigo());
		act.setDESCRIPCION(a.getDescripcion());
		return act;
	}
	/**
	 * Conversión de {@link Persona} a {@link es.stpa.plusvalias.SujetoType}
	 * @param sujeto
	 * @return
	 */
	private es.stpa.plusvalias.SujetoType getDatosSujeto(Persona sujeto){
		if (sujeto==null){
			return null;
		}
		es.stpa.plusvalias.SujetoType s= new es.stpa.plusvalias.SujetoType();
		s.setIDENTIFICADOR(sujeto.getIdentificador());
		
		DATOSPERSONALES dpt= new DATOSPERSONALES();
		if (sujeto.getDatospersonales()!=null){
			dpt.setTIPODOCUMENTO(sujeto.getDatospersonales().getTipodocumento());
			dpt.setNUMERODOCUMENTO(sujeto.getDatospersonales().getNumerodocumento());
			dpt.setNOMBRE(sujeto.getDatospersonales().getNombre());
			dpt.setAPELLIDO1RAZONSOCIAL(sujeto.getDatospersonales().getApellido1RAZONSOCIAL());
			dpt.setAPELLIDO2(sujeto.getDatospersonales().getApellido2());
	
			s.setDATOSPERSONALES(dpt);
		}
		
		DireccionType din = new DireccionType();
		if (sujeto.getDomicilio()!=null){
			din.setINEPROVINCIA(sujeto.getDomicilio().getIneprovincia());
			din.setINEMUNICIPIO(sujeto.getDomicilio().getInemunicipio());
			if (sujeto.getDomicilio().getTipovia()!=null){
				din.setTIPOVIA(TIPOVIAType.valueOf(sujeto.getDomicilio().getTipovia().value()));
			}
			din.setVIA(sujeto.getDomicilio().getVia());
			//CRUBENCVS  10/03/2023. Controlo los opcionales
			din.setNUMEROVIA(toOptional(sujeto.getDomicilio().getNumerovia()));
			din.setESCALERA(toOptional(sujeto.getDomicilio().getEscalera()));
			din.setPLANTA(toOptional(sujeto.getDomicilio().getPlanta()));
			din.setPUERTA(toOptional(sujeto.getDomicilio().getPuerta()));
			if (sujeto.getDomicilio().getDuplicado()!=null){
				din.setDUPLICADO(DUPLICADOType.valueOf(sujeto.getDomicilio().getDuplicado().value()));
			}
			
			din.setBLOQUE(sujeto.getDomicilio().getBloque());
			din.setAPRKM(sujeto.getDomicilio().getAprkm());
			din.setENTIDADMENOR(toOptional(sujeto.getDomicilio().getEntidadmenor()));
			din.setRESTO(toOptional(sujeto.getDomicilio().getResto()));
			din.setCODIGOPOSTAL(sujeto.getDomicilio().getCodigopostal());
			
			s.setDOMICILIO(din);
		}
		
		es.stpa.plusvalias.DireccionExtranjeraType dex = new es.stpa.plusvalias.DireccionExtranjeraType();
		if (sujeto.getDomicilioextranjero() != null) {
			dex.setPAIS(sujeto.getDomicilioextranjero().getPais());
			dex.setESTPRV(toOptional(sujeto.getDomicilioextranjero().getEstprv()));
			dex.setDESCMUNICIPIO(sujeto.getDomicilioextranjero().getDescmunicipio());
			dex.setDIRECCIONNOESTRUCTURADA(sujeto.getDomicilioextranjero().getDireccionnoestructurada());
			dex.setDISTRITOPOSTAL(toOptional(sujeto.getDomicilioextranjero().getDistritopostal()));
			
			s.setDOMICILIOEXTRANJERO(dex);
		}
		
		return s;
	}
	/**
	 * Conversión de {@link List}  de {@link Transmitente} a {@link TRANSMITENTES} 
	 * @param transmitentes
	 * @return
	 */
    private CALCULOLIQUIDACION.REPLY.TRANSMITENTES  getTransmitentes ( List<Transmitente> transmitentes){
		if (transmitentes==null) {
			return null;
		}
	    CALCULOLIQUIDACION.REPLY.TRANSMITENTES ret = new CALCULOLIQUIDACION.REPLY.TRANSMITENTES();
		for (Transmitente t: transmitentes){
			es.stpa.plusvalias.TransmitenteType trans = new es.stpa.plusvalias.TransmitenteType();
			
			//Datos básicos
			trans.setDATOSBASICOS(getDatosSujeto(t.getDatosbasicos()));
			
			//Otros datos
			
			trans.setPORCENTAJETRANSMITIDO(t.getPorcentajetransmitido());
			trans.setCLASEDERECHO(t.getClasederecho());
			trans.setFECHADEFUNCION(t.getFechadefuncion());
			trans.setEDADUSUFRUCTUARIO(t.getEdadusufructuario());
			trans.setANYOSUSUFRUCTO(t.getAnyosusufructo());
			trans.setEDADUSO(t.getEdaduso());
			trans.setANYOSUSO(t.getAnyosuso());
			
			//CRUBENCVS 10/03/2023
			if (t.getTransmisionesanteriores().size()>0){
				trans.setTRANSMISIONESANTERIORES(new es.stpa.plusvalias.TransmitenteType.TRANSMISIONESANTERIORES());
				//Transmisiones anteriores
				for (Transmision tant: t.getTransmisionesanteriores()){
					es.stpa.plusvalias.TransmitenteType.TRANSMISIONESANTERIORES.TRANSMISION ant= new es.stpa.plusvalias.TransmitenteType.TRANSMISIONESANTERIORES.TRANSMISION();
					ant.setFECHA(tant.getFecha());
					ant.setPORCENTAJE(tant.getPorcentaje());
					// 14/02/2022. Valor de la transmisión anterior
					ant.setVALOR(tant.getValor());
					trans.getTRANSMISIONESANTERIORES().getTRANSMISION().add(ant);
				}
			}
			
			//Bonificaciones
			
			if (t.getBonificacion()!=null){
				es.stpa.plusvalias.TransmitenteType.BONIFICACIONES bon = new  es.stpa.plusvalias.TransmitenteType.BONIFICACIONES();
				bon.setPORCENTAJE(t.getBonificacion().getPorcentaje());
				bon.setCONCEPTO(t.getBonificacion().getConcepto());
				trans.setBONIFICACIONES(bon);
			}

			//Exenciones
			if (t.getExencion()!=null){
				es.stpa.plusvalias.TransmitenteType.EXENCIONES ex= new es.stpa.plusvalias.TransmitenteType.EXENCIONES();
				ex.setPORCENTAJE(t.getExencion().getPorcentaje());
				ex.setCONCEPTO(t.getExencion().getConcepto());
				trans.setEXENCIONES(ex);
			}
			
			//Representantes
			if (t.getRepresentantes()!=null){
				es.stpa.plusvalias.TransmitenteType.REPRESENTANTES repr = new es.stpa.plusvalias.TransmitenteType.REPRESENTANTES();
				for (Long idRepresentante:t.getRepresentantes()){
					repr.getIDREPRESENTANTE().add(idRepresentante);
				}
				trans.setREPRESENTANTES(repr);
			}
			// CRUBENCVS 14/02/2022 Valor de la transmisión
			ret.getTRANSMITENTE().add(trans);
		}
		return ret;
	}
    /**
     * Conversión de {@link Adquirente} a {@link CALCULOLIQUIDACION.REPLY.ADQUIRENTES}
     * @param adquirentes
     * @return
     */
    private CALCULOLIQUIDACION.REPLY.ADQUIRENTES  getAdquirentes ( List<Adquirente> adquirentes){
		if (adquirentes==null){
			return null;
		}
	    CALCULOLIQUIDACION.REPLY.ADQUIRENTES ret = new CALCULOLIQUIDACION.REPLY.ADQUIRENTES();
		for (Adquirente a: adquirentes){
			es.stpa.plusvalias.AdquirenteType adq = new es.stpa.plusvalias.AdquirenteType();
			
			//Datos básicos
			adq.setDATOSBASICOS(getDatosSujeto(a.getDatosbasicos()));
			
			//Otros datos
			
			adq.setPORCENTAJEADQUIRIDO(a.getPorcentajeadquirido());
			adq.setCLASEDERECHO(a.getClasederecho());
			adq.setEDADUSUFRUCTUARIO(a.getEdadusufructuario());
			adq.setANYOSUSUFRUCTO(a.getAnyosusufructo());
			adq.setEDADUSO(a.getEdaduso());
			adq.setANYOSUSO(a.getAnyosuso());
			
			if (a.getFechaProrrogaSolicitada()!=null){
				es.stpa.plusvalias.AdquirenteType.PRORROGASOLICITADA prorroga= new es.stpa.plusvalias.AdquirenteType.PRORROGASOLICITADA();
				prorroga.setFECHA(a.getFechaProrrogaSolicitada());
				adq.setPRORROGASOLICITADA(prorroga);
			}
			
			//Representantes
			if (a.getRepresentantes()!=null){
				es.stpa.plusvalias.AdquirenteType.REPRESENTANTES repr = new es.stpa.plusvalias.AdquirenteType.REPRESENTANTES();
				for (Long idRepresentante:a.getRepresentantes()){
					repr.getIDREPRESENTANTE().add(idRepresentante);
				}
				adq.setREPRESENTANTES(repr);
			}
			
			ret.getADQUIRENTE().add(adq);
		}
		return ret;
	}
    /**
     * Conversión de {@link es.stpa.plusvalias.domain.intercambio.Inmueble} a {@link CALCULOLIQUIDACION.REPLY.INMUEBLE}
     * @param inmueble
     * @return
     */
    private CALCULOLIQUIDACION.REPLY.INMUEBLE getInmueble(es.stpa.plusvalias.domain.intercambio.Inmueble inmueble){
    	if (inmueble==null){
    		return null;
    	}
    	CALCULOLIQUIDACION.REPLY.INMUEBLE inm = new CALCULOLIQUIDACION.REPLY.INMUEBLE();
    	inm.setREFERENCIACATASTRAL(inmueble.getReferenciacatastral());
    	if (inmueble.getDireccion()!=null){
    		es.stpa.plusvalias.DireccionInmuebleType d= new es.stpa.plusvalias.DireccionInmuebleType();
    		d.setINEPROVINCIA(inmueble.getDireccion().getIneprovincia());
    		d.setINEMUNICIPIO(inmueble.getDireccion().getInemunicipio());
    		if (inmueble.getDireccion().getTipovia()!=null){
    			d.setTIPOVIA(TIPOVIAType.fromValue(inmueble.getDireccion().getTipovia().value()));
    		}
    		
    		d.setVIA(inmueble.getDireccion().getVia());
   			d.setNUMEROVIA(toOptional(inmueble.getDireccion().getNumerovia()));
    		d.setESCALERA(toOptional(inmueble.getDireccion().getEscalera()));
    		d.setPLANTA(toOptional(inmueble.getDireccion().getPlanta()));
    		d.setPUERTA(toOptional(inmueble.getDireccion().getPuerta()));
    		if (inmueble.getDireccion().getDuplicado()!=null){
    			d.setDUPLICADO(DUPLICADOType.fromValue(inmueble.getDireccion().getDuplicado().value()));
    		}
    		d.setBLOQUE(toOptional(inmueble.getDireccion().getBloque()));
    		if (inmueble.getDireccion().getAprkm()!=null){
    			d.setAPRKM(String.valueOf(inmueble.getDireccion().getAprkm()));
    		}
    		d.setENTIDADMENOR(toOptional(inmueble.getDireccion().getEntidadmenor()));
    		d.setRESTO(toOptional(inmueble.getDireccion().getResto()));
    		d.setCODIGOPOSTAL(inmueble.getDireccion().getCodigopostal());
    		inm.setDIRECCION(d);
    	}
    	inm.setSUPERFICIEINMUEBLE(inmueble.getSuperficieInmueble());
		return inm;
	}
    /**
     * Conversión de {@link List} de {@link Persona} a {@link CALCULOLIQUIDACION.REPLY.REPRESENTANTES}
     * @param representantes
     * @return
     */
    private CALCULOLIQUIDACION.REPLY.REPRESENTANTES getRepresentantes(List<Persona> representantes){
    	if (representantes==null || representantes.size()==0){
    		return null;
    	}
    	CALCULOLIQUIDACION.REPLY.REPRESENTANTES r = new CALCULOLIQUIDACION.REPLY.REPRESENTANTES();
		for (Persona s: representantes){
			r.getREPRESENTANTE().add(getDatosSujeto(s));
		}
		return r;
	}
    /**
     * Conversión de {@link List} de {@link DocumentoType} a {@link CALCULOLIQUIDACION.REPLY.DOCUMENTOS}
     * @param documentos
     * @return
     */
    private CALCULOLIQUIDACION.REPLY.DOCUMENTOS getDocumentos(List<es.stpa.plusvalias.domain.intercambio.DocumentoType> documentos){
    	CALCULOLIQUIDACION.REPLY.DOCUMENTOS docs= new CALCULOLIQUIDACION.REPLY.DOCUMENTOS();
    	if (documentos==null){
    		return null;
    	}
    	for (es.stpa.plusvalias.domain.intercambio.DocumentoType d: documentos){
    		CALCULOLIQUIDACION.REPLY.DOCUMENTOS.DOCUMENTO elem= new CALCULOLIQUIDACION.REPLY.DOCUMENTOS.DOCUMENTO();
    		CALCULOLIQUIDACION.REPLY.DOCUMENTOS.DOCUMENTO.IMPORTE imp = new CALCULOLIQUIDACION.REPLY.DOCUMENTOS.DOCUMENTO.IMPORTE ();
    		
    		elem.setIDSUJETO(d.getIdsujeto());
    		//09/06/2023. Formateo los importes de salida a formato español y dos decimales.
    		imp.setIMPORTETOTAL(getImporteFormateado(d.getImporte().getImportetotal(),2));
    		imp.setIMPORTERECARGO(getImporteFormateado(d.getImporte().getImporterecargo(),2));
    		imp.setPORCENTAJERECARGO(d.getImporte().getPorcentajerecargo());
    		imp.setINFORMARECARGO(d.getImporte().informarecargo());
    		elem.setIMPORTE(imp);
    		elem.setPDF(d.getPdf());
    		docs.getDOCUMENTO().add(elem);
    	}
    	return docs;
    }
    /**
     * Conversión de {@link ResultadoType} a {@link CALCULOLIQUIDACION.REPLY.RESULTADO}
     * @param resultado
     * @return
     */
    private CALCULOLIQUIDACION.REPLY.RESULTADO getResultado(ResultadoType resultado){
    	CALCULOLIQUIDACION.REPLY.RESULTADO r = new CALCULOLIQUIDACION.REPLY.RESULTADO();
    	if (resultado==null){
    		return null;
    	}
    	
    	if (resultado.getError()!=null){
    		CALCULOLIQUIDACION.REPLY.RESULTADO.ERROR error = new CALCULOLIQUIDACION.REPLY.RESULTADO.ERROR();
    		error.setCODIGO(resultado.getError().getCodigo());
    		error.setDESCRIPCION(resultado.getError().getDescripcion());
    		r.setERROR(error);
    	}
    	r.setRETORNO(resultado.esEstadoError());
    	return r;
    }
    /**
     * Conversión de {@link RespuestaOperacionType} a {@CALCULOLIQUIDACION.REPLY}
     * @param reply
     * @return
     */
    public CALCULOLIQUIDACION.REPLY from (RespuestaOperacionType reply){
    	CALCULOLIQUIDACION.REPLY ret= new CALCULOLIQUIDACION.REPLY();
    	ret.setCABECERA(getCabecera(reply.getPeticionOperacion().getCabecera()));
	    //Retorno tiene "TRUE" si es error
	    if (!reply.getResultado().esEstadoError()){
	    	ret.setNOTARIOTITULAR(getNotario(reply.getPeticionOperacion().getNotariotitular()));
	    	ret.setNOTARIOAUTORIZANTE(getNotario(reply.getPeticionOperacion().getNotarioautorizante()));
	    	ret.setPROTOCOLO(getProtocolo(reply.getPeticionOperacion().getProtocolo()));
	    	ret.setACTOJURIDICO(getActoJuridico(reply.getPeticionOperacion().getActojuridico()));
	    	ret.setTRANSMITENTES(getTransmitentes(reply.getPeticionOperacion().getTransmitentes()));
	    	ret.setADQUIRENTES(getAdquirentes(reply.getPeticionOperacion().getAdquirentes()));
	    	ret.setINMUEBLE(getInmueble(reply.getInmueble()));
	    	ret.setREPRESENTANTES(getRepresentantes(reply.getPeticionOperacion().getRepresentantes()));
	    	//09/06/2023. Formateo los importes de salida a formato español y dos decimales.
	    	ret.setIMPORTETOTAL(getImporteFormateado(reply.getImportetotal(),2));
	    	ret.setDOCUMENTOS(getDocumentos(reply.getDocumentos()));
    	}
	    ret.setRESULTADO(getResultado(reply.getResultado()));
    	
		return ret;
	}
    //09/06/2023
    /**
     * Convierte de un double a un importe formateado
     * @param importe
     * @param decimales
     * @return
     */
    private String getImporteFormateado(double importe, int decimales){
    	BigDecimal bd = new BigDecimal(Double.toString(importe));
        bd = bd.setScale(decimales,RoundingMode.HALF_UP);
        return Double.toString(bd.doubleValue()).replace(".",",");
    }
}
