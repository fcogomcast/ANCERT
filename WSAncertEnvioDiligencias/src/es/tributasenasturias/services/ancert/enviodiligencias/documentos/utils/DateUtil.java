package es.tributasenasturias.services.ancert.enviodiligencias.documentos.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
  public static final String FORMATO_RECUPERADO_FORMULARIOS_WEB = "dd/MM/yyyy";

  public static String getFechaAsFormatString(String fechaUHoraAFormatear, String formatoEntrada, String formatoDeSalida)
  {
    SimpleDateFormat formateador = new SimpleDateFormat(formatoEntrada);
    formateador.setLenient(false);
    String fechaUHoraFormateada = null;
    try {
      Date fechaUHora = formateador.parse(fechaUHoraAFormatear);
      formateador.applyPattern(formatoDeSalida);
      fechaUHoraFormateada = formateador.format(fechaUHora);
    } catch (Exception e) {
      fechaUHoraFormateada = null;
    }
    return fechaUHoraFormateada;
  }

  public static Date getFechaUHoraAsDate(String fechaUHoraAsString, String formatoEntradaFechaUHora) {
    SimpleDateFormat formateador = new SimpleDateFormat(formatoEntradaFechaUHora);
    formateador.setLenient(false);
    Date fechaUHoraAsDate = null;
    try {
      fechaUHoraAsDate = formateador.parse(fechaUHoraAsString);
    }
    catch (Exception e) {
      fechaUHoraAsDate = null;
    }
    return fechaUHoraAsDate;
  }

  public static Date getFechaUHoraAsDate(String fechaUHoraAsString) {
    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
    formateador.setLenient(false);
    Date fechaUHoraAsDate = null;
    try {
      fechaUHoraAsDate = formateador.parse(fechaUHoraAsString);
    }
    catch (Exception e) {
      fechaUHoraAsDate = null;
    }
    return fechaUHoraAsDate;
  }

  public static Calendar getFechaUHoraAsCalendar(String fechaUHoraAsString, String formatoEntradaFechaUHora) {
    SimpleDateFormat formateador = new SimpleDateFormat(formatoEntradaFechaUHora);
    formateador.setLenient(false);
    Calendar fechaUHoraAsCalendar;
    try {
      Date fechaUHoraAsDate = formateador.parse(fechaUHoraAsString);
      fechaUHoraAsCalendar = Calendar.getInstance();
      fechaUHoraAsCalendar.setTime(fechaUHoraAsDate);
    } catch (Exception e) {
      fechaUHoraAsCalendar = null;
    }
    return fechaUHoraAsCalendar;
  }

  public static String getTodayAsString(String formatoSalidaFechaUHora) {
    Date today = new Date();
    return getDateAsFormatString(today, formatoSalidaFechaUHora);
  }

  public static String getDateAsFormatString(Date fechaAsDate, String formatoSalidaFechaUHora) {
    SimpleDateFormat formateador = new SimpleDateFormat(formatoSalidaFechaUHora);
    formateador.setLenient(false);
    return formateador.format(fechaAsDate);
  }

  public static boolean isFecha(String fecha)
  {
    return getFechaUHoraAsDate(fecha, "dd/MM/yyyy") != null;
  }

  public static boolean isNotFecha(String fecha)
  {
    return !isFecha(fecha);
  }

  public static boolean isFechaPasada(String fecha)
  {
    Date fechaAsDate = getFechaUHoraAsDate(fecha, "dd/MM/yyyy");
    Date today = new Date();

    return (fechaAsDate != null) && (fechaAsDate.compareTo(today) < 0);
  }

  public static boolean isFechaPasadaPresente(String fecha)
  {
    Date fechaAsDate = getFechaUHoraAsDate(fecha, "dd/MM/yyyy");
    Date today = new Date();

    return (fechaAsDate != null) && (fechaAsDate.compareTo(today) <= 0);
  }
}