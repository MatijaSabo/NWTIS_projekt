/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.filteri;

import java.io.IOException;
import java.util.Date;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matsaboli2.orm.Dnevnik;
import org.foi.nwtis.matsaboli2.orm.DnevnikFacade;

/**
 *
 * @author Matija
 */
@WebFilter(filterName = "AplikacijskiFilter", urlPatterns = {"/*"})
public class AplikacijskiFilter implements Filter {

    @EJB
    private DnevnikFacade dnevnikFacade;

    private FilterConfig filterConfig = null;

    public AplikacijskiFilter() {
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        long pocetak = System.currentTimeMillis();
        String korisnik = "-";

        HttpServletRequest reqt = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession ses = reqt.getSession(false);
        String reqURI = reqt.getRequestURI();
        if (reqURI.contains("/index.xhtml") || reqURI.contains("/pogled1.xhtml")
                || reqURI.contains("/pogled2.xhtml") || reqURI.contains("/pogled3.xhtml")
                || reqURI.contains("/pogled4.xhtml") || reqURI.contains("/pogled5.xhtml")
                || reqURI.contains("/pogled6.xhtml") || reqURI.contains("/pogled7.xhtml")) {

            if (reqURI.contains("/index.xhtml")
                    || (ses != null && ses.getAttribute("kor_ime") != null && ses.getAttribute("pass") != null)) {

                if (ses != null && ses.getAttribute("kor_ime") != null) {
                    korisnik = ses.getAttribute("kor_ime").toString();
                } else {
                    korisnik = "Neprijavljeni";
                }

                chain.doFilter(request, response);
            } else {
                korisnik = "Neprijavljeni";
                resp.sendRedirect(reqt.getContextPath() + "/faces/index.xhtml");
            }

        } else {
            if (ses != null && ses.getAttribute("kor_ime") != null) {
                korisnik = ses.getAttribute("kor_ime").toString();
            } else {
                korisnik = "Neprijavljeni";
            }
            chain.doFilter(request, response);
        }

        long kraj = System.currentTimeMillis();

        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setIpadresa(request.getRemoteAddr());
        dnevnik.setKorisnik(korisnik);
        dnevnik.setTrajanje((int) (kraj - pocetak));
        dnevnik.setUrl(reqt.getRequestURI());
        dnevnik.setVrijeme(new Date());

        dnevnikFacade.create(dnevnik);
    }
}
