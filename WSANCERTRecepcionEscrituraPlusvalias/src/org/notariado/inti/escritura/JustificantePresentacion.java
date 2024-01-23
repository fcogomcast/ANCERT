package org.notariado.inti.escritura;

import org.notariado.inti.bd.BDFactory;
import org.notariado.inti.bd.GestorLlamadasBD;
import org.notariado.inti.bd.ResultadosLlamadasBD;
import org.notariado.inti.exceptions.BDException;
import org.notariado.inti.exceptions.ImpresionException;
import org.notariado.inti.exceptions.RecepcionException;
import org.notariado.inti.impresion.DocumentoDoin;
import org.notariado.inti.impresion.ImpresionFactory;
import org.notariado.inti.impresion.ReimpresionDocumento;
import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.utils.log.Logger;

/**
 * Generación de justificante de presentación.
 * @author crubencvs
 *
 */
public class JustificantePresentacion {

	private String idInforme;
	private String tipoInforme;
	private String tipoDocumento;
	private String numeroJustificante;
	private String macCodigoVerificacion;
	private String nifNotario;
	private Preferencias pref;
	private Logger log;
	private String idSesion;
	private String docPDF;
	
	protected JustificantePresentacion (Preferencias pref, Logger log, String idSesion)
	{
		this.pref = pref;
		this.log = log;
		this.idSesion= idSesion;
	}
	/**
	 * Genera el justificante, e informa los atributos del objeto con el resultado de la impresión.
	 * @param organismo Organismo para el que se genera el informe
	 * @param codNotario Código de notario
	 * @param codNotaria Código de notaría
	 * @param protocolo Protocolo
	 * @param protocoloBis ProtocoloBis
	 * @param fechaAutorizacion Fecha Autorización
	 * @param idSubo Identificador del ayuntamiento para el que se ha enviado la escritura
	 * @throws RecepcionException
	 */
	public String generarJustificante (String organismo,String codNotario, String codNotaria,
													String protocolo, String protocoloBis, String fechaAutorizacion,
													String idSubo) throws RecepcionException
	{
		GestorLlamadasBD datos = BDFactory.newGestorLlamadasBD(pref, log, idSesion);
		try {
			ResultadosLlamadasBD.ResultadoImpresionJustificante res=datos.imprimirJustificantePresentacion(organismo, codNotario, codNotaria, protocolo, protocoloBis, fechaAutorizacion, idSubo);
			if (res.getIdReimprimible()==null || "".equals(res.getIdReimprimible()))
			{
				throw new RecepcionException ("No se ha podido imprimir el justificante de pago, el identificador de informe está vacío.");
			}
			this.idInforme = res.getIdReimprimible();
			this.tipoInforme = res.getTipoReimprimible();
			this.tipoDocumento= res.getTipoDocumento();
			this.nifNotario = res.getNifPresentador();
			this.numeroJustificante= res.getNumeroJustificante();
			this.macCodigoVerificacion= res.getMacCodigoVerificacion();
			return recuperaDocumento();
			
		} catch (BDException e) {
			throw new RecepcionException ("Error producido al generar el justificante de presentación:" + e.getMessage(),e);
		}
		
	}
	/**
	 * Recupera el documento reimprimible previamente generado mediante este objeto.
	 * @return
	 * @throws RecepcionException
	 */
	public String recuperaDocumento() throws RecepcionException
	{
		String doc;
		String codigoVerificacion = this.tipoDocumento+this.numeroJustificante+"-" + this.macCodigoVerificacion;
		ReimpresionDocumento imp = ImpresionFactory.getReimpresion(pref, idSesion);
		try {
			doc = imp.getReimpresion(this.idInforme, this.tipoInforme, codigoVerificacion);
			this.docPDF= doc;
		} catch (ImpresionException e) {
			throw new RecepcionException ("No se ha podido recuperar el reimprimible de informe debido a :"+ e.getMessage(),e);
		}
		return doc;
	}
	
	public String altaDocumento ()
	{
		DocumentoDoin docum = ImpresionFactory.getGestorDoin(pref, idSesion);
		return docum.altaDocumento(this.numeroJustificante, this.tipoDocumento, this.macCodigoVerificacion, this.nifNotario, this.nifNotario, this.docPDF);
	}
	
	/**
	 * Devuelve el identificador del informe generado (id de reimprimible)
	 * @return
	 */
	public String getIdInforme() {
		return idInforme;
	}
	public void setIdInforme(String idInforme) {
		this.idInforme = idInforme;
	}
	/**
	 * Devuelve el tipo de informe que se ha generado (tipo de reimprimible)
	 * @return
	 */
	public String getTipoInforme() {
		return tipoInforme;
	}
	public void setTipoInforme(String tipoInforme) {
		this.tipoInforme = tipoInforme;
	}
	/**
	 * Devuelve el tipo de documento generado ("J" de justificante, en este caso)
	 * @return
	 */
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	/**
	 * Devuelve el número de justificante generado.
	 * @return
	 */
	public String getNumeroJustificante() {
		return numeroJustificante;
	}
	public void setNumeroJustificante(String numeroJustificante) {
		this.numeroJustificante = numeroJustificante;
	}
	/**
	 * Devuelve la mac de Código de verificación.
	 * @return
	 */
	public String getMacCodigoVerificacion() {
		return macCodigoVerificacion;
	}
	public void setMacCodigoVerificacion(String macCodigoVerificacion) {
		this.macCodigoVerificacion = macCodigoVerificacion;
	}
	/**
	 * Devuelve el nif de notario asociado al justificante
	 * @return
	 */
	public String getNifNotario() {
		return nifNotario;
	}
	public void setNifNotario(String nifNotario) {
		this.nifNotario = nifNotario;
	}
	
	
}
