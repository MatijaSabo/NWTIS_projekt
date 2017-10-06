/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.orm.Poruke;
import org.foi.nwtis.matsaboli2.orm.PorukeFacade;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
@Named(value = "pregledMqtt")
@SessionScoped
public class PregledMqttPoruka implements Serializable {

    @EJB
    private PorukeFacade porukeFacade;

    private List<Poruke> lista = new ArrayList<>();
    private int ukupno;
    private boolean show_next = false;
    private boolean show_previous = false;

    private int pocetak;
    private int brojPodatakaStrana;

    /**
     * Creates a new instance of PregledMqtt
     */
    public PregledMqttPoruka() {
        preuzmiPodatke();
    }

    public void preuzmiPodatke() {
        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");

        this.brojPodatakaStrana = Integer.parseInt(konf.dajPostavku("brojLinijaTablica"));
        this.pocetak = 0;
    }
    
    public void prikaziPodatke() {
        this.ukupno = porukeFacade.findAll().size();

        EntityManager em = porukeFacade.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Poruke> q = cb.createQuery(Poruke.class);
        Root<Poruke> c = q.from(Poruke.class);
        q.select(c);

        TypedQuery<Poruke> query = em.createQuery(q).setFirstResult(this.pocetak).setMaxResults(this.brojPodatakaStrana);
        lista = query.getResultList();
        
        checkButtons();
    }
    
    public void obrisiPoruke(){
        List<Poruke> temp = porukeFacade.findAll();
        
        for (Poruke poruke : temp){
            porukeFacade.remove(poruke);
        }
        
        prikaziPodatke();
    }

    public void prethodne() {
        if (this.pocetak >= this.brojPodatakaStrana) {
            this.pocetak = this.pocetak - this.brojPodatakaStrana;
        }

        prikaziPodatke();
    }

    public void sljedece() {
        if ((this.pocetak + this.brojPodatakaStrana) < this.ukupno) {
            this.pocetak = this.pocetak + this.brojPodatakaStrana;
        }

        prikaziPodatke();
    }

    public void checkButtons() {
        if (this.ukupno < this.brojPodatakaStrana) {
            this.show_next = false;
            this.show_previous = false;
        } else {
            if (this.pocetak == 0) {
                this.show_previous = false;
                this.show_next = true;
            } else if ((this.pocetak + this.brojPodatakaStrana) >= this.ukupno) {
                this.show_next = false;
                this.show_previous = true;
            } else {
                this.show_next = true;
                this.show_previous = true;
            }
        }
    }

    public List<Poruke> getLista() {
        return lista;
    }

    public void setLista(List<Poruke> lista) {
        this.lista = lista;
    }

    public int getUkupno() {
        return ukupno;
    }

    public void setUkupno(int ukupno) {
        this.ukupno = ukupno;
    }

    public boolean isShow_next() {
        return show_next;
    }

    public void setShow_next(boolean show_next) {
        this.show_next = show_next;
    }

    public boolean isShow_previous() {
        return show_previous;
    }

    public void setShow_previous(boolean show_previous) {
        this.show_previous = show_previous;
    }

}
