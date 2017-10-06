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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.GMKlijent;
import org.foi.nwtis.matsaboli2.rest.klijenti.OWMKlijent;
import org.foi.nwtis.matsaboli2.web.dretve.PreuzimanjePrognoza;
import org.foi.nwtis.matsaboli2.web.podaci.MeteoPodaci;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
@WebService(serviceName = "SoapMeteoWs")
public class SoapMeteoWs {

    @Resource
    WebServiceContext wsContext;

    /**
     * Web service operation
     */
    @WebMethod(operationName = "zadnjiMeteoPodaciZaIoT")
    public MeteoPodaci zadnjiMeteoPodaciZaIoT(@WebParam(name = "IoT") int IoT, @WebParam(name = "kor_ime") String kor_ime, @WebParam(name = "pass") String pass) {
        MeteoPodaci mp = new MeteoPodaci();

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

            PreparedStatement user_check = veza.prepareStatement("SELECT * FROM korisnici WHERE kor_ime = ? AND pass = ?");
            PreparedStatement select = veza.prepareStatement("SELECT * FROM meteo WHERE id = ? ORDER BY 1 DESC LIMIT 1");

            user_check.setString(1, kor_ime);
            user_check.setString(2, pass);
            ResultSet users = user_check.executeQuery();

            if (users.next()) {
                select.setInt(1, IoT);
                ResultSet result = select.executeQuery();
                result.next();
                
                mp.setWeatherNumber(Integer.parseInt(result.getString("vrijeme")));
                mp.setWeatherValue(result.getString("vrijemeOpis"));
                mp.setWeatherIcon("OK");

                mp.setTemperatureValue(result.getFloat("temp"));
                mp.setTemperatureMin(result.getFloat("tempMin"));
                mp.setTemperatureMax(result.getFloat("tempMax"));
                mp.setTemperatureUnit("C");

                mp.setHumidityUnit("%");
                mp.setHumidityValue(result.getFloat("vlaga"));

                mp.setPressureUnit("hPa");
                mp.setPressureValue(result.getFloat("tlak"));

                mp.setWindDirectionValue(result.getFloat("vjetarSmjer"));
                mp.setWindDirectionCode("");
                mp.setWindDirectionName("");
                mp.setWindSpeedValue(result.getFloat("vjetar"));
                mp.setWindSpeedName("");

                mp.setSunRise(new Date());
                mp.setSunSet(new Date());

                mp.setCloudsName("");
                mp.setCloudsValue(0);

                mp.setPrecipitationMode("");
                mp.setPrecipitationUnit("");
                mp.setPrecipitationValue((float) 0.0);

                mp.setLastUpdate(new Date(result.getTimestamp("preuzeto").getTime()));
            }

            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            String url = req.getRequestURI();

            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, kor_ime);
            dnevnik.setString(2, "zadnjiMeteoPodaciZaIoT");
            dnevnik.setString(3, url);
            dnevnik.setInt(4, 2);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mp;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajZadnjihNMeteoPodatakaZaIoT")
    public List<MeteoPodaci> dajZadnjihNMeteoPodatakaZaIoT(@WebParam(name = "IoT") int IoT, @WebParam(name = "n") int n, @WebParam(name = "kor_ime") String kor_ime, @WebParam(name = "pass") String pass) {
        List<MeteoPodaci> list = new ArrayList<>();

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

            PreparedStatement user_check = veza.prepareStatement("SELECT * FROM korisnici WHERE kor_ime = ? AND pass = ?");
            user_check.setString(1, kor_ime);
            user_check.setString(2, pass);
            ResultSet users = user_check.executeQuery();

            if (users.next()) {
                PreparedStatement select = veza.prepareStatement("SELECT * FROM meteo WHERE id = ? ORDER BY 1 desc LIMIT ?");
                select.setInt(1, IoT);
                select.setInt(2, n);
                ResultSet result = select.executeQuery();

                while (result.next()) {
                    MeteoPodaci mp = new MeteoPodaci();

                    mp.setWeatherNumber(Integer.parseInt(result.getString("vrijeme")));
                    mp.setWeatherValue(result.getString("vrijemeOpis"));
                    mp.setWeatherIcon("OK");

                    mp.setTemperatureValue(result.getFloat("temp"));
                    mp.setTemperatureMin(result.getFloat("tempMin"));
                    mp.setTemperatureMax(result.getFloat("tempMax"));
                    mp.setTemperatureUnit("C");

                    mp.setHumidityUnit("%");
                    mp.setHumidityValue(result.getFloat("vlaga"));

                    mp.setPressureUnit("hPa");
                    mp.setPressureValue(result.getFloat("tlak"));

                    mp.setWindDirectionValue(result.getFloat("vjetarSmjer"));
                    mp.setWindDirectionCode("");
                    mp.setWindDirectionName("");
                    mp.setWindSpeedValue(result.getFloat("vjetar"));
                    mp.setWindSpeedName("");

                    mp.setSunRise(new Date());
                    mp.setSunSet(new Date());

                    mp.setCloudsName("");
                    mp.setCloudsValue(0);

                    mp.setPrecipitationMode("");
                    mp.setPrecipitationUnit("");
                    mp.setPrecipitationValue((float) 0.0);

                    mp.setLastUpdate(new Date(result.getTimestamp("preuzeto").getTime()));
                    list.add(mp);
                }
            }

            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            String url = req.getRequestURI();

            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, kor_ime);
            dnevnik.setString(2, "dajZadnjihNMeteoPodatakaZaIoT");
            dnevnik.setString(3, url);
            dnevnik.setInt(4, 2);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajMeteoPodatkeZaIoT")
    public List<MeteoPodaci> dajMeteoPodatkeZaIoT(@WebParam(name = "id") int id, @WebParam(name = "pocetak") long pocetak, @WebParam(name = "kraj") long kraj, @WebParam(name = "kor_ime") String kor_ime, @WebParam(name = "pass") String pass) {
        List<MeteoPodaci> list = new ArrayList<>();

        Timestamp vrijeme_od = new Timestamp(pocetak * 1000);
        Timestamp vrijeme_do = new Timestamp(kraj * 1000);

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

            PreparedStatement user_check = veza.prepareStatement("SELECT * FROM korisnici WHERE kor_ime = ? AND pass = ?");
            user_check.setString(1, kor_ime);
            user_check.setString(2, pass);
            ResultSet users = user_check.executeQuery();

            if (users.next()) {
                PreparedStatement select = veza.prepareStatement("SELECT * FROM meteo WHERE id = ? AND preuzeto >= ? AND preuzeto <= ?");
                select.setInt(1, id);
                select.setTimestamp(2, vrijeme_od);
                select.setTimestamp(3, vrijeme_do);
                ResultSet result = select.executeQuery();

                while (result.next()) {
                    MeteoPodaci mp = new MeteoPodaci();

                    mp.setWeatherNumber(Integer.parseInt(result.getString("vrijeme")));
                    mp.setWeatherValue(result.getString("vrijemeOpis"));
                    mp.setWeatherIcon("OK");

                    mp.setTemperatureValue(result.getFloat("temp"));
                    mp.setTemperatureMin(result.getFloat("tempMin"));
                    mp.setTemperatureMax(result.getFloat("tempMax"));
                    mp.setTemperatureUnit("C");

                    mp.setHumidityUnit("%");
                    mp.setHumidityValue(result.getFloat("vlaga"));

                    mp.setPressureUnit("hPa");
                    mp.setPressureValue(result.getFloat("tlak"));

                    mp.setWindDirectionValue(result.getFloat("vjetarSmjer"));
                    mp.setWindDirectionCode("");
                    mp.setWindDirectionName("");
                    mp.setWindSpeedValue(result.getFloat("vjetar"));
                    mp.setWindSpeedName("");

                    mp.setSunRise(new Date());
                    mp.setSunSet(new Date());

                    mp.setCloudsName("");
                    mp.setCloudsValue(0);

                    mp.setPrecipitationMode("");
                    mp.setPrecipitationUnit("");
                    mp.setPrecipitationValue((float) 0.0);

                    mp.setLastUpdate(new Date(result.getTimestamp("preuzeto").getTime()));
                    list.add(mp);
                }
            }

            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            String url = req.getRequestURI();

            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, kor_ime);
            dnevnik.setString(2, "dajMeteoPodatkeZaIoT");
            dnevnik.setString(3, url);
            dnevnik.setInt(4, 2);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "vazeciMeteoPodaciZaIoT")
    public MeteoPodaci vazeciMeteoPodaciZaIoT(@WebParam(name = "id") int id, @WebParam(name = "kor_ime") String kor_ime, @WebParam(name = "pass") String pass) {
        MeteoPodaci mp = null;
        String latitude = "";
        String longitude = "";

        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        String apikey = konf.dajPostavku("apikey");

        Connection veza = null;

        try {
            Class.forName(bp_driver);
            veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);

            PreparedStatement user_check = veza.prepareStatement("SELECT * FROM korisnici WHERE kor_ime = ? AND pass = ?");
            user_check.setString(1, kor_ime);
            user_check.setString(2, pass);
            ResultSet users = user_check.executeQuery();

            if (users.next()) {
                PreparedStatement select = veza.prepareStatement("SELECT * FROM uredaji WHERE id = ?");
                select.setInt(1, id);
                ResultSet result = select.executeQuery();
                result.next();

                latitude = String.valueOf(result.getFloat("latitude"));
                longitude = String.valueOf(result.getFloat("longitude"));
            }

            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            String url = req.getRequestURI();

            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, kor_ime);
            dnevnik.setString(2, "vazeciMeteoPodaciZaIoT");
            dnevnik.setString(3, url);
            dnevnik.setInt(4, 2);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        }

        OWMKlijent owm = new OWMKlijent(apikey);
        mp = owm.getRealTimeWeather(latitude, longitude);

        return mp;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajAdresuZaIoT")
    public String dajAdresuZaIoT(@WebParam(name = "id") int id, @WebParam(name = "kor_ime") String kor_ime, @WebParam(name = "pass") String pass) {
        String latitude = "";
        String longitude = "";

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

            PreparedStatement user_check = veza.prepareStatement("SELECT * FROM korisnici WHERE kor_ime = ? AND pass = ?");
            user_check.setString(1, kor_ime);
            user_check.setString(2, pass);
            ResultSet users = user_check.executeQuery();

            if (users.next()) {
                PreparedStatement select = veza.prepareStatement("SELECT * FROM uredaji WHERE id = ?");
                select.setInt(1, id);
                ResultSet result = select.executeQuery();
                result.next();

                latitude = String.valueOf(result.getFloat("latitude"));
                longitude = String.valueOf(result.getFloat("longitude"));
            }

            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            String url = req.getRequestURI();

            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, kor_ime);
            dnevnik.setString(2, "dajAdresuZaIoT");
            dnevnik.setString(3, url);
            dnevnik.setInt(4, 2);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PreuzimanjePrognoza.class.getName()).log(Level.SEVERE, null, ex);
        }

        GMKlijent gm = new GMKlijent();
        String adresa = gm.getAddress(latitude, longitude);
        return adresa;
    }
}
