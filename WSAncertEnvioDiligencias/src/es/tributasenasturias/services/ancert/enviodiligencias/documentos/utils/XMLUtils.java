package es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Clase heredada de los proyectos de Accenture. Es una dependencia de la generación de PDF.
 * No se utilizará para nada más.
 * @author crubencvs
 *
 */
public class XMLUtils
{
  Document doc = null;
  Node currentParent = null;
  Node currentNode = null;
  Element baseNode = null;
  String baseNodeName = "<peti>";

  public static Document crearDocument()
  {
    Document doc = null;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docbld = factory.newDocumentBuilder();
      doc = docbld.newDocument();
    } catch (ParserConfigurationException e) {
      doc = null;
    } finally {
      doc = null;
    }
    return doc;
  }

  public void crearXMLDoc()
    throws RemoteException
  {
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docbld = factory.newDocumentBuilder();
      this.doc = docbld.newDocument();
      this.currentParent = this.doc;
    } catch (ParserConfigurationException e) {
      this.doc = null;
      throw new RemoteException("NO EXISTE XML", e);
    }
  }

  public void reParentar(int nodeLoc)
  {
    if ((nodeLoc < 0) && (this.currentParent != this.baseNode)) {
      this.currentNode = this.currentParent;
      this.currentParent = this.currentParent.getParentNode();
    }
    if (nodeLoc > 0) {
      this.currentParent = this.currentNode;
      if ((this.currentParent.getChildNodes() != null) && (this.currentParent.getChildNodes().getLength() != 0))
        this.currentNode = this.currentParent.getFirstChild();
    }
  }

  public void crearNode(String nombre, String valor)
  {
    crearNode(nombre, valor, null, null);
  }

  public void crearNode(String nombre, String valor, String[] atributos, String[] atribValores)
  {
    Element subNode = this.doc.createElement(nombre);
    if ((atributos != null) && (atribValores != null) && (atributos.length == atribValores.length)) {
      for (int i = 0; i < atributos.length; ++i) {
        subNode.setAttribute(atributos[i], atribValores[i]);
      }
    }
    if ((valor != null) && (!valor.equals("")))
      subNode.setTextContent(valor);
    this.currentParent.appendChild(subNode);
    this.currentNode = subNode;
  }

  public void crearParamNode(String atribValor, String txtValor, String txtTipo, String txtFormato)
  {
    crearNode("param", "", new String[] { "id" }, new String[] { atribValor });
    reParentar(1);
    crearNode("valor", txtValor);
    crearNode("tipo", txtTipo);
    crearNode("formato", txtFormato);
    reParentar(-1);
  }

  public void abrirPetiProcNode(String valor1, String valor2, String[] atributos1, String[] atribValores1, String[] atributos2, String[] atribValores2)
  {
    crearNode("peti", valor1, atributos1, atribValores1);
    reParentar(1);
    crearNode("proc", valor2, atributos2, atribValores2);
    reParentar(1);
  }

  public void cerrarPetiProcNode()
  {
    reParentar(-1);
    reParentar(-1);
  }

  public String informarXMLDoc()
    throws RemoteException
  {
    try
    {
      TransformerFactory tfactory = TransformerFactory.newInstance();
      DOMSource source = new DOMSource(this.doc);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(bos);

      Transformer transformer = tfactory.newTransformer();
      transformer.transform(source, result);
      String resultdata = bos.toString("UTF-8");
      int posicionFin = resultdata.indexOf("/>");
      while (posicionFin != -1) {
        int posicion = resultdata.lastIndexOf("<", posicionFin);
        if (posicion == -1) break; if (posicion == posicionFin - 1)
          break;
        String nombreNode = resultdata.substring(posicion + 1, posicionFin);
        resultdata = resultdata
          .replaceAll("<" + nombreNode + "/>", "<" + nombreNode + "></" + nombreNode + ">");
        posicionFin = resultdata.indexOf("/>");
      }

      resultdata = resultdata.replaceAll("<formato/>", "<formato></formato>");
      if (resultdata.indexOf("<", 1) != -1)
        resultdata = resultdata.substring(resultdata.indexOf("<", 1));
      return resultdata;
    } catch (TransformerException e) {
      throw new RemoteException("NO RETORNA XML", e);
    } catch (UnsupportedEncodingException e) {
      throw new RemoteException("UNSUPPORTED ENCODING EXCEPTION", e);
    }
  }

  public static HashMap<String, Object> compilaXMLDoc(String xmlTexto)
    throws RemoteException
  {
    return compilaXMLDoc(xmlTexto, null, null, true);
  }

  public static boolean esXMLValido(String xml)
  {
    if ((xml == null) || (xml.length() == 0))
      return false;
    try {
      compilaXMLDoc(xml);
      return true;
    } catch (Throwable t) {
    }return false;
  }

  public static String getEncoding(String xml)
  {
    return getEncoding(xml, "UTF-8");
  }

  public static String getEncoding(String xml, String baseEncoding)
  {
    String encoding = baseEncoding;
    if ((xml.startsWith("<?xml")) || (xml.startsWith("<?XML"))) {
      String xmlCabecera = xml.substring(0, xml.indexOf(">") + 1);
      if ((xmlCabecera != null) && (xmlCabecera.indexOf("encoding") != -1)) {
        xmlCabecera = xmlCabecera.substring(xmlCabecera.indexOf("encoding"), xmlCabecera.indexOf(">"));
        int startpos = xmlCabecera.indexOf("\"");
        if (startpos == -1)
          startpos = xmlCabecera.indexOf("'");
        int endpos = xmlCabecera.indexOf("\"", startpos + 1);
        if (endpos == -1)
          endpos = xmlCabecera.indexOf("'", startpos + 1);
        encoding = xmlCabecera.substring(startpos + 1, endpos);
      }
    }
    return encoding;
  }

  public static String borrarDoctype(String xml)
  {
    int startpos = xml.indexOf("<!DOCTYPE", 0);
    if (startpos == -1)
      startpos = xml.indexOf("<!doctype");
    int endpos = xml.indexOf(">", startpos);
    if ((startpos != -1) && (endpos != -1)) {
      xml = xml.substring(0, startpos) + xml.substring(endpos + 1, xml.length() - 1);
    }
    return xml;
  }

  @SuppressWarnings("unchecked")
