package com.cqesolutions.io.idniecap.bean;

import java.io.Serializable;

/**
 * Created by Diego on 12/11/2018.
 */
public class DatosDNIe implements Serializable {
    private String nif;
    private String nombreCompleto;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private byte[] firma;
    private byte[] imagen;
    private String fechaNacimiento;
    private String provinciaNacimiento;
    private String municipioNacimiento;
    private String nombrePadre;
    private String nombreMadre;
    private String fechaValidez;
    private String emisor;
    private String nacionalidad;
    private String sexo;
    private String direccion;
    private String provinciaActual;
    private String municipioActual;
    private String numSoporte;

    private DatosICAO datosICAO;

    public DatosDNIe() {
        this.nif = null;
        this.nombreCompleto = null;
        this.nombre = null;
        this.apellido1 = null;
        this.apellido2 = null;
        this.firma = null;
        this.imagen = null;
        this.fechaNacimiento = null;
        this.provinciaNacimiento = null;
        this.municipioNacimiento = null;
        this.nombrePadre = null;
        this.nombreMadre = null;
        this.fechaValidez = null;
        this.emisor = null;
        this.nacionalidad = null;
        this.sexo = null;
        this.direccion = null;
        this.provinciaActual = null;
        this.municipioActual = null;
        this.numSoporte = null;
        this.datosICAO = null;
    }

    public DatosDNIe(String nif, String nombreCompleto, String nombre, String apellido1, String apellido2, byte[] firma, byte[] imagen, String fechaNacimiento,
                     String provinciaNacimiento, String municipioNacimiento, String nombrePadre, String nombreMadre, String fechaValidez, String emisor,
                     String nacionalidad, String sexo, String direccion, String provinciaActual, String municipioActual, String numSoporte) {
        this.nif = nif;
        this.nombreCompleto = nombreCompleto;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.firma = firma;
        this.imagen = imagen;
        this.fechaNacimiento = fechaNacimiento;
        this.provinciaNacimiento = provinciaNacimiento;
        this.municipioNacimiento = municipioNacimiento;
        this.nombrePadre = nombrePadre;
        this.nombreMadre = nombreMadre;
        this.fechaValidez = fechaValidez;
        this.emisor = emisor;
        this.nacionalidad = nacionalidad;
        this.sexo = sexo;
        this.direccion = direccion;
        this.provinciaActual = provinciaActual;
        this.municipioActual = municipioActual;
        this.numSoporte = numSoporte;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public byte[] getFirma() {
        return firma;
    }

    public void setFirma(byte[] firma) {
        this.firma = firma;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getProvinciaNacimiento() {
        return provinciaNacimiento;
    }

    public void setProvinciaNacimiento(String provinciaNacimiento) {
        this.provinciaNacimiento = provinciaNacimiento;
    }

    public String getMunicipioNacimiento() {
        return municipioNacimiento;
    }

    public void setMunicipioNacimiento(String municipioNacimiento) {
        this.municipioNacimiento = municipioNacimiento;
    }

    public String getNombrePadre() {
        return nombrePadre;
    }

    public void setNombrePadre(String nombrePadre) {
        this.nombrePadre = nombrePadre;
    }

    public String getNombreMadre() {
        return nombreMadre;
    }

    public void setNombreMadre(String nombreMadre) {
        this.nombreMadre = nombreMadre;
    }

    public String getFechaValidez() {
        return fechaValidez;
    }

    public void setFechaValidez(String fechaValidez) {
        this.fechaValidez = fechaValidez;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getProvinciaActual() {
        return provinciaActual;
    }

    public void setProvinciaActual(String provinciaActual) {
        this.provinciaActual = provinciaActual;
    }

    public String getMunicipioActual() {
        return municipioActual;
    }

    public void setMunicipioActual(String municipioActual) {
        this.municipioActual = municipioActual;
    }

    public String getNumSoporte() {
        return numSoporte;
    }

    public void setNumSoporte(String numSoporte) {
        this.numSoporte = numSoporte;
    }

    public DatosICAO getDatosICAO() {
        return datosICAO;
    }

    public void setDatosICAO(DatosICAO datosICAO) {
        this.datosICAO = datosICAO;
    }
}
