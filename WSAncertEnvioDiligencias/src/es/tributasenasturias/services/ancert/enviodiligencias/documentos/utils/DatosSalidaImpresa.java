package es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;

import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.CallContext;
import es.tributasenasturias.services.ancert.enviodiligencias.comunicacion.interna.contextollamadas.IContextReader;
import es.tributasenasturias.services.ancert.enviodiligencias.documentos.DocumentoException;
import es.tributasenasturias.services.ancert.enviodiligencias.log.LogAplicacion;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DatosSalidaImpresa implements IContextReader
{
public HashMap<String, Object> rs_master = null;
  private com.lowagie.text.Document oPDF;
  private PdfContentByte pcb;
  private org.w3c.dom.Document fuente = null;
  private PdfWriter pdfwriter = null;
  private Font ff = null;
  CallContext context;
  LogAplicacion log;
  static Map<String, String> Session = new HashMap();

  public static String pathSalidaBase = null;
  public OutputStream pdfoutput = null;

  public DatosSalidaImpresa() {
    this.oPDF = pcbLoad();
  }

  public org.w3c.dom.Document getXmlDatos() {
    return this.fuente;
  }

  public static String reportRuntimeError(Exception exception)
  {
    String errormsg = "";
    errormsg = errormsg + "<font face=Verdana size=2><font size=4>XSL Runtime Error</font><P><B>" + 
      exception.getMessage() + "</B></P></font>";
    return errormsg;
  }

  public static int parseInt(String s, int radix)
  {
    try
    {
      if (s.indexOf(".") != -1)
        s = s.substring(0, s.indexOf("."));
      if (s.indexOf(",") != -1)
        s = s.substring(0, s.indexOf(","));
      if (s.indexOf(" ") != -1) {
        int index = 0;
        for (index = 0; index < s.length(); ++index)
          if (s.charAt(index) != ' ')
            break;
        s = s.substring(index, s.length());
        for (index = s.length() - 1; index > 0; --index)
          if (s.charAt(index) != ' ')
            break;
        s = s.substring(0, index + 1);
      }
      Integer num = Integer.valueOf(Integer.parseInt(s, radix));
      if (num != null)
        return num.intValue();
    } catch (NumberFormatException e) {
      return 0;
    }
    return 0;
  }

  public static int parseInt(String s)
  {
    try
    {
      Integer num = null;

      if (s.indexOf(".") != -1)
        s = s.substring(0, s.indexOf("."));
      if (s.indexOf(",") != -1)
        s = s.substring(0, s.indexOf(","));
      if (s.indexOf(" ") != -1) {
        int index = 0;
        for (index = 0; index < s.length(); ++index)
          if (s.charAt(index) != ' ')
            break;
        s = s.substring(index, s.length());
        for (index = s.length() - 1; index > 0; --index)
          if (s.charAt(index) != ' ')
            break;
        s = s.substring(0, index + 1);
      }
      Double nummax = Double.valueOf(Double.parseDouble(s));
      if ((s.startsWith("0x")) || (s.startsWith("0X")))
        num = Integer.valueOf(Integer.parseInt(s, 16));
      else
        num = Integer.valueOf(Integer.parseInt(s));
      if (num != null)
        return num.intValue();
    } catch (NumberFormatException e) {
      return 0;
    }
    return 0;
  }

  public static int cuentaLineas(String cadena, int length)
  {
    int lineas = 0;
    if (validString(cadena)) {
      ++lineas;
      int j = 0;
      for (int i = 0; i < cadena.length(); ++i) {
        if ((cadena.charAt(i) == '\n') || (j >= length)) {
          ++lineas;
          j = 0;
        }
        ++j;
      }
    }
    return lineas;
  }

  public static String campo(Map rs, String nombre)
  {
    if ((rs == null) || (rs.isEmpty()))
      return "";
    if (rs.get(nombre) != null)
      return (String)rs.get(nombre);
    if (rs.get(nombre.toUpperCase()) != null) {
      return (String)rs.get(nombre.toUpperCase());
    }
    return "";
  }

  public static String vac(Map rs, String nombre)
  {
    if ((rs == null) || (rs.isEmpty()))
      return "0";
    if (rs.get(nombre) != null)
      return (String)rs.get(nombre);
    if (rs.get(nombre.toUpperCase()) != null) {
      return (String)rs.get(nombre.toUpperCase());
    }
    return "0";
  }

  public static String nvl(Map rs, String nombre)
  {
    if ((rs == null) || (rs.isEmpty()))
      return "";
    return campo(rs, nombre);
  }

  public static Map primeraLinea(Map rs)
  {
    if (rs == null)
      return null;
    return (Map)rs.get(new Integer(0));
  }

  public static boolean validString(String cadena)
  {
    return (cadena != null) && (cadena.length() != 0);
  }

  public static String referencia_dc(String referencia, String entidad)
  {
    String b = "";

    entidad = entidad.replaceAll(" ", "");
    double referencianum = Math.floor(Val(referencia));
    String ref_dc;
    if ((((referencianum > 0.0D) ? 1 : 0) & ((entidad.length() > 0) ? 1 : 0)) != 0) {
      referencianum = (int)(referencianum / 100.0D);
      b = trimDecimales(referencianum + (int)Val(entidad));
      b = (b.indexOf(".") != -1) ? b.substring(0, b.indexOf(".")) : b;
      b = Formato(b, 11);
      int[] multiple = { 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
      float x = 0.0F;
      for (int i = 0; i < 11; ++i) {
        x += Integer.valueOf(b.charAt(i)).intValue() * multiple[i];
      }
      x %= 11.0F;
      if (x == 10.0F)
        x = 0.0F;
      ref_dc = Formato(trimDecimales(referencianum * 10.0D + x), 12);
    } else {
      ref_dc = Formato(trimDecimales(referencianum), 12);
    }return ref_dc;
  }

  private static String trimDecimales(double d)
  {
    Double dd = new Double(d);
    return trimDecimales(dd.longValue());
  }

  private static String trimDecimales(String d)
  {
    if ((d == null) || (d.equals("")))
      return "";
    return (d.indexOf(".") != -1) ? d.substring(0, d.indexOf(".")) : d;
  }

  public static double Val(String input)
  {
    try
    {
      input.replaceAll(" ", "");
      Double value = Double.valueOf(Double.parseDouble(input));
      if (value != null)
        return value.doubleValue();
      return 0.0D; } catch (NumberFormatException e) {
    }
    return 0.0D;
  }

  public static String Abs(String numero)
  {
    return String.valueOf(Math.abs(Val(numero)));
  }

  public static String Trim(String cadena)
  {
    int length = cadena.length();
    int count = 0;
    while ((count < length) && (((cadena.charAt(count) == ' ') || (cadena.startsWith("&nbsp;", count)))))
      ++count;
    int ini = count;
    count = cadena.length() - 1;
    while ((count > ini) && (((cadena.charAt(count) == ' ') || (cadena.startsWith("&nbsp;", count)))))
      --count;
    int fin = count;
    return cadena.substring(ini, fin + 1);
  }

  public static String Left(String cadena, int length)
  {
    if ((cadena == null) || (cadena.length() < length) || (length < 0))
      return cadena;
    return cadena.substring(0, length);
  }

  public static String Right(String cadena, int length)
  {
    if ((cadena == null) || (cadena.length() < length) || (length < 0))
      return cadena;
    return cadena.substring(cadena.length() - length, cadena.length());
  }

  public static String IIf(boolean expr, String cadena1, String cadena2)
  {
    return (expr) ? cadena1 : cadena2;
  }

  public static String Formato(String cadena, int length)
  {
    int longitud = cadena.length();
    if (longitud < length) {
      char[] subcadena = new char[length - longitud];
      for (int i = 0; i < length - longitud; ++i)
        subcadena[i] = '0';
      cadena = new String(subcadena).concat(cadena);
    }
    return cadena;
  }

  public static String Formato(String cadena)
  {
    return cadena;
  }

  public static String campoFecha(Map rs, String nombre)
  {
    if (campo(rs, nombre).equals(""))
      return "";
    try
    {
      Date fecha = new Date(campo(rs, nombre));
      if (fecha == null)
        return "";
      SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
      return formateador.format(fecha); } catch (Exception e) {
    }
    return "";
  }

  public static String campoHora(Map rs, String nombre)
  {
    if (campo(rs, nombre).equals(""))
      return "";
    try
    {
      Date fecha = new Date(campo(rs, nombre));
      if (fecha == null)
        return "";
      SimpleDateFormat formateador = new SimpleDateFormat("hh:mm:ss");
      return formateador.format(fecha); } catch (Exception e) {
    }
    return "";
  }

  public static String dameFecha(String dato)
  {
    if ((dato == null) || (dato.equals("")))
      return "";
    int aux = dato.lastIndexOf(" ", 0);
    String cadena = "";
    if (aux != -1)
      if (aux > dato.length() - 2)
        cadena = "";
      else
        cadena = dato.substring(aux + 2, dato.length());
    else {
      cadena = new String(dato);
    }
    String dia = (cadena.length() >= 10) ? cadena.substring(8, 10) : "";
    int inianyo = cadena.length() - 4;
    if (inianyo < 0)
      inianyo = 0;
    String anyo = cadena.substring(inianyo, cadena.length() - 0);

    String mes = (cadena.length() >= 7) ? cadena.substring(4, 7) : "";
    if (mes.equals("Jan"))
      mes = "01";
    else if (mes.equals("Feb"))
      mes = "02";
    else if (mes.equals("Mar"))
      mes = "03";
    else if (mes.equals("Apr"))
      mes = "04";
    else if (mes.equals("May"))
      mes = "05";
    else if (mes.equals("Jun"))
      mes = "06";
    else if (mes.equals("Jul"))
      mes = "07";
    else if (mes.equals("Aug"))
      mes = "08";
    else if (mes.equals("Sep"))
      mes = "09";
    else if (mes.equals("Oct"))
      mes = "10";
    else if (mes.equals("Nov"))
      mes = "11";
    else if (mes.equals("Dec")) {
      mes = "12";
    }
    String resultado = dia + "/" + mes + "/" + anyo;
    return resultado;
  }

  public static String dameFecha2(String dato)
  {
    if ((dato == null) || (dato.equals("")))
      return "";
    String cadena = new String(dato);
    String dia = (cadena.length() >= 10) ? cadena.substring(8, 10) : "";
    int inianyo = cadena.length() - 4;
    if (inianyo < 0)
      inianyo = 0;
    String anyo = cadena.substring(inianyo, cadena.length() - 0);

    String mes = (cadena.length() >= 7) ? cadena.substring(4, 7) : "";
    if (mes.equals("Jan"))
      mes = "Enero";
    else if (mes.equals("Feb"))
      mes = "Febrero";
    else if (mes.equals("Mar"))
      mes = "Marzo";
    else if (mes.equals("Apr"))
      mes = "Abril";
    else if (mes.equals("May"))
      mes = "Mayo";
    else if (mes.equals("Jun"))
      mes = "Junio";
    else if (mes.equals("Jul"))
      mes = "Julio";
    else if (mes.equals("Aug"))
      mes = "Agosto";
    else if (mes.equals("Sep"))
      mes = "Septiembre";
    else if (mes.equals("Oct"))
      mes = "Octubre";
    else if (mes.equals("Nov"))
      mes = "Noviembre";
    else if (mes.equals("Dec")) {
      mes = "Diciembre";
    }
    String resultado = dia + " de " + mes + " de " + anyo;
    return resultado;
  }

  public static String dameFecha3(String dato)
  {
    if ((dato == null) || (dato.equals("")))
      return "";
    String cadena = new String(dato);
    String dia = (cadena.length() >= 2) ? cadena.substring(0, 2) : "";
    int inianyo = cadena.length() - 4;
    if (inianyo < 0)
      inianyo = 0;
    String anyo = cadena.substring(inianyo, cadena.length() - 0);

    String mes = (cadena.length() >= 5) ? cadena.substring(3, 5) : "";
    int mesnum = parseInt(mes);
    switch (mesnum)
    {
    case 1:
      mes = "enero";
      break;
    case 2:
      mes = "febrero";
      break;
    case 3:
      mes = "marzo";
      break;
    case 4:
      mes = "abril";
      break;
    case 5:
      mes = "mayo";
      break;
    case 6:
      mes = "junio";
      break;
    case 7:
      mes = "julio";
      break;
    case 8:
      mes = "agosto";
      break;
    case 9:
      mes = "septiembre";
      break;
    case 10:
      mes = "octubre";
      break;
    case 11:
      mes = "noviembre";
      break;
    case 12:
      mes = "diciembre";
    }

    String resultado = dia + " de " + mes + " de " + anyo + ".";
    return resultado;
  }

  public Map<Integer, Map<String, String>> findWs(String[] structSelect, String[] paramSelect, String oldSelect)
  {
    Map lstRes = new HashMap();

    if ((!this.rs_master.isEmpty()) && (structSelect != null)) {
      for (int i = 0; i < structSelect.length; ++i) {
        Map mapEstruct = 
          (Map)this.rs_master
          .get(structSelect[i].toLowerCase());
        if ((mapEstruct == null) || (mapEstruct.isEmpty()))
          mapEstruct = (Map)this.rs_master.get(structSelect[i].toUpperCase());
        if (mapEstruct == null) continue; if (mapEstruct.isEmpty())
          continue;
        int numKeys = mapEstruct.keySet().size();
        for (int j = 0; j < numKeys; ++j) {
          Integer key = new Integer(j);
          if (lstRes.containsKey(key)) {
            Map mapParams = (Map)lstRes.get(key);
            mapParams.putAll((Map)mapEstruct.get(key));
          } else {
            lstRes.put(key, (Map)mapEstruct.get(key));
          }
        }
      }
    }
    return lstRes;
  }

  public DatosSalidaImpresa(String xml, String xsl) throws DocumentoException
  {
    this(xml, xsl, null);
  }

  public DatosSalidaImpresa(String xml, String xsl, Object output) throws DocumentoException
  {
    if (output instanceof OutputStream) {
      this.pdfoutput = ((OutputStream)output);
    } else {
    }
    this.oPDF = pcbLoad();
    try {
      this.fuente = ((org.w3c.dom.Document)XMLUtils.compilaXMLObject(xsl, null));

      Element[] campos = XMLUtils.selectNodes(this.fuente, ".//texto[@type='109']");

      for (int j = 0; j < campos.length; ++j)
        campos[j].setTextContent("");
      campos = XMLUtils.selectNodes(this.fuente, ".//textobox[@type='109']");

      for (int j = 0; j < campos.length; ++j)
        campos[j].setTextContent("");
    }
    catch (RemoteException e) {
    	throw new DocumentoException ("Error en DatosSalidaImpresa1:"+e.getMessage(),e);
    }
    try {
      HashMap mapResults = XMLUtils.compilaXMLDoc(xml, null, null, true);
      String key = "";
      Object[] keylst = mapResults.keySet().toArray();
      for (int i = 0; i < keylst.length; ++i) {
        key = (String)keylst[i];
        if (key.equalsIgnoreCase("ERROR"))
          continue;
        Object[] lstStringArray = (Object[])mapResults.get(key);
        HashMap mapSalida = 
          XMLUtils.convertirObjectStringArrayHashMap(lstStringArray);
        mapResults.put(key, mapSalida);
      }
      this.rs_master = mapResults;
    }
    catch (RemoteException e) {
      throw new DocumentoException("Error en DatosSalidaImpresa2:" + e.getMessage());
    }
    Session.put("organismo", "33");
  }

  
  public static String Format(String cad1, String cad2)
  {
    if (cad1 == null)
      cad1 = "";
    if (cad2 == null)
      cad2 = "";
    String cad3 = "";
    String cad4 = "";
    String cad5 = "";
    String cad6 = "";

    int pos = cad1.indexOf(",", 0);

    if (pos != -1) {
      cad3 = cad1.substring(pos, cad1.length());
      cad1 = cad1.substring(0, pos);
    }
    int long1 = cad1.length();
    int long2 = cad2.length();

    cad4 = Cadreverse(cad1, long1);
    cad5 = Cadreverse(cad2, long2);

    int i = 0;
    int j = 0;

    while ((i < long1) && (j < long2)) {
      if (cad5.charAt(j) == '#') {
        cad6 = cad6 + cad4.charAt(i);
        ++i;
      } else {
        cad6 = cad6 + cad5.charAt(j);
      }++j;
    }

    if (i < long1 - 1) {
      cad6 = cad6 + cad4.substring(i, long1 - 1);
    }
    cad6 = Cadreverse(cad6, cad6.length());
    return cad6 + cad3;
  }

  public static String Cadreverse(String cadena, int longitud)
  {
    String cad1 = "";
    for (int i = longitud - 1; i >= 0; --i)
      cad1 = cad1 + cadena.charAt(i);
    return cad1;
  }

  public static String toEuro(String cadena, int decimales)
  {
    return ConvEuro(cadena, 100.0D, decimales);
  }

  public static String toEuro(String cadena)
  {
    return ConvEuro(cadena, 100.0D, 2);
  }

  public static String ConvEuro(String cadena, double factor, int decimales)
  {
    if (cadena == null)
      cadena = "";
    String cad = "";

    cadena = String.valueOf(Val(cadena) / factor);

    int pos = cadena.indexOf(".", 0);

    if (pos != -1) {
      String cad1 = cadena.substring(0, pos);
      String cad2 = cadena.substring(pos + 1, cadena.length());
      cadena = cad1 + "," + cad2;
    }

    cadena = Format(cadena, "###.###.###");

    pos = cadena.indexOf(",", 0);

    if (decimales == 0) {
      if (pos > 0) {
        return cadena.substring(0, pos);
      }
      return cadena;
    }
    if (pos == -1) {
      cad = ",";
      for (int i1 = 0; i1 < decimales; ++i1) {
        cad = cad + "0";
      }
      return cadena + cad;
    }

    if (pos + 1 + decimales <= cadena.length()) {
      return cadena.substring(0, pos + decimales + 1);
    }
    int i1 = pos + 1 + decimales - cadena.length();
    cad = "";
    for (int i2 = 0; i2 < i1; ++i2)
      cad = cad + "0";
    return cadena + cad;
  }

  public static boolean euro()
  {
    return true;
  }

  public static String nombreMoneda()
  {
    if (!euro()) {
      return "ptas.";
    }
    return "euros";
  }

  public static String nombreMoneda(String codMone, int tipo)
  {
    String texto;
    if ((codMone != null) && (codMone.equalsIgnoreCase("P")))
    {
      if (tipo == 0)
        texto = "ptas.";
      else
        texto = "pesetas.";
    } else {
      texto = "Euros";
    }return texto;
  }

  public static String rellenaTexto(int idText, int idOrga) {
    return rellenaTexto(idText, idOrga);
  }

  public static String rellenaTexto(int idText, double idOrga) {
    return rellenaTexto(idText, idOrga);
  }

    private static String TrimLineas(String cadena)
  {
    if ((cadena.indexOf("\n") == -1) && (cadena.indexOf(" ") == -1)) {
      return cadena;
    }
    int ini = cadena.length();
    int fin = 0;
    for (int i = 0; i < cadena.length(); ++i) {
      if (cadena.charAt(i) == ' ')
        continue;
      if (cadena.charAt(i) == '\n')
        continue;
      if (cadena.charAt(i) == '\t')
        continue;
      ini = i;
      break;
    }
    for (int i = cadena.length() - 1; i >= ini; --i) {
      if (cadena.charAt(i) == ' ')
        continue;
      if (cadena.charAt(i) == '\n')
        continue;
      if (cadena.charAt(i) == '\t')
        continue;
      fin = i;
      break;
    }
    if (ini > fin)
      cadena = "";
    else {
      cadena = cadena.substring(ini, fin + 1);
    }
    return cadena;
  }

  public String pdf_open_image_file(Node node)
  {
    Node n1 = node;
    String fichero = n1.getTextContent();
    Node n2 = n1.getFirstChild();
    while ((n1 != null) && (n1 != n2) && (((!validString(fichero)) || (!validString(Trim(fichero)))))) {
      fichero = n2.getTextContent();
      n1 = n2;
      n2 = n1.getFirstChild();
    }

    n1 = node;
    String tipo = n1.getAttributes().getNamedItem("tipo").getTextContent();
    Node padre = n1.getParentNode();
    float y;
    float x;
    if ((padre.getNodeName().equals("bloque")) && 
      (padre.getAttributes().getNamedItem("x") != null) && (padre.getAttributes().getNamedItem("y") != null)) {
      x = parseInt(n1.getAttributes().getNamedItem("x").getNodeValue()) + 
        parseInt(padre.getAttributes().getNamedItem("x").getNodeValue());
      y = -parseInt(n1.getAttributes().getNamedItem("y").getNodeValue()) + 
        parseInt(padre.getAttributes().getNamedItem("y").getNodeValue());
    } else {
      x = parseInt(n1.getAttributes().getNamedItem("x").getNodeValue());
      y = parseInt(n1.getAttributes().getNamedItem("y").getNodeValue());
    }
    Image pdfimage = null;
    try {
      String portalUrl = "";
      if (pathSalidaBase != null)
        portalUrl = pathSalidaBase;
      fichero = TrimLineas(fichero);

      String imagepath = fichero;
      try {
        pdfimage = Image.getInstance(imagepath);
      } catch (FileNotFoundException e) {
        int i = fichero.lastIndexOf("/");
        fichero = fichero.substring(0, i + 1) + fichero.substring(i + 1, fichero.length()).toLowerCase();

        imagepath = portalUrl + fichero;
        pdfimage = Image.getInstance(imagepath);
      }
    } catch (MalformedURLException e) {
      System.err.println("ERROR. No se puede abrir el fichero " + fichero);
    }
    catch (Exception e) {
      System.err.println("ERROR. No se puede cargar la imagen " + fichero + "." + e.getMessage());
    }

    if (pdfimage == null) {
      return "error";
    }
    pdfimage.setAlignment(4);
    float r = fichero.toLowerCase().indexOf("logoorga");
    float scale_y;
    float scale_x;
    if (r == -1.0F) {
      r = fichero.indexOf("FIRMA");
      if (r == -1.0F) {
        scale_x = 140.0F / pdfimage.getWidth();
        scale_y = 76.0F / pdfimage.getHeight();
      } else {
        scale_x = 76.0F / pdfimage.getWidth();
        scale_y = 76.0F / pdfimage.getHeight();
      }
    } else {
      scale_x = 86.0F / pdfimage.getWidth();
      scale_y = 156.0F / pdfimage.getWidth();
    }
    float escalado;
    if (scale_x >= scale_y)
      escalado = scale_y;
    else {
      escalado = scale_x;
    }
    pdfimage.setAbsolutePosition(x, y);
    pdfimage.scalePercent(escalado * 100.0F);
    PdfContentByte pcb2 = this.pdfwriter.getDirectContentUnder();
    try
    {
      pcb2.addImage(pdfimage);
      pcb2.stroke();
    }
    catch (Throwable e) {
      System.err.println("No se ha podido insertar la imagen");
    }

    return "ok";
  }

  public static void make_pdf(String xml_data, String pdf_data)
  {
    return ;
  }

  public String pdf_open_file()
  {
    if (this.oPDF == null)
      this.oPDF = pcbLoad();
    return "ok";
  }

  public String pdf_open_file(Node x)
  {
    if (this.oPDF == null)
      this.oPDF = pcbLoad();
    return "ok";
  }

  public String pdf_begin_page(Node node)
  {
    String giro = "v";
    Node n1 = node;
    if (n1.getAttributes().getNamedItem("orientacion") != null) {
      giro = n1.getAttributes().getNamedItem("orientacion").getNodeValue();
    }
    if (n1.getAttributes().getNamedItem("papel") != null) {
      String papel = n1.getAttributes().getNamedItem("papel").getTextContent();
      papel = papel.toUpperCase();
      if (papel.equals("A0"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(2380.0F, 3368.0F));
        else
          this.oPDF.setPageSize(new Rectangle(3368.0F, 2380.0F));
      else if (papel.equals("A1"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(1684.0F, 2380.0F));
        else
          this.oPDF.setPageSize(new Rectangle(2380.0F, 1684.0F));
      else if (papel.equals("A2"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(1190.0F, 1684.0F));
        else
          this.oPDF.setPageSize(new Rectangle(1684.0F, 1190.0F));
      else if (papel.equals("A3"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(842.0F, 1190.0F));
        else
          this.oPDF.setPageSize(new Rectangle(1190.0F, 842.0F));
      else if (papel.equals("A4"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(595.0F, 842.0F));
        else
          this.oPDF.setPageSize(new Rectangle(842.0F, 595.0F));
      else if (papel.equals("A5"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(421.0F, 595.0F));
        else
          this.oPDF.setPageSize(new Rectangle(595.0F, 421.0F));
      else if (papel.equals("A6"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(297.0F, 421.0F));
        else
          this.oPDF.setPageSize(new Rectangle(421.0F, 297.0F));
      else if (papel.equals("B5"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(501.0F, 709.0F));
        else
          this.oPDF.setPageSize(new Rectangle(709.0F, 501.0F));
      else if (papel.equals("LETTER"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(612.0F, 792.0F));
        else
          this.oPDF.setPageSize(new Rectangle(792.0F, 612.0F));
      else if (papel.equals("LEGAL"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(612.0F, 1008.0F));
        else
          this.oPDF.setPageSize(new Rectangle(1008.0F, 612.0F));
      else if (papel.equals("LEDGER"))
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(1224.0F, 792.0F));
        else
          this.oPDF.setPageSize(new Rectangle(792.0F, 1224.0F));
      else if (papel.equals("11X17")) {
        if (giro.equals("v"))
          this.oPDF.setPageSize(new Rectangle(792.0F, 1224.0F));
        else
          this.oPDF.setPageSize(new Rectangle(1224.0F, 792.0F));
      }
      else if (giro.equals("v"))
        this.oPDF.setPageSize(new Rectangle(595.0F, 842.0F));
      else {
        this.oPDF.setPageSize(new Rectangle(1684.0F, 1190.0F));
      }

    }

    boolean docNuevo = (this.oPDF != null) && (!this.oPDF.isOpen());
    if (docNuevo) {
      this.oPDF.open();
      this.pcb = this.pdfwriter.getDirectContent();
      this.pcb.setLineWidth(1.0F);
    }
    if (!docNuevo)
      this.oPDF.newPage();
    return "ok";
  }

  public String pdf_end_page(Node node)
  {
    return "ok";
  }

  public String pdf_close()
  {
    if (this.oPDF.isOpen())
      this.oPDF.close();
    this.pdfwriter.close();
    return "ok";
  }

  public String pdf_creador(String creador, String autor, String titulo)
  {
    this.oPDF.addCreator(creador);
    this.oPDF.addAuthor(autor);
    this.oPDF.addTitle(titulo);
    return "ok";
  }

  public String pdf_setfont(Node node)
  {
    Node n1 = node;
    String fuentes = "";
    int s = 12;
    if (n1 != null) {
      fuentes = n1.getAttributes().getNamedItem("fuente").getTextContent();
      if (n1.getAttributes().getNamedItem("size") != null)
        s = parseInt(n1.getAttributes().getNamedItem("size").getTextContent());
    }
    if (this.fuente.equals("F1"))
      fuentes = "Helvetica";
    else if (this.fuente.equals("F2"))
      fuentes = "Helvetica-Bold";
    else if (this.fuente.equals("F3"))
      fuentes = "Helvetica-Oblique";
    else if (this.fuente.equals("F4"))
      fuentes = "Helvetica-BoldOblique";
    else if (this.fuente.equals("F5"))
      fuentes = "Courier";
    else if (this.fuente.equals("F6"))
      fuentes = "Courier-Bold";
    else if (this.fuente.equals("F7"))
      fuentes = "Courier-Oblique";
    else if (this.fuente.equals("F8"))
      fuentes = "Courier-BoldOblique";
    else
      fuentes = "Helvetica-Bold";
    String encoding = "Cp1252";
    try {
      BaseFont bf = BaseFont.createFont(fuentes, encoding, false);
      this.pcb.setFontAndSize(bf, s);
      this.ff = new Font(bf);
      this.ff.setSize(s);
      this.ff.setFamily(encoding);
    } catch (Exception e) {
      //No se indica.
    }
    return "ok";
  }

  public String pdf_show_text(Node node)
  {
    Node n1 = node;
    String texto = n1.getTextContent();
    if (node.getFirstChild() != null)
      texto = node.getFirstChild().getTextContent();
    if (!validString(texto))
      texto = "";
    if (Trim(texto).endsWith("\n")) {
      int p = texto.lastIndexOf("\n");
      texto = texto.substring(0, p) + texto.substring(p + 1, texto.length());
    }
    String regex = "&#([0-9]+);/ig";
    Pattern ree = Pattern.compile(regex, 2);
    if ((Pattern.matches(regex, texto)) && 
      (texto.contains("&#8364"))) {
      String cb = "€";
      texto = texto.replaceAll(regex, cb);
    }

    Node padre = n1.getParentNode();
    float x = 0.0F; float y = 0.0F;

    if ((padre.getNodeName().equals("bloque")) && 
      (padre.getAttributes().item(1) != null) && (padre.getAttributes().item(2) != null)) {
      x = parseInt(n1.getAttributes().getNamedItem("x").getTextContent()) + 
        parseInt(padre.getAttributes().item(1).getTextContent());
      y = parseInt(n1.getAttributes().getNamedItem("y").getTextContent()) + 
        parseInt(padre.getAttributes().item(2).getTextContent());
    } else {
      x = parseInt(n1.getAttributes().getNamedItem("x").getTextContent());
      y = parseInt(n1.getAttributes().getNamedItem("y").getTextContent());
    }
    float x1 = (int)(this.oPDF.getPageSize().getWidth() - x - this.oPDF.rightMargin() / 2.0F);
    float y1 = (int)this.ff.getSize() + 2;
    if (n1.getAttributes().getNamedItem("x1") != null) {
      x1 = parseInt(n1.getAttributes().getNamedItem("x1").getTextContent());
    }

    if (n1.getAttributes().getNamedItem("y1") != null)
      y1 = parseInt(n1.getAttributes().getNamedItem("y1").getTextContent());
    if (x1 > this.oPDF.getPageSize().getWidth() - x - 1.0F) {
      x1 = (int)(this.oPDF.getPageSize().getWidth() - x - 1.0F);
    }
    Node codigo = n1.getAttributes().getNamedItem("codigo");
    Node modo = n1.getAttributes().getNamedItem("modo");
    int modostr = 0;
    if (modo != null)
      modostr = parseInt(n1.getAttributes().getNamedItem("modo").getNodeValue());
    if (modostr < 0) {
      modostr = 0;
    }
    if (modostr == -1)
      modostr = 0;
    else if (modostr == 1)
      modostr = 0;
    else if (modostr == 2)
      modostr = 0;
    else if (modostr == 3) {
      modostr = 0;
    }
    this.pcb.beginText();
    y -= 2.0F;
    if (y < 0.0F)
      y = 0.0F;
    this.pcb.setTextMatrix(x, y);
    if (n1.getAttributes().getNamedItem("codigo") != null) {
      int textnum = parseInt(texto);
      int b1 = textnum / 256;
      b1 = (b1 > 128) ? b1 - 256 : b1;
      int b2 = textnum % 256;
      b2 = (b2 > 128) ? b2 - 256 : b2;
      try {
        texto = new String(new byte[] { (byte)b1, (byte)b2 }, "UTF-16");
      }
      catch (Exception localException) {
      }
    }
    else {
      y = y - y1 + this.ff.getSize() + 2.0F;
    }

    ColumnText ct = new ColumnText(this.pcb);

    ct.setSimpleColumn(new Phrase(texto, this.ff), x, y, x + x1, y + y1, this.ff.getSize(), modostr);
    try
    {
      ct.go();
    } catch (Throwable t) {
      //No se indica
    }

    this.pcb.endText();

    return "ok";
  }

  public String pdf_show_text_continue(Node node)
  {
    Node n1 = node;
    String texto = n1.getTextContent();
    if (node.getFirstChild() != null)
      texto = node.getFirstChild().getTextContent();
    if ((texto == null) || (texto.length() == 0))
      texto = "";
    String regex = "&#([0-9]+);/ig";
    Pattern ree = Pattern.compile(regex, 2);
    if (Pattern.matches(regex, texto)) {
      String cb = "€";
      texto = texto.replaceAll(regex, cb);
    }
    this.pcb.beginText();
    if ((texto == null) || (texto.length() == 0))
      texto = "";
    this.pcb.showText(texto);
    this.pcb.endText();
    return "ok";
  }

  public String pdf_show_boxed(Node node)
  {
    Node n1 = node;
    String texto = n1.getTextContent();
    if (node.getFirstChild() != null)
      texto = node.getFirstChild().getTextContent();
    if ((texto == null) || (texto.length() == 0))
      texto = "";
    if (Trim(texto).endsWith("\n")) {
      int p = texto.lastIndexOf("\n");
      texto = texto.substring(0, p) + texto.substring(p + 1, texto.length());
    }

    Node padre = n1.getParentNode();
    float l = 0.0F;
    float t = 0.0F;

    if ((padre.getNodeName().equals("bloque")) && 
      (padre.getAttributes().item(1) != null) && (padre.getAttributes().item(2) != null)) {
      l = parseInt(n1.getAttributes().getNamedItem("x").getTextContent()) + 
        parseInt(padre.getAttributes().item(1).getTextContent());
      t = parseInt(n1.getAttributes().getNamedItem("y").getTextContent()) + 
        parseInt(padre.getAttributes().item(2).getTextContent());
    }
    else {
      l = parseInt(n1.getAttributes().getNamedItem("x").getTextContent());
      t = parseInt(n1.getAttributes().getNamedItem("y").getTextContent());
    }

    float w = this.oPDF.getPageSize().getWidth() - l - this.oPDF.rightMargin() / 2.0F;
    if (n1.getAttributes().getNamedItem("x1") != null)
      w = parseInt(n1.getAttributes().getNamedItem("x1").getTextContent());
    float h = this.ff.getSize() + 2.0F;
    if (n1.getAttributes().getNamedItem("y1") != null) {
      h = parseInt(n1.getAttributes().getNamedItem("y1").getTextContent());
    }
    if (w > this.oPDF.getPageSize().getWidth() - l) {
      w = (int)(this.oPDF.getPageSize().getWidth() - l);
    }
    t -= h / 2.0F;
    int modo = parseInt(n1.getAttributes().getNamedItem("modo").getTextContent());
    int modo1 = 0;
    this.pcb.beginText();
    this.pcb.setTextMatrix(l, t);
    if (modo == 1)
      modo1 = com.lowagie.text.Element.ALIGN_LEFT;
    else if (modo == 2)
      modo1 = com.lowagie.text.Element.ALIGN_CENTER;
    else if (modo == 3)
      modo1 = com.lowagie.text.Element.ALIGN_RIGHT;
    else if (modo == 4)
      modo1 = com.lowagie.text.Element.ALIGN_JUSTIFIED;
    else {
      modo1 = com.lowagie.text.Element.ALIGN_LEFT;
    }

    ColumnText ct = new ColumnText(this.pcb);
    ct.setSimpleColumn(new Phrase(texto, this.ff), l, t, l + w, t + h, this.ff.getSize(), modo1);
    try
    {
      ct.go();
    } catch (DocumentException e) {
      //No se indica.
    }
    this.pcb.stroke();
    this.pcb.endText();
    return "ok";
  }

  public String pdf_rect(Node node)
  {
    Node n1 = node;
    float x = parseInt(n1.getAttributes().getNamedItem("x").getTextContent());
    float y = parseInt(n1.getAttributes().getNamedItem("y").getTextContent());
    float dx = parseInt(n1.getAttributes().getNamedItem("x1").getTextContent());
    float dy = -parseInt(n1.getAttributes().getNamedItem("y1").getTextContent());
    this.pcb.moveTo(x, y);
    this.pcb.rectangle(x, y, dx, dy);
    this.pcb.stroke();
    return "ok";
  }

  public String pdf_line(String x1, String y1, String x2, String y2)
  {
    this.pcb.moveTo(parseInt(x1), parseInt(y1));
    this.pcb.lineTo(parseInt(x2), parseInt(y2));
    this.pcb.stroke();
    return "ok";
  }

  public static String codBarras(String dato)
  {
    String[] patrones = { "|| ||  ||  ", "||  || ||  ", "||  ||  || ", "|  |  ||   ", "|  |   ||  ", 
      "|   |  ||  ", "|  ||  |   ", "|  ||   |  ", "|   ||  |  ", "||  |  |   ", "||  |   |  ", 
      "||   |  |  ", "| ||  |||  ", "|  || |||  ", "|  ||  ||| ", "| |||  ||  ", "|  ||| ||  ", 
      "|  |||  || ", "||  |||  | ", "||  | |||  ", "||  |  ||| ", "|| |||  |  ", "||  ||| |  ", 
      "||| || ||| ", "||| |  ||  ", "|||  | ||  ", "|||  |  || ", "||| ||  |  ", "|||  || |  ", 
      "|||  ||  | ", "|| || ||   ", "|| ||   || ", "||   || || ", "| |   ||   ", "|   | ||   ", 
      "|   |   || ", "| ||   |   ", "|   || |   ", "|   ||   | ", "|| |   |   ", "||   | |   ", 
      "||   |   | ", "| || |||   ", "| ||   ||| ", "|   || ||| ", "| ||| ||   ", "| |||   || ", 
      "|   ||| || ", "||| ||| || ", "|| |   ||| ", "||   | ||| ", "|| ||| |   ", "|| |||   | ", 
      "|| ||| ||| ", "||| | ||   ", "||| |   || ", "|||   | || ", "||| || |   ", "||| ||   | ", 
      "|||   || | ", "||| |||| | ", "||  |    | ", "||||   | | ", "| |  ||    ", "| |    ||  ", 
      "|  | ||    ", "|  |    || ", "|    | ||  ", "|    |  || ", "| ||  |    ", "| ||    |  ", 
      "|  || |    ", "|  ||    | ", "|    || |  ", "|    ||  | ", "||    |  | ", "||  | |    ", 
      "|||| ||| | ", "||    | |  ", "|   |||| | ", "| |  ||||  ", "|  | ||||  ", "|  |  |||| ", 
      "| ||||  |  ", "|  |||| |  ", "|  ||||  | ", "|||| |  |  ", "||||  | |  ", "||||  |  | ", 
      "|| || |||| ", "|| |||| || ", "|||| || || ", "| | ||||   ", "| |   |||| ", "|   | |||| ", 
      "| |||| |   ", "| ||||   | ", "|||| | |   ", "|||| |   | ", "| ||| |||| ", "| |||| ||| ", 
      "||| | |||| ", "|||| | ||| ", "|| |    |  ", "|| |  |    ", "|| |  |||  ", "||   ||| | ||" };
    Map patronesB = new HashMap();
    patronesB.put("0", new Integer(16));
    patronesB.put("1", new Integer(17));
    patronesB.put("2", new Integer(18));
    patronesB.put("3", new Integer(19));
    patronesB.put("4", new Integer(20));
    patronesB.put("5", new Integer(21));
    patronesB.put("6", new Integer(22));
    patronesB.put("7", new Integer(23));
    patronesB.put("8", new Integer(24));
    patronesB.put("9", new Integer(25));
    Map patronesC = new HashMap();
    patronesC.put("CODEB", new Integer(100));
    patronesC.put("CODEA", new Integer(101));
    patronesC.put("FNC1", new Integer(102));
    patronesC.put("STARTA", new Integer(103));
    patronesC.put("STARTB", new Integer(104));
    patronesC.put("STARTC", new Integer(105));
    patronesC.put("STOP", new Integer(106));

    String quiet = "          ";

    if (dato == null)
      dato = "";
    try {
      dato = dato.replace(" ", "");
      if ((dato.length() > 0) && 
        (!StringUtils.isNumeric(dato)))
        dato = "";
    } catch (NumberFormatException e) {
      dato = "";
    }
    int check = ((Integer)patronesC.get("STARTC")).intValue() + ((Integer)patronesC.get("FNC1")).intValue();
    String codigo = quiet + patrones[((Integer)patronesC.get("STARTC")).intValue()] + 
      patrones[((Integer)patronesC.get("FNC1")).intValue()];
    int i = 0; int j = 2; int l = dato.length() / 2 * 2;

    while (i < l) {
      int num = parseInt(dato.substring(i, i + 2), 10);
      check += j * num;
      codigo = codigo + patrones[num];
      i += 2;
      ++j;
    }
    if (dato.length() > l) {
      String numstr = dato.substring(l, dato.length());
      check += j * ((Integer)patronesC.get("CODEB")).intValue();
      check += (j + 1) * ((Integer)patronesB.get(numstr)).intValue();
      codigo = codigo + patrones[((Integer)patronesC.get("CODEB")).intValue()] + 
        patrones[((Integer)patronesB.get(numstr)).intValue()];
    }
    check %= 103;
    codigo = codigo + patrones[check] + patrones[((Integer)patronesC.get("STOP")).intValue()] + quiet;

    return codigo;
  }

  public String pdf_barras(Node nodo)
  {
    Node n = nodo;
    String dato = n.getTextContent();
    if ((!validString(dato)) && (n.hasChildNodes()))
      dato = n.getFirstChild().getTextContent();
    float x = parseInt(n.getAttributes().getNamedItem("x").getTextContent());
    float y = parseInt(n.getAttributes().getNamedItem("y").getTextContent());
    if (validString(dato)) {
      dato = dato.replaceAll(" ", "");
      dato = dato.replaceAll("\n", "");
      dato = dato.replaceAll("\t", "");
    }
    String codigo = codBarras(dato);
    float h = 25.0F;
    float w = 0.72F;
    this.pcb.setLineWidth(w);
    for (int i = 0; i < codigo.length(); ++i) {
      char c = codigo.charAt(i);
      if (c == '|') {
        this.pcb.moveTo(x, y);
        this.pcb.lineTo(x, y + h);
        this.pcb.stroke();
      }
      x += w;
    }
    this.pcb.setLineWidth(1.0F);

    return "ok";
  }

  public String pdf_linea(Node nodo)
  {
    Node n = nodo;
    Node nxy = n.getAttributes().getNamedItem("x");
    int error = 0;
    float x = 0.0F;
    float y = 0.0F;
    float x1 = 0.0F;
    float y1 = 0.0F;
    String errormsg = "";
    if (nxy != null) {
      x = parseInt(nxy.getNodeValue());
    } else {
      errormsg = errormsg + "Debe introducirse un valor x para la linea<br>\n";
      error = 1;
    }

    nxy = n.getAttributes().getNamedItem("y");
    if (nxy != null) {
      y = parseInt(nxy.getNodeValue());
    } else {
      errormsg = errormsg + "Debe introducirse un valor y para la linea<br>\n";
      error = 1;
    }

    nxy = n.getAttributes().getNamedItem("x1");
    if (nxy != null) {
      x1 = parseInt(nxy.getNodeValue());
    } else {
      errormsg = errormsg + "Debe introducirse un valor x1 para la linea<br>\n";
      error = 1;
    }

    nxy = n.getAttributes().getNamedItem("y1");
    if (nxy != null) {
      y1 = parseInt(nxy.getNodeValue());
    } else {
      errormsg = errormsg + "Debe introducirse un valor y1 para la linea<br>\n";
      errormsg = errormsg + "Debe introducirse un valor y1 para la linea\n";
      error = 1;
    }

    this.pcb.saveState();
    Node ngrosor = n.getAttributes().getNamedItem("grosor");
    if (ngrosor != null) {
      int grosor = parseInt(ngrosor.getNodeValue());
      this.pcb.setLineWidth(grosor);
    }
    Node ndiscontinuo = n.getAttributes().getNamedItem("discontinuo");
    if (ndiscontinuo != null) {
      int discontinuo = parseInt(ndiscontinuo.getNodeValue());
      if (discontinuo == 1) {
        this.pcb.setLineDash(2.0F, 2.0F);
      }
    }
    if (error == 0) {
      this.pcb.moveTo(x, y);
      this.pcb.lineTo(x1, y1);
      this.pcb.stroke();
    }
    if (error != 0) {
      //No se indica.
    }

    this.pcb.restoreState();
    this.pcb.setLineWidth(1.0F);
    this.pcb.moveTo(0.0F, 0.0F);
    return "ok";
  }

  public void Campo(String nombre, String v)
  {
    Node nodo = selectSingleNode(this.fuente, ".//*[@ID='" + nombre + "']");
    if (nodo != null) {
      nodo.setTextContent(v);
      nodo.setNodeValue(v);
    }
  }

  public void BorrarCampo(String nombre)
  {
    Node nodo = selectSingleNode(this.fuente, ".//*[@ID='" + nombre + "']");
    if (nodo != null) {
      Node p = nodo.getParentNode();
      p.removeChild(nodo);
    }
  }

  

  public int Paginas2(String espacio, String desplazamiento, String w)
  {
    int salto;
    
    if (validString(desplazamiento))
      salto = parseInt(desplazamiento, 10);
    else
      salto = 780;
    int limite;
    
    if (validString(espacio))
      limite = parseInt(espacio, 10);
    else
      limite = 40;
    int ancho;
    
    if (validString(w))
      ancho = parseInt(w, 10);
    else {
      ancho = 8;
    }
    int despl = 0;
    Element[] campos = selectNodes(this.fuente, ".//*");
    Node nuevapag = null;

    int pag = 1;
    int offset = 0;
    int n1 = limite;

    Node pagina = selectSingleNode(this.fuente, ".//Pagina");
    Element[] pie = selectNodes(this.fuente, ".//pie");

    for (int j = 0; j < campos.length; ++j)
    {
      String nombreNodo = campos[j].getNodeName();

      if ((nombreNodo.equals("bloque")) || (nombreNodo.equals("Fila")) || (nombreNodo.equals("grupo")) || 
        (nombreNodo.equals("orga")) || (nombreNodo.equals("pie")) || (nombreNodo.equals("Paginas")) || 
        (nombreNodo.equals("Pagina")) || (nombreNodo.equals("imagenes")))
        continue;
      String n = campos[j].getAttributes().getNamedItem("y").getNodeValue();
      if (nombreNodo.equals("cajatexto")) {
        offset = cuentaLineas(campos[j].getTextContent(), 110) * ancho;
        n1 = parseInt(n) - offset;
      }
      else if (nombreNodo.equals("cuadro")) {
        String offset_n = campos[j].getAttributes().getNamedItem("y1").getNodeValue();
        offset = parseInt(offset_n);
        n1 = parseInt(n) - offset;
      } else {
        offset = 0;
        n1 = limite;
      }
      String t = campos[j].getTextContent();

      if (campos[j].getParentNode().getNodeName().equals("pie"))
        n = String.valueOf(limite);
      int n_num = parseInt(n);

      if ((n_num >= limite) && (n1 >= limite)) {
        continue;
      }

      if ((parseInt(n) + despl < limite) || (n1 + despl < limite))
      {
        nuevapag = pagina.cloneNode(true);

        Element[] seleccion = selectNodes(nuevapag, ".//*");
        removeAll(seleccion);

        pagina.getParentNode().insertBefore(nuevapag, pagina.getNextSibling());
        pagina = nuevapag;
        ++pag;

        if (pie != null) {
          for (int l = 0; l < pie.length; ++l) {
            Node nuevopie = pie[l].cloneNode(true);

            nuevapag.appendChild(nuevopie);
          }
        }

        despl = salto - parseInt(n);
      }

      if (campos[j].getAttributes().getNamedItem("salto") != null) {
        Element[] bloque = selectNodes(campos[j].getParentNode(), ".//*");

        for (int k = 0; k < bloque.length; ++k) {
          nombreNodo = bloque[k].getNodeName();
          if (bloque[k].getAttributes().getNamedItem("y") != null)
            bloque[k].getAttributes().getNamedItem("y").setNodeValue(
              String.valueOf(Val(bloque[k].getAttributes().getNamedItem("y").getNodeValue()) + despl));
          if (nombreNodo.equals("linea"))
            bloque[k].getAttributes().getNamedItem("y1")
              .setNodeValue(
              String.valueOf(Val(bloque[k].getAttributes().getNamedItem("y1").getNodeValue()) + 
              despl));
          if (nuevapag != null)
            nuevapag.appendChild(bloque[k]);
        }
      }
      else {
        campos[j].getAttributes().getNamedItem("y").setNodeValue(
          String.valueOf(Val(campos[j].getAttributes().getNamedItem("y").getNodeValue()) + despl));
        if (nombreNodo.equals("linea")) {
          campos[j].getAttributes().getNamedItem("y1").setNodeValue(
            String.valueOf(Val(campos[j].getAttributes().getNamedItem("y1").getNodeValue()) + despl));
        }
        nuevapag.appendChild(campos[j]);
      }

    }

    return pag;
  }

  public String Mostrar()
  {
    try
    {
      HashMap params = new HashMap();
      params.put("autor", "autor");
      params.put("titulo", super.getClass().getName());
      PDFParserHandler myparser = new PDFParserHandler(this, this.fuente);
      myparser.parse();
      return "ok";
    } catch (Exception eee) {
      //No se indica.
    }return "error";
  }

  public String Mostrar(HashMap<String, String> params)
  {
    try
    {
      PDFParserHandler myparser = new PDFParserHandler(this, this.fuente, params);
      myparser.parse();
      return "ok";
    } catch (Exception eee) {
      //No se indica.
    }return "error";
  }

  public String Fila(String id, String[] v, int desplazamiento)
  {
    return Fila(id, v, String.valueOf(desplazamiento));
  }

  public String Fila(String id, String[] v, String desplazamiento)
  {
    float despl = 10.0F;
    if (validString(desplazamiento)) {
      despl = parseInt(desplazamiento, 10);
    }

    Node fila = selectSingleNode(this.fuente, ".//Fila[@ID='" + id + "']");
    Node filaOrigen = fila;

    Element[] campos = selectNodes(fila, ".//*");
    if ((campos == null) || (campos.length == 0)) {
      return "error";
    }

    int restaV = 0;
    if (campos.length != 0)
      restaV = v.length % campos.length;
    for (int i = v.length - restaV; i < v.length; ++i) {
      v[i] = "";
    }

    Node cuadro = selectSingleNode(fila, ".//cuadro");
    if (cuadro != null) {
      cuadro.getParentNode().getParentNode().appendChild(cuadro);
      campos = selectNodes(fila, ".//*");
      if ((campos == null) || (campos.length == 0))
        return "error";
      String str1 = "SI";
    }
    int i;
    i = 0; int k = 0;
    for (k = 0; k < v.length / campos.length; ++k)
    {
      if (k > 0) {
        Node l = filaOrigen.cloneNode(true);
        try {
          fila.getParentNode().insertBefore(l, fila.getNextSibling());
        } catch (Exception exception) {
          String r = reportRuntimeError(exception);
          //No se indica
        }
        fila = l;
      }

      Element[] c = campos;

      for (int j = 0; j < campos.length; ++j) {
        c[j].setTextContent(v[(i++)]);
        float y = parseInt(c[j].getAttributes().getNamedItem("y").getNodeValue());
        Node linea = selectSingleNode(fila, ".//linea");
        if (k > 0) {
          c[j].getAttributes().getNamedItem("y").setNodeValue(String.valueOf(y - despl));

          if (c[j].getNodeName().equals("linea")) {
            c[j].getAttributes().getNamedItem("y1").setNodeValue(
              c[j].getAttributes().getNamedItem("y").getNodeValue());
          }
        }

      }

    }

    if (cuadro != null) {
      float y1 = parseInt(cuadro.getAttributes().getNamedItem("y1").getNodeValue());
      cuadro.getAttributes().getNamedItem("y1").setNodeValue(String.valueOf(y1 + despl * (k - 1)));
    }
    return String.valueOf(k - 1);
  }

  public String Fila2(String id, String[] v, String desplazamiento, String dup)
  {
    int despl;
    if (validString(desplazamiento))
      despl = parseInt(desplazamiento, 10);
    else
      despl = 10;
    int copia;
    if (!validString(dup))
      copia = 1;
    else {
      copia = 0;
    }
    Node fila = selectSingleNode(this.fuente, ".//Fila[@ID='" + id + "']");

    Element[] campos = selectNodes(fila, ".//*");

    while ((v.length % campos.length != 0) || (v.length == 0)) {
      v[v.length] = "";
    }

    Node cuadro = null;

    if (copia == 0) {
      cuadro = selectSingleNode(fila, ".//cuadro");
      if (cuadro != null) {
        cuadro.getParentNode().getParentNode().appendChild(cuadro);
        campos = selectNodes(fila, ".//*");
        String str1 = "SI";
      }
    }
    int i;
    i = 0; int k = 0;
    for (k = 0; k < v.length / campos.length; ++k)
    {
      if (k > 0) {
        Node l = fila.cloneNode(true);
        try {
          fila.getParentNode().insertBefore(l, fila.getNextSibling());
        } catch (Exception exception) {
          String r = reportRuntimeError(exception);
          //No se indica.
        }
        fila = l;
      }

      Element[] c = selectNodes(fila, ".//*");

      for (int j = 0; j < campos.length; ++j) {
        c[j].setTextContent(v[(i++)]);
        float y = parseInt(c[j].getAttributes().getNamedItem("y").getNodeValue());
        Node linea = selectSingleNode(fila, ".//linea");
        if (k > 0) {
          c[j].getAttributes().getNamedItem("y").setNodeValue(String.valueOf(y - despl));

          if (c[j].getNodeName().equals("linea")) {
            c[j].getAttributes().getNamedItem("y1").setNodeValue(String.valueOf(
              parseInt(c[j].getAttributes().getNamedItem("y1").getNodeValue()) - despl));
          }
        }
      }

    }

    if ((cuadro != null) && (copia == 0)) {
      float y1 = parseInt(cuadro.getAttributes().getNamedItem("y1").getNodeValue());
      cuadro.getAttributes().getNamedItem("y1").setNodeValue(String.valueOf(y1 + despl * (k - 1) + 5.0F));
    }

    return String.valueOf(k - 1);
  }

  public void CopiaBloque(String id, String desplazamiento, String coletilla)
  {
    int despl;
    if ((validString(desplazamiento)) && (!desplazamiento.equals("0")))
      despl = parseInt(desplazamiento, 10);
    else {
      despl = 10;
    }
    Node bloque = selectSingleNode(this.fuente, ".//bloque[@ID='" + id + "']");
    Element[] campos = selectNodes(bloque, ".//*");
    Element newbloque = (Element)bloque.cloneNode(true);
    newbloque.setAttribute("ID", newbloque.getAttributes().getNamedItem("ID").getNodeValue() + coletilla);
    try {
      bloque.getParentNode().insertBefore(newbloque, bloque.getNextSibling());
    } catch (Exception exception) {
      String r = reportRuntimeError(exception);
      //No se indica
    }

    campos = selectNodes(newbloque, ".//*");

    for (int j = 0; j < campos.length; ++j) {
      campos[j].setAttribute("ID", campos[j].getAttributes().getNamedItem("ID").getNodeValue() + coletilla);

      if ((!campos[j].getNodeName().equals("Fila")) && (!campos[j].getNodeName().equals("bloque"))) {
        if (campos[j].getAttributes().getNamedItem("type").getNodeValue().equals("109")) {
          campos[j].setTextContent("");
        }
        if (!campos[j].getNodeName().equals("linea")) {
          float y = parseInt(campos[j].getAttributes().getNamedItem("y").getNodeValue());
          campos[j].getAttributes().getNamedItem("y").setNodeValue(String.valueOf(y - despl));
        } else {
          float y = parseInt(campos[j].getAttributes().getNamedItem("y").getNodeValue());
          campos[j].getAttributes().getNamedItem("y").setNodeValue(String.valueOf(y - despl));
          float y1 = parseInt(campos[j].getAttributes().getNamedItem("y1").getNodeValue());
          campos[j].getAttributes().getNamedItem("y1").setNodeValue(String.valueOf(y1 - despl));
        }

      }

    }

    campos = selectNodes(newbloque, ".//Fila");
    if (campos.length > 0)
      while (campos.length > 1) {
        Node p = campos[1].getParentNode();
        p.removeChild(campos[1]);
        campos = selectNodes(newbloque, ".//Fila");
      }
  }

  public void MueveBloque(String nombre, String desplazamiento)
  {
    int despl = 0;
    if ((validString(desplazamiento)) && (!desplazamiento.equals("0")))
      despl = parseInt(desplazamiento, 10);
    int inicioPagina = 800;

    Node bloque = selectSingleNode(this.fuente, ".//bloque[@ID='" + nombre + "']");
    Element[] nodos = selectNodes(bloque, ".//*");

    for (int j = 0; j < nodos.length; ++j) {
      Node n = nodos[j];

      Node at = n.getAttributes().getNamedItem("y");
      if (at != null) {
        at.setNodeValue(String.valueOf(parseInt(at.getNodeValue()) - despl));
      }
      if (n.getNodeName().equals("linea")) {
        at = n.getAttributes().getNamedItem("y1");
        if (at != null)
          at.setNodeValue(String.valueOf(parseInt(at.getNodeValue()) - despl));
      }
    }
  }

  public void RellenaNumPag(int paginas, String nombre, String inicio)
  {
    int pag = 1;
    if (validString(inicio))
      pag = parseInt(inicio, 10);
    Element[] nodos = selectNodes(this.fuente, ".//Pagina");
    for (int j = 0; j < nodos.length; ++j) {
      Node n = nodos[j];
      Node pie = selectSingleNode(n, ".//*[@ID='" + nombre + "']");
      if (pie != null) {
        pie.setTextContent("P�gina " + pag + " de " + paginas);
        ++pag;
      }
    }
  }

  public void NuevaPagina(String id)
  {
    Node nodo = null;
    if (validString(id)) {
      nodo = selectSingleNode(this.fuente, ".//Pagina[@ID='" + id + "']");
    }
    if ((nodo == null) || (!validString(id)))
      nodo = selectSingleNode(this.fuente, ".//Pagina");
    if (nodo == null)
      return;
    Node clon = nodo.cloneNode(true);
    try {
      nodo.getParentNode().insertBefore(clon, nodo.getNextSibling());
    } catch (Exception exception) {
      String r = reportRuntimeError(exception);
      //No se indica.
    }
  }

  public void BorraPagina(String id)
  {
    Node nodo = null;
    if (validString(id))
      nodo = selectSingleNode(this.fuente, ".//Pagina[@ID='" + id + "']");
    else
      nodo = selectSingleNode(this.fuente, ".//Pagina");
    try {
      if (nodo != null) {
        Node p = nodo.getParentNode();
        p.removeChild(nodo);
      }
    } catch (Exception exception) {
      //No se indica
    }
  }

  public void Grupo(String nombre, int[] registro, String desplazamiento)
  {
    int despl;
    if (validString(desplazamiento))
      despl = parseInt(desplazamiento, 10);
    else
      despl = 50;
    int inicioPagina = 800;
    if (this.oPDF != null) {
      inicioPagina = (int)this.oPDF.getPageSize().getHeight();
    }
    Node grupo = selectSingleNode(this.fuente, ".//grupo[@ID='" + nombre + "']");
    Element[] nodos = selectNodes(grupo, ".//*");

    for (int j = 0; j < nodos.length; ++j) {
      Element n = nodos[j];
      Node c = n.cloneNode(true);
      Node at = c.getAttributes().getNamedItem("ID");
      if (at != null) {
        int atval = parseInt(at.getNodeValue());
        if ((validString(at.getNodeValue())) && (atval < registro.length) && (registro[atval] != -1))
          c.setTextContent(String.valueOf(registro[atval]));
      }
      n.getParentNode().getParentNode().appendChild(c);
    }

    boolean nuevaPagina = false;
    for (int j = 0; (j < nodos.length) && (!nuevaPagina); ++j) {
      Node n = nodos[j];

      Node at = n.getAttributes().getNamedItem("y");
      if ((at == null) || 
        (parseInt(at.getNodeValue()) >= despl)) continue;
      nuevaPagina = true;
      despl -= inicioPagina;

      Node vpag = grupo.getParentNode();
      Node npag = this.fuente.createElement("Pagina");
      npag.appendChild(grupo);
      vpag.getParentNode().insertBefore(npag, vpag.getNextSibling());
    }

    for (int j = 0; j < nodos.length; ++j) {
      Node n = nodos[j];

      Node at = n.getAttributes().getNamedItem("y");
      if (at != null) {
        at.setNodeValue(String.valueOf(parseInt(at.getNodeValue()) - despl));
      }
      if (n.getNodeName().equals("linea")) {
        at = n.getAttributes().getNamedItem("y1");
        if (at != null)
          at.setNodeValue(String.valueOf(parseInt(at.getNodeValue()) - despl));
      }
    }
  }

  public static Element selectSingleNode(Node doc, String nodename)
  {
    if (doc != null) {
      Element[] result = selectNodes(doc, nodename);
      if (result.length > 0)
        return result[0];
    }
    return null;
  }

  public static Element[] selectNodes(Node doc, String nodename)
  {
    if (doc != null) {
      XPathFactory xpfact = XPathFactory.newInstance();
      XPath xp = xpfact.newXPath();
      try {
        DTMNodeList o = (DTMNodeList)xp.evaluate(nodename, doc, XPathConstants.NODESET);
        Element[] result = new Element[o.getLength()];
        for (int i = 0; i < result.length; ++i)
          result[i] = ((Element)o.item(i));
        return result;
      }
      catch (XPathExpressionException e) {
        return new Element[0];
      }
    }
    return new Element[0];
  }

  public static boolean removeAll(Element[] lstElem)
  {
    boolean ok = true;
    if (lstElem == null)
      return ok;
    for (int i = 0; i < lstElem.length; ++i) {
      Node parent = lstElem[i].getParentNode();
      try {
        if (parent != null)
          parent.removeChild(lstElem[i]);
      } catch (DOMException e) {
        ok = false;
      }
    }
    return ok;
  }

  public com.lowagie.text.Document pcbLoad()
  {
    com.lowagie.text.Document docu = new com.lowagie.text.Document();
    try {
      if (this.pdfoutput == null)
        this.pdfwriter = PdfWriter.getInstance(docu, new FileOutputStream("plantilla_rellena.pdf"));
      else {
        this.pdfwriter = PdfWriter.getInstance(docu, this.pdfoutput);
      }
      return docu;
    } catch (Throwable t) {
      //No se indica.
    }return null;
  }

  public static String FechaActual()
  {
    return DateUtil.getTodayAsString("dd/MM/yyyy");
  }

  private static String join(String[] lstElem, String separator)
  {
    String cadena = "";
    for (int i = 0; i < lstElem.length; ++i) {
      if ((lstElem[i] != null) && (lstElem[i].length() != 0)) {
        cadena = cadena + lstElem[i] + separator;
      }
    }
    if (cadena.indexOf(separator) != -1)
      cadena = cadena.substring(0, cadena.length() - separator.length());
    return cadena;
  }

  public static String Trim(String cadena, String espaciador)
  {
    int length = cadena.length();
    char space = espaciador.charAt(0);
    int count = 0;
    while ((count < length) && (cadena.charAt(count) == space))
      ++count;
    int ini = count;
    count = cadena.length() - 1;
    while ((count > ini) && (cadena.charAt(count) == space))
      --count;
    int fin = count;
    return cadena.substring(ini, fin + 1);
  }

    public String traduc(String valor, String idioma)
  {
    return valor;
  }
	@Override
	public CallContext getCallContext() {
		return context;
	}

	@Override
	public void setCallContext(CallContext ctx) {
		context= ctx;
		
	}
}