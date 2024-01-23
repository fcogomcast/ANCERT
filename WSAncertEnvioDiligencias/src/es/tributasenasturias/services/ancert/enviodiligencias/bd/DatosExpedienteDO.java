package es.tributasenasturias.services.ancert.enviodiligencias.bd;

/**
 * Almacena datos del expediente.
 */
public class DatosExpedienteDO {
	private String oficinaAlta;
	private String organismo;
	private String numExpediente;
	private String numeroAutoliquidacion;
	private String fechaAlta;
	private String oficinaDestino;
	private String horaAlta;
	private String fechaPresentacion;
	private String tipoExpediente;
	private String subtipoExpediente;
	private String numeroProtocolo;
	private String apellidosNombreNotario;
	private String tipoDocumento;
	private String fechaDocumento;
	private String concepto;
	private String tipoSujecion;
	private String codTipoSujecion;
	private String importeAutoliq;
	private String datoEspecifico;
	private String nrc;
	private String modelo;
	
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	protected DatosExpedienteDO()
	{
		
	}
	public String getOficinaAlta() {
		return oficinaAlta;
	}
	public void setOficinaAlta(String oficinaAlta) {
		this.oficinaAlta = oficinaAlta;
	}
	public String getOrganismo() {
		return organismo;
	}
	public void setOrganismo(String organismo) {
		this.organismo = organismo;
	}
	public String getNumExpediente() {
		return numExpediente;
	}
	public void setNumExpediente(String numExpediente) {
		this.numExpediente = numExpediente;
	}
	public String getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(String fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	public String getOficinaDestino() {
		return oficinaDestino;
	}
	public void setOficinaDestino(String oficinaDestino) {
		this.oficinaDestino = oficinaDestino;
	}
	public String getHoraAlta() {
		return horaAlta;
	}
	public void setHoraAlta(String horaAlta) {
		this.horaAlta = horaAlta;
	}
	public String getFechaPresentacion() {
		return fechaPresentacion;
	}
	public void setFechaPresentacion(String fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
	}
	public String getTipoExpediente() {
		return tipoExpediente;
	}
	public void setTipoExpediente(String tipoExpediente) {
		this.tipoExpediente = tipoExpediente;
	}
	public String getSubtipoExpediente() {
		return subtipoExpediente;
	}
	public void setSubtipoExpediente(String subtipoExpediente) {
		this.subtipoExpediente = subtipoExpediente;
	}
	public String getNumeroProtocolo() {
		return numeroProtocolo;
	}
	public void setNumeroProtocolo(String numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}
	public String getApellidosNombreNotario() {
		return apellidosNombreNotario;
	}
	public void setApellidosNombreNotario(String apellidosNombreNotario) {
		this.apellidosNombreNotario = apellidosNombreNotario;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getFechaDocumento() {
		return fechaDocumento;
	}
	public void setFechaDocumento(String fechaDocumento) {
		this.fechaDocumento = fechaDocumento;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getTipoSujecion() {
		return tipoSujecion;
	}
	public void setTipoSujecion(String tipoSujecion) {
		this.tipoSujecion = tipoSujecion;
	}
	public String getCodTipoSujecion() {
		return codTipoSujecion;
	}
	public void setCodTipoSujecion(String codTipoSujecion) {
		this.codTipoSujecion = codTipoSujecion;
	}
	public String getImporteAutoliq() {
		return importeAutoliq;
	}
	public void setImporteAutoliq(String importeAutoliq) {
		this.importeAutoliq = importeAutoliq;
	}
	public String getNumeroAutoliquidacion() {
		return numeroAutoliquidacion;
	}
	public void setNumeroAutoliquidacion(String numeroAutoliquidacion) {
		this.numeroAutoliquidacion = numeroAutoliquidacion;
	}
	public String getDatoEspecifico() {
		return datoEspecifico;
	}
	public void setDatoEspecifico(String datoEspecifico) {
		this.datoEspecifico = datoEspecifico;
	}
	public String getNrc() {
		return nrc;
	}
	public void setNrc(String nrc) {
		this.nrc = nrc;
	}
	
}
