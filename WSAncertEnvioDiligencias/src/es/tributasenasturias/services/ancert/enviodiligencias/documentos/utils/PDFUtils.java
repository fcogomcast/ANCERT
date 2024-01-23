package es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

import es.tributasenasturias.services.ancert.enviodiligencias.documentos.DocumentoException;

//import es.tributasenasturias.documentos.util.Base64;

public class PDFUtils {
	
	public static String generarPdf(PdfBase tl) throws Exception {		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ByteArrayOutputStream[] bufferArray = new ByteArrayOutputStream[]{new ByteArrayOutputStream()};
		HashMap<String, String> session=new HashMap<String, String>();
		session.put("idioma", "es");
		//if(xmlTasasLiq!=null){
			try {				
				//TasasLiquidacion tl=new TasasLiquidacion();
				tl.Session = session;
				tl.compila("", "", getXsl(tl), bufferArray[0]);
				if (bufferArray[0].toByteArray().length == 0){
					throw new DocumentoException("NO SE HA COMPILADO EL PDFBEAN DocComparencia");
				}
			} catch (RemoteException e) {
				throw new DocumentoException("Error generando PDF DocComparencia " + e,e);
			}
		//}
		
		//escribirPdf(bufferArray[0],"prueba.pdf");

		buffer = (ByteArrayOutputStream) agregarDocumentos(bufferArray, buffer, null);
//		escribirPdf(buffer,"c:/AmbasTasas.pdf");
		char[] c = Base64.encode(buffer.toByteArray());
		
		return new String(c);
	}
	
	private static String getXsl(PdfBase fb) throws Exception{
		
		String uristring = fb.getPlantilla();// "impresos\\xml\\tasasliquidacion.xml";
		
		String xslplantilla = getContents(uristring, "iso-8859-1");
		String encoding = XMLUtils.getEncoding(xslplantilla);
		if (encoding != null && !encoding.equals("") && !encoding.equals("iso-8859-1")){
			xslplantilla = getContents(uristring, encoding);
		}		
		return xslplantilla;
	}

	public static String getContents(String aFile, String encoding) throws DocumentoException {
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			try{
				if (!new File(aFile).exists()) {
					URL url = new URL(aFile);
					return getContents(url, encoding);
				}
			}catch(Exception e){
				//Continuamos ya que no es una url
				//Logger.info("Continuamos ya que no es una url "+e.getMessage());
			}
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(aFile));
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			byte[] buf = new byte[512];
			int len;
			while ((len = bis.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
			output.close();
			bis.close();
			return output.toString(encoding);
		} catch (IOException ex) {
			throw new DocumentoException("Error obteniendo XSL "+ex.getMessage(),ex);
		}
	}