public static Object compilaXMLObject(String xmlTexto, DefaultHandler saxhandler)
    throws RemoteException
  {
    List lstErrores = new ArrayList();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docbld = factory.newDocumentBuilder();
      Document doc = null;

      String encoding = getEncoding(xmlTexto);
      xmlTexto = borrarDoctype(xmlTexto);
      InputStream is = new ByteArrayInputStream(xmlTexto.getBytes(encoding));
      if (saxhandler != null) {
        SAXParserFactory parfact = SAXParserFactory.newInstance();
        SAXParser saxer = parfact.newSAXParser();
        saxer.parse(is, saxhandler);
        return saxhandler;
      }
      if ((xmlTexto != null) && (xmlTexto.length() > 0))
        doc = docbld.parse(is);
      return doc;
    }
    catch (ParserConfigurationException e) {
      throw new RemoteException("Error en Parser", e);
    } catch (SAXParseException e) {
      lstErrores.add("Error en Parser SAX");
      return null;
    } catch (Throwable e) {
      throw new RemoteException("NO RETORNA XML", e);
    }
  }

  @SuppressWarnings("unchecked")
public static HashMap<String, Object> compilaXMLDoc(String xmlTexto, String[] estructuras, String[] parametros, boolean recuperarParam)
    throws RemoteException
  {
    HashMap map = new HashMap();
    List lstErrores = new ArrayList();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docbld = factory.newDocumentBuilder();
      Document doc = null;

      String encoding = getEncoding(xmlTexto);
      InputStream is = new ByteArrayInputStream(xmlTexto.getBytes(encoding));
      doc = docbld.parse(is);
      Element baseElement = doc.getDocumentElement();
      NodeList lstEstructura = baseElement.getChildNodes();

      HashMap mapParams = null;
      HashMap mapEstruct = null;
      if ((parametros != null) && (parametros.length != 0)) {
        mapParams = new HashMap();
        for (int i = 0; i < parametros.length; ++i)
          mapParams.put(parametros[i], new Integer(i));
        if ((estructuras != null) && (estructuras.length != 0)) {
          mapEstruct = new HashMap();
          for (int i = 0; i < parametros.length; ++i) {
            mapEstruct.put(i, estructuras[i]);
          }
        }
      }
      for (int i = 0; i < lstEstructura.getLength(); ++i) {
        Node nodeActual = lstEstructura.item(i);
        if (nodeActual.getNodeName().equals("estruc")) {
          String nombreEstruct = ((Element)nodeActual).getAttribute("nombre");
          if ((nombreEstruct == null) || (nombreEstruct.equals("")))
            nombreEstruct = "estruc" + i;
          NodeList lstFilas = nodeActual.getChildNodes();

          nodeActual.getAttributes();
          if ((lstFilas != null) && (lstFilas.getLength() != 0)) {
            ArrayList lstTotalFilas = new ArrayList();

            for (int j = 0; j < lstFilas.getLength(); ++j) {
              Node nodeColumna = lstFilas.item(j);
              NodeList lstColumnas = nodeColumna.getChildNodes();
              if ((lstColumnas != null) && (lstColumnas.getLength() > 0)) {
                ArrayList lstCadenas = new ArrayList();
                if ((mapParams != null) && (!mapParams.isEmpty())) {
                  int limit = (lstColumnas.getLength() > mapParams.size()) ? lstColumnas.getLength() : 
                    mapParams.size();
                  for (int k = 0; k < limit; ++k) {
                    lstCadenas.add("");
                    if (recuperarParam)
                      lstCadenas.add("");
                  }
                }
                for (int k = 0; k < lstColumnas.getLength(); ++k) {
                  String key = lstColumnas.item(k).getNodeName();
                  String valor = lstColumnas.item(k).getTextContent();
                  int tipo = lstColumnas.item(k).getNodeType();
                  if (valor == null)
                    valor = "";
                  if ((tipo != 3) && (tipo != 8)) {
                    if ((mapParams == null) && (!valor.equals(""))) {
                      if (recuperarParam)
                        lstCadenas.add(lstColumnas.item(k).getNodeName());
                      lstCadenas.add(valor);
                    } else if ((mapParams != null) && (mapParams.containsKey(key))) {
                      int pos = ((Integer)mapParams.get(key)).intValue();
                      String estruct = nombreEstruct;
                      if (mapEstruct != null) {
                        estruct = (String)mapEstruct.get(pos);
                        if ((estruct == null) || (estruct.equals("")))
                          estruct = nombreEstruct;
                      }
                      if (recuperarParam) {
                        lstCadenas.set(2 * pos, lstColumnas.item(k).getNodeName());
                        lstCadenas.set(2 * pos + 1, valor);
                      } else if (estruct.equals(nombreEstruct)) {
                        lstCadenas.set(pos, valor);
                      }
                    }
                  }
                }

                if ((mapParams != null) && (!mapParams.isEmpty())) {
                  int size = (recuperarParam) ? 2 * mapParams.size() : mapParams.size();
                  int step = (recuperarParam) ? 2 : 1;
                  int msize = (mapEstruct != null) ? step * mapEstruct.size() - 1 : size - 1;
                  for (int m = msize; m >= 0; m -= step) {
                    String lstcadstr = "";
                    for (int y = 0; y < lstCadenas.size(); ++y)
                      lstcadstr = lstcadstr + (String)lstCadenas.get(y);
                    String estruc_actual = nombreEstruct;
                    if (mapEstruct != null) {
                      estruc_actual = (String)mapEstruct.get(m);
                      if ((estruc_actual == null) || (estruc_actual.equals("")))
                        estruc_actual = nombreEstruct;
                    }
                    if ((!estruc_actual.equals("")) && (!estruc_actual.equals(nombreEstruct))) {
                      lstCadenas.remove(m);
                      if (recuperarParam)
                        lstCadenas.remove(m - 1);
                      size -= step;
                    }
                  }
                  while (lstCadenas.size() > size)
                    lstCadenas.remove(lstCadenas.size() - 1);
                }
                String[] strCadenas = new String[lstCadenas.size()];
                for (int k = 0; k < lstCadenas.size(); ++k) {
                  strCadenas[k] = ((String)lstCadenas.get(k));
                }
                lstTotalFilas.add(strCadenas);
              }
            }
            Object[] totalFilas = lstTotalFilas.toArray();
            map.put(nombreEstruct, totalFilas);
          } else {
            map.put(nombreEstruct, new Object[0]);
          }
        }
        else if (nodeActual.getNodeName().startsWith("error")) {
          lstErrores.add(nodeActual.getTextContent());
        }
      }
      if (!lstErrores.isEmpty())
        map.put("ERROR", lstErrores.toArray());
      return map;
    } catch (ParserConfigurationException e) {
      throw new RemoteException("NO RETORNA XML", e);
    } catch (SAXException e) {
      lstErrores.add("Error: documento no valido");
      map.put("ERROR", lstErrores.toArray());
      return map;
    } catch (DOMException e) {
      lstErrores.add("Error: documento no valido");
      map.put("ERROR", lstErrores.toArray());
      return map;
    } catch (Throwable e) {
      throw new RemoteException("NO RETORNA XML", e);
    }
  }

  @SuppressWarnings("unchecked")
