/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zrna;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import org.foi.nwtis.matsaboli2.podaci.EmailStatistika;
import org.foi.nwtis.matsaboli2.podaci.MQTTStatistika;
import org.foi.nwtis.matsaboli2.podaci.SerializableClass;

/**
 *
 * @author Matija
 */
@Singleton
@LocalBean
@Startup
public class SingletonSessionBean {

    public static List<MQTTStatistika> MqttList;
    public static List<EmailStatistika> EmailList;

    @PostConstruct
    void init() {
        System.out.println("Singleton init");

        deserijalizacija();
    }

    @PreDestroy
    void destroy() {
        serijalizacija();
        System.out.println("Singleton destroy");
    }

    private void serijalizacija() {

        SerializableClass object = new SerializableClass();
        object.setEmailLista(EmailList);
        object.setMqttLista(MqttList);

        try {
            FileOutputStream fileOut = new FileOutputStream("project_data.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data!");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private void deserijalizacija() {
        SerializableClass object = null;
        
        try {
            FileInputStream fileIn = new FileInputStream("project_data.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            object = (SerializableClass) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }

        if(object != null){
            MqttList = object.getMqttLista();
            EmailList = object.getEmailLista();
            System.out.printf("Deserialized data!");
        } else {
            MqttList = new ArrayList<>();
            EmailList = new ArrayList<>();
        }
    }

}
