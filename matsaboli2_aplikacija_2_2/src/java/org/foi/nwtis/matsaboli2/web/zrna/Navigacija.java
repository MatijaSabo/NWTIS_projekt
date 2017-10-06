/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

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
    
    public String goToView3(){
        return "view3";
    }
    
    public String goToView4(){
        return "view4";
    }
    
    public String goToView5(){
        return "view5";
    }
    
    public String goToView6(){
        return "view6";
    }
    
    public String goToView7(){
        return "view7";
    }
}
