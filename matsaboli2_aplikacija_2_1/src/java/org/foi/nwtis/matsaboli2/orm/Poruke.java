/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.orm;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Matija
 */
@Entity
@Table(name = "PORUKE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Poruke.findAll", query = "SELECT p FROM Poruke p")
    , @NamedQuery(name = "Poruke.findById", query = "SELECT p FROM Poruke p WHERE p.id = :id")
    , @NamedQuery(name = "Poruke.findByIotId", query = "SELECT p FROM Poruke p WHERE p.iotId = :iotId")
    , @NamedQuery(name = "Poruke.findByVrijeme", query = "SELECT p FROM Poruke p WHERE p.vrijeme = :vrijeme")
    , @NamedQuery(name = "Poruke.findByTekst", query = "SELECT p FROM Poruke p WHERE p.tekst = :tekst")
    , @NamedQuery(name = "Poruke.findByStatus", query = "SELECT p FROM Poruke p WHERE p.status = :status")})
public class Poruke implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IOT_ID")
    private int iotId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "VRIJEME")
    private String vrijeme;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10000)
    @Column(name = "TEKST")
    private String tekst;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS")
    private int status;

    public Poruke() {
    }

    public Poruke(Integer id) {
        this.id = id;
    }

    public Poruke(Integer id, int iotId, String vrijeme, String tekst, int status) {
        this.id = id;
        this.iotId = iotId;
        this.vrijeme = vrijeme;
        this.tekst = tekst;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIotId() {
        return iotId;
    }

    public void setIotId(int iotId) {
        this.iotId = iotId;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Poruke)) {
            return false;
        }
        Poruke other = (Poruke) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.matsaboli2.orm.Poruke[ id=" + id + " ]";
    }
    
}
