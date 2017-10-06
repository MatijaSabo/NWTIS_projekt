/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.filteri;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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

/**
 *
 * @author Matija
 */
@WebFilter(filterName = "KontrolaPristupa", urlPatterns = {"/*"})
public class KontrolaPristupa implements Filter {
    
    private FilterConfig filterConfig = null;
    
    public KontrolaPristupa() {
    }    
    
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
    }
    
    public void destroy() {        
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest reqt = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession ses = reqt.getSession(false);
        String reqURI = reqt.getRequestURI();
        if (reqURI.contains("/index.xhtml") || reqURI.contains("/pogled1.xhtml")
                || reqURI.contains("/pogled2.xhtml")) {

            if (reqURI.contains("/index.xhtml")
                    || (ses != null && ses.getAttribute("kor_ime") != null && ses.getAttribute("pass") != null)) {
                chain.doFilter(request, response);
            } else {
                resp.sendRedirect(reqt.getContextPath() + "/faces/index.xhtml");
            }
        } else {
            chain.doFilter(request, response);
        }
       
    }
    
}
