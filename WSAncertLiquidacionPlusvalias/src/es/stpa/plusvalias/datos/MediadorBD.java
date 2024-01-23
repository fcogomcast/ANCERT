package es.stpa.plusvalias.datos;



import es.stpa.plusvalias.exceptions.DatosException;
import es.stpa.plusvalias.preferencias.Preferencias;
import es.stpa.plusvalias.presentacion.ValorEstado;
import es.stpa.plusvalias.soap.SoapClientHandler;
import es.tributasenasturias.services.lanzador.client.LanzadorException;
import es.tributasenasturias.services.lanzador.client.LanzadorFactory;
import es.tributasenasturias.services.lanzador.client.ParamType;
import es.tributasenasturias.services.lanzador.client.ProcedimientoAlmacenado;
import es.tributasenasturias.services.lanzador.client.TLanzador;
import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;

/**
 * Accesos a funciones de base de datos
 * @author crubencvs
 *
 */
public class MediadorBD {

	private Preferencias pref;
	private String idLlamada;
	/** 
	 * Estructura para los datos de trámite
	 * @author crubencvs
	 *
	 */
	public static class DatosTramite{
		private String tipoTramite;
		private String tipoTransmision;
		private String causaTransmision;
		private String objetoTransmision;

		public String getTipoTramite() {
			return tipoTramite;
		}

		public void setTipoTramite(String tipoTramite) {
			this.tipoTramite = tipoTramite;
		}

		public String getTipoTransmision() {
			return tipoTransmision;
		}

		public void setTipoTransmision(String tipoTransmision) {
			this.tipoTransmision = tipoTransmision;
		}

		public String getCausaTransmision() {
			return causaTransmision;
		}

		public void setCausaTransmision(String causaTransmision) {
			this.causaTransmision = causaTransmision;
		}

		public String getObjetoTransmision() {
			return objetoTransmision;
		}

		public void setObjetoTransmision(String objetoTransmision) {
			this.objetoTransmision = objetoTransmision;
		}

		
	}
	/**
	 * 
	 * @author crubencvs
	 *
	 */
	public static class DatosCalculoLiquidacion{
		private String totalIngresar;
		private String recargo;
		private String interesesDemora;
		private boolean correcto=true;
		private String codigoTerminacion;
		// Estos realmente deberán ser comunes entre 
		// todos los cálculos de una misma presentación,
		// pero como para recuperarlos dependen de 
		// datos del transmitente, se traen
		// junto con el cálculo de cada uno de ellos.
		private String codigoPltr;
		private String nombrePltr;
		private String codigoPlca;
		private String nombrePlca;
		private String codigoPlop;
		private String nombrePlop;
		private String fechaFinVoluntaria;
		private String valorSueloActual;
		private String baseImponibleDirecta;
		private String valorCompra;
		private String valorVenta;
		private String tipoCalculoElegido;
		private String retrasoPresentacion;
		private String porcentajeRecargo;
		//Datos de adquisiciones anteriores
		//Los campos irán en el mismo orden de las fechas y porcentajes,
		//por ejemplo, si hay una fecha de adquisición, sólo estará informado
		//el aniosTranscurridos1m el coeficienteAplicable1 y la baseImponibleObjetiva1
		private String[] aniosTranscurridos;
		private String[] coeficientesAplicables;
		private String[] basesImponiblesObjetivas;
		private String[] tiposImpositivos;
		private String[] cuotasLiquidacion;
		private String cuotaResultante;
		private String coeficienteAplicableAnual;
		private String valorTerreno;
		private String valorCatastralActual;
		
		
		
