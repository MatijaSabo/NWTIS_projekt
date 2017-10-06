/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.web.podaci.Izbornik;
import org.foi.nwtis.matsaboli2.web.podaci.Poruka;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
@Named(value = "pregledEmail")
@SessionScoped
public class PregledEmailPoruka implements Serializable {

    private Session session;
    private Store store;
    private Folder folder;
    private Folder[] folders;

    private Message[] messages;
    private MimeMessage message;

    private String posluzitelj;
    private String korisnik;
    private String pass;

    private String odabrana_mapa = "INBOX";
    private int ukupno_poruka;
    private int brojPorukaStrana;
    private int pocetnaPozicija = 0;
    private int zavrsnaPozicija = 0;

    private boolean show_previous = false;
    private boolean show_next = false;

    private List<Izbornik> mape = new ArrayList<>();
    private List<Poruka> poruke = new ArrayList<>();
    
    private TimeZone timeZone;

    /**
     * Creates a new instance of PregledEmail
     */
    public PregledEmailPoruka() throws MessagingException, IOException {
        preuzmiPodatke();
        preuzmiMape();
        preuzmiPoruke();
    }

    public void preuzmiPodatke() {
        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");

        this.posluzitelj = konf.dajPostavku("mail.server");
        this.korisnik = konf.dajPostavku("mail.username");
        this.pass = konf.dajPostavku("mail.password");
        this.brojPorukaStrana = Integer.parseInt(konf.dajPostavku("brojLinijaTablica"));
    }

    public void preuzmiMape() throws MessagingException {
        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", this.posluzitelj);
        session = Session.getInstance(properties, null);

        store = session.getStore("imap");
        store.connect(this.posluzitelj, this.korisnik, this.pass);

        folder = store.getDefaultFolder();
        folders = folder.list();

        for (Folder f : folders) {
            this.mape.add(new Izbornik(f.getName(), f.getName()));
        }
    }

    public void preuzmiPoruke() throws NoSuchProviderException, MessagingException, IOException {
        this.poruke.clear();
        messages = null;
        folder = null;

        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", this.posluzitelj);
        session = Session.getInstance(properties, null);

        store = session.getStore("imap");
        store.connect(this.posluzitelj, this.korisnik, this.pass);

        folder = store.getFolder(this.odabrana_mapa);
        folder.open(Folder.READ_WRITE);

        this.ukupno_poruka = folder.getMessageCount();

        if (pocetnaPozicija == 0 && zavrsnaPozicija == 0) {
            pocetnaPozicija = this.ukupno_poruka - this.brojPorukaStrana + 1;
            zavrsnaPozicija = this.ukupno_poruka;
        }

        if (this.ukupno_poruka < this.brojPorukaStrana) {
            messages = folder.getMessages();
        } else {
            messages = folder.getMessages(pocetnaPozicija, zavrsnaPozicija);
        }

        for (int i = messages.length; i > 0; i--) {
            message = (MimeMessage) messages[i - 1];

            this.poruke.add(new Poruka(String.valueOf(message.getMessageNumber()), message.getSentDate(), message.getSentDate(), String.valueOf(message.getFrom()[0]), message.getSubject(), message.getContent().toString(), "0"));

        }

        folder.close(true);
        store.close();
        chechButtons();
    }

    public void promjenaMape() throws MessagingException, IOException {
        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", this.posluzitelj);
        session = Session.getInstance(properties, null);

        store = session.getStore("imap");
        store.connect(this.posluzitelj, this.korisnik, this.pass);

        folder = store.getFolder(this.odabrana_mapa);
        folder.open(Folder.READ_WRITE);

        this.ukupno_poruka = folder.getMessageCount();

        pocetnaPozicija = this.ukupno_poruka - this.brojPorukaStrana + 1;
        zavrsnaPozicija = this.ukupno_poruka;
        
        chechButtons();
        preuzmiPoruke();
    }

    public void prethodnePoruke() throws MessagingException, IOException {
        pocetnaPozicija = zavrsnaPozicija + 1;
        zavrsnaPozicija = pocetnaPozicija + this.brojPorukaStrana - 1;

        chechButtons();
        preuzmiPoruke();
    }

    public void sljedecePoruke() throws MessagingException, IOException {
        zavrsnaPozicija = pocetnaPozicija - 1;
        pocetnaPozicija = pocetnaPozicija - this.brojPorukaStrana;

        if (pocetnaPozicija <= 0) {
            pocetnaPozicija = 1;
        }

        chechButtons();
        preuzmiPoruke();
    }

    public void chechButtons() {
        if (this.ukupno_poruka <= this.brojPorukaStrana) {
            this.show_next = false;
            this.show_previous = false;
        } else {
            if (this.zavrsnaPozicija == this.ukupno_poruka) {
                this.show_previous = false;
                this.show_next = true;
            } else if (this.zavrsnaPozicija <= this.brojPorukaStrana) {
                this.show_next = false;
                this.show_previous = true;
            } else {
                this.show_next = true;
                this.show_previous = true;
            }
        }
    }

    public void obrisiPoruke() throws NoSuchProviderException, MessagingException, IOException {
        messages = null;
        folder = null;

        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", this.posluzitelj);
        session = Session.getInstance(properties, null);

        store = session.getStore("imap");
        store.connect(this.posluzitelj, this.korisnik, this.pass);

        folder = store.getFolder(this.odabrana_mapa);
        folder.open(Folder.READ_WRITE);

        messages = folder.getMessages();
        folder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
        folder.expunge();

        folder.close(true);
        store.close();

        preuzmiPoruke();
    }

    public String getOdabrana_mapa() {
        return odabrana_mapa;
    }

    public void setOdabrana_mapa(String odabrana_mapa) {
        this.odabrana_mapa = odabrana_mapa;
    }

    public int getUkupno_poruka() {
        return ukupno_poruka;
    }

    public void setUkupno_poruka(int ukupno_poruka) {
        this.ukupno_poruka = ukupno_poruka;
    }

    public boolean isShow_previous() {
        return show_previous;
    }

    public void setShow_previous(boolean show_previous) {
        this.show_previous = show_previous;
    }

    public boolean isShow_next() {
        return show_next;
    }

    public void setShow_next(boolean show_next) {
        this.show_next = show_next;
    }

    public List<Izbornik> getMape() {
        return mape;
    }

    public void setMape(List<Izbornik> mape) {
        this.mape = mape;
    }

    public List<Poruka> getPoruke() {
        return poruke;
    }

    public void setPoruke(List<Poruka> poruke) {
        this.poruke = poruke;
    }

    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
