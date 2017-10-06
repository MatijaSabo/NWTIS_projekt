/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.web.podaci.Dnevnik;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
@ManagedBean
@SessionScoped
public class PregledZahtjeva {
    
    private List<Dnevnik> lista = new ArrayList<>();
    private boolean show_next = false;
    private boolean show_previous = false;
    private int limit = 0;
    private int pocetak = 0;
    private int ukupno = 0;
    
    private TimeZone timeZone;
    
    /**
     * Creates a new instance of Zahtjevi
     */
    public PregledZahtjeva() {
        showData();
    }
    
    public String showData() {
        lista.clear();

        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        this.limit = Integer.parseInt(konf.dajPostavku("brojLinijaTablica"));

        Connection veza = null;

        try {
            Class.forName(bp_driver);
            veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);

            PreparedStatement count = veza.prepareStatement("SELECT COUNT(*) AS broj FROM dnevnik WHERE status = ?");
            count.setInt(1, 1);
            ResultSet result1 = count.executeQuery();
            result1.next();
            this.ukupno = result1.getInt("broj");

            PreparedStatement select = veza.prepareStatement("SELECT * FROM dnevnik WHERE status = ? LIMIT ? OFFSET ?");
            select.setInt(1, 1);
            select.setInt(2, this.limit);
            select.setInt(3, this.pocetak);
            ResultSet result2 = select.executeQuery();

            while (result2.next()) {
                Dnevnik dnevnik = new Dnevnik();

                dnevnik.setID(result2.getInt("id"));
                dnevnik.setAkcija(result2.getString("akcija"));
                dnevnik.setKor_ime(result2.getString("kor_ime"));
                dnevnik.setUrl(result2.getString("url"));
                dnevnik.setVrijeme(result2.getTimestamp("vrijeme"));

                lista.add(dnevnik);
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(PregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }

        checkButtons();
        return "showData";
    }
    
    public String next() {
        if (this.ukupno > this.limit) {
            if (this.ukupno > (this.pocetak + this.limit)) {
                this.pocetak = this.pocetak + this.limit;
            }
        }

        showData();
        return "next";
    }

    public String previous() {
        if (this.ukupno > this.limit) {
            if ((this.pocetak - this.limit) >= 0) {
                this.pocetak = this.pocetak - this.limit;
            }
        }

        showData();
        return "previous";
    }

    public void checkButtons() {
        if (this.ukupno < this.limit) {
            this.show_next = false;
            this.show_previous = false;
        } else {
            if (this.pocetak + this.limit >= this.ukupno) {
                this.show_next = false;
            } else {
                this.show_next = true;
            }

            if (this.pocetak == 0) {
                this.show_previous = false;
            } else {
                this.show_previous = true;
            }
        }
    }
    
    public String goToLogout(){
        return "logout";
    }
    
    public String goToPregledKorisnika(){
        return "pregledKorisnika";
    }
    
    public String goToPregledDnevnika(){
        return "pregledDnevnika";
    }

    public List<Dnevnik> getLista() {
        return lista;
    }

    public void setLista(List<Dnevnik> lista) {
        this.lista = lista;
    }

    public boolean isShow_next() {
        return show_next;
    }

    public void setShow_next(boolean show_next) {
        this.show_next = show_next;
    }

    public boolean isShow_previous() {
        return show_previous;
    }

    public void setShow_previous(boolean show_previous) {
        this.show_previous = show_previous;
    }

    public int getPocetak() {
        return pocetak;
    }

    public void setPocetak(int pocetak) {
        this.pocetak = pocetak;
    }

    public int getUkupno() {
        return ukupno;
    }

    public void setUkupno(int ukupno) {
        this.ukupno = ukupno;
    }
    
    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
