package es.stpa.plusvalias.domain.intercambio;
/**
 * Tipo de la cabecera de petición
 * @author crubencvs
 *
 */
public class Cabecera{
	protected String aplicacion;
    protected long idcomunicacion;
    protected String fecha;
    protected String hora;
    protected String operacion;
    protected String emisor;
    protected String receptor;
	public String getAplicacion() {
		return aplicacion;
	}
	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}
	public long getIdcomunicacion() {
		return idcomunicacion;
	}
	public void setIdcomunicacion(long idcomunicacion) {
		this.idcomunicacion = idcomunicacion;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public String getOperacion() {
		return operacion;
	}
	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}
	public String getEmisor() {
		return emisor;
	}
	public void setEmisor(String emisor) {
		this.emisor = emisor;
	}
	public String getReceptor() {
		return receptor;
	}
	public void setReceptor(String receptor) {
		this.receptor = receptor;
	}
    
}
