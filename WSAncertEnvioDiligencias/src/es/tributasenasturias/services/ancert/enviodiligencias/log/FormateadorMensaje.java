/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.log;

import java.util.Date;




/** Formatea el mensaje que se le pasa. Esto permite que ante un mismo mensaje, se tenga m�s de una 
 * forma de mostrarlo.
 * @author crubencvs
 *
 */
public class FormateadorMensaje implements IFormateadorMensaje{

		private String sesionId;//Id de la sesi�n que imprime, si hubiera.
		private String identificadorLog; //Identificador del log.
		/**
		 * Se indica el identificador de sesi�n, que se mostrar� en cada entrada.
		 * @param id Identificador del sesi�n.
		 */
		public void setSesionId(String id)
		{
			sesionId=id;
		}
		/**
		 * Constructor por defecto.
		 */
		protected FormateadorMensaje()
		{
			
		}
		protected FormateadorMensaje (String sesionId,String identificadorMensaje)
		{
				
		}
		
		/**
		 * M�todo que realiza el formateo del mensaje.
		 * @param message Mensaje a logear
		 */
		@Override
		public String formatea(String message)
		{
				Date today = new Date();
	            String completeMsg = identificadorLog+" :: " + today + " :: " + NIVEL_LOG.INFO.toString() + " :: " + message;
	            if (sesionId!=null)
	            {
	            	completeMsg+="::Sesi�n:"+sesionId;
	            }
	            return completeMsg;
		}
		/**
		 * M�todo que realiza el formateo del mensaje con un nivel de mensajes especificado.
		 * @param message Mensaje a logear
		 */
		@Override
		public String formatea(String message,NIVEL_LOG nivel)
		{
				Date today = new Date();
	            String completeMsg = identificadorLog+" :: " + today + " :: " + nivel + " :: " + message;
	            if (sesionId!=null)
	            {
	            	completeMsg+="::Sesi�n:"+sesionId;
	            }
	            return completeMsg;
		}
		/**
		 * Recupera el nombre de log (identificador interno de ese log, aparece en las l�neas de log)
		 * @return {@link String} que indicar� el nombre de log
		 */
		public String getIdentificadorLog() {
			return identificadorLog;
		}
		/**
		 * Modifica el nombre de log (identificador interno, aparece en las l�neas de log).
		 * @param nombre {@link String} con el nuevo nombre de log
		 */
		public void setIdentificadorLog(String identificadorLog) {
			this.identificadorLog = identificadorLog;
		}
}
