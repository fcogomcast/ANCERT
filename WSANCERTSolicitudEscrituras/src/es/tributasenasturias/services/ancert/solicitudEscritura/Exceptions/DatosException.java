package es.tributasenasturias.services.ancert.solicitudEscritura.Exceptions;

public class DatosException extends BaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5887758438660410220L;

	public DatosException(String error, Throwable original){
		super (error, original);
	}

	public DatosException (String error)
	{
		super(error);
	}
	
}
