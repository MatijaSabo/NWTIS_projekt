/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.orm;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matija
 */
@Stateless
public class PorukeFacade extends AbstractFacade<Poruke> {

    @PersistenceContext(unitName = "matsaboli2_aplikacija_2_1PU")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public PorukeFacade() {
        super(Poruke.class);
    }
    
}
