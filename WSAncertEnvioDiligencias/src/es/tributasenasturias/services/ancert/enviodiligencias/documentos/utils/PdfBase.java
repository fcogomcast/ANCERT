package es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public abstract class PdfBase {

	public String plantilla;
	public Map<String, String> Session = new HashMap<String, String>();
	
	public abstract String getPlantilla();
	
	public abstract void compila(String id, String xml, String xsl, OutputStream output) throws RemoteException;
}

