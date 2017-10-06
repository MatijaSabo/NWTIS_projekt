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
import org.foi.nwtis.matsaboli2.podaci.EmailStatistika;
import org.foi.nwtis.matsaboli2.zrna.SingletonSessionBean;

/**
 *
 * @author Matija
 */
@Named(value = "prikazEmailStatitike")
@SessionScoped
public class PrikazEmailStatitike implements Serializable{
    
    private List<EmailStatistika> lista = new ArrayList<>();
    
    private TimeZone timeZone;
    
    /**
     * Creates a new instance of PrikazEmailStatitike
     */
    public PrikazEmailStatitike() {
    }
    
    public void showData(){
        lista.clear();
        
        for(EmailStatistika data : SingletonSessionBean.EmailList){
            lista.add(data);
        }
        
    }
    
    public void deleteData(){
        SingletonSessionBean.EmailList.clear();
        showData();
    }

    public List<EmailStatistika> getLista() {
        return lista;
    }

    public void setLista(List<EmailStatistika> lista) {
        this.lista = lista;
    }
    
    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
