package org.notariado.inti.bd;



import org.notariado.inti.exceptions.BDException;
import org.notariado.inti.preferencias.Preferencias;
import org.notariado.inti.soap.SoapClientHandler;
import org.notariado.inti.utils.log.Logger;

import es.tributasenasturias.services.lanzador.client.LanzadorException;
import es.tributasenasturias.services.lanzador.client.LanzadorFactory;
import es.tributasenasturias.services.lanzador.client.ParamType;
import es.tributasenasturias.services.lanzador.client.ProcedimientoAlmacenado;
import es.tributasenasturias.services.lanzador.client.TLanzador;
import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;

/**
 * Gestiona las llamadas a base de datos.
 * @author crubencvs
 *
 */
public class GestorLlamadasBD {

	private static final String STRUCT_CANU = "CANU_CADENAS_NUMEROS";
	Preferencias pref;
	Logger log;
	String id;
	/**
	 * Constructor
	 * @param preferencias Objeto de Preferencias del servicio. El gestor tomará datos de él, como el esquema 
	 *                     al que enviar las llamadas, el endpoint de lanzador y los nombre de procedimiento almacenado.
	 * @param log Logger para poder escribir en log
	 * @param idSesion Identificador de la llamada actual.
	 */
	protected GestorLlamadasBD(Preferencias preferencias, Logger log,String idSesion)
	{
		pref = preferencias;
		this.log = log; 
		id = idSesion;
	}
	
	
	public ResultadosLlamadasBD.ResultadoImpresionJustificante imprimirJustificantePresentacion(String organismo,String codNotario, String codNotaria,
													String protocolo, String protocoloBis, String fechaAutorizacion,
													String idSubo) throws BDException
	{
		try
		{
		TLanzador lanzador = LanzadorFactory.newTLanzador(pref.getEndpointLanzador(),new SoapClientHandler(id));
		ProcedimientoAlmacenado proc = new ProcedimientoAlmacenado(pref.getProcImprimirJustificante(), pref.getEsquemaBD());
		//Organismo
		proc.param("33", ParamType.CADENA);
		//Notario
		proc.param(codNotario, ParamType.CADENA);
		//Notaría
		proc.param(codNotaria, ParamType.CADENA);
		//Protocolo
		proc.param(protocolo, ParamType.CADENA);
		//Protocolo Bis
		proc.param(protocoloBis, ParamType.CADENA);
		//Fecha autorización
		proc.param(fechaAutorizacion, ParamType.FECHA,"DD/MM/YYYY");
		//Id Subo
		proc.param(idSubo, ParamType.CADENA);
		String respuesta=lanzador.ejecutar(proc);
		RespuestaLanzador resp = new RespuestaLanzador(respuesta);
		if (resp.esErronea())
		{
			throw new BDException ("Error devuelto al imprimir el justificante de presentación:"+ resp.getTextoError());
		}
		//Si no es un fallo, se considera que ha acabado bien, no lo distinguimos de otra forma.
		//Si no hubiese creado realmente el reimprimible, posteriormente fallaría al recuperarlo.
		String idReimprimible = resp.getValue(STRUCT_CANU,1,"NUME1_CANU");
		String tipo = resp.getValue(STRUCT_CANU,1,"STRING1_CANU");
		String tipoDocumento = resp.getValue(STRUCT_CANU,1,"STRING2_CANU");
		String numJustificante = resp.getValue(STRUCT_CANU,1,"STRING3_CANU");
		String nifPresentador  =  resp.getValue(STRUCT_CANU,1, "STRING5_CANU");
		String mac = resp.getValue(STRUCT_CANU,1,"STRING4_CANU");
		return new ResultadosLlamadasBD.ResultadoImpresionJustificante(idReimprimible,
				  tipo, 
				  tipoDocumento,
				  numJustificante,
				  nifPresentador,
				  mac);
		}
		catch (LanzadorException e)
		{
			throw new BDException ("Error en acceso a base de datos:"+ e.getMessage(), e);
		}
		
	}
	
	
}
