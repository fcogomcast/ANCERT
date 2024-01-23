package es.stpa.plusvalias.domain;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.stpa.plusvalias.CodigosTerminacion;
import es.stpa.plusvalias.domain.intercambio.Adquirente;
import es.stpa.plusvalias.domain.intercambio.Persona;
import es.stpa.plusvalias.domain.intercambio.Transmitente;

/**
 * Clase para validar datos de los intervinientes recibidos en operación (cálculo o presentación)
 * @author crubencvs
 *
 */
public class ValidadorIntervinientes {

	private ValidadorIntervinientes(){}
	
	public static class ResultadoValidacion{
		private boolean error;
		private String codigo;
		private String mensajeError;
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public String getCodigo() {
			return codigo;
		}
		public void setCodigo(String codigo) {
			this.codigo = codigo;
		}
		public String getMensajeError() {
			return mensajeError;
		}
		public void setMensajeError(String mensajeError) {
			this.mensajeError = mensajeError;
		}
	}

	/**
	 * Comprueba si el identificador se trata de un NIE
	 * @param data
	 * @return
	 */
	private static boolean esNie(String data)
	{
		boolean toReturn = false;
		boolean valido=true;
		String primeraLetra=null;
		Integer prefijo=null;
		// Se sustituye:
		// X por 0
		// Y por 1
		// Z por 2
		// Para después ir a la validación del número resultante como un NIF.
		data = data.toUpperCase();

		primeraLetra=data.substring(0,1);
		if (primeraLetra.startsWith("X"))
		{
			prefijo=0;
		}
		else if (primeraLetra.startsWith("Y"))
		{
			prefijo=1;
		}
		else if (primeraLetra.startsWith("Z"))
		{
			prefijo=2;
		}
		else 
		{
			toReturn=false;
			valido=toReturn;
		}
		if (valido)
		{
			toReturn=esNif(prefijo.toString()+ data.substring(1));
		}
		return toReturn;
	}
	
