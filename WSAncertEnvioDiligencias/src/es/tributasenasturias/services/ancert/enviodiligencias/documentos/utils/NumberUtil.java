package es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import org.apache.commons.lang.StringUtils;

public class NumberUtil
{
  public static String getDoubleAsFormatString(double numeroAFormatear)
  {
    DecimalFormat formateador = new DecimalFormat();
    return formateador.format(numeroAFormatear);
  }

  public static String getDoubleAsFormatString(double numeroAFormatear, int precisionDecimal, boolean isCompletarCerosParteDecimal)
  {
    String formateado = "";
    double numeroRedondeado = Math.round(numeroAFormatear * Math.pow(10.0D, precisionDecimal)) / Math.pow(10.0D, precisionDecimal);
    DecimalFormat formateador = new DecimalFormat();
    if (!isCompletarCerosParteDecimal) {
      formateado = formateador.format(numeroRedondeado);
    } else {
      String patron = "##,##0";
      if (precisionDecimal > 0) {
        patron = patron + "." + StringUtils.rightPad("", precisionDecimal, '0');
      }
      formateador = new DecimalFormat(patron);
      formateado = formateador.format(numeroRedondeado);
    }
    return formateado;
  }

  public static String getDoubleAsFormatString(String numeroAFormatearAsString, int precisionDecimal, boolean isCompletarCerosParteDecimal) {
    String formateado = "";
    if ((StringUtils.isNotBlank(numeroAFormatearAsString)) && (isNumberValid(numeroAFormatearAsString))) {
      Double numeroAsDouble = getDoubleFromString(numeroAFormatearAsString);
      formateado = getDoubleAsFormatString(numeroAsDouble.doubleValue(), precisionDecimal, isCompletarCerosParteDecimal);
    }
    return formateado;
  }

  public static String getDoubleAsString(double numeroAFormatear)
  {
    DecimalFormat formateador = new DecimalFormat();
    formateador.setGroupingSize(0);
    return formateador.format(numeroAFormatear);
  }

  public static String getIntAsFormatString(int numeroAFormatear)
  {
    DecimalFormat formateador = new DecimalFormat("###,###");
    return formateador.format(numeroAFormatear);
  }

  public static boolean isNumberValid(String numeroAEvaluar)
  {
    boolean isValid = true;
    NumberFormat parseador = NumberFormat.getInstance();

    if (StringUtils.isBlank(numeroAEvaluar)) {
      isValid = false;
    }
    else if (!validarGruposMiles(numeroAEvaluar)) {
      isValid = false;
    } else {
      ParsePosition posicionador = new ParsePosition(0);
      parseador.parse(numeroAEvaluar, posicionador);
      if ((posicionador.getErrorIndex() > -1) || (posicionador.getIndex() < numeroAEvaluar.length())) {
        isValid = false;
      }
    }

    return isValid;
  }

  private static boolean validarGruposMiles(String numeroAEvaluar)
  {
    DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
    char charSimboloMiles = simbolos.getGroupingSeparator();
    String simboloMiles = String.valueOf(charSimboloMiles);
    char charSimboloDecimales = simbolos.getDecimalSeparator();
    String simboloDecimales = String.valueOf(charSimboloDecimales);
    if (StringUtils.contains(numeroAEvaluar, simboloMiles))
    {
      while (numeroAEvaluar.length() > 0) {
        int posicionSimboloMiles = StringUtils.indexOf(numeroAEvaluar, simboloMiles);
        if (posicionSimboloMiles > 0) {
          String posibleGrupoMiles = StringUtils.mid(numeroAEvaluar, posicionSimboloMiles + 1, 3);
          if (StringUtils.contains(posibleGrupoMiles, simboloMiles)) {
            return false;
          }
          numeroAEvaluar = StringUtils.substringAfter(numeroAEvaluar, simboloMiles);
        }
        else {
          numeroAEvaluar = StringUtils.substringBefore(numeroAEvaluar, simboloDecimales);

          return numeroAEvaluar.length() == 3;
        }

      }

    }

    return true;
  }

  public static boolean isInteger(String numero) {
    Integer numeroEntero = getIntegerFromString(numero);

    return numeroEntero != null;
  }

  public static boolean isNotInt(String numero)
  {
    return !isInteger(numero);
  }

  public static boolean isNotDouble(String numero) {
    return !isDouble(numero);
  }

  public static boolean isDouble(String numero) {
    Double numeroDouble = getDoubleFromString(numero);

    return numeroDouble != null;
  }

  public static boolean isBaseDouble(String numero)
  {
    if ((numero == null) || ("".equals(numero)))
      return false;
    Double numeroDouble = Double.valueOf(getDoubleFromBaseString(numero));

    return numeroDouble != null;
  }

