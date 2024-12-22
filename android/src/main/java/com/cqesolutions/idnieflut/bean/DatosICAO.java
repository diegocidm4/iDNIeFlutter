package com.cqesolutions.io.idniecap.bean;

import java.io.Serializable;

/**
 * Created by Diego on 12/11/2018.
 */
public class DatosICAO implements Serializable {
    private String DG1;
    private String DG2;
    private String DG13;
    private String SOD;

    public DatosICAO() {
        this.DG1 = null;
        this.DG2 = null;
        this.DG13 = null;
        this.SOD = null;
    }

    public String getDG1() {
        return DG1;
    }

    public void setDG1(String DG1) {
        this.DG1 = DG1;
    }

    public String getDG2() {
        return DG2;
    }

    public void setDG2(String DG2) {
        this.DG2 = DG2;
    }

    public String getDG13() {
        return DG13;
    }

    public void setDG13(String DG13) {
        this.DG13 = DG13;
    }

    public String getSOD() {
        return SOD;
    }

    public void setSOD(String SOD) {
        this.SOD = SOD;
    }
}
