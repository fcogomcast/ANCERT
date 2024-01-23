package es.stpa.plusvalias.presentacion;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.domain.CalculoAdquisicionesAnteriores;
import es.stpa.plusvalias.domain.Finca;
import es.stpa.plusvalias.domain.NotarioInterviniente;
import es.stpa.plusvalias.domain.TipoTramite;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.domain.intercambio.Transmitente;
import es.stpa.plusvalias.exceptions.DatosException;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa.Declaracion;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa.Declaracion.Identificacion;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa.Declaracion.ListaBienesUrbanos;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa.Declaracion.ListaDatosVenta;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa.Declaracion.ListaIntervinientes;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa.Declaracion.ListaBienesUrbanos.BienUrbano;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa.Declaracion.ListaDatosVenta.DatosVenta;
import es.stpa.plusvalias.presentacion.types.remesa.Remesa.Declaracion.ListaIntervinientes.Interviniente;

/**
 * Maneja el estado de inserción de datos en tabla M029
 * @author crubencvs
 *
 */
public class EstadoInsertarModelo implements Estado{
	private PresentacionLiquidacion p;
	public EstadoInsertarModelo(PresentacionLiquidacion p){
		this.p=p;
	}
	/**
	 * Formatea una fecha a dd/MM/yyyy
	 * @param fecha
	 * @return
	 */
	 
	private String formateaFecha(Date fecha){
		if (fecha==null){
			return "";
		}
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		return sd.format(fecha);
	}
	
	/**
	 * Si la cadena es nula, devuelve una cadena vacía.
	 * @param valor
	 * @return
	 */
	private String null2empty(String valor){
		if (valor==null){
			return "";
		}
		return valor;
	}
	/**
	 * Devuelve la fecha actual en formato DD/MM/YYYY
	 * @return
	 */
	private String today(){
		return formateaFecha(new Date());
		
	}
	

	/**
	 * Convierte de la cadena que representa céntimos al formato euro
	 * con Locale español (coma decimal, punto separador de millares)
	 * @param centimos
	 * @return
	 * @throws NumberFormatException. 
	 */
	//No necesito declarar que lanza numberFormatException por ser RuntimeException
	//pero quiero que sea explícito
	private String centimos2euro(String centimos) throws NumberFormatException{
		if (centimos==null){
			return "";
		}
		return String.format(new Locale("es","ES"),"%,.2f",Float.parseFloat(centimos)/100);
	}
	
