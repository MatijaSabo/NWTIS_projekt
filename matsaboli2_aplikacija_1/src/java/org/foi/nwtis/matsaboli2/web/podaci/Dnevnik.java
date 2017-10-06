/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.podaci;

import java.util.Date;

/**
 *
 * @author Matija
 */
public class Dnevnik {
    private int ID;
    private String kor_ime;
    private String akcija;
    private String ip_adresa;
    private String url;
    private Date vrijeme;

    public Dnevnik() {
    }

    public Dnevnik(int ID, String kor_ime, String akcija, String ip_adresa, String url, Date vrijeme) {
        this.ID = ID;
        this.kor_ime = kor_ime;
        this.akcija = akcija;
        this.ip_adresa = ip_adresa;
        this.url = url;
        this.vrijeme = vrijeme;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAkcija() {
        return akcija;
    }

    public void setAkcija(String akcija) {
        this.akcija = akcija;
    }

    public String getIp_adresa() {
        return ip_adresa;
    }

    public void setIp_adresa(String ip_adresa) {
        this.ip_adresa = ip_adresa;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getKor_ime() {
        return kor_ime;
    }

    public void setKor_ime(String kor_ime) {
        this.kor_ime = kor_ime;
    }
}
