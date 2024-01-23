/**
 * 
 */
package es.tributasenasturias.utils.log;

import java.util.Date;




/** Formatea el mensaje que se le pasa. Esto permite que ante un mismo mensaje, se tenga más de una 
 * forma de mostrarlo.
 * @author crubencvs
 *
 */
public class FormateadorTributas implements IFormateadorMensaje{

		private String infoExtra;//Información extra que se quiere incluir en cada línea de log.
		/**
		 * Se indica la información extra a incluir en cada línea de log.
		 * @param infoExtra Identificador.
		 */
		public void setInfoExtra(String infoExtra)
		{
			this.infoExtra=infoExtra;
		}
		/**
		 * Constructor por defecto.
		 */
		protected FormateadorTributas()
		{
			
		}
		
		/**
		 * Método que realiza el formateo del mensaje con un nivel de mensajes especificado.
		 * @param message Mensaje a logear
		 */
		@Override
		public String formatea(String message,NIVEL_LOG nivel)
		{
				Date today = new Date();
	            String completeMsg = today + " :: " + nivel + " :: " + message;
	            if (infoExtra!=null)
	            {
	            	completeMsg+="::"+infoExtra;
	            }
	            return completeMsg;
		}
}
