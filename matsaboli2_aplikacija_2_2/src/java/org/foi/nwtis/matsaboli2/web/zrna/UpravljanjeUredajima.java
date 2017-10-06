/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matsaboli2.web.podaci.Izbornik;
import org.foi.nwtis.matsaboli2.web.podaci.Lokacija;
import org.foi.nwtis.matsaboli2.ws.MeteoPodaci;
import org.foi.nwtis.matsaboli2.web.podaci.Uredjaj;
import org.foi.nwtis.matsaboli2.web.servisi.RESTUredaj;
import org.foi.nwtis.matsaboli2.web.servisi.RESTUredaji;
import org.foi.nwtis.matsaboli2.web.servisi.SoapWsClient;

/**
 *
 * @author Matija
 */
@Named(value = "upravljanjeUredajima")
@SessionScoped
public class UpravljanjeUredajima implements Serializable {

    private String naziv_add;
    private String adresa_add;

    private List<Izbornik> lista_izbornik = new ArrayList<>();

    private String edit_uredaj_id;
    private String naziv_edit;
    private String adresa_edit;
    private int edit_id = 0;

    private List<Uredjaj> uredaji_lista = new ArrayList<>();

    private String adresa_uradaj_id;
    private String adresa_uredaja;

    private String uredaj_zdanji_podaci_id;
    private MeteoPodaci zadnji_podaci;

    private String uredaj_trenutni_podaci_id;
    private MeteoPodaci trenutni_podaci;
    
    private String korisnik;
    private String pass;

    /**
     * Creates a new instance of UpravljanjeUredajima
     */
    public UpravljanjeUredajima() {
        postaviPodatke();
        dohvatiUredaje();
    }
    
