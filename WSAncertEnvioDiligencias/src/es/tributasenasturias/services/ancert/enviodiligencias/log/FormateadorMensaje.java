/**
 * 
 */
package es.tributasenasturias.services.ancert.enviodiligencias.log;

import java.util.Date;




/** Formatea el mensaje que se le pasa. Esto permite que ante un mismo mensaje, se tenga más de una 
 * forma de mostrarlo.
 * @author crubencvs
 *
 */
public class FormateadorMensaje implements IFormateadorMensaje{

		private String sesionId;//Id de la sesión que imprime, si hubiera.
		private String identificadorLog; //Identificador del log.
		/**
		 * Se indica el identificador de sesión, que se mostrará en cada entrada.
		 * @param id Identificador del sesión.
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
		 * Método que realiza el formateo del mensaje.
		 * @param message Mensaje a logear
		 */
		@Override
		public String formatea(String message)
		{
				Date today = new Date();
	            String completeMsg = identificadorLog+" :: " + today + " :: " + NIVEL_LOG.INFO.toString() + " :: " + message;
	            if (sesionId!=null)
	            {
	            	completeMsg+="::Sesión:"+sesionId;
	            }
	            return completeMsg;
		}
		/**
		 * Método que realiza el formateo del mensaje con un nivel de mensajes especificado.
		 * @param message Mensaje a logear
		 */
		@Override
		public String formatea(String message,NIVEL_LOG nivel)
		{
				Date today = new Date();
	            String completeMsg = identificadorLog+" :: " + today + " :: " + nivel + " :: " + message;
	            if (sesionId!=null)
	            {
	            	completeMsg+="::Sesión:"+sesionId;
	            }
	            return completeMsg;
		}
		/**
		 * Recupera el nombre de log (identificador interno de ese log, aparece en las líneas de log)
		 * @return {@link String} que indicará el nombre de log
		 */
		public String getIdentificadorLog() {
			return identificadorLog;
		}
		/**
		 * Modifica el nombre de log (identificador interno, aparece en las líneas de log).
		 * @param nombre {@link String} con el nuevo nombre de log
		 */
		public void setIdentificadorLog(String identificadorLog) {
			this.identificadorLog = identificadorLog;
		}
}