  public static Integer getIntegerFromString(String numeroAsString)
  {
    Integer numeroDevuelto = null;
    DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
    char charSimboloDecimales = simbolos.getDecimalSeparator();
    if ((StringUtils.isNotBlank(numeroAsString)) && (isNumberValid(numeroAsString)) && 
      (!StringUtils.contains(numeroAsString, charSimboloDecimales))) {
      NumberFormat parseador = NumberFormat.getInstance();
      try {
        Number numero = parseador.parse(numeroAsString);
        numeroDevuelto = Integer.valueOf(numero.intValue());
      } catch (ParseException e) {
        return null;
      }
    }

    return numeroDevuelto;
  }

  public static Integer getIntFromString(String numeroAsString)
  {
    return getIntegerFromString(numeroAsString);
  }

  public static boolean isInt(String numero)
  {
    return isInteger(numero);
  }

  public static Double getDoubleFromString(String numeroAsString) {
    Double numeroDevuelto = null;
    if ((StringUtils.isNotBlank(numeroAsString)) && (isNumberValid(numeroAsString))) {
      NumberFormat parseador = NumberFormat.getInstance();
      try {
        Number numero = parseador.parse(numeroAsString);
        numeroDevuelto = Double.valueOf(numero.doubleValue());
      } catch (ParseException e) {
        return null;
      }
    }
    return numeroDevuelto;
  }

  public static int cantidadDigitosDecimales(Double numero) {
    String numeroAsString = getDoubleAsString(numero.doubleValue());
    return cantidadDigitosDecimales(numeroAsString);
  }

  private static int cantidadDigitosDecimales(String numero) {
    DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
    char charSimboloDecimales = simbolos.getDecimalSeparator();
    String simboloDecimales = String.valueOf(charSimboloDecimales);
    String parteDecimal = StringUtils.substringAfter(numero, simboloDecimales);
    if (StringUtils.isBlank(parteDecimal)) {
      return 0;
    }
    return parteDecimal.length();
  }

  public static int cantidadDigitosEnteros(Double numero) {
    String numeroAsString = getDoubleAsString(numero.doubleValue());
    return cantidadDigitosEnteros(numeroAsString);
  }

  private static int cantidadDigitosEnteros(String numero) {
    DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
    char charSimboloDecimales = simbolos.getDecimalSeparator();
    String simboloDecimales = String.valueOf(charSimboloDecimales);
    String parteEntera = StringUtils.substringBefore(numero, simboloDecimales);
    if (StringUtils.isBlank(parteEntera)) {
      return 0;
    }
    return parteEntera.length();
  }

  public static double getDoubleFromBaseString(String numeroAsString)
  {
    if (numeroAsString == null)
      return getDoubleFromString(numeroAsString).doubleValue();
    int pos = numeroAsString.indexOf(",");

    if ((pos != -1) && (numeroAsString.lastIndexOf(",") != pos))
      numeroAsString = StringUtils.replace(numeroAsString, ",", "");
    else if (pos != -1) {
      return getDoubleFromString(numeroAsString).doubleValue();
    }
    pos = numeroAsString.indexOf(".");

    if ((pos != -1) && (numeroAsString.lastIndexOf(".") != pos))
      try {
        return getDoubleFromString(numeroAsString).doubleValue();
      } catch (NullPointerException e) {
        e.printStackTrace();
        return 0.0D;
      }
    try {
      return Double.parseDouble(numeroAsString); } catch (NumberFormatException nfe) {
    }
    return 0.0D;
  }

  public static String redondearDecimales(String numeroARedondear, int precisionDecimal, boolean isCompletarCerosParteDecimal)
  {
    String redondeo = numeroARedondear;
    if (numeroARedondear != null) {
      if (numeroARedondear.indexOf(",") > 0)
        numeroARedondear = numeroARedondear.replace(",", ".");
      try
      {
        Double dARedondear = Double.valueOf(Double.parseDouble(numeroARedondear));
        redondeo = getDoubleAsFormatString(dARedondear.doubleValue(), precisionDecimal, isCompletarCerosParteDecimal);
      } catch (NumberFormatException nfe) {
        redondeo = numeroARedondear;
      }
    }
    return redondeo;
  }

  @Deprecated
  public static Double getStringAsDouble(String sNumero) {
    Double numero = null;
    if (sNumero != null) {
      if (sNumero.indexOf(",") > 0)
        sNumero = sNumero.replace(",", ".");
      try
      {
        numero = Double.valueOf(Double.parseDouble(sNumero));
      } catch (NumberFormatException nfe) {
        numero = null;
      }
    }
    return numero;
  }

