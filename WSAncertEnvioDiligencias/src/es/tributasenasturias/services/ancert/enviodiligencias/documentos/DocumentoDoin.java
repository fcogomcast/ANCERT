package es.tributasenasturias.services.ancert.enviodiligencias.documentos;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.tributasenasturias.services.ancert.enviodiligencias.SystemException;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.ConversorParametrosLanzador;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.Utils;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.Base64;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.PdfComprimidoUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.XMLUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.lanzador.LanzaPL;
import es.tributasenasturias.services.lanzador.LanzaPLService;



/**
 * Implementa una utilidad para recuperar un documento de la base de datos, de esta forma se podrá 
 * comprobar si ya está generado y traerlo en lugar de generarlo de nuevo.
 * @author crubencvs
 *
 */
public class DocumentoDoin implements IContextReader{

	private String numeroAutoliquidacion = new String();
	private String docRecuperado = "";
	private boolean generado=false;
	private ConversorParametrosLanzador cpl;
	
	private Preferencias pref;
	private CallContext context;
	
	protected DocumentoDoin(String p_numAutoliq) throws SystemException{
		try
		{
			pref=PreferenciasFactory.newInstance();
			this.numeroAutoliquidacion = p_numAutoliq;
		}
		catch (Exception ex)
		{
			throw new SystemException ("Error al recuperar las preferencias:"+ex.getMessage(),ex,this,"Constructor DocumentoDoin");
		}
			
	}
	
	public String getDocumento()
	{
		return docRecuperado;
	}
	public boolean isGenerado()
	{
		return generado;
	}
	
	public void recuperarDocumento(TipoDoc tipoDoc) throws DocumentoException,SystemException
	{
		String numAutoliquidacion = this.numeroAutoliquidacion;
		recuperarDocumentoInsertado(tipoDoc,numAutoliquidacion);
		String docRetorna="";
		if (!cpl.getResultado().equals(null)) {
			// una vez obtenidos los datos y comprobado que la respuesta no es nula
			// se debe inluir el xml con los datos en la base de datos zipped
			// y se pinta el pdf.
				//Debería haber recuperado el resultado.
				try {
					Document docRespuesta = (Document) XMLUtils.compilaXMLObject(cpl.getResultado(), null);
					//Comprobamos si hay error. En ese caso, no habrá documento.
					Element[] rsError = XMLUtils.selectNodes(docRespuesta, "//estructuras/error");
					try {
						if (rsError.length==0) //OK
						{   
							//Recuperamos el documento.
							//Si no termina como "00" o "01", es un error.
							String codError=XMLUtils.selectSingleNode(docRespuesta, "//estruc[@nombre='CADE_CADENA']/fila[1]/STRING_CADE").getTextContent();
							if (!codError.equals("00") && !codError.equals("01"))
							{
								//Error al recuperar el documento.
								throw new DocumentoException ("Imposible comprobar si el documento ya estaba generado.");
								
							}
							else if (codError.equals("01"))
							{
								//No existe, se vuelve.
								this.generado=false;
							}
							else
							{
								Element[] rsDoc = XMLUtils.selectNodes(docRespuesta, "//estruc[@nombre='PDF_PDF']/fila");
								if (rsDoc.length==0) //Dice que hay documento, pero no lo ha recuperado. Es un error.
								{
									throw new DocumentoException ("A pesar de que existe documento en la base de datos, no se ha traído de la base de datos por motivos no conocidos.");
								}
								String doc=XMLUtils.selectSingleNode(rsDoc[0], "pdf").getTextContent();//Sólo esperamos uno, si hay más no importan.
								//Se decodifica el documento. 
								if (necesitaDecodificacion(tipoDoc))
								{
									String documentzippeado = doc;
									ByteArrayOutputStream resulByteArray = new ByteArrayOutputStream();
									try
									{
										resulByteArray = PdfComprimidoUtils.descomprimirPDF(documentzippeado);
									}
									catch (Exception e)
									{
										throw new SystemException ("Error al descomprimir el PDF:"+e.getMessage(),e);
									}
									docRetorna = new String(Base64.encode(resulByteArray.toByteArray()));
								}
								else
								{
									docRetorna=doc;
								}
								this.generado=true;
							}
						}
						else // Parece que hay errores
						{
							//Si hay errores, logeamos el resultado y lanzamos la excepción.
							throw new DocumentoException ("La recuperación del documento de la base de datos ha terminado con error:" + XMLUtils.selectSingleNode(docRespuesta, "//estructuras/error").getTextContent(),this,"recuperarDocumento");
						}
					} catch (DOMException e) {
						throw new SystemException ("Error en XML al recuperar el documento de la base de datos:" + e.getMessage(),e);
						
					}
				} catch (RemoteException e) {
					//Esto puede ser por varios motivos, por ejemplo que no haya devuelto XML, como en
					//"No ejecutado ok".
					//En este caso, se genera un nuevo error. 
					throw new SystemException ("Error general al recuperar el documento de la base de datos:" + e.getMessage(),e);
					
				}
		}
		this.docRecuperado=docRetorna;
	}
	
