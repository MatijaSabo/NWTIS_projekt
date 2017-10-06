/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zrna;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.foi.nwtis.matsaboli2.dretve.MqttPoruke;
import org.foi.nwtis.matsaboli2.ws.RESTKorisnik;

@Stateful
@LocalBean
public class StatefulSessionBean {
    
    MqttPoruke dretva = null;

    public String loginUser(String user, String pass) {
        RESTKorisnik rest = new RESTKorisnik(user);
        String content = rest.getJson();
        JsonReader reader = Json.createReader(new StringReader(content));

        JsonObject jo = reader.readObject();

        String kor_ime_json = "";
        String pass_json = "";
        
        try {
            kor_ime_json = jo.getString("Kor_ime");
            pass_json = jo.getString("Pass");
        } catch (NullPointerException ex) {
            return "ERROR";
        }

        if (kor_ime_json.equals(user) && pass_json.equals(pass)) {
            String komanda = "USER " + user + "; PASSWD " + pass + "; IoT_Master START;";
            String odgovor = posaljiKomandu(komanda);
            System.out.println("ODGOVOR: " + odgovor);
            
            SingletonSessionBean.mqttUser = kor_ime_json;
            SingletonSessionBean.mqttPass = pass_json;
            
            dretva = new MqttPoruke();
            dretva.start();
            
            return "OK";
        } else {
            return "ERROR";
        }
    }
    
    public void logout(){
        if(dretva != null){
            dretva.interrupt();
        }
    }
    
    public String posaljiKomandu(String komanda){
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        String odgovor = "-";

        try {
            socket = new Socket("localhost", 12345);
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
            Logger.getLogger(StatefulSessionBean.class.getName()).log(Level.SEVERE, null, ex);
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
}
