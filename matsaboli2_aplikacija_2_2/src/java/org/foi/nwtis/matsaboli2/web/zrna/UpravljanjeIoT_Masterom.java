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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.web.podaci.Izbornik;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
@Named(value = "upravljanjeIoT_MasterGrupom")
@SessionScoped
public class UpravljanjeIoT_Masterom implements Serializable {

    private String status;
    private String iot_status;
    private String naziv_add;
    private String adresa_add;
    private int id_add;
    private String odabrani_iot;

    private String server;
    private int port;
    private String user;
    private String pass;

    private List<Izbornik> lista = new ArrayList<>();

    /**
     * Creates a new instance of UpravljanjeIoT_MasterGrupom
     */
    public UpravljanjeIoT_Masterom() {
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

    public void sendLoad() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT_Master LOAD;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public void sendClear() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT_Master CLEAR;";
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

    public void sendList() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT_Master LIST;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);

        String temp = odgovor.substring(odgovor.indexOf("{") + 1, odgovor.lastIndexOf("}"));
        if (temp.contains("IoT")) {
            this.lista.clear();

            String[] polje = temp.split(",");

            for (int i = 0; i < polje.length; i++) {
                polje[i] = polje[i].substring(polje[i].indexOf("'") + 1, polje[i].lastIndexOf("'"));

                String id = polje[i].substring(0, polje[i].indexOf("'"));
                String naziv = polje[i].substring(polje[i].lastIndexOf("'") + 1, polje[i].length());

                Izbornik izbornik = new Izbornik(naziv, id);

                lista.add(izbornik);
            }

        } else {
            this.lista.clear();
        }
    }

    public void sendIoTAdd() {
        if (this.adresa_add != null && this.naziv_add != null
                && !this.adresa_add.isEmpty() && !this.naziv_add.isEmpty()) {
            
            String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT " + this.id_add + " ADD '"
                    + this.naziv_add + "' '" + this.adresa_add + "';";
            String odgovor = posaljiKomandu(komanda);
            System.out.println("ODGOVOR: " + odgovor);

            if (odgovor.contains("OK")) {
                this.id_add = 0;
                this.adresa_add = "";
                this.naziv_add = "";
            }
        }

    }

    public void sendIoTWork() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT " + this.odabrani_iot + " WORK;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public void sendIoTWait() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT " + this.odabrani_iot + " WAIT;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public void sendIoTRemove() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT " + this.odabrani_iot + " REMOVE;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);
    }

    public void sendIoTStatus() {
        String komanda = "USER " + this.user + "; PASSWD " + this.pass + "; IoT " + this.odabrani_iot + " STATUS;";
        String odgovor = posaljiKomandu(komanda);
        System.out.println("ODGOVOR: " + odgovor);

        if (odgovor.contains("34")) {
            this.iot_status = "Blokiran";
        } else if (odgovor.contains("35")) {
            this.iot_status = "Aktivan";
        } else {
            this.iot_status = "Problem kod dobivanja statusa";
        }
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Izbornik> getLista() {
        return lista;
    }

    public void setLista(List<Izbornik> lista) {
        this.lista = lista;
    }

    public String getIot_status() {
        return iot_status;
    }

    public void setIot_status(String iot_status) {
        this.iot_status = iot_status;
    }

    public String getNaziv_add() {
        return naziv_add;
    }

    public void setNaziv_add(String naziv_add) {
        this.naziv_add = naziv_add;
    }

    public String getAdresa_add() {
        return adresa_add;
    }

    public void setAdresa_add(String adresa_add) {
        this.adresa_add = adresa_add;
    }

    public int getId_add() {
        return id_add;
    }

    public void setId_add(int id_add) {
        this.id_add = id_add;
    }

    public String getOdabrani_iot() {
        return odabrani_iot;
    }

    public void setOdabrani_iot(String odabrani_iot) {
        this.odabrani_iot = odabrani_iot;
    }
}
