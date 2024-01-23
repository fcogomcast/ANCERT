package es.stpa.plusvalias.domain;

import es.stpa.plusvalias.documentos.MediadorWSDocumentos;
import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.exceptions.PlusvaliaException;
import es.stpa.plusvalias.presentacion.PresentacionLiquidacion;

/**
 * Encargada de imprimir los justificantes de consulta/presentación
 * @author crubencvs
 *
 */
public class ImpresorJustificante {

	private MediadorWSDocumentos doc;
	public ImpresorJustificante(MediadorWSDocumentos doc){
		this.doc= doc;
	}
	

	/**
	 * Para no tener "null" si falta algún elemento
	 * @param val
	 * @return
	 */
	private String null2empty(String val){
		if (val==null){
			return "";
		}
		return val;
	}
	/**
	 * Imprime un justificante de cálculo
	 * @param liq
	 * @return
	 * @throws PlusvaliaException
	 */
	public byte[] imprimirJustificanteCalculo(LiquidacionContribuyente liq, Finca finca, DocumentoNotarial docNotarial) throws PlusvaliaException{
		Persona contribuyente= liq.getSujetoPasivo().getDatosbasicos();
		
		String nombreNotario= null2empty(docNotarial.getNotarioAutorizante().getNombre()) 
        + " " + 
        null2empty(docNotarial.getNotarioAutorizante().getApellidos());
		
		String nombreSp = null2empty(contribuyente.getDatospersonales().getNombre()) 
		+ " " + 
		null2empty(contribuyente.getDatospersonales().getApellido1RAZONSOCIAL()) 
		+ " " + 
		null2empty(contribuyente.getDatospersonales().getApellido2());						
		return  doc.imprimirResumenConsulta(finca.getReferenciaCatastral(), 
							finca.getCodMunicipio(), 
						    docNotarial.getNotarioAutorizante().getCodigo(), 
						    nombreNotario, 
						    docNotarial.getProtocolo().getNumero()+" "+ (docNotarial.getProtocolo().getNumerobis()==null?"":docNotarial.getProtocolo().getNumerobis()),
						    docNotarial.getProtocolo().getFechaautorizacion(),
						    contribuyente.getDatospersonales().getNumerodocumento(), 
						    nombreSp, 
						    String.valueOf(liq.getImporte().getImportetotal()).replace(".",","), 
						    ""); //El porcentaje transmitido no se utiliza, así que no lo usamos
	}
	
	/**
	 * Imprime un recibo de presentación
	 * @param liq
	 * @return
	 * @throws PlusvaliaException
	 */
	public byte[] imprimirReciboPresentacion( PresentacionLiquidacion pres, Finca finca, DocumentoNotarial docNotarial) throws PlusvaliaException{
		LiquidacionContribuyente liq = pres.getLiquidacionContribuyente();
		Persona contribuyente= liq.getSujetoPasivo().getDatosbasicos();
		
		String nombreNotario= null2empty(docNotarial.getNotarioAutorizante().getNombre()) 
        + " " + 
        null2empty(docNotarial.getNotarioAutorizante().getApellidos());
		
		String nombreSp = null2empty(contribuyente.getDatospersonales().getNombre()) 
		+ " " + 
		null2empty(contribuyente.getDatospersonales().getApellido1RAZONSOCIAL()) 
		+ " " + 
		null2empty(contribuyente.getDatospersonales().getApellido2());						
		return  doc.imprimirReciboPresentacion(finca.getReferenciaCatastral(), 
							finca.getCodMunicipio(), 
						    docNotarial.getNotarioAutorizante().getCodigo(), 
						    nombreNotario, 
						    docNotarial.getProtocolo().getNumero()+" "+ (docNotarial.getProtocolo().getNumerobis()==null?"":docNotarial.getProtocolo().getNumerobis()),
						    docNotarial.getProtocolo().getFechaautorizacion(),
						    contribuyente.getDatospersonales().getNumerodocumento(), 
						    nombreSp, 
						    String.valueOf(liq.getImporte().getImportetotal()).replace(".",","), 
						    pres.getNumeroJustificante().getNumeroJustificante()); 
	}
	
	/**
	 * Recupera una copia del justificante de presentación de Tributas
	 * @param pres 
	 * @return
	 * @throws PlusvaliaException
	 */
	public byte[] imprimirJustificantePresentacion(PresentacionLiquidacion pres) throws PlusvaliaException{
		return  doc.getJustificantePresentacion(pres.getNumeroJustificante().getNumeroJustificante(), pres.getBd());
	}
}