		public String getTotalIngresar() {
			return totalIngresar;
		}
		public void setTotalIngresar(String totalIngresar) {
			this.totalIngresar = totalIngresar;
		}
		public String getRecargo() {
			return recargo;
		}
		public void setRecargo(String recargo) {
			this.recargo = recargo;
		}
		public String getInteresesDemora() {
			return interesesDemora;
		}
		public void setInteresesDemora(String interesesDemora) {
			this.interesesDemora = interesesDemora;
		}
		public boolean isCorrecto() {
			return correcto;
		}
		public void setCorrecto(boolean correcto) {
			this.correcto = correcto;
		}
		public String getCodigoTerminacion() {
			return codigoTerminacion;
		}
		public void setCodigoTerminacion(String codigoTerminacion) {
			this.codigoTerminacion = codigoTerminacion;
		}
		public String getCodigoPltr() {
			return codigoPltr;
		}
		public void setCodigoPltr(String codigoPltr) {
			this.codigoPltr = codigoPltr;
		}
		public String getNombrePltr() {
			return nombrePltr;
		}
		public void setNombrePltr(String nombrePltr) {
			this.nombrePltr = nombrePltr;
		}
		public String getCodigoPlca() {
			return codigoPlca;
		}
		public void setCodigoPlca(String codigoPlca) {
			this.codigoPlca = codigoPlca;
		}
		public String getNombrePlca() {
			return nombrePlca;
		}
		public void setNombrePlca(String nombrePlca) {
			this.nombrePlca = nombrePlca;
		}
		public String getCodigoPlop() {
			return codigoPlop;
		}
		public void setCodigoPlop(String codigoPlop) {
			this.codigoPlop = codigoPlop;
		}
		public String getNombrePlop() {
			return nombrePlop;
		}
		public void setNombrePlop(String nombrePlop) {
			this.nombrePlop = nombrePlop;
		}
		public String getFechaFinVoluntaria() {
			return fechaFinVoluntaria;
		}
		public void setFechaFinVoluntaria(String fechaFinVoluntaria) {
			this.fechaFinVoluntaria = fechaFinVoluntaria;
		}
		public String getValorSueloActual() {
			return valorSueloActual;
		}
		public void setValorSueloActual(String valorSueloActual) {
			this.valorSueloActual = valorSueloActual;
		}
		public String getBaseImponibleDirecta() {
			return baseImponibleDirecta;
		}
		public void setBaseImponibleDirecta(String baseImponibleDirecta) {
			this.baseImponibleDirecta = baseImponibleDirecta;
		}
		public String getValorCompra() {
			return valorCompra;
		}
		public void setValorCompra(String valorCompra) {
			this.valorCompra = valorCompra;
		}
		public String getValorVenta() {
			return valorVenta;
		}
		public void setValorVenta(String valorVenta) {
			this.valorVenta = valorVenta;
		}
		public String getTipoCalculoElegido() {
			return tipoCalculoElegido;
		}
		public void setTipoCalculoElegido(String tipoCalculoElegido) {
			this.tipoCalculoElegido = tipoCalculoElegido;
		}
		public String getRetrasoPresentacion() {
			return retrasoPresentacion;
		}
		public void setRetrasoPresentacion(String retrasoPresentacion) {
			this.retrasoPresentacion = retrasoPresentacion;
		}
		public String getPorcentajeRecargo() {
			return porcentajeRecargo;
		}
		public void setPorcentajeRecargo(String porcentajeRecargo) {
			this.porcentajeRecargo = porcentajeRecargo;
		}
		public String[] getAniosTranscurridos() {
			return aniosTranscurridos;
		}
		public String[] getCoeficientesAplicables() {
			return coeficientesAplicables;
		}
		public String[] getBasesImponiblesObjetivas() {
			return basesImponiblesObjetivas;
		}
		public String[] getTiposImpositivos() {
			return tiposImpositivos;
		}
		public String[] getCuotasLiquidacion() {
			return cuotasLiquidacion;
		}
		public String getCoeficienteAplicableAnual() {
			return coeficienteAplicableAnual;
		}
		public String getValorTerreno() {
			return valorTerreno;
		}
		public String getCuotaResultante() {
			return cuotaResultante;
		}
		public String getValorCatastralActual() {
			return valorCatastralActual;
		}
	}
	/**
	 * Para devolver los datos de un notario
	 * @author crubencvs
	 *
	 */
	public static class DatosNotario{
		private boolean existe;
		private String codigo;
		private String nif;
		private String nombre;
		private String apellidos;
		//Esto no pertenece aquí, pero como tenemos un ámbito tan reducido,
		//no creo una clase para la plaza
		private String codNotaria;
		private String plaza;
		private String calleNotaria;
		private String codProvinciaNotaria;
		private String provinciaNotaria;
		private String codMunicipioNotaria;
		private String municipioNotaria;
		private String codigoPostalNotaria;
		public String getCodigo() {
			return codigo;
		}
		public String getNif() {
			return nif;
		}
		public String getNombre() {
			return nombre;
		}
		public String getApellidos() {
			return apellidos;
		}
		public String getCodNotaria() {
			return codNotaria;
		}

		public String getCalleNotaria() {
			return calleNotaria;
		}
		public String getCodProvinciaNotaria() {
			return codProvinciaNotaria;
		}
		public String getProvinciaNotaria() {
			return provinciaNotaria;
		}
		public String getCodMunicipioNotaria() {
			return codMunicipioNotaria;
		}
		public String getMunicipioNotaria() {
			return municipioNotaria;
		}
		public String getCodigoPostalNotaria() {
			return codigoPostalNotaria;
		}

		public String getPlaza() {
			return plaza;
		}
		public boolean existe() {
			return existe;
		}
		
		
	}
	
	/**
	 * Datos de la finca en Tributas. 
	 * Utilizo String para todo ya que no se van a interpretar en el servicio,
	 * se pasarán como parámetros a otras llamadas.
	 * @author crubencvs
	 *
	 */
	public static class DatosFinca {
		private String idEperFinca;
		private String referenciaCatastral;
		private String siglaVia;
		private String nombreVia;
		private String numeroVia;
		private String duplicadoVia;
		private String escalera;
		private String planta;
		private String puerta;
		private String anioValorCatastral;
		private String valorCatastral;
		//CRUBENCVS 14/02/2022
		private String valorCatastralTotalInicial;
		// FIN CRUBENCVS 14/02/2022
		private String anioRevisionCatastral;
		private String superficie;
		private String codMunicipio;
		//CRUBENCVS  11/04/2022
		private String numeroFijo;
		private String codigoVia; 
		private String codProvincia;
		private String provincia;
		private String municipio;
		
