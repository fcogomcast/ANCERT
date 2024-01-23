package es.tributasenasturias.fichas.datos;


import es.tributasenasturias.fichas.Constantes;
import es.tributasenasturias.fichas.comprimido.Base64;
import es.tributasenasturias.fichas.excepciones.AccesoDatosException;
import es.tributasenasturias.fichas.preferencias.Preferencias;
import es.tributasenasturias.fichas.soap.SoapClientHandler;
import es.tributasenasturias.lanzador.LanzadorException;
import es.tributasenasturias.lanzador.LanzadorFactory;
import es.tributasenasturias.lanzador.ParamType;
import es.tributasenasturias.lanzador.ProcedimientoAlmacenado;
import es.tributasenasturias.lanzador.TLanzador;
import es.tributasenasturias.lanzador.response.RespuestaLanzador;

public class GestorAccesoDatos {

	private Preferencias preferencias;
	private String idSesion;
	
	public static class ResultadoInsercionFicha {
		private boolean error;
		private String mensajeError;
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public String getMensajeError() {
			return mensajeError;
		}
		public void setMensajeError(String mensajeError) {
			this.mensajeError = mensajeError;
		}
		
	}
	public GestorAccesoDatos(Preferencias pref, String idSesion){
		this.preferencias= pref;
		this.idSesion= idSesion;
	}
	
	public ResultadoInsercionFicha insertarFicha(String codNotario,
			                  String codNotaria,
			                  String numProtocolo,
			                  String protocoloBis,
			                  String fechaAutorizacion,
			                  String nombrePdf,
			                  String nombreXml,
			                  String contenidoPdf,
			                  String contenidoXml,
			                  String origen,
			                  String codAyto,
			                  String autorizacionEnvioDiligencias) throws AccesoDatosException{
		try {
			TLanzador lanzador = LanzadorFactory.newTLanzador(preferencias.getEndpointLanzador(), new SoapClientHandler(this.idSesion));
			ProcedimientoAlmacenado pa = new ProcedimientoAlmacenado(preferencias.getPAGrabarFicha(), preferencias.getEsquemaBD());
			pa.param(codNotario, ParamType.CADENA);
			pa.param(codNotaria, ParamType.CADENA);
			pa.param(numProtocolo, ParamType.CADENA);
			pa.param(protocoloBis, ParamType.CADENA);
			pa.param(fechaAutorizacion, ParamType.FECHA,"DD-MM-YYYY");
			pa.param(nombrePdf, ParamType.CADENA);
			pa.param(nombreXml, ParamType.CADENA);
			pa.param(contenidoPdf, ParamType.CLOB);
			//pa.param(contenidoXml, ParamType.CLOB); 
			pa.param(new String(Base64.encode(contenidoXml.getBytes(Constantes.CODIFICACION))), ParamType.CLOB);
			pa.param(codAyto, ParamType.CADENA);
			pa.param(origen, ParamType.CADENA);
			if (autorizacionEnvioDiligencias!=null) {
				pa.param(autorizacionEnvioDiligencias,ParamType.CADENA);
			}
			else
			{
				pa.param("",ParamType.CADENA);
			}
			pa.param("P", ParamType.CADENA);
			String xml = lanzador.ejecutar(pa);
			ResultadoInsercionFicha rif = new ResultadoInsercionFicha();
			RespuestaLanzador rl = new RespuestaLanzador(xml);
			if (!rl.esErronea()) {
				String respuesta=rl.getValue("CADE_CADENA", 1, "STRING_CADE");
				if ("00".equals(respuesta)){
					rif.setError(false);
				}
				else{
					rif.setError(true);
					rif.setMensajeError(respuesta);
				}
			} else {
				rif.setError(true);
				rif.setMensajeError(rl.getTextoError());
			}
			return rif;
		}
		 catch (LanzadorException le){
			 throw new AccesoDatosException(le.getMessage(), le);
		 }
	}
	
}
