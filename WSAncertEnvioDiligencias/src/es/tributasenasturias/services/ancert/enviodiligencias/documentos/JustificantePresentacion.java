package es.tributasenasturias.services.ancert.enviodiligencias.documentos;

import java.io.OutputStream;
import java.rmi.RemoteException;


import es.tributasenasturias.services.ancert.enviodiligencias.SystemException;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosExpedienteDO;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosInforme;
import es.tributasenasturias.services.ancert.enviodiligencias.bd.DatosPersonaDO;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.DatosSalidaImpresa;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.NumberUtil;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.PdfBase;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils.Utils;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogAplicacion;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogFactory;



/**
 * Transforma los datos del justificante en un PDF.
 * @author crubencvs
 *
 */
public class JustificantePresentacion extends PdfBase implements IContextReader{

	
	private String numeroAutoliquidacion = new String();
	private String codVerificacion = "";
	private String docCumplimentado = "";
	private CallContext context;
	private DatosInforme datos;
	LogAplicacion log;
	protected JustificantePresentacion(CallContext context,String p_numAutoliq, DatosInforme datos) throws Exception{
		Session.put("cgestor", "");
		plantilla = "recursos//impresos//xml//justificantePresentacion.xml";
		this.numeroAutoliquidacion = p_numAutoliq;
		this.datos=datos;
		log = LogFactory.getLogAplicacionContexto(context);
	}

	public String getPlantilla() {
		return plantilla;
	}

	public void compila(String id, String xml, String xsl, OutputStream output)
			throws RemoteException {
		try {
			justificantePresentacion(id, xml, xsl, output, this.datos);
		} catch (Exception e) {
			throw new RemoteException ("Excepción al compilar el justificante de presentación:" + e.getMessage(),e);
		}

	}