	/**
	 * Valida si el dato que se pasa es un NIF
	 * @param data
	 * @return
	 */
	private static boolean esNif(String data)
	{
		boolean toReturn = false;
		data = data.toUpperCase();
		
		final String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

		Pattern nifPattern = Pattern.compile("([LKM]{0,1})([0-9]{7,8})([" + letras + "])");
		Matcher m = nifPattern.matcher(data);
		if(m.matches())
		{
			//String letra = m.group(2);
			String letra = m.group(3); //Ahora hay letras por delante, L, K o M 
			//Extraer letra del NIF
			//int dni = Integer.parseInt(m.group(1));
			int dni = Integer.parseInt(m.group(2)); //Ahora hay letras por delante
	      	dni = dni % 23;
	      	String reference = letras.substring(dni,dni+1);
	 
	      	toReturn = reference.equalsIgnoreCase(letra);
	   }
		
	   return toReturn;
	}
	/**
	 * Valida si el dato que se pasa es un CIF
	 * @param data
	 * @return
	 */
	private static boolean esCif(String data)
	{
		boolean toReturn = false;
		data = data.toUpperCase();
		 
		Pattern cifPattern =
			Pattern.compile("([ABCDEFGHJKLMNPQRSUVWabcdefghjklmnpqrsuvw])(\\d)(\\d)(\\d)(\\d)(\\d)(\\d)(\\d)([abcdefghijABCDEFGHIJ0123456789])");
	 
		Matcher m = cifPattern.matcher(data);
		if(m.matches())
		{
			//Sumamos las posiciones pares de los números centrales (en realidad posiciones 3,5,7 generales)
			int sumaPar = Integer.parseInt(m.group(3))+Integer.parseInt(m.group(5))+Integer.parseInt(m.group(7));
	 
			//Multiplicamos por 2 las posiciones impares de los números centrales (en realidad posiciones 2,4,6,8 generales)
			//Y sumamos ambos digitos: el primer digito sale al dividir por 10 (es un entero y quedará 0 o 1)
			//El segundo dígito sale de modulo 10
			int sumaDigito2 = ((Integer.parseInt(m.group(2))*2)% 10)+((Integer.parseInt(m.group(2))*2)/ 10);
			int sumaDigito4 = ((Integer.parseInt(m.group(4))*2)% 10)+((Integer.parseInt(m.group(4))*2)/ 10);
			int sumaDigito6 = ((Integer.parseInt(m.group(6))*2)% 10)+((Integer.parseInt(m.group(6))*2)/ 10);
			int sumaDigito8 = ((Integer.parseInt(m.group(8))*2)% 10)+((Integer.parseInt(m.group(8))*2)/ 10);
	 
			int sumaImpar = sumaDigito2 +sumaDigito4 +sumaDigito6 +sumaDigito8 ;

			int suma = sumaPar +sumaImpar;
			int control = 10 - (suma%10);
			//La cadena comienza en el caracter 0, J es 0, no 10
			if (control==10)
			{
				control=0;
			}
			String letras = "JABCDEFGHI";
	 
			//El dígito de control es una letra
			if (m.group(1).equalsIgnoreCase("K")  ||
				m.group(1).equalsIgnoreCase("Q") || m.group(1).equalsIgnoreCase("S"))
			{
				toReturn = m.group(9).equalsIgnoreCase(letras.substring(control,control+1));
			}
			//El dígito de control es un número
			else if (m.group(1).equalsIgnoreCase("B") || m.group(1).equalsIgnoreCase("H"))
			{
				toReturn = m.group(9).equalsIgnoreCase(""+control);
			}
			//El dígito de control puede ser un número o una letra
			else
			{
				toReturn = m.group(9).equalsIgnoreCase(letras.substring(control,control+1))||
					m.group(9).equalsIgnoreCase(""+control);
			}
		}
		
		return toReturn;
	}
	/**
	 * Valida  que el dato que se pasa corresponda con un NIF(NIF, NIE, CIF) válido
	 * @param nif
	 * @return
	 */
	public static boolean nifValido(String nif){
		String nifTemp= nif.trim();
		if (esNif(nifTemp) || esNie(nifTemp) || esCif(nifTemp)){
			return true;
		}
		return false;
	}
	/**
	 * Valida datos del transmitente. Se debería utilizar sólo cuando es contribuyente
	 * @param t
	 * @return
	 */
	public static ResultadoValidacion validarSujeto(Transmitente t){
		ResultadoValidacion resultado= new ResultadoValidacion();
		resultado.setError(false);
		//Usufructo vitalicio
		if (!resultado.isError() && "3".equals(t.getClasederecho())){
			if (t.getEdadusufructuario()==null || t.getEdadusufructuario()==0){
				resultado.setCodigo(CodigosTerminacion.ERROR_EDAD_USUFRUCTUARIO);
				resultado.setError(true);
			}
		}
		//Usufructo temporal
		if ( !resultado.isError() && "7".equals(t.getClasederecho())){
			if (t.getAnyosusufructo()==null || t.getAnyosusufructo()==0){
				resultado.setCodigo(CodigosTerminacion.ERROR_ANIOS_USUFRUCTO);
				resultado.setError(true);
			}
		}
		//Uso y habitación vitalicio
		if (!resultado.isError() && "8".equals(t.getClasederecho())){
			if (t.getEdaduso()==null || t.getEdaduso()==0){
				resultado.setCodigo(CodigosTerminacion.ERROR_EDAD_USO);
				resultado.setError(true);
			}
		}
		//Uso y habitación temporales
		if (!resultado.isError() && "4".equals(t.getClasederecho())){
			if (t.getAnyosuso()==null || t.getAnyosuso()==0){
				resultado.setCodigo(CodigosTerminacion.ERROR_ANIOS_USO);
				resultado.setError(true);
			}
		}
		//Nuda propiedad, necesita siempre edad del usufructurario o años de usufructo
		if (!resultado.isError() && "2".equals(t.getClasederecho())){
			if ((t.getEdadusufructuario()==null || t.getEdadusufructuario()==0) 
					&&
				(t.getAnyosusufructo()==null || t.getAnyosusufructo()==0)
				){
				resultado.setCodigo(CodigosTerminacion.ERROR_NUDA_PROPIEDAD_NO_EDAD_ANIOS);
				resultado.setError(true);
			}
		}
		
		return resultado;
	}
	/**
	 * Valida datos del Adquirente. Se debería utilizar sólo cuando es contribuyente
	 * @param a
	 * @return
	 */
	public static ResultadoValidacion validarSujeto(Adquirente a){
		ResultadoValidacion resultado= new ResultadoValidacion();
		resultado.setError(false);
		//Usufructo vitalicio
		if (!resultado.isError() && "3".equals(a.getClasederecho())){
			if (a.getEdadusufructuario()==null || a.getEdadusufructuario()==0){
				resultado.setCodigo(CodigosTerminacion.ERROR_EDAD_USUFRUCTUARIO);
				resultado.setError(true);
			}
		}
		//Usufructo temporal
		if ( !resultado.isError() && "7".equals(a.getClasederecho())){
			if (a.getAnyosusufructo()==null || a.getAnyosusufructo()==0){
				resultado.setCodigo(CodigosTerminacion.ERROR_ANIOS_USUFRUCTO);
				resultado.setError(true);
			}
		}
		//Uso y habitación vitalicio
		if (!resultado.isError() && "8".equals(a.getClasederecho())){
			if (a.getEdaduso()==null || a.getEdaduso()==0){
				resultado.setCodigo(CodigosTerminacion.ERROR_EDAD_USO);
				resultado.setError(true);
			}
		}
		//Uso y habitación temporales
		if (!resultado.isError() && "4".equals(a.getClasederecho())){
			if (a.getAnyosuso()==null || a.getAnyosuso()==0){
				resultado.setCodigo(CodigosTerminacion.ERROR_ANIOS_USO);
				resultado.setError(true);
			}
		}
		//Nuda propiedad, necesita siempre edad del usufructuario o años de usufructo
		if (!resultado.isError() && "2".equals(a.getClasederecho())){
			if ((a.getEdadusufructuario()==null || a.getEdadusufructuario()==0) 
					&&
				(a.getAnyosusufructo()==null || a.getAnyosusufructo()==0)
				){
				resultado.setCodigo(CodigosTerminacion.ERROR_NUDA_PROPIEDAD_NO_EDAD_ANIOS);
				resultado.setError(true);
			}
		}
		return resultado;
	}
	/**
	 * Validaciones propias cuando se trata de un causante. 
	 * @param t Transmitente, cuando se trata de un causante
	 * @return
	 */
	public static ResultadoValidacion validarCausante(Transmitente t){
		ResultadoValidacion resultado= new ResultadoValidacion();
		resultado.setError(false);
		
		if (!resultado.isError()){
			if (t.getFechadefuncion()==null){
				resultado.setCodigo(CodigosTerminacion.ERROR_CAUSANTE_NO_FECHA_DEF);
				resultado.setError(true);
			}
		}
		return resultado;
	}
	
