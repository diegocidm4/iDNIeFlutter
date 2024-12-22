package com.cqesolutions.io.idniecap.bean;

import java.io.Serializable;

/**
 * Created by Diego on 12/11/2018.
 */
public class DatosCertificado implements Serializable {
    private String nif;
    private String nombre;
    private String apellidos;
    private String fechaNacimiento;
    private String tipo;
    private String nifRepresentante;
    private String nombreRepresentante;
    private String apellidosRepresentante;
    private String fechaInicioValidez;
    private String fechaFinValidez;
    private int estado;
    private String email;

    public DatosCertificado() {
        this.nif = null;
        this.nombre = null;
        this.apellidos = null;
        this.fechaNacimiento = null;
        this.tipo = null;
        this.nifRepresentante = null;
        this.nombreRepresentante = null;
        this.apellidosRepresentante = null;
        this.fechaInicioValidez = null;
        this.fechaFinValidez = null;
        this.estado = 0;
        this.email = null;
    }

    public DatosCertificado(String nif, String nombre, String apellidos, String fechaNacimiento, String tipo, String nifRepresentante,
                            String nombreRepresentante, String apellidosRepresentante, String fechaInicioValidez,
                            String fechaFinValidez, int estado, String email) {
        this.nif = nif;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.tipo = tipo;
        this.nifRepresentante = nifRepresentante;
        this.nombreRepresentante = nombreRepresentante;
        this.apellidosRepresentante = apellidosRepresentante;
        this.fechaInicioValidez = fechaInicioValidez;
        this.fechaFinValidez = fechaFinValidez;
        this.estado = estado;
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNifRepresentante() {
        return nifRepresentante;
    }

    public void setNifRepresentante(String nifRepresentante) {
        this.nifRepresentante = nifRepresentante;
    }

    public String getNombreRepresentante() {
        return nombreRepresentante;
    }

    public void setNombreRepresentante(String nombreRepresentante) {
        this.nombreRepresentante = nombreRepresentante;
    }

    public String getApellidosRepresentante() {
        return apellidosRepresentante;
    }

    public void setApellidosRepresentante(String apellidosRepresentante) {
        this.apellidosRepresentante = apellidosRepresentante;
    }

    public String getFechaInicioValidez() {
        return fechaInicioValidez;
    }

    public void setFechaInicioValidez(String fechaInicioValidez) {
        this.fechaInicioValidez = fechaInicioValidez;
    }

    public String getFechaFinValidez() {
        return fechaFinValidez;
    }

    public void setFechaFinValidez(String fechaFinValidez) {
        this.fechaFinValidez = fechaFinValidez;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
