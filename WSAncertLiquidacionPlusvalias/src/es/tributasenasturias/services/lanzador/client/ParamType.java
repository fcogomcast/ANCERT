package es.tributasenasturias.services.lanzador.client;
/**
 * 
 * @author noelianbb
 * Enumeración que encapsula los posibles tipos de parámetros 
 * (el ordinal comienza en 1)
 * 
 */

public enum ParamType {
	CADENA(1), NUMERO(2), FECHA(3), CLOB(4),CLOB_CDATA(5);
	private int valor;
               
               private ParamType(int valor) {
                       this.valor=valor;
               }

               public int getValor() {
                       return valor;
               }
}

