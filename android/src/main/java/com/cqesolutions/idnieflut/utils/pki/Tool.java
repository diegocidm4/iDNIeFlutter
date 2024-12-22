package com.cqesolutions.io.idniecap.utils.pki;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.X509Principal;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

/**
 * Clase auxiliar.
 */
public class Tool {
    public static final ASN1ObjectIdentifier VATES = new ASN1ObjectIdentifier("2.5.4.97").intern();
    public static final String EXAMPLE_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
            "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    //La función devuelve 0 si es válido, 1 si ha expirado y 2 si aún no es válido
    public static int isValid(X509Certificate certificate){
        int valido = 0;
        try {
            certificate.checkValidity();
        }catch (CertificateExpiredException ex) {
            valido = 1;
        }catch (CertificateNotYetValidException ex) {
            valido = 2;
        }
        return valido;
    }
    public static String getCN(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;
        if(!name.getValues(BCStyle.CN).isEmpty())
            resultado = name.getValues(BCStyle.CN).get(0).toString();
        return resultado;
    }

    public static String getNIF(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;
        if(!name.getValues(BCStyle.SN).isEmpty())
            resultado = name.getValues(BCStyle.SN).get(0).toString();
        return resultado;
    }

    public static String getemailCertificadoDigital(X509Certificate certificate) {
        try {
            Collection<List<?>> atributos = certificate.getSubjectAlternativeNames();
            String resultado = null;

            if (atributos == null)
                return resultado;
            for (List item : atributos) {
                Integer type = (Integer) item.get(0);
                if(type == 1)//El tipo 1 es email.
                {
                    String valor = item.get(1).toString();
                    if (valor.contains("@")) {
                        return valor;
                    }
                }
            }

            return resultado;
        }catch (Exception ex)
        {
            return null;
        }
    }
    public static String getNIFCertificadoDigital(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;

        if (Tool.isCertificadoPersonaFisica(certificate))
        {
            if(!name.getValues(BCStyle.SN).isEmpty()) {
                String valor = name.getValues(BCStyle.SN).get(0).toString();
                if(valor.contains("-"))
                {
                    String[] valores = valor.split("-");
                    valor = valores[valores.length-1];
                }
                resultado = valor;
            }
        }
        else
        {
            if(!name.getValues(BCStyle.CN).isEmpty()) {
                String valor = name.getValues(BCStyle.CN).get(0).toString();
                if(valor.contains("("))
                {
                    int posIni = valor.indexOf("(");
                    int posFin = valor.indexOf(")", posIni);
                    valor = valor.substring(posIni+1, posFin);
                    if(valor.contains(" "))
                    {
                        String[] valores = valor.split(" ");
                        valor = valores[valores.length-1];
                    }

                }
                resultado = valor;
            }
        }
/*
        HashMap<String, String> hmParametros = new HashMap<>();
        if(!name.getValues(BCStyle.OU).isEmpty())
            hmParametros.put("OU",name.getValues(BCStyle.OU).get(0).toString());
        if(!name.getValues(BCStyle.O).isEmpty())
            hmParametros.put("O",name.getValues(BCStyle.O).get(0).toString());
        if(!name.getValues(BCStyle.DN_QUALIFIER).isEmpty())
            hmParametros.put("DN_QUALIFIER",name.getValues(BCStyle.DN_QUALIFIER).get(0).toString());
        if(!name.getValues(BCStyle.C).isEmpty())
            hmParametros.put("C",name.getValues(BCStyle.C).get(0).toString());
        if(!name.getValues(BCStyle.PSEUDONYM).isEmpty())
            hmParametros.put("PSEUDONYM",name.getValues(BCStyle.PSEUDONYM).get(0).toString());
        if(!name.getValues(BCStyle.UNIQUE_IDENTIFIER).isEmpty())
            hmParametros.put("UNIQUE_IDENTIFIER",name.getValues(BCStyle.UNIQUE_IDENTIFIER).get(0).toString());
        if(!name.getValues(BCStyle.UID).isEmpty())
            hmParametros.put("UID",name.getValues(BCStyle.UID).get(0).toString());
        if(!name.getValues(BCStyle.TELEPHONE_NUMBER).isEmpty())
            hmParametros.put("TELEPHONE_NUMBER",name.getValues(BCStyle.TELEPHONE_NUMBER).get(0).toString());
        if(!name.getValues(BCStyle.STREET).isEmpty())
            hmParametros.put("STREET",name.getValues(BCStyle.STREET).get(0).toString());
        if(!name.getValues(BCStyle.POSTAL_CODE).isEmpty())
            hmParametros.put("POSTAL_CODE",name.getValues(BCStyle.POSTAL_CODE).get(0).toString());
        if(!name.getValues(BCStyle.POSTAL_ADDRESS).isEmpty())
            hmParametros.put("POSTAL_ADDRESS",name.getValues(BCStyle.POSTAL_ADDRESS).get(0).toString());
        if(!name.getValues(BCStyle.PLACE_OF_BIRTH).isEmpty())
            hmParametros.put("PLACE_OF_BIRTH",name.getValues(BCStyle.PLACE_OF_BIRTH).get(0).toString());
        if(!name.getValues(BCStyle.NAME_AT_BIRTH).isEmpty())
            hmParametros.put("NAME_AT_BIRTH",name.getValues(BCStyle.NAME_AT_BIRTH).get(0).toString());
        if(!name.getValues(BCStyle.INITIALS).isEmpty())
            hmParametros.put("INITIALS",name.getValues(BCStyle.INITIALS).get(0).toString());
        if(!name.getValues(BCStyle.GENDER).isEmpty())
            hmParametros.put("GENDER",name.getValues(BCStyle.GENDER).get(0).toString());
        if(!name.getValues(BCStyle.EmailAddress).isEmpty())
            hmParametros.put("EmailAddress",name.getValues(BCStyle.EmailAddress).get(0).toString());
        if(!name.getValues(BCStyle.COUNTRY_OF_RESIDENCE).isEmpty())
            hmParametros.put("COUNTRY_OF_RESIDENCE",name.getValues(BCStyle.COUNTRY_OF_RESIDENCE).get(0).toString());
        if(!name.getValues(BCStyle.COUNTRY_OF_CITIZENSHIP).isEmpty())
            hmParametros.put("COUNTRY_OF_CITIZENSHIP",name.getValues(BCStyle.COUNTRY_OF_CITIZENSHIP).get(0).toString());
        if(!name.getValues(BCStyle.DATE_OF_BIRTH).isEmpty())
            hmParametros.put("DATE_OF_BIRTH",name.getValues(BCStyle.DATE_OF_BIRTH).get(0).toString());
        if(!name.getValues(BCStyle.CN).isEmpty())
            hmParametros.put("CN",name.getValues(BCStyle.CN).get(0).toString());
        if(!name.getValues(BCStyle.GIVENNAME).isEmpty())
            hmParametros.put("GIVENNAME",name.getValues(BCStyle.GIVENNAME).get(0).toString());
        if(!name.getValues(BCStyle.SURNAME).isEmpty())
            hmParametros.put("SURNAME",name.getValues(BCStyle.SURNAME).get(0).toString());
        if(!name.getValues(BCStyle.BUSINESS_CATEGORY).isEmpty())
            hmParametros.put("BUSINESS_CATEGORY",name.getValues(BCStyle.BUSINESS_CATEGORY).get(0).toString());
        if(!name.getValues(BCStyle.DMD_NAME).isEmpty())
            hmParametros.put("DMD_NAME",name.getValues(BCStyle.DMD_NAME).get(0).toString());
        if(!name.getValues(BCStyle.DC).isEmpty())
            hmParametros.put("DC",name.getValues(BCStyle.DC).get(0).toString());
        if(!name.getValues(BCStyle.GENERATION).isEmpty())
            hmParametros.put("GENERATION",name.getValues(BCStyle.GENERATION).get(0).toString());
        if(!name.getValues(BCStyle.SERIALNUMBER).isEmpty())
            hmParametros.put("SERIALNUMBER",name.getValues(BCStyle.SERIALNUMBER).get(0).toString());
        if(!name.getValues(BCStyle.SN).isEmpty())
            hmParametros.put("SN",name.getValues(BCStyle.SN).get(0).toString());
        if(!name.getValues(BCStyle.L).isEmpty())
            hmParametros.put("L",name.getValues(BCStyle.L).get(0).toString());
        if(!name.getValues(VATES).isEmpty())
            hmParametros.put("VATES",name.getValues(VATES).get(0).toString());

        System.out.println(Arrays.asList(hmParametros)); // method 1
*/
        return resultado;
    }

