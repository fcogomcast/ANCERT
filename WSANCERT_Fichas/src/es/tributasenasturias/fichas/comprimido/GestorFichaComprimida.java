package es.tributasenasturias.fichas.comprimido;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import es.tributasenasturias.fichas.Constantes;
import es.tributasenasturias.fichas.excepciones.ComprimidoException;

public class GestorFichaComprimida {

	private GestorFichaComprimida()
	{	}
	public static class Contenido {
        private String nombrePdf;
        private String nombreXml;
        private String contenidoPdf;
        private String contenidoXml;
        public String getNombrePdf() {
            return nombrePdf;
        }
        public void setNombrePdf(String nombrePdf) {
            this.nombrePdf = nombrePdf;
        }
        public String getNombreXml() {
            return nombreXml;
        }
        public void setNombreXml(String nombreXml) {
            this.nombreXml = nombreXml;
        }
        public String getContenidoPdf() {
            return contenidoPdf;
        }
        public void setContenidoPdf(String contenidoPdf) {
            this.contenidoPdf = contenidoPdf;
        }
        public String getContenidoXml() {
            return contenidoXml;
        }
        public void setContenidoXml(String contenidoXml) {
            this.contenidoXml = contenidoXml;
        }

    }

    private static byte[] leerEntrada (ZipInputStream zip) throws IOException{
        int leido=0;
        byte[] buffer= new byte[4096];
        ByteArrayOutputStream baos =  new ByteArrayOutputStream();
        while ((leido = zip.read(buffer,0, 4096))!= -1){
            baos.write(buffer,0,leido);
        }
        return baos.toByteArray();
    }
    public static Contenido  descomprimir (byte[] comprimido) throws ComprimidoException{
        Contenido contenido= new Contenido();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(comprimido);
            ZipInputStream zis = new ZipInputStream(bais);
            ZipEntry entry;
            int numPdf=0;
            int numXml=0;
            while ((entry = zis.getNextEntry())!= null){
                if (entry.isDirectory()){
                    //No contamos con directorios, pero aceptamos.
                    continue;
                }
                String entryName = entry.getName().toUpperCase();
                if (entryName.endsWith(".PDF")){
                    numPdf += 1;
                } else if (entryName.endsWith(".XML")){
                    numXml += 1;
                } else {
                    throw new ComprimidoException("Error. El archivo comprimido contiene ficheros que no son PDF o XML");
                }

                if (numPdf> 1 || numXml > 1){
                    if (numPdf>1) {
                        throw new ComprimidoException("Error. Se ha encontrado más de un fichero PDF en el archivo comprimido");
                    } else {
                        throw new ComprimidoException("Error. Se ha encontrado más de un fichero XML en el archivo comprimido");
                    }
                } else {
                    if (entryName.endsWith(".PDF")){
                        contenido.setNombrePdf(entryName);
                        contenido.setContenidoPdf(new  String(Base64.encode(leerEntrada(zis))));
                    }

                    if (entryName.endsWith(".XML")){
                        contenido.setNombreXml(entryName);
                        contenido.setContenidoXml(new String(leerEntrada(zis), Constantes.CODIFICACION));
                    }

                }
            }
        } catch (IOException io){
            throw new ComprimidoException("Error al leer el contenido del archivo:" + io.getMessage(), io);
        }
        return contenido;
    }
}
