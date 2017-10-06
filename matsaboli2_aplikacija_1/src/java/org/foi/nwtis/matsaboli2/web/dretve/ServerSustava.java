/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija
 */
public class ServerSustava extends Thread {

    Konfiguracija konf = null;
    ServletContext sc = null;
    String server;
    int port;
    public static ServerSocket ss = null;
    Socket kor_socket = null;
    public static boolean server_stop = false;
    Date vrijeme_preuzimanja;
    
    @Override
    public void interrupt() {
        if(ss != null && !ss.isClosed()){
            try {
                ss.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        super.interrupt();
    }

    @Override
    public void run() {
        sc = (ServletContext) SlusacAplikacije.kontekst;
        konf = (Konfiguracija) sc.getAttribute("App_Konfig");

        port = Integer.parseInt(konf.dajPostavku("serverPort"));

        try {
            ss = new ServerSocket(this.port);
            System.out.println("Server sustava port - " + ss.getLocalPort());

            while (!server_stop) {
                kor_socket = ss.accept();
                vrijeme_preuzimanja = new Date();
                RadnaDretva rd = new RadnaDretva(kor_socket, vrijeme_preuzimanja);
                rd.start();
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    @Override
    public synchronized void start() {
        super.start();
    }

}