    public static String getNIFRepresentanteCertificadoDigital(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;

        if (!Tool.isCertificadoPersonaFisica(certificate)) {
            if (!name.getValues(BCStyle.SN).isEmpty()) {
                String valor = name.getValues(BCStyle.SN).get(0).toString();
                if (valor.contains("-")) {
                    String[] valores = valor.split("-");
                    valor = valores[valores.length - 1];
                }
                resultado = valor;
            }
        }
        return resultado;
    }

    public static String getNombre(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;

        if (Tool.isCertificadoPersonaFisica(certificate)) {
            if (!name.getValues(BCStyle.GIVENNAME).isEmpty())
                resultado = name.getValues(BCStyle.GIVENNAME).get(0).toString();
        }
        else
        {
            if (!name.getValues(BCStyle.O).isEmpty())
                resultado = name.getValues(BCStyle.O).get(0).toString();

        }
        return resultado;
    }

    public static String getApellidos(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;

        if (Tool.isCertificadoPersonaFisica(certificate)) {
            if (!name.getValues(BCStyle.SURNAME).isEmpty())
                resultado = name.getValues(BCStyle.SURNAME).get(0).toString();
        }

        return resultado;
    }

    public static String getNombreRepresentante(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;

        if (!Tool.isCertificadoPersonaFisica(certificate)) {
            if (!name.getValues(BCStyle.GIVENNAME).isEmpty())
                resultado = name.getValues(BCStyle.GIVENNAME).get(0).toString();
        }

        return resultado;
    }

