/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.dretve;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.matsaboli2.orm.Poruke;
import org.foi.nwtis.matsaboli2.orm.PorukeFacade;
import org.foi.nwtis.matsaboli2.podaci.MQTTStatistika;
import org.foi.nwtis.matsaboli2.zrna.SingletonSessionBean;

/**
 *
 * @author Matija
 */
public class MqttPoruke extends Thread {

    PorukeFacade porukeFacade = lookupPorukeFacadeBean();

    private MQTTStatistika statistika = new MQTTStatistika();
    private long pocetak = 0;
    private long kraj = 0;
    private int counter = 0;
    private int brojac = 0;
    private List<String> poruke = new ArrayList<>();

    String user = "";
    String password = "";
    String host = "nwtis.foi.hr";
    int port = 61613;
    String destination = "/NWTiS/";

    @Override
    public void interrupt() {
        System.out.println("--- MQTT Interupt ---");
        super.interrupt();
    }

    @Override
    public void run() {

        this.user = SingletonSessionBean.mqttUser;
        this.password = SingletonSessionBean.mqttPass;
        this.destination = this.destination + this.user;
        
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost(host, port);
            mqtt.setUserName(user);
            mqtt.setPassword(password);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MqttPoruke.class.getName()).log(Level.SEVERE, null, ex);
        }

        final CallbackConnection connection = mqtt.callbackConnection();
        connection.listener(new org.fusesource.mqtt.client.Listener() {
            long count = 0;

            @Override
            public void onConnected() {
                System.out.println("Otvorena veza na MQTT");
            }

            @Override
            public void onDisconnected() {
                System.out.println("Prekinuta veza na MQTT");
                System.exit(0);
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("Problem u vezi na MQTT");
                System.exit(-2);
            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer msg, Runnable ack) {
                count++;
                brojac++;

                if (count % SingletonSessionBean.mqtt_slot == 1) {
                    pocetak = System.currentTimeMillis();
                }

                String body = msg.utf8().toString().trim();

                JsonReader reader = Json.createReader(new StringReader(body));
                JsonObject json = reader.readObject();

                int id = json.getInt("IoT");
                String vrijeme = json.getString("vrijeme");
                String tekst = json.getString("tekst");
                String status = json.getString("status");

                Poruke poruka = new Poruke();
                poruka.setIotId(id);
                poruka.setStatus(Integer.parseInt(status));
                poruka.setTekst(tekst);
                poruka.setVrijeme(vrijeme);
                porukeFacade.create(poruka);
                
                poruke.add(tekst);

                if (count % SingletonSessionBean.mqtt_slot == 0) {
                    counter++;
                    kraj = System.currentTimeMillis();

                    statistika.setStartTime(new Date(pocetak));
                    statistika.setEndTime(new Date(kraj));
                    statistika.setJMSNumber(counter);
                    statistika.setMessagesNumber(brojac);
                    statistika.setMessagesData(poruke);
                    
                    try {
                        sendJMSMessageToNWTiS_matsaboli2_2();
                        System.out.println("Poslana JMS poruka MQTT");
                    } catch (JMSException | NamingException ex) {
                        Logger.getLogger(MqttPoruke.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    poruke.clear();
                    brojac = 0;
                    
                }

                System.out.println("Pruka: " + body);

            }
        });
        connection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                Topic[] topics = {new Topic(destination, QoS.AT_LEAST_ONCE)};
                connection.subscribe(topics, new Callback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] qoses) {
                        System.out.println("Pretplata na: " + destination);
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        System.out.println("Problem kod pretplate na: " + destination);
                        System.exit(-2);
                    }
                });
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("Neuspjela pretplata na: " + destination);
                System.exit(-2);
            }
        });

        // Wait forever..
        synchronized (MqttPoruke.class) {
            while (true) {
                try {
                    MqttPoruke.class.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MqttPoruke.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private PorukeFacade lookupPorukeFacadeBean() {
        try {
            Context c = new InitialContext();
            return (PorukeFacade) c.lookup("java:global/matsaboli2_aplikacija_2/matsaboli2_aplikacija_2_1/PorukeFacade!org.foi.nwtis.matsaboli2.orm.PorukeFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private void sendJMSMessageToNWTiS_matsaboli2_2() throws JMSException, NamingException {
        Context c = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) c.lookup("jms/NWTiS_QF_matsaboli2");
        Connection conn = null;
        Session s = null;
        try {
            conn = cf.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);

            ObjectMessage object = s.createObjectMessage();
            object.setObject((Serializable) ((Object) this.statistika));

            Destination destination = (Destination) c.lookup("jms/NWTiS_matsaboli2_2");
            MessageProducer mp = s.createProducer(destination);
            mp.send(object);
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

}