public static HashMap<Integer, Map<String, String>> convertirObjectStringArrayHashMap(Object[] arg0)
  {
    HashMap result = new HashMap();
    int count = 0;
    for (int i = 0; i < arg0.length; ++i) {
      String[] cadenapares = (String[])arg0[i];
      if (cadenapares == null)
        continue;
      Integer indice = new Integer(count);
      ++count;
      HashMap lineaResult = new HashMap();
      if ((cadenapares != null) && (cadenapares.length != 0)) {
        for (int j = 0; j < cadenapares.length; j += 2)
          if ((cadenapares[(j + 1)] != null) && (!cadenapares[(j + 1)].equals("")))
            lineaResult.put(cadenapares[j], cadenapares[(j + 1)]);
      }
      result.put(indice, lineaResult);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
public static String getParamIndexedHashMap(HashMap<String, Object> map, String estruct, Integer index, String param)
  {
    if ((map != null) && 
      (map.get(estruct) != null) && (map.get(estruct) instanceof HashMap))
      return (String)((Map)((HashMap)map.get(estruct)).get(index)).get(param);
    return "";
  }

  public static String[] recuperaFilaXMLDoc(String xmlTexto, String[] parametros, boolean recuperarParam)
    throws RemoteException
  {
    return recuperaFilaXMLDoc(xmlTexto, null, parametros, recuperarParam);
  }

  @SuppressWarnings("unchecked")
public static String[] recuperaFilaXMLDoc(String xmlTexto, String[] estructuras, String[] parametros, boolean recuperarParam)
    throws RemoteException
  {
    HashMap map = compilaXMLDoc(xmlTexto, estructuras, parametros, recuperarParam);
    if ((map != null) && (map.containsKey("ERROR"))) {
      Object[] lstErrores = (Object[])map.get("ERROR");
      String listaErrores = "";
      for (int i = 0; i < lstErrores.length; ++i)
        listaErrores = listaErrores + lstErrores[i].toString() + " \n";
      throw new WebServiceException(listaErrores);
    }if (map != null) {
      Iterator it = map.keySet().iterator();
      if (it.hasNext()) {
        String key = (String)it.next();
        Object[] lstFilas = (Object[])map.get(key);
        for (int i = 0; i < lstFilas.length; ++i) {
          String[] lstProps = (String[])lstFilas[i];
          if (lstProps == null) continue; if (lstProps.length == 0)
            continue;
          String[] lstStrings = new String[lstProps.length];
          for (int j = 0; j < lstProps.length; ++j) {
            lstStrings[j] = new String(lstProps[j]);
          }
          return lstStrings;
        }
      }
    }
    return null;
  }

  public static Object[] getNodes(String XMLIn)
    throws RemoteException
  {
    return getNodes(XMLIn, null, false);
  }

  public static Object[] getNodes(String XMLIn, boolean p_nombrecompleto)
    throws RemoteException
  {
    return getNodes(XMLIn, null, p_nombrecompleto);
  }

  public static Object[] getNodes(String XMLIn, String[] nodeNames)
    throws RemoteException
  {
    return getNodes(XMLIn, nodeNames, false);
  }

  @SuppressWarnings("unchecked")
public static Object[] getNodes(String XMLIn, String[] nodeNames, boolean p_nombrecompleto)
    throws RemoteException
  {
    Document doc = (Document)compilaXMLObject(XMLIn, null);
    HashMap mapAttributes = new HashMap();
    HashMap mapTextValue = new HashMap();
    HashMap mapNames = new HashMap();
    if (nodeNames != null) {
      for (int i = 0; i < nodeNames.length; ++i) {
        if (nodeNames[i].lastIndexOf("/") != -1)
          mapNames.put(nodeNames[i].substring(nodeNames[i].lastIndexOf("/") + 1), "");
        else
          mapNames.put(nodeNames[i], "");
      }
    }
    Object[] lstResults = new Object[2];
    Node nodeActual = doc;
    boolean devuelta = false;
    String nameRoot = "";
    while (nodeActual != null) {
      String name = nodeActual.getNodeName();
      String value = nodeActual.getTextContent();
      boolean chkNodeHijo = false;
      boolean notNodeTexto = (nodeActual.getNodeType() != 3) && 
        (nodeActual.getNodeType() != 8);
      if ((notNodeTexto) && (nodeActual.hasChildNodes()) && (value != null) && (value.length() != 0)) {
        for (int i = 0; i < nodeActual.getChildNodes().getLength(); ++i)
        {
          if ((nodeActual.getChildNodes().item(i).getNodeType() == 3) || 
            (nodeActual.getChildNodes().item(i).getNodeType() == 8)) continue;
          if (nodeActual.getChildNodes().item(i).getNodeName().equals(name))
            chkNodeHijo = true;
          int pos = value.indexOf(nodeActual.getChildNodes().item(i).getTextContent());
          int length = nodeActual.getChildNodes().item(i).getTextContent().length();
          if ((pos != -1) && (length != 0)) {
            value = value.substring(0, pos) + value.substring(pos + length, value.length());
          }
        }
      }
      if ((notNodeTexto) && (!p_nombrecompleto) && (!chkNodeHijo) && (!devuelta) && ((
        (mapNames.containsKey(name)) || (mapNames.isEmpty()))))
      {
        List textoActual = (List)mapTextValue.get(name);
        if (textoActual == null)
          textoActual = new ArrayList();
        textoActual.add(value);
        List attribActual = (List)mapAttributes.get(name);
        if (attribActual == null)
          attribActual = new ArrayList();
        attribActual.add(nodeActual.getAttributes());
        mapTextValue.put(name, textoActual);
        mapAttributes.put(name, attribActual);
      } else if ((notNodeTexto) && (p_nombrecompleto) && (!chkNodeHijo) && (!devuelta) && ((
        (mapNames.containsKey(nameRoot + name)) || (mapNames.isEmpty())))) {
        List textoActual = (List)mapTextValue.get(nameRoot + name);
        if (textoActual == null)
          textoActual = new ArrayList();
        textoActual.add(value);
        List attribActual = (List)mapAttributes.get(nameRoot + name);
        if (attribActual == null)
          attribActual = new ArrayList();
        mapTextValue.put(nameRoot + name, textoActual);
        mapAttributes.put(nameRoot + name, attribActual);
      }
      if ((nodeActual.hasChildNodes()) && (!devuelta)) {
        if (nodeActual.getNodeType() != 9)
          nameRoot = nameRoot + nodeActual.getNodeName() + "/";
        nodeActual = nodeActual.getFirstChild();
      } else if (nodeActual.getNextSibling() != null) {
        devuelta = false;
        nodeActual = nodeActual.getNextSibling();
      } else {
        nodeActual = nodeActual.getParentNode();
        if ((nameRoot.lastIndexOf("/") == -1) || (nameRoot.indexOf("/") == nameRoot.length() - 1))
          nameRoot = "";
        else
          nameRoot = nameRoot.substring(0, 1 + nameRoot.lastIndexOf("/", nameRoot.length() - 2));
        devuelta = true;
      }
      if (nodeActual.equals(doc))
        break;
    }
    lstResults[0] = mapAttributes;
    lstResults[1] = mapTextValue;
    return lstResults;
  }

  public static Element[] selectNodes(Node doc, String nodename)
  {
    XPathFactory xpfact = XPathFactory.newInstance();
    XPath xp = xpfact.newXPath();
    try {
      if (doc == null)
        return new Element[0];
      DTMNodeList o = (DTMNodeList)xp.evaluate(nodename, doc, XPathConstants.NODESET);
      Element[] result = new Element[o.getLength()];
      for (int i = 0; i < result.length; ++i)
        result[i] = ((Element)o.item(i));
      return result;
    } catch (XPathExpressionException e) {
    }
    return new Element[0];
  }

  @SuppressWarnings({ "unchecked", "unused" })
@Deprecated
  private static Element[] selectNodesDebug(Node doc, String nodename)
  {
    String name = nodename;
    String nodePath = "";
    String actualPath = "";
    @SuppressWarnings("unused")
	String conds = "";
    ArrayList lstElements = new ArrayList();
    Node nodeActual = doc;

    if (nodename.startsWith("..")) {
      nodeActual = nodeActual.getParentNode();
      nodename.replaceFirst("..", "");
    }
    if (nodename.startsWith(".")) {
      nodename.replaceFirst(".", "");
    }
    if (nodename.startsWith("//"))
      nodename.replaceFirst("//", "/");
    else if (nodename.startsWith("/")) {
      nodeActual = nodeActual.getOwnerDocument();
    }
    if ((nodename.contains("[")) && (nodename.contains("]"))) {
      int ini = nodename.indexOf("[");
      int fin = nodename.lastIndexOf("]");
      if (ini < fin) {
        conds = nodename.substring(ini + 1, fin);
        nodename = nodename.substring(0, ini) + nodename.substring(fin + 1, nodename.length());
      }
    }
    if ((nodename.contains("/")) && (!nodename.endsWith("/"))) {
      name = nodename.substring(nodename.lastIndexOf("/") + 1, nodename.length());
      nodePath = nodename.substring(0, 1 + nodename.lastIndexOf("/"));
    }
    boolean devuelta = false;
    while (nodeActual != null) {
      String nameActual = nodeActual.getNodeName();
      if ((nameActual.equals(name)) && (nodeActual.getNodeType() != 8) && 
        (nodeActual.getNodeType() != 3) && (!devuelta) && ((
        (nodePath.equals("")) || (actualPath.endsWith("/" + nodePath)) || (actualPath.equals(nodePath))))) {
        lstElements.add(nodeActual);
      }
      if ((nodeActual.hasChildNodes()) && (!devuelta)) {
        actualPath = actualPath + nameActual + "/";
        nodeActual = nodeActual.getFirstChild();
      } else if (nodeActual.getNextSibling() != null) {
        devuelta = false;
        nodeActual = nodeActual.getNextSibling();
      } else {
        nodeActual = nodeActual.getParentNode();
        if ((actualPath.lastIndexOf("/") == -1) || (actualPath.indexOf("/") == actualPath.length() - 1))
          actualPath = "";
        else
          actualPath = actualPath.substring(0, 1 + actualPath.lastIndexOf("/", actualPath.length() - 2));
        devuelta = true;
      }
      if (nodeActual.equals(doc)) break; if (nodeActual.equals(doc.getParentNode()))
        break;
    }
    Element[] lstResults = new Element[lstElements.size()];
    for (int i = 0; i < lstElements.size(); ++i) {
      lstResults[i] = ((Element)lstElements.get(i));
    }
    return lstResults;
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

  @SuppressWarnings("unchecked")
public static String setNodes(String XMLIn, String[] nodeNames, String[] nodeValues)
    throws RemoteException
  {
    HashMap mapNames = new HashMap();
    if ((nodeNames == null) || (nodeNames.length == 0) || (nodeValues == null) || (nodeValues.length == 0))
      return XMLIn;
    for (int i = 0; i < nodeNames.length; ++i) {
      mapNames.put(nodeNames[i], nodeValues[i]);
    }
    Document doc = (Document)compilaXMLObject(XMLIn, null);
    Node nodeActual = doc;
    boolean devuelta = false;
    while (nodeActual != null) {
      String name = nodeActual.getNodeName();
      if (name.contains("/"))
      {
        String[] nameParents = name.split("/");
        name = nameParents[(nameParents.length - 1)];
        if (mapNames.containsKey(name)) {
          Node nodeParent = nodeActual;
          for (int i = nameParents.length - 2; i >= 0; --i) {
            nodeParent = nodeParent.getParentNode();
            if (!nodeParent.getNodeName().equals(nameParents[i])) {
              name = nodeActual.getNodeName();
              break;
            }
          }
        }
      }
      if (mapNames.containsKey(name)) {
        nodeActual.setTextContent((String)mapNames.get(name));
        mapNames.remove(name);
      }
      if ((nodeActual.hasChildNodes()) && (!devuelta)) {
        nodeActual = nodeActual.getFirstChild();
      } else if (nodeActual.getNextSibling() != null) {
        devuelta = false;
        nodeActual = nodeActual.getNextSibling();
      } else {
        nodeActual = nodeActual.getParentNode();
        devuelta = true;
      }
      if (nodeActual.equals(doc))
        break;
    }
    try {
      TransformerFactory tfactory = TransformerFactory.newInstance();
      DOMSource source = new DOMSource(doc);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(bos);

      Transformer transformer = tfactory.newTransformer();
      transformer.transform(source, result);
      String resultdata = bos.toString();
      return resultdata;
    } catch (TransformerException e) {
      throw new RemoteException("Error en XML Transformer", e);
    }
  }

  public static String borraEspacios(String input)
  {
    if (input != null) {
      input = input.replaceAll(" ", "");
      input = input.replaceAll("\t", "");
      input = input.replaceAll("\n", "");
    }
    return input;
  }

  public static String borraEspaciosInicioFin(String input)
  {
    String input2 = input;
    if (input2 != null) {
      input2 = input2.replaceAll(" ", "");
      input2 = input2.replaceAll("\t", "");
      input2 = input2.replaceAll("\n", "");
      if (input2.length() == 0)
        return "";
      int posIni = input.indexOf(input2.substring(0, 1));
      int posFin = input.lastIndexOf(input2.substring(input2.length() - 1, input2.length()));
      if (posFin == -1)
        posFin = input.length();
      return input.substring(posIni, posFin + 1);
    }
    return input2;
  }

  public static String fillRandomXML(String XMLIn)
  {
    String XMLOut = XMLIn;
    Document doc = null;
    String[] randset = { "A", "B", "C", "D", "E", "F", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
    try {
      doc = (Document)compilaXMLObject(XMLIn, null);
    } catch (RemoteException e) {
      e.printStackTrace();
      return XMLIn;
    }
    Node nodeActual = doc;
    boolean devuelta = false;
    while (nodeActual != null) {
      String value = nodeActual.getTextContent();
      boolean chkNodeHijo = false;
      if ((nodeActual.hasChildNodes()) && (value != null) && (value.length() != 0)) {
        for (int i = 0; i < nodeActual.getChildNodes().getLength(); ++i)
        {
          if (nodeActual.getChildNodes().item(i).getNodeType() == 3) {
            chkNodeHijo = true;
          }
        }
      }
      if ((!chkNodeHijo) && (!nodeActual.hasChildNodes()) && (nodeActual.getNodeType() != 3)) {
        int randsize = (int)(Math.random() * 5.0D) + 1;
        String randstr = "";
        for (int i = 0; i < randsize; ++i) {
          int randpos = (int)(Math.random() * randset.length);
          randstr = randstr.concat(randset[randpos]);
        }
        nodeActual.appendChild(doc.createTextNode(randstr));
      }
      if ((nodeActual.hasChildNodes()) && (!devuelta)) {
        nodeActual = nodeActual.getFirstChild();
      } else if (nodeActual.getNextSibling() != null) {
        devuelta = false;
        nodeActual = nodeActual.getNextSibling();
      } else {
        nodeActual = nodeActual.getParentNode();
        devuelta = true;
      }
      if (nodeActual.equals(doc))
        break;
    }
    try {
      TransformerFactory tfactory = TransformerFactory.newInstance();
      DOMSource source = new DOMSource(doc);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(bos);

      Transformer transformer = tfactory.newTransformer();
      transformer.transform(source, result);
      XMLOut = bos.toString();
    } catch (TransformerException e) {
      //RemoteException e2 = new RemoteException("Error en XML Transformer", e);
      return XMLIn;
    }

    return XMLOut;
  }

  public static Node borrarNodo(Node objXMLDoc, String nodo)
  {
    Element[] nodelist = selectNodes(objXMLDoc, nodo);
    for (int i = 0; i < nodelist.length; ++i) {
      Node parent = nodelist[i].getParentNode();
      parent.removeChild(nodelist[i]);
    }

    return objXMLDoc;
  }

  public static Node obtenerNodo(Node objXMLDoc, String nodo, String id)
  {
    Element[] elementList = selectNodes(objXMLDoc, nodo + "[@id=" + id + "]");
    for (int i = 0; i < elementList.length; ++i) {
      Element elem = elementList[i];
      if (nodo.endsWith(elem.getNodeName())) {
        String idvalue = elem.getAttribute("id");
        if ((idvalue != null) && (idvalue.equals(id)))
          return elem;
      }
    }
    return null;
  }

  public static Document obtenerXML(HashMap<String, Object> session)
  {
    String xml = (String)session.get("xml");
    if (esXMLValido(xml)) {
      try {
        return (Document)compilaXMLObject(xml, null);
      } catch (RemoteException e) {
      }
    }

    return null;
  }

  public static void CambiaNodo(Node obj, String nodo, String valor)
  {
    if (valor != null) {
      NodeList e = ((Element)obj).getElementsByTagName(nodo);
      if (e.getLength() != 0)
        e.item(0).setTextContent(valor);
    }
  }

  public static void CambiaNodo(Node obj, String valor)
  {
    if (obj != null)
      obj.setTextContent(valor);
  }

  public static String leerNodo(Node objXMLDoc, String nodo)
  {
    return valorNodo(objXMLDoc, "datos/modelo/" + nodo);
  }

  public static String valorNodo(Node objXMLDoc, String nodo)
  {
    Element[] elementlist = selectNodes(objXMLDoc, nodo);
    int i = 0; if (i < elementlist.length) {
      Element elem = elementlist[i];
      return elem.getTextContent();
    }
    return "";
  }

  public static void XMLaMayusculas(Node objXMLDoc)
  {
    Element[] elementlist = selectNodes(objXMLDoc, "datos/modelo");
    for (int i = 0; i < elementlist.length; ++i) {
      Node node = elementlist[i];
      NodeList nodelist = node.getChildNodes();
      for (int j = 0; j < nodelist.getLength(); ++j) {
        Node nodo2 = nodelist.item(j);
        nodo2.setTextContent(nodo2.getTextContent().toUpperCase());
      }
    }
  }

  public static Node addNode(Node objXMLDoc, String nodo)
  {
    Node objNewNode = null;
    if (objXMLDoc.getOwnerDocument() != null)
      objNewNode = objXMLDoc.getOwnerDocument().createElement(nodo);
    return objXMLDoc.appendChild(objNewNode);
  }

  public static byte[] compressGZIP(String str)
  {
    @SuppressWarnings("unused")
	String strout = "";
    byte[] bitout = (byte[])null;
    try {
      GZIPOutputStream gzipOutputStream = null;
      String encoding = "ISO-8859-1";
      ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes(encoding));
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      gzipOutputStream = new GZIPOutputStream(output);
      byte[] buf = new byte[512];
      int len;
      while ((len = input.read(buf)) > 0)
      {
        gzipOutputStream.write(buf, 0, len);
      }
      gzipOutputStream.finish();
      strout = output.toString(encoding);
      strout = new String(output.toByteArray(), encoding);
      bitout = output.toByteArray();
      input.close();
      gzipOutputStream.close();
    } catch (IOException ex) {
    }

    return bitout;
  }

  public static String decompressGZIP(byte[] str)
  {
    return decompressGZIP(str, "ISO-8859-1");
  }

  public static String decompressGZIP(byte[] str, String encoding)
  {
    String strout = "";
    try {
      GZIPInputStream gzipInputStream = null;
      ByteArrayInputStream input = new ByteArrayInputStream(str);
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      gzipInputStream = new GZIPInputStream(input);
      byte[] buf = new byte[512];
      int len;
      while ((len = gzipInputStream.read(buf)) > 0)
      {
        output.write(buf, 0, len);
      }
      output.close();
      if ((encoding == null) || (encoding.equals(""))) {
        encoding = "ISO-8859-1";
      }
      strout = output.toString(encoding);
      gzipInputStream.close();
    } catch (IOException ex) {
    }

    return strout;
  }

  public static void decompressGZIP(InputStream input, OutputStream output)
  {
    try
    {
      GZIPInputStream gzipInputStream = null;
      gzipInputStream = new GZIPInputStream(input);
      byte[] buf = new byte[512];
      int len;
      while ((len = gzipInputStream.read(buf)) > 0)
      {
        output.write(buf, 0, len);
      }
      output.flush();
    } catch (IOException ex) {
    }
  }

  public static void decompressGZIP(String filename, String urlEntrada, OutputStream output)
  {
    try {
      ZipFile z = new ZipFile(new File(urlEntrada));
      GZIPInputStream gzipInputStream = null;
      gzipInputStream = new GZIPInputStream(z.getInputStream(z.getEntry(filename)));
      byte[] buf = new byte[512];
      int len;
      while ((len = gzipInputStream.read(buf)) > 0)
      {
        output.write(buf, 0, len);
      }
      output.flush();
    } catch (IOException ex) {
    }
  }

  public static void compressGZIP(InputStream input, OutputStream output)
  {
    try {
      GZIPOutputStream gzipOutputStream = null;
      gzipOutputStream = new GZIPOutputStream(output);
      byte[] buf = new byte[512];
      int len;
      while ((len = input.read(buf)) > 0)
      {
        gzipOutputStream.write(buf, 0, len);
      }
      gzipOutputStream.flush();
    } catch (IOException ex) {
    }
  }

  @SuppressWarnings("unchecked")
public static ArrayList<String> getCompFile(String urlEntrada, String outputFile) throws Exception
  {
    ZipFile z = new ZipFile(new File(urlEntrada));
    ArrayList lstNombres = new ArrayList();
    if (z != null) {
      Enumeration enu = z.entries();
      while (enu.hasMoreElements()) {
        ZipEntry ze = (ZipEntry)enu.nextElement();
        lstNombres.add(ze.getName());
      }
    }
    return lstNombres;
  }

  public static Document getStringXmlAsDocument(String stringXML)
    throws Exception
  {
    Document document = null;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = factory.newDocumentBuilder();
      String encoding = getEncoding(stringXML);
      InputStream is = new ByteArrayInputStream(stringXML.getBytes(encoding));
      document = docBuilder.parse(is);
    } catch (Exception e) {
      throw new Exception("No se pudo convertir el stringXML a un Document valido");
    }
    return document;
  }

  public static String leerValor(String stringXML, String tag)
  {
    String valor = null;
    Document document = null;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = factory.newDocumentBuilder();
      String encoding = getEncoding(stringXML);
      InputStream is = new ByteArrayInputStream(stringXML.getBytes(encoding));
      document = docBuilder.parse(is);

      Node nodo = document;
      valor = valorNodo(nodo, tag);
    }
    catch (Exception e) {
    }

    return valor;
  }

  @SuppressWarnings("unchecked")
public static boolean compareStruct(String xml1, String xml2)
  {
    boolean result = false;
    try {
      Object[] obj1 = getNodes(xml1);
      Object[] obj2 = getNodes(xml2);

      ArrayList nodos1 = nodosNames(obj1);
      ArrayList nodos2 = nodosNames(obj2);

      @SuppressWarnings("unused")
	boolean r1 = nodos1.containsAll(nodos2);
      @SuppressWarnings("unused")
	boolean r2 = nodos2.containsAll(nodos1);

      return (nodos1.containsAll(nodos2)) && (nodos2.containsAll(nodos1));
    }
    catch (RemoteException re) {
      result = false;
    }
    return result;
  }

  @SuppressWarnings("unchecked")
private static ArrayList<String> nodosNames(Object[] obj)
  {
    ArrayList nodos = new ArrayList();
    for (int i = 0; i < obj.length; ++i) {
      HashMap map = (HashMap)obj[i];
      Iterator it = map.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry)it.next();
        nodos.add((String)entry.getKey());
      }
    }
    return nodos;
  }
  public static NodeList getAllNodes (Document doc, String tagNodo)
	{
		NodeList list=null;
		try
		{
			//Compilamos una expresión XPath que devuelva todos los nodos con el tag, en cualquier parte del documento.
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression exp = xpath.compile("//*[local-name()='"+tagNodo+"']");
			list = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			list=null;
		}
		return list;
	}
  /**
   * Recupera todos los nodos con un tag determinado bajo un nodo particular.
   * @param nodo Nodo del que dependen los nodos a buscar
   * @param tagNodo Tag del nodo
   * @return Lista de nodos.
   */
  public static NodeList getAllNodes (Node nodo, String tagNodo)
	{
		NodeList list=null;
		try
		{
			//Compilamos una expresión XPath que devuelva todos los nodos con el tag, en cualquier parte del documento.
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression exp = xpath.compile("//*[local-name()='"+tagNodo+"']");
			list = (NodeList)exp.evaluate(nodo, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			list=null;
		}
		return list;
	}
  /**
	 * Recupera la lista de nodos que cumplen una condición XPATH.
	 * @param doc Documento en el que buscar.
	 * @param condicion Condición a tener en cuenta.
	 * @return Lista de nodos DOM que cumplan esa condicion coincida.
	 */
	public static NodeList getAllNodesCondicion (Document doc, String condicion)
	{
		NodeList list=null;
		try
		{
			//Compilamos una expresión XPath que devuelva todos los nodos con la condición
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression exp = xpath.compile(condicion);
			list = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			list=null;
		}
		return list;
	}
  /**
	 * Recupera el nesimo nodo con el tag indicado que sea hijo del nodo pasado por parámetro.
	 * @param padre Nodo padre en el que se buscarán sus hijos.
	 * @param tag Tag del nodo hijo a buscar.
	 * @param occur Número de ocurrencia del nodo (1,2,3, para primero, segundo, tercero, etc).
	 * @return Nodo hijo o null si no se ha encontrado.
	 */
	public static Node getNthChildNode (Node padre, String tag, int occur)
	{
		Node res=null;
		Node hijo=null;
		int i=1;
		if (padre!=null)
		{
			if (padre.hasChildNodes() && padre.getChildNodes().getLength()>=occur)
			{
				for (hijo=padre.getFirstChild();hijo!=null && i<=occur;hijo=hijo.getNextSibling())
				{
					if (hijo.getNodeType()==Node.ELEMENT_NODE)
					{
					if (hijo.getLocalName().equalsIgnoreCase(tag) && (i==occur))
					{
						res = hijo;// El texto del nodo está en el nodo texto hijo.
						break;
					}
					else if (hijo.getLocalName().equalsIgnoreCase(tag))
					{
						i++;
					}
					}
				}
			}
		}
		return res;
	}
	/**
	 * Recupera el primer nodo hijo con ese tag.
	 * @param padre Nodo padre.
	 * @param tag Etiqueta del nodo hijo a buscar.
	 * @return Primer hijo con esa etiqueta o null si no se ha encontrado.
	 */
	public static Node getFirstChildNode(Node padre, String tag)
	{
		Node res=null;
		res = getNthChildNode (padre, tag,1);
		return res;
	}
	/**
	 * Recupera el texto de un nodo.
	 * @param nodo
	 * @return El valor del nodo, o null si no tiene valor.
	 */
	public static String getNodeText(Node nodo)
	{
		String valor=null;
		Node hijo=null;
		if (nodo!=null)
		{
			if (nodo.hasChildNodes())
			{
				for (hijo=nodo.getFirstChild();hijo!=null;hijo=hijo.getNextSibling())
				{
					if (hijo.getNodeType()==Node.TEXT_NODE||hijo.getNodeType()==Node.CDATA_SECTION_NODE)
					{
						valor = hijo.getNodeValue();// El texto del nodo está en el nodo texto hijo.
						break;
					}
				}
			}
		}
		return valor;
	}/**
	 * Recupera el nodo que contiene el valor (nodo Texto | CData)
	 * @param nodo Nodo del que se quiere recuperar el nodo valor.
	 * @return
	 */
	private static Node getNodoValor (Node nodo)
	{
		Node nodoValor=null;
		Node hijo=null;
		if (nodo!=null)
		{
			if (nodo.hasChildNodes())
			{
				for (hijo=nodo.getFirstChild();hijo!=null;hijo=hijo.getNextSibling())
				{
					if (hijo.getNodeType()==Node.TEXT_NODE||hijo.getNodeType()==Node.CDATA_SECTION_NODE)
					{
						nodoValor = hijo;// El texto del nodo está en el nodo texto hijo.
						break;
					}
				}
			}
		}
		return nodoValor;
	}
	/**
	 * Inserta el texto en el nodo.
	 * @param doc Documento en el que se encuentra el texto a insertar.
	 * @param nodo Nodo en el que insertar texto (en un nodo hijo )
	 * @param texto  Texto.
	 */
	public static  void setNodeText (Document doc,Node nodo, String texto)
	{
		Node valorAnterior = getNodoValor (nodo);
		if (valorAnterior != null) //Existía un nodo previo con valor
		{
			valorAnterior.setNodeValue(texto);
		}
		else
		{
			Node textN = doc.createTextNode(texto);
			nodo.appendChild(textN);
		}
	}
	/**
	 * Devuelve la representación XML de objeto doc.
	 * @param doc Documento que se devolverá  como texto
	 * @return Cadena que representa el texto de XML.
	 * @throws XMLDOMDocumentException
	 */
	public static String getXMLText(Document doc) throws XMLDOMDocumentException
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no"); // No indentar salida, dejar tal como está.
	
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
	
			String xmlString = result.getWriter().toString();
			return xmlString;
		}
		catch (TransformerConfigurationException ex)
		{
			throw new XMLDOMDocumentException ("Imposible devolver la representación en texto de este XML:" + ex.getMessage(),ex);
		}
		catch (TransformerException ex)
		{
			throw new XMLDOMDocumentException ("Imposible devolver la representación en texto de este XML:" + ex.getMessage(),ex);
		}
	}
	/**
	 * Devuelve la representación XML de un nodo.
	 * @param node Nodo que se devolverá  como texto
	 * @return Cadena que representa el texto de XML.
	 * @throws XMLDOMDocumentException
	 */
	public static String getXMLText(Node node) throws XMLDOMDocumentException
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no"); // No indentar salida, dejar tal como está.
	
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(node);
			transformer.transform(source, result);
	
			String xmlString = result.getWriter().toString();
			return xmlString;
		}
		catch (TransformerConfigurationException ex)
		{
			throw new XMLDOMDocumentException ("Imposible devolver la representación en texto de este XML:" + ex.getMessage(),ex);
		}
		catch (TransformerException ex)
		{
			throw new XMLDOMDocumentException ("Imposible devolver la representación en texto de este XML:" + ex.getMessage(),ex);
		}
	}
	/**
	 * Parsea un xml que se incluye a través de un string, y lo asigna al objeto documento 
	 * @param pXml. String que contiene XML.
	 * @return Documento Xml
	 * @throws XMLDOMDocumentException
	 */
	public static Document parseXml(String pXML) throws XMLDOMDocumentException
	{
		javax.xml.parsers.DocumentBuilderFactory fact;
		javax.xml.parsers.DocumentBuilder db;
		Document doc=null;
		try
		{
			fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			db= fact.newDocumentBuilder();
			org.xml.sax.InputSource inStr = new org.xml.sax.InputSource ();
			inStr.setCharacterStream(new java.io.StringReader(pXML));
			doc = db.parse(inStr);
			return doc;
		}
		catch (javax.xml.parsers.ParserConfigurationException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml:"+ ex.getMessage(),ex);
		}
		catch (org.xml.sax.SAXException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml:" + ex.getMessage(),ex);
		}
		catch (java.io.IOException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml:" + ex.getMessage(),ex);
		}
	}
}