	/**
	 * Valida los nif que intervienen en el trámite.
	 * No valida los del notario, porque no nos lo pasan ellos,
	 * lo recuperamos  de nuestro sistema y lo damos por bueno.
	 * @param peticion
	 * @return
	 */
	public static ResultadoValidacion validaNifs(PeticionOperacionType peticion){
		ResultadoValidacion resultado= new ResultadoValidacion();
		if (peticion.getTransmitentes() != null && peticion.getTransmitentes().size()>0){
			for (Transmitente t: peticion.getTransmitentes()){
				if (!ValidadorIntervinientes.nifValido(t.getDatosbasicos().getDatospersonales().getNumerodocumento())){
					resultado.codigo= CodigosTerminacion.ERROR_NIF_TRANSMITENTE;
					resultado.error=true;
					resultado.mensajeError = CodigosTerminacion.getMessage(resultado.codigo, t.getDatosbasicos().getDatospersonales().getNumerodocumento(), String.valueOf(t.getDatosbasicos().getIdentificador()));
					break;
				}
			}
		}
		if (!resultado.error){
			if (peticion.getAdquirentes() != null && peticion.getAdquirentes().size()>0){
				for (Adquirente a: peticion.getAdquirentes()){
					if (!ValidadorIntervinientes.nifValido(a.getDatosbasicos().getDatospersonales().getNumerodocumento())){
						resultado.codigo= CodigosTerminacion.ERROR_NIF_ADQUIRENTE;
						resultado.error=true;
						resultado.mensajeError = CodigosTerminacion.getMessage(resultado.codigo, a.getDatosbasicos().getDatospersonales().getNumerodocumento(), String.valueOf(a.getDatosbasicos().getIdentificador()));
						break;
					}
				}
			}
		}
		if (!resultado.error){
			if (peticion.getRepresentantes() != null && peticion.getRepresentantes().size() > 0){
				for (Persona r: peticion.getRepresentantes()){
					if (!ValidadorIntervinientes.nifValido(r.getDatospersonales().getNumerodocumento())){
						resultado.codigo= CodigosTerminacion.ERROR_NIF_REPRESENTANTE;
						resultado.error= true;
						resultado.mensajeError = CodigosTerminacion.getMessage(resultado.codigo, r.getDatospersonales().getNumerodocumento(), String.valueOf(r.getIdentificador()));
						break;
					}
				}
			}
		}
		return resultado;
	}
}