  public static boolean isNumberZero(String sNumero) {
    Double numero = null;
    if (sNumero != null) {
      if (sNumero.indexOf(",") > 0)
        sNumero = sNumero.replace(",", ".");
      try
      {
        numero = Double.valueOf(Double.parseDouble(sNumero));
      } catch (NumberFormatException nfe) {
        numero = null;
      }
    }
    return (numero != null) && (numero.doubleValue() == 0.0D);
  }

  public static String getImporteFormateado(String centimos) {
    String euros = "";

    double dcent = getDoubleFromBaseString(centimos);
    euros = getDoubleAsFormatString(dcent / 100.0D);

    return euros;
  }

  public static String Redondear(String importe, Integer decimales)
  {
    Double exponente = Double.valueOf(Math.pow(new Double(10.0D).doubleValue(), new Double(decimales.intValue()).doubleValue()));
    Double producto = Double.valueOf(new Float(importe).floatValue() * exponente.doubleValue());

    String strProducto = producto.toString();
    int coma = strProducto.lastIndexOf(".");
    Integer intValor;
    if (coma != -1)
    {
      String valorRedondeo = strProducto.substring(coma + 1, 1);
      Float fltRedondeo = new Float(valorRedondeo);
      Float valor;
      if (fltRedondeo.floatValue() >= 5.0F)
        valor = Float.valueOf(fltRedondeo.floatValue() + 1.0F);
      else {
        valor = fltRedondeo;
      }
      intValor = Integer.valueOf(valor.intValue());
    }
    else
    {
      intValor = Integer.valueOf(producto.intValue());
    }
    importe = String.valueOf(intValor.intValue() / exponente.doubleValue());

    coma = importe.lastIndexOf(".");
    if (coma == -1)
      importe = importe + ".0000000000";
    coma = importe.lastIndexOf(".");
    importe = importe.substring(0, coma + decimales.intValue() + 1);
    return importe;
  }

  public static String toEuro(String importe) {
    return toEuro(importe, null, null);
  }

  public static String toEuro(String importe, Integer decimales, Integer formato) {
    String importe_f = "";
    String tmp = "";
    String punto = ".";
    String frac = "";

    if ((decimales != null) && (decimales.intValue() != 0)) {
      decimales = Integer.valueOf(2);
    }
    if ((formato != null) && (formato.intValue() != 0)) {
      formato = Integer.valueOf(1);
    }
    if ((importe == null) || (importe.length() == 0) || ("null".equals(importe))) {
      return "";
    }

    String signo = "";
    if (importe.startsWith("-"))
    {
      signo = "-";
      importe = importe.substring(1, importe.length());
    }

    Integer operacionImporte = Integer.valueOf(new Integer(importe).intValue() / 100);
    importe = operacionImporte.toString();

    int coma = importe.lastIndexOf(".");

    if (coma == -1)
    {
      importe = importe + ".0000000000";
      coma = importe.lastIndexOf(".");
      if (decimales.intValue() == -1)
        importe = importe.substring(0, coma + 3);
      else {
        importe = importe.substring(0, coma + decimales.intValue() + 1);
      }
    }

    if (decimales.intValue() != -1) {
      importe = Redondear(importe, decimales);
    }
    coma = importe.lastIndexOf(".");
    if (decimales.intValue() == 0)
    {
      frac = "";
      importe = importe.substring(0, coma);
    }
    else
    {
      frac = "," + importe.substring(coma + 1, importe.length());
      for (int i = frac.length(); i < decimales.intValue() + 1; ++i) {
        frac = frac + "0";
      }
      if ((frac.length() < 3) && (decimales.intValue() == -1))
        frac = frac + "0";
      importe = importe.substring(0, coma);
    }

    if (formato.intValue() == 1)
    {
      Integer longitud = Integer.valueOf(importe.length());
      Integer resto = Integer.valueOf(longitud.intValue() % 3);
      if (("null".equals(importe)) || (longitud.intValue() == 0)) {
        return signo + importe + frac;
      }

      if ((importe.length() == 3) || (importe.length() < 3)) {
        return signo + importe + frac;
      }

      if (resto.intValue() != 0)
      {
        importe_f = importe.substring(0, resto.intValue());
        importe_f = importe_f.concat(punto);
      }
      Integer cociente = Integer.valueOf((longitud.intValue() - resto.intValue()) / 3);
      Integer pos_ini = resto;
      for (int i = 0; i < cociente.intValue(); ++i)
      {
        tmp = importe.substring(pos_ini.intValue(), pos_ini.intValue() + 3);
        if (i != 0)
        {
          tmp = punto.concat(tmp);
          importe_f = importe_f.concat(tmp);
        }
        else
        {
          importe_f = importe_f.concat(tmp);
        }
        pos_ini = Integer.valueOf(pos_ini.intValue() + 3);
      }
      return signo + importe_f + frac;
    }

    return signo + importe + frac;
  }
}