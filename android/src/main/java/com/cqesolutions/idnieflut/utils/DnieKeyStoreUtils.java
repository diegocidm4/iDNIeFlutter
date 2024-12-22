package com.cqesolutions.io.idniecap.utils;

import android.util.Base64;

import com.cqesolutions.io.idniecap.bean.DatosDNIe;
import com.cqesolutions.io.idniecap.bean.DatosICAO;

import de.tsenger.androsmex.mrtd.DG13;
import de.tsenger.androsmex.mrtd.DG1_Dnie;
import de.tsenger.androsmex.mrtd.DG2;
import de.tsenger.androsmex.mrtd.DG7;
import de.tsenger.androsmex.mrtd.EF_SOD;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.baseCard.mrtd.MrtdCard;

/**
 * Created by Diego on 12/11/2018.
 */
public class DnieKeyStoreUtils {

    MrtdCard dnieKeyStore;

    public DnieKeyStoreUtils(MrtdCard dnieKeyStore) {
        this.dnieKeyStore = dnieKeyStore;
    }

    public DatosDNIe obtenerDatosDNIe(boolean datosPersonales, boolean foto, boolean firma)
    {

        DatosDNIe datosDNIe = new DatosDNIe();

        try
        {

            if(datosPersonales) {
                DG1_Dnie m_dg1 = dnieKeyStore.getDataGroup1();

                String nombreCompleto = m_dg1.getSurname()+" "+m_dg1.getName();
                nombreCompleto=nombreCompleto.replaceAll(" ", "+");
                datosDNIe.setNombreCompleto(nombreCompleto);
                datosDNIe.setFechaNacimiento(m_dg1.getDateOfBirth());
                datosDNIe.setNombre(m_dg1.getName());
                //Se recupera el nif del bloque DG1 en lugar del DG11 para evitar errores.
                String nif = m_dg1.getOptData();
                datosDNIe.setNif(nif);

    //            DG11 m_dg11 = dnieKeyStore.getDatagroup11();
    //            String nif = m_dg11.getPersonalNumber();
    //            datosDNIe.setNif(nif.replaceAll("-", ""));

                DG13 m_dg13 = dnieKeyStore.getDataGroup13();
                datosDNIe.setMunicipioNacimiento(m_dg13.getBirthPopulation());
                datosDNIe.setProvinciaNacimiento(m_dg13.getBirthProvince());
                datosDNIe.setApellido1(m_dg13.getSurName1());
                datosDNIe.setApellido2(m_dg13.getSurName2());
                datosDNIe.setNombrePadre(m_dg13.getFatherName());
                datosDNIe.setNombreMadre(m_dg13.getMotherName());
                datosDNIe.setFechaValidez(m_dg13.getExpirationDate().replaceAll(" ","/"));
                datosDNIe.setEmisor(m_dg1.getIssuer());
                datosDNIe.setNacionalidad(m_dg1.getNationality());
                datosDNIe.setSexo(m_dg1.getSex());
                datosDNIe.setDireccion(m_dg13.getActualAddress());
                datosDNIe.setProvinciaActual(m_dg13.getActualProvince());
                datosDNIe.setMunicipioActual(m_dg13.getActualPopulation());
            }

            if(foto)
            {
                DG2 m_dg2 = dnieKeyStore.getDataGroup2();
                datosDNIe.setImagen(m_dg2.getImageBytes());
            }

            if (firma)
            {
                DG7 m_dg7 = dnieKeyStore.getDataGroup7();
                datosDNIe.setFirma(m_dg7.getImageBytes());
            }

            datosDNIe.setDatosICAO(obtenerDatosICAO());
        }
        catch (CryptoCardException ex)
        {

        }

        return datosDNIe;
    }

    public DatosICAO obtenerDatosICAO()
    {
        DatosICAO datosICAO = null;
        try {
            datosICAO = new DatosICAO();

            DG1_Dnie m_dg1 = dnieKeyStore.getDataGroup1();
            datosICAO.setDG1(Base64.encodeToString(m_dg1.getBytes(), Base64.DEFAULT));

            DG2 m_dg2 = dnieKeyStore.getDataGroup2();
            datosICAO.setDG2(Base64.encodeToString(m_dg2.getBytes(), Base64.DEFAULT));

            DG13 m_dg13 = dnieKeyStore.getDataGroup13();
            datosICAO.setDG13(Base64.encodeToString(m_dg13.getBytes(), Base64.DEFAULT));

            EF_SOD sod = dnieKeyStore.getEFSOD();
            datosICAO.setSOD(Base64.encodeToString(sod.getBytes(), Base64.DEFAULT));

        } catch (CryptoCardException e) {

        }

        return datosICAO;
    }

}
