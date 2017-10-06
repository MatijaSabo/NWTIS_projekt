/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.podaci;

/**
 *
 * @author Matija
 */
public class Korisnik {
    private int ID;
    private String kor_ime;
    private String pass;
    private String prezime;
    private String email;

    public Korisnik() {
    }

    public Korisnik(int ID, String kor_ime, String pass, String prezime, String email) {
        this.ID = ID;
        this.kor_ime = kor_ime;
        this.pass = pass;
        this.prezime = prezime;
        this.email = email;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getKor_ime() {
        return kor_ime;
    }

    public void setKor_ime(String kor_ime) {
        this.kor_ime = kor_ime;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
