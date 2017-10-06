/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
@Named(value = "upravljenjeServerom")
@SessionScoped
public class UpravljenjeServerom implements Serializable {

    private String status;
    private String server;
    private int port;
    private String user;
    private String pass;

    /**
     * Creates a new instance of UpravljenjeServerom
     */
    public UpravljenjeServerom() {
        postaviPodatke();
    }

    public void postaviPodatke() {
        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");

        this.server = konf.dajPostavku("serverName");
        this.port = Integer.parseInt(konf.dajPostavku("serverPort"));

        this.user = pullSessionUser();
        this.pass = pullSessionPass();
    }

    public String pullSessionUser() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String user = (String) session.getAttribute("kor_ime");
        return user;
    }

    public String pullSessionPass() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String pass = (String) session.getAttribute("pass");
        return pass;
    }

    public void sendStatus() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; STATUS;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);

        if (odgovor.contains("13")) {
            this.status = "Privremeno ne preuzima podatke";
        } else if (odgovor.contains("14")) {
            this.status = "Preuzima podatke";
        } else if (odgovor.contains("15")) {
            this.status = "Ne preuzima podatke i korisničke komande";
        } else {
            this.status = "Problem kod dobivanja statusa";
        }
    }

    public void sendStart() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; START;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public void sendStop() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; STOP;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public void sendPause() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; PAUSE;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public String posaljiKomandu(String komanda) {
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        String odgovor = "-";

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
