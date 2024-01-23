package es.tributasenasturias.fichas;

public class ValidadorDatos {

	private ValidadorDatos()
	{
	}
	
	private static boolean isEmpty(String valor){
		if (valor==null || "".equals(valor)){
			return true;
		}
		return false;
	}
	
	public static class ValidacionResult {
		private boolean valido;
		private String mensaje;
		public boolean isValido() {
			return valido;
		}
		public void setValido(boolean valido) {
			this.valido = valido;
		}
		public String getMensaje() {
			return mensaje;
		}
		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}
		
	}
	
	public static ValidacionResult validarDatosAlta(IdEscrituraType idEscritura,
							            String origenFicha, 
							            String codigoAyuntamiento,
								        String autorizacionEnvioDiligencias,
								        byte[] contenidoArchivoComprimido) 
	{
		
		ValidacionResult v = new ValidacionResult();
		v.setValido(true);
		if (idEscritura==null){
			v.setValido(false);
			v.setMensaje("No se han recibido datos identificativos de la escritura");
		}
		
		if (v.isValido() && isEmpty(idEscritura.getCodigoNotario())) {
			v.setValido(false);
			v.setMensaje("No se ha recibido c�digo de notario");
		}
		
		if (v.isValido() && isEmpty(idEscritura.getCodigoNotaria())) {
			v.setValido(false);
			v.setMensaje("No se ha recibido c�digo de notar�a");
		}
		
		if (v.isValido() && isEmpty(idEscritura.getNumeroProtocolo())) {
			v.setValido(false);
			v.setMensaje("No se ha recibido n�mero de protocolo");
		}
		
		if (v.isValido() && isEmpty(idEscritura.getProtocoloBis())) {
			v.setValido(false);
			v.setMensaje("No se ha recibido n�mero de protocolo Bis");
		}
		
		if (v.isValido() && isEmpty(idEscritura.getFechaAutorizacion())) {
			v.setValido(false);
			v.setMensaje("No se ha recibido Fecha de autorizacion");
		}
		
		if (v.isValido() && isEmpty(origenFicha)) {
			v.setValido(false);
			v.setMensaje("No se ha recibido Origen de la Ficha");
		}
		
		if (v.isValido() && (!"N".equals(origenFicha) && !"NP".equals(origenFicha))) {
			v.setValido(false);
			v.setMensaje("El origen de la ficha s�lo puede ser \"N\" o \"NP\" ");
		}
		
		return v;
	}
}
