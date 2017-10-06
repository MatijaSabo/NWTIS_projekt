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
import org.foi.nwtis.matsaboli2.web.podaci.Korisnik;
import org.foi.nwtis.matsaboli2.web.servisi.RESTKorisnici;
import org.foi.nwtis.matsaboli2.ws.RESTKorisnik;

/**
 *
 * @author Matija
 */
@Named(value = "upravljanjeKorisnicima")
@SessionScoped
public class UpravljanjeKorisnicima implements Serializable {

    private int id = 0;
    private String prezime;
    private String kor_ime;
    private String pass;
    private String pass2;
    private String email;
    private String temp_kor_ime;

    private boolean error_flag = false;
    private boolean all_data_error = false;
    private boolean edit_error = false;
    private boolean edit_success = false;
    private boolean user_error = false;
    private boolean pass_error = false;
    private boolean email_error = false;

    private List<Korisnik> lista = new ArrayList<>();

    /**
     * Creates a new instance of UpravljanjeKorisnicima
     */
    public UpravljanjeKorisnicima() {
    }

    public void prikaziKorisnike() {
        this.edit_error = false;
        this.edit_success = false;
        this.user_error = false;
        this.pass_error = false;
        this.email_error = false;
        this.error_flag = false;
        this.all_data_error = false;

        lista.clear();

        RESTKorisnici rest = new RESTKorisnici();
        String content = rest.getJson();

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonArray lista = reader.readArray();

        for (int i = 0; i < lista.size(); i++) {
            JsonObject jo = lista.getJsonObject(i);

            Korisnik user = new Korisnik();
            user.setID(jo.getInt("ID"));
            user.setKor_ime(jo.getString("Kor_ime"));
            user.setPrezime(jo.getString("Prezime"));
            user.setEmail(jo.getString("Email"));
            user.setPass("");

            this.lista.add(user);
        }
    }

    public void prikaziKorisnika() {

        this.edit_error = false;
        this.edit_success = false;
        this.user_error = false;
        this.pass_error = false;
        this.email_error = false;
        this.error_flag = false;
        this.all_data_error = false;

        this.kor_ime = "";
        this.prezime = "";
        this.pass = "";
        this.pass2 = "";
        this.email = "";

        String user = pullSessionUser();
        RESTKorisnik rest = new RESTKorisnik(user);
        String content = rest.getJson();

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();

        try {
            this.kor_ime = jo.getString("Kor_ime");
            this.temp_kor_ime = jo.getString("Kor_ime");
            this.prezime = jo.getString("Prezime");
            this.email = jo.getString("Email");
            this.id = jo.getInt("ID");
        } catch (NullPointerException ex) {
        }
    }

    public String pullSessionUser() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String user = (String) session.getAttribute("kor_ime");
        return user;
    }

    public String editUser() {
        this.edit_error = false;
        this.edit_success = false;
        this.user_error = false;
        this.error_flag = false;
        this.all_data_error = false;
        this.pass_error = false;
        this.email_error = false;

        if (this.kor_ime != null && this.pass != null && this.pass2 != null && this.prezime != null && this.email != null
                && !this.kor_ime.isEmpty() && !this.pass.isEmpty() && !this.pass2.isEmpty() && !this.prezime.isEmpty()
                && !this.email.isEmpty() && (this.pass.equals(this.pass2))) {

            validateSamePassword();
            checkEmailData();

            if (!this.pass_error && !this.email_error) {
                if (!this.temp_kor_ime.equals(this.kor_ime)) {
                    RESTKorisnik rest = new RESTKorisnik(this.kor_ime);
                    String content = rest.getJson();

                    JsonReader reader = Json.createReader(new StringReader(content));
                    JsonObject jo = reader.readObject();

                    try {
                        String user = jo.getString("Kor_ime");
                        this.user_error = true;
                    } catch (NullPointerException ex) {
                        this.user_error = false;
                    }

                    if (!this.user_error) {
                        JsonObjectBuilder job = Json.createObjectBuilder();
                        job.add("ID", this.id);
                        job.add("Akcija", "EDIT");
                        job.add("Kor_ime", this.kor_ime);
                        job.add("Pass", this.pass);
                        job.add("Prezime", this.prezime);
                        job.add("Email", this.email);

                        RESTKorisnici rest_2 = new RESTKorisnici();
                        String odgovor = rest_2.postJson(job.build().toString());

                        if (odgovor.contains("1")) {
                            this.id = 0;
                            this.prezime = "";
                            this.kor_ime = "";
                            this.pass = "";
                            this.pass2 = "";
                            this.email = "";
                            this.edit_success = true;
                        } else {
                            this.edit_error = true;
                        }
                    }

                } else {
                    JsonObjectBuilder jo = Json.createObjectBuilder();
                    jo.add("ID", this.id);
                    jo.add("Akcija", "EDIT");
                    jo.add("Kor_ime", this.kor_ime);
                    jo.add("Pass", this.pass);
                    jo.add("Prezime", this.prezime);
                    jo.add("Email", this.email);

                    RESTKorisnici rest = new RESTKorisnici();
                    String odgovor = rest.postJson(jo.build().toString());

                    if (odgovor.contains("1")) {
                        this.id = 0;
                        this.prezime = "";
                        this.kor_ime = "";
                        this.pass = "";
                        this.pass2 = "";
                        this.email = "";
                        this.edit_success = true;
                    } else {
                        this.edit_error = true;
                    }
                }
            }
        } else {
            this.all_data_error = true;
        }

        if (this.all_data_error || this.edit_error || this.user_error
                || this.pass_error || this.email_error) {
            this.error_flag = true;
        }
        
        return "edit";
    }

    public void validateSamePassword() {
        if (this.pass2.equals(this.pass)) {
            this.pass_error = false;
        } else {
            this.pass_error = true;
        }
    }

    public void checkEmailData() {
        if (this.email.contains("@") && (this.email.indexOf("@") > 0)
                && !this.email.contains(" ") && this.email.contains(".")
                && ((this.email.lastIndexOf(".") - this.email.indexOf("@")) > 1)
                && (this.email.indexOf("@") == this.email.lastIndexOf("@"))
                && ((this.email.lastIndexOf(".") + 1) != this.email.length())) {
            this.email_error = false;
        } else {
            this.email_error = true;
        }
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
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

    public String getPass2() {
        return pass2;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Korisnik> getLista() {
        return lista;
    }

    public void setLista(List<Korisnik> lista) {
        this.lista = lista;
    }

    public boolean isError_flag() {
        return error_flag;
    }

    public void setError_flag(boolean error_flag) {
        this.error_flag = error_flag;
    }

    public boolean isAll_data_error() {
        return all_data_error;
    }

    public void setAll_data_error(boolean all_data_error) {
        this.all_data_error = all_data_error;
    }

    public boolean isEdit_error() {
        return edit_error;
    }

    public void setEdit_error(boolean edit_error) {
        this.edit_error = edit_error;
    }

    public boolean isEdit_success() {
        return edit_success;
    }

    public void setEdit_success(boolean edit_success) {
        this.edit_success = edit_success;
    }

    public boolean isUser_error() {
        return user_error;
    }

    public void setUser_error(boolean user_error) {
        this.user_error = user_error;
    }

    public boolean isPass_error() {
        return pass_error;
    }

    public void setPass_error(boolean pass_error) {
        this.pass_error = pass_error;
    }

    public boolean isEmail_error() {
        return email_error;
    }

    public void setEmail_error(boolean email_error) {
        this.email_error = email_error;
    }

    
}
