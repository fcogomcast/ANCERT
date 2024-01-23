package es.stpa.plusvalias.mensajes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.ORDENPAGOType;
import es.stpa.plusvalias.PAGOLIQUIDACION;
import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.domain.DocumentoNotarial;
import es.stpa.plusvalias.domain.Finca;
import es.stpa.plusvalias.domain.NotarioInterviniente;
import es.stpa.plusvalias.domain.OrdenPago;
import es.stpa.plusvalias.domain.OrdenesPago;
import es.stpa.plusvalias.domain.PeticionOperacionType;
import es.stpa.plusvalias.domain.TipoTramite;
import es.stpa.plusvalias.domain.Tramite;
import es.stpa.plusvalias.domain.intercambio.ActoJuridico;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Bonificacion;
import es.stpa.plusvalias.domain.intercambio.Cabecera;
import es.stpa.plusvalias.domain.intercambio.DatosPersonales;
import es.stpa.plusvalias.domain.intercambio.DireccionExtranjera;
import es.stpa.plusvalias.domain.intercambio.DireccionInmueble;
import es.stpa.plusvalias.domain.intercambio.Duplicado;
import es.stpa.plusvalias.domain.intercambio.DatosEscritura;
import es.stpa.plusvalias.domain.intercambio.Exencion;
import es.stpa.plusvalias.domain.intercambio.Inmueble;
import es.stpa.plusvalias.domain.intercambio.Notario;
import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.domain.intercambio.Protocolo;
import es.stpa.plusvalias.domain.intercambio.TipoVia;
import es.stpa.plusvalias.domain.intercambio.Transmision;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.ConversionException;
import es.stpa.plusvalias.exceptions.PlusvaliaException;

/**
 * Conversiones de/hacia {@link PeticionOperacionType}
 * @author crubencvs
 *
 */
public class ConversorPeticionPresentacion {

	private MediadorBD bd;
	public ConversorPeticionPresentacion (MediadorBD bd){
		this.bd= bd;
	}
	
	public static class EstadoProcesoConversion{
		private PeticionOperacionType peticion;
		private boolean error;
		private String codigoError;
		private String mensajeError;
		
		public EstadoProcesoConversion(){
			this.peticion= new PeticionOperacionType();
			this.error=false;
			this.codigoError="";
			this.mensajeError="";
		}
		
		public PeticionOperacionType getPeticion() {
			return peticion;
		}
		public boolean isError() {
			return error;
		}
		public String getCodigoError() {
			return codigoError;
		}
		
		public String getMensajeError(){
			return mensajeError;
		}
		
	}
	/**
	 * Conversión de {@link es.stpa.plusvalias.CabeceraType} a {@link Cabecera}
	 * 
	 * @param c
	 * @return
	 */
	private Cabecera getCabecera(es.stpa.plusvalias.CabeceraType c) {
		Cabecera cab = new Cabecera();
		if (c != null) {
			cab.setIdcomunicacion(c.getIDCOMUNICACION());
			cab.setOperacion(c.getOPERACION());
			cab.setEmisor(c.getEMISOR());
			cab.setReceptor(c.getRECEPTOR());
			cab.setFecha(c.getFECHA());
			cab.setHora(c.getHORA());
			cab.setAplicacion(c.getAPLICACION());
		}
		return cab;
	}

	/**
	 * Conversión de {@link es.stpa.plusvalias.NotarioType} a {@link Notario}
	 * 
	 * @param n
	 * @return
	 */
	private Notario getNotario(es.stpa.plusvalias.NotarioType n) {
		if (n == null) {
			return null;
		}
		Notario not = new Notario();
		not.setCodigo(n.getCODIGO());
		not.setNombre(n.getNOMBRE());
		not.setApellido1(n.getAPELLIDO1());
		not.setApellido2(n.getAPELLIDO2());
		return not;
	}

	/**
	 * Conversión de {@link es.stpa.plusvalias.ProtocoloType} a
	 * {@link Protocolo}
	 * 
	 * @param p
	 * @return
	 */
	private Protocolo getProtocolo(es.stpa.plusvalias.ProtocoloType p) {
		if (p == null) {
			return null;
		}
		Protocolo pro = new Protocolo();
		pro.setNumero(p.getNUMERO());
		pro.setNumerobis(p.getNUMEROBIS());
		pro.setFechaautorizacion(p.getFECHAAUTORIZACION());
		return pro;
	}

