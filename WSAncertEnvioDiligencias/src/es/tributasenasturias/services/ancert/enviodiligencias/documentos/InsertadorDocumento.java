package es.tributasenasturias.services.ancert.enviodiligencias.documentos;

import java.io.ByteArrayOutputStream; //import java.io.ByteArrayInputStream;

import es.tributasenasturias.services.ancert.enviodiligencias.SystemException;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.ConversorParametrosLanzador;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosInforme;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosPersonaDO;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.externa.Utils;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.Base64;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.PdfComprimidoUtils;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.Preferencias;
import es.tributasenasturias.services.ancert.enviodiligencias.preferencias.PreferenciasFactory;
import es.tributasenasturias.services.lanzador.LanzaPL;
import es.tributasenasturias.services.lanzador.LanzaPLService;

//import es.tributasenasturias.documentos.util.Base64;


public class InsertadorDocumento implements IContextReader{

	
	private CallContext context;

	//Log

	protected InsertadorDocumento(){
		super();
	};

	public String altaDocumento(String doc,DatosInforme dat
			) throws DocumentoException,SystemException{
		// Llamar al lanzador para que llame al procedimiento almacenado
		try {
			Preferencias pref = PreferenciasFactory.newInstance();
			pref.cargarPreferencias();
			ConversorParametrosLanzador cpl;
			cpl = new ConversorParametrosLanzador();			
			cpl.setProcedimientoAlmacenado(pref.getpAAltaDocumento());
			DatosPersonaDO sujetoPasivo=null;
			DatosPersonaDO presentador=null;
			//Recuperamos datos del sujeto pasivo. Este es el que tiene "Tipo Persona" a "2".
			//El presentador tiene el tipo a "1".
			for (DatosPersonaDO per:dat.getDatosJustificante().getPersonas())
			{
				if ("1".equals(per.getTipoPersona()))
				{
					presentador=per;
				}
				else if ("2".equals(per.getTipoPersona()))
				{
					sujetoPasivo=per;
				}
			}
			// tipo
			cpl.setParametro("C", ConversorParametrosLanzador.TIPOS.String);
			// nombre
			cpl.setParametro(dat.getDatosJustificante().getDatosExpediente().getNumeroAutoliquidacion(),
							ConversorParametrosLanzador.TIPOS.String);
			// codigo verificacion
			cpl.setParametro(dat.getCodigoVerificacion(),
					ConversorParametrosLanzador.TIPOS.String);
			// sp
			if (sujetoPasivo!=null)
			{
				cpl.setParametro(sujetoPasivo.getNif(), ConversorParametrosLanzador.TIPOS.String);
			}
			else
			{
				cpl.setParametro("", ConversorParametrosLanzador.TIPOS.String);
			}
			// presentador
			if (presentador!=null)
			{
				cpl.setParametro(presentador.getNif(), ConversorParametrosLanzador.TIPOS.String);
			}
			else
			{
				cpl.setParametro("", ConversorParametrosLanzador.TIPOS.String);
			}
			// sesion
			cpl.setParametro("", ConversorParametrosLanzador.TIPOS.String);
			// origen
			cpl.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
			// libre
			cpl.setParametro("", ConversorParametrosLanzador.TIPOS.String);
			// publicable = 'S'
			cpl.setParametro("S", ConversorParametrosLanzador.TIPOS.String);

			// Decodificamos documento, el justificante de presentación siempre vendrá codificado.
			byte[] docDecodificado = Base64.decode(doc.toCharArray());
			String documentzippeado = new String();
			ByteArrayOutputStream resulByteArray = new ByteArrayOutputStream();
			resulByteArray.write(docDecodificado);
			documentzippeado = PdfComprimidoUtils
					.comprimirPDF(resulByteArray);
			
			cpl.setParametro(documentzippeado,
					ConversorParametrosLanzador.TIPOS.Clob);

			cpl.setParametro("PDF",
					ConversorParametrosLanzador.TIPOS.String);
			// conoracle = 'P'
			cpl.setParametro("P", ConversorParametrosLanzador.TIPOS.String);

			LanzaPLService lanzaderaWS = new LanzaPLService();
			LanzaPL lanzaderaPort;

			lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();
			// enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
			// Cambiamos el endpoint
			bpr.getRequestContext().put(
					javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					pref.getEndPointLanzador());
			Utils.setHandlerChain(bpr, context);
			String respuesta = new String();
			respuesta = lanzaderaPort.executePL(pref.getEsquemaBaseDatos(), cpl.codifica(), "", "", "", "");
			cpl.setResultado(respuesta);
			if (!cpl.getError().equals(""))
			{
				throw new DocumentoException ("Excepción en el alta de documento:" + cpl.getError(),this,"altaDocumento");
			}
		} catch (Exception e) {
			if (!(e instanceof DocumentoException) && !(e instanceof SystemException))
			{
				throw new SystemException ("Excepción en el alta de documento:" + e.getMessage(),e,this,"altaDocumento");
			}
		}
		return "ok";
	}
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context = ctx;
	}
}