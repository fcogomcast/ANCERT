package es.tributasenasturias.servicios.ibi.deuda.mensajes;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.tributasenasturias.servicios.ibi.deuda.CABECERAType;
import es.tributasenasturias.servicios.ibi.deuda.CONSULTADEUDAS;
import es.tributasenasturias.servicios.ibi.deuda.LISTADEUDASType;
import es.tributasenasturias.servicios.ibi.deuda.MESSAGERESPONSE;
import es.tributasenasturias.servicios.ibi.deuda.RESULTADOType;
import es.tributasenasturias.servicios.ibi.deuda.CONSULTADEUDAS.REPLY;
import es.tributasenasturias.servicios.ibi.deuda.CONSULTADEUDAS.REPLY.DATOS;
import es.tributasenasturias.servicios.ibi.deuda.CONSULTADEUDAS.REPLY.DATOS.IDINMUEBLE;
import es.tributasenasturias.servicios.ibi.deuda.CONSULTADEUDAS.REPLY.DATOS.IDINMUEBLE.UBICACION;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;
import es.tributasenasturias.servicios.ibi.deuda.util.Base64;
import es.tributasenasturias.services.lanzador.client.response.EstructuraLanzador;
import es.tributasenasturias.services.lanzador.client.response.FilaLanzador;
import es.tributasenasturias.services.lanzador.client.response.RespuestaLanzador;

/**
 * Construye el mensaje de respuesta de la consulta.
 * @author crubencvs
 *
 */
public class ConstructorMensajeRespuestaConsulta {

	/**
	 * Protegido, sólo se debería usar desde el factory
	 */
	protected ConstructorMensajeRespuestaConsulta()
	{
		
	}
	
