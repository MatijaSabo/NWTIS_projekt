/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

/**
 *
 * @author Matija
 */
@Named(value = "navigacija")
@SessionScoped
public class Navigacija implements Serializable {

    /**
     * Creates a new instance of Navigacija
     */
    public Navigacija() {
    }
    
    public String goToView1(){
        return "view1";
    }
    
    public String goToView2(){
        return "view2";
    }    
}
