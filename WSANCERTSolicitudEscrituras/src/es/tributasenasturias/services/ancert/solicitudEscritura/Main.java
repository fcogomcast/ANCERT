package es.tributasenasturias.services.ancert.solicitudEscritura;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EntradaType ent=new EntradaType();
		ent.setCodNotario("9900004");
		ent.setCodNotaria("0000000004");
		ent.setProtocolo("1300");
		ent.setAnioAutorizacion(2010);
		ent.setDestinatario("Tribu");
		ent.setFinalidad("Prueba. No realizar ninguna acción");
		SolicitudEscrituraImpl imp=new SolicitudEscrituraImpl();
		try {
			imp.solicitar(ent);
		} catch (SolicitudEscrituraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
