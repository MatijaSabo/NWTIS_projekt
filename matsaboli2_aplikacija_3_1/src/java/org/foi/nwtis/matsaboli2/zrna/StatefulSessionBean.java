/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zrna;

import java.io.StringReader;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.foi.nwtis.matsaboli2.ws.RESTKorisnik;

/**
 *
 * @author Matija
 */
@Stateful
@LocalBean
public class StatefulSessionBean {

    public boolean login(String user, String pass){
        RESTKorisnik rest = new RESTKorisnik(user);
        String content = rest.getJson();
        JsonReader reader = Json.createReader(new StringReader(content));

        JsonObject jo = reader.readObject();

        String kor_ime_json = "";
        String pass_json = "";
        
        try {
            kor_ime_json = jo.getString("Kor_ime");
            pass_json = jo.getString("Pass");
            
            if(kor_ime_json.equals(user) && pass_json.equals(pass)){
                return true;
            } else {
                return false;
            }
            
        } catch (NullPointerException ex) {
            return false;
        }
    }
}