    public static String getApellidosRepresentante(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;

        if (!Tool.isCertificadoPersonaFisica(certificate)) {
            if (!name.getValues(BCStyle.SURNAME).isEmpty())
                resultado = name.getValues(BCStyle.SURNAME).get(0).toString();
        }

        return resultado;
    }

    public static String getfechaNacimiento(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        String resultado = null;
        if(!name.getValues(BCStyle.DATE_OF_BIRTH).isEmpty())
             resultado= name.getValues(BCStyle.DATE_OF_BIRTH).get(0).toString();

        return resultado;
    }

    public static Boolean isCertificadoPersonaFisica(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getSubjectDN().toString());
        Boolean resultado = true;

        if(!name.getValues(VATES).isEmpty())
        {
            return false;
        }
/*
        X509Principal name = new X509Principal(certificate.getIssuerDN().toString());
        Boolean resultado = false;

        if(!name.getValues(BCStyle.CN).isEmpty())
        {
            String tipoCertificado = name.getValues(BCStyle.CN).get(0).toString();

            if (tipoCertificado.equals(Constantes.CERTIFICADO_JURIDICA))
            {
                resultado = true;
            }
        }
*/
        return resultado;
    }

    public static String getTipoCertificado(X509Certificate certificate) {
        X509Principal name = new X509Principal(certificate.getIssuerDN().toString());
        String resultado = "";

        if(!name.getValues(BCStyle.CN).isEmpty())
        {
            String tipoCertificado = name.getValues(BCStyle.CN).get(0).toString();
            resultado = tipoCertificado;
        }

        return resultado;
    }

    public static String getEmisorCertificado(X509Certificate certificate) {
        String resultado = certificate.getIssuerDN().toString();
        return resultado;
    }

    public static String getSujetoCertificado(X509Certificate certificate) {
        String resultado = certificate.getSubjectDN().toString();
        return resultado;
    }

    public static String getNumeroSerie(X509Certificate certificate) {
        String resultado = certificate.getSerialNumber().toString();
        return resultado;
    }

}
