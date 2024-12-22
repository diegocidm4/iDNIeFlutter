package com.cqesolutions.io.idniecap.bean;

import java.io.Serializable;

/**
 * Created by Diego on 12/11/2018.
 */
public class DatosCertificadoFirma implements Serializable {
    private String numeroSerie;
    private String emisor;
    private String sujeto;
    private String fechaInicioValidez;
    private String fechaFinValidez;
    private int estado;

    public DatosCertificadoFirma() {
        this.numeroSerie = null;
        this.emisor = null;
        this.sujeto = null;
        this.fechaInicioValidez = null;
        this.fechaFinValidez = null;
        this.estado = 0;
    }

    public DatosCertificadoFirma(String numeroSerie, String emisor, String sujeto, String fechaInicioValidez,
                                 String fechaFinValidez, int estado) {
        this.numeroSerie = numeroSerie;
        this.emisor = emisor;
        this.sujeto = sujeto;
        this.fechaInicioValidez = fechaInicioValidez;
        this.fechaFinValidez = fechaFinValidez;
        this.estado = estado;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getSujeto() {
        return sujeto;
    }

    public void setSujeto(String sujeto) {
        this.sujeto = sujeto;
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

}
