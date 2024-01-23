package es.tributasenasturias.indices_fiscales;

public class IndicesFiscalesException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1938369985297763868L;

	public IndicesFiscalesException(String message, Throwable cause) {
		super("IndicesFiscales:"+message, cause);
	}

	public IndicesFiscalesException(String message) {
		super("IndicesFiscales:"+ message);
	}

}
