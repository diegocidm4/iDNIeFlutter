package com.cqesolutions.io.idniecap.utils;

import android.content.Context;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Diego on 05/11/2018.
 */
public class DateUtils {

    public static String formateaFechaNacimiento(String fNacimiento, String formato, Context context)throws IOException, Exception
    {
        String formatoFecha="yyMMdd";

        DateFormat format = new SimpleDateFormat(formatoFecha, Locale.UK);
        Date date = format.parse(fNacimiento);

        String fechaNacimiento = new SimpleDateFormat(formato, Locale.UK).format(date);

        return fechaNacimiento;
    }

    public static String formateaFechaDNIe(String fNacimiento, String formato, Context context)
    {
        try {
            String formatoFecha = "";
            formatoFecha = "yyMMdd";

            DateFormat format = new SimpleDateFormat(formatoFecha, Locale.UK);
            Date date = format.parse(fNacimiento);

            String fechaNacimiento = new SimpleDateFormat(formato, Locale.UK).format(date);

            return fechaNacimiento;
        }catch (Exception ex){
            return fNacimiento;
        }
    }

    public static String formateaFechaeID(String fecha, String formato, Context context)
    {
        try {
            String fechaResultado;
            DateFormat formatter;

            formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = formatter.parse(fecha);
            formatter = new SimpleDateFormat(formato);
            fechaResultado = formatter.format(date1);

            return fechaResultado;
        }catch (Exception ex){
            return fecha;
        }
    }

    public static Boolean esMenorIgualHoy(String fecha)
    {
        try {
            Date dFecha = null;
            if(fecha.contains("/")) {
                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                dFecha = sdformat.parse(fecha);
            }
            else
            {
                SimpleDateFormat sdformat = new SimpleDateFormat("yyMMdd");
                dFecha = sdformat.parse(fecha);
            }
            if(dFecha.compareTo(new Date()) >= 0)
            {
                return true;
            }
            else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

    }

}