		public String getIdEperFinca() {
			return idEperFinca;
		}
		public void setIdEperFinca(String idEperFinca) {
			this.idEperFinca = idEperFinca;
		}
		public String getSiglaVia() {
			return siglaVia;
		}
		public void setSiglaVia(String siglaVia) {
			this.siglaVia = siglaVia;
		}
		public String getNombreVia() {
			return nombreVia;
		}
		public void setNombreVia(String nombreVia) {
			this.nombreVia = nombreVia;
		}
		public String getNumeroVia() {
			return numeroVia;
		}
		public void setNumeroVia(String numeroVia) {
			this.numeroVia = numeroVia;
		}
		public String getDuplicadoVia() {
			return duplicadoVia;
		}
		public void setDuplicadoVia(String duplicadoVia) {
			this.duplicadoVia = duplicadoVia;
		}
		public String getEscalera() {
			return escalera;
		}
		public void setEscalera(String escalera) {
			this.escalera = escalera;
		}
		public String getPlanta() {
			return planta;
		}
		public void setPlanta(String planta) {
			this.planta = planta;
		}
		public String getPuerta() {
			return puerta;
		}
		public void setPuerta(String puerta) {
			this.puerta = puerta;
		}
		public String getAnioValorCatastral() {
			return anioValorCatastral;
		}
		public void setAnioValorCatastral(String anioValorCatastral) {
			this.anioValorCatastral = anioValorCatastral;
		}
		public String getValorCatastral() {
			return valorCatastral;
		}
		public void setValorCatastral(String valorCatastral) {
			this.valorCatastral = valorCatastral;
		}
		public String getAnioRevisionCatastral() {
			return anioRevisionCatastral;
		}
		public void setAnioRevisionCatastral(String anioRevisionCatastral) {
			this.anioRevisionCatastral = anioRevisionCatastral;
		}
		public String getSuperficie() {
			return superficie;
		}
		public void setSuperficie(String superficie) {
			this.superficie = superficie;
		}
		public String getReferenciaCatastral() {
			return referenciaCatastral;
		}
		public void setReferenciaCatastral(String referenciaCatastral) {
			this.referenciaCatastral = referenciaCatastral;
		}
		public String getValorCatastralTotalInicial() {
			return valorCatastralTotalInicial;
		}
		public void setValorCatastralTotalInicial(String valorCatastralTotalInicial) {
			this.valorCatastralTotalInicial = valorCatastralTotalInicial;
		}
		public String getCodMunicipio() {
			return codMunicipio;
		}
		public void setCodMunicipio(String codMunicipio) {
			this.codMunicipio = codMunicipio;
		}
		public String getNumeroFijo() {
			return numeroFijo;
		}
		public void setNumeroFijo(String numeroFijo) {
			this.numeroFijo = numeroFijo;
		}
		public String getCodigoVia() {
			return codigoVia;
		}
		public void setCodigoVia(String codigoVia) {
			this.codigoVia = codigoVia;
		}
		public String getCodProvincia() {
			return codProvincia;
		}
		public void setCodProvincia(String codProvincia) {
			this.codProvincia = codProvincia;
		}
		public String getProvincia() {
			return provincia;
		}
		public void setProvincia(String provincia) {
			this.provincia = provincia;
		}
		public String getMunicipio() {
			return municipio;
		}
		public void setMunicipio(String municipio) {
			this.municipio = municipio;
		}
		
		
	}
	/**
	 * Datos del estado de la presentación
	 * @author crubencvs
	 *
	 */
	public static class DatosEstadoPresentacion{
		private String estadoPresentacion;
		private String numeroJustificante;
		private String emisora;
		public String getNumeroJustificante() {
			return numeroJustificante;
		}
		public void setNumeroJustificante(String numeroJustificante) {
			this.numeroJustificante = numeroJustificante;
		}
		public String getEmisora() {
			return emisora;
		}
		public void setEmisora(String emisora) {
			this.emisora = emisora;
		}
		public String getEstadoPresentacion() {
			return estadoPresentacion;
		}
		public void setEstadoPresentacion(String estadoPresentacion) {
			this.estadoPresentacion = estadoPresentacion;
		}
		
	}
	
