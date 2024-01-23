/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils;

/**
 * @author crubencvs
 *
 */
public class XMLDOMDocumentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 520464406390387410L;
	
	public XMLDOMDocumentException(String error, Throwable original) {
		super("XMLException::" + error, original);
	}

	public XMLDOMDocumentException(String error) {
		super("XMLException::" + error);
	}

}
