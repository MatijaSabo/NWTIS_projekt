/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacija;

/**
 *
 * @author Matija
 */
@Named(value = "upravljanjeServerom")
@SessionScoped
public class UpravljanjeServerom implements Serializable {

    private String status;

    private String server;
    private int port;
    private String user;
    private String pass;

    /**
     * Creates a new instance of UpravljanjeServerom
     */
    public UpravljanjeServerom() {
        postaviPodatke();
    }

    public String pullSessionUser() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String user = (String) session.getAttribute("kor_ime");
        return user;
    }

    public String pullSessionPass() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String user = (String) session.getAttribute("pass");
        return user;
    }

    public void postaviPodatke() {
        ServletContext sc = (ServletContext) SlusacAplikacija.kontekst;
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");

        this.server = konf.dajPostavku("serverName");
        this.port = Integer.parseInt(konf.dajPostavku("serverPort"));

        this.user = pullSessionUser();
        this.pass = pullSessionPass();
    }

    public void sendStart() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT_Master START;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public void sendStop() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT_Master STOP;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public void sendStatus() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT_Master STATUS;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);

        if (odgovor.contains("24")) {
            this.status = "Blokirana";
        } else if (odgovor.contains("25")) {
            this.status = "Aktivna";
        } else {
            this.status = "Problem kod dobivanja statusa";
        }
    }
    
    public void sendWork() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT_Master WORK;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }
    
    public void sendWait() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT_Master WAIT;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public String posaljiKomandu(String komanda) {
        String odgovor = "-";

        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;

        try {
            socket = new Socket(this.server, this.port);
            is = socket.getInputStream();
            os = socket.getOutputStream();

            os.write(komanda.getBytes());
            os.flush();
            socket.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }

            odgovor = sb.toString().trim();
        } catch (IOException ex) {
            Logger.getLogger(Autentikacija.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (os != null) {
                    os.close();
                }

                if (socket != null) {
                    socket.close();
                }

            } catch (IOException ex) {
                System.out.println("ERROR; Problem kod zatvaranja socketa");
            }
        }

        return odgovor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