	public MediadorBD(Preferencias pref, String idLlamada){
		this.pref= pref;
		this.idLlamada= idLlamada;
	}
	/**
	 * Recupera el tipo de tránsmisión en función, exclusivamente, del acto jurídico.
	 * El tipo general de trámite (Transmisión, Sucesión o donación) no depende del resto de 
	 * datos. El tipo concreto sí.
	 * @throws DatosException
	 */
	public String recuperarTipoTransmision(String actoJuridico) throws DatosException{
		final String ESUN="ESUN_ESTRUCTURA_UNIVERSAL";
		final String TIPO_TRAMITE="C0";
		
		String tipoTransmision;
		try {
			TLanzador lanzador= LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcTipoTransmision(), pref.getEsquemaBD());
			//Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param(actoJuridico, ParamType.CADENA);
			proc.param("P", ParamType.CADENA);

			String soapResponse= lanzador.ejecutar(proc);
			RespuestaLanzador response= new RespuestaLanzador(soapResponse);
			if (!response.esErronea()){
				int filas= response.getNumFilasEstructura(ESUN);
				if (filas==1){ 
					tipoTransmision= response.getValue(ESUN, 1, TIPO_TRAMITE);
				} else {
					return null;
				}
			} else {
				throw new DatosException("Error al recuperar tipo del transmisión:" + response.getTextoError());
			}
			return tipoTransmision;
		} catch (LanzadorException le){
			throw new DatosException("Error al recuperar datos de transmisión:"+ le.getMessage(), le);
		}
	}
	/**
	 * Recuperación de los datos de la finca que nos interesan para el proceso
	 * @param referenciaCatastral
	 * @param codigoMunicipio
	 * @param ejercicio
	 * @return
	 */
	public DatosFinca getDatosFinca(String referenciaCatastral,
									String codigoMunicipio,
									String ejercicio) throws DatosException{
		final String DEUU="DEUU_DETALLE_UNIDAD_URBANA";
		final String CADE="CADE_CADENA";
		DatosFinca dat = new DatosFinca();
		try {
			TLanzador lanzador= LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcDatosFinca(), pref.getEsquemaBD());
			//Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param(codigoMunicipio, ParamType.NUMERO);
			proc.param(referenciaCatastral, ParamType.CADENA);
			proc.param(ejercicio, ParamType.CADENA);
			proc.param("P", ParamType.CADENA);
			
			String soapResponse= lanzador.ejecutar(proc);
			RespuestaLanzador response= new RespuestaLanzador(soapResponse);
			if (!response.esErronea()){
				if (response.getNumFilasEstructura(CADE)>0){
					if (!response.getValue(CADE,1,"STRING_CADE").equals("00")){
						return null;
					}
				}
				int filas= response.getNumFilasEstructura(DEUU);
				if (filas==1){ 
					dat.setIdEperFinca(response.getValue(DEUU, 1, "ID_EPER_FIN"));
					dat.setSiglaVia(response.getValue(DEUU, 1, "SIGLA_VIA_FIN"));
					dat.setNombreVia(response.getValue(DEUU, 1, "NOMBRE_VIA_FIN"));
					dat.setNumeroVia(response.getValue(DEUU, 1, "NUMERO_1_VIA_FIN"));
					dat.setDuplicadoVia(response.getValue(DEUU, 1, "DUPLICADO_1_VIA_FIN"));
					dat.setEscalera(response.getValue(DEUU, 1, "ESCALERA_FIN"));
					dat.setPlanta(response.getValue(DEUU, 1, "PLANTA_FIN"));
					dat.setPuerta(response.getValue(DEUU, 1, "PUERTA_FIN"));
					dat.setAnioValorCatastral(response.getValue(DEUU, 1, "ANY_VALOR_CAT_EFIN"));
					dat.setValorCatastral(response.getValue(DEUU, 1, "VALOR_CAT_SUELO_EFIN"));
					
					// CRUBENCVS  14/02/2022. Valor catastral total inicial
					dat.setValorCatastralTotalInicial(response.getValue(DEUU, 1, "VALOR_CAT_TOTAL_EFIN"));
					// FIN CRUBENCVS 14/02/2002
					dat.setAnioRevisionCatastral(response.getValue(DEUU, 1, "ANY_REVISION_EFIN"));
					dat.setSuperficie(response.getValue(DEUU,1,"SUP_SUELO_EFIN"));
					dat.setReferenciaCatastral(referenciaCatastral);
					dat.setCodMunicipio(response.getValue(DEUU, 1, "CODIGO_MUNICIPIO_FIN"));
					//CRUBENCVS  11/04/2022
					dat.setNumeroFijo(response.getValue(DEUU,1,"NUMERO_FIJO_FIN"));
					dat.setCodigoVia(response.getValue(DEUU,1,"COD_VIA_FIN"));
					dat.setMunicipio(response.getValue(DEUU, 1, "NOMBRE_MUNP"));
					dat.setCodProvincia(response.getValue(DEUU, 1, "CODIGO_PROVINCIA_FIN"));
					dat.setProvincia(response.getValue(DEUU, 1, "NOMBRE_PROV"));
				} else {
					throw new DatosException("Error al recuperar datos de la finca. No se han devuelto datos");
				}
			} else {
				throw new DatosException("Error al recuperar datos de la finca:" + response.getTextoError());
			}
			return dat;
		} catch (LanzadorException le){
			throw new DatosException("Error al recuperar datos de la finca:"+ le.getMessage(), le);
		}
	}
	
	public DatosCalculoLiquidacion calcularLiquidacion(
			String tipoTramite,
			String idEperFinca,
			String municipio,
			String fechaDevengo,
			String actoJuridico,
			String claseDerecho,
			String porcentajeTransmitido,
			Long valorVenta,
			String anioValorCatastral,
			String valorCatastral,
			String valorCatastralTotalInicial,
			String anioRevision,
			String[] fechasAdquisicionAnteriores,
			String[] porcentajesAdquisicionAnteriores,
			long valorAdquisicion,
			String edadUsufructoVitalicio,
			String aniosUsufructoTemporal,
			String aniosHabitacionTemporal,
			String edadHabitacionTemporal,
			String porcentajeBonificacion
	) throws DatosException{
		try {
			DatosCalculoLiquidacion dat= new DatosCalculoLiquidacion();
			String REPL="REPL_RESULTADOS_PLUSVALIAS";
			String CANU="CANU_CADENAS_NUMEROS";
			String ES10= "ES10_ESTRUCTURA_UNI10";
			StringBuilder fechasAdquisicion=new StringBuilder();
			for (int i=0;i<fechasAdquisicionAnteriores.length;i++){
				fechasAdquisicion.append(fechasAdquisicionAnteriores[i]+"#");
			}
			//Quitamos la última #.
			fechasAdquisicion.deleteCharAt(fechasAdquisicion.length()-1);
			StringBuilder porcentajesAdquisicion=new StringBuilder();
			for (int i=0; i<porcentajesAdquisicionAnteriores.length;i++){
				porcentajesAdquisicion.append(porcentajesAdquisicionAnteriores[i]+"#");
			}
			porcentajesAdquisicion.deleteCharAt(porcentajesAdquisicion.length()-1);
			
			
			TLanzador lanzador= LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcCalcularLiquidacion(), pref.getEsquemaBD());
			//Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param(tipoTramite, ParamType.CADENA);
			proc.param(idEperFinca, ParamType.NUMERO);
			proc.param(municipio, ParamType.CADENA);
			proc.param(fechaDevengo, ParamType.FECHA,"DD/MM/YYYY");
			proc.param(actoJuridico,ParamType.CADENA);
			proc.param(claseDerecho,ParamType.CADENA);
			proc.param(porcentajeTransmitido, ParamType.NUMERO);
			proc.param(String.valueOf(valorVenta), ParamType.NUMERO);
			proc.param(anioValorCatastral, ParamType.NUMERO);
			proc.param(valorCatastral, ParamType.NUMERO);
			proc.param(anioRevision, ParamType.NUMERO);
			proc.param(valorCatastralTotalInicial, ParamType.NUMERO);
			proc.param(fechasAdquisicion.toString(), ParamType.CADENA);
			proc.param(porcentajesAdquisicion.toString(), ParamType.CADENA);
			proc.param(String.valueOf(valorAdquisicion), ParamType.NUMERO);
			proc.param(edadUsufructoVitalicio, ParamType.NUMERO);
			proc.param(aniosUsufructoTemporal, ParamType.NUMERO);
			proc.param(aniosHabitacionTemporal, ParamType.NUMERO);
			proc.param(edadHabitacionTemporal, ParamType.NUMERO);
			proc.param(porcentajeBonificacion, ParamType.NUMERO);
			proc.param("P", ParamType.CADENA);
			
			String soapResponse= lanzador.ejecutar(proc);
			RespuestaLanzador response= new RespuestaLanzador(soapResponse);
			if (!response.esErronea()){
				//Comprobamos si ha terminado el cálculo
				if (response.getNumFilasEstructura(CANU)> 0){
					String codigo= response.getValue(CANU, 1, "STRING1_CANU");
					dat.setCodigoTerminacion(codigo);
					if ("0000".equals(codigo)){
						int filas= response.getNumFilasEstructura(REPL);
						if (filas==1){ 
							dat.totalIngresar= response.getValue(REPL, 1, "CFL_REPL");
							dat.recargo= response.getValue(REPL, 1, "IMPORTE_VALO_REPL");
							dat.interesesDemora= response.getValue(REPL,1,"ID_REPL");
							dat.valorSueloActual= response.getValue(REPL,1,"VCS_REPL");
							dat.baseImponibleDirecta= response.getValue(REPL,1,"BIED_REPL");
							dat.valorCompra= response.getValue(REPL,1,"VALOR_COMPRA_REPL");
							dat.valorVenta= response.getValue(REPL,1,"VALOR_VENTA_REPL");
							dat.valorTerreno= response.getValue(REPL, 1, "VT_REPL");
							dat.tipoCalculoElegido= response.getValue(REPL,1,"BI_ELEGIDA_REPL");
							dat.retrasoPresentacion= response.getValue(REPL,1,"RET_REPL");
							dat.porcentajeRecargo= response.getValue(REPL,1,"REC_REPL");
							dat.cuotaResultante= response.getValue(REPL, 1, "CUO2_REPL");
							dat.valorCatastralActual= response.getValue(REPL, 1, "VCT_REPL");
							//Datos de adquisiciones anteriores
							//Buscamos tantas adquisiciones anteriores como fechas de adquisición haya.
							String[] aniosTranscurridos= new String[fechasAdquisicionAnteriores.length];
							String[] coeficientesAplicables= new String[fechasAdquisicionAnteriores.length];
							String[] basesImponibleObjetivas= new String[fechasAdquisicionAnteriores.length];
							String[] tiposImpositivos = new String[fechasAdquisicionAnteriores.length];
							String[] cuotasLiquidacion = new String[fechasAdquisicionAnteriores.length];
							//Itero sobre uno de ellos únicamente, porque al final tienen la misma longitud.
							for (int i=0;i<aniosTranscurridos.length;i++){
								
								aniosTranscurridos[i] = response.getValue(REPL, 1, "NA"+(i==0?"":i+1)+"_REPL");
								coeficientesAplicables[i] = response.getValue(REPL, 1, "PA"+(i==0?"":i+1)+"_REPL");
								basesImponibleObjetivas[i] = response.getValue(REPL,1,"BI"+(i==0?"":i+1)+"_REPL");
								tiposImpositivos[i] = response.getValue(REPL, 1, "TP"+(i==0?"":i+1)+"_REPL");
								cuotasLiquidacion[i] = response.getValue(REPL,1,"CUO1"+(i==0?"":"_"+(i+1))+"_REPL");
							}
							dat.aniosTranscurridos= aniosTranscurridos;
							dat.coeficientesAplicables= coeficientesAplicables;
							dat.basesImponiblesObjetivas= basesImponibleObjetivas;
							dat.tiposImpositivos= tiposImpositivos;
							dat.cuotasLiquidacion= cuotasLiquidacion;
							dat.coeficienteAplicableAnual= response.getValue(REPL,1,"PAN_REPL");
							//Datos extra, que no estaban en el cálculo original
							int filasConc = response.getNumFilasEstructura(ES10);
							if (filasConc==1){
								dat.setCodigoPltr(response.getValue(ES10, 1, "C1"));
								dat.setNombrePltr(response.getValue(ES10, 1, "C2"));
								dat.setCodigoPlca(response.getValue(ES10, 1, "C3"));
								dat.setNombrePlca(response.getValue(ES10, 1, "C4"));
								dat.setCodigoPlop(response.getValue(ES10, 1, "C5"));
								dat.setNombrePlop(response.getValue(ES10, 1, "C6"));
								dat.setFechaFinVoluntaria(response.getValue(ES10, 1, "C7"));
							} else {
								throw new DatosException("Error al recuperar datos del concepto de plusvalía en el cálculo. No se han devuelto datos");
							}
							dat.setCorrecto(true);
						} else {
							throw new DatosException("Error al recuperar datos del cálculo. No se han devuelto datos");
						}
					} else {
						dat.setCorrecto(false);
					}
				} else {
					throw new DatosException("Error al realizar el cálculo de la liquidación, no se ha recibido código de terminación");
				}
				
			} else {
				throw new DatosException("Error al realizar el cálculo de la liquidación:" + response.getTextoError());
			}
			return dat;
		} catch (LanzadorException le){
			throw new DatosException("Error al realizar el cálculo de la liquidación:"+ le.getMessage(), le);
		}
	}
	/**
	 * Realiza validaciones previas al cálculo
	 * @param notarioTitular
	 * @param notarioAutorizante
	 * @param actoJuridico
	 * @param fechaAutorizacion
	 * @return
	 * @throws DatosException
	 */
	public String validacionesPreviasCalculo(
			                            String notarioTitular,
			                            String notarioAutorizante,
										String actoJuridico,
										String fechaAutorizacion
										) throws DatosException{
		String CADE="CADE_CADENA";
		
		String codigoValidacion;
		try {
			TLanzador lanzador= LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcValidacionesPreviasCalculo(), pref.getEsquemaBD());
			//Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param(notarioTitular, ParamType.CADENA);
			proc.param(notarioAutorizante, ParamType.CADENA);
			proc.param(actoJuridico, ParamType.CADENA);
			proc.param(fechaAutorizacion, ParamType.FECHA,"DD/MM/YYYY");
			proc.param("P", ParamType.CADENA);

			String soapResponse= lanzador.ejecutar(proc);
			RespuestaLanzador response= new RespuestaLanzador(soapResponse);
			if (!response.esErronea()){
				int filas= response.getNumFilasEstructura(CADE);
				if (filas==1){ 
					codigoValidacion= response.getValue(CADE, 1, "STRING_CADE");
				} else {
					throw new DatosException("Error en comunicación con B.D. al validar la petición de cálculo. No se han devuelto datos");
				}
			} else {
				throw new DatosException("Error al validar la petición:" + response.getTextoError());
			}
			return codigoValidacion;
		} catch (LanzadorException le){
			throw new DatosException("Error al validar la petición:"+ le.getMessage(), le);
		}
	}
	/**
	 * Recupera los datos 
	 * Código
	 * Nombre
	 * Apellidos
	 * NIF
	 * Notaría
	 * de un notario
	 * @param codigoNotario
	 * @param fechaAutorizacion
	 * @return
	 * @throws DatosException
	 */
	public DatosNotario getDatosNotario(
		            String codigoNotario,
		            String fechaAutorizacion
				) throws DatosException{
		String CANU = "CANU_CADENAS_NUMEROS";
		String ESUN = "ESUN_ESTRUCTURA_UNIVERSAL";

		String codigoValidacion;
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcDatosNotario(), pref.getEsquemaBD());
			// Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param(codigoNotario, ParamType.CADENA);
			proc.param(fechaAutorizacion, ParamType.FECHA, "DD/MM/YYYY");
			proc.param("P", ParamType.CADENA);

			String soapResponse = lanzador.ejecutar(proc);
			RespuestaLanzador response = new RespuestaLanzador(soapResponse);
			DatosNotario d= new DatosNotario();
			if (!response.esErronea()) {
				int filas = response.getNumFilasEstructura(CANU);
				if (filas == 1) {
					codigoValidacion = response.getValue(CANU, 1, "STRING1_CANU");
					if ("00".equals(codigoValidacion)){
						if (response.getNumFilasEstructura(ESUN)== 1){
							d.codigo= response.getValue(ESUN, 1, "C1");
							d.nif= response.getValue(ESUN, 1, "C2");
							d.nombre= response.getValue(ESUN, 1, "C3");
							d.apellidos= response.getValue(ESUN,1,"C4");
							d.codNotaria = response.getValue(ESUN, 1, "C5");
							d.calleNotaria= response.getValue(ESUN, 1, "C6");
							d.codProvinciaNotaria= response.getValue(ESUN,1,"C7");
							d.provinciaNotaria= response.getValue(ESUN, 1, "C8");
							d.codMunicipioNotaria= response.getValue(ESUN, 1, "C9");
							d.municipioNotaria= response.getValue(ESUN, 1, "C10");
							d.codigoPostalNotaria= response.getValue(ESUN, 1, "C11");
							d.plaza = response.getValue(ESUN, 1, "C12");
							d.existe=true;
						} else {
							throw new DatosException ("Error, no se han devuelto datos del notario");
						}
					} else {
						d.existe=false;
					}
				} else {
					throw new DatosException(
							"Error en comunicación con B.D. al recuperar los datos del notario. No se han devuelto datos");
				}
			} else {
				throw new DatosException(
						"Error al recuperar datos del notario:"
								+ response.getTextoError());
			}
			return d;
		} catch (LanzadorException le) {
			throw new DatosException("Error al recuperar datos del notario:"+ le.getMessage(), le);
		}
	}
	/**
	 * Recupera un nuevo número de justificante de 029.
	 * @return
	 * @throws DatosException
	 */
	/*public NumeroJustificante getNumeroJustificante() throws DatosException{
		String CADE = "CADE_CADENA";

		String emisor;
		String numeroJustificante;
		NumeroJustificante num = new NumeroJustificante();
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcGeneraModelo(), pref.getEsquemaBD());
			// Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param("P", ParamType.CADENA);
		
			String soapResponse = lanzador.ejecutar(proc);
			RespuestaLanzador response = new RespuestaLanzador(soapResponse);
			
			if (!response.esErronea()) {
				int filas = response.getNumFilasEstructura(CADE);
				if (filas >= 2) { //Por si algún día devuelve más de dos, debemos seguir funcionando
					emisor = response.getValue(CADE, 1, "STRING_CADE");
					numeroJustificante= response.getValue(CADE, 2, "STRING_CADE");
					num.emisora= emisor;
					num.numeroJustificante= numeroJustificante;
				} else {
					throw new DatosException(
							"Error en comunicación con B.D. al recuperar la emisora y el número de justificante. No se han devuelto datos o no suficientes");
				}
			} else {
				throw new DatosException(
						"Error al recuperar datos de la emisora y el número de justificante:"
								+ response.getTextoError());
			}
			return num;
		} catch (LanzadorException le) {
			throw new DatosException("Error al recuperar datos de la emisora y el número de justificante:"+ le.getMessage(), le);
		}
	}*/
	
	/**
	 * Recupera el requisito asociado a 
	 * @param fechaDevengo
	 * @param municipio
	 * @param porcentaje
	 * @param conceptoBeneficio
	 * @param tipoTramite
	 * @return
	 * @throws DatosException
	 */
	public String recuperaRequisito(
							String fechaDevengo, 
							String municipio,
							String porcentaje, 
							String conceptoBeneficio, 
							String tipoTramite)
			throws DatosException {

		String CADE = "CADE_CADENA";

		String requisito;
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(
					this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcRequisitosBeneficios(), pref.getEsquemaBD());
			// Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param(municipio, ParamType.NUMERO);
			proc.param(fechaDevengo, ParamType.FECHA, "DD/MM/YYYY");
			proc.param(porcentaje, ParamType.NUMERO);
			proc.param(conceptoBeneficio, ParamType.CADENA);
			proc.param(tipoTramite, ParamType.CADENA);
			proc.param("P", ParamType.CADENA);

			String soapResponse = lanzador.ejecutar(proc);
			RespuestaLanzador response = new RespuestaLanzador(soapResponse);
			if (!response.esErronea()) {
				int filas = response.getNumFilasEstructura(CADE);
				if (filas == 1) {
					requisito = response
							.getValue(CADE, 1, "STRING_CADE");
				} else {
					throw new DatosException(
							"Error en comunicación con B.D. al recuperar el requisito. No se han devuelto datos");
				}
			} else {
				throw new DatosException("Error al recuperar el requisito:"	+ response.getTextoError());
			}
			return requisito;
		} catch (LanzadorException le) {
			throw new DatosException("Error al recuperar el requisito:"	+ le.getMessage(), le);
		}
	}
	/**
	 * Integra los datos del modelo, es decir, los inserta pero no los presenta.
	 * @param numeroAutoliquidacion Número de autoliquidación cuyos datos se van a insertar
	 * @param remesa Datos de la remesa
	 * @return true si ha podido insertarlo, false si no
	 * @throws DatosException
	 */
	public boolean integraModelo(
			String numeroAutoliquidacion,
			String remesa)
		throws DatosException {

		String CANU = "CANU_CADENAS_NUMEROS";
		boolean error=false;
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(
				this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcIntegraModelo(), pref.getEsquemaBD());
			String modelo = numeroAutoliquidacion.substring(0,3);

			proc.param(modelo, ParamType.CADENA);
			proc.param(numeroAutoliquidacion, ParamType.CADENA);
			proc.param(remesa, ParamType.CLOB);
			proc.param("P", ParamType.CADENA);
			
			String soapResponse = lanzador.ejecutar(proc);
			RespuestaLanzador response = new RespuestaLanzador(soapResponse);
			if (!response.esErronea()) {
			int filas = response.getNumFilasEstructura(CANU);
			if (filas == 1) {
				String codigo = response.getValue(CANU, 1, "NUME1_CANU");
				if (!"0".equals(codigo)){
					error=true;
				}
			} else {
				throw new DatosException(
						"Error en comunicación con B.D. al insertar el modelo. No se han devuelto datos");
			}
			} else {
				throw new DatosException("Error al insertar el modelo:"	+ response.getTextoError());
			}
			return error;
		} catch (LanzadorException le) {
			throw new DatosException("Error al insertar el modelo:"	+ le.getMessage(), le);
		}
	}
	
	/**
	 * Presenta los datos del modelo. No devuelve datos.
	 * @param numeroAutoliquidacion
	 * @throws DatosException
	 */
	public void presentaModelo(
			String numeroAutoliquidacion)
		throws DatosException {
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(
				this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcPresentaModelo(), pref.getEsquemaBD());
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33",ParamType.NUMERO);
			proc.param(numeroAutoliquidacion, ParamType.CADENA);
			//proc.param("P", ParamType.CADENA);
			
			String soapResponse=lanzador.ejecutar(proc);
			RespuestaLanzador response = new RespuestaLanzador(soapResponse);
			if (response.esErronea()) {
				throw new DatosException("Error al presentar el modelo:"	+ response.getTextoError());
			}
			return ;
		} catch (LanzadorException le) {
			throw new DatosException("Error al presentar el modelo:"	+ le.getMessage(), le);
		}
	}
	/**
	 * Recupera los datos actuales de la presentación, tal como están en la tabla de control de la base de datos
	 * @param codNotarioAutorizante
	 * @param protocolo
	 * @param protocoloBis
	 * @param fechaProtocolo
	 * @param actoJuridico
	 * @param refCatastral
	 * @param nifSujetoPasivo
	 * @return
	 * @throws DatosException
	 */
	public DatosEstadoPresentacion estadoPresentacion(
						String codNotarioAutorizante,
						String protocolo,
						String protocoloBis,
						String fechaProtocolo,
						String actoJuridico,
						String refCatastral,
						String nifSujetoPasivo
	) throws DatosException{
		String CANU = "CANU_CADENAS_NUMEROS";

		String emisor;
		String numeroJustificante;
		String estado;
		DatosEstadoPresentacion est = new DatosEstadoPresentacion();
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcRecuperaEstadoPresentacion(), pref.getEsquemaBD());
			// Cabecera
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param(codNotarioAutorizante, ParamType.CADENA);
			proc.param(protocolo, ParamType.CADENA);
			proc.param(protocoloBis, ParamType.CADENA);
			proc.param(fechaProtocolo, ParamType.FECHA,"DD/MM/YYYY");
			proc.param(actoJuridico, ParamType.CADENA);
			proc.param(refCatastral, ParamType.CADENA);
			proc.param(nifSujetoPasivo, ParamType.CADENA);
			proc.param("P", ParamType.CADENA);
		
			String soapResponse = lanzador.ejecutar(proc);
			RespuestaLanzador response = new RespuestaLanzador(soapResponse);
			
			if (!response.esErronea()) {
				int filas = response.getNumFilasEstructura(CANU);
				if (filas >0) { 
					emisor = response.getValue(CANU, 1, "STRING2_CANU");
					numeroJustificante= response.getValue(CANU, 1, "STRING3_CANU");
					estado = response.getValue(CANU, 1, "STRING1_CANU");
					est.emisora= emisor;
					est.numeroJustificante= numeroJustificante;
					est.estadoPresentacion= estado;
				} else {
					throw new DatosException(
							"Error en comunicación con B.D. al recuperar el estado de la presentación. No se han devuelto datos o no suficientes");
				}
			} else {
				throw new DatosException(
						"Error al recuperar el estado de la presentación:"
								+ response.getTextoError());
			}
			return est;
		} catch (LanzadorException le) {
			throw new DatosException("Error al recuperar el estado de la presentación:"+ le.getMessage(), le);
		}
	}
	/**
	 * Actualiza el estado de la presentación en la base de datos, para tener el control
	 * @param numeroAutoliquidacion
	 * @param v Valor del estado de la presentación
	 * @throws DatosException
	 */
	public void actualizaEstado(
					String numeroAutoliquidacion,
					ValorEstado v)
		throws DatosException {
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(
				this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcActualizaEstado(), pref.getEsquemaBD());
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33",ParamType.NUMERO);
			proc.param(numeroAutoliquidacion, ParamType.CADENA);
			proc.param(v.toString(), ParamType.CADENA);
			proc.param("P", ParamType.CADENA);
			
			String soapResponse= lanzador.ejecutar(proc);
			RespuestaLanzador response = new RespuestaLanzador(soapResponse);
			if (response.esErronea()) {
				throw new DatosException("Error al actualizar el estado en la tabla de control:"	+ response.getTextoError());
			}
			return ;
		} catch (LanzadorException le) {
			throw new DatosException("Error al actualizar el estado en la tabla de control:"	+ le.getMessage(), le);
		}
	}
	/**
	 * Recupera el id de reimprimible del justificante de presentacion
	 * @param numeroAutoliquidacion
	 * @return
	 * @throws DatosException
	 */
	public String recuperaIdreimprimibleJustificantePresentacion(
										String numeroAutoliquidacion
									   )
		throws DatosException {

		String CANU = "CANU_CADENAS_NUMEROS";
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(), new SoapClientHandler(
				this.idLlamada));
			ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcJustificantePres(), pref.getEsquemaBD());
			proc.param("1", ParamType.NUMERO);
			proc.param("1", ParamType.NUMERO);
			proc.param("USU_WEB_SAC", ParamType.CADENA);
			proc.param("33", ParamType.NUMERO);
			proc.param(numeroAutoliquidacion, ParamType.CADENA);
			proc.param("P", ParamType.CADENA);
			
			String soapResponse = lanzador.ejecutar(proc);
			RespuestaLanzador response = new RespuestaLanzador(soapResponse);
			if (!response.esErronea()) {
			int filas = response.getNumFilasEstructura(CANU);
			if (filas == 1) {
				String idgdre = response.getValue(CANU, 1, "NUME1_CANU");
				return idgdre;
			} else {
				throw new DatosException(
						"Error en comunicación con B.D. al insertar el modelo. No se han devuelto datos");
			}
			} else {
				throw new DatosException("Error al insertar el modelo:"	+ response.getTextoError());
			}
		} catch (LanzadorException le) {
			throw new DatosException("Error al insertar el modelo:"	+ le.getMessage(), le);
		}
	}
}


