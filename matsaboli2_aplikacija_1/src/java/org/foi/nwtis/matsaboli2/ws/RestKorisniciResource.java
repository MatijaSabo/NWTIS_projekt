/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 * REST Web Service
 *
 * @author Matija
 */
public class RestKorisniciResource {
    
    private String korisnickoIme;

    /**
     * Creates a new instance of RestKorisniciResource
     */
    private RestKorisniciResource(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    /**
     * Get instance of the RestKorisniciResource
     */
    public static RestKorisniciResource getInstance(String korisnickoIme) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of RestKorisniciResource class.
        return new RestKorisniciResource(korisnickoIme);
    }

    /**
     * Retrieves representation of an instance of org.foi.nwtis.matsaboli2.rest.klijenti.RestKorisniciResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Connection veza = null;
        
        JsonObjectBuilder job = Json.createObjectBuilder();
        
        try {
            Class.forName(bp_driver);
            veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);

            PreparedStatement select = veza.prepareStatement("SELECT * FROM korisnici WHERE kor_ime = ?");
            select.setString(1, korisnickoIme);
            ResultSet result = select.executeQuery();

            while (result.next()) {
                job.add("ID", result.getInt("id"));
                job.add("Kor_ime", result.getString("kor_ime"));
                job.add("Pass", result.getString("pass"));
                job.add("Prezime", result.getString("prezime"));
                job.add("Email", result.getString("email"));
            }
            
            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, "-");
            dnevnik.setString(2, "getJson()");
            dnevnik.setString(3, "/matsaboli2_aplikacija_1/webresources/RESTKorisnici/" + korisnickoIme);
            dnevnik.setInt(4, 2);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(RestKorisniciResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return job.build().toString();
    }

    /**
     * PUT method for updating or creating an instance of RestKorisniciResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource RestKorisniciResource
     */
    @DELETE
    public void delete() {
    }
}
