/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.podaci;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matija
 */
public class SerializableClass implements Serializable {

    List<MQTTStatistika> MqttLista;
    List<EmailStatistika> EmailLista;

    public SerializableClass() {
        MqttLista = new ArrayList<>();
        EmailLista = new ArrayList<>();
    }

    public List<MQTTStatistika> getMqttLista() {
        return MqttLista;
    }

    public void setMqttLista(List<MQTTStatistika> MqttLista) {
        this.MqttLista = MqttLista;
    }

    public List<EmailStatistika> getEmailLista() {
        return EmailLista;
    }

    public void setEmailLista(List<EmailStatistika> EmailLista) {
        this.EmailLista = EmailLista;
    }
}
