/**
 * 
 */
package es.tributasenasturias.services.ancert.recepcionescritura.exceptions;

/**
 * @author crubencvs
 *
 */
public class XMLDOMDocumentException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 520464406390387410L;
	
	public XMLDOMDocumentException(String error, Throwable original) {
		super(error, original);
	}

	public XMLDOMDocumentException(String error) {
		super(error);
	}

}
