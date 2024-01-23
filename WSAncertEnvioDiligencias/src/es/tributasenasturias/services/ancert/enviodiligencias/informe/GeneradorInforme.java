package es.tributasenasturias.services.ancert.enviodiligencias.informe;

import es.tributasenasturias.services.ancert.enviodiligencias.SystemException;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosInforme;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.DocumentoDoin;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.DocumentosFactory;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.JustificantePresentacion;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.TipoDoc;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.PDFUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogAplicacion;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogFactory;

/**
 * Implementa la generación de informe.
 * @author crubencvs
 *
 */
public class GeneradorInforme implements IContextReader{
	
	private CallContext context;
	protected GeneradorInforme()
	{
		super();
	}
	private boolean documentoGenerado;
	/**
	 * Genera un nuevo PDF en base a un número de autoliquidación, o bien lo recupera de base de datos.
	 * Si lo genera se puede conocer llamando a la función <b>seHaGeneradoDocumento</b> de la misma clase
	 * @param numAutoliquidacion Número de autoliquidación
	 * @param datos Datos para generar el informe.
	 * @return Cadena con el PDF generado o recuperado en base 64.
	 * @throws InformeException
	 */
	public String  getPDFDocument(String numAutoliquidacion, DatosInforme datos) throws InformeException,SystemException{
		LogAplicacion log = LogFactory.getLogAplicacionContexto(context);
		String pdfJustificantePre="";
		try{
		DocumentoDoin docBD = DocumentosFactory.newDocumentoDoin(context,numAutoliquidacion);
		docBD.recuperarDocumento(TipoDoc.PRESENTACION);
		if (!docBD.isGenerado())
		{
			JustificantePresentacion oJustificantePre = DocumentosFactory.newJustificantePresentacion(context,numAutoliquidacion,datos);
			pdfJustificantePre = PDFUtils.generarPdf(oJustificantePre);
			documentoGenerado=true;
			log.info("====Salida: se ha generado el justificante de presentación.");
		}
		else
		{
			pdfJustificantePre=docBD.getDocumento();
			documentoGenerado=false;
			log.info("====Salida: el justificante de presentación ya estaba dado de alta, no se genera.");
		}
		}
		catch (Exception ex)

		{
			throw new SystemException ("Error al generar el informe justificante de presentación:" + ex.getMessage(),ex);
		}
		return pdfJustificantePre;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}
	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
	/**
	 * Indica si el pdf que devuelve la llamada anterior a la función <b>getPDFDocument</b> se ha
	 * generado o recuperado de la base de datos.
	 * @return true si se ha generado un documento, false si no.
	 */
	public boolean seHaGeneradoDocumento() {
		return documentoGenerado;
	}
}
