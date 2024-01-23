package es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class PDFParserHandler
{
  HashMap<String, String> mapNames = new HashMap<String,String>();
  String dataCadena = "";
  String elemActual = "";
  DatosSalidaImpresa dsi;
  Document doc;

  public PDFParserHandler(DatosSalidaImpresa datos, Document documento)
  {
    this(datos, documento, null);
  }

  protected PDFParserHandler(DatosSalidaImpresa datos, Document documento, HashMap<String, String> mapNombres)
  {
    this.dsi = datos;
    this.doc = documento;
    if (this.dsi == null)
      this.dsi = new DatosSalidaImpresa();
    this.mapNames = mapNombres;
  }

  public void parse()
  {
    if (this.doc == null)
      return;
    Node nodeActual = this.doc;
    boolean devuelta = false;
    while (nodeActual != null) {
      String nombre = nodeActual.getNodeName();
      boolean noesTextoComment = (nodeActual.getNodeType() != 3) && 
        (nodeActual.getNodeType() != 8);

      if ((!devuelta) && 
        (noesTextoComment) && 
        (this.dsi != null)) {
        startElementDSI(nodeActual, nombre);
      }

      if ((nodeActual.hasChildNodes()) && (!devuelta)) {
        nodeActual = nodeActual.getFirstChild();
      } else if (nodeActual.getNextSibling() != null) {
        if ((noesTextoComment) && 
          (this.dsi != null)) {
          endElementDSI(nodeActual, nombre);
        }
        devuelta = false;
        nodeActual = nodeActual.getNextSibling();
      } else {
        if ((noesTextoComment) && 
          (this.dsi != null)) {
          endElementDSI(nodeActual, nombre);
        }
        nodeActual = nodeActual.getParentNode();
        devuelta = true;
      }
      if (nodeActual.equals(this.doc))
        return;
    }
  }

  public void startElementDSI(Node node, String nombre)
  {
    if (nombre.equalsIgnoreCase("Paginas")) {
      this.dsi.pdf_open_file(node);
      String creador = "";
      String autor = "autor";
      String titulo = "titulo";
      if (this.mapNames != null) {
        creador = (String)this.mapNames.get("creador");
        autor = (String)this.mapNames.get("autor");
        titulo = (String)this.mapNames.get("titulo");
        creador = (creador == null) ? "" : creador;
        autor = ((autor == null) || (autor.equals(""))) ? "autor" : creador;
        titulo = ((titulo == null) || (titulo.equals(""))) ? "titulo" : 
          titulo;
        this.dsi.pdf_creador(creador, autor, titulo);
      }
    } else if (nombre.equalsIgnoreCase("Pagina")) {
      this.dsi.pdf_begin_page(node); } else {
      if (nombre.equalsIgnoreCase("grupo"))
        return;
      if (nombre.equalsIgnoreCase("Fila"))
        return;
      if (nombre.equalsIgnoreCase("bloque"))
        return;
      if (nombre.equalsIgnoreCase("texto")) {
        this.dsi.pdf_setfont(node);
        this.dsi.pdf_show_text(node);
      } else if (nombre.equalsIgnoreCase("textobox")) {
        this.dsi.pdf_setfont(node);
        this.dsi.pdf_show_boxed(node);
      } else if (nombre.equalsIgnoreCase("cuadro")) {
        this.dsi.pdf_rect(node);
      } else if (nombre.equalsIgnoreCase("imagen")) {
        this.dsi.pdf_open_image_file(node);
      } else if (nombre.equalsIgnoreCase("barras")) {
        this.dsi.pdf_barras(node);
      } else if (nombre.equalsIgnoreCase("linea")) {
        this.dsi.pdf_linea(node);
      } else if (nombre.equalsIgnoreCase("salto")) {
        this.dsi.pdf_end_page(node);
      }
    }
  }

  public void endElementDSI(Node node, String nombre)
  {
    if (nombre.equalsIgnoreCase("Paginas")) {
      this.dsi.pdf_close();
    } else if (nombre.equalsIgnoreCase("Pagina")) {
      this.dsi.pdf_end_page(node); } else {
      if (nombre.equalsIgnoreCase("grupo"))
        return;
      if (nombre.equalsIgnoreCase("Fila"))
        return;
      if (nombre.equalsIgnoreCase("bloque"))
        return;
      if (nombre.equalsIgnoreCase("texto"))
        return;
      if (nombre.equalsIgnoreCase("textobox"))
        return;
      if (nombre.equalsIgnoreCase("cuadro"))
        return;
      if (nombre.equalsIgnoreCase("imagen"))
        return;
      if (nombre.equalsIgnoreCase("barras"))
        return;
      if (nombre.equalsIgnoreCase("linea"))
        return;
      if (nombre.equalsIgnoreCase("salto"))
        this.dsi.pdf_begin_page(node);
    }
  }

  public void startElementDS(Node nodeActual, String nombre)
  {
  }

  public void endElementDS(Node nodeActual, String nombre)
  {
  }
}