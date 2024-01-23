package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos acerca del importe a pagar/pagado por la liquidación
 * @author crubencvs
 *
 */
public class ImporteType{
    private  boolean informarecargo;
    protected String porcentajerecargo;
    protected double importerecargo;
    protected double importetotal;
	public boolean informarecargo() {
		return informarecargo;
	}
	public void setInformarecargo(boolean informarecargo) {
		this.informarecargo = informarecargo;
	}
	public String getPorcentajerecargo() {
		return porcentajerecargo;
	}
	public void setPorcentajerecargo(String porcentajerecargo) {
		this.porcentajerecargo = porcentajerecargo;
	}
	public double getImporterecargo() {
		return importerecargo;
	}
	public void setImporterecargo(double importerecargo) {
		this.importerecargo = importerecargo;
	}
	public double getImportetotal() {
		return importetotal;
	}
	public void setImportetotal(double importetotal) {
		this.importetotal = importetotal;
	}
	
	
	
    
}