	/**
	 * Conversión de {@link es.stpa.plusvalias.ActoJuridicoType} a
	 * {@link ActoJuridico}
	 * 
	 * @param a
	 * @return
	 */
	private ActoJuridico getActoJuridico(es.stpa.plusvalias.ActoJuridicoType a) {
		if (a == null) {
			return null;
		}
		ActoJuridico act = new ActoJuridico();
		act.setCodigo(a.getCODIGO());
		act.setDescripcion(a.getDESCRIPCION());
		return act;
	}

	/**
	 * Convierte de
	 * 
	 * @{es.stpa.plusvalias.SujetoType} a {@link Persona}
	 * @param sujeto
	 * @return
	 */
	private Persona getDatosSujeto(es.stpa.plusvalias.SujetoType sujeto) {
		if (sujeto == null) {
			return null;
		}
		Persona s = new Persona();
		s.setIdentificador(sujeto.getIDENTIFICADOR());

		DatosPersonales dpt = new DatosPersonales();
		if (sujeto.getDATOSPERSONALES() != null) {
			dpt
					.setTipodocumento(sujeto.getDATOSPERSONALES()
							.getTIPODOCUMENTO());
			dpt.setNumerodocumento(sujeto.getDATOSPERSONALES()
					.getNUMERODOCUMENTO());
			dpt.setNombre(sujeto.getDATOSPERSONALES().getNOMBRE());
			dpt.setApellido1RAZONSOCIAL(sujeto.getDATOSPERSONALES()
					.getAPELLIDO1RAZONSOCIAL());
			dpt.setApellido2(sujeto.getDATOSPERSONALES().getAPELLIDO2());
			s.setDatospersonales(dpt);
		}

		DireccionInmueble din = new DireccionInmueble();
		if (sujeto.getDOMICILIO() != null) {
			din.setIneprovincia(sujeto.getDOMICILIO().getINEPROVINCIA());
			din.setInemunicipio(sujeto.getDOMICILIO().getINEMUNICIPIO());
			if (sujeto.getDOMICILIO().getTIPOVIA() != null) {
				din.setTipovia(TipoVia.fromValue(sujeto.getDOMICILIO()
						.getTIPOVIA().value()));
			}
			din.setVia(sujeto.getDOMICILIO().getVIA());
			din.setNumerovia(sujeto.getDOMICILIO().getNUMEROVIA());
			din.setEscalera(sujeto.getDOMICILIO().getESCALERA());
			din.setPlanta(sujeto.getDOMICILIO().getPLANTA());
			din.setPuerta(sujeto.getDOMICILIO().getPUERTA());
			if (sujeto.getDOMICILIO().getDUPLICADO() != null) {
				din.setDuplicado(Duplicado.fromValue(sujeto.getDOMICILIO()
						.getDUPLICADO().value()));
			}
			din.setBloque(sujeto.getDOMICILIO().getBLOQUE());
			din.setAprkm(sujeto.getDOMICILIO().getAPRKM());
			din.setEntidadmenor(sujeto.getDOMICILIO().getENTIDADMENOR());
			din.setResto(sujeto.getDOMICILIO().getRESTO());
			din.setCodigopostal(sujeto.getDOMICILIO().getCODIGOPOSTAL());

			s.setDomicilio(din);
		}

		DireccionExtranjera dex = new DireccionExtranjera();
		if (sujeto.getDOMICILIOEXTRANJERO() != null) {
			dex.setPais(sujeto.getDOMICILIOEXTRANJERO().getPAIS());
			dex.setEstprv(sujeto.getDOMICILIOEXTRANJERO().getESTPRV());
			dex.setDescmunicipio(sujeto.getDOMICILIOEXTRANJERO()
					.getDESCMUNICIPIO());
			dex.setDireccionnoestructurada(sujeto.getDOMICILIOEXTRANJERO()
					.getDIRECCIONNOESTRUCTURADA());
			dex.setDistritopostal(sujeto.getDOMICILIOEXTRANJERO()
					.getDISTRITOPOSTAL());

			s.setDomicilioextranjero(dex);
		}
		return s;
	}

