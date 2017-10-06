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
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matsaboli2.web.servisi.RESTKorisnici;
import org.foi.nwtis.matsaboli2.ws.RESTKorisnik;
import org.foi.nwtis.matsaboli2.zrna.StatefulSessionBean;

/**
 *
 * @author Matija
 */
@Named(value = "autentikacija")
@SessionScoped
public class Autentikacija implements Serializable {

    @EJB
    private StatefulSessionBean statefulSessionBean;

    private boolean login_error = false;
    private String kor_ime_login;
    private String pass_login;

    private boolean flag_error = false;
    private boolean flag_success = false;
    private boolean all_data_error = false;
    private boolean email_error = false;
    private boolean pass_error = false;
    private boolean user_error = false;
    private boolean register_error = false;

    private String prezime;
    private String kor_ime;
    private String pass;
    private String pass_2;
    private String email;

    /**
     * Creates a new instance of Autentikacija
     */
    public Autentikacija() {
    }

    public String login() {
        
        this.flag_error = false;
        this.flag_success = false;
        this.all_data_error = false;
        this.email_error = false;
        this.pass_error = false;
        this.user_error = false;
        this.register_error = false;
        
        if (this.kor_ime_login != null && this.pass_login != null
                && !this.kor_ime_login.isEmpty() && !this.pass_login.isEmpty()) {
            String status = statefulSessionBean.loginUser(this.kor_ime_login, this.pass_login);

            if ("OK".equals(status)) {
                login_error = false;
                storeDataToSession(this.kor_ime_login, this.pass_login);
                return "login";
            } else {
                login_error = true;
                return "error";
            }
        } else {
            login_error = true;
            return "error";
        }
    }

    public String register() {
        boolean status = checkRegisterData();

        if (status) {
            validateSamePassword();
            checkEmailData();
            checkUsername();

            if (!this.all_data_error && !this.user_error && !this.email_error && !this.pass_error) {
                JsonObjectBuilder jo = Json.createObjectBuilder();
                jo.add("Akcija", "ADD");
                jo.add("Kor_ime", this.kor_ime);
                jo.add("Pass", this.pass);
                jo.add("Prezime", this.prezime);
                jo.add("Email", this.email);
                String json = jo.build().toString();

                RESTKorisnici rest = new RESTKorisnici();
                String content = rest.postJson(json);

                if (content.contains("1")) {
                    this.register_error = false;
                    this.flag_success = true;
                    
                    this.prezime = "";
                    this.kor_ime = "";
                    this.pass = "";
                    this.pass_2 = "";
                    this.email = "";
                    
                } else {
                    this.register_error = true;
                }
            } else {
                this.flag_error = true;
            }
        } else {
            this.flag_error = true;
        }
        
        this.login_error = false;

        return "registracija";
    }

    public boolean checkRegisterData() {
        if (this.kor_ime != null && this.pass != null && this.prezime != null && this.email != null && this.pass_2 != null
                && !this.kor_ime.isEmpty() && !this.prezime.isEmpty() && !this.pass.isEmpty() && !this.pass_2.isEmpty() && !this.email.isEmpty()) {
            all_data_error = false;
            return true;
        } else {
            all_data_error = true;
            return false;
        }
    }

    public void validateSamePassword() {
        if (this.pass_2.equals(this.pass)) {
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

    public void checkUsername() {
        RESTKorisnik rest = new RESTKorisnik(this.kor_ime);
        String content = rest.getJson();

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();

        String kor_ime_json = null;

        try {
            kor_ime_json = jo.getString("Kor_ime");
            this.user_error = true;
        } catch (NullPointerException ex) {
            this.user_error = false;
        }
    }

    public String logout() {
        this.login_error = false;
        this.flag_error = false;
        this.flag_success = false;
        this.all_data_error = false;
        this.email_error = false;
        this.pass_error = false;
        this.user_error = false;
        this.register_error = false;

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();

        return "logout";
    }

    public void storeDataToSession(String user, String pass) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("kor_ime", user);
        session.setAttribute("pass", pass);
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

    public String getKor_ime_login() {
        return kor_ime_login;
    }

    public void setKor_ime_login(String kor_ime_login) {
        this.kor_ime_login = kor_ime_login;
    }

    public String getPass_login() {
        return pass_login;
    }

    public void setPass_login(String pass_login) {
        this.pass_login = pass_login;
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

    public String getPass_2() {
        return pass_2;
    }

    public void setPass_2(String pass_2) {
        this.pass_2 = pass_2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLogin_error() {
        return login_error;
    }

    public void setLogin_error(boolean login_error) {
        this.login_error = login_error;
    }

    public boolean isRegister_error() {
        return register_error;
    }

    public void setRegister_error(boolean register_error) {
        this.register_error = register_error;
    }

    public boolean isEmail_error() {
        return email_error;
    }

    public void setEmail_error(boolean email_error) {
        this.email_error = email_error;
    }

    public boolean isPass_error() {
        return pass_error;
    }

    public void setPass_error(boolean pass_error) {
        this.pass_error = pass_error;
    }

    public boolean isUser_error() {
        return user_error;
    }

    public void setUser_error(boolean user_error) {
        this.user_error = user_error;
    }

    public boolean isFlag_error() {
        return flag_error;
    }

    public void setFlag_error(boolean flag_error) {
        this.flag_error = flag_error;
    }

    public boolean isFlag_success() {
        return flag_success;
    }

    public void setFlag_success(boolean flag_success) {
        this.flag_success = flag_success;
    }

    public boolean isAll_data_error() {
        return all_data_error;
    }

    public void setAll_data_error(boolean all_data_error) {
        this.all_data_error = all_data_error;
    }
}
