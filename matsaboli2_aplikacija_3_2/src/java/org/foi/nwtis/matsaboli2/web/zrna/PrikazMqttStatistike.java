/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.foi.nwtis.matsaboli2.podaci.MQTTStatistika;
import org.foi.nwtis.matsaboli2.zrna.SingletonSessionBean;

/**
 *
 * @author Matija
 */
@Named(value = "prikazMqttStatistike")
@SessionScoped
public class PrikazMqttStatistike implements Serializable {

    private List<MQTTStatistika> lista = new ArrayList<>();
    
    private TimeZone timeZone;
    
    /**
     * Creates a new instance of PrikazMqttStatistike
     */
    public PrikazMqttStatistike() {
    }
    
    public void showData(){
        lista.clear();
        
        for(MQTTStatistika data : SingletonSessionBean.MqttList){
            lista.add(data);
        }
    }
    
    public String refractorList(List<String> lista){
        String string = "";
        
        for (String data : lista){
            string = string + data + "\n";
        }        
        
        return string;
    }
    
    public void deleteData(){
        SingletonSessionBean.MqttList.clear();
        showData();
    }

    public List<MQTTStatistika> getLista() {
        return lista;
    }

    public void setLista(List<MQTTStatistika> lista) {
        this.lista = lista;
    }
    
    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