	public void justificantePresentacion(String id, String xml, String xsl,
			OutputStream output, DatosInforme datos) throws DocumentoException,SystemException{
		DatosSalidaImpresa s = new DatosSalidaImpresa(xml, xsl, output);
			try {
				//Se recupera el expediente.
				DatosExpedienteDO datosExpediente = datos.getDatosJustificante().getDatosExpediente();
				if (datosExpediente!=null && !datos.isVacio()) //No debería serlo, esto ya se comprueba antes.
				{
					// Oficina de alta
					s.Campo("Texto103_2", datosExpediente.getOficinaAlta());
					// Organismo
					s.Campo("Texto101_2", datosExpediente.getOrganismo());
					// Nº Expediente
					s.Campo("Texto70_2", datosExpediente.getNumExpediente());
					// Fecha de alta
					s.Campo("Texto67_2", datosExpediente.getFechaAlta());
					// Oficina de destino
					s.Campo("Texto10_2", datosExpediente.getOficinaDestino());
					// Hora de alta
					s.Campo("Texto160_2", datosExpediente.getHoraAlta());
					// Fecha de presentación
					s.Campo("tFechaPresent_2", datosExpediente.getFechaPresentacion());
	
					// Tipo/subtipo de expediente
					s.Campo("Texto4_2", datosExpediente.getTipoExpediente());
					s.Campo("Texto6_2", datosExpediente.getSubtipoExpediente());
	
					s.Campo("tnumDOCORI_2", datosExpediente.getNumeroProtocolo());
					s.Campo("tnombrenotDOCORI_2", datosExpediente.getApellidosNombreNotario());
					s.Campo("ttipoDOCORI_2", datosExpediente.getTipoDocumento());
					s.Campo("tfechaprotDOCORI_2", datosExpediente.getFechaDocumento());
	
					s.Campo("concepto", datosExpediente.getConcepto());
					//Datos de personas.
					/*int numPersonas = datos.getDatosJustificante().getPersonas().size();
					
					for (int i = 0; i < numPersonas; i++) {
						DatosPersonaDO persona= datos.getDatosJustificante().getPersonas().get(i);
						if (persona.getTipoPersona().equalsIgnoreCase("1")) {
							s.Campo("Texto20_2", persona.getNombre());
							s.Campo("Texto26_2", persona.getNif());
							s.Campo("Texto29_2", persona.getTelefono());
							s.Campo("Texto23_2", persona.getSigla()
									+ " "
									+ persona.getCalle()
									+ ". "
									+ persona.getCodPostal()
									+ " - "
									+ persona.getPoblacion()
									+ ", "
									+ persona.getProvincia()
									+ ". ");
						}
						if (persona.getTipoPersona().equalsIgnoreCase("2")) {
	
							s.Campo("tetiSP", "SUJETO PASIVO");
							s.Campo("tnomSP_2", persona.getNombre());
							s.Campo("tnifSP_2", persona.getNif());
						}
					}*/
					DatosPersonaDO presentador= datos.getDatosJustificante().getDatosPresentador();
					s.Campo("Texto20_2", presentador.getNombre());
					s.Campo("Texto26_2", presentador.getNif());
					s.Campo("Texto29_2", presentador.getTelefono());
					s.Campo("Texto23_2", presentador.getSigla()
							+ " "
							+ presentador.getCalle()
							+ ". "
							+ presentador.getCodPostal()
							+ " - "
							+ presentador.getPoblacion()
							+ ", "
							+ presentador.getProvincia()
							+ ". ");
					DatosPersonaDO sujetoPasivo = datos.getDatosJustificante().getDatosSujetoPasivo();
					String modelo = numeroAutoliquidacion.substring(0, 3);
					if ("650".equals(modelo))
					{
						s.Campo("tetiSP", "CAUSANTE");
					}
					else if ("651".equals(modelo))
					{
						s.Campo("tetiSP", "DONANTE");
					}
					else
					{
						s.Campo("tetiSP", "SUJETO PASIVO");
					}
					s.Campo("tnomSP_2", sujetoPasivo.getNombre());
					s.Campo("tnifSP_2", sujetoPasivo.getNif());
	
					s.Campo("autoliq1", this.numeroAutoliquidacion);
					if (!"600".equals(modelo))
					{
						s.BorrarCampo("bloqueIdBien");
					}
					
					String tipoSujecion =datosExpediente.getCodTipoSujecion();				
					
					if (tipoSujecion.equalsIgnoreCase("S")) {
										
						s.Campo("tipoautoliq1", NumberUtil.getImporteFormateado(datosExpediente.getImporteAutoliq())+ " €");								
					} else {
							s.Campo("tipoautoliq1", datosExpediente.getTipoSujecion());
					}
	
					String textoTitulo = datos.getDatosJustificante().getTextoTitulo();
	
					String texto = datos.getDatosJustificante().getTextoFijo();
					
					
					s.Campo("titulopagina2", textoTitulo);
	
					s.Campo("textoPpal", texto.replace("?","€"));
	
					s.Campo("jefe", "JEFE DEL DEPARTAMENTO DE IMPUESTOS PATRIMONIALES");
					s.Campo("firma", "Benigno López Aparicio");
					this.codVerificacion = datos.getCodigoVerificacion();
					s.Campo("TextoVeri", "C" + this.numeroAutoliquidacion+"-"+this.codVerificacion);
				}
				else
				{
					//Esto no es normal, se tendría que haber comprobado antes. Se lanza una excepción.
					throw new DocumentoException ("Error al componer xml de formato en justificante de presentación. No existen datos para pintar el informe.",this,"justificantePresentacion");
				}
			} catch (Exception e) {
				if (!(e instanceof DocumentoException) && !(e instanceof SystemException))
				throw new SystemException("Error al componer el xml de formato en  justificante de presentacion: "
								+ e.getMessage(),e,this,"justificantePresentacion");
			}
			s.Mostrar();
			this.docCumplimentado =  Utils.newInstance(context).DOM2String(s.getXmlDatos());
	}

	public String getDocXml() {
		return this.docCumplimentado;
	}

	public String getCodVerificacion() {
		return this.codVerificacion;
	}

		@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context= ctx;
		
	}
}