	/**
	 * Conversion de {@link PAGOLIQUIDACION.REQUEST.TRANSMITENTES} a
	 * {@link List} de {@link es.stpa.plusvalias.TransmitenteType}
	 * 
	 * @param transmitentes
	 * @return
	 */
	private List<Transmitente> getTransmitentes(
			PAGOLIQUIDACION.REQUEST.TRANSMITENTES transmitentes) {
		if (transmitentes == null) {
			return new ArrayList<Transmitente>();
		}
		List<Transmitente> l = new ArrayList<Transmitente>();
		for (es.stpa.plusvalias.TransmitenteType t : transmitentes
				.getTRANSMITENTE()) {
			Transmitente trans = new Transmitente();

			// Datos básicos

			trans.setDatosbasicos(getDatosSujeto(t.getDATOSBASICOS()));
			// Otros datos
			trans.setPorcentajetransmitido(t.getPORCENTAJETRANSMITIDO());
			trans.setClasederecho(t.getCLASEDERECHO());
			trans.setFechadefuncion(t.getFECHADEFUNCION());
			trans.setEdadusufructuario(t.getEDADUSUFRUCTUARIO());
			trans.setAnyosusufructo(t.getANYOSUSUFRUCTO());
			trans.setEdaduso(t.getEDADUSO());
			trans.setAnyosuso(t.getANYOSUSO());

			// Transmisiones anteriores
			trans.setTransmisionesanteriores(new ArrayList<Transmision>());
			for (es.stpa.plusvalias.TransmitenteType.TRANSMISIONESANTERIORES.TRANSMISION tant : t
					.getTRANSMISIONESANTERIORES().getTRANSMISION()) {
				Transmision ant = new Transmision();
				ant.setFecha(tant.getFECHA());
				ant.setPorcentaje(tant.getPORCENTAJE());
				// 14/02/2022. Valor de la transmisión anterior
				ant.setValor(tant.getVALOR());
				trans.getTransmisionesanteriores().add(ant);
			}

			// Bonificaciones

			if (t.getBONIFICACIONES() != null) {
				Bonificacion bon = new Bonificacion();
				bon.setPorcentaje(t.getBONIFICACIONES().getPORCENTAJE());
				bon.setConcepto(t.getBONIFICACIONES().getCONCEPTO());
				trans.setBonificacion(bon);
			}

			// Exenciones

			if (t.getEXENCIONES() != null) {
				Exencion ex = new Exencion();
				ex.setPorcentaje(t.getEXENCIONES().getPORCENTAJE());
				ex.setConcepto(t.getEXENCIONES().getCONCEPTO());
				trans.setExencion(ex);
			}

			// Representantes

			if (t.getREPRESENTANTES() != null) {
				for (Long idRepresentante : t.getREPRESENTANTES()
						.getIDREPRESENTANTE()) {
					// CRUBENCVS 16/11/2023 Se corrige el tratamiento de los representantes
					if (trans.getRepresentantes()==null){
						trans.setRepresentantes(new ArrayList<Long>());
					}
					trans.getRepresentantes().add(idRepresentante);
				}
			}
			// 14/02/2022. Valor de la transmisión actual
			trans.setValorTransmision(t.getVALORTRANSMISION());
			l.add(trans);
		}
		return l;
	}

	/**
	 * Conversión de {@link PAGOLIQUIDACION.REQUEST.ADQUIRENTES} a
	 * {@link List} de {@link Adquirente}
	 * 
	 * @param adquirentes
	 * @return
	 */
	private List<Adquirente> getAdquirentes(
			PAGOLIQUIDACION.REQUEST.ADQUIRENTES adquirentes) {
		if (adquirentes == null) {
			return new ArrayList<Adquirente>();
		}
		List<Adquirente> l = new ArrayList<Adquirente>();
		for (es.stpa.plusvalias.AdquirenteType t : adquirentes.getADQUIRENTE()) {
			Adquirente adq = new Adquirente();

			// Datos básicos
			adq.setDatosbasicos(getDatosSujeto(t.getDATOSBASICOS()));
			// Otros datos
			adq.setPorcentajeadquirido(t.getPORCENTAJEADQUIRIDO());
			adq.setClasederecho(t.getCLASEDERECHO());
			adq.setEdadusufructuario(t.getEDADUSUFRUCTUARIO());
			adq.setAnyosusufructo(t.getANYOSUSUFRUCTO());
			adq.setEdaduso(t.getEDADUSO());
			adq.setAnyosuso(t.getANYOSUSO());

			if (t.getPRORROGASOLICITADA() != null) {
				adq.setFechaProrrogaSolicitada(t.getPRORROGASOLICITADA()
						.getFECHA());
			}

			// Representantes

			if (t.getREPRESENTANTES() != null) {
				for (Long idRepresentante : t.getREPRESENTANTES()
						.getIDREPRESENTANTE()) {
					// CRUBENCVS 10/01/2024. Tratamiento de representantes
					if (adq.getRepresentantes()==null){
						adq.setRepresentantes(new ArrayList<Long>());
					}
					adq.getRepresentantes().add(idRepresentante);
				}
			}

			l.add(adq);
		}
		return l;
	}

