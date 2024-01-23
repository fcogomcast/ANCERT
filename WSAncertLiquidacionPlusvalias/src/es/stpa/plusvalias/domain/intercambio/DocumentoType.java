package es.stpa.plusvalias.domain.intercambio;

/**
 * Datos acerca del documento
 * @author crubencvs
 *
 */
public class DocumentoType {
    protected long idsujeto;
    protected ImporteType importe;
    protected byte[] pdf;
	public long getIdsujeto() {
		return idsujeto;
	}
	public void setIdsujeto(long idsujeto) {
		this.idsujeto = idsujeto;
	}
	public ImporteType getImporte() {
		return importe;
	}
	public void setImporte(
			ImporteType importe) {
		this.importe = importe;
	}
	public byte[] getPdf() {
		return pdf;
	}
	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}
}