	private CABECERAType construirCabecera(String idComunicacion, String codOper)
	{
		CABECERAType cabecera = new CABECERAType();
		Date ahora = new Date();
		cabecera.setAPLICACION("NOCD");
		cabecera.setEMISOR("PDA");
		cabecera.setRECEPTOR("CGN");
		cabecera.setIDCOMUNICACION(Long.parseLong(idComunicacion));
		cabecera.setTIPO("2");
		cabecera.setCODOPER(codOper);
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		cabecera.setFECHA(sd.format(ahora));
		sd = new SimpleDateFormat ("HH:mm:ss");
		cabecera.setHORA(sd.format(ahora));
		return cabecera;
	}
	/**
	 * Construye el mensaje de respuesta de la consulta
	 * @param datosBD Datos devueltos por base de datos para la consulta de deuda de Ibi 
	 * @param documento Cadena en base64 del documento.
	 * @return Respuesta del servicio.
	 * @throws IBIException
	 */
	public MESSAGERESPONSE construirMensajeRespuesta(RespuestaLanzador datosBD, String documento, String idComunicacion, String codOper) throws IBIException
	{
		//TODO: es un poquito cutre, lo hacemos todo de una vez. Quizá haya que reformatear el código.
		EstructuraLanzador pedb = datosBD.getEstructura("PEDB_PERSONA_DATOS_BASICOS");
		EstructuraLanzador rece = datosBD.getEstructura("RECE_RECI_EXPED");
		if (pedb==null|| rece==null)
		{
			throw new IBIException("Error, no se han recuperado de BD los datos necesarios para la respuesta a consulta.");
		}
		FilaLanzador fila = pedb.getFila(1);
		MESSAGERESPONSE response = new MESSAGERESPONSE();
		CONSULTADEUDAS consulta = new CONSULTADEUDAS();
		REPLY reply = new REPLY();
		reply.setID("REPLY");
		CABECERAType cabecera= construirCabecera(idComunicacion, codOper);
		reply.setCABECERA(cabecera);
		//Datos
		DATOS datos = new DATOS();
		IDINMUEBLE inmueble = new IDINMUEBLE();
		UBICACION ubicacion = new UBICACION();
		LISTADEUDASType listaDeudas = new LISTADEUDASType();
		String tipoCalle = fila.getCampo("SIGLA_PEDB");
		if (tipoCalle==null || "".equals(tipoCalle))
		{
			tipoCalle="CL";
		}
		ubicacion.setTIPOCALLE(tipoCalle);
		String calle = fila.getCampo("NOMBRE_CALLE_PEDB");
		if (calle==null || "".equals(calle))
		{
			calle="NO DISPONIBLE";
		}
		ubicacion.setCALLE(calle);
		ubicacion.setNUMERO(fila.getCampo("NUM2_PEDB"));
		ubicacion.setESCALERA(fila.getCampo("ESCALERA_PEDB"));
		ubicacion.setPISO(fila.getCampo("PLANTA_PEDB"));
		ubicacion.setPUERTA(fila.getCampo("PUERTA_PEDB"));
		double deuda;
		try{
		deuda = Double.parseDouble(fila.getCampo("NUM_PEDB"))/100;
		}
		catch (NumberFormatException e)
		{
			throw new IBIException ("La deuda total calculada no está expresada correctamente como un número. Deuda recibida:"+fila.getCampo("NUM_PEDB"));
		}
		NumberFormat nf = NumberFormat.getInstance(new Locale("es","ES"));//Símbolos de decimal, etc, de España.
		listaDeudas.setDEUDATOTAL(nf.format(deuda));

		//Asignar ubicacion
		String tipoFinca = fila.getCampo("DUP2_PEDB");
		if (tipoFinca==null || "".equals(tipoFinca))
		{
			throw new IBIException ("Error, no se ha recuperado el dato obligatorio TIPO_FINCA.");
		}
		inmueble.setTIPOFINCA(tipoFinca);
		String superficie = fila.getCampo("COD_VIA_PEDB");
		if (superficie==null || "".equals(superficie))
		{
			throw new IBIException ("Error, no se ha recuperado el dato obligatorio SUPERFICIE.");
		}
		inmueble.setSUPERFICIE(superficie);
		inmueble.setUBICACION(ubicacion);
		//Asignar inmueble.
		datos.setIDINMUEBLE(inmueble);
		datos.setLISTADEUDAS(listaDeudas);
		String ibiAnio="N";
		try
		{
			double pendiente=Double.parseDouble(fila.getCampo("PEND_VOL_PEDB"));
			if (pendiente >0)
			{
				ibiAnio="S";
			}
		}
		catch (NumberFormatException e)
		{
			throw new IBIException ("No se ha podido determinar si se encuentra en campaña de IBI del año actual. Recibido:"+fila.getCampo("PEND_VOL_PEDB"));
		}
		datos.setIBIANYOACTUAL(ibiAnio);
		//Liquidable?
		//Aplazado o suspendido o deuda 0
		if ("1".equals(fila.getCampo("REPRESENTANTE_PEDB")))
		{
			datos.setDEUDALIQUIDABLE(true);
		}
		else
		{
			datos.setDEUDALIQUIDABLE(false);
		}
		//Por el tipo de campo definido en salida, DOCUMENTOJUSTIFICANTE convierte lo que se le pasa a base64,
		//así que hay que pasarle el documento en bytes.
		//Que ya son ganas de hacer dos veces lo mismo.
		datos.setDOCUMENTOJUSTIFICANTE(Base64.decode(documento.toCharArray()));//No importa la codificación, porque viene en Base64 y no hay caracteres que no se puedan traducir.
		RESULTADOType resultado = new RESULTADOType();
		resultado.setRETURNCODE("0"); //OK
		reply.setDATOS(datos);
		reply.setRESULTADO(resultado);
		consulta.setREPLY(reply);
		response.setCONSULTADEUDAS(consulta);
		
		return response;
	}
	/**
	 * Construye un mensaje de error.
	 * @param codError Código del error.
	 * @param descError Descripción del error.
	 * @return
	 */
	public MESSAGERESPONSE construirMensajeError (String codError, String descError, String idComunicacion, String codOper)
	{
		MESSAGERESPONSE response = new MESSAGERESPONSE();
		REPLY reply = new REPLY();
		CABECERAType cabecera= construirCabecera(idComunicacion, codOper);
		reply.setID("REPLY");
		reply.setCABECERA(cabecera);
		RESULTADOType resultado = new RESULTADOType();
		resultado.setRETURNCODE("1");
		resultado.setCODERROR(codError);
		resultado.setDESCERROR(descError);
		reply.setRESULTADO(resultado);
		response.setCONSULTADEUDAS(new CONSULTADEUDAS());
		response.getCONSULTADEUDAS().setREPLY(reply);
		return response;
	}
}
