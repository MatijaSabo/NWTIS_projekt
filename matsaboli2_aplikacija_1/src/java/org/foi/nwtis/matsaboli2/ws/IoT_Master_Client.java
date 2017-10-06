/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.ws;

/**
 *
 * @author Matija
 */
public class IoT_Master_Client {

    public static Boolean aktivirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.aktivirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

    public static boolean aktivirajOdabraneUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.Integer> odabraniUredjaji) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.aktivirajOdabraneUredjajeGrupe(korisnickoIme, korisnickaLozinka, odabraniUredjaji);
    }

    public static boolean aktivirajUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.aktivirajUredjajGrupe(korisnickoIme, korisnickaLozinka, idUredjaj);
    }

    public static Boolean autenticirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.autenticirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean blokirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.blokirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

    public static boolean blokirajOdabraneUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.Integer> odabraniUredjaji) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.blokirajOdabraneUredjajeGrupe(korisnickoIme, korisnickaLozinka, odabraniUredjaji);
    }

    public static boolean blokirajUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.blokirajUredjajGrupe(korisnickoIme, korisnickaLozinka, idUredjaj);
    }

    public static StatusKorisnika dajStatusGrupeIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.dajStatusGrupeIoT(korisnickoIme, korisnickaLozinka);
    }

    public static StatusUredjaja dajStatusUredjajaGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.dajStatusUredjajaGrupe(korisnickoIme, korisnickaLozinka, idUredjaj);
    }

    public static java.util.List<org.foi.nwtis.matsaboli2.ws.Uredjaj> dajSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.dajSveUredjajeGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean deregistrirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.deregistrirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean dodajNoviUredjajGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj, java.lang.String nazivUredjaj, java.lang.String adresaUredjaj) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.dodajNoviUredjajGrupi(korisnickoIme, korisnickaLozinka, idUredjaj, nazivUredjaj, adresaUredjaj);
    }

    public static Boolean dodajUredjajGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, org.foi.nwtis.matsaboli2.ws.Uredjaj iotUredjaj) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.dodajUredjajGrupi(korisnickoIme, korisnickaLozinka, iotUredjaj);
    }

    public static boolean obrisiOdabraneUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.Integer> odabraniUredjaji) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.obrisiOdabraneUredjajeGrupe(korisnickoIme, korisnickaLozinka, odabraniUredjaji);
    }

    public static Boolean obrisiSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.obrisiSveUredjajeGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static boolean obrisiUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.obrisiUredjajGrupe(korisnickoIme, korisnickaLozinka, idUredjaj);
    }

    public static Boolean registrirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.registrirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

    public static boolean ucitajSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.matsaboli2.ws.IoTMaster_Service service = new org.foi.nwtis.matsaboli2.ws.IoTMaster_Service();
        org.foi.nwtis.matsaboli2.ws.IoTMaster port = service.getIoTMasterPort();
        return port.ucitajSveUredjajeGrupe(korisnickoIme, korisnickaLozinka);
    }
}
