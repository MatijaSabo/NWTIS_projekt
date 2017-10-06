/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zrna;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import org.foi.nwtis.matsaboli2.dretve.ProvjeraEmailPoruka;

/**
 *
 * @author Matija
 */
@Startup
@Singleton
@LocalBean
public class SingletonSessionBean {
    
    public static String emailServer = "";
    public static String emailPort = "";
    public static String emailUser = "";
    public static String emailPass = "";
    public static String emailSubject = "";
    public static String emailNwtisFolder = "";
    public static int intervalDretve = 0;
    public static int mqtt_slot = 0;
    public static String mqttUser = "";
    public static String mqttPass = "";
    
    ProvjeraEmailPoruka dretva;
    
    @PostConstruct
    void init (){
        System.out.println("Singleton Start");
        dretva = new ProvjeraEmailPoruka();
        dretva.start();
    }

    @PreDestroy
    void destroy(){
        System.out.println("Singleton End");
        if(dretva != null){
            dretva.interrupt();
        }
    }
}
