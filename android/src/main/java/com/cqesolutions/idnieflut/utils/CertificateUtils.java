package com.cqesolutions.io.idniecap.utils;

import android.content.Context;
import android.util.Base64;

import com.cqesolutions.io.idniecap.bean.DatosCertificado;
import com.cqesolutions.io.idniecap.bean.DatosCertificadoFirma;
import com.cqesolutions.io.idniecap.bean.DatosDNIe;
import com.cqesolutions.io.idniecap.utils.pki.Tool;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import es.gob.jmulticard.jse.provider.DnieProvider;

/**
 * Created by Diego on 08/11/2018.
 */
public class CertificateUtils {

    public static String password = "tramite"; //Pongo una pass cualquiera para el keystore que creamos
    private static final String SUBJECT_DN = "CN=";
    private static final String PKCS12 = "PKCS12";
    public static final String KEY_PAIR_SERIALIZER = "KeyPairSerializer";

    public static KeyStore obtenerKeystoreDNIe(KeyStore keyStoreDNIe, Context contexto, X509Certificate certificate) throws Exception
    {
        /*
        //Recuperamos el certificado de autenticación para poder utilizar el DNIes de menores.
        //Si el DNIe tiene ambos certificados devolvemos el keystore completo
        KeyStore keyStore = keyStoreDNIe;
        char[] password = ((Tramite) contexto).getMyPasswordDialog().showPasswordDialog(-1);
        if(keyStoreDNIe.getKey(DnieProvider.SIGN_CERT_ALIAS, password)==null) {
//            X509Certificate[] certificates = (X509Certificate[]) keyStoreDNIe.getCertificateChain(DnieProvider.AUTH_CERT_ALIAS);
            X509Certificate[] certificates = new X509Certificate[]{certificate};

            Key key = keyStoreDNIe.getKey(DnieProvider.AUTH_CERT_ALIAS, password);
            keyStore = CertificateUtils.createKeyStore(DnieProvider.AUTH_CERT_ALIAS, (PrivateKey) key, certificates);
        }

        return keyStore;

         */
        return null;
    }
    public static KeyStore createKeyStore(String alias, PrivateKey key, X509Certificate[] certificados)
            throws Exception, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keystore = KeyStore.getInstance(PKCS12);
        keystore.load(null);

        keystore.setKeyEntry(alias, key, password.toCharArray(), certificados);

        return keystore;
    }

    public static DatosDNIe obtenerDatosCertificado(X509Certificate[] certificados) {
        X509Certificate certificado = certificados[0];
        return obtenerDatosCertificado(certificado);
    }
    public static DatosDNIe obtenerDatosCertificado(X509Certificate certificado) {

        DatosDNIe datosDNIe = new DatosDNIe();

        datosDNIe.setNif(Tool.getNIFCertificadoDigital(certificado));
        String nombre = Tool.getNombre(certificado);
        String apellidos = Tool.getApellidos(certificado);
        if(apellidos == null)
            apellidos = "";

        String nombreCompleto = "";
        if(apellidos!=null) {
            nombreCompleto = apellidos + " " + nombre;
        }
        else
        {
            nombreCompleto = nombre;
        }

        nombreCompleto=nombreCompleto.replaceAll(" ", "+");
        datosDNIe.setNombreCompleto(nombreCompleto);
        datosDNIe.setFechaNacimiento(Tool.getfechaNacimiento(certificado));
        datosDNIe.setNombre(nombre);
        datosDNIe.setApellido1(apellidos);

        return datosDNIe;
    }

    public static DatosCertificado obtenerTodosDatosCertificado(X509Certificate certificado) {

        if(certificado != null) {

            DatosCertificado datosCertificado = new DatosCertificado();
            datosCertificado.setNif(Tool.getNIFCertificadoDigital(certificado));
            datosCertificado.setTipo(Tool.getTipoCertificado(certificado));
            String nombre = Tool.getNombre(certificado);
            String apellidos = Tool.getApellidos(certificado);
            if (apellidos == null)
                apellidos = "";

            datosCertificado.setFechaNacimiento(Tool.getfechaNacimiento(certificado));
            datosCertificado.setNombre(nombre);
            datosCertificado.setApellidos(apellidos);
            datosCertificado.setEmail(Tool.getemailCertificadoDigital(certificado));

            datosCertificado.setNifRepresentante(Tool.getNIFRepresentanteCertificadoDigital(certificado));
            datosCertificado.setNombreRepresentante(Tool.getNombreRepresentante(certificado));
            datosCertificado.setApellidosRepresentante(Tool.getApellidosRepresentante(certificado));

            datosCertificado.setEstado(Tool.isValid(certificado));
            Date fecha = certificado.getNotBefore();
            if(fecha != null) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = dateFormat.format(fecha);

                datosCertificado.setFechaInicioValidez(strDate);
            }

            fecha = certificado.getNotAfter();
            if(fecha != null) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = dateFormat.format(fecha);

                datosCertificado.setFechaFinValidez(strDate);
            }

            return datosCertificado;
        }
        else
        {
            return null;
        }
    }

    public static DatosCertificadoFirma obtenerDatosCertificadoFirma(X509Certificate certificado) {

        if(certificado != null) {

            DatosCertificadoFirma datosCertificado = new DatosCertificadoFirma();
            datosCertificado.setNumeroSerie(Tool.getNumeroSerie(certificado));
            datosCertificado.setEmisor(Tool.getEmisorCertificado(certificado));
            datosCertificado.setSujeto(Tool.getSujetoCertificado(certificado));

            datosCertificado.setEstado(Tool.isValid(certificado));
            Date fecha = certificado.getNotBefore();
            if(fecha != null) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = dateFormat.format(fecha);

                datosCertificado.setFechaInicioValidez(strDate);
            }

            fecha = certificado.getNotAfter();
            if(fecha != null) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = dateFormat.format(fecha);

                datosCertificado.setFechaFinValidez(strDate);
            }

            return datosCertificado;
        }
        else
        {
            return null;
        }
    }
}