package es.tributasenasturias.lanzador;
/**
 * 
 * @author noelianbb
 * Enumeración que encapsula los posibles tipos de parámetros 
 * (el ordinal comienza en 1)
 * 
 */

public enum ParamType {
	CADENA(1), NUMERO(2), FECHA(3), CLOB(4),CLOB_CDATA(8), ARRNUM(5), ARRCAD(6), ARRFEC(7);
	private int valor;
               
               private ParamType(int valor) {
                       this.valor=valor;
               }

               public int getValor() {
                       return valor;
               }
}