	private void recuperarDocumentoInsertado (TipoDoc tipoDoc,String numAutoliquidacion) throws DocumentoException,SystemException{
		String tipo="";
		String extension="";
		switch (tipoDoc)
		{
		case PAGO:
			tipo="P";
			extension="PDF";
			break;
		case XML:
			tipo="M";
			extension="XML";
			break;
		case COMPARECENCIA:
			tipo="J";
			extension="PDF";
			break;
		case PRESENTACION:
			tipo="C";
			extension="PDF";
			break;
		case MODELO:
			tipo="M";
			extension="PDF";
			break;
		}
		// llamar al servicio PXLanzador para ejecutar el procedimiento almacenado de recuperación
		// de documento ya insertado.
		try
		{
		cpl = new ConversorParametrosLanzador();

		cpl.setProcedimientoAlmacenado(pref.getPAConsultaDocumento());
        // Nombre
        cpl.setParametro(numAutoliquidacion,ConversorParametrosLanzador.TIPOS.String);
        // Tipo 
        cpl.setParametro(tipo,ConversorParametrosLanzador.TIPOS.String);
        // Extensión. 
        cpl.setParametro(extension,ConversorParametrosLanzador.TIPOS.String);
        // conexion oracle
        cpl.setParametro("P",ConversorParametrosLanzador.TIPOS.String);            
        
        
        LanzaPLService lanzaderaWS = new LanzaPLService();
        LanzaPL lanzaderaPort;
		lanzaderaPort =lanzaderaWS.getLanzaPLSoapPort();
        
		// enlazador de protocolo para el servicio.
		javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; 
		// Cambiamos el endpoint
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndPointLanzador()); 
        Utils.setHandlerChain(bpr, context);
        String respuesta = new String();
    	respuesta = lanzaderaPort.executePL(pref.getEsquemaBaseDatos(), cpl.codifica(), "", "", "", "");
    	cpl.setResultado(respuesta);
    	if (!cpl.getError().equals(""))
    	{
    		throw new DocumentoException ("Error recibido de la base de datos al recuperar el documento insertado:"+cpl.getError());
    	}
		}
		catch (ParserConfigurationException ex)
		{
			throw new SystemException ("Error al recuperar el documento insertado:" + ex.getMessage(),ex);
		}
	}
	private boolean necesitaDecodificacion(TipoDoc tipoDoc)
	{
		boolean decodifica=false;
		switch (tipoDoc)
		{
		case PAGO:
			decodifica=true;
			break;
		case XML:
			decodifica=false;
			break;
		case COMPARECENCIA:
			decodifica=true;
			break;
		case PRESENTACION:
			decodifica=true;
			break;
		case MODELO:
			decodifica=true;
			break;
		}
		return decodifica;
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context=ctx;
	}
}
