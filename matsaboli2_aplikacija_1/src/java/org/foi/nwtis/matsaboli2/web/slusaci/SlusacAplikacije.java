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
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.web.dretve.PreuzimanjePrognoza;
import org.foi.nwtis.matsaboli2.web.dretve.ServerSustava;

/**
 * Web application lifecycle listener.
 *
 * @author Matija
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    PreuzimanjePrognoza dretva = null;
    ServerSustava server = null;
    public static ServletContext kontekst = null;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String datoteka = sce.getServletContext().getRealPath("/WEB-INF") + File.separator + sce.getServletContext().getInitParameter("konfiguracija");
        
        BP_Konfiguracija bpkonf = new BP_Konfiguracija(datoteka);
        sce.getServletContext().setAttribute("BP_Konfig", bpkonf);
        
        Konfiguracija konf = null;
        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            sce.getServletContext().setAttribute("App_Konfig", konf);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        kontekst = sce.getServletContext();
        
        dretva = new PreuzimanjePrognoza();
        server = new ServerSustava();
        
        dretva.start();
        server.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(dretva != null){
            dretva.interrupt();
        }
        
        if(server != null){
            server.interrupt();
        }
    }
}