	/**
	 * Convierte un double que representa euros a una cadena que representa
	 * esa cantidad pero en formato de moneda española (coma decimal, puntos separadores 
	 * de millares)
	 * @param euros double
	 * @return Cadena 
	 * @throws NumberFormatException
	 */
	private String toEuroCadena(double euros) throws NumberFormatException{
		return String.format(new Locale("es","ES"),"%,.2f",euros);
	}
	//Construímos el XML 
	private String construirXML(){
		Remesa r = new Remesa();
		
		//Para las conversiones a mayúsculas
		Locale spanish=new Locale("es");
		r.setCodigo("33200"); //TODO: ¿Este qué es?
		r.setTotalDecl("1");
		r.setVersionXsd("1.0");
		r.setFecEmision(today());
		Declaracion decl= new Declaracion();
		decl.setCodigo(p.getNumeroJustificante().getNumeroJustificante());
		decl.setModelo("029A1"); //¿Parametrizar?
		decl.setVersionXsdModelo("1.0");
		decl.setModoPlusvalia("C");
		decl.setFecPres(today());
		String fechaDevengo= p.getLiquidacionContribuyente().getTramite().getFechaTramite();
		decl.setFecDev(fechaDevengo);
		decl.setFechaDevengoConcatenada(fechaDevengo);
		decl.setFechaDevengoTransmision(fechaDevengo);
		decl.setValorTerreno(centimos2euro(p.getLiquidacionContribuyente().getValorTerreno()));
		decl.setAIngresar(toEuroCadena(p.getLiquidacionContribuyente().getImporte().getImportetotal()));
		decl.setPercParticipacion("100");
		decl.setTipoAuto("C"); //Conjunta
		String codMunicipio= p.getLiquidacionContribuyente().getFinca().getCodMunicipio();
		String datoEspecifico= codMunicipio.substring(codMunicipio.length()-2) + fechaDevengo.substring(fechaDevengo.length()-2);
		decl.setDatoEspecifico(datoEspecifico);
		decl.setPorcReferenciaCatastralSuelo("100");
		
		//La sujeción viene establecida por la cuota resultante y el total
		//a ingresar. Si la cuota es distinto de cero, y el total a ingresar es cero,
		//es No sujeto. 
		//Si la cuota es cero, es exento.
		//En otro caso, es sujeto.
		String sujecion;
		Double cuotaResultante= Double.parseDouble(p.getLiquidacionContribuyente().getCuotaResultante())/100;
		if (cuotaResultante.doubleValue()>0 && p.getLiquidacionContribuyente().getImporte().getImportetotal()==0){
			sujecion="NS";
		} else if (cuotaResultante.doubleValue()==0){
			sujecion="E";
		} else {
			sujecion="S";
		}
		decl.setSujecion(sujecion);
		
		decl.setValorCompra(centimos2euro(p.getLiquidacionContribuyente().getValorCompra()));
		decl.setValorVenta(centimos2euro(p.getLiquidacionContribuyente().getValorVenta()));
		decl.setValorCatastral(centimos2euro(p.getLiquidacionContribuyente().getFinca().getValorCatastral()));
		decl.setValorCatastralActu(centimos2euro(p.getLiquidacionContribuyente().getValorCatastralActual()));
		decl.setValorSuelo(centimos2euro(p.getLiquidacionContribuyente().getFinca().getValorCatastral()));
		decl.setValorSueloActu(centimos2euro(p.getLiquidacionContribuyente().getValorSueloActual()));
		
		decl.setBaseImpoDirecta(centimos2euro(p.getLiquidacionContribuyente().getBaseImponibleDirecta()));
		decl.setCuotaResultante(centimos2euro(p.getLiquidacionContribuyente().getCuotaResultante()));
		decl.setBiElegida(p.getLiquidacionContribuyente().getTipoCalculoElegido());
		decl.setNumeroFijo(p.getLiquidacionContribuyente().getFinca().getNumeroFijo());
		//??? No sé para qué sirve este campo
		decl.setCodvia(p.getLiquidacionContribuyente().getFinca().getCodigoVia());
		
		decl.setCodTipoPlusvalia(p.getLiquidacionContribuyente().getConceptoPlusvaliaCalculada().getCodigoPltr());
		decl.setCodCausaPlusvalia(p.getLiquidacionContribuyente().getConceptoPlusvaliaCalculada().getCodigoPlca());
		decl.setCodObjetoOperacionPlusvalia(p.getLiquidacionContribuyente().getConceptoPlusvaliaCalculada().getCodigoPlop());
		decl.setNomTipoPlusvalia(p.getLiquidacionContribuyente().getConceptoPlusvaliaCalculada().getNombrePltr());
		decl.setNomCausaPlusvalia(p.getLiquidacionContribuyente().getConceptoPlusvaliaCalculada().getNombrePlca());
		decl.setNomObjetoOperacionPlusvalia(p.getLiquidacionContribuyente().getConceptoPlusvaliaCalculada().getNombrePlop());
		
		
		decl.setAnyorev(p.getLiquidacionContribuyente().getFinca().getAnioRevisionCatastral());
		decl.setAnyovalcat(p.getLiquidacionContribuyente().getFinca().getAnioValorCatastral());
		decl.setFecFinPeriodo(p.getLiquidacionContribuyente().getFechaFinVoluntaria());
		decl.setPorcRecargo(p.getLiquidacionContribuyente().getPorcentajeRecargo());
		decl.setRET(p.getLiquidacionContribuyente().getRetrasoPresentacion());
		decl.setCFL(toEuroCadena(p.getLiquidacionContribuyente().getImporte().getImportetotal()));
		decl.setRecargo(toEuroCadena(p.getLiquidacionContribuyente().getImporte().getImporterecargo()));
		decl.setAplicaRecargoDemora(p.getLiquidacionContribuyente().getImporte().informarecargo()?"S":"N");
		
		//decl.setAplicaRecargoDemora(p.getLiquidacionContribuyente().getImporte().get)
		//Datos de identificación de acto
		Identificacion identificacion= new Identificacion();
		identificacion.setFechaDevengo(fechaDevengo); 
		identificacion.setFechaDocumento(p.getPeticion().getProtocolo().getFechaautorizacion());
		identificacion.setCodNotario(p.getPeticion().getNotarioautorizante().getCodigo());
		identificacion.setNotario(p.getPeticion().getNotarioautorizante().getNombre());
		identificacion.setApellidosNotario((null2empty(p.getPeticion().getNotarioautorizante().getApellido1()) + " "+
				                           null2empty(p.getPeticion().getNotarioautorizante().getApellido2())).toUpperCase(spanish));
		identificacion.setNumProtocolo(String.valueOf(p.getPeticion().getProtocolo().getNumero()));
		identificacion.setNumProtocoloBis(String.valueOf(p.getPeticion().getProtocolo().getNumerobis()));
		//TODO: Queda la notaría, provincia y municipio
		identificacion.setDocAdministrativo("N");
		identificacion.setDocJudicial("N");
		identificacion.setDocMercantil("N");
		identificacion.setDocPrivado("N");
		identificacion.setDocNotarial("S");
		decl.setIdentificacion(identificacion);
		//Intervinientes
		Interviniente sp= new Interviniente();
		Interviniente adq= new Interviniente();
		Interviniente pres= new Interviniente();
		//Sujeto pasivo y adquirente son los nombres de los nodos del xml resultante,
		//no el papel que juegan en la autoliquidación. En una transmisión,
		//sujeto pasivo es el transmitente, mientras que en una donación es el adquirente.
		Persona sujetoPasivo = p.getLiquidacionContribuyente().getSujetoPasivo().getDatosbasicos();
		Persona adquirente;
		if (p.getLiquidacionContribuyente().getSujetoPasivo() instanceof Transmitente){
			//Sujeto = Transmitente, "Adquirente" (no sujeto) = adquirente
			adquirente = p.getLiquidacionContribuyente().getAdquirente().getDatosbasicos();
			sp.setGrdoPartic(((Transmitente)p.getLiquidacionContribuyente().getSujetoPasivo()).getPorcentajetransmitido());
			adq.setGrdoPartic(p.getLiquidacionContribuyente().getAdquirente().getPorcentajeadquirido());
		} else {
			//Sujeto = Adquirente, "Adquirente" (no sujeto) = transmitente
			adquirente= p.getLiquidacionContribuyente().getTransmitente().getDatosbasicos();
			sp.setGrdoPartic(((Adquirente)p.getLiquidacionContribuyente().getSujetoPasivo()).getPorcentajeadquirido());
			adq.setGrdoPartic(p.getLiquidacionContribuyente().getTransmitente().getPorcentajetransmitido());
		}
		
		ListaIntervinientes linv = new ListaIntervinientes();
		sp.setTipo("Sujeto Pasivo");
		//Los datos de las personas los  recuperamos de lo que nos manda, porque
		//puede que no esté ni en base de datos...
		//FIXME: Si el domicilio es extranjero, y por tanto no residente,
		//pues sólo podemos coger, en su caso, los datos de un representante. 
		//Si no tiene, no sé que coger 
		sp.setNif(sujetoPasivo.getDatospersonales().getNumerodocumento()); 
		sp.setNombreRazonsocial((null2empty(sujetoPasivo.getDatospersonales().getNombre())+" "+
								null2empty(sujetoPasivo.getDatospersonales().getApellido1RAZONSOCIAL())+ " "+ 
								null2empty(sujetoPasivo.getDatospersonales().getApellido2())).toUpperCase(spanish));
		if (sujetoPasivo.getDomicilio()!=null){
			sp.setProvincia(sujetoPasivo.getDomicilio().getIneprovincia());
			sp.setMunicipio(sujetoPasivo.getDomicilio().getIneprovincia()+sujetoPasivo.getDomicilio().getInemunicipio().substring(0, 4));
			sp.setSiglas(sujetoPasivo.getDomicilio().getTipovia().value());
			sp.setNombreVia(sujetoPasivo.getDomicilio().getVia().toUpperCase(spanish));
			sp.setNumero(sujetoPasivo.getDomicilio().getNumerovia());
			sp.setEscalera(sujetoPasivo.getDomicilio().getEscalera());
			sp.setPiso(sujetoPasivo.getDomicilio().getPlanta());
			sp.setPuerta(sujetoPasivo.getDomicilio().getPuerta());
			sp.setCp(sujetoPasivo.getDomicilio().getCodigopostal());
		} 
		
		if (sujetoPasivo.getDomicilio()==null &&
				sujetoPasivo.getDomicilioextranjero()!=null){
			decl.setResidente("N");
		} else {
			decl.setResidente("S");
		}
		
		adq.setTipo("Adquirente");
		adq.setNif(adquirente.getDatospersonales().getNumerodocumento()); 
		adq.setNombreRazonsocial((null2empty(adquirente.getDatospersonales().getNombre())+" "+
								 null2empty(adquirente.getDatospersonales().getApellido1RAZONSOCIAL())+ " "+ 
								 null2empty(adquirente.getDatospersonales().getApellido2())).toUpperCase(spanish));
		if (adquirente.getDomicilio()!=null){
			adq.setProvincia(adquirente.getDomicilio().getIneprovincia());
			adq.setMunicipio(adquirente.getDomicilio().getIneprovincia()+adquirente.getDomicilio().getInemunicipio().substring(0, 4));
			adq.setSiglas(adquirente.getDomicilio().getTipovia().value());
			adq.setNombreVia(adquirente.getDomicilio().getVia().toUpperCase(spanish));
			adq.setNumero(adquirente.getDomicilio().getNumerovia());
			adq.setEscalera(adquirente.getDomicilio().getEscalera());
			adq.setPiso(adquirente.getDomicilio().getPlanta());
			adq.setPuerta(adquirente.getDomicilio().getPuerta());
			adq.setCp(adquirente.getDomicilio().getCodigopostal());
		}

		//Presentador. Siempre el notario
		pres.setTipo("Presentador");
		pres.setRelacion("GE"); //Siempre "Gestor"
		NotarioInterviniente not= p.getPeticion().getDocNotarial().getNotarioAutorizante();
		pres.setNif(not.getNif());
		pres.setNombreRazonsocial(not.getNombre());
		pres.setProvincia(not.getCodigoProvinciaNotaria());
		pres.setMunicipio(not.getCodigoMunicipioNotaria());
		pres.setProvinciaDesc(not.getProvinciaNotaria());
		pres.setMunicipioDesc(not.getMunicipioNotaria());
		pres.setNombreVia(null2empty(not.getCalleNotaria()).toUpperCase(spanish));
		pres.setCp(not.getCodigoPostalNotaria());
		
		linv.getInterviniente().add(sp);
		linv.getInterviniente().add(adq);
		linv.getInterviniente().add(pres);

		decl.setListaIntervinientes(linv);
		
		//Bien urbano
		ListaBienesUrbanos lbu= new ListaBienesUrbanos();
		BienUrbano bu = new BienUrbano();
		
		//Estos dos, transmisión  y carácter no son obligatorios.
		bu.setTransmision("100,00");
		bu.setCaracter("P");
		Finca finca= p.getLiquidacionContribuyente().getFinca();
		bu.setVia(finca.getSiglaVia());
		bu.setViaDesc(finca.getSiglaVia());
		bu.setNombreVia(finca.getNombreVia().toUpperCase(spanish));
		bu.setNumero(finca.getNumeroVia());
		bu.setEscalera(finca.getEscalera());
		bu.setPiso(finca.getPlanta());
		bu.setPuerta(finca.getPuerta());
		bu.setProvincia(finca.getCodProvincia());
		bu.setProvinciaDesc(finca.getProvincia());
		bu.setMunicipio(finca.getCodMunicipio());
		bu.setMunicipioDesc(finca.getMunicipio());
		bu.setReferenciaCatastral(finca.getReferenciaCatastral());
		//En principio nada más es necesario.
		lbu.getBienUrbano().add(bu);
		decl.setListaBienesUrbanos(lbu);
		
		//Adquisiciones anteriores.
		int posicion=1;
		ListaDatosVenta ldv= new ListaDatosVenta();
		
		double baseImponible=0;
		double baseLiquidable=0;
		CalculoAdquisicionesAnteriores cadq= p.getLiquidacionContribuyente().getCalculoAdquisicionesAnteriores();
		if (cadq!=null && cadq.getAdquisiciones()!=null){
			for (int i=0;i< cadq.getAdquisiciones().getFechasAdquisicionAnteriores().size();i++){
				//En teoría sólo se admitirán hasta 4, pero por el momento lo dejo abierto,
				//por si en el futuro se admitiesen otras
				DatosVenta dv = new DatosVenta();
				dv.setId(String.valueOf(i+1));
				dv.setPosicion(String.valueOf(posicion++));
				//Damos por hecho que todos los arrays tienen el mismo número de elementos.
				dv.setFechaAdquisicion(cadq.getAdquisiciones().getFechasAdquisicionAnteriores().get(i));
				dv.setAnyos(cadq.getAniosTranscurridos().get(i));
				dv.setPorcAdquisicion(cadq.getAdquisiciones().getPorcentajesAnteriores().get(i));
				dv.setPorcTenencia(cadq.getCoeficienteAplicableAnual());
				dv.setPorcXTenencia(cadq.getCoeficientesAplicables().get(i));
				dv.setTipoImpositivo(cadq.getTiposImpositivos().get(i));
				dv.setBaseImponible(centimos2euro(cadq.getBasesImponiblesObjetivas().get(i)));
				dv.setCuotaLiquidacion(centimos2euro(cadq.getCuotasLiquidacion().get(i)));
				
				baseImponible+= Double.parseDouble(cadq.getBasesImponiblesObjetivas().get(i))/100;
				baseLiquidable+= Double.parseDouble(cadq.getCuotasLiquidacion().get(i))/100;
				ldv.getDatosVenta().add(dv);
			}
			decl.setListaDatosVenta(ldv);
			decl.setBaseImponible(toEuroCadena(baseImponible));
			decl.setBaseLiquidable(toEuroCadena(baseLiquidable));
		}
		//Requisitos de la bonificación/exención
		//Las exenciones y bonificaciones están a nivel de Transmitente
		//Como a la hora de pedirnos las exenciones o bonificaciones
		//no indican el año, no sabemos si va a encontrar, por lo que
		//no fallamos en ningún caso
		Transmitente t= p.getLiquidacionContribuyente().getTransmitente();
		String porcentaje=null;
		String concepto=null;
		boolean bonificado=false;
		if (t.getExencion()!=null){
			porcentaje=t.getExencion().getPorcentaje();
			concepto= t.getExencion().getConcepto();
			bonificado=true;
		} else if (t.getBonificacion()!=null){
			porcentaje =  t.getBonificacion().getPorcentaje();
			concepto= t.getBonificacion().getConcepto();
			bonificado=true;
		}
		if (bonificado){
			decl.setSupBonificado("S");
			decl.setPorcBonificacionCuota(porcentaje);
		} else {
			decl.setSupBonificado("N");
		}
		if (porcentaje!=null && concepto!=null){
			String tipoTramite;
			if (TipoTramite.TRANSMISION.equals(p.getLiquidacionContribuyente().getTramite().getTipoTramite())){
				tipoTramite="TR";
			} else {
				tipoTramite="SU"; //Sirve también para donación
			}
			try {
				String requisito= p.getBd().recuperaRequisito(fechaDevengo, 
											finca.getCodMunicipio(), 
										    porcentaje, 
										    concepto, 
										    tipoTramite);
				decl.setCodRequisito(requisito);
			} catch (DatosException de){
				//Nada, ignoramos
			}
		}
		r.setDeclaracion(decl);
		try {
			JAXBContext jct= JAXBContext.newInstance(r.getClass());
			Marshaller marshaller= jct.createMarshaller();
			StringWriter sw= new StringWriter();
			marshaller.marshal(r, sw);
			return sw.toString();
		} catch (Exception e) {
			return null;
		}
	}
	

	@Override
	public void operar() {
		p.getLogger().debug("Tratamos el estado \"Insertar Modelo\" para integrar los datos en BD:");
		String remesa= construirXML();
		try {
			p.getBd().integraModelo(p.getNumeroJustificante().getNumeroJustificante(), remesa);
			p.setEstadoActual(new EstadoPresentacion(p));
			p.getBd().actualizaEstado(p.getNumeroJustificante().getNumeroJustificante(), ValorEstado.INTEGRADO);
		} catch (DatosException de){
			p.getLogger().error("Error en el estado \"Insertar Modelo\":" + de.getMessage());
			p.setEstadoActual(new EstadoError(p,CodigosTerminacion.ERROR_INTEGRACION_MODELO, CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_INTEGRACION_MODELO)));
		}
		catch (NumberFormatException nfe){
			p.getLogger().error("Error en el estado \"Insertar Modelo\":" + nfe.getMessage());
			p.setEstadoActual(new EstadoError(p,CodigosTerminacion.ERROR_INTEGRACION_MODELO, CodigosTerminacion.getMessage(CodigosTerminacion.ERROR_INTEGRACION_MODELO)));
		}
	}
}

