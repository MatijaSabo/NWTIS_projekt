/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.dretve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.GMKlijent;
import org.foi.nwtis.matsaboli2.rest.klijenti.OWMKlijent;
import org.foi.nwtis.matsaboli2.web.podaci.MeteoPodaci;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
public class PreuzimanjePrognoza extends Thread {

    private boolean flag = true;
    ServletContext sc = null;
    Konfiguracija konf = null;
    BP_Konfiguracija bp_konf = null;
    public static boolean meteo_pause = false;

    @Override
    public void interrupt() {
        flag = false;
        super.interrupt();
    }

    @Override
    public void run() {
        sc = (ServletContext) SlusacAplikacije.kontekst;
        konf = (Konfiguracija) sc.getAttribute("App_Konfig");
        bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        int interval = Integer.parseInt(konf.dajPostavku("intervalPreuzimanjaMeteoPrognoze"));
        String apikey = konf.dajPostavku("apikey");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Connection veza = null;

        try {
            Class.forName(bp_driver);
            veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (flag) {
            long pocetak = System.currentTimeMillis();

            if (!PreuzimanjePrognoza.meteo_pause) {
                try {
                    PreparedStatement select1 = veza.prepareStatement("SELECT latitude, longitude FROM uredaji GROUP BY 1, 2");
                    PreparedStatement select2 = veza.prepareStatement("SELECT id FROM uredaji WHERE (latitude BETWEEN ? AND ?) AND (longitude BETWEEN ? AND ?)");
                    PreparedStatement insert = veza.prepareStatement("INSERT INTO meteo VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    ResultSet odgovor = select1.executeQuery();

                    while (odgovor.next()) {
                        String latitude = String.valueOf(odgovor.getFloat("latitude"));
                        String longitude = String.valueOf(odgovor.getFloat("longitude"));

                        OWMKlijent owm = new OWMKlijent(apikey);
                        MeteoPodaci mp = owm.getRealTimeWeather(latitude, longitude);

                        GMKlijent gm = new GMKlijent();
                        String adresa_stanice = gm.getAddress(latitude, longitude);
                                
                        String vrijemeOpis = mp.getWeatherValue();
                        if (vrijemeOpis.length() > 25) {
                            vrijemeOpis = mp.getWeatherValue().substring(0, 22) + "...";
                        }

                        insert.setString(2, adresa_stanice);
                        insert.setFloat(3, Float.parseFloat(latitude));
                        insert.setFloat(4, Float.parseFloat(longitude));
                        insert.setString(5, String.valueOf(mp.getWeatherNumber()));
                        insert.setString(6, vrijemeOpis);
                        insert.setFloat(7, mp.getTemperatureValue());
                        insert.setFloat(8, mp.getTemperatureMin());
                        insert.setFloat(9, mp.getTemperatureMax());
                        insert.setFloat(10, mp.getHumidityValue());
                        insert.setFloat(11, mp.getPressureValue());
                        insert.setFloat(12, mp.getWindSpeedValue());
                        insert.setFloat(13, mp.getWindDirectionValue());
                        insert.setTimestamp(14, new Timestamp(mp.getLastUpdate().getTime()));
                        
                        select2.setFloat(1, (float) (odgovor.getFloat("latitude") - 0.0001));
                        select2.setFloat(2, (float) (odgovor.getFloat("latitude") + 0.0001));
                        select2.setFloat(3, (float) (odgovor.getFloat("longitude") - 0.0001));
                        select2.setFloat(4, (float) (odgovor.getFloat("longitude") + 0.0001));
                        ResultSet odgovor2 = select2.executeQuery();
                        
                        while (odgovor2.next()) {
                            insert.setInt(1, odgovor2.getInt("id"));
                            insert.executeUpdate();
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            long kraj = System.currentTimeMillis();
            
            try {
                sleep((interval * 1000) - (kraj - pocetak));
            } catch (InterruptedException ex) {
                Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

}