	public static String getContents(URL aFile, String encoding) throws DocumentoException{
		ByteArrayOutputStream contents = new ByteArrayOutputStream();
		try {
			if (aFile == null)
				return "";
			InputStream is = aFile.openStream();
			byte[] buf = new byte[512];
			int len;
			while ((len = is.read(buf)) > 0) {
				contents.write(buf, 0, len);
			}
		} catch (IOException ex) {
			throw new DocumentoException("Error obteniendo XSL "+ex.getMessage(),ex);
		}
		try {
			return contents.toString(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new DocumentoException("Error obteniendo XSL "+e.getMessage(),e);
		}
	}
	
	/**
	 * Agrega una serie de ByteArrayOutputStreams (ver PortletBridge.java) para formar un solo PDF en outFile.
	 * 
	 * @param lstDocs
	 * @param outFile
	 * @param docProperties - Mapa con Author, Creator, Keywords, Subject, Title
	 */
	private static OutputStream agregarDocumentos(ByteArrayOutputStream[] lstDocs, OutputStream outFile, HashMap<String,String> docProperties) {
		InputStream[] argsin = new ByteArrayInputStream[lstDocs.length];
		for (int i = 0; i < lstDocs.length; i++) {
			if (lstDocs[i] != null) {
				if (lstDocs[i] instanceof ByteArrayOutputStream)
					argsin[i] = new ByteArrayInputStream(((ByteArrayOutputStream) lstDocs[i]).toByteArray());
			}
		}
		return agregarDocumentos(argsin, outFile,docProperties);
	}
	
	/**
	 * Agrega una serie de InputStreamS para formar un solo PDF en outFile. NOTA: crear multiples copias del mismo
	 * Stream da IOException si el metodo markSupported() retorna false o no es un FileInputStream.
	 * 
	 * @param lstDocs
	 * @param outFile
	 * @param docProperties - Mapa con Author, Creator, Keywords, Subject, Title
	 */
	@SuppressWarnings("unchecked")
	private static OutputStream agregarDocumentos(InputStream[] lstDocs, OutputStream outFile, HashMap<String,String> docProperties) {
		// String args[];
		try {
			if (outFile == null)
				outFile = new ByteArrayOutputStream();
			int pageOffset = 0;
			ArrayList master = new ArrayList();
			int f = 0;
			// String outFile = args[args.length-1];
			Document document = null;
			PdfCopy writer = null;
			while (f < lstDocs.length) {
				if (lstDocs[f] == null) {
					f++;
					continue;
				}
				// we create a reader for a certain document
				if (lstDocs[f].markSupported())
					lstDocs[f].reset();
				else if (lstDocs[f] instanceof FileInputStream)
					((FileInputStream) lstDocs[f]).getChannel().position(0);
				PdfReader reader = null;
				try {
					reader = new PdfReader(lstDocs[f]);
				} catch (IOException e) {
					f++;
					continue;
				}
				reader.consolidateNamedDestinations();
				// we retrieve the total number of pages
				int n = reader.getNumberOfPages();
				List bookmarks = SimpleBookmark.getBookmark(reader);
				if (bookmarks != null) {
					if (pageOffset != 0)
						SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
					master.addAll(bookmarks);
				}
				pageOffset += n;

				if (f == 0||document==null) {
					// step 1: creation of a document-object
					document = new Document(reader.getPageSizeWithRotation(1));
					// step 2: we create a writer that listens to the document
					writer = new PdfCopy(document, outFile);// new FileOutputStream(outFile));
					HashMap<String, String> pdfinfo = (HashMap<String, String>) reader.getInfo();
					if (docProperties != null)
						pdfinfo = docProperties;
					if (pdfinfo.containsKey("Author"))
						document.addAuthor(pdfinfo.get("Author"));
					if (pdfinfo.containsKey("Creator"))
						document.addCreator(pdfinfo.get("Creator"));
					if (pdfinfo.containsKey("Subject"))
						document.addSubject(pdfinfo.get("Subject"));
					if (pdfinfo.containsKey("Title"))
						document.addTitle(pdfinfo.get("Title"));
					if (pdfinfo.containsKey("Keywords"))
						document.addKeywords(pdfinfo.get("Keywords"));
					document.addCreationDate();

					// step 3: we open the document
					document.open();
				}
				// step 4: we add content
				PdfImportedPage page;
				for (int i = 0; i < n;) {
					++i;
					page = writer.getImportedPage(reader, i);
					writer.addPage(page);
				}
				String js = reader.getJavaScript();
				if (js != null && js.length() != 0)
					writer.addJavaScript(js);
				PRAcroForm form = reader.getAcroForm();
				if (form != null)
					writer.copyAcroForm(reader);
				if (lstDocs[f].markSupported())
					lstDocs[f].reset();
				else if (lstDocs[f] instanceof FileInputStream)
					((FileInputStream) lstDocs[f]).getChannel().position(0);
				f++;
			}
			if (!master.isEmpty())
				writer.setOutlines(master);
			// step 5: we close the document
			if (document != null)
				document.close();
		} catch (Exception e) {
			
		}
		return outFile;
	}
}

