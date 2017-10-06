/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.ws;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.GMKlijent;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.matsaboli2.web.podaci.Lokacija;

/**
 * REST Web Service
 *
 * @author Matija
 */
@Path("/UredajiREST")
public class RestUredajiResourceContainer {
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RestUredajiResourceContainer
     */
    public RestUredajiResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.matsaboli2.ws.RestUredajiResourceContainer
     *
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

        JsonArrayBuilder jab = Json.createArrayBuilder();

        try {
            Class.forName(bp_driver);
            veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);

            PreparedStatement select = veza.prepareStatement("SELECT * FROM uredaji");
            ResultSet result = select.executeQuery();

            while (result.next()) {
                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("ID", result.getInt("id"));
                job.add("Naziv", result.getString("naziv"));
                job.add("Longitude", result.getFloat("longitude"));
                job.add("Latitude", result.getFloat("latitude"));
                job.add("Status", result.getString("status"));
                jab.add(job);
            }
            
            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, "-");
            dnevnik.setString(2, "getJson()");
            dnevnik.setString(3, "/matsaboli2_aplikacija_1/webresources/UredajiREST");
            dnevnik.setInt(4, 2);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(RestUredajiResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jab.build().toString();
    }

    /**
     * POST method for creating an instance of RestUredajiResource
     *
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postJson(String content) {
        String status = "0";
        JsonReader reader = Json.createReader(new StringReader(content));

        JsonObject jo = reader.readObject();
        String akcija = jo.getString("Akcija");
        String naziv = jo.getString("Naziv");
        String adresa = jo.getString("Adresa");

        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Connection veza = null;

        try {
            Class.forName(bp_driver);
            veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);

            GMKlijent gm = new GMKlijent();
            Lokacija lokacija = gm.getGeoLocation(adresa);

            PreparedStatement select = veza.prepareStatement("SELECT MAX(id) AS id FROM uredaji");
            PreparedStatement insert = veza.prepareStatement("INSERT INTO uredaji (id,naziv,latitude,longitude) VALUES (?,?,?,?)");
            PreparedStatement update = veza.prepareStatement("UPDATE uredaji SET naziv = ?, latitude = ?, longitude = ? WHERE id = ?");

            if ("ADD".equals(akcija)) {
                ResultSet selectResult = select.executeQuery();
                selectResult.next();
                int id = selectResult.getInt("id");

                insert.setInt(1, id + 1);
                insert.setString(2, naziv);
                insert.setFloat(3, Float.parseFloat(lokacija.getLatitude()));
                insert.setFloat(4, Float.parseFloat(lokacija.getLongitude()));
                int flag = insert.executeUpdate();

                if (flag == 0) {
                    status = "0";
                } else {
                    status = "1";
                }
            } else if ("EDIT".equals(akcija)) {
                int id = jo.getInt("ID");

                update.setString(1, naziv);
                update.setFloat(2, Float.parseFloat(lokacija.getLatitude()));
                update.setFloat(3, Float.parseFloat(lokacija.getLongitude()));
                update.setInt(4, id);
                int flag = update.executeUpdate();

                if (flag == 0) {
                    status = "0";
                } else {
                    status = "1";
                }
            }
            
            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, "-");
            dnevnik.setString(2, "postJson() - " + akcija);
            dnevnik.setString(3, "/matsaboli2_aplikacija_1/webresources/UredajiREST");
            dnevnik.setInt(4, 2);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(RestUredajiResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            status = "0";
        }

        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("Status", status);

        return job.build().toString();
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public RestUredajiResource getRestUredajiResource(@PathParam("id") String id) {
        return RestUredajiResource.getInstance(id);
    }
}
