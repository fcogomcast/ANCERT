package es.tributasenasturias.services.ancert.enviodiligencias.log;

public interface IFormateadorMensaje {
public String formatea (String message);
public String formatea (String message,NIVEL_LOG nivel);
}
