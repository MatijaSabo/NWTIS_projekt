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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
@ManagedBean
@SessionScoped
public class Prijava {

    private String kor_ime;
    private String password;
    private boolean error_login = false;
    private boolean login_show = true;

    /**
     * Creates a new instance of prijava
     */
    public Prijava() {
    }

    public String login() {
        if (this.kor_ime != null && this.password != null && !this.password.isEmpty() && !this.kor_ime.isEmpty()) {
            ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
            BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

            String bp_server = bp_konf.getServerDatabase();
            String bp_baza = bp_server + bp_konf.getUserDatabase();
            String bp_korisnik = bp_konf.getUserUsername();
            String bp_lozinka = bp_konf.getUserPassword();
            String bp_driver = bp_konf.getDriverDatabase();

            Connection veza = null;

            try {
                Class.forName(bp_driver);
                veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);

                PreparedStatement select = veza.prepareStatement("SELECT * FROM korisnici WHERE kor_ime = ? AND pass = ?");
                select.setString(1, this.kor_ime);
                select.setString(2, this.password);
                ResultSet result = select.executeQuery();

                if (result.next()) {
                    this.error_login = false;

                    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                    session.setAttribute("kor_ime", this.kor_ime);

                } else {
                    this.error_login = true;
                }

            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(Prijava.class.getName()).log(Level.SEVERE, null, ex);
                this.error_login = true;
            }
        } else {
            this.error_login = true;
        }

        if (this.error_login) {
            return "error";
        } else {
            return "login";
        }
    }

    public String logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        return "logout";
    }

    public String getKor_ime() {
        return kor_ime;
    }

    public void setKor_ime(String kor_ime) {
        this.kor_ime = kor_ime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isError_login() {
        return error_login;
    }

    public void setError_login(boolean error_login) {
        this.error_login = error_login;
    }

    public boolean isLoginShow() {
        return login_show;
    }

    public void setLoginShow(boolean login) {
        this.login_show = login;
    }
}
