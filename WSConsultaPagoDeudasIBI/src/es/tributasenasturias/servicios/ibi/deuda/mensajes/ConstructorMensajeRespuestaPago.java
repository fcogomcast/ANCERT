package es.tributasenasturias.servicios.ibi.deuda.mensajes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Base64;

import es.tributasenasturias.servicios.ibi.deuda.CABECERAType;
import es.tributasenasturias.servicios.ibi.deuda.MESSAGERESPONSE;
import es.tributasenasturias.servicios.ibi.deuda.PAGODEUDAS;
import es.tributasenasturias.servicios.ibi.deuda.RESULTADOType;
import es.tributasenasturias.servicios.ibi.deuda.PAGODEUDAS.REPLY;
import es.tributasenasturias.servicios.ibi.deuda.PAGODEUDAS.REPLY.DATOS;
import es.tributasenasturias.servicios.ibi.deuda.PAGODEUDAS.REPLY.DATOS.JUSTIFICANTEPAGO;
import es.tributasenasturias.servicios.ibi.deuda.exceptions.IBIException;

/**
 * Construye el mensaje de respuesta del pago.
 * @author crubencvs
 *
 */
public class ConstructorMensajeRespuestaPago {

	private static final String TIPO_RESPUESTA = "2";
	/**
	 * Protegido, sólo se debería usar desde el factory
	 */
	protected ConstructorMensajeRespuestaPago()
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
		cabecera.setTIPO(TIPO_RESPUESTA);
		cabecera.setCODOPER(codOper);
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		cabecera.setFECHA(sd.format(ahora));
		sd = new SimpleDateFormat ("HH:mm:ss");
		cabecera.setHORA(sd.format(ahora));
		return cabecera;
	}
	/**
	 * Construye el mensaje de respuesta del pago
	 * @param datosBD Datos devueltos por base de datos para la consulta de deuda de Ibi 
	 * @param documento Cadena en base64 del documento.
	 * @param idComunicacion Identificador de la comunicación con ANCERT
	 * @param codOper Código de la operación (identifica si es entrada/salida)
	 * @return Respuesta del servicio.
	 * @throws IBIException
	 */
	public MESSAGERESPONSE construirMensajeRespuesta(String idComunicacion, String codOper, String importe, String justPago, String numJustif,String nodeuda ) throws IBIException
	{
		MESSAGERESPONSE response = new MESSAGERESPONSE();
		PAGODEUDAS pago = new PAGODEUDAS();
		REPLY reply = new REPLY();
		reply.setID("REPLY");
		CABECERAType cabecera= construirCabecera(idComunicacion, codOper);
		reply.setCABECERA(cabecera);
		//Datos
		DATOS datos = new DATOS();
		JUSTIFICANTEPAGO just = new JUSTIFICANTEPAGO();
		just.setDOCUMENTO(Base64.getDecoder().decode(justPago));
		just.setNUMJUSTIFICANTEPAGO(numJustif);
		datos.setJUSTIFICANTEPAGO(just);
		datos.setDOCUMENTOJUSTSINDEUDAS(Base64.getDecoder().decode(nodeuda));
		datos.setIMPORTEPAGADO(importe);
		
		RESULTADOType resultado = new RESULTADOType();
		resultado.setRETURNCODE("0"); //OK
		reply.setDATOS(datos);
		reply.setRESULTADO(resultado);
		pago.setREPLY(reply);
		response.setPAGODEUDAS(pago);
		
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
		response.setPAGODEUDAS(new PAGODEUDAS());
		response.getPAGODEUDAS().setREPLY(reply);
		return response;
	}
}
