package es.stpa.plusvalias.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.stpa.plusvalias.datos.MediadorBD;
import es.stpa.plusvalias.documentos.MediadorWSDocumentos;
import es.stpa.plusvalias.domain.CalculadoraLiquidacion.LiquidacionCalculada;
import es.stpa.plusvalias.exceptions.PlusvaliaException;
import es.stpa.plusvalias.presentacion.PresentacionLiquidacion;

/**
 * Permite recuperar los documentos de impresión del cálculo o la presentación
 * @author crubencvs
 *
 */
public class DirectorImpresion {

	private MediadorWSDocumentos doc;
	
	public DirectorImpresion(MediadorWSDocumentos doc){
		this.doc= doc;
	}
	/**
	 * Realiza la impresión de los justificantes de Cálculo
	 * @throws PlusvaliaException
	 */
	public Map<LiquidacionContribuyente, byte[]> imprimirJustificantes(LiquidacionCalculada liq, Finca finca,DocumentoNotarial docNotarial) throws PlusvaliaException{
		if (liq==null){
			return new HashMap <LiquidacionContribuyente, byte[]>();
		}
		Map<LiquidacionContribuyente, byte[]> impresos= new HashMap<LiquidacionContribuyente, byte[]>();
		//Impresión de los justificantes
		ImpresorJustificante imp = new ImpresorJustificante(doc);
		for (LiquidacionContribuyente l: liq.liquidaciones){
			byte[] pdf= imp.imprimirJustificanteCalculo(l, finca, docNotarial);
			impresos.put(l, pdf);
		}
		return impresos;
	}
	
	/**
	 * Realiza la impresión de los justificantes de presentación
	 * @throws PlusvaliaException
	 */
	public Map<PresentacionLiquidacion, byte[]> imprimirJustificantesPresentacion(List<PresentacionLiquidacion> pres, MediadorBD bd) throws PlusvaliaException{
		if (pres==null){
			return new HashMap <PresentacionLiquidacion, byte[]>();
		}
		Map<PresentacionLiquidacion, byte[]> impresos= new HashMap<PresentacionLiquidacion, byte[]>();
		//Impresión de los justificantes
		ImpresorJustificante imp = new ImpresorJustificante(doc);
		for (PresentacionLiquidacion p: pres){
			byte[] pdf= imp.imprimirJustificantePresentacion(p);
			impresos.put(p, pdf);
		}
		return impresos;
	}
	
	/**
	 * Realiza la impresión de los justificantes de presentación
	 * @throws PlusvaliaException
	 */
	public Map<PresentacionLiquidacion, byte[]> imprimirReciboPresentacion(List<PresentacionLiquidacion> pres, Finca finca,DocumentoNotarial docNotarial) throws PlusvaliaException{
		if (pres==null){
			return new HashMap <PresentacionLiquidacion, byte[]>();
		}
		Map<PresentacionLiquidacion, byte[]> impresos= new HashMap<PresentacionLiquidacion, byte[]>();
		
		ImpresorJustificante imp = new ImpresorJustificante(doc);
		for (PresentacionLiquidacion p: pres){
			byte[] pdf= imp.imprimirReciboPresentacion(p, finca, docNotarial);
			impresos.put(p, pdf);
		}
		return impresos;
	}
	
	
}