	/**
	 * Convierte de un {@link PAGOLIQUIDACION.REQUEST.INMUEBLE} a
	 * {@link Inmueble}
	 * 
	 * @param inmueble
	 * @return
	 */
	private Inmueble getInmueble(PAGOLIQUIDACION.REQUEST.INMUEBLE inmueble) {
		if (inmueble == null) {
			return null;
		}
		Inmueble inm = new Inmueble();
		inm.setReferenciacatastral(inmueble.getREFERENCIACATASTRAL());
		if (inmueble.getDIRECCION() != null) {
			DireccionInmueble d = new DireccionInmueble();
			d.setIneprovincia(inmueble.getDIRECCION().getINEPROVINCIA());
			d.setInemunicipio(inmueble.getDIRECCION().getINEMUNICIPIO());
			inm.setDireccion(d);
		}
		return inm;
	}

	/**
	 * Convierte de
	 * 
	 * @{PAGOLIQUIDACION.REQUEST.REPRESENTANTES} a {@link List} de
	 *                                              {@link Persona}
	 * @param representantes
	 * @return
	 */
	private List<Persona> getRepresentantes(
			PAGOLIQUIDACION.REQUEST.REPRESENTANTES representantes) {
		if (representantes == null) {
			return new ArrayList<Persona>();
		}
		List<Persona> l = new ArrayList<Persona>();
		for (es.stpa.plusvalias.SujetoType s : representantes
				.getREPRESENTANTE()) {
			l.add(getDatosSujeto(s));
		}
		return l;
	}
	/**
	 * Convierte de  @{PAGOLIQUIDACION.REQUEST.ESCRITURA} a {@link DatosEscritura}
	 * @param escritura
	 * @return
	 */
	private DatosEscritura getDatosEscritura(PAGOLIQUIDACION.REQUEST.ESCRITURA escritura) {
		if (escritura == null) {
			return new DatosEscritura(); //Dado que la existencia de escritura y sus datos pueden no estar coordinados, evito acceso a Null
		}
		DatosEscritura e= new DatosEscritura();
		e.setDigest(escritura.getDIGEST());
		e.setMimetype(escritura.getMIMETYPE());
		e.setTamano(escritura.getTAMANO());
		e.setNombre(escritura.getNOMBRE());
		
		return e;
	}
	/**
	 * Informa datos de documento notarial
	 */
	private DocumentoNotarial getDatosNotariales(PeticionOperacionType peticion) throws ConversionException{
		DocumentoNotarial docNotarial= null;
		try {
			String codigoTitular=peticion.getNotariotitular().getCodigo();
			String codigoAutorizante= peticion.getNotarioautorizante().getCodigo();
			String fechaAutorizacion= peticion.getProtocolo().getFechaautorizacion();
			NotarioInterviniente notarioTitular=  NotarioInterviniente.getNotario(codigoTitular, fechaAutorizacion, bd);
			NotarioInterviniente notarioAutorizante;
			if (codigoTitular.equals(codigoAutorizante)){
				notarioAutorizante= notarioTitular;
			} else {
				notarioAutorizante=NotarioInterviniente.getNotario(codigoAutorizante, fechaAutorizacion, bd);
			}
			if (notarioTitular==null){
				throw new ConversionException( CodigosTerminacion.ERROR_NOTARIO_TITULAR);
			} 
			
			if (notarioAutorizante==null){
				throw new ConversionException(CodigosTerminacion.ERROR_NOTARIO_AUTORIZANTE);
			}
			docNotarial= new DocumentoNotarial(notarioTitular, notarioAutorizante, peticion.getProtocolo());
		} catch (PlusvaliaException e){
			throw new ConversionException( CodigosTerminacion.ERROR_GENERAL_CALCULO,e);
		}
		return docNotarial;
	}/**
	 * Informa datos del trámite. 
	 */
	private Tramite getTramite(PeticionOperacionType peticion) throws ConversionException{
		Tramite tramite=null;
		try {
			String actoJuridico= peticion.getActojuridico().getCodigo();
			if ("".equals(actoJuridico)){
				throw new ConversionException(CodigosTerminacion.ERROR_CORRESPONDENCIA_ACTO_JURIDICO);
			} else {
				tramite = Tramite.getTramite(actoJuridico, bd);
				if (tramite==null){
					throw new ConversionException(CodigosTerminacion.ERROR_CORRESPONDENCIA_ACTO_JURIDICO);
				} else {
					//En base al tipo, tenemos que sacar la fecha de trámite.
					//En transmisión y donación es la de autorización, en 
					//  sucesiones la de causante. Como suponemos que en la escritura
					//  se incluye una sola fecha, cogemos la de primer 
					//  causante.
					TipoTramite tt=tramite.getTipoTramite();
					if (TipoTramite.TRANSMISION.equals(tt) ||
					    TipoTramite.DONACION.equals(tt)){
						tramite.setFechaTramite(peticion.getProtocolo().getFechaautorizacion());
					} else if (TipoTramite.SUCESION.equals(tt)){
						tramite.setFechaTramite(peticion.getTransmitentes().get(0).getFechadefuncion());
					}
				}
			}
		} catch (PlusvaliaException e){
			throw new ConversionException(CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_GENERAL_PRESENTACION,e.getMessage()), e);
		}
		return tramite;
	}
	
	/**
	 * Informa datos de la finca
	 */
	private Finca getFinca(PeticionOperacionType peticion)throws ConversionException{
		Finca finca=null;
		Tramite tramite= peticion.getTramite();
		try {
			String ineMunicipio = peticion.getInmueble().getDireccion().getInemunicipio();
			String municipio= peticion.getInmueble().getDireccion().getIneprovincia() + 
							 ineMunicipio.substring(0, ineMunicipio.length()-1);
			String referenciaCatastral= peticion.getInmueble().getReferenciacatastral();
			String ejercicio="";
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			try {
				Date fechaTemp=sd.parse(tramite.getFechaTramite());
				sd = new SimpleDateFormat ("yyyy");
				ejercicio=sd.format(fechaTemp);
			} catch (ParseException p){
				throw new PlusvaliaException ("La fecha de devengo "+ tramite.getFechaTramite() + " no tiene el formato esperado", p);
			}
			finca= Finca.getFinca(referenciaCatastral, municipio, ejercicio, bd);
			if (finca==null){
				throw new ConversionException(CodigosTerminacion.ERROR_REFERENCIA_CATASTRAL);
			}
		} catch (PlusvaliaException e){
			throw new ConversionException(CodigosTerminacion.ERROR_REFERENCIA_CATASTRAL, e);
		}
		return finca;
	}

	/**
	 * Convierte de una cadena que representa un importe en euros
	 * con separador de miles "." y coma decimal,  a céntimos.
	 * En principio lo único que hacemos es eliminar los puntos,
	 * modificar la coma decimal a punto (si no hay, no importa)
	 * y tratar de convertir la cadena resultante a Double. Después,
	 * multiplico por 100. 
	 * @param euros
	 * @return
	 */
	private Long importeCentimos(String euros){
		if (euros==null){
			return null;
		}
		try{
			Double valor=Double.parseDouble(euros.replace(".","").replace(',', '.'));
			return (long)(valor.doubleValue()*100);
		} catch (NumberFormatException nfe){ return null;}
	}
	/**
	 * Recupera las órdenes de pago de la petición.
	 * Esta función se deberá llamar cuando ya existan los transmitentes y 
	 *	adquirentes cargados, sino, fallará.
	 * @param peticion
	 * @return
	 * @throws ConversionException
	 */
	private OrdenesPago getOrdenesPago(PeticionOperacionType peticion, PAGOLIQUIDACION.REQUEST.PAGO datosPago) throws ConversionException{
		OrdenesPago ordenesPago= new OrdenesPago();
		//La orden de pago pueden haberla emitido contra el adquirente o el transmitente
		//Tenemos que recorrer ambos para averiguar a quién pertenece cada identificador.
		
		if (peticion.getAdquirentes()==null || peticion.getAdquirentes().size()==0){
			throw new ConversionException(CodigosTerminacion.ERROR_GENERAL_PRESENTACION);
		}
		
		if (peticion.getTransmitentes()==null || peticion.getTransmitentes().size()==0){
			throw new ConversionException(CodigosTerminacion.ERROR_GENERAL_PRESENTACION);
		}
		Map<Long, Persona> sujetosPago= new HashMap<Long,Persona>();
		for (Adquirente a : peticion.getAdquirentes()) {
			sujetosPago.put(Long.valueOf(a.getDatosbasicos().getIdentificador()), a.getDatosbasicos());
		}
		
		for (Transmitente t: peticion.getTransmitentes()){
			sujetosPago.put(Long.valueOf(t.getDatosbasicos().getIdentificador()), t.getDatosbasicos());
		}
		
		//Ahora, intento montar las órdenes de pago.
		ORDENPAGOType.PARAMETROSPAGO param= datosPago.getORDENPAGO().getPARAMETROSPAGO();
		if (param.getIMPORTES()==null){
			//No tiene sentido, no es válido según esquema.
			throw new ConversionException(CodigosTerminacion.ERROR_NO_IMPORTE_PAGO);
		}
		String ccc;
		String nifTitular;
		if (param.getDATOSBANCARIOS()!=null){
			ccc= param.getDATOSBANCARIOS().getENTIDAD()+
				 param.getDATOSBANCARIOS().getOFICINA()+
				 param.getDATOSBANCARIOS().getDC()+
				 param.getDATOSBANCARIOS().getCUENTA();
			
			nifTitular= param.getDATOSBANCARIOS().getNIFTITULAR();
			
		} else {
			ccc= "";
			nifTitular ="";
		}
		
		for (ORDENPAGOType.PARAMETROSPAGO.IMPORTES i: param.getIMPORTES() ){
			if (!sujetosPago.containsKey(i.getIDSUJETO())){
				throw new ConversionException(CodigosTerminacion.ERROR_ID_SUJETO_INEXISTENTE, CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_ID_SUJETO_INEXISTENTE, String.valueOf(i.getIDSUJETO())));
			}
			Long importe= importeCentimos(i.getIMPORTE());
			if (importe!=null && importe!=0L && param.getDATOSBANCARIOS()==null){
				throw new ConversionException(CodigosTerminacion.ERROR_NO_DATOS_PAGO, CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_NO_DATOS_PAGO));
			}
			OrdenPago op= new OrdenPago(ccc,nifTitular,sujetosPago.get(i.getIDSUJETO()), i.getIMPORTE());
			ordenesPago.add(op);
		}
		return ordenesPago; 
	}
	/**
	 * Conversión de {@link PAGOLIQUIDACION.REQUEST} a
	 * {@link PeticionOperacionType}
	 * Lo único que asegura que se devuelva en el objeto {@link PeticionOperacionType} es
	 * la cabecera, haya error o no. Si no hay error, devolverá también otros datos,
	 * aunque en ese caso no serán necesario ya que no podremos fiarnos de ellos.
	 * @param request
	 * @return
	 */
	public EstadoProcesoConversion from(PAGOLIQUIDACION.REQUEST request) {
		EstadoProcesoConversion e= new EstadoProcesoConversion();
		PeticionOperacionType p= e.getPeticion();
		p.setCabecera(getCabecera(request.getCABECERA()));
		p.setNotariotitular(getNotario(request.getNOTARIOTITULAR()));
		p.setNotarioautorizante(getNotario(request.getNOTARIOAUTORIZANTE()));
		p.setProtocolo(getProtocolo(request.getPROTOCOLO()));
		p.setActojuridico(getActoJuridico(request.getACTOJURIDICO()));
		p.setTransmitentes(getTransmitentes(request.getTRANSMITENTES()));
		p.setAdquirentes(getAdquirentes(request.getADQUIRENTES()));
		p.setInmueble(getInmueble(request.getINMUEBLE()));
		p.setRepresentantes(getRepresentantes(request.getREPRESENTANTES()));
		p.setDatosEscritura(getDatosEscritura(request.getESCRITURA()));
		//Objetos de uso interno, relacionados con la petición pero ya procesados
		try {
			p.setDocNotarial(getDatosNotariales(p));
			p.setTramite(getTramite(p));
			p.setFinca(getFinca(p));
			p.setOrdenesPago(getOrdenesPago(p, request.getPAGO()));
			e.error= false;
		} catch (ConversionException ce){
			e.error=true;
			e.codigoError= ce.getCodigoError();
			if (ce.getMensajeError()!=null){
				e.mensajeError= ce.getMensajeError();
			}
		}
		return e;
	}
}
