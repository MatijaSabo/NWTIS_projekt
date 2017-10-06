/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
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

    private String kor_ime;
    private String pass;
    
    private boolean login_error = false;
    
    /**
     * Creates a new instance of Autentikacija
     */
    public Autentikacija() {
    }
    
    public String loginUser(){
        if(this.kor_ime != null && this.pass != null
                && !this.kor_ime.isEmpty() && !this.pass.isEmpty()){
            
            boolean status = statefulSessionBean.login(this.kor_ime, this.pass);
            
            if(status){
                storeDataToSession(this.kor_ime, this.pass);
                
                this.login_error = false;
                return "login";
            } else {
                this.login_error = true;
                return "error";
            }
        } else {
            this.login_error = true;
            return "error";
        }
    }
    
    public String logout(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        
        return "logout";
    }
    
    public void storeDataToSession(String user, String pass) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("kor_ime", user);
        session.setAttribute("pass", pass);
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

    public boolean isLogin_error() {
        return login_error;
    }

    public void setLogin_error(boolean login_error) {
        this.login_error = login_error;
    }
}