    public void postaviPodatke(){
        this.korisnik = pullSessionUser();
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

    public void dohvatiUredaje() {
        lista_izbornik.clear();

        RESTUredaji rest = new RESTUredaji();
        String content = rest.getJson();

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonArray lista = reader.readArray();

        for (int i = 0; i < lista.size(); i++) {
            JsonObject jo = lista.getJsonObject(i);

            String id = String.valueOf(jo.getInt("ID"));
            String naziv = jo.getString("Naziv");

            Izbornik izbornik = new Izbornik(naziv, id);

            lista_izbornik.add(izbornik);
        }
    }

    public void dohvatiUredaj() {
        RESTUredaj rest = new RESTUredaj(edit_uredaj_id);
        String content = rest.getJson();

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();

        try {
            this.edit_id = jo.getInt("ID");
            this.naziv_edit = jo.getString("Naziv");
            this.adresa_edit = dajAdresuUredaja(Integer.parseInt(edit_uredaj_id));
        } catch (Exception ex) {
            this.edit_id = 0;
            this.adresa_edit = "";
            this.naziv_edit = "";
        }
    }

    public void azurirajUredaj() {
        if (this.edit_id != 0 && !this.adresa_edit.isEmpty() && !this.naziv_edit.isEmpty()) {
            JsonObjectBuilder json = Json.createObjectBuilder();
            json.add("Akcija", "EDIT");
            json.add("ID", this.edit_id);
            json.add("Naziv", this.naziv_edit);
            json.add("Adresa", this.adresa_edit);

            RESTUredaji rest = new RESTUredaji();
            String odgovor = rest.postJson(json.build().toString());

            if (odgovor.contains("1")) {
                this.naziv_edit = "";
                this.adresa_edit = "";
                this.edit_id = 0;
            } else {

            }

        }
    }

    public void dodajUredaj() {
        if (this.naziv_add != null && this.adresa_add != null
                && !this.naziv_add.isEmpty() && !this.adresa_add.isEmpty()) {
            
            JsonObjectBuilder json = Json.createObjectBuilder();
            json.add("Akcija", "ADD");
            json.add("Naziv", this.naziv_add);
            json.add("Adresa", this.adresa_add);
            
            RESTUredaji rest = new RESTUredaji();
            String odgovor = rest.postJson(json.build().toString());
            
            if(odgovor.contains("1")){
                this.adresa_add = "";
                this.naziv_add = "";
                
                dohvatiUredaje();
            } else {
                
            }
        }
    }

    public void dohvatiAdresuUredaja() {
        this.adresa_uredaja = "";
        this.adresa_uredaja = dajAdresuUredaja(Integer.parseInt(adresa_uradaj_id));
    }

    public String dajAdresuUredaja(int id) {
        return SoapWsClient.dajAdresuZaIoT(id, this.korisnik, this.pass);
    }

    public void dohvatiSveUredaje() {
        uredaji_lista.clear();

        RESTUredaji rest = new RESTUredaji();
        String content = rest.getJson();

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonArray lista = reader.readArray();

        for (int i = 0; i < lista.size(); i++) {
            JsonObject jo = lista.getJsonObject(i);

            Uredjaj uredjaj = new Uredjaj();
            uredjaj.setId(jo.getInt("ID"));
            uredjaj.setNaziv(jo.getString("Naziv"));

            String longitude = String.valueOf(jo.getJsonNumber("Longitude"));
            String latitude = String.valueOf(jo.getJsonNumber("Latitude"));

            uredjaj.setGeoloc(new Lokacija(latitude, longitude));

            uredaji_lista.add(uredjaj);
        }
    }

    public void dohvatiZadnjeMeteoPodatke() {
        String user = "admin";
        String pass = "admin";
        int id = Integer.parseInt(this.uredaj_zdanji_podaci_id);

        zadnji_podaci = SoapWsClient.zadnjiMeteoPodaciZaIoT(id, user, pass);
    }

    public void dohvatiTrenutneMeteoPodatke() {
        String user = "admin";
        String pass = "admin";
        int id = Integer.parseInt(this.uredaj_trenutni_podaci_id);

        trenutni_podaci = SoapWsClient.vazeciMeteoPodaciZaIoT(id, user, pass);
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

    public String getEdit_uredaj_id() {
        return edit_uredaj_id;
    }

    public void setEdit_uredaj_id(String edit_uredaj_id) {
        this.edit_uredaj_id = edit_uredaj_id;
    }

    public String getNaziv_edit() {
        return naziv_edit;
    }

    public void setNaziv_edit(String naziv_edit) {
        this.naziv_edit = naziv_edit;
    }

    public String getAdresa_edit() {
        return adresa_edit;
    }

    public void setAdresa_edit(String adresa_edit) {
        this.adresa_edit = adresa_edit;
    }

    public List<Uredjaj> getUredaji_lista() {
        return uredaji_lista;
    }

    public void setUredaji_lista(List<Uredjaj> uredaji_lista) {
        this.uredaji_lista = uredaji_lista;
    }

    public String getAdresa_uradaj_id() {
        return adresa_uradaj_id;
    }

    public void setAdresa_uradaj_id(String adresa_uradaj_id) {
        this.adresa_uradaj_id = adresa_uradaj_id;
    }

    public String getAdresa_uredaja() {
        return adresa_uredaja;
    }

    public void setAdresa_uredaja(String adresa_uredaja) {
        this.adresa_uredaja = adresa_uredaja;
    }

    public String getUredaj_zdanji_podaci_id() {
        return uredaj_zdanji_podaci_id;
    }

    public void setUredaj_zdanji_podaci_id(String uredaj_zdanji_podaci_id) {
        this.uredaj_zdanji_podaci_id = uredaj_zdanji_podaci_id;
    }

    public MeteoPodaci getZadnji_podaci() {
        return zadnji_podaci;
    }

    public void setZadnji_podaci(MeteoPodaci zadnji_podaci) {
        this.zadnji_podaci = zadnji_podaci;
    }

    public String getUredaj_trenutni_podaci_id() {
        return uredaj_trenutni_podaci_id;
    }

    public void setUredaj_trenutni_podaci_id(String uredaj_trenutni_podaci_id) {
        this.uredaj_trenutni_podaci_id = uredaj_trenutni_podaci_id;
    }

    public MeteoPodaci getTrenutni_podaci() {
        return trenutni_podaci;
    }

    public void setTrenutni_podaci(MeteoPodaci trenutni_podaci) {
        this.trenutni_podaci = trenutni_podaci;
    }

    public List<Izbornik> getLista_izbornik() {
        return lista_izbornik;
    }

    public void setLista_izbornik(List<Izbornik> lista_izbornik) {
        this.lista_izbornik = lista_izbornik;
    }
}
