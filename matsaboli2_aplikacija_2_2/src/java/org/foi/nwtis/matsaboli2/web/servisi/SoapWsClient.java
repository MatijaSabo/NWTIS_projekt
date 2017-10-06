/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.servisi;

import java.util.List;
import org.foi.nwtis.matsaboli2.ws.MeteoPodaci;

/**
 *
 * @author Matija
 */
public class SoapWsClient {

    public static String dajAdresuZaIoT(int id, java.lang.String korIme, java.lang.String pass) {
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service service = new org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service();
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs port = service.getSoapMeteoWsPort();
        return port.dajAdresuZaIoT(id, korIme, pass);
    }

    public static List<MeteoPodaci> dajMeteoPodatkeZaIoT(int id, long pocetak, long kraj, java.lang.String korIme, java.lang.String pass) {
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service service = new org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service();
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs port = service.getSoapMeteoWsPort();
        return port.dajMeteoPodatkeZaIoT(id, pocetak, kraj, korIme, pass);
    }

    public static List<MeteoPodaci> dajZadnjihNMeteoPodatakaZaIoT(int ioT, int n, java.lang.String korIme, java.lang.String pass) {
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service service = new org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service();
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs port = service.getSoapMeteoWsPort();
        return port.dajZadnjihNMeteoPodatakaZaIoT(ioT, n, korIme, pass);
    }

    public static MeteoPodaci vazeciMeteoPodaciZaIoT(int id, java.lang.String korIme, java.lang.String pass) {
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service service = new org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service();
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs port = service.getSoapMeteoWsPort();
        return port.vazeciMeteoPodaciZaIoT(id, korIme, pass);
    }

    public static MeteoPodaci zadnjiMeteoPodaciZaIoT(int ioT, java.lang.String korIme, java.lang.String pass) {
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service service = new org.foi.nwtis.matsaboli2.ws.SoapMeteoWs_Service();
        org.foi.nwtis.matsaboli2.ws.SoapMeteoWs port = service.getSoapMeteoWsPort();
        return port.zadnjiMeteoPodaciZaIoT(ioT, korIme, pass);
    }
}
