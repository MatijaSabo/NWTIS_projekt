/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.matsaboli2.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.matsaboli2.zrna.SingletonSessionBean;

/**
 * Web application lifecycle listener.
 *
 * @author Matija
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    public static ServletContext kontekst;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String datoteka = sce.getServletContext().getRealPath("/WEB-INF") + File.separator + sce.getServletContext().getInitParameter("konfiguracija");
        
        Konfiguracija konf = null;
        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            sce.getServletContext().setAttribute("App_Konfig", konf);
            
            SingletonSessionBean.intervalDretve = Integer.parseInt(konf.dajPostavku("intervalPreuzimanjaPoruka"));
            SingletonSessionBean.emailServer = konf.dajPostavku("mail.server");
            SingletonSessionBean.emailPort = konf.dajPostavku("mail.port");
            SingletonSessionBean.emailUser = konf.dajPostavku("mail.username");
            SingletonSessionBean.emailPass = konf.dajPostavku("mail.password");
            SingletonSessionBean.emailSubject = konf.dajPostavku("mail.subject");
            SingletonSessionBean.emailNwtisFolder = konf.dajPostavku("mail.nwtis_poruke");
            SingletonSessionBean.mqtt_slot = Integer.parseInt(konf.dajPostavku("mqtt.slot"));
            
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

        kontekst = sce.getServletContext();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
